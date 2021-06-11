<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-button size="small" type="primary" @click="onAdd(null)">{{$route.path.indexOf('group') !== -1?'添加用户分组':'添加用户标签'}}</el-button>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="small"
      >
        <el-table-column
          label="ID"
          min-width="80"
          prop="id"
        />
        <el-table-column
          :label="$route.path.indexOf('group') !== -1 ? '分组名称' : '标签名称'"
          min-width="180"
        >
          <template slot-scope="{row}">
            <span v-text="$route.path.indexOf('group') !== -1?row.groupName:row.name"></span>
          </template>
        </el-table-column>
        <!--<el-table-column-->
          <!--prop="create_time"-->
          <!--label="创建时间"-->
          <!--min-width="150"-->
        <!--/>-->
        <el-table-column label="操作" min-width="120" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button class="mr10" type="text" size="small" @click="onAdd(scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleDelete(scope.row.id, scope.$index)" disable>删除</el-button>
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
  import { tagListApi, tagDeleteApi, tagSaveApi, tagInfoApi, tagUpdateApi, groupListApi, groupDeleteApi, groupSaveApi, groupUpdateApi } from '@/api/user'
  export default {
    name: 'UserGroup',
    data() {
      return {
        tableFrom: {
          page: 1,
          limit: 20
        },
        tableData: {
          data: [],
          total: 0
        },
        listLoading: true
      }
    },
    mounted() {
      this.getList()
    },
    methods: {
      info(){

      },
      onAdd(row) {
        this.$prompt(this.$route.path.indexOf('group') !== -1?'分组名称':'标签名称', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputErrorMessage: this.$route.path.indexOf('group') !== -1?'请输入分组名称':'请输入标签名称',
          inputType: 'text',
          closeOnClickModal: false,
          inputValue: row ? (this.$route.path.indexOf('group') !== -1?row.groupName:row.name): ''   ,
          inputPlaceholder: this.$route.path.indexOf('group') !== -1?'请输入分组名称':'请输入标签名称',
          inputValidator: (value) => { if(!value) return '输入不能为空'}
        }).then(({value}) => {
          if(this.$route.path.indexOf('group') !== -1){
            row?groupUpdateApi({id: row.id},{groupName: value}).then(() => {
              this.$message.success('编辑成功')
              this.getList();
            }):groupSaveApi({ groupName: value}).then(() => {
              this.$message.success('新增成功')
              this.getList();
            })
          }else{
            row?tagUpdateApi({id: row.id}, {name: value}).then(() => {
              this.$message.success('编辑成功')
              this.getList();
            }):tagSaveApi({ name: value}).then(() => {
              this.$message.success('新增成功')
              this.getList();
            })
          }
        }).catch(() => {
          this.$message.info('取消输入')
        })
      },
      // 列表
      getList() {
        this.listLoading = true
        this.$route.path.indexOf('group') !== -1 ? groupListApi(this.tableFrom).then(res => {
          this.tableData.data = res.list
          this.tableData.total = res.total
          this.listLoading = false
        }).catch(res => {
          this.listLoading = false
        }) : tagListApi(this.tableFrom).then(res => {
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
      },
      // 删除
      handleDelete(id, idx) {
        this.$modalSure('删除吗？所有用户已经关联的数据都会清除').then(() => {
          this.$route.path.indexOf('group') !== -1 ? groupDeleteApi({id:id}).then(() => {
            this.$message.success('删除成功')
            this.tableData.data.splice(idx, 1)
          }) : tagDeleteApi({id:id}).then(() => {
            this.$message.success('删除成功')
            this.tableData.data.splice(idx, 1)
          })
        })
      }
    }
  }
</script>

<style scoped lang="stylus">

</style>
