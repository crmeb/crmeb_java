<template>
	<view class="orderGoods">
		<view class='total'>共{{totalNmu}}件商品</view>
		<view class='goodWrapper'>
			<view class='item acea-row row-between-wrapper' v-for="(item,index) in cartInfo" :key="index" @click="jumpCon(item.productId)">
				<view class='pictrue'>
					<image :src='item.productInfo.attrInfo.image' v-if="item.productInfo.attrInfo"></image>
					<image :src='item.productInfo.image' v-else></image>
				</view>
				<view class='text'>
					<view class='acea-row row-between-wrapper'>
						<view class='name line1'>{{item.productInfo.storeName}}</view>
						<view class='num'>x {{item.cartNum}}</view>
					</view>
					<view class='attr line1' v-if="item.productInfo.attrInfo">{{item.productInfo.attrInfo.suk}}</view>
					<view class='money font-color' v-if="item.productInfo.attrInfo">￥{{item.productInfo.attrInfo.price}}</view>
					<view class='money font-color' v-else>￥{{item.productInfo.price}}</view>
					<view class='evaluate' v-if='item.isReply==0 && evaluate==3' @click.stop="evaluateTap(item.productAttrUnique,orderId,ids)">评价</view>
					<view class='evaluate' v-else-if="item.isReply==1 && evaluate==3">已评价</view>
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
			}
		},
		data() {
			return {
				totalNmu:''
			};
		},
		watch:{
			cartInfo:function(nVal,oVal){
				let num = 0
				nVal.forEach((item,index)=>{
					num += item.cartNum
				})
				this.totalNmu = num
			}
		},
		methods: {
			evaluateTap:function(unique,orderId,ids){
				uni.navigateTo({
					url:"/pages/users/goods_comment_con/index?unique="+unique+"&uni="+orderId + "&id=" + ids
				})
			},
			jumpCon:function(id){
				if(this.jump){
					uni.navigateTo({
						url: `/pages/goods_details/index?id=${id}`
					})
				}
			}
		}
	}
</script>

<style scoped lang="scss">
	.orderGoods {
		background-color: #fff;
		margin-top: 12rpx;
	}

	.orderGoods .total {
		width: 100%;
		height: 86rpx;
		padding: 0 30rpx;
		border-bottom: 2rpx solid #f0f0f0;
		font-size: 30rpx;
		color: #282828;
		line-height: 86rpx;
		box-sizing: border-box;
	}
	.pictrue image{
		background: #f4f4f4;
	}
</style>
