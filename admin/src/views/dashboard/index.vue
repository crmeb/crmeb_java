<template>
  <div>
    <!--欢迎信息-->
    <div class="divBox" style="padding-bottom: 0">
      <el-card class="box-card welcome-card" shadow="never">
        <div class="welcome">
          <div class="welcome-title">{{ greeting }}，{{ userName || '管理员' }}</div>
          <div class="welcome-desc">欢迎使用 CRMEB 商城管理后台，祝您工作愉快！</div>
        </div>
      </el-card>
    </div>
    <!--快捷入口 / 待办事项-->
    <grid-menu class="mb14" />
  </div>
</template>

<script setup>
import { computed } from 'vue';
import gridMenu from './components/gridMenu';
import { useUserStore } from '@/store/modules/user';

defineOptions({ name: 'Dashboard' });

const userStore = useUserStore();
const userName = computed(() => userStore.name);

const greeting = computed(() => {
  const hour = new Date().getHours();
  if (hour < 6) return '凌晨好';
  if (hour < 12) return '上午好';
  if (hour < 14) return '中午好';
  if (hour < 18) return '下午好';
  return '晚上好';
});
</script>

<style lang="scss" scoped>
.welcome-card {
  :deep(.el-card__body) {
    padding: 24px 20px;
  }
}
.welcome {
  &-title {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
  }
  &-desc {
    margin-top: 10px;
    font-size: 14px;
    color: #909399;
  }
}
</style>
