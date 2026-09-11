export default {
  prepend(h, conf, key) {
    return conf.__slot__[key];
  },
  append(h, conf, key) {
    return conf.__slot__[key];
  },
};
