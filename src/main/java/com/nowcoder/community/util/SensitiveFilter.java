package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * 过滤敏感字
 *
 * @author xi_wang
 * @create 2021-12-2021/12/29-22:14
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    @PostConstruct
    public void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                ) {
            String keyword;
            while ((keyword=br.readLine())!=null){
                // 加载到前缀树中
                this.addKeyword(keyword);
            }

        } catch (IOException e) {
            logger.error("创建输入流失败："+e.getMessage());
        }

    }

    // 将指定字符串添加到前缀树中
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i=0;i<keyword.length();i++){
            Character c= keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            // 如果为空，则创建子节点
            if(subNode==null){
                subNode=new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            // 如果不为空，则不需要添加
            // 不论是否为空，到这一步时，tempNode都应向下移动
            tempNode = subNode;
            // 当结束时，设置结束标志
            if(i==keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }

    }

    /**
     * 过滤文本，将其中的敏感词替换为 *** 后输出
     *
     * @param text 待过滤的文本字符串
     * @return 过滤后的文本字符串
     */
    public String filter(String text){
        StringBuilder sb = new StringBuilder();
        // 依靠三个指针进行敏感词的判断
        // 指针1，tempNode，指向前缀树中已匹配的节点
        TrieNode tempNode = rootNode;
        // 指针2，head, 指向文本中，开始判断的词首字符
        int head=0;
        // 指针3，position, 指向文本中，正在判断的词尾字符
        int position=head;
        while(position<text.length()){
            Character c= text.charAt(position);
            // 如果字符为无效字符，跳过
            if(isInvalid(c)){
                // case 1，head=position，此时不需要判断以head为首的字符，head++，照常记录字符
                if(tempNode==rootNode){
                    head++;
                    sb.append(c);
                }
                // 不论 if 是否为真，position都要++
                position++;
                continue;
            }
            // 字符为有效字符
            TrieNode subNode = tempNode.getSubNode(c);
            // 如果找不到这个子节点，说明不是敏感词，head++, position=head，temp归位，照常记录
            if(subNode==null){
                sb.append(c);
                head++;
                position=head;
                tempNode = rootNode;
            }else {
                // 有该子节点
                // 如果有结束标记，说明找到了敏感词，用***替代正常的记录，head=++position,temp归位
                if(subNode.judgeIsKeywordEnd()){
                    sb.append("***");
                    head=++position;
                    tempNode = rootNode;
                }else{
                    // 否则，position++, tempNode下移， 继续循环
                    position++;
                    tempNode = subNode;
                    continue;
                }
            }
        }
        // 循环结束后，将head-最后的字符记录
        sb.append(text.substring(head));
        return sb.toString();
    }
    // 判断字符是否为无效字符
    private boolean isInvalid(Character character){
        // 0x2E80 - 0x9FFF 是东亚字符，会被认为是无效字符，需要排除掉
        return CharUtils.isAsciiAlphanumeric(character) && (character<0x2E80 || character>0x9FFF);
    }

    private TrieNode rootNode=new TrieNode();
    /**
     * 前缀树类
     */
    private class TrieNode {

        private Map<Character, TrieNode> subNodes = new HashMap<>();
        private boolean isKeywordEnd = false;

        public boolean judgeIsKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c,node);
        }
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
