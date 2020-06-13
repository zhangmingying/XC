package com.xuecheng.framework.domain.cms.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@ApiModel
@Data
public class QueryPageRequest {
    @ApiModelProperty(name ="siteId",value = "站点名称")
    private String siteId;
    private String pageId;
    private String pageName;
    private String pageAliase;
    private String templateId;
}
