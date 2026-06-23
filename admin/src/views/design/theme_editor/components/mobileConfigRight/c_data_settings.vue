<template>
  <div class="mobile-config-component" v-if="configData">
    <div class="title-bar">{{ configData.title }}</div>
    <div class="box-content">
      <c_bg_color :configObj="configData" configNme="originalPriceColor" />
      <c_bg_color :configObj="configData" configNme="stockColor" />
      <c_bg_color :configObj="configData" configNme="salesColor" />
    </div>
  </div>
</template>

<script>
import toolCom from '@/views/design/theme_editor/components/mobileConfigRight/index.js';
import c_bg_color from './c_bg_color';

export default {
  name: 'c_data_settings',
  components: {
    ...toolCom,
    c_bg_color,
  },
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
  margin: 15px 0;
  .title-bar {
    font-size: 14px;
    color: #333;
    padding: 0 15px;
    margin-bottom: 15px;
  }
  .box-content {
    .box-item {
      padding: 0 15px;
      display: flex;
      align-items: center;
      margin-bottom: 15px;
      .label {
        width: 80px;
        font-size: 13px;
        color: #999;
      }
      .input-box {
        flex: 1;
      }
    }
  }
}
</style>
