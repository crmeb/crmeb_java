<template>
  <el-menu :default-active="activeMenu" mode="horizontal" @select="handleSelect">
    <template v-for="(item, index) in topMenus">
      <el-menu-item :style="{ '--theme': theme }" :index="item.path" :key="index" v-if="index < visibleNumber">
        <i :class="'el-icon-' + item.extra" v-if="navIcon" />{{ item.name }}
      </el-menu-item>
    </template>
    <!-- 顶部菜单超出数量折叠 -->
    <el-submenu :style="{ '--theme': theme }" index="more" v-if="topMenus.length > visibleNumber">
      <template slot="title">更多菜单</template>
      <template v-for="(item, index) in topMenus">
        <el-menu-item :index="item.path" :key="index" v-if="index >= visibleNumber">
          <i :class="'el-icon-' + item.extra" v-if="navIcon" />{{ item.name }}
        </el-menu-item>
      </template>
    </el-submenu>
  </el-menu>
</template>

<script>
export default {
  data() {
    return {
      // 顶部栏初始数
      visibleNumber: 9,
      // 当前激活菜单的 index
      currentIndex: undefined,
    };
  },
  computed: {
    theme() {
      return this.$store.state.settings.theme;
    },
    navIcon() {
      return this.$store.state.settings.navIcon;
    },
    // 所有的路由信息
    routers() {
      let routers = this.$store.state.permission.topbarRouters;
      let arr = [],
        obj = {};
      routers.forEach((item) => {
        obj = item;
        obj.path = item.url;
        arr.push(obj);
      });
      return arr;
    },
    // 顶部显示菜单
    topMenus() {
      let topMenus = [];
      this.routers.map((menu) => {
        if (menu.hidden !== true) {
          // 兼容顶部栏一级菜单内部跳转
          if (menu.path === '/') {
            topMenus.push(menu.child[0]);
          } else {
            topMenus.push(menu);
          }
        }
      });
      return topMenus;
    },
    // 默认激活的菜单
    activeMenu() {
      //简单粗暴的办法，获取当前路由截取一级路由赋值给默认选中菜单
      let key = this.$route.path.split('/')[1];
      key = '/' + key;
      return key;
    },
  },
  beforeMount() {
    window.addEventListener('resize', this.setVisibleNumber);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.setVisibleNumber);
  },
  mounted() {
    this.setVisibleNumber();
  },
  methods: {
    // 根据宽度计算设置显示栏数
    setVisibleNumber() {
      const width = document.body.getBoundingClientRect().width / 1.8;
      this.visibleNumber = parseInt(width / 85);
    },
    // 菜单选择事件
    handleSelect(key, keyPath) {
      this.currentIndex = key;
      if (this.ishttp(key)) {
        // http(s):// 路径新窗口打开
        window.open(key, '_blank');
      } else if (key.indexOf('/redirect') !== -1) {
        // /redirect 路径内部打开
        this.$router.push({ path: key.replace('/redirect', '') });
      } else {
        // 显示左侧联动菜单
        this.activeRoutes(key);
      }
    },
    // 当前激活的路由
    activeRoutes(key) {
      var routes = [];
      this.routers.map((item) => {
        if (key == item.url && item.child) {
          //如果选中导航的key值与遍历项的url匹配并且有子级，那么就将该项的子级数组放在routes中
          routes = item.child;
        } else if (key == item.url && !item.child) {
          //只满足选中导航的key值与遍历项的url匹配但是没有子级的情况下，就把这一项赋值给vuex中
          //这一项其实针对控制台，控制台没有子级
          this.$store.commit('permission/SET_SIDEBAR_ROUTERS', [item]);
        }
      });
      if (routes.length > 0) {
        //routes数组有长度就将它放在vuex中，左侧导航就能读取到，展示的也是选中项的子级
        this.$store.commit('permission/SET_SIDEBAR_ROUTERS', routes);
      }
      return routes;
    },
    ishttp(url) {
      return url.indexOf('http://') !== -1 || url.indexOf('https://') !== -1;
    },
  },
};
</script>

<style lang="scss">
.topmenu-container.el-menu--horizontal > .el-menu-item {
  float: left;
  height: 50px !important;
  line-height: 50px !important;
  color: #999093;
  padding: 0 5px !important;
  margin: 0 10px !important;
}

.topmenu-container.el-menu--horizontal > .el-menu-item.is-active,
.el-menu--horizontal > .el-submenu.is-active .el-submenu__title {
  border-bottom: 2px solid #{'var(--theme)'} !important;
  color: var(--theme);
}

/* submenu item */
.topmenu-container.el-menu--horizontal > .el-submenu .el-submenu__title {
  float: left;
  height: 50px !important;
  line-height: 50px !important;
  color: #999093 !important;
  padding: 0 5px !important;
  margin: 0 10px !important;
}
</style>
