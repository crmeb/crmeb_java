<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form inline size="small">
            <el-form-item label="规格搜索：">
              <el-input v-model="tableFrom.keywords" placeholder="请输入商品规格" class="selWidth" clearable>
                <el-button slot="append" icon="el-icon-search" @click="seachList" />
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <div class="acea-row">
          <el-button size="small" type="primary" @click="add">添加商品规格</el-button>
          <el-button size="small" @click="handleDeleteAll">批量删除</el-button>
        </div>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        highlight-current-row
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          type="selection"
          width="55"
        />
        <el-table-column
          prop="id"
          label="ID"
          min-width="60"
        />
        <el-table-column
          prop="ruleName"
          label="规格名称"
          min-width="150"
        />
        <el-table-column
          label="商品规格"
          min-width="150"
        >
          <template slot-scope="scope">
            <span v-for="(item, index) in scope.row.ruleValue" :key="index" class="mr10" v-text="item.value" />
          </template>
        </el-table-column>
        <el-table-column
          label="商品属性"
          min-width="300"
        >
          <template slot-scope="scope">
            <div v-for="(item, index) in scope.row.ruleValue" :key="index" v-text="item.detail.join(',')" />
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="120" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="onEdit(scope.row)" class="mr10">编辑</el-button>
            <el-button type="text" size="small" @click="handleDelete(scope.row.id, scope.$index)">删除</el-button>
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
import { templateListApi, attrDeleteApi } from '@/api/store'
export default {
  name: 'StoreAttr',
  data() {
    return {
      formDynamic: {
        ruleName: '',
        ruleValue: []
      },
      tableFrom: {
        page: 1,
        limit: 20,
        keywords: ''
      },
      tableData: {
        data: [],
        loading: false,
        total: 0
      },
      listLoading: true,
      selectionList: [],
      keyNum: 0
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    seachList() {
      this.tableFrom.page = 1
      this.getList()
    },
    handleSelectionChange(val) {
      this.selectionList = val
      const data = []
      this.selectionList.map((item) => {
        data.push(item.id)
      })
      this.ids = data.join(',')
    },
    add() {
      const _this = this
      this.$modalAttr(Object.assign({}, this.formDynamic), function() {
        _this.getList()
      }, this.keyNum += 1)
    },
    // 列表
    getList() {
      this.listLoading = true
      templateListApi(this.tableFrom).then(res => {
        const list = res.list
        this.tableData.data = list
        this.tableData.total = res.total
        for (var i = 0; i < list.length; i++) {
          list[i].ruleValue = JSON.parse(list[i].ruleValue)
        }
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
    // 删除
    handleDelete(id, idx) {
      this.$modalSure().then(() => {
        attrDeleteApi( id ).then(() => {
          this.$message.success('删除成功')
          this.tableData.data.splice(idx, 1)
        })
      }).catch(() => {
      })
    },
    handleDeleteAll(){
      if(!this.selectionList.length) return this.$message.warning('请选择商品规格')
      this.$modalSure().then(() => {
        attrDeleteApi( this.ids ).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      }).catch(() => {
      })
    },
    onEdit(val) {
      const _this = this
      this.$modalAttr(JSON.parse(JSON.stringify(val)), function() {
        _this.getList()
      })
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
  .fr{
    float: right;
  }
</style>
