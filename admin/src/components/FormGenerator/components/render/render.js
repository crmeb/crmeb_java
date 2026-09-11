import { defineComponent, h, resolveComponent } from 'vue';
import { elementComponentMap } from './elementComponents';

function normalizeElementSize(size) {
  const sizeMap = { medium: 'default', mini: 'small' };
  return sizeMap[size] || size;
}

function vModel(emit, dataObject, defaultValue) {
  dataObject.modelValue = defaultValue;
  dataObject['onUpdate:modelValue'] = (val) => {
    // 2024 12 05 添加所有的input 不得全部为空格
    let trimmedVal = val;
    if (typeof val === 'string') {
      trimmedVal = String(val).trim();
    }
    emit('input', trimmedVal);
    emit('update:modelValue', trimmedVal);
  };
}

function getSlotName(key) {
  return ['options', 'list-type', 'default'].includes(key) ? 'default' : key;
}

function appendSlot(slots, name, slotValue) {
  const valueList = Array.isArray(slotValue) ? slotValue.flat() : [slotValue];
  const validList = valueList.filter((item) => item !== undefined && item !== null && item !== '');
  if (!validList.length) return;
  const existingSlot = slots[name];
  slots[name] = () => [...(existingSlot ? existingSlot() : []), ...validList];
}

function resolveFormComponent(tag) {
  return elementComponentMap[tag] || resolveComponent(tag);
}

/**
 * 将./slots中的文件挂载到对象componentChild上
 * 文件名为key，对应JSON配置中的__config__.tag
 * 文件内容为value，解析JSON配置中的__slot__
 */
const slotModules = import.meta.glob(['./slots/*.js', './slots/*.jsx'], { eager: true });
const componentChild = {};
Object.keys(slotModules).forEach((key) => {
  const tag = key.replace(/^.*\/(.*)\.\w+$/, '$1');
  const value = slotModules[key].default;
  componentChild[tag] = value;
});

export default defineComponent({
  name: 'FormRender',
  props: ['conf'],
  emits: ['input', 'update:modelValue'],
  setup(props, { emit }) {
    return () => {
      const confClone = JSON.parse(JSON.stringify(props.conf));
      const slots = {};

      const childObjs = componentChild[confClone.__config__.tag];
      if (childObjs) {
        Object.keys(childObjs).forEach((key) => {
          const childFunc = childObjs[key];
          if (confClone.__slot__ && confClone.__slot__[key]) {
            appendSlot(slots, getSlotName(key), childFunc(h, confClone, key));
          }
        });
      }

      // Vue3 中 h() 的数据结构：props/events 扁平化，不再用 attrs/props/on 分离
      const dataObject = { style: {} };

      Object.keys(confClone).forEach((key) => {
        const val = confClone[key];
        if (key === '__vModel__') {
          vModel(emit, dataObject, confClone.__config__.defaultValue);
        } else if (key === 'style') {
          dataObject.style = val;
        } else if (key !== '__config__' && key !== '__slot__') {
          dataObject[key] = key === 'size' ? normalizeElementSize(val) : val;
        }
      });

      return h(resolveFormComponent(confClone.__config__.tag), dataObject, Object.keys(slots).length ? slots : null);
    };
  },
});
