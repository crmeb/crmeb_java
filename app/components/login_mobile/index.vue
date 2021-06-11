<template>
	<view v-if="isUp">
		<view class="mobile-bg" v-if="isShow" @click="close"></view>
		<view class="mobile-mask animated" :class="{slideInUp:isUp}" :style="{position:isPos?'fixed':'static'}">
			<view class="input-item">
				<input type="text" v-model="account" placeholder="输入手机号" />
			</view>
			<view class="input-item">
				<input type="text" v-model="codeNum" placeholder="输入验证码" />
				<button class="code" :disabled="disabled" @click="code">{{text}}</button>
			</view>
			<view class="sub_btn" @click="loginBtn">{{(!userInfo.phone && isLogin) || (userInfo.phone && isLogin)?'立即绑定':'立即登录'}}</view>
		</view>
	</view>
</template>

<script>
	const app = getApp();
	import sendVerifyCode from "@/mixins/SendVerifyCode";
	import Routine from '@/libs/routine';
	import {mapGetters} from "vuex";
	import {
		loginMobile,
		registerVerify,
		getCodeApi,
		getUserInfo,
		phoneSilenceAuth,
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
		computed: mapGetters(['userInfo','isLogin']),
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
			}
		},
		data() {
			return {
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
					title: !this.userInfo.phone && this.isLogin?'正在绑定中':'正在登录中'
				});
				if (!this.userInfo.phone && this.isLogin) {
					iosBinding({
						captcha: that.codeNum,
						phone: that.account
					}).then(res => {
						that.$util.Tips({
							title: '绑定手机号成功',
							icon: 'success'
						}, {
							tab: 3
						})
						that.isApp = 1;
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
						key: that.authKey
					}).then(res => {
						that.$store.commit('LOGIN', {
							token: res.data.token
						});
						that.$store.commit("SETUID", res.data.uid);
						that.getUserInfo();
					}).catch(error => {
						uni.hideLoading()
						that.$util.Tips({
							title: error
						})
					})
				}
			},
			// #ifdef MP
			phoneSilenceAuth(code) {
				let self = this
				phoneSilenceAuth({
					code: code,
					spid: app.globalData.spid,
					spread: app.globalData.code,
					phone: this.account,
					captcha: this.codeNum
				}).then(res => {
					this.$store.commit('LOGIN', res.data.token);
					this.$store.commit("SETUID", res.data.uid);
					this.getUserInfo();
				}).catch(error => {
					self.$util.Tips({
						title: error
					})
				})
			},
			// #endif
			/**
			 * 获取个人用户信息
			 */
			getUserInfo: function() {
				let that = this;
				getUserInfo().then(res => {
					uni.hideLoading();
					that.$store.commit("UPDATE_USERINFO", res.data);
					// #ifdef MP 
					that.$util.Tips({
						title: '登录成功',
						icon: 'success'
					}, {
						tab: 3
					})
					that.close()
					// #endif
					// #ifdef H5
					that.$emit('wechatPhone', true)
					// #endif
				});
			},
		}
	}
</script>

<style lang="stylus" scoped>
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
		padding: 67rpx 30rpx;
		background: #fff;

		.input-item {
			display: flex;
			justify-content: space-between;
			width: 100%;
			height: 86rpx;
			margin-bottom: 38rpx;

			input {
				flex: 1;
				display: block;
				height: 100%;
				padding-left: 40rpx;
				border-radius: 43rpx;
				border: 1px solid #DCDCDC;
			}

			.code {
				display: flex;
				align-items: center;
				justify-content: center;
				width: 220rpx;
				height: 86rpx;
				margin-left: 30rpx;
				background: rgba(233, 51, 35, 0.05);
				font-size: 28rpx;
				color: $theme-color;
				border-radius: 43rpx;

				&[disabled] {
					background: rgba(0, 0, 0, 0.05);
					color: #999;
				}
			}
		}

		.sub_btn {
			width: 690rpx;
			height: 86rpx;
			line-height: 86rpx;
			margin-top: 60rpx;
			background: #E93323;
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
