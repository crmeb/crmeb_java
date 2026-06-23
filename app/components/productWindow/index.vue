<template>
	<view>
		<view class="product-window"
			:class="(attr.cartAttr === true ? 'on' : '') + ' ' + (iSbnt?'join':'') + ' ' + (iScart?'joinCart':'')">
			<view class="textpic acea-row row-between-wrapper">
				<view class="pictrue" @click="showImg()">
					<image :src="attr.productSelect.image"></image>
				</view>
				<view class="text">
					<view class="line1">
						{{ attr.productSelect.storeName }}
					</view>
					<view class="money">
						<view class="flex align-baseline">
							￥<text class="num">{{ attr.productSelect.price }}</text>
							<view class="flex pl-2" v-if="attr.productSelect.vipPrice && attr.productSelect.vipPrice > 0">
								<image :src="urlDomain+'crmebimage/perset/staticImg/vip_badge.png'" class="vip_icon"></image>
								<text class='vip_money skeleton-rect'>￥{{attr.productSelect.vipPrice}}</text>
							</view>
						</view>
						<view>
							<text class="stock" v-if='isShow'>库存: {{ attr.productSelect.stock }}</text>
							<text class='stock' v-if="limitNum">限量: {{attr.productSelect.quota}}</text>
						</view>
					</view>
				</view>
				<view class="iconfont icon-guanbi" @click="closeAttr"></view>
			</view>
			<view class="rollTop">
				<view class="productWinList">
					<view class="item" v-for="(item, indexw) in attr.productAttr" :key="indexw">
						<view class="flex flex-between-center">
							<view class="title">{{ item.attrName }}</view>
							<!-- 如果传规格图片，会出现规格样式切换选择-->
							<view class="fs-24 text--w111-666 flex-y-center mr-32" v-show="gridShow == 1 && item.isShowImage"
								@tap="toggleGridAttr(0)">
								<text class="iconfont icon-liebiao1 fs-28"></text>
								<text class="pl-6 line-heightOne">列表</text>
							</view>
							<view class="fs-24 text--w111-666 flex-y-center mr-32" v-show="gridShow == 0 && item.isShowImage"
								@tap="toggleGridAttr(1)">
								<text class="iconfont icon-liebiao2 fs-28"></text>
								<text class="pl-6 line-heightOne">宫格</text>
							</view>
						</view>
						<!-- 大图样式 isShowImage规格图是否展示-->
						<view class="pl-32 mt-32" v-show="gridShow == 1 && item.isShowImage">
							<scroll-view scroll-x="true" class="white-nowrap vertical-middle w-686" show-scrollbar="false">
								<view class="flex">
									<view class="inline-block mr-12" v-for="(itemn, indexn) in item.optionList" :key="indexn"
										@click="tapAttr(indexw, indexn)">
										<view class="grid-item-box"
											:class="item.index === itemn.value ? isMarketingGoods?'red-active':'grid-active' : ''">
											<view class="w-full h-192 relative">
												<image class="w-full h-192 block" :src="itemn.image" mode="aspectFill"></image>
												<view class="proview-icon flex-center" @tap.stop="proviewImg(itemn.image)">
													<text class="iconfont icon-zhankai fs-24 text--w111-fff"></text>
												</view>
											</view>
											<view class="flex-1 bg--w111-f5f5f5 tname text-center flex-center fs-24">{{itemn.value}}
											</view>
										</view>
									</view>
								</view>
							</scroll-view>
						</view>
						<!-- 小图样式 -->
						<view v-show="(gridShow == 0 && item.isShowImage) || !item.isShowImage" class="listn acea-row row-middle">
							<view class="itemn flex"
								:class="item.index === itemn.value ? 'on' : ''"
								v-for="(itemn, indexn) in item.optionList" @click="tapAttr(indexw, indexn,item)" :key="indexn">
								<image v-if="itemn.image && item.isShowImage" class="attr-img" :src="itemn.image" alt="" srcset="" />
								<text class="option-name">{{ itemn.value }}</text>
							</view>
						</view>
						<!-- 旧商品无optionList -->
						<view v-if="item.optionList && item.optionList.length == 0" class="listn acea-row row-middle">
							<view class="itemn old-padding" :class="item.index === itemn ? 'on' : ''" v-for="(itemn, indexn) in item.attrValues"
								@click="tapAttr(indexw, indexn)" :key="indexn">
								{{ itemn }}
							</view>
						</view>
					</view>
				</view>
				<view class="cart acea-row row-between-wrapper">
					<view class="title">数量</view>
					<view class="carnum acea-row row-left">
						<view class="item reduce" :class="attr.productSelect.cart_num <= 1 ? 'on' : ''" @click="CartNumDes">
							-
						</view>
						<view class='item num'>
							<input type="number" v-model="attr.productSelect.cart_num" data-name="productSelect.cart_num"
								@input="bindCode(attr.productSelect.cart_num)" maxlength="3"></input>
						</view>
						<view v-if="iSplus" class="item plus"
							:class="attr.productSelect.cart_num >= attr.productSelect.stock? 'on': ''" @click="CartNumAdd">
							+
						</view>
						<view v-else class='item plus'
							:class='(attr.productSelect.cart_num === onceNum) || (attr.productSelect.cart_num >= attr.productSelect.quota) || (attr.productSelect.cart_num >= attr.productSelect.stock) || (attr.productSelect.cart_num >= attr.productSelect.num)? "on":""'
							@click='CartNumAdd'>+</view>
					</view>
				</view>
			</view>
			<view class="joinBnt bg_color" v-if="iSbnt && attr.productSelect.stock>0 &&attr.productSelect.quota>0"
				@click="goCat">我要参团</view>
			<view class="joinBnt on"
				v-else-if="(iSbnt && attr.productSelect.quota<=0)||(iSbnt &&attr.productSelect.stock<=0)">已售罄</view>
			<view class="joinBnt bg_color" v-if="iScart && attr.productSelect.stock" @click="goCat">确定</view>
			<!-- <view class="joinBnt bg-color" v-if="iSbnt && attr.productSelect.stock && attr.productSelect.quota" @click="goCat">确定</view> -->
			<view class="joinBnt on" v-else-if="(iScart && !attr.productSelect.stock)">已售罄</view>
		</view>
		<view class="mask" @touchmove.prevent :hidden="attr.cartAttr === false" @click="closeAttr"></view>
	</view>
</template>

<script>
	export default {
		props: {
			attr: {
				type: Object,
				default: () => {}
			},
			//一次最多可买几个，活动商品中使用
			onceNum: {
				type: Number,
				value: 1
			},
			limitNum: {
				type: Number,
				value: 0
			},
			isShow: {
				type: Number,
				value: 0
			},
			iSbnt: {
				type: Number,
				value: 0
			},
			iSplus: {
				type: Number,
				value: 0
			},
			iScart: {
				type: Number,
				value: 0
			},
			// 是否是拼团、秒杀、积分商品，用红色不用主题色
			isMarketingGoods: {
			  type: Boolean,
			  default: () => false
			}
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				gridShow: 1, //宫格还是列样式选择
			};
		},
		created() {
		},
		methods: {
			goCat: function() {
				this.$emit('goCat');
			},
			/**
			 * 购物车手动输入数量
			 * 
			 */
			bindCode: function(e) {
				this.$emit('iptCartNum', this.attr.productSelect.cart_num);
			},
			closeAttr: function() {
				this.$emit('myevent');
			},
			CartNumDes: function() {
				this.$emit('ChangeCartNum', false);
			},
			CartNumAdd: function() {
				this.$emit('ChangeCartNum', true);
			},
			tapAttr: function(indexw, indexn) {
				let that = this;
				that.$emit("attrVal", {
					indexw: indexw,
					indexn: indexn
				});
				this.$set(this.attr.productAttr[indexw], 'index', this.attr.productAttr[indexw].attrValues[indexn]);
				let value = that.getCheckedValue().join(",");
				that.$emit("ChangeAttr", value);
			},
			//获取被选中属性；
			getCheckedValue: function() {
				let productAttr = this.attr.productAttr;
				let value = [];
				for (let i = 0; i < productAttr.length; i++) {
					for (let j = 0; j < productAttr[i].attrValues.length; j++) {
						if (productAttr[i].index === productAttr[i].attrValues[j]) {
							value.push(productAttr[i].attrValues[j]);
						}
					}
				}
				return value;
			},
			showImg() {
				this.$emit('getImg');
			},
			//选择规格值样式
			toggleGridAttr(type) {
				this.gridShow = type;
			},
			// 点击贵供图查看图片
			proviewImg(img){
			  uni.previewImage({
			    current: 0,
			    urls: [img]
			  });
			},
		}
	}
</script>

<style scoped lang="scss">
	.mask {
		z-index: 100;
	}

	.product-window {
		position: fixed;
		bottom: 0;
		width: 100%;
		left: 0;
		background-color: #fff;
		z-index: 101;
		border-radius: 16rpx 16rpx 0 0;
		padding-bottom: 100rpx;
		padding-bottom: calc(env(safe-area-inset-bottom) + 100rpx);
		transform: translate3d(0, 100%, 0);
		transition: all .2s cubic-bezier(0, 0, .25, 1);
	}

	.product-window.on {
		transform: translate3d(0, 0, 0);
	}

	.product-window.join {
		padding-bottom: 30rpx;
	}

	.product-window.joinCart {
		padding-bottom: 30rpx;
		z-index: 999;
	}

	.product-window .textpic {
		padding: 0 130rpx 0 30rpx;
		margin-top: 29rpx;
		position: relative;
	}

	.product-window .textpic .pictrue {
		width: 150rpx;
		height: 150rpx;
	}

	.product-window .textpic .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 10rpx;
	}

	.product-window .textpic .text {
		width: 410rpx;
		font-size: 32rpx;
		color: #333333;
	}

	.product-window .textpic .text .money {
		font-size: 24rpx;
		margin-top: 23rpx;
		@include price_color(theme);
	}

	.product-window .textpic .text .money .num {
		font-family: PingFang SC;
		font-size: 36rpx;
		font-weight: 600;
	}

	.product-window .textpic .text .money .stock {
		color: #999;
	}

	.product-window .textpic .iconfont {
		position: absolute;
		right: 30rpx;
		top: -5rpx;
		font-size: 35rpx;
		color: #8a8a8a;
	}

	.product-window .rollTop {
		max-height: 62vh;
		overflow: auto;
		margin-top: 36rpx;
	}

	.product-window .productWinList .item~.item {
		margin-top: 36rpx;
	}

	.product-window .productWinList .item .title {
		font-size: 30rpx;
		color: #999;
		padding: 0 30rpx;
	}

	.product-window .productWinList .item .listn {
		padding: 0 30rpx 0 16rpx;
	}

	.product-window .productWinList .item .listn .itemn {
		border: 1px solid #F2F2F2;
		font-size: 26rpx;
		color: #282828;
		display: flex;
		align-items: center;
		border-radius: 8rpx;
		margin: 20rpx 0 0 14rpx;
		background-color: #F2F2F2;
		min-height: 56rpx;

		.attr-img {
			margin: 0 -8rpx 0 4rpx;
		}

		.option-name {
			margin: 4rpx 12rpx 4rpx;
		}
	}

	.product-window .productWinList .item .listn .itemn.on {
		@include main_color(theme);
		@include coupons_border_color(theme);
		@include cate-two-btn(theme);
	}

	.product-window .productWinList .item .listn .itemn.limit {
		color: #999;
		text-decoration: line-through;
	}

	.product-window .cart {
		margin-top: 50rpx;
		margin-bottom: 30rpx;
		padding: 0 30rpx;
	}

	.product-window .cart .title {
		font-size: 30rpx;
		color: #999;
	}

	.product-window .cart .carnum {
		height: 54rpx;
	}

	.product-window .cart .carnum view {
		// border: 1px solid #a4a4a4;
		width: 84rpx;
		text-align: center;
		height: 100%;
		line-height: 54rpx;
		color: #282828;
		font-size: 45rpx;
	}

	.product-window .cart .carnum .reduce {
		border-right: 0;
		border-radius: 6rpx 0 0 6rpx;
		line-height: 48rpx;
	}

	.product-window .cart .carnum .reduce.on {
		// border-color: #e3e3e3;
		color: #DEDEDE;
		font-size: 44rpx;
	}

	.product-window .cart .carnum .plus {
		border-left: 0;
		border-radius: 0 6rpx 6rpx 0;
		line-height: 46rpx;
	}

	.product-window .cart .carnum .plus.on {
		border-color: #e3e3e3;
		color: #dedede;
	}

	.product-window .cart .carnum .num {
		background: rgba(242, 242, 242, 1);
		color: #282828;
		font-size: 28rpx;
		border-radius: 12rpx;
		line-height: 29px;
		height: 54rpx;

		input {
			display: -webkit-inline-box;
		}
	}

	.product-window .joinBnt {
		font-size: 30rpx;
		width: 620rpx;
		height: 86rpx;
		border-radius: 50rpx;
		text-align: center;
		line-height: 86rpx;
		color: #fff;
		margin: 21rpx auto 0 auto;
	}

	.align-baseline {
		align-items: baseline;
	}

	.bg_color {
		@include main-bg_color(theme);
	}

	.product-window .joinBnt.on {
		background-color: #bbb;
		color: #fff;
	}

	.align-center {
		align-items: center;
	}

	.vip_icon {
		width: 44rpx;
		height: 28rpx;
	}

	.vip_money {
		background: #FFE7B9;
		border-radius: 4px;
		font-size: 22rpx;
		color: #333;
		line-height: 28rpx;
		text-align: center;
		padding: 0 6rpx;
		box-sizing: border-box;
		margin-left: -4rpx;
	}

	.pl-2 {
		padding-left: 20rpx;
	}

	.grid-item-box {
		width: 196rpx;
		border: 2rpx solid #F5F5F5;
		border-radius: 8rpx;
		overflow: hidden;

		image {
			border-radius: 8rpx 8rpx 0 0;
		}

		.tname {
			width: 196rpx;
			border-radius: 0 0 8rpx 8rpx;
			padding: 4rpx 12rpx;
			height: 74rpx;
			line-height: 37rpx;
			display: -webkit-box;
			/* 旧版弹性盒子 */
			overflow: hidden;
			text-overflow: ellipsis;
			-webkit-line-clamp: 2;
			/* 显示两行 */
			-webkit-box-orient: vertical;
			/* 垂直方向排列 */
			word-break: break-all;
			/* 允许单词断行 */
			white-space: normal;
			/* 允许文字折行 */
		}
	}
	.attr-img {
		width: 48rpx;
		height: 48rpx;
		margin-right: 12rpx;
		border-radius: 8rpx;
	}
	.groupOn {
		background: #FCEAE9 !important;
		border: 1px solid #E93323 !important;
		color: #E93323 !important;
	}
	.grid-active{
	  @include coupons_border_color(theme);
	  .tname{
	    @include main_color(theme);
	    @include coupons_light_color(theme);
	  }
	}
	.proview-icon{
	  position: absolute;
	  top: 8rpx;
	  right: 8rpx;
	  width: 36rpx;
	  height: 36rpx;
	  background: rgba(51, 51, 51, 0.15);
	  border-radius: 50%;
	}
	.old-padding {
		padding: 2px 6px 2px;
	}
</style>