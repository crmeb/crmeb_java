<template>
	<!-- 文章列表 -->
	<view>
		<view class="articleList" :style="[boxStyle]" v-if="articleList.length">
			<view v-if="listStyle">
				<navigator :url='"/pages/news/news_details/index?id="+item.id' hover-class='none' :style="[itemStyle]"
					 v-for="(item,index) in articleList" :key='index'
					class="item acea-row row-between-wrapper">
					<view class="pictrue">
						<easy-loadimage :image-src="item.imageInput" :radius="dataConfig.contentStyle.val"></easy-loadimage>
					</view>
					<view class="text">
						<view class="name line2" :style="[titleColor]">{{item.title}}</view>
						<view class="time" :style="[timeColor]">{{item.updateTime}}</view>
					</view>
				</navigator>
			</view>
			<view v-else>
				<navigator  :url='"/pages/news/news_details/index?id="+item.id' hover-class='none'
					:style="[itemStyle]" v-for="(item,index) in articleList" :key='index'
					class="item acea-row row-between-wrapper">
					<view class="text">
						<view class="name line2" :style="[titleColor]">{{item.title}}</view>
						<view class="time" :style="[timeColor]">{{item.updateTime}}</view>
					</view>
					<view class="pictrue">
						<easy-loadimage :image-src="item.imageInput" :radius="dataConfig.contentStyle.val"></easy-loadimage>
					</view>
				</navigator>
			</view>
		</view>
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
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	export default {
		name: 'homeArticle',
		props: {
			dataConfig: {
				type: Object,
				default: () => {}
			},
		},
		data() {
			return {}
		},
		components: {
			easyLoadimage
		},
		computed: {
			//布局样式
			listStyle() {
				return this.dataConfig.layoutConfig.tabVal === 0
			},
			//文章分类
			articleList() {
				return this.dataConfig.selectConfig.articleList
			},
			//最外层盒子的样式
			boxStyle() {
				return {
					borderRadius: this.dataConfig.bgStyle.val * 2 + 'rpx',
					background: `linear-gradient(${this.dataConfig.bgColor.color[0].item}, ${this.dataConfig.bgColor.color[1].item})`,
					margin: this.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + this.dataConfig.lrConfig.val * 2 + 'rpx' +
						' ' + 0,
					padding: this.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + 0 + ' ' + this.dataConfig.downConfig.val *
						2 + 'rpx'
				}
			},
			//文章间距
			itemStyle() {
				return {
					'margin-bottom': this.dataConfig.contentConfig.val * 2 + 'rpx'
				}
			},
			//时间颜色
			timeColor() {
				return {
					'color': this.dataConfig.timeColor.color[0].item
				}
			},
			//标题颜色
			titleColor() {
				return {
					'color': this.dataConfig.titleColor.color[0].item
				}
			}
		}
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