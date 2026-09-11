<template>
  <div>
    <el-dialog
      v-bind="$attrs"
      :close-on-click-modal="false"
      :modal-append-to-body="false"
      @open="onOpen"
      @close="onClose"
    >
      <el-row :gutter="0">
        <el-form ref="elForm" :model="formData" :rules="rules" label-width="100px">
          <el-col :span="24">
            <el-form-item label="选项名" prop="label">
              <el-input v-model="formData.label" placeholder="请输入选项名" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="选项值" prop="value">
              <el-input v-model="formData.value" placeholder="请输入选项值" clearable>
                <template #append>
                  <el-select v-model="dataType" :style="{ width: '100px' }">
                    <el-option
                      v-for="(item, index) in dataTypeOptions"
                      :key="index"
                      :label="item.label"
                      :value="item.value"
                      :disabled="item.disabled"
                    />
                  </el-select>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-form>
      </el-row>
      <template #footer>
        <el-button type="primary" @click="handelConfirm"> 确定 </el-button>
        <el-button @click="close"> 取消 </el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, watch } from 'vue';
import { isNumberStr } from '../utils/index';
import { getTreeNodeId, saveTreeNodeId } from '../utils/db';

defineOptions({ inheritAttrs: false });

const emit = defineEmits(['update:visible', 'commit']);

const elForm = ref(null);
const id = ref(getTreeNodeId());
const formData = reactive({
  label: undefined,
  value: undefined,
});
const rules = {
  label: [
    {
      required: true,
      message: '请输入选项名',
      trigger: 'blur',
    },
  ],
  value: [
    {
      required: true,
      message: '请输入选项值',
      trigger: 'blur',
    },
  ],
};
const dataType = ref('string');
const dataTypeOptions = [
  {
    label: '字符串',
    value: 'string',
  },
  {
    label: '数字',
    value: 'number',
  },
];

// eslint-disable-next-line func-names
watch(
  () => formData.value,
  function (val) {
    dataType.value = isNumberStr(val) ? 'number' : 'string';
  },
);
watch(id, (val) => {
  saveTreeNodeId(val);
});

function onOpen() {
  Object.assign(formData, {
    label: undefined,
    value: undefined,
  });
}
function onClose() {}
function close() {
  emit('update:visible', false);
}
function handelConfirm() {
  elForm.value.validate((valid) => {
    if (!valid) return;
    if (dataType.value === 'number') {
      formData.value = parseFloat(formData.value);
    }
    formData.id = id.value++;
    emit('commit', formData);
    close();
  });
}
</script>

<style lang="scss" scoped></style>
