<template>
  <WangEditor :model-value="editorValue" :height="height" @update:modelValue="handleUpdate" />
</template>

<script setup>
import { computed } from 'vue';
import WangEditor from '@/components/wangEditor/index.vue';

defineOptions({ name: 'tinymce' });

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: undefined,
  },
  value: {
    type: [String, Number],
    default: undefined,
  },
  height: {
    type: [Number, String],
    default: 300,
  },
});

const emit = defineEmits(['update:modelValue', 'input']);

const editorValue = computed(() => {
  if (props.modelValue !== undefined) return props.modelValue;
  return props.value;
});

function handleUpdate(value) {
  emit('update:modelValue', value);
  emit('input', value);
}
</script>
