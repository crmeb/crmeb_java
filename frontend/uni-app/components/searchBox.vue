<template>
	<view  class="acea-row search-contain" :style="{'margin-top':searchTop+'px'}">
		<text class='iconfont icon-fanhui2' @click="toBack" v-if="toBackShow"></text>
		<view class='search-box acea-row row-between-wrapper' :style="[searchBoxStyle]">
			<text class='iconfont icon-sousuo2'></text>
			<input  :value="searchVal" @confirm="inputSearch" type='text' confirm-type='search' name="search" placeholder='点击搜索商品' placeholder-class='placeholder' maxlength="20"></input>
		</view>
	</view>
</template>

<script setup>
	import { ref, computed, watch } from 'vue';

	const props = defineProps({
		//是否展示返回按钮
		toBackShow: {
			type: Boolean,
			default: true
		},
		searchValue:{
			type: String,
			default: ''
		}
	});

	const emit = defineEmits(['searchChange']);

	const searchVal = ref('');
	const searchTop = ref(0);
	const searchRight = ref(0);
	const searchHeight = ref(0);
	const statusWidth = ref(0);

	watch(() => props.searchValue, (val) => {
		searchVal.value = val
	});

	const searchBoxStyle = computed(() => {
		return {
			height: searchHeight.value + 'px',
			flex: 1,
			marginRight: statusWidth.value + searchRight.value + 'px',
		}
	});

	const res = uni.getMenuButtonBoundingClientRect()
	searchTop.value = uni.getMenuButtonBoundingClientRect().top
	const statusRight = res.right //胶囊右边界坐标
	const jnHeight = res.height //胶囊高度
	statusWidth.value = res.width
	searchHeight.value = jnHeight
	//搜索框宽度计算
	uni.getSystemInfo({
		success: r => {
			searchRight.value = r.windowWidth - statusRight
		}
	})

	function inputSearch(e){
		emit('searchChange', e)
	}
	function toBack(){
		uni.navigateBack()
	}
</script>

<style scoped lang="scss">
	.search-contain{
		padding: 0 20rpx 10rpx 0;
	}
	.search-box {
		margin-left: 16rpx;
		background-color: #f7f7f7;
		border-radius: 33rpx;
		padding: 0 35rpx;
		box-sizing: border-box;
		height: 66rpx;
	}
	.icon-fanhui2{
		line-height: 66rpx;
	}
	.search-box input {
		width: 85%;
		font-size: 26rpx;
	}

	.search-box .placeholder {
		color: #bbb;
	}

	.search-box .iconfont {
		color: #000;
		font-size: 35rpx;
	}
</style>