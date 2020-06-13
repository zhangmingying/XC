package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteService {
    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    public QueryResponseResult findAll() {
        List<CmsSite> cmsSites = this.cmsSiteRepository.findAll();
        QueryResult<CmsSite> queryResult = new QueryResult<>();
        queryResult.setList(cmsSites);
        queryResult.setTotal(cmsSites.size());
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        queryResponseResult.setQueryResult(queryResult);
        return queryResponseResult;
    }
}
