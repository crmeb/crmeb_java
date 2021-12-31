<template>
  <div class="divBox">
    <el-card class="box-card">
      <zb-parser
        :form-id="formId"
        :is-create="isCreate"
        :edit-data="editData"
        @submit="handlerSubmit"
        @resetForm="resetForm"
        v-if="isShow"
      />
    </el-card>
  </div>
</template>

<script>
  import zbParser from '@/components/FormGenerator/components/parser/ZBParser'
  import { configSaveForm, configInfo } from '@/api/systemConfig.js'
  import {Debounce} from '@/utils/validate'
  export default {
    name: "integralconfig",
    components: { zbParser },
    data() {
      return {
        isShow: false,
        isCreate: 0,
        editData: {},
        formId: 109
      }
    },
    mounted() {
      this.getFormInfo()
    },
    methods: {
      resetForm(formValue) {
        this.editData = {}
      },
      handlerSubmit:Debounce(function(data) {
        const tempArr = []
        for (var key in data) {
          const obj = {}
          obj.name = key
          obj.title = key
          obj.value = data[key]
          tempArr.push(obj)
        }
        const _pram = {
          'fields': tempArr,
          'id': this.formId,
          'sort': 0,
          'status': true
        }
        configSaveForm(_pram).then(res => {
          this.getFormInfo()
          this.$message.success('操作成功')
        })
      }),
      // 获取表单详情
      getFormInfo() {
        configInfo({ id: this.formId }).then(res => {
          this.isShow = false
          this.editData = res
          this.isCreate = 1
          setTimeout(() => { // 让表单重复渲染待编辑数据
            this.isShow = true
          }, 80)
        })
      }
    }
  }
</script>

<style scoped>

</style>
