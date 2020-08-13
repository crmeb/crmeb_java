<template>
  <div class="divBox">
    <el-card class="box-card">
      <zb-parser
        :form-id="109"
        :is-create="isCreate"
        :edit-data="editData"
        @submit="handlerSubmit"
      />
    </el-card>
  </div>
</template>

<script>
  import zbParser from '@/components/FormGenerator/components/parser/ZBParser'
  import { smsSaveApi } from '@/api/sms'
  export default {
    name: "integralconfig",
    components: { zbParser },
    data() {
      return {
        isCreate: 0,
        editData: {}
      }
    },
    methods: {
      handlerSubmit(formValue) {
        smsSaveApi(formValue).then(data => {
          this.$message.success('新增成功')
          this.editData = {}
        })
      }
    }
  }
</script>

<style scoped>

</style>
