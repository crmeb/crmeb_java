<template>
  <div class="divBox">
    <el-card class="box-card">
      <div class="filter-container mb20">
        <div class="demo-input-suffix">
          <span class="seachTiele">短信状态：</span>
          <el-radio-group v-model="tableFrom.resultCode" size="small" @change="getList">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="100">成功</el-radio-button>
            <el-radio-button label="130">失败</el-radio-button>
            <el-radio-button label="131">空号</el-radio-button>
            <el-radio-button label="132">停机</el-radio-button>
            <el-radio-button label="133">关机</el-radio-button>
            <el-radio-button label="134">无状态</el-radio-button>
            <el-radio-button label="400">黑名单</el-radio-button>
          </el-radio-group>
        </div>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        highlight-current-row
      >
        <el-table-column
          prop="id"
          label="ID"
          min-width="50"
        />
        <el-table-column
          prop="phone"
          label="手机号"
          min-width="120"
        />
        <el-table-column
          prop="content"
          label="模板内容"
          min-width="450"
        />
        <el-table-column
          prop="template"
          label="模板ID"
          min-width="100"
        />
        <el-table-column
          prop="memo"
          label="备注"
          min-width="200"
        />
        <el-table-column
          label="发送时间"
          min-width="150"
          prop="createTime"
        />
        <el-table-column
          prop="resultcode"
          label="状态码"
          min-width="100"
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
import { smsLstApi } from '@/api/sms'
import Template from "../../../appSetting/wxAccount/wxTemplate/index";
export default {
  name: 'TableList',
  components: {Template},
  data() {
    return {
      listLoading: false,
      tableData: {
        data: [],
        total: 0
      },
      tableFrom: {
        page: 1,
        limit: 20,
        resultCode: ''
      }
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    // 列表
    getList() {
      this.listLoading = true
      smsLstApi(this.tableFrom).then(res => {
        this.tableData.data = res.list
        this.tableData.total = res.total
        this.listLoading = false
      }).catch(res => {
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

<style scoped>

</style>
