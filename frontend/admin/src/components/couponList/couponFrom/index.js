import { createVNode, render } from 'vue';

// Vue3 替代 Vue.extend + new Constructor() + $mount 的单例弹窗模式
const couponFrom = {
  install(app) {
    let instance = null;
    let instancePromise = null;

    function getInstance() {
      if (instance) return Promise.resolve(instance);
      if (instancePromise) return instancePromise;
      instancePromise = import('./index.vue').then(({ default: couponFromComponent }) => {
        const container = document.createElement('div');
        document.body.appendChild(container);

        const vnode = createVNode(couponFromComponent);
        vnode.appContext = app._context;
        render(vnode, container);
        instance = vnode.component && vnode.component.exposed;
        return instance;
      });
      return instancePromise;
    }

    app.config.globalProperties.$modalCoupon = function (handle, keyNum, coupons = [], callback, userIds = '', userType = '') {
      getInstance().then((modalInstance) => {
        if (!modalInstance || typeof modalInstance.open !== 'function') return;
        modalInstance.open({
          handle,
          keyNum,
          coupons,
          callback,
          userIds,
          userType,
        });
      });
    };
  },
};
export default couponFrom;
