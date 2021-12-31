<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form label-width="100px" :inline="true">
            <el-form-item>
               <el-select v-model="tableFrom.type" clearable  placeholder="请选择">
                <el-option
                  v-for="item in typeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="getList(1)" size="small">查询</el-button>
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
          prop="id"
          label="ID"
          width="80"
        />
        <el-table-column label="佣金变动" min-width="100">
          <template slot-scope="scope">
            <span :class="scope.row.type == 1 ? 'color_red': 'color_green'">{{scope.row.type == 1 ? '+' : '-' }}{{scope.row.price}}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="mark"
          label="变动信息"
          min-width="150"
        />
        <el-table-column
          label="变动类型"
          min-width="130"
          prop="title"
        />
        <el-table-column
          prop="userName"
          label="用户信息"
          min-width="150"
        />
        <el-table-column label="时间" min-width="130" prop="updateTime">
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
  </div>
</template>

<script>
  import { brokerageListApi } from '@/api/financial'
  export default {
    name: 'AccountsCapital',
    data() {
      return {
        timeVal: [],
        tableData: {
          data: [],
          total: 0
        },
        listLoading: true,
        tableFrom: {
          type:'',
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
        typeOptions:[
          { value: 1, label: '订单返佣'},
          { value: 2, label: '申请提现'},
          { value: 3, label: '提现失败'},
          { value: 4, label: '提现成功'},
          { value: 5, label: '佣金转余额'},
        ],
      }
    },
    mounted() {
      // this.getTypes()
      this.getList()
    },
    methods: {
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
  .color_red{
    color:#F5222D;
  }
  .color_green{
    color:#7ABE5C;
  }
</style>
