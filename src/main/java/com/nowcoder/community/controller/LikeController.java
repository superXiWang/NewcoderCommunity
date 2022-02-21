package com.nowcoder.community.controller;

import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xi_wang
 * @create 2022-02-2022/2/20-14:58
 */
@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;

    // 1. 对帖子详情页面的点赞功能，提供异步请求，点赞后不刷新页面，只更新点赞数据和点赞状态。
    @RequestMapping(value = "/like",method= RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId){
        int userId = hostHolder.getValue().getId();
        // 点赞
        likeService.like(userId,entityType,entityId,entityUserId);
        // 更新点赞数量与点赞状态
        long likeCount = likeService.getLikeCount(entityType, entityId);
        int likeStatus = likeService.getLikeStatus(userId, entityType, entityId);
        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);
        return CommunityUtil.getJSONString(0,null,map);
    }
}
