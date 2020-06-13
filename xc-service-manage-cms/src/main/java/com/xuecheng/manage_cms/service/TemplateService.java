package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {
    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;

    public QueryResponseResult findAll() {
        List<CmsTemplate> cmsTemplates = this.cmsTemplateRepository.findAll();
        QueryResult<CmsTemplate> q = new QueryResult<>();
        q.setTotal(cmsTemplates.size());
        q.setList(cmsTemplates);
        return new QueryResponseResult(CommonCode.SUCCESS, q);
    }

    public QueryResponseResult findBySiteId(String siteId) {
        List<CmsTemplate> cmsTemplates = this.cmsTemplateRepository.findBySiteId(siteId);
        QueryResult<CmsTemplate> q = new QueryResult<>();
        q.setTotal(cmsTemplates.size());
        q.setList(cmsTemplates);
        return new QueryResponseResult(CommonCode.SUCCESS, q);
    }
}
