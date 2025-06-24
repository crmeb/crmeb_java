<template>
	<view class="copyRight-box" v-if="copyRightInfo">
		<view>
			<text @click="toIcp">{{copyRightInfo.copyrightIcpNumber}}</text>
		</view>
		<view class="bottom mt10" @click="toInternet">
			<text>{{copyRightInfo.copyrightInternetRecord}}</text>
		</view>
	</view>
</template>

<script>
	import {getConfigCopyright} from '@/api/api.js'
	export default{
		data(){
			return{
				copyRightInfo:null
			}
		},
		mounted() {
			this.getCopyright()
		},
		methods:{
			getCopyright(){
				getConfigCopyright().then(res=>{
					this.copyRightInfo=res.data
				})
			},
			toIcp(){
				window.open(this.copyRightInfo.copyrightIcpNumberUrl)
			},
			toInternet(){
				window.open(this.copyRightInfo.copyrightInternetRecordUrl)
			},
		}
	}
</script>

<style lang="scss" scoped>
	.copyRight-box{
		padding: 40rpx 0;
		text-align: center;
		text{
			color: #666;
			font-size: 24rpx;
		}
	}
</style>