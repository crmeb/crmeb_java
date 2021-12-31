# CRMEB Admin
## 开发规范
统一使用ES6 语法
方法注释
/*
* th => 表头
* data => 数据
* fileName => 文件名
* fileType => 文件类型
* sheetName => sheet页名
*/
export default function toExcel ({ th, data, fileName, fileType, sheetName }) 
行注释 //

### 命名

页面目录 文件夹命名格式骆驼式命名法,例如：用户列表 userList 
例如：商品模块
store 商品
    ├─ store 商品管理
        ├─index.vue 首页
        ├─ creatStore  新建商品
            ├─ index.vue
        ├─ sort 商品分类
            ├─ index.vue
        ├─storeAttr 商品规格
            ├─ index.vue
        ├─storeComment 商品评论
            ├─ index.vue    

页面命名、组建、文件夹 命名格式小驼峰命名法,例如：用户列表 userList

类名函数命名 大驼峰式 例如：addUser
变量命名 小驼峰式 例如：user 或者 userInfo
常量 采用全大些下划线命名 例如：VUE_APP_API_URl

### 文件管理规范
views 页面模块必须件文件夹区分
api 接口一个模块一个文件
组建 一个组建一个文件夹
plugins 插件一个插件一个文件夹
vuex 路由状态管理，一个模块在modules 中建一个文件夹
router 一个模块一个模块在modules 中建一个文件夹
style 样式尽量采用iView自带组建，common.less 系统通用样式不要轻易动
自定义通用样式 style.less,每次添加必须加注释，页面独立样式在在页面内写，后缀less 格式
组建样式 styles 中添加文件夹 composents 对应components 目录新建样式文件
utils 自定义工具js 独立命名，一般不用新建文件夹

## 模块命名
~~~
├─ login 登录
├─ dashboard 首页
├─ store 商品管理
├─ order 系统订单管理
├─ distribution 分销
├─ user 用户管理
├─ content 内容管理
├─ appSetting 各个应用模块功能管理公众号、小程序、支付宝、百度小程序、今日头条小程序 
├─ marketing 营销管理 优惠劵 积分 秒杀
├─ sms 设置 短信设置
├─ systemSetting 设置 管理员权限 系统设置 物流设置
├─ maintain 维护 配置分类 组合数据 表单配置
├─ error-page 错误页

~~~
## 目录结构
主要目录结构及说明：
~~~
├── public                         # 静态资源
│   ├── favicon.ico                # favicon图标
│   └── index.html                 # html 模板
├── src                            # 源代码
│   ├── api                        # 所有请求
│   │    └──user.js                # 有关登录/用户的接口
│   │    └──article.js             # 有关内容的接口
│   │    └──categoryApi.js         # 有关配置的接口 
│   │    └──configTabApi.js        # 有关配置分类的接口
│   │    └──dashboard.js           # 有关统计的接口
│   │    └──distribution.js        # 有关分销的接口
│   │    └──logistics.js           # 有关城市数据、物流配置的接口
│   │    └──marketing.js           # 有关优惠券、秒杀的接口
│   │    └──order.js               # 有关订单的接口
│   │    └──role.js                # 有关权限的接口
│   │    └──roleApi.js             # 有关菜单的接口
│   │    └──sms.js                 # 有关短信的接口
│   │    └──store.js               # 有关商品的接口
│   │    └──storePoint.js          # 有关提货点的接口
│   │    └──productCreateApi.js    # 有关附件上传的接口
│   │    └──systemadmin.js         # 有关管理员的接口
│   │    └──systemConfig.js        # 有关系统配置的接口
│   │    └──systemFormConfig.js    # 有关表单配置的接口
│   │    └──systemGroup.js         # 有关组合数据的接口
│   │    └──systemSetting.js       # 有关上传文件的接口
│   │    └──user.js                # 有关用户的接口
│   │    └──wxApi.js               # 有关微信的接口
│   ├── assets                 # 图片、svg 等静态资源
│   ├── components            # 公共组件
│   │    └──articleList       # 文章列表
│   │    └──attrFrom          # 商品规格
│   │    └──Breadcrumb        # 头部标题标签
│   │    └──cards             # 统计小方块
│   │    └──echarts           # 统计图
│   │    └──Category          # 分类 
│   │    └──customerInfo      # 用户列表
│   │    └──FormGenerator     # 表单配置
│   │    └──couponList        # 优惠劵列表
│   │    └──goodsList         # 商品列表
│   │    └──Hamburger         # 导航收缩组件
│   │    └──HeaderSearch      # 导航搜索组件
│   │    └──iconFrom          # 导航添加图标
│   │    └──RightPanel        # 右侧设置按钮，设置导航相关
│   │    └──Screenfull        # 全屏
│   │    └──SvgIcon           # svg图标 
│   │    └──Tinymce           # 富文本编辑器
│   │    └──ThemePicker       # 右侧设置按钮，设置组题颜色
│   │    └──templatesFrom     # 运费模板
│   │    └──uploadPicture     # 上传图片组件
│   │    └──Upload            # 上传文件组件
│   │    └──UploadExcel       # 下载Excel
│   │    └──userList          # 用户列表
│   │    └──TopNav            # 顶部一级导航
│   │    └──TimeSelect        # 时间段选择
│   │    └──verifition        # 滑块验证
│   ├── layouts               # 导航布局
│   │    ├──index             # 主页面
│   │    ├──components        # 导航组件
│   │        └──Settings      # 右边小按钮，设置导航等
│   │        └──Sidebar       # 侧边导航
│   │        └──TagsView      # tab标签页导航
│   │        └──Navbar        # 头部导航
│   │        └──AppMain       # 导航路由
│   │        └──index.js      # 组件引用
│   │    └──mixins            # 自适应大小
│   ├── libs                  # 公共js方法
│   │    └──settingMer        # 配置请求地址
│   ├── views                 # 所有页面
│   │    └──login                    # 登录
│   │         └──index               # 登录
│   │    └──dashboard                # 首页
│   │    └──store                    # 商品
│   │         └──creatStore          # 添加商品
│   │         └──storeAttr           # 商品规格
│   │         └──sort                # 商品分类
│   │         └──taoBao              # 复制商品
│   │         └──storeComment        # 商品评论
│   │    └──order                    # 订单管理
│   │    └──marketing                # 营销
│   │         └──coupon              # 优惠劵
│   │    └──systemSetting            # 设置
│   │         └──administratorAuthority     # 管理权限
│   │              └──adminList      # 管理员列表
│   │              └──identityManager# 身份管理
│   │              └──permissionRules# 权限规则
│   │         └──logistics           # 物流设置
│   │              └──cityList       # 城市数据
│   │              └──companyList    # 物流公司
│   │              └──config         # 物流配置
│   │              └──shippingTemplates      # 运费模板 
│   │         └──setting             # 系统设置
│   │         └──systemStore         # 提货点设置
│   │    └──appSetting               # 应用 小程序 公众号设置
│   │    └──content                  # 内容
│   │         └──article             # 文章管理
│   │         └──articleclass        # 文章分类
│   │    └──datas                    # 统计数据
│   │    └──user                     # 用户
│   │         └──list                # 用户管理
│   │         └──grade               # 用户等级/标签
│   │         └──group               # 用户分组
│   │    └──distribution             # 分销设置
│   │    └──maintain                 # 维护 
│   │         └──devconfig           # 组合数据 
│   │         └──formConfig          # 表单配置 
│   │    └──financial                # 财务
│   │    └──error-page               # 错误页
│   │         └──404                 # 错误页404
│   │         └──403                 # 错误页403
│   ├── filters                      # 过滤器
│   ├── router                       # 路由配置
│   │    └──modules                  # 页面路由模块
│   │         └──content.js          # 有关内容 文章
│   │         └──user.js             # 有关用户
│   │         └──appSetting.js       # 有关应用
│   │         └──marketing.js        # 有关优惠券、秒杀
│   │         └──distribution.js     # 有关分销
│   │         └──order.js            # 有关订单
│   │         └──financial.js        # 有关财务
│   │         └──store.js            # 有关商品
│   │         └──maintain.js         # 有关维护
│   │         └──operation.js        # 有关设置
│   │    └──index.js                 # 路由的汇总
│   ├── store                        # Vuex 状态管理
│   ├── utils                        # 全局公用方法
│   │    └──request.js               # 请求封装
│   │    └──settingMer.js            # 请求地址配置
│   ├── styles                       # 样式管理
│   ├── permission.js                # 路由拦截
│   ├── main.js                      # 入口文件 加载组件 初始化等
│   └── App.vue                      # 入口页面
├── tests                      # 测试
├── .env.xxx                   # 环境变量配置
├── .eslintrc.js               # eslint 配置项
├── .babelrc                   # babel-loader 配置
├── .travis.yml                # 自动化CI配置
├── vue.config.js              # vue-cli 配置
├── postcss.config.js          # postcss 配置
└── package.json               # package.json


~~~
## 开发

```bash
# 克隆项目
git clone https://gitee.com/ZhongBangKeJi/crmeb_java/

# 进入项目目录
cd ##

# 安装依赖
npm install

# 建议不要直接使用 cnpm 安装依赖，会有各种诡异的 bug。可以通过如下操作解决 npm 下载速度慢的问题
npm install --registry=https://registry.npm.taobao.org

# 启动服务
npm run dev
```

浏览器访问 http://localhost:9527

## 发布

```bash
# 构建测试环境
npm run build:stage

# 构建生产环境
npm run build:prod
```

## 其它

```bash
# 预览发布环境效果
npm run preview

# 预览发布环境效果 + 静态资源分析
npm run preview -- --report

# 代码格式检查
npm run lint

# 代码格式检查并自动修复
npm run lint -- --fix
```


