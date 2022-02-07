<template>
  <div class="divBox">
    <el-card class="box-card">
      <div class="clearfix">
        <div class="container">
          <el-form size="small" label-width="100px" :inline="true" >
            <el-form-item label="时间选择：" class="width100">
              <el-radio-group v-model="tableFrom.dateLimit" type="button" class="mr20" size="small" @change="selectChange(tableFrom.dateLimit)">
                <el-radio-button v-for="(item,i) in fromList.fromTxt" :key="i" :label="item.val">{{ item.text }}</el-radio-button>
              </el-radio-group>
              <el-date-picker v-model="timeVal" value-format="yyyy-MM-dd" format="yyyy-MM-dd" size="small" type="daterange" placement="bottom-end" placeholder="自定义时间" style="width: 250px;" @change="onchangeTime" />
            </el-form-item>
            <el-form-item label="选择门店：">
              <el-select v-model="tableFrom.storeId" filterable placeholder="请选择" class="selWidth" clearable @change="getList(1)">
                <el-option
                  v-for="item in storeSelectList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="关键字：">
              <el-input v-model="tableFrom.keywords" placeholder="请输入姓名、电话、订单ID" class="selWidth" size="small" clearable>
                <el-button slot="append" icon="el-icon-search" size="small" @click="getList(1)" />
              </el-input>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>
    <div class="mt20">
      <cards-data :card-lists="cardLists" />
    </div>
    <el-card class="box-card">
       <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        class="table"
        highlight-current-row
        :header-cell-style=" {fontWeight:'bold'}"
      >
        <el-table-column
          label="订单号"
          prop="orderId"
          min-width="200"
        />
        <el-table-column
          prop="realName"
          label="用户信息"
          min-width="100"
        />
        <el-table-column
          label="推荐人信息"
          min-width="100"
        >
          <template slot-scope="scope">
            <span>{{ scope.row.spreadInfo.name }}</span>
          </template>
        </el-table-column>
         <el-table-column
          label="商品信息"
          min-width="400"
        >
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="right" >
              <div v-if=" scope.row.productList && scope.row.productList.length" slot="reference">
                <div v-for="(val, i ) in scope.row.productList" :key="i" class="tabBox acea-row row-middle" style="flex-wrap: inherit;">
                  <div class="demo-image__preview mr10">
                    <el-image
                      :src="val.info.image"
                      :preview-src-list="[val.info.image]"
                    />
                  </div>
                  <div class="text_overflow">
                    <span class="tabBox_tit mr10">{{ val.info.productName + ' | ' }}{{ val.info.sku ? val.info.sku:'-' }}</span>
                    <span class="tabBox_pice">{{ '￥'+ val.info.price ? val.info.price + ' x '+ val.info.payNum : '-' }}</span>
                  </div>
                </div>
              </div>
              <div class="pup_card" v-if=" scope.row.productList && scope.row.productList.length">
                <div v-for="(val, i ) in scope.row.productList" :key="i" class="tabBox acea-row row-middle" style="flex-wrap: inherit;">
                  <div class="">
                    <span class="tabBox_tit mr10">{{ val.info.productName + ' | ' }}{{ val.info.sku ? val.info.sku:'-' }}</span>
                    <span class="tabBox_pice">{{ '￥'+ val.info.price ? val.info.price + ' x '+ val.info.payNum : '-' }}</span>
                  </div>
                </div>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column
          prop="payPrice"
          label="实际支付"
          min-width="100"
        />
        <el-table-column
          prop="clerkName"
          label="核销员"
          min-width="100"
        />
        <el-table-column
          prop="storeName"
          label="核销门店"
          min-width="150"
        />
        <el-table-column
          label="支付状态"
          min-width="80"
        >
          <template slot-scope="scope">
            <span>{{ scope.row.paid | paidFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="订单状态"
          min-width="100"
        >
          <template slot-scope="scope">
            <span>{{ scope.row.statusStr.value}}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="下单时间"
          min-width="150"
        />
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
  </div>
</template>

<script>
  import { orderListApi, storeListApi } from '@/api/storePoint'
  import cardsData from '@/components/cards/index'
  export default {
    components: { cardsData },
    data() {
      return {
        storeSelectList: [],
        orderId: 0,
        tableData: {
          data: [],
          total: 0
        },
        listLoading: true,
        tableFrom: {
          keywords: '',
          storeId: '',
          dateLimit: '',
          page: 1,
          limit: 20
        },
        timeVal: [],
        fromList: this.$constants.fromList,
        ids: '',
        cardLists: [],
      }
    },
    mounted() {
      this.storeList()
      this.getList()
    },
    methods: {
      storeList() {
        let artFrom =  {
          page: 1,
          limit: 999,
          status: '1',
          keywords: ''
        };
        storeListApi(artFrom).then(res=>{
          this.storeSelectList = res.list;
        })
      },
      pageChangeLog(page) {
        this.tableFromLog.page = page
        this.getList()
      },
      handleSizeChangeLog(val) {
        this.tableFromLog.limit = val
        this.getList()
      },
      // 选择时间
      selectChange(tab) {
        this.tableFrom.date = tab
        this.tableFrom.page = 1
        this.timeVal = []
        this.getList()
      },
      // 具体日期
      onchangeTime(e) {
        this.timeVal = e
        this.tableFrom.dateLimit = e ? this.timeVal.join(',') : ''
        this.tableFrom.page = 1
        this.getList()
      },
      // 列表
      getList(num) {
        this.listLoading = true
        this.tableFrom.page = num ? num : this.tableFrom.page
        orderListApi(this.tableFrom).then(res => {
          this.tableData.data = res.list.list
          this.tableData.total = res.list.total
          this.cardLists = [
            { name: '订单数量', count: res.total,color:'#1890FF',class:'one',icon:'icondingdan' },
            { name: '订单金额', count: res.orderTotalPrice ,color:'#A277FF',class:'two',icon:'icondingdanjine'},
            { name: '退款总单数', count: res.refundTotal,color:'#EF9C20',class:'three',icon:'icondingdanguanli' },
            { name: '退款总金额', count: res.refundTotalPrice,color:'#1BBE6B',class:'four',icon:'iconshangpintuikuanjine' }
          ]
          // this.cardLists = res.data.stat
          this.listLoading = false
        }).catch(() => {
          this.listLoading = false
        })
      },
      pageChange(page) {
        this.tableFrom.page = page
        this.getList()
      },
      handleSizeChange(val) {
        this.tableFrom.limit = val
        this.getList()
      }
    }
  }
</script>

<style lang="scss" scoped>
  .demo-table-expand{
    ::v-deep label{
      width: 83px !important;
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
    width: 60%;
    font-size: 12px !important;
    margin: 0 2px 0 10px;
    letter-spacing: 1px;
    padding: 5px 0;
    box-sizing: border-box;
  }
  .text_overflow{
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .pup_card{
    width: 200px;
    border-radius: 5px;
    padding: 5px;
    box-sizing: border-box;
    font-size: 12px;
    line-height: 16px;
  }
  .flex-column{
    display: flex;
    flex-direction: column;
  }
</style>
