<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-table
        v-loading="listLoading"
        :data="tableData"

        class="table"
        row-key="cityId"
        highlight-current-row
        border
        lazy
        :load="load"
        :tree-props="{ children: 'child', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="cityId" label="编号" min-width="100" />
        <el-table-column prop="parentName" label="上级名称" min-width="100"> </el-table-column>
        <el-table-column prop="name" min-width="250" label="地区名称"> </el-table-column>
        <el-table-column fixed="right" width="80" label="操作">
          <template #default="scope">
            <a @click="editCity(scope.row)" v-hasPermi="['admin:system:city:update']">编辑</a>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog title="编辑城市" v-model="dialogVisible" width="540px" :before-close="handleClose">
      <parser
        v-if="formShow"
        :form-conf="formConf"
        :form-edit-data="formData"
        :is-edit="isCreate === 1"
        @submit="submit"
        @closeDialog="dialogVisible = false"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import parser from '@/components/FormGenerator/components/parser/Parser';
import * as systemFormConfigApi from '@/api/systemFormConfig.js';
import * as logistics from '@/api/logistics.js';
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'CityList' });

const { proxy } = getCurrentInstance();

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
const listLoading = ref(true);
const dialogVisible = ref(false);
// 选中id
const editId = ref(0);
const formShow = ref(false);
const formData = ref({});
const isCreate = ref(0);

// 获取城市数据
function getCityList() {
  listLoading.value = true;
  logistics
    .cityList({
      parentId: parentId.value,
    })
    .then((data) => {
      listLoading.value = false;
      let arr = [];
      data.forEach((item) => {
        let str = {};
        str = item;
        str.hasChildren = true;
        str.parentName = '中国';
        arr.push(str);
      });
      tableData.value = arr;
    });
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
  proxy
    .$modalSure('修改此配置项？')
    .then(() => {
      editId.value = item.id;
      parentId.value = item.parentId;
      const _pram = { id: formId.value };
      systemFormConfigApi.getFormConfigInfo(_pram).then((data) => {
        formShow.value = false;
        isCreate.value = 0;
        getCityInfo();
        dialogVisible.value = true;
        formConf.value = JSON.parse(data.content);
      });
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消',
      });
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
const submit = Debounce(function (data) {
  const param = {
    id: editId.value,
    parentId: parentId.value,
    name: data.name,
  };
  logistics.cityUpdate(param).then((res) => {
    ElMessage.success('修改成功');
    dialogVisible.value = false;
    // this.getCityList()
  });
});
//  关闭模态框
function handleClose(done) {
  formConf.value.fields = [];
  dialogVisible.value = false;
}
function load(tree, treeNode, resolve) {
  logistics
    .cityList({
      parentId: tree.cityId,
    })
    .then((data) => {
      let arr = [];
      data.forEach((item) => {
        let str = {};
        str = item;
        str.hasChildren = true; //给每个省市节点赋一个可展开的值
        str.parentName = tree.name;
        parentName.value = tree.name;
        if (item.level == 2) {
          str.hasChildren = false; //当节点为三级时，不可展开
        }
        arr.push(str);
      });
      resolve(arr);
    });
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
