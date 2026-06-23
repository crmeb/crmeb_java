<template>
  <div class="mobile-config-component" v-if="configData">
    <div class="c_row-item">
      <el-col class="label" :span="4">
        {{ configData.title }}
      </el-col>
      <el-col :span="18" class="slider-box">
        <el-date-picker
          v-model="configData.val"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy/MM/dd"
          size="mini"
          style="width: 100%"
        >
        </el-date-picker>
      </el-col>
    </div>
  </div>
</template>

<script>
export default {
  name: 'c_datetime_picker',
  props: {
    configObj: {
      type: Object,
    },
    configNme: {
      type: String,
    },
  },
  data() {
    return {
      configData: null,
    };
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.configData = nVal[this.configNme];
      },
      deep: true,
      immediate: true,
    },
  },
};
</script>

<style scoped lang="scss">
.mobile-config-component {
  padding: 0 15px;
  margin-bottom: 10px;
  // border-bottom: 6px solid rgb(240, 242, 245);

  .label {
    color: #999;
    font-size: 12px;
    margin-right: 15px;
  }
}
</style>
