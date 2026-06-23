<template>
  <div class="line-settings">
    <div class="section-title">线条设置</div>
    <el-form size="small" label-width="70px">
      <el-form-item label="线条方向">
        <el-radio-group v-model="curComponent.propValue.direction" @change="handleLineDirectionChange">
          <el-radio label="horizontal">横向</el-radio>
          <el-radio label="vertical">竖向</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="线条样式">
        <el-radio-group v-model="curComponent.propValue.style" @change="onChange">
          <el-radio label="solid">实线</el-radio>
          <el-radio label="dashed">虚线</el-radio>
          <el-radio label="dotted">点线</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="线条长度">
        <div class="row">
          <el-slider
            v-model="lineLength"
            :max="canvasWidth"
            :min="10"
            @change="onChange"
            style="flex: 1; margin-right: 10px"
          ></el-slider>
          <el-input-number
            v-model="lineLength"
            :max="canvasWidth"
            :min="10"
            @change="onChange"
            style="width: 90px"
          ></el-input-number>
        </div>
      </el-form-item>
      <el-form-item label="线条粗细">
        <div class="row">
          <el-slider
            v-model="lineThickness"
            :max="100"
            :min="1"
            @change="onChange"
            style="flex: 1; margin-right: 10px"
          ></el-slider>
          <el-input-number
            v-model="lineThickness"
            :max="100"
            :min="1"
            @change="onChange"
            style="width: 90px"
          ></el-input-number>
        </div>
      </el-form-item>
      <el-form-item label="线条颜色">
        <div class="row">
          <el-color-picker v-model="curComponent.propValue.color" @change="onChange"></el-color-picker>
          <el-input
            v-model="curComponent.propValue.color"
            placeholder="请输入颜色"
            @change="onChange"
            style="margin-left: 10px; flex: 1"
          ></el-input>
          <el-button
            type="text"
            @click="
              curComponent.propValue.color = '#000000';
              onChange();
            "
            >重置</el-button
          >
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'ConfigLine',
  props: {
    curComponent: {
      type: Object,
      required: true,
    },
    canvasWidth: {
      type: Number,
      default: 375,
    },
  },
  computed: {
    lineLength: {
      get() {
        if (this.curComponent.propValue.direction === 'horizontal') {
          return this.curComponent.style.width;
        } else {
          return this.curComponent.style.height;
        }
      },
      set(val) {
        if (this.curComponent.propValue.direction === 'horizontal') {
          this.curComponent.style.width = val;
        } else {
          this.curComponent.style.height = val;
        }
        this.onChange();
      },
    },
    lineThickness: {
      get() {
        return this.curComponent.propValue.height;
      },
      set(val) {
        this.curComponent.propValue.height = val;
        if (this.curComponent.propValue.direction === 'horizontal') {
          this.curComponent.style.height = val;
        } else {
          this.curComponent.style.width = val;
        }
        this.onChange();
      },
    },
  },
  methods: {
    handleLineDirectionChange() {
      const { width, height } = this.curComponent.style;
      this.curComponent.style.width = height;
      this.curComponent.style.height = width;
      this.onChange();
    },
    onChange() {
      this.$emit('change');
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
  width: calc(100% + 30px);
  margin-left: -15px;
  padding-left: 15px;
  padding-top: 10px;
  border-top: 6px solid #f0f2f5;
}
</style>
