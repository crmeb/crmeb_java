<template>
	<view class='recommend'>
		<view class='title acea-row row-center-wrapper'>
			<text class='iconfont icon-zhuangshixian'></text>
			<text class='name'>热门推荐</text>
			<text class='iconfont icon-zhuangshixian lefticon'></text>
		</view>
		<view class='recommendList acea-row row-between-wrapper'>
			<view class='item' v-for="(item,index) in tempArr" :key="index" hover-class='none'
				@tap="goDetail(item)">
				<view class='pictrue'>
					<image :src='item.image'></image>
					<span class="pictrue_log_big pictrue_log_class"
						v-if="item.activityH5 && item.activityH5.type === '1'">秒杀</span>
					<span class="pictrue_log_big pictrue_log_class"
						v-if="item.activityH5 && item.activityH5.type === '2'">砍价</span>
					<span class="pictrue_log_big pictrue_log_class"
						v-if="item.activityH5 && item.activityH5.type === '3'">拼团</span>
				</view>
				<view class='name line1'>{{item.storeName}}</view>
				<view class='money'>￥<text class='num'>{{item.price}}</text></view>
			</view>
		</view>
	    <view class='loadingicon acea-row row-center-wrapper' :hidden='loading==false'>
	    	<text class='loading iconfont icon-jiazai'></text>
	    </view>
	    <view class="mores-txt flex" v-if="goodScroll">
	    	<text>我也是有底线的~</text>
	    </view>
	</view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useAppStore } from "@/store/app.js";
import { storeToRefs } from 'pinia';
import {
	goShopDetail
} from '@/libs/order.js'
import animationType from '@/utils/animationType.js'
import {
	getProductHot
} from '@/api/store.js';

const appStore = useAppStore();
const { uid } = storeToRefs(appStore);

const props = defineProps({
	hostProduct: {
		type: Array,
		default: function() {
			return [];
		}
	}
});

const emit = defineEmits(['getRecommendLength']);

const goodScroll = ref(false);
const params = reactive({ //精品推荐分页
	page: 1,
	limit: 10
});
const loading = ref(false);
const tempArr = ref([]);

onMounted(() => {
	params.page = 1;
	goodScroll.value = false;
	tempArr.value = [];
	get_host_product();
});

/**
 * 获取我的推荐
 */
function get_host_product() {
	if (goodScroll.value) return;
	loading.value = true
	getProductHot(
		params.page,
		params.limit
	).then((res) => {
		params.page = params.page + 1;
		goodScroll.value = params.page > res.data.totalPage;
		tempArr.value = tempArr.value.concat(res.data.list || []);
		emit('getRecommendLength', tempArr.value.length);
		loading.value = false
	}).catch(err => {
		loading.value = false
	});
}

function goDetail(item) {
	goShopDetail(item, uid.value).then(res => {
		uni.navigateTo({
			animationType: animationType.type,
			animationDuration: animationType.duration,
			url: `/pages/goods/goods_details/index?id=${item.id}`
		})
	})
}

defineExpose({ get_host_product });
</script>

<style scoped lang="scss">
	.mores-txt {
		width: 100%;
		align-items: center;
		justify-content: center;
		height: 70rpx;
		color: #999;
		font-size: 24rpx;
	
		.iconfont {
			margin-top: 2rpx;
			font-size: 20rpx;
		}
	}
	.recommend {
		background-color: #fff;
	}

	.recommend .title {
		height: 135rpx;
		line-height: 135rpx;
		font-size: 28rpx;
		color: #282828;
	}

	.recommend .title .name {
		margin: 0 28rpx;
	}

	.recommend .title .iconfont {
		font-size: 170rpx;
		color: #454545;
	}

	.recommend .title .iconfont.lefticon {
		transform: rotate(180deg);
	}

	.recommend .recommendList {
		padding: 0 30rpx;
	}

	.recommend .recommendList .item {
		width: 335rpx;
		margin-bottom: 30rpx;
	}

	.recommend .recommendList .item .pictrue {
		position: relative;
		width: 100%;
		height: 335rpx;
	}

	.recommend .recommendList .item .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 14rpx;
	}

	.recommend .recommendList .item .name {
		font-size: 28rpx;
		color: #282828;
		margin-top: 20rpx;
	}

	.money {
		font-size: 20rpx;
		margin-top: 8rpx;
		font-weight: 600;
		@include price_color(theme);
	}

	.recommend .recommendList .item .money .num {
		font-size: 28rpx;
	}
</style>