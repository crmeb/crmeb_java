<template>
  <!--商户选择框-->
  <div class="mb20 borderPadding" v-if="configData.isShow === 1">
    <div class="c_row-item">
      <div class="label labelwidth" v-if="configData.title">
        {{ configData.title }}
      </div>
      <div class="slider-box ml22">
        <el-cascader
          v-model="merIds"
          class="selWidth"
          :show-all-levels="false"
          :options="merchantList"
          :props="merProps"
          filterable
          clearable
          @change="onChangeMerId"
        />
      </div>
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
// import * as merchant from '@/api/merchant';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'c_merchantName',
  props: {
    configObj: {
      type: Object,
    },
    configNme: {
      type: String,
    },
    number: {
      type: null,
    },
  },
  data() {
    return {
      merProps: {
        value: 'id',
        label: 'name',
        children: 'merchantList',
        expandTrigger: 'hover',
        emitPath: false,
        multiple: true,
      },
      merchantList: [],
      defaults: {},
      configData: {},
      timeStamp: '',
      merIds: [],
    };
  },
  mounted() {
    if (checkPermi(['admin:merchant:page:list'])) this.getMerList();
    this.$nextTick(() => {
      this.defaults = this.configObj;
      this.configData = this.configObj[this.configNme];
      this.merIds = this.configData.activeValue;
    });
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.defaults = nVal;
        this.configData = nVal[this.configNme];
        this.merIds = this.configData.activeValue;
      },
      deep: true,
    },
    number(nVal) {
      this.timeStamp = nVal;
    },
  },
  methods: {
    checkPermi,
    // 列表
    getMerList() {
      merchant.merCategoryListApi().then((res) => {
        this.merchantList = res;
      });
    },
    onChangeMerId(e) {
      this.configData.activeValue = e;
      this.$emit('getConfig', { name: 'merchantName', values: e });
    },
  },
};
</script>

<style scoped>
.label {
  font-size: 12px;
  color: #999;
}

.slider-box {
  width: 81%;
}

.selWidth {
  width: 100% !important;
}
</style>
