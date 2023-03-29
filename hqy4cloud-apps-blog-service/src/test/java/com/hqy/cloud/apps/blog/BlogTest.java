package com.hqy.cloud.apps.blog;

import cn.easyes.starter.register.EsMapperScan;
import com.hqy.cloud.apps.blog.document.TestDocument;
import com.hqy.cloud.apps.blog.service.TestService;
import com.hqy.cloud.util.identity.ProjectSnowflakeIdWorker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/29 10:02
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@EsMapperScan("com.hqy.cloud.apps.blog.es.mapper")
public class BlogTest {

    @Resource
    private TestService testService;

    @Test
    public void testCreateIndex() {
        boolean test = testService.createIndex("test");
    }

    @Test
    public void testExistIndex() {
        boolean test = testService.checkIndexExist("test");
    }


    @Test
    public void insertDocument() {
        TestDocument document = new TestDocument();
        long nextId = ProjectSnowflakeIdWorker.getInstance().nextId();
        document.setId(nextId + "");
        document.setName("haha");
        document.setDescription("");
        testService.addDocument(document, "test");
        TestDocument testDocument = testService.getDocument("test", nextId + "");
        System.out.println(testDocument);

    }



}
