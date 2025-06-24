<template>
	<view :data-theme="theme">
		<view style="height: 100%;">
			<view class='evaluate-list'>
				<view class='generalComment acea-row row-between-wrapper'>
					<view class='acea-row row-middle font_color'>
						<view class='evaluate'>评分</view>
						<view class='start'
							:class="'star'+ (replyData.sumCount===0?'3':Math.round(replyData.replyStar/replyData.sumCount))">
						</view>
					</view>
					<view><text class='font_color'>{{(replyData.replyChance)*100}}%</text>好评率</view>
				</view>
				<view class='nav acea-row row-middle'>
					<view class='item' :class='type==0 ? "bg-color":""' @click='changeType(0)'>全部({{replyData.sumCount}})
					</view>
					<view class='item' :class='type==1 ? "bg-color":""' @click='changeType(1)'>好评({{replyData.goodCount}})
					</view>
					<view class='item' :class='type==2 ? "bg-color":""' @click='changeType(2)'>中评({{replyData.inCount}})
					</view>
					<view class='item' :class='type==3 ? "bg-color":""' @click='changeType(3)'>差评({{replyData.poorCount}})
					</view>
				</view>
				<userEvaluation :reply="reply"></userEvaluation>
			</view>
			<view class='loadingicon acea-row row-center-wrapper'>
				<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
			</view>
			<view class='noCommodity' v-if="!(type==0&&replyData.sumCount||type==1&&replyData.goodCount||type==2&&replyData.inCount||type==3&&replyData.poorCount) && page > 1">
				<view class='pictrue'>
					<image :src="urlDomain+'crmebimage/perset/usersImg/noEvaluate.png'"></image>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getReplyList,
		getReplyConfig
	} from '@/api/store.js';
	import userEvaluation from '@/components/userEvaluation';
	let app = getApp();
	export default {
		components: {
			userEvaluation
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				replyData: {},
				product_id: 0,
				reply: [],
				type: 0,
				loading: false,
				loadend: false,
				loadTitle: '加载更多',
				page: 1,
				limit: 20,
				theme:app.globalData.theme,
			};
		},
		/**
		 * 生命周期函数--监听页面加载
		 */
		onLoad: function(options) {
			let that = this;
			if (!options.productId) return that.$util.Tips({
				title: '缺少参数'
			}, {
				tab: 3,
				url: 1
			});
			that.productId = options.productId;
		},
		onShow: function() {
			this.getProductReplyCount();
			this.getProductReplyList();
		},
		methods: {
			/**
			 * 获取评论统计数据
			 * 
			 */
			getProductReplyCount: function() {
				let that = this;
				getReplyConfig(that.productId).then(res => {
					that.$set(that, 'replyData', res.data);
				});
			},
			/**
			 * 分页获取评论
			 */
			getProductReplyList: function() {
				let that = this;
				if (that.loadend) return;
				if (that.loading) return;
				that.loading = true;
				that.loadTitle = '';
				getReplyList(that.productId, {
					page: that.page,
					limit: that.limit,
					type: that.type,
				}).then(res => {
					let list = res.data.list,
						loadend = list.length < that.limit;
					that.reply = that.$util.SplitArray(list, that.reply);
					that.$set(that, 'reply', that.reply);
					that.loading = false;
					that.loadend = loadend;
					if (that.reply.length) {
						that.loadTitle = loadend ? "我也是有底线的~" : "加载更多";
					}
					that.page = that.page + 1;
				}).catch(err => {
					that.loading = false,
						that.loadTitle = '加载更多'
				});
			},
			/*
			 * 点击事件切换
			 * */
			changeType: function(e) {
				let type = parseInt(e);
				if (type == this.type) return;
				this.type = type;
				this.page = 1;
				this.loadend = false;
				this.$set(this, 'reply', []);
				this.getProductReplyList();
			}
		},
		/**
		 * 页面上拉触底事件的处理函数
		 */
		onReachBottom: function() {
			this.getProductReplyList();
		},
	}
</script>

<style lang="scss">
	page {
		background-color: #fff;
		height: 100%;
	}
	.font_color{
		@include main_color(theme);
	}
    .evaluate-list{
		padding: 30rpx 0 0 0;
		background-color: #fff;
	}
	.evaluate-list .generalComment {
		padding: 0 30rpx;
		margin-top: 1rpx;
		background-color: #fff;
		font-size: 28rpx;
		color: #808080;
	}

	.evaluate-list .generalComment .evaluate {
		margin-right: 7rpx;
		color: #333333;
		font-size: 28rpx;
	}

	.evaluate-list .nav {
		font-size: 24rpx;
		color: #282828;
		padding: 30rpx;
		background-color: #fff;
		border-bottom: 1rpx solid #f5f5f5;
	}

	.evaluate-list .nav .item {
		font-size: 24rpx;
		color: #282828;
		border-radius: 27rpx;
		height: 54rpx;
		padding: 0 20rpx;
		background-color: #f4f4f4;
		line-height: 54rpx;
		margin-right: 17rpx;
	}

	.evaluate-list .nav .item.bg-color {
		color: #fff;
		@include main_bg_color(theme);
	}
</style>
