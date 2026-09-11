<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form :inline="true">
          <el-form-item label="回复类型：">
            <el-select
              v-model="tableFrom.type"
              placeholder="请选择类型"
              @change="seachList"
              class="selWidth"
              clearable
            >
              <el-option label="文本消息" value="text"></el-option>
              <el-option label="图片消息" value="image"></el-option>
              <el-option label="图文消息" value="news"></el-option>
              <el-option label="声音消息" value="voice"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="关键字：">
            <el-input
              v-model="tableFrom.keywords"
              placeholder="请输入关键字"
              class="selWidth"

              clearable
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="seachList" v-hasPermi="['admin:wechat:keywords:reply:info:keywords']"
              >搜索</el-button
            >
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <template #header>
        <div class="clearfix">
          <div class="container">
            <router-link :to="{ path: '/appSetting/publicAccount/wxReply/keyword/save' }">
              <el-button type="primary" v-hasPermi="['admin:wechat:keywords:reply:save']">添加关键字</el-button>
            </router-link>
          </div>
        </div>
      </template>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" highlight-current-row>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="keywords" label="关键字" min-width="150" />
        <el-table-column label="回复类型" min-width="100">
          <template #default="scope">
            <span>{{ $filters.keywordStatusFilter(scope.row.type) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="是否显示" min-width="100">
          <template #default="scope">
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
          <template #default="scope">
            <router-link :to="{ path: '/appSetting/publicAccount/wxReply/keyword/save/' + scope.row.id }">
              <el-button
                class="list-btn"
                link

                :disabled="scope.row.keywords === 'subscribe' || scope.row.keywords === 'default'"
                v-hasPermi="['admin:wechat:keywords:reply:info']"
                >编辑</el-button
              >
            </router-link>
            <el-divider direction="vertical"></el-divider>
            <el-button
              class="list-btn"
              :disabled="scope.row.keywords === 'subscribe' || scope.row.keywords === 'default'"
              link

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

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { replyListApi, replyDeleteApi, replyUpdateApi, replyStatusApi } from '@/api/wxApi';
import { getToken } from '@/utils/auth';
import { checkPermi } from '@/utils/permission';

defineOptions({ name: 'WechatKeyword' });

const { proxy } = getCurrentInstance();

const tableData = reactive({
  data: [],
  total: 0,
});
const tableFrom = reactive({
  page: 1,
  limit: 20,
  keywords: '',
  type: '',
});
const listLoading = ref(true);

//重置
const handleReset = () => {
  tableFrom.type = '';
  tableFrom.keywords = '';
  getList();
};
const seachList = () => {
  tableFrom.page = 1;
  getList();
};
const onchangeIsShow = (row) => {
  replyStatusApi({ id: row.id, status: row.status })
    .then(() => {
      ElMessage.success('修改成功');
      getList();
    })
    .catch(() => {
      row.status = !row.status;
    });
};
// 列表
const getList = () => {
  listLoading.value = true;
  replyListApi(tableFrom)
    .then((res) => {
      tableData.data = res.list;
      tableData.total = res.total;
      listLoading.value = false;
    })
    .catch((res) => {
      listLoading.value = false;
    });
};
const pageChange = (page) => {
  tableFrom.page = page;
  getList();
};
const handleSizeChange = (val) => {
  tableFrom.limit = val;
  getList();
};
// 删除
const handleDelete = (id, idx) => {
  proxy.$modalSure().then(() => {
    replyDeleteApi({ id: id }).then(() => {
      ElMessage.success('删除成功');
      getList();
    });
  });
};

// created
getList();
</script>

<style scoped>
.list-btn {
  padding: 0 !important;
}
</style>
