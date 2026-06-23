<template>
  <div class="mobile-config-component" v-if="configData">
    <div class="title-bar">{{ configData.title }}</div>
    <div class="box-content">
      <!-- Color Tone -->
      <div class="box-item">
        <span class="label">{{ configData.colorTone.title }}</span>
        <div class="input-box">
          <el-radio-group v-model="configData.colorTone.tabVal">
            <el-radio :label="item.val" v-for="(item, index) in configData.colorTone.tabList" :key="index">
              {{ item.name }}
            </el-radio>
          </el-radio-group>
        </div>
      </div>

      <!-- Custom Colors -->
      <div v-if="configData.colorTone.tabVal === 1">
        <c_bg_color :configObj="configData" configNme="finalPriceColor" />
        <c_bg_color :configObj="configData" configNme="sellingPriceColor" />
      </div>

      <!-- Price Font Size -->
      <c_slider :configObj="configData" configNme="priceFontSize" />
    </div>
  </div>
</template>

<script>
import toolCom from '@/views/design/theme_editor/components/mobileConfigRight/index.js';
import c_bg_color from './c_bg_color';
import c_slider from './c_slider';

export default {
  name: 'c_price_settings',
  components: {
    ...toolCom,
    c_bg_color,
    c_slider,
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
  border-bottom: 6px solid rgb(240, 242, 245);
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
        ::v-deep .el-radio {
          margin-bottom: 0px;
          margin-right: 15px;
        }
      }
    }
  }
}
</style>
