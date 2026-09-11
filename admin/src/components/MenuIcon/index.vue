<template>
  <el-icon v-if="iconComponent" class="ivu-icon menu-icon" :size="size">
    <component :is="iconComponent" />
  </el-icon>
</template>

<script setup>
import { computed } from 'vue';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';

defineOptions({ name: 'MenuIcon' });

const props = defineProps({
  name: {
    type: String,
    default: '',
  },
  size: {
    type: [Number, String],
    default: 14,
  },
});

const iconComponent = computed(() => {
  if (!props.name) return null;
  const componentName = props.name
    .split('-')
    .map((item) => item.charAt(0).toUpperCase() + item.slice(1))
    .join('');
  return ElementPlusIconsVue[componentName] || null;
});
</script>
