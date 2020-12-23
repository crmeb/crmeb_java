<template>
  <div class="divBox">
    <el-card  class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form ref="form" inline :model="artFrom" size="small" @submit.native.prevent>
            <el-form-item label="提货点名称：">
              <el-select v-model="artFrom.storeId" placeholder="请选择" class="selWidth" clearable @change="search">
                <el-option
                  v-for="item in storeSelectList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-form>
        </div>
        <el-button type="primary" size="small" @click="add">添加核销员</el-button>
      </div>
      <el-table
        v-loading="loading"
        :data="tableData"
        size="mini"
        style="width: 100%">
        <el-table-column
          prop="id"
          label="ID"
          sortable
          width="80">
        </el-table-column>
        <!--<el-table-column-->
          <!--prop="nickname"-->
          <!--label="微信名称"-->
          <!--min-width="150">-->
        <!--</el-table-column>-->
        <el-table-column
          prop="staffName"
          label="核销员名称"
          min-width="150">
        </el-table-column>
        <el-table-column
          prop="avatar"
          label="账号"
          min-width="150">
        </el-table-column>
        <el-table-column
          prop="phone"
          label="手机号码"
          min-width="100">
        </el-table-column>
        <el-table-column
          prop="systemStore.detailedAddress"
          label="所属提货点"
          min-width="200">
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="添加时间"
          min-width="180">
        </el-table-column>
        <!--<el-table-column-->
          <!--prop="status"-->
          <!--label="状态"-->
          <!--min-width="100">-->
          <!--<template slot-scope="{ row, index }">-->
            <!--<el-switch-->
              <!--v-model="row.status"-->
              <!--:active-value="1"-->
              <!--:inactive-value="0"-->
              <!--active-text="显示"-->
              <!--inactive-text="隐藏"-->
              <!--@change="onchangeIsShow(row.id,row.status)">-->
            <!--</el-switch>-->
          <!--</template>-->
        <!--</el-table-column>-->
        <el-table-column
          fixed="right"
          label="操作"
          min-width="120">
          <template slot-scope="{ row, index }">
            <el-button type="text" size="small" @click="edit(row.id)">编辑</el-button>
            <!--<el-divider direction="vertical"></el-divider>-->
            <el-button type="text" size="small" @click="storeDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
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
      <add-clerk-list ref="template" @tableList="tableList" :storeSelectList="storeSelectList"></add-clerk-list>
    </el-card>
  </div>
</template>

<script>
  import addClerkList from './addClerk';
  import { storeStaffListApi, storeListApi, storeStaffDeleteApi, storeStaffUpdateStatusApi } from '@/api/storePoint';
  export default {
    name: 'clerkList',
    components: { addClerkList },
    data () {
      return{
        storeSelectList:[],
        artFrom: {
          page: 1,
          limit: 20,
          storeId: ''
        },
        loading: false,
        tableData:[],
        total:0
      }
    },
    created () {
      this.tableList();
      this.storeList();
    },
    methods: {
      //是否显示
      onchangeIsShow(id, isShow){
        let that = this;
        storeStaffUpdateStatusApi({id:id,status:isShow}).then(() => {
          that.$message.success("操作成功");
          that.tableList();
        }).catch(()=>{
          row.isShow = !row.isShow
        })
      },
      storeList() {
        let artFrom =  {
            page: 1,
            limit: 9999,
            status: '1',
            keywords: ''
        };
        storeListApi(artFrom).then(res=>{
          this.storeSelectList = res.list;
        })
      },
      tableList(){
        let that = this;
        that.loading = true;
        storeStaffListApi (that.artFrom).then(res=>{
          that.loading = false;
          that.tableData = res.list;
          that.total = res.total;
        }).catch(res => {
          that.$message.error(res.message);
        })
      },
      //切换页数
      pageChange(index){
        this.artFrom.page = index;
        this.tableList();
      },
      //切换显示条数
      sizeChange(index){
        this.artFrom.limit = index;
        this.tableList();
      },
      //搜索
      search(){
        this.artFrom.page = 1;
        this.tableList();
      },
      //刪除
      storeDelete(id){
        let that = this;
        that.$modalSure().then(() => {
          storeStaffDeleteApi({ id: id }).then(() => {
            that.$message.success('删除成功');
            that.tableList();
          })
        }).catch(res=>{
          that.$message.error(res.message);
        })
      },
      //添加
      add() {
        this.$refs.template.dialogFormVisible = true;
      },
      //编辑
      edit(id) {
        this.$refs.template.dialogFormVisible = true;
        this.$refs.template.getInfo(id);
      }
    }
  }
</script>

<style scoped lang="scss">

</style>
