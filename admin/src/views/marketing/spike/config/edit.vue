<template>
  <div>
    <zb-parser
      v-if="formConf.fields.length > 0"
      :form-id="formid"
      :is-create="isCreate"
      :edit-data="editData"
      @submit="handlerSubmit"
      @resetForm="resetForm"
    />
    <!--      {{ editData }}-->
  </div>
</template>

<script>
import zbParser from '@/components/FormGenerator/components/parser/ZBParser'
import * as systemGroupDataApi from '@/api/systemGroupData.js'
import * as systemFormConfigApi from '@/api/systemFormConfig.js'
export default {
  // name: "combineEdit"
  components: { zbParser },
  props: {
    formid: {
      type: Number,
      required: true
    },
    isCreate: {
      type: Number,
      default: 0 // 0=create 1=edit
    },
    editData: {
      type: Object
    }
  },
  data() {
    return {
      constants: this.$constants,
      formConf: { fields: [] },
      selfForm: {
        sort: 0,
        status: 0
      }
    }
  },
  mounted() {
    this.handlerGetFormConfig()
    this.handlerInitEditData()
  },
  methods: {
    resetForm(formValue) {
      this.dialogVisible = false
    },
    handlerInitEditData() {
      const { sort, status } = this.editData
      this.selfForm.sort = sort
      this.selfForm.status = status
    },
    handlerGetFormConfig() { // 获取表单配置后生成table列
      const _pram = { id: this.formid }
      systemFormConfigApi.getFormConfigInfo(_pram).then(data => {
        this.formConf = JSON.parse(data.content)
      })
    },
    handlerSubmit(formValue) {
      this.isCreate === 0 ? this.handlerSave(formValue) : this.handlerEdit(formValue)
    },
    handlerSave(formValue) {
      const _pram = this.buildFormPram(formValue)
      systemGroupDataApi.groupDataSave(_pram).then(data => {
        this.$message.success('添加数据成功')
        this.$emit('hideDialog')
      })
    },
    handlerEdit(formValue) {
      const _pram = this.buildFormPram(formValue)
      systemGroupDataApi.groupDataEdit(_pram, this.editData.id).then(data => {
        this.$message.success('编辑数据成功')
        this.$emit('hideDialog')
      })
    },
    buildFormPram(formValue) {
      const _pram = {
        gid: this.formid,
        form: {
          fields: [],
          id: this.formid
          // sort: this.selfForm.sort,
          // status: this.selfForm.status
        }}
      const _fields = []
      Object.keys(formValue).forEach((key) => {
        _fields.push({
          name: key,
          title: key,
          value: formValue[key]
        })
      })
      _pram.form.fields = _fields
      return _pram
    }
  }
}
</script>

<style scoped>

</style>
