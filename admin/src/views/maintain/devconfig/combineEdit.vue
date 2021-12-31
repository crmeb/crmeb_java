<template>
  <div>
    <el-form ref="selfForm" :model="selfForm" label-width="120px">
      <el-form-item label="排序" prop="sort" :rules="[{ required: true, message:'排序不能为空', trigger:['blur','change'] }]">
        <el-input-number v-model="selfForm.sort" />
      </el-form-item>
      <el-form-item
        label="状态"
        prop="status"
        :rules="[{ required: true, message:'正确操作状态', trigger:['change'] }]">
        <el-switch  v-model="selfForm.status"/>
      </el-form-item>
    </el-form>
    <parser
      v-if="formConf.fields.length > 0"
      :is-edit="isCreate === 1"
      :form-conf="formConf"
      :form-edit-data="editData"
      @submit="handlerSubmit"
    />
    <!--      {{ editData }}-->
  </div>
</template>

<script>
import parser from '@/components/FormGenerator/components/parser/Parser'
import * as systemGroupDataApi from '@/api/systemGroupData.js'
import * as systemFormConfigApi from '@/api/systemFormConfig.js'
import {Debounce} from '@/utils/validate'
export default {
  // name: "combineEdit"
  components: { parser },
  props: {
    formData: {
      type: Object,
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
    handlerInitEditData() {
      const { sort, status } = this.editData
      this.selfForm.sort = sort
      this.selfForm.status = status
    },
    handlerGetFormConfig() { // 获取表单配置后生成table列
      const _pram = { id: this.formData.formId }
      systemFormConfigApi.getFormConfigInfo(_pram).then(data => {
        this.formConf = JSON.parse(data.content)
      })
    },
    handlerSubmit:Debounce(function(formValue) {
      this.isCreate === 0 ? this.handlerSave(formValue) : this.handlerEdit(formValue)
    }),
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
        gid: this.formData.id,
        form: {
          fields: [],
          id: this.formData.formId,
          sort: this.selfForm.sort,
          status: this.selfForm.status
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
