<template>
   <div class="divBox">
     <el-card class="box-card">
       <div slot="header" class="clearfix">
         <el-tabs v-model="artFrom.status" @tab-click="onClickTab" v-if="checkPermi(['admin:system:store:count','admin:system:store:list'])" >
           <el-tab-pane :label="'显示中的提货点('+ headerCount.show +')'" name="1"></el-tab-pane>
           <el-tab-pane :label="'隐藏中的提货点('+ headerCount.hide +')'" name="0"></el-tab-pane>
           <el-tab-pane :label="'回收站的提货点('+ headerCount.recycle +')'" name="2"></el-tab-pane>
         </el-tabs>
         <el-form ref="form" inline :model="artFrom" @submit.native.prevent>
           <el-form-item label="关键字：">
             <el-input v-model="artFrom.keywords" placeholder="请输入提货点名称/电话" class="selWidth" size="small" clearable>
               <el-button slot="append" icon="el-icon-search" @click="search" />
             </el-input>
           </el-form-item>
         </el-form>
         <el-button type="primary" size="small" @click="add" v-hasPermi="['admin:system:store:save']">添加提货点</el-button>
       </div>
       <el-table
         v-loading="loading"
         size="small"
         :data="tableData"
         :header-cell-style=" {fontWeight:'bold'}">
         <el-table-column
           prop="id"
           label="ID"
           min-width="80">
         </el-table-column>
         <el-table-column
           prop="image"
           label="提货点图片"
           min-width="100">
           <template slot-scope="{ row, index }" class="picMiddle">
             <div class="demo-image__preview">
               <el-image
                 style="width: 36px; height: 36px"
                 :src="row.image"
                 :preview-src-list="[row.image]"
               />
             </div>
           </template>
         </el-table-column>
         <el-table-column
           prop="name"
           label="提货点名称"
           min-width="150">
         </el-table-column>
         <el-table-column
           prop="phone"
           label="提货点电话"
           min-width="100">
         </el-table-column>
         <el-table-column
           prop="detailedAddress"
           label="地址"
           min-width="100">
         </el-table-column>
         <el-table-column
           prop="dayTime"
           label="营业时间"
           min-width="180">
         </el-table-column>
         <el-table-column
           prop="isShow"
           label="是否显示"
           min-width="100">
           <template slot-scope="{ row, index }" v-if="checkPermi(['admin:system:store:update:status'])">
             <el-switch
               :active-value="true"
               :inactive-value="false"
               active-text="显示"
               inactive-text="隐藏"
               v-model="row.isShow"
               @change="onchangeIsShow(row.id,row.isShow)">
             </el-switch>
           </template>
         </el-table-column>
         <el-table-column
           fixed="right"
           label="操作"
           min-width="120">
           <template slot-scope="{ row, index }">
             <el-button type="text" size="small" @click="edit(row.id)" v-hasPermi="['admin:system:store:info']">编辑</el-button>
             <el-divider direction="vertical"></el-divider>
             <el-button v-if="artFrom.status==='2'" type="text" size="small" @click="storeRecovery(row.id)" v-hasPermi="['admin:system:store:recovery']">恢复</el-button>
             <el-divider v-if="artFrom.status==='2'" direction="vertical"></el-divider>
             <el-button type="text" size="small" @click="artFrom.status==='2'?allDelete(row.id):storeDelete(row.id)" v-hasPermi="['admin:system:store:delete','admin:system:store:completely:delete']">删除</el-button>
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
     </el-card>
     <system-store ref="template"></system-store>
   </div>
</template>

<script>
  import systemStore from './addPoint';
  import { storeListApi, storeGetCountApi, storeUpdateStatusApi, storeDeleteApi, allDeleteApi, storeRecoveryApi } from '@/api/storePoint';
  import { checkPermi } from "@/utils/permission"; // 权限判断函数
export default {
  name: 'Point',
  components: { systemStore },
  data () {
    return{
      artFrom: {
        page: 1,
        limit: 20,
        status: '1',
        keywords: ''
      },
      loading: false,
      tableData:[],
      total:0,
      headerCount:{}
    }
  },
  created () {
    this.storeGetCount();
    this.tableList();
  },
  methods: {
    checkPermi,
    //头部数量显示；
    storeGetCount(){
      let that = this;
      storeGetCountApi().then(res=>{
        that.headerCount = res
      })
    },
    //表格列表
    tableList(){
      let that = this;
      that.loading = true;
      storeListApi (that.artFrom).then(res=>{
        that.loading = false;
        that.tableData = res.list;
        that.total = res.total;
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
    //头部切换
    onClickTab() {
      this.artFrom.keywords = '';
      this.tableList();
    },
    //搜索
    search(){
      this.artFrom.page = 1;
      this.tableList();
    },
    //是否显示
    onchangeIsShow(id, isShow){
      let that = this;
      storeUpdateStatusApi({id:id,status:isShow}).then(() => {
        that.$message.success("操作成功");
        that.tableList();
        that.storeGetCount();
      }).catch(()=>{
        row.isShow = !row.isShow
      })
    },
    // 恢复
    storeRecovery(id){
      this.$modalSure('恢复提货吗').then(() => {
        storeRecoveryApi({ id: id }).then(() => {
          this.$message.success('恢复成功')
          this.storeGetCount();
          this.tableList();
        })
      })
    },
    //刪除
    storeDelete(id){
      let that = this;
      that.$modalSure('删除提货点吗？').then(() => {
        storeDeleteApi({ id: id }).then(() => {
          that.$message.success('删除成功')
          that.storeGetCount();
          that.tableList();
        })
      })
    },
    allDelete(id){
      this.$modalSure().then(() => {
        allDeleteApi({ id: id }).then(() => {
          this.$message.success('删除成功')
          this.storeGetCount();
          this.tableList();
        })
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
