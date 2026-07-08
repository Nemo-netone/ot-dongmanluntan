package com.admin.controller;

import com.admin.entity.Article;
import com.admin.service.ArticleService;
import com.admin.entity.Category;
import com.admin.service.CategoryService;
import com.admin.sys.base.entity.ResultInfo;
import com.admin.sys.base.module.extend.web.BaseController;
import com.admin.sys.utils.chart.ChartUtils;
import com.admin.sys.utils.chart.data.DataLine;
import com.admin.sys.utils.chart.data.DataPie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *【图表统计】页面接口
 */
@RestController
@RequestMapping("/admin/count")
public class CountController extends BaseController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ArticleService articleService;

    /**
     * 【类别信息】数据统计
     * @param article
     * @return
     */
    @RequestMapping("/getCount")
    public ResultInfo getCount(Article article) {
        Map<String, Object> data = new HashMap<>();
        List<Article> list = articleService.getList(article);
        DataLine line = ChartUtils.createLine();
        DataPie pie = ChartUtils.createPie();
        //数据分组统计
        Map<String, List<Article>> collect = list.stream().collect(Collectors.groupingBy(Article::getCategoryId));
        for (Map.Entry<String, List<Article>> entry : collect.entrySet()) {
            Category category = categoryService.get(entry.getKey()+"");
            String name = category == null ? "未分类" : category.getName();
            //柱状图
            line.getxData().add(name);
            line.getyData().add(entry.getValue().size() + "");
            //饼状图
            DataPie.Item item = pie.createItem();
            item.setName(name);
            item.setValue(entry.getValue().size() + "");
            pie.getData().add(item);
        }
        data.put("line", line);
        data.put("pie", pie);
        return ResultInfo.ok("获取列表成功", data);
    }

}



