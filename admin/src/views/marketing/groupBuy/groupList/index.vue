<template>
  <div class="divBox">
    <div class="mt10">
      <cards-data :cardLists="cardLists" v-if="checkPermi(['admin:combination:statistics'])"></cards-data>
    </div>
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form size="small" label-width="100px">
            <el-form-item label="时间选择：" class="width100">
              <el-radio-group v-model="tableFrom.dateLimit" type="button" class="mr20" size="small" @change="selectChange(tableFrom.dateLimit)">
                <el-radio-button v-for="(item,i) in fromList.fromTxt" :key="i" :label="item.val">{{ item.text }}</el-radio-button>
              </el-radio-group>
              <el-date-picker v-model="timeVal" value-format="yyyy-MM-dd" format="yyyy-MM-dd" size="small" type="daterange" placement="bottom-end" placeholder="自定义时间" style="width: 250px;" @change="onchangeTime" />
            </el-form-item>
            <el-form-item label="拼团状态：">
              <el-select v-model="tableFrom.status" placeholder="请选择" class="filter-item selWidth mr20" @change="getList(1)" clearable>
                <el-option label="进行中" :value="1" />
                <el-option label="已成功" :value="2" />
                <el-option label="未完成" :value="3" />
              </el-select>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        ref="multipleTable"
        highlight-current-row
        :header-cell-style=" {fontWeight:'bold'}"
      >
        <el-table-column
          prop="id"
          label="ID"
          min-width="50"
        />
        <el-table-column label="头像" min-width="80">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.avatar"
                :preview-src-list="[scope.row.avatar]"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column
          label="开团团长"
          prop="nickname"
          min-width="100"
        />
        <el-table-column
          label="开团时间"
          prop="addTime"
          min-width="130"
        />
        <el-table-column 
        label="拼团商品" 
        prop="title" 
        min-width="300" 
        :show-overflow-tooltip="true">
        </el-table-column>
        <el-table-column
          label="几人团"
          prop="people"
          min-width="100"
          align="center"
        />
        <el-table-column
          label="几人参加"
          prop="countPeople"
          min-width="100"
          align="center"
        />
        <el-table-column
          prop="stopTime"
          label="结束时间"
          min-width="130"
          align="center"
        />
        <el-table-column
          label="拼团状态"
          min-width="150"
          align="center"
        >
          <template slot-scope="scope">
            <el-tag :type="scope.row.status | groupColorFilter">{{scope.row.status | groupStatusFilter}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="150" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleLook(scope.row.id)"  class="mr10">查看详情</el-button>
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
        />
      </div>
    </el-card>

    <el-dialog
      title="查看详情"
      :visible.sync="dialogVisible"
      width="650px"
      :before-close="handleClose">
      <el-table
        v-loading="listLoadingPink"
        :data="tableDataPink.data"
        style="width: 100%"
        size="mini"
        ref="multipleTable"
      >
        <el-table-column
          prop="id"
          label="ID"
          min-width="50"
        />
        <el-table-column label="头像" min-width="80">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.avatar"
                :preview-src-list="[scope.row.avatar]"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column
          label="用户名称"
          prop="nickname"
          min-width="100"
        />
        <el-table-column
          label="订单编号"
          prop="orderId"
          min-width="180"
        />
        <el-table-column
          label="金额"
          prop="totalPrice"
          min-width="100"
        />
        <el-table-column
          label="订单状态"
          min-width="100"
        >
          <template slot-scope="scope">
            <span v-if="scope.row.refundStatus==0">{{scope.row.orderStatus | orderStatusFilter}}</span>
            <span v-else>{{scope.row.refundStatus | refundStatusFilter}}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
  import { combineListApi, combineStatisticsApi,combineOrderPinkApi } from '@/api/marketing'
  import cardsData from '@/components/cards/index'
  import { checkPermi } from "@/utils/permission"; // 权限判断函数
  export default {
    name: "groupList",
    components: {
      cardsData
    },
    data() {
      return {
        listLoadingPink: false,
        dialogVisible: false,
        tableDataPink: {
          data: []
        },
        tableData: {
          data: [],
          total: 0
        },
        listLoading: false,
        tableFrom: {
          dateLimit: '',
          status: '',
          page: 1,
          limit: 20
        },
        fromList: this.$constants.fromList,
        timeVal: [],
        cardLists: []
      }
    },
    mounted() {
      this.getStatistics();
      this.getList();
    },
    methods:{
      checkPermi,
      handleClose(){
        this.dialogVisible = false
      },
      handleLook(id){
        this.dialogVisible = true;
        this.getPink(id);
      },
      getPink(id) {
        this.listLoadingPink = true;
        combineOrderPinkApi(id).then(res => {
          this.tableDataPink.data = res
          this.listLoadingPink = false
        }).catch(() => {
          this.listLoadingPink = false
        })
      },
      selectChange(tab) {
        this.tableFrom.dateLimit = tab
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
        this.listLoading = true;
        this.tableFrom.page = num ? num : this.tableFrom.page;
        combineListApi(this.tableFrom).then(res => {
          this.tableData.data = res.list
          this.tableData.total = res.total
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
      },
      // 统计
      getStatistics() {
        combineStatisticsApi().then(res => {
          this.cardLists = [
            { name: '参与人数(人)', count: res.countPeople,color:'#1890FF',class:'one',icon:'iconleijiyonghushu' },
            { name: '成团数量(个)', count: res.countTeam,color:'#A277FF',class:'two',icon:'iconxinzengyonghu' }
          ]
        }).catch(() => {
          this.listLoading = false
        })
      },
    }
  }
</script>

<style scoped>
   .el-table__body {
    width: 100%;
    table-layout: fixed !important;
  }
.text_overflow{
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 400px;
}
.pup_card{
  width: 200px;
  border-radius: 5px;
  padding: 5px;
  box-sizing: border-box;
  font-size: 12px;
  line-height: 16px;
}
.mt10{
  margin-top: 10px;
}
</style>
