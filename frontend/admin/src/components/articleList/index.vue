<template>
  <div class="divBox">
    <div class="header clearfix">
      <div class="container">
        <el-form inline>
          <el-form-item>
            <el-input v-model="listPram.keywords" placeholder="请输入关键词" clearable />
          </el-form-item>
          <el-form-item>
            <el-cascader
              v-model="listPram.cid"
              :options="categoryTreeData"
              :props="categoryProps"
              placeholer="选择分类"
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handerSearch">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <el-table :data="listData.list" style="width: 100%" max-height="400" tooltip-effect="dark" highlight-current-row>
      <el-table-column label="" width="55">
        <template #default="{ row, index }">
          <el-radio v-model="templateRadio" :label="row.id" :value="row.id" @change="getTemplateRow(row)">&nbsp;</el-radio>
        </template>
      </el-table-column>
      <el-table-column label="图片" min-width="80">
        <template #default="scope">
          <div class="demo-image__preview">
            <el-image style="width: 36px; height: 36px" :src="scope.row.imageInput" :preview-src-list="imgList" preview-teleported />
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="150" />
      <el-table-column prop="visit" label="浏览量" min-width="100">
        <template #default="scope">
          <span>{{ $filters.filterEmpty(scope.row.visit) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="150" />
      <!--<el-table-column label="操作" min-width="150">-->
      <!--<template #default="scope">-->
      <!--<el-button type="warning" disabled>关联产品</el-button>-->
      <!--</template>-->
      <!--</el-table-column>-->
    </el-table>
    <div class="block mb20">
      <el-pagination
        :current-page="listPram.page"
        :page-sizes="constants.page.limit"
        :layout="constants.page.layout"
        :total="listData.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue';
import { ElMessage, ElMessageBox } from '@/utils/elementPlusFeedback';
import * as articleApi from '@/api/article.js';
import * as categoryApi from '@/api/categoryApi.js';
import * as selfUtil from '@/utils/ZBKJIutil.js';

const props = defineProps({
  handle: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['getArticle']);

const { proxy } = getCurrentInstance();

const constants = proxy.$constants;

const templateRadio = ref('');
const imgList = ref([]);
const listPram = ref({
  keywords: null,
  cid: null,
  page: 1,
  limit: constants.page.limit[0],
});
const listData = ref({ list: [], total: 0 });
const editDialogConfig = ref({
  visible: false,
  data: {},
  isEdit: 0, // 0=add 1=edit
});
const categoryTreeData = ref([]);
const categoryProps = ref({
  value: 'id',
  label: 'name',
  children: 'child',
  expandTrigger: 'hover',
  checkStrictly: true,
  emitPath: false,
});

function getTemplateRow(row) {
  emit('getArticle', row);
}

function handerSearch() {
  listPram.value.page = 1;
  handlerGetListData(listPram.value);
}

function handlerGetListData(pram) {
  articleApi.ListArticle(pram).then((data) => {
    listData.value = data;
    // this.listData.list.map((item) => {
    //   item.imageInput.map(i => {
    //     this.imgList.push(i)
    //   })
    // })
  });
}

function handlerGetCategoryTreeData() {
  const _pram = { type: constants.categoryType[2].value, status: 1 };
  categoryApi.treeCategroy(_pram).then((data) => {
    categoryTreeData.value = selfUtil.addTreeListLabelForCasCard(data);
  });
}

function handlerHideDialog() {
  handlerGetListData(listPram.value);
  editDialogConfig.value.visible = false;
}

function handlerDelete(rowData) {
  ElMessageBox.confirm('确定删除当前数据', '提示', { customClass: 'deleteConfirm' }).then((result) => {
    articleApi.DelArticle(rowData).then((data) => {
      ElMessage.success('删除数据成功');
      handlerGetListData(listPram.value);
    });
  });
}

function handleSizeChange(val) {
  listPram.value.limit = val;
  handlerGetListData(listPram.value);
}

function handleCurrentChange(val) {
  listPram.value.page = val;
  handlerGetListData(listPram.value);
}

onMounted(() => {
  handlerGetListData(listPram.value);
  handlerGetCategoryTreeData();
});
</script>

<style scoped></style>
