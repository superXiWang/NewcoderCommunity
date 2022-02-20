package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author xi_wang
 * @create 2021-12-2021/12/31-14:00
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public String insertDiscussPost(String title, String content){
        User user = hostHolder.getValue();
        if(user==null){
            return CommunityUtil.getJSONString(403,"你还没有登录哦！");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
//        discussPost.setType(0);
//        discussPost.setStatus(0);
        discussPost.setCreateTime(new Date());
//        discussPost.setCommentCount(1);
//        discussPost.setScore(1.1);
        discussPostService.insertDiscussPost(discussPost);
        return CommunityUtil.getJSONString(0,"发布成功！");
    }

    @RequestMapping(value = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String findDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        DiscussPost discussPost = discussPostService.findDiscussPost(discussPostId);
        model.addAttribute("discussPost",discussPost);

        // 查询发帖用户并添加到model
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user",user);
        // 查询该贴子的点赞数量、点赞装填，并添加到model
        long likeCount = likeService.getLikeCount(ENTITY_TYPE_DISCUSSPOST, discussPostId);
        model.addAttribute("likeCount",likeCount);
        int likeStatus = hostHolder.getValue()==null ? 0 :
                likeService.getLikeStatus(hostHolder.getValue().getId(), ENTITY_TYPE_DISCUSSPOST, discussPostId);
        model.addAttribute("likeStatus",likeStatus);

        // 评论分页设置
        page.setLimit(5);   // 每页显示5条评论
        page.setRows(discussPost.getCommentCount());
        page.setPath("/discuss/detail/"+discussPostId);

        // 查询该贴下的所有评论并添加到model
        List<Comment> commentsList = commentService.getCommentsListByEntity(ENTITY_TYPE_DISCUSSPOST, discussPostId, page.getOffset(), page.getLimit());
        // 由于 comment 中的 userId 在实际使用中并不方便，将user查到后封装到 Map中；
        // 除此之外将针对该 comment 的其他 reply(本质仍然是comment) 封装在列表中装进 Map里。
        List<Map<String, Object>> commentsViewObjectList = new ArrayList<>();
        if(commentsList!=null){
            // 针对每个评论，需要添加评论内容、评论作者、回复列表、回复总数、点赞状态、点赞总数
            for(Comment eachComment:commentsList){
                Map<String, Object> commentViewObject = new HashMap<>();
                // 添加评论内容
                commentViewObject.put("comment", eachComment);
                // 添加评论作者
                commentViewObject.put("author",userService.findUserById(eachComment.getUserId()));
                // 添加针对评论的回复列表
                List<Comment> replysList = commentService.getCommentsListByEntity(ENTITY_TYPE_COMMENT, eachComment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replysViewObjectList = new ArrayList<>();
                if(replysList!=null){
                    // 针对每个回复，增加回复内容、回复作者、目标用户、该回复的点赞状态与点赞总数
                    for(Comment eachReply:replysList){
                        Map<String, Object> replyViewObject = new HashMap<>();
                        // 添加回复内容
                        replyViewObject.put("reply", eachReply);
                        // 添加回复作者
                        replyViewObject.put("author", userService.findUserById(eachReply.getUserId()));
                        // 添加目标用户
                        User targetUser = eachReply.getTargetId()==0 ? null : userService.findUserById(eachReply.getTargetId());
                        replyViewObject.put("targetUser", targetUser);
                        // 添加点赞总数与点赞状态
                        likeCount = likeService.getLikeCount(ENTITY_TYPE_COMMENT, eachReply.getId());
                        replyViewObject.put("likeCount",likeCount);
                        likeStatus = hostHolder.getValue()==null ? 0 :
                                likeService.getLikeStatus(hostHolder.getValue().getId(), ENTITY_TYPE_COMMENT, eachReply.getId());
                        replyViewObject.put("likeStatus",likeStatus);

                        replysViewObjectList.add(replyViewObject);
                    }
                }
                commentViewObject.put("replysViewObjectList", replysViewObjectList);
                // 添加针对评论的回复总数
                commentViewObject.put("replysCount", commentService.getCommentsCountByEntity(ENTITY_TYPE_COMMENT, eachComment.getId()));
                // 添加点赞总数与点赞状态
                likeCount = likeService.getLikeCount(ENTITY_TYPE_COMMENT, eachComment.getId());
                commentViewObject.put("likeCount",likeCount);
                likeStatus = hostHolder.getValue()==null ? 0 :
                        likeService.getLikeStatus(hostHolder.getValue().getId(), ENTITY_TYPE_COMMENT, eachComment.getId());
                commentViewObject.put("likeStatus",likeStatus);

                commentsViewObjectList.add(commentViewObject);
            }
        }
        model.addAttribute("commentsViewObjectList", commentsViewObjectList);
        //model.addAttribute("page",page); // page属于bean容器管理，因此已自动加入model
        return "/site/discuss-detail";
    }
}
