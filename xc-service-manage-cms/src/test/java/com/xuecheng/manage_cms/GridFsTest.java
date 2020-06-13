package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFsTest {
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;

    //存文件
    @Test
    public void testGridFsTemplate() throws FileNotFoundException {
        //
        File file = new File("d:/index_banner.ftl");
        FileInputStream inputStream = new FileInputStream(file);

        ObjectId store = this.gridFsTemplate.store(inputStream, "index_banner.ftl");
        System.out.println(store);
    }

    //5ec0e9457d80a32b601babe2
    @Test
    public void queryFile() throws IOException {
        GridFSFile gridFSFile = this.gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("5ec0f10a7d80a329f04e9ff6")));
        //打开一个下载流
        GridFSDownloadStream gridFSDownloadStream = this.gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建gridFSResource对象，获取流
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        //从流中获取数据
        String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        System.out.println(content);
    }

}
