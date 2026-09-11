<template>
	<view :data-theme="theme" :style="colorStyle">
		<view class="promoter-list">
			<view class='promoterHeader'>
				<view class='headerCon acea-row row-between'>
					<view>
						<view class='name'>推广人数</view>
						<view><text class='num'>{{peopleData.count}}</text>人</view>
					</view>
					<view class='iconfont icon-tuandui'></view>
				</view>
			</view>
			<view class="pad30">
				<view class='nav acea-row row-around'>
					<view :class="grade == 0 ? 'item on' : 'item'" @click='setType(0)'>一级({{peopleData.total}})</view>
					<view :class="grade == 1 ? 'item on' : 'item'" @click='setType(1)'>二级({{peopleData.totalLevel}})
					</view>
				</view>
				<view class='search acea-row row-between-wrapper'>
					<view class='input'>
						<input placeholder='点击搜索会员名称' placeholder-class='placeholder' v-model="keyword"
							@confirm="submitForm" confirm-type='search' name="search" maxlength="10"></input>
						</view>
					<button class='iconfont icon-sousuo2' @click="submitForm"></button>
				</view>
				<view class='list'>
					<view class="sortNav acea-row row-middle">
						<view class="sortItem" @click='setSort("childCount","ASC")' v-if="sort == 'childCountDESC'">团队排序
							<image :src="urlDomain+'/crmebimage/perset/staticImg/sort1.png'"></image>
						</view>
						<view class="sortItem" @click='setSort("childCount")' v-else-if="sort == 'childCountASC'">团队排序
							<image :src="urlDomain+'/crmebimage/perset/staticImg/sort3.png'"></image>
						</view>
						<view class="sortItem" @click='setSort("childCount","DESC")' v-else>团队排序
							<image :src="urlDomain+'/crmebimage/perset/staticImg/sort2.png'"></image>
						</view>
						<view class="sortItem" @click='setSort("numberCount","ASC")' v-if="sort == 'numberCountDESC'">
							金额排序
							<image :src="urlDomain+'/crmebimage/perset/staticImg/sort1.png'"></image>
						</view>
						<view class="sortItem" @click='setSort("numberCount")' v-else-if="sort == 'numberCountASC'">金额排序
							<image :src="urlDomain+'/crmebimage/perset/staticImg/sort3.png'"></image>
						</view>
						<view class="sortItem" @click='setSort("numberCount","DESC")' v-else>金额排序
							<image :src="urlDomain+'/crmebimage/perset/staticImg/sort2.png'"></image>
						</view>
						<view class="sortItem" @click='setSort("orderCount","ASC")' v-if="sort == 'orderCountDESC'">订单排序
							<image :src="urlDomain+'/crmebimage/perset/staticImg/sort1.png'"></image>
						</view>
						<view class="sortItem" @click='setSort("orderCount")' v-else-if="sort == 'orderCountASC'">订单排序
							<image :src="urlDomain+'/crmebimage/perset/staticImg/sort3.png'"></image>
						</view>
						<view class="sortItem" @click='setSort("orderCount","DESC")' v-else>订单排序
							<image :src="urlDomain+'/crmebimage/perset/staticImg/sort2.png'"></image>
						</view>
					</view>
					<block v-for="(item,index) in recordList" :key="index">
						<view class='item acea-row row-between-wrapper'>
							<view class="picTxt acea-row row-between-wrapper">
								<view class='pictrue'>
									<image :src='item.avatar'></image>
								</view>
								<view class='text'>
									<view class='name line1'>{{item.nickname}}</view>
									<view>加入时间: {{item.time ? item.time.split(' ')[0] : ''}}</view>
								</view>
							</view>
							<view class="right">
								<view><text class='num font-color'>{{item.childCount ? item.childCount : 0}}</text>人
								</view>
								<view><text class="num">{{item.orderCount ? item.orderCount : 0}}</text>单</view>
								<view><text class="num">{{item.numberCount ? item.numberCount : 0}}</text>元</view>
							</view>
						</view>
					</block>
					<Loading :loaded="status" :loading="loadingList"></Loading>
					<block v-if="recordList.length == 0 && isShow">
						<emptyPage title="暂无推广人数～"></emptyPage>
					</block>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
	import {
		spreadPeople,
		spreadPeoCount as spreadPeoCountApi
	} from '@/api/user.js';
	import {toLogin} from '@/libs/login.js';
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from 'pinia';
	import emptyPage from '@/components/emptyPage.vue'
	import Loading from "@/components/Loading/index.vue";
	import {setThemeColor} from '@/utils/setTheme.js'
	import { ref } from 'vue';
	import { onLoad, onShow, onHide, onReachBottom } from '@dcloudio/uni-app';
	import Cache from '@/utils/cache.js';
import { useColor } from '@/composables/useColor.js';
	const app = getApp();
	const { isLogin } = storeToRefs(useAppStore());

	const urlDomain = ref(Cache.get("imgHost"));
	const page = ref(1);
	const limit = ref(20);
	const keyword = ref('');
	const sort = ref('');
	const isAsc = ref('');
	const sortKey = ref('');
	const grade = ref(0);
	const status = ref(false);
	const loadingList = ref(false);
	const recordList = ref([]);
	const peopleData = ref({});
	const isShow = ref(false);
	const theme = ref(app.globalData.theme);
	const { colorStyle } = useColor();
	const bgColor = ref('#e93323');
	const is_show = ref(false);

	onLoad(() => {
		if (isLogin.value) {
			userSpreadNewList();
			spreadPeoCount();
		} else {
			toLogin();
		}
		bgColor.value = setThemeColor();
		uni.setNavigationBarColor({
			frontColor: '#ffffff',
			backgroundColor: bgColor.value,
		});
	});
	onShow(() => {
		if (is_show.value) userSpreadNewList();
	});
	onHide(() => {
		is_show.value = true;
	});

	function setSort(sk, asc) {
		isAsc.value = asc;
		sort.value = sk + asc;
		sortKey.value = sk;
		page.value = 1;
		limit.value = 20;
		status.value = false;
		recordList.value = [];
		userSpreadNewList();
	}
	function submitForm() {
		page.value = 1;
		limit.value = 20;
		status.value = false;
		recordList.value = [];
		userSpreadNewList();
	}

	function setType(g) {
		if (grade.value != g) {
			grade.value = g;
			page.value = 1;
			limit.value = 20;
			keyword.value = '';
			sort.value = '';
			isAsc.value = '';
			status.value = false;
			loadingList.value = false;
			recordList.value = [];
			userSpreadNewList();
		}
	}
	function spreadPeoCount() {
		spreadPeoCountApi().then(res => {
			peopleData.value = res.data;
		});
	}
	function userSpreadNewList() {
		let recordListNew = [];
		if (loadingList.value) return;
		if (status.value == true) return;
		spreadPeople({
			page: page.value,
			limit: limit.value,
			keyword: keyword.value,
			grade: grade.value,
			sortKey: sortKey.value,
			isAsc: isAsc.value
		}).then(res => {
			let recordListData = res.data.list ? res.data.list : [];
			let len = recordListData.length;
			recordListNew = recordList.value.concat(recordListData);
			status.value = limit.value > len;
			page.value = page.value + 1;
			recordList.value = recordListNew || [];
			loadingList.value = false;
			if (recordList.value.length === 0) isShow.value = true;
		});
	}

	onReachBottom(() => {
		userSpreadNewList();
	});
</script>

<style scoped lang="scss">
	.promoter-list .nav {
		background-color: #fff;
		height: 86rpx;
		line-height: 86rpx;
		font-size: 28rpx;
		color: #282828;
		border-bottom: 1rpx solid #eee;
		border-top-left-radius: 14rpx;
		border-top-right-radius: 14rpx;
		margin-top: -30rpx;
	}
	.promoterHeader{
		@include main_bg_color(theme);
	}
	.promoter-list .nav .item.on {
		@include main_color(theme);
		@include tab_border_bottom(theme);
	}
	.promoter-list .search {
		width: 100%;
		background-color: #fff;
		height: 100rpx;
		padding: 0 24rpx;
		box-sizing: border-box;
		border-bottom-left-radius: 14rpx;
		border-bottom-right-radius: 14rpx;
	}

	.promoter-list .search .input {
		width: 592rpx;
		height: 60rpx;
		border-radius: 50rpx;
		background-color: #f5f5f5;
		text-align: center;
		position: relative;
	}

	.promoter-list .search .input input {
		height: 100%;
		font-size: 26rpx;
		width: 610rpx;
		text-align: center;
	}

	.promoter-list .search .input .placeholder {
		color: #bbb;
	}

	.promoter-list .search .input .iconfont {
		position: absolute;
		right: 28rpx;
		color: #999;
		font-size: 28rpx;
		top: 50%;
		transform: translateY(-50%);
	}

	.promoter-list .search .iconfont {
		font-size: 32rpx;
		color: #515151;
		height: 60rpx;
		line-height: 60rpx;
	}

	.promoter-list .list {
		margin-top: 20rpx;
	}

	.promoter-list .list .sortNav {
		background-color: #fff;
		height: 76rpx;
		border-bottom: 1rpx solid #eee;
		color: #333;
		font-size: 28rpx;
		border-top-left-radius: 14rpx;
		border-top-right-radius: 14rpx;
	}

	.promoter-list .list .sortNav .sortItem {
		text-align: center;
		flex: 1;
	}

	.promoter-list .list .sortNav .sortItem image {
		width: 24rpx;
		height: 24rpx;
		margin-left: 6rpx;
		vertical-align: -3rpx;
	}

	.promoter-list .list .item {
		background-color: #fff;
		border-bottom: 1rpx solid #eee;
		height: 152rpx;
		padding: 0 24rpx;
		font-size: 24rpx;
		color: #666;
	}

	.promoter-list .list .item .picTxt .pictrue {
		width: 106rpx;
		height: 106rpx;
		border-radius: 50%;
	}

	.promoter-list .list .item .picTxt .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 50%;
		border: 3rpx solid #fff;
		box-shadow: 0 0 10rpx #aaa;
		box-sizing: border-box;
	}

	.promoter-list .list .item .picTxt .text {
		// width: 304rpx;
		font-size: 24rpx;
		color: #666;
		margin-left: 14rpx;
	}

	.promoter-list .list .item .picTxt .text .name {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 13rpx;
	}

	.promoter-list .list .item .right {
		text-align: right;
		font-size: 22rpx;
		color: #333;
	}

	.promoter-list .list .item .right .num {
		margin-right: 7rpx;
	}
</style>
