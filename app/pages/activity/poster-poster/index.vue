<template>
	<view>
		<view class='poster-poster'>
			<view class='tip'><text class='iconfont icon-shuoming'></text>提示：点击图片即可保存至手机相册 </view>
			<!-- <view class='pictrue'>
				<image :src='image' mode="widthFix"></image>
			</view> -->
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

<script>
	import { getCombinationPink, getCombinationPoster } from '../../../api/activity.js';
	import uQRCode from '@/js_sdk/Sansnn-uQRCode/uqrcode.js';
	import { imageBase64 } from "@/api/public";
	export default {
		data() {
			return {
				parameter: {
					'navbar': '1',
					'return': '1',
					'title': '拼团海报',
					'color': true,
					'class': '0'
				},
				type: 0,
				id: 0,
				image: '',
				from:'',
				storeCombination: {},
				qrcodeSize: 600,
				posterbackgd: '/static/images/canbj.png',
				PromotionCode: '',//二维码
				canvasStatus: false,
				imgTop: '' //商品图base64位
			}
		},
		onLoad(options) {
			// #ifdef MP
			this.from = 'routine'
			// #endif
			// #ifdef H5
			this.from = 'wechat'
			// #endif
			var that = this;
			if (options.hasOwnProperty('type') && options.hasOwnProperty('id')) {
				this.type = options.type
				this.id = options.id
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
		},
		onShow() {
			this.getPosterInfo();
		},
		methods: {
			getPosterInfo: function() {
				var that = this,url = '';
				let data = {
					pinkId: parseFloat(that.id),
					from: that.from
				};
				if (that.type == 1) {
				
				} else {
					this.getCombinationPink();
				}
			},
			//拼团信息
			getCombinationPink: function() {
				var that = this;
				getCombinationPink(this.id)
					.then(res => {
					   this.storeCombination = res.data;
					   this.getImageBase64(res.data.storeCombination.image);
					   // #ifdef H5
					   that.make(res.data.userInfo.uid);
					   // #endif
					})
					.catch(err => {
						this.$util.Tips({
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
			},
			getImageBase64:function(images){
				let that = this;
				imageBase64({url:images}).then(res=>{
					that.imgTop = res.data.code
				})
			},
			// 生成二维码；
			make(uid) {
				let href = location.protocol + '//' + window.location.host + '/pages/activity/goods_combination_status/index?id=' + this.id + "&spread=" + uid;
				uQRCode.make({
					canvasId: 'qrcode',
					text: href,
					size: this.qrcodeSize,
					margin: 10,
					success: res => {
						this.PromotionCode = res;
						let arrImages = [this.posterbackgd, this.imgTop, this.PromotionCode];
						let storeName = this.storeCombination.storeCombination.title;
						let price = this.storeCombination.storeCombination.price;
						let people = this.storeCombination.storeCombination.people;
						let otPrice = this.storeCombination.storeCombination.otPrice;
						let count = this.storeCombination.count;
						this.PosterCanvas(arrImages, storeName, price, people,otPrice,count);
					},
					complete: () => {
					},
					fail:res=>{
						this.$util.Tips({
							title: '海报二维码生成失败！'
						});
					}
				})
			},
			// 生成海报
			PosterCanvas:function(arrImages, storeName, price, people,otPrice,count){
				uni.showLoading({
					title: '海报生成中',
					mask: true
				});
				let context = uni.createCanvasContext('firstCanvas')
				context.clearRect(0, 0, 0, 0);
				let that = this;
				uni.getImageInfo({
				            src: arrImages[0],
				            success: function (image) {
								console.log('啦啦', image)
								context.drawImage(arrImages[0], 0, 0, 750, 1190);
								context.setFontSize(36);
								context.setTextAlign('center');
								context.setFillStyle('#282828');
								let maxText = 17;
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
								context.fillText(price, 220, 210);
								
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
										  console.log('啦啦222', res)
									    // 在H5平台下，tempFilePath 为 base64
										uni.hideLoading();
										//successFn && successFn(res.tempFilePath);
										that.imagePath = res.tempFilePath;
										console.log(that.imagePath)
										that.canvasStatus = true;
									  } 
									})
								})
				            },
							fail: function(err) {
								console.log(err)
								uni.hideLoading();
								that.$util.Tips({
									title: '无法获取图片信息'
								});
							}
				})
			},
			showImage: function() {
				var that = this;
				let imgArr = this.image.split(',')
				uni.previewImage({
						urls: imgArr,
						longPressActions: {
								itemList: ['发送给朋友', '保存图片', '收藏'],
								success: function(data) {
										console.log('选中了第' + (data.tapIndex + 1) + '个按钮,第' + (data.index + 1) + '张图片');
								},
								fail: function(err) {
										console.log(err.errMsg);
								}
						}
				});
			},
		}
	}
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
