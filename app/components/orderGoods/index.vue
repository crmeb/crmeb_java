<template>
	<view class="orderGoods borRadius14">
		<view class='total'>共{{ orderProNum?orderProNum:totalNmu}}件商品</view>
		<view class='goodWrapper pad30'>
			<view class='item acea-row row-between-wrapper' v-for="(item,index) in cartInfo" :key="index"
				@click="jumpCon(item.productId)">
				<view class='pictrue'>
					<image :src='item.image'></image>
				</view>
				<view class='text'>
					<view class='acea-row row-between-wrapper'>
						<view class='name line1'>{{item.productName ? item.productName : item.storeName}}</view>
						<view class='num'>x {{item.payNum ? item.payNum : item.cartNum}}</view>
					</view>
					<view class='attr line1' v-if="item.sku">{{item.sku}}</view>
					<view class='money font-color'>￥{{item.price}}</view>
					<view class='evaluate' v-if='item.isReply==0 && evaluate==2' @click.stop="evaluateTap(item)">评价
					</view>
					<view class='evaluate' v-else-if="item.isReply==1">已评价</view>
				</view>
			</view>
		</view>
	</view>

</template>

<script>
	export default {
		props: {
			evaluate: {
				type: Number,
				default: 0,
			},
			cartInfo: {
				type: Array,
				default: function() {
					return [];
				}
			},
			orderId: {
				type: String,
				default: '',
			},
			ids: {
				type: Number,
				default: 0,
			},
			jump: {
				type: Boolean,
				default: false,
			},
			orderProNum: {
				type: Number,
				default: function() {
					return 0;
				}
			},
			productType: {
				type: Number,
				default: function() {
					return 0;
				}
			}
		},
		data() {
			return {
				totalNmu: ''
			};
		},
		watch: {
			cartInfo: function(nVal, oVal) {
				let num = 0
				nVal.forEach((item, index) => {
					num += item.cartNum
				})
				this.totalNmu = num
			}
		},
		methods: {
			evaluateTap(item) {
				uni.navigateTo({
					url: "/pages/users/goods_comment_con/index?unique=" + item.attrId + "&orderId=" + this.orderId + '&id=' + this.ids
				})
			},
			jumpCon: function(id) {
				let type = this.productType==0?'normal':'video'
				if (this.jump) {
					uni.navigateTo({
						url: `/pages/goods_details/index?id=${id}&type=${type}`
					})
				}
			}
		}
	}
</script>

<style scoped lang="scss">
	.orderGoods {
		background-color: #fff;
		margin-top: 15rpx;
	}

	.orderGoods .total {
		width: 100%;
		height: 86rpx;
		padding: 0 24rpx;
		border-bottom: 2rpx solid #f0f0f0;
		font-size: 30rpx;
		color: #282828;
		line-height: 86rpx;
		box-sizing: border-box;
	}

	.pictrue image {
		background: #f4f4f4;
	}
</style>
