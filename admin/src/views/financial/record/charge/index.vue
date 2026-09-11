<template>
  <div class="divBox">
    <el-card class="box-card">
      <div class="clearfix">
        <div class="container">
          <el-form label-width="70px" inline>
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
            <el-form-item label="用户搜索：" label-for="nickname">
              <UserSearchInput v-model="tableFrom" />
            </el-form-item>
            <el-form-item label="订单号：">
              <el-input v-model="tableFrom.keywords" placeholder="订单号" class="selWidth" clearable>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="getList(1)">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>
    <div class="mt14">
      <!-- <cards-data :card-lists="cardLists" v-if="checkPermi(['admin:recharge:balance'])" /> -->
    </div>
    <el-card class="box-card">
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"

        class="table"
        highlight-current-row
      >
        <el-table-column prop="uid" label="UID" width="60" />
        <el-table-column label="头像" min-width="80">
          <template #default="scope">
            <div class="demo-image__preview">
              <el-image :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]" preview-teleported />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="用户昵称" min-width="130" />
        <el-table-column prop="orderId" label="订单号" min-width="180" />
        <el-table-column
          sortable
          label="支付金额"
          min-width="120"
          :sort-method="
            (a, b) => {
              return a.price - b.price;
            }
          "
          prop="price"
        />
        <el-table-column
          sortable
          label="赠送金额"
          min-width="120"
          prop="givePrice"
          :sort-method="
            (a, b) => {
              return a.givePrice - b.givePrice;
            }
          "
        />
        <el-table-column label="充值类型" min-width="80">
          <template #default="scope">
            <span>{{ $filters.rechargeTypeFilter(scope.row.rechargeType) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="支付时间" width="170">
          <template #default="scope">
            <span class="spBlock">{{ scope.row.payTime || '无' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="已退金额" min-width="120">
          <template #default="scope">
            <span>{{ scope.row.refundPrice || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column
          v-if="checkPermi(['admin:recharge:refund'])"
          label="操作"
          width="90"
          fixed="right"
          align="left"
          header-align="left"
          class-name="recharge-operation-column"
          label-class-name="recharge-operation-column"
        >
          <template #default="scope">
            <el-button
              type="primary"
              link
              :disabled="!isRechargeRefundable(scope.row)"
              @click="handleRefund(scope.row)"
            >
              退款
            </el-button>
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
    </el-card>
    <!--退款-->
    <el-dialog title="退款" v-model="dialogVisible" width="500px" :before-close="handleClose">
      <zb-parser
        v-if="dialogVisible"
        :form-id="130"
        :is-create="isCreate"
        :edit-data="editData"
        @submit="handlerSubmit"
        @resetForm="resetForm"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { topUpLogListApi, balanceApi, topUpLogDeleteApi, refundApi } from '@/api/financial';
import cardsData from '@/components/cards/index';
import zbParser from '@/components/FormGenerator/components/parser/ZBParser';
import { checkPermi } from '@/utils/permission'; // 权限判断函数

defineOptions({ name: 'AccountsBill' });

const { proxy } = getCurrentInstance();
const constants = proxy.$constants;

const editData = reactive({});
const isCreate = ref(1);
const cardLists = ref([]);
const timeVal = ref([]);
const tableData = reactive({
  data: [],
  total: 0,
});
const listLoading = ref(true);
const tableFrom = reactive({
  searchType: 'all',
  content: '',
  // paid: '',
  dateLimit: '',
  keywords: '',
  page: 1,
  limit: 20,
});
const fromList = constants.fromList;
const dialogVisible = ref(false);

//重置
function handleReset() {
  tableFrom.uid = '';
  tableFrom.dateLimit = '';
  tableFrom.keywords = '';
  tableFrom.searchType = 'all';
  tableFrom.content = '';
  timeVal.value = [];
  getList();
}
function resetForm(formValue) {
  handleClose();
}
function handlerSubmit(formValue) {
  refundApi(formValue).then((data) => {
    ElMessage.success('操作成功');
    dialogVisible.value = false;
    Object.keys(editData).forEach((k) => delete editData[k]);
    getList();
  });
}
function handleClose() {
  dialogVisible.value = false;
  Object.keys(editData).forEach((k) => delete editData[k]);
}
function isRechargeRefundable(row) {
  const price = Number(row.price || 0);
  const refundPrice = Number(row.refundPrice || 0);
  return row.rechargeType !== 'balance' && price > 0 && refundPrice < price;
}
function handleRefund(row) {
  if (Number(row.refundPrice || 0) >= Number(row.price || 0)) return ElMessage.warning('已退完支付金额，不能再退款');
  if (row.rechargeType === 'balance') return ElMessage.warning('佣金转入余额，不能退款');
  editData.orderId = row.orderId;
  editData.id = row.id;
  editData.type = 2;
  dialogVisible.value = true;
}
function handleDelete(row, idx) {
  proxy.$modalSure().then(() => {
    topUpLogDeleteApi({ id: row.id }).then(() => {
      ElMessage.success('删除成功');
      getList(tableFrom.page);
    });
  });
}
// 选择时间
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
  topUpLogListApi(tableFrom)
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
// 统计
function getStatistics() {
  balanceApi().then((res) => {
    const stat = res;
    cardLists.value = [
      { name: '充值总金额', count: stat.total, color: '#1890FF', class: 'one', icon: 'iconchongzhijine' },
      { name: '小程序充值金额', count: stat.routine, color: '#A277FF', class: 'two', icon: 'iconweixinzhifujine' },
      { name: '公众号充值金额', count: stat.weChat, color: '#EF9C20', class: 'three', icon: 'iconyuezhifujine1' },
    ];
  });
}

onMounted(() => {
  getList();
  // getStatistics();
});
</script>

<style scoped>
.selWidth {
  width: 300px;
}
:deep(.el-card__body) {
  padding: 20px 20px 0;
}
.block {
  padding-bottom: 20px;
}
:deep(.recharge-operation-column .cell) {
  text-align: left;
  padding-left: 0 !important;
  padding-right: 0 !important;
}
:deep(.recharge-operation-column .el-button) {
  margin-left: 0 !important;
  padding-left: 0 !important;
  padding-right: 0 !important;
}
</style>
