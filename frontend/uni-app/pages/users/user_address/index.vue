<template>
	<view :data-theme="theme" :style="colorStyle">
		<form @submit="formSubmit" report-submit='true'>
			<view class='addAddress pad30'>
				<view class='list borRadius14'>
					<view class='item acea-row row-between-wrapper' style="border: none;">
						<view class='name'>姓名</view>
						<input type='text' placeholder='请输入姓名' placeholder-style="color:#ccc;" name='realName'
							:value="userAddress.realName" placeholder-class='placeholder' maxlength="20"></input>
					</view>
					<view class='item acea-row row-between-wrapper'>
						<view class='name'>联系电话</view>
						<input type='number' placeholder='请输入联系电话' placeholder-style="color:#ccc;" name="phone"
							:value='userAddress.phone' placeholder-class='placeholder' maxlength="11"></input>
					</view>
					<view class='item acea-row row-between-wrapper relative'>
						<view class='name'>所在地区</view>
						<view class="address">
							<picker mode="multiSelector" @change="bindRegionChange"
								@columnchange="bindMultiPickerColumnChange" :value="valueRegion" :range="multiArray">
								<view class='acea-row'>
									<view class="picker line1">{{region[0]}}，{{region[1]}}，{{region[2]}}</view>
									<view class='iconfont icon-xiangyou abs_right'></view>
								</view>
							</picker>
						</view>
					</view>
					<view class='item acea-row row-between-wrapper relative'>
						<view class='name'>详细地址</view>
						<input type='text' placeholder='请填写具体地址' placeholder-style="color:#ccc;" name='detail'
							placeholder-class='placeholder' v-model='userAddress.detail' maxlength="100"></input>
						<view class='iconfont icon-dizhi font_color abs_right' @tap="chooseLocation"></view>
					</view>
				</view>
				<view class='default acea-row row-middle borRadius14'>
					<checkbox-group @change='ChangeIsDefault'>
						<!-- #ifndef MP -->
						<checkbox
							:checked="userAddress.isDefault"
							color="#ffffff"
							backgroundColor="#ffffff"
							activeBackgroundColor="var(--view-theme, #E93323)"
							activeBorderColor="var(--view-theme, #E93323)"
							iconColor="#ffffff"
						/>
						<!-- #endif -->
						<!-- #ifdef MP -->
						<checkbox :checked="userAddress.isDefault" />
						<!-- #endif -->
						设置为默认地址
					</checkbox-group>
				</view>

				<button class='keepBnt bg_color' form-type="submit">立即保存</button>
				<!-- #ifdef MP -->
				<view class="wechatAddress" v-if="!id" @click="getWxAddress">导入微信地址</view>
				<!-- #endif -->
				<!-- #ifdef H5 -->
				<view class="wechatAddress" v-if="isWeixin && !id" @click="getAddress">导入微信地址</view>
				<!-- #endif -->
			</view>
		</form>
		<view v-show="showLoading" class="bg-fixed"></view>
	</view>
</template>

<script setup>
	import { ref, watch, getCurrentInstance } from 'vue';
	import { onLoad } from '@dcloudio/uni-app';
	import {
		editAddress,
		getAddressDetail
	} from '@/api/user.js';
	import {
		getCityList
	} from "@/utils";
	import {
		getCity
	} from '@/api/api.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from 'pinia';
	import {
		Debounce
	} from '@/utils/validate.js'
	import atModel from '@/pages/users/components/accredit/index.vue'
import { useColor } from '@/composables/useColor.js';
	// import atModel from '@/components/accredit/index.vue'
	let app = getApp();

	const { proxy } = getCurrentInstance();
	const appStore = useAppStore();
	const { isLogin } = storeToRefs(appStore);

	// #ifdef H5
	const isWeixin = proxy.$wechat.isWeixin();
	// #endif
	// #ifndef H5
	const isWeixin = false;
	// #endif

	// data
	const cartId = ref(''); //购物车id
	const pinkId = ref(0); //拼团id
	const couponId = ref(0); //优惠券id
	const id = ref(0); //地址id
	const userAddress = ref({
		isDefault: false
	}); //地址详情
	const region = ref(['省', '市', '区']);
	const valueRegion = ref([0, 0, 0]);
	const district = ref([]);
	const multiArray = ref([]);
	const multiIndex = ref([0, 0, 0]);
	const cityId = ref(0);
	const bargain = ref(false); //是否是砍价
	const combination = ref(false); //是否是拼团
	const secKill = ref(false); //是否是秒杀
	const theme = ref(app.globalData.theme);
	const { colorStyle } = useColor();
	const showLoading = ref(false);
	const preOrderNo = ref(0);
	const city_id = ref(0);

	watch(isLogin, (newV, oldV) => {
		if (newV) {
			getUserAddress();
		}
	}, { deep: true });

	onLoad((options) => {
		if (proxy.$Cache.getItem('cityList')) {
			//检测城市数据缓存是否过期，有的话从缓存取，没有的话请求接口
			district.value = proxy.$Cache.getItem('cityList');
			initialize();
		} else {
			showLoading.value = true;
			uni.showLoading({
				title: '数据加载中...'
			});
			getCityList().then(res=>{
				district.value = res
				initialize();
				uni.hideLoading();
				showLoading.value = false;
			})
		}
		if (isLogin.value) {
			preOrderNo.value = options.preOrderNo || 0;
			id.value = options.id || 0;
			uni.setNavigationBarTitle({
				title: options.id ? '修改地址' : '添加地址'
			})
			getUserAddress();
		} else {
			toLogin();
		}
	});

	// #ifdef APP-PLUS
	// 获取选择的地区
	function handleGetRegion(regionVal) {
		region.value = regionVal
	}
	// #endif
	function getUserAddress() {
		if (!id.value) return false;
		getAddressDetail(id.value).then(res => {
			if(res.data){
				let regionVal = [res.data.province, res.data.city, res.data.district];
				userAddress.value = res.data;
				region.value = regionVal;
				city_id.value = res.data.cityId;
			}
		});
	}
	function initialize() {
		let province = [],city = [],area = [];
		if (district.value.length) {
			let cityChildren = district.value[0].child || [];
			let areaChildren = cityChildren.length ? (cityChildren[0].child || []) : [];
			district.value.forEach(function(item,i) {
				province.push(item.name);
				if (item.name === region.value[0]) {
					valueRegion.value[0] = i
					multiIndex.value[0] = i
				}
			});
			district.value[valueRegion.value[0]].child.forEach((item,i)=>{
				city.push(item.name);
				if (region.value[1] == item.name) {
					valueRegion.value[1] = i
					multiIndex.value[1] = i
				}
			})
			district.value[valueRegion.value[0]].child[valueRegion.value[1]].child.forEach((item,i)=>{
				area.push(item.name);
				if (region.value[2] == item.name) {
					valueRegion.value[2] = i
					multiIndex.value[2] = i
				}
			})
			multiArray.value = [province, city, area]
		}
	}
	function bindRegionChange(e) {
		let multiIndexVal = multiIndex.value,
			province = district.value[multiIndexVal[0]] || {
				child: []
			},
			city = province.child[multiIndexVal[1]] || {
				child: []
			},
			area = city.child[multiIndexVal[2]] || {
				cityId: 0
			},
			multiArrayVal = multiArray.value,
			value = e.detail.value;
		region.value = [multiArrayVal[0][value[0]], multiArrayVal[1][value[1]], multiArrayVal[2][value[2]]]
		cityId.value = area.cityId;
		valueRegion.value = [0, 0, 0]
		initialize();
	}
	function bindMultiPickerColumnChange(e) {
		let column = e.detail.column,
			value = e.detail.value,
			currentCity = district.value[value] || {
				child: []
			},
			multiArrayVal = multiArray.value,
			multiIndexVal = multiIndex.value;
		multiIndexVal[column] = value;
		switch (column) {
			case 0:
				let areaList = currentCity.child[0] || {
					child: []
				};
				multiArrayVal[1] = currentCity.child.map((item) => {
					return item.name;
				});
				multiArrayVal[2] = areaList.child.map((item) => {
					return item.name;
				});
				break;
			case 1:
				let cityList = district.value[multiIndexVal[0]].child[multiIndexVal[1]].child || [];
				multiArrayVal[2] = cityList.map((item) => {
					return item.name;
				});
				break;
			case 2:

				break;
		}
		// #ifdef MP || APP-PLUS
		multiArray.value[0] = multiArrayVal[0];
		multiArray.value[1] = multiArrayVal[1];
		multiArray.value[2] = multiArrayVal[2];
		// #endif
		// #ifdef H5
		multiArray.value = multiArrayVal;
		// #endif
		multiIndex.value = multiIndexVal
		// this.setData({ multiArray: multiArray, multiIndex: multiIndex});
	}
	function toggleTab(str) {
		proxy.$refs[str].show();
	}
	function onConfirm(val) {
		region.value = val.checkArr[0] + '-' + val.checkArr[1] + '-' + val.checkArr[2];
	}
	//选择地位地址
	function chooseLocation() {
		proxy.$util.$L.getLocation().then(res=>{
			uni.chooseLocation({
				latitude: uni.getStorageSync('user_latitude'),
				longitude: uni.getStorageSync('user_longitude'),
				success: (res) => {
					userAddress.value.detail = res.name;
				}
			})
		})
	}
	// 导入共享地址（小程序）
	function getWxAddress() {
		uni.authorize({
			scope: 'scope.address',
			success: function(res) {
				uni.chooseAddress({
					success: function(res) {
						let addressP = {};
						addressP.province = res.provinceName;
						addressP.city = res.cityName;
						addressP.district = res.countyName;
						addressP.cityId = 0;
						editAddress({
							address: addressP,
							isDefault: 1,
							realName: res.userName,
							postCode: res.postalCode,
							phone: res.telNumber,
							detail: res.detailInfo,
							id: 0
						}).then(res => {
							setTimeout(function() {
								if (cartId.value) {
									let cartIdVal = cartId.value;
									let pinkIdVal = pinkId.value;
									let couponIdVal = couponId.value;
									cartId.value = '';
									pinkId.value = '';
									couponId.value = '';
									uni.navigateTo({
										url: '/pages/order/order_confirm/index?cartId=' +
											cartIdVal +
											'&addressId=' + (
												id.value ? id
												.value :
												res.data
												.id) +
											'&pinkId=' +
											pinkIdVal +
											'&couponId=' +
											couponIdVal +
											'&secKill=' + secKill
											.value +
											'&combination=' +
											combination.value +
											'&bargain=' + bargain
											.value
									});
								} else {
									uni.navigateBack({
										delta: 1
									});
								}
							}, 1000);
							return proxy.$util.Tips({
								title: "添加成功",
								icon: 'success'
							});
						}).catch(err => {
							return proxy.$util.Tips({
								title: err
							});
						});
					},
					fail: function(res) {
						if (res.errMsg == 'chooseAddress:cancel') return proxy.$util
							.Tips({
								title: '取消选择'
							});
					},
				})
			},
			fail: function(res) {
				uni.showModal({
					title: '您已拒绝导入微信地址权限',
					content: '是否进入权限管理，调整授权？',
					success(res) {
						if (res.confirm) {
							uni.openSetting({
								success: function(res) {}
							});
						} else if (res.cancel) {
							return proxy.$util.Tips({
								title: '已取消！'
							});
						}
					}
				})
			},
		})
	}
	// 导入共享地址（微信）；
	// #ifdef H5
	function getAddress() {
		proxy.$wechat.openAddress().then(userInfo => {
			// open();
			editAddress({
					id: id.value,
					realName: userInfo.userName,
					phone: userInfo.telNumber,
					address: {
						province: userInfo.provinceName,
						city: userInfo.cityName,
						district: userInfo.countryName,
						cityId: 0
					},
					detail: userInfo.detailInfo,
					isDefault: 1,
					postCode: userInfo.postalCode
				})
				.then(() => {
					setTimeout(function() {
						if (cartId.value) {
							let cartIdVal = cartId.value;
							let pinkIdVal = pinkId.value;
							let couponIdVal = couponId.value;
							cartId.value = '';
							pinkId.value = '';
							couponId.value = '';
							uni.navigateTo({
								url: '/pages/order/order_confirm/index?cartId=' +
									cartIdVal + '&addressId=' + (id.value ? id.value :
										res.data
										.id) + '&pinkId=' + pinkIdVal + '&couponId=' +
									couponIdVal + '&secKill=' + secKill.value +
									'&combination=' + combination.value + '&bargain=' +
									bargain.value
							});
						} else {
							uni.navigateTo({
								url: '/pages/users/user_address_list/index'
							})
							// history.back();
						}
					}, 1000);
					// close();
					proxy.$util.Tips({
						title: "添加成功",
						icon: 'success'
					});
				})
				.catch(err => {
					// close();
					return proxy.$util.Tips({
						title: err || "添加失败"
					});
				});
		}).catch(err => {
		});
	}
	// #endif
	/**
	 * 提交用户添加地址
	 * 
	 */
	const formSubmit = Debounce(function(e) {
		let value = e.detail.value;
		if (!value.realName) return proxy.$util.Tips({
			title: '请填写收货人姓名'
		});
		if (!value.phone) return proxy.$util.Tips({
			title: '请填写联系电话'
		});
		if (!/^1(3|4|5|7|8|9|6)\d{9}$/i.test(value.phone)) return proxy.$util.Tips({
			title: '请输入正确的手机号码'
		});
		if (region.value == '省-市-区') return proxy.$util.Tips({
			title: '请选择所在地区'
		});
		if (!value.detail) return proxy.$util.Tips({
			title: '请填写详细地址'
		});
		value.id = id.value;
		let regionArray = region.value;
		value.address = {
			province: regionArray[0],
			city: regionArray[1],
			district: regionArray[2],
			cityId: cityId.value,
		};
		value.isDefault = userAddress.value.isDefault;

		uni.showLoading({
			title: '保存中',
			mask: true
		})
		editAddress(value).then(res => {
			if (id.value)
				proxy.$util.Tips({
					title: '修改成功',
					icon: 'success'
				});
			else
				proxy.$util.Tips({
					title: '添加成功',
					icon: 'success'
				});
			setTimeout(function() {
				if (preOrderNo.value > 0) {
					uni.redirectTo({
						url: '/pages/order/order_confirm/index?preOrderNo=' + preOrderNo
							.value + '&addressId=' + (id.value ? id.value : res
								.data.id)
					})
				} else {
					// #ifdef H5
					return history.back();
					// #endif
					// #ifndef H5
					return uni.navigateBack({
						delta: 1,
					})
					// #endif
				}
			}, 1000);
		}).catch(err => {
			return proxy.$util.Tips({
				title: err
			});
		})
	})
	function ChangeIsDefault(e) {
		userAddress.value.isDefault = !userAddress.value.isDefault;
	}
</script>

<style scoped lang="scss">
	.bg-fixed{
		width: 100%;
		height: 750rpx;
		position: absolute;
		top: 0;
	}
	.addAddress {
		padding-top: 20rpx;
	}

	.bg_color {
		@include main_bg_color(theme);
	}

	.addAddress .list {
		background-color: #fff;
		padding: 0 24rpx;
	}

	.addAddress .list .item {
		border-top: 1rpx solid #eee;
		height: 90rpx;
		line-height: 90rpx;
	}

	.addAddress .list .item .name {
		// width: 195rpx;
		font-size: 30rpx;
		color: #333;
	}

	.addAddress .list .item .address {
		flex: 1;
		margin-left: 50rpx;
	}

	.addAddress .list .item input {
		width: 475rpx;
		font-size: 30rpx;
		font-weight: 400;
	}

	.addAddress .list .item .placeholder {
		color: #ccc;
	}

	.addAddress .list .item picker .picker {
		width: 410rpx;
		font-size: 30rpx;
	}

	.addAddress .default {
		padding: 0 30rpx;
		height: 90rpx;
		background-color: #fff;
		margin-top: 23rpx;
	}

	.addAddress .default checkbox {
		margin-right: 15rpx;
	}

	.addAddress .keepBnt {
		width: 690rpx;
		height: 86rpx;
		border-radius: 50rpx;
		text-align: center;
		line-height: 86rpx;
		margin: 80rpx auto 24rpx auto;
		font-size: 32rpx;
		color: #fff;
	}

	.addAddress .wechatAddress {
		width: 690rpx;
		height: 86rpx;
		border-radius: 50rpx;
		text-align: center;
		line-height: 86rpx;
		margin: 0 auto;
		font-size: 32rpx;
		// color: #E93323 ;
		@include main_color(theme);
		@include coupons_border_color(theme);
	}

	.font_color {
		@include main_color(theme);
	}

	.relative {
		position: relative;
	}

	.icon-dizhi {
		font-size: 44rpx;
		z-index: 100;
	}

	.abs_right {
		position: absolute;
		right: 0;
	}

	::v-deep  uni-checkbox .uni-checkbox-input.uni-checkbox-input-checked,
	::v-deep  checkbox .uni-checkbox-input.uni-checkbox-input-checked {
		@include main_bg_color(theme);
		@include coupons_border_color(theme);
		color: #fff !important
	}

	::v-deep  checkbox .wx-checkbox-input.wx-checkbox-input-checked {
		@include main_bg_color(theme);
		@include coupons_border_color(theme);
		color: #fff !important;
		margin-right: 0 !important;
	}
</style>
