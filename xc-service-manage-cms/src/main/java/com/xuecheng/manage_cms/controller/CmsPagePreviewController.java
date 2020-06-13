package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

@Controller
public class CmsPagePreviewController extends BaseController {
    @Autowired
    private PageService pageService;

    //页面预览
    @GetMapping(value = "/cms/preview/{pageId}")
    public void preview(@PathVariable("pageId") String pageId) throws IOException {
        //执行静态化
        String pageHtml = pageService.getPageHtml(pageId);
        //通过response对象将内容输出
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(pageHtml.getBytes("utf-8"));
    }
}
