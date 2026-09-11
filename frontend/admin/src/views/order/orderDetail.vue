<template>
  <div>
    <el-drawer v-model="dialogVisible" :direction="direction" size="1000px" :before-close="handleClose">
      <div v-if="orderDatalist" v-loading="loading">
        <div class="detailHead">
          <div class="full">
            <div class="order_icon"><span class="iconfont icondingdanguanli"></span></div>
            <div class="text">
              <div class="title">{{ orderDatalist.orderTypeText || '-' }}</div>
              <div>
                <span class="mr20">订单号：{{ orderDatalist.orderId }}</span>
              </div>
            </div>
          </div>
          <ul class="list">
            <li class="item" v-if="orderDatalist.statusStr">
              <div class="title">订单状态</div>
              <div class="color-warning">
                <span>{{ orderDatalist.statusStr.value }}</span>
              </div>
            </li>
            <li class="item">
              <div class="title">实际支付</div>
              <div>¥ {{ orderDatalist.payPrice || '0.0' }}</div>
            </li>
            <li class="item">
              <div class="title">支付方式</div>
              <div>{{ $filters.payTypeFilter(orderDatalist.payType) }}</div>
            </li>
            <li class="item">
              <div class="title">创建时间</div>
              <div>{{ orderDatalist.createTime || '-' }}</div>
            </li>
          </ul>
        </div>
        <el-tabs type="border-card" v-model="activeName">
          <el-tab-pane label="订单信息" name="detail">
            <div class="detailSection" style="border: none">
              <div class="title">用户信息</div>
              <ul class="list">
                <li class="item">
                  <div class="lang">用户名称：</div>
                  <div class="value">{{ orderDatalist.nikeName }} | {{ orderDatalist.uid }}</div>
                </li>
                <li class="item">
                  <div class="lang">用户电话：</div>
                  <div class="value">{{ orderDatalist.phone }}</div>
                </li>
              </ul>
            </div>
            <div v-if="orderDatalist.shippingType < 2 && orderDatalist.secondType !== 2" class="detailSection">
              <div class="title">收货信息</div>
              <ul class="list">
                <li class="item">
                  <div class="lang">收货人：</div>
                  <div class="value">
                    {{ orderDatalist.realName }}
                  </div>
                </li>
                <li class="item">
                  <div class="lang">收货电话：</div>
                  <div class="value">
                    {{ orderDatalist.userPhone }}
                  </div>
                </li>
                <li class="item">
                  <div class="lang">收货地址：</div>
                  <div class="value">
                    {{ orderDatalist.userAddress }}
                  </div>
                </li>
              </ul>
            </div>
            <div class="detailSection">
              <div class="title">订单信息</div>
              <ul class="list">
                <li class="item">
                  <div class="lang">商品总价：</div>
                  <div class="value">{{ orderDatalist.proTotalPrice }}</div>
                </li>
                <li class="item">
                  <div class="lang">商品总数：</div>
                  <div class="value">{{ orderDatalist.totalNum }}</div>
                </li>
                <li class="item">
                  <div class="lang">优惠券：</div>
                  <div class="value">{{ orderDatalist.couponPrice }}</div>
                </li>
                <li class="item">
                  <div class="lang">实际支付：</div>
                  <div class="value">{{ orderDatalist.payPrice || '0.0' }}</div>
                </li>
                <li class="item">
                  <div class="lang">退款金额：</div>
                  <div class="value">{{ orderDatalist.refundPrice || '0.0' }}</div>
                </li>
                <li class="item">
                  <div class="lang">支付邮费：</div>
                  <div class="value">{{ orderDatalist.payPostage }}</div>
                </li>
                <li class="item">
                  <div class="lang">支付方式：</div>
                  <div class="value">{{ $filters.payTypeFilter(orderDatalist.payType) }}</div>
                </li>
                <li class="item">
                  <div class="lang">创建时间：</div>
                  <div class="value">{{ $filters.filterEmpty(orderDatalist.createTime) }}</div>
                </li>
                <li class="item">
                  <div class="lang">推广人：</div>
                  <div class="value">{{ $filters.filterEmpty(orderDatalist.spreadName) }}</div>
                </li>
              </ul>
            </div>
            <div class="detailSection">
              <div class="title">买家留言</div>
              <ul class="list">
                <li class="item">
                  <div>{{ $filters.filterEmpty(orderDatalist.mark) }}</div>
                </li>
              </ul>
            </div>
            <div class="detailSection">
              <div class="title">商家备注</div>
              <ul class="list">
                <li class="item">
                  <div>{{ $filters.filterEmpty(orderDatalist.remark) }}</div>
                </li>
              </ul>
            </div>
            <div v-if="orderExtend.length" class="detailSection">
              <div class="title">自定义留言</div>
              <ul class="list">
                <li class="item" v-for="(item, index) in orderExtend" :key="index">
                  <div class="lang" :title="item.title">{{ item.title }}</div>
                  <div>{{ item.title.includes(':') ? '' : '：' }}</div>
                  <div v-if="!Array.isArray(item.value)" class="value">{{ $filters.filterEmpty(item.value) }}</div>
                  <div v-else class="flex conter">
                    <template v-if="item.value">
                      <div v-for="(pic, idx) in item.value" :key="idx">
                        <el-image v-if="pic.includes('http')" class="pictrue" :src="pic" :preview-src-list="[pic]" preview-teleported />
                        <div v-else class="text-14px fontColor333 ml-5px acea-row row-middle mr5">
                          {{ pic }}
                          <div style="margin-left: 6px" v-show="idx < item.value.length - 1">-</div>
                        </div>
                      </div>
                    </template>
                    <template v-else> - </template>
                  </div>
                </li>
              </ul>
            </div>
          </el-tab-pane>
          <el-tab-pane label="商品信息" name="goods" class="tabBox">
            <el-table class="mt20 orderDetailList" :data="orderDatalist.orderInfo">
              <el-table-column label="商品信息" min-width="400" :show-overflow-tooltip="true">
                <template #default="scope">
                  <div class="acea-row row-middle">
                    <div class="demo-image__preview mr15">
                      <el-image :src="scope.row.info.image" :preview-src-list="[scope.row.info.image]" preview-teleported />
                    </div>
                    <div style="width: 408px">
                      <div class="line1 mb10">{{ scope.row.info.productName }}</div>
                      <div class="line1 color-909399 line-heightOne">规格：{{ scope.row.info.sku }}</div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="商品售价" min-width="90">
                <template #default="scope">
                  <div class="acea-row row-middle">
                    <div class="line1">
                      {{ scope.row.info.price }}
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="购买数量" min-width="90">
                <template #default="scope">
                  <div class="acea-row row-middle">
                    <div class="line1">
                      {{ scope.row.info.payNum }}
                    </div>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane v-if="checkPermi(['admin:order:status:list'])" label="订单记录" name="orderLog" class="tabBox">
            <el-table v-loading="orderLogLoading" class="mt20" border :data="orderLogData.data" style="width: 100%">
              <el-table-column prop="oid" label="ID" min-width="90" />
              <el-table-column prop="changeMessage" label="订单记录" min-width="360" />
              <el-table-column prop="createTime" label="记录时间" min-width="180" />
            </el-table>
            <div class="block">
              <el-pagination
                :page-sizes="[10, 20, 30, 40]"
                :page-size="orderLogParams.limit"
                :current-page="orderLogParams.page"
                layout="total, sizes, prev, pager, next, jumper"
                :total="orderLogData.total"
                @size-change="handleOrderLogSizeChange"
                @current-change="handleOrderLogPageChange"
              />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { ref, reactive } from 'vue';
import { orderDetailApi, orderLogApi } from '@/api/order';
import { checkPermi } from '@/utils/permission';

defineOptions({ name: 'OrderDetail' });

defineProps({
  orderId: {
    default: 0,
  },
});

const activeName = ref('detail');
const direction = ref('rtl');
const dialogVisible = ref(false);
const orderDatalist = ref({});
const loading = ref(false);
const orderExtend = ref([]); //系统表单数据
const orderLogLoading = ref(false);
const orderLogData = reactive({
  data: [],
  total: 0,
});
const orderLogParams = reactive({
  page: 1,
  limit: 10,
  orderNo: 0,
});

function handleClose() {
  dialogVisible.value = false;
}
function getDetail(id) {
  loading.value = true;
  resetOrderLog(id);
  orderDetailApi({ orderNo: id })
    .then((res) => {
      orderDatalist.value = res;
      orderExtend.value = res.orderExtend ? JSON.parse(res.orderExtend) : [];
      activeName.value = 'detail';
      loading.value = false;
      if (checkPermi(['admin:order:status:list'])) getOrderLogList(id);
    })
    .catch(() => {
      orderDatalist.value = null;
      loading.value = false;
    });
}
function resetOrderLog(id) {
  orderLogParams.page = 1;
  orderLogParams.limit = 10;
  orderLogParams.orderNo = id;
  orderLogData.data = [];
  orderLogData.total = 0;
}
function getOrderLogList(id) {
  orderLogLoading.value = true;
  orderLogParams.orderNo = id;
  orderLogApi(orderLogParams)
    .then((res) => {
      orderLogData.data = res.list;
      orderLogData.total = res.total;
      orderLogLoading.value = false;
    })
    .catch(() => {
      orderLogLoading.value = false;
    });
}
function handleOrderLogPageChange(page) {
  orderLogParams.page = page;
  getOrderLogList(orderLogParams.orderNo);
}
function handleOrderLogSizeChange(val) {
  orderLogParams.limit = val;
  getOrderLogList(orderLogParams.orderNo);
}

defineExpose({ getDetail, dialogVisible });
</script>

<style scoped lang="scss">
:deep(.el-tabs__content) {
  padding: 0 20px !important;
}
.detailSection {
  padding: 25px 15px !important;
}
:deep(.el-table th.el-table__cell > .cell),
:deep(.el-table .cell),
.el-table--border .el-table__cell:first-child .cell {
  padding-left: 15px;
}
.wrapper {
  background-color: #fff;
  margin-top: 7px;
  padding: 10px 12px;
  &-num {
    font-size: 10px;
    color: #999999;
  }

  &-title {
    color: #666666;
    font-size: 12px;
  }

  &-img {
    width: 60px;
    height: 60px;
    margin-right: 10px;
    border-radius: 7px;
    overflow: hidden;
    margin-bottom: 10px;

    image {
      width: 100%;
      height: 100%;
    }

    &:nth-child(5n) {
      margin-right: 0;
    }
  }
}

.title {
  font-size: 36px;
}

.demo-drawer__content {
  padding: 0 30px;
}

.demo-image__preview {
  display: inline-block;
  .el-image {
    width: 50px;
    height: 50px;
  }
}

.title {
  margin-bottom: 14px;
  color: #303133;
  font-weight: 500;
  font-size: 14px;
}

.description {
  &-term {
    display: table-cell;
    padding-bottom: 5px;
    line-height: 20px;
    width: 50%;
    font-size: 12px;
    color: #606266;
  }
  :deep(.el-divider--horizontal) {
    margin: 12px 0 !important;
  }
}
:deep(.el-tabs__item:focus.is-active.is-focus:not(:active)) {
  -webkit-box-shadow: none;
  box-shadow: none;
}
:deep(.el-drawer__header) {
  display: block !important;
  margin-bottom: 0 !important;
  padding: 0 !important;
}
:deep(.el-drawer__close-btn) {
  position: absolute;
  right: 20px;
  top: 30px;
}
:deep(.el-tabs__nav .el-tabs__item:nth-of-type(1)) {
  padding-left: 20px !important;
}
</style>
