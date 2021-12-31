<template>
  <div :class="{'has-logo':showLogo}" :style="{ backgroundColor: variables.menuBackground }">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="variables.menuBackground"
        :text-color=" variables.menuColor"
        :unique-opened="true"
        :active-text-color="variables.menuActiveText"
        :collapse-transition="true"
        mode="vertical"
      >
        <sidebar-item v-for="route in sidebarRouters" :key="route.url" :item="route" :base-path="route.url" /> 
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
import { mapGetters,mapState } from 'vuex'
import Logo from './Logo'
import SidebarItem from './SidebarItem'
import variables from '@/styles/variables.scss'
export default {
  components: { SidebarItem, Logo },
  computed: {
    ...mapState(["settings"]),
    ...mapGetters([
      'permission_routes',
      'sidebarRouters',
      'sidebar'
    ]),
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    showLogo() {
      return this.$store.state.settings.sidebarLogo
    },
    variables() {
      return variables
    },
    isCollapse() {
      return !this.sidebar.opened
    }
  },
}
</script>
<style lang="scss">
  .el-submenu__icon-arrow{
    color: #fff !important;
  }
</style>

