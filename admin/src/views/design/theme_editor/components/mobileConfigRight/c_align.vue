<template>
  <div class="txt_tab" v-if="configData">
    <div class="c_row-item">
      <el-col class="c_label">
        {{ configData.title }}
        <span v-if="configData.val">{{ list[configData.val].val }}</span>
      </el-col>
      <div class="c_radio-group">
        <el-radio-group v-model="configData.val" type="button" @change="radioChange($event)">
          <el-radio-button :label="item.key" :value="item.key" v-for="(item, key) in list" :key="key">
            <span class="iconfont" :class="item.icon"></span>
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

defineOptions({ name: 'c_align' });

const props = defineProps({
  configObj: {
    type: Object,
  },
  configNme: {
    type: String,
  },
});

const emit = defineEmits(['getConfig']);

const defaults = ref({});
const configData = ref({});
const list = ref({
  left: {
    val: '居左',
    icon: 'iconzuoduiqi',
    key: 'left',
  },
  center: {
    val: '居中',
    icon: 'iconjuzhongduiqi',
    key: 'center',
  },
  right: {
    val: '居右',
    icon: 'iconyouduiqi',
    key: 'right',
  },
});

defaults.value = props.configObj;
configData.value = props.configObj[props.configNme] || {};

watch(
  () => props.configObj,
  (nVal, oVal) => {
    defaults.value = nVal;
    configData.value = nVal[props.configNme] || {};
  },
  { immediate: true, deep: true },
);

function radioChange(e) {
  emit('getConfig', { name: 'radio', values: e });
}
</script>

<style scoped lang="scss">
.txt_tab {
  // margin-top: 20px;
}
.c_row-item {
  margin: 0 15px 20px;
  justify-content: flex-start;
  gap: 16px;
  min-width: 0;

  .c_label {
    flex: 0 0 104px;
    white-space: nowrap;
  }
}
.c_radio-group {
  flex: 1;
  min-width: 0;
}
.row-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.iconfont {
  font-size: 18px;
}
:deep(.el-radio-group) {
  display: flex;
  flex-wrap: nowrap;
}
:deep(.el-radio-button__inner) {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 32px;
  padding: 0;
}
</style>
