<template>
  <div class="login-container">
    <el-steps :active="current"  align-center>
      <el-step title="验证账号信息"></el-step>
      <el-step title="修改账户密码"></el-step>
      <el-step title="登录"></el-step>
    </el-steps>
    <el-form ref="formInline" :model="formInline" size="medium" :rules="ruleInline" class="login-form" autocomplete="on" label-position="left">
      <template v-if="current === 0">
        <el-form-item prop="phone">
          <el-input type="text" v-model="formInline.phone" prefix="ios-contact-outline"
                    placeholder="请输入手机号" size="large" :readonly="infoData.phone?true:false"/>
        </el-form-item>
        <el-form-item prop="code" class="captcha">
          <div class="acea-row" style="flex-wrap: nowrap;">
            <el-input
              ref="username"
              v-model="formInline.code"
              placeholder="验证码"
              name="username"
              type="text"
              tabindex="1"
              autocomplete="off"
              prefix-icon="el-icon-message"
              style="width: 90%"
            />
            <el-button size="mini" :disabled=!this.canClick @click="cutDown" v-hasPermi="['admin:pass:send:code']">{{cutNUm}}</el-button>
          </div>
        </el-form-item>
      </template>
      <template v-if="current === 1">
        <el-form-item prop="password" class="maxInpt">
          <el-input type="password" v-model="formInline.password" prefix="ios-lock-outline"
                    placeholder="请输入新密码" size="large"/>
        </el-form-item>
        <el-form-item prop="checkPass" class="maxInpt">
          <el-input type="password" v-model="formInline.checkPass" prefix="ios-lock-outline"
                    placeholder="请验证新密码" size="large"/>
        </el-form-item>
      </template>
      <template v-if="current === 2">
        <el-form-item prop="phone" class="maxInpt">
          <el-input type="text" v-model="formInline.phone" prefix="ios-contact-outline"
                    placeholder="请输入手机号"/>
        </el-form-item>
        <el-form-item prop="password" class="maxInpt">
          <el-input type="password" v-model="formInline.password" prefix="ios-lock-outline"
                    placeholder="请输入密码"/>
        </el-form-item>
      </template>
      <el-form-item class="maxInpt">
        <el-button v-if="current === 0" type="primary"  @click="handleSubmit1('formInline',current)" class="mb20 width100">下一步</el-button>
        <el-button  v-if="current === 1" type="primary"  @click="handleSubmit2('formInline',current)" class="mb20 width100">提交</el-button>
        <el-button  v-if="current === 2" type="primary"  @click="handleSubmit('formInline',current)" class="mb20 width100">登录</el-button>
        <el-button @click="returns('formInline')" class="width100" style="margin-left: 0px;">返回</el-button>
      </el-form-item>

      <!--<el-button v-if="current === 0"  size="mini" :loading="loading" type="primary" style="width:100%;margin-bottom:20px;" @click="handleSubmit('formInline')">注册</el-button>-->
      <!--<el-button size="mini" type="primary" style="width:100%;margin-bottom:20px;" @click="changelogo">立即登录</el-button>-->
    </el-form>
  </div>
</template>

<script>
    import { updatePasswordApi, captchaApi, configApi } from '@/api/sms';
    export default {
        name: 'forgetPassword',
        data () {
            const validatePhone = (rule, value, callback) => {
                if (!value) {
                    return callback(new Error('请填写手机号'));
                } else if (!/^1[3456789]\d{9}$/.test(value)) {
                    callback(new Error('手机号格式不正确!'));
                } else {
                    callback();
                }
            };
            var validatePass = (rule, value, callback) => {
                if (value === '') {
                    callback(new Error('请输入密码'));
                }else {
                    if(this.current === 1){
                        if (this.formInline.checkPass !== '') {
                            this.$refs.formInline.validateField('checkPass');
                        }
                        callback();
                    }else{
                        if(value !== this.formInline.checkPass){
                            callback(new Error('请输入正确密码!'));
                        }
                        callback();
                    }
                }
            };
            var validatePass2 = (rule, value, callback) => {
                if (value === '') {
                    callback(new Error('请再次输入密码'));
                } else if (value !== this.formInline.password) {
                    callback(new Error('两次输入密码不一致!'));
                } else {
                    callback();
                }
            };
            return {
                isReadonly: false,
                cutNUm: '获取验证码',
                canClick: true,
                current: 0,
                formInline: {
                    account: '',
                    phone: '',
                    code: '',
                    password: '',
                    checkPass: '',
                },
                ruleInline: {
                    phone: [
                        { required: true, validator: validatePhone, trigger: 'blur' }
                    ],
                    code: [
                        { required: true, message: '请输入验证码', trigger: 'blur' }
                    ],
                    password: [
                        { validator: validatePass, trigger: 'blur' }
                    ],
                    checkPass: [
                        { validator: validatePass2, trigger: 'blur' }
                    ],
                }
            }
        },
        props:{
          infoData: {
            type: Object,
            default: null
          }
        },
        mounted(){
          this.infoData ? this.formInline.phone = this.infoData.phone : this.formInline.phone = '';
        },
        methods: {
            // 短信验证码
            cutDown () {
                if (this.formInline.phone) {
                    if (!this.canClick) return;
                    this.canClick = false;
                    this.cutNUm = 60;
                    let data = {
                        phone: this.formInline.phone,
                        types: 1
                    };
                    captchaApi(data).then(async res => {
                        this.$message.success(res.msg);
                    })
                    let time = setInterval(() => {
                        this.cutNUm--;
                        if (this.cutNUm === 0) {
                            this.cutNUm = '获取验证码';
                            this.canClick = true;
                            clearInterval(time)
                        }
                    }, 1000)
                } else {
                    this.$message.warning('请填写手机号!');
                }
            },
            handleSubmit1 (name, current) {
                this.$refs[name].validate((valid) => {
                    if (valid) {
                      this.current = 1;
                    } else {
                        return false;
                    }
                })
            },
            handleSubmit2(name) {
                this.formInline.account = this.formInline.phone
                this.$refs[name].validate((valid) => {
                    if (valid) {
                      updatePasswordApi(this.formInline).then(async res => {
                            this.$message.success('修改成功');
                            this.current = 2;
                        })
                    } else {
                        return false;
                    }
                })
            },
            //登录
            handleSubmit (name) {
                this.$refs[name].validate((valid) => {
                    if (valid) {
                        configApi({
                            account: this.formInline.account,
                            password: this.formInline.password
                        }).then(async res => {
                            this.$message.success('登录成功!');
                            this.$emit('on-Login');
                        })
                    } else {
                        return false;
                    }
                })
            },
            returns () {
                this.current === 0 ? this.$emit('goback'): this.current = 0
            }
        }
    }
</script>

<style scoped lang="scss">
  .login-form {
    flex: 1;
    padding: 32px 0;
    text-align: center;
    width: 384px;
    margin: 0 auto;
    overflow: hidden;
  }
  .width100{
    width: 100%;
  }

    .maxInpt{
        max-width:400px;
        margin-left:auto;
        margin-right:auto;
    }
    .code {
        display: flex;
        align-items: center;
        justify-content: center;
    }
    .ivu-steps-item:last-child{
        width: unset!important;
    }
</style>
