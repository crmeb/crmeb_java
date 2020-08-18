<template>
  <div>
    <el-form ref="pram" :model="pram" :rules="rules" label-width="100px" @submit.native.prevent>
      <el-form-item label="管理员账号" prop="account">
        <el-input v-model="pram.account" placeholder="管理员账号" />
      </el-form-item>
      <el-form-item label="管理员密码" prop="pwd">
        <el-input
          v-model="pram.pwd"
          placeholder="管理员密码"
          clearable
          @input="handlerPwdInput"
          @clear="handlerPwdInput"
        />
      </el-form-item>
      <el-form-item v-if="pram.pwd" label="确认密码" prop="repwd">
        <el-input v-model="pram.repwd" placeholder="确认密码" clearable />
      </el-form-item>
      <el-form-item label="管理员姓名" prop="realName">
        <el-input v-model="pram.realName" placeholder="管理员姓名" />
      </el-form-item>
      <el-form-item label="管理员身份" prop="roles">
        <el-select v-model="pram.roles" placeholder="身份" clearable multiple>
          <el-option
            v-for="item,index in roleList.list"
            :key="index"
            :label="item.roleName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-switch v-model="pram.status" :active-value="true" :inactive-value="false" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handlerSubmit('pram')">{{ isCreate===0?'確定':'更新' }}</el-button>
        <el-button @click="close">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import * as constants from '@/utils/constants.js'
import * as roleApi from '@/api/role.js'
import * as systemAdminApi from '@/api/systemadmin.js'
export default {
  // name: "edit"
  components: { },
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
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.pram.pwd) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      constants,
      pram: {
        account: null,
        level: null,
        pwd: null,
        repwd: null,
        realName: null,
        roles: [],
        status: null,
        id: null
      },
      roleList: [],
      rules: {
        account: [{ required: true, message: '请填管理员账号', trigger: ['blur', 'change'] }],
        // level: null,
        pwd: [{ required: true, message: '请填管理员密码', trigger: ['blur', 'change'] }],
        repwd: [{ required: true, message: '确认密码密码', validator: validatePass, trigger: ['blur', 'change'] }],
        realName: [{ required: true, message: '管理员姓名', trigger: ['blur', 'change'] }],
        roles: [{ required: true, message: '管理员身份', type: 'array', trigger: ['blur', 'change'] }]
      }
    }
  },
  mounted() {
    this.initEditData()
    this.handleGetRoleList()
  },
  methods: {
    close() {
      this.$emit('hideEditDialog')
    },
    handleGetRoleList() {
      const _pram = {
        page: 1,
        limit: constants.page.limit[4],
        status: 1
      }
      roleApi.getRoleList(_pram).then(data => {
        this.roleList = data
      })
    },
    initEditData() {
      if (this.isCreate !== 1) return
      const { account, realName, roles, level, status, id } = this.editData
      this.pram.account = account
      this.pram.realName = realName
      const _roles = []
      if (roles.length > 0 && !roles.includes(',')) {
        _roles.push(Number.parseInt(roles))
      } else {
        _roles.push(...roles.split(',').map(item => Number.parseInt(item)))
      }
      this.pram.roles = _roles
      this.pram.status = status
      this.pram.id = id
      this.rules.pwd = []
      this.rules.repwd = []
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
      systemAdminApi.adminAdd(this.pram).then(data => {
        this.$message.success('创建管理员成功')
        this.$emit('hideEditDialog')
      })
    },
    handlerEdit() {
      this.pram.roles = this.pram.roles.join(',')
      systemAdminApi.adminUpdate(this.pram).then(data => {
        this.$message.success('更新管理员成功')
        this.$emit('hideEditDialog')
      })
    },
    rulesSelect(selectKeys) {
      this.pram.rules = selectKeys
    },
    handlerPwdInput(val) {
      if (!val) {
        this.rules.pwd = []
        this.rules.repwd = []
        return
      }
      this.rules.pwd = [
        { required: true, message: '请填管理员密码', trigger: ['blur', 'change'] },
        { min: 6, max: 20, message: '长度6-20个字符', trigger: ['blur', 'change'] }]
      this.rules.repwd = [{ required: true, message: '两次输入密码不一致', validator: (rule, value, callback) => {
        if (value === '') {
          callback(new Error('两次输入密码不一致!'))
        } else if (value !== this.pram.pwd) {
          callback(new Error('两次输入密码不一致!'))
        } else {
          callback()
        }
      }, trigger: ['blur', 'change'] }]
    }
  }
}
</script>

<style scoped>

</style>
