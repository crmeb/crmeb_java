<template>
<view>
	<view v-if="canvasStatus">
		<view class="mask"></view>
		<image :src='imagePath' class="canvas"></image>
	</view>
	<view  v-else>
		<canvas style="width:750rpx;height:1190rpx;position: fixed;z-index: -5;" canvas-id="firstCanvas"></canvas>
		<canvas style="position:fixed;z-index: -5;opacity: 0;" canvas-id="qrcode" :style="{width: `${qrcodeSize}px`, height: `${qrcodeSize}px`}"/>
	</view>
	<div @click='clickgg'>点击</div>
</view>
</template>
 
<script>
	import uQRCode from '@/js_sdk/Sansnn-uQRCode/uqrcode.js'
	import {
		HTTP_REQUEST_URL
	 } from '@/config/app.js';
export default {
	data(){
		return{
			imagePath:'',
			canvasStatus:false,
			
			qrcodeText: HTTP_REQUEST_URL,
			qrcodeSize: 129,
			PromotionCode:''
		}
	},
    onReady: function (e) {
		this.make();
    },
    methods: {
		// 生成二维码；
		make() {
			let that = this;
			uQRCode.make({
				canvasId: 'qrcode',
				text: this.qrcodeText,
				size: this.qrcodeSize,
				margin: 10,
				success: res => {
					that.PromotionCode = res;
				},
				complete: () => {
				},
				fail:res=>{
					that.$util.Tips({
						title: '海报二维码生成失败！'
					});
				}
			})
		},
		clickgg(){
			let that = this;
			// ../../static/images/barg002.png
			let arrImages = ['../../static/images/posterbackgd.png','../../static/images/explosion.png',that.PromotionCode];
			let storeName = '1十大歌手大好时光的电视广上世纪法国设计风格加工费设计规范技术规范设计规范手机话费csdsdsdsd9';
			let price = '20';
			that.$util.PosterCanvas(arrImages, storeName, price, function(tempFilePath) {
				console.log('klklkl');
				that.imagePath = tempFilePath;
				that.canvasStatus = true;
			});
		}
    }
}
</script>
<style>
	.canvas{
		position: fixed;
		width: 512rpx;
		height: 864rpx;
		top:50%;
		left:50%;
		margin-left: -256rpx;
		margin-top: -432rpx;
		z-index: 999;
	}
</style>