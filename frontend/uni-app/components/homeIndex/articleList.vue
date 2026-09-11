<template>
	<!-- 文章列表 -->
	<view>
		<view class="articleList" :style="[boxStyle]" v-if="articleList.length && articleListData.length">
			<view v-if="listStyle">
				<navigator :render-link="false" :url='"/pages/news/news_details/index?id="+item.id' hover-class='none' :style="[itemStyle]"
					 v-for="(item,index) in articleList" :key='index'
					class="item acea-row row-between-wrapper">
					<view class="pictrue">
						<easy-loadimage :image-src="articleListData[index].imageInput" :radius="dataConfig.contentStyle.val"></easy-loadimage>
					</view>
					<view class="text">
						<view class="name line2" :style="[titleColor]">{{articleListData[index].title}}</view>
						<view class="time" :style="[timeColor]">{{articleListData[index].createTime}}</view>
					</view>
				</navigator>
			</view>
			<view v-else>
				<navigator :render-link="false"  :url='"/pages/news/news_details/index?id="+item.id' hover-class='none'
					:style="[itemStyle]" v-for="(item,index) in articleList" :key='index'
					class="item acea-row row-between-wrapper">
					<view class="text">
						<view class="name line2" :style="[titleColor]">{{articleListData[index].title}}</view>
						<view class="time" :style="[timeColor]">{{articleListData[index].createTime}}</view>
					</view>
					<view class="pictrue">
						<easy-loadimage :image-src="articleListData[index].imageInput" :radius="dataConfig.contentStyle.val"></easy-loadimage>
					</view>
				</navigator>
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
	import { ref, computed } from 'vue';
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	import {getArticleList} from '@/api/api.js'

	const props = defineProps({
		dataConfig: {
			type: Object,
			default: () => {}
		},
	});

	const cid = ref(0);
	const articleListData = ref([]); // 文章列表数据

	//布局样式
	const listStyle = computed(() => {
		return props.dataConfig.layoutConfig.tabVal === 0
	});
	//文章分类
	const articleList = computed(() => {
		return props.dataConfig.selectConfig.articleList
	});
	//最外层盒子的样式
	const boxStyle = computed(() => {
		return {
			borderRadius: props.dataConfig.bgStyle.val * 2 + 'rpx',
			background: `linear-gradient(${props.dataConfig.bgColor.color[0].item}, ${props.dataConfig.bgColor.color[1].item})`,
			margin: props.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + props.dataConfig.lrConfig.val * 2 + 'rpx' +
				' ' + 0,
			padding: props.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + 0 + ' ' + props.dataConfig.downConfig.val *
				2 + 'rpx'
		}
	});
	//文章间距
	const itemStyle = computed(() => {
		return {
			'margin-bottom': props.dataConfig.contentConfig.val * 2 + 'rpx'
		}
	});
	//时间颜色
	const timeColor = computed(() => {
		return {
			'color': props.dataConfig.timeColor.color[0].item
		}
	});
	//标题颜色
	const titleColor = computed(() => {
		return {
			'color': props.dataConfig.titleColor.color[0].item
		}
	});

	// created
	// 获取cid
	cid.value = props.dataConfig.selectConfig.articleList[0].cid
	// 获取文章列表数据
	getArticleListData()

	// 获取文章列表
	function getArticleListData() {
		getArticleList(cid.value).then(res =>{
			articleListData.value = res.data.list
		}).catch(err => {
		})
	}
</script>

<style lang="scss" scoped>
	.articleList {

		.item {
			padding: 0 20rpx;

			&:last-child {
				margin-bottom: 0 !important;
			}

			.text {
				width: 60%;

				.name {
					font-size: 30rpx;
					color: #282828;
					height: 82rpx;
				}

				.time {
					font-size: 30rpx;
					color: #999;
					margin-top: 40rpx;
				}
			}

			.pictrue {
				width: 37%;
				height: 156rpx;
				border-radius: 6rpx;

				image {
					width: 100%;
					height: 100%;
					border-radius: 6rpx;
				}
			}
		}
	}
</style>