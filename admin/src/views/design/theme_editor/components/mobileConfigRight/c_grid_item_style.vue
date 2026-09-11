<template>
  <div class="grid-item-style-config">
    <div class="config-title">{{ configData.title }}</div>

    <!-- 内容间距 -->
    <div class="config-item">
      <span class="item-label">内容间距</span>
      <div class="slider-container">
        <el-slider
          v-model="configData.itemPadding"
          show-input
          :show-input-controls="false"
          :min="0"
          :step="1"
          @input="handleChange"
          @change="handleSliderChange('itemPadding')"
        ></el-slider>
      </div>
    </div>
    <!-- 上下间距 -->
    <div class="config-item">
      <span class="item-label">上下内边距</span>
      <div class="slider-container">
        <el-slider
          v-model="configData.itemPaddingTop"
          show-input
          :show-input-controls="false"
          :min="0"
          :step="1"
          @input="handleChange"
          @change="handleSliderChange('itemPaddingTop')"
        ></el-slider>
      </div>
    </div>
    <!-- 背景色 -->
    <div class="config-item">
      <span class="item-label">背景色</span>
      <!-- <el-color-picker v-model="configData.itemBgColor" @change="handleChange"></el-color-picker> -->
      <div class="row slider-container">
        <el-color-picker v-model="configData.itemBgColor" @change="handleChange" show-alpha></el-color-picker>
        <el-input
          v-model="configData.itemBgColor"
          placeholder="请输入颜色"
          @change="handleChange"
          style="margin-left: 10px; flex: 1"
        ></el-input>
        <span class="reset-btn" @click="resetItemBgColor">重置</span>
      </div>
    </div>
    <!-- 圆角 -->
    <div class="config-item">
      <span class="item-label">圆角</span>
      <div class="slider-container">
        <el-slider
          v-model="configData.itemRadius"
          show-input
          :show-input-controls="false"
          :min="0"
          :step="1"
          @input="handleChange"
          @change="handleSliderChange('itemRadius')"
        ></el-slider>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { normalizeNumberField } from '@/views/design/theme_editor/utils/numberInput'

defineOptions({ name: 'c_grid_item_style' })

const props = defineProps({
  configNme: {
    type: String
  },
  configObj: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['getConfig'])

const defaultConfig = {
  title: '宫格项样式',
  itemPadding: 8,
  itemBgColor: '#ffffff',
  itemRadius: 8,
  itemPaddingTop: 0
}
const configData = ref({})

watch(
  () => props.configObj,
  (nVal, oVal) => {
    configData.value = Object.assign({}, defaultConfig, nVal[props.configNme])
  },
  { deep: true, immediate: true }
)

function handleChange() {
  // 将修改写回 configObj，触发父组件的 deep watch -> store 更新 -> 预览刷新
  if (props.configObj && props.configObj[props.configNme]) {
    const target = props.configObj[props.configNme]
    target.itemPadding = configData.value.itemPadding
    target.itemPaddingTop = configData.value.itemPaddingTop
    target.itemBgColor = configData.value.itemBgColor
    target.itemRadius = configData.value.itemRadius
  }
  emit('getConfig', configData.value)
}

function handleSliderChange(key) {
  normalizeNumberField(configData.value, key, { min: 0 })
  handleChange()
}

function resetItemBgColor() {
  configData.value.itemBgColor = '#fff'
  handleChange()
}
</script>

<style lang="scss" scoped>
.grid-item-style-config {
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;

  .config-title {
    font-size: 14px;
    font-weight: 500;
    color: #333;
    margin-bottom: 12px;
  }

  .config-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    .slider-container {
      width: 75%;
    }
    &:last-child {
      margin-bottom: 0;
    }

    .item-label {
      font-size: 13px;
      color: #999;
    }
  }
  .row {
    display: flex;
    align-items: center;
    position: relative;
    .reset-btn {
      cursor: pointer;
      font-size: 12px;
      margin-left: 10px;
      color: var(--prev-color-primary);
      &:hover {
        color: var(--prev-color-primary-light-1);
      }
    }
  }
}
</style>
