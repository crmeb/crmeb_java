<template>
  <div>
    <el-menu
      :class="setColumnsAsideStyle"
      background-color="transparent"
      :default-active="activePath || defaultActive"
      :collapse="setIsCollapse"
      :unique-opened="getThemeConfig.isUniqueOpened"
      :collapse-transition="true"
      @select="handleMenuSelect"
    >
      <template v-for="val in menuList" :key="val.path">
        <el-sub-menu :index="val.path" v-if="val.children && val.children.length > 0">
          <template #title>
            <MenuIcon v-if="val.icon" :name="val.icon" />
            <span @click.stop="handleSubMenuTitleClick(val)">{{ val.title }}</span>
          </template>
          <SubItem :chil="val.children" />
        </el-sub-menu>
        <template v-else>
          <el-menu-item :index="val.path">
            <MenuIcon v-if="val.icon" :name="val.icon" />
            <template #title>
              <span>{{ val.title }}</span>
            </template>
          </el-menu-item>
        </template>
      </template>
    </el-menu>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import SubItem from '@/layout/navMenu/subItem.vue';
import MenuIcon from '@/components/MenuIcon/index.vue';
import { useMenuStore } from '@/store/modules/menu';
import { useThemeConfigStore } from '@/store/modules/themeConfig';
import { findFirstNonNullChildren } from '@/utils/system.js';

defineOptions({ name: 'navMenuVertical' });

const props = defineProps({
  menuList: {
    type: Array,
    default() {
      return [];
    },
  },
});

const route = useRoute();
const router = useRouter();
const menuStore = useMenuStore();
const themeConfigStore = useThemeConfigStore();

const defaultActive = ref(route.path);
const onRoutes = ref('');

// el-menu 的 router 模式在 element-plus 中有时不可靠，显式处理导航
function handleMenuSelect(index) {
  router.push(index);
}

// 有 children 的一级菜单点击标题时导航到第一个子菜单
function handleSubMenuTitleClick(val) {
  if (val.children && val.children.length > 0) {
    const firstLeaf = findFirstNonNullChildren(val.children, val.path);
    if (firstLeaf) {
      router.push(firstLeaf.path);
    }
  }
}

const activePath = computed(() => menuStore.activePath);
// 设置分栏高亮风格
const setColumnsAsideStyle = computed(() => themeConfigStore.themeConfig.columnsAsideStyle);
// 获取布局配置信息
const getThemeConfig = computed(() => themeConfigStore.themeConfig);
// 设置左侧菜单是否展开/收起
const setIsCollapse = computed(() => {
  return document.body.clientWidth < 1000 ? false : themeConfigStore.themeConfig.isCollapse;
});

// 监听路由的变化
watch(
  route,
  (to) => {
    defaultActive.value = to.path;
    const clientWidth = document.body.clientWidth;
    if (clientWidth < 1000) themeConfigStore.themeConfig.isCollapse = false;
  },
  { deep: true }
);
</script>
<style lang="scss" scoped>
:deep(.center) {
  text-align: center;
  margin-right: 0 !important;
  margin-left: 5px;
}
</style>
