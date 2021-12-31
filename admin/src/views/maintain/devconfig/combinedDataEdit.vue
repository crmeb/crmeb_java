<template>
  <div class="components-container">
    <el-form ref="editPram" :model="editPram" label-width="100px">
      <el-form-item
        label="数据组名称"
        prop="name"
        :rules="[{ required:true, message:'填写数据组名称', trigger:['blur','change'] }]"
      >
        <el-input v-model="editPram.name" placeholder="数据组名称" clearable />
      </el-form-item>
      <el-form-item
        label="数据简介"
        prop="info"
        :rules="[{ required:true, message:'填写数据简介', trigger:['blur','change'] }]"
      >
        <el-input v-model="editPram.info" placeholder="数据简介" clearable />
      </el-form-item>
      <el-form-item
        label="表单数据ID"
        prop="formId"
        :rules="[{ required:true, message:'请选择表单数据', trigger:['change'] }]"
      >
        <span>{{ editPram.formId }}</span>
        <el-button type="primary" @click="selectFormDialogConfig.visible = true">选择模板数据</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" class="btn-width100" @click="handlerSubmit('editPram')">确定</el-button>
      </el-form-item>
    </el-form>
    <el-dialog title="选择表单模板" :visible.sync="selectFormDialogConfig.visible" append-to-body>
      <form-config-list select-model @selectedRowData="handlerSelectedRowData" />
    </el-dialog>
  </div>

</template>

<script>
import formConfigList from '@/views/maintain/formConfig'
import * as systemGroupApi from '@/api/systemGroup'
import {Debounce} from '@/utils/validate'
export default {
  // name: "combinedDataEdit"
  components: { formConfigList },
  props: {
    isCreate: {
      type: Number,
      default: 0
    },
    editData: {
      type: Object,
      default: {}
    }
  },
  data() {
    return {
      editPram: {
        formId: null,
        info: null,
        name: null,
        id: null
      },
      selectedFormConfigData: {},
      selectFormDialogConfig: {
        visible: false
      }
    }
  },
  mounted() {
    this.handlerInitEditData()
  },
  methods: {
    handlerInitEditData() {
      if (this.isCreate !== 1) return
      const { id, name, info, formId, createTime, updateTime } = this.editData
      this.editPram.id = id
      this.editPram.name = name
      this.editPram.info = info
      this.editPram.formId = formId
    },
    handlerSelectedRowData(rowData) {
      this.selectedFormConfigData = rowData
      this.editPram.formId = this.selectedFormConfigData.id
      this.selectFormDialogConfig.visible = false
    },
    handlerSubmit:Debounce(function(form) {
      this.$refs[form].validate(result => {
        if (!result) return
        this.isCreate === 0 ? this.handlerSave(this.editPram) : this.handlerEdit(this.editPram)
      })
    }),
    handlerSave(pram) {
      systemGroupApi.groupSave(pram).then(data => {
        this.$message.success('添加组合数据成功')
        this.$emit('hideDialog')
      })
    },
    handlerEdit(pram) {
      systemGroupApi.groupEdit(pram).then(data => {
        this.$message.success('编辑组合数据成功')
        this.$emit('hideDialog')
      })
    }
  }
}
</script>

<style scoped>

</style>
