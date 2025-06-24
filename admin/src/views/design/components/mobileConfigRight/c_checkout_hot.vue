<template>
  <div class="acea-row row-between borderPadding mb20" v-if="configObj['tabConfig']['tabVal'] === 0">
    <div class="title-tips labelwidth hot-title" v-if="configData.title">
      <span>{{ configData.title }}</span>
    </div>
    <div class="radio-box labelml">
      <template v-for="(item, index) in configData.list">
        <el-radio v-model="configData.checkoutVal" :label="item.label">{{ item.value }}</el-radio>
      </template>
    </div>
  </div>
</template>

<script>
export default {
  name: 'c_checkout_hot',
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
      },
      deep: true,
    },
    'configData.checkoutVal'(nVal) {
      if (nVal !== '2') {
        this.configData.hotspot = [];
      }
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
.checkout-hot-box {
  .checkout-hot-radio {
    display: flex;
    justify-content: space-between;
  }
}
.hot-title {
  color: #333;
  font-size: 14px;
  font-family: PingFang SC-Regular, PingFang SC;
  font-weight: 400;
}
</style>
