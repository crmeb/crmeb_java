<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form :inline="true">
          <el-form-item label="时间选择：">
            <optionDatePicker v-model="timeVal" @changeOptTime="onchangeTime"></optionDatePicker>
          </el-form-item>
          <el-form-item label="评价状态：" class="mr10">
            <el-select
              v-model="tableFrom.isReply"
              placeholder="请选择评价状态"
              @change="seachList"

              class="selWidth"
              clearable
            >
              <el-option label="已回复" value="1"></el-option>
              <el-option label="未回复" value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="商品搜索：" class="mr10">
            <el-input
              v-model="tableFrom.productSearch"
              placeholder="请输入商品名称"
              class="selWidth"

              clearable
            >
            </el-input>
          </el-form-item>
          <el-form-item label="用户搜索：" label-for="nickname">
            <UserSearchInput v-model="tableFrom" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="seachList">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <template #header>
        <div class="clearfix">
          <el-button type="primary" @click="handleAdd" v-hasPermi="['admin:product:reply:save']">添加虚拟评论</el-button>
        </div>
      </template>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column label="商品信息" min-width="400" :show-overflow-tooltip="true">
          <template #default="scope">
            <div class="demo-image__preview acea-row row-middle" v-if="scope.row.storeProduct">
              <el-image
                style="width: 30px; height: 30px"
                :src="scope.row.storeProduct.image"
                :preview-src-list="[scope.row.storeProduct.image]" preview-teleported
                class="mr10"
              />
              <div class="info line1">{{ scope.row.storeProduct.storeName }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="用户名称" min-width="100" />
        <el-table-column prop="productScore" label="商品评分" min-width="90" />
        <el-table-column prop="serviceScore" label="服务评分" min-width="90" />
        <el-table-column label="评价内容" min-width="210">
          <template #default="scope">
            <div class="mb5 content_font">{{ scope.row.comment || '-' }}</div>
            <template v-if="scope.row.pics.length && scope.row.pics[0]">
              <div class="demo-image__preview">
                <el-image
                  :src="item"
                  class="mr5"
                  :preview-src-list="[item]" preview-teleported
                  v-for="(item, index) in scope.row.pics"
                  :key="index"
                />
              </div>
            </template>
          </template>
        </el-table-column>
        <el-table-column prop="merchantReplyContent" label="回复内容" min-width="250">
          <template #default="scope">
            <div>{{ scope.row.merchantReplyContent || '-' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="评价时间" min-width="120">
          <template #default="scope">
            <span> {{ scope.row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <a @click="reply(scope.row.id)" v-hasPermi="['admin:product:reply:comment']">回复</a>
            <el-divider direction="vertical"></el-divider>
            <a @click="handleDelete(scope.row.id, scope.$index)" v-hasPermi="['admin:product:reply:delete']">删除</a>
          </template>
        </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[20, 40, 60, 80]"
          :page-size="tableFrom.limit"
          :current-page="tableFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
          background
        />
      </div>
      <el-dialog title="添加评论" v-model="dialogVisible" width="700px" :before-close="handleClose">
        <creat-comment :key="timer" @getList="seachList"></creat-comment>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="jsx">
import { reactive, ref, watch, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { useRoute } from 'vue-router';
import creatComment from './creatComment.vue';
import { categoryApi, replyListApi, replyDeleteApi, replyCommentApi } from '@/api/store';
import { formatDates } from '@/utils/index';
import { userListApi } from '@/api/user';

defineOptions({ name: 'StoreComment' });

const { proxy } = getCurrentInstance();
const route = useRoute();

// filter: formatDate
function formatDate(time) {
  if (time !== 0) {
    const date = new Date(time * 1000);
    return formatDates(date, 'YYYY-MM-DD hh:mm');
  }
}

const merCateList = ref([]);
const props = {
  children: 'child',
  label: 'name',
  value: 'id',
  emitPath: false,
};
const fromList = proxy.$constants.fromList;
const tableData = reactive({
  data: [],
  total: 0,
});
const listLoading = ref(true);
const tableFrom = reactive({
  page: 1,
  limit: 20,
  isReply: '',
  dateLimit: '',
  searchType: 'all',
  content: '',
  // uid: '',
  nickname: '',
  productSearch: '',
  isDel: false,
});
const timeVal = ref([]);
const loading = ref(false);
const uids = ref([]);
const options = ref([]);
const dialogVisible = ref(false);
const timer = ref('');

//重置
function handleReset() {
  tableFrom.isReply = '';
  tableFrom.nickname = '';
  tableFrom.productSearch = '';
  tableFrom.dateLimit = '';
  (tableFrom.searchType = 'all'), (tableFrom.content = ''), (timeVal.value = []);
  getList();
}
function remoteMethod(query) {
  if (query !== '') {
    loading.value = true;
    setTimeout(() => {
      loading.value = false;
      userListApi({ keywords: query, page: 1, limit: 10 }).then((res) => {
        options.value = res.list;
      });
    }, 200);
  } else {
    options.value = [];
  }
}
function seachList() {
  dialogVisible.value = false;
  tableFrom.page = 1;
  getList();
}
// 回复
function reply(id) {
  proxy.$modalPrompt('textarea', '回复', null, '回复内容').then((V) => {
    replyCommentApi({
      ids: id,
      merchantReplyContent: V,
    }).then((res) => {
      ElMessage({
        type: 'success',
        message: '回复成功',
      });
      getList();
    });
  });
}
// 选择时间
function selectChange(tab) {
  timeVal.value = [];
  tableFrom.page = 1;
  getList();
}
// 商户分类；
function getCategorySelect() {
  categoryApi({ status: -1, type: 1 })
    .then((res) => {
      merCateList.value = res;
    })
    .catch((res) => {
      ElMessage.error(res.message);
    });
}
function handleAdd() {
  dialogVisible.value = true;
  timer.value = new Date().getTime();
}
function handleClose() {
  dialogVisible.value = false;
}
// 具体日期
function onchangeTime(e) {
  timeVal.value = e;
  tableFrom.dateLimit = e ? timeVal.value.join(',') : '';
  tableFrom.page = 1;
  getList();
}
// 删除
function handleDelete(id, idx) {
  proxy.$modalSure().then(() => {
    replyDeleteApi(id).then(() => {
      ElMessage.success('删除成功');
      if (tableData.data.length === 1 && tableFrom.page > 1) tableFrom.page = tableFrom.page - 1;
      getList();
    });
  });
}
// 列表
function getList() {
  listLoading.value = true;
  tableFrom.uid = uids.value.join(',');
  replyListApi(tableFrom)
    .then((res) => {
      tableData.data = res.list;
      tableData.total = res.total;
      listLoading.value = false;
    })
    .catch(() => {
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

watch(route, (to, from) => {
  getList();
  getCategorySelect();
});

onMounted(() => {
  // this.getLstFilterApi()
  getList();
  getCategorySelect();
});
</script>

<style scoped lang="scss">
.table {
  .el-image {
    width: auto !important;
    height: 30px !important;
  }

  :deep(.el-image__inner),
  .el-image__placeholder,
  .el-image__error {
    width: auto !important;
  }
}

.info {
  width: 63%;
}
</style>
