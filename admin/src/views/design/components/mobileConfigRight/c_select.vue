<template>
  <!--下拉框-->
  <div class="borderPadding" v-if="configData && configData.isShow === 1">
    <div class="c_row-item">
      <div class="label labelwidth" v-if="configData.title">
        {{ configData.title }}
      </div>
      <div class="slider-box ml22">
        <el-select
          size="small"
          v-model="configData.activeValue"
          :multiple="configData.isMultiple"
          @change="sliderChange"
          style="width: 100%"
        >
          <el-option
            v-for="(item, index) in configData.list"
            :value="item.id"
            :label="item.name"
            :key="index"
            :disabled="!item.isShow && !item.status"
          ></el-option>
        </el-select>
      </div>
    </div>
  </div>
</template>

<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
export default {
  name: 'c_select',
  props: {
    configObj: {
      type: Object,
    },
    configNme: {
      type: String,
    },
    number: {
      type: null,
    },
  },
  data() {
    return {
      defaults: {},
      configData: {},
      timeStamp: '',
      multiple: false,
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.defaults = this.configObj;
      this.configData = this.configObj[this.configNme];
    });
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.defaults = nVal;
        this.configData = nVal[this.configNme];
      },
      deep: true,
    },
    number(nVal) {
      this.timeStamp = nVal;
    },
  },
  methods: {
    sliderChange(e) {
      let storage = window.localStorage;
      this.configData.activeValue = e ? e : storage.getItem(this.timeStamp);
      this.$emit('getConfig', { name: 'select', values: e });
    },
  },
};
</script>

<style scoped lang="scss">
::v-deep.el-input--small {
  font-size: 12px !important;
}
.label {
  font-size: 12px;
  color: #999;
}
.slider-box {
  width: 81%;
}
.c_row-item {
  margin-bottom: 20px;
}
</style>
