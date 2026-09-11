<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-button type="primary" v-hasPermi="['admin:system:config:clear:cache']" v-debounceClick="handleClear"
        >清除缓存</el-button
      >
    </el-card>
  </div>
</template>

<script setup>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import { ElMessage } from '@/utils/elementPlusFeedback';
import { clearCacheApi } from '@/api/systemConfig';

defineOptions({ name: 'clearCache' });

function handleClear() {
  clearCacheApi().then((response) => {
    ElMessage.success('清除成功');
  });
}
</script>

<style scoped></style>
