<template>
  <div class="page-account" :style="backgroundImages ? { backgroundImage: 'url(' + backgroundImages + ')'} : { backgroundImage: 'url(' + backgroundImageMo + ')'}">
    <div class="container" :class="[ fullWidth > 768 ? 'containerSamll':'containerBig']">
      <template v-if="fullWidth>768">
        <swiper :options="swiperOption" class="swiperPross">
          <swiper-slide v-for="(item,index) in swiperList" :key="index" class="swiperPic">
            <img :src="item.pic">
          </swiper-slide>
          <div slot="pagination" class="swiper-pagination" />
        </swiper>
      </template>
      <div class="index_from page-account-container">
        <div class="page-account-top ">
          <div class="page-account-top-logo">
            <img :src="loginLogo" alt="logo">
          </div>
        </div>
        <el-form
          ref="loginForm"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          autocomplete="on"
          label-position="left"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="account">
            <el-input
              ref="account"
              v-model="loginForm.account"
              prefix-icon="el-icon-user"
              placeholder="用户名"
              name="username"
              type="text"
              tabindex="1"
              autocomplete="on"
            />
          </el-form-item>

          <el-form-item prop="pwd">
            <el-input
              :key="passwordType"
              ref="pwd"
              v-model="loginForm.pwd"
              prefix-icon="el-icon-lock"
              :type="passwordType"
              placeholder="密码"
              name="pwd"
              tabindex="2"
              auto-complete="on"
            />
            <span class="show-pwd" @click="showPwd">
              <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
            </span>
          </el-form-item>

          <el-form-item prop="code" class="captcha">
            <div class="captcha">
              <el-input
                ref="username"
                v-model="loginForm.code"
                style="width: 218px;"
                prefix-icon="el-icon-message"
                placeholder="验证码"
                name="username"
                type="text"
                tabindex="3"
                autocomplete="on"
              />
              <div class="imgs" @click="getCaptcha()"><img :src="captchatImg"></div>
            </div>
          </el-form-item>

          <div class="acea-row">
            <el-button
              :loading="loading"
              type="primary"
              style="width:100%;margin-bottom:30px;"
              @click.native.prevent="handleLogin"
            >登录
            </el-button>
            <!--<div class="acea-row footer" @click="onWechat">-->
              <!--<div class="wechat mr10"><img src="../../assets/imgs/weixin.png"></div>-->
              <!--<span>微信</span>-->
            <!--</div>-->
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { validUsername } from '@/utils/validate'
import "@/assets/js/canvas-nest.min.js";
import { getLoginPicApi, captchaApi, codeCheckApi } from '@/api/user'
import { getStoreStaff } from '@/libs/public'
import { getWXCodeByUrl, loginByWxCode } from "@/libs/wechat";
import { getWechatConfig } from "@/api/wxApi";
import { getToken, removeToken, setToken } from '@/utils/auth'
import Cookies from 'js-cookie'
export default {
  name: 'Login',
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!validUsername(value)) {
        callback(new Error('Please enter the correct user name'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6 || value.length >12) {
        callback(new Error('密码位数为6-12位'))
      } else {
        callback()
      }
    }
    return {
      captchatImg: '',
      swiperList: [],
      loginLogo: '',
      backgroundImages: '',
      backgroundImageMo: require("@/assets/imgs/bg.jpg"),
      fullWidth: document.body.clientWidth,
      swiperOption: {
        pagination: {
          el: '.pagination'
        },
        autoplay: {
          enabled: true,
          disableOnInteraction: false,
          delay: 3000
        }
      },
      loginForm: {
        account: 'demo', // admin
        pwd: 'crmeb.com',
        key: '',
        code: '',
        wxCode: ''
      },
      loginRules: {
        account: [{ required: true, trigger: 'blur', message: '请输入用户名' }], // validator: validateUsername
        pwd: [{ required: true, trigger: 'blur', message: '请输入密码' }],
        code: [{ required: true, message: '请输入正确的验证码', trigger: 'blur' }]
      },
      passwordType: 'password',
      capsTooltip: false,
      loading: false,
      showDialog: false,
      redirect: undefined,
      otherQuery: {}
    }
  },
  watch: {
    fullWidth(val) {
      // 为了避免频繁触发resize函数导致页面卡顿，使用定时器
      if (!this.timer) {
        // 一旦监听到的screenWidth值改变，就将其重新赋给data里的screenWidth
        this.screenWidth = val
        this.timer = true
        const that = this
        setTimeout(function() {
          // 打印screenWidth变化的值
          that.timer = false
        }, 400)
      }
    },
    $route: {
      handler: function(route) {
        const query = route.query
        if (query) {
          this.redirect = query.redirect
          this.otherQuery = this.getOtherQuery(query)
        }
      },
      immediate: true
    }
  },
  created() {
    const _this = this
    document.onkeydown = function(e) {
      if (_this.$route.path.indexOf('login') !== -1) {
        const key = window.event.keyCode
        if (key === 13) {
          _this.handleLogin()
        }
      }
    }
    window.addEventListener('resize', this.handleResize)
  },
  mounted() {
    this.getInfo()
    this.$nextTick(() => {
      if (this.screenWidth < 768) {
        document
          .getElementsByTagName("canvas")[0]
          .removeAttribute("class", "index_bg");
      } else {
        document.getElementsByTagName("canvas")[0].className = "index_bg";
      }
    });
    if (this.loginForm.account === '') {
      this.$refs.account.focus()
    } else if (this.loginForm.pwd === '') {
      this.$refs.pwd.focus()
    }
    this.getCaptcha()
    this.agentWeiXinLogin()
  },
  beforeCreate() {
    if (this.fullWidth < 768) {
      document
        .getElementsByTagName("canvas")[0]
        .removeAttribute("class", "index_bg");
    } else {
      document.getElementsByTagName("canvas")[0].className = "index_bg";
    }
  },
  destroyed() {
    // window.removeEventListener('storage', this.afterQRScan)
  },
  beforeDestroy: function() {
    window.removeEventListener('resize', this.handleResize)
    document
      .getElementsByTagName("canvas")[0]
      .removeAttribute("class", "index_bg");
  },
  methods: {
    agentWeiXinLogin(){ // 判断是否需要微信公众号登陆
      const _isWechat = this.$wechat.isWeixin();
      if (_isWechat) {
        let code = this.$route.query.code
        let state = this.$route.query.state
        let wxAuthPath = location.origin + '/login';
        //  如果没有code 去获取
        if(null == code){
          getWXCodeByUrl(wxAuthPath,'step1');
        }
        // 如果有state=step1 根据code去登陆
        if(state === 'step1'){
          loginByWxCode(code).then(res => {
            sessionStorage.setItem('token',res.token)
            this.$router.push({ path: this.redirect || '/', query: this.otherQuery })
          }).catch(err => {
            // 如果登陆失败，那么输入账号登陆，重新获取code传递给后端做绑定
            getWXCodeByUrl(wxAuthPath,'step2');
          })
        }else if(state === 'step2'){
          this.loginForm.wxCode = code
        }

      }
      },
    onWechat(){
      let url = this.$route.query.redirect ? this.$route.query.redirect : '/dashboard'
      this.$wechat.oAuth(url, 'login')
    },
    handleResize(event) {
      this.fullWidth = document.body.clientWidth
      if (this.fullWidth < 768) {
        document
          .getElementsByTagName("canvas")[0]
          .removeAttribute("class", "index_bg");
      } else {
        document.getElementsByTagName("canvas")[0].className = "index_bg";
      }
    },
    getInfo() {
      getLoginPicApi().then(res => {
        this.swiperList = res.banner
        this.loginLogo = res.loginLogo
        this.backgroundImages = res.backgroundImage
        // Cookies.set('MerInfo', JSON.stringify(data))
      })
    },
    checkCapslock(e) {
      const { key } = e
      this.capsTooltip = key && key.length === 1 && (key >= 'A' && key <= 'Z')
    },
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.pwd.focus()
      })
    },
    handleLogin() {
      const code = this.$route.query.code;
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if(this.$wechat.isWeixin()){
            this.loginForm.wxCode = code
          }
          this.$store.dispatch('user/login', this.loginForm)
            .then(() => {
              this.$router.push({ path: this.redirect || '/', query: this.otherQuery })
              getStoreStaff()
              this.loading = false
            }).catch((err) => {
            this.loading = false
            if(this.$wechat.isPhone()) this.$dialog.error(err.message);
            this.getCaptcha()
          })
        } else {
          return false
        }
      })
    },
    getCaptcha() {
      captchaApi().then(( data ) => {
        this.captchatImg = data.code
        this.loginForm.key = data.key
      }).catch(({ message }) => {
        this.$message.error(message)
      })
    },
    getOtherQuery(query) {
      return Object.keys(query).reduce((acc, cur) => {
        if (cur !== 'redirect') {
          acc[cur] = query[cur]
        }
        return acc
      }, {})
    }
  }
}
</script>

<style lang="scss" scoped>
  $screen-md: 768px;
  $font-size-base: 14px;
  $animation-time         : .3s;
  $animation-time-quick   : .15s;
  $transition-time        : .2s;
  $ease-in-out            : ease-in-out;
  $subsidiary-color       : #808695;
  .footer{
    align-items: center;
    justify-content: center;
    width: 50%;
    height: 36px;
    cursor: pointer;
  }
  .wechat{
    width: 26px;
    height: 26px;
    img{
      width: 100%;
      height: 100%;
    }
  }
  .page-account{
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100vh;
    overflow: auto;

    &-container{
      flex: 1;
      padding: 32px 0;
      text-align: center;
      width: 384px;
      margin: 0 auto;

      &-result{
        width: 100%;
      }
    }

    &-tabs{
      .ivu-tabs-bar{
        border-bottom: none;
      }
      .ivu-tabs-nav-scroll{
        text-align: center;
      }
      .ivu-tabs-nav{
        display: inline-block;
        float: none;
      }
    }
    &-top{
      padding: 32px 0;
      &-logo{
        img{
          max-height: 75px;
        }
      }
      &-desc{
        font-size: $font-size-base;
        color: $subsidiary-color;
      }
    }

    &-auto-login{
      margin-bottom: 24px;
      text-align: left;
      a{
        float: right;
      }
    }

    &-other{
      margin: 24px 0;
      text-align: left;
      span{
        font-size: $font-size-base;
      }
      img{
        width: 24px;
        margin-left: 16px;
        cursor: pointer;
        vertical-align: middle;
        opacity: 0.7;
        transition: all $transition-time $ease-in-out;
        &:hover{
          opacity: 1;
        }
      }
    }

    .ivu-poptip, .ivu-poptip-rel{
      display: block;
    }

    &-register{
      float: right;
      &-tip{
        text-align: left;
        &-title{
          font-size: $font-size-base;
        }
        &-desc{
          white-space: initial;
          font-size: $font-size-base;
          margin-top: 6px;
        }
      }
    }

    &-to-login{
      text-align: center;
      margin-top: 16px;
    }

    &-header{
      text-align: right;
      position: fixed;
      top: 16px;
      right: 24px;
    }
  }
  .labelPic{
    position: absolute;
    right: 0;
  }
  @media (min-width: $screen-md) {
    .page-account {
      background-repeat: no-repeat;
      background-position: center;
      background-size: cover;
    }
    .page-account-container {
      padding: 32px 0 24px 0;
      position: relative;
    }
  }
  .page-account {
    display: flex;
  }

  .page-account .code {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .page-account .code .pictrue {
    height: 40px;
  }

  .swiperPross {
    border-radius: 6px 0px 0px 6px;
    overflow: hidden;
  }

  .swiperPross, .swiperPic, .swiperPic img {
    width: 286px;
    height: 100%;
  }

  .swiperPic img {
    width: 100%;
    height: 100%;
  }

  .container {
    height: 400px !important;
    padding: 0 !important;
    /*overflow: hidden;*/
    border-radius: 6px;
    z-index: 1;
    display: flex;
  }

  .containerSamll {
    /*width: 56% !important;*/
    width: 670px;
    background: #fff !important;
  }

  .containerBig {
    width: auto !important;
    background: #f7f7f7 !important;
  }

  .index_from {
    width: 384px;
    padding: 0 40px 32px 40px;
    height: 400px;
    box-sizing: border-box;
  }

  .page-account-top {
    padding: 20px 0 !important;
    box-sizing: border-box !important;
    display: flex;
    justify-content: center;
  }

  .page-account-container {
    border-radius: 0px 6px 6px 0px;
  }

  .btn {
    background: linear-gradient(90deg, rgba(25, 180, 241, 1) 0%, rgba(14, 115, 232, 1) 100%) !important;
  }
</style>

<style lang="scss" scoped>
  .captcha{
    display: flex;
    align-items: flex-start;
  }
  $bg: #2d3a4b;
  $dark_gray: #889aa4;
  $light_gray: #eee;
  .imgs{
    img{
      height: 36px;
    }
  }
  .login-form {
    position: relative;
    max-width: 100%;
    margin: 0 auto;
    overflow: hidden;
  }
  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;

    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }
  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }
  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
    /deep/.svg-icon {
      vertical-align: 0.3em;
    }
  }
  .thirdparty-button {
    position: absolute;
    right: 0;
    bottom: 6px;
  }
</style>
