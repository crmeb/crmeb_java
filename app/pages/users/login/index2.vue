<template>
	<div class="register absolute">
		<div class="shading">
			<div class="pictrue acea-row row-center-wrapper">
				<image :src="logoUrl" v-if="logoUrl" />
				<image src="../../../static/images/logo2.png" v-else />
			</div>
		</div>
		<div class="whiteBg" v-if="formItem === 1">
			<div class="title acea-row row-center-wrapper">
				<div class="item" :class="current === index ? 'on' : ''" v-for="(item, index) in navList" @click="navTap(index)"
				 :key="index">
					{{ item }}
				</div>
			</div>
			<div class="list" :hidden="current !== 1">
				<form @submit.prevent="submit">
					<div class="item">
						<div class="acea-row row-middle">
							<image src="/static/images/phone_1.png"></image>
							<input type="text" placeholder="输入手机号码" v-model="account" required />
						</div>
					</div>
					<div class="item">
						<div class="acea-row row-middle">
							<image src="/static/images/code_2.png"></image>
							<input type="password" placeholder="填写登录密码" v-model="password" required />
						</div>
					</div>
				</form>
				<!-- <navigator class="forgetPwd" hover-class="none" url="/pages/users/retrievePassword/index">
					<span class="iconfont icon-wenti"></span>忘记密码
				</navigator> -->
			</div>
			<div class="list" :hidden="current !== 0">
				<div class="item">
					<div class="acea-row row-middle">
						<image src="/static/images/phone_1.png"></image>
						<input type="text" placeholder="输入手机号码" v-model="account" />
					</div>
				</div>
				<div class="item">
					<div class="acea-row row-middle">
						<image src="/static/images/code_2.png"></image>
						<input type="text" placeholder="填写验证码" class="codeIput" v-model="captcha" />
						<button class="code" :disabled="disabled" :class="disabled === true ? 'on' : ''" @click="code">
							{{ text }}
						</button>
					</div>
				</div>
				<div class="item" v-if="isShowCode">
					<div class="acea-row row-middle">
						<image src="/static/images/code_2.png"></image>
						<input type="text" placeholder="填写验证码" class="codeIput" v-model="codeVal" />
						<div class="code" @click="again"><img :src="codeUrl" /></div>
					</div>
				</div>
			</div>
			<div class="logon" @click="loginMobile" :hidden="current !== 0">登录</div>
			<div class="logon" @click="submit" :hidden="current === 0">登录</div>
			<div class="tip">
				<div :hidden="current !== 1">
					没有账号?
					<span @click="current = 0" class="font-color-red">快速登录</span>
				</div>
			</div>
		</div>
		<!-- <div class="whiteBg" v-else>
			<div class="title">注册账号</div>
			<div class="list">
				<div class="item">
					<div class="acea-row row-middle">
						<image src="/static/images/phone_1.png"></image>
						<input type="text" placeholder="输入手机号码" v-model="account" />
					</div>
				</div>
				<div class="item">
					<div class="acea-row row-middle">
						<image src="/static/images/code_2.png"></image>
						<input type="text" placeholder="填写验证码" class="codeIput" v-model="captcha" />
						<button class="code" :disabled="disabled" :class="disabled === true ? 'on' : ''" @click="code">
							{{ text }}
						</button>
					</div>
				</div>
				<div class="item">
					<div class="acea-row row-middle">
						<image src="/static/images/code_1.png"></image>
						<input type="password" placeholder="填写您的登录密码" v-model="password" />
					</div>
				</div>
				<div class="item" v-if="isShowCode">
					<div class="acea-row row-middle">
						<image src="/static/images/code_2.png"></image>
						<input type="text" placeholder="填写验证码" class="codeIput" v-model="codeVal" />
						<div class="code" @click="again"><img :src="codeUrl" /></div>
					</div>
				</div>
			</div>
			<div class="logon" @click="register">注册</div>
			<div class="tip">
				已有账号?
				<span @click="formItem = 1" class="font-color-red">立即登录</span>
			</div>
		</div> -->
		<div class="bottom"></div>
	</div>
</template>
<script>
	import dayjs from "@/plugin/dayjs/dayjs.min.js";
	import sendVerifyCode from "@/mixins/SendVerifyCode";
	import {
		loginH5,
		loginMobile,
		registerVerify,
		register,
		// getCodeApi,
		getUserInfo
	} from "@/api/user";
	import attrs, {
		required,
		alpha_num,
		chs_phone
	} from "@/utils/validate";
	import {
		validatorDefaultCatch
	} from "@/utils/dialog";
	import {
		getLogo
	} from "@/api/public";
	import {
		VUE_APP_API_URL
	} from "@/utils";

	const BACK_URL = "login_back_url";

	export default {
		name: "Login",
		mixins: [sendVerifyCode],
		data: function() {
			return {
				navList: ["快速登录", "账号登录"],
				current: 0,
				account: "",
				password: "",
				captcha: "",
				formItem: 1,
				type: "login",
				logoUrl: "",
				keyCode: "",
				codeUrl: "",
				codeVal: "",
				isShowCode: false
			};
		},
		watch:{
			formItem:function(nval,oVal){
				if(nval == 1){
					this.type = 'login'
				}else{
					this.type = 'register'
				}
			}
		},
		mounted: function() {
			this.getCode();
			this.getLogoImage();
		},
		methods: {
			again() {
				this.codeUrl =
					VUE_APP_API_URL +
					"/sms_captcha?" +
					"key=" +
					this.keyCode +
					Date.parse(new Date());
			},
			getCode() {
				let that = this
				// getCodeApi()
				// 	.then(res => {
				// 		that.keyCode = res.data.key;
				// 	})
				// 	.catch(res => {
				// 		that.$util.Tips({
				// 			title: res
				// 		});
				// 	});
			},
			async getLogoImage() {
				let that = this;
				getLogo().then(res => {
					that.logoUrl = res.data.logoUrl;
				});
			},
			async loginMobile() {
				let that = this;
				if (!that.account) return that.$util.Tips({
					title: '请填写手机号码'
				});
				if (!/^1(3|4|5|7|8|9|6)\d{9}$/i.test(that.account)) return that.$util.Tips({
					title: '请输入正确的手机号码'
				});
				if (!that.captcha) return that.$util.Tips({
					title: '请填写验证码'
				});
				if (!/^[\w\d]+$/i.test(that.captcha)) return that.$util.Tips({
					title: '请输入正确的验证码'
				});
				loginMobile({
						account: that.account,
						captcha: that.captcha,
						spread: that.$Cache.get("spread")
					})
					.then(res => {
						let data = res.data;
						let newTime = Math.round(new Date() / 1000);
						that.$store.commit("LOGIN", {
							'token': data.token
							// 'time': dayjs(data.expiresTime) - newTime
						});
						const backUrl = that.$Cache.get(BACK_URL) || "/pages/index/index";
						that.$Cache.clear(BACK_URL);
						// getUserInfo().then(res => {
							that.$store.commit("SETUID", res.data.user.uid);
							if (backUrl === '/pages/index/index' || backUrl === '/pages/order_addcart/order_addcart' || backUrl ===
								'/pages/user/index') {

								uni.switchTab({
									url: backUrl
								});

							} else {
								uni.switchTab({
									url: '/pages/index/index'
								});
							}
						// })
					})
					.catch(res => {
						that.$util.Tips({
							title: res
						});
					});
			},
			async register() {
				let that = this;
				if (!that.account) return that.$util.Tips({
					title: '请填写手机号码'
				});
				if (!/^1(3|4|5|7|8|9|6)\d{9}$/i.test(that.account)) return that.$util.Tips({
					title: '请输入正确的手机号码'
				});
				if (!that.captcha) return that.$util.Tips({
					title: '请填写验证码'
				});
				if (!/^[\w\d]+$/i.test(that.captcha)) return that.$util.Tips({
					title: '请输入正确的验证码'
				});
				if (!that.password) return that.$util.Tips({
					title: '请填写密码'
				});
				if (!/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$/i.test(that.password)) return that.$util.Tips({
					title: '您输入的密码过于简单'
				});
				register({
						account: that.account,
						captcha: that.captcha,
						password: that.password,
						spread: that.$Cache.get("spread")
					})
					.then(res => {
						that.$util.Tips({
							title: res
						});
						that.formItem = 1;
					})
					.catch(res => {
						that.$util.Tips({
							title: res
						});
					});
			},
			async code() {
				let that = this;
				if (!that.account) return that.$util.Tips({
					title: '请填写手机号码'
				});
				if (!/^1(3|4|5|7|8|9|6)\d{9}$/i.test(that.account)) return that.$util.Tips({
					title: '请输入正确的手机号码'
				});
				if (that.formItem == 2) that.type = "register";
				// phone: that.account
				// type: that.type,
				// key: that.keyCode,
				// code: that.codeVal
				await registerVerify(that.account)
					.then(res => {
						that.$util.Tips({title:res.message});
						that.sendCode();
					})
					.catch(res => {
						// if (res.data.status === 402) {
						// 	that.codeUrl = `${VUE_APP_API_URL}/sms_captcha?key=${that.keyCode}`;
						// 	that.isShowCode = true;
						// }
						that.$util.Tips({title:res.message});
					});
			},
			navTap: function(index) {
				this.current = index;
			},
			async submit() {
				let that = this;
				if (!that.account) return that.$util.Tips({
					title: '请填写账号'
				});
				if (!/^[\w\d]{5,16}$/i.test(that.account)) return that.$util.Tips({
					title: '请输入正确的账号'
				});
				if (!that.password) return that.$util.Tips({
					title: '请填写密码'
				});
				loginH5({
						account: that.account,
						password: that.password,
						spread: that.$Cache.get("spread")
					})
					.then(({
						data
					}) => {
						// let newTime = Math.round(new Date() / 1000);
						that.$store.commit("LOGIN", {
							'token': data.token
							// 'time': dayjs(data.expiresTime) - newTime
						});
						const backUrl = that.$Cache.get(BACK_URL) || "/pages/index/index";
						that.$Cache.clear(BACK_URL);
						getUserInfo().then(res => {
							that.$store.commit("SETUID", res.data.uid);
							if (backUrl === '/pages/index/index' || backUrl === '/pages/order_addcart/order_addcart' || backUrl ==='/pages/user/index') {
								uni.switchTab({
									url: backUrl
								});
							} else {
								uni.switchTab({
									url: '/pages/index/index'
								});
							}
						})
					})
					.catch(e => {
						that.$util.Tips({
							title: e
						});
					});
			}
		}
	};
</script>
<style lang="scss">
	.code img {
		width: 100%;
		height: 100%;
	}

	.acea-row.row-middle {
		input {
			margin-left: 20rpx;
		}
	}
</style>
