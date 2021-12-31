<template>
  <div class="divBox">
    <el-card class="box-card">
      <div class="page_title">授权证书申请</div>
      <div class="page_desc">您的支持是我们不断进步的动力，商业授权更多是一个保障和附加的增值服务，让您优先享受新版本的强大功能和安全保障</div>
      <el-form ref="form" :model="form" label-width="80px" :rules="rules">
        <el-form-item label="企业名称" prop="company_name">
          <el-input v-model="form.company_name" placeholder="请填写您的企业名称"></el-input>
        </el-form-item>
       <el-form-item label="企业域名" prop="domain_name">
          <el-input v-model="form.domain_name" placeholder="请输入域名，格式：baidu.com"></el-input>
        </el-form-item>
        <el-form-item label="订单号" prop="order_id">
          <el-input v-model="form.order_id" placeholder="请输入您在淘宝或小程序购买的源码订单号"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="负责人电话"></el-input>
        </el-form-item>
        <el-form-item label="授权产品">
          <el-input value="java" disabled></el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="captcha">
          <div class="captcha">
              <el-input
                ref="username"
                v-model="form.captcha"
                style="width: 218px;"
                prefix-icon="el-icon-message"
                placeholder="验证码"
                name="username"
                type="text"
                tabindex="3"
                autocomplete="on"
              />
              <div class="imgs" @click="getCaptcha()">
                <img :src="captchs">
              </div>
            </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('form')">提交</el-button>
          <el-button @click="resetForm('form')">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import {authCertSubmit} from '@/api/authInformation'
import {configSaveUniq} from '@/api/systemConfig'
import {Debounce} from '@/utils/validate'
export default {
  name: "index",
  data() {
    return {
       form: {
          company_name: '',
          domain_name:'',
          order_id:'',
          captcha:'',
          phone:'',
          label:22
        },
        captchs: 'http://authorize.crmeb.net/api/captchs/',
        rules:{
          company_name: [
          {required: true, message: '请填写您的企业名称', trigger: 'blur'}
          ],
          domain_name: [
            { required: true, message: '请输入域名，格式：baidu.com', trigger: 'blur' }
          ],
          order_id: [
            { required: true, message: '请输入您购买的源码订单号', trigger: 'blur' }
          ],
          phone: [
            { required: true, message: '请输入负责人电话', trigger: 'blur' }
          ],
          captcha: [
            { required: true, message: '请输入验证码', trigger: 'blur' }
          ]
        }
    }
  },
  mounted(){
    this.getCaptcha();
  },
  methods: {
    getCaptcha(){
      this.captchs = this.captchs + Date.parse(new Date());
    },
    submitForm:Debounce(function(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          authCertSubmit(this.form).then(res=>{
            if(res.status === 200){
              this.$modal.msgSuccess(res.msg);
              configSaveUniq({key:'authHost',value:this.form.domain_name})
            }else{
              this.$modal.msgError(res.msg);
            }
          }).catch(res => {
            this.getCaptcha();
            return this.$modal.msgError(res.msg);
          })
        } else {
          return false;
        }
      });
    }),
    resetForm(formName) {
      this.$refs[formName].resetFields();
    }
  },
};
</script>

<style scoped lang="scss">
.page_title{
  text-align: center;
  font-size: 30px;
  padding:20px 0 20px;
}
.page_desc{
  font-size: 18px;
  text-align: center;
  margin-bottom: 40px;
}
.captcha{
  display: flex;
  align-items: flex-start;
}
.imgs{
  margin-left:20px;
  img{
    height: 36px;
  }
}
</style>
