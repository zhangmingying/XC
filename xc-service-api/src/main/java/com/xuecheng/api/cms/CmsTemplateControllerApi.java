package com.xuecheng.api.cms;

import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

public interface CmsTemplateControllerApi {
    QueryResponseResult findAll();

    QueryResponseResult findBySiteId(String siteId);
}
