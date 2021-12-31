<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="filter-container">
          <el-form :inline="true">
            <el-form-item label="使用状态：" class="mr10">
              <el-select v-model="tableFromIssue.status" placeholder="请选择使用状态" clearable  class="selWidth" @change="seachList">
                <el-option label="已使用" value="1" />
                <el-option label="未使用" value="0" />
                <el-option label="已过期" value="2" />
              </el-select>
            </el-form-item>
            <el-form-item label="领取人：">
              <el-select v-model="tableFromIssue.uid" class="selWidth" reserve-keyword remote filterable
                         :remote-method="remoteMethod" :loading="loading" placeholder="请输入领取人" clearable @change="seachList">
                <el-option
                  v-for="item in options"
                  :key="item.uid"
                  :label="item.nickname"
                  :value="item.uid">
                </el-option>
              </el-select>
            </el-form-item>
            <!--<el-form-item label="领取人：" class="mr10">-->
              <!--<el-input v-model="tableFromIssue.uid" placeholder="请输入领取人" class="selWidth">-->
                <!--<el-button size="mini" slot="append" icon="el-icon-search" @click="seachList"/>-->
              <!--</el-input>-->
            <!--</el-form-item>-->
            <el-form-item label="优惠劵：" class="mr10">
              <el-input v-model="tableFromIssue.name" placeholder="请输入优惠劵" class="selWidth" clearable>
                <el-button slot="append" icon="el-icon-search"  @click="seachList"/>
              </el-input>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <el-table
        v-loading="Loading"
        :data="issueData.data"
        style="width: 100%"
        :header-cell-style=" {fontWeight:'bold'}"
      >
        <el-table-column
          prop="couponId"
          label="优惠券ID"
          min-width="80"
        />
        <el-table-column
          prop="name"
          label="优惠券名称"
          min-width="150"
        />
        <el-table-column
          prop="nickname"
          label="领取人"
          min-width="130"
        />
        <el-table-column
          prop="money"
          label="面值"
          min-width="100"
        />
        <el-table-column
          prop="minPrice"
          label="最低消费额"
          min-width="120"
        />
        <el-table-column
          prop="startTime"
          label="开始使用时间"
          min-width="150"
        />
        <el-table-column
          prop="endTime"
          label="结束使用时间"
          min-width="150"
        />
        <el-table-column
          label="获取方式"
          min-width="150"
        >
          <template slot-scope="scope">
            <span>{{scope.row.type | failFilter}}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="is_fail"
          label="是否可用"
          min-width="100"
        >
          <template slot-scope="scope">
            <i v-if="scope.row.status === 0" class="el-icon-check" style="font-size: 14px;color: #0092DC;" />
            <i v-else class="el-icon-close" style="font-size: 14px;color: #ed5565;" />
          </template>
        </el-table-column>
        <el-table-column
          label="使用状态"
          min-width="100"
        >
          <template slot-scope="scope">
            <span>{{scope.row.status | statusFilter}}</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[20, 40, 60, 80]"
          :page-size="tableFromIssue.limit"
          :current-page="tableFromIssue.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="issueData.total"
          @size-change="handleSizeChangeIssue"
          @current-change="pageChangeIssue"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
  import { couponUserListApi } from '@/api/marketing'
  import { roterPre } from '@/settings'
  import { userListApi } from '@/api/user'
  export default {
    name: 'CouponUser',
    filters: {
      failFilter(status) {
        const statusMap = {
          'receive': '自己领取',
          'send': '后台发送',
          'give': '满赠',
          'new': '新人',
          'buy': '买赠送'
        }
        return statusMap[status]
      },
      statusFilter(status) {
        const statusMap = {
          0: '未使用',
          1: '已使用',
          2: '已过期'
        }
        return statusMap[status]
      }
    },
    data() {
      return {
        Loading: false,
        roterPre: roterPre,
        imgList: [],
        tableFromIssue: {
          page: 1,
          limit: 20,
          uid: '',
          name: '',
          status: ''
        },
        issueData: {
          data: [],
          total: 0
        },
        loading: false,
        options: []
      }
    },
    mounted() {
      this.getIssueList()
    },
    methods: {
      remoteMethod(query) {
        if (query !== '') {
          this.loading = true;
          setTimeout(() => {
            this.loading = false;
            userListApi({keywords: query, page: 1, limit: 10}).then(res => {
              this.options = res.list
            })
          }, 200);
        } else {
          this.options = [];
        }
      },
      seachList() {
        this.tableFromIssue.page = 1
        this.getIssueList()
      },
      // 列表
      getIssueList() {
        this.Loading = true
        couponUserListApi(this.tableFromIssue).then(res => {
          this.issueData.data = res.list
          this.issueData.total = res.total
          // this.issueData.data.map((item) => {
          //   this.imgList.push(item.user.avatar)
          // })
          this.Loading = false
        }).catch(res => {
          this.Loading = false
          this.$message.error(res.message)
        })
      },
      pageChangeIssue(page) {
        this.tableFromIssue.page = page
        this.getIssueList()
      },
      handleSizeChangeIssue(val) {
        this.tableFromIssue.limit = val
        this.getIssueList()
      }
    }
  }
</script>

<style scoped lang="scss">
  .selWidth{
    width: 350px !important;
  }
  .seachTiele{
    line-height: 35px;
  }
  .fa{
    color: #0a6aa1;
    display: block;
  }
  .sheng{
    color: #ff0000;
    display: block;
  }
</style>
