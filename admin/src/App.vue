<template>
  <div id="app">
    <router-view v-if="isRouterAlive" />
    <Setings ref="setingsRef" />
  </div>
</template>

<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import { Local } from '@/utils/storage.js';
import Setings from '@/layout/navBars/breadcrumb/setings.vue';

export default {
  name: 'App',
  components: { Setings },

  provide() {
    return {
      reload: this.reload,
    };
  },
  data() {
    return {
      isRouterAlive: true,
    };
  },
  watch: {
    // 监听路由 控制侧边栏显示 标记当前顶栏菜单（如需要）
    $route(to, from) {
      const onRoutes = to.meta.activeMenu ? to.meta.activeMenu : to.meta.path;
      this.$store.commit('menu/setActivePath', onRoutes);
      if (to.name == 'crud_crud') {
        this.$store.state.user.oneLvRoutes.map((e) => {
          if (e.path === to.path) {
            to.meta.title = e.title;
          }
        });
      }
      //优惠券、秒杀活动
      if (['creatProduct', 'CreatCoupon', 'CreatSeckill', 'CreatTag', 'border', 'articleCreat'].includes(to.name)) {
        let route = to.matched[1].path.split(':')[0];
        this.$store.state.user.oneLvRoutes.map((e) => {
          if (route.indexOf(e.path) != -1) {
            to.meta.title = `${e.title} ${to.params.id ? 'ID:' + to.params.id : ''}`;
          }
        });
      }
      //个人中心、修改密码
      if (['MaintainUser', 'MaintainUpdate'].includes(to.name)) {
        this.bus.$emit('oneCatName', '控制台');
      }
    },
  },
  mounted() {
    this.openSetingsDrawer();
    this.getLayoutThemeConfig();
  },
  methods: {
    reload() {
      this.isRouterAlive = false;
      this.$nextTick(function () {
        this.isRouterAlive = true;
      });
    },
    // 布局配置弹窗打开
    openSetingsDrawer() {
      this.bus.$on('openSetingsDrawer', () => {
        this.$refs.setingsRef.openDrawer();
      });
    },
    // 获取缓存中的布局配置
    getLayoutThemeConfig() {
      if (Local.get('JavaPlatThemeConfigPrev')) {
        this.$store.dispatch('themeConfig/setThemeConfig', Local.get('JavaPlatThemeConfigPrev'));
        document.documentElement.style.cssText = Local.get('JavaPlatThemeConfigStyle');
      } else {
        Local.set('JavaPlatThemeConfigPrev', this.$store.state.themeConfig.themeConfig);
      }
    },
  },
  destroyed() {
    this.bus.$off('openSetingsDrawer');
  },
};
</script>
