<template>
  <!--单选框组-->
  <div class="acea-row row-between borderPadding mb20">
    <div class="title-tips labelwidth" v-if="configData.title">
      <span>{{ configData.title }}</span
      >{{ configData.list[configData.tabVal].val }}
    </div>
    <div class="radio-box labelml" :class="{ on: configData.type == 1 }">
      <el-radio-group v-model="configData.tabVal" size="large" @change="radioChange($event)">
        <el-radio-button :label="index" v-for="(item, index) in configData.list" :key="index">
          <span class="iconfont" :class="item.icon" v-if="item.icon"></span>
          <span v-else>{{ item.name }}</span>
        </el-radio-button>
      </el-radio-group>
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
  name: 'c_tab',
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
  },
  mounted() {
    this.$nextTick(() => {
      this.defaults = this.configObj;
      this.configData = this.configObj[this.configNme];
    });
  },
  methods: {
    radioChange(e) {
      if (this.defaults.picStyle) {
        this.defaults.picStyle.tabVal = '0';
      }
      this.$emit('getConfig', e);
    },
  },
};
</script>

<style scoped lang="scss">
::v-deep.el-radio-group .el-radio:after {
  height: 0;
}
::v-deep.el-radio-group .el-radio {
  height: 34px;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease-in-out;
  cursor: pointer;
  border: 1px solid #dcdee2;
  position: relative;
  margin: 0;
  padding: 0 15px;
}
::v-deep.el-radio__label {
  padding-left: 0;
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
::v-deep.el-radio-group .el-radio:nth-of-type(2) {
  border-left: 1px solid #dcdee2;
}
::v-deep.el-radio__inner {
  opacity: 0;
  width: 0;
  height: 0;
}
.radio-box {
  ::v-deep.el-radio-group {
    display: flex;
    width: 100%;
    flex-wrap: wrap;
    .ivu-radio-wrapper {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
  &.on {
    ::v-deep.el-radio {
      flex: unset;
      width: 67px;
      margin-right: 20px;
      margin-top: 10px;
      border-radius: 4px;
    }
  }
}
.title-tips {
  padding-bottom: 10px;
  font-size: 14px;
  color: #333;
  span {
    color: #999;
  }
}
.iconfont-diy,
.iconfont {
  font-size: 20px;
  line-height: 18px;
}
</style>
