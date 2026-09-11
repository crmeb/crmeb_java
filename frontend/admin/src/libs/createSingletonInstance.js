// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { createVNode, render } from 'vue';

/**
 * 在 body 上挂载一个单例组件实例，返回该实例的代理对象。
 * 用于替代 Vue2 时代 Vue.extend + new Constructor() + $mount 的单例弹窗模式。
 *
 * @param {import('vue').Component} component 组件定义
 * @param {Object} [props] 初始 props
 * @returns {Object} 组件实例代理（可通过代理读写其 setup 暴露的数据 / Options API 的 data）
 *
 * 说明：返回的是组件内部通过 defineExpose / Options 暴露的内容 + 公共实例属性。
 * 为兼容旧代码中直接 instance.xxx = yyy 的写法，组件需通过 expose 暴露相应字段，
 * 或组件本身用 Options API（Vue3 仍支持 instance 的 data 字段直接赋值）。
 */
export function createSingletonInstance(component, props = {}) {
  const container = document.createElement('div');
  document.body.appendChild(container);

  const vnode = createVNode(component, props);
  // 提供 app context，使组件内部能用 inject、全局组件等
  // 这里使用当前活跃的 app context（若在 install 内调用则已设置）
  vnode.appContext = window.__APP_CONTEXT__ || null;
  render(vnode, container);

  const instance = vnode.component;

  // 销毁方法
  instance.destroy = () => {
    render(null, container);
    if (container.parentNode) {
      container.parentNode.removeChild(container);
    }
  };

  return instance;
}
