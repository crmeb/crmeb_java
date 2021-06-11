<template>
	<view>
		<view class="promoter-order">
			<view class='promoterHeader bg-color'>
				<view class='headerCon acea-row row-between-wrapper'>
					<view>
						<view class='name'>累积推广订单</view>
						<view><text class='num'>{{recordCount || 0}}</text>单</view>
					</view>
					<view class='iconfont icon-2'></view>
				</view>
			</view>
			<view class='list pad30' v-if="recordList.length>0">
				<block v-for="(item,index) in recordList" :key="index">
					<view class='item'>
						<view class='title acea-row row-column row-center'>
							<view class='data'>{{item.time}}</view>
							<view>本月累计推广订单：{{item.count || 0}}单</view>
						</view>
						<view class='listn'>
							<block v-for="(child,indexn) in item.child" :key="indexn">
								<view class='itenm borRadius14'>
									<view class='top acea-row row-between-wrapper'>
										<view class='pictxt acea-row row-between-wrapper'>
											<view class='pictrue'>
												<image :src='child.avatar'></image>
											</view>
											<view class='text line1'>{{child.nickname}}</view>
										</view>
										<view class='money'>返佣：<text class='font-color'>￥{{child.number}}</text></view>
									<!-- 	<view class='money' v-if="child.type == 'brokerage'">返佣：<text class='font-color'>￥{{child.number}}</text></view>
										<view class='money' v-else>暂未返佣：<text class='font-color'>￥{{child.number}}</text></view> -->
									</view>
									<view class='bottom'>
										<view><text class='name'>订单编号：</text>{{child.orderId}}</view>
										<view><text class='name'>下单时间：</text>{{child.time}}</view>
									</view>
								</view>
							</block>
						</view>
					</view>
				</block>
			</view>
			<view v-if="recordList.length == 0">
				<emptyPage title="暂无推广订单～"></emptyPage>
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
		spreadOrder
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
		data() {
			return {
				page: 1,
				limit: 10,
				status: false,
				recordList: [],
				recordCount: 0,
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false,//是否隐藏授权
				time: 0
			};
		},
		computed: mapGetters(['isLogin']),
		onLoad() {
			if (this.isLogin) {
				this.getRecordOrderList();
			} else {
				toLogin();
			}
		},
		methods: {
			stringToDate : function(data){
				let str = data.replace(/-/g,'/');
				let date = new Date(str);
				return data;
			 },
			onLoadFun() {
				this.getRecordOrderList();
			},
			// 授权关闭
			authColse: function(e) {
				this.isShowAuth = e
			},
			getRecordOrderList: function() {
				let that = this;
				let page = that.page;
				let limit = that.limit;
				let status = that.status;
				let recordList = that.recordList;
				let newList = [];
				if (status == true) return;
				spreadOrder({
					page: page,
					limit: limit
				}).then(res => {
					let recordListData = res.data.list ? res.data.list : [];
					// 每页返回的总条数；
					let len = 0;
					for(let i = 0;i<recordListData.length;i++) {
						len = len + recordListData[i].child.length;
						let str = recordListData[i].time.replace(/-/g,'/');
						let date = new Date(str).getTime();
						if(that.time === date){
							that.$set(that.recordList[i],'child',that.recordList[i].child.concat(recordListData[i].child));
						}else{
							recordListData.forEach((item,index)=>{
								if(recordListData[i]==item){
									newList.push(item);
								}
							})
							that.$set(that, 'recordList', recordList.concat(newList));
						}
						that.time = date;
					};
					that.recordCount = res.data.count || 0;
					that.status = limit > len;
					that.page = page + 1;
				});
			}
		},
		onReachBottom() {
			this.getRecordOrderList()
		}
	}
</script>

<style scoped lang="scss">
	.promoter-order .list .item .title {
		height: 133rpx;
		font-size: 26rpx;
		color: #999;
	}

	.promoter-order .list .item .title .data {
		font-size: 28rpx;
		color: #282828;
		margin-bottom: 5rpx;
	}

	.promoter-order .list .item .listn .itenm {
		background-color: #fff;
	}

	.promoter-order .list .item .listn .itenm~.itenm {
		margin-top: 20rpx;
	}

	.promoter-order .list .item .listn .itenm .top {
		padding: 0 24rpx;
		border-bottom: 1rpx solid #eee;
		height: 100rpx;
	}

	.promoter-order .list .item .listn .itenm .top .pictxt {
		width: 320rpx;
	}

	.promoter-order .list .item .listn .itenm .top .pictxt .text {
		width: 230rpx;
		font-size: 30rpx;
		color: #282828;
	}

	.promoter-order .list .item .listn .itenm .top .pictxt .pictrue {
		width: 66rpx;
		height: 66rpx;
	}

	.promoter-order .list .item .listn .itenm .top .pictxt .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 50%;
		border: 3rpx solid #fff;
		box-sizing: border-box;
		box-shadow: 0 0 15rpx #aaa;
	}

	.promoter-order .list .item .listn .itenm .top .money {
		font-size: 28rpx;
	}

	.promoter-order .list .item .listn .itenm .bottom {
		padding: 20rpx 24rpx;
		font-size: 28rpx;
		color: #666;
		line-height: 1.6;
	}

	.promoter-order .list .item .listn .itenm .bottom .name {
		color: #999;
	}
</style>
