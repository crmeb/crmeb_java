<template>
  <div class="divBox">
    <el-card class="box-card">
      <template #header>
        <div class="clearfix" v-if="parentId > 0">
          <el-form inline>
            <el-form-item>
              <el-button type="primary" @click="back()">返回</el-button>
              <!--          <el-button type="primary" @click="addcity()"><i class="el-icon-plus" />添加城市</el-button>-->
            </el-form-item>
          </el-form>
        </div>
      </template>
      <el-table v-loading="loading" :data="tableData" class="table" highlight-current-row>
        <el-table-column prop="cityId" label="编号" min-width="100" />
        <el-table-column label="上级名称" min-width="100">
          <div>{{ parentName }}</div>
        </el-table-column>
        <el-table-column min-width="250" label="地区名称">
          <template #default="scope">
            <el-link underline="never" @click="cityDetail(scope.row)">{{ scope.row.name }}</el-link>
          </template>
        </el-table-column>
        <!--<el-table-column-->
        <!--label="状态"-->
        <!--min-width="100"-->
        <!--&gt;-->
        <!--<template #default="scope">-->
        <!--<el-switch-->
        <!--v-model="scope.row.isShow"-->
        <!--class="demo"-->
        <!--active-text="开启"-->
        <!--inactive-text="关闭"-->
        <!--@change="cityStatus(scope.row)"-->
        <!--/>-->
        <!--</template>-->
        <!--</el-table-column>-->
        <el-table-column fixed="right" width="80" label="操作">
          <template #default="scope">
            <el-button link @click="editCity(scope.row)">编辑</el-button>
            <!--            <el-button link>删除</el-button>-->
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog title="提示" v-model="dialogVisible" width="30%" :before-close="handleClose">
      <parser
        v-if="formShow"
        :form-conf="formConf"
        :form-edit-data="formData"
        :is-edit="isCreate === 1"
        @submit="submit"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import parser from '@/components/FormGenerator/components/parser/Parser';
import * as systemFormConfigApi from '@/api/systemFormConfig.js';
import * as logistics from '@/api/logistics.js';

defineOptions({ name: 'CityList' });

// 表单
const formConf = ref({ fields: [] });
// 生成表单id
const formId = ref(70);
const tableData = ref([]);
// 上级名称
const parentName = ref('中国');
// 父级城市id
const parentId = ref(0);
const loading = ref(false);
const dialogVisible = ref(false);
// 选中id
const editId = ref(0);
const formShow = ref(false);
const formData = ref({});
const isCreate = ref(0);

// 获取城市数据
function getCityList() {
  loading.value = true;
  logistics
    .cityList({
      parentId: parentId.value,
    })
    .then((data) => {
      loading.value = false;
      tableData.value = data;
    });
}
// 城市详情
function cityDetail(item) {
  parentId.value = item.cityId;
  parentName.value = item.name;
  getCityList();
}
// 返回
function back() {
  parentName.value = '中国';
  parentId.value = 0;
  getCityList();
}
// 状态
function cityStatus(e) {
  logistics
    .updateStatus({
      id: e.id,
      cityId: e.cityId,
      status: e.isShow,
    })
    .then((res) => {
      ElMessage.success('操作成功');
    })
    .catch(() => {
      e.isShow = !e.isShow;
    });
}
// 编辑
function editCity(item) {
  editId.value = item.id;
  const _pram = { id: formId.value };
  systemFormConfigApi.getFormConfigInfo(_pram).then((data) => {
    formShow.value = false;
    isCreate.value = 0;
    getCityInfo();
    dialogVisible.value = true;
    formConf.value = JSON.parse(data.content);
  });
}
// 详情
function getCityInfo() {
  logistics
    .cityInfo({
      id: editId.value,
    })
    .then((res) => {
      isCreate.value = 1;
      formData.value = res;
      formData.value.parentName = parentName.value;
      setTimeout(() => {
        // 让表单重复渲染待编辑数据
        formShow.value = true;
      }, 80);
    });
}
function submit(data) {
  const param = {
    id: editId.value,
    parentId: parentId.value,
    name: data.name,
  };
  logistics.cityUpdate(param).then((res) => {
    dialogVisible.value = false;
    getCityList();
  });
}
//  关闭模态框
function handleClose(done) {
  formConf.value.fields = [];
  dialogVisible.value = false;
}

// created
getCityList();
</script>

<style lang="scss" scoped>
.el-icon-plus {
  margin-right: 5px;
}

.demo .el-switch__label {
  position: absolute;
  display: none;
  color: #fff;
}

/*打开时文字位置设置*/
.demo .el-switch__label--right {
  z-index: 1;
}

/*关闭时文字位置设置*/
.demo .el-switch__label--left {
  z-index: 1;
  left: 19px;
}

/*显示文字*/
.demo .el-switch__label.is-active {
  display: block;
}

.demo.el-switch .el-switch__core,
.el-switch .el-switch__label {
  width: 60px !important;
}
</style>
