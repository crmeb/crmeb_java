<template>
  <div class="edit-theme-layout">
    <!-- 左侧导航 -->
    <div class="layout-sidebar" :class="{ collapsed }" v-if="!isMicroPage">
      <sidebar
        :active-menu="activeMenu"
        :collapsed="collapsed"
        :unsaved="isDirty"
        @change="handleMenuChange"
        @save="handleSidebarSave"
        @toggle="collapsed = !collapsed"
      ></sidebar>
    </div>

    <!-- 右侧主体 -->
    <div class="layout-main">
      <!-- 顶部栏 -->
      <page-header
        :theme-name="themeName"
        :theme-info="themeInfoVal"
        :isMicroPage="isMicroPage"
        @preview="onPreview"
        @save="onSave"
        @save-close="onSaveClose"
        @save-template="onSaveTemplate"
        @update-info="handleUpdateInfo"
      ></page-header>

      <!-- 内容区域 -->
      <div class="layout-content">
        <style-config ref="styleConfig" v-if="activeMenu === 'theme'"></style-config>
        <home-editor ref="homeEditor" v-else-if="activeMenu === 'home'"></home-editor>
        <category-editor ref="categoryEditor" v-else-if="activeMenu === 'category'"></category-editor>
        <detail-editor ref="detailEditor" v-else-if="activeMenu === 'detail'"></detail-editor>
        <user-editor ref="userEditor" v-else-if="activeMenu === 'user'"></user-editor>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, provide, inject, watch, onMounted } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { useRoute, useRouter } from 'vue-router';
import Sidebar from './components/Sidebar.vue';
import PageHeader from './components/Header.vue';
import StyleConfig from './components/StyleConfig.vue';
import HomeEditor from './components/HomeEditor.vue';
import CategoryEditor from './components/CategoryEditor.vue';
import DetailEditor from './components/DetailEditor.vue';
import UserEditor from './components/UserEditor.vue';
import { saveThemeTitle, themeInfo } from '@/api/theme';

defineOptions({ name: 'EditTheme' });

const route = useRoute();
const router = useRouter();

const reload = inject('reload', null);

const themeName = ref('请设置页面名称');
const themeInfoVal = ref('');
const activeMenu = ref('home'); // 默认选中商城首页
const collapsed = ref(false);
const isDirty = ref(false); // 是否有未保存的修改

const styleConfig = ref(null);
const homeEditor = ref(null);
const categoryEditor = ref(null);
const detailEditor = ref(null);
const userEditor = ref(null);

const setDirty = (dirty) => {
  isDirty.value = dirty;
};
provide('setDirty', setDirty);

const isMicroPage = computed(() => route.query.page_type === 'micro');

watch(
  () => route.query.type,
  (val) => {
    if (val) {
      activeMenu.value = val;
    }
  },
  { immediate: true },
);

function getThemeBaseInfo() {
  const id = route.query.id || 0;
  if (!id) {
    themeName.value = '请设置页面名称';
    return;
  }
  themeInfo(id, 'base')
    .then((res) => {
      themeName.value = res.data.title || '请设置页面名称';
      themeInfoVal.value = res.data.info;
    })
    .catch((err) => {
      ElMessage.error((err && (err.message || err.msg)) || '获取主题信息失败');
    });
}
function handleMenuChange(menuKey) {
  if (isDirty.value) {
    isDirty.value = false;
  }
  activeMenu.value = menuKey;
  if (route.query.type !== menuKey) {
    router.replace({ query: { ...route.query, type: menuKey } });
  }
}
function handleSidebarSave(key) {
  onSave();
}
function handleUpdateInfo(data) {
  themeName.value = data.title;
  themeInfoVal.value = data.info;
  let id = route.query.id || 0;
  saveThemeTitle(id, data)
    .then((res) => {
      ElMessage.success('保存成功');
      if (id == 0) {
        let query = { ...route.query, id: res.data.id };
        if (query.tid) {
          delete query.tid; // 保存后移除 tid
        }
        router.replace({ query });
        // Update active component's pageId
        const refName = getRefName();
        const refMap = {
          styleConfig,
          homeEditor,
          categoryEditor,
          detailEditor,
          userEditor,
        };
        const targetRef = refMap[refName];
        if (refName && targetRef.value && targetRef.value.$refs && targetRef.value.$refs.diy) {
          targetRef.value.$refs.diy.pageId = res.data.id;
        }
      }
    })
    .catch((err) => {
      ElMessage.error((err && (err.message || err.msg)) || '保存失败');
    });
}
function getRefName() {
  switch (activeMenu.value) {
    case 'theme':
      return 'styleConfig';
    case 'home':
      return 'homeEditor';
    case 'category':
      return 'categoryEditor';
    case 'detail':
      return 'detailEditor';
    case 'user':
      return 'userEditor';
    default:
      return '';
  }
}
function onPreview() {
  const refName = getRefName();
  const refMap = {
    styleConfig,
    homeEditor,
    categoryEditor,
    detailEditor,
    userEditor,
  };
  const targetRef = refMap[refName];
  if (refName && targetRef.value && targetRef.value.preview) {
    targetRef.value.preview();
  }
}
function onSave() {
  const refName = getRefName();
  const refMap = {
    styleConfig,
    homeEditor,
    categoryEditor,
    detailEditor,
    userEditor,
  };
  const targetRef = refMap[refName];
  if (refName && targetRef.value && targetRef.value.saveOnly) {
    targetRef.value.saveOnly();
  }
}
function onSaveClose() {
  const refName = getRefName();
  const refMap = {
    styleConfig,
    homeEditor,
    categoryEditor,
    detailEditor,
    userEditor,
  };
  const targetRef = refMap[refName];
  if (refName && targetRef.value && targetRef.value.saveAndClose) {
    targetRef.value.saveAndClose();
  }
}
function onSaveTemplate() {
  const refName = getRefName();
  const refMap = {
    styleConfig,
    homeEditor,
    categoryEditor,
    detailEditor,
    userEditor,
  };
  const targetRef = refMap[refName];
  if (refName && targetRef.value && targetRef.value.saveTemplate) {
    targetRef.value.saveTemplate();
  }
}

onMounted(() => {
  if (route.query.id != 0) getThemeBaseInfo();
});
</script>
<style>
.el-main {
  padding: 0;
}
</style>
<style lang="scss" scoped>
.edit-theme-layout {
  display: flex;
  height: 100vh;
  background-color: #f5f7fa;
  overflow: hidden;

  .layout-sidebar {
    width: 120px;
    background: #fff;
    height: 100%;
    flex-shrink: 0;
    transition: width 0.2s ease;
    &.collapsed {
      width: 70px;
    }
  }

  .layout-main {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-width: 0;

    .layout-content {
      flex: 1;
      overflow: hidden;
      position: relative;
      background: #fff;
    }
  }
}
</style>
