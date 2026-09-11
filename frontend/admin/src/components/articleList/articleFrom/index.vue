<template>
  <div>
    <el-dialog title="提示" v-model="visible" width="896px" :before-close="handleClose">
      <article-list
        v-if="visible"
        :handle="handle"
        :userIds="userIds"
        :couponId="couponId"
        @getArticle="getArticle"
        :keyNum="keyNum"
      ></article-list>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import articleList from '../index.vue';

defineOptions({ name: 'CouponFrom' });

const visible = ref(false);
const callback = ref(function () {});
const handle = ref('');
const keyNum = ref(0);
const couponId = ref([]);
const userIds = ref('');

function handleClose() {
  visible.value = false;
}

function getArticle(couponObj) {
  callback.value(couponObj);
  visible.value = false;
}

function open(options = {}) {
  callback.value = typeof options.callback === 'function' ? options.callback : function () {};
  handle.value = options.handle;
  visible.value = true;
}

defineExpose({
  open,
  visible,
  callback,
  handle,
  keyNum,
  couponId,
  userIds,
});
</script>

<style scoped></style>
