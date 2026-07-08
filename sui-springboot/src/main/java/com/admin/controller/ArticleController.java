package com.admin.controller;

import com.admin.entity.Article;
import com.admin.service.ArticleService;
import com.admin.sys.base.entity.ResultInfo;
import com.admin.sys.base.module.extend.web.BaseController;
import com.admin.sys.utils.admin.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
*【文章信息】页面接口
*/
@RestController
@RequestMapping("/admin/article")
public class ArticleController extends BaseController {

    @Autowired
    private ArticleService articleService;

    /**
    * 【文章信息】根据条件分页查询
    * @param page
    * @param article
    * @return
    */
    @RequestMapping("/getPage")
    public ResultInfo getPage(Page<Article> page, Article article) {
        IPage<Article> iPage = articleService.getPage(page, article);
        return ResultInfo.ok("获取分页成功", iPage);
    }

    /**
    * 【文章信息】根据条件查询
    * @param article
    * @return
    */
    @RequestMapping("/getList")
    public ResultInfo getList(Article article) {
        List<Article> list = articleService.getList(article);
        return ResultInfo.ok("获取列表成功", list);
    }

    /**
    * 【文章信息】根据对象查询
    * @param article
    * @return
    */
    @RequestMapping("/get")
    public ResultInfo get(Article article) {
        Article entity = articleService.get(article);
        return ResultInfo.ok("获取对象成功", entity);
    }

    /**
    * 【文章信息】提交(新增或修改)
    * @param article
    * @return
    */
    @RequestMapping("/sub")
    public ResultInfo insert(Article article) {
        if (StringUtils.isEmpty(article.getId())) { //新增
            articleService.insert(article);
        } else {//修改
            articleService.update(article);
        }
        return ResultInfo.ok("提交成功!");
    }

    /**
    * 【文章信息】删除
    * @param id
    * @return
    */
    @RequestMapping("/delete")
    public ResultInfo delete(String id) {
        articleService.delete(id);
        return ResultInfo.ok("删除成功!");
    }

    /**
    * 【文章信息】批量删除
    * @param ids
    * @return
    */
    @RequestMapping("/delAll")
    public ResultInfo delAll(String[] ids) {
        articleService.delAll(ids);
        return ResultInfo.ok("批量删除成功！");
    }



}



