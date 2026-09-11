<template>
	<view v-if="isUp" :data-theme="theme" :style="colorStyle">
		<view class="mobile-bg" v-if="isShow" @click="close"></view>
		<view class="mobile-mask" :class="[{slideInUp:isUp},{animated:isPos}]"
			:style="{position:isPos?'fixed':'static'}">
			<view class="input-item">
				<view class="item">
					<input class="ipt" type="number" v-model="account" placeholder-class='placeholder'
						placeholder="输入手机号" maxlength="11" />
				</view>

			</view>
			<view class="input-item">
				<view class="item acea-row row-between-wrapper">
					<input class="ipt codeIput" type="number" v-model="codeNum" placeholder-class='placeholder'
						placeholder="输入验证码" maxlength="6" />
					<view class="line">

					</view>
					<button class="code font-num" :disabled="disabled" @click="code">{{text}}</button>
				</view>
			</view>
				<view class="sub_btn" @click="loginBtn">
					{{ isLogin ? '立即绑定' : '立即登录' }}
				</view>
		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue';
import Cache from '@/utils/cache.js';
import util from '@/utils/util.js';
import { useAppStore } from "@/store/app.js";
import { storeToRefs } from 'pinia';
import { useSendVerifyCode } from '@/composables/useSendVerifyCode.js';
import Routine from '@/libs/routine.js';
import {
	loginMobile,
	registerVerify,
	getCodeApi,
	getUserInfo,
	phoneWxSilenceAuth
} from "@/api/user.js";
import {
	bindingPhone
} from '@/api/api.js'
import {
	getUserPhone,
	iosBinding
} from '@/api/public.js';
import { useColor } from '@/composables/useColor.js';

const app = getApp();
const BACK_URL = "login_back_url";
const appStore = useAppStore();
const { userInfo, isLogin } = storeToRefs(appStore);
const { disabled, text, sendCode } = useSendVerifyCode();

const props = defineProps({
	isUp: { type: Boolean, default: false },
	authKey: { type: String, default: '' },
	isShow: { type: Boolean, default: true },
	isPos: { type: Boolean, default: true },
	appleShow: { type: String, default: '' },
	platform: { type: String, default: '' },
	wxCode: { type: String, default: '' },
	loginConfig: { type: String, default: '' },
});

const emit = defineEmits(['close', 'wechatPhone']);

const theme = ref(app.globalData.theme);
const { colorStyle } = useColor();
const keyCode = ref('');
const account = ref('');
const codeNum = ref('');
const isApp = ref(0);

// 获取验证码
async function code() {
	if (!account.value) return util.Tips({ title: '请填写手机号码' });
	if (!/^1(3|4|5|7|8|9|6)\d{9}$/i.test(account.value)) return util.Tips({ title: '请输入正确的手机号码' });
	await registerVerify(account.value).then(res => {
		util.Tips({ title: res.msg });
		sendCode();
	}).catch(err => {
		return util.Tips({ title: err })
	})
}

// 获取验证码api
function getCode() {
	getCodeApi().then(res => {
		keyCode.value = res.data.key;
	}).catch(res => {
		util.Tips({ title: res });
	});
}

function close() {
	emit('close', false)
}

// 登录
function loginBtn() {
	if (!account.value) return util.Tips({ title: '请填写手机号码' });
	if (!/^1(3|4|5|7|8|9|6)\d{9}$/i.test(account.value)) return util.Tips({ title: '请输入正确的手机号码' });
	if (!codeNum.value) return util.Tips({ title: '请填写验证码' });
	if (!/^[\w\d]+$/i.test(codeNum.value)) return util.Tips({ title: '请输入正确的验证码' });
	const isBinding = isLogin.value && !(userInfo.value && userInfo.value.phone);
	uni.showLoading({
		title: isBinding ? '正在绑定中' : '正在登录中'
	});
	if (isBinding) {
		iosBinding({
			captcha: codeNum.value,
			phone: account.value
		}).then(res => {
			isApp.value = 0;
			onSuccess();
			getUserInfoFn();
		}).catch(error => {
			uni.hideLoading()
			util.Tips({ title: error })
		})
	} else {
		getUserPhone({
			captcha: codeNum.value,
			phone: account.value,
			// #ifdef H5
			type: 'public',
			// #endif
			// #ifdef MP
			type: 'routine',
			code: props.wxCode,
			// #endif
			// #ifdef APP-PLUS
			type: props.platform === 'ios' ? 'iosWx' : 'androidWx',
			// #endif
			key: props.authKey
		}).then(res => {
			appStore.LOGIN({ token: res.data.token });
			appStore.SETUID(res.data.uid);
			onSuccess();
			getUserInfoFn();
		}).catch(error => {
			uni.hideLoading()
			util.Tips({ title: error })
		})
	}
}

/**
 * 登录成功后的方法
 */
function onSuccess() {
	uni.hideLoading();
	let backUrl = Cache.get(BACK_URL) || "/pages/index/index";
	// #ifdef MP
	util.Tips({ title: '绑定手机号成功' }, { tab: 4, url: backUrl });
	close();
	// #endif
	// #ifdef H5
	emit('wechatPhone', true)
	// #endif
	// #ifdef APP-PLUS
	if (isApp.value == 0) {
		if (backUrl.indexOf('/pages/users/login/index') !== -1) {
			backUrl = '/pages/index/index';
		}
		uni.reLaunch({ url: backUrl });
	}
	// #endif
}

/**
 * 获取个人用户信息
 */
function getUserInfoFn() {
	getUserInfo().then(res => {
		uni.hideLoading();
		appStore.UPDATE_USERINFO(res.data);
	});
}

defineExpose({ code, getCode, close, loginBtn, onSuccess, getUserInfo: getUserInfoFn });
</script>

<style lang="scss" scoped>
	.mobile-bg {
		position: fixed;
		left: 0;
		top: 0;
		width: 100%;
		height: 100%;
		background: rgba(0, 0, 0, 0.5);
	}

	.isPos {
		position: static;
	}

	.mobile-mask {
		z-index: 20;
		// position: fixed;
		left: 0;
		bottom: 0;
		width: 100%;
		padding: 67rpx 72rpx;
		background: #fff;

		.input-item {
			display: flex;
			justify-content: space-between;
			width: 606rpx;
			height: 88rpx;
			margin-bottom: 38rpx;

			.codeIput {
				width: 300rpx !important;
			}

			.ipt {
				width: 100%;
				height: 100%;
				font-size: 32rpx;
			}

			.item {
				width: 100%;
				background: #F5F5F5;
				flex: 1;
				height: 100%;
				padding: 0 32rpx 0 48rpx;
				border-radius: 45rpx;
				font-size: 28rpx;
				font-family: PingFang SC-Regular, PingFang SC;
				font-weight: 400;
				color: #333333;
				line-height: 40px;

				.placeholder {
					color: #BBBBBB;
					font-size: 28rpx;
				}
			}

			.line {
				width: 2rpx;
				height: 28rpx;
				background: #CCCCCC;
			}

			.code {
				background: none;
				font-size: 28rpx;
				@include main_color(theme);
				border-radius: 43rpx;

				&[disabled] {
					color: #999;
				}
			}
		}

		.sub_btn {
			width: 100%;
			height: 86rpx;
			line-height: 86rpx;
			margin-top: 60rpx;
			@include main_bg_color(theme);
			border-radius: 43rpx;
			color: #fff;
			font-size: 28rpx;
			text-align: center;
		}
	}

	.animated {
		animation-duration: .4s
	}
</style>
