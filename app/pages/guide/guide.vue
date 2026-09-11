<template>
	<!-- 开屏广告 -->
	<view class="content">
		<swiper class="swiper" :class="advData.adList.length==1?'on':''" :autoplay="autoplay" :duration="duration"
			@change="stopChange" v-if="advData.adList.length" :indicator-dots="advData.adList.length>1?true:false"
			indicator-active-color="#e93323" circular="true" :interval="3000">
			<swiper-item v-for="(item,index) in advData.adList" :key="index" @click="jump(item.linkUrl)">
				<view class="swiper-item">
					<view class="swiper-item-img">
						<image :src="item.imageUrl" mode="aspectFill"></image>
					</view>
				</view>
			</swiper-item>
		</swiper>
		<view class="jump-over" :style="{ top: navH + 'rpx' }" @tap="launchFlag()">跳过<text
				v-if="closeType == 1">{{time}}</text>
			<slot name="bottom"></slot>
		</view>
	</view>
</template>

<script setup>
	import { ref, onMounted, onBeforeUnmount, getCurrentInstance } from 'vue'
	let app = getApp();
	let sysHeight = uni.getSystemInfoSync().statusBarHeight;
	//let menuHeight = uni.getMenuButtonBoundingClientRect().height; // 获取胶囊高度并设置标题高度

	const { proxy } = getCurrentInstance();

	const props = defineProps({
		advData: {
			type: Object,
			default: () => {}
		},
		// 1 倒计时 2 手动关闭(预留)
		closeType: {
			type: Number,
			default: 1
		}
	})

	//iSindicator: false,
	const autoplay = ref(true)
	const duration = ref(300)
	const jumpover = ref('跳过')
	const experience = ref('立即体验')
	const time = ref(props.advData.splashAdShowTime)
	const timecount = ref(undefined)
	// #ifndef H5 
	const navH = ref(20 + sysHeight * 2)
	// #endif
	// #ifdef H5
	const navH = ref(60 + sysHeight * 2)
	// #endif

	onMounted(() => {
		timer()
	})

	onBeforeUnmount(() => {
		clearInterval(timecount.value)
	})

	function stopChange() {
		if (props.advData.adList.length == 1) {
			return false
		}
	}

	function timer() {
		var t = props.advData.splashAdShowTime || 5
		timecount.value = setInterval(() => {
			t--
			time.value = t
			if (t <= 0) {
				clearInterval(timecount.value)
				launchFlag()
			}
		}, 1000)
	}

	function launchFlag() {
		clearInterval(timecount.value)
		uni.switchTab({
			url: '/pages/index/index'
		});
	}

	function jump(url) {
		if (url) {
			clearInterval(timecount.value)
			uni.redirectTo({
				url: url,
				// 如果要跳转到tabBar页面则通过switchTab跳转
				fail: function(err) {
					const errMsg = 'redirectTo:fail can not redirectTo a tabbar page'
					if (errMsg == err.errMsg) {
						uni.switchTab({
							url: url
						});
					} else {
						proxy.$util.Tips({
							title: err.errMsg
						})
					}
				}
			})
		}
	}
</script>
<style lang="scss" scoped>
	page,
	.content {
		width: 100%;
		height: 100%;
		background-size: 100% auto;
		padding: 0;
	}

	.swiper {
		width: 100%;
		height: 100vh;
		background: #FFFFFF;
	}

	.swiper-item {
		width: 100%;
		height: 100%;
		text-align: center;
		position: relative;
		display: flex;
		/* justify-content: center; */
		align-items: flex-end;
		flex-direction: column-reverse
	}

	.swiper-item-img {
		width: 100vw;
		height: 100vh;
		margin: 0 auto;
	}

	.swiper-item-img image {
		width: 100%;
		height: 100%;
	}

	.jump-over {
		position: absolute;
		height: 45rpx;
		line-height: 40rpx;
		padding: 0 15rpx;
		border-radius: 30rpx;
		font-size: 24rpx;
		color: #b09e9a;
		border: 1px solid #b09e9a;
		z-index: 999;
		left: 30rpx;
	}
</style>