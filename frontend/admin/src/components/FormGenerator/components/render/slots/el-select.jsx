import { elementComponentMap } from '../elementComponents';

const ElOption = elementComponentMap['el-option'];

export default {
  options(h, conf, key) {
    const list = [];
    conf.__slot__.options.forEach((item) => {
      list.push(<ElOption label={item.label} value={item.value} disabled={item.disabled} />);
    });
    return list;
  },
};
