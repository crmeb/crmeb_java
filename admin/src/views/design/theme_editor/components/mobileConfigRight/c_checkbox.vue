<template>
  <div class="checkboxs acea-row row-top" v-if="hasConfig">
    <div class="title-tips">
      <span>{{ configData.title }}</span>
    </div>
    <div class="checkbox-box">
      <el-checkbox-group size="small" v-model="configData.type" @change="checkboxChange()">
        <el-checkbox
          :label="item.id"
          :disabled="isDisabled(item)"
          v-for="(item, index) in configData.list"
          :key="index"
        >
          <span>{{ item.name }}</span>
        </el-checkbox>
      </el-checkbox-group>
    </div>
  </div>
</template>

<script>
export default {
  name: 'c_checkbox',
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
      selectedData: [],
      userStyle: 0,
    };
  },
  computed: {
    hasConfig() {
      return this.configData && Array.isArray(this.configData.type) && Array.isArray(this.configData.list);
    },
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.setConfig(nVal);
      },
      deep: true,
      immediate: true,
    },
    'configObj.styleConfig.tabVal': {
      handler(nVal, oVal) {
        if (this.configData && this.configData.userType) {
          this.configData.type = [3, 1, 2];
        }
      },
    },
    'configObj.storeStyleConfig.tabVal': {
      handler(nVal, oVal) {
        if (this.configData.storeType) {
          if (nVal == 1) {
            this.configData.list = [
              {
                id: 0,
                name: '配送方式',
              },
              {
                id: 2,
                name: '门店距离',
              },
              {
                id: 3,
                name: '门店地址',
              },
            ];
          } else {
            this.configData.list = [
              {
                id: 0,
                name: '配送方式',
              },
              {
                id: 1,
                name: '营业时间',
              },
              {
                id: 2,
                name: '门店距离',
              },
              {
                id: 3,
                name: '门店地址',
              },
            ];
          }
        }
      },
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.setConfig(this.configObj);
    });
  },
  methods: {
    setConfig(configObj) {
      this.defaults = configObj || {};
      const configData = this.defaults && this.configNme ? this.defaults[this.configNme] : null;
      if (!configData) {
        this.configData = {
          type: [],
          list: [],
        };
        this.userStyle = 0;
        this.selectedData = [];
        return;
      }
      if (!Array.isArray(configData.type)) {
        this.$set(configData, 'type', configData.type === undefined || configData.type === null ? [] : [configData.type]);
      }
      if (!Array.isArray(configData.list)) {
        this.$set(configData, 'list', []);
      }
      this.configData = configData;
      this.userStyle = (this.defaults.styleConfig && this.defaults.styleConfig.tabVal) || 1;
      this.selectedData = (this.defaults.checkboxInfo && this.defaults.checkboxInfo.type) || [];
    },
    isDisabled(item) {
      const type = this.configData.type || [];
      const maxList = Number(this.configData.maxList) || 0;
      return (
        (this.selectedData.length >= 3 && this.userStyle && this.configData.userType && !this.selectedData.includes(item.id)) ||
        (type.length >= 3 && this.configData.name == 'showContent' && !type.includes(item.id)) ||
        (maxList > 0 && type.length >= maxList && !type.includes(item.id))
      );
    },
    checkboxChange(e) {
      // this.$emit('getConfig', e);
    },
  },
};
</script>

<style scoped lang="scss">
.checkboxs {
  padding: 0 15px;
  margin-bottom: 20px;
}
.title-tips {
  margin-right: 14px;
  color: #999;
  font-size: 12px;
  width: 82px;
}
.checkbox-box {
  width: 270px;
}
.ivu-checkbox-group-item {
  margin-bottom: 15px;
  margin-right: 15px;
}
::v-deep.ivu-checkbox {
  margin-right: 4px;
}
::v-deep.ivu-checkbox-group-item {
  font-size: 12px;
}
::v-deep.ivu-checkbox-wrapper:nth-last-child(1) {
  margin-right: 0;
}
</style>
