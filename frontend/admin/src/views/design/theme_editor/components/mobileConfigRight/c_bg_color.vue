<template>
  <div>
    <div class="c_row-item" v-if="configData && configData.color && configData.color.length">
      <div class="c_label">{{ configData.title }}</div>
      <div class="color-box">
        <div class="color-item" v-for="(color, key) in configData.color" :key="key">
          <el-color-picker v-model="color.item" @change="changeColor($event, color)" show-alpha=""></el-color-picker>
          <el-input class="input" v-model="color.item" />
          <span class="white-space-nowrap" @click="resetBgA(color, index, key)">重置</span>
        </div>
        <div class="iconfont iconlianjie" v-if="configData.color && configData.color.length > 1"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

defineOptions({ name: 'c_bg_color' });

const props = defineProps({
  configObj: {
    type: Object,
  },
  configNme: {
    type: String,
  },
});

const defaults = ref({});
const configData = ref({});
const index = ref(0);

syncConfigData(props.configObj);

watch(
  () => props.configObj,
  (nVal, oVal) => {
    syncConfigData(nVal);
  },
  { immediate: true, deep: true },
);

function syncConfigData(configObj) {
  defaults.value = configObj || {};
  const data = getColorConfig(configObj);
  normalizeColorConfig(data);
  configData.value = data;
}

function getColorConfig(configObj) {
  if (!configObj || typeof configObj !== 'object') return {};
  if (!configObj[props.configNme] || typeof configObj[props.configNme] !== 'object') {
    configObj[props.configNme] = {
      title: '颜色设置',
    };
  }
  return configObj[props.configNme];
}

function normalizeColorConfig(data) {
  if (!data || typeof data !== 'object') return;
  if (!data.title) data.title = '颜色设置';

  let defaultList = normalizeColorList(data.default);
  let colorList = normalizeColorList(data.color);
  const valueList = normalizeColorList(data.val);
  if (!colorList.length) colorList = valueList.length ? valueList : cloneColorList(defaultList);
  if (!defaultList.length) defaultList = colorList.length ? cloneColorList(colorList) : [{ item: '#ffffff' }];
  if (!colorList.length) colorList = cloneColorList(defaultList);

  colorList = colorList.map((item, key) => {
    return {
      ...item,
      item: item.item || (defaultList[key] && defaultList[key].item) || defaultList[0].item,
    };
  });
  defaultList = colorList.map((item, key) => {
    return defaultList[key] && defaultList[key].item ? defaultList[key] : { item: item.item };
  });

  assignIfChanged(data, 'color', colorList);
  assignIfChanged(data, 'default', defaultList);
}

function normalizeColorList(value) {
  if (Array.isArray(value)) {
    return value
      .map((item) => {
        if (item && typeof item === 'object') return { ...item, item: item.item || '' };
        if (typeof item === 'string') return { item };
        return null;
      })
      .filter(Boolean);
  }
  if (value && typeof value === 'object') {
    return [{ ...value, item: value.item || '' }];
  }
  if (typeof value === 'string' && value) {
    return [{ item: value }];
  }
  return [];
}

function cloneColorList(list) {
  return JSON.parse(JSON.stringify(list || []));
}

function assignIfChanged(data, key, value) {
  if (JSON.stringify(data[key]) !== JSON.stringify(value)) {
    data[key] = value;
  }
}

function changeColor(e, color) {
  if (!e) {
    // color.item = 'transparent';
  }
  // emit('getConfig', defaults.value)
}
// 重置
function resetBgA(color, index, key) {
  const defaultColor =
    configData.value &&
    configData.value.default &&
    configData.value.default[key] &&
    configData.value.default[key].item;
  color.item = defaultColor || color.item || '#ffffff';
}
</script>

<style scoped lang="scss">
.color-box {
  position: relative;
  .iconfont {
    position: absolute;
    top: 27px;
    left: 24px;
    color: #bbbbbb;
    font-size: 21px;
  }
  .color-item {
    display: flex;
    align-items: center;
    margin-left: 15px;
    & ~ .color-item {
      margin-top: 15px;
    }
    span {
      margin-left: 15px;
      color: var(--prev-color-primary);
      font-size: 13px;
      cursor: pointer;
    }
    .input {
      margin-left: 11px;
      width: 192px;
    }
  }
  .white-space-nowrap {
    white-space: nowrap;
  }
}
.c_row-item {
  margin: 0 15px 20px 15px;
  align-items: flex-start;
  .c_label {
    font-size: 12px;
    margin-top: 8px;
  }
}
:deep(.ivu-color-picker-color ){
  width: 22px;
  height: 22px;
}
:deep(.ivu-input-icon ){
  width: 35px;
  height: 35px;
  line-height: 35px;
  font-size: 14px;
  right: -1px;
  color: #fff;
}
:deep(.ivu-input-icon-normal + .ivu-input ){
  padding-right: 4px;
}

:deep(.ivu-color-picker-color ){
  top: 0;
}
:deep(.ivu-input ){
  padding: 4px 5px;
  border: 1px solid #eee;
}
</style>
