package com.nowcoder.community.util;

/**
 * @author xi_wang
 * @create 2021-12-2021/12/19-13:16
 */
public interface CommunityConstant {
    // 激活状态
    int ACTIVATION_SUCCESS=0;
    int ACTIVATION_REPEAT=1;
    int ACTIVATION_FAILURE=2;
    // 是否勾选“记住我”，登录凭证的失效时间
    int NORMAL_EXPIRED_SECOND=60*10;
    int LONG_EXPIRED_SECOND=60*60*24*7;
}
