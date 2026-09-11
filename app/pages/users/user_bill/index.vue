<template>
	<view :data-theme="theme" :style="colorStyle">
		<view class='bill-details'>
			<view class='nav acea-row'>
				<view class='item' :class='type==="all" ? "on":""' @click='changeType("all")'>全部</view>
				<view class='item' :class='type==="expenditure" ? "on":""' @click='changeType("expenditure")'>消费</view>
				<view class='item' :class='type==="income" ? "on":""' @click='changeType("income")'>充值</view>
			</view>
			<view class='sign-record'>
				<view class='list pad30' v-for="(item,index) in userBillList" :key="index">
					<view class='item'>
						<view class='data'>{{item.date}}</view>
						<view class='listn borRadius14'>
							<view class='itemn acea-row row-between-wrapper' v-for="(vo,indexn) in item.list"
								:key="indexn">
								<view>
									<view class='name line1'>{{vo.title}}</view>
									<view>{{vo.add_time}}</view>
								</view>
								<view class='num font_color' v-if="vo.pm">+{{vo.number}}</view>
								<view class='num' v-else>-{{vo.number}}</view>
							</view>
						</view>
					</view>
				</view>
				<view class='loadingicon acea-row row-center-wrapper' v-if="userBillList.length>0">
					<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
				</view>
				<view v-if="userBillList.length == 0">
					<emptyPage title="暂无账单的记录哦～"></emptyPage>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { onShow, onLoad, onReachBottom } from '@dcloudio/uni-app';
	import {
		getBillList
	} from '@/api/user.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from 'pinia';
	import emptyPage from '@/components/emptyPage.vue';
import { useColor } from '@/composables/useColor.js';
	let app = getApp();

	const appStore = useAppStore();
	const { isLogin } = storeToRefs(appStore);

	// data
	const loadTitle = ref('加载更多');
	const loading = ref(false);
	const loadend = ref(false);
	const page = ref(1);
	const limit = ref(12);
	const type = ref('all');
	const userBillList = ref([]);
	const theme = ref(app.globalData.theme);
	const { colorStyle } = useColor();

	onShow(() => {
		if (isLogin.value) {
			getUserBillList();
		} else {
			toLogin();
		}
	});
	/**
	 * 生命周期函数--监听页面加载
	 */
	onLoad((options) => {
		type.value = options.type ? options.type : 'all';
	});
	/**
	 * 页面上拉触底事件的处理函数
	 */
	onReachBottom(() => {
		getUserBillList();
	});

	/**
	 * 获取账户明细
	 */
	function getUserBillList() {
		if (loadend.value) return;

		if (loading.value) return;
		loading.value = true;
		loadTitle.value = "";
		let data = {
			page: page.value,
			limit: limit.value,
			type: type.value
		}
		getBillList(data).then(function(res) {
			let list = res.data.list ? res.data.list : [],
			loadendVal = res.data.totalPage <= page.value;
			for (let i = 0; i < list.length; i++) {
				let time1 = list[i].date;
				let array1 = list[i].list;
				let isEquals = false;
				for (let j = 0; j < userBillList.value.length; j++) {
					let time2 = userBillList.value[j].date;
					let array2 = userBillList.value[j].list;
					if (time1 == time2) {
						array2.push.apply(array2, array1);
						userBillList.value[j].list = array2;
						isEquals = true;
						break;
					}
				}
				if (!isEquals) {
					userBillList.value.push({
						date: time1,
						list: array1
					})
				}
			}
			page.value += 1;
			loadend.value = loadendVal;
			loading.value = false;
			loadTitle.value = loadendVal ? "我也是有底线的~" : "加载更多";
		}, function(res) {
			loading.value = false;
			loadTitle.value = '加载更多';
		});
	}
	/**
	 * 切换导航
	 */
	function changeType(typeVal) {
		type.value = typeVal;
		loadend.value = false;
		page.value = 1;
		userBillList.value = [];
		getUserBillList();
	}
</script>

<style scoped lang='scss'>
	.sign-record {}

	.bill-details .nav {
		background-color: #fff;
		height: 90rpx;
		width: 100%;
		line-height: 90rpx;
	}
	.bill-details .nav .item {
		flex: 1;
		text-align: center;
		font-size: 30rpx;
		color: #282828;
	}

	.bill-details .nav .item.on {
		@include main_color(theme);
		@include tab_border_bottom(theme);
	}
	.font_color{
		color: #E93323 !important;
	}
</style>
