package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

/**
 * @author xi_wang
 * @create 2021-12-2021/12/2-21:04
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testSelect(){
        User user1 = userMapper.selectById(111);
        System.out.println(user1);

        User liubei = userMapper.selectByName("liubei");
        System.out.println(liubei);

        User user3 = userMapper.selectByEmail("nowcoder11@sina.com");
        System.out.println(user3);
    }
    @Test
    public void testDiscussPostSelect(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for(DiscussPost each:discussPosts){
            System.out.println(each);
        }
        int allCount = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(allCount);

        List<DiscussPost> discussPosts_2 = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for(DiscussPost each:discussPosts_2){
            System.out.println(each);
        }
        int countOf149 = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(countOf149);
    }
    @Test
    public void testDiscussPostInsert(){
        DiscussPost discussPost=new DiscussPost();
        discussPost.setUserId(1);
        discussPost.setTitle("test");
        discussPost.setContent("test");
        discussPost.setType(0);
        discussPost.setStatus(0);
        discussPost.setCreateTime(new Date());
        discussPost.setCommentCount(1);
        discussPost.setScore(1.1);
        discussPostMapper.insertDiscussPost(discussPost);
    }
    @Test
    public void testLoginTicketInsert(){
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }
//    @Test
//    public void testLoginTicketSelectAndUpdate(){
//        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
//        System.out.println(loginTicket);
//        loginTicketMapper.updateStatus("abc",1);
//    }
}
