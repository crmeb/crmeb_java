<template>
  <!--级联下拉框-->
  <div class="borderPadding mb20">
    <div class="c_row-item" v-if="configData && configData.isShow === 1">
      <div class="label labelwidth">
        {{ configData.title }}
      </div>
      <div class="slider-box ml22">
        <el-cascader
          style="width: 100%"
          :options="configData.list"
          :placeholder="`请选择${configData.title}`"
          v-model="configData.activeValue[configData.tabVal]"
          filterable
          :props="props"
          clearable
          :show-all-levels="false"
          @change="sliderChange"
        ></el-cascader>
      </div>
    </div>
  </div>
</template>

<script>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
export default {
  name: 'c_cascader',
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
      props: {
        value: 'id',
        label: 'name',
        children: 'child',
        expandTrigger: 'hover',
        checkStrictly: false,
        multiple: true,
        emitPath: false,
      },
      defaults: {},
      configData: {},
      timeStamp: '',
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
      // this.configData.activeValue = e ? e : z;
      this.$emit('getConfig', { name: 'cascader', values: e });
    },
  },
};
</script>

<style scoped lang="scss">
.label {
  font-size: 12px;
  color: #999;
}

.slider-box {
  width: 81%;
}
::v-deep .el-cascader__search-input {
  background-color: #fff !important;
}
</style>
