<template>
	<!-- 优惠券 -->
	<view v-if="couponList.length">
		<view :style="[...boxStyle]">
			<template v-if="listStyle == 0">
				<view class="scroll_box">
					<scroll-view scroll-x="true" class="scroll_view acea-row">
						<view class="no-warp acea-row row-middle">
							<view class="couponBg acea-row row-middle style1" v-for="(item, index) in couponList" :key="index"
								:style="[...contentConfig]">
								<view class="left">
									<view :style="[...priceColorStyle]"><text class="price-icon">￥</text><text
											class="price"
											:class="item.money.length>6?'sizePrice':''">{{item.money}}</text></view>
									<view class="title" :class="item.minPrice.length>6?'sizeTitle':''">
										满{{item.minPrice}}元可用</view>
								</view>
								<view class="right" @click="getCoupon(item.isUse,item.id)">
									{{item.isUse?'已 领 取':'领 取'}}
								</view>
							</view>
						</view>
					</scroll-view>
				</view>
			</template>
			<template v-if="listStyle == 1">
				<view class="coupon1 acea-row row-middle">
					<scroll-view scroll-x="true" class="scroll_view">
						<view class="list acea-row row-middle">
							<view class="item" v-for="(item, index) in couponList" :key="index"
								:style="[...contentConfig]">
								<view class="money" :style="[...priceColorStyle]">
									<view :class="item.money.length>=6?'sizePrice-two':''"><text class="lable"
											:class="item.money.length>=6?'sizeLable-two':''">¥</text>{{item.money}}
									</view>
									<view class="tips">满{{item.minPrice}}可用</view>
								</view>
								<view class="sill" :style="[...btnColorStyle]" @click="getCoupon(item.isUse,item.id)">
									{{item.isUse?'已领取':'去领取'}}
								</view>
								<image src="../../static/images/newVip02.png" />
							</view>
						</view>
					</scroll-view>
				</view>
			</template>
			<template v-if="listStyle == 2">
				<view class="scroll_box">
					<scroll-view scroll-x="true" class="scroll_view acea-row">
						<view class="no-warp acea-row row-middle">
							<view class="couponBg acea-row row-middle couponBg-new" v-for="(item, index) in couponList" :key="index"
								:style="[...contentConfig]">
								<view class="left">
									<view :style="[...priceColorStyle]"><text class="price-icon">￥</text><text
											class="price"
											:class="item.money.length>6?'sizePrice':''">{{item.money}}</text></view>
									<view class="title" :class="item.minPrice.length>6?'sizeTitle':''">
										满{{item.minPrice}}元可用</view>
								</view>
								<view class="right" @click="getCoupon(item.isUse,item.id)" v-if="!item.isUse">
									立即领取
								</view>
								<view class="right" @click="getCoupon(item.isUse,item.id)" v-else>
									立即使用
								</view>
							</view>
						</view>
					</scroll-view>
				</view>
			</template>
			<template v-if="listStyle == 3">
				<view class="coupon5 acea-row row-middle" :style="[...boxBg]">
					<scroll-view scroll-x="true" style="white-space: nowrap; display: flex">
						<view class="list acea-row row-middle">
							<view class="item acea-row row-middle" v-for="(item, index) in couponList" :key="index"
								:style="[...contentConfig]">
								<view class="left">
									<view :class="item.money.length>=6?'sizePrice-four':''"
										:style="[...priceColorStyle]" class="money"><text
											class="label">¥</text>{{item.money}}
									</view>
									<view class="tips">满{{item.minPrice}}可用</view>
								</view>
								<view class="right acea-row row-center">
									<view class="rightCon" @click="getCoupon(item.isUse,item.id)">
										{{item.isUse?'已 领 取':'领 取'}}
									</view>
								</view>
								<view class="roll" :style="[...boxBg]"></view>
							</view>
						</view>
					</scroll-view>
				</view>
			</template>
		</view>
	</view>
</template>

<script>
	// +----------------------------------------------------------------------
	// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
	// +----------------------------------------------------------------------
	// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
	// +----------------------------------------------------------------------
	// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
	// +----------------------------------------------------------------------
	// | Author: CRMEB Team <admin@crmeb.com>
	// +----------------------------------------------------------------------
	import {
		getCoupons,
		setCouponReceive
	} from "@/api/api.js"
	let app = getApp();
	export default {
		name: 'homeCoupon',
		props: {
			dataConfig: {
				type: Object,
				default: () => {}
			}
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				couponList: [],
				listStyle: 0,
				themeColor:this.$options.filters.filterTheme(app.globalData.theme)
			};
		},
		computed: {
			//最外层盒子的样式
			boxStyle() {
				return [{
						'border-radius': 2 * this.dataConfig.bgStyle.val ? 2 * this.dataConfig.bgStyle.val + 'rpx' :
							'0'
					},
					{
						background: this.listStyle != 3 ?
							`linear-gradient(to right,${this.dataConfig.bgColor.color[0].item}, ${this.dataConfig.bgColor.color[1].item})` :
							this.dataConfig.bgColorNew.color[0].item,
					},
					{
						'margin': 2 * this.dataConfig.mbConfig.val + 'rpx' + ' ' + 2 * this.dataConfig.lrConfig.val +
							'rpx' +
							' ' + 0
					},
					{
						'padding': 2 * this.dataConfig.upConfig.val + 'rpx' + ' ' + '20rpx' + ' ' + 2 * this.dataConfig
							.downConfig
							.val + 'rpx'
					},
				];
			},
			boxBg() {
				return [{
					background: this.dataConfig.bgColorNew.color[0].item,
				}, ];
			},
			//内容边距
			contentConfig() {
				return [{
						'margin-right': this.dataConfig.contentConfig.val ? 2 * this.dataConfig.contentConfig.val +
							'rpx' : '0'
					},
					{
						'background': this.listStyle == 1 ?
							(this.dataConfig.themeStyleConfig.tabVal?this.dataConfig.itemBgColor.color[0].item:this.themeColor) : this.listStyle == 3 ?
							`linear-gradient(180deg,${this.dataConfig.btnColor.color[0].item}, ${this.dataConfig.themeStyleConfig.tabVal?this.dataConfig.btnColor.color[1].item:this.themeColor})` :
							'',
					},
				];
			},
			//优惠金额颜色
			priceColorStyle() {
				return [{
					'color': this.dataConfig.themeStyleConfig.tabVal?this.dataConfig.priceColor.color[0].item:this.themeColor
				}];
			},
			//领取按钮
			btnColorStyle() {
				return [{
					'background': `linear-gradient(90deg,${this.dataConfig.themeStyleConfig.tabVal?this.dataConfig.btnColor.color[0].item:'#FF7931'}, ${this.dataConfig.themeStyleConfig.tabVal?this.dataConfig.btnColor.color[1].item:this.themeColor})`,
				}, ];
			},
			//展示数量
			limit() {
				return this.dataConfig.numConfig.val
			},
		},
		mounted() {
			this.getList();
			this.setConfig()
		},
		methods: {
			getCoupon(isUse, id) {
				let that = this
				if (!isUse) {
					setCouponReceive(id).then(res => {
						that.getList();
					})
				}
			},
			setConfig(data) {
				this.listStyle = this.dataConfig.tabConfig.tabVal;
			},
			//优惠券列表
			getList() {
				getCoupons({
					page: 1,
					limit: this.limit
				}).then(res => {
					this.couponList = res.data.list;
				})
			},
			//去更多
			goPage() {
				this.$util.navigateTo(this.dataConfig.linkConfig.val ? this.dataConfig.linkConfig.val :
					'/pages/activity/couponList/index')
			}
		}
	};
</script>

<style lang="scss" scoped>
	.scroll_box {
		display: flex;
		overflow: hidden;
	}
	.couponBg.style1{
		width: 254rpx;
		height: 144rpx;
		background: url('../../static/images/couponBg.png');
		background-repeat: no-repeat;
	}
	.couponBg {
		background-size: contain;
		background-repeat: no-repeat;
		flex-shrink: 0;
		color: #fff;
		.left {
			width: 80%;
			text-align: center;

			.price-icon {
				font-weight: 500;
				font-size: 30rpx;
			}

			.price {
				font-weight: 500;
				font-size: 52rpx;
			}

			.sizePrice {
				font-size: 38rpx !important;
			}

			.sizeTitle {
				font-size: 20rpx !important;
			}

			.title {
				font-weight: 400;
				font-size: 26rpx;
			}
		}

		.right {
			/* #ifdef H5 */
			margin-left: 10rpx;
			/* #endif */
			font-size: 26rpx;
			text-align: center;
			writing-mode: tb-rl;
		}
	}
	.couponBg-new.couponBg {
	  width: 137px;
	  height: 75px;
	  background: url('../../static/images/couponBg2.png') !important;
	  .right {
	    width: 13%;
	    color: #e93323;
	    font-weight: 400;
	    font-size: 13px;
	  }
	  .title {
	    color: #e93323;
	  }
	  .sizePrice {
	  	font-size: 36rpx !important;
		margin-right: 10rpx;
	  }
	}
	.coupon1 {
		flex-shrink: 0;
		overflow: hidden;

		.list {
			margin-top: 16rpx;
			display: inline-flex;
			flex-wrap: nowrap;

			.item {
				width: 156rpx;
				height: 152rpx;
				background: #f12a13;
				position: relative;
				border-radius: 12rpx 12rpx 30rpx 30rpx;

				.money {
					width: 140rpx;
					height: 106rpx;
					background: #ffffff;
					border: 2rpx solid #fceae9;
					position: absolute;
					left: 9rpx;
					top: -16rpx;
					text-align: center;
					font-size: 40rpx;
					font-family: D-DIN-PRO, D-DIN-PRO;
					font-weight: 600;
					color: #e93323;
					padding-top: 6px;
					border-radius: 12rpx 12rpx 0 0;

					.lable {
						font-size: 28rpx;
					}

					.tips {
						font-size: 18rpx;
						color: #999999;
						font-weight: 500;
					}

					.sizePrice {
						// font-size: 24rpx;
					}
				}

				.sill {
					position: absolute;
					bottom: -2rpx;
					left: 0;
					width: 100%;
					height: 78rpx;
					// background: linear-gradient(90deg, #e93323 0%, #ff7931 100%);
					color: #fff;
					line-height: 92rpx;
					border-radius: 0 0 30rpx 30rpx;
					font-size: 20rpx;
					text-align: center;
				}

				image {
					position: absolute;
					left: 0;
					width: 156rpx;
					height: 20rpx;
					bottom: 58rpx;
				}
			}
		}
	}

	

	.coupon5 {
		flex-shrink: 0;
		background: #ffffff;
		border-radius: 16rpx;
		width: 100%;
		overflow: hidden;

		.list {
			flex-wrap: nowrap;

			.item {
				flex-shrink: 0;
				width: 228rpx;
				height: 108rpx;
				border-radius: 12rpx;
				position: relative;

				.roll {
					width: 16rpx;
					height: 16rpx;
					background: #fff;
					border-radius: 50%;
					position: absolute;
					left: -8rpx;
				}

				.right {
					flex: 1;

					.rightCon {
						font-size: 22rpx;
						color: #fff;
						text-align: center;
						writing-mode: tb-rl;
					}
				}

				.left {
					width: 172rpx;
					height: 100%;
					background: linear-gradient(0deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.8) 100%);
					border-radius: 12rpx;
					text-align: center;
					color: #e93323;
					display: flex;
					flex-direction: column;
					justify-content: center;

					.money {
						font-size: 42rpx;
						font-family: D-DIN-PRO, D-DIN-PRO;
						font-weight: 600;

						.label {
							font-size: 28rpx;
						}
					}

					.tips {
						font-size: 22rpx;
					}
				}
			}
		}
	}

	.no-warp {
		flex-wrap: nowrap;
	}

	.scroll_view {
		white-space: nowrap;
	}

	.sizePrice-two {
		font-size: 24rpx;
		margin-top: 5px;
	}

	.sizeLable-two {
		font-size: 18rpx !important;
	}

	.sizePrice-three {
		font-size: 32rpx !important;
	}

	.sizePrice-three-tips {
		font-size: 22rpx !important;
		margin-left: 6rpx !important;
	}

	.sizePrice-four {
		font-size: 30rpx !important;
	}
</style>