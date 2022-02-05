package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author xi_wang
 * @create 2021-12-2021/12/26-20:02
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users =new ThreadLocal<>();

    public User getValue(){
        return users.get();
    }

    public void setValue(User user){
        users.set(user);
    }
    public void clear(){
        users.remove();
    }
}
