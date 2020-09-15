<template>
  <div class="divBox">
    <el-card class="box-card">
      <parser v-if="isShow" class="formBox" :form-conf="formConf" :form-edit-data="formData" :is-edit="isCreate === 1" @submit="submit" />
    </el-card>
  </div>
</template>

<script>
import parser from '@/components/FormGenerator/components/parser/Parser'
import * as systemFormConfigApi from '@/api/systemFormConfig.js'
import { configSaveForm, configInfo } from '@/api/systemConfig.js'
export default {
  name: 'Config',
  components: { parser },
  data() {
    return {
      // 表单
      formConf: { fields: [] },
      formId: 74,
      formData: {},
      isCreate: 0,
      isShow: false
    }
  },
  created() {
    systemFormConfigApi.getFormConfigInfo({ id: this.formId }).then(data => {
      this.formConf = JSON.parse(data.content)
    })
    this.getFormInfo()
  },
  methods: {
    submit(data) {
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
    },
    // 获取表单详情
    getFormInfo() {
      configInfo({ id: this.formId }).then(res => {
        this.isShow = false
        this.formData = res
        this.isCreate = 1
        setTimeout(() => { // 让表单重复渲染待编辑数据
          this.isShow = true
        }, 80)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
  .el-button--primary{
    width: 100%;
    height: 40px;
    margin-top: 10px;
  }
</style>
