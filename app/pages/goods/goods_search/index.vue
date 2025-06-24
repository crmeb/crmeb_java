<template>
	<view :data-theme="theme">
		<view class='searchGood'>
			<view class='search acea-row row-between-wrapper'>
				<!-- #ifndef MP -->
				<view class='input acea-row row-between-wrapper'>
					<text class='iconfont icon-sousuo2'></text>
					<input type='text' :value='searchValue'
					:focus="focus" placeholder='点击搜索商品'
					confirm-type='search' @confirm="searchBut"
					placeholder-class='placeholder' @input="setValue"
					maxlength="20"></input>
				</view>
				<view class='bnt' @tap='searchBut'>搜索</view>
				<!-- #endif -->
				<!-- #ifdef MP -->
				<searchBox :searchValue="searchValue" class="searchBox"  @searchChange="searchBut"></searchBox>
				<!-- #endif -->
			</view>
			<!-- #ifdef MP -->
			<view class='title' :style="{'margin-top':searchTop+60+'px'}">热门搜索</view>
			<!-- #endif -->
			<!-- #ifndef MP -->
			<view class='title'>热门搜索</view>
			<!-- #endif -->
			<view class='list acea-row'>
				<block v-for="(item,index) in hotSearchList" :key="index">
					<view class='item' @tap='setHotSearchValue(item.title)'>{{item.title}}</view>
				</block>
			</view>
			<view class='line'></view>
			<goodList :bastList="bastList" v-if="bastList.length > 0"></goodList>
			<view class='loadingicon acea-row row-center-wrapper' v-if="bastList.length > 0">
				<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
			</view>
		</view>
		<view class='noCommodity'>
			<view class='pictrue'  v-if="bastList.length == 0 && isbastList">
				<image :src="urlDomain+'crmebimage/perset/staticImg/noSearch.png'"></image>
			</view>
			<recommend ref="recommendIndex" v-if="bastList.length == 0"></recommend>
		</view>
	</view>
</template>

<script>
	import {
		getSearchKeyword,
		getProductslist
	} from '@/api/store.js';
	import goodList from '@/components/goodList';
	import recommend from '@/components/recommend';
	// #ifdef MP
	import searchBox from "@/components/searchBox.vue";
	// #endif
	let app = getApp();
	export default {
		components: {
			goodList,
			recommend,
			// #ifdef MP
			searchBox
			// #endif
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				searchValue: '',
				focus: true,
				bastList: [],
				hotSearchList: [],
				first: 0,
				limit: 8,
				page: 1,
				loading: false,
				loadend: false,
				loadTitle: '加载更多',
				isbastList: false,
				theme:app.globalData.theme,
				searchTop:''
			};
		},
		onLoad(e){
			// #ifdef MP
			this.searchTop=uni.getMenuButtonBoundingClientRect().top
			// #endif
		},
		onShow: function() {
			this.getRoutineHotSearch();
		},
		onReachBottom: function() {
			if(this.bastList.length>0){
				this.getProductList();
			}else{
				this.$refs.recommendIndex.get_host_product();
			}
			
		},
		methods: {
			getRoutineHotSearch: function() {
				let that = this;
				getSearchKeyword().then(res => {
					that.$set(that, 'hotSearchList', res.data);
				});
			},
			getProductList: function() {
				let that = this;
				if (that.loadend) return;
				if (that.loading) return;
				that.loading = true;
				that.loadTitle = '';
				getProductslist({
					keyword: that.searchValue,
					page: that.page,
					limit: that.limit
				}).then(res => {
					let list = res.data.list,
						loadend = list.length < that.limit;
					that.bastList = that.$util.SplitArray(list, that.bastList);
					that.$set(that,'bastList',that.bastList);
					that.loading = false;
					that.loadend = loadend;
					that.loadTitle = loadend ? "我也是有底线的~" : "加载更多";
					that.page = that.page + 1;
					that.isbastList = true;
				}).catch(err => {
					that.loading = false,
					that.loadTitle = '加载更多'
				});
			},
			setHotSearchValue: function(event) {
				this.$set(this, 'searchValue', event);
				this.page = 1;
				this.loadend = false;
				this.$set(this, 'bastList', []);
				this.getProductList();
			},
			setValue: function(event) {
				this.$set(this, 'searchValue', event.detail.value);
			},
			searchBut: function(e) {
				let that = this;
				that.focus = false;
				if(e.detail.value){
					this.searchValue =e.detail.value
				}
				if (that.searchValue.length > 0||val) {
					that.page = 1;
					that.loadend = false;
					that.$set(that, 'bastList', []);
					uni.showLoading({
						title: '正在搜索中'
					});
					that.getProductList();
					uni.hideLoading();
				} else {
					return this.$util.Tips({
						title: '请输入要搜索的商品',
						icon: 'none',
						duration: 1000,
						mask: true,
					});
				}
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #fff !important;
	}

	.searchGood .search {
		padding-left: 30rpx;
		background-color: #fff !important;
	}

	.searchGood .search {
		/* #ifndef MP */
		padding-top: 20rpx;
		padding-bottom: 20rpx;
		/* #endif */
		/* #ifdef MP */
		position: fixed;
		width: 100%;
		z-index: 9999;
		top: 0;
		/* #endif */
	}

	.searchGood .search .input {
		width: 598rpx;
		background-color: #f7f7f7;
		border-radius: 33rpx;
		padding: 0 35rpx;
		box-sizing: border-box;
		height: 66rpx;
	}

	.searchGood .search .input input {
		width: 472rpx;
		font-size: 26rpx;
	}

	.searchGood .search .input .placeholder {
		color: #bbb;
	}

	.searchGood .search .input .iconfont {
		color: #000;
		font-size: 35rpx;
	}

	.searchGood .search .bnt {
		width: 120rpx;
		text-align: center;
		height: 66rpx;
		line-height: 66rpx;
		font-size: 30rpx;
		color: #282828;
	}

	.searchGood .title {
		font-weight: 500;
		font-family: PingFang SC, PingFang SC;
		font-size: 28rpx;
		color: #333;
		margin: 50rpx 30rpx 25rpx 30rpx;
	}

	.searchGood .list {
		padding-left: 10rpx;
	}

	.searchGood .list .item {
		font-size: 26rpx;
		color: #454545;
		padding: 0 21rpx;
		height: 60rpx;
		border-radius: 30rpx;
		line-height: 60rpx;
		border: 1rpx solid #aaa;
		margin: 0 0 20rpx 20rpx;
	}

	.searchGood .line {
		border-bottom: 1rpx solid #eee;
		margin: 20rpx 30rpx 0 30rpx;
	}
	.searchBox{
		width: 100%;
	}
</style>
