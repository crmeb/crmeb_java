<template>
  <div>
    <el-form-item label="图标">
      <div class="upload-box" @click="openIconModal">
        <div
          v-if="curComponent.propValue.class"
          style="display: flex; align-items: center; justify-content: center; width: 100%; height: 100%"
        >
          <span class="mb-iconfont" :class="curComponent.propValue.class" style="font-size: 24px"></span>
        </div>
        <div v-else class="upload-icon"><i class="el-icon-plus"></i></div>
      </div>
    </el-form-item>
    <ConfigLink
      :curComponent="curComponent"
      :type="type"
      :currentFieldList="currentFieldList"
      :showLinkType="true"
      @change="onChange"
      @get-link="getLink"
    />
    <c_icon_select ref="iconSelect" @select="handleIconSelect"></c_icon_select>
  </div>
</template>

<script>
import ConfigLink from './ConfigLink';

export default {
  name: 'ConfigIconContent',
  components: {
    ConfigLink,
    c_icon_select: () => import('../../mobileConfigRight/c_icon_select'),
  },
  props: {
    curComponent: {
      type: Object,
      required: true,
    },
    type: {
      type: String,
      default: 'user',
    },
    currentFieldList: {
      type: Array,
      default: () => [],
    },
  },
  methods: {
    openIconModal() {
      this.$refs.iconSelect.show();
    },
    handleIconSelect(icon) {
      this.curComponent.propValue.class = icon;
      this.onChange();
    },
    getLink() {
      this.$emit('get-link');
    },
    onChange() {
      this.$emit('change');
    },
  },
};
</script>

<style scoped lang="scss">
.upload-box {
  width: 80px;
  height: 80px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  &:hover {
    border-color: #409eff;
  }
  .upload-icon {
    font-size: 28px;
    color: #8c939d;
  }
}
</style>
