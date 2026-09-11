<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form inline label-width="70px">
          <el-form-item label="时间选择：">
            <optionDatePicker v-model="timeVal" @changeOptTime="onchangeTime"></optionDatePicker>
            <!-- <el-date-picker
              v-model="timeVal"
              value-format="YYYY-MM-DD"
              format="YYYY-MM-DD"

              type="daterange"
              placement="bottom-end"
              placeholder="自定义时间"
              style="width: 250px"
              @change="onchangeTime"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
            /> -->
          </el-form-item>
          <el-form-item label="用户搜索：">
            <UserSearchInput ref="userSearchInputRef" v-model="tableFrom" />
          </el-form-item>
          <!-- <el-form-item label="关键字：">
            <el-input v-model="tableFrom.keywords" placeholder="微信昵称/ID" class="selWidth" clearable>
            </el-input>
          </el-form-item> -->
          <el-form-item label="明细类型：">
            <el-select
              class="selWidth"
              v-model="tableFrom.title"

              clearable
              placeholder="请选择"
              @change="selectType"
            >
              <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"> </el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getList(1)">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"

        class="table"
        highlight-current-row
      >
        <el-table-column prop="uid" label="会员ID" width="80" />
        <el-table-column prop="nickName" label="昵称" min-width="130" />
        <el-table-column label="金额" min-width="120">
          <template #default="scope">
            <div>
              <span :class="scope.row.pm == 1 ? 'color_red' : 'color_green'"
                >{{ scope.row.pm == 1 ? '+' : '-' }}{{ scope.row.number }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="明细类型" min-width="100" prop="title" />
        <el-table-column prop="mark" label="备注" min-width="200" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { monitorListApi, monitorListOptionApi } from '@/api/financial';

defineOptions({ name: 'AccountsCapital' });

const { proxy } = getCurrentInstance();
const constants = proxy.$constants;

const userSearchInputRef = ref(null);
const timeVal = ref([]);
const tableData = reactive({
  data: [],
  total: 0,
});
const listLoading = ref(true);
const tableFrom = reactive({
  title: '',
  dateLimit: '',
  content: '',
  searchType: 'all',
  page: 1,
  limit: 20,
});
const categoryId = ref('');
const fromList = constants.fromList;
const options = [
  { value: 'recharge', label: '充值支付' },
  { value: 'admin', label: '后台操作' },
  { value: 'productRefund', label: '商品退款' },
  { value: 'payProduct', label: '购买商品' },
  { value: 'transferIn', label: '佣金转入' },
];

//重置
function handleReset() {
  tableFrom.title = '';
  tableFrom.dateLimit = '';
  tableFrom.content = '';
  tableFrom.searchType = 'all';
  timeVal.value = [];
  getList();
}
function selectChange(tab) {
  tableFrom.dateLimit = tab;
  timeVal.value = [];
  tableFrom.page = 1;
  getList();
}
// 具体日期
function onchangeTime(e) {
  timeVal.value = e;
  tableFrom.dateLimit = e ? timeVal.value.join(',') : '';
  tableFrom.page = 1;
  getList();
}
// 列表
function getList(num) {
  listLoading.value = true;
  tableFrom.page = num ? num : tableFrom.page;
  monitorListApi(tableFrom)
    .then((res) => {
      tableData.data = res.list;
      tableData.total = res.total;
      listLoading.value = false;
    })
    .catch((res) => {
      ElMessage.error(res.message);
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
function selectType(e) {
  getList();
}

onMounted(() => {
  // getTypes()
  getList();
});
</script>

<style scoped>
.selWidth {
  width: 300px;
}
.color_red {
  color: #f5222d;
}
.color_green {
  color: #7abe5c;
}
</style>
