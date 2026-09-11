<template>
	<view class="lottie-bg">
		<view id="lottie">
			<image :src="urlDomain+'/crmebimage/perset/staticImg/live-logo.gif'" rel="preload" />
		</view>
	</view>
</template>

<script setup>
	import { ref, getCurrentInstance } from 'vue'
	import { onLoad } from '@dcloudio/uni-app'
	// #ifdef H5
	import wechat from "@/libs/wechat.js";
	// #endif
	import {
		getUserInfo
	} from "@/api/user.js";

	const { proxy } = getCurrentInstance();

	const urlDomain = ref(proxy.$Cache.get("imgHost"))

	onLoad((option) => {
		// #ifdef H5
		const {
			code,
			state
		} = option;
		wechat.auth(code, state)
			.then(() => {
				getUserInfo().then(res => {
					location.href = decodeURIComponent(
						decodeURIComponent(option.back_url)
					);
				}).catch(res => {
				});
			})
			.catch((err) => {
			});
		// #endif
		// #ifndef H5
		uni.switchTab({
			url: '/pages/index/index'
		});
		// #endif
	})
</script>

<style scoped lang="scss">
	.lottie-bg {
		position: fixed;
		left: 0;
		top: 0;
		background-color: #fff;
		width: 100%;
		height: 100%;
		z-index: 999;

		display: flex;
		align-items: center;
		justify-content: center;
	}

	#lottie {
		display: block;
		width: 100%;
		height: 100%;
		display: flex;
		align-items: center;
		justify-content: center;
		overflow: hidden;
		transform: translate3d(0, 0, 0);
		margin: auto;

		image {
			width: 200rpx;
			height: 200rpx;
		}
	}
</style>
