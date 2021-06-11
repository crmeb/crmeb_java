<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form size="small" label-width="100px" :inline="true">
            <el-form-item label="关键字：">
              <el-input v-model="tableFrom.keywords" placeholder="昵称/ID" class="selWidth" size="small" clearable>
                <el-button slot="append" icon="el-icon-search" size="small" @click="getList(1)" />
              </el-input>
            </el-form-item>
            <el-form-item label="佣金范围：">
              <el-col :span="11">
                <el-input v-model="tableFrom.min" placeholder="请输入最小金额" type="number" clearable></el-input>
              </el-col>
              <el-col class="line" :span="2" style="text-align: center;">-</el-col>
              <el-col :span="11">
                <el-input v-model="tableFrom.max" placeholder="请输入最大金额" type="number" clearable></el-input>
              </el-col>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="getList(1)" size="mini">查询</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        class="table"
        highlight-current-row
      >
        <el-table-column
          prop="uid"
          label="会员ID"
          width="80"
        />
        <el-table-column
          prop="nickname"
          label="昵称"
          min-width="130"
        />
        <el-table-column
          sortable
          prop="totalBrokerage"
          label="总佣金金额"
          min-width="120"
        />
        <el-table-column
          sortable
          label="账户余额"
          min-width="100"
          prop="nowMoney"
        />
        <el-table-column
          sortable
          :default-sort = "{prop: 'brokerage', order: 'descending'}"
          prop="brokerage"
          label="账户佣金"
          min-width="100"
        />
        <el-table-column
          sortable
          prop="totalExtract"
          label="到账佣金"
          min-width="150"
        />
        <el-table-column label="操作" min-width="130" fixed="right">
          <template slot-scope="scope">
            <el-button size="small" type="text" @click="handlerOpenInfo(scope.row)">详情</el-button>
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

    <el-dialog
      title="佣金详情"
      :visible.sync="Visible"
      width="900px"
      v-if="Visible"
      :before-close="Close">
      <div>
        <div class="acea-row row-middle" v-if="userDetail">
          <div class="dashboard-workplace-header-tip-desc">
            <span class="dashboard-workplace-header-tip-desc-sp">姓名: {{ userDetail.nickname }}</span>
            <span class="dashboard-workplace-header-tip-desc-sp">上级推广人: {{ userDetail.spreadName | filterEmpty }}</span>
            <span class="dashboard-workplace-header-tip-desc-sp">佣金总收入: {{ userDetail.totalBrokerage }}</span>
            <span class="dashboard-workplace-header-tip-desc-sp">账户余额: {{ userDetail.nowMoney }}</span>
            <span class="dashboard-workplace-header-tip-desc-sp">创建时间: {{ userDetail.createTime }}</span>
          </div>
        </div>
        <el-divider></el-divider>
        <el-form size="small" label-position="left" label-width="90px">
          <el-form-item label="时间选择：" class="width100">
            <el-radio-group v-model="userTableFrom.dateLimit" type="button" class="mr20" size="small" @change="selectChangeUser(userTableFrom.dateLimit)">
              <el-radio-button v-for="(item,i) in fromList.fromTxt" :key="i" :label="item.val">{{ item.text }}</el-radio-button>
            </el-radio-group>
            <el-date-picker v-model="timeValUser" value-format="yyyy-MM-dd" format="yyyy-MM-dd" size="small" type="daterange" placement="bottom-end" placeholder="自定义时间" style="width: 250px;" @change="onchangeTimeUser" />
          </el-form-item>
        </el-form>
        <el-divider></el-divider>
        <el-table
          v-loading="tablistLoading"
          :data="userDetailData.data"
          style="width: 100%"
          size="mini"
          class="table"
          highlight-current-row
        >
          <el-table-column
            prop="price"
            label="佣金金额"
            min-width="100"
          />
          <el-table-column
            prop="updateTime"
            label="获得时间"
            min-width="120"
          />
          <el-table-column
            label="备注"
            min-width="150"
            prop="mark"
          />
        </el-table>
        <div class="block">
          <el-pagination
            :page-sizes="[10, 20, 30, 40]"
            :page-size="userTableFrom.limit"
            :current-page="userTableFrom.page"
            layout="total, sizes, prev, pager, next, jumper"
            :total="userDetailData.total"
            @size-change="userHandleSizeChange"
            @current-change="userPageChange"
          />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import { brokerageListApi, monitorListDetailApi } from '@/api/financial'
  export default {
    name: 'AccountsCapital',
    data() {
      return {
        timeVal: [],
        tableData: {
          data: [],
          total: 0
        },
        userDetailData: {
          data: [],
          total: 0
        },
        listLoading: true,
        tableFrom: {
          min: '',
          max: '',
          keywords: '',
          page: 1,
          limit: 20
        },
        userTableFrom: {
          page: 1,
          limit: 10,
          dateLimit: ''
        },
        fromList: this.$constants.fromList,
        options: [],
        Visible: false,
        tablistLoading: false,
        userDetail: {},
        timeValUser: []
      }
    },
    mounted() {
      // this.getTypes()
      this.getList()
    },
    methods: {
      Close(){
        this.Visible = false
      },
      handlerOpenInfo(row){
        this.userDetail = row
        this.Visible = true
        this.tablistLoading = true
        this.userTableFrom.limit = 10
        this.getUserList()
      },
      getUserList(num){
        this.userTableFrom.page = num ? num : this.userTableFrom.page;
        monitorListDetailApi(this.userDetail.uid, this.userTableFrom).then(res => {
          this.userDetailData.data = res.list
          this.userDetailData.total = res.total
          this.tablistLoading = false
        }).catch((res) => {
          this.tablistLoading = false
        })
      },
      selectChangeUser (tab) {
        this.userTableFrom.dateLimit = tab
        this.timeValUser = []
        this.userTableFrom.page = 1;
        this.getUserList()
      },
      userPageChange(page) {
        this.userTableFrom.page = page
        this.getUserList()
      },
      userHandleSizeChange(val) {
        this.userTableFrom.limit = val
        this.getUserList()
      },
      // 具体日期
      onchangeTimeUser(e) {
        this.timeValUser = e
        this.userTableFrom.dateLimit = e ? this.timeValUser.join(',') : ''
        this.userTableFrom.page = 1;
        this.getUserList()
      },
      // 列表
      getList(num) {
        this.listLoading = true
        this.tableFrom.page = num ? num : this.tableFrom.page;
        brokerageListApi(this.tableFrom).then(res => {
          this.tableData.data = res.list
          this.tableData.total = res.total
          this.listLoading = false
        }).catch((res) => {
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
      getTypes() {
        billTypeApi().then(res => {
          this.options = res.data
          localStorage.setItem('CashKey', JSON.stringify(res.data))
        }).catch((res) => {
          this.$message.error(res.message)
        })
      }
    }
  }
</script>

<style scoped lang="scss">
  .dashboard-workplace {
    &-header {
      &-avatar {
        width: 64px;
        height: 64px;
        border-radius: 50%;
        margin-right: 16px;
        font-weight: 600;
      }

      &-tip {
        width: 82%;
        display: inline-block;
        vertical-align: middle;
        margin-top: -12px;
        &-title {
          font-size: 13px;
          color: #000000;
          margin-bottom: 12px;
        }

        &-desc {
          width: 100%;
          &-sp {
            width: 32%;
            color: #17233D;
            font-size: 13px;
            display: inline-block;
            line-height: 25px;
          }
        }
      }

      &-extra {
        .ivu-col {
          p {
            text-align: right;
          }

          p:first-child {
            span:first-child {
              margin-right: 4px;
            }

            span:last-child {
              color: #808695;
            }
          }

          p:last-child {
            font-size: 22px;
          }
        }
      }
    }
  }
  .selWidth{
    width: 300px;
  }
</style>
