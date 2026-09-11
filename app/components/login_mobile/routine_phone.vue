<template>
	<view v-if="isPhoneBox">
		<view class="mobile-bg" @click="close"></view>
		<view class="mobile-mask animated" :class="{slideInUp:isUp}">
			<view class="info-box">
				<image :src="logoUrl"></image>
				<view class="title">获取授权</view>
				<view class="txt">获取手机号授权</view>
			</view>
			<button class="sub_btn" open-type="getPhoneNumber" @getphonenumber="getphonenumber">获取手机号</button>
		</view>
	</view>
</template>
<script setup>
	import { ref } from 'vue';
	import util from '@/utils/util.js';
	import { useAppStore } from "@/store/app.js";
	import Routine from '@/libs/routine.js';
	import {
		loginMobile,
		registerVerify,
		getCodeApi,
		getUserInfo
	} from "@/api/user.js";
	import { getLogo, getUserPhone } from '@/api/public.js';

	const appStore = useAppStore();

	const props = defineProps({
		isPhoneBox: { type: Boolean, default: false },
		logoUrl: { type: String, default: '' },
		authKey: { type: String, default: '' }
	});

	const emit = defineEmits(['close']);

	const keyCode = ref('');
	const account = ref('');
	const codeNum = ref('');
	const isStatus = ref(false);
	const userInfo = ref({});

	// #ifdef MP
	// 小程序获取手机号码
	function getphonenumber(e) {
		uni.showLoading({ title: '加载中' });
		Routine.getCode()
			.then(code => {
				getUserPhoneNumber(e.detail.encryptedData, e.detail.iv, code);
			})
			.catch(error => {
				uni.hideLoading();
			});
	}
	// 小程序获取手机号码回调
	function getUserPhoneNumber(encryptedData, iv, code) {
		getUserPhone({
			encryptedData: encryptedData,
			iv: iv,
			code: code,
			key: props.authKey,
			type: 'routine'
		})
			.then(res => {
				appStore.LOGIN({ token: res.data.token });
				appStore.SETUID(res.data.uid);
				getUserInfoFn();
			})
			.catch(res => {
				uni.hideLoading();
				util.Tips({ title: res });
			});
	}
	/**
	 * 获取个人用户信息
	 */
	function getUserInfoFn() {
		getUserInfo().then(res => {
			uni.hideLoading();
			userInfo.value = res.data
			appStore.UPDATE_USERINFO(res.data);
			isStatus.value = true
			close()
		});
	}
	// #endif
	function close() {
		emit('close', { isStatus: isStatus.value })
	}

	defineExpose({ close });
</script>

<style lang="scss">
	.mobile-bg{
		position: fixed;
		left: 0;
		top: 0;
		width: 100%;
		height: 100%;
		background: rgba(0,0,0,0.5);
	}
	.mobile-mask {
		z-index: 20;
		position: fixed;
		left: 0;
		bottom: 0;
		width: 100%;
		padding: 67rpx 30rpx;
		background: #fff;
		.info-box{
			display:flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			image{
				width: 150rpx;
				height: 150rpx;
				border-radius: 10rpx;
			}
			.title{
				margin-top: 30rpx;
				margin-bottom: 20rpx;
				font-size: 36rpx;
			}
			.txt{
				font-size: 30rpx;
				color: #868686;
			}
		}
		.sub_btn{
			width: 690rpx;
			height: 86rpx;
			line-height: 86rpx;
			margin-top: 60rpx;
			background: $theme-color;
			border-radius: 43rpx;
			color: #fff;
			font-size: 28rpx;
			text-align: center;
		}
	}
	.animated{
		animation-duration:.4s
	}
</style>