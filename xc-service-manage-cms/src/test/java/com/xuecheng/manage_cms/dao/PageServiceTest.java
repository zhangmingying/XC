package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms.service.PageService;
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
public class PageServiceTest {
    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private PageService pageService;

    @Test
    public void testGetPageHtml() {
            String pageHtml = this.pageService.getPageHtml("5ec0f6ed7d80a346e4c99fea");
        System.out.println(pageHtml);
    }


}
