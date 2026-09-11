<template>
  <div>
    <el-date-picker
      v-model="dateValue"
      align="left"
      unlink-panels
      value-format="YYYY-MM-DD HH:mm:ss"
      type="datetimerange"
      placement="bottom-end"
      placeholder="自定义时间"
      class="selWidth"
      @change="onchangeTime"
      start-placeholder="开始时间"
      end-placeholder="结束时间"
      :default-time="defaultTime"
      :shortcuts="shortcuts"
    />
  </div>
</template>
<script setup>
import { ref, watch } from 'vue'

defineOptions({ name: 'optionDateTimePicker' })

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue', 'changeOptTime'])

const dateValue = ref([])
const oneDay = 3600 * 1000 * 24
// Element Plus 的 default-time 需传入 Date 对象
const defaultTime = [new Date(2000, 0, 1, 0, 0, 0), new Date(2000, 0, 1, 23, 59, 59)]
function startOfDay(date) {
  date.setHours(0, 0, 0, 0)
  return date
}

function endOfDay(date) {
  date.setHours(23, 59, 59, 999)
  return date
}

function offsetDate(days) {
  const date = new Date()
  date.setTime(date.getTime() - oneDay * days)
  return date
}

const shortcuts = [
  {
    text: '今天',
    value() {
      return [startOfDay(new Date()), endOfDay(new Date())]
    }
  },
  {
    text: '昨天',
    value() {
      return [startOfDay(offsetDate(1)), endOfDay(offsetDate(1))]
    }
  },
  {
    text: '最近7天',
    value() {
      const end = new Date()
      const start = offsetDate(6)
      return [startOfDay(start), endOfDay(end)]
    }
  },
  {
    text: '最近30天',
    value() {
      const end = new Date()
      const start = offsetDate(29)
      return [startOfDay(start), endOfDay(end)]
    }
  },
  {
    text: '上个月',
    value() {
      const now = new Date()
      const start = startOfDay(new Date(now.getFullYear(), now.getMonth() - 1, 1))
      const end = endOfDay(new Date(now.getFullYear(), now.getMonth(), 0))
      return [start, end]
    }
  },
  {
    text: '本月',
    value() {
      const now = new Date()
      const start = startOfDay(new Date(now.getFullYear(), now.getMonth(), 1))
      const end = endOfDay(new Date())
      return [start, end]
    }
  },
  {
    text: '本年',
    value() {
      const now = new Date()
      const start = startOfDay(new Date(now.getFullYear(), 0, 1))
      const end = endOfDay(new Date())
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
