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