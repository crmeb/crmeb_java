import { ElButton } from 'element-plus/es/components/button/index.mjs';
import { ElCascader } from 'element-plus/es/components/cascader/index.mjs';
import { ElCheckbox, ElCheckboxButton, ElCheckboxGroup } from 'element-plus/es/components/checkbox/index.mjs';
import { ElCol } from 'element-plus/es/components/col/index.mjs';
import { ElColorPicker } from 'element-plus/es/components/color-picker/index.mjs';
import { ElDatePicker } from 'element-plus/es/components/date-picker/index.mjs';
import { ElForm, ElFormItem } from 'element-plus/es/components/form/index.mjs';
import { ElInput } from 'element-plus/es/components/input/index.mjs';
import { ElInputNumber } from 'element-plus/es/components/input-number/index.mjs';
import { ElLink } from 'element-plus/es/components/link/index.mjs';
import { ElOption, ElSelect } from 'element-plus/es/components/select/index.mjs';
import { ElRadio, ElRadioButton, ElRadioGroup } from 'element-plus/es/components/radio/index.mjs';
import { ElRate } from 'element-plus/es/components/rate/index.mjs';
import { ElRow } from 'element-plus/es/components/row/index.mjs';
import { ElSlider } from 'element-plus/es/components/slider/index.mjs';
import { ElTimePicker } from 'element-plus/es/components/time-picker/index.mjs';
import { ElUpload } from 'element-plus/es/components/upload/index.mjs';
import CompatElSwitch from '@/components/ElementPlusCompat/Switch.vue';
import SelfUpload from '@/components/uploadPicture/forGenrator/index.vue';

export const elementComponentMap = {
  'el-button': ElButton,
  'el-cascader': ElCascader,
  'el-checkbox': ElCheckbox,
  'el-checkbox-button': ElCheckboxButton,
  'el-checkbox-group': ElCheckboxGroup,
  'el-col': ElCol,
  'el-color-picker': ElColorPicker,
  'el-date-picker': ElDatePicker,
  'el-form': ElForm,
  'el-form-item': ElFormItem,
  'el-input': ElInput,
  'el-input-number': ElInputNumber,
  'el-link': ElLink,
  'el-option': ElOption,
  'el-radio': ElRadio,
  'el-radio-button': ElRadioButton,
  'el-radio-group': ElRadioGroup,
  'el-rate': ElRate,
  'el-row': ElRow,
  'el-select': ElSelect,
  'el-slider': ElSlider,
  'el-switch': CompatElSwitch,
  'el-time-picker': ElTimePicker,
  'el-upload': ElUpload,
  'self-upload': SelfUpload,
};
