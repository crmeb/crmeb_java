<template>
	<view :data-theme="theme" :style="colorStyle">
		<view class='sign-record'>
		   <view class='list pad30' v-for="(item,index) in signList" :key="index">
		      <view class='item'>
		         <view class='data'>{{item.month}}</view>
		         <view class='listn borRadius14'>
		            <view class='itemn acea-row row-between-wrapper' v-for="(itemn,indexn) in item.list" :key="indexn">
		               <view>
		                  <view class='name line1'>{{itemn.title}}</view>
		                  <view>{{itemn.createDay}}</view>
		               </view>
		               <view class='num font_color'>+{{itemn.number}}</view>
		            </view>
		         </view>
		      </view>
		   </view>
		    <view class='loadingicon acea-row row-center-wrapper'>
		        <text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>
						<text v-if="count!=0">{{loadtitle}}</text>
		    </view>
				<view class="flex-column-center" v-if="count==0">
					<img class="no-sign-img" :src="urlDomain+'/crmebimage/perset/staticImg/noSign.png'" alt="" />
					<text class="no-sign-text">暂无签到记录~</text>
				</view>
		</view>
	</view>
</template>

<script setup>
	import { ref, watch, getCurrentInstance } from 'vue';
	import { onLoad, onReachBottom } from '@dcloudio/uni-app';
	import { getSignMonthList } from '@/api/user.js';
	import { toLogin } from '@/libs/login.js';
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from 'pinia';
import { useColor } from '@/composables/useColor.js';
	const { proxy } = getCurrentInstance();
	const appStore = useAppStore();
	const { isLogin } = storeToRefs(appStore);
	let app = getApp();

	const loading = ref(false);
	const loadend = ref(false);
	const loadtitle = ref('加载更多');
	const page = ref(1);
	const limit = ref(8);
	const signList = ref([]);
	const { colorStyle } = useColor();
	const theme = ref(app.globalData.theme);
	const urlDomain = ref(proxy.$Cache.get("imgHost"));
	const count = ref(0); // 签到记录总数

	watch(isLogin, (newV, oldV) => {
		if (newV) {
			getSignMoneList();
		}
	}, { deep: true });

	onLoad(() => {
		if (isLogin.value) {
			getSignMoneList();
		} else {
			toLogin();
		}
	});

	onReachBottom(() => {
		getSignMoneList();
	});

	/**
	 * 获取签到记录列表
	 */
	function getSignMoneList() {
		if (loading.value) return;
		if (loadend.value) return;
		loading.value = true;
		loadtitle.value = "";
		getSignMonthList({ page: page.value, limit: limit.value }).then(res => {
			let list = res.data.list;
			let loadendVal = list.length < limit.value;
			count.value = res.data.total
			signList.value = proxy.$util.SplitArray(list, signList.value);
			signList.value = signList.value;
			loadend.value = loadendVal;
			loading.value = false;
			loadtitle.value = loadendVal ? "我也是有底线的~" : "加载更多"
		}).catch(err => {
			loading.value = false;
			loadtitle.value = '加载更多';
		});
	}
</script>

<style lang="scss">
	.sign-record {
		.no-sign-img {
			width: 100%;
			// height: 360rpx;
		}
		.no-sign-text {
			color: #ccc;
		}
	}
	.font_color{
		@include main_color(theme);
	}
	.flex-column-center {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}
</style>
