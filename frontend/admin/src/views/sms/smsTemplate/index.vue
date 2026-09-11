<template>
  <!--v-if="isLogin"-->
  <div class="divBox" v-if="isLogin">
    <el-card v-loading="fullscreenLoading" class="box-card">
      <template #header>
        <div class="clearfix">
          <div class="container">
            <router-link :to="{ path: '/operation/onePassConfig' }">
              <el-button class="mb35" :icon="ArrowLeft">返回</el-button>
            </router-link>
          </div>
          <div class="acea-row">
            <el-button type="primary" @click="add" class="mr20">添加短信模板</el-button>
            <el-alert style="width: 80%" title="短信模板申请后通过审核才能看到，审核时间3-5个工作日。" type="warning" :closable="false"
              effect="dark">
            </el-alert>
          </div>
        </div>
      </template>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" highlight-current-row>
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column prop="temp_id" label="模板ID" min-width="80" />
        <el-table-column prop="title" label="模板名称" min-width="120" />
        <el-table-column prop="content" label="模板内容" min-width="500" />
        <el-table-column label="模板类型" min-width="100">
          <template #default="{ row }">
            <span>{{ typesFilter(row.temp_type) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="模板状态">
          <template #default="{ row }">
            <span>{{ statusFilter(row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="mark" label="审核结果" min-width="100" />
      </el-table>
      <div class="block">
        <el-pagination :page-sizes="[20, 40, 60, 80]" :page-size="tableFrom.limit" :current-page="tableFrom.page"
          layout="total, sizes, prev, pager, next, jumper" :total="tableData.total" @size-change="handleSizeChange"
          @current-change="pageChange" />
      </div>
    </el-card>

    <!--编辑-->
    <el-dialog title="添加模板" v-model="dialogVisible" width="500px" :before-close="handleClose">
      <zb-parser v-if="dialogVisible" :form-id="110" :is-create="isCreate" :edit-data="editData" @submit="handlerSubmit"
        @resetForm="resetForm" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/store/modules/user';
import { ArrowLeft } from '@element-plus/icons-vue';
import { smsTempLstApi, tempCreateApi } from '@/api/sms';
import { roterPre } from '@/settings';
import zbParser from '@/components/FormGenerator/components/parser/ZBParser';
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'SmsTemplate' });

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const isLogin = computed(() => userStore.isLogin);

function statusFilter(status) {
  const statusMap = {
    0: '不可用',
    1: '可用',
  };
  return statusMap[status];
}
function typesFilter(status) {
  const statusMap = {
    1: '验证码',
    2: '通知',
    3: '推广',
  };
  return statusMap[status];
}

const isCreate = ref(0);
const editData = ref({});
const dialogVisible = ref(false);
const fullscreenLoading = ref(false);
const listLoading = ref(false);
const tableData = reactive({
  data: [],
  total: 0,
});
const tableFrom = reactive({
  page: 1,
  limit: 20,
});

function resetForm(formValue) {
  handleClose();
}
function handleClose() {
  dialogVisible.value = false;
  editData.value = {};
}
const handlerSubmit = Debounce(function (formValue) {
  tempCreateApi(formValue).then((data) => {
    ElMessage.success('新增成功');
    dialogVisible.value = false;
    editData.value = {};
    getList();
  });
});
function add() {
  dialogVisible.value = true;
}
// 查看是否登录
function onIsLogin() {
  fullscreenLoading.value = true;
  userStore
    .checkIsLogin()
    .then(async (res) => {
      const data = res;
      if (!data.status) {
        ElMessage.warning('请先登录');
        router.push('/operation/onePassConfig?url=' + route.path);
      } else {
        getList();
      }
      fullscreenLoading.value = false;
    })
    .catch((res) => {
      router.push('/operation/onePassConfig?url=' + route.path);
      fullscreenLoading.value = false;
    });
}
// 列表
function getList() {
  listLoading.value = true;
  smsTempLstApi(tableFrom)
    .then((res) => {
      tableData.data = res.data;
      tableData.total = res.count;
      listLoading.value = false;
    })
    .catch((res) => {
      listLoading.value = false;
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
// 表格搜索
function userSearchs() {
  tableFrom.page = 1;
  getList();
}

onMounted(() => {
  if (!isLogin.value) {
    router.push('/operation/onePassConfig?url=' + route.path);
  } else {
    getList();
  }
});
</script>

<style scoped lang="scss">
.selWidth {
  width: 350px !important;
}
</style>
