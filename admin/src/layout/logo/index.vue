<template>
  <div
    class="layout-logo"
    v-if="themeConfig.layout !== 'columns' && !themeConfig.isCollapse"
    @click="onThemeConfigChange"
  >
    <img v-if="maxLogo" class="layout-logo-medium-img" :src="maxLogo" />
  </div>
  <div class="layout-logo-size" v-else @click="onThemeConfigChange">
    <img v-if="minLogo" class="layout-logo-size-img" :src="minLogo" />
  </div>
</template>

<script setup>
import { computed, onMounted, reactive } from 'vue';
import { useRouter } from 'vue-router';
import Cookies from 'js-cookie';
import { getSiteLogoApi } from '@/api/systemConfig';
import { useThemeConfigStore } from '@/store/modules/themeConfig';

defineOptions({ name: 'layoutLogo' });

const router = useRouter();
const themeConfigStore = useThemeConfigStore();

const platMerLoginInfo = reactive({});

const maxLogo = computed(() => {
  return (
    platMerLoginInfo.siteLogoSquare ||
    platMerLoginInfo.siteLogoLeftTop ||
    platMerLoginInfo.logoSquare ||
    platMerLoginInfo.logo_square ||
    platMerLoginInfo.logo ||
    platMerLoginInfo.loginLogo ||
    ''
  );
});
const minLogo = computed(() => {
  return (
    platMerLoginInfo.siteLogoLeftTop ||
    platMerLoginInfo.siteLogoSquare ||
    platMerLoginInfo.logoLeftTop ||
    platMerLoginInfo.logo_square ||
    platMerLoginInfo.logo ||
    platMerLoginInfo.loginLogo ||
    ''
  );
});
// 获取布局配置信息
const themeConfig = computed(() => themeConfigStore.themeConfig);
// 设置 logo 是否显示
const setShowLogo = computed(() => {
  let { isCollapse, layout } = themeConfigStore.themeConfig;
  return !isCollapse || layout === 'classic' || document.body.clientWidth < 1000;
});

function getLogoInfo() {
  getSiteLogoApi()
    .then((res) => {
      Object.assign(platMerLoginInfo, res || {});
    })
    .catch(() => {
      const logoInfo = Cookies.get('logoInfo');
      Object.assign(platMerLoginInfo, parseLogoInfo(logoInfo));
    });
}
function parseLogoInfo(logoInfo) {
  if (!logoInfo) return {};
  try {
    return JSON.parse(logoInfo);
  } catch (e) {
    return {};
  }
}
// logo 点击实现菜单展开/收起
function onThemeConfigChange() {
  // if (
  //   themeConfigStore.themeConfig.layout == 'columns' &&
  //   !this.$store.state.user.childMenuList.length &&
  //   themeConfigStore.themeConfig.isCollapse
  // )
  //   return;
  // if (themeConfigStore.themeConfig.layout === 'transverse' || themeConfigStore.themeConfig.layout === 'classic') return false;
  // themeConfigStore.themeConfig.isCollapse = !themeConfigStore.themeConfig.isCollapse;
  router.push(`/dashboard`);
}

onMounted(() => {
  getLogoInfo();
});
</script>

<style scoped lang="scss">
.layout-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--prev-color-primary);
  font-size: 16px;
  cursor: pointer;
  animation: logoAnimation 0.3s ease-in-out;
  height: 64px;
  line-height: 64px;
  width: 206px;
  &:hover {
    span {
      opacity: 0.9;
    }
  }
  &-medium-img {
    height: 32px;
  }
}
.layout-logo-size {
  display: flex;
  cursor: pointer;
  margin: auto;
  height: 50px;
  &-img {
    width: 50px;
    height: auto;
    margin: auto;
    animation: logoAnimation 0.3s ease-in-out;
  }
}
</style>
