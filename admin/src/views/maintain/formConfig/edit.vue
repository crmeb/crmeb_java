<template>
  <div>
    <config-list
      :edit-data="editData"
      :is-create="isCreate"
      @getFormConfigDataResult="handlerGetFormConfigData"
    />
  </div>
</template>

<script>
import configList from '@/components/FormGenerator/index/Home.vue'
import * as systemFormConfigApi from '@/api/systemFormConfig.js'
export default {
  // name: "edit"
  components: { configList },
  props: {
    editData: {
      type: Object,
      default: {}
    },
    isCreate: {
      type: Number,
      default: 0 // 0=创建，1=编辑
    }
  },
  data() {
    return {}
  },
  methods: {
    handlerGetFormConfigData(formConfigData) {
      formConfigData.id ? this.handlerEdit(formConfigData) : this.handlerSave(formConfigData)
    },
    handlerSave(pram) {
      systemFormConfigApi.getFormConfigSave(pram).then(data => {
        this.$message.success('创建表单配置成功')
        setTimeout(() => {
          this.$emit('hideDialog')
        }, 800)
      })
    },
    handlerEdit(pram) {
      systemFormConfigApi.getFormConfigEdit(pram).then(data => {
        this.$message.success('编辑表单配置成功')
        setTimeout(() => {
          this.$emit('hideDialog')
        }, 800)
      })
    }
  }
}
</script>

<style scoped>

</style>
