<template>
  <upload-picture
    :isMore="uploadIsMore"
    :modelName="modelName"
    :checkedMore="normalizedCheckedMore"
    :isShowVideo="isShowVideo"
    @getImage="handleGetImage"
  />
</template>

<script setup>
import { computed } from 'vue';
import UploadPicture from '@/components/uploadPicture/index.vue';

defineOptions({ name: 'uploadPictures' });

const props = defineProps({
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
});

const emit = defineEmits(['getPic', 'getPicD']);

const uploadIsMore = computed(() => (props.isChoice === '多选' ? '0' : '1'));
const isShowVideo = computed(() => Number(props.isType) !== 1);
const normalizedCheckedMore = computed(() => props.checkedMore.map(toJavaImage));

function toLegacyImage(item = {}) {
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
}
function toJavaImage(item = {}) {
  const url = item.sattDir || item.att_dir || item.satt_dir || item.url || '';
  const id = item.attId || item.att_id || item.id || '';
  return {
    ...item,
    sattDir: url,
    attId: id,
  };
}
function handleGetImage(list = []) {
  const images = (Array.isArray(list) ? list : [list]).map(toLegacyImage);
  if (props.isChoice === '多选') {
    emit('getPic', images);
    emit('getPicD', images);
    return;
  }
  emit('getPic', images[0] || {});
  emit('getPicD', images[0] || {});
}
</script>
