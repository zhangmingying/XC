package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {
    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Test
    public void testFindAllPageable() {
        //分页 page 从0开始
        Pageable pageable = PageRequest.of(1, 10);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);

        for (CmsPage cmsPage : all.getContent()) {
            System.out.println(cmsPage);
        }
    }

    @Test
    public void testFindAll() {
        List<CmsPage> all = cmsPageRepository.findAll();
        for (CmsPage cmsPage : all) {
            System.out.println(cmsPage);
        }
    }

    @Test
    public void testUpdate() {
        //查询对象\
        Optional<CmsPage> optional = cmsPageRepository.findById("5a754adf6abb500ad05688d9");
        if (optional.isPresent()) {
            CmsPage cmsPage = optional.get();
            //设置修改的值
            cmsPage.setPageAliase("test111111");
            //修改
            CmsPage save = cmsPageRepository.save(cmsPage);
            System.out.println(save);
        }

    }

    @Test
    public void testFindAllByExample() {
        //分页 page 从0开始
        Pageable pageable = PageRequest.of(0, 10);
        CmsPage cmsPage = new CmsPage();
//        cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        cmsPage.setPageAliase("轮");
//        cmsPage.setTemplateId("5a962b52b00ffc514038faf7");
        //定义example
        ExampleMatcher matcher = ExampleMatcher.matching();

        ExampleMatcher ex = matcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<CmsPage> example = Example.of(cmsPage, ex);
        Page<CmsPage> pages = cmsPageRepository.findAll(example, pageable);
        System.out.println(pages.getContent());
    }
}
