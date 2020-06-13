package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.CommonResponseResult;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfigService {

    @Autowired
    private CmsConfigRepository cmsConfigRepository;

    public CommonResponseResult<CmsConfig> getConfigById(String id) {
        Optional<CmsConfig> optional = this.cmsConfigRepository.findById(id);
        if (optional.isPresent()) {
            return new CommonResponseResult<>(CommonCode.SUCCESS, optional.get());
        }
        return new CommonResponseResult<>(CommonCode.FAIL, null);
    }
}
