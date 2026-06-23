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
<script>
	import pageFooter from '@/components/pageFooter/index.vue'
	import cate from './components/default_cate';
	import optimization from './components/optimization';
	import contracted from './components/contracted';
	import fresh from './components/fresh';
	import {getShare} from '@/api/public.js';
	import {getThemeInfo} from '@/api/api.js';
	import {mapGetters} from 'vuex';
	const app = getApp();
	const categoryPageMap = {
		'1': 'one',
		'2': 'two',
		'3': 'three',
		'4': 'four',
	};
	export default {
		data() {
			return {
				footerShow:true,
				currentPage:'one',
				theme:app.globalData.theme,
				showSlide:true,
				winHeight:'',
				configApi: {}, //分享类容配置
				iStatusBarHeight: 0, // 状态栏高度
			}
		},
		computed: mapGetters(['isLogin', 'uid']),
		onLoad(){
			let that = this;
			let config = that.$Cache.getItem('categoryConfig') || {};
			// #ifdef APP-PLUS
			this.iStatusBarHeight = uni.getSystemInfoSync().statusBarHeight;
			// #endif
			that.showSlide = config.isShowCategory == 'true'? true : false;
			that.applyCategoryStatus(config.categoryConfig);
			that.getThemeCategory();
			uni.getSystemInfo({
			    success: function (res) {
			        that.winHeight = res.windowHeight;
			    }
			});
			// #ifdef H5
			that.shareApi();
			// #endif
		},
		onShow(){
			switch (this.currentPage){
				case 'one':
					break;
				case 'two':
					break;
				case 'three':
					uni.hideTabBar()
					this.footerShow=false
					setTimeout(()=>{
						if(this.isLogin){
							//登录的情况下获取模板3,4的购物车商品数量和列表
							this.$refs.classThree.getCartNum();
							this.$refs.classThree.getCartLists(1);
						}
					},500)
					break;
				case 'four':
					uni.hideTabBar()
					this.footerShow=false
					setTimeout(()=>{
						if(this.isLogin){
							this.$refs.classFour.getCartNum();
							this.$refs.classFour.getCartLists(1);
						}
					},500)
					break;
			}
		},
		components:{
			cate,optimization,contracted,fresh,pageFooter
		},
		methods:{
			shareApi: function() {
				getShare().then(res => {
					this.$set(this, 'configApi', res.data);
					// #ifdef H5
					this.setOpenShare(res.data);
					// #endif
				})
			},
			getThemeCategory() {
				let data = {};
				let previewThemeId = uni.getStorageSync('previewThemeId');
				if (previewThemeId) data.theme_id = previewThemeId;
				getThemeInfo('category', data).then((res) => {
					if (res.data && res.data.status) {
						this.applyCategoryStatus(res.data.status);
					}
				}).catch(() => {});
			},
			applyCategoryStatus(status) {
				let currentPage = categoryPageMap[String(status)];
				if (!currentPage) return;
				this.$set(this, 'currentPage', currentPage);
				if (currentPage == 'three' || currentPage == 'four') {
					uni.hideTabBar();
					this.footerShow = false;
				} else {
					this.footerShow = true;
				}
			},
			// 微信分享；
			setOpenShare: function(data) {
				let that = this;
				if (that.$wechat.isWeixin()) {
					let configAppMessage = {
						desc: data.synopsis,
						title: data.title,
						link: location.href,
						imgUrl: data.img
					};
					that.$wechat.wechatEvevt(["updateAppMessageShareData", "updateTimelineShareData"],
						configAppMessage);
				}
			},
		},
		onReachBottom(){
			if(this.currentPage=='two'){
				this.$refs.classTwo.getProductList();
			}
			if(this.currentPage=='three'){
				this.$refs.classThree.productslist();
			}
			if(this.currentPage=='four'){
				this.$refs.classFour.productslist();
			}
		}
	}
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
