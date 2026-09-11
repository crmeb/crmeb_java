<template>
  <div class="slider-box">
    <div class="c_row-item">
      <el-col class="label" span="4" v-if="configData.title">
        {{ configData.title }}
      </el-col>
      <el-col :span="19" class="slider-box">
        <el-cascader
          :data="configData.list"
          placeholder="请选择商品分类"
          :props="{ multiple: true, checkStrictly: true, emitPath: false }"
          v-model="configData.activeValue"
          filterable
          @change="sliderChange"
        ></el-cascader>
      </el-col>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue';

defineOptions({ name: 'c_cascader' });

const props = defineProps({
  configObj: {
    type: Object,
  },
  configNme: {
    type: String,
  },
  number: {
    type: null,
  },
});

const emit = defineEmits(['getConfig']);

const defaults = ref({});
const configData = ref({});
const timeStamp = ref('');

onMounted(() => {
  nextTick(() => {
    defaults.value = props.configObj;
    configData.value = props.configObj[props.configNme] || {};
  });
});

watch(
  () => props.configObj,
  (nVal, oVal) => {
    defaults.value = nVal;
    configData.value = nVal[props.configNme] || {};
  },
  { deep: true },
);

watch(
  () => props.number,
  (nVal) => {
    timeStamp.value = nVal;
  },
);

function sliderChange(e) {
  let storage = window.localStorage;
  configData.value.activeValue = e ? e : storage.getItem(timeStamp.value);
  emit('getConfig', { name: 'cascader', values: e });
}
</script>

<style scoped lang="scss"></style>
