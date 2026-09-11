// wangeditor v5 自定义「视频」按钮菜单
// 通过 Boot.registerModule 注册，按钮点击后通过 mitt 事件总线触发外部视频上传弹窗。
import { Boot } from '@wangeditor/editor';
import bus from '@/utils/bus';

// 菜单唯一 key
const menuKey = 'alertMenuKey';

// IButtonMenu 实现
class VideoMenu {
  constructor() {
    this.title = '视频'; // 鼠标悬停提示
    this.tag = 'button'; // 按钮形态
    // 视频按钮图标（使用项目里已有的 iconshipin 字体图标不便于 svg 化，这里用 svg 代替）
    this.iconSvg =
      '<svg viewBox="0 0 1024 1024"><path d="M896 224H128c-35.3 0-64 28.7-64 64v448c0 35.3 28.7 64 64 64h768c35.3 0 64-28.7 64-64V288c0-35.3-28.7-64-64-64zM384 672V352l320 160-320 160z" fill="currentColor"/></svg>';
  }

  getValue() {
    return '';
  }

  isActive() {
    return false;
  }

  isDisabled() {
    return false;
  }

  exec(editor) {
    // 触发 index.vue 中的视频上传逻辑
    bus.emit('Video');
  }
}

const menuModule = {
  key: menuKey,
  factory() {
    return new VideoMenu();
  },
};

// 注册模块（多次引入时避免重复注册报错）
try {
  Boot.registerModule(menuModule);
} catch (e) {
  // 已注册则忽略
}

export { menuKey as videoMenuKey };
export default VideoMenu;
