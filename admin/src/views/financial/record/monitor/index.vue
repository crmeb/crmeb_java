<template>
  <div class="divBox">
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
            <!--<el-form-item label="明细类型：">-->
              <!--<el-select  class="selWidth" v-model="tableFrom.type" filterable clearable placeholder="请选择" @change="getList(1)">-->
                <!--<el-option-->
                  <!--v-for="(item, index) in optionList"-->
                  <!--:key="index"-->
                  <!--:label="item.title"-->
                  <!--:value="item.type"-->
                <!--/>-->
              <!--</el-select>-->
            <!--</el-form-item>-->
            <el-form-item label="关键字：" class="width100">
              <el-input v-model="tableFrom.keywords" placeholder="微信昵称/ID" class="selWidth" size="small" clearable>
                <el-button slot="append" icon="el-icon-search" size="small" @click="getList(1)" />
              </el-input>
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
          prop="nickName"
          label="昵称"
          min-width="130"
        />
        <el-table-column
          prop="number"
          label="金额"
          sortable
          :sort-method="(a,b)=>{return a.number - b.number}"
          min-width="120"
        />
        <el-table-column
          label="明细类型"
          min-width="100"
          prop="title"
        />
        <el-table-column
          prop="mark"
          label="备注"
          min-width="200"
        />
        <el-table-column
          prop="createTime"
          label="创建时间"
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
  import { monitorListApi, monitorListOptionApi } from '@/api/financial'
  import { fromList } from '@/utils/constants.js'
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
         // type: '',
          dateLimit: '',
          keywords: '',
          page: 1,
          limit: 20
        },
        categoryId: '',
        fromList: fromList,
        optionList: []
      }
    },
    mounted() {
      // this.getTypes()
      this.getOptionList()
      this.getList()
    },
    methods: {
      getOptionList() {
        monitorListOptionApi().then(res => {
          this.optionList = res
        })
      },
      selectChange(tab) {
        this.tableFrom.dateLimit = tab
        this.timeVal = []
        this.tableFrom.page = 1;
        this.getList()
      },
      // 具体日期
      onchangeTime(e) {
        this.timeVal = e
        this.tableFrom.dateLimit = e ? this.timeVal.join(',') : ''
        this.tableFrom.page = 1;
        this.getList()
      },
      // 列表
      getList(num) {
        this.listLoading = true
        this.tableFrom.page = num ? num : this.tableFrom.page;
        monitorListApi(this.tableFrom).then(res => {
          this.tableData.data = res.list
          this.tableData.total = res.total
          this.listLoading = false
        }).catch((res) => {
          this.$message.error(res.message)
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
  .selWidth{
    width: 300px;
  }
</style>
