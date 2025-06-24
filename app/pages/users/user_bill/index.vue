<template>
	<view :data-theme="theme">
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

<script>
	import {
		getBillList
	} from '@/api/user.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import emptyPage from '@/components/emptyPage.vue';
	let app = getApp();
	export default {
		components: {
			emptyPage
		},
		data() {
			return {
				loadTitle: '加载更多',
				loading: false,
				loadend: false,
				page: 1,
				limit: 12,
				type: 'all',
				userBillList: [],
				theme:app.globalData.theme,
			};
		},
		computed: mapGetters(['isLogin']),
		onShow() {
			if (this.isLogin) {
				this.getUserBillList();
			} else {
				toLogin();
			}
		},
		/**
		 * 生命周期函数--监听页面加载
		 */
		onLoad: function(options) {
			this.type = options.type?options.type:'all';
		},
		/**
		 * 页面上拉触底事件的处理函数
		 */
		onReachBottom: function() {
			this.getUserBillList();
		},
		methods: {
			/**
			 * 获取账户明细
			 */
			getUserBillList: function() {
				let that = this;
				if (that.loadend) return;

				if (that.loading) return;
				that.loading = true;
				that.loadTitle = "";
				let data = {
					page: that.page,
					limit: that.limit,
					type: that.type
				}
				getBillList(data).then(function(res) {
					let list = res.data.list ? res.data.list : [],
					loadend = res.data.totalPage <= that.page;
					for (let i = 0; i < list.length; i++) {
						let time1 = list[i].date;
						let array1 = list[i].list;
						let isEquals = false;
						for (let j = 0; j < that.userBillList.length; j++) {
							let time2 = that.userBillList[j].date;
							let array2 = that.userBillList[j].list;
							if (time1 == time2) {
								array2.push.apply(array2, array1);
								that.userBillList[j].list = array2;
								isEquals = true;
								break;
							}
						}
						if (!isEquals) {
							that.userBillList.push({
								date: time1,
								list: array1
							})
						}
					}
                    that.$set(that, 'userBillList', that.userBillList);
					that.page += 1;
					that.loadend = loadend;
					that.loading = false;
					that.loadTitle = loadend ? "我也是有底线的~" : "加载更多";
				}, function(res) {
					that.loading = false;
					that.loadTitle = '加载更多';
				});
			},
			/**
			 * 切换导航
			 */
			changeType: function(type) {
				this.type = type;
				this.loadend = false;
				this.page = 1;
				this.$set(this, 'userBillList', []);
				this.getUserBillList();
			},
		}
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
