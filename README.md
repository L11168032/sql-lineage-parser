## 简介
数据平台建设过程中需要对数据血缘进行解析，通过对血缘数据的探索，可以快速获取数据，加快数据开发的效率。


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
![524DD638-EEB0-4735-9F56-CB1450D62E46.png](http://ww1.sinaimg.cn/large/71f45afdgy1gha8plj0jjj212e10mn0n.jpg)

最终血缘信息为
```
--- 输出列uid 数据来源于user表的user_id 列
uid	from:{"expression":"user_id","isEnd":true,"sourceTableName":"user","targetColumnName":"user_id"}


-- 输出列uname 数据来源于常量字段test及 user表的user_name 列
uname	from:{"expression":"concat('test', user_name)","isEnd":true,"sourceTableName":"user","targetColumnName":"user_name"}

uname	from:{"expression":"concat('test', user_name)","isEnd":true,"sourceTableName":"user","targetColumnName":"'test'"}

```


### 如何使用
参考DruidTest即可。