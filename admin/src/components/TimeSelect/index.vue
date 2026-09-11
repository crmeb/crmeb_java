<template>
  <div class="time-select-range">
    <el-time-select
      class="time-select-range__item"
      placeholder="起始时间"
      v-model="startTime"
      start="00:00"
      step="01:00"
      end="24:00"
    >
    </el-time-select>
    <el-time-select
      class="time-select-range__item"
      placeholder="结束时间"
      v-model="endTime"
      start="00:00"
      step="01:00"
      end="24:00"
      :min-time="startTime"
    >
    </el-time-select>
  </div>
</template>

<script setup>
import { ref, watch, onBeforeMount } from 'vue';

defineOptions({ name: 'index' });

const props = defineProps({
  modelValue: {},
});

const emit = defineEmits(['update:modelValue']);

const startTime = ref('');
const endTime = ref('');

onBeforeMount(() => {
  // 接收 v-model 数据
  if (props.modelValue) {
    startTime.value = props.modelValue.split(',')[0];
    endTime.value = props.modelValue.split(',')[1];
  }
});

watch(startTime, function (val) {
  emit('update:modelValue', [val, endTime.value].join(','));
});

watch(endTime, function (val) {
  emit('update:modelValue', [startTime.value, val].join(','));
});
</script>

<style scoped>
.time-select-range {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

:deep(.time-select-range__item) {
  flex: 1 1 0;
  min-width: 0;
}
</style>
