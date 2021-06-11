<template>
	<view>
		<block v-if="bargain.length>0">
			<div class="bargain-record pad30" ref="container">
				<div class="item borRadius14" v-for="(item, index) in bargain" :key="index">
					<div class="picTxt acea-row row-between-wrapper">
						<div class="pictrue">
							<image :src="item.image" />
						</div>
						<div class="text acea-row row-column-around">
							<div class="line1" style="width: 100%;">{{ item.title }}</div>
							<count-down :justify-left="'justify-content:left'" :bgColor="bgColor" :is-day="true" :tip-text="'倒计时 '" :day-text="'天'"
							 :hour-text="' 时 '" :minute-text="' 分 '"
							 :second-text="' 秒 '" :datatime="item.stopTime/1000" v-if="item.status === 1"></count-down>
							<div class="successTxt font-color-red" v-else-if="item.status === 3">砍价成功</div>
							<div class="endTxt" v-else>活动已结束</div>
							<div class="money">
								已砍至<span class="symbol font-color-red">￥</span><span class="num font-color-red">{{ item.surplusPrice }}</span>
							</div>
						</div>
					</div>
					<div class="bottom acea-row row-between-wrapper">
						<div class="purple" v-if="item.status === 1">活动进行中</div>
						<div class="success" v-else-if="item.status === 3">砍价成功</div>
						<div class="end" v-else>活动已结束</div>
						<div class="acea-row row-middle row-right">
							<div class="bnt bg-color-red" v-if="item.status === 3 && !item.isOrder" @click="goConfirm(item)">
								去付款
							</div>
							<div class="bnt bg-color-red" v-if="item.status === 3 && !item.isDel && item.isOrder && !item.isPay" @click="goPay(item.surplusPrice,item.orderNo)">
								立即付款
							</div>
							<div class="bnt bg-color-red" v-if="item.status === 1" @click="goDetail(item.id)">
								继续砍价
							</div>
							<div class="bnt bg-color-red" v-if="item.status === 2" @click="goList">重开一个</div>
						</div>
					</div>
				</div>
				<Loading :loaded="status" :loading="loadingList"></Loading>
			</div>
		</block>
		<block v-if="bargain.length == 0">
			<emptyPage title="暂无砍价记录～"></emptyPage>
		</block>
		<home></home>
		<payment :payMode='payMode' :pay_close="pay_close" @onChangeFun='onChangeFun' :order_id="pay_order_id" :totalPrice='totalPrice'></payment>
	</view>
</template>
<script>
	import CountDown from "@/components/countDown";
	import emptyPage from '@/components/emptyPage.vue'
	import {
		getBargainUserList,
		getBargainUserCancel
	} from "@/api/activity";
	import Loading from "@/components/Loading";
	import home from '@/components/home';
	import payment from '@/components/payment';
	import {
		mapGetters
	} from "vuex";
	export default {
		name: "BargainRecord",
		components: {
			CountDown,
			Loading,
			emptyPage,
			home,
			payment
		},
		props: {},
		computed: mapGetters(['isLogin', 'userInfo', 'uid']),
		data: function() {
			return {
				bgColor:{
					'bgColor': '',
					'Color': '#E93323',
					'width': '40rpx',
					'timeTxtwidth': '28rpx',
					'isDay': false
				},
				bargain: [],
				status: false, //砍价列表是否获取完成 false 未完成 true 完成
				loadingList: false, //当前接口是否请求完成 false 完成 true 未完成
				page: 1, //页码
				limit: 20, //数量
				payMode: [{
						name: "微信支付",
						icon: "icon-weixinzhifu",
						value: 'weixin',
						title: '微信快捷支付'
					},
					{
						name: "余额支付",
						icon: "icon-yuezhifu",
						value: 'yue',
						title: '可用余额:',
						number: 0
					}
				],
				pay_close: false,
				pay_order_id: '',
				totalPrice: '0'
			};
		},
		onLoad: function() {
			this.getBargainUserList();
			// this.$scroll(this.$refs.container, () => {
			//   !this.loadingList && this.getBargainUserList();
			// });
		},
		onShow() {
			if (this.isLogin) {
				this.payMode[1].number = this.userInfo.nowMoney;
				this.$set(this, 'payMode', this.payMode);
			} else {
				toLogin();
			}
		},
		methods: {
			/**
			 * 打开支付组件
			 * 
			 */
			goPay(pay_price, order_id) {
				this.$set(this, 'pay_close', true);
				this.$set(this, 'pay_order_id', order_id);
				this.$set(this, 'totalPrice', pay_price);
			},
			/**
			 * 事件回调
			 * 
			 */
			onChangeFun: function(e) {
				let opt = e;
				let action = opt.action || null;
				let value = opt.value != undefined ? opt.value : null;
				(action && this[action]) && this[action](value);
			},
			/**
			 * 关闭支付组件
			 * 
			 */
			payClose: function() {
				this.pay_close = false;
			},
			/**
			 * 支付成功回调
			 * 
			 */
			pay_complete: function() {
				this.status = false;
				this.page = 1;
				this.$set(this, 'bargain', []);
				this.$set(this, 'pay_close', false);
				this.getBargainUserList();
			},
			/**
			 * 支付失败回调
			 * 
			 */
			pay_fail: function() {
				this.pay_close = false;
			},
			goConfirm: function(item) { //立即支付
				if (this.isLogin === false) {
					toLogin();
				} else {
					uni.navigateTo({
						url: `/pages/activity/goods_bargain_details/index?id=${item.id}&startBargainUid=${this.uid}&storeBargainId=${item.bargainUserId}`
					})
				}
			},
			goDetail: function(id) {
				uni.navigateTo({
					url: `/pages/activity/goods_bargain_details/index?id=${id}&startBargainUid=${this.uid}`
				})
			},
			// 砍价列表
			goList: function() {
				uni.navigateTo({
					url: '/pages/activity/goods_bargain/index'
				})
			},
			getBargainUserList: function() {
				var that = this;
				if (that.loadingList) return;
				if (that.status) return;
				getBargainUserList({
						page: that.page,
						limit: that.limit
					})
					.then(res => {
						that.status = res.data.list.length < that.limit;
						that.bargain.push.apply(that.bargain, res.data.list);
						that.page++;
						that.loadingList = false;
					})
					.catch(res => {
						that.$dialog.error(res);
					});
			},
			getBargainUserCancel: function(bargainId) {
				var that = this;
				getBargainUserCancel({
						bargainId: bargainId
					})
					.then(res => {
						that.status = false;
						that.loadingList = false;
						that.page = 1;
						that.bargain = [];
						that.getBargainUserList();
						that.$util.Tips({
							title: res
						})
					})
					.catch(res => {
						that.$util.Tips({
							title: res
						})
					});
			}
		},
		onReachBottom() {
			this.getBargainUserList();
		}
	};
</script>

<style lang="scss" scoped>
	/*砍价记录*/
	.bargain-record .item .picTxt .text .time {
		height: 36rpx;
		line-height: 36rpx;
		.styleAll {
			color: #fc4141;
			font-size:24rpx;
		}
	}
	.bargain-record .item .picTxt .text .time .red {
		color: #999;
		font-size:24rpx;
	}
	.bargain-record .item {
		background-color: #fff;
		margin-top: 15rpx;
		padding: 30rpx 24rpx 0 24rpx;
	}

	.bargain-record .item .picTxt {
		border-bottom: 1px solid #f0f0f0;
		padding-bottom: 30rpx;
	}

	.bargain-record .item .picTxt .pictrue {
		width: 150upx;
		height: 150upx;
	}

	.bargain-record .item .picTxt .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 6upx;
	}

	.bargain-record .item .picTxt .text {
		width: 470rpx;
		font-size: 30upx;
		color: #333333;
		height: 160rpx;
	}

	.bargain-record .item .picTxt .text .time {
		font-size: 24upx;
		color: #868686;
		justify-content: left !important;
	}
	
	.bargain-record .item .picTxt .text .successTxt{
		font-size:24rpx;
	}
	
	.bargain-record .item .picTxt .text .endTxt{
		font-size:24rpx;
		color: #999;
	}
	.bargain-record .item .picTxt .text .money {
		font-size: 24upx;
		color: #999999;
	}

	.bargain-record .item .picTxt .text .money .num {
		font-size: 32upx;
		font-weight: bold;
	}

	.bargain-record .item .picTxt .text .money .symbol {
		font-weight: bold;
	}

	.bargain-record .item .bottom {
		height: 100upx;
		font-size: 27upx;
	}

	.bargain-record .item .bottom .purple {
		color: #f78513;
	}

	.bargain-record .item .bottom .end {
		color: #999;
	}

	.bargain-record .item .bottom .success {
		color: $theme-color;
	}

	.bargain-record .item .bottom .bnt {
		font-size: 27upx;
		color: #fff;
		width: 176upx;
		height: 60upx;
		border-radius: 32upx;
		text-align: center;
		line-height: 60upx;
	}

	.bargain-record .item .bottom .bnt.cancel {
		color: #aaa;
		border: 1px solid #ddd;
	}

	.bargain-record .item .bottom .bnt~.bnt {
		margin-left: 18upx;
	}
</style>
