<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-button  type="primary" class="mr10" @click="add" size="small" v-hasPermi="['admin:system:user:level:save']">添加用户等级</el-button>
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
          min-width="100"
        />
        <el-table-column
          prop="experience"
          label="经验"
          min-width="100"
        />
        <el-table-column
          prop="discount"
          label="享受折扣(%)"
          min-width="100"
        />
        <el-table-column
          label="状态"
          min-width="100"
        >
          <template slot-scope="scope" v-if="checkPermi(['admin:system:user:level:use'])">
            <el-switch
              v-model="scope.row.isShow"
              :active-value="true"
              :inactive-value="false"
              active-text="开启"
              inactive-text="关闭"
              disabled
              @click.native="onchangeIsShow(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="120" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="edit(scope.row)" class="mr10" v-hasPermi="['admin:system:user:level:update']">编辑</el-button>
            <el-button type="text" size="small" @click="handleDelete(scope.row.id, scope.$index)" v-hasPermi="['admin:system:user:level:delete']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <creat-grade ref="grades" :user="userInfo"></creat-grade>
  </div>
</template>

<script>
  import { userListApi, groupListApi, levelListApi, levelUseApi, levelDeleteApi } from '@/api/user'
  import creatGrade from './creatGrade'
  import { checkPermi } from "@/utils/permission"; // 权限判断函数
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
        userInfo:{},
        tableData: {
          data: [],
          total: 0,
        }
      }
    },
    mounted() {
      this.getList()
    },
    methods: {
      checkPermi,
      seachList() {
        this.getList()
      },
      add() {
        this.$refs.grades.dialogVisible = true
        this.userInfo = {};
      },
      edit(id) {
        // this.$refs.grades.info(id)
        this.userInfo = id;
        this.$refs.grades.dialogVisible = true
      },
      // 列表
      getList() {
        this.listLoading = true
        levelListApi().then(res => {
          this.tableData.data = res
          this.listLoading = false
        }).catch(() => {
          this.listLoading = false
        })
      },
      // 删除
      handleDelete(id, idx) {
        this.$modalSure('删除吗？删除会导致对应用户等级数据清空，请谨慎操作！').then(() => {
          levelDeleteApi(id).then(() => {
            this.$message.success('删除成功')
            this.tableData.data.splice(idx, 1)
          })
        })
      },
      onchangeIsShow(row) {
        if(row.isShow == false){
          row.isShow = !row.isShow
          levelUseApi({id: row.id, isShow:row.isShow}).then(() => {
            this.$message.success('修改成功')
            this.getList()
          }).catch(()=>{
            row.isShow = !row.isShow
          })
        }else{
          this.$modalSure('该操作会导致对应用户等级隐藏，请谨慎操作').then(() => {
            row.isShow = !row.isShow
            levelUseApi({id: row.id, isShow:row.isShow}).then(() => {
              this.$message.success('修改成功')
              this.getList()
            }).catch(()=>{
              row.isShow = !row.isShow
            })
          })
        }
      }
    }
  }
</script>

<style scoped lang="scss">
  .el-switch.is-disabled {
    opacity: 1;
  }
  ::v-deep .el-switch__label {
    cursor: pointer !important;;
  }
</style>
