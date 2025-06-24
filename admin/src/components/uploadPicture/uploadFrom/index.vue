<template>
  <div>
    <el-dialog
      title="上传图片"
      :visible.sync="visible"
      width="950px"
      :modal="booleanVal"
      append-to-body
      :before-close="handleClose"
    >
      <el-button
        class="selfDialogClose"
        type="text"
        icon="el-icon-close"
        circle
        @click="handleClose"
        size="medium"
      ></el-button>
      <upload-index v-if="visible" :isMore="isMore" :modelName="modelName" @getImage="getImage" />
    </el-dialog>
  </div>
</template>

<script>
// import UploadIndex from '@/components/uploadPicture/index.vue'
export default {
  name: 'UploadFroms',
  // components: { UploadIndex },
  data() {
    return {
      visible: false,
      callback: function () {},
      isMore: '',
      modelName: '',
      ISmodal: false,
      booleanVal: false,
    };
  },
  watch: {
    // show() {
    //   this.visible = this.show
    // }
  },
  methods: {
    handleClose() {
      this.visible = false;
    },
    getImage(img) {
      this.callback(img);
      this.visible = false;
    },
  },
};
</script>

<style lang="scss" scoped>
/* 统一组件中的特殊组件 */
::v-deep .el-dialog__header {
  display: none !important;
}
.selfDialogClose {
  display: inline-block;
  position: absolute;
  right: 0;
  top: 3px;
  pointer-events: auto;
  z-index: 999;
  font-size: 16px;
  color: #363f4d;
}
</style>
