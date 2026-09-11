<template>
	<view class="wf-page">
		<!--    left    -->
		<view>
			<view id="left" v-if="leftList.length">
				<view v-for="(item,index) in leftList" :key="index" class="wf-item">
					<WaterfallsFlowItem  :item="item" :isStore="isStore" :type="type"/>
				</view>
			</view>
		</view>
		<!--    right    -->
		<view>
			<view id="right" v-if="rightList.length">
				<view v-for="(item,index) in rightList" :key="index" class="wf-item">
					<WaterfallsFlowItem  :item="item" :isStore="isStore" :type="type"/>
				</view>
			</view>
		</view>
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
	import { ref, watch, nextTick, getCurrentInstance } from 'vue'
	import WaterfallsFlowItem from '../WaterfallsFlowItem/WaterfallsFlowItem.vue'

	const { proxy } = getCurrentInstance();

	const props = defineProps({
		// 区分从发现列表、我的主页作品进去，点进去内容列表，home从我的主页点进去
		fromTo: {
			type: String,
			default: ''
		},
		// 区分瀑布流使用子组件，1逛逛瀑布流
		fromType: {
			type: Number || String,
			default: 0
		},
		// 瀑布流列表
		wfList: {
			type: Array || String,
			require: true
		},
		updateNum: {
			type: Number,
			default: 10
		},
		type: {
			type: Number,
			default: 0
		},
		isStore: {
			type: Number,
			default: 0
		}
	});

	const emit = defineEmits(['itemTap']);

	const allList = ref([]); // 全部列表
	const leftList = ref([]); // 左边列表
	const rightList = ref([]); // 右边列表
	const mark = ref(0); // 列表标记
	const boxHeight = ref([]); // 下标0和1分别为左列和右列高度

	// 监听列表数据变化
	watch(() => props.wfList, (nVal, oVal) => {
		// 如果数据为空或新的列表数据少于旧的列表数据（通常为下拉刷新或切换排序或使用筛选器），初始化变量
		if (!props.wfList.length ||
			(props.wfList.length === props.updateNum && props.wfList.length <= allList.value.length)) {
			allList.value = [];
			leftList.value = [];
			rightList.value = [];
			boxHeight.value = [];
			mark.value = 0;
		}

		// 如果列表有值，调用waterfall方法
		if (props.wfList.length) {
			allList.value = props.wfList;
			leftList.value = [];
			rightList.value = [];
			boxHeight.value = [];
			allList.value.forEach((v, i) => {
				if (allList.value.length < 3 || (allList.value.length <= 7 && allList.value.length - i >
						1) || (allList.value.length > 7 && allList.value.length - i > 2)) {
					if (i % 2) {
						rightList.value.push(v);
					} else {
						leftList.value.push(v);
					}
				}
			});
			if (allList.value.length < 3) {
				mark.value = allList.value.length + 1;
			} else if (allList.value.length <= 7) {
				mark.value = allList.value.length - 1;
			} else {
				mark.value = allList.value.length - 2;
			}
			if (mark.value < allList.value.length) {
				waterFall()
			}
		}
	}, {
		immediate: true,
		deep: true
	});

	// 监听标记，当标记发生变化，则执行下一个item排序
	watch(mark, () => {
		const len = allList.value.length;
		if (mark.value < len && mark.value !== 0 && boxHeight.value.length) {
			waterFall();
		}
	});

	// 瀑布流排序
	function waterFall() {
		const i = mark.value;
		if (i == 0) {
			// 初始化，从左边开始插入
			leftList.value.push(allList.value[i]);
			// 更新左边列表高度
			getViewHeight(0);
		} else if (i == 1) {
			// 第二个item插入，默认为右边插入
			rightList.value.push(allList.value[i]);
			// 更新右边列表高度
			getViewHeight(1);
		} else {
			// 根据左右列表高度判断下一个item应该插入哪边
			if (!boxHeight.value.length) {
				rightList.value.length < leftList.value.length ?
					rightList.value.push(allList.value[i]) :
					leftList.value.push(allList.value[i]);
			} else {
				const leftOrRight = boxHeight.value[0] > boxHeight.value[1] ? 1 : 0;
				if (leftOrRight) {
					rightList.value.push(allList.value[i])
				} else {
					leftList.value.push(allList.value[i])
				}
			}
			// 更新插入列表高度
			getViewHeight();
		}
	}

	// 获取列表高度
	function getViewHeight() {
		// 使用nextTick，确保页面更新结束后，再请求高度
		nextTick(() => {
			setTimeout(() => {
				uni.createSelectorQuery().in(proxy).select('#right').boundingClientRect(res => {
					res ? boxHeight.value[1] = res.height : '';
					uni.createSelectorQuery().in(proxy).select('#left').boundingClientRect(
						res => {
							res ? boxHeight.value[0] = res.height : '';
							mark.value = mark.value + 1;
						}).exec();
				}).exec();
			}, 100)
		})
	}

	// item点击
	function itemTap(item) {
		emit('itemTap', item)
	}
</script>

<style lang="scss" scoped>
	$page-padding: 12px;
	$grid-gap: 24rpx;

	.wf-page {
		padding: 0 20rpx;
		display: grid;
		grid-template-columns: 1fr 1fr;
		grid-gap: $grid-gap;
	}

	.wf-item {
		width: calc((100vw - 2 * #{$page-padding} - #{$grid-gap}) / 2);
		padding-bottom: $grid-gap;
		//margin-bottom: 20rpx;
	}

	.wf-page1 .wf-item {
		background-color: #fff;
		border-radius: 20rpx;
		padding-bottom: 0;
	}

	.wf-item-page {
		padding-bottom: 20rpx;
	}
</style>
