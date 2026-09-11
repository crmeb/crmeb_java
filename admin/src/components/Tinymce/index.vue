<template>
  <WangEditor
    ref="editorRef"
    class="tinymce-container editor-container"
    v-bind="$attrs"
    :model-value="editorValue"
    :height="height"
    :disabled="disabled"
    @update:modelValue="handleUpdate"
  />
</template>

<script setup>
import { computed, ref } from 'vue';
import WangEditor from '@/components/wangEditor/index.vue';

defineOptions({ name: 'Tinymce', inheritAttrs: false });

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
    default: 400,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  toolbar: {
    type: Array,
    default: () => [],
  },
  menubar: {
    type: String,
    default: '',
  },
  id: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['update:modelValue', 'update:value', 'input', 'editorContent', 'change']);
const editorRef = ref(null);

const editorValue = computed(() => {
  if (props.modelValue !== undefined) return props.modelValue;
  return props.value;
});

function handleUpdate(value) {
  emit('update:modelValue', value);
  emit('update:value', value);
  emit('input', value);
  emit('editorContent', value);
  emit('change', value);
}

function setContent(value) {
  editorRef.value?.setContent(value);
}

function getContent() {
  return editorRef.value?.getContent() || '';
}

defineExpose({ setContent, getContent });
</script>
