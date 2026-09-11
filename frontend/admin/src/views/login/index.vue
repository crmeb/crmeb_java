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
        <swiper
          :modules="swiperModules"
          :pagination="{ clickable: true }"
          :autoplay="{ delay: 3000, disableOnInteraction: false }"
          class="swiperPross"
        >
          <swiper-slide v-for="(item, index) in swiperList" :key="index" class="swiperPic">
            <img :src="item.pic" />
          </swiper-slide>
          <div class="swiper-pagination" />
        </swiper>
      </template>
      <div class="index_from page-account-container">
        <div class="page-account-top">
          <div class="page-account-top-logo">
            <img :src="loginLogo" alt="logo" />
          </div>
        </div>
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          autocomplete="on"
          label-position="left"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="account">
            <el-input
              ref="accountRef"
              v-model="loginForm.account"
              :prefix-icon="User"
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
              ref="pwdRef"
              v-model="loginForm.pwd"
              :prefix-icon="Lock"
              :type="passwordType"
              placeholder="密码"
              name="pwd"
              tabindex="2"
              autocomplete="on"
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
              @click.prevent="handleLogin"
              :disabled="disabled"
              >登录
            </el-button>
          </div>
        </el-form>
        <verifition-verify ref="verifyRef" @success="handlerOnVerSuccess"></verifition-verify>
      </div>
    </div>
    <div class="footer" v-if="companyName">
      <div class="pull-right">{{ companyName }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Pagination, Autoplay } from 'swiper/modules'
import 'swiper/css'
import 'swiper/css/pagination'
import Cookies from 'js-cookie'
import { validUsername } from '@/utils/validate'
import '@/assets/js/canvas-nest.min.js'
import { getLoginPicApi } from '@/api/user'
import VerifitionVerify from './verifition/Verify.vue'
import { accountDetectionApi } from '@/api/authInformation'
import { frontDomainApi, mediaDomainApi, getSiteLogoApi } from '@/api/systemConfig'
import { useUserStore } from '@/store/modules/user'
import { useSettingsStore } from '@/store/modules/settings'
import { ElLoading } from 'element-plus'
import bgImage from '@/assets/imgs/bg.jpg'

defineOptions({ name: 'Login' })

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const settingsStore = useSettingsStore()

const swiperModules = [Pagination, Autoplay]

const loginFormRef = ref(null)
const accountRef = ref(null)
const pwdRef = ref(null)
const verifyRef = ref(null)

const captchatOn = ref(true) // 是否开启行为验证码
const swiperList = ref([])
const loginLogo = ref('')
const backgroundImages = ref('')
const companyName = ref('Copyright © 2026 西安众邦网络科技有限公司')
const version = ref('CRMEB')
const backgroundImageMo = bgImage // 由 import 提供，替代 require
const fullWidth = ref(document.body.clientWidth)
const swiperOption = reactive({
  pagination: {
    el: '.pagination'
  },
  autoplay: {
    enabled: true,
    disableOnInteraction: false,
    delay: 3000
  }
})
const loginForm = reactive({
  account: '',
  pwd: '',
  captchaVO: {}
})
const loginRules = reactive({
  account: [{ required: true, trigger: 'blur', message: '请输入用户名' }],
  pwd: [{ required: true, trigger: 'blur', message: '请输入密码' }]
})
const passwordType = ref('password')
const capsTooltip = ref(false)
const loading = ref(false)
const showDialog = ref(false)
const redirect = ref(undefined)
const otherQuery = ref({})
const disabled = ref(false)
//账号密码输入错误次数
const errorsNumber = ref(0)
let timer = false
let screenWidth = fullWidth.value

watch(fullWidth, (val) => {
  if (!timer) {
    screenWidth = val
    timer = true
    setTimeout(function () {
      timer = false
    }, 400)
  }
})

watch(
  route,
  (route) => {
    const query = route.query
    if (query) {
      redirect.value = query.redirect
      otherQuery.value = getOtherQuery(query)
    }
  },
  { immediate: true }
)

// created 时机（setup 顶层）
{
  document.onkeydown = function (e) {
    if (route.path.indexOf('login') !== -1) {
      const key = window.event.keyCode
      if (key === 13) {
        handleLogin()
      }
    }
  }
  window.addEventListener('resize', handleResize)
}

onMounted(() => {
  getInfo()
  onBlurAccount()
  nextTick(() => {
    const canvas = document.getElementsByTagName('canvas')[0]
    if (canvas) {
      if (screenWidth < 768) {
        canvas.removeAttribute('class', 'index_bg')
      } else {
        canvas.className = 'index_bg'
      }
    }
  })
  if (loginForm.account === '') {
    accountRef.value && accountRef.value.focus()
  } else if (loginForm.pwd === '') {
    pwdRef.value && pwdRef.value.focus()
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  const canvas = document.getElementsByTagName('canvas')[0]
  if (canvas) canvas.removeAttribute('class', 'index_bg')
})

//校验成功之后
function handlerOnVerSuccess(repData) {
  loginForm.captchaVO = repData
  success(null, true)
}

//账号失去焦点
async function onBlurAccount() {
  if (loginForm.account) {
    errorsNumber.value = await accountDetectionApi({ account: loginForm.account })
  }
}

// 获取移动端域名-图片域名
async function getUrl() {
  let res = await getSiteLogoApi()
  Cookies.set('logoInfo', JSON.stringify(res))
  frontDomainApi().then((res) => {
    settingsStore.SET_FrontDomain(res)
  })
  mediaDomainApi().then((res) => {
    settingsStore.SET_mediaDomain(res)
  })
}

function handleResize(event) {
  fullWidth.value = document.body.clientWidth
  const canvas = document.getElementsByTagName('canvas')[0]
  if (canvas) {
    if (fullWidth.value < 768) {
      canvas.removeAttribute('class', 'index_bg')
    } else {
      canvas.className = 'index_bg'
    }
  }
}

function getInfo() {
  getLoginPicApi().then((res) => {
    swiperList.value = res.banner
    loginLogo.value = res.loginLogo
    backgroundImages.value = res.backgroundImage
    localStorage.setItem('singleAdminSiteName', res.siteName)
    companyName.value = res.companyName || ''
    version.value = res.version || 'CRMEB'
  })
}

function checkCapslock(e) {
  const { key } = e
  capsTooltip.value = key && key.length === 1 && key >= 'A' && key <= 'Z'
}

function showPwd() {
  if (passwordType.value === 'password') {
    passwordType.value = ''
  } else {
    passwordType.value = 'password'
  }
  nextTick(() => {
    pwdRef.value && pwdRef.value.focus()
  })
}

function handleLogin() {
  loginFormRef.value.validate((valid) => {
    if (valid) {
      if (Number(errorsNumber.value) > 2) {
        verifyRef.value.show()
      } else {
        success(null)
      }
    } else {
      return false
    }
  })
}

function success(params, type) {
  const loadingInstance = ElLoading.service({
    lock: true,
    text: '正在登录中.'
  })
  userStore
    .login(loginForm)
    .then(() => {
      router.push({
        path: redirect.value || '/',
        query: otherQuery.value
      })
      loadingInstance.close()
      disabled.value = true
      getUrl()
      userStore.getMenus({ that: null }).then((res) => {
        router.push({ path: redirect.value || '/dashboard', query: otherQuery.value })
      })
    })
    .catch(async (err) => {
      await onBlurAccount()
      if (Number(errorsNumber.value) > 2 && !type) await verifyRef.value.show()
      loadingInstance.close()
      disabled.value = false
    })
}

function getOtherQuery(query) {
  return Object.keys(query).reduce((acc, cur) => {
    if (cur !== 'redirect') {
      acc[cur] = query[cur]
    }
    return acc
  }, {})
}

// 暴露给模板（swiper 组件需作为标签使用）
defineExpose({ handleLogin })
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
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  margin: 0;
  background: rgba(255, 255, 255, 0.8);
  border-top: 1px solid #e7eaec;
  overflow: hidden;
  padding-right: 20px;
  height: 36px;
  line-height: 36px;
  z-index: 999;
}

.pull-right {
  float: right !important;
  color: #666;

  a {
    margin-left: 0;
    color: #666;
  }
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
  color: j8bc6f6;
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

  &-register {
    float: right;
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

  :deep(svg-icon) {
    vertical-align: 0.3em;
  }
}

.thirdparty-button {
  position: absolute;
  right: 0;
  bottom: 6px;
}
</style>
