<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form size="small" :inline="true"  label-width="100px">
            <el-form-item label="模板标题：">
              <el-input v-model="tableFrom.title" placeholder="请输入模板标题" class="selWidth" size="small" clearable></el-input>
            </el-form-item>
            <el-form-item label="所属类目：">
              <el-select v-model="tableFrom.categoryId" placeholder="请选择状态" clearable class="selWidth">
                <el-option :label="item.name" :value="item.id" v-for="item in categoryList" :key="item.id"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="模板类型：">
              <el-select v-model="tableFrom.type" placeholder="请选择类型" clearable class="selWidth">
                <el-option label="一次性订阅" value="2"></el-option>
                <el-option label="长期订阅" value="3"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="seachList" size="small">查询</el-button>
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
          label="ID"
          width="80"
          prop="id"
        />
        <el-table-column
          prop="tid"
          label="模板ID"
          min-width="100"
        />
        <el-table-column
          prop="title"
          label="模版标题"
          min-width="150"
        />
        <el-table-column
          label="所属类目"
          min-width="100"
        >
          <template slot-scope="scope">
            <span>{{ scope.row.categoryId | wxCategoryFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="模版类型"
          min-width="100"
        >
          <template slot-scope="scope">
            <span>{{ scope.row.type | wxTypeFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          min-width="150"
        />
        <el-table-column label="操作" min-width="80" fixed="right" align="center">
          <template slot-scope="scope">
            <router-link :to=" { path:'/appSetting/publicRoutine/creatPublicTemplate/' + scope.row.tid + '/' + scope.row.id + '/0' } ">
              <el-button size="small" type="text" class="mr10">选用</el-button>
            </router-link>
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
  </div>
</template>

<script>
  import { publicTempListApi, categoryApi } from '@/api/wxApi'
  import Cookies from 'js-cookie'
  export default {
    name: "index",
    data() {
      return {
        tableData: {
          data: [],
          total: 0
        },
        listLoading: true,
        tableFrom: {
          page: 1,
          limit: 20,
          title: '',
          type: '',
          categoryId: ''
        },
        categoryList: []
      }
    },
    mounted() {
      this.getList()
      this.getCategoryList()
    },
    methods: {
      seachList() {
        this.tableFrom.page = 1
        this.getList()
      },
      // 列表
      getList() {
        this.listLoading = true
        publicTempListApi(this.tableFrom).then(res => {
          this.tableData.data = res.list || []
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
      // 所属类目
      getCategoryList() {
        categoryApi().then(res => {
          this.categoryList = res
          Cookies.set('WxCategory', res)
        })
      },
    }
  }
</script>

<style scoped>

</style>
