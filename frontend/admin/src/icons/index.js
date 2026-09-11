// SVG 图标加载由 vite-plugin-svg-icons 在构建/开发时处理（见 vite.config.js）。
// 此处仅做 svg-icon 组件的全局注册。
import SvgIcon from '@/components/SvgIcon/index.vue'; // svg component

export function setupIcons(app) {
  app.component('svg-icon', SvgIcon);
}

export default {
  install(app) {
    app.component('svg-icon', SvgIcon);
  },
};
