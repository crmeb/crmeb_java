<template>
  <div class="components-container">
    <el-form ref="editPramRef" :model="editPram" label-width="90px">
      <el-form-item
        label="数据组名称："
        prop="name"
        :rules="[{ required: true, message: '填写数据组名称', trigger: ['blur', 'change'] }]"
      >
        <el-input v-model="editPram.name" placeholder="数据组名称" clearable />
      </el-form-item>
      <el-form-item
        label="数据简介："
        prop="info"
        :rules="[{ required: true, message: '填写数据简介', trigger: ['blur', 'change'] }]"
      >
        <el-input v-model="editPram.info" placeholder="数据简介" clearable />
      </el-form-item>
      <el-form-item
        label="表单数据ID："
        prop="formId"
        :rules="[{ required: true, message: '请选择表单数据', trigger: ['change'] }]"
      >
        <span>{{ editPram.formId }}</span>
        <el-button class="ml10" type="primary" @click="selectFormDialogConfig.visible = true">选择模板数据</el-button>
      </el-form-item>
      <el-form-item class="dialog-footer-inner">
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" class="btn-width100" @click="handlerSubmit('editPramRef')">确定</el-button>
      </el-form-item>
    </el-form>
    <el-dialog title="选择表单模板" v-model="selectFormDialogConfig.visible" append-to-body>
      <form-config-list select-model @selectedRowData="handlerSelectedRowData" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import formConfigList from '@/views/maintain/formConfig';
import * as systemGroupApi from '@/api/systemGroup';
import { Debounce } from '@/utils/validate';

defineOptions({});

const props = defineProps({
  isCreate: {
    type: Number,
    default: 0,
  },
  editData: {
    type: Object,
    default: () => ({}),
  },
});

const emit = defineEmits(['closeDialog', 'hideDialog']);

const { proxy } = getCurrentInstance();

const editPram = reactive({
  formId: null,
  info: null,
  name: null,
  id: null,
});
const selectedFormConfigData = ref({});
const selectFormDialogConfig = reactive({
  visible: false,
});

const editPramRef = ref(null);

function closeDialog() {
  emit('closeDialog');
}
function handlerInitEditData() {
  if (props.isCreate !== 1) return;
  const { id, name, info, formId, createTime, updateTime } = props.editData;
  editPram.id = id;
  editPram.name = name;
  editPram.info = info;
  editPram.formId = formId;
}
function handlerSelectedRowData(rowData) {
  selectedFormConfigData.value = rowData;
  editPram.formId = selectedFormConfigData.value.id;
  selectFormDialogConfig.visible = false;
}
const handlerSubmit = Debounce(function (formRefName) {
  proxy.$refs[formRefName].validate((result) => {
    if (!result) return;
    props.isCreate === 0 ? handlerSave(editPram) : handlerEdit(editPram);
  });
});
function handlerSave(pram) {
  systemGroupApi.groupSave(pram).then((data) => {
    ElMessage.success('添加组合数据成功');
    emit('hideDialog');
  });
}
function handlerEdit(pram) {
  systemGroupApi.groupEdit(pram).then((data) => {
    ElMessage.success('编辑组合数据成功');
    emit('hideDialog');
  });
}

onMounted(() => {
  handlerInitEditData();
});
</script>

<style lang="scss" scoped>
:deep(.el-dialog__body) {
  padding: 0;
  height: 600px;
}
</style>
