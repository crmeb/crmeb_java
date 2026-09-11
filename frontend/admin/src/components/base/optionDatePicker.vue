<template>
  <div>
    <el-date-picker
      v-model="dateValue"
      align="left"
      unlink-panels
      value-format="YYYY-MM-DD"
      format="YYYY-MM-DD"
      type="daterange"
      placement="bottom-end"
      placeholder="自定义时间"
      class="selWidth"
      @change="onchangeTime"
      start-placeholder="开始时间"
      end-placeholder="结束时间"
      :shortcuts="shortcuts"
    />
  </div>
</template>
<script setup>
import { ref, watch } from 'vue'

defineOptions({ name: 'optionDatePicker' })

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue', 'changeOptTime'])

const dateValue = ref([])
const oneDay = 3600 * 1000 * 24

function offsetDate(days) {
  const date = new Date()
  date.setTime(date.getTime() - oneDay * days)
  return date
}

const shortcuts = [
  {
    text: '今天',
    value() {
      return [new Date(), new Date()]
    }
  },
  {
    text: '昨天',
    value() {
      const date = offsetDate(1)
      return [date, new Date(date)]
    }
  },
  {
    text: '最近7天',
    value() {
      const end = new Date()
      const start = offsetDate(6)
      return [start, end]
    }
  },
  {
    text: '最近30天',
    value() {
      const end = new Date()
      const start = offsetDate(29)
      return [start, end]
    }
  },
  {
    text: '上个月',
    value() {
      const now = new Date()
      const start = new Date(now.getFullYear(), now.getMonth() - 1, 1)
      const end = new Date(now.getFullYear(), now.getMonth(), 0)
      return [start, end]
    }
  },
  {
    text: '本月',
    value() {
      const now = new Date()
      const start = new Date(now.getFullYear(), now.getMonth(), 1)
      const end = new Date()
      return [start, end]
    }
  },
  {
    text: '本年',
    value() {
      const now = new Date()
      const start = new Date(now.getFullYear(), 0, 1)
      const end = new Date()
      return [start, end]
    }
  }
]

watch(
  () => props.modelValue,
  (nval) => {
    dateValue.value = nval || []
  },
  { immediate: true }
)

function onchangeTime(dateValueArg) {
  emit('update:modelValue', dateValueArg)
  emit('changeOptTime', dateValueArg)
}
</script>
<style lang="scss" scoped></style>
