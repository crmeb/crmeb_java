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
        :theme-info="themeInfo"
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

<script>
import Sidebar from './components/Sidebar.vue';
import PageHeader from './components/Header.vue';
import StyleConfig from './components/StyleConfig.vue';
import HomeEditor from './components/HomeEditor.vue';
import CategoryEditor from './components/CategoryEditor.vue';
import DetailEditor from './components/DetailEditor.vue';
import UserEditor from './components/UserEditor.vue';
import { saveThemeTitle, themeInfo } from '@/api/theme';

export default {
  name: 'EditTheme',
  components: {
    Sidebar,
    PageHeader,
    StyleConfig,
    HomeEditor,
    CategoryEditor,
    DetailEditor,
    UserEditor,
  },
  data() {
    return {
      themeName: '请设置页面名称',
      themeInfo: '',
      activeMenu: 'home', // 默认选中商城首页
      collapsed: false,
      isDirty: false, // 是否有未保存的修改
    };
  },
  provide() {
    return {
      setDirty: (dirty) => {
        this.isDirty = dirty;
      },
    };
  },
  watch: {
    '$route.query.type': {
      handler(val) {
        if (val) {
          this.activeMenu = val;
        }
      },
      immediate: true,
    },
  },
  computed: {
    isMicroPage() {
      return this.$route.query.page_type === 'micro';
    },
  },
  mounted() {
    if (this.$route.query.id != 0) this.getThemeBaseInfo();
  },
  methods: {
    getThemeBaseInfo() {
      const id = this.$route.query.id || 0;
      if (!id) {
        this.themeName = '请设置页面名称';
        return;
      }
      themeInfo(id, 'base')
        .then((res) => {
          this.themeName = res.data.title || '请设置页面名称';
          this.themeInfo = res.data.info;
        })
        .catch((err) => {
          this.$message.error(err.msg);
        });
    },
    handleMenuChange(menuKey) {
      if (this.isDirty) {
        this.isDirty = false;
      }
      this.activeMenu = menuKey;
      if (this.$route.query.type !== menuKey) {
        this.$router.replace({ query: { ...this.$route.query, type: menuKey } });
      }
    },
    handleSidebarSave(key) {
      this.onSave();
    },
    handleUpdateInfo(data) {
      this.themeName = data.title;
      this.themeInfo = data.info;
      let id = this.$route.query.id || 0;
      saveThemeTitle(id, data)
        .then((res) => {
          this.$message.success('保存成功');
          if (id == 0) {
            let query = { ...this.$route.query, id: res.data.id };
            if (query.tid) {
              delete query.tid; // 保存后移除 tid
            }
            this.$router.replace({ query });
            // Update active component's pageId
            const refName = this.getRefName();
            if (refName && this.$refs[refName] && this.$refs[refName].$refs.diy) {
              this.$refs[refName].$refs.diy.pageId = res.data.id;
            }
          }
        })
        .catch((err) => {
          this.$message.error(err.msg);
        });
    },
    getRefName() {
      switch (this.activeMenu) {
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
    },
    onPreview() {
      const refName = this.getRefName();
      if (refName && this.$refs[refName] && this.$refs[refName].preview) {
        this.$refs[refName].preview();
      }
    },
    onSave() {
      const refName = this.getRefName();
      if (refName && this.$refs[refName] && this.$refs[refName].saveOnly) {
        this.$refs[refName].saveOnly();
      }
    },
    onSaveClose() {
      const refName = this.getRefName();
      if (refName && this.$refs[refName] && this.$refs[refName].saveAndClose) {
        this.$refs[refName].saveAndClose();
      }
    },
    onSaveTemplate() {
      const refName = this.getRefName();
      if (refName && this.$refs[refName] && this.$refs[refName].saveTemplate) {
        this.$refs[refName].saveTemplate();
      }
    },
  },
};
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
