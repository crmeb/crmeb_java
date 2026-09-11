<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form inline label-width="75px">
          <el-form-item label="时间选择：">
            <optionDatePicker v-model="timeVal" @changeOptTime="onchangeTime"></optionDatePicker>
            <!-- <el-date-picker
              v-model="timeVal"
              value-format="YYYY-MM-DD"
              format="YYYY-MM-DD"

              type="daterange"
              placement="bottom-end"
              placeholder="自定义时间"
              style="width: 260px"
              @change="onchangeTime"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
            /> -->
          </el-form-item>
          <el-form-item label="砍价状态：">
            <el-select
              v-model="tableFrom.status"
              placeholder="请选择"
              class="filter-item selWidth mr20"
              @change="getList(1)"
              clearable
            >
              <el-option label="进行中" :value="1" />
              <el-option label="未完成" :value="2" />
              <el-option label="已成功" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="用户搜索：">
            <UserSearchInput ref="userSearchInput" v-model="tableFrom" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getList(1)">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" ref="multipleTableRef">
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column label="头像" min-width="80">
          <template #default="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.avatar"
                :preview-src-list="[scope.row.avatar]" preview-teleported
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="发起用户" prop="nickname" min-width="100" />
        <el-table-column label="开启时间" prop="addTime" min-width="150" />
        <el-table-column label="砍价商品" prop="title" min-width="300" :show-overflow-tooltip="true"> </el-table-column>
        <el-table-column label="最低价" prop="bargainPriceMin" min-width="100" />
        <el-table-column label="当前价" prop="nowPrice" min-width="100" />
        <el-table-column label="总砍价次数" prop="peopleNum" min-width="100" />
        <el-table-column label="剩余砍价次数" min-width="100" prop="num" />
        <el-table-column prop="dataTime" label="结束时间" min-width="150" />
        <el-table-column label="砍价状态" min-width="150">
          <template #default="scope">
            <span
              class="tag-background tag-padding"
              :class="scope.row.status == 1 ? 'doingTag' : scope.row.status == 2 ? 'notStartTag' : 'endTag'"
              >{{ $filters.bargainStatusFilter(scope.row.status) }}</span
            >
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <a @click="handleLook(scope.row.id)" class="mr10">查看详情</a>
          </template>
        </el-table-column>
      </el-table>
      <div class="block mb20">
        <el-pagination
          :page-sizes="[10, 20, 30, 40]"
          :page-size="tableFrom.limit"
          :current-page="tableFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
          background
        />
      </div>
    </el-card>
    <el-dialog title="查看详情" v-model="dialogVisible" width="650px" :before-close="handleClose">
      <el-table
        v-loading="listLoadingPink"
        :data="tableDataPink.data"
        style="width: 100%"

        ref="multipleTablePinkRef"
      >
        <el-table-column prop="uid" label="用户id" min-width="50" />
        <el-table-column label="用户头像" min-width="80">
          <template #default="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.avatar"
                :preview-src-list="[scope.row.avatar]" preview-teleported
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="用户名称" prop="nickname" min-width="100" />
        <el-table-column label="砍价金额" prop="price" min-width="100" />
        <el-table-column label="砍价时间" prop="addTime" min-width="180" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { bargainListListApi, bargainOrderPinkApi } from '@/api/marketing';

defineOptions({ name: 'index' });

const { proxy } = getCurrentInstance();

const listLoadingPink = ref(false);
const dialogVisible = ref(false);
const tableDataPink = reactive({
  data: [],
});
const tableData = reactive({
  data: [],
  total: 0,
});
const listLoading = ref(false);
const tableFrom = reactive({
  dateLimit: '',
  status: '',
  content: '',
  searchType: 'all',
  page: 1,
  limit: 20,
});
const fromList = proxy.$constants.fromList;
const timeVal = ref([]);
const multipleTableRef = ref(null);
const multipleTablePinkRef = ref(null);

const handleClose = () => {
  dialogVisible.value = false;
};
const handleLook = (id) => {
  dialogVisible.value = true;
  getPink(id);
};
const getPink = (id) => {
  listLoadingPink.value = true;
  bargainOrderPinkApi(id)
    .then((res) => {
      tableDataPink.data = res;
      listLoadingPink.value = false;
    })
    .catch(() => {
      listLoadingPink.value = false;
    });
};
const selectChange = (tab) => {
  tableFrom.dateLimit = tab;
  tableFrom.page = 1;
  timeVal.value = [];
  getList();
};
// 具体日期
const onchangeTime = (e) => {
  timeVal.value = e;
  tableFrom.dateLimit = e ? timeVal.value.join(',') : '';
  tableFrom.page = 1;
  getList();
};
// 列表
const getList = (num) => {
  listLoading.value = true;
  tableFrom.page = num ? num : tableFrom.page;
  bargainListListApi(tableFrom)
    .then((res) => {
      tableData.data = res.list;
      tableData.total = res.total;
      listLoading.value = false;
    })
    .catch(() => {
      listLoading.value = false;
    });
};
const pageChange = (page) => {
  tableFrom.page = page;
  getList();
};
const handleSizeChange = (val) => {
  tableFrom.limit = val;
  getList();
};
//重置
const handleReset = () => {
  tableFrom.dateLimit = '';
  tableFrom.content = '';
  tableFrom.searchType = 'all';
  timeVal.value = [];
  tableFrom.status = '';
  getList();
};
// 搜索
const seachList = () => {
  tableFrom.page = 1;
  getList();
};

onMounted(() => {
  getList();
});
</script>

<style scoped lang="scss">
.el-table__body {
  width: 100%;
  table-layout: fixed !important;
}
:deep(.el-dialog__body) {
  padding-bottom: 20px;
}
</style>
