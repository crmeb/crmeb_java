<template>
  <el-card shadow="never" class="box-card" :bordered="false">
    <div class="acea-row row-middle">
      <div class="font-sm after-line" @click="goBack">
        <span class="el-icon-arrow-left"></span>
        <span>返回</span>
      </div>
      <span class="ht_title ml10">{{ title }}</span>
    </div>
  </el-card>
</template>

<script setup>
import { useRouter } from 'vue-router';

defineOptions({ name: 'pagesHeader' });

const props = defineProps({
  title: {
    type: String,
    default: '',
  },
  backUrl: {
    type: String,
    default: '',
  },
});

const router = useRouter();

function goBack() {
  if (props.backUrl) {
    router.push({
      path: props.backUrl,
    });
  } else {
    router.back();
  }
}
</script>

<style scoped>
.after-line {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  position: relative;
  margin-right: 16px;
  color: rgba(0, 0, 0, 0.85);
  cursor: pointer;
}
.after-line:after {
  content: '';
  position: absolute;
  top: 50%;
  right: -16px;
  width: 1px;
  height: 16px;
  background: #eee;
  transform: translateY(-50%);
}
.font-sm {
  font-size: 14px;
}
.ht_title {
  font-weight: 500;
  font-size: 18px;
}
</style>
