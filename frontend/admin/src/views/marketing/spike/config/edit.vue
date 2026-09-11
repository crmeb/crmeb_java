<template>
  <div>
    <zb-parser
      v-if="formConf.fields.length > 0"
      :form-id="formid"
      :is-create="isCreate"
      :edit-data="editData"
      @submit="handlerSubmit"
      @resetForm="resetForm"
    />
    <!--      {{ editData }}-->
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import zbParser from '@/components/FormGenerator/components/parser/ZBParser';
import * as systemGroupDataApi from '@/api/systemGroupData.js';
import * as systemFormConfigApi from '@/api/systemFormConfig.js';

defineOptions({});

const props = defineProps({
  formid: {
    type: Number,
    required: true,
  },
  isCreate: {
    type: Number,
    default: 0, // 0=create 1=edit
  },
  editData: {
    type: Object,
  },
});

const emit = defineEmits(['hideDialog']);

const { proxy } = getCurrentInstance();

const constants = proxy.$constants;
const formConf = ref({ fields: [] });
const selfForm = reactive({
  sort: 0,
  status: 0,
});

function resetForm(formValue) {
  // dialogVisible = false;
}
function handlerInitEditData() {
  const { sort, status } = props.editData;
  selfForm.sort = sort;
  selfForm.status = status;
}
function handlerGetFormConfig() {
  // 获取表单配置后生成table列
  const _pram = { id: props.formid };
  systemFormConfigApi.getFormConfigInfo(_pram).then((data) => {
    formConf.value = JSON.parse(data.content);
  });
}
function handlerSubmit(formValue) {
  props.isCreate === 0 ? handlerSave(formValue) : handlerEdit(formValue);
}
function handlerSave(formValue) {
  const _pram = buildFormPram(formValue);
  systemGroupDataApi.groupDataSave(_pram).then((data) => {
    ElMessage.success('添加数据成功');
    emit('hideDialog');
  });
}
function handlerEdit(formValue) {
  const _pram = buildFormPram(formValue);
  systemGroupDataApi.groupDataEdit(_pram, props.editData.id).then((data) => {
    ElMessage.success('编辑数据成功');
    emit('hideDialog');
  });
}
function buildFormPram(formValue) {
  const _pram = {
    gid: props.formid,
    form: {
      fields: [],
      id: props.formid,
      // sort: selfForm.sort,
      // status: selfForm.status
    },
  };
  const _fields = [];
  Object.keys(formValue).forEach((key) => {
    _fields.push({
      name: key,
      title: key,
      value: formValue[key],
    });
  });
  _pram.form.fields = _fields;
  return _pram;
}

onMounted(() => {
  handlerGetFormConfig();
  handlerInitEditData();
});
</script>

<style scoped></style>
