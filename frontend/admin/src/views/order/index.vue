<template>
  <div class="divBox relative">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form inline label-width="75px">
          <el-form-item label="订单号码：">
            <el-input
              v-model="tableFrom.orderNo"
              @blur="seachList"
              @clear="seachList"
              placeholder="请输入订单号"
              class="selWidth"

              clearable
            >
            </el-input>
          </el-form-item>
          <el-form-item label="用户搜索：" label-for="nickname">
            <UserSearchInput v-model="tableFrom" @searchList="seachList" />
          </el-form-item>
          <el-form-item label="创建时间：">
            <optionDatePicker v-model="timeVal" @changeOptTime="onchangeTime"></optionDatePicker>
          </el-form-item>
          <el-form-item label="物流单号：">
            <el-input
              v-model="tableFrom.deliveryId"
              @blur="seachList"
              @clear="seachList"
              placeholder="请输入物流单号"
              class="selWidth"

              clearable
            >
            </el-input>
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
        <el-tabs
          v-model="tableFrom.status"
          @tab-change="seachList"
          v-if="checkPermi(['admin:order:status:num']) && isNumber(orderChartType.all)"
        >
          <el-tab-pane name="all" :label="`全部(${orderChartType.all ? orderChartType.all : 0})`" />
          <el-tab-pane name="unPaid" :label="`未支付(${orderChartType.unPaid ? orderChartType.unPaid : 0})`" />
          <el-tab-pane
            name="notShipped"
            :label="`未发货(${orderChartType.notShipped ? orderChartType.notShipped : 0})`"
          />
          <el-tab-pane name="spike" :label="`待收货(${orderChartType.spike ? orderChartType.spike : 0})`" />
          <el-tab-pane name="bargain" :label="`待评价(${orderChartType.bargain ? orderChartType.bargain : 0})`" />
          <el-tab-pane name="complete" :label="`交易完成(${orderChartType.complete ? orderChartType.complete : 0})`" />
          <el-tab-pane name="refunding" :label="`退款中(${orderChartType.refunding ? orderChartType.refunding : 0})`" />
          <el-tab-pane name="refunded" :label="`已退款(${orderChartType.refunded ? orderChartType.refunded : 0})`" />
          <el-tab-pane name="deleted" :label="`已删除(${orderChartType.deleted ? orderChartType.deleted : 0})`" />
        </el-tabs>
        <el-button @click="exports" v-hasPermi="['admin:export:excel:order']">导出</el-button>
      </div>
      </template>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"

        class="table"
        highlight-current-row
        :row-key="
          (row) => {
            return row.orderId;
          }
        "
      >
        <!-- @selection-change="handleSelectionChange" -->
        <!-- <el-table-column
          type="selection"
          :reserve-selection="true"
          width="55"
        /> -->
        <el-table-column label="订单号" min-width="210" v-if="checkedCities.includes('订单号')">
          <template #default="scope">
            <span style="display: block" v-text="scope.row.orderId" />
            <span v-show="scope.row.isDel" style="color: #ed4014; display: block">用户已删除</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderType" label="订单类型" min-width="110" v-if="checkedCities.includes('订单类型')" />
        <el-table-column prop="realName" label="收货人" min-width="100" v-if="checkedCities.includes('收货人')" />
        <el-table-column
          :show-overflow-tooltip="true"
          label="商品信息"
          min-width="400"
          v-if="checkedCities.includes('商品信息')"
        >
          <template #default="scope">
            <div v-if="scope.row.productList && scope.row.productList.length">
              <div
                v-for="(val, i) in scope.row.productList"
                :key="i"
                class="tabBox acea-row row-middle"
                style="flex-wrap: inherit"
              >
                <div class="demo-image__preview mr10">
                  <el-image :src="val.info.image" :preview-src-list="[val.info.image]" preview-teleported />
                </div>
                <div class="text_overflow">
                  <span class="tabBox_tit mr10"
                    >{{ val.info.productName + ' | ' }}{{ val.info.sku ? val.info.sku : '-' }}</span
                  >
                  <span class="tabBox_pice">{{
                    '￥' + val.info.price ? val.info.price + ' x ' + val.info.payNum : '-'
                  }}</span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="payPrice" label="实际支付" min-width="80" v-if="checkedCities.includes('实际支付')" />
        <el-table-column label="支付方式" min-width="80" v-if="checkedCities.includes('支付方式')">
          <template #default="scope">
            <span>{{ scope.row.payTypeStr }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" min-width="100" v-if="checkedCities.includes('订单状态')">
          <template #default="scope">
            <div class="order-status">
              <span :class="{ 'refund-status': isRefundOrder(scope.row) }">{{ getOrderStatusText(scope.row) }}</span>
              <el-popover v-if="isRefundOrder(scope.row)" trigger="hover" placement="left" :open-delay="500">
                <template #reference>
                  <el-icon class="refund-detail-icon" aria-label="退款详情"><InfoFilled /></el-icon>
                </template>
                <div class="pup_card flex-column">
                  <span>退款原因：{{ scope.row.refundReasonWap }}</span>
                  <span>备注说明：{{ scope.row.refundReasonWapExplain }}</span>
                  <span>退款时间：{{ scope.row.refundReasonTime }}</span>
                  <span class="acea-row">
                    退款凭证：
                    <template v-if="scope.row.refundReasonWapImg">
                      <div
                        v-for="(item, index) in scope.row.refundReasonWapImg.split(',')"
                        :key="index"
                        class="demo-image__preview"
                        style="width: 35px; height: auto; display: inline-block"
                      >
                        <el-image :src="item" :preview-src-list="[item]" preview-teleported />
                      </div>
                    </template>
                    <span v-else style="display: inline-block">无</span>
                  </span>
                </div>
              </el-popover>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="150" v-if="checkedCities.includes('创建时间')" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #header>
            <div class="col-setting-header">
              <span style="padding-right: 5px">操作</span>
              <el-popover
              placement="bottom-end"
              :width="200"
              trigger="click"
              v-model:visible="card_select_show"
              popper-class="order-col-setting-popover"
            >
              <template #reference>
                <i class="el-icon-setting" style="cursor: pointer"></i>
              </template>
              <div class="cell_ht">
                <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">全选</el-checkbox>
                <el-button link @click="checkSave()">保存</el-button>
              </div>
              <el-checkbox-group
                v-model="checkedCities"
                class="column-checkbox-group"
                @change="handleCheckedCitiesChange"
              >
                <el-checkbox v-for="item in columnData" :label="item" :value="item" :key="item" class="check_cell">{{ item }}</el-checkbox>
              </el-checkbox-group>
            </el-popover>
            </div>
          </template>
          <template #default="scope">
            <template v-if="checkPermi(['admin:order:info'])">
              <a @click="onOrderDetails(scope.row.orderId)">详情</a>
              <el-divider direction="vertical"></el-divider>
            </template>
            <template
              v-if="scope.row.paid === false && !scope.row.isAlterPrice && checkPermi(['admin:order:update:price'])"
            >
              <a @click="edit(scope.row)">编辑</a>
              <el-divider direction="vertical"></el-divider>
            </template>
            <template
              v-if="
                scope.row.statusStr.key === 'notShipped' &&
                scope.row.refundStatus === 0 &&
                checkPermi(['admin:order:send'])
              "
            >
              <a @click="sendOrder(scope.row)">发送货</a>
              <el-divider direction="vertical"></el-divider>
            </template>

            <template
              v-if="
                scope.row.statusStr.key === 'spike' &&
                scope.row.type === 0 &&
                ['express', 'send'].includes(scope.row.deliveryType) &&
                checkPermi(['admin:order:tracking:number:update']) &&
                !scope.row.shipmentTaskId
              "
            >
              <a @click="handleUpdateNumber(scope.row)">
                {{ scope.row.deliveryType === 'send' ? '修改送货员' : '修改快递单号' }}
              </a>
              <el-divider direction="vertical"></el-divider>
            </template>
            <el-dropdown trigger="click">
              <span class="el-dropdown-link"> 更多<i class="el-icon-arrow-down el-icon--right" /> </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    @click="onOrderLog(scope.row.orderId)"
                    v-if="checkPermi(['admin:order:status:list'])"
                    >订单记录</el-dropdown-item
                  >
                  <el-dropdown-item @click="onOrderMark(scope.row)" v-if="checkPermi(['admin:order:mark'])"
                    >订单备注</el-dropdown-item
                  >
                  <el-dropdown-item
                    v-if="scope.row.refundStatus === 1 && checkPermi(['admin:order:refund:refuse'])"
                    @click="onOrderRefuse(scope.row)"
                    >拒绝退款</el-dropdown-item
                  >
                  <!--v-show="((scope.row.statusStr.key !== 'refunded' && scope.row.statusStr.key !== 'unPaid') && (parseFloat(scope.row.payPrice) >= parseFloat(scope.row.refundPrice))) || (scope.row.payPrice == 0 && [0,1].indexOf(scope.row.refundStatus) !== -1)"-->
                  <el-dropdown-item
                    v-if="scope.row.refundStatus === 1 && checkPermi(['admin:order:refund'])"
                    @click="onOrderRefund(scope.row)"
                    >立即退款</el-dropdown-item
                  >
                  <el-dropdown-item
                    v-if="scope.row.statusStr.key === 'deleted' && checkPermi(['admin:order:delete'])"
                    @click="handleDelete(scope.row, scope.$index)"
                    >删除订单</el-dropdown-item
                  >
                  <el-dropdown-item v-if="scope.row.statusStr.key !== 'unPaid'" @click="onOrderPrint(scope.row)"
                    >打印小票</el-dropdown-item
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
    <!--编辑-->
    <el-dialog title="编辑订单" v-model="dialogVisible" width="500px" :before-close="handleClose">
      <zb-parser
        v-if="dialogVisible"
        :form-id="104"
        :is-create="isCreate"
        :edit-data="editData"
        @submit="handlerSubmit"
        @resetForm="resetForm"
        @closeDialog="dialogVisible = false"
      />
    </el-dialog>

    <!--记录-->
    <el-dialog title="操作记录" v-model="dialogVisibleJI" width="700px">
      <el-table v-loading="LogLoading" border :data="tableDataLog.data" style="width: 100%">
        <el-table-column prop="oid" label="ID" min-width="80" />
        <el-table-column prop="changeMessage" label="操作记录" min-width="280" />
        <el-table-column prop="createTime" label="操作时间" min-width="280" />
      </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[10, 20, 30, 40]"
          :page-size="tableFromLog.limit"
          :current-page="tableFromLog.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableDataLog.total"
          @size-change="handleSizeChangeLog"
          @current-change="pageChangeLog"
        />
      </div>
    </el-dialog>

    <!--详情-->
    <details-from ref="orderDetailRef" :orderId="orderId" />

    <!-- 发送货 -->
    <order-send
      ref="sendRef"
      :orderId="orderId"
      @submitFail="getList"
      :expressListNormal="expressListNormal"
      :expressListElec="expressListElec"
      :orderDetail="orderDetail"
    ></order-send>

    <!--拒绝退款-->
    <el-dialog
      title="拒绝退款原因"
      v-if="RefuseVisible"
      v-model="RefuseVisible"
      width="500px"
      :before-close="RefusehandleClose"
    >
      <zb-parser
        :form-id="106"
        :is-create="1"
        :edit-data="RefuseData"
        @submit="RefusehandlerSubmit"
        @resetForm="resetFormRefusehand"
        @closeDialog="RefuseVisible = false"
      />
    </el-dialog>

    <!--立即退款-->
    <el-dialog title="退款处理" v-model="refundVisible" width="500px" :before-close="refundhandleClose">
      <zb-parser
        :form-id="107"
        :is-create="1"
        :edit-data="refundData"
        @submit="refundhandlerSubmit"
        v-if="refundVisible"
        @resetForm="resetFormRefundhandler"
        @closeDialog="refundVisible = false"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="jsx">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage, ElMessageBox } from '@/utils/elementPlusFeedback';
import { InfoFilled } from '@element-plus/icons-vue';
import {
  orderListDataApi,
  orderStatusNumApi,
  orderListApi,
  updatePriceApi,
  orderLogApi,
  orderMarkApi,
  orderDeleteApi,
  orderRefuseApi,
  orderRefundApi,
  orderPrint,
  orderDetailApi,
} from '@/api/order';
import zbParser from '@/components/FormGenerator/components/parser/ZBParser';
import detailsFrom from './orderDetail';
import orderSend from './orderSend';
import Cookies from 'js-cookie';
import { orderExcelApi } from '@/api/store';
import { expressAllApi } from '@/api/sms';
import { checkPermi } from '@/utils/permission'; // 权限判断函数

defineOptions({ name: 'orderlistDetails' });

const { proxy } = getCurrentInstance();

const RefuseVisible = ref(false);
const RefuseData = ref({});
const orderId = ref('');
const refundVisible = ref(false);
const refundData = ref({});
const dialogVisibleJI = ref(false);
const tableDataLog = reactive({
  data: [],
  total: 0,
});
const tableFromLog = reactive({
  page: 1,
  limit: 10,
  orderNo: 0,
});
const LogLoading = ref(false);
const isCreate = ref(1);
const editData = ref(null);
const dialogVisible = ref(false);
const tableData = reactive({
  data: [],
  total: 0,
});
const listLoading = ref(true);
//订单状态（all 总数； 未支付 unPaid； 未发货 notShipped；待收货 spike；待评价 bargain；已完成 complete；
// 退款中:refunding；已退款:refunded；已删除:deleted
const tableFrom = reactive({
  status: 'all',
  dateLimit: '',
  orderNo: '',
  deliveryId: '', // 物流单号
  searchType: 'all',
  content: '',
  page: 1,
  limit: 20,
  type: 2,
});
const orderChartType = ref({});
const timeVal = ref([]);
const fromList = proxy.$constants.fromList;
const fromType = [
  { value: 'all', text: '全部' },
  { value: 'info', text: '普通' },
  { value: 'pintuan', text: '拼团' },
  { value: 'bragin', text: '砍价' },
  { value: 'miaosha', text: '秒杀' },
];
const selectionList = ref([]);
const ids = ref('');
const orderids = ref('');
const cardLists = ref([]);
const proType = ref(0);
const active = ref(false);
const card_select_show = ref(false);
const checkAll = ref(false);
const checkedCities = ref(['订单号', '订单类型', '收货人', '商品信息', '实际支付', '支付方式', '订单状态', '创建时间']);
const columnData = ['订单号', '订单类型', '收货人', '商品信息', '实际支付', '支付方式', '订单状态', '创建时间'];
const isIndeterminate = ref(true);
const expressListNormal = ref([]); //全部物流公司 normal
const expressListElec = ref([]); //全部物流公司 elec
const orderDetail = ref(null); //订单详情
const loading = ref(false);

const orderDetailRef = ref(null);
const sendRef = ref(null);

//重置
function handleReset() {
  tableFrom.type = 2;
  tableFrom.dateLimit = '';
  tableFrom.orderNo = '';
  tableFrom.deliveryId = '';
  tableFrom.content = '';
  tableFrom.searchType = 'all';
  timeVal.value = [];
  getList();
  getOrderStatusNum();
}
function isNumber(val) {
  return typeof val === 'number' && !Number.isNaN(val);
}
const refundStatusText = {
  1: '申请退款',
  2: '已退款',
  3: '退款中',
  applyRefund: '申请退款',
  refunding: '退款中',
  refunded: '已退款',
};
function isRefundOrder(row) {
  return Boolean(refundStatusText[Number(row.refundStatus)] || refundStatusText[row.statusStr?.key]);
}
function getOrderStatusText(row) {
  if (row.statusStr?.key === 'deleted') return row.statusStr.value;
  return refundStatusText[Number(row.refundStatus)] || refundStatusText[row.statusStr?.key] || row.statusStr?.value || '-';
}
// 物流公司列表
async function getExpress() {
  expressListNormal.value = await getExpressList('normal'); //全部物流公司
  expressListElec.value = await getExpressList('elec');
}
// 物流公司列表
async function getExpressList(expressType) {
  return new Promise((resolve, reject) => {
    expressAllApi({ type: expressType }).then((res) => {
      resolve(res);
    });
  });
}
function resetFormRefundhandler() {
  refundVisible.value = false;
}
function resetFormRefusehand() {
  RefuseVisible.value = false;
}
function resetForm(formValue) {
  dialogVisible.value = false;
}
function seachList() {
  tableFrom.page = 1;
  getList();
  getOrderStatusNum();
}
// 拒绝退款
function RefusehandleClose() {
  RefuseVisible.value = false;
}
function onOrderRefuse(row) {
  orderids.value = row.orderId;
  RefuseData.value = {
    orderId: row.orderId,
    reason: '',
  };
  RefuseVisible.value = true;
}
function RefusehandlerSubmit(formValue) {
  orderRefuseApi({ orderNo: orderids.value, reason: formValue.reason }).then((data) => {
    ElMessage.success('操作成功');
    RefuseVisible.value = false;
    getList();
  });
}
// 立即退款
function refundhandleClose() {
  refundVisible.value = false;
}
function onOrderRefund(row) {
  refundData.value = {
    orderId: row.orderId,
    amount: row.payPrice,
    type: '',
  };
  orderids.value = row.orderId;
  refundVisible.value = true;
}
function refundhandlerSubmit(formValue) {
  orderRefundApi({ amount: formValue.amount, orderNo: orderids.value }).then((data) => {
    ElMessage.success('操作成功');
    refundVisible.value = false;
    getList();
  });
}
// 详情接口
function getDetail(id) {
  orderDetailApi({ orderNo: id })
    .then((res) => {
      orderDetail.value = res;
      editData.value = {
        orderId: id,
        totalPrice: orderDetail.value.proTotalPrice,
        totalPostage: orderDetail.value.payPostage,
        payPrice: orderDetail.value.payPrice,
        payPostage: orderDetail.value.payPostage,
      };
      loading.value = true;
    })
    .catch(() => {
      orderDetail.value = null;
      loading.value = false;
    });
}
//修改快递单号
function handleUpdateNumber(row) {
  orderId.value = row.orderId;
  sendRef.value.modals = true;
  sendRef.value.loading = true;
  //默认加载Normal物流公司
  sendRef.value.express = expressListNormal.value;
  getDetail(row.orderId);
}
// 发送
function sendOrder(row) {
  orderDetail.value = null;
  sendRef.value.modals = true;
  //默认加载Normal物流公司
  sendRef.value.express = expressListNormal.value;
  sendRef.value.sheetInfo();
  orderId.value = row.orderId;
}
// 订单删除
function handleDelete(row, idx) {
  if (row.isDel) {
    proxy.$modalSure().then(() => {
      orderDeleteApi({ orderNo: row.orderId }).then(() => {
        ElMessage.success('删除成功');
        if (tableData.data.length === 1 && tableFrom.page > 1) tableFrom.page = tableFrom.page - 1;
        getList();
      });
    });
  } else {
    ElMessageBox.confirm('您选择的的订单存在用户未删除的订单，无法删除用户未删除的订单！', '提示', {
      confirmButtonText: '确定',
      type: 'error',
    });
  }
}
// 详情
function onOrderDetails(id) {
  orderId.value = id;
  orderDetailRef.value.getDetail(id);
  orderDetailRef.value.dialogVisible = true;
}
// 订单记录
function onOrderLog(id) {
  tableFromLog.limit = 10;
  orderId.value = id;
  dialogVisibleJI.value = true;
  getOrderStatusList(id);
}
//订单记录请求列表
function getOrderStatusList(id) {
  LogLoading.value = true;
  tableFromLog.orderNo = id;
  orderLogApi(tableFromLog)
    .then((res) => {
      tableDataLog.data = res.list;
      tableDataLog.total = res.total;
      LogLoading.value = false;
    })
    .catch(() => {
      LogLoading.value = false;
    });
}
function pageChangeLog(page) {
  tableFromLog.page = page;
  getOrderStatusList(orderId.value);
}
function handleSizeChangeLog(val) {
  tableFromLog.limit = val;
  getOrderStatusList(orderId.value);
}
function handleClose() {
  dialogVisible.value = false;
}
// 备注
function onOrderMark(row) {
  proxy.$modalPrompt('textarea', '订单备注', null, '订单备注').then((V) => {
    orderMarkApi({ mark: V, orderNo: row.orderId }).then(() => {
      ElMessage.success('操作成功');
      getList();
    });
  });
}
function handleSelectionChange(val) {
  selectionList.value = val;
  const data = [];
  selectionList.value.map((item) => {
    data.push(item.orderId);
  });
  ids.value = data.join(',');
}
// 选择时间
function selectChange(tab) {
  timeVal.value = [];
  tableFrom.page = 1;
  getList();
  getOrderStatusNum();
  // this.getOrderListData();
}
// 具体日期
function onchangeTime(e) {
  timeVal.value = e;
  tableFrom.dateLimit = e ? timeVal.value.join(',') : '';
  tableFrom.page = 1;
  getList();
  getOrderStatusNum();
  // this.getOrderListData();
}
// 编辑
function edit(row) {
  //this.getDetail(row.orderId);
  orderId.value = row.orderId;

  editData.value = {
    orderId: row.orderId,
    totalPrice: row.proTotalPrice,
    proTotalPrice: row.proTotalPrice,
    totalPostage: row.payPostage,
    payPostage: row.payPostage,
    beforePayPrice: row.beforePayPrice ? row.beforePayPrice : row.payPrice,
    payPrice: row.payPrice,
  };
  dialogVisible.value = true;
}
function handlerSubmit(formValue) {
  let data = {
    orderNo: formValue.orderId,
    payPrice: formValue.payPrice,
  };
  updatePriceApi(data).then((data) => {
    ElMessage.success('编辑成功');
    dialogVisible.value = false;
    getList();
  });
}
// 列表
function getList() {
  listLoading.value = true;
  orderListApi(tableFrom)
    .then((res) => {
      tableData.data = res.list || [];
      tableData.total = res.total;
      listLoading.value = false;
      checkedCities.value = proxy.$cache.local.has('order_stroge')
        ? proxy.$cache.local.getJSON('order_stroge')
        : checkedCities.value;
    })
    .catch(() => {
      listLoading.value = false;
    });
}
// 数据统计
function getOrderListData() {
  orderListDataApi({ dateLimit: tableFrom.dateLimit }).then((res) => {
    cardLists.value = [
      { name: '订单数量', count: res.count, color: '#1890FF', class: 'one', icon: 'icondingdan' },
      { name: '订单金额', count: res.amount, color: '#A277FF', class: 'two', icon: 'icondingdanjine' },
      {
        name: '微信支付金额',
        count: res.weChatAmount,
        color: '#EF9C20',
        class: 'three',
        icon: 'iconweixinzhifujine',
      },
      { name: '余额支付金额', count: res.yueAmount, color: '#1BBE6B', class: 'four', icon: 'iconyuezhifujine2' },
    ];
  });
}
// 获取各状态数量
function getOrderStatusNum() {
  orderStatusNumApi({
    dateLimit: tableFrom.dateLimit,
    type: tableFrom.type,
    orderNo: tableFrom.orderNo,
    deliveryId: tableFrom.deliveryId,
    searchType: tableFrom.searchType,
    content: tableFrom.content,
  }).then((res) => {
    orderChartType.value = res;
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
function exports() {
  let data = {
    dateLimit: tableFrom.dateLimit,
    orderNo: tableFrom.orderNo,
    deliveryId: tableFrom.deliveryId,
    status: tableFrom.status,
    type: tableFrom.type,
  };
  orderExcelApi(data).then((res) => {
    window.open(res.fileName);
  });
}
function handleCheckAllChange(val) {
  checkedCities.value = val ? columnData : [];
  isIndeterminate.value = false;
}
function handleCheckedCitiesChange(value) {
  let checkedCount = value.length;
  checkAll.value = checkedCount === columnData.length;
  isIndeterminate.value = checkedCount > 0 && checkedCount < columnData.length;
}
function checkSave() {
  card_select_show.value = false;
  proxy.$modal.loading('正在保存到本地，请稍候...');
  proxy.$cache.local.setJSON('order_stroge', checkedCities.value);
  setTimeout(proxy.$modal.closeLoading(), 1000);
}
//打印小票
function onOrderPrint(data) {
  orderPrint(data.orderId)
    .then(() => {
      proxy.$modal.msgSuccess('打印成功');
    })
    .catch(() => {});
}

onMounted(() => {
  getList();
  getOrderStatusNum();
  // getOrderListData();
  if (checkPermi(['admin:express:list'])) getExpress();
});

defineExpose({
  getList,
});
</script>
<style lang="scss" scoped>
.el-table__body {
  width: 100%;
  table-layout: fixed !important;
}

.demo-table-expand {
  :deep(.label ){
    width: 83px !important;
  }
}

.order-status {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.refund-status {
  color: #f124c7;
  font-weight: 700;
}

.refund-detail-icon {
  color: #909399;
  cursor: pointer;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409eff;
  font-size: 12px;
}

.el-icon-arrow-down {
  font-size: 12px;
}

.tabBox_tit {
  font-size: 12px !important;
  /*margin: 0 2px 0 10px;*/
  letter-spacing: 1px;
  /*padding: 5px 0;*/
  box-sizing: border-box;
}

.text_overflow {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 400px;
}

.pup_card {
  width: 200px;
  border-radius: 5px;
  padding: 5px;
  box-sizing: border-box;
  font-size: 12px;
  line-height: 16px;
}

.flex-column {
  display: flex;
  flex-direction: column;
}

.mt20 {
  margin-top: 20px;
}

.relative {
  position: relative;
}

/* 操作列表头：让“操作”文字与设置图标垂直居中对齐 */
.col-setting-header {
  display: inline-flex;
  align-items: center;
}

.cell_ht {
  height: 50px;
  padding: 15px 20px;
  box-sizing: border-box;
  border-bottom: 1px solid #eeeeee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.check_cell {
  width: 100%;
  padding: 15px 20px 0;
}

.column-checkbox-group {
  padding-bottom: 15px;
}

:deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #606266;
}
.block {
  margin-bottom: 20px;
}
</style>

<style lang="scss">
/* el-popover 渲染在 body 下，需用全局样式覆盖内边距 */
.order-col-setting-popover.el-popover.el-popper {
  padding: 0 !important;
}
</style>
