<template>
  <div class="components-container">
    <category-list :biztype="constants.categoryType[0]" />
  </div>
</template>

<script setup>
import { getCurrentInstance } from 'vue';
import categoryList from '@/components/Category/list';

const { proxy } = getCurrentInstance();
const constants = proxy.$constants;
</script>

<style scoped></style>
