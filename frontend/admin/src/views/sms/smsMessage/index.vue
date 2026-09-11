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
import { ref, onMounted } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import zbParser from '@/components/FormGenerator/components/parser/ZBParser';
import { configSaveForm, configInfo } from '@/api/systemConfig.js';

defineOptions({ name: 'SmsMessage' });

const isShow = ref(true);
const isCreate = ref(0);
const editData = ref({});
const formId = ref(174);

function resetForm(formValue) {
  isShow.value = false;
}
function handlerSubmit(data) {
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
}
// 获取表单详情
function getFormInfo() {
  configInfo({ id: formId.value }).then((res) => {
    isShow.value = false;
    editData.value = res;
    isCreate.value = 1;
    setTimeout(() => {
      // 让表单重复渲染待编辑数据
      isShow.value = true;
    }, 80);
  });
}

onMounted(() => {
  getFormInfo();
});
</script>

<style scoped></style>
