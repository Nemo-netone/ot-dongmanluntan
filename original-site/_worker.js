const PROJECT_NAME = "动漫论坛社区";
const DEFAULT_IMAGE = "static/images/front/chat/banner.jpg";
const DEFAULT_AVATAR = "static/images/front/logo/logo.jpg";

export default {
  async fetch(request, env) {
    try {
      const url = new URL(request.url);
      if (request.method === "OPTIONS") return new Response(null, { headers: corsHeaders(request, env) });

      if (url.pathname === "/health") {
        return json(request, env, result(true, "ok", {
          service: `${schema(env)}-api`,
          schema: schema(env),
          frontend: "original-sui-element-ui",
          time: new Date().toISOString(),
        }));
      }

      if (!url.pathname.startsWith("/api/") && !url.pathname.startsWith("/admin/")) {
        return serveAssetOrSpa(request, env);
      }

      const body = await parseBody(request);
      const params = { ...Object.fromEntries(url.searchParams.entries()), ...body };
      const path = url.pathname.replace(/^\/+/, "");

      if (path.startsWith("api/")) {
        const legacy = await handleApi(path.slice(4), params, env);
        if (legacy) return json(request, env, legacy);
      }

      if (path.startsWith("admin/")) {
        const legacy = await handleAdmin(path.slice(6), params, env);
        if (legacy) return json(request, env, legacy);
      }

      return json(request, env, legacyFail("API path not found"), 404);
    } catch (error) {
      return json(request, env, legacyFail(error.message || "Server error"), 500);
    }
  },
};


async function portfolioSummary(env) {
  const rows = await requestSupabase(env, "items", "GET", { order: "updated_at.desc" });
  const modules = {};
  for (const row of rows) modules[row.module_key] = (modules[row.module_key] || 0) + 1;
  return result(true, null, { totalItems: rows.length, modules, latest: rows.slice(0, 6) });
}

async function portfolioItems(action, params, env) {
  if (action === "list") {
    const query = { order: "updated_at.desc" };
    if (params.module_key) query.module_key = `eq.${params.module_key}`;
    return result(true, null, await requestSupabase(env, "items", "GET", query));
  }
  if (action === "add") {
    const rows = await requestSupabase(env, "items", "POST", {}, portfolioPayload(params));
    return result(true, "created", rows[0] || null);
  }
  if (action === "update") {
    if (!params.id) return result(false, "Missing id", null);
    const rows = await requestSupabase(env, "items", "PATCH", { id: `eq.${params.id}` }, portfolioPayload(params));
    return result(true, "updated", rows[0] || null);
  }
  if (action === "delete") {
    if (!params.id) return result(false, "Missing id", null);
    await requestSupabase(env, "items", "DELETE", { id: `eq.${params.id}` });
    return result(true, "deleted", null);
  }
  return result(false, "Unknown action", null);
}

function portfolioPayload(params) {
  return { module_key: String(params.module_key || "article"), title: String(params.title || params.name || "Forum demo record"), subtitle: String(params.subtitle || ""), status: String(params.status || "published"), owner: String(params.owner || "demo user"), amount: Number(params.amount || 0), description: String(params.description || params.content || ""), extra: params.extra || {}, updated_at: new Date().toISOString() };
}
async function serveAssetOrSpa(request, env) {
  if (!env.ASSETS) return new Response("Not found", { status: 404 });
  const response = await env.ASSETS.fetch(request);
  if (response.status !== 404) return response;

  const accept = request.headers.get("Accept") || "";
  if (!accept.includes("text/html")) return response;

  const url = new URL(request.url);
  url.pathname = "/index.html";
  url.search = "";
  return env.ASSETS.fetch(new Request(url, request));
}

async function handleApi(action, params, env) {
  if (action === "summary") return portfolioSummary(env);
  if (action.startsWith("items/")) return portfolioItems(action.slice(6), params, env);
  if (action === "login/getToken") return legacyOk({ data: { token: `demo-token-${params.code || Date.now()}` } });
  if (action === "login/userLogin") return frontLogin(params, env);
  if (action === "login/loginOut") return legacyOk({ data: null, obj: null });
  if (action === "login/register") return legacyOk({ info: "注册成功", data: buildUser({ loginName: params.loginName || "demo" }, "user") });

  if (action === "sys/getAdminConfig") return legacyOk({ data: adminConfig() });
  if (action === "sys/getDictList") return legacyOk({ data: [] });
  if (action === "sys/getMenuUserTree") return legacyOk({ list: adminMenus() });
  if (action === "sys/getUserRoutes") return legacyOk({ list: adminRoutes() });
  if (action === "sys/unique") return legacyOk({ data: true });

  if (action === "register/getLoginInfo") return legacyOk({ data: { userInfo: buildUser({ loginName: "test" }, "user") } });
  if (action === "register/getList") return legacyOk({ list: await users(env), data: await users(env) });
  if (action === "register/updateUserInfo" || action === "register/modifyPsd") {
    return legacyOk({ obj: buildUser(params, "user"), data: { userInfo: buildUser(params, "user") } });
  }

  if (action === "category/getList") return legacyOk({ list: await categories(env), data: await categories(env) });

  if (action === "article/getPage" || action === "article/getUserPage") return pageResponse(await articles(env), params);
  if (action === "article/get") return legacyOk({ obj: findById(await articles(env), params.id), data: findById(await articles(env), params.id) });
  if (action === "article/sub") return legacyOk({ info: "保存成功", obj: mapArticle(normalizePostedItem(params, "article")) });
  if (action === "article/delete" || action === "article/delAll") return legacyOk({ info: "删除成功" });

  if (action === "notice/getPage") return pageResponse(await notices(env), params);
  if (action === "notice/getList") return legacyOk({ list: await notices(env), data: await notices(env) });
  if (action === "notice/get") return legacyOk({ obj: findById(await notices(env), params.id), data: findById(await notices(env), params.id) });

  if (action === "message/getTreePage") return pageResponse(await messages(env), params);
  if (action === "message/getList") return legacyOk({ list: await messages(env), data: await messages(env) });
  if (action === "message/get") return legacyOk({ obj: firstOrNull(await messages(env)), data: firstOrNull(await messages(env)) });
  if (action === "message/sub" || action === "message/subCollect" || action === "message/cancel" || action === "message/delete") {
    return legacyOk({ info: "操作成功", obj: mapMessage(normalizePostedItem(params, "comment")) });
  }

  if (action === "integral/getRankingList") return pageResponse(await rankings(env), params);
  if (action === "integral/delete" || action === "integral/delAll") return legacyOk({ info: "删除成功" });

  if (action.startsWith("socket/")) return legacyOk({ list: [], data: [], obj: null });
  if (action.startsWith("summary") || action.startsWith("items/")) return null;
  return null;
}

async function handleAdmin(action, params, env) {
  if (action === "login/userLogin") return adminLogin(params, env);
  if (action === "login/loginOut") return legacyOk({ data: null, obj: null });
  if (action === "login/getLoginInfo") {
    return legacyOk({
      data: {
        roles: [{ roleCode: "admin", roleName: "管理员" }],
        permissions: ["*:*:*"],
        userInfo: buildUser({ loginName: "admin" }, "admin"),
      },
    });
  }
  if (action === "login/getUserInfo") return legacyOk({ obj: buildUser({ loginName: "admin" }, "admin") });

  if (action === "sysAttach/getFileHistory") return legacyOk({ list: [], data: [] });
  if (action === "sysAttach/deleteFile") return legacyOk({ info: "删除成功" });

  if (action.includes("/getPage")) return pageResponse(await adminModuleRows(action, env), params);
  if (action.includes("/getList")) return legacyOk({ list: await adminModuleRows(action, env), data: await adminModuleRows(action, env) });
  if (action.includes("/get")) return legacyOk({ obj: findById(await adminModuleRows(action, env), params.id), data: findById(await adminModuleRows(action, env), params.id) });
  if (action.includes("/sub")) return legacyOk({ info: "保存成功", obj: params });
  if (action.includes("/delete") || action.includes("/delAll") || action.includes("/resetPassword") || action.includes("/lockUser")) {
    return legacyOk({ info: "操作成功" });
  }
  return legacyOk({ list: [], data: [], obj: null });
}

async function frontLogin(params, env) {
  const loginName = String(params.loginName || params.username || "").trim();
  const password = String(params.password || "").trim();
  const account = await findAccount(env, loginName, password);
  if (!account) return legacyFail("账号或密码错误");
  const role = account.role || "user";
  const userInfo = buildUser({ loginName: account.username, id: account.id }, role);
  return legacyOk({ data: { code: `front-${userInfo.id}`, userInfo } });
}

async function adminLogin(params, env) {
  const loginName = String(params.loginName || params.username || "").trim();
  const password = String(params.password || "").trim();
  const account = await findAccount(env, loginName, password);
  if (!account) return legacyFail("账号或密码错误");
  return legacyOk({ data: { userInfo: buildUser({ loginName: account.username, id: account.id }, account.role || "admin") } });
}

async function findAccount(env, loginName, password) {
  const rows = await requestSupabase(env, "accounts", "GET", { username: `eq.${loginName}`, password: `eq.${password}`, limit: "1" });
  return rows[0] || null;
}

async function rows(env, moduleKey) {
  const all = await requestSupabase(env, "items", "GET", { order: "id.asc" });
  return moduleKey ? all.filter((row) => row.module_key === moduleKey) : all;
}

async function categories(env) {
  const data = await rows(env, "category");
  return data.map((row, index) => ({
    id: row.id,
    name: cleanTitle(row.title, "分类", index),
    title: cleanTitle(row.title, "分类", index),
    content: row.description || row.subtitle || "",
    createDate: formatDate(row.created_at),
  }));
}

async function articles(env) {
  const data = await rows(env, "article");
  return data.map(mapArticle);
}

function mapArticle(row, index = 0) {
  return {
    id: row.id || Date.now(),
    title: cleanTitle(row.title, "动漫文章", index),
    name: row.subtitle || "动漫专题",
    categoryId: row.categoryId || ((index % 3) + 4),
    content: row.description || row.content || "这里展示动漫论坛的文章内容、图文信息和社区讨论入口。",
    picture: row.picture || DEFAULT_IMAGE,
    createDate: formatDate(row.created_at || row.updated_at),
    updateDate: formatDate(row.updated_at),
    userId: row.userId || 1,
    author: row.owner || "论坛用户",
    status: row.status || "已发布",
  };
}

async function notices(env) {
  const data = await rows(env, "notice");
  return data.map((row, index) => ({
    id: row.id,
    title: cleanTitle(row.title, "动漫资讯", index),
    content: row.description || row.subtitle || "动漫社区公告与活动资讯。",
    picture: DEFAULT_IMAGE,
    createDate: formatDate(row.created_at),
  }));
}

async function messages(env) {
  const data = await rows(env, "comment");
  return data.map(mapMessage);
}

function mapMessage(row, index = 0) {
  return {
    id: row.id || Date.now(),
    pid: row.pid || 0,
    refId: row.refId || 0,
    type: row.type || "评论",
    content: row.content || row.description || "欢迎在动漫论坛社区交流作品、角色和剧情观点。",
    createDate: formatDate(row.created_at || row.updated_at),
    userId: row.userId || index + 1,
    loginName: row.owner || "论坛用户",
    photo: DEFAULT_AVATAR,
    children: [],
  };
}

async function rankings(env) {
  const data = await rows(env, "points");
  return data.map((row, index) => ({
    id: row.id,
    registerId: index + 1,
    loginName: row.owner || `user${index + 1}`,
    integral: row.amount || (100 - index * 10),
    content: row.description || row.subtitle || "积分排行记录",
    createDate: formatDate(row.created_at),
  }));
}

async function users(env) {
  const accounts = await requestSupabase(env, "accounts", "GET", { order: "id.asc" });
  return accounts.map((account) => buildUser({ id: account.id, loginName: account.username }, account.role || "user"));
}

function buildUser(input = {}, role = "user") {
  const id = Number(input.id || 1);
  const loginName = String(input.loginName || input.username || (role === "admin" ? "admin" : "test"));
  return {
    id,
    loginName,
    userName: loginName,
    realName: role === "admin" ? "平台管理员" : role === "staff" ? "内容运营" : "论坛用户",
    nickName: role === "admin" ? "平台管理员" : role === "staff" ? "内容运营" : "动漫爱好者",
    roleCode: role,
    roleName: role === "admin" ? "管理员" : role === "staff" ? "运营" : "用户",
    photo: DEFAULT_AVATAR,
    integral: 100,
  };
}

async function adminModuleRows(action, env) {
  if (action.includes("category")) return categories(env);
  if (action.includes("article")) return articles(env);
  if (action.includes("notice")) return notices(env);
  if (action.includes("message")) return messages(env);
  if (action.includes("register") || action.includes("sysUser")) return users(env);
  if (action.includes("integral")) return rankings(env);
  if (action.includes("sysRole")) return [{ id: 1, roleCode: "admin", roleName: "管理员", remarks: "演示角色" }];
  if (action.includes("sysMenu")) return adminMenus();
  if (action.includes("sysOffice")) return [{ id: 1, name: "动漫论坛运营部", label: "动漫论坛运营部" }];
  return rows(env);
}

function adminConfig() {
  return {
    projectName: PROJECT_NAME,
    fileBasePath: "",
    showRoles: false,
    showRegister: true,
    showValidCode: false,
    allRole: [
      { roleCode: "admin", roleName: "管理员" },
      { roleCode: "staff", roleName: "运营" },
      { roleCode: "user", roleName: "用户" },
    ],
  };
}

function adminRoutes() {
  return [
    { name: "categoryManage", path: "/main/category", title: "内容分类", component: "admin/module/category/categoryManage.vue" },
    { name: "articleManage", path: "/main/article", title: "动漫文章", component: "admin/module/article/articleManage.vue" },
    { name: "noticeManage", path: "/main/notice", title: "动漫资讯", component: "admin/module/notice/noticeManage.vue" },
    { name: "messageManage", path: "/main/message", title: "社区交流", component: "admin/module/message/messageManage.vue" },
    { name: "registerManage", path: "/main/register", title: "用户管理", component: "admin/module/register/registerManage.vue" },
  ];
}

function adminMenus() {
  return adminRoutes().map((route, index) => ({ id: index + 1, pid: 0, name: route.name, title: route.title, path: route.path, component: route.component }));
}

function pageResponse(list, params) {
  const current = Math.max(Number(params.current || params.page || 1), 1);
  const size = Math.max(Number(params.size || params.limit || list.length || 10), 1);
  const filtered = filterRows(list, params);
  const start = (current - 1) * size;
  const records = filtered.slice(start, start + size);
  return legacyOk({ obj: { records, total: filtered.length, current, size }, data: { records, total: filtered.length, current, size }, list: records });
}

function filterRows(list, params) {
  const title = String(params.title || params.name || "").trim().toLowerCase();
  const categoryId = String(params.categoryId || params.id || "").trim();
  return list.filter((item) => {
    const titleMatch = !title || String(item.title || item.name || item.content || "").toLowerCase().includes(title);
    const categoryMatch = !categoryId || !item.categoryId || String(item.categoryId) === categoryId || String(item.id) === categoryId;
    return titleMatch && categoryMatch;
  });
}

function findById(list, id) {
  return list.find((item) => String(item.id) === String(id)) || list[0] || null;
}

function firstOrNull(list) {
  return list[0] || null;
}

function normalizePostedItem(params, moduleKey) {
  return {
    id: Number(params.id || Date.now()),
    module_key: moduleKey,
    title: params.title || params.name || "演示记录",
    subtitle: params.subtitle || "",
    owner: params.owner || params.loginName || "论坛用户",
    amount: Number(params.amount || 0),
    status: params.status || "已发布",
    description: params.content || params.description || "",
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString(),
  };
}

function cleanTitle(title, fallback, index = 0) {
  return title || `${fallback}${index + 1}`;
}

function formatDate(value) {
  const date = value ? new Date(value) : new Date();
  if (Number.isNaN(date.getTime())) return "";
  return date.toISOString().slice(0, 10);
}

async function requestSupabase(env, table, method, query = {}, payload = {}) {
  const base = cleanEnv(env.SUPABASE_URL);
  const key = cleanEnv(env.SUPABASE_ANON_KEY || env.SUPABASE_SERVICE_ROLE_KEY);
  if (!base || !key) throw new Error("Worker missing Supabase environment variables");
  const response = await fetch(`${base.replace(/\/$/, "")}/rest/v1/rpc/${schema(env)}_demo_rest`, {
    method: "POST",
    headers: {
      apikey: key,
      Authorization: `Bearer ${key}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ p_table_name: table, p_method: method, p_query: query, p_payload: payload }),
  });
  if (!response.ok) throw new Error(`Supabase request failed: ${response.status} ${await response.text()}`);
  const data = await response.json();
  return Array.isArray(data) ? data : [];
}

function schema(env) {
  return cleanEnv(env.SUPABASE_SCHEMA || "ot_dongmanluntan");
}

function cleanEnv(value) {
  return String(value || "").replace(/^\uFEFF/, "").trim();
}

async function parseBody(request) {
  if (request.method === "GET" || request.method === "HEAD") return {};
  const text = await request.text();
  if (!text) return {};
  const contentType = request.headers.get("Content-Type") || "";
  if (contentType.includes("application/json")) {
    try {
      return JSON.parse(text);
    } catch {
      return {};
    }
  }
  return Object.fromEntries(new URLSearchParams(text).entries());
}

function result(status, message, data) {
  return { status, message, data };
}

function legacyOk(payload = {}) {
  return { code: 200, isOk: true, info: payload.info || "ok", msg: payload.info || "ok", ...payload };
}

function legacyFail(info) {
  return { code: 400, isOk: false, info, msg: info, data: null, obj: null, list: [] };
}

function json(request, env, payload, status = 200) {
  return new Response(JSON.stringify(payload), {
    status,
    headers: { "Content-Type": "application/json; charset=utf-8", ...corsHeaders(request, env) },
  });
}

function corsHeaders(request, env) {
  const origin = request.headers.get("Origin") || "";
  const allowed = String(env.CORS_ALLOWED_ORIGINS || "").split(",").map((item) => item.trim()).filter(Boolean);
  const allowOrigin = allowed.includes(origin) ? origin : allowed[0] || "*";
  return {
    "Access-Control-Allow-Origin": allowOrigin,
    "Access-Control-Allow-Methods": "GET,POST,PATCH,DELETE,OPTIONS",
    "Access-Control-Allow-Headers": "Content-Type,Authorization,token",
    "Access-Control-Max-Age": "86400",
  };
}
