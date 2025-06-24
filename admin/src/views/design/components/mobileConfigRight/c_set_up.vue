<template>
  <div class="setUp borderPadding">
    <template>
      <el-tabs v-model="configData.tabVal">
        <el-tab-pane label="内容设置" />
        <el-tab-pane label="样式设置" />
      </el-tabs>
    </template>
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
  name: 'c_set_up',
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
      defaults: {},
      configData: {},
    };
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.defaults = nVal;
        this.configData = nVal[this.configNme];
      },
      deep: true,
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.defaults = this.configObj;
      this.configData = this.configObj[this.configNme];
    });
  },
};
</script>

<style scoped lang="scss">
.setUp ::v-deep.ivu-tabs-nav-scroll {
  padding: 0 30px;
}
.setUp ::v-deep.ivu-tabs-nav .ivu-tabs-tab {
  padding: 8px 45px;
}
</style>
