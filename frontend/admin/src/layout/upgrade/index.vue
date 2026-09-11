<template>
  <div class="upgrade-dialog">
    <el-dialog
      v-model="isUpgrade"
      width="470px"
      destroy-on-close
      :show-close="true"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { Local, Session } from '@/utils/storage';
import config from '../../../package.json';
import setting from '../../setting';
import { useThemeConfigStore } from '@/store/modules/themeConfig';

defineOptions({ name: 'layoutUpgrade' });

const router = useRouter();
const themeConfigStore = useThemeConfigStore();

const isUpgrade = ref(false);
const version = config.version;
const isLoading = ref(false);
const btnTxt = ref('');

// 获取布局配置信息
const getThemeConfig = computed(() => themeConfigStore.themeConfig);

// 残忍拒绝
function onCancel() {
  isUpgrade.value = false;
}
// 马上更新
function onUpgrade() {
  isLoading.value = true;
  btnTxt.value = '';
  setTimeout(() => {
    Local.clear();
    Session.clear();
    Local.set('version', version);
    router.push({ path: `${setting.routePre}/login` });
  }, 2000);
}
// 延迟显示，防止刷新时界面显示太快
function delayShow() {
  setTimeout(() => {
    btnTxt.value = '';
  }, 1000);
  setTimeout(() => {
    isUpgrade.value = true;
  }, 2000);
}

onMounted(() => {
  delayShow();
});
</script>

<style scoped lang="scss">
.upgrade-dialog {
  & :deep(.el-dialog) {
    .el-dialog__body {
      padding: 0 !important;
    }
    .el-dialog__header {
      //display: none !important;
    }
    .upgrade-title {
      text-align: center;
      height: 130px;
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;
      &::after {
        content: '';
        position: absolute;
        background-color: var(--prev-color-primary-light-1);
        width: 130%;
        height: 130px;
        border-bottom-left-radius: 100%;
        border-bottom-right-radius: 100%;
      }
      .upgrade-title-warp {
        z-index: 1;
        position: relative;
        .upgrade-title-warp-txt {
          color: var(--prev-color-text-white);
          font-size: 22px;
          letter-spacing: 3px;
        }
        .upgrade-title-warp-version {
          background-color: var(--prev-color-primary-light-4);
          color: var(--prev-color-text-white);
          font-size: 12px;
          position: absolute;
          display: flex;
          top: -2px;
          right: -50px;
          padding: 2px 4px;
          border-radius: 2px;
        }
      }
    }
    .upgrade-content {
      padding: 20px;
      line-height: 22px;
      color: var(--prev-color-text-regular);
      .upgrade-content-desc {
        color: var(--prev-color-text-placeholder);
        font-size: 12px;
      }
    }
    .upgrade-btn {
      border-top: 1px solid var(--prev-border-color-lighter);
      display: flex;
      justify-content: space-around;
      padding: 15px 20px;
      .el-button {
        width: 100%;
      }
    }
  }
}
</style>
