<template>
	<web-view class="web-view" :webview-styles="webviewStyles" :src="url" :style="{width: windowW + 'px', height: windowH + 'px'}"></web-view>
</template>

<script>
	import {
		mapGetters
	} from "vuex";
	export default {
		//computed: mapGetters(['chatUrl']),
		data() {
			return {
				windowH: 0,
				windowW: 0,
				webviewStyles: {
					progress: {
						color: 'transparent'
					}
				},
				url: ''
			}
		},
		onLoad(option) {
			if(option.webUel) this.url = option.webUel;
			uni.setNavigationBarTitle({
				title: option.title
			})
			try {
				const res = uni.getSystemInfoSync();
				this.windowW = res.windowWidth;
				this.windowH = res.windowHeight;
			} catch (e) {
				// error
			}
		}
	}
</script>
