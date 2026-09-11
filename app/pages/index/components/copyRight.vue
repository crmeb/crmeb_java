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

<script setup>
	import { ref, onMounted } from 'vue'
	import {getConfigCopyright} from '@/api/api.js'

	const copyRightInfo = ref(null)

	onMounted(() => {
		getCopyright()
	})

	function getCopyright(){
		getConfigCopyright().then(res=>{
			copyRightInfo.value = res.data
		})
	}
	function toIcp(){
		window.open(copyRightInfo.value.copyrightIcpNumberUrl)
	}
	function toInternet(){
		window.open(copyRightInfo.value.copyrightInternetRecordUrl)
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