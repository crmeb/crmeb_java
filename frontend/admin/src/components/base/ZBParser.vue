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
      :key="keyNum"
    />
    <!--editData:{{ editData }}-->
    <!--    formConf:{{ formConf }}-->
    <!--    isCreate:{{ isCreate }}-->
  </div>
</template>

<script setup>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

/**
 * 注意：和Parser唯一的区别就是这里仅仅传入表单配置id即可自动加载已配置的表单
 *      数据后渲染表单，
 *      其他业务和Parser保持一致
 */
import { ref, watch, onMounted } from 'vue';
import { ElMessageBox } from 'element-plus';
import { ElLoadingDirective as vLoading } from 'element-plus/es/components/loading/index.mjs';
import { getFormTempByNameApi } from '@/api/systemFormConfig.js';
import parser from '@/components/FormGenerator/components/parser/Parser';
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'ZBParser' });

const props = defineProps({
  formName: {
    type: String,
    required: '',
  },
  isCreate: {
    type: Number,
    default: 0, // 0=create 1=edit
  },
  editData: {
    type: Object,
  },
  keyNum: {
    type: Number,
    default: 0,
  },
});

const emit = defineEmits(['submit', 'resetForm']);

const loading = ref(false);
const formConf = ref({ fields: [] });

watch(
  () => props.keyNum,
  (val) => {
    formConf.value = { fields: [] };
    handlerGetFormConfig(props.formName);
  },
  { deep: true, immediate: false },
);

onMounted(() => {
  handlerGetFormConfig(props.formName);
});

function handlerGetFormConfig(formName) {
  // 获取表单配置后生成table列
  loading.value = true;
  const _pram = { name: encodeURIComponent(formName) };
  getFormTempByNameApi(_pram)
    .then((data) => {
      formConf.value = JSON.parse(data.content);
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
    });
}

const handlerSubmit = Debounce(function (formValue) {
  emit('submit', formValue);
});

function closeDialog() {
  ElMessageBox.close();
}

function resetForm(formValue) {
  emit('resetForm', formValue);
}
</script>

<style scoped>
.zb-parser {
  width: 100%;
}
</style>
