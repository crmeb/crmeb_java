<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form inline size="small" @submit.native.prevent>
            <el-form-item label="关键字">
              <el-input v-model="codeListData.pram.tableName" placeholder="表名称" clearable  size="small">
                <el-button slot="append" icon="el-icon-search" size="small" @click="handlerSearch" />
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click.native="handleGenCode" :disabled="codeListData.selectedTables.length === 0">生成代码</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <el-table
        ref="codeList"
        :data="codeListData.data.list"
        :highlight-current-row="true"
        size="mini"
        class="table"
        @selection-change="handleSelectionChange"
        :header-cell-style=" {fontWeight:'bold'}">
        <el-table-column
          type="selection"
          width="55">
        </el-table-column>
        <el-table-column label="表名称" prop="tableName" min-width="180"/>
        <el-table-column label="表描述" prop="tableComment" min-width="180"/>
        <el-table-column label="创建时间" prop="createTime" min-width="200" />
      </el-table>
      <el-pagination
        :current-page="codeListData.pram.page"
        :page-sizes="constants.page.limit"
        :layout="constants.page.layout"
        :total="codeListData.data.totalCount"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
  </div>
</template>

<script>
import { getCodegenList } from '@/api/codegen'
import SettingMer from "@/utils/settingMer";

export default {
  name: "codegenList",
  data(){
    return{
      constants: this.$constants,
      codeListData:{
        pram:{
          page: 1,
          limit: 10,
          tableName: ''
        },
        data:{
          list:[],
          totalCount:0
        },
        selectedTables:[]
      }
    }
  },
  created(){

  },
  mounted(){

  },
  methods: {
    handlerSearch(){
      this.codeListData.pram.limit = 10;
      this.codeListData.pram.page = 1;
      this.getList(this.codeListData.pram);
    },
    getList(pram){
      getCodegenList(pram).then(res => {
        this.codeListData.data = res;
      })
    },
    handleSizeChange(val) {
      this.codeListData.pram.limit = val
      this.getList(this.codeListData.pram)
    },
    handleCurrentChange(val) {
      this.codeListData.pram.page = val
      this.getList(this.codeListData.pram)
    },
    handleSelectionChange(selectedRows){
      this.codeListData.selectedTables = [];
      selectedRows.forEach(row => {
        this.codeListData.selectedTables.push(row.tableName);
      });
    },
    handleGenCode(){
      window.open(`${SettingMer.apiBaseURL}codegen/code?tables=${this.codeListData.selectedTables.join(",")}`);

    }
  }
}
</script>

<style scoped>

</style>
