package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int insertDiscussPost(DiscussPost discussPost){
        if(discussPost==null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        // 对文本进行 html转义
        String title = discussPost.getTitle();
        title = HtmlUtils.htmlEscape(title);
        String content = discussPost.getContent();
        content = HtmlUtils.htmlEscape(content);

        // 对文本进行敏感词检查
        title = sensitiveFilter.filter(title);
        content = sensitiveFilter.filter(content);

        discussPost.setTitle(title);
        discussPost.setContent(content);

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    public DiscussPost findDiscussPost(int id){
        return discussPostMapper.findDiscussPost(id);
    }
}
