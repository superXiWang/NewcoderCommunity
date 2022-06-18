package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.EsDiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xi_wang
 * @create 2022-05-2022/5/24-8:58
 */
@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private EsDiscussPostService esDiscussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    // url = /search?keyword=xxx
    @RequestMapping(value = "/search",method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model){
        System.out.println("--------------------------------------");
        System.out.println("进入SearchController的search()方法");

        // 搜索
        org.springframework.data.domain.Page<DiscussPost> searchResult = esDiscussPostService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        List<Map<String,Object>> discussPosts=null;
        if(searchResult!=null && searchResult.getTotalPages()!=0){
            model.addAttribute("keyword",keyword);
            // 将每个帖子的信息整理在Map中
            discussPosts=new ArrayList<>();
            for(DiscussPost post:searchResult){
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);
                map.put("user",userService.findUserById(post.getUserId()));
                map.put("likeCount",likeService.getLikeCount(ENTITY_TYPE_DISCUSSPOST,post.getId()));

                discussPosts.add(map);
            }

        }
        model.addAttribute("discussPosts",discussPosts);

        // 设置分页
        page.setPath("/search?keyword="+keyword);
        page.setRows(searchResult==null?0:searchResult.getSize());

        System.out.println("--------------------------------------");
        System.out.println("开始进入模板渲染");
        return "/site/search";
    }
}
