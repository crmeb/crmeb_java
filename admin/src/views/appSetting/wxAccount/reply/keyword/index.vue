<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form size="small" :inline="true">
            <el-form-item label="回复类型：">
              <el-select v-model="tableFrom.type" placeholder="请选择类型" @change="seachList" class="selWidth" clearable>
                <el-option label="文本消息" value="text"></el-option>
                <el-option label="图片消息" value="image"></el-option>
                <el-option label="图文消息" value="news"></el-option>
                <el-option label="音频消息" value="voice"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="关键字：">
              <el-input v-model="tableFrom.keywords" placeholder="请输入关键字" class="selWidth" size="small" clearable>
                <el-button slot="append" icon="el-icon-search" size="small" @click="seachList" v-hasPermi="['admin:wechat:keywords:reply:info:keywords']" />
              </el-input>
            </el-form-item>
          </el-form>
          <router-link :to="{path: '/appSetting/publicAccount/wxReply/keyword/save'}">
            <el-button size="small" type="primary" v-hasPermi="['admin:wechat:keywords:reply:save']">添加关键字</el-button>
          </router-link>
        </div>
      </div>  
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="small"
        highlight-current-row
      >
        <el-table-column
          prop="id"
          label="ID"
          width="60"
        />
        <el-table-column
          prop="keywords"
          label="关键字"
          min-width="150"
        />
        <el-table-column
          label="回复类型"
          min-width="100"
        >
          <template slot-scope="scope">
            <span>{{ scope.row.type | keywordStatusFilter}}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          label="是否显示"
          min-width="100"
        >
          <template slot-scope="scope" v-if="checkPermi(['admin:wechat:keywords:reply:status'])">
            <el-switch
              v-model="scope.row.status"
              :active-value="true"
              :inactive-value="false"
              active-text="显示"
              inactive-text="隐藏"
              @change="onchangeIsShow(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="100">
          <template slot-scope="scope">
            <router-link :to="{path: '/appSetting/publicAccount/wxReply/keyword/save/' + scope.row.id}">
              <el-button type="text" size="small" 
              v-if="scope.row.keywords !=='subscribe' && scope.row.keywords !=='default'" 
              v-hasPermi="['admin:wechat:keywords:reply:info']">编辑</el-button>
            </router-link>
            <el-button type="text" size="small" @click="handleDelete(scope.row.id, scope.$index)" v-hasPermi="['admin:wechat:keywords:reply:delete']">删除</el-button>
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
import { replyListApi, replyDeleteApi, replyUpdateApi, replyStatusApi } from '@/api/wxApi'
import { getToken } from '@/utils/auth'
import { checkPermi } from "@/utils/permission"; 
export default {
  name: 'WechatKeyword',
  data() {
    return {
      tableData: {
        data: [],
        total: 0
      },
      tableFrom: {
        page: 1,
        limit: 20,
        keywords: '',
        type: ''
      },
      listLoading: true
    }
  },
  created() {
    this.getList()
  },
  methods: {
    checkPermi,
    seachList() {
      this.tableFrom.page = 1
      this.getList()
    },
    onchangeIsShow(row) {
      replyStatusApi({id:row.id, status:row.status}).then(() => {
        this.$message.success('修改成功');
        this.getList();
      }).catch(()=>{
        row.status = !row.status
      })
    },
    // 列表
    getList() {
      this.listLoading = true
      replyListApi(this.tableFrom).then(res => {
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
      this.$modalSure().then(() => {
        replyDeleteApi({id: id}).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    }
  }
}
</script>

<style scoped>

</style>
