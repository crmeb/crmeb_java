 
<div align="center" >
    <img src="https://images.gitee.com/uploads/images/2021/1109/164354_0aafe3d2_892944.gif" />
</div>
<div align="center">

CRMEB开源商城系统Java版

</div>

<div align="center" >
    <a href="http://www.crmeb.com">
        <img src="https://img.shields.io/badge/License-apache2.0-green.svg?style=flat" />
    </a>
    <a href='https://gitee.com/ZhongBangKeJi/crmeb_java/members'>
        <img src='https://gitee.com/ZhongBangKeJi/crmeb_java/badge/fork.svg?theme=dark' alt='fork'></img>
    </a>
   <a href='https://gitee.com/ZhongBangKeJi/crmeb_java/stargazers'>
        <img src='https://gitee.com/ZhongBangKeJi/crmeb_java/badge/star.svg?theme=dark' alt='star'></img>
    </a>
</div>

####

<div align="center">

[官网](https://www.crmeb.com/) |
[在线体验](https://admin.java.crmeb.net) |
[帮助文档](https://doc.crmeb.com/java/crmeb_java) |
[技术社区](https://www.crmeb.com/ask/)

[comment]: <> ([宽屏预览]&#40;https://gitee.com/ZhongBangKeJi/crmeb_java/blob/master/README.md&#41;)

</div>

<div align="center" >
    <a href="https://gitee.com/ZhongBangKeJi/crmeb_java/blob/master/README.md">宽 屏 预 览</a>
</div>

<div align="center">
    如果对您有帮助，您可以点右上角 "Star" ❤️ 支持一下 谢谢！
</div>


---
### 📖 项目介绍：

CRMEB开源商城系统Java版，是一款基于Apache-2.0协议发布的开源电商解决方案。代码全开源无加密，可免费商用，适合各类企业及开发者快速构建多端一体化商城。

#### 技术架构
系统采用Java + Vue + uni-app技术栈，基于SpringBoot框架构建，前后端分离架构设计清晰。借助uni-app的跨端能力，轻松实现小程序、公众号、H5、APP、PC端全覆盖，数据统一、业务同步，极大提升开发与维护效率。同时，系统代码规范清晰，注释详尽，二次开发十分方便。

#### 核心功能
管理功能全面
系统核心管理功能完善，包含商品管理（支持SKU、属性、分类、评价）、订单管理、用户管理、购物车、物流管理、财务统计、多种支付方式等。

#### 丰富的营销工具
10余种营销功能能充分满足日常需求，包括拼团、砍价、秒杀、优惠券、签到、积分、等级会员、余额充值、分销裂变、活动边框、氛围图等，可灵活设置活动规则。

#### 可视化页面DIY装修
系统支持页面DIY设计，内置21种组件可自由拖拽设置，无需编码即可快速搭建个性化页面。<br>
图片热区：在一张图片中，为不同位置添加不同的跳转链接。<br>
图片魔方：一个组件，6种布局样式可选，能灵活满足使用需求。<br>
导航跳转：不仅能跳转商城内部页面，也能根据具体的运营环境跳转小程序、H5等。<br>
每种组件都有多种样式可选，组件细节支持自由调整。<br>

#### 社区共建计划
我们致力于打造开发者友好生态，开放源码、持续更新功能模块，并欢迎开发者提交优化建议或贡献代码。通过共享技术成果，降低行业重复造轮子成本，推动开源电商系统的可持续发展。

CRMEB开源商城PHP：https://gitee.com/ZhongBangKeJi/CRMEB


系统代码全开源无加密，独立部署、二开方便，适用于企业新零售、分销、拼团、砍价，秒杀等各种业务需求。

[![](https://crmebjavasingle.oss-cn-beijing.aliyuncs.com/crmebimage/251201003.jpg)](https://www.crmeb.com/index/java_merchant)
---

### 统优势
#### 成熟稳定的架构
后端：SpringBoot + SpringSecurity，前端：Vue + ElementUI（PC管理端）与uni-app（移动端），多端并行开发，结构清晰。
#### 规范的接口设计
提供规范的RESTful API与数据结构，接口复用率高，逻辑层次清晰，便于二次开发与系统集成。
#### 数据与统计
集成ECharts实现多维数据看板，支持订单、用户、资金等多维度统计分析，数据可导出，助力运营决策。
#### 高效开发支持
支持Vue表单拖拽生成，大幅减少前端重复工作，提升开发效率。
#### 精细权限控制
基于Spring Security实现角色权限控制，可精确到按钮级别，保障系统安全。
#### 高可用设计
内置Redis队列，有效削峰解耦，提升系统并发能力与稳定性。

---

### 技术特点
#### 💻 运行环境及框架：
~~~
1.	移动端uniapp开发框架 可生成H5 公众号 微信小程序
2.	WEB Pc 管理后台使用Vue + Element UI 开发 兼容主流浏览器 ie11+
3.	后台服务 Java SpringBoot + Mybatis-plus + Mysql + redis
4.	运行环境 linux和windows等都支持,只要有Java环境和对应的数据库 redis
5.	运行条件 Java 1.8 Mysql5.7
~~~

#### 🔧 Java项目框架 和 WEB PC 项目运行环境
~~~
1. SpringBoot 2.2.6.RELEASE
2. Maven 3.6.1
3. swagger-bootstrap-ui 1.0
4. Mybatis Plus 3.3.1
5. npm 6
6. node 14
7. vue 2.x
8. element ui 2.13
~~~

---

#### 🧭 项目代码包介绍
~~~
1. admin     WEB程序         PC端管理端 VUE + ElementUi
2. app       移动商城         UniApp标准开发(H5 + 微信小程序)
3. crmeb     Api            Java SpringBoot + mybatisPlus
4. 接口文档   Api对应的接口文档也可以部署项目后查看
~~~

移动端 uniapp 开发 使用 HbuilderX 开发

---

### 系统演示

![](crmeb/crmebimage/demoimage/c1.jpg)

管理后台：https://admin.java.crmeb.net <br>
账号：demo<br>
密码：crmeb.com<br>

H5端：https://java.crmeb.net<br>

### 项目资料
资料支持<br>
使用文档：https://doc.crmeb.com/java/crmeb_java/1868<br>
接口文档：https://s.apifox.cn/f6079100-abee-40fe-959d-846e44f1a048<br>
数据字典：https://doc.crmeb.com/java/crmeb_java/5410<br>
二开文档：https://doc.crmeb.com/java<br>
技术社区：https://www.crmeb.com/ask/thread/list/152<br>

安装教程<br>
快速安装视频教程：  [点击查看](https://doc.crmeb.com/java/crmeb_java/2154)<br>
安装步骤详解：https://doc.crmeb.com/java/crmeb_java/2154<br>

---
### CRMEB开源技术交流
欢迎加入CRMEB开源技术交流群，扫码进群免费领取接口文档、功能清单、UI设计图、思维导图等资源！
![](crmeb/crmebimage/demoimage/wellcomJavaGroup.jpg)

您还可以在 [CRMEB技术社区](https://www.crmeb.com/ask/thread/list/152) 中提交Bug、交流解决方案、获取官方更新动态，CRMEB技术社区伴您一路前行。


[![](https://crmebjavasingle.oss-cn-beijing.aliyuncs.com/crmebimage/251201002.jpg)](https://www.crmeb.com/index/java_merchant)

[![](https://crmebjavasingle.oss-cn-beijing.aliyuncs.com/crmebimage/251201005.jpg)](https://www.crmeb.com/index/java_merchant)

[![](https://crmebjavasingle.oss-cn-beijing.aliyuncs.com/crmebimage/251201004.jpg)](https://www.crmeb.com/index/java_merchant)

[![](https://crmebjavasingle.oss-cn-beijing.aliyuncs.com/crmebimage/251201001.gif)](https://www.crmeb.com/index/java_merchant)