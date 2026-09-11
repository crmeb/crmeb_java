<template>
  <el-color-picker
    v-model="theme"
    :predefine="['#409EFF', '#1890ff', '#304156', '#212121', '#11a983', '#13c2c2', '#6959CD', '#f5222d']"
    class="theme-picker"
    popper-class="theme-picker-dropdown"
  />
</template>

<script setup>
// NOTE: 原 ThemePicker 基于 element-ui 主题 chalk 替换实现，依赖 element-ui 的 theme-chalk/index.css。
// 迁移到 element-plus 后该主题替换机制不再适用（element-plus 使用 CSS 变量）。
// 此处保留结构与逻辑，但 element-ui 相关部分已注释/适配，可能无法完全工作。
import { ref, computed, watch } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { useSettingsStore } from '@/store/modules/settings';

// element-ui version 不再可用（已迁移到 element-plus），保留常量占位
// const version = require('element-ui/package.json').version; // element-ui version from node_modules
const version = ''; // element-ui 已移除，置空
const ORIGINAL_THEME = '#409EFF'; // default color

const emit = defineEmits(['change']);

const settingsStore = useSettingsStore();

const chalk = ref(''); // content of theme-chalk css
const theme = ref('');

const defaultTheme = computed(() => settingsStore.theme);

watch(
  defaultTheme,
  (val) => {
    theme.value = val;
  },
  { immediate: true },
);

watch(theme, async (val) => {
  const oldVal = chalk.value ? theme.value : ORIGINAL_THEME;
  if (typeof val !== 'string') return;
  const themeCluster = getThemeCluster(val.replace('#', ''));
  const originalCluster = getThemeCluster(oldVal.replace('#', ''));

  const $message = ElMessage({
    message: '  Compiling the theme',
    customClass: 'theme-message',
    type: 'success',
    duration: 0,
    iconClass: 'el-icon-loading',
  });

  const getHandler = (variable, id) => {
    return () => {
      const originalClusterInner = getThemeCluster(ORIGINAL_THEME.replace('#', ''));
      const newStyle = updateStyle(variableRef(variable), originalClusterInner, themeCluster);

      let styleTag = document.getElementById(id);
      if (!styleTag) {
        styleTag = document.createElement('style');
        styleTag.setAttribute('id', id);
        document.head.appendChild(styleTag);
      }
      styleTag.innerText = newStyle;
    };
  };

  // 读取 chalk/variables 变量值（兼容原 this[variable] 访问）
  const variableRef = (variable) => {
    if (variable === 'chalk') return chalk.value;
    return '';
  };

  if (!chalk.value) {
    const url = `https://unpkg.com/element-ui@${version}/lib/theme-chalk/index.css`;
    await getCSSString(url, 'chalk');
  }

  const chalkHandler = getHandler('chalk', 'chalk-style');

  chalkHandler();

  const styles = [].slice.call(document.querySelectorAll('style')).filter((style) => {
    const text = style.innerText;
    return new RegExp(oldVal, 'i').test(text) && !/Chalk Variables/.test(text);
  });
  styles.forEach((style) => {
    const { innerText } = style;
    if (typeof innerText !== 'string') return;
    style.innerText = updateStyle(innerText, originalCluster, themeCluster);
  });

  emit('change', val);

  $message.close();
});

function updateStyle(style, oldCluster, newCluster) {
  let newStyle = style;
  oldCluster.forEach((color, index) => {
    newStyle = newStyle.replace(new RegExp(color, 'ig'), newCluster[index]);
  });
  return newStyle;
}

function getCSSString(url, variable) {
  return new Promise((resolve) => {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
      if (xhr.readyState === 4 && xhr.status === 200) {
        // 原 this[variable] = ... → 此处仅处理 chalk
        if (variable === 'chalk') {
          chalk.value = xhr.responseText.replace(/@font-face{[^}]+}/, '');
        }
        resolve();
      }
    };
    xhr.open('GET', url);
    xhr.send();
  });
}

function getThemeCluster(theme) {
  const tintColor = (color, tint) => {
    let red = parseInt(color.slice(0, 2), 16);
    let green = parseInt(color.slice(2, 4), 16);
    let blue = parseInt(color.slice(4, 6), 16);

    if (tint === 0) {
      // when primary color is in its rgb space
      return [red, green, blue].join(',');
    } else {
      red += Math.round(tint * (255 - red));
      green += Math.round(tint * (255 - green));
      blue += Math.round(tint * (255 - blue));

      red = red.toString(16);
      green = green.toString(16);
      blue = blue.toString(16);

      return `#${red}${green}${blue}`;
    }
  };

  const shadeColor = (color, shade) => {
    let red = parseInt(color.slice(0, 2), 16);
    let green = parseInt(color.slice(2, 4), 16);
    let blue = parseInt(color.slice(4, 6), 16);

    red = Math.round((1 - shade) * red);
    green = Math.round((1 - shade) * green);
    blue = Math.round((1 - shade) * blue);

    red = red.toString(16);
    green = green.toString(16);
    blue = blue.toString(16);

    return `#${red}${green}${blue}`;
  };

  const clusters = [theme];
  for (let i = 0; i <= 9; i++) {
    clusters.push(tintColor(theme, Number((i / 10).toFixed(2))));
  }
  clusters.push(shadeColor(theme, 0.1));
  return clusters;
}
</script>

<style>
.theme-message,
.theme-picker-dropdown {
  z-index: 99999 !important;
}

.theme-picker .el-color-picker__trigger {
  height: 26px !important;
  width: 26px !important;
  padding: 2px;
}

.theme-picker-dropdown .el-color-dropdown__link-btn {
  display: none;
}
</style>
