<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <!--<el-tabs v-model="tableFrom.type" @tab-click="getList">-->
          <!--<el-tab-pane label="全部订单" name=""/>-->
          <!--<el-tab-pane label="普通订单" name="1"/>-->
          <!--<el-tab-pane label="拼团订单" name="2"/>-->
          <!--<el-tab-pane label="秒杀订单" name="3"/>-->
          <!--<el-tab-pane label="砍价订单" name="4"/>-->
        <!--</el-tabs>-->
        <div class="container">
          <el-form size="small" label-width="100px">
            <el-form-item label="订单状态：">
              <el-radio-group v-model="tableFrom.status" type="button" @change="seachList">
                <el-radio-button label="all">全部 {{ '(' +orderChartType.all?orderChartType.all:0 + ')' }}</el-radio-button>
                <el-radio-button label="unPaid">未支付 {{ '(' +orderChartType.unPaid?orderChartType.unPaid:0+ ')' }}</el-radio-button>
                <el-radio-button label="notShipped">未发货 {{ '(' +orderChartType.notShipped?orderChartType.notShipped:0+ ')' }}</el-radio-button>
                <el-radio-button label="spike">待收货 {{ '(' +orderChartType.spike?orderChartType.spike:0+ ')' }}</el-radio-button>
                <el-radio-button label="bargain">待评价 {{ '(' +orderChartType.bargain?orderChartType.bargain:0+ ')' }}</el-radio-button>
                <el-radio-button label="complete">交易完成 {{ '(' +orderChartType.complete?orderChartType.complete:0+ ')' }}</el-radio-button>
                <el-radio-button label="toBeWrittenOff">待核销 {{ '(' +orderChartType.toBeWrittenOff?orderChartType.toBeWrittenOff:0+ ')' }}</el-radio-button>
                <el-radio-button label="refunding">退款中 {{ '(' +orderChartType.refunding?orderChartType.refunding:0+ ')' }}</el-radio-button>
                <el-radio-button label="refunded">已退款 {{ '(' +orderChartType.refunded?orderChartType.refunded:0+ ')' }}</el-radio-button>
                <el-radio-button label="deleted">已删除 {{ '(' +orderChartType.deleted?orderChartType.deleted:0+ ')' }}</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="时间选择：" class="width100">
              <el-radio-group v-model="tableFrom.dateLimit" type="button" class="mr20" size="small" @change="selectChange(tableFrom.dateLimit)">
                <el-radio-button v-for="(item,i) in fromList.fromTxt" :key="i" :label="item.val">{{ item.text }}</el-radio-button>
              </el-radio-group>
              <el-date-picker v-model="timeVal" value-format="yyyy-MM-dd" format="yyyy-MM-dd" size="small" type="daterange" placement="bottom-end" placeholder="自定义时间" style="width: 220px;" @change="onchangeTime" />
            </el-form-item>
            <el-form-item label="订单类型：" class="width100">
              <el-select v-model="tableFrom.type" placeholder="请选择" class="selWidth" @change="seachList">
                <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="订单号：" class="width100">
              <el-input v-model="tableFrom.orderNo" placeholder="请输入订单号" class="selWidth" size="small" clearable>
                <el-button slot="append" icon="el-icon-search" size="small" @click="seachList" />
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <cards-data :cardLists="cardLists"></cards-data>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        class="table"
        highlight-current-row
        @selection-change="handleSelectionChange"
      >
        <!--<el-table-column type="expand">-->
          <!--<template slot-scope="props">-->
            <!--<el-form label-position="left" inline class="demo-table-expand">-->
              <!--<el-form-item label="商品总价：">-->
                <!--<span>{{ props.row.totalPrice | filterEmpty }}</span>-->
              <!--</el-form-item>-->
              <!--<el-form-item label="推广人：">-->
                <!--<span>{{ props.row.spreadInfo.id + ' / ' + props.row.spreadInfo.name }}</span>-->
              <!--</el-form-item>-->
              <!--<el-form-item label="用户备注：">-->
                <!--<span>{{ props.row.mark | filterEmpty }}</span>-->
              <!--</el-form-item>-->
              <!--<el-form-item label="商家备注：">-->
                <!--<span>{{ props.row.remark | filterEmpty }}</span>-->
              <!--</el-form-item>-->
              <!--<el-form-item label="核销码：" v-if="props.row.verifyCode">-->
                <!--<span>{{ props.row.verifyCode }}</span>-->
              <!--</el-form-item>-->
            <!--</el-form>-->
          <!--</template>-->
        <!--</el-table-column>-->
        <el-table-column
          type="selection"
          width="55"
        />
        <el-table-column
          label="订单号"
          min-width="170"
        >
          <template slot-scope="scope">
            <span style="display: block;" v-text="scope.row.orderId" />
            <span v-show="scope.row.isDel" style="color: #ED4014;display: block;">用户已删除</span>
          </template>
        </el-table-column>
        <el-table-column
        prop="orderType"
        label="订单类型"
        min-width="130"
        />
        <el-table-column
          prop="realName"
          label="用户信息"
          min-width="130"
        />
        <el-table-column
          label="商品信息"
          min-width="400"
        >
          <template slot-scope="scope">
            <div v-if=" scope.row.productList && scope.row.productList.length">
              <div v-for="(val, i ) in scope.row.productList" :key="i" class="tabBox acea-row row-middle" style="flex-wrap: inherit;">
                <div class="demo-image__preview mr10">
                  <el-image
                    :src="val.info.image"
                    :preview-src-list="[val.info.image]"
                  />
                </div>
                <div>
                  <span class="tabBox_tit mr10">{{ val.info.productName + ' | ' }}{{ val.info.sku ? val.info.sku:'-' }}</span>
                  <span class="tabBox_pice">{{ '￥'+ val.info.price ? val.info.price + ' x '+ val.info.payNum : '-' }}</span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="payPrice"
          label="实际支付"
          min-width="80"
        />
        <el-table-column
          label="支付方式"
          min-width="80"
        >
          <template slot-scope="scope">
            <span>{{ scope.row.payTypeStr }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="订单状态"
          min-width="180"
        >
          <template slot-scope="scope">
            <div v-if="scope.row.refundStatus === 1 || scope.row.refundStatus === 2" class="refunding">
              <b style="color: #f124c7;">{{scope.row.statusStr.value}}</b>
              <span>退款原因：{{scope.row.refundReasonWap}}</span>
              <span>备注说明：{{scope.row.refundReasonWapExplain}}</span>
              <span>退款时间：{{scope.row.refundReasonTime}}</span>
              <span class="acea-row">
                退款凭证：
                <template v-if="scope.row.refundReasonWapImg">
                  <div v-for="(item, index) in scope.row.refundReasonWapImg.split(',')" :key="index" class="demo-image__preview" style="width: 35px;height: auto;display: inline-block;">
                  <el-image
                    :src="item"
                    :preview-src-list="[item]"
                  />
                </div>
                </template>
                <!--<img :src="scope.row.refundReasonWapImg" v-if="scope.row.refundReasonWapImg" >-->
                <span v-else style="display: inline-block">无</span>
              </span>
            </div>
            <span v-else>{{ scope.row.statusStr.value }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="下单时间"
          min-width="150"
        />
        <el-table-column label="操作" min-width="150" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button v-if="scope.row.paid === false" type="text" size="small" @click="edit(scope.row)" class="mr10">编辑</el-button>
            <el-button v-if="scope.row.statusStr.key === 'notShipped' && scope.row.refundStatus ===0" type="text" size="small" class="mr10" @click="sendOrder(scope.row)">发送货</el-button>
            <el-button v-if=" scope.row.statusStr.key === 'toBeWrittenOff'  && scope.row.paid == true && scope.row.refundStatus === 0 " type="text" size="small" class="mr10" @click="onWriteOff(scope.row)">立即核销</el-button>
            <el-dropdown trigger="click">
              <span class="el-dropdown-link">
                更多<i class="el-icon-arrow-down el-icon--right" />
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="onOrderDetails(scope.row.orderId)">订单详情</el-dropdown-item>
                <el-dropdown-item @click.native="onOrderLog(scope.row.orderId)">订单记录</el-dropdown-item>
                <el-dropdown-item @click.native="onOrderMark(scope.row)">订单备注</el-dropdown-item>
                <el-dropdown-item v-show="scope.row.refundStatus === 1" @click.native="onOrderRefuse(scope.row)">拒绝退款</el-dropdown-item>
                <!--v-show="((scope.row.statusStr.key !== 'refunded' && scope.row.statusStr.key !== 'unPaid') && (parseFloat(scope.row.payPrice) >= parseFloat(scope.row.refundPrice))) || (scope.row.payPrice == 0 && [0,1].indexOf(scope.row.refundStatus) !== -1)"-->
                <el-dropdown-item v-show="scope.row.refundStatus === 1" @click.native="onOrderRefund(scope.row)">立即退款</el-dropdown-item>
                <el-dropdown-item v-show="scope.row.statusStr.key === 'deleted'" @click.native="handleDelete(scope.row, scope.$index)">删除订单</el-dropdown-item>
              </el-dropdown-menu>
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
        />
      </div>
    </el-card>

    <!--编辑-->
    <el-dialog
      title="编辑订单"
      :visible.sync="dialogVisible"
      width="500px"
      :before-close="handleClose">
        <zb-parser
        :form-id="104"
        :is-create="isCreate"
        :edit-data="editData"
        @submit="handlerSubmit"
        @resetForm="resetForm"
      />
    </el-dialog>

    <!--记录-->
    <el-dialog
      title="操作记录"
      :visible.sync="dialogVisibleJI"
      width="700px"
    >
      <el-table
        v-loading="LogLoading"
        border
        :data="tableDataLog.data"
        style="width: 100%"
      >
        <el-table-column
          prop="oid"
          align="center"
          label="ID"
          min-width="80"
        />
        <el-table-column
          prop="changeMessage"
          label="操作记录"
          align="center"
          min-width="280"
        />
        <el-table-column
          prop="createTime"
          label="操作时间"
          align="center"
          min-width="280"
        />
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
    <details-from ref="orderDetail" :orderId="orderId"/>

    <!-- 发送货 -->
    <order-send ref="send" :orderId="orderId" @submitFail="getList"></order-send>

    <!-- 发送货视频号商品 -->
    <order-video-send ref="videoSend" :orderId="orderId" @submitFail="getList"></order-video-send>

    <!--拒绝退款-->
    <el-dialog
      title="拒绝退款原因"
      :visible.sync="RefuseVisible"
      width="500px"
      :before-close="RefusehandleClose">
      <zb-parser
        :form-id="106"
        :is-create="1"
        :edit-data="RefuseData"
        @submit="RefusehandlerSubmit"
        @resetForm="resetFormRefusehand"
      />
    </el-dialog>

    <!--立即退款-->
    <el-dialog
      title="退款处理"
      :visible.sync="refundVisible"
      width="500px"
      :before-close="refundhandleClose">
      <zb-parser
        :form-id="107"
        :is-create="1"
        :edit-data="refundData"
        @submit="refundhandlerSubmit"
        v-if="refundVisible"
        @resetForm="resetFormRefundhandler"
      />
    </el-dialog>
  </div>
</template>

<script>
  import { orderListDataApi, orderStatusNumApi, writeUpdateApi, orderListApi, orderUpdateApi, orderLogApi, orderMarkApi, orderDeleteApi, orderRefuseApi, orderRefundApi } from '@/api/order'
  import cardsData from '@/components/cards/index'
  import zbParser from '@/components/FormGenerator/components/parser/ZBParser'
  import detailsFrom from './orderDetail'
  import orderSend from './orderSend'
  import orderVideoSend from './orderVideoSend'
  import { storeStaffListApi } from '@/api/storePoint'
  import Cookies from 'js-cookie'
  import { isWriteOff } from "@/utils";
  export default {
    name: 'orderlistDetails',
    components: {
      cardsData,
      zbParser,
      detailsFrom,
      orderSend,
      orderVideoSend
    },
    data() {
        return {
          options: [{
            value: 0,
            label: '商城订单'
          }, {
            value: 1,
            label: '视频号订单'
          }],
          RefuseVisible: false,
          RefuseData:{},
          orderId: '',
          refundVisible: false,
          refundData: {},
          dialogVisibleJI: false,
          tableDataLog: {
            data: [],
            total: 0
          },
          tableFromLog: {
            page: 1,
            limit: 10,
            orderNo: 0
          },
          LogLoading: false,
          isCreate: 1,
          editData: null,
          dialogVisible: false,
          tableData: {
            data: [],
            total: 0
          },
          listLoading: true,
          tableFrom: {
            status: 'all',
            dateLimit: '',
            orderNo: '',
            page: 1,
            limit: 20,
            type: ''
          },
          orderChartType: {},
          timeVal: [],
          fromList: this.$constants.fromList,
          selectionList: [],
          ids: '',
          orderids: '',
          cardLists: [],
          isWriteOff: isWriteOff(),
          proType: 0
        }
      },
    mounted() {
      this.getList();
      this.getOrderStatusNum();
      this.getOrderListData();
    },
    methods: {
      resetFormRefundhandler(){
        this.refundVisible = false
      },
      resetFormRefusehand(){
        this.RefuseVisible = false
      },
      resetForm(formValue) {
        this.dialogVisible = false
      },
      // 核销订单
      onWriteOff(row) {
        this.$modalSure('核销订单吗').then(() => {
          writeUpdateApi(row.verifyCode).then(() => {
            this.$message.success('核销成功')
            this.tableFrom.page = 1
            this.getList()
          })
        })
      },
      seachList() {
        this.tableFrom.page = 1
        this.getList()
      },
      // 拒绝退款
      RefusehandleClose() {
        this.RefuseVisible = false
      },
      onOrderRefuse(row) {
        this.orderids = row.orderId
        this.RefuseData = {
          orderId: row.orderId,
          reason: ''
        }
        this.RefuseVisible = true
      },
      RefusehandlerSubmit(formValue) {
        orderRefuseApi({ orderNo: this.orderids, reason: formValue.reason}).then(data => {
          this.$message.success('操作成功')
          this.RefuseVisible = false
          this.getList()
        })
      },
      // 立即退款
      refundhandleClose() {
        this.refundVisible = false
      },
      onOrderRefund(row) {
        this.refundData = {
          orderId: row.orderId,
          amount: row.payPrice,
          type: ''
        }
        this.orderids = row.orderId
        this.refundVisible = true
      },
      refundhandlerSubmit(formValue) {
        orderRefundApi({ amount: formValue.amount, orderNo: this.orderids}).then(data => {
          this.$message.success('操作成功')
          this.refundVisible = false
          this.getList()
        })
      },
      // 发送
      sendOrder(row) {
        if(row.type===0){
          this.$refs.send.modals = true;
          this.$refs.send.getList();
          this.$refs.send.sheetInfo();
        }else{
          this.$refs.videoSend.modals = true;
          if(!JSON.parse(sessionStorage.getItem('videoExpress'))) this.$refs.videoSend.companyGetList();
        }
        this.orderId = row.orderId;
      },
      // 订单删除
      handleDelete(row, idx) {
        if (row.isDel) {
          this.$modalSure().then(() => {
            orderDeleteApi({ orderNo: row.orderId }).then(() => {
              this.$message.success('删除成功')
              this.tableData.data.splice(idx, 1)
            })
          })
        } else {
          this.$confirm('您选择的的订单存在用户未删除的订单，无法删除用户未删除的订单！', '提示', {
            confirmButtonText: '确定',
            type: 'error'
          })
        }
      },
      // 详情
      onOrderDetails(id) {
        this.orderId = id
        this.$refs.orderDetail.getDetail(id)
        this.$refs.orderDetail.dialogVisible = true
      },
      // 订单记录
      onOrderLog(id) {
        this.dialogVisibleJI = true
        this.LogLoading = true
        this.tableFromLog.orderNo = id
        orderLogApi( this.tableFromLog ).then(res => {
          this.tableDataLog.data = res.list
          this.tableDataLog.total = res.total
          this.LogLoading = false
        }).catch(() => {
          this.LogLoading = false
        })
      },
      pageChangeLog(page) {
        this.tableFromLog.page = page
        this.onOrderLog()
      },
      handleSizeChangeLog(val) {
        this.tableFromLog.limit = val
        this.onOrderLog()
      },
      handleClose() {
        this.dialogVisible = false
      },
      // 备注
      onOrderMark(row) {
        this.$prompt('订单备注', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputErrorMessage: '请输入订单备注',
          inputType: 'textarea',
          inputValue: row.remark,
          inputPlaceholder: '请输入订单备注',
          inputValidator: (value) => { if(!value) return '输入不能为空'}
        }).then(({value}) => {
          orderMarkApi({ mark : value, orderNo: row.orderId}).then(() => {
            this.$message.success('操作成功')
            this.getList();
          })
        }).catch(() => {
          this.$message.info('取消输入')
        })
      },
      handleSelectionChange(val) {
        this.selectionList = val
        const data = []
        this.selectionList.map((item) => {
          data.push(item.id)
        })
        this.ids = data.join(',')
      },
      // 选择时间
      selectChange (tab) {
        this.timeVal = [];
        this.tableFrom.page = 1
        this.getList();
        this.getOrderStatusNum();
        this.getOrderListData();
      },
      // 具体日期
      onchangeTime (e) {
        this.timeVal = e;
        this.tableFrom.dateLimit = e ? this.timeVal.join(',') : ''
        this.tableFrom.page = 1
        this.getList();
        this.getOrderStatusNum();
        this.getOrderListData();
      },
      // 编辑
      edit(row) {
        this.dialogVisible = true
        this.orderId = row.id
        this.editData = {
          orderId: row.orderId,
          totalPrice: row.totalPrice,
          totalPostage: row.totalPostage,
          payPrice: row.payPrice,
          payPostage: row.payPostage,
          gainIntegral: row.gainIntegral,
        }
      },
      handlerSubmit(formValue) {
        orderUpdateApi(formValue, {id: this.orderId}).then(data => {
          this.$message.success('编辑数据成功')
          this.dialogVisible = false
          this.getList()
        })
      },
      // 列表
      getList() {
        this.listLoading = true
        orderListApi(this.tableFrom).then(res => {
          this.tableData.data = res.list || [];
          this.tableData.total = res.total;
          this.listLoading = false
        }).catch(() => {
          this.listLoading = false
        })
      },
      // 数据统计
      getOrderListData() {
        orderListDataApi({dateLimit:this.tableFrom.dateLimit}).then(res => {
          this.cardLists = [
            { name: '订单数量', count: res.count },
            { name: '订单金额', count: res.amount },
            { name: '微信支付金额', count: res.weChatAmount },
            { name: '余额支付金额', count: res.yueAmount }
          ]
        });
      },
      // 获取各状态数量
      getOrderStatusNum() {
        orderStatusNumApi({dateLimit:this.tableFrom.dateLimit}).then(res => {
          this.orderChartType = res;
        });
      },
      pageChange(page) {
        this.tableFrom.page = page
        this.getList()
      },
      handleSizeChange(val) {
        this.tableFrom.limit = val
        this.getList()
      },
    }
}
</script>

<style lang="scss" scoped>
  .demo-table-expand{
    /deep/label{
      width: 83px !important;
    }
  }
  .refunding{
    span{
      display: block;
    }
  }
  .selWidth{
    width: 300px;
  }
  .el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
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
</style>
