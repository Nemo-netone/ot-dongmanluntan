SET search_path TO ot_dongmanluntan;

UPDATE article
SET picture = CASE title
  WHEN '海贼王' THEN 'https://ot-dongmanluntan.pages.dev/static/demo-images/one-piece.jpg'
  WHEN '龙珠' THEN 'https://ot-dongmanluntan.pages.dev/static/demo-images/dragon-ball.jpg'
  WHEN '名侦探柯南' THEN 'https://ot-dongmanluntan.pages.dev/static/demo-images/conan.jpg'
  WHEN '千与千寻' THEN 'https://ot-dongmanluntan.pages.dev/static/demo-images/spirited-away.jpg'
  ELSE picture
END
WHERE title IN ('海贼王', '龙珠', '名侦探柯南', '千与千寻');

UPDATE notice
SET picture = CASE title
  WHEN '资讯公告1' THEN 'https://ot-dongmanluntan.pages.dev/static/demo-images/banner-1.jpg'
  WHEN '资讯公告2' THEN 'https://ot-dongmanluntan.pages.dev/static/demo-images/banner-2.jpg'
  WHEN '资讯公告3' THEN 'https://ot-dongmanluntan.pages.dev/static/demo-images/banner-3.jpg'
  ELSE picture
END
WHERE title IN ('资讯公告1', '资讯公告2', '资讯公告3');
