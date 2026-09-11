<template>
	<view>
		<view class='poster-poster'>
			<view class='tip'><text class='iconfont icon-shuoming'></text>提示：点击图片即可保存至手机相册 </view>
			<view class='pictrue' v-if="canvasStatus">
				<image :src='imagePath'></image>
			</view>
			<view class="canvas">
				<canvas style="width:750px;height:1130px;" canvas-id="firstCanvas" id="firstCanvas"></canvas>
				<canvas canvas-id="qrcode" :style="{width: `${qrcodeSize}px`, height: `${qrcodeSize}px`}" style="opacity: 0;"/>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { ref, getCurrentInstance } from 'vue';
	import { onLoad, onShow } from '@dcloudio/uni-app';
	import { getCombinationPink as getCombinationPinkApi, getCombinationPoster } from '../../../api/activity.js';
	import uQRCode from '@/js_sdk/Sansnn-uQRCode/uqrcode.js';
	import { imageBase64 } from "@/api/public.js";
	const { proxy } = getCurrentInstance();

	const parameter = ref({
		'navbar': '1',
		'return': '1',
		'title': '拼团海报',
		'color': true,
		'class': '0'
	});
	const type = ref(0);
	const id = ref(0);
	const image = ref('');
	const from = ref('');
	const storeCombination = ref({});
	const qrcodeSize = ref(600);
	const posterbackgd = ref(`${proxy.$Cache.get("imgHost")}/crmebimage/perset/staticImg/canbj.png`);
	const PromotionCode = ref(''); //二维码
	const canvasStatus = ref(false);
	const imgTop = ref(''); //商品图base64位
	const imagePath = ref('');

	function getPosterInfo() {
		let data = {
			pinkId: parseFloat(id.value),
			from: from.value
		};
		if (type.value == 1) {

		} else {
			getCombinationPink();
		}
	}
	//拼团信息
	function getCombinationPink() {
		getCombinationPinkApi(id.value)
			.then(res => {
			   storeCombination.value = res.data;
			   getImageBase64(res.data.storeCombination.image);
			   // #ifdef H5
			   make(res.data.userInfo.uid);
			   // #endif
			})
			.catch(err => {
				proxy.$util.Tips({
					title: err
				});
				uni.redirectTo({
					success(){},
					fail() {
						uni.navigateTo({
							url: '/pages/index/index',
						})
					}
				})
			});
	}
	function getImageBase64(images){
		imageBase64({url:images}).then(res=>{
			imgTop.value = res.data.code
		})
	}
	// 生成二维码；
	function make(uid) {
		let href = location.protocol + '//' + window.location.host + '/pages/activity/goods_combination_status/index?id=' + id.value + "&spread=" + uid;
		uQRCode.make({
			canvasId: 'qrcode',
			text: href,
			size: qrcodeSize.value,
			margin: 10,
			success: res => {
				PromotionCode.value = res;
				let arrImages = [posterbackgd.value, imgTop.value, PromotionCode.value];
				let storeName = storeCombination.value.storeCombination.title;
				let price = storeCombination.value.storeCombination.price;
				let people = storeCombination.value.storeCombination.people;
				let otPrice = storeCombination.value.storeCombination.otPrice;
				let count = storeCombination.value.count;
				setTimeout(() => {
					PosterCanvas(arrImages, storeName, price, people,otPrice,count);
				}, 300);
			},
			complete: () => {
			},
			fail:res=>{
				proxy.$util.Tips({
					title: '海报二维码生成失败！'
				});
			}
		})
	}
	// 生成海报
	function PosterCanvas(arrImages, storeName, price, people,otPrice,count){
		uni.showLoading({
			title: '海报生成中',
			mask: true
		});
		let context = uni.createCanvasContext('firstCanvas')
		context.clearRect(0, 0, 0, 0);
		uni.getImageInfo({
		            src: arrImages[0],
		            success: function (image) {
						context.drawImage(arrImages[0], 0, 0, 750, 1190);
						context.setFontSize(36);
						context.setTextAlign('center');
						context.setFillStyle('#282828');
						let maxText = 20;
						let text = storeName;
						let topText = '';
						let bottomText = '';
						let len = text.length;
						if(len>maxText*2){
							text = text.slice(0,maxText*2-4)+'......';
							topText = text.slice(0,maxText-1);
							bottomText = text.slice(maxText-1,len);
						}else{
							if(len>maxText){
								topText = text.slice(0,maxText-1);
								bottomText = text.slice(maxText-1,len);
							}else{
								topText = text;
								bottomText = '';
							}
						}
						context.fillText(topText, 750/2, 60);
						context.fillText(bottomText, 750/2, 100);
						
						context.drawImage(arrImages[1], 150, 350, 450, 450);
						context.save();
						context.drawImage(arrImages[2], 300, 950, 140, 140);
						context.restore();
						
						context.setFontSize(72);
						context.setFillStyle('#fc4141');
						context.fillText(price, 250, 210);
						
						context.setFontSize(32);
						context.setFillStyle('#FFFFFF');
						context.fillText( people+'人团', 538, 198);
						
						
						context.setFontSize(26);
						context.setFillStyle('#3F3F3F');
						context.setTextAlign('center');
						context.fillText( '原价：￥'+otPrice +'   还差 ' + count + '人 拼团成功', 750 / 2, 275);
						
						context.draw(true,function(){
							uni.canvasToTempFilePath({
							  destWidth: 750,
							  destHeight: 1190,
							  canvasId: 'firstCanvas',
							  fileType: 'jpg',
							  success: function(res) {
							    // 在H5平台下，tempFilePath 为 base64
								uni.hideLoading();
								imagePath.value = res.tempFilePath;
								canvasStatus.value = true;
							  } 
							})
						})
		            },
					fail: function(err) {
						uni.hideLoading();
						proxy.$util.Tips({
							title: '无法获取图片信息'
						});
					}
		})
	}
	function showImage() {
		let imgArr = image.value.split(',')
		uni.previewImage({
				urls: imgArr,
				longPressActions: {
						itemList: ['发送给朋友', '保存图片', '收藏'],
					success: function(data) {
						},
						fail: function(err) {
						}
				}
		});
	}

	onLoad((options) => {
		// #ifdef MP
		from.value = 'routine'
		// #endif
		// #ifdef H5
		from.value = 'wechat'
		// #endif
		if (options.hasOwnProperty('type') && options.hasOwnProperty('id')) {
			type.value = options.type
			id.value = options.id
			if (options.type == 1) {
				uni.setNavigationBarTitle({
					title: '砍价海报'
				})
			} else {
				uni.setNavigationBarTitle({
					title: '拼团海报'
				})
			}
		} else {
			return app.Tips({
				title: '参数错误',
				icon: 'none'
			}, {
				tab: 3,
				url: 1
			});
		}
	});

	onShow(() => {
		getPosterInfo();
	});
</script>

<style>
	page {
		background-color: #d22516 !important;
	}
    .canvas {
		position:fixed;
		z-index: -5;
		opacity: 0;
	}
	.poster-poster .tip {
		height: 80rpx;
		font-size: 26rpx;
		color: #e8c787;
		text-align: center;
		line-height: 80rpx;
	}

	.poster-poster .tip .iconfont {
		font-size: 36rpx;
		vertical-align: -4rpx;
		margin-right: 18rpx;
	}

	.poster-poster .pictrue {
		width: 690rpx;
		height: 1130rpx;
		margin: 0 auto 50rpx auto;
	}

	.poster-poster .pictrue image {
		width: 100%;
		height: 100%;
	}
</style>
