//package com.nowcoder.community;
//
//import com.nowcoder.community.entity.DiscussPost;
//import com.nowcoder.community.service.DiscussPostService;
//import com.nowcoder.community.service.EsDiscussPostService;
//import com.nowcoder.community.service.EsIndexServiceImpl;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Optional;
//
///**
// * @author xi_wang
// * @create 2022-05-2022/5/19-17:02
// */
//@SpringBootTest
//@ContextConfiguration(classes = CommunityApplication.class)
//public class TestElasticsearch {
//    @Autowired
//    EsIndexServiceImpl esIndexIndexService;
//    @Autowired
//    EsDiscussPostService esDiscussPostService;
//    @Autowired
//    DiscussPostService discussPostService;
//
//    @Test
//    public void testES(){
//        try {
//            // 给ES插入一条数据
//            DiscussPost discussPost = discussPostService.findDiscussPost(287);
//            DiscussPost inserted = esDiscussPostService.addDiscussPost(discussPost);
//            System.out.println("插入结果："+inserted);
//            // 用ES查询插入的这条数据
//            Optional<DiscussPost> findRes = esDiscussPostService.findDiscussPostById(String.valueOf(discussPost.getId()));
//            System.out.println("查询结果："+findRes.get());
//            // 用ES搜索关键词 “敏感“
//            List<DiscussPost> searchRes = esDiscussPostService.searchDiscussPosts("敏感", 1, 10);
//            for(DiscussPost each:searchRes){
//                System.out.println(searchRes);
//            }
//            // 用ES删除数据
////            esDiscussPostService.deleteDiscussPost(findRes.get());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    @Test
//    public void testESBulkInsert(){
//        try {
//            // 给ES插入多条数据
//            String s = esDiscussPostService.batchInsert(discussPostService.findDiscussPosts(101, 0, 100));
//            System.out.println(s);
//
//            esDiscussPostService.batchInsert(discussPostService.findDiscussPosts(102, 0, 100));
//            esDiscussPostService.batchInsert(discussPostService.findDiscussPosts(103, 0, 100));
//            esDiscussPostService.batchInsert(discussPostService.findDiscussPosts(111, 0, 100));
//            esDiscussPostService.batchInsert(discussPostService.findDiscussPosts(112, 0, 100));
//            esDiscussPostService.batchInsert(discussPostService.findDiscussPosts(131, 0, 100));
//            esDiscussPostService.batchInsert(discussPostService.findDiscussPosts(132, 0, 100));
//            esDiscussPostService.batchInsert(discussPostService.findDiscussPosts(133, 0, 100));
//            esDiscussPostService.batchInsert(discussPostService.findDiscussPosts(134, 0, 100));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    @Test
//    public void testESsearch(){
//        try {
//            List<DiscussPost> searchRes = esDiscussPostService.searchDiscussPosts("互联网寒冬", 1, 10);
//            for(DiscussPost each:searchRes){
//                System.out.println(each);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//}
