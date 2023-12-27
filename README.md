## English


## Chinese

### 简介

实现了自定义procedure
- `bytecodedl.findOnePath`
  - `findOnePath(@Name("start") Node start, @Name("end") Node end, @Name("maxLength") Long maxLength @Name("callProperty") String callProperty)`
- `bytecodedl.biFindOnePath`
  - `biFindOnePath(@Name("start") Node start, @Name("end") Node end, @Name("maxLength") Long maxLength @Name("callProperty") String callProperty)`

功能包括：
- 从start到end找到长度小于maxlength任意一条路径就返回 -> 速度较快
- 然后找到该路径第一个存在multi dispatch的边，同时返回所有的dispatch结果 -> 方便排查virtual invoke存在多个callee的情况

### 安装

- 手动安装
  - 从[releases](https://github.com/BytecodeDL/bytecodedl-pathfinder-neo4j-procedure/releases/)下载最新的jar, 然后放到neo4j的`/var/lib/neo4j/plugins`目录
  - 然后在`/var/lib/neo4j/conf/neo4j.conf`增加一行`dbms.security.procedures.unrestricted=bytecodedl.*`
- docker
- 