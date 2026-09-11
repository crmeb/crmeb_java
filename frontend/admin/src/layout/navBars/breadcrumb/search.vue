<template>
  <div class="layout-search-dialog">
    <el-dialog
      v-model="isShowSearch"
      class="layout-search-dialog-panel"
      width="540px"
      destroy-on-close
      :modal="false"
      fullscreen
      :show-close="true"
      @click="onSearchDialogClick"
    >
      <el-autocomplete
        v-model="menuQuery"
        :fetch-suggestions="menuSearch"
        placeholder="菜单搜索：支持中文、路由路径"
        :prefix-icon="Search"
        ref="layoutMenuAutocompleteRef"
        @select="onHandleSelect"
      >
        <template #default="{ item }">
          <div><i :class="item.icon" class="mr10"></i>{{ item.title }}</div>
        </template>
      </el-autocomplete>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { getAllSiderMenu } from '@/utils/system.js';
import { useUserStore } from '@/store/modules/user';

defineOptions({ name: 'layoutBreadcrumbSearch' });

const router = useRouter();
const userStore = useUserStore();

const isShowSearch = ref(false);
const menuQuery = ref('');
const tagsViewList = ref([]);
const layoutMenuAutocompleteRef = ref(null);

// 搜索弹窗打开
function openSearch() {
  menuQuery.value = '';
  isShowSearch.value = true;
  initTageView();
  nextTick(() => {
    layoutMenuAutocompleteRef.value?.focus?.();
  });
}

// 搜索弹窗关闭
function closeSearch() {
  setTimeout(() => {
    isShowSearch.value = false;
  }, 150);
}

// 点击搜索框外的蒙层区域关闭弹窗
function onSearchDialogClick(event) {
  const target = event.target;
  if (!(target instanceof HTMLElement)) return;
  if (target.closest('.el-autocomplete') || target.closest('.el-dialog__headerbtn')) return;
  closeSearch();
}

// 菜单搜索数据过滤
function menuSearch(queryString, cb) {
  let results = queryString ? tagsViewList.value.filter(createFilter(queryString)) : tagsViewList.value;
  cb(results);
}

// 菜单搜索过滤
function createFilter(queryString) {
  return (restaurant) => {
    return (
      restaurant.path.toLowerCase().indexOf(queryString.toLowerCase()) > -1 ||
      restaurant.title.toLowerCase().indexOf(queryString.toLowerCase()) > -1
    );
  };
}

// 初始化菜单数据
function initTageView() {
  if (tagsViewList.value.length > 0) return false;
  tagsViewList.value = getAllSiderMenu(userStore.menuList);
  // tagsViewStore.visitedViews.map((v) => {
  // 	if (!v.isHide) tagsViewList.value.push({ ...v });
  // });
}

// 当前菜单选中时
function onHandleSelect(item) {
  let { path, redirect } = item;
  if (redirect) router.push(redirect);
  else router.push(path);
  closeSearch();
}

defineExpose({ openSearch, closeSearch });
</script>

<style scoped lang="scss">
:global(.layout-search-dialog-panel) {
  box-shadow: unset !important;
  border-radius: 0 !important;
  background: rgba(0, 0, 0, 0.5);
  overflow: hidden;
}

:global(.layout-search-dialog-panel .el-dialog__header) {
  height: 56px;
  padding: 12px 20px !important;
  border: none !important;
}

:global(.layout-search-dialog-panel .el-dialog__headerbtn) {
  position: absolute !important;
  top: 12px;
  right: 20px;
  width: 32px;
  height: 32px;
  color: #fff;
}

:global(.layout-search-dialog-panel .el-dialog__body) {
  max-height: none;
  padding: 44px 24px 0 !important;
  overflow: visible;
}

:global(.layout-search-dialog-panel .el-autocomplete) {
  display: block;
  width: min(560px, calc(100vw - 48px));
  margin: 0 auto;
}

:global(.layout-search-dialog-panel .el-autocomplete .el-input__wrapper) {
  min-height: 40px;
  padding: 0 14px;
}
</style>
