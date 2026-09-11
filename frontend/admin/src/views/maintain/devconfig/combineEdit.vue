<template>
  <div>
    <el-form ref="selfFormRef" :model="selfForm" label-width="120px">
      <el-form-item
        label="排序："
        prop="sort"
        :rules="[{ required: true, message: '排序不能为空', trigger: ['blur', 'change'] }]"
      >
        <el-input-number controls-position="right" v-model="selfForm.sort" />
      </el-form-item>
      <el-form-item
        label="状态："
        prop="status"
        :rules="[{ required: true, message: '正确操作状态', trigger: ['change'] }]"
      >
        <el-switch v-model="selfForm.status" />
      </el-form-item>
    </el-form>
    <parser
      @closeDialog="closeDialog"
      v-if="formConf.fields.length > 0"
      :is-edit="isCreate === 1"
      :form-conf="formConf"
      :form-edit-data="editData"
      @submit="handlerSubmit"
    />
    <!--      {{ editData }}-->
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import parser from '@/components/FormGenerator/components/parser/Parser';
import * as systemGroupDataApi from '@/api/systemGroupData.js';
import * as systemFormConfigApi from '@/api/systemFormConfig.js';
import { Debounce } from '@/utils/validate';

defineOptions({});

const props = defineProps({
  formData: {
    type: Object,
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

const emit = defineEmits(['closeDialog', 'hideDialog']);

const { proxy } = getCurrentInstance();

const formConf = ref({ fields: [] });
const selfForm = reactive({
  sort: 1,
  status: 0,
});

const selfFormRef = ref(null);

function closeDialog() {
  emit('closeDialog');
}
function handlerInitEditData() {
  const { sort, status } = props.editData || {};
  props.isCreate && (selfForm.sort = sort);
  selfForm.status = status;
}
function handlerGetFormConfig() {
  // 获取表单配置后生成table列
  const _pram = { id: props.formData.formId };
  systemFormConfigApi.getFormConfigInfo(_pram).then((data) => {
    formConf.value = JSON.parse(data.content);
  });
}
const handlerSubmit = Debounce(function (formValue) {
  props.isCreate === 0 ? handlerSave(formValue) : handlerEdit(formValue);
});
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
    gid: props.formData.id,
    form: {
      fields: [],
      id: props.formData.formId,
      sort: selfForm.sort,
      status: selfForm.status,
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
