<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form ref="formRef" inline :model="form">
          <el-form-item label="关键字：">
            <el-input v-model="form.keywords" placeholder="请输入关键字" class="selWidth" clearable>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handlerSearch" v-hasPermi="['admin:express:list']"
              >搜索</el-button
            >
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <template #header>
        <div class="clearfix">
          <el-button type="primary" @click="addExpress" v-hasPermi="['admin:express:sync']"
            >同步物流公司</el-button
          >
        </div>
      </template>
      <el-table v-loading="loading" :data="tableData.list">
        <el-table-column prop="id" label="ID" min-width="180" />
        <el-table-column label="物流公司名称" min-width="150" prop="name" />
        <el-table-column min-width="200" label="编码" prop="code" />
        <el-table-column min-width="100" label="排序" prop="sort" sortable />
        <el-table-column label="是否显示" min-width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.isShow"
              class="demo"
              :active-value="true"
              :inactive-value="false"
              active-text="开启"
              inactive-text="关闭"
              @change="bindStatus(scope.row)"
              v-if="checkPermi(['admin:express:update:show'])"
            />
          </template>
        </el-table-column>
        <el-table-column prop="address" fixed="right" width="120" label="操作">
          <template #default="scope">
            <el-button
              class="list-btn"
              v-if="scope.row.net"
              link

              @click="bindEdit(scope.row)"
              v-hasPermi="['admin:express:info']"
              >收件网点名称编辑</el-button
            >
            <el-button
              class="list-btn"
              v-else-if="scope.row.partnerId"
              link

              @click="bindEdit(scope.row)"
              v-hasPermi="['admin:express:info']"
              >月结账号编辑</el-button
            >
            <el-button
              class="list-btn"
              v-else
              link

              @click="bindEdit(scope.row)"
              v-hasPermi="['admin:express:info']"
              >编辑</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <div class="block-pagination">
        <el-pagination
          :page-sizes="[20, 40, 60, 80]"
          :page-size="tableData.limit"
          :current-page="tableData.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @current-change="pageChange"
          @size-change="handleSizeChange"
          background
        />
      </div>
    </el-card>
    <el-dialog title="编辑物流公司" v-model="dialogVisible" width="540px" :before-close="handleClose">
      <el-form :model="formData" :rules="rules" ref="formDataRef" label-width="75px" class="demo-ruleForm">
        <el-form-item label="月结账号：" prop="account" v-if="formData.partnerId">
          <el-input v-model="formData.account" placeholder="请输入月结账号"></el-input>
        </el-form-item>
        <el-form-item label="月结密码：" prop="password" v-if="formData.partnerKey">
          <el-input v-model="formData.password" placeholder="请输入月结密码"></el-input>
        </el-form-item>
        <el-form-item label="网点名称：" prop="netName" v-if="formData.net">
          <el-input v-model="formData.netName" placeholder="请输入网点名称"></el-input>
        </el-form-item>
        <el-form-item label="排序：" prop="sort">
          <el-input-number
            controls-position="right"
            v-model="formData.sort"
            :min="0"
            :max="9999"
            label="排序"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="是否启用：" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="false" :value="false">关闭</el-radio>
            <el-radio :label="true" :value="true">开启</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submit('formDataRef')" v-hasPermi="['admin:express:update']">确定</el-button>
        </span>
      </template>
      <!--<parser v-if="formShow" ref="formBox" class="formBox" :form-conf="formConf" :form-edit-data="formData" :is-edit="isCreate === 1" @submit="submit" />-->
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import parser from '@/components/FormGenerator/components/parser/Parser';
import * as systemFormConfigApi from '@/api/systemFormConfig.js';
import * as logistics from '@/api/logistics.js';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'CompanyList' });

const { proxy } = getCurrentInstance();
const constants = proxy.$constants;

// 表单
const formConf = ref({ fields: [] });
const form = reactive({
  keywords: '',
});
const tableData = ref({
  list: [],
  page: 1,
  limit: 20,
  total: 0,
});
const page = ref(1);
const limit = ref(20);
const loading = ref(false);
const dialogVisible = ref(false);
const fromType = ref('add');
const formData = reactive({
  status: false,
});
const isCreate = ref(0);
const formShow = ref(false);
const editId = ref(0);
const rules = reactive({
  sort: [{ required: true, message: '请输入排序', trigger: 'blur' }],
  account: [{ required: true, message: '请输入月结账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入月结密码', trigger: 'blur' }],
  netName: [{ required: true, message: '请输入网点名称', trigger: 'blur' }],
});

const formDataRef = ref(null);

function handlerSearch() {
  page.value = 1;
  getExpressList();
}
//  获取物流公司列表
function getExpressList() {
  loading.value = true;
  logistics
    .expressList({
      page: page.value,
      limit: limit.value,
      keywords: form.keywords,
    })
    .then((res) => {
      loading.value = false;
      tableData.value = res;
    })
    .catch(() => {
      loading.value = false;
    });
}
// 物流开关
function bindStatus(item) {
  logistics
    .expressUpdateShow({
      account: item.account,
      code: item.code,
      id: item.id,
      isShow: item.isShow,
      name: item.name,
      sort: item.sort,
    })
    .then((res) => {
      ElMessage.success('操作成功');
      // this.getExpressList()
    })
    .catch(() => {
      item.isShow = !item.isShow;
    });
}
// 分页
function pageChange(e) {
  page.value = e;
  getExpressList();
}
function handleSizeChange(e) {
  limit.value = e;
  getExpressList();
}
// 添加物流公司
function addExpress() {
  logistics.expressSyncApi().then((res) => {
    ElMessage.success('同步成功');
    page.value = 1;
    getExpressList();
  });
}
// 删除物流公司
function bindDelete(item) {
  proxy.$modalSure().then(() => {
    logistics.expressDelete({ id: item.id }).then((res) => {
      ElMessage.success('删除成功');
      getExpressList();
    });
  });
}
// 表单提交
const submit = Debounce(function (formName) {
  proxy.$refs[formName].validate((valid) => {
    if (valid) {
      logistics.expressUpdate(formData).then((res) => {
        ElMessage.success('操作成功');
        handleClose();
        getExpressList();
      });
    } else {
      return false;
    }
  });
});
//  关闭模态框
function handleClose(done) {
  formShow.value = false;
  // this.formData = {}
  formConf.value.fields = [];
  dialogVisible.value = false;
  isCreate.value = 0;
}
// 编辑
function bindEdit(item) {
  dialogVisible.value = true;
  editId.value = item.id;
  logistics.expressInfo({ id: item.id }).then((res) => {
    Object.assign(formData, res);
  });
}

// created
getExpressList();
</script>

<style lang="scss" scoped>
.selWidth {
  width: 350px;
}
.el-icon-plus {
  margin-right: 5px;
}

.demo .el-switch__label {
  position: absolute;
  display: none;
  color: #fff;
}

/*打开时文字位置设置*/
.demo .el-switch__label--right {
  z-index: 1;
}

/*关闭时文字位置设置*/
.demo .el-switch__label--left {
  z-index: 1;
  left: 19px;
}

/*显示文字*/
.demo .el-switch__label.is-active {
  display: block;
}

.demo.el-switch .el-switch__core,
.el-switch .el-switch__label {
  width: 60px !important;
}
.formBox {
  .el-input-number--medium {
    width: 100px;
  }
}
.list-btn {
  padding: 0 !important;
}
</style>
