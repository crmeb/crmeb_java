<template>
  <div class="divBox">
    <el-card class="box-card">
      <upload-index :pictureType="pictureType"></upload-index>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import UploadIndex from '@/components/uploadPicture/index.vue';

defineOptions({ name: 'index' });

const pictureType = ref('maintain');
</script>

<style scoped></style>
