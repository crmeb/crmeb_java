<template>
	<view>
		<guide v-if="guidePages" :advData="advData"></guide>
	</view>
</template>

<script setup>
	import { ref, getCurrentInstance } from 'vue'
	import { onShow, onHide } from '@dcloudio/uni-app'
	import guide from './guide.vue'
	import Cache from '@/utils/cache.js';
	import {
		getOpenAdvApi
	} from '@/api/api.js'

	const { proxy } = getCurrentInstance();

	const guidePages = ref(false)
	const advData = ref([])

	onShow(() => {
		// #ifdef H5
		if (proxy.$wechat.isWeixin()) {
			proxy.$wechat.wechat();
		}
		// #endif
		loadExecution()
	})

	function loadExecution() {
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
				advData.value = res.data
				let intervalTime = parseFloat(res.data.splashAdShowInterval) * 60 * 60 * 1000 || 0;
				uni.setStorageSync('intervalTime', intervalTime);
				uni.setStorageSync('guideDate', new Date().getTime());
				guidePages.value = true
			}
		}).catch(err => {
			uni.switchTab({
				url: '/pages/index/index'
			});
		})
	}

	onHide(() => {
		guidePages.value = false
	})
</script>

<style>
	page,
	.main {
		width: 100%;
		height: 100%;
	}
</style>