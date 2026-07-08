package com.admin.service;

import com.admin.dao.ArticleDao;
import com.admin.entity.Article;
import com.admin.sys.base.module.extend.service.BaseService;
import com.admin.sys.utils.admin.ObjectUtils;
import com.admin.sys.utils.admin.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

//文章信息接口类
@Service
public class ArticleService extends BaseService {

    @Autowired
    private ArticleDao articleDao;

    //【文章信息】设置查询条件
    private LambdaQueryWrapper<Article> getArticleQueryCondition(Article article) {
        LambdaQueryWrapper<Article> lambdaQuery = this.getBaseQueryCondition(article);
        //根据创建时间排序
        lambdaQuery.orderByDesc(Article::getCreateDate);
        if (ObjectUtils.isNotNull(article)) {
            //【主键id】精确查询
            lambdaQuery.eq(StringUtils.isNotEmpty(article.getId()), Article::getId, article.getId());
            //【文章标题】模糊查询
            lambdaQuery.like(StringUtils.isNotEmpty(article.getTitle()), Article::getTitle, article.getTitle());
            //【文章作者】精确查询
            lambdaQuery.eq(StringUtils.isNotEmpty(article.getUserId()), Article::getUserId, article.getUserId());
            //【发布时间】范围查询
            if (ObjectUtils.isNotNull(article.getPublishTimeRange()) && article.getPublishTimeRange().size() > 0) {
                lambdaQuery.between(Article::getPublishTime, article.getPublishTimeRange().get(0), article.getPublishTimeRange().get(1));
            }
            //【文章分类】精确查询
            lambdaQuery.eq(StringUtils.isNotEmpty(article.getCategoryId()), Article::getCategoryId, article.getCategoryId());
            //【审核状态】范围查询
            lambdaQuery.in(ObjectUtils.isNotNull(article.getAuditStatusRange()), Article::getAuditStatus, article.getAuditStatusRange());
            //【审核状态】精确查询
            lambdaQuery.eq(ObjectUtils.isNotNull(article.getAuditStatus()), Article::getAuditStatus, article.getAuditStatus());
        }
        return lambdaQuery;
    }

    //【文章信息】分页查询
    public IPage<Article> getPage(Page<Article> page, Article article) {
        LambdaQueryWrapper<Article> lambdaQuery = getArticleQueryCondition(article);
        return articleDao.selectPage(page, lambdaQuery);
    }
    
    //【文章信息】查询列表
    public List<Article> getList(Article article) {
        LambdaQueryWrapper<Article> lambdaQuery = getArticleQueryCondition(article);
        return articleDao.selectList(lambdaQuery);
    }
    
    //【文章信息】根据id查询
    public Article get(String id) {
        return articleDao.selectById(id);
    }

    //【文章信息】根据对象查询
    public Article get(Article article) {
    LambdaQueryWrapper<Article> lambdaQuery = getArticleQueryCondition(article);
        return articleDao.selectOne(lambdaQuery);
    }
    
    //【文章信息】新增
    public int insert(Article article) {
        this.preInsert(article);
        return articleDao.insert(article);
    }
    
    //【文章信息】修改
    public int update(Article article) {
        this.preUpdate(article);
        return articleDao.updateById(article);
    }
    
    //【文章信息】删除
    public int delete(String id) {
        return articleDao.deleteById(id);
    }

    //【文章信息】批量删除
    public int delAll(String[] ids) {
        return articleDao.deleteBatchIds(Arrays.asList(ids));
    }

}