<template>
  <view
    class="page"
    :data-theme="theme"
    :style="[colorStyle, { 'background-image': `url(${backBg})` }]"
  >
    <view class="system-height" :style="{ height: statusBarHeight }"></view>
    <!-- #ifdef MP -->
    <view class="title-bar" style="height: 43px">
      <view class="icon" @click="back" v-if="!isHome">
        <image :src="urlDomain + 'crmebimage/perset/usersImg/left.png'"></image>
      </view>
      <view class="icon" @click="home" v-else>
        <image :src="urlDomain + 'crmebimage/perset/usersImg/home.png'"></image>
      </view>
      账户登录
    </view>
    <!-- #endif -->
    <view class="wechat_login">
      <view class="img acea-row row-center">
        <image :src="mobileLoginLogo" mode="widthFix" class="image"></image>
      </view>
      <view class="company">{{ companyName }}</view>
      <view class="btn-wrapper">
        <!-- #ifdef H5 -->
        <button hover-class="none" @click="wechatLogin" class="btn1 bg-color">
          <text class="iconfont icon-weixin2"></text>立即登录
        </button>
        <!-- #endif -->
        <!-- #ifdef MP -->
        <button
          v-if="wxLogin"
          hover-class="none"
          open-type="getPhoneNumber"
          @getphonenumber="getUserProfileWithPhone"
          class="btn1 bg-color"
        >
          立即登录
        </button>
        <view v-else>
          <button
            v-if="canUseManualPhone"
            hover-class="none"
            @click="onUserPhone('isPhone')"
            class="btn2"
          >
            手动绑定手机号
          </button>
        </view>
        <!-- #endif -->
        <button hover-class="none" @click="cancelLogin" class="btn-cancel">
          取消登录
        </button>
      </view>
    </view>
    <block v-if="isUp">
      <mobileLogin
        :isUp="isUp"
        :loginConfig="loginConfig"
        @close="maskClose"
        :authKey="authKey"
        @wechatPhone="wechatPhone"
      ></mobileLogin>
    </block>
  </view>
</template>

<script setup>
import { computed, ref, getCurrentInstance } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import { useAppStore } from "@/store/app.js";
import Cache from "@/utils/cache.js";
import util from "@/utils/util.js";
import mobileLogin from "@/components/login_mobile/index.vue";
import { getUserPhone } from "@/api/public.js";
import {
  LOGO_URL,
  EXPIRES_TIME,
  USER_INFO,
  BACK_URL,
  STATE_R_KEY,
} from "@/config/cache.js";
import { HTTP_REQUEST_URL } from "@/config/app.js";
import { loginConfigApi } from "@/api/api.js";
import { getUserInfo, spread } from "@/api/user.js";
import Routine from "@/libs/routine.js";
// #ifdef H5
import wechat from "@/libs/wechat.js";
// #endif
import { useColor } from '@/composables/useColor.js';

const app = getApp();
const { proxy } = getCurrentInstance();
const appStore = useAppStore();

let statusBarHeight = getStatusBarHeight() + "px";

const urlDomain = ref(Cache.get("imgHost"));
const isUp = ref(false); // 绑定手机号手动输入弹窗
const phone = ref("");
const isHome = ref(false);
const code = ref("");
const authKey = ref("");
const options = ref("");
const userInfoData = ref({});
const codeNum = ref(0);
const pageStyle = ref({}); //背景图
const theme = ref(app.globalData.theme);
const { colorStyle } = useColor();
const backBg = ref(""); //背景图片
const wxCode = ref(""); //小程序code值
const companyName = ref(app.globalData.companyName); //公司名称
const routinePhoneVerification = ref(app.globalData.routinePhoneVerification); //小程序手机号校验类型（多选）1微信小程序验证 2短信验证
const loginConfig = ref(""); //小程序绑定手机号，isPhone其他手机号绑定
const wxLogin = ref(true); //登录显示
const mobileLoginLogo = ref(app.globalData.mobileLoginLogo); // 登录页logo
const routinePhoneTypes = computed(() =>
  parseRoutinePhoneVerification(routinePhoneVerification.value),
);
const canUseManualPhone = computed(() => routinePhoneTypes.value.includes("2"));

function getStatusBarHeight() {
  if (typeof uni.getWindowInfo === "function") {
    return uni.getWindowInfo().statusBarHeight || 0;
  }
  return 0;
}

function parseRoutinePhoneVerification(value) {
  if (Array.isArray(value)) {
    return value.map((item) => String(item));
  }

  if (typeof value === "number") {
    return String(value).split("");
  }

  const normalizedValue = String(value || "").trim();
  if (!normalizedValue) return [];

  return (normalizedValue.includes(",")
    ? normalizedValue.split(",")
    : normalizedValue.split("")
  )
    .map((item) => item.trim())
    .filter(Boolean);
}

async function ensureRoutinePhoneVerification() {
  if (routinePhoneTypes.value.length) return;

  if (app.globalData.routinePhoneVerification) {
    routinePhoneVerification.value = app.globalData.routinePhoneVerification;
    if (routinePhoneTypes.value.length) return;
  }

  try {
    const res = await loginConfigApi();
    const config = res.data || {};
    app.globalData.routinePhoneVerification =
      config.routinePhoneVerification || "";
    routinePhoneVerification.value = app.globalData.routinePhoneVerification;
  } catch (error) {}
}

function decodeBackUrl(backUrl, fallbackUrl = "") {
  if (!backUrl) return fallbackUrl;
  try {
    return decodeURIComponent(decodeURIComponent(backUrl));
  } catch (error) {
    try {
      return decodeURIComponent(backUrl);
    } catch (e) {
      return fallbackUrl;
    }
  }
}

function normalizeBackUrl(backUrl) {
  if (!backUrl) return "/pages/index/index";
  if (
    backUrl.indexOf("/pages/users/wechat_login/index") !== -1 ||
    backUrl.indexOf("/pages/users/login/index") !== -1 ||
    backUrl.indexOf("/pages/users/app_login/index") !== -1
  ) {
    return "/pages/index/index";
  }
  return backUrl.indexOf("/pages/index/index") !== -1 ? "/" : backUrl;
}

function syncBackUrl(backUrl) {
  const decodedBackUrl = decodeBackUrl(backUrl);
  if (!decodedBackUrl) return "";
  const normalizedBackUrl = normalizeBackUrl(decodedBackUrl);
  uni.setStorageSync("snRouter", normalizedBackUrl);
  return normalizedBackUrl;
}

function getBackUrl() {
  const rawBackUrl =
    options.value.back_url ||
    uni.getStorageSync("snRouter") ||
    Cache.get(BACK_URL) ||
    "";
  return normalizeBackUrl(decodeBackUrl(rawBackUrl));
}

function finishWechatLogin() {
  Cache.clear("snsapiKey");
  const backUrl = getBackUrl();
  Cache.clear(BACK_URL);
  uni.removeStorageSync("snRouter");
  isUp.value = false;
  uni.showToast({
    title: "登录成功",
    icon: "none",
  });
  setTimeout(() => {
    location.href = backUrl;
  }, 800);
}

function getH5UserInfoData() {
  return getUserInfo().then((res) => {
    appStore.UPDATE_USERINFO(res.data);
    return res;
  });
}

onLoad((loadOptions) => {
  //背景图片
  switch (app.globalData.theme) {
    case "theme1":
      backBg.value = `${urlDomain.value}/crmebimage/perset/usersImg/wxbj1.png`;
      break;
    case "theme2":
      backBg.value = `${urlDomain.value}/crmebimage/perset/usersImg/wxbj2.png`;
      break;
    case "theme3":
      backBg.value = `${urlDomain.value}/crmebimage/perset/usersImg/wxbj3.png`;
      break;
    case "theme4":
      backBg.value = `${urlDomain.value}/crmebimage/perset/usersImg/wxbj4.png`;
      break;
    case "theme5":
      backBg.value = `${urlDomain.value}/crmebimage/perset/usersImg/wxbj5.png`;
      break;
  }

  // #ifdef H5
  document.body.addEventListener("focusout", () => {
    setTimeout(() => {
      const scrollHeight =
        document.documentElement.scrollTop || document.body.scrollTop || 0;
      window.scrollTo(0, Math.max(scrollHeight - 1, 0));
    }, 100);
  });
  const { code: authCode, state, scope } = loadOptions;
  options.value = loadOptions;
  syncBackUrl(loadOptions.back_url || "");
  // 获取确认授权code
  code.value = authCode || "";
  const registerAuthKey = loadOptions.authKey || Cache.get("snsapiKey") || "";
  if (!authCode && registerAuthKey) {
    authKey.value = registerAuthKey;
    isUp.value = true;
  }
  //if(!authCode) location.replace(decodeURIComponent(decodeURIComponent(option.query.back_url)));
  if (authCode && options.value.scope !== "snsapi_base") {
    let spreadId = app.globalData.spread ? app.globalData.spread : 0;
    //公众号授权登录回调 wechatAuth(code, Cache.get("spread"), loginType)
    wechat
      .auth(authCode, spreadId)
      .then((res) => {
        if (res.type === "register") {
          Cache.set("snsapiKey", res.key);
          authKey.value = res.key;
          isUp.value = true;
        }
        if (res.type === "login") {
          appStore.LOGIN({
            token: res.token,
          });
          appStore.SETUID(res.uid);
          getH5UserInfoData().then(() => {
            finishWechatLogin();
          }).catch(() => {
            finishWechatLogin();
          });
        }
      })
      .catch((error) => {});
  }
  // #endif
  let pages = getCurrentPages();
  // let prePage = pages[pages.length - 2];
  // if (prePage.route == 'pages/order_addcart/order_addcart') {
  // 	isHome.value = true
  // } else {
  // 	isHome.value = false
  // }
});

function back() {
  cancelLogin();
}

function cancelLogin() {
  uni.navigateBack({
    fail() {
      uni.switchTab({
        url: "/pages/index/index",
      });
    },
  });
}

function home() {
  uni.switchTab({
    url: "/pages/index/index",
  });
}

// 弹窗关闭
function maskClose() {
  // isUp.value = false  //点击模态框会关闭登录弹框，防止用户误触而关闭
}

// #ifdef MP
/**
 * 获取个人用户信息
 */
function getUserInfoData() {
  getUserInfo().then((res) => {
    uni.hideLoading();
    userInfoData.value = res.data;
    appStore.UPDATE_USERINFO(res.data);
    util.Tips(
      {
        title: "登录成功",
        icon: "success",
      },
      {
        tab: 3,
      },
    );
  });
}

//绑定手机号
function onUserPhone(type) {
  loginConfig.value = type;
  //如果是其他手机号绑定，调整页面。否则授权本机手机号登录
  if (loginConfig.value === "isPhone") {
    uni.navigateTo({
      url: `/pages/users/app_login/index?code=${wxCode.value}&authKey=${authKey.value}`,
    });
  }
}

async function openRegisterPhoneFlow() {
  wxLogin.value = false;
  isUp.value = false;
  await ensureRoutinePhoneVerification();

  if (canUseManualPhone.value) {
    onUserPhone("isPhone");
    return;
  }

  util.Tips({
    title: "未配置手机号绑定方式",
  });
}

function isPhoneAuthorizeSuccess(detail = {}) {
  const errMsg = detail.errMsg || "";
  const hasPhoneCode = !!detail.code;
  const hasEncryptedPhone = !!detail.encryptedData && !!detail.iv;
  return (
    (!errMsg || errMsg.indexOf(":ok") !== -1) &&
    (hasPhoneCode || hasEncryptedPhone)
  );
}

async function bindRegisterPhone(phoneDetail = {}) {
  if (!isPhoneAuthorizeSuccess(phoneDetail)) {
    uni.hideLoading();
    wxLogin.value = false;
    await ensureRoutinePhoneVerification();
    util.Tips({
      title: canUseManualPhone.value
        ? "请授权手机号或手动绑定"
        : "请授权手机号完成登录",
    });
    return;
  }

  try {
    const hasEncryptedPhone = !!phoneDetail.encryptedData && !!phoneDetail.iv;
    if (!hasEncryptedPhone) {
      uni.hideLoading();
      wxLogin.value = false;
      await ensureRoutinePhoneVerification();
      util.Tips({
        title: canUseManualPhone.value
          ? "当前手机号授权方式不兼容，请手动绑定手机号"
          : "手机号授权数据不完整，请重新授权",
      });
      return;
    }

    // code2Session 需要 wx.login() 的新 code，不能使用 getPhoneNumber 返回的 phoneCode。
    const bindingCode = await Routine.getCode();
    const phoneData = {
      encryptedData: phoneDetail.encryptedData || "",
      iv: phoneDetail.iv || "",
      code: bindingCode,
      key: authKey.value,
      type: "routine",
    };
    if (phoneDetail.code) phoneData.phoneCode = phoneDetail.code;
    if (bindingCode) phoneData.wxCode = bindingCode;

    const res = await getUserPhone(phoneData);
    appStore.LOGIN({
      token: res.data.token,
    });
    appStore.SETUID(res.data.uid);
    getUserInfoData();
    if (app.globalData.spread) {
      spread(app.globalData.spread).then((res) => {});
    }
  } catch (error) {
    uni.hideLoading();
    util.Tips({
      title: error,
    });
  }
}

function getUserProfileWithPhone(event) {
  uni.showLoading({
    title: "正在登录中",
  });
  Routine.getCode()
    .then((getCode) => {
      const userInf = {
        code: getCode,
        spread_spid: app.globalData.spread,
        avatar: "",
        nickName: "微信用户",
        type: "routine",
      };
      getWxUser(getCode, userInf, event.detail || {});
    })
    .catch((res) => {
      uni.hideLoading();
    });
}

// 登录调用方法
function getWxUser(wxUserCode, userInf, phoneDetail) {
  wxCode.value = wxUserCode;
  Routine.authUserInfo(userInf.code, userInf)
    .then((res) => {
      authKey.value = res.data.key;
      if (res.data.type === "register") {
        if (phoneDetail) {
          bindRegisterPhone(phoneDetail);
        } else {
          uni.hideLoading();
          openRegisterPhoneFlow();
        }
      }
      if (res.data.type === "login") {
        uni.hideLoading();
        appStore.LOGIN({
          token: res.data.token,
        });
        appStore.SETUID(res.data.uid);
        getUserInfoData();
        if (app.globalData.spread) {
          spread(app.globalData.spread).then((res) => {}); //登录成功后读取spread绑定分销关系
        }
      }
    })
    .catch((res) => {
      uni.hideLoading();
      util.Tips({
        title: res,
      });
    });
}
// #endif

// #ifdef H5
// 获取url后面的参数
function getQueryString(name) {
  var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
  var reg_rewrite = new RegExp("(^|/)" + name + "/([^/]*)(/|$)", "i");
  var r = window.location.search.substr(1).match(reg);
  var q = window.location.pathname.substr(1).match(reg_rewrite);
  if (r != null) {
    return unescape(r[2]);
  } else if (q != null) {
    return unescape(q[2]);
  } else {
    return null;
  }
}

// 公众号登录
function wechatLogin() {
  if (!code.value && options.value.scope !== "snsapi_base") {
    proxy.$wechat.oAuth("snsapi_userinfo", "/pages/users/wechat_login/index");
  } else {
    // if (authKey.value) {
    // 	isUp.value = true;
    // }
    isUp.value = true;
  }
}

// 输入手机号后的回调
function wechatPhone() {
  Cache.clear("snsapiKey");
  finishWechatLogin();
}
// #endif
</script>
<style lang="scss">
page {
  background: #fff;
  height: 100%;
}
</style>
<style lang="scss" scoped>
.icon-weixin2 {
  margin-right: 10rpx;
}
.company {
  font-size: 40rpx;
  color: #333;
  text-align: center;
  font-weight: 500;
  margin: 32rpx 0 96rpx 0;
}

.page {
  background: #fff;
  height: 100vh;
  background-size: contain;
  background-repeat: no-repeat;
}

.wechat_login {
  padding-top: 238rpx;

  .img .image {
    width: 152rpx;
    height: 152rpx;
  }

  .btn-wrapper {
    margin-top: 86rpx;
    padding: 0 66rpx;

    button {
      width: 100%;
      height: 86rpx;
      line-height: 84rpx;
      margin-bottom: 32rpx;
      border-radius: 120rpx;
      font-size: 32rpx;

      &.btn1 {
        color: #fff;
      }

      &.btn2 {
        color: #666666;
        border: 2px solid #e4e4e4;
      }

      &.btn-cancel {
        color: #999;
        background: #fff;
        border: 2px solid #e4e4e4;
      }
    }
  }
}

.title-bar {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
}

.icon {
  position: absolute;
  left: 30rpx;
  top: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 86rpx;
  height: 86rpx;

  image {
    width: 50rpx;
    height: 50rpx;
  }
}
</style>
