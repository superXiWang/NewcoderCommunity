package com.nowcoder.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

/**
 * @author xi_wang
 * @create 2022-02-2022/2/18-21:39
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TestRedis {
    @Autowired
    public RedisTemplate redisTemplate;

    @Test
    public void testRedis(){
        String key1 = "test:key1";
        redisTemplate.opsForValue().set(key1,1);
        redisTemplate.opsForValue().increment(key1);
        System.out.println(redisTemplate.opsForValue().get(key1));

        // 哈希表
        String hash = "test:hash";
        redisTemplate.opsForHash().put(hash,"hashKey3",3);
        redisTemplate.opsForHash().put(hash,"hashKey4",4);
        System.out.println(redisTemplate.opsForHash().get(hash,"hashKey3"));
        System.out.println(redisTemplate.opsForHash().size(hash));

        // 列表
        String list = "test:list";
        redisTemplate.opsForList().leftPush(list, 1);
        //System.out.println(redisTemplate.opsForList().indexOf(list,1)); // 0

        redisTemplate.delete(key1);
        System.out.println(redisTemplate.hasKey(key1));
        redisTemplate.expire(list,10, TimeUnit.SECONDS);

        // 事务
        Object executeResult = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi(); //开启事务
                operations.opsForValue().set("test:tx",1);
                System.out.println(operations.opsForValue().get("text:tx"));
                return operations.exec(); // 结束事务
            }
        });
        System.out.println(executeResult);
    }
}
