<template>
	<view class='recommend'>
		<block v-if="tempArr.length">
			<view v-if="isShowTitle" class="flex-center recommend-box mt-20 mb-24">
				<image :src="`${urlDomain}crmebimage/presets/haowuzuo.png`"></image>
				<view class="f-s-32 lh-44rpx ml-4">热门推荐</view>
				<image class="ml-6" :src="`${urlDomain}crmebimage/presets/haowuyou.png`"></image>
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

<script>
	// +----------------------------------------------------------------------
	// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
	// +----------------------------------------------------------------------
	// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
	// +----------------------------------------------------------------------
	// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
	// +----------------------------------------------------------------------
	// | Author: CRMEB Team <admin@crmeb.com>
	// +----------------------------------------------------------------------
	import {
		mapGetters
	} from "vuex";
	import animationType from '@/utils/animationType.js'
	import {
		getProductslist
	} from '@/api/store.js';
	import WaterfallsFlow from '@/components/WaterfallsFlow/WaterfallsFlow.vue';
	let app = getApp();
	export default {
		name: 'recommend',
		computed: mapGetters(['uid']),
		components: {
			WaterfallsFlow
		},
		props: {
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
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				theme: app.globalData.theme,
				goodScroll: false,
				params: { //精品推荐分页
					page: 1,
					limit: 10,
					cid: 0
				},
				loading: false,
				tempArr: []
			};
		},
		computed:{
			myCategoryId(){
				return this.categoryId
			}
		},
		watch: {
			myCategoryId: function(val) { //监听props中的属性
				this.params.page = 1;
				this.tempArr = [];
				this.goodScroll = false;
				this.get_host_product()
			}
		},
		mounted() {
			if (this.isDefault) {
				this.params.page = 1;
				this.goodScroll = false;
				this.tempArr = [];
				this.get_host_product()
			} else {
				this.tempArr = this.recommendList
			};
		},
		methods: {
			/**
			 * 获取我的推荐
			 */
			get_host_product: function() {
				if (this.goodScroll) return;
				this.loading = true
				this.params.cid = this.categoryId;
				getProductslist(
					this.params
				).then((res) => {
					this.$set(this.params, 'page', this.params.page + 1);
					this.goodScroll = this.params.page > res.data.totalPage;
					this.tempArr = this.tempArr.concat(res.data.list || []);
					// this.$emit('getRecommendLength', this.tempArr.length);
					this.loading = false
				}).catch(err => {
					this.loading = false
				});
			}
		},
		onReachBottom() {
			if (this.isDefault) this.get_host_product();
		}
	}
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