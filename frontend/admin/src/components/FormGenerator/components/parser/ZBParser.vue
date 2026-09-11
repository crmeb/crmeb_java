<template>
  <div class="zb-parser">
    <parser
      v-if="formConf.fields.length > 0"
      v-loading="loading"
      :is-edit="isCreate === 1"
      :form-conf="formConf"
      :form-edit-data="editData"
      @submit="handlerSubmit"
      @resetForm="resetForm"
      @closeDialog="closeDialog"
    />
    <!--    editData:{{ editData }}-->
    <!--    formConf:{{ formConf }}-->
    <!--    isCreate:{{ isCreate }}-->
  </div>
</template>

<script setup>
/**
 * 注意：和Parser唯一的区别就是这里仅仅传入表单配置id即可自动加载已配置的表单
 *      数据后渲染表单，
 *      其他业务和Parser保持一致
 */
import { ref, onMounted } from 'vue'
import { ElLoadingDirective as vLoading } from 'element-plus/es/components/loading/index.mjs'
import * as systemFormConfigApi from '@/api/systemFormConfig.js'
import parser from '@/components/FormGenerator/components/parser/Parser'

const props = defineProps({
  formId: {
    type: Number,
    required: true
  },
  isCreate: {
    type: Number,
    default: 0 // 0=create 1=edit
  },
  editData: {
    type: Object
  },
  formatFormConf: {
    type: Function,
    default: null
  }
})
const emit = defineEmits(['closeDialog', 'submit', 'resetForm'])

const loading = ref(false)
const formConf = ref({ fields: [] })

function handlerGetFormConfig(formId) {
  // 获取表单配置后生成table列
  loading.value = true
  const _pram = { id: formId }
  systemFormConfigApi
    .getFormConfigInfo(_pram)
    .then((data) => {
      const parsedContent = JSON.parse(data.content)
      formConf.value = props.formatFormConf ? props.formatFormConf(parsedContent) || parsedContent : parsedContent
      loading.value = false
    })
    .catch(() => {
      loading.value = false
    })
}
function closeDialog() {
  emit('closeDialog')
}
function handlerSubmit(formValue) {
  emit('submit', formValue)
}
function resetForm(formValue) {
  emit('resetForm', formValue)
}

onMounted(() => {
  handlerGetFormConfig(props.formId)
})
</script>

<style scoped>
.zb-parser {
  width: 100%;
}
</style>
