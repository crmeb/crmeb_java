<template>
  <div>
    <el-date-picker
      v-model="dateValue"
      align="left"
      unlink-panels
      value-format="yyyy-MM-dd"
      format="yyyy-MM-dd"
      size="small"
      type="daterange"
      placement="bottom-end"
      placeholder="自定义时间"
      class="selWidth"
      @change="onchangeTime"
      start-placeholder="开始时间"
      end-placeholder="结束时间"
      :picker-options="pickerOptions"
    />
  </div>
</template>
<script>
export default {
  name: 'optionDatePicker',
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
              const date = new Date();
              const start = date,
                end = date;
              picker.$emit('pick', [start, end]);
            },
          },
          {
            text: '昨天',
            onClick(picker) {
              const date = new Date();
              date.setTime(date.getTime() - 3600 * 1000 * 24);
              const start = date,
                end = date;
              picker.$emit('pick', [start, end]);
            },
          },
          {
            text: '最近7天',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 6);
              picker.$emit('pick', [start, end]);
            },
          },
          {
            text: '最近30天',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 29);
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
