<template>
	<view :data-theme="theme" :style="colorStyle">
		<view class="ChangePassword">
			<view class="list">
				<view class="item" v-if="isNew">
					<input  disabled='true' placeholder='填写手机号码' placeholder-class='placeholder' v-model="userInfo.phone"></input>
				</view>
				<view class="item" v-else>
					<input type='number' placeholder='填写手机号码' placeholder-class='placeholder' v-model="phone" maxlength="11"></input>
				</view>
				<view class="item acea-row row-between-wrapper">
					<input type='number' placeholder='填写验证码' placeholder-class='placeholder' class="codeIput" v-model="captcha" maxlength="6"></input>
					<button class="code" :class="disabled === true ? 'on' : ''" :disabled='disabled' @click="code">
						{{ text }}
					</button>
				</view>
			</view>
			<button form-type="submit" v-if="isNew" class="confirmBnt bg_color" @click="next">下一步</button>
			<button form-type="submit" v-else class="confirmBnt bg_color"  @click="editPwd">保存</button>
		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import util from '@/utils/util.js';
import { useSendVerifyCode } from '@/composables/useSendVerifyCode.js';
import {
	registerVerify,
	bindingPhone,
	verifyCode,
	bindingVerify
} from '@/api/api.js';
import { toLogin } from '@/libs/login.js';
import { useAppStore } from "@/store/app.js";
import { storeToRefs } from 'pinia';
import { setThemeColor } from '@/utils/setTheme.js'
import { useColor } from '@/composables/useColor.js';

const app = getApp();
const appStore = useAppStore();
const { isLogin, userInfo } = storeToRefs(appStore);
// 本组件仅复用 disabled，倒计时文案由本地 getTimes 管理
const { disabled } = useSendVerifyCode();

const phone = ref('');
const captcha = ref('');
const isAuto = ref(false); //没有授权的不会自动授权
const isShowAuth = ref(false); //是否隐藏授权
const key = ref('');
const isNew = ref(true);
const timer = ref('');
const text = ref('获取验证码');
const nums = ref(60);
const theme = ref(app.globalData.theme);
const { colorStyle } = useColor();
const bgColor = ref('');

onLoad(() => {
	bgColor.value = setThemeColor();
	uni.setNavigationBarColor({
		frontColor: '#ffffff',
		backgroundColor: bgColor.value,
	});
	if (isLogin.value) {
		// verifyCode().then(res=>{ key.value = res.data.key });
	} else {
		toLogin();
	}
});

function getTimes() {
	nums.value = nums.value - 1;
	text.value = "剩余 " + nums.value + "s";
	if (nums.value < 0) {
		clearInterval(timer.value);
	}
	text.value = "剩余 " + nums.value + "s";
	if (text.value < "剩余 " + 0 + "s") {
		disabled.value = false;
		text.value = "重新获取";
	}
}

function onLoadFun() {}

// 授权关闭
function authColse(e) {
	isShowAuth.value = e
}

function next() {
	if (!captcha.value) return util.Tips({
		title: '请填写验证码'
	});
	bindingVerify({
		phone: userInfo.value.phone,
		captcha: captcha.value
	}).then(res => {
		isNew.value = false;
		captcha.value = '';
		clearInterval(timer.value);
		disabled.value = false;
		text.value = "获取验证码";
	}).catch(err => {
		return util.Tips({
			title: err
		});
	})
}

function editPwd() {
	if (!phone.value) return util.Tips({
		title: '请填写手机号码！'
	});
	if (!(/^1(3|4|5|7|8|9|6)\d{9}$/i.test(phone.value))) return util.Tips({
		title: '请输入正确的手机号码！'
	});
	if (!captcha.value) return util.Tips({
		title: '请填写验证码'
	});
	uni.showModal({
		title: '是否更换绑定账号',
		confirmText: '绑定',
		success(res) {
			if (res.confirm) {
				bindingPhone({
					phone: phone.value,
					captcha: captcha.value
				}).then(res => {
					return util.Tips({
						title: res.message,
						icon: 'success'
					}, {
						tab: 5,
						url: '/pages/infos/user_info/index'
					});
				}).catch(err => {
					return util.Tips({
						title: err
					});
				})
			} else if (res.cancel) {
				return util.Tips({
					title: '您已取消更换绑定！'
				}, {
					tab: 5,
					url: '/pages/infos/user_info/index'
				});
			}
		}
	});
}

/**
 * 发送验证码
 */
async function code() {
	nums.value = 60;
	uni.showLoading({
		title: '加载中',
		mask: true
	});
	if (!isNew.value) {
		if (!phone.value) return util.Tips({
			title: '请填写手机号码！'
		});
		if (!(/^1(3|4|5|7|8|9|6)\d{9}$/i.test(phone.value))) return util.Tips({
			title: '请输入正确的手机号码！'
		});
	}
	await registerVerify(isNew.value ? userInfo.value.phone : phone.value).then(res => {
		util.Tips({
			title: res.message
		});
		timer.value = setInterval(getTimes, 1000);
		disabled.value = true;
		uni.hideLoading();
	}).catch(err => {
		return util.Tips({
			title: err
		});
	});
}
</script>

<style lang="scss" scoped>
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
	page {
		background-color: #fff !important;
	}
	.ChangePassword{padding-top:100rpx;}
	.ChangePassword .phone {
		font-size: 32rpx;
		font-weight: bold;
		text-align: center;
		margin-top: 55rpx;
	}

	.ChangePassword .list {
		width: 580rpx;
		margin: 0 auto;
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
		@include main_color(theme);
		background-color: #f5f5f5;
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
	}
	.bg_color{
		@include main_bg_color(theme);
	}
</style>
