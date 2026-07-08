package com.front;

import com.admin.entity.Article;
import com.admin.entity.Integral;
import com.admin.service.ArticleService;
import com.admin.service.IntegralService;
import com.admin.sys.base.entity.ResultInfo;
import com.admin.sys.base.module.extend.web.BaseController;
import com.admin.sys.utils.admin.StringUtils;
import com.admin.sys.utils.jwt.JwtUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
*【文章信息】管理
*/
@RestController
@RequestMapping("/api/article")
public class ApiArticleController extends BaseController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private IntegralService integralService;

    /**
     *【文章信息】获取分页数据
     */
    @RequestMapping("/getPage")
    public ResultInfo getPage(Page<Article> page, Article article) {
        article.setAuditStatus(2);//审核通过
        IPage<Article> iPage = articleService.getPage(page, article);
        return ResultInfo.ok("获取分页成功",iPage);
    }

    /**
     *【文章信息】获取用户文章
     */
    @RequestMapping("/getUserPage")
    public ResultInfo getUserPage(Page<Article> page, Article article) {
        article.setUserId(JwtUtils.getCurrentUserId());
        IPage<Article> iPage = articleService.getPage(page, article);
        return ResultInfo.ok("获取分页成功",iPage);
    }

    /**
    * 【文章信息】获取列表数据
    */
    @RequestMapping("/getList")
    public ResultInfo getList(Article article) {
        List<Article> list = articleService.getList(article);
        return ResultInfo.ok("获取列表成功", list);
    }

    /**
    *【文章信息】根据对象数据
    */
    @RequestMapping("/get")
    public ResultInfo get(Article article) {
        Article entity = articleService.get(article);
        return ResultInfo.ok("获取成功",entity);
    }

    /**
     *【文章信息】保存或修改
     */
    @RequestMapping("/sub")
    public ResultInfo saveOrUpdate(HttpServletRequest request,Article article){
        try {
            if(StringUtils.isEmpty(article.getId())){
                article.setPublishTime(new Date());
                article.setUserId(JwtUtils.getCurrentUserId());
                article.setAuditStatus(1);//待审核
                articleService.insert(article);//新增
                //添加积分
                Integral integral = new Integral();
                integral.setName("文章积分");
                integral.setIntegralTime(new Date());
                integral.setIntegralNum(5);
                integral.setRegisterId(JwtUtils.getCurrentUserId());
                integral.setRemarks("发布文章获得5积分");
                integralService.insert(integral);
                return ResultInfo.ok("保存成功！");
            }else{
                articleService.update(article);//修改
                return ResultInfo.ok("修改成功！");
            }
        } catch (Exception e) {
            return ResultInfo.error("保存失败！失败信息:"+e.getMessage());
        }
    }

    /**
     *【文章信息】根据id删除
     */
    @RequestMapping("/delete")
    public ResultInfo delete(HttpServletRequest request, Article article) {
        articleService.delete(article.getId());
        return ResultInfo.ok("删除成功！");
    }

    /**
    * 【文章信息】批量删除
    */
    @RequestMapping("/delAll")
    public ResultInfo delAll(String[] ids) {
        articleService.delAll(ids);
        return ResultInfo.ok("批量删除成功！");
    }

}



