<template>
  <div
    class="layout-logo"
    v-if="$store.state.themeConfig.themeConfig.layout !== 'columns' && !$store.state.themeConfig.themeConfig.isCollapse"
    @click="onThemeConfigChange"
  >
    <img v-if="platMerLoginInfo" class="layout-logo-medium-img" :src="platMerLoginInfo.siteLogoSquare" />
  </div>
  <div class="layout-logo-size" v-else @click="onThemeConfigChange">
    <img v-if="platMerLoginInfo" class="layout-logo-size-img" :src="platMerLoginInfo.siteLogoLeftTop" />
  </div>
</template>

<script>
import Cookies from 'js-cookie';
export default {
  name: 'layoutLogo',
  data() {
    return {
      platMerLoginInfo: JSON.parse(Cookies.get('logoInfo')), //登录后的信息
    };
  },
  computed: {
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
  mounted() {},
  methods: {
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
