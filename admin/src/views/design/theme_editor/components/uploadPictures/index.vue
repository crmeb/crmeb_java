<template>
  <upload-picture
    :isMore="uploadIsMore"
    :modelName="modelName"
    :checkedMore="normalizedCheckedMore"
    :isShowVideo="isShowVideo"
    @getImage="handleGetImage"
  />
</template>

<script>
import UploadPicture from '@/components/uploadPicture/index.vue';

export default {
  name: 'uploadPictures',
  components: {
    UploadPicture,
  },
  props: {
    isChoice: {
      type: String,
      default: '单选',
    },
    checkedMore: {
      type: Array,
      default: () => [],
    },
    modelName: {
      type: String,
      default: 'theme',
    },
    isType: {
      type: [String, Number],
      default: 1,
    },
    gridBtn: {
      type: Object,
      default: () => ({}),
    },
    gridPic: {
      type: Object,
      default: () => ({}),
    },
    isIframe: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    uploadIsMore() {
      return this.isChoice === '多选' ? '0' : '1';
    },
    isShowVideo() {
      return Number(this.isType) !== 1;
    },
    normalizedCheckedMore() {
      return this.checkedMore.map(this.toJavaImage);
    },
  },
  methods: {
    toLegacyImage(item = {}) {
      const url = item.att_dir || item.sattDir || item.satt_dir || item.url || '';
      const id = item.att_id || item.attId || item.id || '';
      return {
        ...item,
        att_dir: url,
        satt_dir: url,
        sattDir: url,
        att_id: id,
        attId: id,
      };
    },
    toJavaImage(item = {}) {
      const url = item.sattDir || item.att_dir || item.satt_dir || item.url || '';
      const id = item.attId || item.att_id || item.id || '';
      return {
        ...item,
        sattDir: url,
        attId: id,
      };
    },
    handleGetImage(list = []) {
      const images = (Array.isArray(list) ? list : [list]).map(this.toLegacyImage);
      if (this.isChoice === '多选') {
        this.$emit('getPic', images);
        this.$emit('getPicD', images);
        return;
      }
      this.$emit('getPic', images[0] || {});
      this.$emit('getPicD', images[0] || {});
    },
  },
};
</script>
