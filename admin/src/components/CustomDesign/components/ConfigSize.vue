<template>
  <div>
    <!-- <div class="section-title">容器设置</div> -->
    <div class="size-container">
      <el-form-item :label="labelPrefix + '宽度'" class="size-item">
        <div class="row">
          <el-slider
            v-model="curComponent.style.width"
            :max="canvasWidth"
            :min="10"
            @change="handleWidthChange"
            style="flex: 1; margin-right: 10px"
          ></el-slider>
          <el-input-number
            v-model="curComponent.style.width"
            :max="canvasWidth"
            :min="10"
            @change="handleWidthChange"
            style="width: 70px"
            :controls="false"
          ></el-input-number>
        </div>
      </el-form-item>
      <div class="lock-wrapper">
        <div class="lock-line"></div>
        <div class="lock-btn" @click="toggleLock" :class="{ active: isLocked }" title="锁定宽高">
          <span class="iconfont iconsuodingbili"></span>
        </div>
      </div>
      <el-form-item :label="labelPrefix + '高度'" class="size-item">
        <div class="row">
          <el-slider
            v-model="curComponent.style.height"
            :max="canvasHeight"
            :min="10"
            @change="handleHeightChange"
            style="flex: 1; margin-right: 10px"
          ></el-slider>
          <el-input-number
            v-model="curComponent.style.height"
            :max="canvasHeight"
            :min="10"
            @change="handleHeightChange"
            style="width: 70px"
            :controls="false"
          ></el-input-number>
        </div>
      </el-form-item>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ConfigSize',
  props: {
    curComponent: {
      type: Object,
      required: true,
    },
    canvasWidth: {
      type: Number,
      required: true,
    },
    canvasHeight: {
      type: Number,
      required: true,
    },
    labelPrefix: {
      type: String,
      default: '容器',
    },
  },
  data() {
    return {
      isLocked: false,
      aspectRatio: 1,
    };
  },
  watch: {
    'curComponent.id': {
      handler(val) {
        if (val && this.curComponent && this.curComponent.style) {
          this.isLocked = !!this.curComponent.isRatioLock;
          if (this.isLocked) {
            this.aspectRatio = this.curComponent.style.width / this.curComponent.style.height;
          }
        }
      },
      immediate: true,
    },
  },
  methods: {
    onChange() {
      this.$emit('change');
    },
    toggleLock() {
      this.isLocked = !this.isLocked;
      this.$set(this.curComponent, 'isRatioLock', this.isLocked);
      if (this.isLocked) {
        // Calculate aspect ratio when locked
        this.aspectRatio = this.curComponent.style.width / this.curComponent.style.height;
      }
    },
    handleWidthChange(val) {
      if (this.isLocked) {
        this.curComponent.style.height = Math.round(val / this.aspectRatio);
      }
      this.onChange();
    },
    handleHeightChange(val) {
      if (this.isLocked) {
        this.curComponent.style.width = Math.round(val * this.aspectRatio);
      }
      this.onChange();
    },
  },
};
</script>

<style scoped lang="scss">
.row {
  display: flex;
  align-items: center;
}
.section-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 18px;
  margin-top: 10px;
}
.size-container {
  position: relative;
  padding-right: 40px;

  .size-item {
    margin-bottom: 10px;
  }
}
.lock-wrapper {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  position: absolute;
  right: -5px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 10;

  .lock-btn {
    width: 32px;
    height: 32px;
    margin: 0;
    border: 1px solid #dcdfe6;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    background: #fff;
    transition: all 0.3s;
    border-radius: 4px;
    color: #606266;

    &.active {
      background: none;
      color: #409eff;
      border-color: #409eff;
    }

    &:hover {
      border-color: #409eff;
    }

    .iconfont {
      font-size: 18px;
    }
  }

  &::before {
    content: '';
    position: absolute;
    right: 37px;
    top: -15px;
    width: 8px;
    height: 1px;
    background: #dcdfe6;
  }

  &::after {
    content: '';
    position: absolute;
    right: 37px;
    bottom: -15px;
    width: 8px;
    height: 1px;
    background: #dcdfe6;
  }

  .lock-line {
    position: absolute;
    right: 37px;
    top: -15px;
    bottom: -15px;
    width: 1px;
    background: #dcdfe6;
  }
}
</style>
