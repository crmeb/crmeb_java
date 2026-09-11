<template>
  <el-dialog v-model="visible" width="1024px" title="选择图片" :close-on-click-modal="false">
    <uploadPictures
      v-if="visible"
      :isChoice="isMore"
      @getPic="getImage"
      :gridBtn="gridBtn"
      :gridPic="gridPic"
    ></uploadPictures>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue';
import uploadPictures from './index.vue';

const isChoice = ref('');
const visible = ref(false);
const callback = ref(function () {});
const gridBtn = ref({
  xl: 4,
  lg: 8,
  md: 8,
  sm: 8,
  xs: 8,
});
const gridPic = ref({
  xl: 6,
  lg: 8,
  md: 12,
  sm: 12,
  xs: 12,
});
const more = ref(false);

const isMore = computed(() => (more.value ? '多选' : '单选'));

function handleClose() {
  visible.value = false;
  callback.value(visible.value);
}
function getImage(img) {
  callback.value(img);
  visible.value = false;
}

defineExpose({ visible, callback, more, handleClose, getImage });
</script>
<style lang="scss" scoped></style>
