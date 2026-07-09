SET search_path TO ot_dongmanluntan;

UPDATE sys_config
SET val = 'https://ot-dongmanluntan-api-273280-7-1369167244.sh.run.tcloudbase.com'
WHERE code = 'projectDomain';

UPDATE sys_config
SET val = ''
WHERE code IN (
  'mailSmtp',
  'mailPort',
  'mailPassword',
  'mailName',
  'appletAppId',
  'appletAppSecret',
  'appletToken',
  'officialAppId',
  'officialAppSecret',
  'officialToken'
);
