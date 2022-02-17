package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
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
    @Autowired
    private MessageMapper messageMapper;

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
    @Test
    public void testMessageMapper(){
        List<Message> messages = messageMapper.selectConversationsListByUserId(111, 0, 20);
        for(Message each:messages){
            System.out.println(each);
        }
        System.out.println(messageMapper.selectConversationsCountByUserId(111));
        List<Message> messages1 = messageMapper.selectMessagesListByConversationId("111_112", 0, 20);
        for(Message each:messages1){
            System.out.println(each);
        }
        System.out.println(messageMapper.selectMessagesCountByConversationId("111_112"));
        System.out.println(messageMapper.selectUnreadMessagesCount(131,"111_131")); // suppose to be 2
        // 插入
        Message message =new Message();
        message.setFromId(2);
        message.setToId(3);
        message.setContent("2-3(2)");
        message.setConversationId("2_3");
        message.setCreateTime(new Date());
        messageMapper.insertMessage(message);
        // 更新状态
        List<Integer> ids = new ArrayList<>();
        ids.add(355);
        messageMapper.updateMessagesStatus(ids,2);

    }
}
