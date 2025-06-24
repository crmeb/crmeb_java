<template>
  <!--input输入框，选择链接-->
  <div class="box borderPadding" v-if="configData && configData.isShow === 1">
    <div class="c_row-item">
      <div class="title labelwidth">
        {{ configData.title }}
      </div>
      <div class="slider-box ml22">
        <div @click="getLink(configData.title)">
          <el-input
            size="small"
            icon="ios-arrow-forward"
            :icon="configData.title === '链接' || configData.title === '更多链接' ? 'ios-arrow-forward' : ''"
            :readonly="configData.title === '链接' || configData.title === '更多链接' ? true : false"
            v-model="configData.val"
            :placeholder="configData.place"
            :maxlength="configData.max"
          >
            <el-button
              v-if="configData.title === '链接' || configData.title === '更多链接'"
              slot="append"
              icon="el-icon-arrow-right"
            ></el-button>
            <span slot="suffix">{{ configData.unit || '' }}</span>
          </el-input>
        </div>
      </div>
    </div>
    <linkaddress ref="linkaddres" @linkUrl="linkUrl"></linkaddress>
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
import linkaddress from '@/components/linkaddress';
export default {
  name: 'c_input_item',
  props: {
    configObj: {
      type: Object,
    },
    configNme: {
      type: String,
    },
  },
  components: {
    linkaddress,
  },
  data() {
    return {
      value: '',
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
    linkUrl(e) {
      this.configData.val = e;
      this.$emit('getConfig', { name: 'c_input_item', values: this.configData.val });
    },
    getLink(title) {
      if (title !== '链接' && title !== '更多链接') {
        return;
      }
      this.$refs.linkaddres.dialogVisible = true;
    },
  },
};
</script>

<style scoped lang="scss">
.title {
  font-size: 12px;
  color: #999;
}
.slider-box {
  width: 81%;
}
::v-deep .el-input--medium,
::v-deep.ivu-input {
  font-size: 12px !important;
}
.c_row-item {
  margin-bottom: 20px;
  margin-top: 20px;
}
.slider-box {
  ::v-deep.el-input__suffix {
    top: 10px !important;
  }
}

.label {
  font-size: 12px;
  font-family: PingFang SC-Regular, PingFang SC;
  font-weight: 400;
  color: #999999;
}
</style>
