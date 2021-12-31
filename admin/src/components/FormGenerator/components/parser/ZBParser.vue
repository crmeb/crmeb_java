<template>
  <div>
    <parser
      v-if="formConf.fields.length > 0"
      v-loading="loading"
      :is-edit="isCreate === 1"
      :form-conf="formConf"
      :form-edit-data="editData"
      @submit="handlerSubmit"
      @resetForm="resetForm"
    />
    <!--    editData:{{ editData }}-->
    <!--    formConf:{{ formConf }}-->
    <!--    isCreate:{{ isCreate }}-->
  </div>
</template>

<script>
/**
   * 注意：和Parser唯一的区别就是这里仅仅传入表单配置id即可自动加载已配置的表单
   *      数据后渲染表单，
   *      其他业务和Parser保持一致
   */
import * as systemFormConfigApi from '@/api/systemFormConfig.js'
import parser from '@/components/FormGenerator/components/parser/Parser'
export default {
  // name: "ZBParser"
  components: { parser },
  props: {
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
  },
  data() {
    return {
      loading: false,
      formConf: { fields: [] }
    }
  },
  mounted() {
    this.handlerGetFormConfig(this.formId)
  },
  methods: {
    handlerGetFormConfig(formId) { // 获取表单配置后生成table列
      this.loading = true
      const _pram = { id: formId }
      systemFormConfigApi.getFormConfigInfo(_pram).then(data => {
        this.formConf = JSON.parse(data.content)
        this.loading = false
      }).catch(()=>{
        this.loading = false
      })
    },
    handlerSubmit(formValue) {
      this.$emit('submit', formValue)
    },
    resetForm(formValue){
      this.$emit('resetForm', formValue)
    }
  }
}
</script>

<style scoped>

</style>
