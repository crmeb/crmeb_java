<template>
  <!--单选框组-->
  <div class="txt_tab borderPadding" v-if="configData && configData.isShow === 1">
    <div class="c_row-item">
      <el-row class="c_label">
        <span class="labelwidth">{{ configData.title }}</span>
        <span class="labelml">{{ configData.list[configData.tabVal].val }}</span>
      </el-row>
      <el-row class="color-box labelml">
        <el-radio-group v-model="configData.tabVal" type="button" @change="radioChange($event)">
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
  name: 'c_txt_tab',
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
  methods: {
    radioChange(e) {
      //防止魔方样式一出现两张图的情况
      if (this.configData.name === 'pictureCure') {
        this.configObj['picStyle'].tabVal = 0;
      }
      if (
        this.configData.name !== 'itemStyle' &&
        this.configData.name !== 'bgStyle' &&
        this.configData.name !== 'contentStyle'
      ) {
        this.$emit('getConfig', { name: 'radio', values: e });
      }
      e !== 0 && this.configObj['checkoutConfig'] && (this.configObj['checkoutConfig']['checkoutVal'] = '1');
    },
  },
};
</script>

<style scoped lang="scss">
.c_row-item {
  margin-bottom: 20px;
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
.el-radio-group {
  display: inline-block;
  font-size: 0;
  vertical-align: middle;
  -webkit-text-size-adjust: none;
  ::v-deep.el-radio-group .el-radio:after {
    height: 0;
  }

  ::v-deep.el-radio__label {
    padding-left: 0;
  }
  ::v-deep.el-radio-group .el-radio:nth-of-type(2) {
    border-left: 1px solid #dcdee2;
  }
  ::v-deep.el-radio__inner {
    opacity: 0;
    width: 0;
    height: 0;
  }
}
::v-deep.el-radio-group .el-radio {
  display: inline-block;
  height: 32px;
  line-height: 30px;
  margin: 0;
  padding: 0 12px;
  font-size: 14px;
  color: #515a6e;
  transition: all 0.2s ease-in-out;
  cursor: pointer;
  border: 1px solid #dcdee2;
  border-left: 0;
  background: #fff;
  position: relative;
  &:first-child {
    border-radius: 4px 0 0 4px;
    border-left: 1px solid #dcdee2;
  }
}
.el-radio-group .el-radio.is-checked {
  background: #fff;
  border-color: var(--prev-color-primary);
  color: var(--prev-color-primary);
  box-shadow: -1px 0 0 0 var(--prev-color-primary);
  z-index: 1;
  &:first-child {
    border-color: var(--prev-color-primary);
    box-shadow: none;
  }
}
</style>
