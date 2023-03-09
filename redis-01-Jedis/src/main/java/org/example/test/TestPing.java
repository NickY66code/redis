package org.example.test;

import redis.clients.jedis.Jedis;

public class TestPing {
    public static void main(String[] args) {
        //new Jedis对象 连接redis
        Jedis jedis = new Jedis("192.168.138.35",6379);
        //jedis 的所有方法都是redis的指令
        System.out.println(jedis.ping());
    }
}
