<template>
	<view :data-theme="theme" class="user_payment">
		<form @submit="submitSub" report-submit='true'>
			<view class="payment-top acea-row row-column row-center-wrapper">
				<span class="name1">我的余额</span>
				<view class="pic">
					￥<span class="pic-font">{{ userInfo.nowMoney || 0 }}</span>
				</view>
			</view>
			<view class="payment">
				<view class="nav acea-row row-around row-middle">
					<view class="item" :class="active==index?'on':''" v-for="(item,index) in navRecharge" :key="index" @click="navRecharges(index)">{{item}}</view>
				</view>
				<view class='tip picList' v-if='!active'>
					<view class="pic-box pic-box-color acea-row row-center-wrapper row-column" :class="activePic === index ? 'pic-box-color-active' : ''"
					 v-for="(item, index) in picList" :key="index" @click="picCharge(index, item)">
						<view class="pic-number-pic">
							{{ item.price }}<span class="pic-number"> 元</span>
						</view>
						<view class="pic-number">赠送：{{ item.giveMoney }} 元</view>
					</view>
					<view class="pic-box pic-box-color acea-row row-center-wrapper" :class="parseFloat(activePic)===parseFloat(picList.length)?'pic-box-color-active':''" @click="picCharge(picList.length)">
						<input type="number" placeholder="其他" v-model="money" maxlength="5" class="pic-box-money pic-number-pic uni-input" :class="parseFloat(activePic) === parseFloat(picList.length) ? 'pic-box-color-active' : ''" @blur="addMoney()" />
					</view>
					<view class="tips-box">
						<view class="tips mt-30">注意事项：</view>
						<view class="tips-samll" v-for="item in rechargeAttention" :key="item">
							{{ item }}
						</view>
					</view>
				</view>
				<view class="tip" v-else>
					<view class='input'><text>￥</text>
					<input placeholder="0.00" type='number' placeholder-class='placeholder' :value="number"
						 name="number"></input></view>
					<view class="tips-title">
						<view style="font-weight: bold; font-size: 26rpx;">提示：</view>
						<view style="margin-top: 10rpx;">当前佣金为 <text class='font-color'>￥{{userInfo.brokeragePrice || 0}}</text></view>
					</view>
					<view class="tips-box">
						<view class="tips mt-30">注意事项：</view>
						<view class="tips-samll" v-for="item in rechargeAttention" :key="item">
							{{ item }}
						</view>
					</view>
				</view>
				<!-- #ifndef  MP-->
				<view class='wrapper borRadius14  px-30'  v-if='!active'>
					<view class='item'>
						<view>支付方式</view>
						<view class='list'>
							<view class='payItem acea-row row-middle' :class='curActive==index ?"on":""'
								@tap='payItem(index)' v-for="(item,index) in cartArr" :key='index'
								v-if="item.payStatus===1">
								<view class='name acea-row row-center-wrapper'>
									<view class='iconfont animated'
										:class='(item.icon) + " " + (animated==true&&active==index ?"bounceIn":"")'>
									</view>
									{{item.name}}
								</view>
								<view class='tip'>{{item.title}}</view>
							</view>
						</view>
					</view>
				</view>
				<!-- #endif -->
				<button class='but' formType="submit"> {{active ? '立即转入': '立即充值' }}</button>
				<view class="alipaysubmit" v-html="formContent"></view>
			</view>
		</form>
	</view>
</template>

<script>
	import {
		rechargeRoutine,
		rechargeWechat,
		getRechargeApi,
		transferIn,
		appWechat,
	} from '@/api/user.js';
	import { wechatQueryPayResult,getOrderPayConfig} from '@/api/order.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import {Debounce} from '@/utils/validate.js'
	let app = getApp();
	export default {
		data() {
			let that = this;
			return {
				now_money: 0,
				navRecharge: ['账户充值', '佣金转入'],
				active: 0,
				number: '',
				placeholder: "0.00",
				from: '',
				picList: [],
				activePic: 0,
				money: "",
				numberPic: '',
				rechar_id: 0,
				rechargeAttention: [],
				theme:app.globalData.theme,
				//支付方式
				cartArr: [{
						"name": "微信支付",
						"icon": "icon-weixin2",
						value: 'weixin',
						title: '微信快捷支付',
						// #ifdef APP
						payStatus: 0,
						// #endif
						// #ifndef APP
						payStatus: 1,
						// #endif
					},
				],
				payType: 'weixin', //支付方式
				openType: 1, //优惠券打开方式 1=使用
				curActive: 0, //支付方式切换
				animated: false,
				formContent:''
			};
		},
		computed: mapGetters(['isLogin', 'systemPlatform','userInfo']),
		watch:{
			isLogin:{
				handler:function(newV,oldV){
					if(newV){
						this.getRecharge();
					}
				},
				deep:true
			}
		},
		onLoad(options) {
			// #ifdef H5
			this.from = this.$wechat.isWeixin() ? "public" : "weixinh5";
			// #endif
			// #ifdef APP-PLUS
			this.from = this.systemPlatform === 'ios' ? 'weixinAppIos' : 'weixinAppAndroid';
			// #endif
			if (this.isLogin) {
				this.getRecharge();
				this.payConfig();
			} else {
				toLogin();
			}
		},
		methods: {
			/**
			 * 选择金额
			 */
			picCharge(idx, item) {
				this.activePic = idx;
				if (item === undefined) {
					this.rechar_id = 0;
					this.numberPic = "";
				} else {
					this.money = "";
					this.rechar_id = item.id;
					this.numberPic = item.price;
				}
			},


			/**
			 * 充值额度选择
			 */
			getRecharge() {
				getRechargeApi()
					.then(res => {
						this.picList = res.data.rechargeQuota;
						if (this.picList[0]) {
							this.rechar_id = this.picList[0].id;
							this.numberPic = this.picList[0].price;
						}
						this.rechargeAttention = res.data.rechargeAttention || [];
					})
					.catch(res => {
						this.$dialog.toast({
							mes: res
						});
					});
			},
			// 支付配置
			payConfig(){
				getOrderPayConfig().then(res=>{
					this.cartArr[0].payStatus = res.data.payWechatOpen ? 1 : 0;
					// #ifndef MP
					// this.cartArr[1].payStatus = res.data.aliPayStatus ? 1 : 0;
					// #endif
					if(this.$wechat.isWeixin()) this.cartArr.pop();
				})
			},
			navRecharges: function(index) {
				this.active = index;
			},
			payItem: function(e) {
				let that = this;
				let active = e;
				that.curActive = active;
				that.animated = true;
				that.payType = that.cartArr[active].value;
			},
			/*
			 * 用户充值
			 */
			submitSub: Debounce(function(e) {
				let that = this
				let value = e.detail.value.number ? e.detail.value.number :that.numberPic;
				// 转入余额
				if (that.active) {
					if (parseFloat(value) < 0 || parseFloat(value) == NaN || value == undefined || value == "") {
						return that.$util.Tips({
							title: '请输入金额'
						});
					}
					uni.showModal({
						title: '转入余额',
						content: '转入余额后无法再次转出，确认是否转入余额',
						success(res) {
							if (res.confirm) {
								transferIn({
											price: parseFloat(value)
								}).then(res => {
									that.$store.commit("changInfo", {
										amount1: 'brokeragePrice',
										amount2: that.$util.$h.Sub(that.userInfo.brokeragePrice, parseFloat(value))
									});
									return that.$util.Tips({
										title: '转入成功',
										icon: 'success'
									}, {
										tab: 5,
										url: '/pages/users/user_money/index'
									});
								}).catch(err=>{
									return that.$util.Tips({
										title: err
									});
								})
							} else if (res.cancel) {
								return that.$util.Tips({
									title: '已取消'
								});
							}
						},
					})
				} else {
					uni.showLoading({
						title: '正在支付',
					})
					let money = parseFloat(this.money);
					if (this.rechar_id == 0) {
						if (Number.isNaN(money)) {
							return that.$util.Tips({
								title: '充值金额必须为数字'
							});
						}
						if (money <= 0) {
							return that.$util.Tips({
								title: '充值金额不能为0'
							});
						}
						if (money > 50000) {
							return that.$util.Tips({
								title: '充值金额最大值为50000'
							});
						}
					} else {
						money = this.numberPic
					}
					switch (that.payType){
						case 'weixin':
						// #ifdef APP-PLUS
						appWechat({
							from: that.from,
							price: money,
							type: 0,
							rechar_id: this.rechar_id
						}).then(res => {
							uni.hideLoading();
							let jsConfig = res.data.jsConfig;
							uni.requestPayment({
								provider: 'wxpay',
								orderInfo: {
									"appid": jsConfig.appId, // 微信开放平台 - 应用 - AppId，注意和微信小程序、公众号 AppId 可能不一致
									"noncestr": jsConfig.nonceStr, // 随机字符串
									"package": "Sign=WXPay", // 固定值
									"partnerid": jsConfig.partnerid, // 微信支付商户号
									"prepayid": jsConfig.packages, // 统一下单订单号 
									"timestamp": Number(jsConfig.timeStamp), // 时间戳（单位：秒）
									"sign": this.systemPlatform === 'ios' ? 'MD5' : jsConfig.paySign // 签名，这里用的 MD5 签名
								}, //订单数据 【注意微信的订单信息，键值应该全部是小写，不能采用驼峰命名】
								success: function(res) {
									that.$store.commit("changInfo", {
										amount1: 'nowMoney',
										amount2: that.$util.$h.Add(value, that.userInfo.nowMoney)
									});
									return that.$util.Tips({
										title: '支付成功',
										icon: 'success'
									}, {
										tab: 5,
										url: '/pages/users/user_money/index'
									});
								},
								fail: function(err) {
									return that.$util.Tips({
										title: '支付失败'
									});
								},
								complete: function(res) {
									if (res.errMsg == 'requestPayment:cancel') return that.$util.Tips({
										title: '取消支付'
									});
								}
							})
						}).catch(err => {
							uni.hideLoading();
							return that.$util.Tips({
								title: err
							})
						});
						// #endif
						
						// #ifdef MP
						rechargeRoutine({
							price: money,
							type: 0,
							rechar_id: this.rechar_id
						}).then(res => {
							uni.hideLoading();
							let jsConfig = res.data.data.jsConfig;
							uni.requestPayment({
								timeStamp: jsConfig.timeStamp,
								nonceStr: jsConfig.nonceStr,
								package: jsConfig.packages,
								signType: jsConfig.signType,
								paySign: jsConfig.paySign,
								success: function(res) {
									that.$store.commit("changInfo", {
										amount1: 'nowMoney',
										amount2: that.$util.$h.Add(value, that.userInfo.nowMoney)
									});
									return that.$util.Tips({
										title: '支付成功',
										icon: 'success'
									}, {
										tab: 5,
										url: '/pages/users/user_money/index'
									});
								},
								fail: function(err) {
									return that.$util.Tips({
										title: '支付失败'
									});
								},
								complete: function(res) {
									if (res.errMsg == 'requestPayment:cancel') return that.$util.Tips({
										title: '取消支付'
									});
								}
							})
						}).catch(err => {
							uni.hideLoading();
							return that.$util.Tips({
								title: err
							})
						});
						// #endif
						// #ifdef H5
							rechargeWechat({
								price: money,
								from: that.from,
								rechar_id: that.rechar_id,
								payType: 0
							}).then(res => {
								let jsConfig = res.data.jsConfig;
								let orderNo = res.data.orderNo;
								let data = {
									timestamp:jsConfig.timeStamp,
									nonceStr:jsConfig.nonceStr,
									package:jsConfig.packages,
									signType:jsConfig.signType,
									paySign:jsConfig.paySign
								};
								if (that.from == "weixinh5") {
									uni.hideLoading();
									that.$util.Tips({
										title: '支付成功'
									}, {
										tab: 5,
										url:'/pages/users/user_money/index'
									});
									setTimeout(() => {
										location.href = jsConfig.mwebUrl;
									}, 100)
								} else {
									that.$wechat.pay(data)
										.finally(() => {
											that.$store.commit("changInfo", {
												amount1: 'nowMoney',
												amount2: that.$util.$h.Add(value, that.userInfo.nowMoney)
											});
											return that.$util.Tips({
												title: '支付成功',
												icon: 'success'
											}, {
												tab: 5,
												url: '/pages/users/user_money/index'
											});
										})
										.catch(function(err) {
											return that.$util.Tips({
												title: '支付失败'
											});
										});
								}
							}).catch(res=>{
								uni.hideLoading();
								return that.$util.Tips({
									title: res
								});
							})
							// #endif
							break;
					}
				}
			}),
			addMoney(){
				this.money = this.money.replace(/[^\d]/g,'').replace(/^0{1,}/g,'');
			}
		}
	}
</script>

<style lang="scss">
	.user_payment{
		height: 100vh;
		background-color: #fff;
	}
	.payment {
		position: relative;
		top: -60rpx;
		width: 100%;
		background-color: #fff;
		border-radius: 10rpx;
		padding-top: 25rpx;
		border-top-right-radius: 14rpx;
		border-top-left-radius: 14rpx;
	}

	.payment .nav {
		height: 75rpx;
		line-height: 75rpx;
		padding: 0 100rpx;
	}

	.payment .nav .item {
		font-size: 30rpx;
		color: #333;
	}

	.payment .nav .item.on {
		font-weight: bold;
		@include tab_border_bottom(theme);
	}

	.payment .input {
		display: flex;
		align-items: center;
		justify-content: center;
		border-bottom: 1px dashed #dddddd;
		margin: 60rpx auto 0 auto;
		padding-bottom: 20rpx;
		font-size: 56rpx;
		color: #333333;
		flex-wrap: nowrap;

	}

	.payment .input text {
		padding-left: 106rpx;
	}

	.payment .input input {
		padding-right: 106rpx;
		width: 300rpx;
		height: 94rpx;
		text-align: center;
		font-size: 70rpx;
	}

	.payment .placeholder {
		color: #d0d0d0;
		height: 100%;
		line-height: 94rpx;
	}

	.payment .tip {
		font-size: 26rpx;
		color: #888888;
		padding: 0 30rpx;
		// margin-top: 25rpx;
	}

	.payment .but {
		color: #fff;
		font-size: 30rpx;
		width: 700rpx;
		height: 86rpx;
		border-radius: 43rpx;
		margin: 50rpx auto 0 auto;
		@include linear-gradient(theme);
		line-height: 86rpx;
	}

	.payment-top {
		width: 100%;
		height: 350rpx;
		@include main_bg_color(theme);

		.name1 {
			font-size: 26rpx;
			color: rgba(255, 255, 255, 0.8);
			margin-top: -38rpx;
			margin-bottom: 30rpx;
		}

		.pic {
			font-size: 32rpx;
			color: #fff;
		}

		.pic-font {
			font-size: 78rpx;
			color: #fff;
		}
	}

	.picList {
		display: flex;
		flex-wrap: wrap;
		margin: 30rpx 0;

		.pic-box {
			width: 32%;
			height: auto;
			border-radius: 20rpx;
			margin-top: 21rpx;
			padding: 20rpx 0;
			margin-right: 12rpx;

			&:nth-child(3n) {
				margin-right: 0;
			}
		}

		.pic-box-color {
			background-color: #f4f4f4;
			color: #656565;
		}

		.pic-number {
			font-size: 22rpx;
		}

		.pic-number-pic {
			font-size: 38rpx;
			margin-right: 10rpx;
			text-align: center;
		}

	}
    .pic-box-color-active {
			@include linear-gradient(theme);
			color: #fff !important;
	}
	.tips-box {
		.tips {
			font-size: 28rpx;
			color: #333333;
			font-weight: 800;
			margin-bottom: 14rpx;
			margin-top: 20rpx;
		}

		.tips-samll {
			font-size: 24rpx;
			color: #333333;
			margin-bottom: 14rpx;
		}

		.tip-box {
			margin-top: 30rpx;
		}
	}

	.tips-title {
		margin-top: 20rpx;
		font-size: 24rpx;
		color: #333;
	}
	.wrapper .item textarea {
		background-color: #f9f9f9;
		width: auto !important;
		height: 140rpx;
		border-radius: 14rpx;
		margin-top: 30rpx;
		padding: 15rpx;
		box-sizing: border-box;
		font-weight: 400;
	}
	.px-30{
		padding-left: 30rpx;
		padding-rigt: 30rpx;
	}
	 .wrapper .item .placeholder {
		color: #ccc;
	}
	
	.wrapper .item .list {
		margin-top: 35rpx;
	}
	
	.wrapper .item .list .payItem {
		border: 1px solid #eee;
		border-radius: 14rpx;
		height: 86rpx;
		width: 95%;
		box-sizing: border-box;
		margin-top: 20rpx;
		font-size: 28rpx;
		color: #282828;
	}
	
	.wrapper .item .list .payItem.on {
		// border-color: #fc5445;
		@include coupons_border_color(theme);
		color: $theme-color;
	}
	
	.name {
		width: 50%;
		text-align: center;
		border-right: 1px solid #eee;
	}
	.name .iconfont {
		width: 44rpx;
		height: 44rpx;
		border-radius: 50%;
		text-align: center;
		line-height: 44rpx;
		background-color: #fe960f;
		color: #fff;
		font-size: 30rpx;
		margin-right: 15rpx;
	}
	.name .iconfont.icon-weixin2 {
		background-color: #41b035;
	}
	.name .iconfont.icon-zhifubao {
		background-color: #00AAEA;
	}
	.payItem .tip {
		width: 49%;
		text-align: center;
		font-size: 26rpx;
		color: #aaa;
	}
</style>