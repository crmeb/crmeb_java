<template>
  <div />
</template>

<script setup>
defineOptions({ name: 'ErrorTestB' });

// created 时机（setup 顶层）：故意触发未定义变量错误，用于测试错误日志收集
// eslint-disable-next-line
const _ = () => {
  // eslint-disable-next-line no-undef
  this.b = b;
};
</script>
