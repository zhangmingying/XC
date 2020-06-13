package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsTemplateControllerApi;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cms/template")
public class CmsTemplateController implements CmsTemplateControllerApi {
    @Autowired
    private TemplateService templateService;

    @Override
    @GetMapping
    public QueryResponseResult findAll() {
        return this.templateService.findAll();
    }

    @Override
    @GetMapping("/siteId/{siteId}")
    public QueryResponseResult findBySiteId(@PathVariable("siteId") String siteId) {
        return this.templateService.findBySiteId(siteId);
    }
}
