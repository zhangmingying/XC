package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.model.response.CommonResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "cms配置管理接口", description = "cms配置管理，提供数据模型的管理、查询接口")
public interface CmsConfigControllerApi {
    @ApiOperation("根据id查询CMS配置信息")
    public CommonResponseResult<CmsConfig> getModel(String id);
}
