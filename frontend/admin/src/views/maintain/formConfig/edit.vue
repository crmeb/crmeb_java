<template>
  <div>
    <config-list :edit-data="editData" :is-create="isCreate" @getFormConfigDataResult="handlerGetFormConfigData" />
  </div>
</template>

<script setup>
import { getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import configList from '@/components/FormGenerator/index/Home.vue';
import * as systemFormConfigApi from '@/api/systemFormConfig.js';

defineOptions({});

const props = defineProps({
  editData: {
    type: Object,
    default: () => ({}),
  },
  isCreate: {
    type: Number,
    default: 0, // 0=创建，1=编辑
  },
});

const emit = defineEmits(['hideDialog']);

function handlerGetFormConfigData(formConfigData) {
  formConfigData.id ? handlerEdit(formConfigData) : handlerSave(formConfigData);
}
function handlerSave(pram) {
  systemFormConfigApi.getFormConfigSave(pram).then((data) => {
    ElMessage.success('创建表单配置成功');
    setTimeout(() => {
      emit('hideDialog');
    }, 800);
  });
}
function handlerEdit(pram) {
  systemFormConfigApi.getFormConfigEdit(pram).then((data) => {
    ElMessage.success('编辑表单配置成功');
    setTimeout(() => {
      emit('hideDialog');
    }, 800);
  });
}
</script>

<style scoped></style>
