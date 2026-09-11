<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form inline>
          <el-form-item label="文章分类：">
            <el-select
              v-model="listPram.cid"
              clearable
              class="selWidth"
              placeholder="请选择文章分类"
              @change="handerSearch"
            >
              <el-option v-for="item in categoryTreeData" :key="item.id" :label="item.name" :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="关键词：">
            <el-input v-model="listPram.keywords" placeholder="请输入关键词" class="selWidth" clearable>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handerSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <template #header>
        <div class="clearfix">
          <router-link :to="{ path: '/content/articleCreat' }">
            <el-button type="primary" class="mr10" v-hasPermi="['admin:article:save']">添加文章</el-button>
          </router-link>
        </div>
      </template>
      <el-table v-loading="listLoading" :data="listData.list" class="table" highlight-current-row>
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column label="图片" min-width="80">
          <template #default="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.imageInput"
                :preview-src-list="[scope.row.imageInput]" preview-teleported
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="title" show-overflow-tooltip label="标题" min-width="300" />
        <el-table-column prop="visit" label="文章分类" min-width="100">
          <template #default="scope">
            <span>{{ $filters.articleTypeFilter(scope.row.cid) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="visit" label="浏览量" min-width="100">
          <template #default="scope">
            <span>{{ $filters.filterEmpty(scope.row.visit) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" min-width="120" />
        <el-table-column prop="synopsis" label="文章简介" show-overflow-tooltip min-width="250" />
        <el-table-column prop="updateTime" label="更新时间" min-width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <router-link :to="{ path: '/content/articleCreat/' + scope.row.id }">
              <a v-hasPermi="['admin:article:info']">编辑</a>
            </router-link>
            <el-divider direction="vertical"></el-divider>
            <a @click="handlerDelete(scope.row)" v-hasPermi="['admin:article:delete']">删除</a>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        :current-page="listPram.page"
        :page-sizes="constants.page.limit"
        :layout="constants.page.layout"
        :total="listData.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        background
      />
    </el-card>
    <el-dialog
      v-model="editDialogConfig.visible"
      :title="editDialogConfig.isEdit === 0 ? '创建文章' : '编辑文章'"
      top="1vh"
      width="900px"
      destroy-on-close
      :modal="false"
      :close-on-click-modal="false"
      class="articleModal"
    >
      <edit
        v-if="editDialogConfig.visible"
        :is-edit="editDialogConfig.isEdit"
        :edit-data="editDialogConfig.editData"
        @hideDialog="handlerHideDialog"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import * as articleApi from '@/api/article.js';
import * as categoryApi from '@/api/categoryApi.js';
import * as selfUtil from '@/utils/ZBKJIutil.js';
import edit from './edit';

const { proxy } = getCurrentInstance();
const constants = proxy.$constants;

const listPram = reactive({
  keywords: null,
  cid: null,
  page: 1,
  limit: constants.page.limit[0],
});
const listData = reactive({ list: [], total: 0 });
const editDialogConfig = reactive({
  visible: false,
  data: {},
  isEdit: 0, // 0=add 1=edit
});
const listLoading = ref(true);
const categoryTreeData = ref([]);
const categoryProps = {
  value: 'id',
  label: 'name',
  children: 'child',
  expandTrigger: 'hover',
  checkStrictly: true,
  emitPath: false,
};

//重置
function handleReset() {
  listPram.cid = null;
  listPram.keywords = null;
  handlerGetListData(listPram);
}
function handlerGetTreeList() {
  categoryApi.listCategroy({ type: 3, status: '' }).then((data) => {
    categoryTreeData.value = data;
    localStorage.setItem('adminArticleClassify', JSON.stringify(data));
  });
}
function handerSearch() {
  listPram.page = 1;
  handlerGetListData(listPram);
}
function handlerGetListData(pram) {
  listLoading.value = true;
  articleApi.ListArticle(pram).then((data) => {
    Object.assign(listData, data);
    listLoading.value = false;
  });
}
function handlerOpenEdit(isEdit, editData) {
  // 0=add 1=edit
  if (isEdit === 1) {
    editDialogConfig.isEdit = 1;
    editDialogConfig.editData = editData;
  } else {
    editDialogConfig.isEdit = 0;
  }
  editDialogConfig.visible = true;
}
// handlerGetCategoryTreeData() {
//   const _pram = { type: constants.categoryType[2].value, status: 1 }
//   categoryApi.treeCategroy(_pram).then(data => {
//     categoryTreeData.value = selfUtil.addTreeListLabelForCasCard(data)
//   })
// }
function handlerHideDialog() {
  handlerGetListData(listPram);
  editDialogConfig.visible = false;
}
function handlerDelete(rowData) {
  proxy.$modalSure('删除当前数据').then((result) => {
    articleApi.DelArticle(rowData).then((data) => {
      ElMessage.success('删除数据成功');
      handlerGetListData(listPram);
    });
  });
}
function handleSizeChange(val) {
  listPram.limit = val;
  handlerGetListData(listPram);
}
function handleCurrentChange(val) {
  listPram.page = val;
  handlerGetListData(listPram);
}

onMounted(() => {
  handlerGetListData(listPram);
  handlerGetTreeList();
});
</script>

<style scoped lang="scss">
.articleModal {
  z-index: 333 !important;
}
</style>
