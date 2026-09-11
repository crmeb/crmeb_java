<template>
  <div>
    <el-dialog title="商品列表" v-model="visible" width="896" :before-close="handleClose">
      <good-list
        v-if="visible"
        @getStoreItem="getStoreItem"
        :handleNum="handleNum"
        :checked="checked"
        @close="close"
      ></good-list>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import goodList from '@/components/goodList/index.vue';

defineOptions({ name: 'GoodListFrom' });

const handleNum = ref('');
const visible = ref(false);
const callback = ref(function () {});
const checked = ref([]);

function close() {
  visible.value = false;
}

function handleClose() {
  visible.value = false;
}

function getStoreItem(img) {
  callback.value(img);
  visible.value = false;
}

function open(options = {}) {
  callback.value = typeof options.callback === 'function' ? options.callback : function () {};
  handleNum.value = options.handleNum;
  checked.value = options.checked ?? [];
  visible.value = true;
}

defineExpose({
  open,
  visible,
  callback,
  handleNum,
  checked,
});
</script>

<style scoped>
:deep(.el-dialog__body) {
  padding: 20px 24px 0 24px !important;
}
</style>
