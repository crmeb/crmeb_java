<template>
	<view :data-theme="theme">
		<view class="cart_nav" :style='"height:"+navH+"rpx;"'>
			<view class='navbarCon acea-row'>
				<!-- #ifdef MP -->
				<view class="select_nav flex justify-center align-center" id="home" :style="{ top: homeTop + 'rpx' }">
					<text class="iconfont icon-fanhui2 px-20" @tap="returns"></text>
					<text class="iconfont icon-gengduo5 px-20" @tap.stop="showNav"></text>
					<text class="nav_line"></text>
				</view>
				<!-- #endif -->
				<!-- #ifdef H5 -->
				<view id="home" class="home acea-row row-center-wrapper iconfont icon-shouye4 h5_back"
					:style="{ top: homeTop + 'rpx' }" @tap="returns">
				</view>
				<!-- #endif -->
				<!-- #ifndef APP-PLUS -->
				<view class="nav_title" :style="{ top: homeTop + 'rpx' }">购物车</view>
				<!-- #endif -->
				<!-- #ifdef H5 || APP-PLUS -->
				<view class="right_select" :style="{ top: homeTop + 'rpx' }" @tap="showNav">
					<text class="iconfont icon-gengduo2"></text>
				</view>
				<!-- #endif -->
			</view>
		</view>
		<view class="dialog_nav" :style='"top:"+navH+"rpx;"' v-show="currentPage">
			<view class="dialog_nav_item" v-for="(item,index) in selectNavList" :key="index" @click="linkPage(item.url)">
				<text class="iconfont" :class="item.icon"></text>
				<text class="pl-20">{{item.name}}</text>
			</view>
		</view>
		<view class='shoppingCart copy-data' :style='"top:"+navH+"rpx;"' @touchstart="touchStart">
			<view class='labelNav acea-row row-around'>
				<view class='item'><text class='iconfont icon-xuanzhong'></text>100%正品保证</view>
				<view class='item'><text class='iconfont icon-xuanzhong'></text>所有商品精挑细选</view>
				<view class='item'><text class='iconfont icon-xuanzhong'></text>售后无忧</view>
			</view>
			<view class="borRadius14 cartBox">
				<view
					v-if="(cartList.valid.length === 0 && cartList.invalid.length === 0) || (cartList.valid.length > 0)"
					class='nav acea-row row-between-wrapper'>
					<view>购物数量 <text class='num font_color'>{{cartCount}}</text></view>
					<view v-if="cartList.valid.length > 0 || cartList.invalid.length > 0"
						class='administrate acea-row row-center-wrapper' @click='manage'>{{ footerswitch ? '管理' : '取消'}}
					</view>
				</view>
				<view v-if="cartList.valid.length > 0 || cartList.invalid.length > 0" class="pad30">
					<view class='list'>
						<checkbox-group @change="checkboxChange">
							<block v-for="(item,index) in cartList.valid" :key="index">
								<view class='item acea-row row-between-wrapper'>
									<!-- #ifndef MP -->
									<checkbox :value="(item.id).toString()" :checked="item.checked"
										:disabled="!item.attrStatus && footerswitch" style="margin-right: 10rpx;" />
									<!-- #endif -->
									<!-- #ifdef MP -->
									<checkbox :value="item.id" :checked="item.checked"
										:disabled="!item.attrStatus && footerswitch" />
									<!-- #endif -->
									<navigator :url='"/pages/goods/goods_details/index?id="+item.productId' hover-class='none'
										class='picTxt acea-row row-between-wrapper'>
										<view class='pictrue'>
											<image :src='item.image'></image>
										</view>
										<view class='text'>
											<view class='line1' :class="item.attrStatus?'':'reColor'">{{item.storeName}}
											</view>
											<view class='infor line1' v-if="item.suk">属性：{{item.suk}}</view>
											<view class='money mt-28' v-if="item.attrStatus">￥{{item.vipPrice ? item.vipPrice :item.price}}</view>
											<view class="reElection acea-row row-between-wrapper" v-else>
												<view class="title">请重新选择商品规格</view>
												<view class="reBnt cart-color acea-row row-center-wrapper"
													@click.stop="reElection(item)">重选</view>
											</view>
										</view>
										<view class='carnum acea-row row-center-wrapper' v-if="item.attrStatus">
											<view class="reduce" :class="item.numSub ? 'on' : ''"
												@click.stop='subCart(index)'>-</view>
											<view class='num'>{{item.cartNum}}</view>
											<view class="plus" :class="item.numAdd ? 'on' : ''"
												@click.stop='addCart(index)'>+</view>
										</view>
									</navigator>
								</view>
							</block>
						</checkbox-group>
					</view>
					<view v-if="cartList.invalid.length > 0" class='invalidGoods borRadius14'
						:style="cartList.valid.length===0 && cartList.invalid.length > 0 ? 'position: relative;z-index: 111;top: -120rpx;':'position: static;'">
						<view class='goodsNav acea-row row-between-wrapper'>
							<view v-if="cartList.invalid.length > 1 || cartList.valid.length > 0" @click='goodsOpen'>
								<text class='iconfont'
									:class='goodsHidden==true?"icon-xiangxia":"icon-xiangshang"'></text>失效商品
							</view>
							<view v-else>
								失效商品
							</view>
							<view class='del' @click='unsetCart'><text class='iconfont icon-shanchu1'></text>清空</view>
						</view>
						<view class='goodsList' :hidden='goodsHidden'>
							<block v-for="(item,index) in cartList.invalid" :key='index'>
								<view class='item acea-row row-between-wrapper'>
									<view class='invalid'>失效</view>
									<view class='picTxt acea-row row-between-wrapper'>
										<view class='pictrue'>
											<image :src='item.image'></image>
										</view>
										<view class='text acea-row row-column-between'>
											<view class='line1 name'>{{item.storeName}}</view>
											<view class='infor line1' v-if="item.suk">属性：{{item.suk}}</view>
											<view class='acea-row row-between-wrapper'>
												<view class='end'>该商品已失效</view>
											</view>
										</view>
									</view>
								</view>
							</block>
						</view>
					</view>
					<!-- #ifdef H5 -->
					<view style="height:240rpx;"></view>
					<!-- #endif -->
					<!-- #ifdef MP || APP-PLUS -->
					<view style="height:120rpx;"></view>
					<!-- #endif -->
					<view class='loadingicon acea-row row-center-wrapper' v-if="cartList.invalid.length&&loadend">
						<text class='loading iconfont icon-jiazai'
							:hidden='loadingInvalid==false'></text>{{loadTitleInvalid}}
					</view>
				</view>
				<view class='noCart' v-if="(cartList.valid.length == 0 && cartList.invalid.length == 0 && canShow) || !isLogin">
					<view class='pictrue'>
						<image :src="urlDomain+'crmebimage/perset/staticImg/noCart.png'"></image>
					</view>
					<!-- 推荐商品 -->
					<recommend ref="recommendIndex"></recommend>
					<!-- #ifdef H5 -->
					<view style="height:120rpx;"></view>
					<!-- #endif -->
				</view>
			</view>
		</view>
		<!-- <view style="height:260rpx;"></view> -->
		<view class='footer acea-row row-between-wrapper' v-if="cartList.valid.length > 0" :class="bottomNavigationIsCustom?'bottom-custom':''">
			<view>
				<checkbox-group @change="checkboxAllChange">
					<checkbox value="all" :checked="!!isAllSelect" />
					<text class='checkAll'>全选({{selectValue.length}})</text>
				</checkbox-group>
			</view>
			<view class='money acea-row row-middle' v-if="footerswitch==true">
				<text class='price-color'>￥{{selectCountPrice}}</text>
				<form @submit="subOrder" report-submit='true'>
					<button class='placeOrder bg_color' formType="submit">立即下单</button>
				</form>
			</view>
			<view class='button acea-row row-middle' v-else>
				<form @submit="subCollect" report-submit='true'>
					<button class='btn_cart_color' formType="submit">收藏</button>
				</form>
				<form @submit="subDel" report-submit='true'>
					<button class='bnt' formType="submit">删除</button>
				</form>
			</view>
		</view>
		<productWindow :attr="attr" :isShow='1' :iSplus='1' :iScart='1' @myevent="onMyEvent" @ChangeAttr="ChangeAttr"
			@ChangeCartNum="ChangeCartNum" @attrVal="attrVal" @iptCartNum="iptCartNum" @goCat="reGoCat"
			id='product-window'></productWindow>
		<view class="uni-p-b-96"></view>
		<view class="uni-p-b-98"></view>
		<pageFooter></pageFooter>
	</view>
</template>

<script>
	import pageFooter from '@/components/pageFooter/index.vue'
	// #ifdef APP-PLUS
	let sysHeight = uni.getSystemInfoSync().statusBarHeight + 'px';
	// #endif
	// #ifndef APP-PLUS
	let sysHeight = 0
	// #endif
	import {
		getCartList,
		getCartCounts,
		changeCartNum,
		cartDel,
		getResetCart
	} from '@/api/order.js';
	import {
		tokenIsExistApi
	} from '@/api/api.js';
	import {
		collectAll,
		getProductDetail
	} from '@/api/store.js';
	import {getShare} from '@/api/public.js';
	import {mapGetters} from "vuex";
	import recommend from '@/components/recommend';
	import productWindow from '@/components/productWindow';
	import animationType from '@/utils/animationType.js'
	import {
		Debounce
	} from '@/utils/validate.js'
	let app = getApp();
	export default {
		components: {
			recommend,
			productWindow,
			pageFooter
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				cartCount: 0,
				goodsHidden: false,
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
				hotScroll: false,
				hotPage: 1,
				hotLimit: 10,
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
				productInfo: {},
				attrValue: '', //已选属性
				attrTxt: '请选择', //属性页面提示
				cartId: 0,
				product_id: 0,
				sysHeight: sysHeight,
				canShow: false,
				configApi: {}, //分享类容配置
				theme:app.globalData.theme,
				navH:"",
				homeTop: 20,
				currentPage:false,
				selectNavList:[
					{name:'首页',icon:'icon-shouye8',url:'/pages/index/index'},
					{name:'搜索',icon:'icon-sousuo6',url:'/pages/goods/goods_search/index'},
					{name:'我的收藏',icon:'icon-shoucang3',url:'/pages/users/user_goods_collection/index'},
					{name:'个人中心',icon:'icon-gerenzhongxin1',url:'/pages/user/index'},
				],
				tokenIsExist: false, //校验token是否有效
			};
		},
		
		computed: mapGetters(['isLogin','bottomNavigationIsCustom']),
		onLoad: function(options) {
			//检查token是否有效
			this.getTokenIsExist();
			// #ifdef MP
			this.navH = app.globalData.navHeight;
			// #endif
			// #ifndef MP
			this.navH = 96;
			// #endif
			// #ifdef H5
			this.shareApi();
			// #endif
		},
		onReady() {
			this.$nextTick(function() {
				// #ifdef MP
				const menuButton = uni.getMenuButtonBoundingClientRect();
				const query = uni.createSelectorQuery().in(this);
				query
					.select('#home')
					.boundingClientRect(data => {
						this.homeTop = menuButton.top * 2 + menuButton.height - data.height;
					})
					.exec();
				// #endif
			});
		},
		onShow: function() {
			this.canShow = false
			if (this.isLogin && this.tokenIsExist) {
				this.getIndex();
			};
		},
		methods: {
			//校验token是否有效,true为有效，false为无效
			getTokenIsExist() {
				tokenIsExistApi().then(res => {
					this.tokenIsExist = res.data;
					this.canShow = true;
					if (this.isLogin && this.tokenIsExist) {
						this.getIndex();
					}
				})
			},
			//首次进入加载的接口
			getIndex(){
				this.hotPage = 1;
				this.hostProduct = [],
					this.hotScroll = false,
					this.loadend = false;
				this.page = 1;
				this.cartList.valid = [];
				this.getCartList();
				this.loadendInvalid = false;
				this.pageInvalid = 1;
				this.cartList.invalid = [];
				this.getInvalidList();
				this.footerswitch = true;
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
			},
			// 修改购物车
			reGoCat: function() {
				let that = this,
					productSelect = that.productValue[this.attrValue];
				//如果有属性,没有选择,提示用户选择
				if (
					that.attr.productAttr.length &&
					productSelect === undefined
				)
					return that.$util.Tips({
						title: "产品库存不足，请选择其它"
					});

				let q = {
					id: that.cartId,
					productId: that.product_id,
					num: that.attr.productSelect.cart_num,
					unique: that.attr.productSelect !== undefined ?
						that.attr.productSelect.unique : that.productInfo.id
				};
				getResetCart(q)
					.then(function(res) {
						that.attr.cartAttr = false;
						that.$util.Tips({
							title: "添加购物车成功",
							success: () => {
								that.loadend = false;
								that.page = 1;
								that.cartList.valid = [];
								that.getCartList();
								that.getCartNum();
							}
						});
					})
					.catch(res => {
						return that.$util.Tips({
							title: res
						});
					});
			},
			onMyEvent: function() {
				this.$set(this.attr, 'cartAttr', false);
			},
			reElection: function(item) {
				this.getGoodsDetails(item)
			},
			/**
			 * 获取产品详情
			 * 
			 */
			getGoodsDetails: function(item) {
				uni.showLoading({
					title: '加载中',
					mask: true
				});
				let that = this;
				that.cartId = item.id;
				that.product_id = item.productId;
				getProductDetail(item.productId).then(res => {
					uni.hideLoading();
					that.attr.cartAttr = true;
					let productInfo = res.data.productInfo;
					that.$set(that, 'productInfo', productInfo);
					// that.$set(that.attr, 'productAttr', res.data.productAttr);
					that.$set(that, 'productValue', res.data.productValue);
					let productAttr = res.data.productAttr.map(item => {
					return {
						attrName : item.attrName,
						attrValues: item.attrValues.split(','),
						id:item.id,
						isDel:item.isDel,
						productId:item.productId,
						type:item.type
					 }
					});
					this.$set(that.attr,'productAttr',productAttr);
					that.DefaultSelect();
				}).catch(err => {
					uni.hideLoading();
				})
			},
			/**
			 * 属性变动赋值
			 * 
			 */
			ChangeAttr: function(res) {
				let productSelect = this.productValue[res];
				if (productSelect && productSelect.stock > 0) {
					this.$set(this.attr.productSelect, "image", productSelect.image);
					this.$set(this.attr.productSelect, "price", productSelect.price);
					this.$set(this.attr.productSelect, "stock", productSelect.stock);
					this.$set(this.attr.productSelect, "unique", productSelect.id);
					this.$set(this.attr.productSelect, "cart_num", 1);
					this.$set(this, "attrValue", res);
					this.$set(this, "attrTxt", "已选择");
				} else {
					this.$set(this.attr.productSelect, "image", this.productInfo.image);
					this.$set(this.attr.productSelect, "price", this.productInfo.price);
					this.$set(this.attr.productSelect, "stock", 0);
					this.$set(this.attr.productSelect, "unique", this.productInfo.id);
					this.$set(this.attr.productSelect, "cart_num", 0);
					this.$set(this, "attrValue", "");
					this.$set(this, "attrTxt", "请选择");
				}
			},
			/**
			 * 默认选中属性
			 * 
			 */
			DefaultSelect: function() {
				let productAttr = this.attr.productAttr;
				let value = [];
				for (let key in this.productValue) {
					if (this.productValue[key].stock > 0) {
						value = this.attr.productAttr.length ? key.split(",") : [];
						break;
					}
				}
				for (let i = 0; i < productAttr.length; i++) {
					this.$set(productAttr[i], "index", value[i]);
				}
				//sort();排序函数:数字-英文-汉字；
				let productSelect = this.productValue[value.sort().join(",")];
				if (productSelect && productAttr.length) {
					this.$set(
						this.attr.productSelect,
						"storeName",
						this.productInfo.storeName
					);
					this.$set(this.attr.productSelect, "image", productSelect.image);
					this.$set(this.attr.productSelect, "price", productSelect.price);
					this.$set(this.attr.productSelect, "stock", productSelect.stock);
					this.$set(this.attr.productSelect, "unique", productSelect.id);
					this.$set(this.attr.productSelect, "cart_num", 1);
					this.$set(this, "attrValue", value.sort().join(","));
					this.$set(this, "attrTxt", "已选择");
				} else if (!productSelect && productAttr.length) {
					this.$set(
						this.attr.productSelect,
						"storeName",
						this.productInfo.storeName
					);
					this.$set(this.attr.productSelect, "image", this.productInfo.image);
					this.$set(this.attr.productSelect, "price", this.productInfo.price);
					this.$set(this.attr.productSelect, "stock", 0);
					this.$set(this.attr.productSelect, "unique", this.productInfo.id);
					this.$set(this.attr.productSelect, "cart_num", 0);
					this.$set(this, "attrValue", "");
					this.$set(this, "attrTxt", "请选择");
				} else if (!productSelect && !productAttr.length) {
					this.$set(
						this.attr.productSelect,
						"storeName",
						this.productInfo.storeName
					);
					this.$set(this.attr.productSelect, "image", this.productInfo.image);
					this.$set(this.attr.productSelect, "price", this.productInfo.price);
					this.$set(this.attr.productSelect, "stock", this.productInfo.stock);
					this.$set(
						this.attr.productSelect,
						"unique",
						this.productInfo.id || ""
					);
					this.$set(this.attr.productSelect, "cart_num", 1);
					this.$set(this, "attrValue", "");
					this.$set(this, "attrTxt", "请选择");
				}
			},
			attrVal(val) {
				this.$set(this.attr.productAttr[val.indexw], 'index', this.attr.productAttr[val.indexw].attrValues[val
					.indexn]);
			},
			/**
			 * 购物车数量加和数量减
			 * 
			 */
			ChangeCartNum: function(changeValue) {
				//changeValue:是否 加|减
				//获取当前变动属性
				let productSelect = this.productValue[this.attrValue];
				//如果没有属性,赋值给商品默认库存
				if (productSelect === undefined && !this.attr.productAttr.length)
					productSelect = this.attr.productSelect;
				//无属性值即库存为0；不存在加减；
				if (productSelect === undefined) return;
				let stock = productSelect.stock || 0;
				let num = this.attr.productSelect;
				if (changeValue) {
					num.cart_num++;
					if (num.cart_num > stock) {
						this.$set(this.attr.productSelect, "cart_num", stock ? stock : 1);
						this.$set(this, "cart_num", stock ? stock : 1);
					}
				} else {
					num.cart_num--;
					if (num.cart_num < 1) {
						this.$set(this.attr.productSelect, "cart_num", 1);
						this.$set(this, "cart_num", 1);
					}
				}
			},
			/**
			 * 购物车手动填写
			 * 
			 */
			iptCartNum: function(e) {
				this.$set(this.attr.productSelect, 'cart_num', e);
			},
			subDel: function(event) {
				let that = this,
					selectValue = that.selectValue;
				if (selectValue.length > 0)
					cartDel(selectValue).then(res => {
						that.loadend = false;
						that.page = 1;
						that.cartList.valid = [];
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
							productId.push(validList[index].productId);
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
					collectAll(that.getSelectValueProductId()).then(res => {
						return that.$util.Tips({
							title: '收藏成功',
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
			// 立即下单
			subOrder: Debounce(function(event) {

				let that = this,
					selectValue = that.selectValue;
				if (selectValue.length > 0) {
					that.getPreOrder();
				} else {
					return that.$util.Tips({
						title: '请选择产品'
					});
				}
			}),
			/**
			 * 预下单
			 */
			getPreOrder: function() {
				let shoppingCartId = this.selectValue.map(item => {
					return {
						"shoppingCartId": Number(item)
					}
				})
				this.$Order.getPreOrder("shoppingCart", shoppingCartId);
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
					let newValid = valid.map(item => {
						if (status) {
							if (that.footerswitch) {
								if (item.attrStatus) {
									item.checked = true;
									selectValue.push(item.id);
								} else {
									item.checked = false;
								}
							} else {
								item.checked = true;
								selectValue.push(item.id);
							}
							that.isAllSelect = true;
						} else {
							item.checked = false;
							that.isAllSelect = false;
						}
						return item;
					});
					that.$set(that.cartList, 'valid', newValid);
					that.selectValue = selectValue;
					that.switchSelect();
				}
			},
			checkboxChange: function(event) {
				let that = this;
				let value = event.detail.value;
				let valid = that.cartList.valid;
				let arr1 = [];
				let arr2 = [];
				let arr3 = [];
				let newValid = valid.map(item => {
					if (that.inArray(item.id, value)) {
						if (that.footerswitch) {
							if (item.attrStatus) {
								item.checked = true;
								arr1.push(item);
							} else {
								item.checked = false;
							}
						} else {
							item.checked = true;
							arr1.push(item);
						}
					} else {
						item.checked = false;
						arr2.push(item);
					}
					return item;
				});
				if (that.footerswitch) {
					arr3 = arr2.filter(item => !item.attrStatus);
				}
				that.$set(that.cartList, 'valid', newValid);
				// let newArr = that.cartList.valid.filter(item => item.attrStatus);
				that.isAllSelect = newValid.length === arr1.length + arr3.length;
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
							selectCountPrice = that.$util.$h.Add(selectCountPrice, that.$util.$h.Mul(validList[index]
								.cartNum, validList[index].vipPrice ? validList[index].vipPrice : validList[index].price))
						}
					}
					that.selectCountPrice = selectCountPrice;
				}
			},
			/**
			 * 购物车手动填写
			 * 
			 */
			iptCartNum: function(index) {
				let item = this.cartList.valid[index];
				if (item.cartNum) {
					this.setCartNum(item.id, item.cartNum);
				}
				this.switchSelect();
			},
			blurInput: function(index) {
				let item = this.cartList.valid[index];
				if (!item.cartNum) {
					item.cartNum = 1;
					this.$set(this.cartList, 'valid', this.cartList.valid)
				}
			},
			subCart: Debounce(function(index) {
				let that = this;
				let status = false;
				let item = that.cartList.valid[index];
				item.cartNum = Number(item.cartNum) - 1;
				if (item.cartNum < 1) status = true;
				if (item.cartNum <= 1) {
					item.cartNum = 1;
					item.numSub = true;
				} else {
					item.numSub = false;
					item.numAdd = false;
				}
				if (false == status) {
					that.setCartNum(item.id, item.cartNum, function(data) {
						that.cartList.valid[index] = item;
						that.switchSelect();
						that.getCartNum();
					});
				}
			}),
			addCart: Debounce(function(index) {
				let that = this;
				let item = that.cartList.valid[index];
				item.cartNum = Number(item.cartNum) + 1;
				if (item.cartNum < item.stock) {
					item.numAdd = false;
					item.numSub = false;
					that.setCartNum(item.id, item.cartNum, function(data) {
						that.cartList.valid[index] = item;
						that.switchSelect();
						that.getCartNum();
					});
				} else if (item.cartNum === item.stock) {
					item.numAdd = true;
					item.numSub = false;
					that.setCartNum(item.id, item.cartNum, function(data) {
						that.cartList.valid[index] = item;
						that.switchSelect();
						that.getCartNum();
					});
				} else {
					item.cartNum = item.stock;
					item.numAdd = true;
					item.numSub = false;
				}
			}),
			//购物车数量变化
			setCartNum(cartId, cartNum, successCallback) {
				let that = this;
				changeCartNum(cartId, cartNum).then(res => {
					successCallback && successCallback(res.data);
				});
			},
			getCartNum: function() {
				let that = this;
				getCartCounts(true, 'total').then(res => {
					that.cartCount = res.data.count;
				});
			},
			getCartData(data) {
				return new Promise((resolve, reject) => {
					getCartList(data).then((res) => {
						resolve(res.data);
					}).catch(function(err) {
						this.loading = false;
						this.canShow = true;
						this.$util.Tips({
							title: err
						});
					})
				});
			},
			async getCartList() {
				uni.showLoading({
					title: '加载中',
					mask: true
				});
				let that = this;
				let data = {
					page: that.page,
					limit: that.limit,
					isValid: true
				}
				getCartCounts(true, 'total').then(async c => {
					that.cartCount = c.data.count;
					for (let i = 0; i < Math.ceil(that.cartCount / that.limit); i++) {
						let cartList = await this.getCartData(data);
						let valid = cartList.list;
						let validList = that.$util.SplitArray(valid, that.cartList.valid);
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
						if (validList.length > 0) {
							for (let index in validList) {
								if (validList[index].cartNum == 1) {
									validList[index].numSub = true;
								} else {
									validList[index].numSub = false;
								}
								let productInfo = validList[index];
								let stock = validList[index].stock ? validList[index].stock : 0;
								if (validList[index].cartNum == stock) {
									validList[index].numAdd = true;
								} else if (validList[index].cartNum == validList[index].stock) {
									validList[index].numAdd = true;
								} else {
									validList[index].numAdd = false;
								}
								if (validList[index].attrStatus) {
									validList[index].checked = true;
									selectValue.push(validList[index].id);
								} else {
									validList[index].checked = false;
								}
							}
						}
						that.$set(that.cartList, 'valid', validList);
						data.page +=1;
						that.selectValue = selectValue;
						let newArr = validList.filter(item => item.attrStatus);
						that.isAllSelect = newArr.length == selectValue.length && newArr.length;
						that.switchSelect();
					}

					that.loading = false;
					that.canShow = true;
					uni.hideLoading();
				});
			},
			getInvalidList: function() {
				let that = this;
				if (this.loadendInvalid) return false;
				if (this.loadingInvalid) return false;
				let data = {
					page: that.pageInvalid,
					limit: that.limitInvalid,
					isValid: false
				}
				getCartList(data).then(res => {
					let invalid = res.data.list,
						loadendInvalid = invalid.length < that.limitInvalid;
					let invalidList = that.$util.SplitArray(invalid, that.cartList.invalid);
					that.$set(that.cartList, 'invalid', invalidList);
					that.loadendInvalid = loadendInvalid;
					that.loadTitleInvalid = loadendInvalid ? '我也是有底线的~' : '加载更多';
					that.pageInvalid = that.pageInvalid + 1;
					that.loadingInvalid = false;
					//if(invalid.length===0) that.getHostProduct();
				}).catch(res => {
					that.loadingInvalid = false;
					that.loadTitleInvalid = '加载更多';
				})

			},
			goodsOpen: function() {
				let that = this;
				that.goodsHidden = !that.goodsHidden;
			},
			manage: function() {
				let that = this;
				that.footerswitch = !that.footerswitch;
				let arr1 = [];
				let arr2 = [];
				let newValid = that.cartList.valid.map(item => {
					if (that.footerswitch) {
						if (item.attrStatus) {
							if (item.checked) {
								arr1.push(item.id);
							}
						} else {
							item.checked = false;
							arr2.push(item);
						}
					} else {
						if (item.checked) {
							arr1.push(item.id);
						}
					}
					return item;
				});
				that.cartList.valid = newValid;
				if (that.footerswitch) {
					that.isAllSelect = newValid.length === arr1.length + arr2.length;
				} else {
					that.isAllSelect = newValid.length === arr1.length;
				}
				that.selectValue = arr1;
				that.switchSelect();
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
					that.getHostProduct();
				}).catch(res => {

				});
			},
			shareApi: function() {
				getShare().then(res => {
					this.$set(this, 'configApi', res.data);
					// #ifdef H5
					this.setOpenShare(res.data);
					// #endif
				})
			},
			// 微信分享；
			setOpenShare: function(data) {
				let that = this;
				if (that.$wechat.isWeixin()) {
					let configAppMessage = {
						desc: data.synopsis,
						title: data.title,
						link: location.href,
						imgUrl: data.img
					};
					that.$wechat.wechatEvevt(["updateAppMessageShareData", "updateTimelineShareData"],
						configAppMessage);
				}
			},
			returns: function() {
				uni.switchTab({
					url:'/pages/index/index'
				})
			},
			showNav(){
				this.currentPage = !this.currentPage;
			},
			//下拉导航页面跳转
			linkPage(url){
				if(url == '/pages/index/index' || url == '/pages/user/index'){
					uni.switchTab({
						url
					})
				}else{
					uni.navigateTo({
						animationType: animationType.type,						animationDuration: animationType.duration,
						url
					})
				}
				this.currentPage = false
			},
			touchStart(){
				this.currentPage = false;
			}
		},
		onReachBottom() {
			let that = this;
			if (that.loadend) {
				that.getInvalidList();
			}
			if (that.cartList.valid.length == 0 && that.cartList.invalid.length == 0) {
				that.$refs.recommendIndex.get_host_product();
			}
		}
	}
</script>

<style scoped lang="scss">
	.invalidClas {
		position: relative;
		z-index: 111;
		top: -120rpx;
	}

	.invalidClasNO {
		position: static;
		margin-top: 15px;
	}

	.cartBox {
		// background-color: #fff;
	}
	.cart_nav{
		position: fixed;
		@include main_bg_color(theme);
		top: 0;
		left: 0;
		z-index: 99;
		width: 100%;
	}
	.navbarCon {
		position: absolute;
		bottom: 0;
		height: 100rpx;
		width: 100%;
	}
	.h5_back {
		color: #fff;
		position: fixed;
		left:20rpx;
		font-size: 32rpx;
		text-align: center;
		line-height: 58rpx;
	}
	.select_nav{
		width: 170rpx !important;
		height: 60rpx !important;
		border-radius: 33rpx;
		background: rgba(255, 255, 255, 0.6);
		color: #000;
		position: fixed;
		font-size: 18px;
		line-height: 58rpx;
		z-index: 1000;
		left: 14rpx;
	}
	.px-20{
		padding: 0 20rpx 0;
	}
	.nav_line{
		content: '';
		display: inline-block;
		width: 1px;
		height: 34rpx;
		background: #fff;
		position: absolute;
		left: 0;
		right: 0;
		margin: auto;
	}
	.container_detail{
		/* #ifdef MP */
		margin-top:32rpx;
		/* #endif */
	}
	.tab_nav{
		width: 100%;
		height: 48px;
		padding:0 30rpx 0;
	}
	.nav_title{
		width: 200rpx;
		height: 58rpx;
		line-height: 58rpx;
		color: #fff;
		font-size: 36rpx;
		position: fixed;
		text-align: center;
		left: 0;
		right: 0;
		margin: auto;
	}
	.right_select{
		position: fixed;
		right: 20rpx;
		color: #fff;
		text-align: center;
		line-height: 58rpx;
	}
	.dialog_nav{
		position: fixed;
		/* #ifdef MP */
		left: 14rpx;
		/* #endif */
		/* #ifdef H5 || APP-PLUS*/
		right: 14rpx;
		/* #endif */
		width: 240rpx;
		background: #FFFFFF;
		box-shadow: 0px 0px 16rpx rgba(0, 0, 0, 0.08);
		z-index: 999;
		border-radius: 14rpx;
		&::before{
			content: '';
			width: 0;
			height: 0;
			position: absolute;
			/* #ifdef MP */
			left: 0;
			right: 0;
			margin:auto;
			/* #endif */
			/* #ifdef H5 || APP-PLUS */
			right: 8px;
			/* #endif */
			top:-9px;
			border-bottom: 10px solid #fff;
			border-left: 10px solid transparent;    /*transparent 表示透明*/
			border-right: 10px solid transparent;
		}
	}
	.dialog_nav_item{
		width: 100%;
		height: 84rpx;
		line-height: 84rpx;
		padding: 0 20rpx 0;
		box-sizing: border-box;
		border-bottom: #eee;
		font-size: 28rpx;
		color: #333;
		position: relative;
		.iconfont{
			font-size: 32rpx;
		}
		&::after{
			content: '';
			position: absolute;
			width:86px;
			height: 1px;
			background-color: #EEEEEE;
			bottom: 0;
			right: 0;
		}
	}
	.pl-20{
		padding-left: 20rpx;
	}
	.px-20{
		padding: 0 20rpx 0;
	}
	.justify-center{
		justify-content: center;
	}
	.align-center {
		align-items: center;
	}
	.shoppingCart {
		/* #ifdef H5 */
		// padding-bottom: 0;
		// padding-bottom: constant(safe-area-inset-bottom);  
		// padding-bottom: env(safe-area-inset-bottom);
		/* #endif */
		position: absolute;
		width: 100%;
	}

	.shoppingCart .labelNav {
		height: 178rpx;
		padding: 30rpx 30rpx 0 ;
		font-size: 22rpx;
		color: #fff;
		width: 100%;
		box-sizing: border-box;
		@include main_bg_color(theme);
		z-index: 5;
	}

	.shoppingCart .labelNav .item .iconfont {
		font-size: 25rpx;
		margin-right: 10rpx;
	}

	.shoppingCart .nav {
		width: 92%;
		height: 90rpx;
		background-color: #fff;
		padding: 0 24rpx;
		-webkit-box-sizing: border-box;
		box-sizing: border-box;
		font-size: 28rpx;
		color: #282828;
		margin: -90rpx auto 0 ;
		z-index: 6;
		border-top-left-radius: 14rpx;
		border-top-right-radius: 14rpx;
	}

	.shoppingCart .nav .num {
		margin-left: 12rpx;
	}

	.shoppingCart .nav .administrate {
		font-size: 28rpx;
		color: #333333;
	}

	.shoppingCart .noCart {
		// margin-top: 171rpx;
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
		width: 100%;
		// margin-top: 178rpx;
		/* #ifdef MP */
		// margin-bottom:120rpx;
		/* #endif */
		/* #ifndef MP */
		// margin-bottom:240rpx;
		/* #endif */
		overflow: hidden;
		border-bottom-left-radius: 14rpx;
		border-bottom-right-radius: 14rpx;
	}
	

	.shoppingCart .list .item {
		padding: 24rpx;
		background-color: #fff;
	}

	.shoppingCart .list .item .picTxt {
		width: 582rpx;
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
		width: 396rpx;
		font-size: 28rpx;
		color: #282828;
	}

	.shoppingCart .list .item .picTxt .text .reColor {
		color: #999;
	}

	.shoppingCart .list .item .picTxt .text .reElection {
		margin-top: 20rpx;
	}

	.shoppingCart .list .item .picTxt .text .reElection .title {
		font-size: 24rpx;
	}

	.shoppingCart .list .item .picTxt .text .reElection .reBnt {
		width: 120rpx;
		height: 46rpx;
		border-radius: 23rpx;
		font-size: 26rpx;
	}

	.shoppingCart .list .item .picTxt .text .infor {
		font-size: 24rpx;
		color: #999999;
		margin-top: 16rpx;
	}

	.money {
		font-size: 32rpx;
		font-weight: 600;
		@include price_color(theme);
		.price-color{
			@include price_color(theme);
		}
	}
	.mt-28{
		margin-top: 28rpx;
	}
	.bg_color{
		@include main_bg_color(theme);
	}
	.font_color{
		@include main_color(theme);
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
		line-height: 44rpx;
		font-size: 28rpx;
		color: #a4a4a4;
	}

	.shoppingCart .list .item .picTxt .carnum .reduce {
		border-right: 0;
		border-radius: 3rpx 0 0 3rpx;
		border-radius: 22rpx 0rpx 0rpx 22rpx;
		font-size: 34rpx;
		line-height: 40rpx;
	}

	.on {
		border-color: #e3e3e3 !important;
		color: #dedede !important;
	}

	.shoppingCart .list .item .picTxt .carnum .plus {
		border-left: 0;
		border-radius: 0 3rpx 3rpx 0;
		border-radius: 0rpx 22rpx 22rpx 0rpx;
		font-size: 34rpx;
		line-height: 40rpx;
	}

	.shoppingCart .list .item .picTxt .carnum .num {
		color: #282828;
	}

	.shoppingCart .invalidGoods {
		background-color: #fff;
		margin-top: 30rpx;
	}

	.shoppingCart .invalidGoods .goodsNav {
		width: 100%;
		height: 90rpx;
		padding: 0 24rpx;
		box-sizing: border-box;
		font-size: 28rpx;
		color: #333333;
	}

	.shoppingCart .invalidGoods .goodsNav .iconfont {
		color: #424242;
		font-size: 28rpx;
		margin-right: 17rpx;
	}

	.shoppingCart .invalidGoods .goodsNav .del {
		font-size: 26rpx;
		color: #333;
	}

	.shoppingCart .invalidGoods .goodsNav .del .icon-shanchu1 {
		color: #333;
		font-size: 33rpx;
		vertical-align: -2rpx;
		margin-right: 8rpx;
	}

	.shoppingCart .invalidGoods .goodsList .item {
		padding: 24rpx;
	}

	.shoppingCart .invalidGoods .goodsList .picTxt {
		width: 576rpx;
	}

	.shoppingCart .invalidGoods .goodsList .item .invalid {
		font-size: 22rpx;
		color: #CCCCCC;
		height: 36rpx;
		border-radius: 3rpx;
		text-align: center;
		line-height: 36rpx;
	}

	.shoppingCart .invalidGoods .goodsList .item .pictrue {
		width: 160rpx;
		height: 160rpx;
	}

	.shoppingCart .invalidGoods .goodsList .item .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 6rpx;
	}

	.shoppingCart .invalidGoods .goodsList .item .text {
		width: 396rpx;
		font-size: 28rpx;
		color: #999;
		height: 140rpx;
	}

	.shoppingCart .invalidGoods .goodsList .item .text .name {
		width: 100%;
	}

	.shoppingCart .invalidGoods .goodsList .item .text .infor {
		font-size: 24rpx;
	}

	.shoppingCart .invalidGoods .goodsList .item .text .end {
		font-size: 26rpx;
		color: #bbb;
	}

	.footer {
		z-index: 999;
		width: 100%;
		height: 100rpx;
		background-color: #fff;
		position: fixed;
		padding: 0 24rpx;
		box-sizing: border-box;
		border-top: 1rpx solid #eee;
		bottom: var(--window-bottom);
		
	}

	.footer .checkAll {
		font-size: 28rpx;
		color: #282828;
		margin-left: 14rpx;
	}

	.footer .money {
		font-size: 30rpx;

		.font-color {
			font-weight: 600;
		}
	}

	.footer .placeOrder {
		color: #fff;
		font-size: 30rpx;
		width: 226rpx;
		height: 70rpx;
		border-radius: 50rpx;
		text-align: center;
		line-height: 70rpx;
		margin-left: 22rpx;
	}

	.footer .button .bnt {
		font-size: 28rpx;
		color: #999;
		border-radius: 50rpx;
		border: 1px solid #999;
		width: 160rpx;
		height: 60rpx;
		text-align: center;
		line-height: 60rpx;
	}
	.btn_cart_color{
		font-size: 14px;
		border-radius: 25px;
		width: 80px;
		height: 30px;
		text-align: center;
		line-height: 30px;
		@include coupons_border_color(theme);
		@include main_color(theme);
	}
	.footer .button form~form {
		margin-left: 17rpx;
	}

	.uni-p-b-96 {
		height: 96rpx;
	}
	/deep/ checkbox .uni-checkbox-input.uni-checkbox-input-checked {
		@include main_bg_color(theme);
		border: none !important;
		color: #fff!important
	}
	
	/deep/ checkbox .wx-checkbox-input.wx-checkbox-input-checked {
		@include main_bg_color(theme);
		border: none !important;
		color: #fff!important;
		margin-right: 0 !important;
	}
	.bottom-custom{
		bottom: calc(98rpx+ constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		bottom: calc(98rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
	}
</style>