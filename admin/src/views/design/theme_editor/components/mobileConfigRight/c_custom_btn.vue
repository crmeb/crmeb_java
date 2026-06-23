<template>
  <div class="custom-btn-box">
    <el-button type="primary" class="btn" @click="onClick">{{ configData.title || '设计组件' }}</el-button>
  </div>
</template>

<script>
export default {
  name: 'c_custom_btn',
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
      configData: {},
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.configData = this.configObj[this.configNme] || {};
    });
  },
  watch: {
    configObj: {
      handler(nVal) {
        this.configData = nVal[this.configNme] || {};
      },
      deep: true,
    },
  },
  methods: {
    onClick() {
      this.$emit('getConfig', { name: 'custom_btn_click' });
    },
  },
};
</script>

<style scoped lang="scss">
.custom-btn-box {
  padding: 0 15px 20px;
  .btn {
    width: 100%;
  }
}
</style>
