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
| Supabase schema | `ot_dongmanluntan` |

第一次部署使用 `main` 分支。后续更新继续推送 `main` 并复用同一个 Cloudflare Pages 项目，避免演示地址变化。

## 数据库隔离

Supabase 初始化脚本：

```text
supabase/migrations/202607080001_init_ot_dongmanluntan.sql
```

隔离策略：

- 创建并使用 `ot_dongmanluntan` schema。
- 不写入 `public` schema。
- 不执行 `DROP`、`TRUNCATE` 或针对未知 schema 的清理语句。
- 初始化数据使用 `ON CONFLICT (id) DO NOTHING`，重复执行不会覆盖已有记录。
- 原始 MySQL dump 仅作为本地来源文件保留，不提交公开仓库，也不直接导入 Supabase。

## CloudBase Run 环境变量

```text
PORT=8090
DB_TYPE=postgres
DB_DRIVER=org.postgresql.Driver
DB_URL=jdbc:postgresql://<supabase-host>:5432/postgres?currentSchema=ot_dongmanluntan&sslmode=require
DB_USERNAME=<database-user>
DB_PASSWORD=<database-password>
APP_AUTH_SECRET=<change-me>
CORS_ALLOWED_ORIGINS=https://ot-dongmanluntan.pages.dev,http://localhost:8088,http://localhost:8080
LOG_PATH=/tmp/logs
UPLOAD_DISK_PATH=/tmp
UPLOAD_FILE_BASE_PATH=/attach_files/upload
```

真实密码和密钥只配置在平台环境变量中，不写入仓库。

## Cloudflare Pages

前端构建目录：

```text
sui-element-ui/dist
```

发布命令：

```powershell
wrangler pages deploy sui-element-ui/dist --project-name ot-dongmanluntan --branch main
```

前端运行时 API 地址在：

```text
sui-element-ui/static/config.js
```

部署后需要将 `API_BASE_URL` 指向 CloudBase Run 后端 API base URL。

## 验证清单

- `https://ot-dongmanluntan.pages.dev` 可访问。
- `/front/home` 能展示动漫文章、分类、公告数据。
- `/login` 能进入后台登录页。
- 后端 `/api/health` 返回正常。
- 数据库接口 `/api/category/getList` 返回 Supabase 数据。
- 前台账号 `test / 123456` 可登录。
- 后台账号 `admin / 123456` 可登录。
- GitHub README 的演示地址和账号信息正确。
- secret scan 不出现真实 token、云密钥、数据库密码。
