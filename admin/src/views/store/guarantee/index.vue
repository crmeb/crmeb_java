<template>
  <div class="divBox relative">
    <el-card class="box-card" shadow="never" :bordered="false">
      <div class="clearfix">
        <el-button type="primary" v-hasPermi="['admin:product:guarantee:add']" @click="handlerOpenEdit(0)"
          >添加保障服务</el-button
        >
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        class="mt20"

        :highlight-current-row="true"
      >
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column prop="name" label="服务条款" min-width="150" />
        <el-table-column label="服务条款图标" min-width="120">
          <template #default="scope">
            <div class="demo-image__preview line-heightOne">
              <el-image :src="scope.row.icon" :preview-src-list="[scope.row.icon]" preview-teleported />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="服务内容描述" min-width="250" :show-overflow-tooltip="true" />
        <el-table-column prop="sort" label="排序" min-width="80" />
        <el-table-column label="创建时间" min-width="150">
          <template #default="scope">
            <span>{{ scope.row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="是否显示" min-width="90" fixed="right">
          <template #default="scope">
            <el-switch
              v-if="checkPermi(['admin:product:guarantee:show:status'])"
              v-model="scope.row.isShow"
              :active-value="true"
              :inactive-value="false"
              active-text="显示"
              inactive-text="隐藏"
              @click="onchangeIsShow(scope.row)"
            />
            <div v-else>{{ scope.row.isShow ? '显示' : '隐藏' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <a @click="handlerOpenEdit(1, scope.row)" v-hasPermi="['admin:product:guarantee:update']">编辑</a>
            <el-divider direction="vertical"></el-divider>
            <a @click="handlerOpenDel(scope.row)" v-hasPermi="['admin:product:guarantee:delete']">删除</a>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
<script setup>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import { reactive, ref, onMounted, getCurrentInstance } from 'vue';
import { ElMessage, ElMessageBox } from '@/utils/elementPlusFeedback';
import {
  guaranteeListApi,
  guaranteeAddApi,
  guaranteeDelApi,
  guaranteeUpdateApi,
  guaranteeUpdateShowApi,
} from '@/api/store';
import { checkPermi } from '@/utils/permission'; // 权限判断函数

const { proxy } = getCurrentInstance();

const tableData = reactive({
  data: [],
  total: 0,
});
const listLoading = ref(false);
const keyNum = ref(0);
const id = ref(0);
const loading = ref(false); // 原 submit 中引用 this.loading（未声明），保留
// tableFrom 在 pageChange/handleSizeChange 中被引用但未在原 data 中声明（保留原逻辑）
const tableFrom = reactive({ page: 1, limit: 20 });

// 列表
function getList() {
  listLoading.value = true;
  guaranteeListApi()
    .then((res) => {
      tableData.data = res;
      listLoading.value = false;
    })
    .catch((err) => {
      listLoading.value = false;
      ElMessage.error(err.message);
    });
}
function pageChange(page) {
  tableFrom.page = page;
  getList();
}
function handleSizeChange(val) {
  tableFrom.limit = val;
  getList();
}
function handlerOpenEdit(isCreate, editDate) {
  id.value = editDate ? editDate.id : 0;
  proxy.$modalParserFrom(
    isCreate === 0 ? '添加服务条款' : '编辑服务条款',
    '保障服务',
    isCreate,
    isCreate === 0 ? {} : Object.assign({}, editDate),
    function (formValue) {
      submit(formValue);
    },
    (keyNum.value += 4),
  );
}
function submit(formValue) {
  const data = {
    id: id.value,
    name: formValue.name,
    content: formValue.content,
    icon: formValue.icon,
    sort: formValue.sort,
    isShow: formValue.isShow,
  };
  !id.value
    ? guaranteeAddApi(data)
        .then((res) => {
          ElMessage.success('操作成功');
          ElMessageBox.close();
          getList();
        })
        .catch(() => {
          loading.value = false;
        })
    : guaranteeUpdateApi(data)
        .then((res) => {
          ElMessage.success('操作成功');
          ElMessageBox.close();
          getList();
        })
        .catch(() => {
          loading.value = false;
        });
}
function handlerOpenDel(rowData) {
  proxy.$modalSure('删除当前保障服务吗').then(() => {
    guaranteeDelApi(rowData.id).then((data) => {
      ElMessage.success('删除成功');
      getList();
    });
  });
}
function onchangeIsShow(row) {
  guaranteeUpdateShowApi(row.id).then(() => {
    ElMessage.success('操作成功');
    getList();
  });
}

onMounted(() => {
  if (checkPermi(['admin:product:guarantee:list'])) getList();
});
</script>
<style>
.upload-form {
  padding-bottom: 0 !important;
}
</style>
