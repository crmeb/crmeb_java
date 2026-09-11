<template>
  <div>
    <el-input
      v-model="tableFromNew.content"
      placeholder="请输入用户信息"
      :type="tableFromNew.searchType === 'phone' || tableFromNew.searchType === 'uid' ? 'number' : 'text'"
      @input="handleChangeContent"
      @blur="handleBlur"
      @clear="handleClear"
      clearable
      class="selWidth"
    >
      <template #prepend>
        <el-select @change="handleChangeType" v-model="tableFromNew.searchType" style="width: 100px">
          <el-option value="all" label="全部"></el-option>
          <el-option value="uid" label="用户ID"></el-option>
          <el-option value="phone" label="手机号"></el-option>
          <el-option value="nickname" label="用户昵称"></el-option>
        </el-select>
      </template>
    </el-input>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

defineOptions({ name: 'UserSearchInput' });

const props = defineProps({
  modelValue: {
    type: Object,
    default: {},
  },
});

const emit = defineEmits(['update:modelValue', 'searchList']);

const tableFromNew = ref(props.modelValue);

watch(
  tableFromNew,
  (newVal) => {
    emit('update:modelValue', newVal);
  },
  { deep: true },
);

function handleChangeType() {
  tableFromNew.value.content = '';
}

function handleChangeContent(event) {
  if (tableFromNew.value.searchType === 'phone' || tableFromNew.value.searchType === 'uid')
    tableFromNew.value.content = event.replace(/[^0-9]/g, '');
  if (tableFromNew.value.content > 99999999999) {
    tableFromNew.value.content = ''; // 设置最大值为100
  }
}

// 清空搜索框
function clearInput() {
  tableFromNew.value.content = '';
  tableFromNew.value.searchType = 'all';
}

// 失去焦点
function handleBlur() {
  emit('searchList');
}

// 清除搜索框内容
function handleClear() {
  emit('searchList');
}

defineExpose({
  clearInput,
});
</script>
