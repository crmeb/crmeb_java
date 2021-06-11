<template>
	<div class="group-con">
		<div class="header acea-row row-between-wrapper">
			<div class="pictrue"><img :src="storeCombination.image" /></div>
			<div class="text">
				<div class="line1" v-text="storeCombination.title"></div>
				<div class="money">
					￥
					<span class="num" v-text="storeCombination.price || 0"></span>
					<span class="team cart-color">{{storeCombination.people +'人拼'}}</span>
				</div>
			</div>
			<div v-if="pinkBool === -1" class="iconfont icon-pintuanshibai"></div>
			<div v-else-if="pinkBool === 1" class="iconfont icon-pintuanchenggong font-color-red"></div>
		</div>
		<div class="wrapper">
			<div class="title acea-row row-center-wrapper" v-if="pinkBool === 0">
				<div class="line"></div>
				<div class="name acea-row row-center-wrapper">
					剩余
					<CountDown :bgColor="bgColor" :is-day="false" :tip-text="' '" :day-text="' '" :hour-text="' : '"
						:minute-text="' : '" :second-text="' '" :datatime="pinkT.stopTime/1000"></CountDown>
					<span class="end">结束</span>
				</div>
				<div class="line"></div>
			</div>
			<div class="tips font-color-red" v-if="pinkBool === 1">恭喜您拼团成功</div>
			<div class="tips" v-else-if="pinkBool === -1">还差{{ count }}人，拼团失败</div>
			<div class="tips font-color-red" v-else-if="pinkBool === 0">拼团中，还差{{ count }}人拼团成功</div>
			<div class="list acea-row row-middle"
				:class="[pinkBool === 1 || pinkBool === -1 ? 'result' : '', iShidden ? 'on' : '']">
				<div class="pinkT">
					<div class="pictrue"><img :src="pinkT.avatar" /></div>
					<div class="chief">团长</div>
				</div>
				<block v-if="pinkAll.length > 0">
					<div class="pictrue" v-for="(item, index) in pinkAll" :key="index"><img :src="item.avatar" /></div>
				</block>
				<div class="pictrue" v-for="index in count" :key="index"><img class="img-none"
						src="/static/images/vacancy.png" /></div>
			</div>
			<div v-if="(pinkBool === 1 || pinkBool === -1) && count > 9" class="lookAll acea-row row-center-wrapper"
				@click="lookAll">
				{{ iShidden ? '收起' : '查看全部' }}
				<span class="iconfont" :class="iShidden ? 'icon-xiangshang' : 'icon-xiangxia'"></span>
			</div>
			<!-- #ifndef MP -->
			<div v-if="userBool === 1 && isOk == 0 && pinkBool === 0">
				<div class="teamBnt bg-color-red" v-if='pinkT.stopTime>timestamp' @click="goPoster">邀请好友参团</div>
			</div>
			<!-- #endif -->
			<!-- #ifdef MP -->
			<button open-type="share" class="teamBnt bg-color-red"
				v-if="userBool === 1 && isOk == 0 && pinkBool === 0 && pinkT.stopTime>timestamp">邀请好友参团</button>
			<!-- #endif -->
			<div class="teamBnt bg-color-hui" v-if="pinkT.stopTime<timestamp && isOk == 0 && pinkBool === 0">拼团已过期</div>
			<div class="teamBnt bg-color-red"
				v-else-if="userBool === 0 && pinkBool === 0 && count > 0 && pinkT.stopTime>timestamp" @click="pay">我要参团
			</div>
			<div class="teamBnt bg-color-red" v-if="pinkBool === 1 || pinkBool === -1"
				@click="goDetail(storeCombination.id)">再次开团</div>
			<div class="cancel" @click="getCombinationRemove" v-if="pinkBool === 0 && userBool === 1">
				<span class="iconfont icon-guanbi3"></span>
				取消开团
			</div>

			<div class="lookOrder" v-if="pinkBool === 1" @click="goOrder">
				查看订单信息
				<span class="iconfont icon-xiangyou"></span>
			</div>
		</div>
		<div class="group-recommend">
			<div class="title acea-row row-between-wrapper">
				<div>大家都在拼</div>
				<div class="more" @click="goList">
					更多拼团
					<span class="iconfont icon-jiantou"></span>
				</div>
			</div>
			<div class="list acea-row row-middle">
				<div class="item" v-for="(item, index) in storeCombinationHost" :key="index" @click="goDetail(item.id)">
					<div class="pictrue">
						<img :src="item.image" />
						<div class="team" v-text="item.people + '人团'"></div>
					</div>
					<div class="name line1" v-text="item.title"></div>
					<div class="money font-color-red" v-text="'￥' + item.price"></div>
				</div>
			</div>
		</div>
		<product-window :attr="attr" :limitNum="1" :iSbnt="1" @myevent="onMyEvent" @ChangeAttr="ChangeAttr"
			@ChangeCartNum="ChangeCartNum" @iptCartNum="iptCartNum" @attrVal="attrVal" @goCat="goPay"></product-window>
		<view class="mask" v-if="posters || canvasStatus" @click="listenerActionClose"></view>
		<!-- 发送给朋友图片 -->
		<view class="share-box" v-if="H5ShareBox">
			<image src="/static/images/share-info.png" @click="H5ShareBox = false"></image>
		</view>
		<!-- 海报展示 -->
		<view class='poster-pop' v-if="canvasStatus">
			<image :src='imagePath'></image>
			<!-- #ifndef H5  -->
			<view class='save-poster' @click="savePosterPath">保存到手机</view>
			<!-- #endif -->
			<!-- #ifdef H5 -->
			<view class="keep">长按图片保存至相册</view>
			<view class='iconfont icon-cha2 close' @tap='listenerActionClose'></view>
			<!-- #endif -->
		</view>
		<view class="canvas">
			<canvas style="width:597px;height:850px;" canvas-id="activityCanvas"></canvas>
			<canvas canvas-id="qrcode" :style="{width: `${qrcodeSize}px`, height: `${qrcodeSize}px`}"
				style="opacity: 0;" />
		</view>

		<!-- #ifdef MP -->
		<!-- <authorize @onLoadFun="onLoadFun" :isAuto="isAuto" :isShowAuth="isShowAuth" @authColse="authColse"></authorize> -->
		<!-- #endif -->
		<home></home>
	</div>
</template>
<script>
	import CountDown from '@/components/countDown';
	import ProductWindow from '@/components/productWindow';
	import uQRCode from '@/js_sdk/Sansnn-uQRCode/uqrcode.js';
	import {
		imageBase64
	} from "@/api/public";
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from 'vuex';
	import {
		getCombinationPink,
		postCombinationRemove,
		getCombinationMore
	} from '@/api/activity';
	import {
		postCartAdd
	} from '@/api/store';
	// #ifdef MP
	import authorize from '@/components/Authorize';
	// #endif
	import home from '@/components/home';
	const NAME = 'GroupRule';
	const app = getApp();
	export default {
		name: NAME,
		components: {
			CountDown,
			ProductWindow,
			home,
			// #ifdef MP
			authorize
			// #endif
		},
		props: {},
		data: function() {
			return {
				bgColor: {
					'bgColor': '#333333',
					'Color': '#fff',
					'width': '44rpx',
					'timeTxtwidth': '16rpx',
					'isDay': true
				},
				currentPinkOrder: '', //当前拼团订单
				isOk: 0, //判断拼团是否完成
				pinkBool: 0, //判断拼团是否成功|0=失败,1=成功
				userBool: 0, //判断当前用户是否在团内|0=未在,1=在
				pinkAll: [], //团员
				pinkT: [], //团长信息
				storeCombination: {}, //拼团产品
				storeCombinationHost: [], //拼团推荐
				pinkId: 0,
				count: 0, //拼团剩余人数
				iShidden: false,
				isOpen: false, //是否打开属性组件
				attr: {
					cartAttr: false,
					productSelect: {
						image: '',
						storeName: '',
						price: '',
						quota: 0,
						unique: '',
						cart_num: 1,
						quotaShow: 0,
						stock: 0,
						num: 0
					},
					attrValue: '',
					productAttr: []
				},
				cart_num: '',
				limit: 10,
				page: 1,
				loading: false,
				loadend: false,
				userInfo: {},
				posters: false, // app分享
				H5ShareBox: false, //公众号分享图片
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false, //是否隐藏授权
				onceNum: 0, //一次可以购买几个,
				timestamp: 0, // 当前时间戳
				qrcodeSize: 600,
				posterbackgd: '/static/images/canbj.png',
				PromotionCode: '', //二维码
				canvasStatus: false,
				imgTop: '', //商品图base64位
				imagePath: '' // 海报图片
			};
		},
		watch: {
			userData: {
				handler: function(newV, oldV) {
					if (newV) {
						this.userInfo = newV;
						app.globalData.openPages = '/pages/activity/goods_combination_status/index?id=' + this.pinkId +
							"&spread=" + this.uid;
					}
				},
				deep: true
			}
		},
		computed: mapGetters({
			'isLogin': 'isLogin',
			'userData': 'userInfo',
			'uid': 'uid'
		}),
		onLoad(options) {
			var that = this;
			that.pinkId = options.id;
			if (that.isLogin == false) {
				toLogin();
			} else {
				this.timestamp = (new Date()).getTime();
				this.getCombinationPink();
			}
		},
		onShow() {},
		mounted: function() {
			this.combinationMore();
		},
		// link: window.location.protocol + '//' + window.location.host +
		// 	'/pages/activity/goods_combination_status/index?id=' + that.pinkId + "&spread=" + this.uid,
		//#ifdef MP
		/**
		 * 用户点击右上角分享
		 */
		onShareAppMessage: function() {
			let that = this;
			return {
				title: '您的好友' + that.userInfo.nickname + '邀请您参团' + that.storeCombination.title,
				path: app.globalData.openPages,
				imageUrl: that.storeCombination.image
			};
		},
		//#endif
		methods: {
			// 分享关闭
			listenerActionClose: function() {
				this.posters = false;
				this.canvasStatus = false;
			},
			// 更多拼团
			combinationMore: function() {
				var that = this;
				if (that.loadend) return;
				if (that.loading) return;
				var data = {
					page: that.page,
					limit: that.limit,
					comId: that.pinkId
				};
				this.loading = true
				getCombinationMore(data)
					.then(res => {
						var storeCombinationHost = that.storeCombinationHost;
						var limit = that.limit;
						that.page++;
						that.loadend = limit > res.data.length;
						that.storeCombinationHost = storeCombinationHost.concat(res.data.list);
						that.page = that.data.page;
						that.loading = false;
					})
					.catch(res => {
						that.loading = false
						that.$util.Tips({
							title: res
						});
					});
			},
			// 授权关闭
			authColse: function(e) {
				this.isShowAuth = e;
			},
			// 授权后回调
			onLoadFun: function(e) {
				this.userInfo = e;
				app.globalData.openPages = '/pages/activity/goods_combination_status/index?id=' + this.pinkId;
				this.getCombinationPink();
			},
			/**
			 * 购物车手动填写
			 *
			 */
			iptCartNum: function(e) {
				if (e > this.onceNum) {
					this.$util.Tips({
						title: `该商品每次限购${this.onceNum}${this.storeCombination.unitName}`
					});
					this.$set(this.attr.productSelect, 'cart_num', this.onceNum);
					this.$set(this, "cart_num", this.onceNum);
				} else {
					this.$set(this.attr.productSelect, 'cart_num', e);
					this.$set(this, "cart_num", e);
				}
			},
			attrVal(val) {
				this.attr.productAttr[val.indexw].index = this.attr.productAttr[val.indexw].attrValues[val.indexn];
			},
			onMyEvent: function() {
				this.$set(this.attr, 'cartAttr', false);
				this.$set(this, 'isOpen', false);
			},
			//将父级向子集多次传送的函数合二为一；
			// changeFun: function(opt) {
			// 	if (typeof opt !== "object") opt = {};
			// 	let action = opt.action || "";
			// 	let value = opt.value === undefined ? "" : opt.value;
			// 	this[action] && this[action](value);
			// },
			// changeattr: function(res) {
			// 	var that = this;
			// 	that.attr.cartAttr = res;
			// },
			//选择属性；
			ChangeAttr: function(res) {
				this.$set(this, 'cart_num', 1);
				let productSelect = this.productValue[res];
				if (productSelect) {
					this.$set(this.attr.productSelect, 'image', productSelect.image);
					this.$set(this.attr.productSelect, 'price', productSelect.price);
					this.$set(this.attr.productSelect, 'quota', productSelect.quota);
					this.$set(this.attr.productSelect, 'unique', productSelect.id);
					this.$set(this.attr.productSelect, 'cart_num', 1);
					this.$set(this.attr.productSelect, 'stock', productSelect.stock);
					this.$set(this.attr.productSelect, 'quotaShow', productSelect.quotaShow);
					this.attrValue = res;
					this.attrTxt = '已选择';
				} else {
					this.$set(this.attr.productSelect, 'image', this.storeCombination.image);
					this.$set(this.attr.productSelect, 'price', this.storeCombination.price);
					this.$set(this.attr.productSelect, 'quota', 0);
					this.$set(this.attr.productSelect, 'unique', '');
					this.$set(this.attr.productSelect, 'cart_num', 0);
					this.$set(this.attr.productSelect, 'quotaShow', 0);
					this.$set(this.attr.productSelect, 'stock', 0);
					this.attrValue = '';
					this.attrTxt = '请选择';
				}
			},
			ChangeCartNum: function(res) {
				//changeValue:是否 加|减
				//获取当前变动属性
				let productSelect = this.productValue[this.attrValue];
				if (this.cart_num) {
					productSelect.cart_num = this.cart_num;
					this.attr.productSelect.cart_num = this.cart_num;
				}
				//如果没有属性,赋值给商品默认库存
				if (productSelect === undefined && !this.attr.productAttr.length) productSelect = this.attr
					.productSelect;
				if (productSelect === undefined) return;
				let stock = productSelect.stock || 0;
				let quotaShow = productSelect.quotaShow || 0;
				let quota = productSelect.quota || 0;
				let num = this.attr.productSelect;
				let nums = this.storeCombination.num || 0;
				//设置默认数据
				if (productSelect.cart_num == undefined) productSelect.cart_num = 1;
				if (res) {
					num.cart_num++;
					let arrMin = [];
					arrMin.push(nums);
					arrMin.push(quota);
					arrMin.push(stock);
					let minN = Math.min.apply(null, arrMin);
					if (num.cart_num >= minN) {
						this.$set(this.attr.productSelect, 'cart_num', minN ? minN : 1);
						this.$set(this, 'cart_num', minN ? minN : 1);
					}
					this.$set(this, 'cart_num', num.cart_num);
					this.$set(this.attr.productSelect, 'cart_num', num.cart_num);
				} else {
					num.cart_num--;
					if (num.cart_num < 1) {
						this.$set(this.attr.productSelect, 'cart_num', 1);
						this.$set(this, 'cart_num', 1);
					}
					this.$set(this, 'cart_num', num.cart_num);
					this.$set(this.attr.productSelect, 'cart_num', num.cart_num);
				}
			},
			//默认选中属性；
			DefaultSelect() {
				let productAttr = this.attr.productAttr,
					value = [];
				for (var key in this.productValue) {
					if (this.productValue[key].quota > 0) {
						value = this.attr.productAttr.length ? key.split(',') : [];
						break;
					}
				}
				for (let i = 0; i < productAttr.length; i++) {
					this.$set(productAttr[i], 'index', value[i]);
				}
				//sort();排序函数:数字-英文-汉字；
				let productSelect = this.productValue[value.join(',')];
				if (productSelect && productAttr.length) {
					this.$set(this.attr.productSelect, 'storeName', this.storeCombination.title);
					this.$set(this.attr.productSelect, 'image', productSelect.image);
					this.$set(this.attr.productSelect, 'price', productSelect.price);
					this.$set(this.attr.productSelect, 'quota', productSelect.quota);
					this.$set(this.attr.productSelect, 'unique', productSelect.id);
					this.$set(this.attr.productSelect, 'cart_num', 1);
					this.$set(this.attr.productSelect, 'stock', productSelect.stock);
					this.$set(this.attr.productSelect, 'quotaShow', productSelect.quotaShow);
					//this.$set(this, 'attrValue', value.join(','));
					this.attrValue = value.join(',');
					this.attrTxt = '已选择';
					//this.$set(this, 'attrTxt', '已选择');
				} else if (!productSelect && productAttr.length) {
					this.$set(this.attr.productSelect, 'storeName', this.storeCombination.title);
					this.$set(this.attr.productSelect, 'image', this.storeCombination.image);
					this.$set(this.attr.productSelect, 'price', this.storeCombination.price);
					this.$set(this.attr.productSelect, 'quota', 0);
					this.$set(this.attr.productSelect, 'unique', '');
					this.$set(this.attr.productSelect, 'cart_num', 0);
					this.$set(this.attr.productSelect, 'stock', 0);
					this.$set(this.attr.productSelect, 'quotaShow', 0);
					//this.$set(this, 'attrValue', '');
					this.attrValue = '';
					this.attrTxt = '请选择';
					//	this.$set(this, 'attrTxt', '请选择');
				} else if (!productSelect && !productAttr.length) {
					this.$set(this.attr.productSelect, 'storeName', this.storeCombination.title);
					this.$set(this.attr.productSelect, 'image', this.storeCombination.image);
					this.$set(this.attr.productSelect, 'price', this.storeCombination.price);
					this.$set(this.attr.productSelect, 'quota', 0);
					this.$set(this.attr.productSelect, 'unique', this.storeCombination.id || '');
					this.$set(this.attr.productSelect, 'cart_num', 1);
					this.$set(this.attr.productSelect, 'quotaShow', 0);
					this.$set(this.attr.productSelect, 'stock', 0);
					//this.$set(this, 'attrValue', '');
					this.attrValue = '';
					this.attrTxt = '请选择';
					//this.$set(this, 'attrTxt', '请选择');
				}
			},
			setProductSelect: function() {
				var that = this;
				var attr = that.attr;
				attr.productSelect.image = that.storeCombination.image;
				attr.productSelect.storeName = that.storeCombination.title;
				attr.productSelect.price = that.storeCombination.price;
				attr.productSelect.quota = 0;
				attr.productSelect.quotaShow = 0;
				attr.productSelect.stock = 0;
				attr.cartAttr = false;
				that.$set(that, 'attr', attr);
			},
			pay: function() {
				var that = this;
				that.attr.cartAttr = true;
				that.isOpen = true;
			},
			goPay() {
				this.$Order.getPreOrder("buyNow", [{
					"attrValueId": parseFloat(this.attr.productSelect.unique),
					"combinationId": parseFloat(this.storeCombination.id),
					"productNum": parseFloat(this.attr.productSelect.cart_num),
					"productId": parseFloat(this.storeCombination.productId),
					"pinkId": parseFloat(this.pinkId)
				}]);
			},
			goPoster: function() {
				//#ifdef H5
				if (this.$wechat.isWeixin()) {
					this.H5ShareBox = true;
				} else {
					uni.showLoading({
						title: '海报生成中',
						mask: true
					});
					this.posters = false;
					let arrImagesUrl = '';
					let arrImagesUrlTop = '';
					if (!this.PromotionCode) {
						uni.hideLoading();
						this.$util.Tips({
							title: this.errT
						});
						return
					}
					setTimeout(() => {
						if (!this.imgTop) {
							uni.hideLoading();
							this.$util.Tips({
								title: '无法生成商品海报！'
							});
							return
						}
					}, 1000);
					uni.downloadFile({
						url: this.imgTop,
						success: (res) => {
							arrImagesUrlTop = res.tempFilePath;
							let arrImages = [this.posterbackgd, arrImagesUrlTop, this.PromotionCode];
							setTimeout(() => {
								this.$util.activityCanvas(arrImages, this.storeCombination.title,
									this.storeCombination.price, this.storeCombination.people +
									'人团', '还差' + this.count + '人拼团成功', 9,
									(tempFilePath) => {
										this.imagePath = tempFilePath;
										this.canvasStatus = true;
										uni.hideLoading();
									});
							}, 500);
						}
					});
				}
				//#endif
			},
			goOrder: function() {
				var that = this;
				uni.navigateTo({
					url: '/pages/order_details/index?order_id=' + that.currentPinkOrder
				});
			},
			//拼团列表
			goList: function() {
				uni.navigateTo({
					url: '/pages/activity/goods_combination/index'
				});
			},
			//拼团详情
			goDetail: function(id) {
				this.pinkId = id;
				uni.navigateTo({
					url: '/pages/activity/goods_combination_details/index?id=' + id
				});
			},
			// 商品图片转base64
			getImageBase64: function(images) {
				let that = this;
				imageBase64({
					url: images
				}).then(res => {
					that.imgTop = res.data.code;
				})
			},
			// 生成二维码；
			make() {
				let href = location.protocol + '//' + location.host +
					'/pages/activity/goods_combination_status/index?id=' + this.pinkId + "&spread=" + this.uid;
				uQRCode.make({
					canvasId: 'qrcode',
					text: href,
					size: this.qrcodeSize,
					margin: 10,
					success: res => {
						this.PromotionCode = res;
					},
					complete: () => {},
					fail: res => {
						this.$util.Tips({
							title: '海报二维码生成失败！'
						});
					}
				})
			},
			//拼团信息
			getCombinationPink: function() {
				var that = this;
				getCombinationPink(that.pinkId)
					.then(res => {
						let storeCombination = res.data.storeCombination;
						res.data.pinkT.stop_time = parseInt(res.data.pinkT.stopTime);
						that.$set(that, 'storeCombination', storeCombination);
						that.$set(that.attr.productSelect, 'num', storeCombination.totalNum);
						that.$set(that, 'pinkT', res.data.pinkT);
						that.$set(that, 'pinkAll', res.data.pinkAll);
						that.$set(that, 'count', res.data.count);
						that.$set(that, 'userBool', res.data.userBool);
						that.$set(that, 'pinkBool', res.data.pinkBool);
						that.$set(that, 'isOk', res.data.isOk);
						that.$set(that, 'currentPinkOrder', res.data.currentPinkOrder);
						that.$set(that, 'userInfo', res.data.userInfo);
						that.onceNum = storeCombination.onceNum;
						that.attr.productAttr = storeCombination.productAttr;
						that.productValue = storeCombination.productValue;
						//#ifdef H5
						this.getImageBase64(storeCombination.image);
						that.make();

						that.setOpenShare();
						//#endif
						that.setProductSelect();
						if (that.attr.productAttr != 0) that.DefaultSelect();

					})
					.catch(err => {
						if (that.isLogin) {
							that.$util.Tips({
								title: err
							}, {
								url: '/pages/index/index'
							});
						}
					});
			},
			//#ifdef H5
			setOpenShare() {
				let that = this;
				let configTimeline = {
					title: '您的好友' + that.userInfo.nickname + '邀请您参团' + that.storeCombination.title,
					desc: that.storeCombination.title,
					link: window.location.protocol + '//' + window.location.host +
						'/pages/activity/goods_combination_status/index?id=' + that.pinkId + "&spread=" + this.uid,
					imgUrl: that.storeCombination.image
				};
				if (this.$wechat.isWeixin()) {
					this.$wechat
						.wechatEvevt(['updateAppMessageShareData', 'updateTimelineShareData', 'onMenuShareAppMessage',
							'onMenuShareTimeline'
						], configTimeline)
						.then(res => {
							console.log(res);
						})
						.catch(res => {
							if (res.is_ready) {
								res.wx.updateAppMessageShareData(configTimeline);
								res.wx.updateTimelineShareData(configTimeline);
								res.wx.onMenuShareAppMessage(configTimeline);
								res.wx.onMenuShareTimeline(configTimeline);
							}
						});
				}
			},
			//#endif
			//拼团取消
			getCombinationRemove: function() {
				var that = this;
				postCombinationRemove({
						id: that.pinkId,
						cid: that.storeCombination.id
					})
					.then(res => {
						that.$util.Tips({
							title: res.msg
						}, {
							tab: 3
						});
					})
					.catch(res => {
						that.$util.Tips({
							title: res
						});
					});
			},
			lookAll: function() {
				this.iShidden = !this.iShidden;
			}
		}
	};
</script>
<style lang="scss" scoped>
	.pinkT {
		position: relative;

		.chief {
			position: absolute;
			width: 72rpx;
			height: 30rpx;
			background-color: #E93323;
			border-radius: 15rpx;
			font-size: 20rpx;
			line-height: 30rpx;
			text-align: center;
			right: -24rpx;
			top: -16rpx;
			color: #fff;
		}
	}

	.canvas {
		position: fixed;
		opacity: 0;
	}
	.poster-pop {
		width: 594rpx;
		height: 850rpx;
		position: fixed;
		left: 50%;
		transform: translateX(-50%);
		z-index: 999;
		top: 50%;
		margin-top: -466rpx;
	
		image {
			width: 100%;
			height: 100%;
			display: block;
			border-radius: 10rpx;
		}
	
		.close {
			text-align: center;
			margin-top: 55rpx;
			color: #fff;
			font-size: 52rpx;
		}
	
		.save-poster {
			background-color: #df2d0a;
			font-size: ：22rpx;
			color: #fff;
			text-align: center;
			height: 76rpx;
			line-height: 76rpx;
			width: 100%;
		}
	
		.keep {
			color: #fff;
			text-align: center;
			font-size: 25rpx;
			margin-top: 25rpx;
		}
	}
	
	/*开团*/
	.group-con .header {
		width: 100%;
		height: 186rpx;
		background-color: #fff;
		border-top: 1px solid #f5f5f5;
		padding: 0 30rpx;
		position: relative;
	}

	.group-con .header .iconfont {
		font-size: 100rpx;
		position: absolute;
		color: #ccc;
		right: 33rpx;
		bottom: 20rpx;
	}

	.group-con .header .pictrue {
		width: 140rpx;
		height: 140rpx;
	}

	.group-con .header .pictrue img {
		width: 100%;
		height: 100%;
		border-radius: 6rpx;
	}

	.group-con .header .text {
		width: 540rpx;
		font-size: 30rpx;
		color: #222;
	}

	.group-con .header .text .money {
		font-size: 24rpx;
		font-weight: bold;
		margin-top: 15rpx;
	}

	.group-con .header .text .money .num {
		font-size: 32rpx;
	}

	.group-con .header .text .money .team {
		padding: 1rpx 10rpx;
		font-weight: normal;
		border-radius: 50rpx;
		font-size: 20rpx;
		vertical-align: 4rpx;
		margin-left: 15rpx;
	}

	.group-con .wrapper {
		background-color: #fff;
		margin-top: 20rpx;
		padding: 2rpx 0 35rpx 0;
	}

	.group-con .wrapper .title {
		margin-top: 30rpx;
	}

	.group-con .wrapper .title .line {
		width: 136rpx;
		height: 1px;
		background-color: #ddd;
	}

	.group-con .wrapper .title .name {
		margin: 0 45rpx;
		font-size: 28rpx;
		color: #282828;
	}

	.group-con .wrapper .title .name .time {
		margin: 0 14rpx;
	}

	.group-con .wrapper .title .name .timeTxt {
		color: #fc4141;
	}

	.group-con .wrapper .title .name .time .styleAll {
		background-color: #ffcfcb;
		text-align: center;
		border-radius: 3rpx;
		font-size: 28rpx;
		font-weight: bold;
		display: inline-block;
		vertical-align: middle;
		color: #fc4141;
		padding: 2rpx 5rpx;
	}

	.group-con .wrapper .tips {
		font-size: 30rpx;
		font-weight: bold;
		text-align: center;
		margin-top: 30rpx;
		color: #999;
	}

	.group-con .wrapper .list {
		padding: 0 30rpx;
		margin-top: 45rpx;
	}

	.group-con .wrapper .list.result {
		max-height: 240rpx;
	}

	.group-con .wrapper .list.result.on {
		max-height: 2000rpx;
	}

	.group-con .wrapper .list .pictrue {
		width: 94rpx;
		height: 94rpx;
		margin: 0 0 29rpx 35rpx;
	}

	.group-con .wrapper .list .pictrue img {
		width: 100%;
		height: 100%;
		border-radius: 50%;
		border: 2rpx solid #e93323;
	}

	.group-con .wrapper .list .pictrue img.img-none {
		border: none;
	}

	.group-con .wrapper .lookAll {
		font-size: 24rpx;
		color: #282828;
		padding-top: 10rpx;
	}

	.group-con .wrapper .lookAll .iconfont {
		font-size: 25rpx;
		margin: 2rpx 0 0 10rpx;
	}

	.group-con .wrapper .teamBnt {
		font-size: 30rpx;
		width: 620rpx;
		height: 86rpx;
		border-radius: 50rpx;
		text-align: center;
		line-height: 86rpx;
		color: #fff;
		margin: 21rpx auto 0 auto;
	}

	.group-con .wrapper .cancel,
	.group-con .wrapper .lookOrder {
		text-align: center;
		font-size: 24rpx;
		color: #282828;
		padding-top: 30rpx;
	}

	.group-con .wrapper .cancel .iconfont {
		font-size: 35rpx;
		color: #2c2c2c;
		vertical-align: -4rpx;
		margin-right: 9rpx;
	}

	.group-con .wrapper .lookOrder .iconfont {
		font-size: 25rpx;
		color: #2c2c2c;
		margin-left: 10rpx;
	}

	.group-con .group-recommend {
		background-color: #fff;
		margin-top: 25rpx;
	}

	.group-con .group-recommend .title {
		padding-right: 30rpx;
		margin-left: 30rpx;
		height: 85rpx;
		border-bottom: 1px solid #eee;
		font-size: 28rpx;
		color: #282828;
	}

	.group-con .group-recommend .title .more {
		color: #808080;
	}

	.group-con .group-recommend .title .more .iconfont {
		margin-left: 13rpx;
		font-size: 28rpx;
	}

	.group-con .group-recommend .list {
		margin-top: 30rpx;
	}

	.group-con .group-recommend .list .item {
		width: 210rpx;
		margin: 0 0 25rpx 30rpx;
	}

	.group-con .group-recommend .list .item .pictrue {
		width: 100%;
		height: 210rpx;
		position: relative;
	}

	.group-con .group-recommend .list .item .pictrue img {
		width: 100%;
		height: 100%;
		border-radius: 10rpx;
	}

	.group-con .group-recommend .list .item .pictrue .team {
		position: absolute;
		top: 28rpx;
		left: -5rpx;
		min-width: 100rpx;
		height: 36rpx;
		line-height: 36rpx;
		text-align: center;
		border-radius: 0 18rpx 18rpx 0;
		font-size: 20rpx;
		color: #fff;
		background-image: linear-gradient(to right, #fb5445 0%, #e93323 100%);
		background-image: -webkit-linear-gradient(to right, #fb5445 0%, #e93323 100%);
		background-image: -moz-linear-gradient(to right, #fb5445 0%, #e93323 100%);
	}

	.group-con .group-recommend .list .item .name {
		font-size: 28rpx;
		color: #333;
		margin-top: 0.18rem;
	}

	.group-con .group-recommend .list .item .money {
		font-weight: bold;
		font-size: 28rpx;
	}

	.share-box {
		z-index: 1000;
		position: fixed;
		left: 0;
		top: 0;
		width: 100%;
		height: 100%;

		image {
			width: 100%;
			height: 100%;
		}
	}
</style>
