<template>
	<div>
		<div class="storeBox" ref="container">
			<div class="storeBox-box" v-for="(item, index) in storeList" :key="index" @click.stop="checked(item)">
				<div class="store-img"><img class="store-image" :src="item.image" lazy-load="true" /></div>
				<div class="store-cent-left">
					<div class="store-name">{{ item.name }}</div>
					<div class="store-address line1">
						{{ item.address }}{{ ", " + item.detailedAddress }}
					</div>
				</div>
				<div class="row-right">
					<div>
						<!-- #ifdef H5 -->
						<a class="store-phone" :href="'tel:' + item.phone"><span
								class="iconfont icon-dadianhua01"></span></a>
						<!-- #endif -->
						<!-- #ifdef MP || APP-PLUS -->
						<view class="store-phone" @click="call(item.phone)"><text
								class="iconfont icon-dadianhua01"></text></view>
						<!-- #endif -->
					</div>
					<!-- <div>
						<a class="store-phone" :href="'tel:' + item.phone"><span class="iconfont icon-dadianhua01"></span></a>
					</div> -->
					<div class="store-distance" @click.stop="showMaoLocation(item)">
						<span class="addressTxt" v-if="item.distance">距离{{ item.distance/1000 }}千米</span>
						<span class="addressTxt" v-else>查看地图</span>
						<span class="iconfont icon-youjian"></span>
					</div>
				</div>
			</div>


			<Loading :loaded="loaded" :loading="loading"></Loading>
		</div>
		<div>
			<!-- <iframe v-if="locationShow && !isWeixin" ref="geoPage" width="0" height="0" frameborder="0" style="display:none;"
			 scrolling="no" :src="
          'https://apis.map.qq.com/tools/geolocation?key=' +
            mapKey +
            '&referer=myapp'
        ">
			</iframe> -->
		</div>
	</div>
</template>

<script setup>
	import { ref, getCurrentInstance, onMounted } from 'vue';
	import { onLoad, onReachBottom } from '@dcloudio/uni-app';
	import Loading from "@/components/Loading/index.vue";
import {
	storeListApi
} from "@/api/store.js";
import Cache from "@/utils/cache.js";
// import cookie from "@/utils/store/cookie";
	const LONGITUDE = "user_longitude";
	const LATITUDE = "user_latitude";
	const MAPKEY = "mapKey";
	const { proxy } = getCurrentInstance();

	const page = ref(1);
	const limit = ref(20);
	const loaded = ref(false);
	const loading = ref(false);
	const storeList = ref([]);
	const system_store = ref({});
	// mapKey: cookie.get(MAPKEY),
	const locationShow = ref(false);
	const user_latitude = ref(0);
	const user_longitude = ref(0);

	function call(phone) {
		uni.makePhoneCall({
			phoneNumber: phone,
		});
	}
	function selfLocation() {
		// #ifdef H5
		if (proxy.$wechat.isWeixin()) {
			proxy.$wechat.location().then(res => {
				user_latitude.value = res.latitude;
				user_longitude.value = res.longitude;
				uni.setStorageSync('user_latitude', res.latitude);
				uni.setStorageSync('user_longitude', res.longitude);
				getList();
			})
		} else {
		// #endif	
			uni.getLocation({
				type: 'wgs84',
				success: (res) => {
					try {
						user_latitude.value = res.latitude;
						user_longitude.value = res.longitude;
						uni.setStorageSync('user_latitude', res.latitude);
						uni.setStorageSync('user_longitude', res.longitude);
					} catch {}
					getList();
				},
				complete: function() {
					getList();
				}
			});
			// #ifdef H5	
		}
		// #endif
	}
	function showMaoLocation(e) {
		// #ifdef H5
		if (proxy.$wechat.isWeixin()) {
			proxy.$wechat.seeLocation({
				latitude: Number(e.latitude),
				longitude: Number(e.longitude)
			}).then(res => {
			})
		} else {
			// #endif
			uni.openLocation({
				latitude: Number(e.latitude),
				longitude: Number(e.longitude),
				name: e.name,
				address: `${e.address}-${e.detailedAddress}`,
				success: function() {
				}
			});
			// #ifdef H5	
		}
		// #endif
	}
	// 选中门店
	function checked(e) {

		uni.$emit("handClick", {
			address: e
		});
		uni.navigateBack();
		// if (this.goName === "orders") {
		//   this.$store.commit("GET_STORE", e);
		//   this.$router.go(-1); //返回上一层
		// }
	}
	// 获取门店列表数据
	function getList() {
		if (loading.value || loaded.value) return;
		loading.value = true;
		let data = {
			latitude: user_latitude.value || "", //纬度
			longitude: user_longitude.value || "", //经度
			page: page.value,
			limit: limit.value
		};
		storeListApi(data)
			.then(res => {
				loading.value = false;
				loaded.value = res.data.list.length < limit.value;
				storeList.value.push.apply(storeList.value, res.data.list);
				page.value = page.value + 1;
			})
			.catch(err => {
				proxy.$dialog.error(err);
			});
	}

	onLoad(() => {
		try {
			user_latitude.value = uni.getStorageSync('user_latitude');
			user_longitude.value = uni.getStorageSync('user_longitude');
		} catch (e) {
			// error
		}
	});

	onMounted(() => {
		if (user_latitude.value && user_longitude.value) {
			getList();
		} else {
			selfLocation();
			getList();
		}
		// this.$scroll(this.$refs.container, () => {
		//   !this.loading && this.getList();
		// });
	});

	onReachBottom(() => {
		getList();
	});
</script>

<style>
	.geoPage {
		position: fixed;
		width: 100%;
		height: 100%;
		top: 0;
		z-index: 10000;
	}

	.storeBox {
		width: 100%;
		background-color: #fff;
		padding: 0 30rpx;
	}

	.storeBox-box {
		width: 100%;
		height: auto;
		display: flex;
		align-items: center;
		padding: 23rpx 0;
		justify-content: space-between;
		border-bottom: 1px solid #eee;
	}

	.store-cent {
		display: flex;
		align-items: center;
		width: 80%;
	}

	.store-cent-left {
		width: 45%;
	}

	.store-img {
		width: 120rpx;
		height: 120rpx;
		border-radius: 6rpx;
		margin-right: 22rpx;
	}

	.store-image {
		width: 100%;
		height: 100%;
	}

	.store-name {
		color: #282828;
		font-size: 30rpx;
		margin-bottom: 22rpx;
		font-weight: 800;
	}

	.store-address {
		color: #666666;
		font-size: 24rpx;
	}

	.store-phone {
		width: 50rpx;
		height: 50rpx;
		color: #fff;
		border-radius: 50%;
		display: block;
		text-align: center;
		line-height: 48rpx;
		background-color: #e83323;
		margin-bottom: 22rpx;
		text-decoration: none;
	}

	.store-distance {
		font-size: 22rpx;
		color: #e83323;
	}

	.iconfont {
		font-size: 20rpx;
	}

	.row-right {
		display: flex;
		flex-direction: column;
		align-items: center;
		width: 33.5%;
	}
</style>
