<template>
	<view>
		<view class='coupon-list-window' :class='coupon.coupon==true?"on":""'>
			<view v-if="!orderShow"  class="nav acea-row row-around">
				<view :class="['acea-row', 'row-middle', type === 1 ? 'on' : '']" @click="setType(1)">通用券</view>
				<view :class="['acea-row', 'row-middle', type === 2 ? 'on' : '']" @click="setType(2)">商品券</view>
				<view :class="['acea-row', 'row-middle', type === 3 ? 'on' : '']" @click="setType(3)">品类券</view>
			</view>
			<!-- <view class="occupy" v-if="!orderShow"></view> -->
			<!-- <view class='title'>优惠券<text class='iconfont icon-guanbi' @click='close'></text></view> -->
			<view class='coupon-list' :style="{'margin-top':!orderShow?'0':'50rpx'}">
				<block v-if="coupon.list.length">
					<!-- <view class='item acea-row row-center-wrapper' v-for="(item,index) in coupon.list" :key='index'> -->
					<view class='item acea-row row-center-wrapper' v-for="(item,index) in coupon.list"
						@click="getCouponUser(index,item.id)" :key='index'>
						<view class='money acea-row row-column row-center-wrapper' :class='item.isUse?"moneyGray":""'>
							<view>￥<text class='num'>{{item.money?Number(item.money):''}}</text></view>
							<view class="pic-num">满{{item.minPrice}}元可用</view>
						</view>
						<view class='text'>
							<view class='condition line2'>
								<span class='line-title' :class='item.isUse?"gray":""' v-if='item.useType===1'>通用</span>
								<span class='line-title' :class='item.isUse?"gray":""'
									v-else-if='item.useType===3'>品类</span>
								<span class='line-title' :class='item.isUse?"gray":""' v-else>商品</span>
								<span>{{item.name}}</span>
							</view>
							<view class='data acea-row row-between-wrapper'>
								<view v-if="item.day>0">领取后{{item.day}}天内可用</view>
								<view v-else>
									{{ item.useStartTimeStr&& item.useEndTimeStr ? item.useStartTimeStr + " - " + item.useEndTimeStr : ""}}
								</view>
								<view class='bnt gray' v-if="item.isUse">{{item.use_title || '已领取'}}</view>
								<view class='bnt bg-color' v-else>{{coupon.statusTile || '立即领取'}}</view>
							</view>
						</view>
					</view>
				</block>
				<!-- 无优惠券 -->
				<view class='pictrue' v-else>
					<image src='../../static/images/noCoupon.png'></image>
				</view>
			</view>
			
		</view>
		<view class='mask' catchtouchmove="true" :hidden='coupon.coupon==false' @click='close'></view>
	</view>
</template>

<script>
	import {
		setCouponReceive
	} from '@/api/api.js';
	export default {
		props: {
			//打开状态 0=领取优惠券,1=使用优惠券
			openType: {
				type: Number,
				default: 0,
			},
			coupon: {
				type: Object,
				default: function() {
					return {};
				}
			},
			//下单页面使用优惠券组件不展示tab切换页
			orderShow: {
				type: String,
				default: function() {
					return '';
				}
			}
		},
		data() {
			return {
               type: 1
			};
		},

		methods: {
			close: function() {
				this.type = 1
				this.$emit('ChangCouponsClone');
			},
			getCouponUser: function(index, id) {
				let that = this;
				let list = that.coupon.list;
				if (list[index].isUse == true && this.openType == 0) return true;
				switch (this.openType) {
					case 0:
						//领取优惠券
						let ids = [];
						ids.push(id);
						setCouponReceive(id).then(res => {
							that.$emit('ChangCouponsUseState', index);
							that.$util.Tips({
								title: "领取成功"
							});
							that.$emit('ChangCoupons', list[index]);
						})
						break;
					case 1:
						that.$emit('ChangCoupons', index);
						break;
				}
			},
			setType: function(type) {
				this.type = type;
				this.$emit('tabCouponType', type);
			}
		}
	}
</script>

<style scoped lang="scss">
	.coupon-list-window {
		position: fixed;
		bottom: 0;
		left: 0;
		width: 100%;
		background-color: #f5f5f5;
		border-radius: 16rpx 16rpx 0 0;
		z-index: 555;
		transform: translate3d(0, 100%, 0);
		transition: all .3s cubic-bezier(.25, .5, .5, .9);
	}

	.coupon-list-window.on {
		transform: translate3d(0, 0, 0);
	}

	.coupon-list-window .title {
		height: 124rpx;
		width: 100%;
		text-align: center;
		line-height: 124rpx;
		font-size: 32rpx;
		font-weight: bold;
		position: relative;
	}

	.coupon-list-window .title .iconfont {
		position: absolute;
		right: 30rpx;
		top: 50%;
		transform: translateY(-50%);
		font-size: 35rpx;
		color: #8a8a8a;
		font-weight: normal;
	}

	.coupon-list-window .coupon-list {
		margin: 0 0 30rpx 0;
		height: 823rpx;
		overflow: auto;
		padding-top: 30rpx;
	}

	.coupon-list-window .pictrue {
		width: 414rpx;
		height: 336rpx;
		margin: 208rpx auto;
	}

	.coupon-list-window .pictrue image {
		width: 100%;
		height: 100%;
	}

	.pic-num {
		color: #fff;
		font-size: 24rpx;
	}

	.line-title {
		width: 90rpx;
		padding: 0 10rpx;
		box-sizing: border-box;
		background: rgba(255, 247, 247, 1);
		border: 1px solid rgba(232, 51, 35, 1);
		opacity: 1;
		border-radius: 20rpx;
		font-size: 20rpx;
		color: #E83323;
		margin-right: 12rpx;
	}

	.line-title.gray {
		border-color: #BBB;
		color: #bbb;
		background-color: #F5F5F5;
	}

	.nav {
		// position: absolute;
		// top: 0;
		// left: 0;
		width: 100%;
		height: 96rpx;
		border-bottom: 2rpx solid #F5F5F5;
		border-top-left-radius: 16rpx;
		border-top-right-radius: 16rpx;
		background-color: #FFFFFF;
		font-size: 30rpx;
		color: #999999;
	}

	.nav .acea-row {
		border-top: 5rpx solid transparent;
		border-bottom: 5rpx solid transparent;
	}

	.nav .acea-row.on {
		border-bottom-color: #E93323;
		color: #282828;
	}

	.nav .acea-row:only-child {
		border-bottom-color: transparent;
	}

	.occupy {
		height: 106rpx;
	}

	.coupon-list .item {
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.06);
	}

	.coupon-list .item .money {
		font-weight: normal;
	}
</style>
