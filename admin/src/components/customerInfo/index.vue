<template>
  <el-dialog v-model="dialogFormVisible" title="请选择商城用户" append-to-body :visible.sync="dialogFormVisible" width="1000px" @close="cancel">
    <el-form ref="form" inline :model="artFrom">
      <el-form-item label="搜索日期：">
        <el-radio-group v-model="artFrom.data" size="small" @change="search">
          <el-radio-button :label="item.val" v-for="(item,index) in fromData" :key="index" >{{item.text}}</el-radio-button>
        </el-radio-group>
        <el-date-picker
          v-model="timeVal"
          size="small"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy:MM:dd"
          @change="onchangeTime">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="用户名称：">
        <el-input v-model="artFrom.keywords" size="small" placeholder="请输入用户名称" style="width:100%">
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
          <el-radio v-model="templateRadio" :label="row.uid" @change.native="getTemplateRow(row.uid,row.avatar)">&nbsp;</el-radio>
        </template>
      </el-table-column>
      <el-table-column
        prop="uid"
        label="ID"
        sortable
        width="80">
      </el-table-column>
      <el-table-column
        prop="nickname"
        label="微信用户名称"
        min-Width="100">
      </el-table-column>
      <el-table-column
        label="客服头像"
        min-Width="100">
        <template slot-scope="{ row, index }" class="picMiddle">
          <div class="demo-image__preview">
            <el-image
              :src="row.avatar"
              :preview-src-list="[row.avatar]"
            />
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="sex"
        label="性别"
        min-Width="60">
        <template slot-scope="{ row, index }">
          <span v-show="row.sex ===1">男</span>
          <span v-show="row.sex ===2">女</span>
          <span v-show="row.sex ===0">保密</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="country"
        label="地区"
        min-Width="100">
        <template slot-scope="{ row, index }">
          {{row.country}}{{row.province}}{{row.city}}
        </template>
      </el-table-column>
      <el-table-column
        prop="subscribe"
        label="是否关注公众号"
        min-Width="100">
        <template slot-scope="{ row, index }">
          {{row.subscribe?'关注':'未关注'}}
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
  import { userListApi } from '@/api/user';
    export default {
      name: "index",
      data(){
          return{
            loading:false,
            templateRadio:'',
            dialogFormVisible:false,
            tableData:[],
            artFrom: {
              page: 1,
              limit: 20,
              data: '',
              keywords: ''
            },
            total:0,
            timeVal:'',
            fromData: [
              { text: '全部', val: '' },
              { text: '今天', val: 'today' },
              { text: '昨天', val: 'yesterday' },
              { text: '最近7天', val: 'lately7' },
              { text: '最近30天', val: 'lately30' },
              { text: '本月', val: 'month' },
              { text: '本年', val: 'year' }
            ]
          }
      },
      created(){
      },
      methods:{
        getTemplateRow(id,img){
          this.dialogFormVisible = false;
          this.$emit("upImgUid",id,img);
        },
        tableList(){
          let that = this;
          that.loading = true;
          userListApi(that.artFrom).then(res=>{
            that.loading = false;
            that.tableData = res.list;
            that.total = res.total
          })
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
              keywords: ''
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
