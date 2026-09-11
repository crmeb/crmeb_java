<template>
	<view :data-theme="theme" :style="colorStyle">
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
			<view class='noCommodity' v-if="!replyData.sumCount && page > 1">
				<view class='pictrue'>
					<image :src="urlDomain+'/crmebimage/perset/usersImg/noEvaluate.png'"></image>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref } from "vue";
import { onLoad, onShow, onReachBottom } from "@dcloudio/uni-app";
import { getReplyList, getReplyConfig } from "@/api/store.js";
import userEvaluation from "@/components/userEvaluation/index.vue";
import util from "@/utils/util.js";
import Cache from "@/utils/cache.js";
import { useColor } from '@/composables/useColor.js';

const app = getApp();
const urlDomain = ref(Cache.get("imgHost"));
const replyData = ref({});
const productId = ref(0);
const reply = ref([]);
const type = ref(0);
const loading = ref(false);
const loadend = ref(false);
const loadTitle = ref("加载更多");
const page = ref(1);
const limit = ref(20);
const theme = ref(app.globalData.theme);
const { colorStyle } = useColor();

onLoad((options) => {
	if (!options.productId) return util.Tips({ title: '缺少参数' }, { tab: 3, url: 1 });
	productId.value = options.productId;
});

onShow(() => { getProductReplyCount(); getProductReplyList(); });
onReachBottom(() => { getProductReplyList(); });

function getProductReplyCount() {
	getReplyConfig(productId.value).then(res => { replyData.value = res.data; });
}

function getProductReplyList() {
	if (loadend.value) return;
	if (loading.value) return;
	loading.value = true;
	loadTitle.value = '';
	getReplyList(productId.value, { page: page.value, limit: limit.value, type: type.value })
		.then(res => {
			let list = res.data.list;
			let isEnd = list.length < limit.value;
			reply.value = util.SplitArray(list, reply.value);
			loading.value = false;
			loadend.value = isEnd;
			if (reply.value.length) loadTitle.value = isEnd ? "我也是有底线的~" : "加载更多";
			page.value++;
		}).catch(() => { loading.value = false; loadTitle.value = '加载更多'; });
}

function changeType(e) {
	let t = parseInt(e);
	if (t == type.value) return;
	type.value = t;
	page.value = 1;
	loadend.value = false;
	reply.value = [];
	getProductReplyList();
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
		width: max-content;
	}

	.evaluate-list .nav .item.bg-color {
		color: #fff;
		@include main_bg_color(theme);
	}
</style>
