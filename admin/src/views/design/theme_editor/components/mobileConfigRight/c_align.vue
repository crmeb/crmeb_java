<template>
  <div class="txt_tab" v-if="configData">
    <div class="c_row-item">
      <el-col class="c_label">
        {{ configData.title }}
        <span>{{ list[configData.val].val }}</span>
      </el-col>
      <div class="c_radio-group">
        <el-radio-group v-model="configData.val" type="button" @input="radioChange($event)">
          <el-radio-button size="small" :label="item.key" v-for="(item, key) in list" :key="key">
            <span class="iconfont" :class="item.icon"></span>
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'c_align',
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
      list: {
        left: {
          val: '居左',
          icon: 'iconzuoduiqi',
          key: 'left',
        },
        center: {
          val: '居中',
          icon: 'iconjuzhongduiqi',
          key: 'center',
        },
        right: {
          val: '居右',
          icon: 'iconyouduiqi',
          key: 'right',
        },
      },
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
      this.$emit('getConfig', { name: 'radio', values: e });
    },
  },
};
</script>

<style scoped lang="scss">
.txt_tab {
  // margin-top: 20px;
}
.c_row-item {
  margin: 0 15px 20px;
}
.c_radio-group {
  width: 70%;
}
.row-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.iconfont {
  font-size: 18px;
}
::v-deep .el-radio-button__inner {
  padding: 5px 10px;
}
</style>
