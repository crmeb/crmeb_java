<template>
	<view :data-theme="theme" :style="colorStyle">
		<view class='commission-details'>
			<view class='promoterHeader'>
				<view class='headerCon acea-row row-between-wrapper'>
					<view>
						<view class='name'>{{name}}</view>
						<view class='money' v-if="recordType == 4">￥<text class='num'>{{extractCount}}</text></view>
						<view class='money' v-else>￥<text class='num'>{{commissionCount}}</text></view>
					</view>
					<view class='iconfont icon-jinbi1'></view>
				</view>
			</view>
			<view class='sign-record' v-if="recordType == 4 && recordList.length>0">
				<block v-for="(item,index) in recordList" :key="index">
					<view class='list pad30'>
						<view class='item'>
							<view class='data'>{{item.date}}</view>
							<view class='listn borRadius14'>
								<block v-for="(child,indexn) in item.list" :key="indexn">
									<view class='itemn acea-row row-between-wrapper'>
										<view class="left-box">
											<view class='name line1'>{{statusFilter(child.status)}}</view>
											<view class="remark font_color"> {{child.failMsg || ''}}</view>
											<view>{{child.createTime}}</view>
										</view>
										<view class='num font_color' v-if="child.status == -1">+{{child.extractPrice}}
										</view>
										<view class='num' v-else>-{{child.extractPrice}}</view>
										<!-- <view>
											<view class='name line1'>{{child.status === -1 ? '提现失败' : '提现成功'}}<span
													v-show="child.status === -1"
													style="font-size: 12px;color: red;">{{'('+child.failMsg+')'}}</span>
											</view>
											<view>{{child.createTime}}</view>
										</view>
										<view class='num font-color' v-if="child.status == -1">+{{child.extractPrice}}
										</view>
										<view class='num' v-else>-{{child.extractPrice}}</view> -->
									</view>
								</block>
							</view>
						</view>
					</view>
				</block>
				<view v-if="recordList.length == 0">
					<emptyPage title='暂无提现记录~'></emptyPage>
				</view>
			</view>
			<view class='sign-record' v-else>
				<block v-for="(item,index) in recordList" :key="index">
					<view class='list pad30'>
						<view class='item'>
							<view class='data'>{{item.date}}</view>
							<view class='listn borRadius14'>
								<block v-for="(child,indexn) in item.list" :key="indexn">
									<view class='itemn acea-row row-between-wrapper'>
										<view>
											<view class='name line1'>{{child.title}}</view>
											<view>{{child.updateTime}}</view>
										</view>
										<view class='num font_color' v-if="child.type == 1">+{{child.price}}
										</view>
										<view class='num' v-else>-{{child.price}}</view>
									</view>
								</block>
							</view>
						</view>
					</view>
				</block>
				<view v-if="recordList.length == 0">
					<emptyPage title='暂无佣金记录~'></emptyPage>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { onLoad, onShow, onReachBottom } from '@dcloudio/uni-app';
	import {
		getCommissionInfo,
		getRecordApi,
	} from '@/api/user.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from 'pinia';
	import emptyPage from '@/components/emptyPage.vue'
	import {
		setThemeColor
	} from '@/utils/setTheme.js'
import { useColor } from '@/composables/useColor.js';
	const app = getApp();

	const appStore = useAppStore();
	const { isLogin } = storeToRefs(appStore);

	function statusFilter(status) {
		const statusMap = {
			'-1': '未通过',
			'0': '审核中',
			'1': '已提现'
		}
		return statusMap[status]
	}

	const name = ref('');
	const type = ref(0);
	const page = ref(1);
	const limit = ref(10);
	const recordList = ref([]);
	const recordType = ref(0);
	const statuss = ref(false);
	const extractCount = ref(0);
	const { colorStyle } = useColor();
	const theme = ref(app.globalData.theme);
	const commissionCount = ref(0);
	const bgColor = ref('#e93323');

	onLoad((options) => {
		if (isLogin.value) {
			type.value = options.type;
			extractCount.value = options.extractCount;
			commissionCount.value = options.commissionCount;
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
		if (type.value == 1) {
			uni.setNavigationBarTitle({
				title: "提现记录"
			});
			name.value = '提现总额';
			recordType.value = 4;
			getList();
		} else if (type.value == 2) {
			uni.setNavigationBarTitle({
				title: "佣金记录"
			});
			name.value = '佣金明细';
			recordType.value = 3;
			getRecordList();
		} else {
			uni.showToast({
				title: '参数错误',
				icon: 'none',
				duration: 1000,
				mask: true,
				success: function(res) {
					setTimeout(function() {
						// #ifndef H5
						uni.navigateBack({
							delta: 1,
						});
						// #endif
						// #ifdef H5
						history.back();
						// #endif
					}, 1200)
				},
			});
		}
	});

	function getList() {
		if (statuss.value == true) return;
		getRecordApi({
			page: page.value,
			limit: limit.value
		}).then(res => {
			let len = res.data.list ? res.data.list.length : 0;
			let recordListData = res.data.list || [];
			statuss.value = limit.value > len;
			page.value = page.value + 1;
			recordList.value = recordList.value.concat(recordListData);
		});
	}

	function getRecordList() {
		if (statuss.value == true) return;
		getCommissionInfo({
			page: page.value,
			limit: limit.value
		}).then(res => {
			if (res.data.list) {
				let len = res.data.list ? res.data.list.length : 0;
				let recordListData = res.data.list || [];
				statuss.value = limit.value > len;
				page.value = page.value + 1;
				recordList.value = recordList.value.concat(recordListData);
			}
		});
	}

	onReachBottom(() => {
		getRecordList();
	});
</script>

<style scoped lang="scss">
	.sign-record .list .item .listn .itemn {
		height: auto;
		padding: 24rpx;
		flex-wrap: nowrap;

		.left-box {
			flex-shrink: 1;
			min-width: 0;

			.name {
				width: auto;
				margin-bottom: 0;
			}

			.remark {
				min-width: 0;
			}
		}

		.num {
			flex-shrink: 0;
		}
	}

	.commission-details .promoterHeader .headerCon .money {
		font-size: 36rpx;
	}

	.promoterHeader {
		@include main_bg_color(theme);
	}

	.commission-details .promoterHeader .headerCon .money .num {
		font-family: 'Guildford Pro';
	}

	.font_color {
		color: #E93323 !important;
	}
</style>