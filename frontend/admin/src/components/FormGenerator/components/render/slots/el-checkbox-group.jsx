import { elementComponentMap } from '../elementComponents';

const ElCheckbox = elementComponentMap['el-checkbox'];
const ElCheckboxButton = elementComponentMap['el-checkbox-button'];

export default {
  options(h, conf, key) {
    const list = [];
    conf.__slot__.options.forEach((item) => {
      if (conf.__config__.optionType === 'button') {
        list.push(<ElCheckboxButton value={item.value}>{item.label}</ElCheckboxButton>);
      } else {
        list.push(
          <ElCheckbox value={item.value} border={conf.border}>
            {item.label}
          </ElCheckbox>,
        );
      }
    });
    return list;
  },
};
