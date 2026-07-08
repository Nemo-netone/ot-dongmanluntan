package com.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.admin.entity.Article;
import org.springframework.stereotype.Repository;

/**
* 【文章信息】数据接口
* ArticleDao与ArticleDaoMapper.xml进行接口映射
* 用于扩展自定义sql方法
*/
@Repository
public interface ArticleDao extends BaseMapper<Article> {

}

