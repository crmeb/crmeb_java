<template>
  <div>
    <el-date-picker
      v-model="dateValue"
      align="left"
      unlink-panels
      value-format="yyyy-MM-dd HH:mm:ss"
      size="small"
      type="datetimerange"
      placement="bottom-end"
      placeholder="自定义时间"
      class="selWidth"
      @change="onchangeTime"
      start-placeholder="开始时间"
      end-placeholder="结束时间"
      :default-time="['00:00:00', '23:59:59']"
      :picker-options="pickerOptions"
    />
  </div>
</template>
<script>
export default {
  name: 'optionDateTimePicker',
  props: {
    value: {
      type: Array,
      default: [],
    },
  },
  data() {
    return {
      dateValue: [],
      pickerOptions: {
        shortcuts: [
          {
            text: '今天',
            onClick(picker) {
              const start = new Date(),
              end = new Date();
              start.setHours(0, 0, 0)
              end.setHours(23, 59, 59)
              picker.$emit('pick', [start, end]);
            },
          },
          {
            text: '昨天',
            onClick(picker) {
              const start = new Date(),
              end = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24);
              end.setTime(end.getTime() - 3600 * 1000 * 24);
              start.setHours(0, 0, 0)
              end.setHours(23, 59, 59)
              picker.$emit('pick', [start, end]);
            },
          },
          {
            text: '最近7天',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 6);
              start.setHours(0, 0, 0)
              end.setHours(23, 59, 59)
              picker.$emit('pick', [start, end]);
            },
          },
          {
            text: '最近30天',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 29);
              start.setHours(0, 0, 0)
              end.setHours(23, 59, 59)
              picker.$emit('pick', [start, end]);
            },
          },
          {
            text: '上个月',
            onClick(picker) {
              const now = new Date();
              const start = new Date(now.getFullYear(), now.getMonth() - 1, 1);
              const end = new Date(now.getFullYear(), now.getMonth(), 0);
              picker.$emit('pick', [start, end]);
            },
          },
          {
            text: '本月',
            onClick(picker) {
              const now = new Date();
              const start = new Date(now.getFullYear(), now.getMonth(), 1);
              const end = new Date();
              picker.$emit('pick', [start, end]);
            },
          },
          {
            text: '本年',
            onClick(picker) {
              const now = new Date();
              const start = new Date(now.getFullYear(), 0, 1); 
              const end = new Date();
              picker.$emit('pick', [start, end]);
            },
          },
        ],
      },
    };
  },
  watch: {
    value(nval) {
      this.dateValue = nval
    }
  },
  created() {
    this.dateValue = this.value;
  },
  methods: {
    onchangeTime(dateValue) {
      this.$emit('changeOptTime', dateValue);
    },
  },
};
</script>
<style lang="scss" scoped></style>
