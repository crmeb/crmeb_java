<template>
	<view class="new-users">
		<view class="head">
			<view class="user-card">
				<view class="bg"></view>
				<view class="user-info">
					<image class="avatar" :src='userInfo.avatar' v-if="userInfo.avatar" @click="goEdit()"></image>
					<image v-else class="avatar" src="/static/images/f.png" mode="" @click="goEdit()"></image>
					<view class="info">
						<!-- #ifdef MP -->
						<view class="name" v-if="!userInfo.uid" @tap="openAuto">
							请点击授权
						</view>
						<!-- #endif -->
						<view class="name" v-if="userInfo.uid">
							{{userInfo.nickname}}
							<view class="vip" v-if="userInfo.vip">
								<image :src="userInfo.vipIcon" alt="">
								<view style="margin-left: 10rpx;" class="vip-txt">{{userInfo.vipName}}</view>
							</view>
						</view>
						<view class="num" v-if="userInfo.phone" @click="goEdit()">
							<view class="num-txt">ID：{{userInfo.uid}}</view>
							<view class="icon">
								<image src="/static/images/edit.png" mode=""></image>
							</view>
						</view>
						<view class="phone" v-if="!userInfo.phone && isLogin" @tap="bindPhone">绑定手机号</view>
					</view>
				</view>
				<view class="num-wrapper">
					<view class="num-item" @click="goMenuPage('/pages/users/user_money/index')">
						<text class="num">{{userInfo.nowMoney?Number(userInfo.nowMoney).toFixed(2):0}}</text>
						<view class="txt">余额</view>
					</view>
					<view class="num-item" @click="goMenuPage('/pages/users/user_integral/index')">
						<text class="num">{{userInfo.integral || 0}}</text>
						<view class="txt">积分</view>
					</view>
					<view class="num-item" @click="goMenuPage('/pages/users/user_coupon/index')">
						<text class="num">{{userInfo.couponCount || 0}}</text>
						<view class="txt">优惠券</view>
					</view>
				</view>
				<view class="sign" @click="goSignIn">签到</view>
			</view>
			<view class="order-wrapper">
				<view class="order-hd flex">
					<view class="left">订单中心</view>
					<navigator class="right flex" hover-class="none" url="/pages/users/order_list/index" open-type="navigate">
						查看全部
						<text class="iconfont icon-xiangyou"></text>
					</navigator>
				</view>
				<view class="order-bd">
					<block v-for="(item,index) in orderMenu" :key="index">
						<navigator class="order-item" hover-class="none" :url="item.url">
							<view class="pic">
								<image :src="item.img" mode=""></image>
								<text class="order-status-num" v-if="item.num > 0">{{ item.num }}</text>
							</view>
							<view class="txt">{{item.title}}</view>
						</navigator>
					</block>
				</view>
			</view>
		</view>
		<!-- 轮播 -->
		<view class="slider-wrapper" v-if="imgUrls.length>0">
			<swiper indicator-dots="true" :autoplay="autoplay" :circular="circular" :interval="interval" :duration="duration"
			 indicator-color="rgba(255,255,255,0.6)" indicator-active-color="#fff">
				<block v-for="(item,index) in imgUrls" :key="index">
					<swiper-item>
						<navigator :url='item.url' class='slide-navigator acea-row row-between-wrapper' hover-class='none'>
							<image :src="item.pic" class="slide-image"></image>
						</navigator>
					</swiper-item>
				</block>
			</swiper>
		</view>
		<!-- 会员菜单 -->
		<view class="user-menus" style="margin-top: 20rpx;">
			<block v-for="(item,index) in MyMenus" :key="index">
				<navigator class="item" :url="item.url" hover-class="none" v-if="!(item.url =='/pages/service/index' || (item.url =='/pages/users/user_spread_user/index' && !userInfo.isPromoter))">
					<view class="left">
						<image :src="item.pic"></image>
						<text>{{item.name}}</text>
					</view>
					<view class="iconfont icon-xiangyou"></view>
				</navigator>
			</block>
			<!-- #ifdef H5 -->
		<!-- 	<navigator class="item" url="/pages/customer_list/index" hover-class="none">
				<view class="left">
					<image src="/static/images/user_menu08.png"></image>
					<text>联系客服</text>
				</view>
				<view class="iconfont icon-xiangyou"></view>
			</navigator> -->
			<view class="item" @click="kefuClick">
				<view class="left">
					<image src="/static/images/user_menu08.png"></image>
					<text>联系客服</text>
				</view>
				<view class="iconfont icon-xiangyou"></view>
			</view>
			<!-- #endif -->
			<!-- #ifdef MP -->
			<button class="item" open-type='contact' hover-class='none'>
				<view class="left">
					<image src="/static/images/user_menu08.png"></image>
					<text>联系客服</text>
				</view>
				<view class="iconfont icon-xiangyou"></view>
			</button>
			<!-- #endif -->
		</view>
		<!-- <view style="height: 50rpx;"></view> -->
		<img src="/static/images/support.png" alt="" class='support'>

		<!-- #ifdef MP -->
		<authorize @onLoadFun="onLoadFun" :isAuto="isAuto" :isShowAuth="isShowAuth" @authColse="authColse"></authorize>
		<!-- #endif -->
	</view>
</template>
<script>
	import {
		getMenuList,
		getUserInfo
		// setVisit
	} from '@/api/user.js';
	import {
		orderData
	} from '@/api/order.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	// #ifdef MP
	import authorize from '@/components/Authorize';
	// #endif
	const app = getApp();
	export default {
		components: {
			// #ifdef MP
			authorize
			// #endif
		},
		computed: mapGetters(['isLogin']),
		data() {
			return {
				orderMenu: [{
						img: '/static/images/order1.png',
						title: '待付款',
						url: '/pages/users/order_list/index?status=0',
						num: 0
					},
					{
						img: '/static/images/order2.png',
						title: '待发货',
						url: '/pages/users/order_list/index?status=1',
						num: 0
					},
					{
						img: '/static/images/order3.png',
						title: '待收货',
						url: '/pages/users/order_list/index?status=2',
						num: 0
					},
					{
						img: '/static/images/order4.png',
						title: '待评价',
						url: '/pages/users/order_list/index?status=3',
						num: 0
					},
					{
						img: '/static/images/order5.png',
						title: '售后/退款',
						url: '/pages/users/user_return_list/index',
						num: 0
					},
				],
				imgUrls: [],
				userMenu: [],
				autoplay: true,
				circular: true,
				interval: 3000,
				duration: 500,
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false, //是否隐藏授权
				orderStatusNum: {},
				userInfo: {},
				MyMenus: [],
				wechatUrl: []
			}
		},
		onLoad() {
			let that = this;
			that.$set(that, 'MyMenus', app.globalData.MyMenus);
		},
		onShow: function() {
			let that = this;
			if (that.isLogin) {
				this.getUserInfo();
				this.getMyMenus();
				// this.setVisit();
				this.getOrderData();
			}else{
				// #ifdef H5 || APP-PLUS
				if (that.isLogin == false) {
					toLogin();
				}
				// #endif
			}
		},
		methods: {
			// 记录会员访问
			// setVisit(){
			// 	setVisit({
			// 		url:'/pages/user/index'
			// 	}).then(res=>{})
			// },
			kefuClick(){
				location.href = this.wechatUrl[0].wap_url
				// return this.$util.Tips({
				// 	title: '客服功能正在开发中......'
				// });
			},
			getOrderData(){
				let that = this;
				orderData().then(res=>{
					that.orderMenu.forEach((item, index) => {
						switch (item.title) {
							case '待付款':
								item.num = res.data.unPaidCount
								break
							case '待发货':
								item.num = res.data.unShippedCount
								break
							case '待收货':
								item.num = res.data.receivedCount
								break
							case '待评价':
								item.num = res.data.evaluatedCount
								break
							case '售后/退款':
								item.num = res.data.refundCount
								break
						}
					})
					that.$set(that,'orderMenu',that.orderMenu);
				})
			},
			// 打开授权
			openAuto() {
				this.isAuto = true;
				this.isShowAuth = true
			},
			// 授权回调
			onLoadFun() {
				this.getUserInfo();
				this.getMyMenus();
				// this.setVisit();
				this.getOrderData();
			},
			Setting: function() {
				uni.openSetting({
					success: function(res) {
						console.log(res.authSetting)
					}
				});
			},
			// 授权关闭
			authColse: function(e) {
				this.isShowAuth = e
			},
			// 绑定手机
			bindPhone() {
				uni.navigateTo({
					url: '/pages/users/user_phone/index'
				})
			},
			/**
			 * 获取个人用户信息
			 */
			getUserInfo: function() {
				let that = this;
				getUserInfo().then(res => {
					that.userInfo = res.data;
					that.$store.commit("SETUID", res.data.uid);
				});
			},
			/**
			 * 
			 * 获取个人中心图标
			 */
			getMyMenus: function() {
				let that = this;
				if (this.MyMenus.length) return;
				getMenuList().then(res => {
					that.$set(that, 'MyMenus', res.data.routine_my_menus);
					// location.pathname.indexOf('auth') !== -1
					// console.log( res.data.routine_my_menus.filter( item => {
					// 	if( item.url.indexOf('service') !== -1 ) return item.wap_url
					// }))
					this.wechatUrl = res.data.routine_my_menus.filter((item) => {
						return item.url.indexOf('service') !== -1
					})
					console.log(this.wechatUrl)
					this.imgUrls = res.data.routine_my_banner
				});
			},
			// 编辑页面
			goEdit() {
				uni.navigateTo({
					url: '/pages/users/user_info/index'
				})
			},
			// 签到
			goSignIn() {
				uni.navigateTo({
					url: '/pages/users/user_sgin/index'
				})
			},
			// goMenuPage
			goMenuPage(url){
				if(this.isLogin){
					uni.navigateTo({
						url
					})
				}else{
					// #ifdef MP
						this.openAuto()
					// #endif
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	.support {
		width: 219rpx;
		height: 74rpx;
		margin: 54rpx auto;
		display: block;
	}

	.new-users {
		.head {
			padding-top: 30rpx;
			background: #fff;

			.user-card {
				position: relative;
				width: 710rpx;
				height: 340rpx;
				margin: 0 auto;
				padding: 35rpx 28rpx;
				background: linear-gradient(90deg, $bg-star 0%, $bg-end 100%);
				box-shadow: 0px 10rpx 20rpx 0px rgba(255, 2, 0, 0.2);
				border-radius: 24rpx;

				.bg {
					position: absolute;
					left: 0;
					top: 0;
					width: 100%;
					height: 100%;
					background-image: url('~@/static/images/user_bg.png');
					background-size: 100% 100%;
				}

				.user-info {
					z-index: 20;
					position: relative;
					display: flex;

					.avatar {
						width: 120rpx;
						height: 120rpx;
						border-radius: 50%;
					}

					.info {
						flex: 1;
						display: flex;
						flex-direction: column;
						justify-content: space-between;
						margin-left: 20rpx;
						padding: 15rpx 0;

						.name {
							display: flex;
							align-items: center;
							color: #fff;
							font-size: 31rpx;

							.vip {
								display: flex;
								align-items: center;
								height: 36rpx;
								padding: 0 20rpx;
								background: rgba(0, 0, 0, 0.2);
								border-radius: 18px;
								font-size: 20rpx;
								margin-left: 12rpx;
								margin-top: 2rpx;

								image {
									width: 27rpx;
									height: 27rpx;
								}
							}
						}

						.num {
							display: flex;
							align-items: center;
							font-size: 26rpx;
							color: rgba(255, 255, 255, 0.6);

							image {
								width: 22rpx;
								height: 23rpx;
								margin-left: 20rpx;
							}
						}
					}
				}

				.num-wrapper {
					z-index: 30;
					position: relative;
					display: flex;
					align-items: center;
					justify-content: space-between;
					margin-top: 60rpx;
					// padding: 0 47rpx;
					color: #fff;

					.num-item {
						width: 33.33%;
						text-align: center;

						.num {
							font-size: 42rpx;
							font-weight: bold;
						}

						.txt {
							margin-top: 8rpx;
							font-size: 26rpx;
							color: rgba(255, 255, 255, 0.6);
						}
					}
				}

				.sign {
					z-index: 200;
					position: absolute;
					right: -12rpx;
					top: 80rpx;
					display: flex;
					align-items: center;
					justify-content: center;
					width: 120rpx;
					height: 60rpx;
					background: linear-gradient(90deg, rgba(255, 225, 87, 1) 0%, rgba(238, 193, 15, 1) 100%);
					border-radius: 29rpx 4rpx 4rpx 29rpx;
					color: #282828;
					font-size: 28rpx;
					font-weight: bold;
				}
			}

			.order-wrapper {
				.order-hd {
					justify-content: space-between;
					padding: 0 20rpx 28rpx;
					margin-top: 33rpx;
					border-bottom: 1px solid #F5F5F5;
					font-size: 30rpx;
					color: #282828;

					.right {
						align-items: center;
						color: #666666;
						font-size: 26rpx;

						.icon-xiangyou {
							margin-left: 5rpx;
							margin-top: 6rpx;
							font-size: 26rpx;
						}
					}
				}

				.order-bd {
					display: flex;
					padding: 0 24rpx;
					.order-item {
						display: flex;
						flex-direction: column;
						justify-content: center;
						align-items: center;
						width: 20%;
						height: 160rpx;

						.pic {
							position: relative;
							text-align: center;

							image {
								width: 48rpx;
								height: 48rpx;
							}
						}

						.txt {
							margin-top: 15rpx;
							font-size: 26rpx;
							color: #454545;
						}
					}
				}
			}
		}

		.slider-wrapper {
			margin: 20rpx 0;
			height: 130rpx;

			swiper,
			swiper-item {
				height: 100%;
			}

			image {
				width: 100%;
				height: 130rpx;
			}
		}

		.user-menus {
			background-color: #fff;

			.item {
				position: relative;
				display: flex;
				align-items: center;
				justify-content: space-between;
				height: 90rpx;
				padding: 0 30rpx;

				.left {
					display: flex;
					align-items: center;

					image {
						width: 46rpx;
						height: 46rpx;
						margin-right: 25rpx;
					}
				}

				&::before {
					content: ' ';
					position: absolute;
					right: 0;
					bottom: 0;
					width: 655rpx;
					height: 1px;
					background-color: #EEEEEE;
				}

				&:last-child::before {
					display: none;
				}
			}
			button{
				font-size: 28rpx;
			}
		}

		.phone {
			color: #fff;
		}

		.order-status-num {
			
			min-width:12rpx;
			background-color: #fff;
			color: #ee5a52;
			border-radius: 15px;
			position: absolute;
			right:-14rpx;
			top: -15rpx;
			font-size: 20rpx;
			padding: 0 8rpx;
			border: 1px solid #ee5a52;
		}
	}
</style>
