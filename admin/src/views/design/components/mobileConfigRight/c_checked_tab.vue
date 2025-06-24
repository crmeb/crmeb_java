<template>
  <!--右侧头部组件，标题，按钮选择框-->
  <div class="acea-row row-between mobile-config">
    <div class="title-tips" v-if="configData && configData.title">
      <span>{{ configData.title }}</span>
    </div>
    <div v-show="configData.cname" class="title-bar">{{ configData.cname }}</div>
    <div v-if="configData.tabVal < 2" class="radio-boxs acea-row">
      <div
        v-for="(item, index) in list"
        :key="index"
        :class="actived === index ? 'active' : ''"
        class="btn"
        @click="onClick(index)"
      >
        {{ item }}
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
  name: 'c_checked_tab',
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
      list: ['内容', '样式'],
      actived: 0,
      formData: {
        type: 0,
      },
      defaults: {},
      configData: {},
    };
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.defaults = nVal;
        this.configData = nVal[this.configNme];
        this.actived = this.configData.tabVal;
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
  methods: {
    onClick(index) {
      this.actived = index;
      this.configData.tabVal = this.actived;
      this.$emit('getConfig', { name: 'checkbox_tab', values: index });
    },
  },
};
</script>

<style scoped lang="scss">
.mobile-config {
  height: 63px;
  padding: 0 15px;
  line-height: 63px;
  .title-bar {
    font-size: 16px;
    color: #333;
    font-family: PingFang SC-Medium, PingFang SC;
    font-weight: 600;
  }
}
.radio-boxs {
  width: 140px;
  height: 28px;
  cursor: pointer;
  background: #f9f9f9;
  border-radius: 14px 14px 14px 14px;
  margin-top: 17px;
  opacity: 1;
  font-size: 12px;
  color: #333333;
  .btn {
    width: 70px;
    height: 28px;
    text-align: center;
    line-height: 28px;
  }
  .active {
    background: var(--prev-color-primary);
    border-radius: 14px 14px 14px 14px;
    opacity: 1;
    color: #fff;
  }
}
</style>
