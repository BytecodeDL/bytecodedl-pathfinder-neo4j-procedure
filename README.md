## English


## Chinese

### Introduction

实现了自定义procedure

- `bytecodedl.findOnePath`
  - `findOnePath(@Name("start") Node start, @Name("end") Node end, @Name("maxLength") long maxLength, @Name(value = "relationshipType", defaultValue = "Call") String relationType, @Name(value = "callProperty", defaultValue = "insn") String callProperty)`
- `bytecodedl.biFindOnePath`
  - `biFindOnePath(@Name("start") Node start, @Name("end") Node end, @Name("maxLength") long maxLength, @Name(value = "relationshipType", defaultValue = "Call") String relationType, @Name(value = "callProperty", defaultValue = "insn") String callProperty)`

其中各个参数的意义

- start: 开始节点
- end: 结束节点
- maxLength: path 最大长度
- relationshipType: 边的类型 默认是Call
- callProperty: 边的属性名称 默认是insn 值的格式为`<org.apache.logging.log4j.core.appender.ScriptAppenderSelector$Builder: org.apache.logging.log4j.core.Appender build()>/org.apache.logging.log4j.Logger.error/0`，会根据这个值计算multi dispatch

功能包括：
- 从start到end找到长度小于maxlength任意一条路径就返回 -> 速度较快
- 然后找到该路径第一个存在multi dispatch的边，同时返回所有的dispatch结果 -> 方便排查virtual invoke存在多个callee的情况
- 过滤掉属性is_deleted为1的边

### Build

`./mvnw clean package -DskipTests`

### Install

- 手动安装
  - 从[releases](https://github.com/BytecodeDL/bytecodedl-pathfinder-neo4j-procedure/releases/)下载最新的jar, 然后放到neo4j的`/var/lib/neo4j/plugins`目录
  - 然后在`/var/lib/neo4j/conf/neo4j.conf`增加一行`dbms.security.procedures.unrestricted=bytecodedl.*`
- docker
  - `docker pull wuxxxxx/neo4j-server:5.12.0-bytecodedl-pathfinder-1.0.1`