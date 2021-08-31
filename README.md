## 简介
数据平台建设过程中需要获取数据血缘信息，通过对血缘数据的探索，就可以快速获取数据，加快数据开发的效率。


对于以hive为中心的数仓系统，简化架构可能为
![DBE7657D-731D-4D32-9200-B03012B01B01.png](http://ww1.sinaimg.cn/large/71f45afdgy1gha7grg147j218y0dudh9.jpg)

- 数据同步阶段可以通过同步系统解决源与数仓的列级血缘信息
- ETL阶段可以使用hive的hooks的得到ETL过程的的列级血缘信息,例如http://cxy7.com/articles/2017/11/10/1510310104765.html
- 最后一个数据展示阶段则可以通过对页面SQL的解析获取报表页面与数据的关系，此阶段则为本例需要处理问题

通过这几个阶段血缘数据的打通，可以跟踪数据从产生到展示的整个过程，提高数据监控及使用的效率。


### 如何解析SQL

可以通过语法树的解析得到列级的血缘。那么怎么得到语法树呢？

- [alibaba-druid](https://github.com/alibaba/druid/wiki/Druid_SQL_AST)
- [antlr4](https://github.com/antlr/antlr4)


可以通过以上两种工具的使用，得到语法数，那么剩下的问题就是如何将语法书解析为血缘信息。

此例使用一个简单的对象LineageColumn进行血缘存储

name    | desc
---|---
targetColumnName | 目标字段，即SELECT的列
sourceDbName | 字段来源DB  
sourceTableName | 字段来源表
sourceColumnName | 字段来源列
expression | 表达式
isEnd | 是否结束标识


通过递归迭代获取数结构，最终树的叶子节点即为最终的血缘信息，以SQL为例
```
select
user_id  as uid
,user_name  as uname
from
(
    select user_id, concat("test",user_name) as user_name
    from user
)t
```
需要经过两层循环，过程为
![DC10D4B3-A22E-40AD-AFB5-C12D95C2CEC1.png](http://ww1.sinaimg.cn/large/71f45afdgy1ghacfj01a8j212c10in0g.jpg)


最终血缘信息为
```
--- 输出列uid 数据来源于user表的user_id 列
uid	from:{"expression":"user_id","isEnd":true,"sourceTableName":"user","targetColumnName":"user_id"}


-- 输出列uname 数据来源于常量字段test及 user表的user_name 列
uname	from:{"expression":"concat('test', user_name)","isEnd":true,"sourceTableName":"user","targetColumnName":"user_name"}

uname	from:{"expression":"concat('test', user_name)","isEnd":true,"sourceTableName":"user","targetColumnName":"'test'"}

```
展开的树型结构为
```
{"isEnd":false}

{"expression":"user_id","isEnd":false,"targetColumnName":"uid"}

     {"isEnd":false,"targetColumnName":"user_id"}

          {"expression":"user_id","isEnd":false,"targetColumnName":"user_id"}

               {"expression":"user_id","isEnd":true,"sourceTableName":"user","targetColumnName":"user_id"}

{"expression":"user_name","isEnd":false,"targetColumnName":"uname"}

     {"isEnd":false,"targetColumnName":"user_name"}

          {"expression":"concat('test', user_name)","isEnd":false,"targetColumnName":"user_name"}

               {"expression":"concat('test', user_name)","isEnd":true,"sourceTableName":"user","targetColumnName":"'test'"}

               {"expression":"concat('test', user_name)","isEnd":true,"sourceTableName":"user","targetColumnName":"user_name"}

```

叶子节点即为最终关联的目标表及列。
### 如何使用
参考DruidTest即可。

# 可能遇到的问题
#### interval 语句解析异常
如果数据可视化使用的是tableau，动态sql会生成很多interval语句，interval使用druid进行语法解析的时候可能会报错。
###### 原因
druid对interval的词法解析存在缺陷
###### 解决方案
下载druid源码，参照https://github.com/alibaba/druid/pull/4368 进行代码修改，使用修改源码后的jar包。
