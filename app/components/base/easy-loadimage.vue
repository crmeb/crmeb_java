<template>
	<view class="easy-loadimage" :style="[boxStyle]" :id="uid">
		<image class="origin-img" :style="[imageRadius]" :src="imageSrc" mode="scaleToFill" v-if="loadImg&&!isLoadError"
			v-show="showImg" :class="{'no-transition':!openTransition,'show-transition':showTransition&&openTransition}"
			@load="handleImgLoad" @error="handleImgError">
		</image>
		<view class="loadfail-img" v-else-if="isLoadError"
			:style="{'background-image': `url(${urlDomain}/crmebimage/presets/loadfail.png) no-repeat center`}"></view>
		<view :class="['loading-img',loadingMode]" v-show="!showImg&&!isLoadError"></view>
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
	import { ref, computed, watch, nextTick, onMounted, onBeforeUnmount, getCurrentInstance } from 'vue';
	import {
		throttle
	} from '@/utils/validate.js'

	const { proxy } = getCurrentInstance();

	// 生成全局唯一id
	function generateUUID() {
		return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
			let r = Math.random() * 16 | 0,
				v = c == 'x' ? r : (r & 0x3 | 0x8);
			return v.toString(16);
		})
	}

	const props = defineProps({
		imageSrc: {
			type: String || null,
			default () {
				return '';
			}
		},
		mode: {
			type: String,
		},
		loadingMode: {
			type: String,
			default: 'looming-gray'
		},
		openTransition: {
			type: Boolean,
			default: true,
		},
		viewHeight: {
			type: Number,
			default () {
				return uni.getSystemInfoSync().windowHeight;
			}
		},
		width: {
			type: String,
			default: ''
		},
		height: {
			type: String,
			default: ''
		},
		borderRadius: {
			type: String,
			default: ''
		},
		radius: {
			type: Number,
			default: 0
		},
	});

	const urlDomain = ref(proxy.$Cache.get("imgHost"));
	const uid = ref('uid-' + generateUUID());
	const loadImg = ref(false);
	const showImg = ref(false);
	const isLoadError = ref(false);
	const showTransition = ref(false);
	let observer = null;
	const scrollFn = throttle(function() {
		// 加载img时才执行滚动监听判断是否可加载
		if (loadImg.value || isLoadError.value) return;
		const id = uid.value
		const query = uni.createSelectorQuery().in(proxy);
		query.select('#' + id).boundingClientRect(data => {
			if (!data) return;
			if (data.top - props.viewHeight < 0) {
				loadImg.value = !!props.imageSrc;
				isLoadError.value = !loadImg.value;
			}
		}).exec()
	}, 200);

	// IntersectionObserver 自行观察视口，不依赖页面 emit scroll 事件
	function observeViewport() {
		if (loadImg.value || isLoadError.value) return;
		// #ifdef H5 || APP-PLUS || MP
		try {
			observer = uni.createIntersectionObserver(proxy, {
				thresholds: [0, 0.01, 0.1]
			});
			observer.relativeToViewport();
			observer.observe('#' + uid.value, (res) => {
				if (loadImg.value) return;
				// 进入或已在视口内即加载（intersectionRatio > 0 表示有相交）
				if (res.intersectionRatio > 0 || res.isIntersecting) {
					loadImg.value = !!props.imageSrc;
					isLoadError.value = !loadImg.value;
					disconnectObserver();
				}
			});
		} catch (e) {
			// 降级：observer 不可用时退回 scroll 监听
		}
		// #endif
	}
	function disconnectObserver() {
		if (observer) {
			try { observer.disconnect(); } catch (e) {}
			observer = null;
		}
	}

	const boxStyle = computed(() => {
		return {
			width: props.width,
			height: props.height,
			borderRadius: props.borderRadius || props.radius * 2 + 'rpx'
		}
	});
	const imageRadius = computed(() => {
		if (props.borderRadius) {
			return {
				'border-radius': props.borderRadius
			}
		}
		if (props.radius && props.radius > 0) {
			return {
				'border-radius': props.radius * 2 + 'rpx'
			}
		}
	});

	function init() {
		nextTick(() => {
			onScroll()
			observeViewport()
		})
	}
	function handleImgLoad(e) {
		showImg.value = true;
		setTimeout(() => {
			showTransition.value = true
		}, 50)
	}
	function handleImgError(e) {
		isLoadError.value = true;
	}
	function onScroll() {
		scrollFn();
	}

	onMounted(() => {
		init()
		uni.$on('scroll', scrollFn);
		onScroll()
	});

	// 图片地址异步到达时，重置错误状态并重新观察视口
	watch(() => props.imageSrc, (val) => {
		if (val && !loadImg.value) {
			isLoadError.value = false;
			nextTick(() => {
				observeViewport()
				onScroll()
			})
		}
	});

	onBeforeUnmount(() => {
		uni.$off('scroll', scrollFn);
		disconnectObserver()
	});
</script>

<style scoped lang="scss">
	.easy-loadimage {
		width: 100%;
		height: 100%;
		position: relative;
		overflow: hidden;
	}

	/* 官方优化图片tips */
	image {
		will-change: transform;
		overflow: hidden;
		object-fit: cover;
	}

	/* 渐变过渡效果处理 */
	image.origin-img {
		width: 100%;
		height: 100%;
		opacity: 0.3;
		/* max-height: 360rpx; */
		/* border-radius: 14rpx;
		overflow: hidden; */
		/* min-height: 360rpx; */
	}

	image.origin-img.show-transition {
		transition: opacity .5s;
		opacity: 1;
	}

	image.origin-img.no-transition {
		opacity: 1;
	}

	/* 加载失败、加载中的占位图样式控制 */
	.loadfail-img {
		height: 100%;
		background-size: 50%;
	}

	.loading-img {
		height: 100%;
	}

	/* 动态灰色若隐若现 */
	.looming-gray {
		animation: looming-gray 1s infinite linear;
		background-color: #e3e3e3;
	}

	@keyframes looming-gray {
		0% {
			background-color: #e3e3e3aa;
		}

		50% {
			background-color: #e3e3e3;
		}

		100% {
			background-color: #e3e3e3aa;
		}
	}

	/* 骨架屏1 */
	.skeleton-1 {
		background-color: #e3e3e3;
		background-image: linear-gradient(100deg, rgba(255, 255, 255, 0), rgba(255, 255, 255, 0.2) 50%, rgba(255, 255, 255, 0) 80%);
		background-size: 100rpx 100%;
		background-repeat: repeat-y;
		background-position: 0 0;
		animation: skeleton-1 .6s infinite;
	}

	@keyframes skeleton-1 {
		to {
			background-position: 200% 0;
		}
	}

	/* 骨架屏2 */
	.skeleton-2 {
		background-image: linear-gradient(-90deg, #fefefe 0%, #e6e6e6 50%, #fefefe 100%);
		background-size: 400% 400%;
		background-position: 0 0;
		animation: skeleton-2 1.2s ease-in-out infinite;
	}

	@keyframes skeleton-2 {
		to {
			background-position: -135% 0;
		}
	}
</style>
