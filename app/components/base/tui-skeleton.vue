<template>
	<view class="tui-skeleton-cmomon tui-skeleton-box" :style="{width: winWidth+'px', height:winHeight+'px', backgroundColor:backgroundColor}">
		<view class="tui-skeleton-cmomon" v-for="(item,index) in skeletonElements" :key="index" :style="{width: item.width+'px', height:item.height+'px', left: item.left+'px', top: item.top+'px',backgroundColor: skeletonBgColor,borderRadius:getRadius(item.skeletonType,borderRadius)}"></view>
		<view class="tui-loading" :class="[getLoadingType(loadingType)]" v-if="isLoading"></view>
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
	export default {
		name: "tuiSkeleton",
		props: {
			//选择器(外层容器)
			selector: {
				type: String,
				default: "tui-skeleton"
			},
			//外层容器背景颜色
			backgroundColor: {
				type: String,
				default: "#fff"
			},
			//骨架元素背景颜色
			skeletonBgColor: {
				type: String,
				default: "#e9e9e9"
			},
			//骨架元素类型：矩形，圆形，带圆角矩形["rect","circular","fillet"]
			//默认所有，根据页面情况进行传值
			//页面对应元素class为：tui-skeleton-rect，tui-skeleton-circular，tui-skeleton-fillet
			//如果传入的值不在下列数组中，则为自定义class值，默认按矩形渲染
			skeletonType: {
				type: Array,
				default () {
					return ["rect", "circular", "fillet"]
				}
			},
			//圆角值，skeletonType=fillet时生效
			borderRadius: {
				type: String,
				default: "16rpx"
			},
			//骨架屏预生成数据：提前生成好的数据，当传入该属性值时，则不会再次查找子节点信息
			preloadData: {
				type: Array,
				default () {
					return []
				}
			},
			//是否需要loading
			isLoading: {
				type: Boolean,
				default: false
			},
			//loading类型[1-10]
			loadingType: {
				type: Number,
				default: 1
			}
		},
		created() {
			const res = uni.getSystemInfoSync();
			this.winWidth = res.windowWidth;
			this.winHeight = res.windowHeight;
			//如果有预生成数据，则直接使用
			this.isPreload(true)
		},
		mounted() {
			this.$nextTick(() => {
				this.nodesRef(`.${this.selector}`).then((res) => {
					if(res && res[0]){
						this.winHeight = res[0].height + Math.abs(res[0].top)
					}
				});
				!this.isPreload() && this.selectorQuery()
			})

		},
		data() {
			return {
				winWidth: 375,
				winHeight: 800,
				skeletonElements: []
			};
		},
		methods: {
			getLoadingType: function(type) {
				let value = 1
				if (type && type > 0 && type < 11) {
					value = type
				}
				return 'tui-loading-' + value
			},
			getRadius: function(type, val) {
				let radius = "0"
				if (type == "circular") {
					radius = "50%"
				} else if (type == "fillet") {
					radius = val
				}
				return radius;
			},
			isPreload(init) {
				let preloadData = this.preloadData || []
				if (preloadData.length) {
					init && (this.skeletonElements = preloadData)
					return true
				}
				return false
			},
			async selectorQuery() {
				let skeletonType = this.skeletonType || []
				let nodes = []
				for (let item of skeletonType) {
					let className = '';
					// #ifndef MP-WEIXIN
					className = `.${item}`;
					if (~'rect_circular_fillet'.indexOf(item)) {
						className = `.${this.selector}-${item}`;
					}
					// #endif

					// #ifdef MP-WEIXIN
					className = `.${this.selector} >>> .${item}`;
					if (~'rect_circular_fillet'.indexOf(item)) {
						className = `.${this.selector} >>> .${this.selector}-${item}`;
					}
					// #endif
					await this.nodesRef(className).then((res) => {
						res.map(d => {
							d.skeletonType = item
						})
						nodes = nodes.concat(res)
					})
				}
				this.skeletonElements = nodes
			},
			async nodesRef(className) {
				return await new Promise((resolve, reject) => {
					uni.createSelectorQuery().selectAll(className).boundingClientRect((res) => {
						if (res) {
							resolve(res);
						} else {
							reject(res)
						}
					}).exec();
				})
			}
		}
	}
</script>

<style scoped>
	.tui-skeleton-cmomon {
		position: absolute;
		z-index: 99999;
	}

	.tui-skeleton-box {
		left: 0;
		top: 0;
	}

	.tui-loading {
		display: inline-block;
		vertical-align: middle;
		width: 40rpx;
		height: 40rpx;
		background: 0 0;
		border-radius: 50%;
		border: 2px solid;
		animation: tui-rotate 0.7s linear infinite;
		position: fixed;
		z-index: 999999;
		left: 50%;
		top: 50%;
		margin-left: -20rpx;
		margin-top: -20rpx;
	}

	.tui-loading-1 {
		border-color: #e5e5e5 #e5e5e5 #e5e5e5 #5677fc;
	}

	.tui-loading-2 {
		border-color: #e5e5e5 #e5e5e5 #e5e5e5 #8f8d8e;
	}

	.tui-loading-3 {
		border-color: rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.1) #fff;
	}

	.tui-loading-4 {
		border-color: #e5e5e5 #e5e5e5 #e5e5e5 #35b06a;
	}

	.tui-loading-5 {
		border-color: #e5e5e5 #e5e5e5 #e5e5e5 #fc872d;
	}

	.tui-loading-6 {
		border-color: #e5e5e5 #e5e5e5 #e5e5e5 #eb0909;
	}

	.tui-loading-7 {
		border-color: #5677fc transparent #5677fc transparent;
	}

	.tui-loading-8 {
		border-color: #35b06a transparent #35b06a transparent;
	}

	.tui-loading-9 {
		border-color: #fc872d transparent #fc872d transparent;
	}

	.tui-loading-10 {
		border-color: #eb0909 transparent #eb0909 transparent;
	}

	@-webkit-keyframes tui-rotate {
		0% {
			transform: rotate(0);
		}

		100% {
			transform: rotate(360deg);
		}
	}

	@keyframes tui-rotate {
		0% {
			transform: rotate(0);
		}

		100% {
			transform: rotate(360deg);
		}
	}
</style>
