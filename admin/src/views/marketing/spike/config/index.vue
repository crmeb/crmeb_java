<template>
  <div class="components-container">
    <el-form inline>
      <el-form-item>
        <el-button type="primary" @click="handlerOpenEditData({}, 0)">添加数据</el-button>
      </el-form-item>
      <el-form-item>
        <el-select v-model="listPram.status" placeholder="状态" clearable @change="handlerSearch">
          <el-option
            v-for="item in constants.showHiddenStatus"
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
    <el-dialog
      :title="editDataConfig.isCreate === 0 ? '添加数据' : '编辑数据'"
      v-model="editDataConfig.visible"
      append-to-body
      destroy-on-close
    >
      <edit
        v-if="editDataConfig.visible"
        :formid="formId"
        :edit-data="editDataConfig.editData"
        :is-create="editDataConfig.isCreate"
        @hideDialog="handlerHideDia"
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
        <template #default="scope"
          ><!-- 自定义维护特殊列展示 -->
          <span v-if="item.__config__.label === '状态'">
            {{ $filters.filterShowOrHideForFormConfig(scope.row[item.__vModel__]) }}</span
          >
          <span v-else>{{ scope.row[item.__vModel__] }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button type="primary" @click="handlerOpenEditData(scope.row, 1)">编辑</el-button>
          <el-button type="danger" @click="handlerDelete(scope.row)">删除</el-button>
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
import edit from './edit';
import * as systemGroupDataApi from '@/api/systemGroupData.js';
import * as systemFormConfigApi from '@/api/systemFormConfig.js';

defineOptions({});

const { proxy } = getCurrentInstance();

const constants = proxy.$constants;
const listPram = reactive({
  gid: null,
  keywords: null,
  status: null, // 1=开启 2=关闭
  page: 1,
  limit: constants.page.limit[0],
});
const editDataConfig = reactive({
  visible: false,
  isCreate: 0, // 0=create 1=edit
  editData: {},
});
const formConf = ref({ fields: [] });
const dataList = reactive({ list: [], total: 0 });
const formId = constants.formConfigIds[1].id;

const handlerSearch = () => {
  listPram.page = 1;
  handlerGetListData(listPram);
};
const handlerGetListData = (pram) => {
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
};
const handlerGetFormConfig = () => {
  // 获取表单配置后生成table列
  const _pram = { id: formId };
  systemFormConfigApi.getFormConfigInfo(_pram).then((data) => {
    formConf.value = JSON.parse(data.content);
  });
};
const handlerOpenEditData = (rowData, isCreate) => {
  editDataConfig.editData = rowData;
  editDataConfig.isCreate = isCreate;
  editDataConfig.visible = true;
};
const handlerHideDia = () => {
  handlerGetListData(listPram);
  editDataConfig.visible = false;
};
const handlerDelete = (rowData) => {
  proxy.$modalSure('删除当前数据').then(() => {
    systemGroupDataApi.groupDataDelete(rowData).then((data) => {
      ElMessage.success('删除数据成功');
      handlerHideDia();
    });
  });
};
const handleSizeChange = (val) => {
  listPram.limit = val;
  handlerGetListData(listPram);
};
const handleCurrentChange = (val) => {
  listPram.page = val;
  handlerGetListData(listPram);
};

onMounted(() => {
  handlerGetFormConfig();
  listPram.gid = formId;
  handlerGetListData(listPram);
});
</script>

<style scoped></style>
