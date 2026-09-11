<template>
  <div class="checkboxs" v-if="hasConfig">
    <div class="c_row-item">
      <el-col class="title-tips" :span="4">
        <span>{{ configData.title }}</span>
      </el-col>
      <el-col class="checkbox-box" :span="18">
        <el-checkbox-group v-model="configData.type" @change="checkboxChange()">
          <el-checkbox
            :label="item.id"
            :value="item.id"
            :disabled="isDisabled(item)"
            v-for="(item, index) in visibleCheckboxList"
            :key="index"
          >
            <span>{{ item.name }}</span>
          </el-checkbox>
        </el-checkbox-group>
      </el-col>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, nextTick } from 'vue';

defineOptions({ name: 'c_checkbox' });

const props = defineProps({
  configObj: {
    type: Object,
  },
  configNme: {
    type: String,
  },
});

const formData = ref({
  type: 0,
});
const defaults = ref({});
const configData = ref({});
const selectedData = ref([]);
const userStyle = ref(0);

const hasConfig = computed(() => {
  return configData.value && Array.isArray(configData.value.type) && Array.isArray(configData.value.list);
});
const visibleCheckboxList = computed(() => {
  const list = Array.isArray(configData.value.list) ? configData.value.list : [];
  return list.filter((item) => item && !isHiddenCheckboxItem(item));
});

function isHiddenCheckboxItem(item) {
  if (item.name === '会员价格') return true;
  if (props.configNme === 'checkboxInfo' && defaults.value.name === 'goodList' && item.name === '商品标签') return true;
  return false;
}

watch(
  () => props.configObj,
  (nVal, oVal) => {
    setConfig(nVal);
  },
  { deep: true, immediate: true },
);

watch(
  () => props.configObj.styleConfig?.tabVal,
  (nVal, oVal) => {
    if (configData.value && configData.value.userType) {
      configData.value.type = [3, 1, 2];
    }
  },
);

watch(
  () => props.configObj.storeStyleConfig?.tabVal,
  (nVal, oVal) => {
    if (configData.value.storeType) {
      if (nVal == 1) {
        configData.value.list = [
          {
            id: 0,
            name: '配送方式',
          },
          {
            id: 2,
            name: '门店距离',
          },
          {
            id: 3,
            name: '门店地址',
          },
        ];
      } else {
        configData.value.list = [
          {
            id: 0,
            name: '配送方式',
          },
          {
            id: 1,
            name: '营业时间',
          },
          {
            id: 2,
            name: '门店距离',
          },
          {
            id: 3,
            name: '门店地址',
          },
        ];
      }
    }
  },
);

onMounted(() => {
  nextTick(() => {
    setConfig(props.configObj);
  });
});

function setConfig(configObj) {
  defaults.value = configObj || {};
  const configDataVal = defaults.value && props.configNme ? defaults.value[props.configNme] : null;
  if (!configDataVal) {
    configData.value = {
      type: [],
      list: [],
    };
    userStyle.value = 0;
    selectedData.value = [];
    return;
  }
  if (!Array.isArray(configDataVal.type)) {
    configDataVal.type = configDataVal.type === undefined || configDataVal.type === null ? [] : [configDataVal.type];
  }
  if (!Array.isArray(configDataVal.list)) {
    configDataVal.list = [];
  }
  configData.value = configDataVal;
  userStyle.value = (defaults.value.styleConfig && defaults.value.styleConfig.tabVal) || 1;
  selectedData.value = (defaults.value.checkboxInfo && defaults.value.checkboxInfo.type) || [];
}
function isDisabled(item) {
  const type = configData.value.type || [];
  const maxList = Number(configData.value.maxList) || 0;
  return (
    (selectedData.value.length >= 3 && userStyle.value && configData.value.userType && !selectedData.value.includes(item.id)) ||
    (type.length >= 3 && configData.value.name == 'showContent' && !type.includes(item.id)) ||
    (maxList > 0 && type.length >= maxList && !type.includes(item.id))
  );
}
function checkboxChange(e) {
  // emit('getConfig', e);
}
</script>

<style scoped lang="scss">
.checkboxs {
  padding: 0 15px;
  margin-bottom: 20px;
  .c_row-item {
    align-items: flex-start;
  }
}
.title-tips {
  color: #999;
  font-size: 12px;
  line-height: 32px;
}
.checkbox-box {
  min-width: 0;
}
:deep(.el-checkbox-group) {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  column-gap: 12px;
  row-gap: 4px;
}
:deep(.el-checkbox) {
  width: calc((100% - 24px) / 3);
  height: 28px;
  margin-right: 0;
}
:deep(.el-checkbox__label) {
  font-size: 12px;
  white-space: nowrap;
}
</style>
