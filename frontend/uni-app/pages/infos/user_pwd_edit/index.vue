<template>
	<view :data-theme="theme" class="upda_pasd" :style="colorStyle">
		<view class="ChangePassword">
			<form @submit="editPwd" report-submit='true'>
				<view class="phone">当前手机号：{{phone}}</view>
				<view class="list">
					<view class="item">
						<input type='password' placeholder='6-8位字母加数字' placeholder-class='placeholder' name="password" v-model="password" maxlength="18"></input>
					</view>
					<view class="item">
						<input type='password' placeholder='确认新密码' placeholder-class='placeholder' name="qr_password" v-model="qr_password" maxlength="18"></input>
					</view>
					<view class="item acea-row row-between-wrapper">
						<input type='number' placeholder='填写验证码' placeholder-class='placeholder' class="codeIput" name="captcha" :value="captcha" maxlength="6"></input>
						<button class="code" :class="disabled === true ? 'on' : ''" :disabled='disabled' @click="clickCode">
							{{ text }}
						</button>
					</view>
				</view>
				<button form-type="submit" class="confirmBnt">确认修改</button>
			</form>
		</view>
		<Verify @success="handlerOnVerSuccess" :captchaType="'blockPuzzle'" :imgSize="{ width: '330px', height: '155px' }"
		        ref="verify"></Verify>
	</view>
</template>

<script setup>
import { ref, watch } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import util from '@/utils/util.js';
import { useSendVerifyCode } from '@/composables/useSendVerifyCode.js';
import Verify from '@/pages/users/components/verifition/verify.vue';
import {
	phoneRegisterReset,
	registerVerify
} from '@/api/api.js';
import {
	getUserInfo
} from '@/api/user.js';
import {
	toLogin
} from '@/libs/login.js';
import { useAppStore } from "@/store/app.js";
import { storeToRefs } from 'pinia';
import { setThemeColor } from '@/utils/setTheme.js'
import { useColor } from '@/composables/useColor.js';

const app = getApp();
const appStore = useAppStore();
const { isLogin } = storeToRefs(appStore);
const { disabled, text, sendCode } = useSendVerifyCode();

const verify = ref(null);
const userInfo = ref({});
const phone = ref('');
const password = ref('');
const captcha = ref('');
const qr_password = ref('');
const isAuto = ref(false); //没有授权的不会自动授权
const isShowAuth = ref(false); //是否隐藏授权
const theme = ref(app.globalData.theme);
const { colorStyle } = useColor();
const bgColor = ref('');

watch(isLogin, (newV) => {
	if (newV) {
		getUserInfoFn();
	}
}, { deep: true });

onLoad(() => {
	if (isLogin.value) {
		getUserInfoFn();
	} else {
		toLogin();
	}
	bgColor.value = setThemeColor();
	uni.setNavigationBarColor({
		frontColor: '#ffffff',
		backgroundColor: bgColor.value,
	});
});

function clickCode() {
	if (qr_password.value != password.value) return util.Tips({
		title: '两次输入的密码不一致！'
	});
	if (!/^[a-zA-Z]\w{5,17}$/i.test(password.value)) return util.Tips({
		title: '密码格式错误，密码必须以字母开头，长度在6～18之间，只能包含字符、数字和下划线'
	});
	verify.value.show();
}

//滑块验证成功后
function handlerOnVerSuccess(data) {
	verify.value.hide();
	code();
}

/**
 * 授权回调
 */
function onLoadFun(e) {
	getUserInfoFn();
}

// 授权关闭
function authColse(e) {
	isShowAuth.value = e
}

/**
 * 获取个人用户信息
 */
function getUserInfoFn() {
	getUserInfo().then(res => {
		let tel = res.data.phone;
		let p = tel.substr(0, 3) + "****" + tel.substr(7);
		userInfo.value = res.data;
		phone.value = p;
	});
}

/**
 * 发送验证码
 */
async function code() {
	if (!userInfo.value.phone) return util.Tips({
		title: '手机号码不存在,无法发送验证码！'
	});
	await registerVerify(userInfo.value.phone).then(res => {
		util.Tips({
			title: res.message
		});
		sendCode();
	}).catch(err => {
		return util.Tips({
			title: err
		});
	});
}

/**
 * H5登录 修改密码
 */
function checkPasd(e) {
	let pwd = e.detail.value;
	password.value = pwd;
	if (!/^[a-zA-Z]\w{5,17}$/i.test(pwd)) return util.Tips({
		title: '密码格式错误，密码必须以字母开头，长度在6～18之间，只能包含字符、数字和下划线'
	});
}

//校验2次密码是否一样
function checkPassword(e) {
	let qr = e.detail.value;
	if (qr != password.value) return util.Tips({
		title: '两次输入的密码不一致！'
	});
}

function editPwd(e) {
	let pwd = e.detail.value.password,
		qr = e.detail.value.qr_password,
		cap = e.detail.value.captcha;
	if (!pwd) return util.Tips({
		title: '请输入新密码'
	});
	if (!qr) return util.Tips({
		title: '请确认新密码'
	});
	if (!cap) return util.Tips({
		title: '请输入验证码'
	});
	if (qr != password.value) return util.Tips({
		title: '两次输入的密码不一致！'
	});
	phoneRegisterReset({
		account: userInfo.value.phone,
		captcha: cap,
		password: pwd
	}).then(res => {
		return util.Tips({
			title: res.message
		}, {
			tab: 3,
			url: 1
		});
	}).catch(err => {
		return util.Tips({
			title: err
		});
	});
}
</script>

<style lang="scss">
	page {
		background-color: #fff !important;
	}
	.upda_pasd{
		background: #FFFFFF;
		height: 100vh;
	}
	.ChangePassword .phone {
		font-size: 32rpx;
		font-weight: bold;
		text-align: center;
		padding-top: 100rpx;
	}

	.ChangePassword .list {
		width: 580rpx;
		margin: 53rpx auto 0 auto;
	}

	.ChangePassword .list .item {
		width: 100%;
		height: 110rpx;
		border-bottom: 2rpx solid #f0f0f0;
	}

	.ChangePassword .list .item input {
		width: 100%;
		height: 100%;
		font-size: 32rpx;
	}

	.ChangePassword .list .item .placeholder {
		color: #b9b9bc;
	}

	.ChangePassword .list .item input.codeIput {
		width: 340rpx;
	}

	.ChangePassword .list .item .code {
		font-size: 32rpx;
		// background-color: #fff;
		@include main_color(theme);
	}

	.ChangePassword .list .item .code.on {
		color: #b9b9bc !important;
	}

	.ChangePassword .confirmBnt {
		font-size: 32rpx;
		width: 580rpx;
		height: 90rpx;
		border-radius: 45rpx;
		color: #fff;
		margin: 92rpx auto 0 auto;
		text-align: center;
		line-height: 90rpx;
		@include main_bg_color(theme);
	}
</style>
