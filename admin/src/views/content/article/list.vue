<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form inline size="small">
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
            <el-input v-model="listPram.keywords" placeholder="请输入关键词" class="selWidth" size="small" clearable>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handerSearch" size="small">搜索</el-button>
            <el-button size="small" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <div slot="header" class="clearfix">
        <router-link :to="{ path: '/content/articleCreat' }">
          <el-button type="primary" class="mr10" v-hasPermi="['admin:article:save']">添加文章</el-button>
        </router-link>
      </div>
      <el-table v-loading="listLoading" :data="listData.list" size="mini" class="table" highlight-current-row>
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column label="图片" min-width="80">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.imageInput"
                :preview-src-list="[scope.row.imageInput]"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="visit" label="文章分类" min-width="180">
          <template slot-scope="scope">
            <span>{{ scope.row.cid | articleTypeFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="visit" label="浏览量" min-width="180">
          <template slot-scope="scope">
            <span>{{ scope.row.visit | filterEmpty }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" min-width="180" />
        <el-table-column prop="synopsis" label="文章简介" show-overflow-tooltip min-width="250" />
        <el-table-column prop="updateTime" label="更新时间" min-width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template slot-scope="scope">
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
      :visible.sync="editDialogConfig.visible"
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

<script>
import * as articleApi from '@/api/article.js';
import * as categoryApi from '@/api/categoryApi.js';
import * as selfUtil from '@/utils/ZBKJIutil.js';
import edit from './edit';
export default {
  // name: "list",
  components: { edit },
  data() {
    return {
      constants: this.$constants,
      listPram: {
        keywords: null,
        cid: null,
        page: 1,
        limit: this.$constants.page.limit[0],
      },
      listData: { list: [], total: 0 },
      editDialogConfig: {
        visible: false,
        data: {},
        isEdit: 0, // 0=add 1=edit
      },
      listLoading: true,
      categoryTreeData: [],
      categoryProps: {
        value: 'id',
        label: 'name',
        children: 'child',
        expandTrigger: 'hover',
        checkStrictly: true,
        emitPath: false,
      },
    };
  },
  mounted() {
    this.handlerGetListData(this.listPram);
    this.handlerGetTreeList();
  },
  methods: {
    //重置
    handleReset() {
      this.listPram.cid = null;
      this.listPram.keywords = null;
      this.handlerGetListData(this.listPram);
    },
    handlerGetTreeList() {
      categoryApi.listCategroy({ type: 3, status: '' }).then((data) => {
        this.categoryTreeData = data;
        localStorage.setItem('adminArticleClassify', JSON.stringify(data));
      });
    },
    handerSearch() {
      this.listPram.page = 1;
      this.handlerGetListData(this.listPram);
    },
    handlerGetListData(pram) {
      this.listLoading = true;
      articleApi.ListArticle(pram).then((data) => {
        this.listData = data;
        this.listLoading = false;
      });
    },
    handlerOpenEdit(isEdit, editData) {
      // 0=add 1=edit
      if (isEdit === 1) {
        this.editDialogConfig.isEdit = 1;
        this.editDialogConfig.editData = editData;
      } else {
        this.editDialogConfig.isEdit = 0;
      }
      this.editDialogConfig.visible = true;
    },
    // handlerGetCategoryTreeData() {
    //   const _pram = { type: constants.categoryType[2].value, status: 1 }
    //   categoryApi.treeCategroy(_pram).then(data => {
    //     this.categoryTreeData = selfUtil.addTreeListLabelForCasCard(data)
    //   })
    // },
    handlerHideDialog() {
      this.handlerGetListData(this.listPram);
      this.editDialogConfig.visible = false;
    },
    handlerDelete(rowData) {
      this.$modalSure('删除当前数据').then((result) => {
        articleApi.DelArticle(rowData).then((data) => {
          this.$message.success('删除数据成功');
          this.handlerGetListData(this.listPram);
        });
      });
    },
    handleSizeChange(val) {
      this.listPram.limit = val;
      this.handlerGetListData(this.listPram);
    },
    handleCurrentChange(val) {
      this.listPram.page = val;
      this.handlerGetListData(this.listPram);
    },
  },
};
</script>

<style scoped lang="scss">
.articleModal {
  z-index: 333 !important;
}
</style>
