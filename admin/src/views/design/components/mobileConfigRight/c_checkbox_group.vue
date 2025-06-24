<template>
  <!--多选框组-->
  <div class="acea-row borderPadding row-middle" style="margin-bottom: 20px">
    <div class="title-tips labelwidth" v-if="configData.title">
      <span>{{ configData.title }}</span>
    </div>
    <div class="radio-boxs ml22">
      <el-checkbox-group v-model="configData.activeValue" size="large" @change="radioChange($event)">
        <el-checkbox :label="index" v-for="(item, index) in configData.list" :key="index">
          {{ item.val }}
        </el-checkbox>
      </el-checkbox-group>
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
  name: 'c_checkbox_group',
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
      this.$emit('getConfig', { name: 'checkbox_group', values: e });
    },
  },
};
</script>

<style scoped lang="scss">
.radio-boxs {
  ::v-deep.el-checkbox__label {
    font-size: 14px !important;
  }
}
.title-tips {
  font-size: 12px;
  color: #999;
}
</style>
