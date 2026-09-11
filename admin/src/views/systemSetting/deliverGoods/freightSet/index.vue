<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form ref="formRef" inline :model="form">
          <el-form-item label="模板名称：">
            <el-input v-model="form.keywords" placeholder="请输入模板名称" class="selWidth" clearable>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <template #header>
        <div class="clearfix">
          <el-button type="primary" @click="handleSubmit()" v-hasPermi="['admin:shipping:templates:save']"
            >添加运费模板</el-button
          >
        </div>
      </template>
      <el-table v-loading="loading" :data="tableData.list" style="width: 100%">
        <el-table-column prop="id" label="ID" min-width="60" />
        <el-table-column label="模板名称" min-width="180" prop="name" />
        <el-table-column min-width="100" label="计费方式" prop="type">
          <template #default="{ row }">
            <p>{{ typeFilter(row.type) }}</p>
          </template>
        </el-table-column>
        <el-table-column min-width="100" label="包邮方式" prop="appoint">
          <template #default="{ row }">
            <p>{{ statusFilter(row.appoint) }}</p>
          </template>
        </el-table-column>
        <el-table-column label="排序" min-width="100" prop="sort" />
        <el-table-column label="添加时间" min-width="150" prop="createTime" />
        <el-table-column prop="address" fixed="right" width="120" label="操作">
          <template #default="scope">
            <a @click="bindEdit(scope.row)" v-hasPermi="['admin:shipping:templates:info']">编辑</a>
            <el-divider direction="vertical"></el-divider>
            <a @click="bindDelete(scope.row)" v-hasPermi="['admin:shipping:templates:delete']">删除</a>
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
    <CreatTemplates ref="addTemplatesRef" @getList="getList" />
  </div>
</template>

<script setup>
import { reactive, ref, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import CreatTemplates from './creatTemplates';
import * as logistics from '@/api/logistics.js';

defineOptions({ name: 'ShippingTemplates' });

const { proxy } = getCurrentInstance();

function statusFilter(status) {
  const statusMap = {
    0: '全国包邮',
    1: '部分包邮',
    2: '自定义',
  };
  return statusMap[status];
}
function typeFilter(status) {
  const statusMap = {
    0: '无',
    1: '按件数',
    2: '按重量',
    3: '按体积',
  };
  return statusMap[status];
}

const isShow = ref(false);
const dialogVisible = ref(false);
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

const addTemplatesRef = ref(null);

// 添加
function handleSubmit() {
  addTemplatesRef.value.dialogVisible = true;
  if (!sessionStorage.getItem('cityList')) addTemplatesRef.value.getCityList();
  addTemplatesRef.value.changType(0);
}
function handleSearch() {
  page.value = 1;
  getDataList();
}
// 分页
function pageChange(e) {
  page.value = e;
  getDataList();
}
function handleSizeChange(e) {
  limit.value = e;
  getDataList();
}
// 数据列表
function getDataList() {
  loading.value = true;
  logistics
    .shippingTemplatesList({
      keywords: form.keywords,
      page: page.value,
      limit: limit.value,
    })
    .then((res) => {
      loading.value = false;
      tableData.value = res;
    });
}
// 编辑
function bindEdit(item) {
  if (!sessionStorage.getItem('cityList')) addTemplatesRef.value.getCityList();
  addTemplatesRef.value.getInfo(item.id, item.appoint);
  addTemplatesRef.value.changType(1);
}
// 删除
function bindDelete(item) {
  proxy.$modalSure().then(() => {
    logistics.shippingDetete({ id: item.id }).then((res) => {
      ElMessage.success('删除成功');
      getDataList();
    });
  });
  // logistics.shippingDetete()
}
function getList() {
  getDataList();
}

// created
getDataList();
</script>

<style scoped lang="scss">
.selWidth {
  width: 350px;
}
</style>
