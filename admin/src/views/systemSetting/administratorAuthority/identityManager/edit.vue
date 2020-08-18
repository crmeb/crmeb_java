<template>
  <div>
    <el-form ref="pram" :model="pram" label-width="100px" @submit.native.prevent>
      <el-form-item label="身份名称" prop="roleName" :rules="[{required:true,message:'请填写身份名称', trigger:['blur','change']}]">
        <el-input v-model="pram.roleName" placeholder="身份名称" />
      </el-form-item>
      <el-form-item label="状态">
        <el-switch v-model="pram.status" :active-value="true" :inactive-value="false" />
      </el-form-item>
      <el-form-item label="权限">
        <category-list
          :biztype="constants.categoryType[4]"
          :select-model="true"
          :row-select="pram.rules"
          :select-model-keys="editData.rules"
          @rulesSelect="rulesSelect"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handlerSubmit('pram')">{{ isCreate===0?'確定':'更新' }}</el-button>
        <el-button @click="close">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import categoryList from '@/components/Category/list'
import * as constants from '@/utils/constants.js'
import * as roleApi from '@/api/role.js'
export default {
  // name: "edit"
  components: { categoryList },
  props: {
    isCreate: {
      type: Number,
      required: true
    },
    editData: {
      type: Object,
      default: () => {
        return { rules: [] }
      }
    }
  },
  data() {
    return {
      constants,
      pram: {
        level: 0,
        roleName: null,
        rules: [],
        status: null,
        id: null
      }
    }
  },
  mounted() {
    this.initEditData()
  },
  methods: {
    close() {
      this.$emit('hideEditDialog')
    },
    initEditData() {
      if (this.isCreate !== 1) return
      const { level, roleName, rules, status, id } = this.editData
      this.pram.rules = rules
      this.pram.level = level
      this.pram.roleName = roleName
      this.pram.status = status
      this.pram.id = id
    },
    handlerSubmit(form) {
      this.$refs[form].validate(valid => {
        if (!valid) return
        if (this.isCreate === 0) {
          this.handlerSave()
        } else {
          this.handlerEdit()
        }
      })
    },
    handlerSave() {
      roleApi.addRole(this.pram).then(data => {
        this.$message.success('创建身份成功')
        this.$emit('hideEditDialog')
      })
    },
    handlerEdit() {
      roleApi.updateRole(this.pram).then(data => {
        this.$message.success('更新身份成功')
        this.$emit('hideEditDialog')
      })
    },
    rulesSelect(selectKeys) {
      // let _ids = []
      // select.map(item => {
      //   _ids.push(item.id)
      // })
      // this.pram.rules = _ids.join(',')
      this.pram.rules = selectKeys
    }
  }
}
</script>

<style scoped>

</style>
