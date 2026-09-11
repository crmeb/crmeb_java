const OBSERVER_KEY = '__lazyObserver__';

function clearObserver(el) {
  if (el[OBSERVER_KEY]) {
    el[OBSERVER_KEY].disconnect();
    delete el[OBSERVER_KEY];
  }
}

function setSource(el, src) {
  if (!src) return;

  if (el.tagName === 'IMG') {
    el.setAttribute('src', src);
    return;
  }

  el.style.backgroundImage = `url("${src}")`;
}

function bindLazy(el, binding) {
  clearObserver(el);

  const src = binding.value;
  if (!src) return;

  if (typeof window === 'undefined' || !window.IntersectionObserver) {
    setSource(el, src);
    return;
  }

  const observer = new IntersectionObserver((entries) => {
    const [entry] = entries;
    if (entry.isIntersecting || entry.intersectionRatio > 0) {
      setSource(el, src);
      clearObserver(el);
    }
  });

  el[OBSERVER_KEY] = observer;
  observer.observe(el);
}

export default {
  mounted: bindLazy,
  updated(el, binding) {
    if (binding.value !== binding.oldValue) {
      bindLazy(el, binding);
    }
  },
  unmounted(el) {
    clearObserver(el);
  },
};
