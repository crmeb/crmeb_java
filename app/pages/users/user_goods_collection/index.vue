<template>
	<view>
		<view class="hdbj"></view>
		<view class='collectionGoods' v-if="collectProductList.length">
			<!-- #ifdef  H5 || MP-->
			<view class='nav acea-row row-between-wrapper'>
				<view>当前共 <text class='num font-color'>{{ totals }}</text>件商品</view>
				<view class='administrate acea-row row-center-wrapper' @click='manage'>{{ footerswitch ? '管理' : '取消'}}
				</view>
			</view>
			<!-- #endif -->
			<view class="list">
				<checkbox-group @change="checkboxChange" class="centent">
					<view v-for="(item,index) in collectProductList" :key="index" class='item acea-row row-middle'>
						<checkbox :value="item.id.toString()" :checked="item.checked" v-if="!footerswitch"
							style="margin-right: 10rpx;" />
						<navigator :url='"/pages/goods_details/index?id="+item.productId' hover-class='none'
							class="acea-row">
							<view class='pictrue'>
								<image :src="item.image"></image>
							</view>
							<view>
								<view class='name line1'>{{item.storeName}}</view>
								<view class='money font-color'>￥{{item.price}}</view>
							</view>
						</navigator>
					</view>
				</checkbox-group>
			</view>
			<view class='loadingicon acea-row row-center-wrapper'>
				<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
			</view>
			<view v-if="!footerswitch" class='footer acea-row row-between-wrapper'>
				<view>
					<checkbox-group @change="checkboxAllChange">
						<checkbox value="all" :checked="!!isAllSelect" />
						<text class='checkAll'>全选</text>
					</checkbox-group>
				</view>
				<view class='button acea-row row-middle'>
					<form @submit="delCollectionAll" report-submit='true'>
						<button class='bnt cart-color' formType="submit">取消收藏</button>
					</form>
				</view>
			</view>
		</view>
		<view class='noCommodity' v-else-if="!collectProductList.length && page > 1">
			<view class='pictrue'>
				<image src='../static/noCollection.png'></image>
			</view>
			<recommend :hostProduct="hostProduct"></recommend>
		</view>
		<!-- #ifdef MP -->
		<!-- <authorize @onLoadFun="onLoadFun" :isAuto="isAuto" :isShowAuth="isShowAuth" @authColse="authColse"></authorize> -->
		<!-- #endif -->
		<home></home>
	</view>
</template>

<script>
	import {
		getCollectUserList,
		getProductHot,
		collectDelete
	} from '@/api/store.js';
	import {
		mapGetters
	} from "vuex";
	import {
		toLogin
	} from '@/libs/login.js';
	import recommend from '@/components/recommend';
	// #ifdef MP
	import authorize from '@/components/Authorize';
	// #endif
	import home from '@/components/home';
	export default {
		components: {
			recommend,
			// #ifdef MP
			authorize,
			// #endif
			home
		},
		data() {
			return {
				footerswitch: true,
				hostProduct: [],
				loadTitle: '加载更多',
				loading: false,
				loadend: false,
				collectProductList: [],
				limit: 8,
				page: 1,
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false, //是否隐藏授权
				hotScroll: false,
				hotPage: 1,
				hotLimit: 10,
				isAllSelect: false, //全选
				selectValue: [], //选中的数据
				delBtnWidth: 80, //左滑默认宽度
				totals: 0
			};
		},
		computed: mapGetters(['isLogin']),
		onLoad() {
			if (this.isLogin) {
				this.loadend = false;
				this.page = 1;
				this.collectProductList = [];
				this.get_user_collect_product();
			} else {
				toLogin();
			}
		},
		onShow() {
			this.loadend = false;
			this.page = 1;
			this.collectProductList = [];
			this.get_user_collect_product();
		},
		methods: {
			// #ifdef MP
			drawStart(e) {
				var touch = e.touches[0];
				this.startX = touch.clientX;
			},
			//触摸滑动
			drawMove(e) {
				var touch = e.touches[0];
				var item = this.collectProductList[e.currentTarget.dataset.index];
				var disX = this.startX - touch.clientX;
				if (disX >= 20) {
					if (disX > this.delBtnWidth) {
						disX = this.delBtnWidth;
					}
					this.$set(this.collectProductList[e.currentTarget.dataset.index], 'right', disX);
				} else {
					this.$set(this.collectProductList[e.currentTarget.dataset.index], 'right', 0);
				}
			},
			//触摸滑动结束
			drawEnd(e) {
				var item = this.collectProductList[e.currentTarget.dataset.index];
				if (item.right >= this.delBtnWidth / 2) {
					this.$set(this.collectProductList[e.currentTarget.dataset.index], 'right', this.delBtnWidth);
				} else {
					this.$set(this.collectProductList[e.currentTarget.dataset.index], 'right', 0);
				}
			},
			// #endif
			manage: function() {
				this.footerswitch = !this.footerswitch;
			},

			checkboxChange: function(event) {
				var items = this.collectProductList,
					values = event.detail.value;
				for (var i = 0, lenI = items.length; i < lenI; ++i) {
					const item = items[i]
					if (values.includes(item.id.toString())) {
						this.$set(item, 'checked', true)
					} else {
						this.$set(item, 'checked', false)
					}
				}
				this.selectValue = values.toString();
				this.isAllSelect = items.length === values.length;
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
				let selectValue = [];
				if (this.collectProductList.length > 0) {
					this.collectProductList.map(item => {
						if (status) {
							this.$set(item, 'checked', true)
							selectValue.push(item.id);
							this.isAllSelect = true;
						} else {
							this.$set(item, 'checked', false)
							this.isAllSelect = false;
						}
					});
					this.selectValue = selectValue.toString();
				}
			},
			/**
			 * 授权回调
			 */
			onLoadFun: function() {
				this.get_user_collect_product();
				this.get_host_product();
			},
			// 授权关闭
			authColse: function(e) {
				this.isShowAuth = e
			},
			/**
			 * 获取收藏产品
			 */
			get_user_collect_product: function() {
				let that = this;
				if (this.loading) return;
				if (this.loadend) return;
				that.loading = true;
				that.loadTitle = "";
				getCollectUserList({
					page: that.page,
					limit: that.limit
				}).then(res => {
					res.data.list.map(item => {
						that.$set(item, 'right', 0);
					});
					that.totals = res.data.total;
					let collectProductList = res.data.list;
					let loadend = collectProductList.length < that.limit;
					that.collectProductList = that.$util.SplitArray(collectProductList, that
						.collectProductList);
					that.$set(that, 'collectProductList', that.collectProductList);
					if (that.collectProductList.length === 0) that.get_host_product();
					that.loadend = loadend;
					that.loadTitle = loadend ? '我也是有底线的' : '加载更多';
					that.page = that.page + 1;
					that.loading = false;
				}).catch(err => {
					that.loading = false;
					that.loadTitle = "加载更多";
				});
			},
			/**
			 * 取消收藏
			 */
			delCollection: function(id, index) {
				this.selectValue = id;
				this.del({
					ids: this.selectValue.toString()
				});
			},
			delCollectionAll: function() {
				if (!this.selectValue || this.selectValue.length == 0) return this.$util.Tips({
					title: '请选择商品'
				});
				this.del({
					ids: this.selectValue
				});
			},
			del: function(data) {
				collectDelete(data).then(res => {
					this.$util.Tips({
						title: '取消收藏成功',
						icon: 'success'
					});
					// this.collectProductList = this.collectProductList.filter(item=>item!==this.selectValue)
					this.collectProductList = [];
					this.loadend = false;
					this.page = 1;
					this.get_user_collect_product();
				}).catch(err => {
					return this.$util.Tips({
						title: err
					})
				});
			},
			/**
			 * 获取我的推荐
			 */
			get_host_product: function() {
				let that = this;
				if (that.hotScroll) return
				getProductHot(
					that.hotPage,
					that.hotLimit,
				).then(res => {
					that.hotPage++
					that.hotScroll = res.data.list.length < that.hotLimit
					that.hostProduct = that.hostProduct.concat(res.data.list)
				});
			}
		},
		/**
		 * 页面上拉触底事件的处理函数
		 */
		onReachBottom() {
			this.get_user_collect_product();
			this.get_host_product();
		}
	}
</script>

<style scoped lang="scss">

	.hdbj {
		width: 100%;
		height: 30rpx;
		background-color: #f5f5f5;
		z-index: 999999;
		position: fixed;
		top: 0;
	}

	.order-item {
		width: 100%;
		display: flex;
		position: relative;
		align-items: right;
		flex-direction: row;
	}

	.remove {
		width: 120rpx;
		height: 100%;
		background-color: $theme-color;
		color: white;
		position: absolute;
		top: 0;
		right: -160rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: 24rpx;
	}

	.collectionGoods {

		.nav {
			width: 92%;
			height: 90rpx;
			background-color: #fff;
			padding: 0 24rpx;
			-webkit-box-sizing: border-box;
			box-sizing: border-box;
			font-size: 28rpx;
			color: #282828;
			position: fixed;
			left: 30rpx;
			z-index: 5;
			top: 30rpx;
			border-bottom: 1px solid #EEEEEE;
			border-top-left-radius: 14rpx;
			border-top-right-radius: 14rpx;
		}

		.list {
			padding: 30rpx;
			margin-top: 90rpx;

			.name {
				width: 434rpx;
				margin-bottom: 56rpx;
			}
		}

		.centent {
			/* #ifdef H5 || MP */
			background-color: #fff;
			/* #endif */
			border-bottom-left-radius: 14rpx;
			border-bottom-right-radius: 14rpx;
		}
	}

	.collectionGoods .item {
		background-color: #fff;
		padding-left: 24rpx;
		height: 180rpx;
		margin-bottom: 15rpx;
		border-radius: 14rpx;

	}

	.collectionGoods .item .pictrue {
		width: 130rpx;
		height: 130rpx;
		margin-right: 20rpx;
	}

	.collectionGoods .item .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 14rpx;
	}

	.collectionGoods .item .text {
		width: 535rpx;
		height: 130rpx;
		font-size: 28rpx;
		color: #282828;
	}

	.collectionGoods .item .text .name {
		width: 100%;
	}

	.collectionGoods .item .text .money {
		font-size: 26rpx;
	}

	.collectionGoods .item .text .delete {
		font-size: 26rpx;
		color: #282828;
		width: 144rpx;
		height: 46rpx;
		border: 1px solid #bbb;
		border-radius: 4rpx;
		text-align: center;
		line-height: 46rpx;
	}

	.noCommodity {
		background-color: #fff;
		padding-top: 1rpx;
		border-top: 0;
	}

	.footer {
		z-index: 9;
		width: 100%;
		height: 96rpx;
		background-color: #fff;
		position: fixed;
		padding: 0 30rpx;
		box-sizing: border-box;
		border-top: 1rpx solid #eee;
		border-bottom: 1px solid #EEEEEE;
		/* #ifdef H5 || MP */
		bottom: 0rpx;
		/* #endif */
		/* #ifndef MP */
		// bottom: 98rpx;
		// bottom: calc(98rpx+ constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		// bottom: calc(98rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
		/* #endif */
		.checkAll {
			font-size: 28rpx;
			color: #282828;
			margin-left: 16rpx;
		}

		.button .bnt {
			font-size: 28rpx;
			color: #999;
			border-radius: 30rpx;
			border: 1px solid #999;
			width: 160rpx;
			height: 60rpx;
			text-align: center;
			line-height: 60rpx;
		}
	}
</style>
