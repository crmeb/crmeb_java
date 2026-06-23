<template>
  <div class="grid-item-style-config">
    <div class="config-title">{{ configData.title }}</div>

    <!-- 左右间距 -->
    <div class="config-item">
      <span class="item-label">左右内边距</span>
      <div class="slider-container">
        <el-slider v-model="configData.itemPadding" show-input :min="0"></el-slider>
      </div>
    </div>
    <!-- 上下间距 -->
    <div class="config-item">
      <span class="item-label">上下内边距</span>
      <div class="slider-container">
        <el-slider v-model="configData.itemPaddingTop" show-input :min="0"></el-slider>
      </div>
    </div>
    <!-- 背景色 -->
    <div class="config-item">
      <span class="item-label">背景色</span>
      <!-- <el-color-picker v-model="configData.itemBgColor" @change="handleChange" size="small"></el-color-picker> -->
      <div class="row slider-container">
        <el-color-picker v-model="configData.itemBgColor" @change="handleChange" show-alpha></el-color-picker>
        <el-input
          v-model="configData.itemBgColor"
          placeholder="请输入颜色"
          @change="handleChange"
          style="margin-left: 10px; flex: 1"
        ></el-input>
        <span
          class="reset-btn"
          @click="
            configData.itemBgColor = '#fff';
            handleChange();
          "
          >重置</span
        >
      </div>
    </div>
    <!-- 圆角 -->
    <div class="config-item">
      <span class="item-label">圆角</span>
      <div class="slider-container">
        <el-slider v-model="configData.itemRadius" show-input :min="0"></el-slider>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'c_grid_item_style',
  props: {
    configNme: {
      type: String,
    },
    configObj: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      configData: {
        title: '宫格项样式',
        itemPadding: 8,
        itemBgColor: '#ffffff',
      },
    };
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.configData = nVal[this.configNme] || {
          title: '宫格项样式',
          itemPadding: 8,
          itemBgColor: '#ffffff',
        };
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    handleChange() {
      this.$emit('getConfig', this.configData);
    },
  },
};
</script>

<style lang="scss" scoped>
.grid-item-style-config {
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;

  .config-title {
    font-size: 14px;
    font-weight: 500;
    color: #333;
    margin-bottom: 12px;
  }

  .config-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    .slider-container {
      width: 75%;
    }
    &:last-child {
      margin-bottom: 0;
    }

    .item-label {
      font-size: 13px;
      color: #999;
    }
  }
  .row {
    display: flex;
    align-items: center;
    position: relative;
    .reset-btn {
      cursor: pointer;
      font-size: 12px;
      margin-left: 10px;
      color: var(--prev-color-primary);
      &:hover {
        color: var(--prev-color-primary-light-1);
      }
    }
  }
}
</style>
