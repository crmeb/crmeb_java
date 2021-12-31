# 环境
1. Java Jdk1.8
2. Redis 5+
3. Mysql 5.7+

# Java项目框架
1. SpringBoot 2.2.6.RELEASE
2. Maven 3.6.1
3. Swagger 2.9.2
4. Mybatis Plus 3.3.1


# 部署
1. 拿到jar包，上传到web目录下，（宝塔配置的域名指向的web目录下即可）
2. 在 jar包同级目录下运行 `start.sh (运行命令为 ./start.sh )` 脚本即可启动项目
3. shell脚本会自动运行 `tail -f crmeb_out.file` 命令输出当前启动日志
4. 看到 `Completed 200 OK` 表示启动成功
5. 默认启动 `20000` 端口号

# 注意事项
1. web端口号不可以设置为`20000`
2. 反向代理地址: `http://127.0.0.1:20000` 【外网域名指向这个地址】


# 二次开发帮助文档
##注解参考：
1. `@NotNull`    注解	作用类型	解释	任何类型	属性不能为 `null`
2. `@NotEmpty`	集合	集合不能为 `null`，且 `size` 大于 `0`
3. `@NotBlank`	只能作用在 `String` 上，不能为 `null`，而且调用 `trim()` 后，长度必须大于 `0`
4. `@AssertTrue`	`Boolean、boolean`	布尔属性必须是 `true`
5. `@Min`	数字类型（原子和包装）	限定数字的最小值（整型）
6. `@Max`	同 `@Min`	限定数字的最大值（整型）
7. `@DecimalMin`	同 `@Min`	限定数字的最小值（字符串，可以是小数）
8. `@DecimalMax`	同 `@Min`	限定数字的最大值（字符串，可以是小数）
9. `@Range`	数字类型（原子和包装）	限定数字范围（长整型）
10. `@Length`	字符串	限定字符串长度
11. `@Size`	集合	限定集合大小
12. `@Past`	时间、日期	必须是一个过去的时间或日期
13. `@Future`	时期、时间	必须是一个未来的时间或日期
14. `@Email`	字符串	必须是一个邮箱格式
15. `@Pattern`	字符串、字符	正则匹配字符串

# 产品复制
1. 设置 [99api](https://www.99api.com "99api") 对应的api到配置表eb_system_config 
2. 配置baseUrl和key 实际的key可以根据自己的定义修改
3. 目前支持天猫，京东，淘宝，苏宁，拼多多

# 打印机
1. [易联云文档](http://doc2.10ss.net/337744 "易联云文档")
2. [易联云JAVA SDK](http://doc2.10ss.net/337744 "易联云JAVA SDK gitee文档")
3. [如何导入第三方JAVA SDK包](https://blog.csdn.net/weixin_46028577/article/details/106342938?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase "如何导入第三方JAVA SDK包")

