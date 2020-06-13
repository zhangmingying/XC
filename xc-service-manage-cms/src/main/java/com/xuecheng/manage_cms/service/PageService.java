package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.*;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PageService {
    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private CmsTemplateRepository templateRepository;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        if (queryPageRequest == null) queryPageRequest = new QueryPageRequest();
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //条件值对象
        CmsPage cmsPage = new CmsPage();
        if (StringUtils.isNotBlank(queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        if (StringUtils.isNotBlank(queryPageRequest.getTemplateId())) {
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        if (StringUtils.isNotBlank(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        if (page <= 0) {
            page = 1;
        }
        page = page - 1;
        if (size <= 0) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size);
        Example<CmsPage> example = Example.of(cmsPage, matcher);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        QueryResult<CmsPage> queryResult = new QueryResult<>();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());
        QueryResponseResult responseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return responseResult;
    }

    @Transactional
    public CmsPageResult add(CmsPage cmsPage) {
        if (cmsPage == null) {
            //抛出异常，非法参数异常
        }

        //校验页面名称、站点Id、页面webpath的唯一性
        //根据字段查询cms_page集合，如果查到说明此页面已经存在
        CmsPage page = this.cmsPageRepository.findByPageNameAndAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (page != null) {
            //页面已经存在
            //抛出异常，异常内容就是页面已经存在
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        //调用dao新增页面
        cmsPage.setPageId(null);
        cmsPage.setPageCreateTime(new Date());
        CmsPage save = this.cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS, save);

    }

    public CmsPageResult findById(String id) {
        Optional<CmsPage> optional = this.cmsPageRepository.findById(id);
        if (optional.isPresent()) {
            return new CmsPageResult(CommonCode.SUCCESS, optional.get());
        }
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    @Transactional
    public CmsPageResult update(String id, CmsPage cmsPage) {
        CmsPageResult result = this.findById(id);
        if (result.isSuccess()) {
            CmsPage one = result.getCmsPage();
            //更新模板
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            // 更新页面别名             
            one.setPageAliase(cmsPage.getPageAliase());
            // 更新页面名称             
            one.setPageName(cmsPage.getPageName());
            // 更新访问路径             
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径             
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            one.setDataUrl(cmsPage.getDataUrl());
            //执行更新
            CmsPage save = this.cmsPageRepository.save(one);
            if (save != null) {
                return new CmsPageResult(CommonCode.SUCCESS, save);
            }
        }
        //失败
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    public ResponseResult delete(String id) {
        Optional<CmsPage> optional = this.cmsPageRepository.findById(id);
        if (optional.isPresent()) {
            this.cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }


    /**
     * 页面静态化
     */

    public String getPageHtml(String pageId) {
        //获取页面的DataUrl
        //请求DataUrl获取数据模型
        Map model = getModelByPageId(pageId);
        //获取页面的模板信息
        String template = this.getTemplateByPageId(pageId);
        if (StringUtils.isBlank(template)) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        //执行页面静态化
        return this.generateHtml(template, model);
    }

    //执行静态化
    private String generateHtml(String templateString, Map model) {
        //定义配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //使用模板加载器变为模板
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template", templateString);
        //在配置类中设置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板的内容
        Template template = null;
        try {
            template = configuration.getTemplate("template", "utf-8");
            //静态化
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取页面的模板信息
    private String getTemplateByPageId(String pageId) {
        CmsPage cmsPage = this.findById(pageId).getCmsPage();
        if (cmsPage == null) ExceptionCast.cast(CmsCode.CMS_PAGE_NOT_FOUND);
        //获取页面的模板Id
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isBlank(templateId)) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        Optional<CmsTemplate> optional = this.templateRepository.findById(templateId);
        if (!optional.isPresent()) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        String templateFileId = optional.get().getTemplateFileId();
        if (StringUtils.isBlank(templateFileId)) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        GridFSFile gridFSFile = this.gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
        //打开一个下载流
        GridFSDownloadStream gridFSDownloadStream = this.gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建gridFSResource对象，获取流
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        //从流中获取数据
        try {
            String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map getModelByPageId(String pageId) {
        CmsPage cmsPage = this.findById(pageId).getCmsPage();
        if (cmsPage == null) ExceptionCast.cast(CmsCode.CMS_PAGE_NOT_FOUND);
        //取出页面的data
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isBlank(dataUrl)) ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        ResponseEntity<Map> forEntity = this.restTemplate.getForEntity(dataUrl, Map.class);
        if (forEntity.getBody() == null || forEntity.getBody().get("data") == null)
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        return (Map) forEntity.getBody().get("data");
    }

    public ResponseResult post(String pageId) {
        //执行页面静态化
        String pageHtml = this.getPageHtml(pageId);
        //将页面静态化文件存储到GridFS中
        this.saveHtml(pageId, pageHtml);
        //向MQ发送消息
        this.sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //向mq发送消息
    private void sendPostPage(String pageId) {
        //得到页面信息
        CmsPage cmsPage = this.findById(pageId).getCmsPage();
        if (cmsPage == null) ExceptionCast.cast(CmsCode.CMS_PAGE_NOT_FOUND);
        //创建消息对象
        Map<String, String> msg = new HashMap<>();
        msg.put("pageId", pageId);
        //转成json
        String s = JSON.toJSONString(msg);
        //发送给mq
        //站点id
        String siteId = cmsPage.getSiteId();
        this.rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE, siteId, s);
    }

    //保存html 到GridFS
    private CmsPage saveHtml(String pageId, String htmlContent) {

        //先得到页面的信息
        CmsPage cmsPage = this.findById(pageId).getCmsPage();
        if (cmsPage == null) ExceptionCast.cast(CmsCode.CMS_PAGE_NOT_FOUND);
        ObjectId objectId = null;
        try {
            //将htmlContent转成输入流
            InputStream inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
            //将html文件内容保存到GridFS
            objectId = this.gridFsTemplate.store(inputStream, cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //将html文件Id更新到cmspage
        cmsPage.setHtmlFileId(objectId.toHexString());
        this.cmsPageRepository.save(cmsPage);
        return cmsPage;
    }
}
