<template>
	<view>
		<view class='commission-details'>
			<view class='promoterHeader bg-color'>
				<view class='headerCon acea-row row-between-wrapper'>
					<view>
						<view class='name'>{{name}}</view>
						<view class='money' v-if="recordType == 4">￥<text class='num'>{{extractCount}}</text></view>
						<view class='money' v-else>￥<text class='num'>{{commissionCount}}</text></view>
					</view>
					<view class='iconfont icon-jinbi1'></view>
				</view>
			</view>
			<view class='sign-record' v-if="recordType == 4">
				<block v-for="(item,index) in recordList" :key="index" v-if="recordList.length>0">
					<view class='list pad30'>
						<view class='item'>
							<view class='data'>{{item.date}}</view>
							<view class='listn borRadius14'>
								<block v-for="(child,indexn) in item.list" :key="indexn">
									<view class='itemn acea-row row-between-wrapper'>
										<view>
											<view class='name line1'>{{child.status | statusFilter}}</view>
											<view>{{child.createTime}}</view>
										</view>
										<view class='num font-color' v-if="child.status == 1">+{{child.extractPrice}}
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
				<block v-for="(item,index) in recordList" :key="index" v-if="recordList.length>0">
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
										<view class='num font-color' v-if="child.type == 1">+{{child.price}}
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
		<!-- #ifdef MP -->
		<!-- <authorize @onLoadFun="onLoadFun" :isAuto="isAuto" :isShowAuth="isShowAuth" @authColse="authColse"></authorize> -->
		<!-- #endif -->
		<home></home>
	</view>
</template>

<script>
	import {
		getCommissionInfo,
		getRecordApi,
	} from '@/api/user.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	// #ifdef MP
	import authorize from '@/components/Authorize';
	// #endif
	import emptyPage from '@/components/emptyPage.vue'
	import home from '@/components/home';
	export default {
		components: {
			// #ifdef MP
			authorize,
			// #endif
			emptyPage,
			home
		},
		filters: {
			statusFilter(status) {
				const statusMap = {
					'-1': '未通过',
					'0': '审核中',
					'1': '已提现'
				}
				return statusMap[status]
			}
		},
		data() {
			return {
				name: '',
				type: 0,
				page: 1,
				limit: 10,
				recordList: [],
				recordType: 0,
				statuss: false,
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false, //是否隐藏授权
				extractCount: 0
			};
		},
		computed: mapGetters(['isLogin']),
		onLoad(options) {
			if (this.isLogin) {
				this.type = options.type;
				this.extractCount = options.extractCount;
				this.commissionCount = options.commissionCount;
			} else {
				toLogin();
			}
		},
		onShow: function() {
			let type = this.type;
			if (type == 1) {
				uni.setNavigationBarTitle({
					title: "提现记录"
				});
				this.name = '提现总额';
				this.recordType = 4;
				this.getList();
			} else if (type == 2) {
				uni.setNavigationBarTitle({
					title: "佣金记录"
				});
				this.name = '佣金明细';
				this.recordType = 3;
				this.getRecordList();
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

		},
		methods: {
			onLoadFun() {
				this.getRecordList();
			},
			// 授权关闭
			authColse: function(e) {
				this.isShowAuth = e
			},
			getList: function() {
				let that = this;
				let recordList = that.recordList;
				let recordListNew = [];
				if (that.statuss == true) return;
				getRecordApi({
					page: that.page,
					limit: that.limit
				}).then(res => {
					let len = res.data.list ? res.data.list.length : 0;
					let recordListData = res.data.list || [];
					recordListNew = recordList.concat(recordListData);
					that.statuss = that.limit > len;
					that.page = that.page + 1;
					that.$set(that, 'recordList', recordListNew);
				});
			},
			getRecordList: function() {
				let that = this;
				let page = that.page;
				let limit = that.limit;
				let statuss = that.statuss;
				let recordType = that.recordType;
				let recordList = that.recordList;
				let recordListNew = [];
				if (statuss == true) return;
				getCommissionInfo({
					page: page,
					limit: limit
				}).then(res => {
					if (res.data.list) {
						let len = res.data.list ? res.data.list.length : 0;
						let recordListData = res.data.list || [];
						recordListNew = recordList.concat(recordListData);
						that.statuss = limit > len;
						that.page = page + 1;
						that.$set(that, 'recordList', recordListNew);
					}
				});
			}
		},
		onReachBottom: function() {
			this.getRecordList();
		}
	}
</script>

<style scoped lang="scss">
	.commission-details .promoterHeader .headerCon .money {
		font-size: 36rpx;
	}

	.commission-details .promoterHeader .headerCon .money .num {
		font-family: 'Guildford Pro';
	}
</style>
