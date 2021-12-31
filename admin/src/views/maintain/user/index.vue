<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-form ref="pram" :model="pram" :rules="rules" label-width="100px">
        <el-form-item label="管理员账号" prop="account">
          <el-input v-model="pram.account" placeholder="管理员账号" :disabled="true"/>
        </el-form-item>
        <el-form-item label="管理员姓名" prop="realName">
          <el-input v-model="pram.realName" placeholder="管理员姓名" />
        </el-form-item>
        <el-form-item label="原始密码">
          <el-input v-model="password" placeholder="原始密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="pwd">
          <el-input
            v-model="pram.pwd"
            placeholder="管理员密码"
            clearable
            @input="handlerPwdInput"
            @clear="handlerPwdInput"
          />
        </el-form-item>
        <el-form-item v-if="pram.pwd" label="确认新密码" prop="repwd">
          <el-input v-model="pram.repwd" placeholder="确认新密码" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handlerSubmit('pram')">提交</el-button>
          <el-button @click="close('pram')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
  import * as systemAdminApi from '@/api/systemadmin.js'
  import Cookies from 'js-cookie'
  import {Debounce} from '@/utils/validate'
  export default {
    name: "index",
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
      const JavaInfo = JSON.parse(Cookies.get('JavaInfo'));
      return {
        password: '',
        JavaInfo: JSON.parse(Cookies.get('JavaInfo')),
        pram: {
          account:JavaInfo.account,
          pwd: null,
          repwd: null,
          realName: JavaInfo.realName,
          id: JavaInfo.id
        },
        roleList: [],
        rules: {
          account: [{ required: true, message: '请填管理员账号', trigger: ['blur', 'change'] }],
          pwd: [{ required: true, message: '请填管理员密码', trigger: ['blur', 'change'] }],
          repwd: [{ required: true, message: '确认密码密码', validator: validatePass, trigger: ['blur', 'change'] }],
          realName: [{ required: true, message: '管理员姓名', trigger: ['blur', 'change'] }],
        }
      }
    },
    methods: {
      close(formName) {
        this.$refs[formName].resetFields();
      },
      handlerSubmit:Debounce(function(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            systemAdminApi.adminUpdate(this.pram).then(data => {
              this.$message.success('提交成功')
              this.$router.go(-1)
            })
          } else {
            return false;
          }
        });
      }),
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
