## English


## Chinese

### 简介

实现了自定义procedure `bytecodedl.findOnePath` 
具体函数签名如下

`findOnePath(@Name("start") Node start, @Name("end") Node end, @Name("maxLength") String maxLength, @Name("minLength") String minLength, @Name("callProperty") String callProperty)`

功能包括：
- 从start到end找到任意一条路径就返回，长度在minlength和maxlength之间
- 然后找到该路径第一个存在multi dispatch的边，同时返回所有的边