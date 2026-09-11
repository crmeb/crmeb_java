import { elementComponentMap } from '../elementComponents';

const ElRadio = elementComponentMap['el-radio'];
const ElRadioButton = elementComponentMap['el-radio-button'];

export default {
  options(h, conf, key) {
    const list = [];
    conf.__slot__.options.forEach((item) => {
      if (conf.__config__.optionType === 'button') {
        list.push(<ElRadioButton value={item.value}>{item.label}</ElRadioButton>);
      } else {
        list.push(
          <ElRadio value={item.value} border={conf.border}>
            {item.label}
          </ElRadio>,
        );
      }
    });
    return list;
  },
};
