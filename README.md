<h1 align="center"> CRMEB客户管理+电商营销系统Java版</h1> 
<p align="center">
    <a href="http://www.crmeb.com">
    </p>
<p align="center">    
    <b>如果对您有帮助，您可以点右上角 "Star" 支持一下 谢谢！</b>
</p>

# 项目介绍

   CRMEB JAVA版商城系统是基于JavaSpringBoot + Vue + UniApp开发的一套新零售移动电商系统，CRMEB系统就是集客户关系管理+营销电商系统，能够快速积累客户、会员数据分析、智能转化客户、
   有效提高销售、会员维护、网络营销的一款企业应用，更适合企业二次开发；

## 导航栏目

 | [使用手册](http://help.crmeb.net/crmeb_java/1748037t)
 | [论坛地址](http://bbs.crmeb.net)
 | [官网地址](https://www.crmeb.com)
 | [服务器](https://promotion.aliyun.com/ntms/yunparter/invite.html?userCode=dligum2z)
 | [授权价格](http://crmeb.com/web/index/price.html)
 | [GitHub地址](https://github.com/crmeb/crmeb_java)
 | [码云地址](https://gitee.com/ZhongBangKeJi/crmeb_java)

# 加技术交流群
## [CRMEB JAVA 技术交流QQ群 1群](<a target="_blank" href="https://qm.qq.com/cgi-bin/qm/qr?k=XJ79nw1CVADxGTl1Ju0iMxzq9HMIhTR0&jump_from=webapi">点击加群 893839288</a>)

# 演示站
1. 移动端：[https://java.crmeb.net](https://java.crmeb.net)
2. 后台：[https://admin.java.crmeb.net](https://admin.java.crmeb.net)   
### 后台账号 demo  密码 crmeb.com， 请大家不要随意改密码！
### 请大家不要随意改密码！请大家不要随意改密码！请大家不要随意改密码！

# 推荐项目
CRMEB知识付费项目：https://gitee.com/ZhongBangKeJi/crmeb_zzff_class
CRMEB打通版：https://gitee.com/ZhongBangKeJi/CRMEB

#运行环境

运行环境要求JAVA1.8

注意：请尽量遵循阿里巴巴开发规范，可以减少在开发过程中出现不必要的错误

# 开源项目内包含三个子项目
~~~
    1:admin     WEB程序         PC端管理端 VUE + ElementUi
    2:app       移动商城         UniApp标准开发
    3:crmeb     Api            Java SpringBoot + mybatisPlus ...
    4:icons     基本素材         分类图标等
    5:接口文档   Api对应的接口文档也可以部署项目后查看  
~~~
## 主要特性

有详细的代码注释，有完整系统手册
### SpringBoot框架
使用SpringBoot框架框架开发
### 前端采用Vue UniApp框架
前端使用Vue UniApp框架，标准前后端分离开发.
### 标准接口
标准RESTful 接口、逻辑层次更明确
### 支持队列
降低流量高峰，解除耦合，高可用
### 无缝事件机制
行为扩展更方便，方便二次开发
### 数据操作
Excel数据导出,导出表格更加美观可视；
### 数据统计分析
后台使用ECharts图表统计，实现用户、产品、订单、资金等统计分析
### 强大的后台权限管理
后台多种角色、多重身份权限管理，权限可以控制到每一步操作
### 基于Vue表单生成控件
可以自由拖拉拽配置表单，减少前端重复表单工作量，后端接口统一调用


# 安装

### 创建三个站点
1. 把crmeb内的文件夹上传到 接口站点，此站点重定向到自己配置的端口，详情见crmeb项目.md文档
2. 打包app项目，请使用uniapp专业工具
3. 打包admin项目，详情见admin文件夹里.md文档
4. 新建数据库导入sql文件

### 解析三个域名：
1. api.xx.com       后台服务使用
2. app.xx.com       移动端使用
3. admin.xx.com     PC管理端使用


### Java项目说明
### 运行环境
1. Java Jdk1.8
2. Redis 5+
3. Mysql 5.7+
4. Nginx

### Java项目框架
1. SpringBoot 2.2.6.RELEASE
2. Maven 3.6.1
3. swagger-bootstrap-ui 1.0
4. Mybatis Plus 3.3.1

# 运行部署
1. 拿到jar包，上传到web目录下，（宝塔配置的域名指向的web目录下即可）
2. 在 jar包同级目录下运行 `start.sh (运行命令为 ./start.sh )` 脚本即可启动项目
3. shell脚本会自动运行 `tail -f crmeb_out.file` 命令输出当前启动日志
4. 看到 `Completed 200 OK` 表示启动成功
5. 默认启动 `20000` 端口号
~~~
Swagger接口文档
演示站接口: https://api.java.crmeb.net/doc.html

本地启动Java Api项目接口地址：http://localhost:8080/doc.html#    8080为配置文件里配置的 server/port

prod环境swagger默认账号密码在 prod.yml配置里
~~~
# **自己安装项目后台管理员**
### 超级管理员：admin
### 密码：123456

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
#

# v1.2.2 更新列表
	1. 修复了商品从回收站中删除的逻辑
	2. 新增了icon文件
	3. 短信下单购买二维码加载不到的问题
    4. 秒杀配置时间小于当前所有时间的重叠判断

# v1.2.1 更新列表
    1. 短信后台升级,更新供应商
    2. 替换官方swagger文档提升友好和易用[swagger-bootstrap-ui] https://doc.xiaominfo.com/knife4j
    3. 添加商品分类必要素材,可在配置好图片服务后自行维护

# v1.2 修复内容列表
	1. 修复购物车,订单和退单后后置任务正确执行
	2. 修复佣金记录金额和详情
	3. 修复管理端 移动应用界面下订单管理数据统计不准确的问题
	4. 修复短信API升级-后台使用一号通
	5. 修复用户管理相关问题
	6. 修复核销点核销后核销地址不准确
	7. 修复资源同步云服务的问题
### 新增功能
	1. 秒杀
		a. 秒杀时段配置
		b. 秒杀商品维护
	2. 财务管理
		a. 申请提现
		b. 财务记录
			i. 充值记录
			ii. 资金监控
		c. 佣金记录
普通商品显示该商品正在参加的活动信息[秒杀]


# admin项目说明
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


## 帮助文档
[帮助文档](http://help.crmeb.net/crmeb_java/1748037)
![详细的安装流程](https://images.gitee.com/uploads/images/2020/0824/153018_a0d52767_2012975.png "屏幕截图.png")
![目录结构](https://images.gitee.com/uploads/images/2020/0824/153257_9b80e21b_2012975.png "屏幕截图.png")
![数据字典](https://images.gitee.com/uploads/images/2020/0824/153317_abb02860_2012975.png "屏幕截图.png")
![任务](https://images.gitee.com/uploads/images/2020/0824/153342_96cb48f6_2012975.png "屏幕截图.png")
![系统配置](https://images.gitee.com/uploads/images/2020/0824/153403_a3aa12c7_2012975.png "屏幕截图.png")
![组合数据](https://images.gitee.com/uploads/images/2020/0824/153420_18c99224_2012975.png "屏幕截图.png")
![form表单拖拉拽](https://images.gitee.com/uploads/images/2020/0824/153433_3a83ffe2_2012975.png "屏幕截图.png")
![拦截器](https://images.gitee.com/uploads/images/2020/0824/153505_126970b4_2012975.png "屏幕截图.png")
![过滤器](https://images.gitee.com/uploads/images/2020/0824/153521_be33bd82_2012975.png "屏幕截图.png")
![无限级分类](https://images.gitee.com/uploads/images/2020/0824/153546_4b28e184_2012975.png "屏幕截图.png")



###后台界面
![登录界面](https://images.gitee.com/uploads/images/2020/0824/153756_35878cdb_2012975.png "屏幕截图.png")
![控制台](https://images.gitee.com/uploads/images/2020/0824/153848_442b3527_2012975.png "屏幕截图.png")
![商品列表](https://images.gitee.com/uploads/images/2020/0824/153920_877f7cba_2012975.png "屏幕截图.png")
![复制第三方平台商品](https://images.gitee.com/uploads/images/2020/0824/153945_60d9ef79_2012975.png "屏幕截图.png")
![添加商品](https://images.gitee.com/uploads/images/2020/0824/154012_3f1fa586_2012975.png "屏幕截图.png")
![商品规格](https://images.gitee.com/uploads/images/2020/0824/154034_745ba403_2012975.png "屏幕截图.png")
![商品分类](https://images.gitee.com/uploads/images/2020/0824/154123_b4b0adf8_2012975.png "屏幕截图.png")
![订单管理](https://images.gitee.com/uploads/images/2020/0824/154210_e4c821ed_2012975.png "屏幕截图.png")
![用户管理](https://images.gitee.com/uploads/images/2020/0824/154353_c5e5f618_2012975.png "屏幕截图.png")
![用户等级](https://images.gitee.com/uploads/images/2020/0824/154427_77f4e80d_2012975.png "屏幕截图.png")
![文章管理](https://images.gitee.com/uploads/images/2020/0824/154443_cd6c6a90_2012975.png "屏幕截图.png")
![微信](https://images.gitee.com/uploads/images/2020/0824/154515_9db27282_2012975.png "屏幕截图.png")
![优惠券](https://images.gitee.com/uploads/images/2020/0824/154536_8d1d2db2_2012975.png "屏幕截图.png")
![分销](https://images.gitee.com/uploads/images/2020/0824/154553_0b9f66a4_2012975.png "屏幕截图.png")
![身份](https://images.gitee.com/uploads/images/2020/0824/154609_e9616963_2012975.png "屏幕截图.png")
![权限](https://images.gitee.com/uploads/images/2020/0824/154647_f7fb8c69_2012975.png "屏幕截图.png")
![账号](https://images.gitee.com/uploads/images/2020/0824/154709_db94d36a_2012975.png "屏幕截图.png")
![系统设置](https://images.gitee.com/uploads/images/2020/0824/154727_fc2e4f49_2012975.png "屏幕截图.png")
![短信](https://images.gitee.com/uploads/images/2020/0824/154752_5552a5d0_2012975.png "屏幕截图.png")
![系统设置分类](https://images.gitee.com/uploads/images/2020/0824/154816_db8139fd_2012975.png "屏幕截图.png")


# 参与开发

请参阅 [CRMEB](https://github.com/crmeb/crmeb_java)。

# 开源版使用须知

1.允许用于个人学习、毕业设计、教学案例、公益事业;

2.如果商用必须保留版权信息，请自觉遵守;

3.禁止将本项目的代码和资源进行任何形式的出售，产生的一切任何后果责任由侵权者自负。

# 版权信息


本项目包含的第三方源码和二进制文件之版权信息另行标注。

版权所有Copyright © 2017-2020 by CRMEB (http://www.crmeb.com)

All rights reserved。

CRMEB® 商标和著作权所有者为西安众邦网络科技有限公司。
