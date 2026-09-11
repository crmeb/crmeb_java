<template>
  <div class="slider-box">
    <div class="c_row-item">
      <el-col class="label" :span="4" v-if="configData.title">
        {{ configData.title }}
      </el-col>
      <el-col :span="18">
        <el-slider
          v-model="configData.val"
          show-input
          :show-input-controls="false"
          :min="getNumberMin(configData)"
          :max="getNumberMax(configData)"
          :step="getNumberStep(configData)"
          @change="handleSliderChange"
        ></el-slider>
      </el-col>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue'
import {
  getNumberMax,
  getNumberMin,
  getNumberStep,
  normalizeNumberField
} from '@/views/design/theme_editor/utils/numberInput'

defineOptions({ name: 'c_slider' })

const props = defineProps({
  configObj: {
    type: Object
  },
  configNme: {
    type: String
  }
})

const emit = defineEmits(['getConfig'])

const defaults = ref({})
const sliderWidth = ref(0)
const configData = ref({})

onMounted(() => {
  nextTick(() => {
    defaults.value = props.configObj
    configData.value = props.configObj[props.configNme] || {}
  })
})

watch(
  () => props.configObj,
  (nVal, oVal) => {
    defaults.value = nVal
    configData.value = nVal[props.configNme] || {}
  },
  { deep: true }
)

function handleSliderChange() {
  normalizeNumberField(configData.value, 'val', configData.value)
  emit('getConfig', configData.value)
}
</script>

<style scoped lang="scss">
.c_row-item {
  margin: 0 15px 20px 15px;
  .label {
    color: #999;
    font-size: 12px;
  }
}
</style>
