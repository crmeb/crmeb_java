<template>
  <div class="layout-breadcrumb-seting">
    <el-drawer
      title="主题编辑"
      v-model="getThemeConfig.isDrawer"
      direction="rtl"
      destroy-on-close
      size="320px"
      @close="onDrawerClose"
    >
      <el-scrollbar class="layout-breadcrumb-seting-bar el-main">
        <!-- 布局切换 -->
        <el-divider :content-position="contentPosotion">布局切换</el-divider>
        <div class="layout-drawer-content-flex">
          <!-- defaults 布局 -->
          <div
            class="layout-drawer-content-item"
            :class="{
              'drawer-layout-active': getThemeConfig.layout === 'defaults',
            }"
            @click="onSetLayout('defaults')"
          >
            <section class="el-container el-circular">
              <aside class="el-aside w10 mr5" style="width: 17px"></aside>
              <section class="el-container is-vertical">
                <header class="el-header mb5" style="height: 10px"></header>
                <main class="el-main"></main>
              </section>
            </section>
          </div>

          <!-- columns 布局 -->
          <div
            class="layout-drawer-content-item"
            :class="{
              'drawer-layout-active': getThemeConfig.layout === 'columns',
            }"
            @click="onSetLayout('columns')"
          >
            <section class="el-container el-circular">
              <aside class="el-aside mr5" style="width: 10px"></aside>
              <aside class="el-aside-dark mr5" style="width: 17px"></aside>
              <section class="el-container is-vertical">
                <header class="el-header mb5" style="height: 10px"></header>
                <main class="el-main"></main>
              </section>
            </section>
          </div>
          <!-- classic 布局 -->
          <div
            class="layout-drawer-content-item"
            :class="{
              'drawer-layout-active': getThemeConfig.layout === 'classic',
            }"
            @click="onSetLayout('classic')"
          >
            <section class="el-container is-vertical el-circular">
              <header class="el-aside mb5" style="height: 10px"></header>
              <section class="el-container">
                <aside class="el-aside-dark mr5" style="width: 17px"></aside>
                <section class="el-container is-vertical">
                  <main class="el-main"></main>
                </section>
              </section>
            </section>
          </div>

          <!-- transverse 布局 -->
          <div
            class="layout-drawer-content-item"
            :class="{
              'drawer-layout-active': getThemeConfig.layout === 'transverse',
            }"
            @click="onSetLayout('transverse')"
          >
            <section class="el-container is-vertical el-circular">
              <header class="el-aside mb5" style="height: 10px"></header>
              <section class="el-container">
                <section class="el-container is-vertical">
                  <main class="el-main"></main>
                </section>
              </section>
            </section>
          </div>
        </div>
        <!-- 界面设置 -->
        <el-divider :content-position="contentPosotion">界面设置</el-divider>
        <div class="layout-breadcrumb-seting-bar-flex mb10">
          <div class="layout-breadcrumb-seting-bar-flex-label">主题</div>
          <div class="layout-breadcrumb-seting-bar-flex-value">
            <el-select
              v-model="getThemeConfig.themeStyle"
              placeholder="请选择"

              style="width: 90px"
              @change="setLocalTheme"
            >
              <el-option label="蓝黑" value="theme-1"></el-option>
              <el-option label="蓝白" value="theme-2"></el-option>
              <el-option label="绿黑" value="theme-3"></el-option>
              <el-option label="绿白" value="theme-4"></el-option>
              <el-option label="紫黑" value="theme-5"></el-option>
              <el-option label="紫白" value="theme-6"></el-option>
              <el-option label="红黑" value="theme-7"></el-option>
              <el-option label="红白" value="theme-8"></el-option>
              <el-option label="渐变" value="theme-9" v-if="getThemeConfig.layout === 'columns'"></el-option>
            </el-select>
          </div>
        </div>

        <div
          class="layout-breadcrumb-seting-bar-flex"
          v-if="getThemeConfig.layout === 'columns' || getThemeConfig.layout === 'defaults'"
        >
          <div class="layout-breadcrumb-seting-bar-flex-label">菜单水平折叠</div>
          <div class="layout-breadcrumb-seting-bar-flex-value">
            <el-switch v-model="getThemeConfig.isCollapse" :width="35" @change="setLocalThemeConfig"> </el-switch>
          </div>
        </div>
        <div class="layout-breadcrumb-seting-bar-flex mt15">
          <div class="layout-breadcrumb-seting-bar-flex-label">菜单手风琴</div>
          <div class="layout-breadcrumb-seting-bar-flex-value">
            <el-switch v-model="getThemeConfig.isUniqueOpened" :width="35" @change="setLocalThemeConfig"> </el-switch>
          </div>
        </div>
        <div class="layout-breadcrumb-seting-bar-flex mt15">
          <div class="layout-breadcrumb-seting-bar-flex-label">固定 Header</div>
          <div class="layout-breadcrumb-seting-bar-flex-value">
            <el-switch v-model="getThemeConfig.isFixedHeader" :width="35" @change="setLocalThemeConfig"> </el-switch>
          </div>
        </div>

        <!-- 界面显示 -->
        <el-divider :content-position="contentPosotion">界面显示</el-divider>
        <div
          v-show="themeConfigStore.themeConfig.layout !== 'columns'"
          class="layout-breadcrumb-seting-bar-flex"
        >
          <div class="layout-breadcrumb-seting-bar-flex-label">侧边栏 Logo</div>
          <div class="layout-breadcrumb-seting-bar-flex-value">
            <el-switch v-model="getThemeConfig.isShowLogo" :width="35" @change="setLocalThemeConfig"> </el-switch>
          </div>
        </div>
        <div
          class="layout-breadcrumb-seting-bar-flex mt15"
          :style="{
            opacity: getThemeConfig.layout === 'classic' || getThemeConfig.layout === 'transverse' ? 0.5 : 1,
          }"
        >
          <div class="layout-breadcrumb-seting-bar-flex-label">面包屑</div>
          <div class="layout-breadcrumb-seting-bar-flex-value">
            <el-switch
              v-model="getThemeConfig.isBreadcrumb"
              :disabled="getThemeConfig.layout === 'classic' || getThemeConfig.layout === 'transverse'"
              :width="35"
              @change="setLocalThemeConfig"
            >
            </el-switch>
          </div>
        </div>
        <div class="layout-breadcrumb-seting-bar-flex mt15">
          <div class="layout-breadcrumb-seting-bar-flex-label">面包屑图标</div>
          <div class="layout-breadcrumb-seting-bar-flex-value">
            <el-switch v-model="getThemeConfig.isBreadcrumbIcon" :width="35" @change="setLocalThemeConfig"> </el-switch>
          </div>
        </div>
        <div class="layout-breadcrumb-seting-bar-flex mt15">
          <div class="layout-breadcrumb-seting-bar-flex-label">历史菜单</div>
          <div class="layout-breadcrumb-seting-bar-flex-value">
            <el-switch v-model="getThemeConfig.isTagsview" :width="35" @change="setLocalThemeConfig"> </el-switch>
          </div>
        </div>
        <div class="layout-breadcrumb-seting-bar-flex mt15">
          <div class="layout-breadcrumb-seting-bar-flex-label">Footer</div>
          <div class="layout-breadcrumb-seting-bar-flex-value">
            <el-switch v-model="getThemeConfig.isFooter" :width="35" @change="setLocalThemeConfig"> </el-switch>
          </div>
        </div>
        <div class="layout-breadcrumb-seting-bar-flex mt15">
          <div class="layout-breadcrumb-seting-bar-flex-label">灰色模式</div>
          <div class="layout-breadcrumb-seting-bar-flex-value">
            <el-switch v-model="getThemeConfig.isGrayscale" :width="35" @change="onAddFilterChange('grayscale')">
            </el-switch>
          </div>
        </div>
        <div class="layout-breadcrumb-seting-bar-flex mt15">
          <div class="layout-breadcrumb-seting-bar-flex-label">色弱模式</div>
          <div class="layout-breadcrumb-seting-bar-flex-value">
            <el-switch v-model="getThemeConfig.isInvert" :width="35" @change="onAddFilterChange('invert')"> </el-switch>
          </div>
        </div>
        <!-- 其它设置 -->
        <el-divider :content-position="contentPosotion">其它设置</el-divider>
        <div class="layout-breadcrumb-seting-bar-flex mt15">
          <div class="layout-breadcrumb-seting-bar-flex-label">历史菜单风格</div>
          <div class="layout-breadcrumb-seting-bar-flex-value">
            <el-radio-group
              v-model="getThemeConfig.tagsStyle"
              :disabled="!getThemeConfig.isTagsview"

              @change="setLocalThemeConfig"
            >
              <el-radio-button label="tags-style-one" value="tags-style-one">卡片</el-radio-button>
              <el-radio-button label="tags-style-four" value="tags-style-four">灵动</el-radio-button>
              <el-radio-button label="tags-style-five" value="tags-style-five">圆滑</el-radio-button>
            </el-radio-group>
          </div>
        </div>
        <div class="layout-breadcrumb-seting-bar-flex mt15">
          <div class="layout-breadcrumb-seting-bar-flex-label">主页面切换动画</div>
          <div class="layout-breadcrumb-seting-bar-flex-value">
            <el-radio-group v-model="getThemeConfig.animation" @input="setLocalThemeConfig">
              <el-radio-button label="slide-left" value="slide-left">左滑</el-radio-button>
              <el-radio-button label="opacitys" value="opacitys">透明</el-radio-button>
              <el-radio-button label="slide-right" value="slide-right">右滑</el-radio-button>
            </el-radio-group>
          </div>
        </div>
        <div
          class="layout-breadcrumb-seting-bar-flex mt15"
          :class="{
            mb28: getThemeConfig.layout !== 'columns' && getThemeConfig.layout !== 'classic',
          }"
        >
          <div class="layout-breadcrumb-seting-bar-flex-label">菜单高亮风格</div>
          <div class="layout-breadcrumb-seting-bar-flex-value">
            <el-radio-group v-model="getThemeConfig.columnsAsideStyle" @input="setLocalThemeConfig">
              <el-radio-button label="columns-round" value="columns-round">圆角</el-radio-button>
              <el-radio-button label="columns-card" value="columns-card">卡片</el-radio-button>
            </el-radio-group>
          </div>
        </div>
        <div
          class="layout-breadcrumb-seting-bar-flex mt15 mb28"
          v-if="getThemeConfig.layout === 'columns' || getThemeConfig.layout === 'classic'"
        >
          <div class="layout-breadcrumb-seting-bar-flex-label">顶级菜单风格</div>
          <div class="layout-breadcrumb-seting-bar-flex-value">
            <el-radio-group v-model="getThemeConfig.columnsAsideLayout" @input="setLocalThemeConfig">
              <el-radio-button label="columns-horizontal" value="columns-horizontal">水平</el-radio-button>
              <el-radio-button label="columns-vertical" value="columns-vertical">垂直</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </el-scrollbar>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount, nextTick, getCurrentInstance } from 'vue';
import ClipboardJS from 'clipboard';
import { Local } from '@/utils/storage.js';
import { useChangeColor } from '@/utils/theme.js';
import config from '../../../../package.json';
import { themeList } from './theme';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { useThemeConfigStore } from '@/store/modules/themeConfig';
import { useSettingsStore } from '@/store/modules/settings';
import bus from '@/utils/bus';

defineOptions({ name: 'layoutBreadcrumbSeting' });

const { proxy } = getCurrentInstance();
const themeConfigStore = useThemeConfigStore();
const settingsStore = useSettingsStore();
const colorUtils = useChangeColor();

const contentPosotion = ref('center');

// 获取布局配置信息
const getThemeConfig = computed(() => {
  return themeConfigStore.themeConfig;
});

watch(
  () => themeConfigStore.themeConfig.primary,
  (val) => {
    setPrimaryColorVariables(val);
  },
  { immediate: true },
);

// 设置主题色全局变换颜色
function normalizeThemeColor(color) {
  if (typeof color !== 'string') return '';
  const value = color.trim();
  if (!/^#?[0-9A-Fa-f]{6}$/.test(value)) return '';
  return value.startsWith('#') ? value : `#${value}`;
}

function setPrimaryColorVariables(primary) {
  const primaryColor = normalizeThemeColor(primary);
  if (!primaryColor) return;

  const rootStyle = document.documentElement.style;
  const primaryRgb = colorUtils.hexToRgb(primaryColor);

  rootStyle.setProperty('--prev-color-primary', primaryColor);
  rootStyle.setProperty('--el-color-primary', primaryColor);
  rootStyle.setProperty('--el-color-primary-rgb', primaryRgb.join(','));

  for (let i = 1; i <= 9; i++) {
    const lightColor = colorUtils.getLightColor(primaryColor, i / 10);
    rootStyle.setProperty(`--prev-color-primary-light-${i}`, lightColor);
    rootStyle.setProperty(`--el-color-primary-light-${i}`, lightColor);
  }

  rootStyle.setProperty('--el-color-primary-dark-2', colorUtils.getDarkColor(primaryColor, 0.2));
}

// 全局主题
function onColorPickerChange() {
  setPrimaryColorVariables(getThemeConfig.value.primary);
  setLocalThemeConfig();
}

function setLocalTheme(val) {
  let themeSelect = themeList[val];
  themeSelect['--prev-border-color-lighter'] = '#ebeef5';
  if (['theme-1', 'theme-2'].includes(val)) {
    themeConfigStore.themeConfig.primary = '#0256FF'; //蓝黑蓝白
  } else if (['theme-3', 'theme-4'].includes(val)) {
    themeConfigStore.themeConfig.primary = '#41a584'; //绿黑绿白
  } else if (['theme-5', 'theme-6'].includes(val)) {
    themeConfigStore.themeConfig.primary = '#6954f0'; //紫黑紫白
  } else if (['theme-7', 'theme-8'].includes(val)) {
    themeConfigStore.themeConfig.primary = '#f34d37'; //红黑红白
  } else {
    themeConfigStore.themeConfig.primary = '#0256FF'; //默认蓝
  }
  settingsStore.changeSetting({
    key: 'theme',
    value: themeConfigStore.themeConfig.primary,
  });
  if (['classic'].includes(getThemeConfig.value.layout)) {
    //第三和第四种布局
    themeSelect['--prev-bg-topBar'] = '#282c34';
    themeSelect['--prev-bg-topBarColor'] = '#fff';
    themeSelect['--prev-bg-menuBarColor'] = '#fff';
    themeSelect['--prev-MenuActiveColor'] = '#fff';
    if (val == 'theme-1') {
      themeSelect['--prev-bg-menu-hover-ba-color'] = '#0256FF';
      themeSelect['--prev-bg-menuBar'] = '#282c34';
      themeSelect['--prev-border-color-lighter'] = '#282c34';
      themeSelect['--prev-MenuActiveColor'] = '#fff';
      // themeSelect["--prev-MenuActiveColor"] = themeConfigStore.themeConfig.primary;
    } else if (val == 'theme-3') {
      themeSelect['--prev-bg-menu-hover-ba-color'] = '#41b584';
      themeSelect['--prev-bg-menuBar'] = '#282c34';
      themeSelect['--prev-border-color-lighter'] = '#282c34';
    } else if (val == 'theme-5') {
      themeSelect['--prev-bg-menu-hover-ba-color'] = '#6954f0';
      themeSelect['--prev-bg-menuBar'] = '#282c34';
      themeSelect['--prev-border-color-lighter'] = '#282c34';
    } else if (val == 'theme-7') {
      themeSelect['--prev-bg-menu-hover-ba-color'] = '#f34d37';
      themeSelect['--prev-bg-menuBar'] = '#282c34';
      themeSelect['--prev-border-color-lighter'] = '#282c34';
    } else {
      themeSelect['--prev-border-color-lighter'] = '#ebeef5';
      themeSelect['--prev-bg-topBar'] = '#fff';
      themeSelect['--prev-bg-topBarColor'] = '#515a6e';
      themeSelect['--prev-bg-menuBarColor'] = '#515a6e';
      themeSelect['--prev-MenuActiveColor'] = themeConfigStore.themeConfig.primary;
    }
    themeSelect['--prev-bg-menuBar'] = '#fff';
    // themeSelect["--prev-MenuActiveColor"] = "#fff";
    themeSelect['--prev-bg-menuBarColor'] = '#282c34';
  } else if (['transverse'].includes(getThemeConfig.value.layout)) {
    //第三和第四种布局
    themeSelect['--prev-bg-topBar'] = '#282c34';
    themeSelect['--prev-bg-topBarColor'] = '#fff';
    themeSelect['--prev-bg-menuBarColor'] = '#fff';
    themeSelect['--prev-MenuActiveColor'] = '#fff';
    if (val == 'theme-1') {
      themeSelect['--prev-bg-menu-hover-ba-color'] = '#0256FF';
      themeSelect['--prev-bg-menuBar'] = '#282c34';
      themeSelect['--prev-border-color-lighter'] = '#282c34';
      themeSelect['--prev-MenuActiveColor'] = '#fff';
    } else if (val == 'theme-3') {
      themeSelect['--prev-bg-menu-hover-ba-color'] = '#41b584';
      themeSelect['--prev-bg-menuBar'] = '#282c34';
      themeSelect['--prev-border-color-lighter'] = '#282c34';
    } else if (val == 'theme-5') {
      themeSelect['--prev-bg-menu-hover-ba-color'] = '#6954f0';
      themeSelect['--prev-bg-menuBar'] = '#282c34';
      themeSelect['--prev-border-color-lighter'] = '#282c34';
    } else if (val == 'theme-7') {
      themeSelect['--prev-bg-menu-hover-ba-color'] = '#f34d37';
      themeSelect['--prev-bg-menuBar'] = '#282c34';
      themeSelect['--prev-border-color-lighter'] = '#282c34';
    } else {
      themeSelect['--prev-border-color-lighter'] = '#ebeef5';
      themeSelect['--prev-bg-topBar'] = '#fff';
      themeSelect['--prev-bg-topBarColor'] = '#515a6e';
      themeSelect['--prev-bg-menuBarColor'] = '#515a6e';
      themeSelect['--prev-MenuActiveColor'] = '#515a6e';
    }
  } else if (getThemeConfig.value.layout === 'columns') {
    //第二种布局
    themeSelect['--prev-bg-topBar'] = '#fff';
    themeSelect['--prev-bg-topBarColor'] = '#515a6e';
    themeSelect['--prev-bg-menuBar'] = '#fff';
    themeSelect['--prev-bg-menuBarColor'] = '#303133';
    themeSelect['--prev-border-color-lighter'] = '#ebeef5';
    if (val == 'theme-1') {
      themeSelect['--prev-bg-menu-hover-ba-color'] = '#E5EEFF';
      themeSelect['--prev-color-primary'] = '#0256FF';
      themeSelect['--prev-MenuActiveColor'] = '#0256FF';
    } else if (val == 'theme-3') {
      themeSelect['--prev-bg-menu-hover-ba-color'] = '#ecf8f3';
      themeSelect['--prev-color-primary'] = '#41b584';
      themeSelect['--prev-MenuActiveColor'] = '#41b584';
    } else if (val == 'theme-5' || val == 'theme-6') {
      themeSelect['--prev-bg-menu-hover-ba-color'] = '#f0eefe';
      themeSelect['--prev-color-primary'] = '#6954f0';
      themeSelect['--prev-MenuActiveColor'] = '#6954f0';
    } else if (val == 'theme-7') {
      themeSelect['--prev-bg-menu-hover-ba-color'] = '#feedeb';
      themeSelect['--prev-color-primary'] = '#f34d37';
      themeSelect['--prev-MenuActiveColor'] = '#f34d37';
    }
  } else {
    //默认布局
    if (val == 'theme-1') {
      themeSelect['--prev-bg-menuBar'] = '#282c34';
      themeSelect['--prev-color-primary'] = '#0256FF';
      themeSelect['--prev-bg-topBarColor'] = '#282c34';
      themeSelect['--prev-bg-topBar'] = '#fff';
      themeSelect['--prev-bg-menuBarColor'] = '#fff';
      themeSelect['--prev-MenuActiveColor'] = '#fff';
      themeSelect['--prev-bg-menu-hover-ba-color'] = '#0256FF';
    } else if (val == 'theme-3') {
      themeSelect['--prev-bg-menuBar'] = '#282c34';
      themeSelect['--prev-color-primary'] = '#41b584';
      themeSelect['--prev-bg-topBar'] = '#fff';
      themeSelect['--prev-bg-topBarColor'] = '#282c34';
      themeSelect['--prev-bg-menuBarColor'] = '#fff';
      themeSelect['--prev-MenuActiveColor'] = '#fff';
      themeSelect['--prev-bg-menu-hover-ba-color'] = '#41b584';
    } else if (val == 'theme-5') {
      themeSelect['--prev-bg-menuBar'] = '#282c34';
      themeSelect['--prev-bg-topBarColor'] = '#282c34';
      themeSelect['--prev-color-primary'] = '#6954f0';
      themeSelect['--prev-bg-topBar'] = '#fff';
      themeSelect['--prev-bg-menuBarColor'] = '#fff';
      themeSelect['--prev-MenuActiveColor'] = '#fff';
      themeSelect['--prev-bg-menu-hover-ba-color'] = '#6954f0';
    } else if (val == 'theme-7') {
      themeSelect['--prev-bg-menuBar'] = '#282c34';
      themeSelect['--prev-bg-topBar'] = '#fff';
      themeSelect['--prev-bg-topBarColor'] = '#282c34';
      themeSelect['--prev-color-primary'] = '#f34d37';
      themeSelect['--prev-bg-menuBarColor'] = '#fff';
      themeSelect['--prev-MenuActiveColor'] = '#fff';
      themeSelect['--prev-bg-menu-hover-ba-color'] = '#f34d37';
    }
  }

  for (let key in themeSelect) {
    document.documentElement.style.setProperty(key, themeSelect[key]);
  }
  nextTick((e) => {
    onColorPickerChange();
  });
}

function onMenuBgColorChange() {
  if (!getThemeConfig.value.menuBgColor) return;
  // 颜色加深
  document.documentElement.style.setProperty('--prev-bg-menuBar', getThemeConfig.value.menuBgColor);
  setLocalThemeConfig();
}

// 深色模式
function onAddDarkChange() {
  const body = document.documentElement;
  if (getThemeConfig.value.isIsDark) body.setAttribute('data-theme', 'dark');
  else body.setAttribute('data-theme', '');
  setLocalThemeConfig();
}

// 初始化：刷新页面时，设置了值，直接取缓存中的值进行初始化
function initLayoutConfig() {
  window.addEventListener(
    'load',
    () => {
      // 默认样式
      onColorPickerChange();
      // 灰色模式
      if (themeConfigStore.themeConfig.isGrayscale) onAddFilterChange('grayscale');
      // 色弱模式
      if (themeConfigStore.themeConfig.isInvert) onAddFilterChange('invert');
      // 深色模式
      if (themeConfigStore.themeConfig.isIsDark) onAddDarkChange();
    },
    { passive: true },
  );
}

// 存储布局配置
function setLocalThemeConfig() {
  Local.remove('JavaPlatThemeConfigPrev');
  Local.set('JavaPlatThemeConfigPrev', themeConfigStore.themeConfig);
  setLocalThemeConfigStyle();
}

// 存储布局配置全局主题样式（html根标签）
function setLocalThemeConfigStyle() {
  Local.set('JavaPlatThemeConfigStyle', document.documentElement.style.cssText);
}

// 布局配置弹窗打开
function openDrawer() {
  themeConfigStore.themeConfig.isDrawer = true;
}

// 关闭弹窗时，初始化变量
function onDrawerClose() {
  themeConfigStore.themeConfig.isDrawer = false;
  setLocalThemeConfig();
}

// 灰色模式/色弱模式
function onAddFilterChange(attr) {
  if (attr === 'grayscale') {
    if (themeConfigStore.themeConfig.isGrayscale) themeConfigStore.themeConfig.isInvert = false;
  } else {
    if (themeConfigStore.themeConfig.isInvert) themeConfigStore.themeConfig.isGrayscale = false;
  }
  const cssAttr =
    attr === 'grayscale'
      ? `grayscale(${themeConfigStore.themeConfig.isGrayscale ? 1 : 0})`
      : `invert(${themeConfigStore.themeConfig.isInvert ? '80%' : '0%'})`;
  const appEle = document.body;
  appEle.setAttribute('style', `filter: ${cssAttr};`);
  setLocalThemeConfig();
}

// 布局切换
function onSetLayout(layout) {
  Local.set('oldLayout', layout);
  if (themeConfigStore.themeConfig.layout === layout) return false;
  if (['classic', 'transverse'].includes(layout)) {
    themeConfigStore.themeConfig.isTagsview = false;
  } else {
    themeConfigStore.themeConfig.isTagsview = true;
  }
  themeConfigStore.themeConfig.layout = layout;
  themeConfigStore.themeConfig.isDrawer = false;
  themeConfigStore.themeConfig.columnsAsideStyle = 'columns-card';
  setLocalTheme(themeConfigStore.themeConfig.themeStyle);
}

// 菜单 / 顶栏背景等
function onBgColorPickerChange(bg, rgb) {
  document.documentElement.style.setProperty(`--prev-bg-${bg}`, rgb);
  setLocalThemeConfigStyle();
}

// 一键复制配置
function onCopyConfigClick() {
  themeConfigStore.themeConfig.isDrawer = false;
  let clipboardJS = new ClipboardJS('.copy-config-btn', {
    text: () => JSON.stringify(themeConfigStore.themeConfig),
  });
  clipboardJS.on('success', () => {
    ElMessage.success('配置复制成功');
    themeConfigStore.themeConfig.isDrawer = false;
    clipboardJS.destroy();
  });
  clipboardJS.on('error', () => {
    ElMessage.error('配置复制失败');
  });
}

// 一键恢复默认
function onResetConfigClick() {
  Local.clear();
  window.location.reload();
  Local.set('version', config.version);
}

// created
// 判断当前布局是否不相同，不相同则初始化当前布局的样式，防止监听窗口大小改变时，布局配置logo、菜单背景等部分布局失效问题
Local.set('frequency', 1);
// 监听窗口大小改变，非默认布局，设置成默认布局（适配移动端）
function onLayoutMobileResize(res) {
  if (themeConfigStore.themeConfig.layout === res.layout) return false;
  themeConfigStore.themeConfig.layout = res.layout;
  themeConfigStore.themeConfig.isDrawer = false;
  themeConfigStore.themeConfig.isCollapse = false;
}
bus.on('layoutMobileResize', onLayoutMobileResize);
setLocalTheme(themeConfigStore.themeConfig.themeStyle);

onMounted(() => {
  initLayoutConfig();
});

onBeforeUnmount(() => {
  bus.off('layoutMobileResize', onLayoutMobileResize);
});

defineExpose({ openDrawer });
</script>
<style scoped lang="scss">
.w10 {
  width: 10px;
}
.mr5 {
  margin-right: 5px;
}
:deep(.el-drawer__header) {
  margin-bottom: 0;
}
:deep(.el-radio-button--mini .el-radio-button__inner) {
  padding: 7px 8px;
}

.layout-breadcrumb-seting-bar {
  // height: calc(100vh - 50px);
  padding: 0 15px;
  :deep(.el-scrollbar__view) {
    // overflow-x: auto !important;
    overflow-x: hidden;
  }
  .layout-breadcrumb-seting-bar-flex {
    display: flex;
    align-items: center;
    &-label {
      flex: 1;
      color: var(--prev-color-text-primary);
    }
  }
  .layout-drawer-content-flex {
    overflow: hidden;
    display: flex;
    flex-wrap: wrap;
    align-content: center;
    justify-content: center;
    margin: 0 -5px;
    .layout-drawer-content-item.drawer-layout-active {
      border: 1px solid;
      border-color: var(--prev-color-primary);
    }
    .layout-drawer-content-item:hover {
      transition: all 0.3s ease-in-out;
      border: 1px solid;
      border-color: var(--prev-color-primary);
    }
    .layout-drawer-content-item {
      width: 107px;
      height: 70px;
      cursor: pointer;
      border: 1px solid rgba(0, 0, 0, 0);
      position: relative;
      padding: 6px;
      background: #ffffff;
      box-shadow: 0px 2px 5px 0px rgba(0, 0, 0, 0.08);
      border-radius: 6px;
      opacity: 1;
      margin: 10px;

      .el-container {
        height: 100%;
        .el-aside-dark {
          opacity: 0.5;
          background-color: var(--prev-tag-active-color);
          border-radius: 2px;
        }
        .el-aside {
          background-color: var(--prev-tag-active-color);
          border-radius: 2px;
        }
        .el-header {
          border-radius: 2px;
          background-color: var(--prev-color-seting-header);
        }
        .el-main {
          border-radius: 2px;
          border: 1px dashed var(--prev-color-primary);
          padding: 0;
          background-color: var(--prev-color-seting-main);
        }
      }
      .el-circular {
        border-radius: 2px;
        overflow: hidden;
        border: 1px solid transparent;
        transition: all 0.3s ease-in-out;
      }

      .layout-tips-warp,
      .layout-tips-warp-active {
        transition: all 0.3s ease-in-out;
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
        border: 1px solid;
        border-color: var(--prev-color-primary-light-5);
        border-radius: 100%;
        padding: 4px;
        .layout-tips-box {
          transition: inherit;
          width: 30px;
          height: 30px;
          z-index: 9;
          border: 1px solid;
          border-color: var(--prev-color-primary-light-5);
          border-radius: 100%;
          .layout-tips-txt {
            transition: inherit;
            position: relative;
            top: 5px;
            font-size: 12px;
            line-height: 1;
            letter-spacing: 2px;
            white-space: nowrap;
            color: var(--prev-color-primary-light-5);
            text-align: center;
            transform: rotate(30deg);
            left: -1px;
            background-color: var(--prev-color-seting-main);
            width: 32px;
            height: 17px;
            line-height: 17px;
          }
        }
      }
      .layout-tips-warp-active {
        border: 1px solid;
        border-color: var(--prev-color-primary);
        .layout-tips-box {
          border: 1px solid;
          border-color: var(--prev-color-primary);
          .layout-tips-txt {
            color: var(--prev-color-primary) !important;
            background-color: var(--prev-color-seting-main) !important;
          }
        }
      }
      &:hover {
        .layout-tips-warp {
          transition: all 0.3s ease-in-out;
          border-color: var(--prev-color-primary);
          .layout-tips-box {
            transition: inherit;
            border-color: var(--prev-color-primary);
            .layout-tips-txt {
              transition: inherit;
              color: var(--prev-color-primary) !important;
              background-color: var(--prev-color-seting-main) !important;
            }
          }
        }
      }
    }
  }
  .copy-config {
    margin: 10px 0;
    .copy-config-btn {
      width: 100%;
      margin-top: 15px;
    }
    .copy-config-btn-reset {
      width: 100%;
      margin: 10px 0 0;
    }
  }
}
</style>
