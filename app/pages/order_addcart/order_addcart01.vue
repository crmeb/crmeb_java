<template>
	<view>
		<view class='shoppingCart'>
			<view class='labelNav acea-row row-around row-middle'>
				<view class='item'><text class='iconfont icon-xuanzhong'></text>100%正品保证</view>
				<view class='item'><text class='iconfont icon-xuanzhong'></text>所有商品精挑细选</view>
				<view class='item'><text class='iconfont icon-xuanzhong'></text>售后无忧</view>
			</view>
			<view class='nav acea-row row-between-wrapper'>
				<view>购物数量 <text class='num font-color'>{{cartCount}}</text></view>
				<view v-if="cartList.valid.length > 0 || cartList.invalid.length > 0" class='administrate acea-row row-center-wrapper'
				 @click='manage'>{{ footerswitch ? '管理' : '取消'}}</view>
			</view>
			<view v-if="cartList.valid.length > 0 || cartList.invalid.length > 0">
				<view class='list'>
					<checkbox-group @change="checkboxChange">
						<block v-for="(item,index) in cartList.valid" :key="index">
							<view class='item acea-row row-between-wrapper'>
								<!-- #ifndef MP -->
								<checkbox :value="(item.id).toString()" :checked="item.checked" :disabled="!item.attrStatus && footerswitch" />
								<!-- <checkbox :value="(item.id).toString()" :checked="item.checked" /> -->
								<!-- #endif -->
								<!-- #ifdef MP -->
								<checkbox :value="item.id" :checked="item.checked" :disabled="!item.attrStatus && footerswitch" />
								<!-- <checkbox :value="item.id" :checked="item.checked" /> -->
								<!-- #endif -->
								<navigator :url='"/pages/goods_details/index?id="+item.product_id' hover-class='none' class='picTxt acea-row row-between-wrapper'>
									<view class='pictrue'>
										<image v-if="item.productInfo.attrInfo" :src='item.productInfo.attrInfo.image'></image>
										<image v-else :src='item.productInfo.image'></image>
									</view>
									<view class='text'>
										<view class='line1' :class="item.attrStatus?'':'reColor'">{{item.productInfo.store_name}}</view>
										<view class='infor line1' v-if="item.productInfo.attrInfo">属性：{{item.productInfo.attrInfo.suk}}</view>
										<view class='money' v-if="item.attrStatus">￥{{item.truePrice}}</view>
										<view class="reElection acea-row row-between-wrapper" v-else>
											<view class="title">请重新选择商品规格</view>
											<view class="reBnt cart-color acea-row row-center-wrapper" @click.stop="reElection(item)">重选</view>
										</view>
									</view>
									<view class='carnum acea-row row-center-wrapper' v-if="item.attrStatus">
										<view class="reduce" :class="item.numSub ? 'on' : ''" @click.stop='subCart(index)'>-</view>
										<view class='num'>{{item.cart_num}}</view>
										<!-- <view class="num">
											<input type="number" v-model="item.cart_num" @click.stop @input="iptCartNum(index)" @blur="blurInput(index)"/>
										</view> -->
										<view class="plus" :class="item.numAdd ? 'on' : ''" @click.stop='addCart(index)'>+</view>
									</view>
								</navigator>
								<!-- <navigator :url='"/pages/goods_details/index?id="+item.product_id' hover-class='none' class='picTxt acea-row row-between-wrapper'>
									<view class='pictrue'>
										<image v-if="item.productInfo.attrInfo" :src='item.productInfo.attrInfo.image'></image>
										<image v-else :src='item.productInfo.image'></image>
									</view>
									<view class='text'>
										<view class='line1'>{{item.productInfo.store_name}}</view>
										<view class='infor line1' v-if="item.productInfo.attrInfo">属性：{{item.productInfo.attrInfo.suk}}</view>
										<view class='money'>￥{{item.truePrice}}</view>
									</view>
									<view class='carnum acea-row row-center-wrapper'>
										<view class="reduce" :class="item.numSub ? 'on' : ''" @click.stop='subCart(index)'>-</view>
										<view class='num'>{{item.cart_num}}</view>
										<view class="plus" :class="item.numAdd ? 'on' : ''" @click.stop='addCart(index)'>+</view>
									</view>
								</navigator> -->
							</view>
						</block>
					</checkbox-group>
				</view>
				<view class='invalidGoods' v-if="cartList.invalid.length > 0">
					<view class='goodsNav acea-row row-between-wrapper'>
						<view @click='goodsOpen'><text class='iconfont' :class='goodsHidden==true?"icon-xiangxia":"icon-xiangshang"'></text>失效商品</view>
						<view class='del' @click='unsetCart'><text class='iconfont icon-shanchu1'></text>清空</view>
					</view>
					<view class='goodsList' :hidden='goodsHidden'>
						<block v-for="(item,index) in cartList.invalid" :key='index'>
							<view class='item acea-row row-between-wrapper'>
								<view class='invalid'>失效</view>
								<view class='pictrue'>
									<image v-if="item.productInfo.attrInfo" :src='item.productInfo.attrInfo.image'></image>
									<image v-else :src='item.productInfo.image'></image>
								</view>
								<view class='text acea-row row-column-between'>
									<view class='line1 name'>{{item.productInfo.store_name}}</view>
									<view class='infor line1' v-if="item.productInfo.attrInfo">属性：{{item.productInfo.attrInfo.suk}}</view>
									<view class='acea-row row-between-wrapper'>
										<!-- <view>￥{{item.truePrice}}</view> -->
										<view class='end'>该商品已失效</view>
									</view>
								</view>
							</view>
						</block>
					</view>
				</view>
				<view class='loadingicon acea-row row-center-wrapper' v-if="cartList.valid.length&&!loadend">
					<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
				</view>
				<view class='loadingicon acea-row row-center-wrapper' v-if="cartList.invalid.length&&loadend">
					<text class='loading iconfont icon-jiazai' :hidden='loadingInvalid==false'></text>{{loadTitleInvalid}}
				</view>
			</view>
			<view class='noCart' v-if="cartList.valid.length == 0 && cartList.invalid.length == 0">
				<view class='pictrue'>
					<image src='../../static/images/noCart.png'></image>
				</view>
				<recommend :hostProduct='hostProduct'></recommend>
			</view>
			<view style='height:120rpx;'></view>
			<view class='footer acea-row row-between-wrapper' v-if="cartList.valid.length > 0">
				<view>
					<checkbox-group @change="checkboxAllChange">
						<checkbox value="all" :checked="!!isAllSelect" /><text class='checkAll'>全选 ({{selectValue.length}})</text>
					</checkbox-group>
				</view>
				<view class='money acea-row row-middle' v-if="footerswitch==true">
					<text class='font-color'>￥{{selectCountPrice}}</text>
					<form @submit="subOrder" report-submit='true'>
						<button class='placeOrder bg-color' formType="submit">立即下单</button>
					</form>
				</view>
				<view class='button acea-row row-middle' v-else>
					<form @submit="subCollect" report-submit='true'>
						<button class='bnt cart-color' formType="submit">收藏</button>
					</form>
					<form @submit="subDel" report-submit='true'>
						<button class='bnt' formType="submit">删除</button>
					</form>
				</view>
			</view>
		</view>
		<!-- #ifdef MP -->
		<authorize :isAuto="isAuto" :isShowAuth="isShowAuth" @authColse="authColse"></authorize>
		<!-- #endif -->
	</view>
</template>

<script>
	import {
		getCartList,
		getCartCounts,
		changeCartNum,
		cartDel
	} from '@/api/order.js';
	import {
		getProductHot,
		collectAll
	} from '@/api/store.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import recommend from '@/components/recommend';
	import ClipboardJS from "@/plugin/clipboard/clipboard.js";
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
				cartCount: 0,
				goodsHidden: true,
				footerswitch: true,
				hostProduct: [],
				cartList: {
					valid: [],
					invalid: []
				},
				isAllSelect: false, //全选
				selectValue: [], //选中的数据
				selectCountPrice: 0.00,
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false, //是否隐藏授权
				hotScroll:false,
				hotPage:1,
				hotLimit:10,
				
				loading: false,
				loadend: false,
				loadTitle: '加载更多', //提示语
				page: 1,
				limit: 20,
				loadingInvalid: false,
				loadendInvalid: false,
				loadTitleInvalid: '加载更多', //提示语
				pageInvalid: 1,
				limitInvalid: 20,
				attr: {
					cartAttr: false,
					productAttr: [],
					productSelect: {}
				},
				productValue: [], //系统属性
				storeInfo: {},
				attrValue: '', //已选属性
				attrTxt: '请选择', //属性页面提示
				cartId: 0,
				product_id: 0
			};
		},
		computed: mapGetters(['isLogin']),
		onLoad: function(options) {
			let that = this;
			if (that.isLogin == false) {
				// #ifdef H5 || APP-PLUS
				toLogin();
				// #endif 
				// #ifdef MP
				that.isAuto = true;
				that.$set(that, 'isShowAuth', true);
				// #endif
			}
		},
		onShow: function() {
			if (this.isLogin == true) {
				this.getHostProduct();
				this.getCartList();
				this.getCartNum();
				this.goodsHidden = true;
				this.footerswitch = true;
				this.hostProduct = [];
				this.hotScroll = false;
				this.hotPage = 1;
				this.hotLimit = 10;
				this.cartList = {
						valid: [],
						invalid: []
					},
					this.isAllSelect = false; //全选
				this.selectValue = []; //选中的数据
				this.selectCountPrice = 0.00;
				this.cartCount = 0;
				this.isShowAuth = false;
			};
		},
		methods: {
			     /**
			     * 手动输入数量失焦事件
			     */
			  //   inputBlur(index,val) {
			  //     if (val <= 1) {
			  //       // let index = e.currentTarget.dataset.index;
			  //       let item = this.cartList.valid[index];
			  //       item.cart_num = 1;
			  //       if (item.cart_num) this.setCartNum(item.id, item.cart_num);
					// this.cartList.valid[index] = item;
					// this.$set(this.cartList,'valid',this.cartList.valid);
					// this.switchSelect();
			  //     }
			  //   },
			// 授权关闭
			authColse: function(e) {
				this.isShowAuth = e;
			},
			subDel: function(event) {
				let that = this,
					selectValue = that.selectValue;
				if (selectValue.length > 0)
					cartDel(selectValue).then(res => {
						that.getCartList();
						that.getCartNum();
					});
				else
					return that.$util.Tips({
						title: '请选择产品'
					});
			},
			getSelectValueProductId: function() {
				let that = this;
				let validList = that.cartList.valid;
				let selectValue = that.selectValue;
				let productId = [];
				if (selectValue.length > 0) {
					for (let index in validList) {
						if (that.inArray(validList[index].id, selectValue)) {
							productId.push(validList[index].product_id);
						}
					}
				};
				return productId;
			},
			subCollect: function(event) {
				let that = this,
					selectValue = that.selectValue;
				if (selectValue.length > 0) {
					let selectValueProductId = that.getSelectValueProductId();
					collectAll(that.getSelectValueProductId().join(',')).then(res => {
						return that.$util.Tips({
							title: res.msg,
							icon: 'success'
						});
					}).catch(err => {
						return that.$util.Tips({
							title: err
						});
					});
				} else {
					return that.$util.Tips({
						title: '请选择产品'
					});
				}
			},
			subOrder: function(event) {
				let that = this,
					selectValue = that.selectValue;
				if (selectValue.length > 0) {
					uni.navigateTo({
						url: '/pages/users/order_confirm/index?cartId=' + selectValue.join(',')
					});
				} else {
					return that.$util.Tips({
						title: '请选择产品'
					});
				}
			},
			checkboxAllChange: function(event) {
				let value = event.detail.value;
				if (value.length > 0) {
					this.setAllSelectValue(1)
				} else {
					this.setAllSelectValue(0)
				}
			},
			setAllSelectValue: function(status) {
				let that = this;
				let selectValue = [];
				let valid = that.cartList.valid;
				if (valid.length > 0) {
					for (let index in valid) {
						if (status == 1) {
							valid[index].checked = true;
							selectValue.push(valid[index].id);
						} else valid[index].checked = false;
					}
					that.$set(that.cartList, 'valid', valid);
					that.selectValue = selectValue;
					that.switchSelect();
				}
			},
			checkboxChange: function(event) {
				let that = this;
				let value = event.detail.value;
				let valid = that.cartList.valid;
				for (let index in valid) {
					if (that.inArray(valid[index].id, value)) valid[index].checked = true;
					else valid[index].checked = false;
				}
				that.$set(that.cartList, 'valid', valid);
				that.isAllSelect = value.length == that.cartList.valid.length;
				that.selectValue = value;
				that.switchSelect();
			},
			inArray: function(search, array) {
				for (let i in array) {
					if (array[i] == search) {
						return true;
					}
				}
				return false;
			},
			switchSelect: function() {
				let that = this;
				let validList = that.cartList.valid;
				let selectValue = that.selectValue;
				let selectCountPrice = 0.00;
				if (selectValue.length < 1) {
					that.selectCountPrice = selectCountPrice;
				} else {
					for (let index in validList) {
						if (that.inArray(validList[index].id, selectValue)) {
							selectCountPrice = that.$util.$h.Add(selectCountPrice, that.$util.$h.Mul(validList[index].cart_num, validList[
								index].truePrice))
						}
					}
					that.selectCountPrice = selectCountPrice;
				}
			},
			/**
			  * 购物车手动填写
			  * 
			 */
			 iptCartNum: function (index) {
					let item = this.cartList.valid[index];
					if(item.cart_num){
						this.setCartNum(item.id, item.cart_num);
					}
					this.switchSelect();
			 },
			 blurInput:function (index) {
				 let item = this.cartList.valid[index];
				 if(!item.cart_num){
					 item.cart_num = 1;
					 this.$set(this.cartList,'valid',this.cartList.valid)
				 }
			 },
			subCart: function(index) {
				let that = this;
				let status = false;
				let item = that.cartList.valid[index];
				item.cart_num = Number(item.cart_num) - 1;
				if (item.cart_num < 1) status = true;
				if (item.cart_num <= 1) {
					item.cart_num = 1;
					item.numSub = true;
				} else {
					item.numSub = false;
					item.numAdd = false;
				}
				if (false == status) {
					that.setCartNum(item.id, item.cart_num, function(data) {
						that.cartList.valid[index] = item;
						that.switchSelect();
					});
				}
			},
			addCart: function(index) {
				let that = this;
				let item = that.cartList.valid[index];
				item.cart_num = Number(item.cart_num) + 1;
				let productInfo = item.productInfo;
				if (productInfo.hasOwnProperty('attrInfo') && item.cart_num >= item.productInfo.attrInfo.stock) {
					item.cart_num = item.productInfo.attrInfo.stock;
					item.numAdd = true;
					item.numSub = false;
				}else {
					item.numAdd = false;
					item.numSub = false;
				}
				that.setCartNum(item.id, item.cart_num, function(data) {
					that.cartList.valid[index] = item;
					that.switchSelect();
				});
			},
			setCartNum(cartId, cartNum, successCallback) {
				let that = this;
				changeCartNum(cartId, cartNum).then(res => {
					successCallback && successCallback(res.data);
				});
			},
			getCartNum: function() {
				let that = this;
				getCartCounts().then(res => {
					that.cartCount = res.data.count;
				});
			},
			getCartList: function() {
				let that = this;
				getCartList().then(res => {
					let cartList = res.data;
					let valid = cartList.valid;
					let numSub = [{
						numSub: true
					}, {
						numSub: false
					}];
					let numAdd = [{
							numAdd: true
						}, {
							numAdd: false
						}],
						selectValue = [];
					if (valid.length > 0) {
						for (let index in valid) {
							if (valid[index].cart_num == 1) {
								valid[index].numSub = true;
							} else {
								valid[index].numSub = false;
							}
							let productInfo = valid[index].productInfo;
							if (productInfo.hasOwnProperty('attrInfo') && valid[index].cart_num == valid[index].productInfo.attrInfo.stock) {
								valid[index].numAdd = true;
							} else if (valid[index].cart_num == valid[index].productInfo.stock) {
								valid[index].numAdd = true;
							} else {
								valid[index].numAdd = false;
							}
							valid[index].checked = true;
							selectValue.push(valid[index].id);
						}
					}
					that.$set(that, 'cartList', cartList);
					that.goodsHidden = cartList.valid.length <= 0 ? false : true;
					that.selectValue = selectValue;
					that.isAllSelect = valid.length == selectValue.length && valid.length;
					that.switchSelect();
				});
			},
			getHostProduct: function() {
				let that = this;
				if(that.hotScroll) return
				getProductHot(
					that.hotPage,
					that.hotLimit,
				).then(res => {
					that.hotPage++
					that.hotScroll = res.data.list.length<that.hotLimit
					that.hostProduct = that.hostProduct.concat(res.data.list)
				});
			},
			goodsOpen: function() {
				let that = this;
				that.goodsHidden = !that.goodsHidden;
			},
			manage: function() {
				let that = this;
				that.footerswitch = !that.footerswitch;
			},
			unsetCart: function() {
				let that = this,
					ids = [];
				for (let i = 0, len = that.cartList.invalid.length; i < len; i++) {
					ids.push(that.cartList.invalid[i].id);
				}
				cartDel(ids).then(res => {
					that.$util.Tips({
						title: '清除成功'
					});
					that.$set(that.cartList, 'invalid', []);
				}).catch(res => {

				});
			}
		},
		onReachBottom() {
			this.getHostProduct();
		}
	}
</script>

<style scoped lang="scss">
	.shoppingCart .labelNav {
		height: 76rpx;
		padding: 0 30rpx;
		font-size: 22rpx;
		color: #8c8c8c;
		position: fixed;
		left: 0;
		width: 100%;
		box-sizing: border-box;
		background-color: #f5f5f5;
		z-index: 5;
		top: 0;
	}

	.shoppingCart .labelNav .item .iconfont {
		font-size: 25rpx;
		margin-right: 10rpx;
	}

	.shoppingCart .nav {
		width: 100%;
		height: 80rpx;
		background-color: #fff;
		padding: 0 30rpx;
		box-sizing: border-box;
		font-size: 28rpx;
		color: #282828;
		position: fixed;
		left: 0;
		z-index: 5;
		top: 76rpx;
	}

	.shoppingCart .nav .administrate {
		font-size: 26rpx;
		color: #282828;
		width: 110rpx;
		height: 46rpx;
		border-radius: 6rpx;
		border: 1px solid #868686;
	}

	.shoppingCart .noCart {
		margin-top: 171rpx;
		background-color: #fff;
		padding-top: 0.1rpx;
	}

	.shoppingCart .noCart .pictrue {
		width: 414rpx;
		height: 336rpx;
		margin: 78rpx auto 56rpx auto;
	}

	.shoppingCart .noCart .pictrue image {
		width: 100%;
		height: 100%;
	}

	.shoppingCart .list {
		margin-top: 171rpx;
	}

	.shoppingCart .list .item {
		padding: 25rpx 30rpx;
		background-color: #fff;
		margin-bottom: 15rpx;
	}

	.shoppingCart .list .item .picTxt {
		width: 627rpx;
		position: relative;
	}

	.shoppingCart .list .item .picTxt .pictrue {
		width: 160rpx;
		height: 160rpx;
	}

	.shoppingCart .list .item .picTxt .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 6rpx;
	}

	.shoppingCart .list .item .picTxt .text {
		width: 444rpx;
		font-size: 28rpx;
		color: #282828;
	}

	.shoppingCart .list .item .picTxt .text .infor {
		font-size: 24rpx;
		color: #868686;
		margin-top: 16rpx;
	}

	.shoppingCart .list .item .picTxt .text .money {
		font-size: 32rpx;
		color: #282828;
		margin-top: 28rpx;
	}

	.shoppingCart .list .item .picTxt .carnum {
		height: 47rpx;
		position: absolute;
		bottom: 7rpx;
		right: 0;
	}

	.shoppingCart .list .item .picTxt .carnum view {
		border: 1rpx solid #a4a4a4;
		width: 66rpx;
		text-align: center;
		height: 100%;
		line-height: 40rpx;
		font-size: 28rpx;
		color: #a4a4a4;
	}

	.shoppingCart .list .item .picTxt .carnum .reduce {
		border-right: 0;
		border-radius: 3rpx 0 0 3rpx;
	}

	.shoppingCart .list .item .picTxt .carnum .reduce.on {
		border-color: #e3e3e3;
		color: #dedede;
	}

	.shoppingCart .list .item .picTxt .carnum .plus {
		border-left: 0;
		border-radius: 0 3rpx 3rpx 0;
	}

	.shoppingCart .list .item .picTxt .carnum .num {
		color: #282828;
	}

	.shoppingCart .invalidGoods {
		background-color: #fff;
	}

	.shoppingCart .invalidGoods .goodsNav {
		width: 100%;
		height: 66rpx;
		padding: 0 30rpx;
		box-sizing: border-box;
		font-size: 28rpx;
		color: #282828;
	}

	.shoppingCart .invalidGoods .goodsNav .iconfont {
		color: #424242;
		font-size: 28rpx;
		margin-right: 17rpx;
	}

	.shoppingCart .invalidGoods .goodsNav .del {
		font-size: 26rpx;
		color: #999;
	}

	.shoppingCart .invalidGoods .goodsNav .del .icon-shanchu1 {
		color: #999;
		font-size: 33rpx;
		vertical-align: -2rpx;
		margin-right: 8rpx;
	}

	.shoppingCart .invalidGoods .goodsList .item {
		padding: 20rpx 30rpx;
		border-top: 1rpx solid #f5f5f5;
	}

	.shoppingCart .invalidGoods .goodsList .item .invalid {
		font-size: 22rpx;
		color: #fff;
		width: 70rpx;
		height: 36rpx;
		background-color: #aaa;
		border-radius: 3rpx;
		text-align: center;
		line-height: 36rpx;
	}

	.shoppingCart .invalidGoods .goodsList .item .pictrue {
		width: 140rpx;
		height: 140rpx;
	}

	.shoppingCart .invalidGoods .goodsList .item .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 6rpx;
	}

	.shoppingCart .invalidGoods .goodsList .item .text {
		width: 433rpx;
		font-size: 28rpx;
		color: #999;
		height: 140rpx;
	}
    .shoppingCart .invalidGoods .goodsList .item .text .name{width:100%;}
	.shoppingCart .invalidGoods .goodsList .item .text .infor {
		font-size: 24rpx;
	}

	.shoppingCart .invalidGoods .goodsList .item .text .end {
		font-size: 26rpx;
		color: #bbb;
	}

	.shoppingCart .footer {
		z-index:9;
		width: 100%;
		height: 96rpx;
		background-color: #fafafa;
		position: fixed;
		padding: 0 30rpx;
		box-sizing: border-box;
		border-top: 1rpx solid #eee;
		// 
		bottom: 0;
		// 
		// #ifndef MP
		bottom: 50px;
		// #endif
	}

	.shoppingCart .footer .checkAll {
		font-size: 28rpx;
		color: #282828;
		margin-left: 16rpx;
	}

	// .shoppingCart .footer checkbox .wx-checkbox-input{background-color:#fafafa;}
	.shoppingCart .footer .money {
		font-size: 30rpx;
	}

	.shoppingCart .footer .placeOrder {
		color: #fff;
		font-size: 30rpx;
		width: 226rpx;
		height: 70rpx;
		border-radius: 50rpx;
		text-align: center;
		line-height: 70rpx;
		margin-left: 22rpx;
	}

	.shoppingCart .footer .button .bnt {
		font-size: 28rpx;
		color: #999;
		border-radius: 50rpx;
		border: 1px solid #999;
		width: 160rpx;
		height: 60rpx;
		text-align: center;
		line-height: 60rpx;
	}

	.shoppingCart .footer .button form~form {
		margin-left: 17rpx;
	}
</style>
