function isPlainObject(value) {
  return Object.prototype.toString.call(value) === '[object Object]';
}

export function cloneConfig(value) {
  if (value === undefined) return undefined;
  return JSON.parse(JSON.stringify(value));
}

export function fillMissingConfig(target, source) {
  if (!isPlainObject(target) || !isPlainObject(source)) return target;

  Object.keys(source).forEach((key) => {
    const sourceValue = source[key];
    const targetValue = target[key];

    if (targetValue === undefined || targetValue === null) {
      target[key] = cloneConfig(sourceValue);
      return;
    }

    if (isPlainObject(targetValue) && isPlainObject(sourceValue)) {
      fillMissingConfig(targetValue, sourceValue);
    }
  });

  return target;
}

export function mergeConfigDefaults(config, defaults) {
  const target = isPlainObject(config) ? cloneConfig(config) : {};
  return fillMissingConfig(target, defaults);
}
