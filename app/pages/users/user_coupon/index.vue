<template>
	<view>
		<view class="navbar acea-row row-around">
			<view class="item acea-row row-center-wrapper" :class="{ on: navOn === 'usable' }" @click="onNav('usable')">未使用</view>
			<view class="item acea-row row-center-wrapper" :class="{ on: navOn === 'unusable' }" @click="onNav('unusable')">已使用/过期</view>
		</view>
		<view class='coupon-list' v-if="couponsList.length">
			<view class='item acea-row row-center-wrapper' v-for='(item,index) in couponsList' :key="index">
				<view class='money' :class="item.validStr==='unusable'||item.validStr==='overdue'||item.validStr==='notStart' ? 'moneyGray' : ''">
					<view>￥<text class='num'>{{item.money?Number(item.money):''}}</text></view>
					<view class="pic-num">满{{ item.minPrice?Number(item.minPrice):'' }}元可用</view>
				</view>
				<view class='text'>
					<view class='condition line2'>
						<span class="line-title" :class="item.validStr==='unusable'||item.validStr==='overdue'||item.validStr==='notStart' ? 'bg-color-huic' : 'bg-color-check'" v-if="item.useType === 1">通用</span>
						<span class="line-title" :class="item.validStr==='unusable'||item.validStr==='overdue'||item.validStr==='notStart' ? 'bg-color-huic' : 'bg-color-check'"  v-else-if="item.useType === 2">商品</span>
						<span class="line-title" :class="item.validStr==='unusable'||item.validStr==='overdue'||item.validStr==='notStart' ? 'bg-color-huic' : 'bg-color-check'" v-else-if="item.useType === 3">品类</span>
						<span>{{item.name}}</span>
					</view>
					<view class='data acea-row row-between-wrapper'>
						<view>{{item.useStartTimeStr}}~{{item.useEndTimeStr}}</view>
						<view class='bnt' :class="item.validStr==='unusable'||item.validStr==='overdue'||item.validStr==='notStart'?'gray':'bg-color'">{{item.validStr | validStrFilter}}</view>
					</view>
				</view>
			</view>
		</view>
		<view class='loadingicon acea-row row-center-wrapper' v-if="couponsList.length">
		     <text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
		  </view>
		<view class='noCommodity' v-if="!couponsList.length">
			<view class='pictrue'>
				<image src='../../../static/images/noCoupon.png'></image>
			</view>
		</view>
		<!-- #ifdef MP -->
		<!-- <authorize @onLoadFun="onLoadFun" :isAuto="isAuto" :isShowAuth="isShowAuth" @authColse="authColse"></authorize> -->
		<!-- #endif -->
		<home></home>
	</view>
</template>

<script>
	import {
		getUserCoupons
	} from '@/api/api.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	// #ifdef MP
	import authorize from '@/components/Authorize';
	// #endif
	import home from '@/components/home';
	export default {
		components: {
			// #ifdef MP
			authorize,
			// #endif
			home
		},
		filters: {
		    validStrFilter(status) {
		      const statusMap = {
		        'usable': '可用',
		        'unusable': '已用',
				'overdue': '过期',
				'notStart': '未开始'
		      }
		      return statusMap[status]
		    }
		},
		data() {
			return {
				couponsList: [],
				loading: false,
				loadend: false,
				loadTitle: '加载更多',//提示语
				page: 1,
				limit: 20,
				navOn: 'usable',
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false //是否隐藏授权
			};
		},
		computed: mapGetters(['isLogin']),
		watch: {
			isLogin: {
				handler: function(newV, oldV) {
					if (newV) {
						this.getUseCoupons();
					}
				},
				deep: true
			}
		},
		onLoad() {
			if (this.isLogin) {
				this.getUseCoupons();
			} else {
				toLogin();
			}
		},
		methods: {
			/**
			 * 授权回调
			 */
			onLoadFun: function() {
				this.getUseCoupons();
			},
			// 授权关闭
			authColse: function(e) {
				this.isShowAuth = e
			},
			onNav: function(type) {
				this.navOn = type;
				this.couponsList = [];
				this.page = 1;
				this.loadend = false;
				this.getUseCoupons();
			},
			/**
			 * 获取领取优惠券列表
			 */
			getUseCoupons: function() {
				let that = this;
				if(this.loadend) return false;
				if(this.loading) return false;
				getUserCoupons({ page: that.page, limit: that.limit, type: that.navOn}).then(res => {
					let list= res.data ? res.data.list : [],loadend=list.length < that.limit;
					let couponsList = that.$util.SplitArray(list, that.couponsList);
					that.$set(that,'couponsList',couponsList);
					that.loadend = loadend;
					that.loadTitle = loadend ? '我也是有底线的' : '加载更多';
					that.page = that.page + 1;
					that.loading = false;
				}).catch(err=>{
					  that.loading = false;
					  that.loadTitle = '加载更多';
				  });
			}
		},
		/**
		  * 页面上拉触底事件的处理函数
		  */
		 onReachBottom: function () {
		   this.getUseCoupons();
		 }
	}
</script>

<style lang="scss" scoped>
	.navbar {
		position: fixed;
		top: 0;
		left: 0;
		width: 100%;
		height: 106rpx;
		background-color: #FFFFFF;
		z-index: 9;
	
		.item {
			border-top: 5rpx solid transparent;
			border-bottom: 5rpx solid transparent;
			font-size: 30rpx;
			color: #999999;
	
			&.on {
				border-bottom-color: #E93323;
				color: #282828;
			}
		}
	}
	.money {
		display: flex;
		flex-direction: column;
		justify-content: center;
	}

	.pic-num {
		color: #ffffff;
		font-size: 24rpx;
	}
	.coupon-list {
		margin-top: 122rpx;
	}
	.coupon-list .item .text{
		height: 100%;
	}
	.coupon-list .item .text .condition{
		/* display: flex;
		align-items: center; */
	}
	.condition .line-title {
		width: 90rpx;
		height: 40rpx !important;
		line-height: 40rpx !important;
		padding: 2rpx 10rpx;
		-webkit-box-sizing: border-box;
		box-sizing: border-box;
		background: rgba(255, 247, 247, 1);
		border: 1px solid rgba(232, 51, 35, 1);
		opacity: 1;
		border-radius: 20rpx;
		font-size: 18rpx !important;
		color: #e83323;
		margin-right: 12rpx;
	}
	.noCommodity {
		margin-top: 300rpx;
	}
</style>
