<template>
  <div class="divBox">
    <el-card class="box-card">
      <zb-parser
        :form-id="formId"
        :is-create="isCreate"
        :edit-data="editData"
        @submit="handlerSubmit"
        @resetForm="resetForm"
        v-if="isShow"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import zbParser from '@/components/FormGenerator/components/parser/ZBParser';
import { configSaveForm, configInfo } from '@/api/systemConfig.js';
import { Debounce } from '@/utils/validate';
import { checkPermi } from '@/utils/permission'; // 权限判断函数

defineOptions({ name: 'integralconfig' });

const { proxy } = getCurrentInstance();

const isShow = ref(false);
const isCreate = ref(0);
const editData = ref({});
const formId = ref(109);

const resetForm = (formValue) => {
  editData.value = {};
};
const handlerSubmit = Debounce(function (data) {
  const tempArr = [];
  for (var key in data) {
    const obj = {};
    obj.name = key;
    obj.title = key;
    obj.value = data[key];
    tempArr.push(obj);
  }
  const _pram = {
    fields: tempArr,
    id: formId.value,
    sort: 0,
    status: true,
  };
  configSaveForm(_pram).then((res) => {
    getFormInfo();
    ElMessage.success('操作成功');
  });
});
// 获取表单详情
const getFormInfo = () => {
  configInfo({ id: formId.value }).then((res) => {
    isShow.value = false;
    editData.value = res;
    isCreate.value = 1;
    setTimeout(() => {
      // 让表单重复渲染待编辑数据
      isShow.value = true;
    }, 80);
  });
};

onMounted(() => {
  if (checkPermi(['admin:system:config:info'])) getFormInfo();
});
</script>

<style scoped lang="scss">
:deep(.closeBtn) {
  display: none;
}
:deep(.dialog-footer-inner .el-form-item__content) {
  margin-left: 300px !important;
}
:deep(.dialog-footer-inner .saveBtn) {
  margin-left: 0 !important;
}
:deep(.dialog-footer-inner) {
  float: left !important;
}
</style>
