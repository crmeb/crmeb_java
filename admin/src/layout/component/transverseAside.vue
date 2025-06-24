<template>
  <div class="layout-columns-tra-aside el-menu-horizontal-warp">
    <el-scrollbar ref="elMenuHorizontalScrollRef" @wheel.native.prevent="onElMenuHorizontalScroll">
      <ul>
        <li
          v-for="(v, k) in columnsAsideList"
          :key="k"
          @click="onColumnsAsideMenuClick(v)"
          ref="columnsAsideOffsetLeftRefs"
          class="layout-columns"
          :class="{ 'layout-columns-active': v.k === liIndex }"
          :title="v.title"
        >
          <div :class="setColumnsAsidelayout">
            <div class="font12">
              {{
                v.title && v.title.length >= 4
                  ? v.title.substr(0, setColumnsAsidelayout === 'columns-vertical' ? 4 : 3)
                  : v.title
              }}
            </div>
          </div>
        </li>
        <div ref="columnsAsideActiveRef" :class="setColumnsAsideStyle"></div>
      </ul>
    </el-scrollbar>
  </div>
</template>

<script>
import { getMenuSider, getHeaderName, findFirstNonNullChildren } from '@/utils/system.js';
import Logo from '@/layout/logo/index.vue';

export default {
  name: 'layoutColumnsAside',
  components: { Logo },
  data() {
    return {
      columnsAsideList: [],
      liIndex: 0,
      difference: 0,
      routeSplit: [],
      activePath: '',
    };
  },
  computed: {
    // 设置分栏高亮风格
    setColumnsAsideStyle() {
      return this.$store.state.themeConfig.themeConfig.columnsAsideStyle;
    },
    // 设置分栏布局风格
    setColumnsAsidelayout() {
      return this.$store.state.themeConfig.themeConfig.columnsAsideLayout;
    },
    Layout() {
      return this.$store.state.themeConfig.themeConfig.Layout;
    },
    routesList() {
      this.$store.state.user.menuList;
    },
  },
  beforeDestroy() {
    this.bus.$off('routesListChange');
  },
  mounted() {
    this.bus.$on('routesListChange', () => {
      this.setFilterRoutes();
    });
    this.setFilterRoutes();
    this.$nextTick((e) => {
      this.initElMenuOffsetLeft();
    });
  },
  methods: {
    // 设置横向滚动条可以鼠标滚轮滚动
    onElMenuHorizontalScroll(e) {
      const eventDelta = e.wheelDelta || -e.deltaY * 40;
      this.$refs.elMenuHorizontalScrollRef.$refs.wrap.scrollLeft =
        this.$refs.elMenuHorizontalScrollRef.$refs.wrap.scrollLeft + eventDelta / 4;
    },
    // 初始化数据，页面刷新时，滚动条滚动到对应位置
    initElMenuOffsetLeft() {
      this.$nextTick(() => {
        let els = document.querySelector('.layout-columns.layout-columns-active');
        if (!els) return false;
        this.$refs.elMenuHorizontalScrollRef.$refs.wrap.scrollLeft = els.offsetLeft;
      });
    },
    // 设置菜单高亮位置移动
    setColumnsAsideMove(k) {
      if (k === undefined) return false;
      const els = this.$refs.columnsAsideOffsetLeftRefs;
      this.liIndex = k;
      this.$refs.columnsAsideActiveRef.style.left = `${els[k].offsetLeft + this.difference}px`;
    },
    // 菜单高亮点击事件
    onColumnsAsideMenuClick(v) {
      let { path, redirect } = v;
      if (v.children.length) {
        this.$router.push(findFirstNonNullChildren(v.children).path);
      } else {
        this.$router.push(path);
      }
      // 一个路由设置自动收起菜单
      if (!v.children || v.children.length <= 1) this.$store.state.themeConfig.themeConfig.isCollapse = true;
      else if (v.children.length > 1) this.$store.state.themeConfig.themeConfig.isCollapse = false;
      // this.bus.$emit('setSendColumnsChildren', getMenuSider(this.columnsAsideList, path));
    },
    // 设置高亮动态位置
    onColumnsAsideDown(k) {
      this.$nextTick(() => {
        this.setColumnsAsideMove(k);
      });
    },
    // 设置/过滤路由（非静态路由/是否显示在菜单中）
    setFilterRoutes() {
      if (this.$store.state.user.menuList.length <= 0) return false;
      this.columnsAsideList = this.filterRoutesFun(this.$store.state.user.menuList);
      //   const resData = getHeaderName(this.$route.path, this.columnsAsideList);
      const resData = this.setSendChildren(getHeaderName(this.$route, this.columnsAsideList));
      if (!resData && !resData.item[0].children.length) {
        this.bus.$emit('setSendColumnsChildren', []);
        this.$store.commit('user/childMenuList', []);

        this.$store.state.themeConfig.themeConfig.isCollapse = true;
        return false;
      }
      if (!resData) return;
      this.bus.$emit('oneCatName', resData.item[0].title);
      this.onColumnsAsideDown(resData.item[0].k);
      // 刷新时，初始化一个路由设置自动收起菜单
      resData.item[0].children.length > 0
        ? (this.$store.state.themeConfig.themeConfig.isCollapse = false)
        : (this.$store.state.themeConfig.themeConfig.isCollapse = true);
      this.bus.$emit('setSendColumnsChildren', resData.item[0].children || []);
      this.$store.commit('user/childMenuList', resData.item[0].children || []);
    },
    // 传送当前子级数据到菜单中
    setSendChildren(path) {
      // const currentPathSplit = path.split('/');
      let currentData = {};
      this.columnsAsideList.map((v, k) => {
        v['k'] = k;
        if (v.path === path) {
          currentData['item'] = [{ ...v }];
          //   currentData['children'] = [{ ...v }];
          if (v.children.length) currentData['children'] = v.children;
        }
      });
      return currentData;
    },
    // 路由过滤递归函数
    filterRoutesFun(arr) {
      return arr
        .filter((item) => item.path)
        .map((item) => {
          item = Object.assign({}, item);
          if (item.children.length) item.children = this.filterRoutesFun(item.children);
          return item;
        });
    },
    // tagsView 点击时，根据路由查找下标 columnsAsideList，实现左侧菜单高亮
    setColumnsMenuHighlight(path) {
      // this.routeSplit = path.split('/');
      // this.routeSplit.shift();
      // const routeFirst = `/${this.routeSplit[0]}`;
      const currentSplitRoute = this.columnsAsideList.find((v) => v.path === path);
      if (!currentSplitRoute) {
        this.onColumnsAsideDown(0);
        return false;
      }
      // 延迟拿值，防止取不到
      setTimeout(() => {
        this.onColumnsAsideDown(currentSplitRoute.k);
      }, 0);
    },
  },
  watch: {
    // 监听 vuex 数据变化
    '$store.state': {
      handler(val) {
        val.themeConfig.themeConfig.columnsAsideStyle === 'columnsRound'
          ? (this.difference = 3)
          : (this.difference = 0);
        if (val.user.menuListlength === this.columnsAsideList.length) return false;
      },
      deep: true,
    },
    // 监听路由的变化
    $route: {
      handler(to) {
        this.setColumnsMenuHighlight(to.path);
        // this.setColumnsAsideMove();
        let HeadName = getHeaderName(to, this.columnsAsideList);
        let asideList = getMenuSider(this.columnsAsideList, HeadName)[0].children;
        const resData = this.setSendChildren(HeadName);
        if (resData && resData.item && resData.item.length) {
          this.onColumnsAsideDown(resData.item[0].k);
          this.bus.$emit('oneCatName', resData.item[0].title);
        }

        this.bus.$emit('setSendColumnsChildren', asideList || []);
        this.$store.commit('user/childMenuList', asideList || []);
      },
      deep: true,
    },
  },
};
</script>

<style scoped lang="scss">
::v-deep .el-scrollbar__bar.is-horizontal {
  height: 0;
}
.el-menu-horizontal-warp {
  ::v-deep .el-scrollbar__bar.is-vertical {
    display: none;
  }
  ::v-deep .el-scrollbar__wrap {
    overflow-y: hidden !important;
    overflow-x: scroll !important;
  }
  ::v-deep a {
    width: 100%;
  }
  .el-menu.el-menu--horizontal {
    display: flex;
    height: 100%;
    width: 100%;
    box-sizing: border-box;
  }
}

.layout-columns-tra-aside {
  height: 100%;
  background: var(--prev-bg-columnsMenuBar);
  // box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  overflow-y: hidden;
  // flex: 1;
  ul {
    position: relative;
    display: flex;
    li {
      color: var(--prev-bg-columnsMenuBarColor);
      width: 70px;
      height: 50px;
      text-align: center;
      display: flex;
      cursor: pointer;
      position: relative;
      z-index: 1;
      .columns-vertical {
        margin: auto;
        // .columns-vertical-title {
        //   padding-top: 1px;
        // }
      }
      .columns-horizontal {
        display: flex;
        height: 50px;
        width: 70px;
        align-items: center;
        justify-content: center;
        padding: 0 5px;
        i {
          margin-right: 5px;
        }
        a {
          display: flex;
          .columns-horizontal-title {
            padding-top: 1px;
          }
        }
      }
      a {
        text-decoration: none;
        color: var(--prev-bg-columnsMenuBarColor);
      }
    }
    // li:hover {
    //   background: var(--prev-bg-menu-hover-ba-color);
    //   color: var(--prev-bg-columnsMenuBarColor);
    // }
    .layout-columns {
      transition: 0.3s ease-in-out;
    }
    .layout-columns-active,
    .layout-columns-active a {
      color: var(--prev-bg-columnsMenuActiveColor);
      transition: 0.3s ease-in-out;
    }
    .columns-round {
      background: var(--prev-color-primary);
      position: absolute;
      left: 0;
      height: 40px;
      width: 70px;
      margin-top: 5px;
      transform: translatey(0%);
      z-index: 0;
      transition: 0.2s ease-in-out;
      border-radius: 3px;
    }
    .columns-card {
      @extend .columns-round;
      top: 0;
      height: 50px;
      width: 70px;
      border-radius: 0;
      margin-top: 0px;
    }
  }
}
::v-deep .el-scrollbar {
  height: 50px;
}
::v-deep .el-scrollbar__bar.is-horizontal {
  display: none;
}
::v-deep .el-scrollbar__thumb {
  display: none;
}
</style>
