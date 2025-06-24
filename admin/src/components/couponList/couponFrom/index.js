import couponFromComponent from './index.vue';
const couponFrom = {};
couponFrom.install = function (Vue, options) {
  const ToastConstructor = Vue.extend(couponFromComponent);
  // 生成一个该子类的实例
  const instance = new ToastConstructor();
  instance.$mount(document.createElement('div'));
  document.body.appendChild(instance.$el);
  Vue.prototype.$modalCoupon = function (handle, keyNum, coupons = [], callback, userIds = '', userType = '') {
    instance.visible = true;
    instance.handle = handle;
    instance.keyNum = keyNum;
    instance.coupons = coupons;
    instance.userIds = userIds;
    instance.callback = callback;
    instance.userType = userType;
  };
};
export default couponFrom;
