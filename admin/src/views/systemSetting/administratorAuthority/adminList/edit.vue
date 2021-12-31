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
        <el-select v-model="pram.roles" placeholder="身份" clearable multiple style="width: 100%">
          <el-option
            v-for="item,index in roleList.list"
            :key="index"
            :label="item.roleName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input type="text" v-model="pram.phone" prefix="ios-contact-outline"
                  placeholder="请输入手机号" size="large"/>
      </el-form-item>
      <el-form-item label="状态">
        <el-switch v-model="pram.status" :active-value="true" :inactive-value="false" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handlerSubmit('pram')" v-hasPermi="['admin:system:admin:update','admin:system:admin:save']">{{ isCreate===0?'確定':'更新' }}</el-button>
        <el-button @click="close">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import * as roleApi from '@/api/role.js'
import * as systemAdminApi from '@/api/systemadmin.js'
import {Debounce} from '@/utils/validate'
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
    const validatePhone = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('请填写手机号'));
      } else if (!/^1[3456789]\d{9}$/.test(value)) {
        callback(new Error('手机号格式不正确!'));
      } else {
        callback();
      }
    };
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
      constants:this.$constants,
      pram: {
        account: null,
        level: null,
        pwd: null,
        repwd: null,
        realName: null,
        roles: [],
        status: null,
        id: null,
        phone: null
      },
      roleList: [],
      rules: {
        account: [{ required: true, message: '请填管理员账号', trigger: ['blur', 'change'] }],
        pwd: [{ required: true, message: '请填管理员密码', trigger: ['blur', 'change'] }],
        repwd: [{ required: true, message: '确认密码密码', validator: validatePass, trigger: ['blur', 'change'] }],
        realName: [{ required: true, message: '管理员姓名', trigger: ['blur', 'change'] }],
        roles: [{ required: true, message: '管理员身份', trigger: ['blur', 'change'] }],
        phone: [ { required: true, message: '请输入手机号', trigger: ['blur', 'change']} ]
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
        limit: this.constants.page.limit[4],
        status: 1
      }
      roleApi.getRoleList(_pram).then(data => {
        this.roleList = data;
        let arr = [];
        data.list.forEach(item=>{
          arr.push(item.id);
        })
        if(!arr.includes(Number.parseInt(this.pram.roles))){
          this.$set(this.pram,'roles',[]);
        }
      })
    },
    initEditData() {
      if (this.isCreate !== 1) return
      const { account, realName, roles, level, status, id, phone } = this.editData
      this.pram.account = account
      this.pram.realName = realName
      const _roles = []
      if (roles.length > 0 && !roles.includes(',')) {
        //如果权限id集合有长度并且是只有一个，就将它Push进_roles这个数组
        _roles.push(Number.parseInt(roles))
      } else {
        //否则就将多个id集合解构以后push进roles并且转换为整型
        _roles.push(...roles.split(',').map(item => Number.parseInt(item)))
      }
      this.pram.roles = _roles
      this.pram.status = status
      this.pram.id = id
      this.pram.phone = phone
      this.rules.pwd = []
      this.rules.repwd = []
    },
    handlerSubmit:Debounce(function(form) {
      this.$refs[form].validate(valid => {
        if (!valid) return
        if (this.isCreate === 0) {
          this.handlerSave()
        } else {
          this.handlerEdit()
        }
      })
    }),
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
