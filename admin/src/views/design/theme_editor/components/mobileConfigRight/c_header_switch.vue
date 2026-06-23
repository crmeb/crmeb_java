<template>
  <div class="config-item">
    <div class="item-header">
      <span class="item-title">{{ configData.title }}</span>
      <el-switch
        class="defineSwitch"
        v-model="configData.enable"
        active-text="开启"
        inactive-text="关闭"
        @change="handleChange"
      ></el-switch>
    </div>
  </div>
</template>

<script>
export default {
  name: 'c_header_switch',
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
        title: '',
        enable: false,
      },
    };
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.configData = nVal[this.configNme] || { title: '', enable: false };
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
.config-item {
  padding: 12px 16px;

  .item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .item-title {
      font-size: 14px;
      color: #333;
    }
  }
}
</style>
