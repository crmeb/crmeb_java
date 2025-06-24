<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
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
            <el-input
              v-model="tableFrom.keywords"
              placeholder="请输入关键字"
              class="selWidth"
              size="small"
              clearable
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="seachList" v-hasPermi="['admin:wechat:keywords:reply:info:keywords']"
              >搜索</el-button
            >
            <el-button size="small" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <div slot="header" class="clearfix">
        <div class="container">
          <router-link :to="{ path: '/appSetting/publicAccount/wxReply/keyword/save' }">
            <el-button type="primary" v-hasPermi="['admin:wechat:keywords:reply:save']">添加关键字</el-button>
          </router-link>
        </div>
      </div>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" size="small" highlight-current-row>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="keywords" label="关键字" min-width="150" />
        <el-table-column label="回复类型" min-width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.type | keywordStatusFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="是否显示" min-width="100">
          <template slot-scope="scope">
            <el-switch
              v-if="checkPermi(['admin:wechat:keywords:reply:status'])"
              v-model="scope.row.status"
              :active-value="true"
              :inactive-value="false"
              active-text="显示"
              inactive-text="隐藏"
              @change="onchangeIsShow(scope.row)"
            />
            <span v-else>{{ scope.row.status ? '显示' : '隐藏' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <router-link :to="{ path: '/appSetting/publicAccount/wxReply/keyword/save/' + scope.row.id }">
              <el-button
                class="list-btn"
                type="text"
                size="small"
                :disabled="scope.row.keywords === 'subscribe' || scope.row.keywords === 'default'"
                v-hasPermi="['admin:wechat:keywords:reply:info']"
                >编辑</el-button
              >
            </router-link>
            <el-divider direction="vertical"></el-divider>
            <el-button
              class="list-btn"
              :disabled="scope.row.keywords === 'subscribe' || scope.row.keywords === 'default'"
              type="text"
              size="small"
              @click="handleDelete(scope.row.id, scope.$index)"
              v-hasPermi="['admin:wechat:keywords:reply:delete']"
              >删除</el-button
            >
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
          background
        />
      </div>
    </el-card>
  </div>
</template>

<script>
import { replyListApi, replyDeleteApi, replyUpdateApi, replyStatusApi } from '@/api/wxApi';
import { getToken } from '@/utils/auth';
import { checkPermi } from '@/utils/permission';
export default {
  name: 'WechatKeyword',
  data() {
    return {
      tableData: {
        data: [],
        total: 0,
      },
      tableFrom: {
        page: 1,
        limit: 20,
        keywords: '',
        type: '',
      },
      listLoading: true,
    };
  },
  created() {
    this.getList();
  },
  methods: {
    checkPermi,
    //重置
    handleReset() {
      this.tableFrom.type = '';
      this.tableFrom.keywords = '';
      this.getList();
    },
    seachList() {
      this.tableFrom.page = 1;
      this.getList();
    },
    onchangeIsShow(row) {
      replyStatusApi({ id: row.id, status: row.status })
        .then(() => {
          this.$message.success('修改成功');
          this.getList();
        })
        .catch(() => {
          row.status = !row.status;
        });
    },
    // 列表
    getList() {
      this.listLoading = true;
      replyListApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          this.listLoading = false;
        })
        .catch((res) => {
          this.listLoading = false;
        });
    },
    pageChange(page) {
      this.tableFrom.page = page;
      this.getList();
    },
    handleSizeChange(val) {
      this.tableFrom.limit = val;
      this.getList();
    },
    // 删除
    handleDelete(id, idx) {
      this.$modalSure().then(() => {
        replyDeleteApi({ id: id }).then(() => {
          this.$message.success('删除成功');
          this.getList();
        });
      });
    },
  },
};
</script>

<style scoped>
.list-btn {
  padding: 0 !important;
}
</style>
