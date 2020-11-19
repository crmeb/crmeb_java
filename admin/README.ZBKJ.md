
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