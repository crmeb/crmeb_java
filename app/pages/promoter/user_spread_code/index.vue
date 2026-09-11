<template>
	<view class="page" :style="colorStyle">
		<view class='distribution-posters'>
			<swiper :indicator-dots="indicatorDots" :autoplay="autoplay" :circular="circular" :interval="interval"
				:duration="duration" @change="bindchange" previous-margin="40px" next-margin="40px">
				<block v-for="(item,index) in spreadList" :key="index">
					<swiper-item>
						<image :src="item.pic" class="slide-image" :class="swiperIndex == index ? 'active' : 'quiet'"
							mode='aspectFill' />
					</swiper-item>
				</block>
			</swiper>
			<!-- #ifdef APP-PLUS || MP-->
			<view class='keep' :style="{backgroundColor:bgColor}" @click='savePhoto(spreadList[swiperIndex].pic)'>保存海报</view>
			<!-- #endif -->
			<!-- #ifndef MP || APP-PLUS -->
			<div class="preserve acea-row row-center-wrapper">
				<div class="line"></div>
				<div class="tip">长按保存图片</div>
				<div class="line"></div>
			</div>
			<!-- #endif -->
		</view>
		<view class="canvas" v-if="canvasStatus">
			<canvas style="width:750px;height:1190px;" canvas-id="canvasOne"></canvas>
			<canvas canvas-id="qrcode" :style="{width: `${qrcodeSize}px`, height: `${qrcodeSize}px`}" />
		</view>
	</view>
</template>

<script setup>
	// #ifdef H5 || APP-PLUS
	import uQRCode from '@/js_sdk/Sansnn-uQRCode/uqrcode.js'
	// #endif
	// #ifdef APP-PLUS
	import {
		HTTP_H5_URL
	} from '@/config/app.js';
	// #endif
	import {
		getUserInfo,
		spreadBanner
	} from '@/api/user.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from 'pinia';
	// #ifdef MP
	import {
		base64src
	} from '@/utils/base64src.js'
	import {
		getQrcode as getQrcodeApi
	} from '@/api/api.js';
	// #endif
	import {setThemeColor} from '@/utils/setTheme.js'
	import {
		imageBase64
	} from "@/api/public.js";
	import { ref, getCurrentInstance } from 'vue';
	import { onLoad, onReady, onShareAppMessage } from '@dcloudio/uni-app';
	import util from '@/utils/util.js';
	import { useColor } from '@/composables/useColor.js';
const { colorStyle } = useColor();
	const { proxy } = getCurrentInstance();
	const { isLogin, uid, userInfo } = storeToRefs(useAppStore());

	const imgUrls = ref([]);
	const indicatorDots = ref(false);
	const circular = ref(false);
	const autoplay = ref(false);
	const interval = ref(3000);
	const duration = ref(500);
	const swiperIndex = ref(0);
	const spreadList = ref([]);
	const poster = ref('');
	const qrcodeSize = ref(1000);
	const PromotionCode = ref('');
	const base64List = ref([]);
	const canvasStatus = ref(true); //海报绘图标签
	const bgColor = ref('#e93323');

	watch(isLogin, (newV, oldV) => {
		if (newV) {
			userSpreadBannerList();
		}
	}, { deep: true });

	onLoad(() => {
		bgColor.value = setThemeColor();
		if (isLogin.value) {
			userSpreadBannerList();
		} else {
			toLogin();
		}
	});
	/**
	 * 用户点击右上角分享
	 */
	// #ifdef MP
	onShareAppMessage(() => {
		return {
			title: userInfo.value.nickname + '-分销海报',
			imageUrl: spreadList.value[0].pic,
			path: '/pages/index/index?spread=' + uid.value,
		};
	});
	// #endif
	onReady(() => {});

	function userSpreadBannerList() {
		uni.showLoading({
			title: '获取中',
			mask: true,
		})
		spreadBanner({
			page: 1,
			limit: 5
		}).then(res => {
			uni.hideLoading();
			spreadList.value = res.data;
			getImageBase64(res.data);
		}).catch(err => {
			uni.hideLoading();
		});
	}
	function getImageBase64(images) {
		uni.showLoading({
			title: '海报生成中',
			mask: true
		});
		// #ifdef H5
		let spreadListArr = []
		// 生成一个Promise对象的数组
		images.forEach(item => {
			const oneApi = imageBase64({
				url: item.pic
			}).then(res => {
				return res.data.code;
			})
			spreadListArr.push(oneApi)
		})
		Promise.all(spreadListArr).then(result => {
			base64List.value = result;
			make();
			setShareInfoStatus();
		})
		// #endif

		// #ifdef MP || APP-PLUS
		base64List.value = images.map(item => {
			return item.pic
		});
		// #endif

		// #ifdef MP
		getQrcode();
		// #endif
		// #ifdef APP-PLUS
		make();
		// #endif
	}
	// 小程序二维码
	function getQrcode() {
		// #ifdef MP
		let data = {
			pid: uid.value,
			path: 'pages/index/index'
		}
		let arrImagesUrl = "";
		uni.downloadFile({
			url: base64List.value[0],
			success: (res) => {
				arrImagesUrl = res.tempFilePath;
			}
		});
		getQrcodeApi(data).then(res => {
			base64src(res.data.code, Date.now(), res => {
				PromotionCode.value = res;
				setTimeout(() => {
					PosterCanvas(arrImagesUrl, PromotionCode.value, userInfo.value.nickname, 0);
				}, 1000);
			});
		}).catch(err => {
			uni.hideLoading();
			util.Tips({
				title: err
			});
			canvasStatus.value = false;
		});
		// #endif
	}
	// 生成二维码；
	function make() {
		let href = '';
		// #ifdef H5
		href = window.location.href.split('/pages')[0];
		// #endif
		// #ifdef APP-PLUS
		href = HTTP_H5_URL;
		let routes = getCurrentPages(); // 获取当前打开过的页面路由数组
		let curRoute = routes[routes.length - 1].route //获取当前页面路由
		let curParam = routes[routes.length - 1].options; //获取路由参数
		// #endif
		// #ifdef H5 || APP-PLUS
		uQRCode.make({
			canvasId: 'qrcode',
			text: href + '/pages/index/index?spread=' + uid.value,
			size: qrcodeSize.value,
			margin: 10,
			success: res => {
				PromotionCode.value = res;
				setTimeout(() => {
					PosterCanvas(base64List.value[0], PromotionCode.value, userInfo.value.nickname, 0);
				}, 1000);
			},
			complete: (res) => {},
			fail: res => {
				uni.hideLoading();
				util.Tips({
					title: '海报二维码生成失败！'
				});
			}
		})
		// #endif
	}
	function PosterCanvas(arrImages, code, nickname, index) {
		let context = uni.createCanvasContext('canvasOne')
		context.clearRect(0, 0, 0, 0);
		uni.getImageInfo({
			src: arrImages,
			success: function(res) {
				context.drawImage(arrImages, 0, 0, 750, 1190);
				context.save();
				context.drawImage(code, 110, 925, 140, 140);
				context.restore();
				context.setFontSize(28);
				context.fillText(nickname, 270, 980);
				context.fillText('邀请您加入', 270, 1020);
				setTimeout(() => {
					context.draw(true, function() {
						uni.canvasToTempFilePath({
							destWidth: 750,
							destHeight: 1190,
							canvasId: 'canvasOne',
							fileType: 'jpg',
							success: function(res) {
								// 在H5平台下，tempFilePath 为 base64
								uni.hideLoading();
								spreadList.value[index].pic = res.tempFilePath;
								poster.value = res.tempFilePath;
								canvasStatus.value = false;
							}
						})
					})
					
				}, 100);
			},
			fail: function(err) {
				uni.hideLoading();
				util.Tips({
					title: '无法获取图片信息'
				});
			}
		});
	}
	function bindchange(e) {
		let index = e.detail.current;
		swiperIndex.value = index;
		let arrImagesUrl = "";
		uni.downloadFile({
			url: base64List.value[index],
			success: (res) => {
				arrImagesUrl = res.tempFilePath;
				setTimeout(() => {
					canvasStatus.value = true;
					PosterCanvas(arrImagesUrl, PromotionCode.value, userInfo.value.nickname, index);
				}, 300);
			}
		});
	}
	// 点击保存海报
	function savePhoto(url) {
		uni.saveImageToPhotosAlbum({
			filePath: url,
			success: function(res) {
				util.Tips({
					title: '保存成功',
					icon: 'success'
				});
			},
			fail: function(res) {
				util.Tips({
					title: '保存失败'
				});
			}
		}); 
	}
	// #ifdef H5
	function setShareInfoStatus() {
		if (proxy.$wechat.isWeixin()) {
			let configAppMessage = {
				desc: '分销海报',
				title: userInfo.value.nickname + '-分销海报',
				link: '/pages/index/index?spread=' + uid.value,
				imgUrl: spreadList.value[0].pic
			};
			proxy.$wechat.wechatEvevt(["updateAppMessageShareData", "updateTimelineShareData"],
				configAppMessage)
		}
	}
	// #endif
</script>

<style lang="scss" scoped>
	// page {
	// 	background-color: #A2A2A2 !important;
	// 	height: 100vh;
	// 	overflow: auto;
	// }
	.page{
		height: 100vh;
		overflow: auto;
		background-color: #A2A2A2 !important;
	}
	.canvas {
		position: relative;
	}

	.distribution-posters {
		width: 100%;
		height: 100%;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
	}

	.distribution-posters swiper {
		width: 100%;
		height: 1000rpx;
		position: relative;
		margin-top: 40rpx;
	}

	.distribution-posters .slide-image {
		width: 100%;
		height: 100%;
		margin: 0 auto;
		border-radius: 15rpx;
	}

	.distribution-posters .slide-image.active {
		transform: none;
		transition: all 0.2s ease-in 0s;
	}

	.distribution-posters .slide-image.quiet {
		transform: scale(0.8333333);
		transition: all 0.2s ease-in 0s;
	}

	.distribution-posters .keep {
		font-size: 30rpx;
		color: #fff;
		width: 600rpx;
		height: 80rpx;
		border-radius: 50rpx;
		text-align: center;
		line-height: 80rpx;
		margin: 38rpx auto;
		
	}

	.distribution-posters .preserve {
		color: #fff;
		text-align: center;
		margin-top: 38rpx;
	}

	.distribution-posters .preserve .line {
		width: 100rpx;
		height: 1px;
		background-color: #fff;
	}

	.distribution-posters .preserve .tip {
		margin: 0 30rpx;
	}
</style>
