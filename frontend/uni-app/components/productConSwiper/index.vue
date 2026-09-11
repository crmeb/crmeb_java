<template>
	<view class='product-bg'>
		<swiper :indicator-dots="indicatorDots" :indicator-active-color="indicatorBg" :autoplay="autoplay" :circular="circular"
		 :interval="interval" :duration="duration" @change="change" v-if="isPlay"> 
		 <!-- #ifndef APP-PLUS -->
			<swiper-item v-if="videoline">
				<view class="item">
					<view v-show="!controls" style="width:100%;height:100% ">
						<video id="myVideo" :src='videoline' objectFit="cover" controls style="width:100%;height:100% "
						 show-center-play-btn show-mute-btn="true" auto-pause-if-navigate :custom-cache="false" :enable-progress-gesture="false" :poster="imgUrls[0]" @pause="videoPause"></video>
					</view>
					<view class="poster" v-show="controls">
						<image class="image" :src="imgUrls[0]"></image>
					</view>
					<view class="stop" v-show="controls" @tap="bindPause">
						<image class="image" :src="urlDomain+'/crmebimage/perset/staticImg/stop.png'"></image>
					</view>
				</view>
			</swiper-item>
			<!-- #endif -->
			<!-- #ifdef APP-PLUS -->
			<swiper-item v-if="videoline">
				<view class="item">
					<view class="poster" v-show="controls">
						<image class="image" :src="imgUrls[0]"></image>
					</view>
					<view class="stop" v-show="controls" @tap="bindPause">
						<image class="image" :src="urlDomain+'/crmebimage/perset/staticImg/stop.png'"></image>
					</view>
				</view>
			</swiper-item>
			<!-- #endif -->
			<block v-for="(item,index) in imgUrls" :key='index'>
				<swiper-item v-if="videoline?index>=1:index>=0">
					<image :src="item" class="slide-image" />
				</swiper-item>
			</block>
		</swiper>
		<!-- #ifdef APP-PLUS -->
		<view v-if="!isPlay" style="width: 100%; height: 750rpx;">
			<video id="myVideo" :src='videoline' objectFit="cover" controls style="width:100%;height:100% "
			 show-center-play-btn show-mute-btn="true" autoplay="true" auto-pause-if-navigate :custom-cache="false" :enable-progress-gesture="false" :poster="imgUrls[0]" @pause="videoPause"></video>
		</view>
		<!-- #endif -->
	</view>
</template>

<script setup>
	import { ref, onMounted, getCurrentInstance } from 'vue'
	import {setThemeColor} from '@/utils/setTheme.js'
	import Cache from '@/utils/cache.js'

	const { proxy } = getCurrentInstance();

	const props = defineProps({
		imgUrls: {
			type: Array,
			default: function() {
				return [];
			}
		},
		videoline: {
			type: String,
			value: ""
		}
	})

	// data
	const urlDomain = ref(Cache.get("imgHost"))
	const indicatorDots = ref(true)
	const circular = ref(true)
	const autoplay = ref(true)
	const interval = ref(3000)
	const duration = ref(500)
	const currents = ref("1")
	const controls = ref(true)
	const isPlay = ref(true)
	const videoContext = ref('')
	const indicatorBg = ref('#e93323')

	// created
	indicatorBg.value = setThemeColor();

	onMounted(() => {
		if(props.videoline){
			props.imgUrls.shift()
		}
		// #ifndef APP-PLUS
		videoContext.value = uni.createVideoContext('myVideo', proxy);
		// #endif
	})

	function videoPause(e){
		// #ifdef APP-PLUS
		isPlay.value = true
		autoplay.value = true
		// #endif
	}
	function bindPause() {

		// #ifndef APP-PLUS
		videoContext.value.play();
		controls.value = false
		autoplay.value = false
		// #endif
		// #ifdef APP-PLUS
		isPlay.value = false
		videoContext.value = uni.createVideoContext('myVideo', proxy);
		videoContext.value.play();
		// #endif
	}
	function change(e) {
		currents.value = e.detail.current + 1;
	}
</script>

<style scoped lang="scss">
	.product-bg {
		width: 100%;
		height: 750rpx;
		position: relative;
	}

	.product-bg swiper {
		width: 100%;
		height: 100%;
		position: relative;
	}

	.product-bg .slide-image {
		width: 100%;
		height: 100%;
	}

	.product-bg .pages {
		position: absolute;
		background-color: #fff;
		height: 34rpx;
		padding: 0 10rpx;
		border-radius: 3rpx;
		right: 30rpx;
		bottom: 30rpx;
		line-height: 34rpx;
		font-size: 24rpx;
		color: #050505;
	}

	#myVideo {
		width: 100%;
		height: 100%
	}

	.product-bg .item {
		position: relative;
		width: 100%;
		height: 100%;
	}

	.product-bg .item .poster {
		position: absolute;
		top: 0;
		left: 0;
		height: 750rpx;
		width: 100%;
		z-index: 9;
	}

	.product-bg .item .poster .image {
		width: 100%;
		height: 100%;
	}

	.product-bg .item .stop {
		position: absolute;
		top: 50%;
		left: 50%;
		width: 136rpx;
		height: 136rpx;
		margin-top: -68rpx;
		margin-left: -68rpx;
		z-index: 9;
	}

	.product-bg .item .stop .image {
		width: 100%;
		height: 100%;
	}
</style>