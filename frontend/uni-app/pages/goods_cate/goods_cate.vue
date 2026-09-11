<template>
	<view class="page" :data-theme="theme" :style="{height:winHeight + 'px'}">
		<view class="cart-nav" :style="{ height: iStatusBarHeight + 'px'}"></view>
		<cate v-if="currentPage == 'one'"></cate>
		<contracted v-if="currentPage == 'two'" ref="classTwo"></contracted>
		<optimization v-if="currentPage == 'three'" :showSlide="showSlide" ref="classThree"></optimization>
		<fresh v-if="currentPage == 'four'" :showSlide="showSlide" ref="classFour"></fresh>
		<pageFooter v-if="footerShow"></pageFooter>
	</view>
</template>
<script setup>
	import { ref, getCurrentInstance } from 'vue';
	import { onLoad, onShow, onReachBottom } from '@dcloudio/uni-app';
	import pageFooter from '@/components/pageFooter/index.vue'
	import cate from './components/default_cate';
	import optimization from './components/optimization';
	import contracted from './components/contracted';
	import fresh from './components/fresh';
	import { getShare } from '@/api/public.js';
	import { getThemeInfo } from '@/api/api.js';
	import Cache from '@/utils/cache.js';
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from 'pinia';

	const { proxy } = getCurrentInstance();
	const app = getApp();
	const appStore = useAppStore();
	const { isLogin, uid } = storeToRefs(appStore);

	const categoryPageMap = {
		'1': 'one',
		'2': 'two',
		'3': 'three',
		'4': 'four',
	};

	const footerShow = ref(true);
	const currentPage = ref('one');
	const theme = ref(app.globalData.theme);
	const showSlide = ref(true);
	const winHeight = ref('');
	const configApi = ref({}); //分享类容配置
	const iStatusBarHeight = ref(0); // 状态栏高度

	// 子组件引用
	const classTwo = ref(null);
	const classThree = ref(null);
	const classFour = ref(null);

	onLoad(() => {
		let config = Cache.getItem('categoryConfig') || {};
		// #ifdef APP-PLUS
		iStatusBarHeight.value = uni.getSystemInfoSync().statusBarHeight;
		// #endif
		showSlide.value = config.isShowCategory == 'true' ? true : false;
		applyCategoryStatus(config.categoryConfig);
		getThemeCategory();
		uni.getSystemInfo({
			success: function(res) {
				winHeight.value = res.windowHeight;
			}
		});
		// #ifdef H5
		shareApi();
		// #endif
	});

	onShow(() => {
		switch (currentPage.value) {
			case 'one':
				break;
			case 'two':
				break;
			case 'three':
				uni.hideTabBar()
				footerShow.value = false
				setTimeout(() => {
					if (isLogin.value) {
						//登录的情况下获取模板3,4的购物车商品数量和列表
						classThree.value.getCartNum();
						classThree.value.getCartLists(1);
					}
				}, 500)
				break;
			case 'four':
				uni.hideTabBar()
				footerShow.value = false
				setTimeout(() => {
					if (isLogin.value) {
						classFour.value.getCartNum();
						classFour.value.getCartLists(1);
					}
				}, 500)
				break;
		}
	});

	function shareApi() {
		getShare().then(res => {
			configApi.value = res.data;
			// #ifdef H5
			setOpenShare(res.data);
			// #endif
		})
	}
	function getThemeCategory() {
		let data = {};
		let previewThemeId = uni.getStorageSync('previewThemeId');
		if (previewThemeId) data.theme_id = previewThemeId;
		getThemeInfo('category', data).then((res) => {
			if (res.data && res.data.status) {
				applyCategoryStatus(res.data.status);
			}
		}).catch(() => {});
	}
	function applyCategoryStatus(status) {
		let page = categoryPageMap[String(status)];
		if (!page) return;
		currentPage.value = page;
		if (page == 'three' || page == 'four') {
			uni.hideTabBar();
			footerShow.value = false;
		} else {
			footerShow.value = true;
		}
	}
	// 微信分享；
	// #ifdef H5
	function setOpenShare(data) {
		if (proxy.$wechat.isWeixin()) {
			let configAppMessage = {
				desc: data.synopsis,
				title: data.title,
				link: location.href,
				imgUrl: data.img
			};
			proxy.$wechat.wechatEvevt(["updateAppMessageShareData", "updateTimelineShareData"],
				configAppMessage);
		}
	}
	// #endif

	onReachBottom(() => {
		if (currentPage.value == 'two') {
			classTwo.value.getProductList();
		}
		if (currentPage.value == 'three') {
			classThree.value.productslist();
		}
		if (currentPage.value == 'four') {
			classFour.value.productslist();
		}
	});
</script>
<style lang="scss">
	.page{
		background: #fff;
		height: 100% !important;
	}
	.cart-nav {
		position: fixed;
		z-index: 99;
		top: 0;
		width: 100%;
		background-color: #fff;
	}
</style>
