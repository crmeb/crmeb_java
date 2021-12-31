<template>
  <el-dialog v-model="dialogFormVisible" title="请选择管理员" append-to-body :visible.sync="dialogFormVisible" width="1200px" @close="cancel">
    <el-form ref="form" inline :model="artFrom">
      <el-form-item label="身份：">
        <el-select v-model="artFrom.roles" placeholder="请输入身份" clearable class="selWidth">
          <el-option
            v-for="item in roleList.list"
            :key="item.id"
            :label="item.roleName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="姓名：">
        <el-input v-model="artFrom.realName" size="small" placeholder="请输入姓名或者账号" class="selWidth">
          <el-button slot="append" icon="el-icon-search" @click="search" class="">搜索</el-button>
        </el-input>
      </el-form-item>
    </el-form>
    <el-table
      v-loading="loading"
      :row-style="{height:'50px'}"
      :data="tableData"
      max-height="400px"
      size="mini"
      style="width: 100%;">
      <el-table-column
        label=""
        width="55">
        <template slot-scope="{ row, index }">
          <el-radio v-model="templateRadio" :label="row.uid" @change.native="getTemplateRow(row)">&nbsp;</el-radio>
        </template>
      </el-table-column>
      <el-table-column
        prop="id"
        label="ID"
        sortable
        width="80">
      </el-table-column>
      <el-table-column
        prop="realName"
        label="姓名"
        min-Width="120">
      </el-table-column>
      <!--<el-table-column-->
        <!--label="客服头像"-->
        <!--min-Width="100">-->
        <!--<template slot-scope="{ row, index }" class="picMiddle">-->
          <!--<div class="demo-image__preview">-->
            <!--<el-image-->
              <!--:src="row.avatar"-->
              <!--:preview-src-list="[row.avatar]"-->
            <!--/>-->
          <!--</div>-->
        <!--</template>-->
      <!--</el-table-column>-->
      <el-table-column
        prop="account"
        label="账号"
        min-Width="120"/>
      <el-table-column label="身份" prop="realName" min-width="230">
        <template slot-scope="scope">
          <el-tag size="small" type="info" v-for="(item, index) in scope.row.roleNames.split(',')" :key="index" class="mr5">{{ item }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="最后登录时间" prop="lastTime" min-width="180">
        <template slot-scope="scope">
          <span>{{ scope.row.lastTime | filterEmpty }}</span>
        </template>
      </el-table-column>
      <el-table-column label="最后登录IP" prop="lastIp" min-width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.lastIp | filterEmpty }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" min-width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.status | filterShowOrHide }}</span>
        </template>
      </el-table-column>
      <el-table-column label="删除标记" prop="status" min-width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.isDel | filterYesOrNo }}</span>
        </template>
      </el-table-column>
    </el-table>
    <div class="block">
      <el-pagination
        class="mt20"
        @size-change="sizeChange"
        @current-change="pageChange"
        :current-page="artFrom.page"
        :page-sizes="[20, 40, 60, 100]"
        :page-size="artFrom.limit"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>
  </el-dialog>
</template>

<script>
  import * as systemAdminApi from '@/api/systemadmin.js'
  import * as roleApi from '@/api/role.js'
    export default {
      name: "index",
      data(){
          return {
            constants: this.$constants,
            loading:false,
            templateRadio:'',
            dialogFormVisible:false,
            tableData:[],
            artFrom: {
              page: 1,
              limit: 20,
              status: 1,
              realName: '',
              roles: ''
            },
            total:0,
            timeVal:'',
            roleList: []
          }
      },
      created(){
        this.handleGetRoleList()
      },
      methods:{
        handleGetRoleList() {
          const _pram = {
            page: 1,
            limit: 9999
          }
          roleApi.getRoleList(_pram).then(data => {
            this.roleList = data
          })
        },
        getTemplateRow(row){
          this.dialogFormVisible = false;
          this.$emit("upImgUid", row);
        },
        tableList(){
          this.loading = true;
          systemAdminApi.adminList( this.artFrom ).then(data => {
            this.tableData = data.list
            this.total = data.total
            this.loading = false;
          }).catch(() => {
            this.loading = false;
          })
          // let that = this;
          // that.loading = true;
          // userListApi(that.artFrom).then(res=>{
          //   that.loading = false;
          //   that.tableData = res.list;
          //   that.total = res.total
          // })
        },
        //切换显示条数
        sizeChange(index){
          this.artFrom.limit = index;
          this.tableList();
        },
        //切换页数
        pageChange(index){
          this.artFrom.page = index;
          this.tableList();
        },
        onchangeTime(e){
          this.artFrom.page = 1;
          if(e!==null){
            this.artFrom.data = e.join(',');
          }else {
            this.artFrom.data = '';
          }
          this.tableList();
        },
        search(){
          this.timeVal = '';
          this.artFrom.page = 1;
          this.tableList();
        },
        cancel(){
          this.artFrom = {
              page: 1,
              limit: 20,
              data: '',
              realName: ''
          };
          this.timeVal = '';
          this.templateRadio = ''
        }
      }
    }
</script>

<style lang="scss">
  .el-input-group__append, .el-input-group__prepend{
    background-color:#1890ff!important;
    color: #fff!important;
    border-color:#1890ff!important;
    border-radius: 0 4px 4px 0;
  }
</style>
