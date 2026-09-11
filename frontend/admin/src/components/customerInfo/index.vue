<template>
  <el-dialog
    v-model="dialogFormVisible"
    title="请选择管理员"
    append-to-body
    width="900px"
    @close="cancel"
  >
    <el-form ref="formRef" inline :model="artFrom">
      <el-form-item label="身份：">
        <el-select v-model="artFrom.roles" placeholder="请输入身份" clearable class="selWidth">
          <el-option v-for="item in roleList.list" :key="item.id" :label="item.roleName" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="姓名：">
        <el-input v-model="artFrom.realName" placeholder="请输入姓名或者账号" class="selWidth"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table
      v-loading="loading"
      :row-style="{ height: '50px' }"
      :data="tableData"
      max-height="400px"

      style="width: 100%"
    >
      <el-table-column label="" width="55">
        <template #default="{ row, index }">
          <el-radio v-model="templateRadio" :label="row.uid" :value="row.uid" @change="getTemplateRow(row)">&nbsp;</el-radio>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="ID" sortable width="80"> </el-table-column>
      <el-table-column prop="realName" label="姓名" min-Width="120"> </el-table-column>
      <!--<el-table-column-->
      <!--label="客服头像"-->
      <!--min-Width="100">-->
      <!--<template #default="{ row, index }" class="picMiddle">-->
      <!--<div class="demo-image__preview">-->
      <!--<el-image-->
      <!--:src="row.avatar"-->
      <!--:preview-src-list="[row.avatar]"-->
      <!--/>-->
      <!--</div>-->
      <!--</template>-->
      <!--</el-table-column>-->
      <el-table-column prop="account" label="账号" min-Width="120" />
      <el-table-column label="身份" prop="realName" min-width="230">
        <template #default="scope">
          <el-tag

            type="info"
            v-for="(item, index) in scope.row.roleNames.split(',')"
            :key="index"
            class="mr5"
            >{{ item }}</el-tag
          >
        </template>
      </el-table-column>
      <el-table-column label="最后登录时间" prop="lastTime" min-width="180">
        <template #default="scope">
          <span>{{ $filters.filterEmpty(scope.row.lastTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="最后登录IP" prop="lastIp" min-width="150">
        <template #default="scope">
          <span>{{ $filters.filterEmpty(scope.row.lastIp) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" min-width="100">
        <template #default="scope">
          <span>{{ $filters.filterShowOrHide(scope.row.status) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="删除标记" prop="status" min-width="100">
        <template #default="scope">
          <span>{{ $filters.filterYesOrNo(scope.row.isDel) }}</span>
        </template>
      </el-table-column>
    </el-table>
    <div class="block">
      <el-pagination
        class="mt20"
        @size-change="sizeChange"
        @current-change="pageChange"
        :current-page="artFrom.page"
        :page-sizes="[20, 40, 60, 100]"
        :page-size="artFrom.limit"
        layout="sizes, prev, pager, next, jumper"
        :total="total"
      >
      </el-pagination>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue';
import * as systemAdminApi from '@/api/systemadmin.js';
import * as roleApi from '@/api/role.js';
import { checkPermi } from '@/utils/permission'; // 权限判断函数

defineOptions({ name: 'index' });

const emit = defineEmits(['upImgUid']);

const { proxy } = getCurrentInstance();

const constants = proxy.$constants;
const formRef = ref(null);
const loading = ref(false);
const templateRadio = ref('');
const dialogFormVisible = ref(false);
const tableData = ref([]);
const artFrom = ref({
  page: 1,
  limit: 20,
  status: 1,
  realName: '',
  roles: '',
});
const total = ref(0);
const timeVal = ref('');
const roleList = ref([]);

// created
if (checkPermi(['admin:system:role:list'])) handleGetRoleList();

//重置
function handleReset() {
  artFrom.value.realName = '';
  artFrom.value.roles = '';
  tableList();
}

function handleGetRoleList() {
  const _pram = {
    page: 1,
    limit: 9999,
  };
  roleApi.getRoleList(_pram).then((data) => {
    roleList.value = data;
  });
}

function getTemplateRow(row) {
  dialogFormVisible.value = false;
  emit('upImgUid', row);
}

function tableList() {
  loading.value = true;
  systemAdminApi
    .adminList(artFrom.value)
    .then((data) => {
      tableData.value = data.list;
      total.value = data.total;
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
    });
  // let that = this;
  // that.loading = true;
  // userListApi(that.artFrom).then(res=>{
  //   that.loading = false;
  //   that.tableData = res.list;
  //   that.total = res.total
  // })
}

//切换显示条数
function sizeChange(index) {
  artFrom.value.limit = index;
  tableList();
}

//切换页数
function pageChange(index) {
  artFrom.value.page = index;
  tableList();
}

function onchangeTime(e) {
  artFrom.value.page = 1;
  if (e !== null) {
    artFrom.value.data = e.join(',');
  } else {
    artFrom.value.data = '';
  }
  tableList();
}

function search() {
  timeVal.value = '';
  artFrom.value.page = 1;
  tableList();
}

function cancel() {
  artFrom.value = {
    page: 1,
    limit: 20,
    data: '',
    realName: '',
  };
  timeVal.value = '';
  templateRadio.value = '';
}
</script>

<style lang="scss">
.el-input-group__append,
.el-input-group__prepend {
  background-color: #1890ff !important;
  color: #fff !important;
  border-color: #1890ff !important;
  border-radius: 0 4px 4px 0;
}
.block {
  text-align: right;
  padding-bottom: 20px;
}
</style>
