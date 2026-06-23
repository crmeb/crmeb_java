/**
 * 防止短时间内重复点击。
 */
const dbClick = {
  inserted(el) {
    el.addEventListener('click', () => {
      if (!el.disabled) {
        el.disabled = true;
        el.style.cursor = 'not-allowed';
        setTimeout(() => {
          el.style.cursor = 'pointer';
          el.disabled = false;
        }, 1000);
      }
    });
  },
};

export default dbClick;
