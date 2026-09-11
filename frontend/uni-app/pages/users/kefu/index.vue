<template>
	<web-view class="web-view" :webview-styles="webviewStyles" :src="url" :style="{width: windowW + 'px', height: windowH + 'px'}"></web-view>
</template>

<script setup>
	import { ref } from "vue";
	import { onLoad } from "@dcloudio/uni-app";
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from "pinia";

	const appStore = useAppStore();
	const { chatUrl } = storeToRefs(appStore);

	const windowH = ref(0);
	const windowW = ref(0);
	const webviewStyles = ref({ progress: { color: 'transparent' } });
	const url = ref('');

	onLoad((option) => {
		url.value = chatUrl.value;
		try {
			const res = uni.getSystemInfoSync();
			windowW.value = res.windowWidth;
			windowH.value = res.windowHeight;
		} catch (e) {}
	});
</script>
