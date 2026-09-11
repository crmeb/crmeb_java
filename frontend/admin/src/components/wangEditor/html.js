// wangeditor v5 自定义「HTML 源码」按钮菜单
// 通过 Boot.registerModule 注册，按钮点击后通过 mitt 事件总线触发外部处理。
import { Boot } from '@wangeditor/editor';
import bus from '@/utils/bus';

// 菜单唯一 key
const menuKey = 'alertHtml';

// IButtonMenu 实现
class HtmlMenu {
  constructor() {
    this.title = 'HTML'; // 鼠标悬停提示
    this.tag = 'button'; // 按钮形态
    // 按钮内部展示内容（可放文字或 svg）
    this.iconSvg =
      '<svg viewBox="0 0 1024 1024"><path d="M128 128h768v768H128z" fill="none"/><path d="M384 320l-128 192 128 192M640 320l128 192-128 192" stroke="currentColor" stroke-width="64" fill="none" stroke-linecap="round" stroke-linejoin="round"/></svg>';
  }

  // 菜单按钮点击：触发外部 HTML 源码切换
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
    // 触发 index.vue 中的 HTML 源码切换逻辑
    bus.emit('Html');
  }
}

const menuModule = {
  key: menuKey,
  factory() {
    return new HtmlMenu();
  },
};

// 注册模块（多次引入时避免重复注册报错）
try {
  Boot.registerModule(menuModule);
} catch (e) {
  // 已注册则忽略
}

export { menuKey as htmlMenuKey };
export default HtmlMenu;
