<template>
	<view class='recommend'>
		<block v-if="tempArr.length">
			<view v-if="isShowTitle" class="flex-center recommend-box mt-20 mb-24">
				<image :src="`${urlDomain}/crmebimage/presets/haowuzuo.png`"></image>
				<view class="f-s-32 lh-44rpx ml-4">热门推荐</view>
				<image class="ml-6" :src="`${urlDomain}/crmebimage/presets/haowuyou.png`"></image>
			</view>
			<view class='recommendList borderPad' :class="isShowTitle?'':'mt30'">
				<WaterfallsFlow :wfList='tempArr' :type="1" :isStore="1">
				</WaterfallsFlow>
			</view>
			<view class='loadingicon acea-row row-center-wrapper' :hidden='loading==false'>
				<text class='loading iconfont icon-jiazai'></text>
			</view>
			<view class="mores-txt flex" v-if="goodScroll">
				<text>我是有底线的</text>
			</view>
		</block>
	</view>
</template>

<script setup>
	// +----------------------------------------------------------------------
	// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
	// +----------------------------------------------------------------------
	// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
	// +----------------------------------------------------------------------
	// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
	// +----------------------------------------------------------------------
	// | Author: CRMEB Team <admin@crmeb.com>
	// +----------------------------------------------------------------------
	import { ref, reactive, computed, watch, onMounted } from 'vue';
	import { onReachBottom } from '@dcloudio/uni-app';
	import Cache from '@/utils/cache.js';
	import animationType from '@/utils/animationType.js'
	import {
		getProductslist
	} from '@/api/store.js';
	import WaterfallsFlow from '@/components/WaterfallsFlow/WaterfallsFlow.vue';

	const app = getApp();

	const props = defineProps({
		categoryId: {
			type: Number,
			default: function() {
				return 0;
			}
		},
		//是否显示头部
		isShowTitle: {
			type: Boolean,
			default: function() {
				return true;
			}
		},
		//是否使用本页面的请求数据
		isDefault: {
			type: Boolean,
			default: function() {
				return true;
			}
		},
		//使用的页面中调用数据传来的商品列表，isDefault为false时使用
		recommendList: {
			type: Array,
			default: function() {
				return [];
			}
		}
	});

	const urlDomain = ref(Cache.get("imgHost"));
	const theme = ref(app.globalData.theme);
	const goodScroll = ref(false);
	const params = reactive({ //精品推荐分页
		page: 1,
		limit: 10,
		cid: 0
	});
	const loading = ref(false);
	const tempArr = ref([]);

	const myCategoryId = computed(() => props.categoryId);

	watch(myCategoryId, () => { //监听props中的属性
		params.page = 1;
		tempArr.value = [];
		goodScroll.value = false;
		get_host_product()
	});

	onMounted(() => {
		if (props.isDefault) {
			params.page = 1;
			goodScroll.value = false;
			tempArr.value = [];
			get_host_product()
		} else {
			tempArr.value = props.recommendList
		}
	});

	/**
	 * 获取我的推荐
	 */
	function get_host_product() {
		if (goodScroll.value) return;
		loading.value = true
		params.cid = props.categoryId;
		getProductslist(
			params
		).then((res) => {
			params.page = params.page + 1;
			goodScroll.value = params.page > res.data.totalPage;
			tempArr.value = tempArr.value.concat(res.data.list || []);
			loading.value = false
		}).catch(err => {
			loading.value = false
		});
	}

	onReachBottom(() => {
		if (props.isDefault) get_host_product();
	});
</script>

<style scoped lang="scss">
	.recommend-box {
		image {
			width: 42rpx;
			height: 36rpx;
		}

		view {
			font-weight: 600;
		}
	}

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
		.title {
			height: 120rpx;
			line-height: 120rpx;
			font-size: 32rpx;
			color: #333333;

			.iconfont {
				font-size: 170rpx;
				color: #454545;
			}
		}

		.name {
			margin: 0 28rpx;
		}
	}
</style>