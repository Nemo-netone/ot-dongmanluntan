function normalizeApiBase(url) {
  if (!url) {
    return '';
  }
  return String(url).replace(/\/+$/, '');
}

export function getApiBaseURL() {
  const runtimeConfig = typeof window !== 'undefined' ? (window.__APP_CONFIG__ || {}) : {};
  const localOverride = typeof window !== 'undefined' && window.localStorage
    ? window.localStorage.getItem('API_BASE_URL')
    : '';
  const buildConfig = typeof process !== 'undefined' && process.env ? process.env.API_ROOT : '';
  return normalizeApiBase(localOverride || runtimeConfig.API_BASE_URL || buildConfig || '');
}

export function withApiBase(url) {
  if (!url || /^(https?:)?\/\//.test(url)) {
    return url;
  }
  const normalizedUrl = url.charAt(0) === '/' ? url : '/' + url;
  return getApiBaseURL() + normalizedUrl;
}
