<script setup>
import { useRoute, useRouter } from 'vue-router';

defineOptions({ name: 'Redirect' });

const route = useRoute();
const router = useRouter();

// created 时机（setup 顶层）
const { params, query } = route;
const { path } = params;
router.replace({ path: '/' + path, query });
</script>

<template>
  <div></div>
</template>
