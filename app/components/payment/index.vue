<template>
	<view>
		<view class="payment" :class="pay_close ? 'on' : ''">
			<view class="title acea-row row-center-wrapper">
				选择付款方式<text class="iconfont icon-guanbi" @click='close'></text>
			</view>
			<view class="item acea-row row-between-wrapper" @click='goPay(item.number || 0 , item.value)'
				v-for="(item,index) in payMode" :key="index" v-if="item.payStatus==1">
				<view class="left acea-row row-between-wrapper">
					<view class="iconfont" :class="item.icon"></view>
					<view class="text">
						<view class="name">{{item.name}}</view>
						<view class="info" v-if="item.number">
							{{item.title}} <span class="money">￥{{ item.number }}</span>
						</view>
						<view class="info" v-else>{{item.title}}</view>
					</view>
				</view>
				<view class="iconfont icon-xiangyou"></view>
			</view>
		</view>
		<view class="mask" @click='close' v-if="pay_close"></view>
		<view class="alipaysubmit" v-html="formContent"></view>
	</view>
</template>

<script>
	import {
		orderPay,
		wechatQueryPayResult,
		getPayConfig
	} from '@/api/order.js';
	import {
		mapGetters
	} from "vuex";
	export default {
		props: {
			pay_close: {
				type: Boolean,
				default: false,
			},
			order_id: {
				type: String,
				default: ''
			},
			totalPrice: {
				type: String,
				default: '0'
			}
		},
		data() {
			return {
				formContent:'',
				payChannel:'',
				//支付方式
				payMode: [
					{
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
					{
						"name": "余额支付",
						"icon": "icon-yuezhifu",
						value: 'yue',
						title: '可用余额:',
						payStatus: 1,
						number: 0
					},
				],
			};
		},
		computed: mapGetters(['systemPlatform','userInfo','productType']),
		created(){
			this.payConfig();
			this.payMode[1].number = this.userInfo.nowMoney;
		},
		methods: {
			close: function() {
				this.$emit('onChangeFun', {
					action: 'payClose'
				});
			},
			payConfig(){
				getPayConfig().then(res=>{
					this.payMode[1].payStatus = res.data.yuePayStatus === "'1'" ? 1 : 0;
					// #ifndef APP
					this.payMode[0].payStatus = res.data.payWeixinOpen === "'1'" ? 1 : 0;
					// #endif
				})
			},
			goPay: function(number, paytype) {
				let that = this;
				let goPages = '/pages/order/order_pay_status/index?order_id=' + that.order_id;
				if (!that.order_id) return that.$util.Tips({
					title: '请选择要支付的订单'
				});
				if (paytype == 'yue' && parseFloat(number) < parseFloat(that.totalPrice)) return that.$util.Tips({
					title: '余额不足！'
				});
				uni.showLoading({
					title: '支付中'
				});
				// #ifdef H5
				if(paytype == 'alipay'){
					that.payChannel = 'alipay';
				}else if(paytype == 'weixin' && this.$wechat.isWeixin()){
					that.payChannel = 'public';
				}else{
					that.payChannel = 'weixinh5';
				}
				// #endif
				// #ifdef APP-PLUS
				if(paytype == 'alipay'){
					that.payChannel = 'appAliPay';
				}else if(paytype == 'weixin'){
					that.payChannel = that.systemPlatform === 'ios' ? 'weixinAppIos' : 'weixinAppAndroid';
				}
				// #endif
				orderPay({
					orderNo: that.order_id,
					// #ifdef MP
					payChannel: 'routine',
					// #endif
					// #ifndef MP
					payChannel:that.payChannel,
					// #endif
					payType: paytype,
					scene: that.productType === 'normal' ? 0 : 1177 //下单时小程序的场景值
				}).then(res => {
					let jsConfig = res.data.jsConfig;
					that.order_id = res.data.orderNo;
					switch (res.data.payType) {
						case 'weixin':
						that.weixinPay(jsConfig);
							break;
						case 'yue':
							uni.hideLoading();
							return that.$util.Tips({
								title: '余额支付成功',
								icon: 'success'
							}, () => {
								that.$emit('onChangeFun', {
									action: 'pay_complete'
								});
							});
							break;
						case 'weixinh5':
							uni.hideLoading();
							location.replace(jsConfig.mwebUrl + '&redirect_url=' + window.location.protocol +
								'//' + window.location.host + goPages + '&status=1');
							return that.$util.Tips({
								title: "支付中",
								icon: 'success'
							}, () => {
								that.$emit('onChangeFun', {
									action: 'pay_complete'
								});
							});
							break;
						case 'alipay':
							//#ifdef H5
							if (this.$wechat.isWeixin()) {
								//微信公众号内支付
							} else {
								//h5支付
								uni.hideLoading();
								that.formContent = res.data.alipayRequest;
								uni.setStorage({key: 'orderNo', data:that.order_id});
								that.$nextTick(() => {
									document.forms['punchout_form'].submit();
								})
							}
							//#endif
							// #ifdef APP-PLUS
							let alipayRequest = res.data.alipayRequest;
							uni.requestPayment({
								provider: 'alipay',
								orderInfo: alipayRequest,
								success: (e) => {
									uni.showToast({
										title: "支付成功"
									})
									setTimeout(res => {
										that.$emit('onChangeFun', {
											action: 'pay_complete'
										});
									}, 2000)
								},
								fail: (e) => {
									uni.showModal({
										content: "支付失败",
										showCancel: false,
										success: function(res) {
											if (res.confirm) {
												//点击确认的操作
												that.$emit('onChangeFun', {
													action: 'pay_fail'
												});
											} else if (res.cancel) {
												
											}
										}
									})
								},
								complete: () => {
									uni.hideLoading();
								},
							});
							// #endif
							break;
					}
				}).catch(err => {
					uni.hideLoading();
					return that.$util.Tips({
						title: err
					}, () => {
						that.$emit('onChangeFun', {
							action: 'pay_fail'
						});
					});
				})
			},
			weixinPay(jsConfig){
				let that = this;
				// #ifdef MP
                uni.requestOrderPayment({
                    timeStamp: jsConfig.timeStamp,
                    nonceStr: jsConfig.nonceStr,
                    package: jsConfig.packages,
                    signType: jsConfig.signType,
                    paySign: jsConfig.paySign,
                    ticket: jsConfig.ticket,
                    success: function(ress) {
                        uni.hideLoading();
                        wechatQueryPayResult(that.order_id).then(res => {
                            uni.hideLoading();
                            return that.$util.Tips({
                                title: "支付成功",
                                icon: 'success'
                            }, () => {
                                that.$emit('onChangeFun', {
                                    action: 'pay_complete'
                                });
                            });
                        }).catch(err => {
                            uni.hideLoading();
                            return that.$util.Tips({
                                title: err
                            });
                        })
                    },
                    fail: function(e) {
                        uni.hideLoading();
                        return that.$util.Tips({
                            title: '取消支付'
                        }, () => {
                            that.$emit('onChangeFun', {
                                action: 'pay_fail'
                            });
                        });
                    },
                    complete: function(e) {
                        uni.hideLoading();
                        if (e.errMsg == 'requestPayment:cancel') return that.$util
                            .Tips({
                                title: '取消支付'
                            }, () => {
                                that.$emit('onChangeFun', {
                                    action: 'pay_fail'
                                });
                            });
                    },
                })
				// #endif
				// #ifdef H5
				let datas = {
					timestamp: jsConfig.timeStamp,
					nonceStr: jsConfig.nonceStr,
					package: jsConfig.packages,
					signType: jsConfig.signType,
					paySign: jsConfig.paySign
				};
				that.$wechat.pay(datas).then(res => {
					if (res.errMsg == 'chooseWXPay:cancel') {
						uni.hideLoading();
						return that.$util.Tips({
							title: '支付失败'
						});
					} else {
						wechatQueryPayResult(that.order_id).then(res => {
							uni.hideLoading();
							return that.$util.Tips({
								title: "支付成功",
								icon: 'success'
							}, () => {
								that.$emit('onChangeFun', {
									action: 'pay_complete'
								});
							});
						}).catch(err => {
							uni.hideLoading();
							return that.$util.Tips({
								title: err
							});
						})
					}
				
				}).catch(errW => {
					uni.hideLoading();
					return that.$util.Tips({
						title: errW
					});
				})
				// #endif
				// #ifdef APP-PLUS
				uni.requestPayment({
					provider: 'wxpay',
					orderInfo: {
						"appid": jsConfig.appId, // 微信开放平台 - 应用 - AppId，注意和微信小程序、公众号 AppId 可能不一致
						"noncestr": jsConfig.nonceStr, // 随机字符串
						"package": "Sign=WXPay", // 固定值
						"partnerid": jsConfig.partnerid, // 微信支付商户号
						"prepayid": jsConfig.packages, // 统一下单订单号 
						"timestamp": Number(jsConfig.timeStamp), // 时间戳（单位：秒）
						"sign": this.systemPlatform === 'ios' ? 'MD5' : jsConfig
							.paySign // 签名，这里用的 MD5 签名
					}, //订单数据 【注意微信的订单信息，键值应该全部是小写，不能采用驼峰命名】
					success: (e) => {
						uni.hideLoading();
						let url = '/pages/order/order_pay_status/index?order_id=' + that.order_id;
						uni.showToast({
							title: "支付成功"
						})
						setTimeout(res => {
							that.$emit('onChangeFun', {
								action: 'pay_complete'
							});
						}, 2000)
					},
					fail: (e) => {
						uni.hideLoading();
						uni.showModal({
							content: "支付失败",
							showCancel: false,
							success: function(res) {
								if (res.confirm) {
									that.$emit('onChangeFun', {
										action: 'pay_fail'
									});
								} else if (res.cancel) {
									console.log('用户点击取消');
								}
							}
						})
					},
					complete: () => {
						uni.hideLoading();
					},
				});
				// #endif
			}
		}
	}
</script>

<style scoped lang="scss">
	.payment {
		position: fixed;
		bottom: 0;
		left: 0;
		width: 100%;
		border-radius: 16rpx 16rpx 0 0;
		background-color: #fff;
		padding-bottom: 60rpx;
		z-index: 99;
		transition: all 0.3s cubic-bezier(0.25, 0.5, 0.5, 0.9);
		transform: translate3d(0, 100%, 0);
	}

	.payment.on {
		transform: translate3d(0, 0, 0);
	}

	.payment .title {
		text-align: center;
		height: 123rpx;
		font-size: 32rpx;
		color: #282828;
		font-weight: bold;
		padding-right: 30rpx;
		margin-left: 30rpx;
		position: relative;
		border-bottom: 1rpx solid #eee;
	}

	.payment .title .iconfont {
		position: absolute;
		right: 30rpx;
		top: 50%;
		transform: translateY(-50%);
		font-size: 43rpx;
		color: #8a8a8a;
		font-weight: normal;
	}

	.payment .item {
		border-bottom: 1rpx solid #eee;
		height: 130rpx;
		margin-left: 30rpx;
		padding-right: 30rpx;
	}

	.payment .item .left {
		width: 610rpx;
	}

	.payment .item .left .text {
		width: 540rpx;
	}

	.payment .item .left .text .name {
		font-size: 32rpx;
		color: #282828;
	}

	.payment .item .left .text .info {
		font-size: 24rpx;
		color: #999;
	}

	.payment .item .left .text .info .money {
		color: #ff9900;
	}

	.payment .item .left .iconfont {
		font-size: 45rpx;
		color: #09bb07;
	}

	.payment .item .left .iconfont.icon-zhifubao {
		color: #00aaea;
	}

	.payment .item .left .iconfont.icon-yuezhifu {
		color: #ff9900;
	}

	.payment .item .left .iconfont.icon-yuezhifu1 {
		color: #eb6623;
	}

	.payment .item .iconfont {
		font-size: 0.3rpx;
		color: #999;
	}
</style>
