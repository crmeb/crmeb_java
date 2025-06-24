<template>
	<view>
		<view class="accredit_model">
			<view class="model_container">
				<view class="model_top">
					<image class="model_icon" :src="urlDomain+'crmebimage/perset/staticImg/model_img.png'"></image>
				</view>
				<view class="model_content">
					<view class="model_title">授权提示</view>
					<view class="model_desc">{{content}}</view>
					<view class="model_btns">
						<view class="mbtn cancel_btn" @click="modelCancel()">取消</view>
						<view class="mbtn confirm_btn" @click="modelConfirm()" v-if="locationType">确定</view>
						<!-- <view class="mbtn confirm_btn" @click="modelConfirm()" v-if="userPhoneType">确定</view> -->
						<button class="mbtn confirm_btn" v-if="userPhoneType" open-type="getPhoneNumber" @getphonenumber="getphonenumber">确定</button>
					</view>
				</view>
			</view>
		</view>
		<view class="mask"></view>
	</view>
</template>
<script>
	const app = getApp();
	import Routine from '@/libs/routine';
	import {loginMobile,registerVerify,getCodeApi,getUserInfo} from "@/api/user";
	import { getLogo, getUserPhone } from '@/api/public';
	export default {
		name:'',
		props:{
			locationType:{
				type:Boolean,
				default:false
			},
			userPhoneType:{
				type:Boolean,
				default:false
			},
			authKey:{
				type:String,
				default:'',
			},
			isPhoneBox:{
				type:Boolean,
				default:false,
			},
			content:{
				type:String,
				default:'申请获取用于完整服务',
			}
		},
		data() {
			return {
				isStatus:false,
				urlDomain: this.$Cache.get("imgHost"),
			}
		},
		methods:{
			modelCancel(){
				this.$emit('closeModel',{isStatus:this.isStatus});
			},
			modelConfirm(){
				this.$emit('confirmModel');
			},
			// #ifdef MP
			// 小程序获取手机号码
			getphonenumber(e){
				uni.showLoading({ title: '加载中' });
				Routine.getCode()
					.then(code => {
						this.getUserPhoneNumber(e.detail.encryptedData, e.detail.iv, code);
					})
					.catch(error => {
						uni.hideLoading();
					});
			},
			// 小程序获取手机号码回调
			getUserPhoneNumber(encryptedData, iv, code) {
				getUserPhone({
					encryptedData: encryptedData,
					iv: iv,
					code: code,
					key:this.authKey,
					type: 'routine'
				})
					.then(res => {
						this.$store.commit('LOGIN', {
							token: res.data.token
						});
						this.$store.commit("SETUID", res.data.uid);
						this.getUserInfo();
					})
					.catch(res => {
						uni.hideLoading();
						this.$util.Tips({
							title: res
						});
					});
			},
			/**
			 * 获取个人用户信息
			 */
			getUserInfo: function() {
				let that = this;
				getUserInfo().then(res => {
					uni.hideLoading();
					that.$store.commit("UPDATE_USERINFO", res.data);
					that.isStatus = true
					this.modelCancel();
				});
			},
			// #endif
		}
	}
</script>

<style scoped lang="scss">
	.accredit_model{
		/* position: relative; */
		width: 560rpx;
		height: 660rpx;
		position: fixed;
		left: 50%;
		top: 50%;
		transform: translate(-50%,-50%);
		z-index: 999;
	}
	.model_container{
		width: 100%;
		height: 660rpx;
	}
	.model_top{
		width: 100%;
		height: 270rpx;
		// background: #D64532;
		@include main_bg_color(theme);
		border-radius: 20rpx 20rpx 0 0;
		background-image: url(data:image/png;base64,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);
		background-size: cover;
		display: flex;
		justify-content: center;
		align-items: flex-end;
	}
	.model_icon{
		width: 230rpx;
		height: 210rpx;
	}
	.model_content{
		width: 100%;
		height: 390rpx;
		background-color: #fff;
		border-radius: 0 0 20rpx 20rpx;
		padding: 32rpx;
		box-sizing: border-box;
	}
	.model_title{
		text-align: center;
		font-size: 36rpx;
		font-family: PingFangSC-Semibold, PingFang SC;
		font-weight: 600;
		color: #333333;
		line-height: 50rpx;
	}
	.model_desc{
		width: 448rpx;
		height: 96rpx;
		margin: 20rpx auto 0;
		font-size: 32rpx;
		text-align: center;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #666666;
		line-height: 48rpx;
	}
	.model_btns{
		width: 448rpx;
		height: 82rpx;
		margin: auto;
		display: flex;
		justify-content: space-between;
	}
	.mbtn{
		width: 208rpx;
		height: 82rpx;
		border-radius: 42rpx;
		margin-top: 44rpx;
	}
	.cancel_btn{
		background: #F5F5F5;
		font-weight: 400;
		color: #666666;
		line-height: 82rpx;
		text-align: center;
		font-size: 32rpx;
	}
	.confirm_btn{
		@include linear-gradient(theme);
		font-weight: 400;
		color: #fff;
		line-height: 82rpx;
		text-align: center;
		font-size: 32rpx;
	}
	.mask{
		z-index: 900;
	}
</style>