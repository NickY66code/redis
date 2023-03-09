package org.example.test;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * @author noah
 * @version 1.0
 * @Description 事务
 * Create by 2023/1/31 13:53
 */
public class TestTx {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.138.35",6379);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name","noah");
        jsonObject.put("type","hello");
        String result=jsonObject.toJSONString();

        //开启事务
        Transaction multi = jedis.multi();

        try {
            multi.set("user1",result);
            multi.set("user2",result);

            //执行事务
            multi.exec();
        } catch (Exception e) {
            //放弃事务
            multi.discard();
            throw new RuntimeException(e);
        } finally {
            System.out.println(jedis.get("user1"));
            System.out.println(jedis.get("user2"));
            //关闭连接
            jedis.close();
        }
    }



}
