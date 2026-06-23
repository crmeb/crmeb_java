<template>
  <div
    class="layout-logo"
    v-if="$store.state.themeConfig.themeConfig.layout !== 'columns' && !$store.state.themeConfig.themeConfig.isCollapse"
    @click="onThemeConfigChange"
  >
    <img v-if="maxLogo" class="layout-logo-medium-img" :src="maxLogo" />
  </div>
  <div class="layout-logo-size" v-else @click="onThemeConfigChange">
    <img v-if="minLogo" class="layout-logo-size-img" :src="minLogo" />
  </div>
</template>

<script>
import Cookies from 'js-cookie';
import { getSiteLogoApi } from '@/api/systemConfig';
export default {
  name: 'layoutLogo',
  data() {
    return {
      platMerLoginInfo: {},
    };
  },
  computed: {
    maxLogo() {
      return (
        this.platMerLoginInfo.siteLogoSquare ||
        this.platMerLoginInfo.siteLogoLeftTop ||
        this.platMerLoginInfo.logoSquare ||
        this.platMerLoginInfo.logo_square ||
        this.platMerLoginInfo.logo ||
        this.platMerLoginInfo.loginLogo ||
        ''
      );
    },
    minLogo() {
      return (
        this.platMerLoginInfo.siteLogoLeftTop ||
        this.platMerLoginInfo.siteLogoSquare ||
        this.platMerLoginInfo.logoLeftTop ||
        this.platMerLoginInfo.logo_square ||
        this.platMerLoginInfo.logo ||
        this.platMerLoginInfo.loginLogo ||
        ''
      );
    },
    // 获取布局配置信息
    getThemeConfig() {
      return this.$store.state.themeConfig.themeConfig;
    },
    // 设置 logo 是否显示
    setShowLogo() {
      let { isCollapse, layout } = this.$store.state.themeConfig.themeConfig;
      return !isCollapse || layout === 'classic' || document.body.clientWidth < 1000;
    },
  },
  mounted() {
    this.getLogoInfo();
  },
  methods: {
    getLogoInfo() {
      getSiteLogoApi()
        .then((res) => {
          this.platMerLoginInfo = res || {};
        })
        .catch(() => {
          const logoInfo = Cookies.get('logoInfo');
          this.platMerLoginInfo = this.parseLogoInfo(logoInfo);
        });
    },
    parseLogoInfo(logoInfo) {
      if (!logoInfo) return {};
      try {
        return JSON.parse(logoInfo);
      } catch (e) {
        return {};
      }
    },
    // logo 点击实现菜单展开/收起
    onThemeConfigChange() {
      // if (
      //   this.$store.state.themeConfig.themeConfig.layout == 'columns' &&
      //   !this.$store.state.user.childMenuList.length &&
      //   this.$store.state.themeConfig.themeConfig.isCollapse
      // )
      //   return;
      // if (this.$store.state.themeConfig.themeConfig.layout === 'transverse' || this.$store.state.themeConfig.themeConfig.layout === 'classic') return false;
      // this.$store.state.themeConfig.themeConfig.isCollapse = !this.$store.state.themeConfig.themeConfig.isCollapse;
      this.$router.push(`/dashboard`);
    },
  },
};
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
