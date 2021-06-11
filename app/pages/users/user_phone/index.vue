<template>
	<view>
		<view class="ChangePassword">
			<view class="list">
				<view class="item" v-if="isNew">
					<input type='number' disabled='true' placeholder='填写手机号码1' placeholder-class='placeholder' v-model="userInfo.phone"></input>
				</view>
				<view class="item" v-if="!isNew">
					<input type='number' placeholder='填写手机号码' placeholder-class='placeholder' v-model="phone"></input>
				</view>
				<view class="item acea-row row-between-wrapper">
					<input type='number' placeholder='填写验证码' placeholder-class='placeholder' class="codeIput" v-model="captcha"></input>
					<button class="code font-color" :class="disabled === true ? 'on' : ''" :disabled='disabled' @click="code">
						{{ text }}
					</button>
				</view>
			</view>
			<button form-type="submit" v-if="isNew" class="confirmBnt bg-color" @click="next">下一步</button>
			<button form-type="submit" v-if="!isNew" class="confirmBnt bg-color"  @click="editPwd">保存</button>
		</view>
		<!-- #ifdef MP -->
		<!-- <authorize @onLoadFun="onLoadFun" :isAuto="isAuto" :isShowAuth="isShowAuth" @authColse="authColse"></authorize> -->
		<!-- #endif -->
	</view>
</template>

<script>
	import sendVerifyCode from "@/mixins/SendVerifyCode";
	import {
		registerVerify,
		bindingPhone,
		verifyCode,
		bindingVerify
	} from '@/api/api.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	// #ifdef MP
	import authorize from '@/components/Authorize';
	// #endif
	export default {
		mixins: [sendVerifyCode],
		components: {
			// #ifdef MP
			authorize
			// #endif
		},
		data() {
			return {
				phone:'',
				captcha:'',
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false, //是否隐藏授权
				key: '',
				isNew: true,
				timer: '',
				text: '获取验证码',
				nums: 60
			};
		},
		mounted() {
		      // this.timer = setInterval(this.getTimes, 1000);
		},
		computed: mapGetters(['isLogin','userInfo']),
		onLoad() {
			if (this.isLogin) {
				// verifyCode().then(res=>{
				// 	this.$set(this, 'key', res.data.key)
				// });
			} else {
				toLogin();
			}
		},
		methods: {
			getTimes(){
				this.nums = this.nums - 1;
				this.text = "剩余 " + this.nums + "s";
				if (this.nums < 0) {
				  clearInterval(this.timer);
				}
				this.text = "剩余 " + this.nums + "s";
				if (this.text < "剩余 " + 0 + "s") {
				  this.disabled = false;
				  this.text = "重新获取";
				}
			},
			onLoadFun:function(){},
			// 授权关闭
			authColse: function(e) {
				this.isShowAuth = e
			},
			next() {
				uni.hideLoading();
				this.isNew = false;
				this.captcha = '';
				clearInterval(this.timer);
				this.disabled = false;
				this.text = "获取验证码";
				uni.showLoading({
					title: '加载中',
					mask: true
				});
				if (!this.captcha) return this.$util.Tips({
					title: '请填写验证码'
				});
				bindingVerify({
					phone: this.userInfo.phone,
					captcha: this.captcha
				}).then(res => {
					uni.hideLoading();
					this.isNew = false;
					this.captcha = '';
					clearInterval(this.timer);
					this.disabled = false;
					this.text = "获取验证码";
				}).catch(err => {
					return this.$util.Tips({
						title: err
					});
					uni.hideLoading();
				})
			},
			editPwd: function() {
				let that = this;
				if (!that.phone) return that.$util.Tips({
					title: '请填写手机号码！'
				});
				if (!(/^1(3|4|5|7|8|9|6)\d{9}$/i.test(that.phone))) return that.$util.Tips({
					title: '请输入正确的手机号码！'
				});
				if (!that.captcha) return that.$util.Tips({
					title: '请填写验证码'
				});
				uni.showModal({
					title: '是否更换绑定账号',
					confirmText: '绑定',
					success(res) {
						if (res.confirm) {
							bindingPhone({
								phone: that.phone,
								captcha: that.captcha
							}).then(res => {
								return that.$util.Tips({
									title: res.message,
									icon: 'success'
								}, {
									tab: 5,
									url: '/pages/users/user_info/index'
								});
							}).catch(err => {
								return that.$util.Tips({
									title: err
								});
							})
						} else if (res.cancel) {
							return that.$util.Tips({
								title: '您已取消更换绑定！'
							}, {
								tab: 5,
								url: '/pages/users/user_info/index'
							});
						}
					}
				});
			},
			/**
			 * 发送验证码
			 * 
			 */
			async code() {
				this.nums = 60;
				uni.showLoading({
					title: '加载中',
					mask: true
				});
				let that = this;
				if(!that.isNew){
					if (!that.phone) return that.$util.Tips({
						title: '请填写手机号码！'
					});
					if (!(/^1(3|4|5|7|8|9|6)\d{9}$/i.test(that.phone))) return that.$util.Tips({
						title: '请输入正确的手机号码！'
					});
				}
				await registerVerify(that.isNew?that.userInfo.phone:that.phone).then(res => {
					that.$util.Tips({
						title: res.message
					});
					
					that.timer = setInterval(that.getTimes, 1000);
					 that.disabled = true;
					 uni.hideLoading();
				}).catch(err => {
					return that.$util.Tips({
						title: err
					});
					uni.hideLoading();
				});
			}
		}
	}
</script>

<style lang="scss" scoped>
	.shading {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 100%;
		
		/* #ifdef APP-VUE */
		margin-top: 50rpx;
		/* #endif */
		/* #ifndef APP-VUE */
		
		margin-top: 200rpx;
		/* #endif */
		
		
		image {
			width: 180rpx;
			height: 180rpx;
		}
	}
	page {
		background-color: #fff !important;
	}

	.ChangePassword .phone {
		font-size: 32rpx;
		font-weight: bold;
		text-align: center;
		margin-top: 55rpx;
	}

	.ChangePassword .list {
		width: 580rpx;
		margin: 53rpx auto 0 auto;
	}

	.ChangePassword .list .item {
		width: 100%;
		height: 110rpx;
		border-bottom: 2rpx solid #f0f0f0;
	}

	.ChangePassword .list .item input {
		width: 100%;
		height: 100%;
		font-size: 32rpx;
	}

	.ChangePassword .list .item .placeholder {
		color: #b9b9bc;
	}

	.ChangePassword .list .item input.codeIput {
		width: 340rpx;
	}

	.ChangePassword .list .item .code {
		font-size: 32rpx;
		// background-color: #fff;
	}

	.ChangePassword .list .item .code.on {
		color: #b9b9bc !important;
	}

	.ChangePassword .confirmBnt {
		font-size: 32rpx;
		width: 580rpx;
		height: 90rpx;
		border-radius: 45rpx;
		color: #fff;
		margin: 92rpx auto 0 auto;
		text-align: center;
		line-height: 90rpx;
	}
</style>
