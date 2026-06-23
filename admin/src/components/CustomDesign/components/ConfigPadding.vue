<template>
  <el-form-item label="内边距" class="margin-style-config">
    <div class="c_content">
      <div class="main-setting">
        <el-slider v-model="paddingMainVal" :max="100" show-input @input="handlePaddingMainChange"></el-slider>
        <div class="expand-icon" @click="isPaddingExpanded = !isPaddingExpanded">
          <span class="iconfont iconbianju" :class="isPaddingExpanded ? 'selected' : ''"></span>
        </div>
      </div>

      <div class="sub-settings" v-if="isPaddingExpanded">
        <div class="sub-item">
          <div class="input-box">
            <span class="prefix-icon iconfont iconneibianju-shang"></span>
            <el-input-number
              v-model="curComponent.propValue.paddingTop"
              controls-position="right"
              @change="onChange"
              size="small"
            ></el-input-number>
          </div>
        </div>
        <div class="sub-item">
          <div class="input-box">
            <span class="prefix-icon iconfont iconneibianju-you"></span>
            <el-input-number
              v-model="curComponent.propValue.paddingRight"
              controls-position="right"
              @change="onChange"
              size="small"
            ></el-input-number>
          </div>
        </div>
        <div class="sub-item">
          <div class="input-box">
            <span class="prefix-icon iconfont iconneibianju-xia"></span>
            <el-input-number
              v-model="curComponent.propValue.paddingBottom"
              controls-position="right"
              @change="onChange"
              size="small"
            ></el-input-number>
          </div>
        </div>
        <div class="sub-item">
          <div class="input-box">
            <span class="prefix-icon iconfont iconneibianju-zuo"></span>
            <el-input-number
              v-model="curComponent.propValue.paddingLeft"
              controls-position="right"
              @change="onChange"
              size="small"
            ></el-input-number>
          </div>
        </div>
      </div>
    </div>
  </el-form-item>
</template>

<script>
export default {
  name: 'ConfigPadding',
  props: {
    curComponent: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      paddingMainVal: 0,
      isPaddingExpanded: false,
    };
  },
  watch: {
    'curComponent.id': {
      handler() {
        if (this.curComponent && this.curComponent.propValue) {
          // Ensure padding properties are reactive
          if (this.curComponent.propValue.paddingTop === undefined)
            this.$set(this.curComponent.propValue, 'paddingTop', 0);
          if (this.curComponent.propValue.paddingRight === undefined)
            this.$set(this.curComponent.propValue, 'paddingRight', 0);
          if (this.curComponent.propValue.paddingBottom === undefined)
            this.$set(this.curComponent.propValue, 'paddingBottom', 0);
          if (this.curComponent.propValue.paddingLeft === undefined)
            this.$set(this.curComponent.propValue, 'paddingLeft', 0);

          this.paddingMainVal = this.curComponent.propValue.paddingTop || 0;
        }
      },
      immediate: true,
    },
  },
  methods: {
    onChange() {
      this.$emit('change');
    },
    handlePaddingMainChange(val) {
      this.$set(this.curComponent.propValue, 'paddingTop', val);
      this.$set(this.curComponent.propValue, 'paddingRight', val);
      this.$set(this.curComponent.propValue, 'paddingBottom', val);
      this.$set(this.curComponent.propValue, 'paddingLeft', val);
      this.onChange();
    },
  },
};
</script>

<style scoped lang="scss">
.margin-style-config {
  margin-bottom: 20px;
  .c_content {
    flex: 1;
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
        font-size: 12px;
      }
      .selected {
        color: #409eff;
        border-color: #c6e2ff;
      }
    }
    ::v-deep .el-slider {
      flex: 1;
      margin-right: 0;
      .el-slider__input {
        width: 60px;
        padding-right: 0px;
      }
      .el-slider__runway {
        margin-right: 75px;
      }
    }
  }
  .sub-settings {
    display: flex;
    flex-wrap: wrap;
    margin-top: 10px;
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
        background-color: #fff;
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
            padding-right: 30px;
          }
        }
      }
    }
  }
}
</style>
