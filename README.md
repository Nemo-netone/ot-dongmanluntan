# 动漫论坛平台

动漫论坛平台是一个基于 Vue 2、Element UI、Spring Boot 和 Supabase PostgreSQL 的论坛类全栈演示项目，覆盖动漫文章浏览、分类筛选、评论互动、积分排行、公告资讯和后台内容管理。

## 在线演示

- GitHub 仓库：https://github.com/Nemo-netone/ot-dongmanluntan
- 演示地址：https://ot-dongmanluntan.pages.dev
- 生产分支：`main`
- Cloudflare Pages 项目：`ot-dongmanluntan`
- CloudBase Run 服务：`ot-dongmanluntan-api`
- CloudBase API Base：`https://ot-dongmanluntan-api-273280-7-1369167244.sh.run.tcloudbase.com`
- Supabase schema：`ot_dongmanluntan`

## 演示账号

| 入口 | 地址 | 账号 | 密码 |
|---|---|---|---|
| 前台用户 | `/front/home` | `test` | `123456` |
| 后台管理 | `/login` | `admin` | `123456` |

演示账号仅用于功能体验，请不要在公开演示环境中录入真实个人信息或敏感内容。

## 主要功能

- 前台门户：轮播推荐、动漫文章列表、文章详情、分类浏览、公告入口。
- 用户体系：前台登录、注册、个人资料、密码修改。
- 社区互动：文章评论、回复、收藏、浏览记录、用户留言。
- 积分模块：评论积分记录、积分排行榜。
- 公告资讯：公告列表和公告详情。
- 后台管理：文章、分类、公告、注册用户、评论留言、积分、附件、菜单、角色、字典、系统配置等管理。
- 文件能力：图片和附件上传下载；线上演示使用 CloudBase Run 临时文件目录，适合演示，不适合作为长期文件存储。

## 技术栈

| 层级 | 技术 |
|---|---|
| 前端 | Vue 2、Vue Router、Vuex、Element UI、Axios、Webpack 3 |
| 后端 | Spring Boot 2.5、MyBatis-Plus、Shiro、JWT、Druid |
| 数据库 | Supabase PostgreSQL，独立 schema `ot_dongmanluntan` |
| 部署 | Cloudflare Pages 托管前端，CloudBase Run 托管后端 API |

## 项目结构

```text
.
├── sui-element-ui/                 # Vue 2 前端
├── sui-springboot/                 # Spring Boot 后端
├── supabase/migrations/            # Supabase PostgreSQL 初始化和演示配置脚本
├── docs/deployment.md              # 部署记录
├── cloudbaserc.json                # CloudBase Run 项目配置，不包含密钥
└── .env.example                    # 环境变量样例
```

公开仓库只保留脱敏后的 Supabase migration。原始 MySQL dump、旧版静态构建包和视频素材属于本地来源或生成文件，不提交到 GitHub。

## 本地运行

### 1. 初始化数据库

Supabase 使用独立 schema，初始化脚本位于：

```text
supabase/migrations/202607080001_init_ot_dongmanluntan.sql
```

执行脚本前确认连接的是目标 Supabase 项目。脚本只创建并使用 `ot_dongmanluntan` schema，不覆盖 `public` 或其他项目 schema。

### 2. 启动后端

```powershell
cd sui-springboot
mvn -DskipTests package
java -Dserver.port=8090 -jar target/sui-springboot.jar
```

后端默认从环境变量读取数据库配置。可参考 `.env.example` 设置 `DB_URL`、`DB_SCHEMA`、`DB_USERNAME`、`DB_PASSWORD`、`APP_AUTH_SECRET` 等变量。

### 3. 启动前端

```powershell
cd sui-element-ui
npm install
npm run dev
```

如果使用较新的 Node.js 构建旧版 Webpack 项目，可能需要：

```powershell
$env:NODE_OPTIONS="--openssl-legacy-provider"
npm run build
```

前端 API 地址由 `sui-element-ui/static/config.js` 的 `API_BASE_URL` 控制，也可在浏览器 `localStorage.API_BASE_URL` 中临时覆盖。

## 部署说明

当前约定：

- GitHub 仓库名和 Cloudflare Pages 项目名统一为 `ot-dongmanluntan`。
- 第一次生产部署分支固定为 `main`，后续继续使用 `main`，保持 `https://ot-dongmanluntan.pages.dev` 不变。
- 后端服务名固定为 `ot-dongmanluntan-api`。
- Supabase 数据独立放在 `ot_dongmanluntan` schema，避免影响同一个 Supabase 项目内的其他数据。

更多部署记录见 [docs/deployment.md](docs/deployment.md)。

## 线上注意事项

- 原始 SQL 是 MySQL dump，线上使用的是转换后的 PostgreSQL migration。
- 代码生成模块里依赖 MySQL `information_schema/database()` 的查询不作为线上核心演示功能验证范围。
- CloudBase Run 文件系统不是长期文件存储，上传功能适合演示；生产环境建议接入对象存储。
- 公开仓库不保存任何真实平台 token、数据库密码、云密钥或第三方 API Key。

## 许可证

本项目采用 PolyForm Noncommercial License 1.0.0。允许非商业学习、使用和修改；商业使用需要获得作者单独授权。
