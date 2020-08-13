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


## admin项目超级管理员
admin 123456

## 参与开发

请参阅 [CRMEB](https://github.com/crmeb/crmeb_java)。

## 版权信息


本项目包含的第三方源码和二进制文件之版权信息另行标注。

版权所有Copyright © 2017-2020 by CRMEB (http://www.crmeb.com)

All rights reserved。

CRMEB® 商标和著作权所有者为西安众邦网络科技有限公司。
