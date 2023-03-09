# Redis

## 1.NoSQL概述

####   网站瓶颈

1. 数据量访问量太大，机器放不下
2. 数据得索引（B+ Tree）一个机器放不下
3. 访问量（读写混合），一个服务器承受不了

80%得网站基本都是读，因此可以通过缓存减轻数据得压力，保证效率

NoSQL==Not Only SQL不仅仅是SQL

关系型数据库：表格，行，列

很多数据类型用户得个人信息，社交网络，地理位置，这些数据类型得存储不需要一个固定的格式

#### NoSQL特点

1. 方便扩展（数据之间没有关系，很好扩展）
2. 大数据量高性能（Redis一秒写8万次，读取11万，NoSQL的缓存，是一种细粒度的缓存，性能高）
3. 数据类型是多样型的（不需要事先设计数据库，随取随用）
4. 传统RDBMS和NoSQL

```
 传统的RDBMS
 - 结构化组织
 - SQL
 - 数据和关系都存在的单独的表中 row col
 - 操作严格，数据定义语言
 - 严格的一致性
 - 基础的事务
 ...
```

```
NoSQL
- 不仅仅是数据
- 没有固定的查询语言
-  键值对存储，列存储，文档存储，图形数据库（社交关系）
- 最终一致性
- CAP定理和 BASE（异地多活） 
- 高性能、高可用、高可扩
...
```

#### 3v+3高

  3v：主要是描述问题的

1. 海量Volume
2. 多样Variety
3. 实时Velocity

3高：主要是对程序的要求

1. 高并发
2. 高可拓（随时水平拆分，机器不够了，可以拓展机器来）  
3. 高性能

```bash
# 1. 商品的基本信息
	名称、价格、商家信息；
	关系型数据库 MySQL/Oracle
	
# 2. 商品的描述、评论（文字比较多）
	文档型数据库 MongoDB

# 3. 图片
	分布式文件系统 FastDFS
	- 淘宝自己的 TFS
	- Google GFS
	- Hadoop HDFS
	- 阿里云的 OSS
	
# 4. 商品的关键字（搜索）
	- 搜索引擎 solr elasticsearch
	-ISearch：多隆
	
#5. 商品热门的波段信息
	- 内存数据库
	- Redis Tair Memecache 
#6. 商品的交易、外部的支付接口
	- 三方应用
```

##  2.Redis

(**Re**mote **Di**ctionary **S**erver)，即远程字典服务																												

#### Redis能干什么

1. 内存存储、持久化、内存中是断电即失、所以说持久化很重要
2. 效率高，可以用于高速缓存
3. 发布订阅系统
4. 地图信息分析
5. 计时器、计数器

#### Redis linux的安装

```bash
tar -zxvf redis-7.0.6
yum install gcc-c++
make
make install
```

redis路径

![](Redis.assets/image-20230129110600094.png)



配置文件

![image-20230129111142967](Redis.assets/image-20230129111142967.png)

redis默认不是后台启动的，需要修改配置文件

![image-20230129111904691](Redis.assets/image-20230129111904691.png)

启动redis服务

![image-20230129113820293](Redis.assets/image-20230129113820293.png)

使用redis-cli 进行测试

退出命令

![image-20230129114456299](Redis.assets/image-20230129114456299.png)

#### 性能测试

redis-benchmark 压力测试工具

![image-20230129134711969](Redis.assets/image-20230129134711969.png)

```bash
# 测试：100个并发连接
./redis-benchamrk -h localhost -p 6379 -c 100 -n 100000
```

#### redis 基础知识

redis默认有16个数据库

```bash
127.0.0.1:6379> SELECT 3 #切换数据库
OK
127.0.0.1:6379[3]> DBSIZE
(integer) 0
127.0.0.1:6379[3]> keys * #查看所有的key
1) "name"
27.0.0.1:6379[3]> flushdb #清除当前数据库
OK
127.0.0.1:6379> FLUSHALL #清空所有数据库
OK

```

redis是单线程的

官方表示 Redis是基于内存操作，cpu不是Redis的性能瓶颈，Redis的瓶颈是根据机器的内存喝网络带宽，既可以使用单线程来实现，就使用单线程了。

为什么单线程还那么快？

C语言写的，官方数据10w+QPS，完全不比同样使用key-value 的Memecache差！

**误区**

1. 高性能的服务器一定是多线程的？

2. 多线程（CPU上下文切换，耗时）一定比单线程效率高（CPU>内存>硬盘速度）

   核心：redis是所有数据都是存放在内存中的，所有说单线程去操作效率是最高的，对于内存系统，没有上下文切换效率最高

#### 五大数据类型

Redis 是一个开源（BSD许可）的，内存中的数据结构存储系统，它可以用作**数据库、缓存和消息中间件**。 它支持多种类型的数据结构，如 [字符串（strings）](http://www.redis.cn/topics/data-types-intro.html#strings)， [散列（hashes）](http://www.redis.cn/topics/data-types-intro.html#hashes)， [列表（lists）](http://www.redis.cn/topics/data-types-intro.html#lists)， [集合（sets）](http://www.redis.cn/topics/data-types-intro.html#sets)， [有序集合（sorted sets）](http://www.redis.cn/topics/data-types-intro.html#sorted-sets) 与范围查询， [bitmaps](http://www.redis.cn/topics/data-types-intro.html#bitmaps)， [hyperloglogs](http://www.redis.cn/topics/data-types-intro.html#hyperloglogs) 和 [地理空间（geospatial）](http://www.redis.cn/commands/geoadd.html) 索引半径查询。 Redis 内置了 [复制（replication）](http://www.redis.cn/topics/replication.html)，[LUA脚本（Lua scripting）](http://www.redis.cn/commands/eval.html)， [LRU驱动事件（LRU eviction）](http://www.redis.cn/topics/lru-cache.html)，[事务（transactions）](http://www.redis.cn/topics/transactions.html) 和不同级别的 [磁盘持久化（persistence）](http://www.redis.cn/topics/persistence.html)， 并通过 [Redis哨兵（Sentinel）](http://www.redis.cn/topics/sentinel.html)和自动 [分区（Cluster）](http://www.redis.cn/topics/cluster-tutorial.html)提供高可用性（high availability）。

##### Redis-Key

```bash
127.0.0.1:6379> EXISTS name #查询是否存在
(integer) 0 #1存在 0不存在
127.0.0.1:6379> EXPIRE name 10
(integer) 1 #设置过期时间
127.0.0.1:6379> TTL name
(integer) -2 #查询当前剩余过期时间
127.0.0.1:6379> TYPE name #查看当前key的类型
string
```

##### String （字符串）

```bash
127.0.0.1:6379> get key1
"v1"
127.0.0.1:6379> APPEND key1 "hello" #value追加字符串
(integer) 7
127.0.0.1:6379> get key1
"v1hello"
127.0.0.1:6379> STRLEN key1 #获取字符串长度
(integer) 7
127.0.0.1:6379> set views 0
OK
127.0.0.1:6379> get views
"0"
127.0.0.1:6379> incr views #增加数量1
(integer) 1
127.0.0.1:6379> incr views
(integer) 2
127.0.0.1:6379> get views
"2"
127.0.0.1:6379> decr views #减少数量1
(integer) 1
127.0.0.1:6379> get views
"1"
127.0.0.1:6379> INCRBY views 10 #增加10
(integer) 11
127.0.0.1:6379> DECRBY views 10 #减少10
(integer) 1
127.0.0.1:6379> get name
"hello,noah"
127.0.0.1:6379> GETRANGE name 0 3 #截取value长度
"hell"
127.0.0.1:6379> GETRANGE name 0 -1 #获取全部value
"hello,noah"
127.0.0.1:6379> set k2 abcdefg
OK
127.0.0.1:6379> get k2
"abcdefg"
127.0.0.1:6379> SETRANGE k2 1 xx #替换字符串
(integer) 7
127.0.0.1:6379> get k2
"axxdefg"
#setex (set with expire) 设置过期时间
127.0.0.1:6379> setex k3 30 "james" #设置k3 30s后过期
OK
127.0.0.1:6379> ttl k3
(integer) 16
#setnx (set if not exist) 不存在再设置
127.0.0.1:6379> setnx mykey "redis" #如果mykey不存在，创建mykey
(integer) 1
127.0.0.1:6379> keys *
1) "k2"
2) "mykey"
3) "name"
127.0.0.1:6379> ttl k3
(integer) -2
127.0.0.1:6379> setnx mykey "mongoDB" #如果mykey存在创建失败
(integer) 0
127.0.0.1:6379> get mykey
"redis"
127.0.0.1:6379> mset k1 v1 k2 v2 k3 v3 #批量set
OK
127.0.0.1:6379> keys *
1) "k2"
2) "k3"
3) "k1"
127.0.0.1:6379> mget k1 k2 k3 #批量get
1) "v1"
2) "v2"
3) "v3"
127.0.0.1:6379> msetnx k1 v1 k4 v4 #原子性操作 要么一起成功 要么一起失败
(integer) 0
127.0.0.1:6379> get k4 
(nil)
# key 的巧妙设计 user:{id}:{filed}
127.0.0.1:6379> mset user:1:name kobe user:1:age 40
OK
127.0.0.1:6379> mget user:1:name user:1:age
1) "kobe"
2) "40"
127.0.0.1:6379> getset db redis #如果不存在值返回nil
(nil)
127.0.0.1:6379> get db
"redis"
127.0.0.1:6379> getset db monogoddb #如果存在值返回原来的值 并更新新值
"redis"
127.0.0.1:6379> get db
"monogoddb"


```

String类似的使用场景：value除了字符串类型也可以是数字

- 计数器
- 统计多单位的数量
- 粉丝数
- 对象缓存存储

##### list

实际上是一个链表 before Node after ，left，right都可以插入。在两边插入或改动值，效率最高。中间元素修改效率会低一点。

消息队列（LPUSHRPOP） 栈（LPUSHLPOP）

再redis里，可以把list玩成队列、栈、阻塞队列

list命令 基本都带l开头

```bash
127.0.0.1:6379> LPUSH list one #将list值左侧插入
(integer) 1
127.0.0.1:6379> LPUSH list two
(integer) 2
127.0.0.1:6379> LPUSH list three
(integer) 3
127.0.0.1:6379> LRANGE list 0 -1 #
1) "three"
2) "two"
3) "one"
127.0.0.1:6379> RPUSH list right #将list值右侧插入
(integer) 4
127.0.0.1:6379> LPOP list #从左移出队列
"three"
127.0.0.1:6379> RPOP list #从右移出队列
"right"
127.0.0.1:6379> LRANGE list 0 -1
1) "two"
2) "one"
127.0.0.1:6379> LINDEX list 0 #查询下标的value
"two"
127.0.0.1:6379> LINDEX list 1
"one"
127.0.0.1:6379> LLEN list #返回列表长度
(integer) 2
127.0.0.1:6379> LREM list 1 one #移除1个值
(integer) 1
127.0.0.1:6379> LRANGE list 0 -1
1) "three"
2) "three"
3) "two"
127.0.0.1:6379> LREM list 0 three #移除所有值
(integer) 2
127.0.0.1:6379> LRANGE list 0 -1
1) "two"

127.0.0.1:6379> LRANGE list 0 -1
1) "three"
2) "two"
3) "three"
127.0.0.1:6379> LREM list -1 three #移除从前往后的一个值
(integer) 1
127.0.0.1:6379> LRANGE list 0 -1
1) "three"
2) "two"

127.0.0.1:6379> LRANGE list 0 -1
1) "four"
2) "three"
3) "two"
4) "one"
127.0.0.1:6379> LTRIM list 1 2 #截取list长度
OK
127.0.0.1:6379> LRANGE list 0 -1
1) "three"
2) "two"

127.0.0.1:6379> LRANGE mylist 0 -1
1) "four"
2) "three"
3) "two"
4) "one"
127.0.0.1:6379> RPOPLPUSH mylist myotherlist #组合命令 移除mylist 到 myotherlist内
"one"
127.0.0.1:6379> LRANGE mylist 0 -1
1) "four"
2) "three"
3) "two"
127.0.0.1:6379> LRANGE myotherlist 0 -1
1) "one"

127.0.0.1:6379> LSET mylist 0 kobe #将下标指定位置替换value list为空或无该下标则报错
OK
127.0.0.1:6379> LRANGE mylist 0 -1
1) "kobe"
2) "three"
3) "two"

127.0.0.1:6379> LRANGE mylist 0 -1
1) "kobe"
2) "three"
3) "two"
127.0.0.1:6379> LINSERT mylist after kobe forever #往指定的位置后插入
(integer) 4
127.0.0.1:6379> LRANGE mylist 0 -1
1) "kobe"
2) "forever"
3) "three"
4) "two"
127.0.0.1:6379> LINSERT mylist before kobe manba #往指定的位置前插入
(integer) 5
127.0.0.1:6379> LRANGE mylist 0 -1
1) "manba"
2) "kobe"
3) "forever"
4) "three"
5) "two"
```

##### set（集合）

set的值无序不重复集合

```bash
127.0.0.1:6379> SADD myset hello #set集合添加元素
(integer) 1
127.0.0.1:6379> SADD myset world
(integer) 1
127.0.0.1:6379> SADD myset noah
(integer) 1
127.0.0.1:6379> SMEMBERS myset #查看set集合的所有值
1) "noah"
2) "world"
3) "hello"
127.0.0.1:6379> SISMEMBER myset noah #查询是否存在某个值
(integer) 1
127.0.0.1:6379> SISMEMBER myset kobe
(integer) 0
127.0.0.1:6379> SCARD myset #获取集合个数
(integer) 3
127.0.0.1:6379> SREM myset hello #移除‘hello’元素
(integer) 1
127.0.0.1:6379> SMEMBERS myset
1) "noah"
2) "world"
127.0.0.1:6379> SRANDMEMBER myset #随机获取一个元素
"noah"
127.0.0.1:6379> SPOP myset #随机移除元素
"momo"
127.0.0.1:6379> SPOP myset
"jordan"
127.0.0.1:6379> SMEMBERS myset
1) "noah"
2) "kobe"
3) "world"
4) "james"

127.0.0.1:6379> SADD myset noah world james kobe rose
(integer) 5
127.0.0.1:6379> SADD myset2 set2
(integer) 1
127.0.0.1:6379> SMOVE myset myset2 rose #移动指定元素
(integer) 1
127.0.0.1:6379> SMEMBERS myset
1) "james"
2) "world"
3) "noah"
4) "kobe"
127.0.0.1:6379> SMEMBERS myset2
1) "set2"
2) "rose"


### 数字集合类 --差集 --并集 --交集
127.0.0.1:6379> SADD myset a b c d e f g
(integer) 7
127.0.0.1:6379> SADD myset2 f g h j
(integer) 4
127.0.0.1:6379> SDIFF myset myset2 #差集
1) "e"
2) "d"
3) "b"
4) "a"
5) "c"
127.0.0.1:6379> SINTER myset myset2 #交集
1) "f"
2) "g"
127.0.0.1:6379> SUNION myset myset2 #并集
1) "d"
2) "e"
3) "f"
4) "c"
5) "a"
6) "b"
7) "g"
8) "j"
9) "h"
```

##### hash

Map集合，key-Map<key,value>集合 值是map集合

hash可以用于变更的数据user name age 用于数据保存

hset user:1 name noah age 33尤其是用户信息等经常变动的信息

更适合对象的存储 String更适合字符串存储

```bash
127.0.0.1:6379> hset myhash f1 v1 #set key-value值
(integer) 1
127.0.0.1:6379> hget myhash f1 #获取hash值
"v1"
127.0.0.1:6379> hmset myhash f1 v2 f2 v2 #set多个hash值
OK
127.0.0.1:6379> hmget myhash f1 f2 #获取多个hash值
1) "v2"
2) "v2"
127.0.0.1:6379> hgetall myhash #获取所有key-value值
1) "f1"
2) "v2"
3) "f2"
4) "v2"
127.0.0.1:6379> hdel myhash f1 #删除一个hash值
(integer) 1
127.0.0.1:6379> hgetall myhash
1) "f2"
2) "v2"
127.0.0.1:6379> hlen myhash #获取hash字段长度
(integer) 1
127.0.0.1:6379> hexists myhash f1 #判断hash是否存在
(integer) 0
127.0.0.1:6379> hexists myhash f2
(integer) 1
127.0.0.1:6379> hkeys myhash #只获取所有field
1) "f2"
127.0.0.1:6379> hvals myhash #只获取所有value
1) "v2"
127.0.0.1:6379> HINCRBY myhash f5 1 #增加1
(integer) 6
127.0.0.1:6379> HINCRBY myhash f5 -1 #减少1
(integer) 5

127.0.0.1:6379> HGET myhash f5
"6"
127.0.0.1:6379> HSETNX myhash f6 noah #若不存在则创建
(integer) 1
127.0.0.1:6379> HSETNX myhash f6 rose #存在则创建失败
(integer) 0

```

##### zset

set排序 存储班级成绩，工资表排序 

普通消息1、重要消息2 带权重进行判断

排行榜应用场景

```bash
127.0.0.1:6379> ZADD myzset 1 one #插入set数值
(integer) 1
127.0.0.1:6379> ZADD myzset 2 two
(integer) 1
127.0.0.1:6379> ZADD myzset 2 three
(integer) 1
127.0.0.1:6379> ZRANGE myzset 0 -1 #显示全部
1) "one"
2) "three"
3) "two"
127.0.0.1:6379> zadd salary 2600 xiaoming
(integer) 1
127.0.0.1:6379> zadd salary 7600 kobe
(integer) 1
127.0.0.1:6379> zadd salary 500 noah
(integer) 1
127.0.0.1:6379> ZRANGEBYSCORE salary -inf +inf 
#升序 -inf 负无穷 +inf 正无穷
1) "noah"
2) "xiaoming"
3) "kobe"
127.0.0.1:6379> ZREVRANGEBYSCORE salary +inf -inf withscores #降序 withscores显示分数
1) "kobe"
2) "7600"
3) "noah"
4) "500"



127.0.0.1:6379> ZRANGE salary 0 -1
1) "noah"
2) "xiaoming"
3) "kobe"
127.0.0.1:6379> ZREM salary xiaoming #移除元素
(integer) 1
127.0.0.1:6379> ZRANGE salary 0 -1
1) "noah"
2) "kobe"
127.0.0.1:6379> ZREVRANGE salary 0 -1 #逆序排名
1) "kobe"
2) "noah"
127.0.0.1:6379> ZCARD salary #查看有多少元素
(integer) 2
127.0.0.1:6379> zcount salary 0 10000 #统计该范围有多少元素
(integer) 2
127.0.0.1:6379> zcount salary 0 1000
(integer) 1
```

#### 三种特殊数据类型

##### geospatial 地理位置

朋友的定位，附近的人，打车距离计算

Redis的Geo在Redis3.2版本就推出了，这个功能可以推算地理位置的信息，两地之间的距离，方圆几里的人

```bash
##GEOADD 添加地理位置
##规则：两极无法直接添加，一般会下载城市数据，通过java程序一次性导入
##参数 key 值（纬度、经度、名称）
127.0.0.1:6379> GEOADD china:city 110.405529 21.195338 zhanjiang
(integer) 1
127.0.0.1:6379> GEOADD china:city 113.280637 23.125178 guangzhou
(integer) 1
127.0.0.1:6379> GEOADD china:city 106.50496 29.533155 chongqing
(integer) 1
127.0.0.1:6379> GEOADD china:city 116.405285 39.904989 beijing
(integer) 1
127.0.0.1:6379> GEOADD china:city 120.153576 30.287459 hangzhou
(integer) 1
127.0.0.1:6379> GEOADD china:city 116.405285 39.90498 beijing
(integer) 0
127.0.0.1:6379> GEOADD china:city 108.948024 34.263161 xian
(integer) 1
127.0.0.1:6379> GEOADD china:city 118.11022 24.490474 xiamen
(integer) 1

##geopos获取执行城市的经度纬度
127.0.0.1:6379> GEOPOS china:city beijing chongqing
1) 1) "116.40528291463851929"
   2) "39.90498081874901715"
2) 1) "106.50495976209640503"
   2) "29.53315530684997015"

##geodist 返回两个给定位置之间的距离
## m米、km千米、mi英里、ft英尺
"1889369.7406"
127.0.0.1:6379> GEODIST china:city beijing guangzhou km #查看北京到广州的直线距离
"1889.3697"

##georadius 以给定的经度纬度为中心，找出某一半径内的元素
##附近的人
127.0.0.1:6379> GEORADIUS china:city 113.280637 23.125178 1000 km 
1) "guangzhou"
2) "xiamen"
3) "chongqing"
4) "zhanjiang"
127.0.0.1:6379> GEORADIUS china:city 113.280637 23.125178 1000 km withdist #withdist直线距离
1) 1) "guangzhou"
   2) "0.0001"
2) 1) "xiamen"
   2) "514.3512"
3) 1) "chongqing"
   2) "981.4771"
4) 1) "zhanjiang"
   2) "365.7502"
127.0.0.1:6379> GEORADIUS china:city 113.280637 23.125178 1000 km withcoord #withcoord经度纬度
1) 1) "guangzhou"
   2) 1) "113.28063815832138062"
      2) "23.12517743834835215"
2) 1) "xiamen"
   2) 1) "118.110218346118927"
      2) "24.49047457417236018"
3) 1) "chongqing"
   2) 1) "106.50495976209640503"
      2) "29.53315530684997015"
4) 1) "zhanjiang"
   2) 1) "110.40553003549575806"
      2) "21.19533706703172982"
127.0.0.1:6379> GEORADIUS china:city 113.280637 23.125178 1000 km withcoord withdist count 2 #count 数量
1) 1) "guangzhou"
   2) "0.0001"
   3) 1) "113.28063815832138062"
      2) "23.12517743834835215"
2) 1) "zhanjiang"
   2) "365.7502"
   3) 1) "110.40553003549575806"
      2) "21.19533706703172982"

##GEORADIUSBYMEMBER 根据key查找周围的其他元素
127.0.0.1:6379> GEORADIUSBYMEMBER china:city guangzhou 1000 km
1) "guangzhou"
2) "xiamen"
3) "chongqing"
4) "zhanjiang"
127.0.0.1:6379> GEORADIUSBYMEMBER china:city beijing 500 km
1) "beijing"

##geohash --返回一个或多个位置元素的Geohash标识
##该命令将返回11个字符的Geohash字符串
##将二维的经纬度转换为一维的字符串，如果两个字符串越接近，那么则距离越近
127.0.0.1:6379> GEOHASH china:city beijing guangzhou
1) "wx4g0b7xre0"
2) "ws0e9cb3yj0"
##geo底层实现原理是ZSET 因此可以使用Z命令进行操作
127.0.0.1:6379> ZRANGE china:city 0 -1
1) "zhanjiang"
2) "chongqing"
3) "xian"
4) "guangzhou"
5) "xiamen"
6) "hangzhou"
7) "beijing"
127.0.0.1:6379> ZREM china:city zhanjiang
(integer) 1
127.0.0.1:6379> ZRANGE china:city 0 -1
1) "chongqing"
2) "xian"
3) "guangzhou"
4) "xiamen"
5) "hangzhou"
6) "beijing"

```

##### Hyperloglog

什么是基数

A{1，3，5，7，8，7}

B{1，3，5，7，8}

基数（不重复的元素）=5，可以接受误差

Redis 2.8.9就更新了Htperloglog数据结构：基数统计算法

网页的uv（一个人访问一个网站多次，但是还是算作一个人）

优点：占用的内存是固定的，2^64不同的元素的基数，只需要12kb内存。从内存角度来讲Hyperloglog是首选

存在0.81%的错误率！统计UV任务，是允许的

允许容错使用Hyperloglog

不允许容错，就使用set或者自己的数据类型即可

传统方式，set保存用户id，然后可以统计set中的元素数量作为标准判断。但这种方法如果保存大量的用户id，就会比较麻烦。

目的是为了计数，而不是保存用户id

```bash
127.0.0.1:6379> PFADD mykey a b c d e f g h j i z w
(integer) 1 #创建第一组mykey
127.0.0.1:6379> PFADD mykey2 q w e r t b a s f e m
(integer) 1 #创建第二组mykey
127.0.0.1:6379> PFCOUNT mykey
(integer) 12 #统计mykey元素的基数数量
127.0.0.1:6379> PFCOUNT mykey2
(integer) 10 #统计mykey2元素的基数数量
127.0.0.1:6379> PFMERGE mykey3 mykey mykey2 #合并mykey和mykey2为mykey3
OK
127.0.0.1:6379> PFCOUNT mykey3
(integer) 16

```

##### Bitmaps

**位存储**

统计用户信息，活跃，不活跃。登录、未登录。打卡，365天打卡

**两个状态**的 都可以使用Bitmaps

Bitmaps位图，数据结构！都是操作二进制来进行记录，只有0和1两个状态

365天=365bit 1字节=8bit 46个字节左右

```bash
127.0.0.1:6379> setbit sign 0 1 #设置bit 从零开始 1打卡0未打卡
(integer) 0
127.0.0.1:6379> setbit sign 1 1
(integer) 0
127.0.0.1:6379> setbit sign 2 0
(integer) 0
127.0.0.1:6379> setbit sign 3 1
(integer) 0
127.0.0.1:6379> setbit sign 4 0
(integer) 0
127.0.0.1:6379> setbit sign 5 1
(integer) 0
127.0.0.1:6379> setbit sign 6 0
(integer) 0
127.0.0.1:6379> getbit sign 0 #获取bit
(integer) 1
127.0.0.1:6379> getbit sign 2
(integer) 0
127.0.0.1:6379> BITCOUNT sign #统计打卡数量
(integer) 4

```

#### 事务

Redis事务本质：一组命令的集合。一个事务中的所有的命令都会被序列化，在事务执行过程中，会按照顺序执行

一次性、顺序性、排他性！执行一些列的命令

---- 队列 set set set 执行 ----

**Redis单条命令是保存原子性的，但是事务是不保证原子性的**

**Redis事务没有隔离级别的概念**

所有的命令在事务中并没有被执行！只有发起执行命令的时候才会执行

Redis的事务：

- 开启事务（multi）
- 命令入队（.........）
- 执行事务 （exec）

```bash
127.0.0.1:6379> multi #开启事务
OK
#命令入队
127.0.0.1:6379(TX)> set k1 v1
QUEUED
127.0.0.1:6379(TX)> set k2 v2
QUEUED
127.0.0.1:6379(TX)> get k2
QUEUED
127.0.0.1:6379(TX)> set k3 v3
QUEUED
127.0.0.1:6379(TX)> exec #执行事务
1) OK
2) OK
3) "v2"
4) OK

##放弃事务
127.0.0.1:6379> MULTI
OK
127.0.0.1:6379(TX)> set k1 v1
QUEUED
127.0.0.1:6379(TX)> set k2 v2
QUEUED
127.0.0.1:6379(TX)> set k6 v6
QUEUED
127.0.0.1:6379(TX)> DISCARD #取消事务
OK
127.0.0.1:6379> get k6 #事务队列中命令不会被执行
(nil)

```

- 编译型异常（代码有问题！命令有错），事务中所有的命令都不会被执行

```bash
127.0.0.1:6379> MULTI
OK
127.0.0.1:6379(TX)> set k1 v1
QUEUED
127.0.0.1:6379(TX)> set k2 v2
QUEUED
127.0.0.1:6379(TX)> set k3 v3
QUEUED
127.0.0.1:6379(TX)> getset k3 #错误的命令
(error) ERR wrong number of arguments for 'getset' command
127.0.0.1:6379(TX)> set k4 v4
QUEUED
127.0.0.1:6379(TX)> exec #执行事务报错
(error) EXECABORT Transaction discarded because of previous errors.
127.0.0.1:6379> get k1 #所有的命令都不会被执行
(nil)
```

- 运行时异常（1/0），如果事务队列中存在语法性，那么执行命令的时候，其他命令是可以正常执行的，错误命令抛出异常

```bash
127.0.0.1:6379> set k1 v1
OK
127.0.0.1:6379> MULTI
OK
127.0.0.1:6379(TX)> INCR k1
QUEUED
127.0.0.1:6379(TX)> set k2 v2
QUEUED
127.0.0.1:6379(TX)> set k3 v3
QUEUED
127.0.0.1:6379(TX)> get k3
QUEUED
127.0.0.1:6379(TX)> EXEC
1) (error) ERR value is not an integer or out of range #虽然第一条命令报错了，但其他命令依然执行成功
2) OK
3) OK
4) "v3"
127.0.0.1:6379> get k2
"v2"
127.0.0.1:6379> get k3
"v3"

```

#### 监控 Watch

**悲观锁：**

- 很悲观，认为什么时候都会出问题，什么时候都加锁

**乐观锁：**

- 很乐观，认为什么时候都不会出问题，所以不会上锁。更新数据的时候去判断，在此期间是否有人修改过数据，version
- 获取version
- 更新的时候比较version

```bash
127.0.0.1:6379> set money 100
OK
127.0.0.1:6379> set out 0
OK
127.0.0.1:6379> WATCH money #监视money对象
OK
127.0.0.1:6379> MULTI #事务正常结束，数据期间没有发生变动，这个时候正常执行成功
OK
127.0.0.1:6379(TX)> DECRBY money 10
QUEUED
127.0.0.1:6379(TX)> INCRBY out 10
QUEUED
127.0.0.1:6379(TX)> exec
1) (integer) 90
2) (integer) 10

#其他线程修改money状态
127.0.0.1:6379> WATCH money #监视money对象
OK
127.0.0.1:6379> MULTI #事务正常结束，数据期间发生变动，这个时候执行失败
OK
127.0.0.1:6379(TX)> DECRBY money 10
QUEUED
127.0.0.1:6379(TX)> INCRBY out 10
QUEUED
127.0.0.1:6379(TX)> exec
(nil) #执行失败

##如果发现事务执行失败  
127.0.0.1:6379> UNWATCH #先解锁
OK
127.0.0.1:6379> WATCH money # 获取最新的值 select version
OK
127.0.0.1:6379> MULTI #再次监视
OK
127.0.0.1:6379(TX)> DECRBY money 10
QUEUED
127.0.0.1:6379(TX)> INCRBY out 10
QUEUED
127.0.0.1:6379(TX)> exec #比对监视的值是否发生了变化，如没有变化则执行成功；如果变化了，再次执行此类操作
1) (integer) 80
2) (integer) 20

```

#### Jedis

1.导入依赖

```xml
<dependencies>
        <!-- https://mvnrepository.com/artifact/redis.clients/jedis -->
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>4.0.0</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>2.0.0</version>
        </dependency>
    </dependencies>
```

2.编码测试

- 连接数据库
- 操作连接
- 断开连接

```java
package org.example.test;

import redis.clients.jedis.Jedis;

//测试连接
public class TestPing {
    public static void main(String[] args) {
        //new Jedis对象 连接redis
        Jedis jedis = new Jedis("192.168.138.35",6379);
        //jedis 的所有方法都是redis的指令
        System.out.println(jedis.ping());
        //输出PONG
    }
}
```

所有的方法对应redis的指令

**事务**

```java
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
```

#### Springboot整合

springBoot操作数据：spring-data jpa mongdb redis

SpringData也是和SpringBoot齐名的项目

说明：在SpringBoot2.x之后，原来使用的jedis被替换为lettuce

![image-20230131144654755](Redis.assets/image-20230131144654755.png)

- jedis：采用直连，多个线程操作的话，是不安全的，如果想要避免不安全的操作，需要使用jedis pool连接池 BIO
- letuce：采用netty，实例可以再多个线程中进行共享，不存在线程不安全的情况 NIO 性能更好

```java
 @Bean
    @ConditionalOnMissingBean(
        name = {"redisTemplate"}
    )
    @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate(); 
  //默认的RedisTemplate没有过多的设置，redis对象都是需要序列化
  //两个泛型都是Object，Object的类型，后续使用需要强制转换<String,object>
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
//由于String类型较长使用，因此单独提出来一个bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
```

- 导入依赖

```xml
<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis</artifactId>
		</dependency>
```

- 配置连接

```
##配置redis
spring.redis.host=192.168.138.35
spring.redis.port=6379
```

- 测试

```java
package com.noah;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class Redis02SpringbootApplicationTests {

	@Autowired
	RedisTemplate redisTemplate;

	@Test
	void contextLoads() {

		//操作不同类型
		//操作字符串，类似String
//		redisTemplate.opsForValue();
//		redisTemplate.opsForList();
//		redisTemplate.opsForHash();
//		redisTemplate.opsForSet();
//		redisTemplate.opsForZSet();
//		redisTemplate.opsForGeo();
//		redisTemplate.opsForHyperLogLog();

		//除了基本的操作，redisTemplate里也有常用的方法可以直接使用，比如事务和基本的增删改查
//		redisTemplate.multi();
//		redisTemplate.discard();
//		redisTemplate.exec();

		//获取redis的连接对象
//		RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
//		connection.flushDb();
//		connection.flushAll();

		redisTemplate.opsForValue().set("name","noah");
		System.out.println(redisTemplate.opsForValue().get("name"));
	}
}
```

关于对象的保存需要序列化

 RedisTemplate 默认使用 JdkSerializationRedisSerializer序列化

![image-20230131164435380](Redis.assets/image-20230131164435380.png)

自定义序列化配置

```java
package com.noah.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author noah
 * @version 1.0
 * @Description TODO
 * Create by 2023/1/31 16:09
 */
@Configuration
public class RedisConfig {

    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        //为了开发方便一般使用<String, Object>类型
        RedisTemplate<String, Object> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        //配置具体的序列化方式

        //Json序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        //转义序列化
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        //String的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        //key采用String的序列化
        template.setKeySerializer(stringRedisSerializer);
        //hash的key采用String的序列化
        template.setHashKeySerializer(stringRedisSerializer);
        //value采用jackson序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);
        //hash的Value采用jackson序列化
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
```

utils类

```java
package com.noah.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author noah
 * @version 1.0
 * @Description redisUtil类
 * Create by 2023/1/31 17:35
 */
@Component
public final class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // =============================common============================
    /**
     * 指定缓存失效时间
     * @param key  键
     * @param time 时间(秒)
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间(秒)   返回-1：代表永久有效   返回-2：代表已经过期的数据
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 删除缓存
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }


    // ============================String=============================

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */

    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 普通缓存放入并设置时间
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */

    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 递增
     * @param key   键
     * @param delta 要增加几(大于0)
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }


    /**
     * 递减
     * @param key   键
     * @param delta 要减少几(小于0)
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }


    // ================================Map=================================

    /**
     * HashGet
     * @param key  键 不能为null
     * @param item 项 不能为null
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * HashSet 并设置时间
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }


    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }


    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }


    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }


    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     * @param key 键
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0)
                expire(key, time);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 获取set缓存的长度
     *
     * @param key 键
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */

    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取list缓存的长度
     *
     * @param key 键
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 将list放入缓存
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0)
                expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0)
                expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */

    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */

    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
```

#### Redis.conf详解

##### 单位

units对大小写不敏感

![image-20230201134429910](Redis.assets/image-20230201134429910.png)

##### 包含

可以包含其他配置文件

![image-20230201134751983](Redis.assets/image-20230201134751983.png)

##### 网络

```
# 服务器外可连接
# bind 127.0.0.1 -::1 
#关闭保护模式
protected-mode no
```

##### 主从复制 REPLICATION

```bash
# replicaof <masterip> <masterport>
# 配置主机host port
# If the master is password protected (using the "requirepass" configuration
# directive below) it is possible to tell the replica to authenticate before
# starting the replication synchronization process, otherwise the master will
# refuse the replica request.
#
# masterauth <master-password>
# 主机密码
# However this is not enough if you are using Redis ACLs (for Redis version
# 6 or greater), and the default user is not capable of running the PSYNC
# command and/or other commands needed for replication. In this case it's
# better to configure a special user to use with replication, and specify the
# masteruser configuration as such:
#
# masteruser <username>

```

##### 通用

```
#以守护进程的方式进行，默认是no
daemonize yes

#如果以后台方式运行，需要指定pid文件
pidfile /var/run/redis_6379.pid

#日志
# Specify the server verbosity level.
# This can be one of:
# debug (a lot of information, useful for development/testing)
# verbose (many rarely useful info, but not a mess like the debug level)
# notice (moderately verbose, what you want in production probably) 默认。生产环境使用
# warning (only very important / critical messages are logged)
loglevel notice

#日志的文件位置名
logfile ""

#默认有16个数据库
databases 16
```

##### 快照

持久化，在规定的时间内，执行了多少次操作，则会持久化文件.rdb.aof

redis是内存数据库，如果没有持久化，那么数据断电即失

```
# save ""
#
# Unless specified otherwise, by default Redis will save the DB:
#   * After 3600 seconds (an hour) if at least 1 change was performed
#   * After 300 seconds (5 minutes) if at least 100 changes were performed
#   * After 60 seconds if at least 10000 changes were performed
#
# You can set these explicitly by uncommenting the following line.
#
# 如果3600s内，至少有1个 key进行了修改，就进行持久化操作
# 如果300s内，至少有100个 key进行了修改，就进行持久化操作
# 如果60s内，至少有10000个 key进行了修改，就进行持久化操作
# save 3600 1 300 100 60 10000

#持久化出错 是否还继续工作
stop-writes-on-bgsave-error yes
#是否压缩rdb文件。需要消耗cpu资源
rdbcompression yes
#保存rdb文件的时候，进行错误校验检查
rdbchecksum yes
#rdb文件名
dbfilename dump.rdb
#rdb文件保存路径 默认当前目录
dir ./
```

##### 安全 SECURITY

```
config get requirepass #获取密码
1) "requirepass"
2)""
config set requirepass "12345" #设置密码

#登录
auth 12345
config get requirepass
1) "requirepass"
2)"12345"

save #保存
```

##### 客户端  限制 clients

```
maxclients 10000 #最大客户端连接数
maxmemory <bytes> #配置最大的内存容量
maxmemory-policy noeviction #内存达到上限的处理策略
    #1、volatile-lru：只对设置了过期时间的key进行LRU（默认值） 
    #2、allkeys-lru ： 删除lru算法的key   
    #3、volatile-random：随机删除即将过期key   
    #4、allkeys-random：随机删除   
    #5、volatile-ttl ： 删除即将过期的   
    #6、noeviction ： 永不过期，返回错误
```

##### APPEND ONLY 模式 aof配置

```
#默认不开启aof模式，默认使用rdb方式持久化。在大部分情况下，rdb完全够用
appendonly no 
#持久化文件名字
appendfilename "appendonly.aof"

# appendfsync always #每次修改都会sync。速度慢，消耗性能
appendfsync everysec #每秒执行一次sync 可能会丢失1s的数据
# appendfsync no #不执行sync，这个时候操作系统自己同步数据，速度是最快！

```

#### Redis的持久化

因为Redis是内存数据库，因此需要提供持久化功能

##### RDB（Redis DataBase）

![image-20230201164003248](Redis.assets/image-20230201164003248.png)

在指定的时间间隔内将内存中的数据集快照写入磁盘，也就是Snapshot快照，它恢复时是将快照文件直接读到内存里。

Redis会单独创建（fork）一个子进程来进行持久化，会先将数据写入到一个临时文件中，待持久化过程结束了，再用这个临时文件替换上次持久化好的文件。整个过程中，主进程是不进行任何IO操作的。这就确保了极高的性能。如果需要进行大规模数据恢复，且对于数据恢复的完整性不是非常敏感，那RDB方式要比AOF方式更加高效。RDB的缺点是最后一次持久化后的数据可能丢失。默认是RDB，一般情况下不修改。

rdb保存的文件dump.rdb

**在生产环境一般会进行备份**

**触发机制**

1. save的规则满足的情况下，会自动触发rdb规则
2. 执行flushall命令，也会触发rdb规则
3. 退出redis，也会触发rdb规则

自动生成dump.rdb

**如何恢复**

1. 只需将rdb文件放在redis启动目录就可以，在redis启动的时候会自动检查dump.rdb恢复其中的数据！
2. 查看dump.rdb存放目录

```bash
127.0.0.1:6379> config get dir
1) "dir"
2) "/usr/local/bin" #如果在这个目录下存在dump.rdb，启动就会自动恢复其中给的数据
```

**优点**

1. 适合大规模的数据恢复
2. 对数据的完整性要求不高

**缺点**

1. 需要一定的时间间隔操作！如果redis意外宕机了最后一次修改的数据就没有了
2. fork进程会占用一定的内存空间



##### AOF（Append Only File）

将所有命令都记录下来，恢复的时候就把这个文件全部再执行一遍

![image-20230201172008880](Redis.assets/image-20230201172008880.png)

以日志的形式记录每个写的操作，将Redis执行过的所有指令记录下来（读操作不记录），只许追加文件但不可以改写文件，redis启动之初会读取该文件重新构建数据，换言之，redis重启的话就根据日志的内容将写指令从前到后执行一次以完成数据的恢复工作

AOF保存的是appendonly.aof文件

 默认只需修改 appendonly yes 即可

如果appendonly.aof遭到破坏可以使用redis-check-aof来进行修复

```bash
./redis-check-aof --fix appendonly.aof
```

![image-20230201174612027](Redis.assets/image-20230201174612027.png)

**优点**

1. 每一次修改都同步，文件的完整性更好
2. 每秒同步一次，可能会丢失一秒的数据
3. 从不同步，效率最高

**缺点**

1. 相对于数据文件来说，aof远远大于rdb，修复的速度远比rdb慢
2. AOF的运行效率也比rdb慢，所以redis默认的配置是rdb

**重写规则**

![image-20230201175335239](Redis.assets/image-20230201175335239.png)

如果aof文件大于64mb，太大了！就会fork一个新的进程来将文件进行重写。重写

aof默认文件无限制追加，文件会越来越大

**扩展**

1.   RDB持久化方式能够在指定的时间间隔内对数据进行快照存储

2. AOF持久化方式记录每次对服务器写的操作，当服务器重启的时候会重新执行这些命令来恢复原始的数据，AOF命令以Redis协议追加保存每次写的操作到文件末尾，Redis还能对AOF文件进行后台重写，使得AOF文件的体积不至于过大。

3. **只做缓存，如果你只希望你的数据再服务器运行的时候存在，可以不适用任何持久化**

4. 同时开启两种持久化方式

   - 在这种情况下，当redis重启的时候会优先载入AOF文件来恢复原始数据，因为在通常情况下AOF文件保存的数据集要比RDB文件保存的数据集要完整
   - RDB的数据不实时，同时使用两者时服务器重启也只会找AOF文件，那要不要只使用AOF呢？作者建议不要，因为RDB更适合用于备份数据库（AOF在不断变化不好备份）。快速重启，而且不会有AOF可能潜在的Bug，留着作为一个万一的手段。

5. 性能建议

   - 因为RDB文件只用作后备用途，建议只在Slave上持久化RDB文件，而且只要15分钟备份一次就够了，只保留save 900 1这条规则
   - 如果Enable AOF，好处是在最恶劣情况下也只会丢失不超过两秒数据，启动脚本较简单只liad自己的AOF文件就可以了，代价一时带来了持续的IO,二是AOF rewrite的最后将rewrite过程中产生的新数据写到新文件造成的阻塞几乎是不可避免的。只要硬盘许可，应该尽量减少AOF rewrite的频率，AOF重写的基础大小默认值64M太小了，可以设到5G以上，默认超过原大小100%大小重写可以改到适当的数值。
   - 如果不Enable AOF，仅靠Master-Slave Repllcation实现高可用性也可以，能省掉大笔IO，也减少rewrite时带来的系统波动。代价时如果Master/slavet同时down掉，会丢失十几分钟的数据，启动脚本也要比较两个Master/Slave中的RDB文件，载入较新的那个，微博就是这种架构。

   #### Redis 的发布订阅

   Redis发布订阅（pub/sub）是一种**消息通信模式**：发送者（pub）发送消息，订阅和（sub）接收消息。微信、微博、关注系统

   Redis客户端可以订阅任意数量的频道。

   订阅/发布消息图：

   ![image-20230202101333468](Redis.assets/image-20230202101333468.png)

第一个：消息发送者 第二个：频道 第三个：消息订阅者

下图展示了频道1channel1，以及订阅这个频道的三个客户端--client2、client5和client1之间的关系：	

![image-20230202101530866](Redis.assets/image-20230202101530866.png)

当又新消息通过PUBLISH命令发送给channel1时，这个消息就会被发送给订阅它的三个客户端：

![image-20230202102053051](Redis.assets/image-20230202102053051.png)

**命令**

这些命令被广泛用于构建即时通信应用，比如网络聊天室（chatroom）和实时广播、实时提醒等。

![image-20230202102143670](Redis.assets/image-20230202102143670.png)

```bash
#订阅端
127.0.0.1:6379> SUBSCRIBE noahSeeNBA #订阅频道
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "noahSeeNBA"
3) (integer) 1
#等待读取推送的信息
1) "message" #消息
2) "noahSeeNBA" #哪个频道的消息
3) "welcome to the NBA!" #消息的具体内容
1) "message"
2) "noahSeeNBA"
3) "hello,redis"

#发布端
127.0.0.1:6379> PUBLISH noahSeeNBA "welcome to the NBA!" #发布者发布消息到频道！
(integer) 1
127.0.0.1:6379> PUBLISH noahSeeNBA "hello,redis"
(integer) 1
```

**原理**

Redis是使用C实现的，通过分析Redis源码里的pubsub.c文件，了解发布和订阅机制的底层实现，加深对Redis的理解。

Redis通过PUBLISH、SUBSCRIBE和PSUBSCRIBE等命令实现发布和订阅功能

通过SUBSCRIBE命令订阅某频道后，redis-server里维护了一个字典，字典的键就是一个个频道！而频道的值则是一个链表，链表中保存了所有订阅这个channel的客户端，SUBSCRIBE命令的关键，就是将客户端添加到给定的channel的订阅链表中。

通过PUBLISH命令向订阅者发布消息，redis-server会使用给定的频道作为键，在它所维护的channel字典中查找记录了订阅这个频道的所有客户端的链表，遍历这个链表，将消息发布给所有订阅者。

Pub/Sub从字面上理解就是发布（Publish）与订阅（Subscribe），在Redis中，你可以设定对某一个key值进行消息发布及消息订阅，当一个key值上进行了消息发布后，所有订阅它的客户端都会受到相应的消息。这一功能最明显的用法就是用作实时消息系统，比如普通的即时聊天，群聊等功能。

使用场景：

1. 实时消息系统
2. 实时聊天（频道当作聊天室，将信息回显给所有人即可）
3. 订阅，关注系统

复杂的场景，推荐使用消息中间件 MQ

#### Redis的主从复制

##### 概念

主从复制，是指将一台Redis服务器的数据，复制到其他的Redis服务器。前者称为主节点（master/leader），后者称为从节点（slave/follower）；数据的复制时单向的，只能由主节点到从节点。Master以写为主，Slave以读为主。

**默认情况下，每台Redis服务器都是主节点**；且一个主节点可以由多个从节点（或者没有从节点），但一个从节点只能由一个主节点。

主从复制的作用主要包括：

1. 数据冗余：主从复制实现了数据的热备份，是持久化之外的一种数据冗余方式
2. 故障恢复：当主节点出现问题时，可以由从节点提供服务，实现快速的故障恢复；实际上是一种服务的冗余。
3. 负载均衡：在主从复制的基础上，配合读写分离，可以由主节点提供写服务，由从节点提供读服务（即写redis数据时应用连接主节点，读redis数据时应用连接从节点），分担服务器负载；尤其时在写少读多的场景下，通过多个从节点分担读负载，可以大大提高Redis服务器的并发量
4. 高可用基石：除了上述作用以外，主从复制还是哨兵和集群能够实施的基础，因此说主从复制时Redis高可用的基础。



一般来说，要将Redis运用于工程项目中，只使用一台Redis时万万不能的，原因如下：

1. 从结构上，单个Redis服务器会发生单点故障，而且一台服务器需要处理所有的请求负载，压力较大；
2. 从容量上，单个Redis服务器内存容量有限，就算一台Redis服务器内存容量位256G，也不能将所有内存用作Redis存储内存，**一般来说，单台Redis最大使用内存不应该超过20G**。

电商网站上的商品，一般都是一次上传，无数次浏览的，说专业点就是“多读少写”

对于这种场景，可以使用如下架构：

![image-20230202113930168](Redis.assets/image-20230202113930168.png)

##### 环境配置

只配置从库，不用配置主库

```bash
127.0.0.1:6379> info replication #查看配置信息
# Replication
role:master #角色 master
connected_slaves:0 #没有从机
master_failover_state:no-failover
master_replid:00184a38d3b09fee1e8541a2de4bb58e59306d2b
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:0
second_repl_offset:-1
repl_backlog_active:0
repl_backlog_size:1048576
repl_backlog_first_byte_offset:0
repl_backlog_histlen:0
```

单机集群

复制3个配置文件，然后修改对应的信息

1. 端口port
2. pid名字
3. log文件名字
4. dump.rdb名字

成功启动

![image-20230202140227550](Redis.assets/image-20230202140227550.png)

##### 一主二从

**默认情况下，每台Redis服务器都是主节点**

一般情况下，只需配置从机

**认老大！**

```bash
127.0.0.1:6380> SLAVEOF 127.0.0.1 6379 #认老大 SLAVEOF host port
OK
127.0.0.1:6380> INFO replication
# Replication
role:slave #从机
master_host:127.0.0.1 #可以看到主机信息
master_port:6379
master_link_status:up
master_last_io_seconds_ago:5
master_sync_in_progress:0
slave_read_repl_offset:14
slave_repl_offset:14
slave_priority:100
slave_read_only:1
replica_announced:1
connected_slaves:0
master_failover_state:no-failover
master_replid:733b02ae87e68c860356810e1f39bf2a7851f52f
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:14
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:1
repl_backlog_histlen:14

#在主机中查看
127.0.0.1:6379> info replication
# Replication
role:master #主机
connected_slaves:2 #从机数量
slave0:ip=127.0.0.1,port=6380,state=online,offset=560,lag=0 #从机
slave1:ip=127.0.0.1,port=6381,state=online,offset=560,lag=0 #从机
master_failover_state:no-failover
master_replid:733b02ae87e68c860356810e1f39bf2a7851f52f
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:28
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:1
repl_backlog_histlen:28
```

真实的使用场景是需要修改配置文件，通过命令修改只是暂时的。

**细节**

主机可以设置值，从机不能写只能读

主机写：

```bash
127.0.0.1:6379> set k1 v1
OK

```

从机读：

```bash
127.0.0.1:6380> set k2 v2
(error) READONLY You can't write against a read only replica.

```

主机断开连接，从机依旧连接欸得到主机，但是灭有写操作，这个时候，主机回来了，从机依然可以直接获取到主机写的信息

如果是使用命令行来配置主从，重启从机，就会变成主机。只要变为从机，数据立马就会从主机中获取值。

**复制原理**

Slave启动成功连接到master后会发送一个sync同步命令

Master接到命令，启动后台的存盘进程，同时手机所有接收到的用于修改数据集命令，在后台进程执行完毕之后，**master将传送整个数据文件到Slave，并完成一次完全同步**

**全量复制：**而slave服务在接收到数据库文件数据后，将其存盘并加载到内存中

**增量复制：**Master继续将新的所有收集到的修改命令一次传给slave，完成同步

但是只要是重 新连接master，一次完全同步（全量复制）将被自动执行

**层层链路**

Mster 79 ==》Slave 80 (Master 79)==》Slave 81(Master 80)

此时80作为81的Master依旧不能进行写，但79的数据可以同步到81上

**谋朝篡位**（手动选老大）

```bash
SLAVEOF no one #自己变成主节点
```

##### 哨兵模式

（自动选举老大的模式）

**概述**

主从切换技术的方法是：当主服务器宕机后，需要手动把一台从服务器切换为主服务器，这就需要人工干预，费时费力，还会造成一段时间内服务不可用。这不是一种推荐的方式，更多时候，优先考虑哨兵模式。Redis2.8开始正式提供了Sentinel（哨兵）架构来解决这个问题

谋朝篡位自动版，能够后台监控主机是否故障，如果故障了**根据投票数自动将从库转换为主库**

哨兵模式是一种特殊的模式，首先Redis提供了哨兵的命令，哨兵是一个独立的进程，作为进程，他会独立运行。其原理是**哨兵通过发送命令，等待Redis服务器响应，从而监控运行多个Redis实例。**

![image-20230202154603974](Redis.assets/image-20230202154603974.png)

这里的哨兵由两个作用

- 通过发送命令，让redis服务器返回监控其运行状态，包括主服务器和从服务器
- 当哨兵检测到master宕机，会自动将slave切换成master，然后通过**发布订阅模式**通知其他的从服务器，修改配置文件，让他们切换主机

然而一个哨兵进程对Redis服务器进行监控，可能会出现问题，为此，我们可以使用多个哨兵进行监控。各个哨兵之间还会进行监控，这样就形成了多哨兵模式。

![image-20230202155307110](Redis.assets/image-20230202155307110.png)

假设主服务器宕机，哨兵1先检测到这个结果，系统并不会马上进行failover过程，仅仅是哨兵1主观的认为主服务器不可用，这个现象成功**主管下线**。当后面的哨兵页检测到主服务器不可用，并且数量达到一定值时，那么哨兵之间就会进行一次投票，投票的结果由一个哨兵发起，进行failover[故障转移]操作。切换成功之后，就会通过发布订阅模式，让各个哨兵吧自己监控的从服务器实现切换主机，这个过程称为**客观下线**

**测试**

- 配置哨兵配置文件sentinel.conf

```bash
#sentinel monitor 被监控的名称 host port 1:代表主机挂了，slave投票让谁来接替成为主机，票数最多，就会成为主机
sentinel monitor myredis 127.0.0.1 6379 1
```

- 启动哨兵服务

```bash
[root@localhost bin]# ./redis-sentinel myconfig/sentinel.conf #启动
13128:X 02 Feb 2023 00:34:33.767 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
13128:X 02 Feb 2023 00:34:33.767 # Redis version=7.0.8, bits=64, commit=00000000, modified=0, pid=13128, just started
13128:X 02 Feb 2023 00:34:33.767 # Configuration loaded
13128:X 02 Feb 2023 00:34:33.767 * Increased maximum number of open files to 10032 (it was originally set to 1024).
13128:X 02 Feb 2023 00:34:33.767 * monotonic clock: POSIX clock_gettime
                _._                                                  
           _.-``__ ''-._                                             
      _.-``    `.  `_.  ''-._           Redis 7.0.8 (00000000/0) 64 bit
  .-`` .-```.  ```\/    _.,_ ''-._                                  
 (    '      ,       .-`  | `,    )     Running in sentinel mode
 |`-._`-...-` __...-.``-._|'` _.-'|     Port: 26379
 |    `-._   `._    /     _.-'    |     PID: 13128
  `-._    `-._  `-./  _.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |           https://redis.io       
  `-._    `-._`-.__.-'_.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |                                  
  `-._    `-._`-.__.-'_.-'    _.-'                                   
      `-._    `-.__.-'    _.-'                                       
          `-._        _.-'                                           
              `-.__.-'                                               

13128:X 02 Feb 2023 00:34:33.769 # WARNING: The TCP backlog setting of 511 cannot be enforced because /proc/sys/net/core/somaxconn is set to the lower value of 128.
13128:X 02 Feb 2023 00:34:33.770 * Sentinel new configuration saved on disk
13128:X 02 Feb 2023 00:34:33.770 # Sentinel ID is 6e2325aa07386f3a2ce2afc08f973255f681179b
13128:X 02 Feb 2023 00:34:33.770 # +monitor master myredis 127.0.0.1 6379 quorum 1
13128:X 02 Feb 2023 00:34:33.771 * +slave slave 127.0.0.1:6380 127.0.0.1 6380 @ myredis 127.0.0.1 6379
13128:X 02 Feb 2023 00:34:33.772 * Sentinel new configuration saved on disk
13128:X 02 Feb 2023 00:34:33.772 * +slave slave 127.0.0.1:6381 127.0.0.1 6381 @ myredis 127.0.0.1 6379
13128:X 02 Feb 2023 00:34:33.773 * Sentinel new configuration saved on disk
```

当6379宕机后，sentinel会自动推举6381为master

```bash
13128:X 02 Feb 2023 00:38:32.813 # +failover-state-select-slave master myredis 127.0.0.1 6379
13128:X 02 Feb 2023 00:38:32.897 # +selected-slave slave 127.0.0.1:6381 127.0.0.1 6381 @ myredis 127.0.0.1 6379
13128:X 02 Feb 2023 00:38:32.897 * +failover-state-send-slaveof-noone slave 127.0.0.1:6381 127.0.0.1 6381 @ myredis 127.0.0.1 6379
13128:X 02 Feb 2023 00:38:32.969 * +failover-state-wait-promotion slave 127.0.0.1:6381 127.0.0.1 6381 @ myredis 127.0.0.1 6379
13128:X 02 Feb 2023 00:38:33.799 * Sentinel new configuration saved on disk
13128:X 02 Feb 2023 00:38:33.799 # +promoted-slave slave 127.0.0.1:6381 127.0.0.1 6381 @ myredis 127.0.0.1 6379
13128:X 02 Feb 2023 00:38:33.799 # +failover-state-reconf-slaves master myredis 127.0.0.1 6379
13128:X 02 Feb 2023 00:38:33.869 * +slave-reconf-sent slave 127.0.0.1:6380 127.0.0.1 6380 @ myredis 127.0.0.1 6379
13128:X 02 Feb 2023 00:38:34.802 * +slave-reconf-inprog slave 127.0.0.1:6380 127.0.0.1 6380 @ myredis 127.0.0.1 6379
13128:X 02 Feb 2023 00:38:34.802 * +slave-reconf-done slave 127.0.0.1:6380 127.0.0.1 6380 @ myredis 127.0.0.1 6379
13128:X 02 Feb 2023 00:38:34.865 # +failover-end master myredis 127.0.0.1 6379
13128:X 02 Feb 2023 00:38:34.865 # +switch-master myredis 127.0.0.1 6379 127.0.0.1 6381
13128:X 02 Feb 2023 00:38:34.865 * +slave slave 127.0.0.1:6380 127.0.0.1 6380 @ myredis 127.0.0.1 6381
13128:X 02 Feb 2023 00:38:34.865 * +slave slave 127.0.0.1:6379 127.0.0.1 6379 @ myredis 127.0.0.1 6381
13128:X 02 Feb 2023 00:38:34.866 * Sentinel new configuration saved on disk
13128:X 02 Feb 2023 00:39:04.919 # +sdown slave 127.0.0.1:6379 127.0.0.1 6379 @ myredis 127.0.0.1 6381
```

当原主机回来了，只能归并到新主机下当作从机

优点：

1. 哨兵集群，基于主从复制模式，所有的主从配置优点全有
2. 主从可以切换，故障可以转移，系统可用性好
3. 哨兵模式就是主从模式的升级，手动到自动更加健壮

缺点：

1. Redis不好在线扩容，集群容量一旦达到上限，在线扩容就十分麻烦
2. 实现哨兵模式的配置其实时很麻烦的，里面有很多选择

**哨兵模式的全部配置**

```bash
# Example sentinel.conf

# *** IMPORTANT ***
# 绑定IP地址
# bind 127.0.0.1 192.168.1.1
# 保护模式（是否禁止外部链接，除绑定的ip地址外）
# protected-mode no

# port <sentinel-port>
# 此Sentinel实例运行的端口
port 26379

# 默认情况下，Redis Sentinel不作为守护程序运行。 如果需要，可以设置为 yes。
daemonize no

# 启用守护进程运行后，Redis将在/var/run/redis-sentinel.pid中写入一个pid文件
pidfile /var/run/redis-sentinel.pid

# 指定日志文件名。 如果值为空，将强制Sentinel日志标准输出。守护进程下，如果使用标准输出进行日志记录，则日志将发送到/dev/null
logfile ""

# sentinel announce-ip <ip>
# sentinel announce-port <port>
#
# 上述两个配置指令在环境中非常有用，因为NAT可以通过非本地地址从外部访问Sentinel。
#
# 当提供announce-ip时，Sentinel将在通信中声明指定的IP地址，而不是像通常那样自动检测本地地址。
#
# 类似地，当提供announce-port 有效且非零时，Sentinel将宣布指定的TCP端口。
#
# 这两个选项不需要一起使用，如果只提供announce-ip，Sentinel将宣告指定的IP和“port”选项指定的服务器端口。
# 如果仅提供announce-port，Sentinel将通告自动检测到的本地IP和指定端口。
#
# Example:
#
# sentinel announce-ip 1.2.3.4

# dir <working-directory>
# 每个长时间运行的进程都应该有一个明确定义的工作目录。对于Redis Sentinel来说，/tmp就是自己的工作目录。
dir /tmp

# sentinel monitor <master-name> <ip> <redis-port> <quorum>
#
# 告诉Sentinel监听指定主节点，并且只有在至少<quorum>哨兵达成一致的情况下才会判断它 O_DOWN 状态。
#
#
# 副本是自动发现的，因此您无需指定副本。
# Sentinel本身将重写此配置文件，使用其他配置选项添加副本。另请注意，当副本升级为主副本时，将重写配置文件。
#
# 注意：主节点（master）名称不能包含特殊字符或空格。
# 有效字符可以是 A-z 0-9 和这三个字符 ".-_".
sentinel monitor mymaster 127.0.0.1 6379 2

# 如果redis配置了密码，那这里必须配置认证，否则不能自动切换
# Example:
#
# sentinel auth-pass mymaster MySUPER--secret-0123passw0rd

# sentinel down-after-milliseconds <master-name> <milliseconds>
#
# 主节点或副本在指定时间内没有回复PING，便认为该节点为主观下线 S_DOWN 状态。
#
# 默认是30秒
sentinel down-after-milliseconds mymaster 30000

# sentinel parallel-syncs <master-name> <numreplicas>
#
# 在故障转移期间，多少个副本节点进行数据同步
sentinel parallel-syncs mymaster 1

# sentinel failover-timeout <master-name> <milliseconds>
#
# 指定故障转移超时（以毫秒为单位）。 它以多种方式使用：
#
# - 在先前的故障转移之后重新启动故障转移所需的时间已由给定的Sentinel针对同一主服务器尝试，是故障转移超时的两倍。
#
# - 当一个slave从一个错误的master那里同步数据开始计算时间。直到slave被纠正为向正确的master那里同步数据时。
#
# - 取消已在进行但未生成任何配置更改的故障转移所需的时间
#
# - 当进行failover时，配置所有slaves指向新的master所需的最大时间。
#   即使过了这个超时，slaves依然会被正确配置为指向master。
#
# 默认3分钟
sentinel failover-timeout mymaster 180000

# 脚本执行
#
# sentinel notification-script和sentinel reconfig-script用于配置调用的脚本，以通知系统管理员或在故障转移后重新配置客户端。
# 脚本使用以下规则执行以进行错误处理：
#
# 如果脚本以“1”退出，则稍后重试执行（最多重试次数为当前设置的10次）。
#
# 如果脚本以“2”（或更高的值）退出，则不会重试执行。
#
# 如果脚本因为收到信号而终止，则行为与退出代码1相同。
#
# 脚本的最长运行时间为60秒。 达到此限制后，脚本将以SIGKILL终止，并重试执行。

# 通知脚本
#
# sentinel notification-script <master-name> <script-path>
#
# 为警告级别生成的任何Sentinel事件调用指定的通知脚本（例如-sdown，-odown等）。
# 此脚本应通过电子邮件，SMS或任何其他消息传递系统通知系统管理员 监控的Redis系统出了问题。
#
# 使用两个参数调用脚本：第一个是事件类型，第二个是事件描述。
#
# 该脚本必须存在且可执行，以便在提供此选项时启动sentinel。
#
# 举例:
#
# sentinel notification-script mymaster /var/redis/notify.sh

# 客户重新配置脚本
#
# sentinel client-reconfig-script <master-name> <script-path>
#
# 当主服务器因故障转移而变更时，可以调用脚本执行特定于应用程序的任务，以通知客户端，配置已更改且主服务器地址已经变更。
#
# 以下参数将传递给脚本：
#
# <master-name> <role> <state> <from-ip> <from-port> <to-ip> <to-port>
#
# <state> 目前始终是故障转移 "failover"
# <role> 是 "leader" 或 "observer"
#
# 参数 from-ip, from-port, to-ip, to-port 用于传递主服务器的旧地址和所选副本的新地址。
#
# 举例:
#
# sentinel client-reconfig-script mymaster /var/redis/reconfig.sh

# 安全
# 避免脚本重置，默认值yes
# 默认情况下，SENTINEL SET将无法在运行时更改notification-script和client-reconfig-script。
# 这避免了一个简单的安全问题，客户端可以将脚本设置为任何内容并触发故障转移以便执行程序。
sentinel deny-scripts-reconfig yes

# REDIS命令重命名
#
#
# 在这种情况下，可以告诉Sentinel使用不同的命令名称而不是正常的命令名称。
# 例如，如果主“mymaster”和相关副本的“CONFIG”全部重命名为“GUESSME”，我可以使用：

# SENTINEL rename-command mymaster CONFIG GUESSME

# 设置此类配置后，每次Sentinel使用CONFIG时，它将使用GUESSME。 请注意，实际上不需要尊重命令案例，因此在上面的示例中写“config guessme”是相同的。

# SENTINEL SET也可用于在运行时执行此配置。

# 为了将命令设置回其原始名称（撤消重命名），可以将命令重命名为它自身：

# SENTINEL rename-command mymaster CONFIG CONFIG
```

##### Redis缓存穿透、击穿和雪崩

###### 缓存穿透（查不到）

**概念**

缓存穿透的概念很简单，用户想要查询一个数据，发现redis内存数据库没有，也就是缓存命运命中，于是向持久层数据库查询。发现也没有，于是本次查询失败。当用户很多的时候，缓存没有命中，于是都去请求了持久层数据库。这会给持久层数据库造成很大的压力，这时候就造成了缓存穿透。

**布隆过滤器**

布隆过滤器是一种数据结构，对所有可能查询的参数以hash形式存储，在控制层先进行校验，不符合则丢弃，从而避免对底层存储系统的查询压力

![image-20230203103704956](Redis.assets/image-20230203103704956.png)

布隆过滤器是一种空间效率很高的随机数据结构，专门用来检测集合中是否存在特定的元素。布隆过滤器由一个长度为m比特的位数组与k个独立的哈希函数组成的数据结构。位数组初始化均为0，所有的哈希函数都可以分别把输入数据尽量均匀地散列。当要向布隆过滤器中插入一个元素时，该元素经过k个哈希函数计算产生k个哈希值，以哈希值作为位数组中的下标，将所有k个对应的比特值由0置为1。当要查询一个元素时，同样将其经过哈希函数计算产生哈希值，然后检查对应的k个比特值：如果有任意一个比特为0，表明该元素一定不在集合中；如果所有比特均为1，表明该元素有可能性在集合中。

![image-20230203141756609](Redis.assets/image-20230203141756609.png)

由于可能出现哈希碰撞，不同元素计算的哈希值有可能一样，导致一个不存在的元素有可能对应的比特位为1，这就是所谓“假阳性”（false positive）。相对地，“假阴性”（false negative）在BF中是绝不会出现的。因此，Bloom Filter不适合那些“零错误”的应用场合。而在能容忍低错误率的应用场合下，Bloom Filter通过极少的错误换取了存储空间的极大节省。

所以，布隆过滤器认为不在的，一定不会在集合中；布隆过滤器认为在的，不一定存在集合中。

示例讲解：

https://www.jasondavies.com/bloomfilter/

优点

- **节省空间**：不需要存储数据本身，只需要存储数据对应hash比特位
- **时间复杂度低**：插入和查找的时间复杂度都为O(k)，k为哈希函数的个数

缺点

- **存在假阳性**：布隆过滤器判断存在，但可能出现元素实际上不在集合中的情况；
- **不支持删除元素**：如果一个元素被删除，但是却不能从布隆过滤器中删除，这也是存在假阳性的原因之一

解决不能删除元素方案：

1.Counting Bloom Filter（删除元素）

利用多占用几倍的存储空间代价，给Bloom Filter 增加了删除操作。

2.布谷鸟过滤器

当一个不存在的元素插入的时候，会先根据 h1 计算出其在 T1 表的位置，如果该位置为空则可以放进去。

如果该位置不为空，则根据 h2 计算出其在 T2 表的位置，如果该位置为空则可以放进去。

如果该位置不为空，就把当前位置上的元素踢出去，然后把当前元素放进去就行了。

也可以随机踢出两个位置中的一个，总之会有一个元素被踢出去。

示例：

http://www.lkozma.net/cuckoo_hashing_visualization/

布谷鸟过滤器：我支持删除操作。

布隆过滤器：我不需要限制长度为 2 的指数倍。

布谷鸟过滤器：我查找性能比你高。

布隆过滤器：我不需要限制长度为 2 的指数倍。

布谷鸟过滤器：我空间利用率也高。

布隆过滤器：我不需要限制长度为 2 的指数倍。

布谷鸟过滤器：我烦死了，TMD！

![image-20230203142754654](Redis.assets/image-20230203142754654.png)

**缓存空对象**

当存储层不命中后，即时返回空对象也将其缓存起来，同时会设置一个过期时间，之后再访问这个数据就会从缓存中获取，保护后端数据源

![image-20230203104036040](Redis.assets/image-20230203104036040.png)

但这种方法存在两个问题：

1. 如果空值能够被缓存起来，这就意味着缓存需要更多的空间存储更多的键，因为这当中可能会有很多的空值的键
2. 即时对空值设置了过期时间，还是会存在缓存层和存储层的数据会有一段时间窗口的不一致，这对于需要保持一致性的业务会有影响

###### 缓存击穿（量太大，缓存过期）

微博服务器宕机

**概述**

这里需要注意和缓存击穿的区别，缓存击穿是指一个key非常热点，在不断的扛着大并发，大并发集中对这个点进行访问，当这个key失效的瞬间，持续的大并发就穿破缓存，直接请求数据库，就像在一个屏障上凿开一个洞

在某个key在过期的瞬间，有大量的请求并发访问，这类数据一般是热点数据，由于缓存过期，会同时访问数据库来查询最新数据，并且回写缓存，会导致数据库瞬间压力过大。

**解决方案**

**设置热点数据永不过期**

从缓存层面来看，没有设置过期时间，所以不会出现热点key过期后产生的问题

**加互斥锁**

分布式锁，使用分布式锁，保证对于每个key同时只有一个线程去查询后端服务，其他线程没有获得分布式锁的权限，因此只需要等待即可。这种方式将高并发的压力转移到了分布式锁，因此对分布式锁的考验很大。

###### 缓存雪崩

是指在某一个某一个时间段，缓存集中过期失效。Redis宕机！

产生雪崩的原因之一，比如写本文的时候吗，马上就要双十二零点，很快就会迎来一波抢购，这波商品时间比较集中的放入缓存，假设缓存时间一个小时。那么到了凌晨一点的时候，这批商品的缓存就都过期了。而对这批商品的访问查询，都落到了数据库上，对于数据库而言，就会产生周期性的压力波峰。于是所有请求都会到达存储层，存储层的调用量就会暴增，造成存储层也会挂掉的情况。

![image-20230203110601375](Redis.assets/image-20230203110601375.png)

其实集中过期，倒不是非常致命，比较致命的缓存雪崩，时缓存服务器某个节点宕机或断网。因为自然形成的缓存雪崩，一定是在某个时间段集中创建缓存，这个时候，数据库也是可以顶住压力的。无非就是对数据库产生的周期性的压力而已。而缓存服务节点的宕机，对数据库服务器造成的压力是不可预知的，很有可能瞬间就把数据库压垮。

双十一：停掉一些服务，(保证主要的服务可用)比如停掉退款服务

**解决方案**

**redis高可用**

这个思想的含义，既然redis有可能会挂掉，那就多设几台redis，这样一台挂掉之后其他的还可以继续工作，其实就是搭建集群（异地多活）

**限流降级 **

这个解决方案的思想是，在缓存失效后，通过枷锁或者队列来控制读数据库写缓存的线程数量。比如对某一个key只允许一个线程查询数据和写缓存，其他线程等待

**数据预热**

数据加热的含义就是在正式部署之前，可以先把可能的胡数据先预先访问一边，这样部分可能大量访问的数据就会加载到缓存中。在即将发生大并发访问前手动触发加载缓存不同的key，设置不同的过期时间，让缓存失效的时间点尽量均匀。
