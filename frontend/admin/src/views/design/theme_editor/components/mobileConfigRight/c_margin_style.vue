<template>
  <div class="margin-style-config" v-if="configData">
    <div class="c_row">
      <div class="c_label">{{ configData.title }}</div>
      <div class="c_content">
        <div class="main-setting">
          <el-slider
            v-model="configData.val"
            show-input
            :show-input-controls="false"
            :min="getNumberMin(configData)"
            :max="getNumberMax(configData)"
            :step="getNumberStep(configData)"
            @change="normalizeMainValue"
          ></el-slider>
          <div class="expand-icon" :class="configData.isAll ? 'selected' : ''" @click="toggleExpand">
            <span class="iconfont iconbianju"></span>
          </div>
        </div>
        <div class="sub-settings" v-if="configData.isAll">
          <div class="sub-item" v-for="(item, index) in configData.valList" :key="index">
            <div class="input-box">
              <span class="prefix-icon iconfont" :class="getIcon(index)"></span>
              <el-input-number
                v-model="item.val"
                :min="getNumberMin(configData)"
                :max="getNumberMax(configData)"
                :step="getNumberStep(configData)"
                :step-strictly="true"
                :controls="false"
                @change="normalizeSubValue(item)"
              ></el-input-number>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import {
  getNumberMax,
  getNumberMin,
  getNumberStep,
  normalizeNumberField
} from '@/views/design/theme_editor/utils/numberInput'

defineOptions({ name: 'c_margin_style' })

const props = defineProps({
  configObj: {
    type: Object
  },
  configNme: {
    type: String
  }
})

const configData = ref(null)

watch(
  () => props.configObj,
  (nVal, oVal) => {
    if (!nVal) return
    configData.value = nVal[props.configNme] || {}
  },
  { deep: true, immediate: true }
)

watch(
  () => configData.value && configData.value.val,
  (nVal) => {
    if (configData.value && configData.value.valList) {
      configData.value.valList.forEach((item) => {
        item.val = nVal
      })
    }
  }
)

watch(
  () => configData.value && configData.value.isAll,
  (nVal) => {
    if (configData.value && configData.value.valList) {
      configData.value.valList.forEach((item) => {
        item.val = configData.value.val
      })
    }
  }
)

function toggleExpand() {
  if (!configData.value) return
  configData.value.isAll = !configData.value.isAll
}

function normalizeMainValue() {
  normalizeNumberField(configData.value, 'val')
}

function normalizeSubValue(item) {
  normalizeNumberField(item, 'val', configData.value)
}

function getIcon(index) {
  const icons = ['iconshangbianju', 'iconyoubianju', 'iconxiabianju', 'iconzuobianju']
  const paddingIcons = ['iconneibianju-shang', 'iconneibianju-you', 'iconneibianju-xia', 'iconneibianju-zuo']
  return props.configNme === 'paddingConfig' ? paddingIcons[index] || '' : icons[index] || ''
}
</script>

<style scoped lang="scss">
.margin-style-config {
  padding: 0px 15px 0 15px;
  .c_row {
    display: flex;
    justify-content: space-between;
  }
  .c_label {
    font-size: 12px;
    color: #999;
    line-height: 38px;
    margin-bottom: 10px;
    white-space: nowrap;
  }
  .c_content {
    width: 75%;
  }
  .main-setting {
    display: flex;
    align-items: center;
    .expand-icon {
      margin-left: 10px;
      cursor: pointer;
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 1px solid #dcdfe6;
      border-radius: 4px;
      color: #606266;
      &:hover {
        color: #409eff;
        border-color: #c6e2ff;
      }
      .iconfont {
        font-size: 18px;
      }
    }
    .expand-icon.selected {
      color: #409eff;
      border-color: #c6e2ff;
    }
    :deep(.el-slider) {
      flex: 1;
      margin-right: 0;
      .el-slider__input {
        width: 90px;
      }
    }
  }
  .sub-settings {
    display: flex;
    flex-wrap: wrap;
    margin-top: 10px;
    margin-right: -10px;
    .sub-item {
      width: 50%;
      padding-right: 10px;
      margin-bottom: 10px;
      box-sizing: border-box;
      .input-box {
        display: flex;
        align-items: center;
        border: 1px solid #dcdfe6;
        border-radius: 4px;
        padding-left: 5px;
        &:hover {
          border-color: #c0c4cc;
        }
        .prefix-icon {
          color: #909399;
          font-size: 14px;
          margin-right: 5px;
        }
        :deep(.el-input-number) {
          width: 100%;
          border: none;
          .el-input__wrapper {
            box-shadow: none;
            border: none;
            padding: 0;
            background: transparent;
          }
          .el-input__inner {
            border: none;
            padding-left: 5px;
            text-align: left;
          }
        }
      }
    }
  }
}
</style>
