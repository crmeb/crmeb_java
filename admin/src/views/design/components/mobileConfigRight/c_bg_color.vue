<template>
  <!--颜色组件-->
  <div
    class="borderPadding"
    v-if="(configData.isShow && configData.isShow == 1) || (!configData.isShow && configData.isShow != 0)"
  >
    <div class="acea-row row-middle mb20" v-if="configData">
      <div class="c_label labelwidth">{{ configData.title }}</div>
      <div class="color-box ml22">
        <div class="color-item acea-row row-middle" v-for="(color, key) in configData.color" :key="key">
          <el-color-picker
            v-model="color.item"
            @change="changeColor($event, color)"
            alpha
            style="vertical-align: middle"
            show-alpha
          ></el-color-picker>
          <el-input size="small" v-model="color.item" style="width: 78%" class="ml10"></el-input>
          <span class="reset-theme" @click="resetBgA(color, index, key)">重置</span>
          <i v-show="key === 0 && configData.color.length > 1" class="iconfont iconjianbianlianjie"></i>
        </div>
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
let restColor = '';
export default {
  name: 'c_bg_color',
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
      bgColor: {
        bgStar: '',
        bgEnd: '',
      },
      oldColor: {
        bgStar: '',
        bgEnd: '',
      },
      index: 0,
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
  methods: {
    changeColor(e, color) {
      if (!e) {
        color.item = 'rgba(255, 255, 255, 0)';
      }
    },
    // 重置
    resetBgA(color, index, key) {
      color.item = this.configData.default[key].item;
    },
  },
};
</script>

<style scoped lang="scss">
.iconfont {
  margin-left: 7px;
  font-size: 24px;
  margin-top: -7px;
  z-index: 1;
  color: #bbbbbb;
  margin-bottom: -7px;
}

.c_row-item {
  margin-bottom: 20px;
}

.color-box {
  width: 79%;

  .color-item {
    span {
      margin-left: 10px;
      color: #1890ff;
      font-size: 13px;
      cursor: pointer;
    }
  }
}
</style>
