<template>
  <div class="divBox">
    <el-card class="box-card" shadow="never">
      <template #header>
        <div class="clearfix mt5">
          <el-form inline>
            <el-form-item label="用户搜索：">
              <UserSearchInput ref="userSearchInputRef" v-model="tableFrom" />
            </el-form-item>
            <el-button type="primary" icon="ios-search" label="default" @click="userSearchs">搜索</el-button>
            <el-button class="ResetSearch mr14" @click="reset()">重置</el-button>
            <!-- <el-form-item label="用户名称：">
              <el-input v-model="tableFrom.keywords" placeholder="请输入用户名称" class="selWidth"> </el-input>
              <el-button class="ml30" type="primary" @click="search">搜索</el-button>
            </el-form-item> -->
          </el-form>
        </div>
      </template>
      <el-table v-loading="loading" :data="tableData.data" width="800px">
        <el-table-column label="" width="40">
          <template #default="scope">
            <el-radio
              v-model="templateRadio"
              :label="scope.row.uid" :value="scope.row.uid"
              @change="getTemplateRow(scope.$index, scope.row)"
              >&nbsp;</el-radio
            >
          </template>
        </el-table-column>
        <el-table-column prop="uid" label="ID" min-width="60" />
        <el-table-column prop="nickname" label="微信用户名称" min-width="130" />
        <el-table-column label="用户头像" min-width="80">
          <template #default="scope">
            <div class="demo-image__preview">
              <el-image class="tabImage" :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]" preview-teleported />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="性别" min-width="80">
          <template #default="scope">
            <span>{{ saxFilter(scope.row.sex) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="地区" min-width="130">
          <template #default="scope">
            <span>{{ scope.row.addres }}</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="acea-row row-between">
        <el-pagination
          :page-sizes="[10, 20, 30, 40]"
          :page-size="tableFrom.limit"
          :current-page="tableFrom.page"
          layout=" sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
        />
        <div class="mt30">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="closeDialog">确定</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { userListApi } from '@/api/user';
import UserSearchInput from '@/components/base/UserSearchInput.vue';

defineOptions({ name: 'UserList' });

const emit = defineEmits(['closeDialog', 'getTemplateRow']);

// 原 filters: { saxFilter, statusFilter }
function saxFilter(status) {
  const statusMap = {
    0: '未知',
    1: '男',
    2: '女',
  };
  return statusMap[status];
}

function statusFilter(status) {
  const statusMap = {
    wechat: '微信用户',
    routine: '小程序用户',
  };
  return statusMap[status];
}

const userSearchInputRef = ref(null);
const templateRadio = ref(0);
const loading = ref(false);
const tableData = ref({
  data: [],
  total: 0,
});
const tableFrom = ref({
  page: 1,
  limit: 10,
  searchType: 'all',
  content: '',
  // keywords: '',
});

onMounted(() => {
  getList();
});

function closeDialog() {
  emit('closeDialog');
}

function getTemplateRow(idx, row) {
  emit('getTemplateRow', row);
}

// 列表
function getList() {
  loading.value = true;
  userListApi(tableFrom.value)
    .then((res) => {
      tableData.value.data = res.list;
      tableData.value.total = res.total;
      loading.value = false;
    })
    .catch((res) => {
      ElMessage.error(res.message);
      loading.value = false;
    });
}

function search() {
  loading.value = true;
  userListApi({ keywords: tableFrom.value.keywords })
    .then((res) => {
      tableData.value.data = res.list;
      tableData.value.total = res.total;
      loading.value = false;
    })
    .catch((res) => {
      ElMessage.error(res.message);
      loading.value = false;
    });
}

function pageChange(page) {
  tableFrom.value.page = page;
  getList();
}

function handleSizeChange(val) {
  tableFrom.value.limit = val;
  getList();
}

// 搜索
function userSearchs() {
  tableFrom.value.page = 1;
  getList();
}

// 重置
function reset() {
  tableFrom.value = {
    searchType: 'all',
    content: '',
  };
  userSearchInputRef.value.clearInput();
  getList();
}
</script>

<style lang="scss" scoped>
:deep(.el-table .cell ){
  padding-left: 20px !important;
}
.mt5 {
  margin-top: 5px;
}
:deep(.el-card__body) {
  padding: 30px 24px !important;
}
:deep(.el-form-item) {
  margin-bottom: 0px !important;
}
:deep(.el-card__body) {
  padding: 20px 24px !important;
}
</style>
