# CRMEB Java 开源商城

单商户商城系统，包含后端 API、管理后台和移动端（H5 / App / 小程序）。

## 目录结构

```
crmeb_java
├── .crmeb/              协作文档目录
│   ├── agents/
│   ├── documents/       文档（接口文档、安装必读）
│   ├── rules/           项目规范
│   ├── skills/          技能
│   └── AGENTS.md
├── backend/             后端
│   ├── crmeb-admin/     管理后台接口服务，默认端口 8080
│   ├── crmeb-front/     移动端接口服务，默认端口 8081
│   ├── crmeb-service/   业务服务层
│   ├── crmeb-common/    公共模块（实体、工具类、常量）
│   ├── crmebimage/      默认图片素材
│   └── sql/             数据库脚本
└── frontend/            前端
    ├── admin/           管理后台
    ├── pc/              PC 端
    └── uni-app/         H5 / App / 小程序端
```

## 技术栈

| 模块 | 技术 |
| --- | --- |
| 后端 | JDK 1.8、SpringBoot 2.2.6、SpringSecurity、MyBatis-Plus、Quartz |
| 数据库 | MySQL 8.0+、Redis 5+ |
| 管理后台 | Vue3、Element Plus、Pinia、Vite |
| 移动端 | uni-app（Vue3）、Pinia |

## 本地运行

### 后端

1. 导入 `backend/sql/Crmeb_v3.1.sql`（已有 v3.0 数据库执行 `backend/sql/v3.0-to-v3.1-upgrade.sql`）。
2. 修改 `crmeb-admin`、`crmeb-front` 下 `src/main/resources/application.yml` 中的数据库、Redis 连接和 `crmeb.imagePath`（指向 `crmebimage` 所在目录，斜杠结尾）。
3. 在 `backend` 目录执行 `mvn clean package -DskipTests`，分别启动 `crmeb-admin/target/Crmeb-admin.jar` 与 `crmeb-front/target/Crmeb-front.jar`。

### 管理后台

```bash
cd frontend/admin
npm install
npm run dev      # 接口地址见 .env.development 中的 VITE_APP_BASE_API
npm run build
```

### 移动端

使用 HBuilderX 打开 `frontend/uni-app` 运行，或使用命令行：

```bash
cd frontend/uni-app
npm install
npm run dev:h5            # H5
npm run dev:mp-weixin     # 微信小程序，产物在 dist/dev/mp-weixin
npm run build:h5
npm run build:mp-weixin
```

接口地址在 `frontend/uni-app/config/app.js` 的 `HTTP_REQUEST_URL` 中配置。

## 约定

- 数据库结构或初始数据有变动时，同时更新完整脚本 `Crmeb_vX.X.sql` 和对应的升级脚本。
- 配置文件中的账号、密码、密钥使用占位值（如 `111111`），不提交真实的连接信息与第三方密钥。
- 系统配置项（支付、短信、存储、小程序等）通过管理后台维护，存放在 `eb_system_config` 表。
- 后台菜单与权限由 `eb_system_menu` 表维护，新增页面需要同步菜单数据。
