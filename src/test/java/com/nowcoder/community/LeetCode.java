package com.nowcoder.community;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import java.util.Scanner;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xi_wang
 * @create 2022-02-2022/2/17-10:37
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LeetCode {
    @Test
    public void testSolution001(){
        TreeNode root=new TreeNode(1);
        root.left=new TreeNode(2);
        root.right=new TreeNode(3);

        Solution001 solution001=new Solution001();
        System.out.println(solution001.isUnivalTree(root));
    }


}
class MyThread implements Runnable{

    @Override
    public void run() {
        for(int i=0;i<10;i++){
            System.out.println("输出线程正在执行");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
class Solution001 {
    long fore=0L;
    long back=0L;
    public boolean isUnivalTree(TreeNode root) {
        // 使用2个long型变量，共128位，其中后100位中，第i+1位记录是否有i
        // 遍历root，广度优先
        if(root==null) return true;
        Deque<TreeNode> queue=new LinkedList<TreeNode>();
        queue.offer(root);
        while(!queue.isEmpty()){
            root=queue.poll();
            // 判断
            if(root.val<64 && ((back>>root.val) & 1)==1){
                return false;
            }else{
                back=back | (1<<root.val);
            }
            if(root.val>=64 && (((fore>>(root.val-64)) & 1) ==1)){
                return false;
            }else{
                fore=fore | (1<<(root.val-64));
            }
            if(root.left!=null) queue.offer(root.left);
            if(root.right!=null) queue.offer(root.right);
        }
        return true;
    }
}
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}