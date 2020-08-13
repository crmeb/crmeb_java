<template>
	<view>
		<view class='poster-poster'>
			<view class='tip'><text class='iconfont icon-shuoming'></text>提示：点击图片即可保存至手机相册 </view>
			<view class='pictrue'>
				<image :src='image' mode="widthFix"></image>
			</view>
		</view>
	</view>
</template>

<script>
	import { getBargainPoster, getCombinationPoster } from '../../../api/activity.js';
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
				from:''
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
					id: that.id,
					'from': that.from
				};
				if (that.type == 1) {
					getBargainPoster({
						bargainId: that.id,
						'from': that.from
					}).then(res => {
							that.image = res.data.url
					}).catch(err => {
						console.log(err)
					})
				} else {
					getCombinationPoster(data).then(res => {
						that.image = res.data.url
					}).catch(err => {
						
					})
				}
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
		height: 100%;
		margin: 0 auto 50rpx auto;
	}

	.poster-poster .pictrue image {
		width: 100%;
		height: 100%;
	}
</style>
