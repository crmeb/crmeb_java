<template>
	<view>
		<view class='logistics'>
			<view class='header acea-row row-between row-top'>
				<view class='pictrue'>
					<image :src='product.productImg'></image>
				</view>
				<view class='text acea-row row-between'>
					<view class='name line2'>{{product.productName}}</view>
					<view class='money'>
						<view>￥{{product.price}}</view>
						<view>x{{product.payNum}}</view>
					</view>
				</view>
			</view>
			<view class='logisticsCon'>
				<view class='company acea-row row-between-wrapper'>
					<view class='picTxt acea-row row-between-wrapper'>
						<view class='iconfont icon-wuliu'></view>
						<view class='text'>
							<view><text class='name line1'>物流公司：</text> {{orderInfo.deliveryName}}</view>
							<view class='express line1'><text class='name'>快递单号：</text> {{orderInfo.deliveryId}}</view>
						</view>
					</view>
					<!-- #ifndef H5 -->
					<view class='copy' @tap='copyOrderId'>复制单号</view>
					<!-- #endif -->
					<!-- #ifdef H5 -->
					<view class='copy copy-data' :data-clipboard-text="orderInfo.deliveryId">复制单号</view>
					<!-- #endif -->
				</view>
				<view class='item' v-for="(item,index) in expressList" :key="index">
					<view class='circular' :class='index === 0 ? "on":""'></view>
					<view class='text' :class='index===0 ? "on-font on":""'>
						<view>{{item.status}}</view>
						<view class='data' :class='index===0 ? "on-font on":""'>{{item.time}}</view>
					</view>
				</view>
			</view>
			<recommend :hostProduct='hostProduct' v-if="hostProduct.length"></recommend>
		</view>
		<!-- #ifdef MP -->
		<!-- <authorize :isAuto="isAuto" :isShowAuth="isShowAuth" @authColse="authColse"></authorize> -->
		<!-- #endif -->
	</view>
</template>

<script>
	import {
		express
	} from '@/api/order.js';
	import {
		getProductHot
	} from '@/api/store.js';
	import ClipboardJS from "@/plugin/clipboard/clipboard.js";
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import recommend from '@/components/recommend';
	// #ifdef MP
	import authorize from '@/components/Authorize';
	// #endif
	export default {
		components: {
			recommend,
			// #ifdef MP
			authorize
			// #endif
		},
		data() {
			return {
				orderId: '',
				product: {
					productInfo: {}
				},
				orderInfo: {},
				expressList: [],
				hostProduct: [],
				loading: false,
				goodScroll: true,
				params: { //精品推荐分页
					page: 1,
					limit: 10,
				},
			};
		},
		computed: mapGetters(['isLogin']),
		watch:{
			isLogin:{
				handler:function(newV,oldV){
					if(newV){
						this.getExpress();
						this.get_host_product();
					}
				},
				deep:true
			}
		},
		onLoad: function (options) {
		    if (!options.orderId) return this.$util.Tips({title:'缺少订单号'});
			this.orderId = options.orderId;
			if (this.isLogin) {
				this.getExpress();
				this.get_host_product();
			} else {
				toLogin();
			}
		  },
		  onReady: function() {
		  	// #ifdef H5
		  	this.$nextTick(function() {
		  		const clipboard = new ClipboardJS(".copy-data");
		  		clipboard.on("success", () => {
		  			this.$util.Tips({
		  				title: '复制成功'
		  			});
		  		});
		  	});
		  	// #endif
		  },
		methods: {
			/**
			 * 授权回调
			 */
			onLoadFun: function() {
				this.getExpress();
				this.get_host_product();
			},
			copyOrderId:function(){
			    uni.setClipboardData({ data: this.orderInfo.deliveryId });
			  },
			  getExpress:function(){
			    let that=this;
			    express(that.orderId).then(function(res){
			      let result = res.data.express|| {};
				  that.$set(that,'product',res.data.order.info[0] || {});
				  that.$set(that,'orderInfo',res.data.order);
				  that.$set(that,'expressList',result.list || []);
			    });
			  },
			  get_host_product: function () {
				  	this.loading = true
				  	if (!this.goodScroll) return
			    let that = this;
			    getProductHot(that.params.page,that.params.limit).then(function (res) {
							//this.iSshowH = false
							that.loading = false
							that.goodScroll = res.data.list.length >= that.params.limit
							that.params.page++
							that.hostProduct = that.hostProduct.concat(res.data.list)
			    });
			  },
		},
		// 滚动到底部
		onReachBottom() {
		
			if (this.params.page != 1) {
				this.get_host_product();
			}
		},
	}
</script>

<style scoped lang="scss">
	.logistics .header {
		padding: 23rpx 30rpx;
		background-color: #fff;
		height: 166rpx;
		box-sizing: border-box;
	}

	.logistics .header .pictrue {
		width: 120rpx;
		height: 120rpx;
	}

	.logistics .header .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 6rpx;
	}

	.logistics .header .text {
		width: 540rpx;
		font-size: 28rpx;
		color: #999;
		margin-top: 6rpx;
	}

	.logistics .header .text .name {
		width: 365rpx;
		color: #282828;
	}

	.logistics .header .text .money {
		text-align: right;
	}

	.logistics .logisticsCon {
		background-color: #fff;
		margin: 12rpx 0;
	}

	.logistics .logisticsCon .company {
		height: 120rpx;
		margin: 0 0 45rpx 30rpx;
		padding-right: 30rpx;
		border-bottom: 1rpx solid #f5f5f5;
	}

	.logistics .logisticsCon .company .picTxt {
		width: 520rpx;
	}

	.logistics .logisticsCon .company .picTxt .iconfont {
		width: 50rpx;
		height: 50rpx;
		background-color: #666;
		text-align: center;
		line-height: 50rpx;
		color: #fff;
		font-size: 35rpx;
	}

	.logistics .logisticsCon .company .picTxt .text {
		width: 450rpx;
		font-size: 26rpx;
		color: #282828;
	}

	.logistics .logisticsCon .company .picTxt .text .name {
		color: #999;
	}

	.logistics .logisticsCon .company .picTxt .text .express {
		margin-top: 5rpx;
	}

	.logistics .logisticsCon .company .copy {
		font-size: 20rpx;
		width: 106rpx;
		height: 40rpx;
		text-align: center;
		line-height: 40rpx;
		border-radius: 20rpx;
		border: 1rpx solid #999;
	}

	.logistics .logisticsCon .item {
		padding: 0 40rpx;
		position: relative;
	}

	.logistics .logisticsCon .item .circular {
		width: 20rpx;
		height: 20rpx;
		border-radius: 50%;
		position: absolute;
		top: -1rpx;
		left: 31.5rpx;
		background-color: #ddd;
	}

	.logistics .logisticsCon .item .circular.on {
		background-color: $theme-color;
	}

	.logistics .logisticsCon .item .text.on-font {
		color: $theme-color;
	}

	.logistics .logisticsCon .item .text .data.on-font {
		color: $theme-color;
	}

	.logistics .logisticsCon .item .text {
		font-size: 26rpx;
		color: #666;
		width: 615rpx;
		border-left: 1rpx solid #e6e6e6;
		padding: 0 0 60rpx 38rpx;
	}

	.logistics .logisticsCon .item .text.on {
		border-left-color: #f8c1bd;
	}

	.logistics .logisticsCon .item .text .data {
		font-size: 24rpx;
		color: #999;
		margin-top: 10rpx;
	}

	.logistics .logisticsCon .item .text .data .time {
		margin-left: 15rpx;
	}
</style>
