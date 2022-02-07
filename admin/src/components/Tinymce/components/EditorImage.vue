<template>
  <div class="upload-container">
    <el-button :style="{background:color,borderColor:color}" icon="el-icon-upload" size="mini" type="primary" @click="modalPicTap('2')"> upload</el-button>
  </div>
</template>

<script>
export default {
  name: 'EditorSlideUpload',
  props: {
    color: {
      type: String,
      default: '#1890ff'
    }
  },
  data() {
    return {
      dialogVisible: false,
      listObj: {},
      fileList: []
    }
  },
  methods: {
     modalPicTap(tit) {
      const _this = this
      this.$modalUpload(function(img) {
        let arr = [];
        if(img.length>10) return this.$message.warning("最多选择10张图片！");
        img.map((item) => {
          arr.push(item.sattDir)
        });
        // console.log(arr);
        _this.$emit('successCBK', arr)
      }, tit, 'content')
    },
  }
}
</script>

<style lang="scss" scoped>
.editor-slide-upload {
  margin-bottom: 20px;
  ::v-deep .el-upload--picture-card {
    width: 100%;
  }
}
</style>
