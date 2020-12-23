<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form inline>
            <el-form-item label="砍价状态：">
              <el-select v-model="tableFrom.status" placeholder="请选择" class="filter-item selWidth mr20" @change="getList(1)" clearable>
                <el-option label="关闭" :value="0" />
                <el-option label="开启" :value="1" />
              </el-select>
            </el-form-item>
            <el-form-item label="商品搜索：">
              <el-input v-model="tableFrom.keywords" placeholder="请输入商品名称、ID" class="selWidth" clearable>
                <el-button slot="append" icon="el-icon-search" @click="getList(1)"/>
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <router-link :to=" { path:'/marketing/bargain/creatBargain' }">
          <el-button size="mini" type="primary" class="mr10">添加砍价商品</el-button>
        </router-link>
        <el-button size="mini" class="mr10" @click="exportList">导出</el-button>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        ref="multipleTable"
      >
        <el-table-column
          prop="id"
          label="ID"
          min-width="50"
        />
        <el-table-column label="砍价图片" min-width="80">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.image"
                :preview-src-list="[scope.row.image]"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column
          label="砍价名称"
          prop="title"
          min-width="100"
        />
        <el-table-column
          label="砍价价格"
          prop="price"
          min-width="100"
        />
        <el-table-column
          label="最低价"
          prop="minPrice"
          min-width="100"
        />
        <el-table-column
          label="参与人数"
          prop="countPeopleAll"
          min-width="100"
        />
        <el-table-column
          label="帮忙砍价人数"
          prop="countPeopleHelp"
          min-width="100"
        />
        <el-table-column
          label="砍价成功人数"
          prop="countPeopleSuccess"
          min-width="100"
        />
        <el-table-column
          label="限量"
          min-width="100"
          prop="quotaShow"
        />
        <el-table-column
          label="限量剩余"
          prop="surplusQuota"
          min-width="100"
        />
        <el-table-column
          prop="stopTime"
          label="结束时间"
          min-width="130"
        />
        <el-table-column
          label="砍价状态"
          min-width="150"
        >
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="true"
              :inactive-value="false"
              active-text="开启"
              inactive-text="关闭"
              @change="onchangeIsShow(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="130" fixed="right" align="center">
          <template slot-scope="scope">
            <router-link :to="{ path:'/marketing/bargain/creatBargain/' + scope.row.id}">
              <el-button type="text" size="small">编辑</el-button>
            </router-link>
            <el-button type="text" size="small" @click="handleDelete(scope.row.id, scope.$index)"  class="mr10">删除</el-button>
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
  </div>
</template>

<script>
  import { bargainListApi,bargainDeleteApi,bargainStatusApi,exportBargainApi } from '@/api/marketing'
  export default {
    name: "index",
    data() {
      return {
        tableFrom: {
          page: 1,
          limit: 20,
          keywords: '',
          status: null
        },
        listLoading: true,
        tableData: {
          data: [],
          total: 0
        },
      }
    },
    mounted() {
      this.getList()
    },
    methods: {
      //导出
      exportList(){
        exportBargainApi({keywords: this.tableFrom.keywords, status:this.tableFrom.status}).then((res) => {
          window.open(res.fileName)
        })
      },
      // 删除
      handleDelete(id, idx) {
        this.$modalSure().then(() => {
          bargainDeleteApi({id: id}).then(() => {
            this.$message.success('删除成功')
            this.tableData.data.splice(idx, 1)
          })
        })
      },
      onchangeIsShow(row) {
        bargainStatusApi({id:row.id, status: row.status})
          .then(async () => {
            this.$message.success('修改成功');
            this.getList()
          }).catch(()=>{
          row.status = !row.status
        })
      },
      // 列表
      getList(num) {
        this.listLoading = true
        this.tableFrom.page = num ? num : this.tableFrom.page;
        bargainListApi(this.tableFrom).then(res => {
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
      add() {
        this.isCreate = 0
        this.dialogVisible = true
      },
      handleClose() {
        this.dialogVisible = false
        this.editData = {}
      }
    }
  }
</script>

<style scoped>

</style>
