<template>
  <div>
    <el-form-item label="图片">
      <div class="upload-box" @click="modalPicTap">
        <img :src="curComponent.propValue.url" v-if="curComponent.propValue.url" class="img" />
        <div v-else class="upload-icon"><i class="el-icon-plus"></i></div>
        <div class="delete-box" @click.stop="bindDelete" v-if="curComponent.propValue.url">
          <span class="iconfont icondel_1"></span>
        </div>
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
    <el-dialog :visible.sync="modalPic" width="950px" title="上传图片" append-to-body>
      <uploadPictures
        :isChoice="isChoice"
        @getPic="getPic"
        :gridBtn="gridBtn"
        :gridPic="gridPic"
        v-if="modalPic"
      ></uploadPictures>
    </el-dialog>
  </div>
</template>

<script>
import uploadPictures from '@/views/design/theme_editor/components/uploadPictures';
import ConfigLink from './ConfigLink';

export default {
  name: 'ConfigPictureContent',
  components: {
    uploadPictures,
    ConfigLink,
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
  data() {
    return {
      modalPic: false,
      isChoice: '单选',
      gridBtn: {
        xl: 4,
        lg: 8,
        md: 8,
        sm: 8,
        xs: 8,
      },
      gridPic: {
        xl: 6,
        lg: 8,
        md: 12,
        sm: 12,
        xs: 12,
      },
    };
  },
  methods: {
    modalPicTap() {
      this.modalPic = true;
    },
    bindDelete() {
      this.curComponent.propValue.url = '';
      this.onChange();
    },
    getPic(pc) {
      this.curComponent.propValue.url = pc.att_dir;
      this.modalPic = false;
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
  width: 64px;
  height: 64px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  cursor: pointer;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  &:hover {
    border-color: #409eff;
  }
  .img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 4px;
  }
  .upload-icon {
    font-size: 28px;
    color: #8c939d;
  }
  .delete-box {
    position: absolute;
    top: -9px;
    right: -9px;
    line-height: 18px;
    cursor: pointer;
    z-index: 10;
    .iconfont {
      font-size: 16px;
      color: #f56c6c;
    }
  }
}
</style>
