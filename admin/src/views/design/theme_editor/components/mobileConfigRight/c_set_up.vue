<template>
  <div>
    <div class="setUpTop"></div>
    <div class="setUp">
      <!-- <div class="label" v-if="defaults.cname">
        {{ defaults.cname }}
      </div> -->
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane v-for="(item, index) in list" :key="index" :label="item" :name="String(index)"> </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
export default {
  name: 'c_set_up',
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
      configData: {
        tabVal: 0,
      },
      list: ['内容设置', '样式设置'],
      current: 0,
      activeTab: '0',
    };
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.defaults = nVal;
        this.configData = nVal[this.configNme];
        this.current = (this.configData && this.configData.tabVal);
        this.activeTab = String((this.configData && this.configData.tabVal));
      },
      deep: true,
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.defaults = this.configObj;
      this.configData = this.configObj[this.configNme];
      this.$nextTick((e) => {
        this.current = (this.configData && this.configData.tabVal);
        this.activeTab = String((this.configData && this.configData.tabVal));
      });
    });
  },
  methods: {
    handleTabClick(tab) {
      const index = parseInt(tab.name);
      this.configData.tabVal = index;
      this.current = index;
    },
    onClickTab(index) {
      this.configData.tabVal = index;
      this.current = index;
      this.activeTab = String(index);
    },
  },
};
</script>

<style scoped lang="scss">
.setUpTop {
  height: 6px;
  background: rgb(240, 242, 245);
}
.setUp {
  display: flex;
  justify-content: center;
  padding: 0px 15px 0;
  border-bottom: 1px solid #f0f2f5;
  z-index: 99;
  position: relative;
  background: #fff;
  .label {
    font-size: 16px;
    color: #333333;
    margin-bottom: 15px;
  }

  ::v-deep .el-tabs__header {
    margin: 0;
  }

  ::v-deep .el-tabs__nav-wrap::after {
    height: 1px;
    background-color: #e4e7ed;
  }

  ::v-deep .el-tabs__item {
    font-size: 14px;
    color: #606266;
    padding: 0 60px;
    height: 40px;
    line-height: 40px;

    &.is-active {
      color: var(--prev-color-primary);
      font-weight: 500;
    }

    &:hover {
      color: var(--prev-color-primary);
    }
  }

  ::v-deep .el-tabs__active-bar {
    background-color: var(--prev-color-primary);
    height: 2px;
  }

  ::v-deep .el-tabs__content {
    display: none;
  }
}
</style>
