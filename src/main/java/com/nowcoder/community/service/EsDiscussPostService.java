package com.nowcoder.community.service;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.util.elasticsearch.DiscussPostDocumentOperations;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author xi_wang
 * @create 2022-05-2022/5/23-21:21
 */
@Service
public class EsDiscussPostService {
    @Autowired
    public DiscussPostDocumentOperations operations;

    public static final String[] searchFields=new String[]{"title","content"};

    // 向Elasticsearch增加帖子
    public void insertDiscussPost(DiscussPost discussPost){
        operations.insert(String.valueOf(discussPost.getId()),discussPost);
    }
    // 在Elasticsearch中 删除帖子
    public void insertDiscussPost(int postId){
        operations.delete(String.valueOf(postId));
    }
    // 在Elasticsearch中 搜索帖子
    public Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit){
        SearchResponse response = operations.search(keyword, current, limit, searchFields);
        // 用List收集结果，再用PageImpl转为Page
        List<DiscussPost> resList=new ArrayList<>();
        if (response.status().getStatus() == 200) {
            for (SearchHit hit : response.getHits().getHits()) {
                DiscussPost post=new DiscussPost();
                // 数据来源
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                post.setId((Integer)sourceAsMap.get("id"));
                post.setScore((Double) sourceAsMap.get("score"));
                post.setStatus((Integer) sourceAsMap.get("status"));
                post.setType((Integer) sourceAsMap.get("type"));
                post.setCommentCount((Integer) sourceAsMap.get("commentCount"));

//                SimpleDateFormat df = new SimpleDateFormat();
//                df.applyPattern("yyyy-MM-dd HH:mm:ss z");
//                df.setLenient(false);//设置解析日期格式是否严格解析日期
//                ParsePosition pos = new ParsePosition(0);
//                String createTime1 = (String) sourceAsMap.get("createTime");
//                System.out.println("日期数据源："+createTime1);
//                Date createTime = df.parse((String) sourceAsMap.get("createTime"), pos);
////                    Date createTime = new SimpleDateFormat().parse((String) sourceAsMap.get("createTime"));
//                if(createTime==null) System.out.println("日期转换失败");
//                post.setCreateTime(createTime);
                try {
                    post.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").parse((String) sourceAsMap.get("createTime")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                post.setUserId((Integer) sourceAsMap.get("userId"));
                // content与title需要先判断是否有高亮字段
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if(highlightFields.containsKey("content")){
                    HighlightField content = highlightFields.get("content");
                    post.setContent(content.getFragments()[0].toString());
                }else{
                    post.setContent((String) sourceAsMap.get("content"));
                }
                if(highlightFields.containsKey("title")){
                    HighlightField title = highlightFields.get("title");
                    post.setTitle(title.getFragments()[0].toString());
                }else{
                    post.setTitle((String) sourceAsMap.get("title"));
                }

                resList.add(post);
                Page<DiscussPost> res= new PageImpl<DiscussPost>(resList);
                return res;
            }
        }
        return null;
    }
}
