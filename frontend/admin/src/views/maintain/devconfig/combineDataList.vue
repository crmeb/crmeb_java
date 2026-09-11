<template>
  <div class="components-container">
    <div class="container">
      <el-form inline>
        <el-form-item label="状态">
          <el-select v-model="listPram.status" placeholder="状态" clearable @change="handlerSearch" class="selWidth">
            <el-option
              v-for="item in constants.roleListStatus"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <!--        <el-form-item label="关键词">-->
        <!--          <el-input v-model="listPram.keywords" placeholder="请输入关键词" clearable></el-input>-->
        <!--        </el-form-item>-->
        <!--        <el-form-item>-->
        <!--          <el-button type="primary" @click="handlerSearch">查询</el-button>-->
        <!--        </el-form-item>-->
      </el-form>
    </div>
    <el-button
      class="addBtn"
      type="primary"
      @click="handlerOpenEditData({}, 0)"
      v-hasPermi="['admin:system:group:data:save']"
      >添加数据</el-button
    >
    <!-- v-if="((formData.id==55 || formData.name==='签到天数配置') && dataList.list.length<7) || (formData.id!=55|| formData.name!=='签到天数配置')" -->
    <el-dialog
      :title="editDataConfig.isCreate === 0 ? '添加数据' : '编辑数据'"
      v-model="editDataConfig.visible"
      append-to-body
      destroy-on-close
      width="700px"
    >
      <edit
        v-if="editDataConfig.visible"
        :form-data="formData"
        :edit-data="editDataConfig.editData"
        :is-create="editDataConfig.isCreate"
        @hideDialog="handlerHideDia"
        @closeDialog="editDataConfig.visible = false"
      />
    </el-dialog>
    <el-table :data="dataList.list" style="width: 100%; margin-bottom: 20px">
      <el-table-column label="编号" prop="id" />
      <el-table-column
        v-for="(item, index) in formConf.fields"
        :key="index"
        :label="item.__config__.label"
        :prop="item.__vModel__"
      >
        <template #default="scope">
          <div v-if="['img', 'image', 'pic'].indexOf(item.__vModel__) > -1" class="demo-image__preview">
            <el-image
              style="width: 36px; height: 36px"
              :src="scope.row[item.__vModel__]"
              :preview-src-list="[scope.row[item.__vModel__]]" preview-teleported
            />
          </div>
          <span v-else>{{ scope.row[item.__vModel__] }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status">
        <template #default="scope">
          <span>{{ $filters.filterShowOrHide(scope.row.status) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button
            link

            @click="handlerOpenEditData(scope.row, 1)"
            v-hasPermi="['admin:system:group:data:update', 'admin:system:group:data:info']"
            >编辑</el-button
          >
          <el-button
            link

            @click="handlerDelete(scope.row)"
            v-if="formMark !== 99"
            v-hasPermi="['admin:system:group:data:delete']"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page="listPram.page"
      :page-sizes="constants.page.limit"
      :layout="constants.page.layout"
      :total="dataList.total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import edit from './combineEdit';
import * as systemGroupDataApi from '@/api/systemGroupData.js';
import * as systemFormConfigApi from '@/api/systemFormConfig.js';

defineOptions({});

const props = defineProps({
  formData: {
    type: Object,
    required: true,
  },
});

const { proxy } = getCurrentInstance();
const constants = proxy.$constants;

const listPram = reactive({
  gid: null,
  keywords: null,
  status: null, // 1=开启 2=关闭
  page: 1,
  pageSize: constants.page.limit[0],
});
const editDataConfig = reactive({
  visible: false,
  isCreate: 0, // 0=create 1=edit
  editData: {},
});
const formConf = ref({ fields: [] });
const dataList = reactive({ list: [], total: 0 });
const formMark = ref(0);

function handlerSearch() {
  listPram.page = 1;
  handlerGetListData(listPram);
}
function handlerGetListData(pram) {
  // 获取列表数据
  systemGroupDataApi.groupDataList(pram).then((data) => {
    const _selfList = [];
    data.list.forEach((_lItem) => {
      _lItem.value = JSON.parse(_lItem.value);
      const _fields = _lItem.value.fields;
      const _rowData = {};
      _fields.map((item) => {
        _rowData[item.name] = item.value;
      });
      _rowData.id = _lItem.id;
      _rowData.sort = _lItem.sort;
      _rowData.status = _lItem.status;
      _selfList.push(_rowData);
    });
    dataList.list = _selfList;
    dataList.total = data.total;
  });
}
function handlerGetFormConfig() {
  // 获取表单配置后生成table列
  const _pram = { id: props.formData.formId };
  systemFormConfigApi.getFormConfigInfo(_pram).then((data) => {
    formMark.value = parseInt(data.id);
    formConf.value = JSON.parse(data.content);
  });
}
function handlerOpenEditData(rowData, isCreate) {
  editDataConfig.editData = rowData;
  editDataConfig.isCreate = isCreate;
  editDataConfig.visible = true;
}
function handlerHideDia() {
  handlerGetListData(listPram);
  editDataConfig.visible = false;
}
function handlerDelete(rowData) {
  proxy.$modalSure('删除当前数据', '提示').then(() => {
    systemGroupDataApi.groupDataDelete(rowData).then((data) => {
      ElMessage.success('删除数据成功');
      handlerHideDia();
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
  handlerGetFormConfig();
  listPram.gid = props.formData.id;
  handlerGetListData(listPram);
});
</script>

<style lang="scss" scoped>
.addBtn {
  margin-bottom: 20px;
}
.divBox .el-pagination {
  padding-bottom: 20px;
}
</style>
