<template>
	<view>
		<guide v-if="guidePages" :advData="advData"></guide>
	</view>
</template>

<script>
	import guide from './guide.vue'
	import Cache from '@/utils/cache';
	import {
		getOpenAdvApi
	} from '@/api/api.js'
	export default {
		components: {
			guide
		},
		data() {
			return {
				guidePages: false,
				advData: []
			}
		},
		onShow() {
			// #ifdef H5
			if (this.$wechat.isWeixin()) {
				this.$wechat.wechat();
			}
			// #endif
			this.loadExecution()
		},
		methods: {
			loadExecution() {
				getOpenAdvApi().then(res => {
					if (res.data.splashAdSwitch == 0 || res.data.adList.length == 0) {
						uni.switchTab({
							url: '/pages/index/index'
						});
					} else if (res.data.splashAdSwitch && res.data.adList.length) {
						if (res.data.splashAdShowInterval != 0) {
							const tagDate = uni.getStorageSync('guideDate') || 0,
								nowDate = new Date().getTime();
							if ((nowDate - tagDate) <= uni.getStorageSync('intervalTime')) {
								uni.switchTab({
									url: '/pages/index/index'
								});
								return
							}
						}
						this.advData = res.data
						let intervalTime = parseFloat(res.data.splashAdShowInterval) * 60 * 60 * 1000 || 0;
						uni.setStorageSync('intervalTime', intervalTime);
						uni.setStorageSync('guideDate', new Date().getTime());
						this.guidePages = true
					}
				}).catch(err => {
					uni.switchTab({
						url: '/pages/index/index'
					});
				})
			}
		},
		onHide() {
			this.guidePages = false
		}
	}
</script>

<style>
	page,
	.main {
		width: 100%;
		height: 100%;
	}
</style>