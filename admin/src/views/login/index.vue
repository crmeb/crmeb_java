<template>
  <div
    class="page-account"
    :style="
      backgroundImages
        ? { backgroundImage: 'url(' + backgroundImages + ')' }
        : { backgroundImage: 'url(' + backgroundImageMo + ')' }
    "
  >
    <div class="container" :class="[fullWidth > 768 ? 'containerSamll' : 'containerBig']">
      <template v-if="fullWidth > 768">
        <swiper :options="swiperOption" class="swiperPross">
          <swiper-slide v-for="(item, index) in swiperList" :key="index" class="swiperPic">
            <img :src="item.pic" />
          </swiper-slide>
          <div slot="pagination" class="swiper-pagination" />
        </swiper>
      </template>
      <div class="index_from page-account-container">
        <div class="page-account-top">
          <div class="page-account-top-logo">
            <img :src="loginLogo" alt="logo" />
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
              @blur="onBlurAccount"
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

          <div class="acea-row">
            <el-button
              :loading="loading"
              type="primary"
              style="width: 100%; margin-bottom: 30px"
              @click.native.prevent="handleLogin"
              :disabled="disabled"
              >登录
            </el-button>
          </div>
        </el-form>
        <verifition-verify ref="verifyRef" @success="handlerOnVerSuccess"></verifition-verify>
      </div>
    </div>
  </div>
</template>

<script>
import Cookies from 'js-cookie';
import { validUsername } from '@/utils/validate';
import '@/assets/js/canvas-nest.min.js';
import { getLoginPicApi } from '@/api/user';
import { getStoreStaff } from '@/libs/public';
import VerifitionVerify from './verifition/Verify.vue';
import { accountDetectionApi } from '@/api/authInformation';
import { frontDomainApi, mediaDomainApi, getSiteLogoApi } from '@/api/systemConfig';
export default {
  name: 'Login',
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!validUsername(value)) {
        callback(new Error('Please enter the correct user name'));
      } else {
        callback();
      }
    };
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6 || value.length > 12) {
        callback(new Error('密码位数为6-12位'));
      } else {
        callback();
      }
    };
    return {
      captchatOn: true, // 是否开启行为验证码
      swiperList: [],
      loginLogo: '',
      backgroundImages: '',
      backgroundImageMo: require('@/assets/imgs/bg.jpg'),
      fullWidth: document.body.clientWidth,
      swiperOption: {
        pagination: {
          el: '.pagination',
        },
        autoplay: {
          enabled: true,
          disableOnInteraction: false,
          delay: 3000,
        },
      },
      loginForm: {
        account: '',
        pwd: '',
        captchaVO: {},
      },
      loginRules: {
        account: [{ required: true, trigger: 'blur', message: '请输入用户名' }], // validator: validateUsername
        pwd: [{ required: true, trigger: 'blur', message: '请输入密码' }],
      },
      passwordType: 'password',
      capsTooltip: false,
      loading: false,
      showDialog: false,
      redirect: undefined,
      otherQuery: {},
      disabled: false,
      //账号密码输入错误次数
      errorsNumber: 0,
    };
  },
  components: {
    VerifitionVerify,
  },
  watch: {
    fullWidth(val) {
      // 为了避免频繁触发resize函数导致页面卡顿，使用定时器
      if (!this.timer) {
        // 一旦监听到的screenWidth值改变，就将其重新赋给data里的screenWidth
        this.screenWidth = val;
        this.timer = true;
        const that = this;
        setTimeout(function () {
          // 打印screenWidth变化的值
          that.timer = false;
        }, 400);
      }
    },
    $route: {
      handler: function (route) {
        const query = route.query;
        if (query) {
          this.redirect = query.redirect;
          this.otherQuery = this.getOtherQuery(query);
        }
      },
      immediate: true,
    },
  },
  created() {
    const _this = this;
    document.onkeydown = function (e) {
      if (_this.$route.path.indexOf('login') !== -1) {
        const key = window.event.keyCode;
        if (key === 13) {
          _this.handleLogin();
        }
      }
    };
    window.addEventListener('resize', this.handleResize);
  },
  mounted() {
    this.getInfo();
    this.onBlurAccount();
    this.$nextTick(() => {
      if (this.screenWidth < 768) {
        document.getElementsByTagName('canvas')[0].removeAttribute('class', 'index_bg');
      } else {
        document.getElementsByTagName('canvas')[0].className = 'index_bg';
      }
    });
    if (this.loginForm.account === '') {
      this.$refs.account.focus();
    } else if (this.loginForm.pwd === '') {
      this.$refs.pwd.focus();
    }
  },
  beforeCreate() {
    if (this.fullWidth < 768) {
      document.getElementsByTagName('canvas')[0].removeAttribute('class', 'index_bg');
    } else {
      document.getElementsByTagName('canvas')[0].className = 'index_bg';
    }
  },
  destroyed() {
    // window.removeEventListener('storage', this.afterQRScan)
  },
  beforeDestroy: function () {
    window.removeEventListener('resize', this.handleResize);
    document.getElementsByTagName('canvas')[0].removeAttribute('class', 'index_bg');
  },
  methods: {
    //校验成功之后
    handlerOnVerSuccess(repData) {
      this.loginForm.captchaVO = repData;
      this.success(null, true);
    },
    //账号失去焦点
    async onBlurAccount() {
      if(this.loginForm.account){
        this.errorsNumber = await accountDetectionApi({ account: this.loginForm.account });
      }
    },
    // 获取移动端域名-图片域名
    async getUrl() {
      let res = await getSiteLogoApi();
      Cookies.set('logoInfo', JSON.stringify(res));
      frontDomainApi().then((res) => {
        this.$store.commit('settings/SET_FrontDomain', res);
      });
      mediaDomainApi().then((res) => {
        this.$store.commit('settings/SET_mediaDomain', res);
      });
    },
    handleResize(event) {
      this.fullWidth = document.body.clientWidth;
      if (this.fullWidth < 768) {
        document.getElementsByTagName('canvas')[0].removeAttribute('class', 'index_bg');
      } else {
        document.getElementsByTagName('canvas')[0].className = 'index_bg';
      }
    },
    getInfo() {
      getLoginPicApi().then((res) => {
        this.swiperList = res.banner;
        this.loginLogo = res.loginLogo;
        this.backgroundImages = res.backgroundImage;
        localStorage.setItem('singleAdminSiteName', res.siteName);
      });
    },
    checkCapslock(e) {
      const { key } = e;
      this.capsTooltip = key && key.length === 1 && key >= 'A' && key <= 'Z';
    },
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = '';
      } else {
        this.passwordType = 'password';
      }
      this.$nextTick(() => {
        this.$refs.pwd.focus();
      });
    },
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          if (Number(this.errorsNumber) > 3) {
            this.$refs.verifyRef.show();
          } else {
            this.success(null);
          }
        } else {
          return false;
        }
      });
    },
    success(params, type) {
      // this.loginForm.captcha = this.$store.state.user.captcha;
      // this.loginForm.captcha.captchaVerification = params ? params.captchaVerification : '';
      const loading = this.$loading({
        lock: true,
        text: '正在登录中.',
      });
      this.$store
        .dispatch('user/login', this.loginForm)
        .then(() => {
          this.$router.push({
            path: this.redirect || '/',
            query: this.otherQuery,
          });
          getStoreStaff();
          loading.close();
          this.disabled = true;
          this.getUrl();
          this.$store
            .dispatch('user/getMenus', {
              that: this,
            })
            .then((res) => {
              this.$router.push({ path: this.redirect || '/dashboard', query: this.otherQuery });
              //location.reload();
            });
        })
        .catch(async (err) => {
          await this.onBlurAccount();
          if (Number(this.errorsNumber) > 3 && !type) await this.$refs.verifyRef.show();
          loading.close();
          this.disabled = false;
        });
    },
    getOtherQuery(query) {
      return Object.keys(query).reduce((acc, cur) => {
        if (cur !== 'redirect') {
          acc[cur] = query[cur];
        }
        return acc;
      }, {});
    },
  },
};
</script>

<style lang="scss" scoped>
$screen-md: 768px;
$font-size-base: 14px;
$animation-time: 0.3s;
$animation-time-quick: 0.15s;
$transition-time: 0.2s;
$ease-in-out: ease-in-out;
$subsidiary-color: #808695;

.footer {
  align-items: center;
  justify-content: center;
  width: 50%;
  height: 36px;
  cursor: pointer;
}

.wechat {
  width: 26px;
  height: 26px;

  img {
    width: 100%;
    height: 100%;
  }
}

.page-account {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  overflow: auto;

  &-container {
    flex: 1;
    padding: 32px 0;
    text-align: center;
    width: 384px;
    margin: 0 auto;

    &-result {
      width: 100%;
    }
  }

  &-tabs {
    .ivu-tabs-bar {
      border-bottom: none;
    }

    .ivu-tabs-nav-scroll {
      text-align: center;
    }

    .ivu-tabs-nav {
      display: inline-block;
      float: none;
    }
  }

  &-top {
    padding: 32px 0;

    &-logo {
      img {
        max-height: 75px;
      }
    }

    &-desc {
      font-size: $font-size-base;
      color: $subsidiary-color;
    }
  }

  &-auto-login {
    margin-bottom: 24px;
    text-align: left;

    a {
      float: right;
    }
  }

  &-other {
    margin: 24px 0;
    text-align: left;

    span {
      font-size: $font-size-base;
    }

    img {
      width: 24px;
      margin-left: 16px;
      cursor: pointer;
      vertical-align: middle;
      opacity: 0.7;
      transition: all $transition-time $ease-in-out;

      &:hover {
        opacity: 1;
      }
    }
  }

  .ivu-poptip,
  .ivu-poptip-rel {
    display: block;
  }

  &-register {
    float: right;

    &-tip {
      text-align: left;

      &-title {
        font-size: $font-size-base;
      }

      &-desc {
        white-space: initial;
        font-size: $font-size-base;
        margin-top: 6px;
      }
    }
  }

  &-to-login {
    text-align: center;
    margin-top: 16px;
  }

  &-header {
    text-align: right;
    position: fixed;
    top: 16px;
    right: 24px;
  }
}

.labelPic {
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

.swiperPross,
.swiperPic,
.swiperPic img {
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
  padding: 80px 0 20px 0 !important;
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
.captcha {
  display: flex;
  align-items: flex-start;
}

$bg: #2d3a4b;
$dark_gray: #889aa4;
$light_gray: #eee;

.imgs {
  position: relative;
  height: 36px;
  cursor: pointer;

  img {
    height: 100%;
  }

  span {
    width: 84px;
    line-height: 36px;
    display: inline-block;
    background: rgba(0, 0, 0, 0.4);
    position: absolute;
    left: 0;
    color: #fff;
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

  ::v-deepsvg-icon {
    vertical-align: 0.3em;
  }
}

.thirdparty-button {
  position: absolute;
  right: 0;
  bottom: 6px;
}
</style>
