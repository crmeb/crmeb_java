CRMEB JAVA
===============

> 运行环境要求JAVA1.8。

注意：请尽量遵循阿里巴巴开发规范，可以减少在开发过程中出现不必要的错误

项目内包含三个子项目

## crmeb项目结构详细说明， 此处主要是Api
~~~
    crmeb
    │    ├─ crmeb_log 日志
    │    ├─ image图片临时文件夹
    │    ├─ /src/main/
    │        ├─ java/com
    │          ├─ aop 切片
    │          ├─ common 公用基础类
    │          ├─ constants 常量类
    │          ├─ exception 异常 
    │          ├─ filter 过滤器
    │          ├─ interceptor 拦截器
    │          ├─ utils 工具类
    │          ├─ zbkj.crmeb 模块服务
    │              ├─ article  文章
    │              ├─ authorization 后台用户登录，token
    │              ├─ category 分类服务
    │              ├─ config 配置
    │              ├─ export 导出
    │              ├─ express 物流
    │              ├─ finance 资金
    │              ├─ front 对移动端接口
    │              ├─ marketing 营销
    │              ├─ payment 支付
    │              ├─ pub 公共服务（可根据自己情况决定此处是否隐藏）
    │              ├─ sms 短信
    │              ├─ statistics 统计
    │              ├─ store 产品、订单
    │              ├─ system 系统服务
    │              ├─ task 计划任务
    │              ├─ upload 上传
    │              ├─ user 用户
    │              ├─ validatecode 验证码
    │              ├─ wechat 微信相关
    │              └─ wechatMessage 微信消息
    │       ├─resource 资源文件
    │           ├─ mapper mapper文件 按模块分文件夹
    │           ├─ application-*.yml 配置文件，在启动项目的时候使用
    └─sql sql文件目录

~~~
## 主要特性

有详细的代码注释，有完整系统手册
### SpringBoot框架
使用SpringBoot框架框架开发
### 前端采用Vue CLI框架
前端使用Vue CLI框架nodejs打包，页面加载更流畅，用户体验更好
### 标准接口
标准接口、前后端分离，二次开发更方便
### 支持队列
降低流量高峰，解除耦合，高可用
### 无缝事件机制
行为扩展更方便，方便二次开发
### 数据表格导出
Excel数据导出,导出表格更加美观可视；
### 数据统计分析
后台使用ECharts图表统计，实现用户、产品、订单、资金等统计分析
### 强大的后台权限管理
后台多种角色、多重身份权限管理，权限可以控制到每一步操作
### 强大的表单生成控件
可以自由配置开发表单，减少前端工作量，后端接口统一调用


## 安装

## 创建三个站点
1. 把crmeb内的文件夹上传到 接口站点，此站点重定向到自己配置的端口，详情见crmeb项目.md文档
2. 打包app项目，请使用uniapp专业工具
3. 打包admin项目，详情见admin文件夹里.md文档
4. 新建数据库导入sql文件

##解析三个域名：
1. api.xx.com  | 此域名配置到crmeb站点下 需要做重定向域名
2. app.xx.com
3. admin.xx.com


## Java项目说明
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


# 产品复制
1. 设置 [99api](https://www.99api.com "99api") 对应的api到配置表eb_system_config 
2. 配置baseUrl和key 实际的key可以根据自己的定义修改
3. 目前支持天猫，京东，淘宝，苏宁，拼多多

# 打印机
1. [易联云文档](http://doc2.10ss.net/337744 "易联云文档")
2. [易联云JAVA SDK](http://doc2.10ss.net/337744 "易联云JAVA SDK gitee文档")
3. [如何导入第三方JAVA SDK包](https://blog.csdn.net/weixin_46028577/article/details/106342938?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase "如何导入第三方JAVA SDK包")


## admin项目说明
公用组件
无限极分类组件 src/components/Category
使用范围：菜单，文章等各种分类配置数据

表单生成组件
https://github.com/JakHuang/form-generator

json渲染表单以及是否需要编辑数据回填的功能以下为例
示例：src/views/maintain/devconfig/combineEdit.vue

<parser v-if="formConf.fields.length > 0" :isEdit="isCreate === 1"
              :form-conf="formConf" :formEditData="editData"
              @submit="handlerSubmit" />
注意：设置单选按钮的值时需要带上单引号，要不渲染待编辑数据时不会自动选中

## admin项目超级管理员
admin 123456

## 帮助文档
[帮助文档](http://help.crmeb.net/crmeb_java/1748037)

## 演示站
1. [移动端](https://java.crmeb.net)
2. [后台](https://admin.java.crmeb.net)

## 参与开发

请参阅 [CRMEB](https://github.com/crmeb/crmeb_java)。

## 版权信息


本项目包含的第三方源码和二进制文件之版权信息另行标注。

版权所有Copyright © 2017-2020 by CRMEB (http://www.crmeb.com)

All rights reserved。

CRMEB® 商标和著作权所有者为西安众邦网络科技有限公司。
