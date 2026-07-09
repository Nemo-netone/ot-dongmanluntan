# 部署记录

## 固定资源

| 项目 | 值 |
|---|---|
| GitHub 仓库 | `Nemo-netone/ot-dongmanluntan` |
| GitHub 仓库地址 | `https://github.com/Nemo-netone/ot-dongmanluntan` |
| 生产分支 | `main` |
| Cloudflare Pages 项目 | `ot-dongmanluntan` |
| Cloudflare Pages 地址 | `https://ot-dongmanluntan.pages.dev` |
| CloudBase Run 服务 | `ot-dongmanluntan-api` |
| CloudBase API Base | `https://ot-dongmanluntan-api-273280-7-1369167244.sh.run.tcloudbase.com` |
| Supabase schema | `ot_dongmanluntan` |

第一次生产部署使用 `main` 分支。后续更新继续推送 `main`，并复用同一个 Cloudflare Pages 项目，避免演示地址变化。

## 数据库隔离

Supabase 初始化脚本：

```text
supabase/migrations/202607080001_init_ot_dongmanluntan.sql
```

补充演示配置脚本：

```text
supabase/migrations/202607080002_demo_config_sanitization.sql
supabase/migrations/202607080003_demo_image_urls.sql
```

隔离策略：

- 创建并使用 `ot_dongmanluntan` schema。
- 不写入 `public` schema。
- 不执行 `DROP`、`TRUNCATE` 或针对未知 schema 的清理语句。
- 初始化数据尽量使用 `ON CONFLICT` 或限定条件更新，重复执行不覆盖其他项目数据。
- 原始 MySQL dump 仅作为本地来源文件保留，不提交公开仓库，也不直接导入 Supabase。

同一个 Supabase 项目内还有其他项目数据，因此不要执行全局 migration repair、全库 reset 或未限定 schema 的清理操作。本项目 SQL 应通过项目专用脚本执行。

## CloudBase Run 环境变量

```text
PORT=8090
DB_TYPE=postgres
DB_DRIVER=org.postgresql.Driver
DB_URL=jdbc:postgresql://<supabase-host>:5432/postgres
DB_SCHEMA=ot_dongmanluntan
DB_SSLMODE=require
DB_USERNAME=<database-user>
DB_PASSWORD=<database-password>
APP_AUTH_SECRET=<change-me>
CORS_ALLOWED_ORIGINS=https://ot-dongmanluntan.pages.dev,http://localhost:8088,http://localhost:8080
LOG_PATH=/tmp/logs
UPLOAD_DISK_PATH=/tmp
UPLOAD_FILE_BASE_PATH=/attach_files/upload
```

真实密码和密钥只配置在平台环境变量中，不写入仓库、文档或提交记录。

## CloudBase Run 部署

后端目录：

```text
sui-springboot
```

服务名：

```text
ot-dongmanluntan-api
```

部署要点：

- Spring Boot jar 需要通过 `spring-boot-maven-plugin` 打包为可执行 jar。
- Dockerfile 直接在镜像构建阶段执行 `mvn -DskipTests package`。
- 运行端口为 `8090`。
- `application.yml` 通过环境变量拼接 PostgreSQL URL，并设置 `currentSchema=${DB_SCHEMA}` 和 `sslmode=${DB_SSLMODE}`。

## Cloudflare Pages

前端构建目录：

```text
sui-element-ui/dist
```

发布命令：

```powershell
wrangler pages deploy sui-element-ui/dist --project-name ot-dongmanluntan --branch main
```

前端运行时 API 地址位于：

```text
sui-element-ui/static/config.js
```

当前 `API_BASE_URL` 指向 CloudBase Run 后端 API Base。

## 演示账号

| 入口 | 地址 | 账号 | 密码 |
|---|---|---|---|
| 前台用户 | `/front/home` | `test` | `123456` |
| 后台管理 | `/login` | `admin` | `123456` |

## 验证清单

- `https://ot-dongmanluntan.pages.dev` 可访问。
- `/front/home` 能展示动漫文章、分类和公告数据。
- `/login` 能进入后台登录页。
- 后端 `/api/health` 返回正常。
- 数据库接口 `/api/category/getList` 返回 Supabase 数据。
- 前台账号 `test / 123456` 可登录。
- 后台账号 `admin / 123456` 可登录。
- README 的演示地址和账号信息正确。
- `LICENSE` 记录非商业许可证。
- secret scan 不出现真实 token、云密钥、数据库密码。

## 已知限制

- 上传文件保存在 CloudBase Run 临时目录，适合演示，不保证长期保留。
- 前端是旧版 Vue 2 + Webpack 3 项目，构建时可能出现 Sass/Webpack 旧 API 警告，但不影响当前构建产物。
- Supabase 远端 migration 历史包含其他项目记录，本项目只使用 schema 限定的 SQL 脚本，不对远端全局 migration 历史做修复或重置。
