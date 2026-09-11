<template>
	<div class="login-wrapper" :data-theme="theme" :style="colorStyle">
		<div class="shading">
			<image :src="mobileLoginLogo"/>
		</div>
		<div class="whiteBg" v-if="formItem === 1">
			<div class="list" v-if="current !== 1">
				<form @submit.prevent="submit">
					<div class="item">
						<div class="acea-row row-middle">
							<image :src="urlDomain+'/crmebimage/perset/staticImg/phone_1.png'"  style="width: 24rpx; height: 34rpx;"></image>
							<input type="number" class="texts" placeholder="输入手机号码" v-model="account" maxlength="11" required/>
						</div>
					</div>
					<div class="item">
						<div class="acea-row row-middle">
							<image :src="urlDomain+'/crmebimage/perset/staticImg/code_2.png'" style="width: 28rpx; height: 32rpx;"></image>
							<input type="password" class="texts" placeholder="填写登录密码" maxlength="18" v-model="password" required />
						</div>
					</div>
				</form>
			</div>
			<div class="list" v-if="current !== 0 || appLoginStatus || appleLoginStatus">
				<div class="item">
					<div class="acea-row row-middle">
						<image :src="urlDomain+'/crmebimage/perset/staticImg/phone_1.png'" style="width: 24rpx; height: 34rpx;"></image>
						<input type="number" class="texts" placeholder="输入手机号码" v-model="account" maxlength="11"/>
					</div>
				</div>
				<div class="item">
					<div class="acea-row row-middle">
						<image :src="urlDomain+'/crmebimage/perset/staticImg/code_2.png'" style="width: 28rpx; height: 32rpx;"></image>
						<input type="number" placeholder="填写验证码" class="codeIput" v-model="captcha" maxlength="6" />
						<button class="code main_color" :disabled="disabled" :class="disabled === true ? 'on' : ''" @click="code">
							{{ text }}
						</button>
					</div>
				</div>
				<div class="item" v-if="isShowCode">
					<div class="acea-row row-middle">
						<image :src="urlDomain+'/crmebimage/perset/staticImg/code_2.png'" style="width: 28rpx; height: 32rpx;"></image>
						<input type="number" placeholder="填写验证码" class="codeIput" v-model="codeVal" maxlength="6"/>
						<div class="code" @click="again"><img class="code-img" :src="codeUrl" /></div>
					</div>
				</div>
			</div>
			<view class="protocol acea-row row-between-wrapper">
				<checkbox-group class="checkgroup acea-row" @change='isAgree=!isAgree'  style="align-items: end;">
					<!-- #ifndef MP -->
					<checkbox
						class="checkbox"
						:checked="isAgree ? true : false"
						color="#ffffff"
						backgroundColor="#ffffff"
						activeBackgroundColor="var(--view-theme, #E93323)"
						activeBorderColor="var(--view-theme, #E93323)"
						iconColor="#ffffff"
						style="transform: scale(0.9)"
					/>
					<!-- #endif -->
					<!-- #ifdef MP -->
					<checkbox
						class="checkbox"
						:checked="isAgree ? true : false"
						color="#ffffff"
					/>
					<!-- #endif -->
					<text class="protocol_text">我已阅读并同意<text  @click="userAgree('userinfo')"
							class="font_pro">《用户协议》</text>和<text   @click="userAgree('userprivacyinfo')"
							class="font_pro">《隐私政策》</text></text>
				</checkbox-group>
			</view>
			<div class="logon bg_color" @click="loginMobile" v-if="current !== 0">登录</div>
			<div class="logon bg_color" @click="submit" v-if="current === 0">登录</div>
			<!-- #ifndef APP-PLUS -->
			<div class="tips">
				<div v-if="current==0" @click="current = 1">快速登录</div>
				<div v-if="current==1" @click="current = 0">账号登录</div>
			</div>
			<!-- #endif -->
			<!-- #ifdef APP-PLUS -->
			<view class="appLogin" v-if="!appLoginStatus && !appleLoginStatus">
				<view class="hds">
					<span class="line"></span>
					<p>其他方式登录</p>
					<span class="line"></span>
				</view>
				<view class="btn-wrapper">
					<view class="btn wx" @click="wxLogin">
						<span class="iconfont icon-s-weixindenglu1"></span>
					</view>
					<view class="btn mima" v-if="current == 1" @click="current =0">
						<span class="iconfont icon-s-mimadenglu1"></span>
					</view>
					<view class="btn yanzheng" v-if="current == 0" @click="current =1">
						<span class="iconfont icon-s-yanzhengmadenglu1"></span>
					</view>
					<view class="btn apple-btn" @click="appleLogin" v-if="appleShow">
						<view class="iconfont icon-s-pingguo"></view>
					</view>
				</view>
			</view>
			<!-- #endif -->
		</div>
		<div class="bottom"></div>
		<Verify @success="handlerOnVerSuccess" :captchaType="'blockPuzzle'" :imgSize="{ width: '330px', height: '155px' }"
		        ref="verify"></Verify>
	</div>
</template>
<script setup>
import { ref, watch } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import util from '@/utils/util.js';
import Cache from '@/utils/cache.js';
import { useAppStore } from "@/store/app.js";
import { useColor } from "@/composables/useColor.js";
import { useSendVerifyCode } from '@/composables/useSendVerifyCode.js';
import Verify from '../components/verifition/verify.vue';
import {
	loginH5,
	loginMobile as loginMobileApi,
	registerVerify,
	register as registerApi,
	getUserInfo as getUserInfoApi
} from "@/api/user.js";
import { appAuth, appleLogin as appleLoginRequest } from "@/api/public.js";
import { HTTP_REQUEST_URL } from "@/config/app.js";
import Routine from '@/libs/routine.js';
import { Debounce } from '@/utils/validate.js'
import {
	goToAgreement
} from "@/libs/order.js";

const app = getApp();
const BACK_URL = "login_back_url";
const appStore = useAppStore();
const { disabled, text, sendCode } = useSendVerifyCode();

const verify = ref(null);
const isAgree = ref(false);
const urlDomain = ref(Cache.get("imgHost"));
const navList = ref(["快速登录", "账号登录"]);
const current = ref(1);
const account = ref("");
const password = ref("");
const captcha = ref("");
const formItem = ref(1);
const type = ref("login");
const keyCode = ref("");
const codeUrl = ref("");
const codeVal = ref("");
const isShowCode = ref(false);
const platform = ref('');
const appLoginStatus = ref(false); // 微信登录强制绑定手机号码状态
const appUserInfo = ref(null); // 微信登录保存的用户信息
const appleLoginStatus = ref(false); // 苹果登录强制绑定手机号码状态
const appleUserInfo = ref(null);
const appleShow = ref(false); // 苹果登录版本必须要求ios13以上的
const theme = ref(app.globalData.theme);
const { colorStyle } = useColor();
const mobileLoginLogo = ref(app.globalData.mobileLoginLogo); // 登录页logo

watch(formItem, (nval) => {
	if (nval == 1) {
		type.value = 'login'
	} else {
		type.value = 'register'
	}
});

onLoad(() => {
	uni.getSystemInfo({
		success: function(res) {
			if (res.platform.toLowerCase() == 'ios' && res.system.split(' ')[1] >= '13') {
				appleShow.value = true
			}
		}
	});
});

//滑块验证成功后
function handlerOnVerSuccess(data) {
	verify.value.hide();
	codeSend();
}

//发送验证码
function codeSend() {
	if (!account.value) return util.Tips({ title: '请填写手机号码' });
	if (!isAgree.value) return util.Tips({ title: '请勾选用户隐私协议' });
	if (!/^1(3|4|5|7|8|9|6)\d{9}$/i.test(account.value)) return util.Tips({ title: '请输入正确的手机号码' });
	registerVerify(account.value)
		.then(res => {
			util.Tips({ title: res.message });
			sendCode();
		})
		.catch(err => {
			return util.Tips({ title: err });
		});
}

function userAgree(t) {
	goToAgreement(t)
}

// 苹果登录
function appleLogin() {
	account.value = ''
	captcha.value = ''
	if (!isAgree.value) return util.Tips({ title: '请勾选用户隐私协议' });
	uni.showLoading({ title: '登录中' })
	uni.login({
		provider: 'apple',
		timeout: 10000,
		success(loginRes) {
			uni.getUserInfo({
				provider: 'apple',
				success: function(infoRes) {
					appleUserInfo.value = infoRes.userInfo
					appleLoginApi()
				},
				fail() {
					uni.hideLoading()
					uni.showToast({ title: '获取用户信息失败', icon: 'none', duration: 2000 })
				},
				complete() {
					uni.hideLoading()
				}
			});
		},
		fail(error) {
			uni.hideLoading()
		}
	})
}

// 苹果登录Api
function appleLoginApi() {
	appleLoginRequest({
		openId: appleUserInfo.value.openId,
		email: appleUserInfo.value.email == undefined ? '' : appleUserInfo.value.email,
		identityToken: appleUserInfo.value.identityToken || ''
	}).then((res) => {
		appStore.LOGIN({ 'token': res.data.token });
		getUserInfo(res.data);
	}).catch(error => {
		uni.hideLoading();
		uni.showModal({
			title: '提示',
			content: `错误信息${error}`,
			success: function(res) {
				if (res.confirm) {
				} else if (res.cancel) {
				}
			}
		});
	})
}

// App微信登录
const wxLogin = Debounce(function() {
	account.value = ''
	captcha.value = ''
	if (!isAgree.value) return util.Tips({ title: '请勾选用户隐私协议' });
	uni.showLoading({ title: '登录中' })
	uni.login({
		provider: 'weixin',
		success: function(loginRes) {
			// 获取用户信息
			uni.getUserInfo({
				provider: 'weixin',
				success: function(infoRes) {
					uni.hideLoading();
					appUserInfo.value = infoRes.userInfo
					appUserInfo.value.type = platform.value === 'ios' ? 'iosWx' : 'androidWx'
					wxLoginGo(appUserInfo.value)
				},
				fail() {
					uni.hideLoading();
					uni.showToast({ title: '获取用户信息失败', icon: 'none', duration: 2000 })
				},
				complete() {
					uni.hideLoading()
				}
			});
		},
		fail() {
			uni.hideLoading()
			uni.showToast({ title: '登录失败', icon: 'none', duration: 2000 })
		}
	});
})

function wxLoginGo(info) {
	appAuth(info).then(res => {
		if (res.data.type === 'register') {
			uni.navigateTo({
				url: '/pages/users/app_login/index?authKey=' + res.data.key
			})
		}
		if (res.data.type === 'login') {
			appStore.LOGIN({ 'token': res.data.token });
			getUserInfo(res.data);
		}
	}).catch(res => {
		util.Tips({ title: res });
	});
}

function again() {
	codeUrl.value =
		HTTP_REQUEST_URL +
		"/sms_captcha?" +
		"key=" +
		keyCode.value +
		Date.parse(new Date());
}

//手机号验证码登录
const loginMobile = Debounce(function() {
	if (!account.value) return util.Tips({ title: '请填写手机号码' });
	if (!/^1(3|4|5|7|8|9|6)\d{9}$/i.test(account.value)) return util.Tips({ title: '请输入正确的手机号码' });
	if (!captcha.value) return util.Tips({ title: '请填写验证码' });
	if (!/^[\w\d]+$/i.test(captcha.value)) return util.Tips({ title: '请输入正确的验证码' });
	if (!isAgree.value) return util.Tips({ title: '请勾选用户隐私协议' });
	uni.showLoading({ title: '登录中' })
	loginMobileApi({
			phone: account.value,
			captcha: captcha.value,
			spread_spid: Cache.get("spread")
		})
		.then(res => {
			let data = res.data;
			appStore.LOGIN({ 'token': res.data.token });
			uni.hideLoading();
			getUserInfo(data);
		})
		.catch(res => {
			uni.hideLoading();
			util.Tips({ title: res });
		});
})

async function register() {
	if (!account.value) return util.Tips({ title: '请填写手机号码' });
	if (!/^1(3|4|5|7|8|9|6)\d{9}$/i.test(account.value)) return util.Tips({ title: '请输入正确的手机号码' });
	if (!isAgree.value) return util.Tips({ title: '请勾选用户隐私协议' });
	if (!captcha.value) return util.Tips({ title: '请填写验证码' });
	if (!/^[\w\d]+$/i.test(captcha.value)) return util.Tips({ title: '请输入正确的验证码' });
	if (!password.value) return util.Tips({ title: '请填写密码' });
	if (!/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$/i.test(password.value)) return util.Tips({ title: '您输入的密码过于简单' });
	registerApi({
			account: account.value,
			captcha: captcha.value,
			password: password.value,
			spread_spid: Cache.get("spread")
		})
		.then(res => {
			util.Tips({ title: res });
			formItem.value = 1;
		})
		.catch(res => {
			util.Tips({ title: res });
		});
}

async function code() {
	if (!account.value) return util.Tips({ title: '请填写手机号码' });
	if (!isAgree.value) return util.Tips({ title: '请勾选用户隐私协议' });
	if (!/^1(3|4|5|7|8|9|6)\d{9}$/i.test(account.value)) return util.Tips({ title: '请输入正确的手机号码' });
	if (formItem.value == 2) type.value = "register";
	verify.value.show();
}

function navTap(index) {
	current.value = index;
}

//账号密码登录
const submit = Debounce(function() {
	if (!account.value) return util.Tips({ title: '请填写账号' });
	if (!/^[\w\d]{5,16}$/i.test(account.value)) return util.Tips({ title: '请输入正确的账号' });
	if (!password.value) return util.Tips({ title: '请填写密码' });
	if (!isAgree.value) return util.Tips({ title: '请勾选用户隐私协议' });
	uni.showLoading({ title: '登录中' })
	loginH5({
			account: account.value,
			password: password.value,
			spread_spid: Cache.get("spread")
		}).then(({ data }) => {
			appStore.LOGIN({ 'token': data.token });
			uni.hideLoading();
			getUserInfo(data);
		})
		.catch(e => {
			uni.hideLoading();
			util.Tips({ title: e });
		});
})

function getUserInfo(data) {
	appStore.SETUID(data.uid);
	getUserInfoApi().then(res => {
		appStore.UPDATE_USERINFO(res.data);
		let backUrl = Cache.get(BACK_URL) || "/pages/index/index";
		if (backUrl.indexOf('/pages/users/login/index') !== -1) {
			backUrl = '/pages/index/index';
		}
		uni.reLaunch({ url: backUrl });
	})
}
</script>
<style lang="scss" scoped>
	page {
		background: #fff;
	}
	.appLogin {
		margin-top: 60rpx;
	
		.hds {
			display: flex;
			justify-content: center;
			align-items: center;
			font-size: 24rpx;
			color: #B4B4B4;
	
			.line {
				width: 68rpx;
				height: 1rpx;
				background: #CCCCCC;
			}
	
			p {
				margin: 0 20rpx;
			}
		}
	
		.btn-wrapper {
			display: flex;
			align-items: center;
			justify-content: center;
			margin-top: 30rpx;
	
			.btn {
				display: flex;
				align-items: center;
				justify-content: center;
				width: 68rpx;
				height: 68rpx;
				border-radius: 50%;
			}
	
			.apple-btn {
				display: flex;
				align-items: center;
				justify-content: center;
				margin-left: 30rpx;
				background: #000;
				border-radius: 34rpx;
				font-size: 40rpx;
	
				.icon-s-pingguo {
					color: #fff;
					font-size: 40rpx;
				}
			}
	
			.iconfont {
				font-size: 40rpx;
				color: #fff;
			}
	
			.wx {
				margin-right: 30rpx;
				background-color: #61C64F;
			}
	
			.mima {
				background-color: #28B3E9;
			}
	
			.yanzheng {
				background-color: #F89C23;
			}
	
		}
	}
	
	.main_color{
		@include main_color(theme);
	}
	.bg_color{
		@include main_bg_color(theme);
	}
	.code-img {
		width: 100%;
		height: 100%;
	}
	
	.acea-row.row-middle {
		input {
			margin-left: 20rpx;
			display: block;
		}
	}
	
	.login-wrapper {
		padding: 30rpx;
	
		.shading {
			display: flex;
			align-items: center;
			justify-content: center;
			width: 100%;
	
			/* #ifdef APP-VUE */
			margin-top: 50rpx;
			/* #endif */
			/* #ifndef APP-VUE */
	
			margin-top: 200rpx;
			/* #endif */
	
	
			image {
				width: 180rpx;
				height: 180rpx;
			}
		}
	
		.whiteBg {
			margin-top: 100rpx;
	
			.list {
				border-radius: 16rpx;
				overflow: hidden;
	
				.item {
					border-bottom: 1px solid #F0F0F0;
					background: #fff;
	
					.row-middle {
						position: relative;
						padding: 16rpx 45rpx;
						
						.texts{
							flex: 1;
							font-size: 28rpx;
							height: 80rpx;
							line-height: 80rpx;
							display: flex;
							justify-content: center;
							align-items: center;
						}
	
						input {
							flex: 1;
							font-size: 28rpx;
							height: 80rpx;
							line-height: 80rpx;
							display: flex;
							justify-content: center;
							align-items: center;
						}
	
						.code {
							position: absolute;
							right: 30rpx;
							top: 50%;
							color: $theme-color;
							font-size: 26rpx;
							transform: translateY(-50%);
						}
					}
				}
			}
	
			.logon {
				display: flex;
				align-items: center;
				justify-content: center;
				width: 100%;
				height: 86rpx;
				margin-top: 80rpx;
				background-color: $theme-color;
				border-radius: 120rpx;
				color: #FFFFFF;
				font-size: 30rpx;
			}

			.tips {
				margin: 30rpx;
				text-align: center;
				color: #999;
			}
		}
	}
	.protocol {
		margin: 30rpx 0;
		padding-left: 44rpx;
		::v-deep .uni-checkbox-input.uni-checkbox-input-checked {
			@include main_bg_color(theme);
			border: none !important;
			color: #fff !important;
		}
		::v-deep checkbox .wx-checkbox-input.wx-checkbox-input-checked {
			@include main_bg_color(theme);
			border: none !important;
			color: #fff !important;
		}
		.protocol_text {
			.font_pro {
				@include main_color(theme);
			}
		}
	}
</style>
