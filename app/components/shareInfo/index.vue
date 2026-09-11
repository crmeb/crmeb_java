<template>
	<view v-if="shareInfoStatus" class="poster-first">
	    <view class="mask-share">
			<image :src="urlDomain+'/crmebimage/perset/staticImg/share-info.png'" @click="shareInfoClose" @touchmove.stop.prevent="false"></image>
	    </view>
	  </view>
</template>

<script setup>
import { ref } from 'vue'
import Cache from '@/utils/cache.js'

const props = defineProps({
	shareInfoStatus: {
		type: Boolean,
		default: false,
	}
})

const emit = defineEmits(['setShareInfoStatus'])

const urlDomain = ref(Cache.get("imgHost"))

function shareInfoClose() {
	emit("setShareInfoStatus");
}
</script>

<style scoped lang="scss">
	.poster-first {
	  overscroll-behavior: contain;
	}
	.mask-share {
	  position: fixed;
	  top: 0;
	  left: 0;
	  right: 0;
	  bottom: 0;
	  z-index: 99;
	}
	.mask-share image {
	  width: 100%;
	  height:100%;
	}
</style>
