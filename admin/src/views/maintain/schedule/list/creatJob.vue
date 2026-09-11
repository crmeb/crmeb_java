<template>
  <el-dialog
    width="540px"
    title="定时任务"
    v-model="dialogVisibleModel"
    :before-close="handleClose"
    :closeOnClickModal="false"
  >
    <el-form ref="dataFormRef" :model="dataForm" label-width="100px" :rules="rules" v-loading="loadingFrom">
      <el-form-item required label="定时任务类名：" prop="beanName">
        <el-input v-model.trim="dataForm.beanName" placeholder="请输入定时任务名称" />
      </el-form-item>
      <el-form-item required label="cron表达式：" prop="cronExpression">
        <el-input v-model.trim="dataForm.cronExpression" placeholder="请输入cron表达式" />
      </el-form-item>
      <el-form-item required label="方法名：" prop="methodName">
        <el-input v-model.trim="dataForm.methodName" placeholder="请输入定时任务方法名" />
      </el-form-item>
      <el-form-item label="参数：" prop="params">
        <el-input v-model.trim="dataForm.params" placeholder="请输入定时任务方法名" />
      </el-form-item>
      <el-form-item label="备注：" prop="remark">
        <el-input v-model.trim="dataForm.remark" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose('dataFormRef')">取消</el-button>
        <el-button type="primary" :loading="loading" @click="onsubmit('dataFormRef')">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import { ref, reactive, computed, watch, nextTick, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import * as schedule from '@/api/schedule';

defineOptions({ name: 'creatClassify' });

const props = defineProps({
  editData: {
    type: Object,
    default: () => {
      return {};
    },
  },
  dialogVisible: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['closeModel', 'getList', 'update:dialogVisible']);

// dialogVisible 是 prop，Vue3 中 prop 不可直接 v-model，用 computed 转 emit update:dialogVisible
const dialogVisibleModel = computed({
  get: () => props.dialogVisible,
  set: (val) => emit('update:dialogVisible', val),
});

const { proxy } = getCurrentInstance();

const loading = ref(false);
const loadingFrom = ref(false);
const rules = ref({});
const dataForm = reactive({ ...props.editData });

const dataFormRef = ref(null);

watch(
  () => props.editData,
  (val) => {
    Object.assign(dataForm, { ...val });
  },
  { deep: true }
);

function handleClose() {
  nextTick(() => {
    emit('closeModel');
    dataFormRef.value.resetFields();
  });
}
function onClose() {
  dataFormRef.value.resetFields();
  emit('closeModel');
  loading.value = false;
  emit('getList');
}
function onsubmit(formName) {
  proxy.$refs[formName].validate((valid) => {
    if (valid) {
      loading.value = true;
      !dataForm.jobId
        ? schedule
            .scheduleJobAdd(dataForm)
            .then((res) => {
              ElMessage.success('操作成功');
              onClose();
            })
            .catch(() => {
              loading.value = false;
            })
        : schedule
            .scheduleJobUpdate(dataForm)
            .then((res) => {
              ElMessage.success('操作成功');
              onClose();
            })
            .catch(() => {
              loading.value = false;
            });
    } else {
      return false;
    }
  });
}
</script>

<style scoped lang="scss">
.lang {
  width: 100%;
  :deep(.el-form-item__content ){
    width: 79%;
  }
}
</style>
