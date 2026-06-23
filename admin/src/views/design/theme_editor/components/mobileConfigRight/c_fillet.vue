<template>
  <div class="margin-style-config" v-if="configData">
    <div class="c_row">
      <div class="c_label">{{ configData.title }}</div>
      <div class="c_content">
        <div class="main-setting">
          <el-slider v-model="configData.val" show-input :min="configData.min" :max="configData.max || 100"></el-slider>
          <div class="expand-icon" :class="configData.type ? 'selected' : ''" @click="toggleExpand">
            <span class="iconfont iconcaozuo-bianjiao"></span>
          </div>
        </div>
        <div class="sub-settings" v-if="configData.type">
          <div class="sub-item" v-for="(item, index) in configData.valList" :key="index">
            <div class="input-box">
              <span class="prefix-icon iconfont" :class="getIcon(index)"></span>
              <el-input-number
                v-model="item.val"
                :min="configData.min"
                :max="configData.max || 100"
                size="small"
                controls-position="right"
              ></el-input-number>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'c_fillet',
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
  created() {
    if (this.configObj) {
      this.configData = this.configObj[this.configNme];
    }
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        if (!nVal) return;
        this.configData = nVal[this.configNme];
      },
      immediate: true,
      deep: true,
    },
    'configData.val': {
      handler(nVal) {
        if (this.configData && this.configData.type == 0 && this.configData.valList) {
          this.configData.valList.forEach((item) => {
            item.val = nVal;
          });
        }
      },
    },
    'configData.type': {
      handler(nVal) {
        if (nVal == 0 && this.configData && this.configData.valList) {
          this.configData.valList.forEach((item) => {
            item.val = this.configData.val;
          });
        }
      },
    },
  },
  methods: {
    toggleExpand() {
      if (!this.configData) return;
      this.$set(this.configData, 'type', this.configData.type ? 0 : 1);
    },
    getIcon(index) {
      const icons = ['iconzuoshangjiao', 'iconyoushangjiao', 'iconzuoxiajiao', 'iconyouxiajiao'];
      // Adjust index order if needed: Top-Left, Top-Right, Bottom-Left, Bottom-Right
      // valList usually [TL, TR, BL, BR] or [TL, TR, BR, BL]?
      // home_bottom_menu: valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
      // Common order in CSS: TL, TR, BR, BL.
      // But c_fillet old code: 0: zuoshang, 1: youshang, 2: zuoxia, 3: youxia. (TL, TR, BL, BR)
      // Check c_margin_style: icons[index].
      // Old c_fillet: index 2 is zuoxia (BL), index 3 is youxia (BR).
      // So order is TL, TR, BL, BR.
      return icons[index] || '';
    },
  },
};
</script>

<style scoped lang="scss">
.margin-style-config {
  padding: 0px 15px 0 15px;
  .c_row {
    display: flex;
    justify-content: space-between;
  }
  .c_label {
    font-size: 12px;
    color: #999;
    line-height: 38px;
    margin-bottom: 10px;
    white-space: nowrap;
  }
  .c_content {
    width: 75%;
  }
  .main-setting {
    display: flex;
    align-items: center;
    .expand-icon {
      margin-left: 10px;
      cursor: pointer;
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 1px solid #dcdfe6;
      border-radius: 4px;
      color: #606266;
      &:hover {
        color: #409eff;
        border-color: #c6e2ff;
      }
      .iconfont {
        font-size: 18px;
      }
    }
    .expand-icon.selected {
      color: #409eff;
      border-color: #c6e2ff;
    }
    ::v-deep .el-slider {
      flex: 1;
      margin-right: 0;
      .el-slider__input {
        width: 90px;
      }
      .el-slider__runway {
        margin-right: 100px;
      }
    }
  }
  .sub-settings {
    display: flex;
    flex-wrap: wrap;
    margin-top: 10px;
    margin-right: -10px;
    .sub-item {
      width: 50%;
      padding-right: 10px;
      margin-bottom: 10px;
      box-sizing: border-box;
      .input-box {
        display: flex;
        align-items: center;
        border: 1px solid #dcdfe6;
        border-radius: 4px;
        padding-left: 5px;
        &:hover {
          border-color: #c0c4cc;
        }
        .prefix-icon {
          color: #909399;
          font-size: 14px;
          margin-right: 5px;
        }
        ::v-deep .el-input-number {
          width: 100%;
          border: none;
          .el-input__inner {
            border: none;
            padding-left: 5px;
            text-align: left;
          }
        }
      }
    }
  }
}
</style>
