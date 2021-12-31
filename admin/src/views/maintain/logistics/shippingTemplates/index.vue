<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form ref="form" inline :model="form">
            <el-form-item label="模板名称：">
              <el-input v-model="form.keywords" placeholder="请输入模板名称" class="selWidth" size="small" clearable>
                <el-button slot="append" icon="el-icon-search" @click="handleSearch" v-hasPermi="['admin:shipping:templates:list']" />
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <el-button type="primary" size="mini" @click="handleSubmit()" v-hasPermi="['admin:shipping:templates:save']">添加运费模板</el-button>
      </div>
      <el-table
        v-loading="loading"
        :data="tableData.list"
        style="width: 100%"
        size="small"
        :header-cell-style=" {fontWeight:'bold'}"
      >
        <el-table-column
          prop="id"
          label="ID"
          min-width="60"
        />
        <el-table-column
          label="模板名称"
          min-width="180"
          prop="name"
        />
        <el-table-column
          min-width="100"
          label="计费方式"
          prop="type"
        >
          <template slot-scope="{row}">
            <p>{{ row.type | typeFilter }}</p>
          </template>
        </el-table-column>
        <el-table-column
          min-width="100"
          label="指定包邮"
          prop="appoint"
        >
          <template slot-scope="{row}">
            <p>{{ row.appoint | statusFilter }}</p>
          </template>
        </el-table-column>
        <el-table-column
          label="排序"
          min-width="100"
          prop="sort"
        />
        <el-table-column
          label="添加时间"
          min-width="150"
          prop="createTime"
        />
        <el-table-column
          prop="address"
          fixed="right"
          width="120"
          label="操作"
        >
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="bindEdit(scope.row)" v-hasPermi="['admin:shipping:templates:info']">修改</el-button>
            <el-button type="text" size="small" @click="bindDelete(scope.row)" v-hasPermi="['admin:shipping:templates:delete']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="block-pagination">
        <el-pagination
          :page-sizes="[20, 40, 60, 80]"
          :page-size="tableData.limit"
          :current-page="tableData.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @current-change="pageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
    <CreatTemplates ref="addTemplates" @getList="getList"/>
  </div>
</template>

<script>
import CreatTemplates from './creatTemplates'
import * as logistics from '@/api/logistics.js'
export default {
  name: 'ShippingTemplates',
  filters: {
    statusFilter(status) {
      const statusMap = {
        true: '开启',
        false: '关闭'
      }
      return statusMap[status]
    },
    typeFilter(status) {
      const statusMap = {
        1: '按件数',
        2: '按重量',
        3: '按体积'
      }
      return statusMap[status]
    }
  },
  components: { CreatTemplates },
  data() {
    return {
      isShow: false,
      dialogVisible: false,
      form: {
        keywords: ''
      },
      tableData: '',
      page: 1,
      limit: 20,
      loading: false
    }
  },
  created() {
    this.getDataList()
  },
  methods: {
    // 添加
    handleSubmit() {
      this.$refs.addTemplates.dialogVisible = true
      this.$refs.addTemplates.getCityList()
      this.$refs.addTemplates.changType(0)
    },
    handleSearch() {
      this.page = 1
      this.getDataList()
    },
    // 分页
    pageChange(e) {
      this.page = e
      this.getDataList()
    },
    handleSizeChange(e) {
      this.limit = e
      this.getDataList()
    },
    // 数据列表
    getDataList() {
      this.loading = true
      logistics.shippingTemplatesList({
        keywords: this.form.keywords,
        page: this.page,
        limit: this.limit
      }).then(res => {
        this.loading = false
        this.tableData = res
      })
    },
    // 编辑
    bindEdit(item) {
      this.$refs.addTemplates.getCityList()
      this.$refs.addTemplates.getInfo(item.id, item.appoint)
      this.$refs.addTemplates.changType(1)
    },
    // 删除
    bindDelete(item) {
      this.$modalSure().then(() => {
        logistics.shippingDetete({ id: item.id }).then(res => {
          this.$message.success('删除成功')
          this.getDataList()
        })
      })
      // logistics.shippingDetete()
    },
    getList() {
      this.getDataList()
    }
  }
}
</script>

<style scoped lang="scss">
.selWidth{
  width: 350px;
}
</style>
