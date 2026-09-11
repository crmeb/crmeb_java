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
          <el-form-item label="商品搜索：">
            <el-input v-model="tableFrom.productName" placeholder="请输入商品名字" class="selWidth"></el-input>
          </el-form-item>
          <el-form-item label="团长搜索：">
            <UserSearchInput ref="userSearchInput" v-model="tableFrom" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getList(1)">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
          <!-- <el-form-item label="拼团状态：">
            <el-select
              v-model="tableFrom.status"
              placeholder="请选择"
              class="filter-item selWidth mr20"
              @change="getList(1)"
              clearable
            >
              <el-option label="进行中" :value="1" />
              <el-option label="已成功" :value="2" />
              <el-option label="未完成" :value="3" />
            </el-select>
          </el-form-item> -->
        </el-form>
      </div>
    </el-card>
    <div class="mt14">
      <!-- <cards-data :cardLists="cardLists" v-if="checkPermi(['admin:combination:statistics'])"></cards-data> -->
    </div>
    <el-card class="box-card">
      <el-tabs v-model="tableFrom.status" @tab-change="getList(1)">
        <el-tab-pane name="2">
          <template #label>已成功({{successNum}})</template>
        </el-tab-pane>
        <el-tab-pane name="1">
          <template #label>进行中({{ingNum}})</template>
        </el-tab-pane>
        <el-tab-pane name="3">
          <template #label>未完成({{failNum}})</template>
        </el-tab-pane>
      </el-tabs>
      <el-table
        class="table"
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"

        ref="multipleTableRef"
        highlight-current-row
      >
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
        <el-table-column label="开团团长" prop="nickname" min-width="100" />
        <el-table-column label="开团时间" prop="addTime" min-width="130" />
        <el-table-column label="拼团商品" prop="title" min-width="300" :show-overflow-tooltip="true"> </el-table-column>
        <el-table-column label="几人团" prop="people" min-width="100" />
        <el-table-column label="几人参加" prop="countPeople" min-width="100" />
        <el-table-column prop="stopTime" label="结束时间" min-width="130" />
        <el-table-column label="拼团状态" min-width="150">
          <template #default="scope">
            <!-- <el-tag :type="scope.row.status | groupColorFilter">{{ scope.row.status | groupStatusFilter }}</el-tag> -->
            <span
              class="tag-background tag-padding"
              :class="scope.row.status == 1 ? 'doingTag' : scope.row.status == 2 ? 'endTag' : 'notStartTag'"
              >{{ $filters.groupStatusFilter(scope.row.status) }}</span
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
        <el-table-column label="用户名称" prop="nickname" min-width="100" />
        <el-table-column label="订单编号" prop="orderId" min-width="180" />
        <el-table-column label="金额" prop="totalPrice" min-width="100" />
        <el-table-column label="订单状态" min-width="100">
          <template #default="scope">
            <span v-if="scope.row.refundStatus == 0">{{ $filters.orderStatusFilter(scope.row.orderStatus) }}</span>
            <span v-else>{{ $filters.refundStatusFilter(scope.row.refundStatus) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { combineListApi, combineListCountApi, combineOrderPinkApi } from '@/api/marketing';
import cardsData from '@/components/cards/index';
import { checkPermi } from '@/utils/permission'; // 权限判断函数

defineOptions({ name: 'groupList' });

const { proxy } = getCurrentInstance();

const listLoadingPink = ref(false);
const dialogVisible = ref(false);
const failNum = ref(0);
const ingNum = ref(0);
const successNum = ref(0);
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
  status: '2',
  content: '',
  searchType: 'all',
  productName: '',
  page: 1,
  limit: 20,
});
const fromList = proxy.$constants.fromList;
const timeVal = ref([]);
const cardLists = ref([]);
const multipleTableRef = ref(null);
const multipleTablePinkRef = ref(null);
const userSearchInput = ref(null);

const handleClose = () => {
  dialogVisible.value = false;
};
const handleLook = (id) => {
  dialogVisible.value = true;
  getPink(id);
};
const getPink = (id) => {
  listLoadingPink.value = true;
  combineOrderPinkApi(id)
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
  combineListApi(tableFrom)
    .then((res) => {
      getListCount();
      tableData.data = res.list;
      tableData.total = res.total;
      listLoading.value = false;
    })
    .catch(() => {
      listLoading.value = false;
    });
};
// 列表统计
const getListCount = () => {
  combineListCountApi(tableFrom).then((res) => {
    failNum.value = res.failNum;
    ingNum.value = res.ingNum;
    successNum.value = res.successNum;
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
// 统计
const getStatistics = () => {
  combineStatisticsApi()
    .then((res) => {
      cardLists.value = [
        {
          name: '参与人数(人)',
          count: res.countPeople,
          color: '#1890FF',
          class: 'one',
          icon: 'iconleijiyonghushu',
        },
        { name: '成团数量(个)', count: res.countTeam, color: '#A277FF', class: 'two', icon: 'iconxinzengyonghu' },
      ];
    })
    .catch(() => {
      listLoading.value = false;
    });
};
//重置
const handleReset = () => {
  timeVal.value = [];
  tableFrom.dateLimit = '';
  tableFrom.keywords = '';
  tableFrom.content = '';
  tableFrom.searchType = 'all';
  tableFrom.productName = '';
  getList();
};
// 搜索
const seachList = () => {
  tableFrom.page = 1;
  getList();
};

onMounted(() => {
  // this.getStatistics();
  getList();
});
</script>

<style scoped>
.el-table__body {
  width: 100%;
  table-layout: fixed !important;
}
.mt10 {
  margin-top: 10px;
}
:deep(.el-dialog__body) {
  padding-bottom: 20px;
}
.mt14 {
  margin-top: 14px;
}
.mb4 {
  margin-bottom: 4px;
}
</style>
