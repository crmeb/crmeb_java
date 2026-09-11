import { createVNode, render } from 'vue';

// Vue3 替代 Vue.extend + new Constructor() + $mount 的单例弹窗模式
const goodListFrom = {
  install(app) {
    let instance = null;
    let instancePromise = null;

    function getInstance() {
      if (instance) return Promise.resolve(instance);
      if (instancePromise) return instancePromise;
      instancePromise = import('./index.vue').then(({ default: uploadFromComponent }) => {
        const container = document.createElement('div');
        document.body.appendChild(container);

        const vnode = createVNode(uploadFromComponent);
        vnode.appContext = app._context;
        render(vnode, container);
        instance = vnode.component && vnode.component.exposed;
        return instance;
      });
      return instancePromise;
    }

    app.config.globalProperties.$modalGoodList = function (callback, handleNum, row) {
      getInstance().then((modalInstance) => {
        if (!modalInstance || typeof modalInstance.open !== 'function') return;
        modalInstance.open({
          callback,
          handleNum,
          checked: row,
        });
      });
    };
  },
};
export default goodListFrom;
