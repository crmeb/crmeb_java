<template>
  <!--单选框组-->
  <div class="txt_tab borderPadding" v-if="configData && configData.isShow === 1">
    <div class="radio_row-item">
      <el-row class="c_label">
        <span class="labelwidth">{{ configData.title }}</span>
        <!-- <span class="labelml">{{ configData.list[configData.tabVal].val }}</span> -->
      </el-row>
      <el-row class="ml22">
        <el-radio-group v-model="configData.tabVal" type="button">
          <el-radio :label="key" v-for="(radio, key) in configData.list" :key="key">
            <span class="iconfont-diy iconfont" :class="radio.icon" v-if="radio.icon"></span>
            <span v-else>{{ radio.val }}</span>
          </el-radio>
        </el-radio-group>
      </el-row>
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
  name: 'c_radio',
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
  created() {
    this.defaults = this.configObj;
    this.configData = this.configObj[this.configNme];
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.defaults = nVal;
        this.configData = nVal[this.configNme];
      },
      immediate: true,
      deep: true,
    },
  },
  methods: {},
};
</script>

<style scoped lang="scss">
.radio_row-item {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.row-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.iconfont {
  font-size: 24px;
}

.color-box {
  position: relative;
  max-width: 100%;
  min-height: 1px;
}
</style>
