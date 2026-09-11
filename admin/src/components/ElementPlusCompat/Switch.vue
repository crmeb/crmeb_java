<script>
import { computed, defineComponent, h } from 'vue';
import { ElSwitch } from 'element-plus/es/components/switch/index.mjs';

function toBooleanValue(value) {
  if (value === true || value === 1 || value === '1' || value === 'true') return true;
  if (value === false || value === 0 || value === '0' || value === 'false') return false;
  return undefined;
}

function matchesValue(value, target) {
  if (value === target) return true;
  if (value === undefined || value === null || target === undefined || target === null) return false;
  return String(value) === String(target);
}

function normalizeSwitchValue(value, activeValue, inactiveValue) {
  if (value === activeValue || value === inactiveValue) return value;
  if (matchesValue(value, activeValue)) return activeValue;
  if (matchesValue(value, inactiveValue)) return inactiveValue;

  const booleanValue = toBooleanValue(value);
  if (booleanValue !== undefined) {
    const activeBoolean = toBooleanValue(activeValue);
    const inactiveBoolean = toBooleanValue(inactiveValue);
    if (activeBoolean === booleanValue && inactiveBoolean !== booleanValue) return activeValue;
    if (inactiveBoolean === booleanValue && activeBoolean !== booleanValue) return inactiveValue;
    return booleanValue ? activeValue : inactiveValue;
  }

  return inactiveValue;
}

export default defineComponent({
  name: 'ElSwitch',
  inheritAttrs: false,
  props: {
    modelValue: {
      default: undefined,
    },
    activeValue: {
      default: true,
    },
    inactiveValue: {
      default: false,
    },
  },
  emits: ['update:modelValue', 'change', 'input'],
  setup(props, { attrs, emit, slots }) {
    const modelValue = computed(() => normalizeSwitchValue(props.modelValue, props.activeValue, props.inactiveValue));

    return () =>
      h(
        ElSwitch,
        {
          ...attrs,
          modelValue: modelValue.value,
          activeValue: props.activeValue,
          inactiveValue: props.inactiveValue,
          'onUpdate:modelValue': (value) => emit('update:modelValue', value),
          onChange: (value) => emit('change', value),
          onInput: (value) => emit('input', value),
        },
        slots,
      );
  },
});
</script>
