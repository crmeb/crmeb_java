<template>
	<web-view class="web-view" :webview-styles="webviewStyles" :src="url" :style="{width: windowW + 'px', height: windowH + 'px'}"></web-view>
</template>

<script setup>
	import { ref } from "vue";
	import { onLoad } from "@dcloudio/uni-app";

	const windowH = ref(0);
	const windowW = ref(0);
	const webviewStyles = ref({ progress: { color: 'transparent' } });
	const url = ref('');

	onLoad((option) => {
		if (option.webUel) url.value = option.webUel;
		if (option.tntInstId) url.value += `?tntInstId=${option.tntInstId}`;
		if (option.scene) url.value += `&scene=${option.scene}`;
		uni.setNavigationBarTitle({ title: option.title });
		try {
			const res = uni.getSystemInfoSync();
			windowW.value = res.windowWidth;
			windowH.value = res.windowHeight;
		} catch (e) {}
	});
</script>
