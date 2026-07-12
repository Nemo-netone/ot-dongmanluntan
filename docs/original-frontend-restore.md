# Original Frontend Restore - 2026-07-12

This record documents the rollback from the generic portfolio demo shell to the original anime forum frontend.

## Production

- GitHub repository: `https://github.com/Nemo-netone/ot-dongmanluntan`
- Stable demo URL: `https://ot-dongmanluntan.pages.dev`
- Cloudflare Pages project: `ot-dongmanluntan`
- Production branch: `main`
- Published directory: `original-site/`
- Original frontend artifact: `sui-element-ui/dist`
- Compatibility backend: `original-site/_worker.js`
- Supabase schema: `ot_dongmanluntan`

## What Changed

- Added `original-site/` as the Cloudflare Pages output directory.
- Copied the original Vue 2 / Element UI build from `sui-element-ui/dist`.
- Set `original-site/static/config.js` to use `window.location.origin`, so the original frontend calls the same Pages domain instead of the stale local or CloudBase API base.
- Added a Pages Worker compatibility layer for original legacy routes.
- Kept the previous `site/` generic demo shell for comparison and rollback; it is not the current production deployment target.

## Compatibility Routes

The Worker now supports the original frontend response shape:

```json
{
  "code": 200,
  "isOk": true
}
```

Core restored routes:

- `/health`
- `/api/sys/getAdminConfig`
- `/api/sys/getDictList`
- `/api/category/getList`
- `/api/article/getPage`
- `/api/notice/getList`
- `/api/login/userLogin`
- `/api/login/getToken`
- `/api/register/getLoginInfo`
- `/admin/login/userLogin`
- `/admin/login/getLoginInfo`

## Verification

Completed on 2026-07-12:

- `https://ot-dongmanluntan.pages.dev/front/home` returns HTTP 200.
- `/health` returns `frontend: original-sui-element-ui`.
- Category, article, notice, config, logout, and login routes return HTTP 200.
- Front login with public demo account `test / 123456` succeeds.
- Browser screenshot shows the original anime forum layout: fixed top navigation, search block, category links, carousel, notices, and article cards.
- Screenshots:
  - `docs/screenshots/original-home.png`
  - `docs/screenshots/original-login.png`

## Known Boundary

The original page embeds a third-party weather iframe. In headless Playwright checks, some external weather/statistic image requests can abort. Project-owned Pages assets and API routes passed.

## Restore Rule For Other Projects

For the remaining projects, follow this order:

1. Prefer the original project frontend artifact (`dist`, `static`, `templates`, or built Vue/Vite output).
2. Keep the generic `site/` shell only as fallback, not as the default production UI.
3. Patch runtime API config to same-origin when the compiled frontend otherwise falls back to `127.0.0.1`, CloudBase, or another stale API.
4. Implement only the compatibility routes required by the original frontend's first screen, login, and core workflow.
5. Verify with browser screenshots before marking the project restored.
