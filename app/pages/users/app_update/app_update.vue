<template>
	<view class="app_update">
		<view class="logo_box">
			<image :src="urlDomain+'crmebimage/perset/staticImg/crmeb_java.png'"></image>
			<view class="title">crmeb</view>
			<view class="version">Version {{appUpdate.versionCode}}</view> 
		</view>
		<view class="jiancha" @click="appVersionConfig()">
			<text>检查新版本</text>
			<text class="iconfont icon-you"></text>
		</view>
	</view>
</template>

<script>
	import {getAppVersion} from '@/api/api.js'; 
	export default {
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				appUpdate:{} 
			}
		},
		onLoad() {
			let that = this;
			plus.runtime.getProperty(plus.runtime.appid,function(inf){
				that.$set(that.appUpdate,'versionCode',inf.version);
			})
		},
		methods: {
			appVersionConfig(){
				var that = this;
				//app升级
				// 获取本地应用资源版本号  
				getAppVersion().then(res=>{
					that.$set(that.appUpdate,'androidAddress',res.data.androidAddress);
					that.$set(that.appUpdate,'appVersion',res.data.appVersion);
					that.$set(that.appUpdate,'iosAddress',res.data.iosAddress);
					that.$set(that.appUpdate,'openUpgrade',res.data.openUpgrade);
					plus.runtime.getProperty(plus.runtime.appid,function(inf){
						let nowVersion = (inf.version).split('.').join('');
						let appVersion = (res.data.appVersion).split('.').join('');
						uni.getSystemInfo({
							success:(res) => {
								if(appVersion > nowVersion){
									uni.showModal({
										title: '更新提示',
										content: '发现新版本，是否前去下载?',
										showCancel:that.appUpdate.openUpgrade == 'false' ? true : false,
										cancelColor: '#eeeeee',
										confirmColor: '#FF0000',
										success(response) {
											if (response.confirm) {
												switch (res.platform){
													case "android":
														plus.runtime.openURL(that.appUpdate.androidAddress); 
														break;
													case "ios":
														plus.runtime.openURL(encodeURI(that.appUpdate.iosAddress));
														break;
												}
												
											}
										}
									});
								}else if(appVersion <= nowVersion){ 
									uni.showToast({
										title:'已是最新版本', 
										icon:'none' 
									})
								}
							}  
						}) 
					});
				})
			},
		}
	}
</script>

<style>
	.app_update{
		background-color: #fff;
		height: 100vh;
	}
	.logo_box{
		height: 500rpx;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
	}
	.logo_box image{
		display: block; 
		margin-top:80rpx; 
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
	}
	.title{
		font-size: 34rpx;
		font-family: PingFang SC;
		font-weight: 600;
		color: #333333;
		margin: 20rpx auto 20rpx;
	}
	.version{
		font-size: 28rpx;
		font-family: PingFang SC;
		font-weight: 500;
		color: #333333;
	}
	.jiancha{
		width: 690rpx;
		margin: 20rpx auto 0;
		padding: 0 20rpx 0;
		height: 100rpx;
		line-height: 100rpx;
		color: #333333;
		font-size: 30rpx;
		border-top:1px solid #f5f5f5;
		border-bottom:1px solid #f5f5f5;
		display: flex;
		justify-content: space-between;
	}
</style>
