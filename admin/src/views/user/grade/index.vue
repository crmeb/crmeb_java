<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form :inline="true" size="small">
            <el-form-item label="用户状态：" class="mr10">
              <el-select v-model="tableFrom.isShow" clearable  placeholder="请选择用户状态" @change="seachList"  class="selWidth">
                <el-option label="全部" value=""></el-option>
                <el-option label="显示" value="true"></el-option>
                <el-option label="隐藏" value="false"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="等级名称：">
              <el-input v-model="tableFrom.name" placeholder="请输入等级名称"  class="selWidth" clearable>
                <el-button slot="append" icon="el-icon-search"  @click="seachList" size="small"/>
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <el-button  type="primary" class="mr10" @click="add" size="small">添加用户等级</el-button>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
      >
        <el-table-column
          prop="id"
          label="ID"
          min-width="50"
        />
        <el-table-column label="等级图标" min-width="80">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.icon"
                :preview-src-list="[scope.row.icon]"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="name"
          label="等级名称"
          min-width="150"
        />
        <el-table-column
          prop="grade"
          label="等级"
          min-width="100"
        />
        <el-table-column
          prop="discount"
          label="享受折扣"
          min-width="100"
        />
        <el-table-column
          label="是否显示"
          min-width="150"
        >
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.isShow"
              :active-value="true"
              :inactive-value="false"
              active-text="显示"
              inactive-text="隐藏"
              @change="onchangeIsShow(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="120" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button type="text" @click="edit(scope.row.id)" class="mr10">编辑</el-button>
            <!--<el-button type="text" @click="handleDelete(scope.row.id, scope.$index)">删除</el-button>-->
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
    <creat-grade ref="grades"></creat-grade>
  </div>
</template>

<script>
  import { userListApi, groupListApi, levelListApi, levelUseApi, levelDeleteApi } from '@/api/user'
  import creatGrade from './creatGrade'
  export default {
    name: 'Grade',
    filters: {
      typeFilter(status) {
        const statusMap = {
          'wechat': '微信用户',
          'routine': '小程序你用户',
          'h5': 'H5用户'
        }
        return statusMap[status]
      }
    },
    components: {creatGrade},
    data() {
      return {
        listLoading: true,
        tableData: {
          data: [],
          total: 0
        },
        tableFrom: {
          isShow: '',
          name: '',
          page: 1,
          limit: 20,
          isDel: 0
        }
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
      add() {
        this.$refs.grades.dialogVisible = true
      },
      edit(id) {
        this.$refs.grades.dialogVisible = true
        this.$refs.grades.info(id)
      },
      // 列表
      getList() {
        this.listLoading = true
        levelListApi(this.tableFrom).then(res => {
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
      // 删除
      handleDelete(id, idx) {
        this.$modalSure().then(() => {
          levelDeleteApi({id:id}).then(() => {
            this.$message.success('删除成功')
            this.tableData.data.splice(idx, 1)
          })
        })
      },
      onchangeIsShow(row) {
        levelUseApi({id: row.id, value:row.isShow}).then(() => {
          this.$message.success('修改成功')
          this.getList()
        }).catch(()=>{
          row.isShow = !row.isShow
        })
      }
    }
  }
</script>

<style scoped lang="scss">

</style>
