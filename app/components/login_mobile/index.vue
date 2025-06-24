<template>
	<view v-if="isUp" :data-theme="theme">
		<view class="mobile-bg" v-if="isShow" @click="close"></view>
		<view class="mobile-mask" :class="[{slideInUp:isUp},{animated:isPos}]"
			:style="{position:isPos?'fixed':'static'}">
			<view class="input-item">
				<view class="item">
					<input class="ipt" type="number" v-model="account" placeholder-class='placeholder'
						placeholder="输入手机号" maxlength="11" />
				</view>

			</view>
			<view class="input-item">
				<view class="item acea-row row-between-wrapper">
					<input class="ipt codeIput" type="number" v-model="codeNum" placeholder-class='placeholder'
						placeholder="输入验证码" maxlength="6" />
					<view class="line">

					</view>
					<button class="code font-num" :disabled="disabled" @click="code">{{text}}</button>
				</view>
			</view>
			<view class="sub_btn" @click="loginBtn">
				{{(!userInfo.phone && isLogin) || (userInfo.phone && isLogin)?'立即绑定':'立即登录'}}
			</view>
		</view>
	</view>
</template>

<script>
	const app = getApp();
	import sendVerifyCode from "@/mixins/SendVerifyCode";
	import Routine from '@/libs/routine';
	import {
		mapGetters
	} from "vuex";
	import {
		loginMobile,
		registerVerify,
		getCodeApi,
		getUserInfo,
		phoneWxSilenceAuth
	} from "@/api/user";
	import {
		bindingPhone
	} from '@/api/api.js'
	import {
		getUserPhone,
		iosBinding
	} from '@/api/public';
	const BACK_URL = "login_back_url";
	export default {
		name: 'login_mobile',
		computed: mapGetters(['userInfo', 'isLogin']),
		props: {
			isUp: {
				type: Boolean,
				default: false,
			},
			authKey: {
				type: String,
				default: '',
			},
			isShow: {
				type: Boolean,
				default: true
			},
			// 是否定位
			isPos: {
				type: Boolean,
				default: true
			},
			appleShow: {
				type: String,
				default: ''
			},
			platform: {
				type: String,
				default: '',
			},
			//小程序code值
			wxCode: {
				type: String,
				default: '',
			},
			// 小程序绑定手机号，isPhone其他手机号绑定
			loginConfig:{
				type: String,
				default: '',
			}
		},
		data() {
			return {
				theme: app.globalData.theme,
				keyCode: '',
				account: '',
				codeNum: '',
				isApp: 0
			}
		},
		mixins: [sendVerifyCode],
		mounted() {
			//this.getCode();
		},
		onLoad() {

		},
		methods: {
			// 获取验证码
			async code() {
				let that = this;
				if (!that.account) return that.$util.Tips({
					title: '请填写手机号码'
				});
				if (!/^1(3|4|5|7|8|9|6)\d{9}$/i.test(that.account)) return that.$util.Tips({
					title: '请输入正确的手机号码'
				});
				await registerVerify(that.account).then(res => {
					that.$util.Tips({
						title: res.msg
					});
					that.sendCode();
				}).catch(err => {
					return that.$util.Tips({
						title: err
					})
				})
			},
			// 获取验证码api
			getCode() {
				let that = this
				getCodeApi().then(res => {
					that.keyCode = res.data.key;
				}).catch(res => {
					that.$util.Tips({
						title: res
					});
				});
			},
			close() {
				this.$emit('close', false)
			},
			// 登录
			loginBtn() {
				let that = this
				if (!that.account) return that.$util.Tips({
					title: '请填写手机号码'
				});
				if (!/^1(3|4|5|7|8|9|6)\d{9}$/i.test(that.account)) return that.$util.Tips({
					title: '请输入正确的手机号码'
				});
				if (!that.codeNum) return that.$util.Tips({
					title: '请填写验证码'
				});
				if (!/^[\w\d]+$/i.test(that.codeNum)) return that.$util.Tips({
					title: '请输入正确的验证码'
				});
				uni.showLoading({
					title: !this.userInfo.phone && this.isLogin ? '正在绑定中' : '正在登录中'
				});
				if (!this.userInfo.phone && this.isLogin) {
					iosBinding({
						captcha: that.codeNum,
						phone: that.account
					}).then(res => {
						that.isApp = 0;
						that.onSuccess();
						that.getUserInfo();
					}).catch(error => {
						uni.hideLoading()
						that.$util.Tips({
							title: error
						})
					})
				} else {
					getUserPhone({
						captcha: that.codeNum,
						phone: that.account,
						// #ifdef H5
						type: 'public',
						// #endif
						// #ifdef MP
						type: 'routine',
						code: this.wxCode,
						// #endif
						// #ifdef APP-PLUS
						type: that.platform === 'ios' ? 'iosWx' : 'androidWx',
						// #endif
						key: that.authKey
					}).then(res => {
						that.$store.commit('LOGIN', {
							token: res.data.token
						});
						that.$store.commit("SETUID", res.data.uid);
						that.onSuccess();
						that.getUserInfo();
					}).catch(error => {
						uni.hideLoading()
						that.$util.Tips({
							title: error
						})
					})
				}
			},
			/**
			 * 登录成功后的方法
			 */
			onSuccess(){
				uni.hideLoading();
				let backUrl = this.$Cache.get(BACK_URL) || "/pages/index/index";
				// #ifdef MP
				this.$util.Tips({
					title: '绑定手机号成功'
				}, {
					tab: 4,
					url: backUrl
				});
				this.close();
				// #endif
				// #ifdef H5
				this.$emit('wechatPhone', true)
				// #endif
				// #ifdef APP-PLUS
				if (this.isApp == 0) {
					if (backUrl.indexOf('/pages/users/login/index') !== -1) {
						backUrl = '/pages/index/index';
					}
					uni.reLaunch({
						url: backUrl
					});
				}
				// #endif
			},
			/**
			 * 获取个人用户信息
			 */
			getUserInfo: function() {
				let that = this;
				getUserInfo().then(res => {
					uni.hideLoading();
					that.$store.commit("UPDATE_USERINFO", res.data);
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
	.mobile-bg {
		position: fixed;
		left: 0;
		top: 0;
		width: 100%;
		height: 100%;
		background: rgba(0, 0, 0, 0.5);
	}

	.isPos {
		position: static;
	}

	.mobile-mask {
		z-index: 20;
		// position: fixed;
		left: 0;
		bottom: 0;
		width: 100%;
		padding: 67rpx 72rpx;
		background: #fff;

		.input-item {
			display: flex;
			justify-content: space-between;
			width: 606rpx;
			height: 88rpx;
			margin-bottom: 38rpx;

			.codeIput {
				width: 300rpx !important;
			}

			.ipt {
				width: 100%;
				height: 100%;
				font-size: 32rpx;
			}

			.item {
				width: 100%;
				background: #F5F5F5;
				flex: 1;
				height: 100%;
				padding: 0 32rpx 0 48rpx;
				border-radius: 45rpx;
				font-size: 28rpx;
				font-family: PingFang SC-Regular, PingFang SC;
				font-weight: 400;
				color: #333333;
				line-height: 40px;

				.placeholder {
					color: #BBBBBB;
					font-size: 28rpx;
				}
			}

			.line {
				width: 2rpx;
				height: 28rpx;
				background: #CCCCCC;
			}

			.code {
				background: none;
				font-size: 28rpx;
				@include main_color(theme);
				border-radius: 43rpx;

				&[disabled] {
					color: #999;
				}
			}
		}

		.sub_btn {
			width: 100%;
			height: 86rpx;
			line-height: 86rpx;
			margin-top: 60rpx;
			@include main_bg_color(theme);
			border-radius: 43rpx;
			color: #fff;
			font-size: 28rpx;
			text-align: center;
		}
	}

	.animated {
		animation-duration: .4s
	}
</style>