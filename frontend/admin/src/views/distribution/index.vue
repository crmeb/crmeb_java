<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form label-width="75px">
          <el-form-item class="mr10" label="时间选择：">
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
          <el-form-item label="用户搜索：">
            <UserSearchInput ref="userSearchInputRef" v-model="tableFrom" />
          </el-form-item>
          <!-- <el-form-item label="关键词搜索：">
            <el-input
              v-model="tableFrom.keywords"
              placeholder="请输入姓名、电话、UID"
              class="selWidth"

              clearable
            >
            </el-input>
          </el-form-item> -->
          <div class="ml30">
            <el-button type="primary" @click="seachList">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </div>
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
        <el-table-column prop="uid" label="ID" width="60" />
        <el-table-column label="头像" min-width="80">
          <template #default="scope">
            <div class="demo-image__preview">
              <el-image :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]" preview-teleported />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="用户信息" min-width="130" />
        <el-table-column
          sortable
          prop="spreadCount"
          label="推广用户(一级)数量"
          :sort-method="
            (a, b) => {
              return a.spreadCount - b.spreadCount;
            }
          "
          min-width="160"
        />
        <el-table-column
          sortable
          label="推广订单数量"
          prop="spreadOrderNum"
          :sort-method="
            (a, b) => {
              return a.spreadOrderNum - b.spreadOrderNum;
            }
          "
          min-width="120"
        />
        <el-table-column
          sortable
          label="推广订单金额"
          min-width="120"
          :sort-method="
            (a, b) => {
              return a.spreadOrderTotalPrice - b.spreadOrderTotalPrice;
            }
          "
          prop="spreadOrderTotalPrice"
        />
        <el-table-column
          sortable
          label="佣金总金额"
          min-width="120"
          :sort-method="
            (a, b) => {
              return a.totalBrokeragePrice - b.totalBrokeragePrice;
            }
          "
          prop="totalBrokeragePrice"
        />
        <el-table-column
          sortable
          label="已提现金额"
          min-width="120"
          :sort-method="
            (a, b) => {
              return a.extractCountPrice - b.extractCountPrice;
            }
          "
          prop="extractCountPrice"
        />
        <el-table-column
          sortable
          label="已提现次数"
          min-width="120"
          :sort-method="
            (a, b) => {
              return a.extractCountNum - b.extractCountNum;
            }
          "
          prop="extractCountNum"
        />
        <el-table-column
          sortable
          label="未提现金额"
          min-width="120"
          :sort-method="
            (a, b) => {
              return a.brokeragePrice - b.brokeragePrice;
            }
          "
          prop="brokeragePrice"
        />
        <el-table-column
          sortable
          label="冻结中佣金"
          min-width="120"
          :sort-method="
            (a, b) => {
              return a.freezeBrokeragePrice - b.freezeBrokeragePrice;
            }
          "
          prop="freezeBrokeragePrice"
        />
        <el-table-column prop="promoterTime" label="成为推广员时间" min-width="150" />
        <el-table-column prop="spreadNickname" label="上级推广人" min-width="150" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <a @click="onSpread(scope.row.uid, 'man', '推广人')" v-hasPermi="['admin:retail:spread:list']">推广人</a>
            <el-divider direction="vertical"></el-divider>
            <el-dropdown>
              <span class="el-dropdown-link"> 更多<i class="el-icon-arrow-down el-icon--right" /> </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    @click="onSpreadOrder(scope.row.uid, 'order', '推广订单')"
                    v-if="checkPermi(['admin:retail:spread:order:list'])"
                    >推广订单</el-dropdown-item
                  >
                  <!--<el-dropdown-item @click="onSpreadType(scope.row.uid)">推广方式</el-dropdown-item>-->
                  <el-dropdown-item
                    @click="clearSpread(scope.row)"
                    v-if="scope.row.spreadNickname && scope.row.spreadNickname !== '无'"
                    v-hasPermi="['admin:retail:spread:clean']"
                    >清除上级推广人</el-dropdown-item
                  >
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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

    <!--推广人-->
    <el-dialog :title="titleName + '列表'" v-model="dialogVisible" width="900px" :before-close="handleClose">
      <div class="container">
        <el-form label-width="66px" class="dialog-search-form">
          <el-form-item v-if="onName !== 'man'" key="1" label="时间选择：" class="mr30">
            <el-date-picker
              class="selWidth"
              v-model="timeValSpread"
              value-format="YYYY-MM-DD"
              format="YYYY-MM-DD"

              type="daterange"
              placement="bottom-end"
              placeholder="自定义时间"
              style="width: 380px"
              @change="onchangeTimeSpread"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
            />
          </el-form-item>
          <el-form-item label="用户类型：" class="mr30">
            <el-select class="selWidth" @change="onChanges" v-model="spreadFrom.type" placeholder="请选择用户类型">
              <el-option label="全部" :value="0"></el-option>
              <el-option label="一级推广人" :value="1"></el-option>
              <el-option label="二级推广人" :value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="关键字：" class="mr30">
            <el-input
              v-model="spreadFrom.nickName"
              :placeholder="onName === 'order' ? '请输入订单号' : '请输入姓名、电话、UID'"
              class="selWidth"

              clearable
            >
            </el-input>
          </el-form-item>
          <el-form-item label-width="0" class="dialog-search-actions">
            <el-button type="primary" @click="onChanges">搜索</el-button>
            <el-button @click="handleResetDialog">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table
        v-if="onName === 'man'"
        key="men"
        v-loading="spreadLoading"
        :data="spreadData.data"
        style="width: 100%"

        class="table"
        highlight-current-row
      >
        <el-table-column prop="uid" label="ID" width="60" />
        <el-table-column label="头像" min-width="80">
          <template #default="scope">
            <div class="demo-image__preview">
              <el-image :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]" preview-teleported />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="用户信息" min-width="130" />
        <el-table-column prop="is_promoter" label="是否推广员" min-width="120">
          <template #default="scope">
            <span>{{ $filters.filterYesOrNo(scope.row.isPromoter) }}</span>
          </template>
        </el-table-column>
        <el-table-column sortable label="推广人数" min-width="120" prop="spreadCount" />
        <el-table-column sortable label="订单数" min-width="120" prop="payCount" />
      </el-table>
      <el-table
        v-if="onName === 'order'"
        key="order"
        v-loading="spreadLoading"
        :data="spreadData.data"
        style="width: 100%"

        class="table"
        highlight-current-row
      >
        <el-table-column prop="orderId" label="订单ID" min-width="120" />
        <el-table-column label="用户信息" min-width="150">
          <template #default="scope">
            <span>{{ scope.row.realName }}<el-divider direction="vertical"></el-divider>{{ scope.row.userPhone }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="时间" min-width="150" />
        <el-table-column sortable label="返佣金额" min-width="120" prop="price" />
      </el-table>
      <div class="block dialog-block">
        <el-pagination
          :page-sizes="[10, 20, 30, 40]"
          :page-size="spreadFrom.limit"
          :current-page="spreadFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="spreadData.total"
          @size-change="handleSizeChangeSpread"
          @current-change="pageChangeSpread"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import {
  promoterListApi,
  spreadStatisticsApi,
  spreadListApi,
  spreadOrderListApi,
  spreadClearApi,
} from '@/api/distribution';
import cardsData from '@/components/cards/index';
import { checkPermi } from '@/utils/permission'; // 权限判断函数

defineOptions({ name: 'AccountsUser' });

const { proxy } = getCurrentInstance();
const constants = proxy.$constants;

const userSearchInputRef = ref(null);
const cardLists = ref([]);
const timeVal = ref([]);
const tableData = reactive({
  data: [],
  total: 0,
});
const listLoading = ref(true);
const tableFrom = reactive({
  dateLimit: '',
  content: '',
  searchType: 'all',
  page: 1,
  limit: 20,
});
const fromList = constants.fromList;
const dialogVisible = ref(false);
const spreadData = reactive({
  data: [],
  total: 0,
});
const spreadFrom = reactive({
  page: 1,
  limit: 10,
  dateLimit: '',
  type: '0',
  nickName: '',
  uid: '',
});
const timeValSpread = ref([]);
const spreadLoading = ref(false);
const uid = ref('');
const onName = ref('');
const titleName = ref('');

function handleResetDialog() {
  spreadFrom.dateLimit = '';
  spreadFrom.type = 0;
  spreadFrom.nickName = '';
  timeValSpread.value = [];
  onName.value === 'man' ? getListSpread() : getSpreadOrderList();
}
//重置
function handleReset() {
  tableFrom.dateLimit = '';
  tableFrom.content = '';
  tableFrom.searchType = 'all';
  timeVal.value = [];
  getList();
}
function seachList() {
  tableFrom.page = 1;
  getList();
}
// 统计
// spreadStatistics() {
//   spreadStatisticsApi({ dateLimit: tableFrom.dateLimit, keywords: tableFrom.nickName}).then((res) => {
//     cardLists.value = [
//       { name: '分销人员人数', count: res.distributionNum },
//       { name: '发展会员人数', count: res.developNum },
//       { name: '推广订单总数', count: res.orderNum },
//       { name: '推广订单金额（元）', count: res.orderPriceCount },
//       { name: '提现次数', count: res.withdrawCount }
//     ]
//   })
// },
// 清除
function clearSpread(row) {
  proxy.$modalSure('解除【' + row.nickname + '】的上级推广人吗').then(() => {
    spreadClearApi(row.uid).then((res) => {
      ElMessage.success('清除成功');
      getList();
    });
  });
}
function onSpread(uidParam, n, p) {
  onName.value = n;
  titleName.value = p;
  uid.value = uidParam;
  dialogVisible.value = true;
  Object.assign(spreadFrom, {
    page: 1,
    limit: 10,
    dateLimit: '',
    type: 0,
    nickName: '',
    uid: uidParam,
  });
  getListSpread();
}
function handleClose() {
  dialogVisible.value = false;
}
// 选择时间
function selectChangeSpread(tab) {
  timeValSpread.value = [];
  spreadFrom.dateLimit = tab;
  spreadFrom.page = 1;
  onName.value === 'man' ? getListSpread() : getSpreadOrderList();
}
// 具体日期
function onchangeTimeSpread(e) {
  timeValSpread.value = e;
  spreadFrom.dateLimit = e ? timeValSpread.value.join(',') : '';
  spreadFrom.page = 1;
  onName.value === 'man' ? getListSpread() : getSpreadOrderList();
}
function onChanges() {
  spreadFrom.page = 1;
  onName.value === 'man' ? getListSpread() : getSpreadOrderList();
}
// 推广人列表
function getListSpread() {
  spreadLoading.value = true;
  spreadListApi({ page: spreadFrom.page, limit: spreadFrom.limit }, spreadFrom)
    .then((res) => {
      spreadData.data = res.list;
      spreadData.total = res.total;
      spreadLoading.value = false;
    })
    .catch(() => {
      spreadLoading.value = false;
    });
}
function pageChangeSpread(page) {
  spreadFrom.page = page;
  onName.value === 'man' ? getListSpread(uid.value) : getSpreadOrderList(uid.value);
}
function handleSizeChangeSpread(val) {
  spreadFrom.limit = val;
  onName.value === 'man' ? getListSpread(uid.value) : getSpreadOrderList(uid.value);
}
// 推广订单
function onSpreadOrder(uidParam, n, p) {
  uid.value = uidParam;
  onName.value = n;
  titleName.value = p;
  dialogVisible.value = true;
  Object.assign(spreadFrom, {
    page: 1,
    limit: 10,
    dateLimit: '',
    type: 0,
    nickName: '',
    uid: uidParam,
  });
  getSpreadOrderList();
}
function getSpreadOrderList() {
  spreadLoading.value = true;
  spreadOrderListApi({ page: spreadFrom.page, limit: spreadFrom.limit }, spreadFrom)
    .then((res) => {
      spreadData.data = res.list;
      spreadData.total = res.total;
      spreadLoading.value = false;
    })
    .catch(() => {
      spreadLoading.value = false;
    });
}
function selectChange(tab) {
  tableFrom.dateLimit = tab;
  tableFrom.page = 1;
  timeVal.value = [];
  // spreadStatistics()
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
function getList() {
  listLoading.value = true;
  promoterListApi(tableFrom)
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

onMounted(() => {
  // spreadStatistics()
  getList();
});
</script>

<style scoped>
.el-dropdown-link {
  cursor: pointer;
  color: #409eff;
  font-size: 12px;
}
.el-icon-arrow-down {
  font-size: 12px;
}
.el-form {
  display: flex;
  flex-wrap: wrap;
}
.dialog-search-form {
  align-items: flex-start;
}
.dialog-search-actions {
  margin-bottom: 30px;
}
.dialog-block {
  padding-bottom: 20px;
}
:deep(.el-form-item__content) {
  display: flex;
}
.date-picker {
  margin-left: 20px;
}
.el-dropdown-link {
  color: var(--prev-color-primary);
}
</style>
