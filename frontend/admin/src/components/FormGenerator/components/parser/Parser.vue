<script lang="jsx">
import { defineComponent, reactive, watch } from 'vue';
import render from '@/components/FormGenerator/components/render/render.js';
import { elementComponentMap } from '@/components/FormGenerator/components/render/elementComponents';
import { Debounce } from '@/utils/validate';

const ElButton = elementComponentMap['el-button'];
const ElCol = elementComponentMap['el-col'];
const ElForm = elementComponentMap['el-form'];
const ElFormItem = elementComponentMap['el-form-item'];
const ElLink = elementComponentMap['el-link'];
const ElRow = elementComponentMap['el-row'];

const ruleTrigger = {
  'el-input': 'blur',
  'el-input-number': 'blur',
  'el-select': 'change',
  'el-radio-group': 'change',
  'el-checkbox-group': 'change',
  'el-cascader': 'change',
  'el-time-picker': 'change',
  'el-date-picker': 'change',
  'el-rate': 'change',
};

function normalizeElementSize(size) {
  const sizeMap = { medium: 'default', mini: 'small' };
  return sizeMap[size] || size;
}

function isNumberLike(value) {
  return value !== '' && value !== null && value !== undefined && !Number.isNaN(Number(value));
}

function toNumberOrNull(value) {
  if (value === '' || value === null || value === undefined) return null;
  return isNumberLike(value) ? Number(value) : value;
}

// ElCheckboxGroup 的 modelValue 必须为数组，后端可能以单个字符串、逗号分隔字符串或 JSON 数组字符串存储
function toCheckboxArray(value) {
  if (Array.isArray(value)) return value;
  if (value === '' || value === null || value === undefined) return [];
  if (typeof value === 'string') {
    const trimmed = value.trim();
    if (trimmed.startsWith('[') && trimmed.endsWith(']')) {
      try {
        const parsed = JSON.parse(trimmed);
        if (Array.isArray(parsed)) return parsed;
      } catch (e) {
        // 非合法 JSON，按逗号分隔处理
      }
    }
    return trimmed.split(',').filter((item) => item !== '');
  }
  return [value];
}

function normalizeComponentValue(config, value) {
  if (!config) return value;
  if (config.tag === 'el-input-number') return toNumberOrNull(value);
  if (config.tag === 'el-slider') {
    return Array.isArray(value) ? value.map((item) => toNumberOrNull(item)) : toNumberOrNull(value);
  }
  if (config.tag === 'el-rate') return toNumberOrNull(value);
  if (config.tag === 'el-checkbox-group') return toCheckboxArray(value);
  return value;
}

function renderFrom(h, vm) {
  const { formConfCopy } = vm.state;
  return (
    <ElRow class="form-parser-row" gutter={formConfCopy.gutter}>
      <ElForm
        size={normalizeElementSize(formConfCopy.size)}
        label-position={formConfCopy.labelPosition}
        disabled={formConfCopy.disabled}
        label-width={`${formConfCopy.labelWidth}px`}
        ref={vm.setFormRef}
        model={vm.state[formConfCopy.formModel]}
        rules={vm.state[formConfCopy.formRules]}
      >
        {renderFormItem(h, formConfCopy.fields, vm)}
        {formConfCopy.formBtns && formBtns(h, vm)}
      </ElForm>
    </ElRow>
  );
}

function formBtns(h, vm) {
  return (
    <ElCol>
      <ElFormItem class="dialog-footer-inner zdy-btn">
        <ElButton onClick={vm.closeDialog} class="closeBtn">
          取消
        </ElButton>
        <ElButton class="saveBtn" type="primary" onClick={vm.debouncedSubmitForm}>
          提交
        </ElButton>
      </ElFormItem>
    </ElCol>
  );
}

function renderFormItem(h, elementList, vm) {
  return elementList.map((scheme) => {
    const config = scheme.__config__;
    const layout = layouts[config.layout];
    if (layout) {
      return layout(h, scheme, vm);
    }
    throw new Error(`没有与${config.layout}匹配的layout`);
  });
}

function renderChildren(h, scheme, vm) {
  const config = scheme.__config__;
  if (!Array.isArray(config.children)) return null;
  return renderFormItem(h, config.children, vm);
}

function setValue(event, config, scheme, vm) {
  config.defaultValue = event;
  vm.state[vm.props.formConf.formModel][scheme.__vModel__] = event;
}

function buildListeners(scheme, vm) {
  const config = scheme.__config__;
  const methods = vm.props.formConf.__methods__ || {};
  const listeners = {};

  // 给__methods__中的方法绑定this和event
  Object.keys(methods).forEach((key) => {
    listeners[key] = (event) => methods[key].call(vm, event);
  });
  // 响应 render.js 中的 vModel $emit('input', val)
  listeners.input = (event) => setValue(event, config, scheme, vm);

  return listeners;
}

function toEventKey(name) {
  return `on${name.charAt(0).toUpperCase()}${name.slice(1)}`;
}

function buildRenderProps(scheme, listeners) {
  const listenerProps = {};
  Object.keys(listeners).forEach((key) => {
    listenerProps[toEventKey(key)] = listeners[key];
  });
  return { conf: scheme, ...listenerProps };
}
const layouts = {
  colFormItem(h, scheme, vm) {
    const config = scheme.__config__;
    const listeners = buildListeners(scheme, vm);
    let labelWidth = config.labelWidth ? `${config.labelWidth}px` : null;
    if (config.showLabel === false) labelWidth = '0';
    if (config.tips && !config.tipsIsLink) {
      return (
        <ElCol span={config.span}>
          <ElFormItem label-width={labelWidth} prop={scheme.__vModel__} label={config.showLabel ? config.label : ''}>
            <render {...buildRenderProps(scheme, listeners)} />
            <div>
              <span class="tips-info">{config.tipsDesc}</span>
            </div>
          </ElFormItem>
        </ElCol>
      );
    } else if (config.tips && config.tipsIsLink) {
      return (
        <ElCol span={config.span}>
          <ElFormItem label-width={labelWidth} prop={scheme.__vModel__} label={config.showLabel ? config.label : ''}>
            <render {...buildRenderProps(scheme, listeners)} />
            <div>
              <ElLink class="tips-info" type="info" href={config.tipsLink} target="_blank">
                {config.tipsDesc}
              </ElLink>
            </div>
          </ElFormItem>
        </ElCol>
      );
    } else {
      return (
        <ElCol span={config.span}>
          <ElFormItem label-width={labelWidth} prop={scheme.__vModel__} label={config.showLabel ? config.label : ''}>
            <render {...buildRenderProps(scheme, listeners)} />
          </ElFormItem>
        </ElCol>
      );
    }
  },
  rowFormItem(h, scheme, vm) {
    let child = renderChildren(h, scheme, vm);
    if (scheme.type === 'flex') {
      child = (
        <ElRow type={scheme.type} justify={scheme.justify} align={scheme.align}>
          {child}
        </ElRow>
      );
    }
    return (
      <ElCol span={scheme.span}>
        <ElRow gutter={scheme.gutter}>{child}</ElRow>
      </ElCol>
    );
  },
};

export default defineComponent({
  components: {
    render,
  },
  props: {
    formConf: {
      type: Object,
      required: true,
    },
    formEditData: {
      type: Object,
    },
    isEdit: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['resetForm', 'closeDialog', 'submit'],
  setup(props, { emit, expose }) {
    const state = reactive({
      formConfCopy: JSON.parse(JSON.stringify(props.formConf)),
      [props.formConf.formModel]: {},
      [props.formConf.formRules]: {},
    });

    function applyEditData(componentList) {
      if (!props.isEdit || !props.formEditData || !componentList) return;
      componentList.forEach((conf) => {
        const vModel = conf.__vModel__;
        const hasValueForEdit = vModel ? props.formEditData[vModel] : undefined;
        if (hasValueForEdit !== undefined && hasValueForEdit !== null && hasValueForEdit !== '') {
          conf.__config__.defaultValue = normalizeComponentValue(conf.__config__, hasValueForEdit);
        }
        if (conf.__config__.tag === 'el-select' || conf.__config__.tag === 'el-radio-group') {
          const options = (conf.__slot__ && conf.__slot__.options) || [];
          const perValue = options.filter((option) => option.value == props.formEditData[vModel]);
          if (perValue.length > 0) {
            conf.__config__.defaultValue = perValue[0].value;
          }
        }
        if (conf.__config__.tag === 'el-checkbox-group') {
          const options = (conf.__slot__ && conf.__slot__.options) || [];
          const selectedValues = toCheckboxArray(props.formEditData[vModel]);
          // 与选项宽松比较后，保留选项原始类型的值，避免出现类型不一致导致的无法勾选问题
          const perValues = options
            .filter((option) => selectedValues.some((val) => option.value == val))
            .map((option) => option.value);
          if (perValues.length > 0) {
            conf.__config__.defaultValue = perValues;
          }
        }
        if (conf.__config__.children) applyEditData(conf.__config__.children);
      });
    }
    function syncEditData() {
      if (!props.formConf || !props.formConf.fields || !props.formConf.fields.length) return;
      if (!props.isEdit) return;
      const formConfCopy = JSON.parse(JSON.stringify(props.formConf));
      applyEditData(formConfCopy.fields);
      state.formConfCopy = formConfCopy;
      const formData = {};
      initFormData(state.formConfCopy.fields, formData);
      state[props.formConf.formModel] = formData;
    }
    function initFormData(componentList, formData) {
      componentList.forEach((cur) => {
        const config = cur.__config__;
        config.defaultValue = normalizeComponentValue(config, config.defaultValue);
        if (cur.__vModel__) formData[cur.__vModel__] = config.defaultValue;
        if (config.children) initFormData(config.children, formData);
      });
    }
    function buildRules(componentList, rules) {
      componentList.forEach((cur) => {
        const config = cur.__config__;
        if (Array.isArray(config.regList)) {
          if (config.required) {
            const required = { required: config.required, message: cur.placeholder };
            if (Array.isArray(config.defaultValue)) {
              required.type = 'array';
              required.message = `请至少选择一个${config.label}`;
            }
            required.message === undefined && (required.message = `${config.label}不能为空`);
            config.regList.push(required);
          }
          rules[cur.__vModel__] = config.regList.map((item) => {
            if (typeof item.pattern === 'string' && /^\/(.*)\/([gimsuy]*)$/.test(item.pattern)) {
              const match = item.pattern.match(/^\/(.*)\/([gimsuy]*)$/);
              item.pattern = new RegExp(match[1], match[2]);
            }
            item.trigger = ruleTrigger && ruleTrigger[config.tag];
            return item;
          });
        }
        if (config.children) buildRules(config.children, rules);
      });
    }
    function resetForm() {
      emit('resetForm', props.formConf);
      state.formConfCopy = JSON.parse(JSON.stringify(props.formConf));
      formRefs[props.formConf.formRef].value.resetFields();
    }
    function closeDialog() {
      emit('closeDialog');
    }
    function submitForm() {
      formRefs[props.formConf.formRef].value.validate((valid) => {
        if (!valid) return false;
        // 触发sumit事件
        emit('submit', state[props.formConf.formModel]);
        return true;
      });
    }
    const debouncedSubmitForm = Debounce(submitForm);

    const formRefs = {};
    const setFormRef = (el) => {
      if (el && props.formConf && props.formConf.formRef) {
        formRefs[props.formConf.formRef] = { value: el };
      }
    };

    applyEditData(state.formConfCopy.fields);
    initFormData(state.formConfCopy.fields, state[props.formConf.formModel]);
    buildRules(state.formConfCopy.fields, state[props.formConf.formRules]);

    watch(
      () => props.formConf,
      () => {
        syncEditData();
      },
      { deep: true },
    );
    watch(
      () => props.formEditData,
      () => {
        syncEditData();
      },
      { deep: true },
    );
    watch(
      () => props.isEdit,
      () => {
        syncEditData();
      },
    );

    const vm = {
      props,
      state,
      closeDialog,
      submitForm,
      debouncedSubmitForm,
      setFormRef,
    };

    expose({ resetForm, submitForm, closeDialog });

    return () => renderFrom(null, vm);
  },
});
</script>
<style scoped lang="scss">
.tips-info {
  line-height: 18px;
  //margin-top: 3px;
  color: #c0c4cc;
  font-size: 12px;
}
.saveBtn {
  font-size: 12px;
}
.form-parser-row {
  width: 100%;
}
:deep(.el-form) {
  width: 100%;
}
</style>
