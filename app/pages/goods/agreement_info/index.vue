<template>
	<view class="user_about" :data-theme="theme">
		<view>
			<view class="text cancelTxt" :class="{cancelTxt: type == 'useraccountcancelinfo'}">
				<jyf-parser :html="agreementData?agreementData:''" ref="article" :tag-style="tagStyle"></jyf-parser>
			</view>
			<view v-if="type === 'useraccountcancelinfo'" class="btn">
				<button @click="cancelBtn" class="sure-btn">申请注销</button>
			</view>
		</view>
		<view class="cancel" v-if="type == 'useraccountcancelinfo' && loaded">
			<view class="acea-row row-center">
				<view class="checkbox acea-row row-middle" @click="setCheck">
					<text v-if="!check" class="iconfont icon-weixuanzhong"></text>
					<text v-else class="iconfont icon-xuanzhong1"></text>
				</view>
				<view class="ml10">已阅读并同意<text class="font font-color" @click="toCancel">《重要提醒》</text></view>
			</view>
		</view>
		<view class="outMoal" v-if="moal">
			<view class="box">
				<view class="title">该账号将永久注销</view>
				<view class="moalBtn">
					<view class="ok" @click="ok">确定</view>
					<view class="no" @click="cancelMoal">取消</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	// +----------------------------------------------------------------------
	// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
	// +----------------------------------------------------------------------
	// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
	// +----------------------------------------------------------------------
	// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
	// +----------------------------------------------------------------------
	// | Author: CRMEB Team <admin@crmeb.com>
	// +----------------------------------------------------------------------
	import {
		userOut,
	} from '@/api/user.js'
	import {
		agreementInfo
	} from '@/api/api.js'
	import {
		goToAgreement
	} from "@/libs/order";
	import parser from "@/components/jyf-parser/jyf-parser";
	let app = getApp();
	export default {
		name: 'user_about',
		components: {
			"jyf-parser": parser,
		},
		data() {
			return {
				theme: app.globalData.theme,
				agreementData: '',
				loaded: false,
				check: false,
				moal: false,
				tagStyle: {
					img: 'width:100%;display:block;',
					table: 'width:100%',
					video: 'width:100%'
				},
				type:''
			}
		},
		onLoad: function(options) {
			this.type = options.from;
			this.setTitle(this.type);
			this.getCacheinfo();
		},
		methods: {
			toCancel() {
				goToAgreement('useraccountcancelnoticeinfo')
			},
			setCheck() {
				this.check = !this.check
			},
			cancelMoal() {
				this.moal = false
				this.check = false
			},
			ok() {
				uni.showLoading({
					title: '注销中',
					mask: true
				});
				this.moal = false;
				userOut().then(res => {
					this.$store.commit("clearStorage");
					this.$store.dispatch("GetTokenIsExist");
					this.$store.dispatch('GetThemeConfig');
					uni.hideLoading();
					setTimeout(() => {
						uni.reLaunch({
							url: '/pages/index/index'
						});
					}, 500);
				}).then(err => {
					uni.hideLoading()
					this.$util.Tips({
						title: err
					});
				});
			},
			cancelBtn() {
				if (!this.check) {
					return uni.showToast({
						title: "请勾选已阅读",
						icon: 'none',
						duration: 2000,
					})
				} else {
					this.moal = true
				}
			},
			getCacheinfo() {
				this.loaded = false;
				agreementInfo(this.type).then(res => {
					this.agreementData = res.data ? JSON.parse(res.data).agreement : ''
					this.loaded = true;
				})
			},
			setTitle(e) {
				switch (e) {
					case 'aboutusinfo':
						uni.setNavigationBarTitle({
							title: '关于我们协议'
						})
						break;
					case 'intelligentinfo':
						uni.setNavigationBarTitle({
							title: '平台资质证明'
						})
						break;
					case 'useraccountcancelinfo':
						uni.setNavigationBarTitle({
							title: '用户注销协议'
						})
						break;
					case 'useraccountcancelnoticeinfo':
						uni.setNavigationBarTitle({
							title: '用户注销声明'
						})
						break;
					case 'userinfo':
						uni.setNavigationBarTitle({
							title: '用户注册协议'
						})
						break;
					case 'coupon/agreement/info':
						uni.setNavigationBarTitle({
							title: '优惠券协议'
						})
						break;
					default:
						uni.setNavigationBarTitle({
							title: '用户隐私协议'
						})
						break;
				}
			}
		}
	}
</script>

<style lang="scss">
	.user_about {
		.text {
			font-size: 30rpx;
			font-weight: 400;
			padding: 30rpx;
			color: #282828;
		}
	
		.cancelTxt {
			overflow: hidden;
			overflow-y: auto;
			padding-bottom: 240rpx;
			image {
				max-width: 100%;
			}
		}
	
		.cancel {
			position: fixed;
			bottom: 160rpx;
			left: 0;
			z-index: 99999;
			width: 100%;
			bottom: 120rpx;
			padding: 10rpx 0;
			background: #fff;
			.checkbox {
				text-align: center;
				font-size: 24rpx;
				font-weight: 400;
	
				span {
					margin-left: 5rpx;
				}
	
				.font {
					@include main_color(theme);
					font-style: normal;
				}
	
				.iconfont {
					font-size: 36rpx;
				}
			}
	
			.btn {
				width: 690rpx;
				height: 90rpx;
				@include linear-gradient(theme);
				border-radius: 45rpx;
				margin: 0 auto;
				margin-top: 30rpx;
				text-align: center;
				line-height: 90rpx;
				font-size: 32rpx;
				font-weight: 400;
				color: #FFFFFF;
			}
		}
	}
	
	.outMoal {
		width: 100%;
		height: 100%;
		background: rgba(0, 0, 0, 0.5);
		position: fixed;
		top: 0;
		left: 0;
		z-index: 2;
		display: flex;
		align-items: center;
		justify-content: center;
	
		.box {
			position: fixed;
			width: 590rpx;
			height: 258rpx;
			background: #FFFFFF;
			opacity: 1;
			border-radius: 20rpx;
			text-align: center;
			padding: 50rpx;
	
			.title {
				font-size: 30rpx;
				font-weight: 600;
				color: #282828;
			}
	
			.moalBtn {
				margin-top: 43rpx;
				display: flex;
				justify-content: space-between;
	
				.ok {
					width: 234rpx;
					height: 66rpx;
					@include coupons_border_color(theme);
					border-radius: 33rpx;
					font-size: 26rpx;
					line-height: 66rpx;
					@include main_color(theme);
				}
	
				.no {
					width: 234rpx;
					height: 66rpx;
					@include linear-gradient(theme);
					border-radius: 33rpx;
					font-size: 26rpx;
					color: #FFFFFF;
					line-height: 66rpx;
				}
			}
		}
	}
	
	.btn {
		width: 100%;
		height: 120rpx;
		background-color: #FFFFFF;
		position: fixed;
		bottom: 0;
		line-height: 120rpx;
		padding: 17rpx 0;
		z-index: 99999;
		.sure-btn {
			width: 690rpx;
			height: 86rpx;
			margin: 0 auto;
			line-height: 86rpx;
			color: #FFFFFF;
			border-radius: 43rpx;
			@include main_bg_color(theme);
			font-size: 32rpx;
		}
	}
</style>