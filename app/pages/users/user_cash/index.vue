<template>
	<view :data-theme="theme" :style="colorStyle">
		<view class='cash-withdrawal'>
			<view class='nav acea-row'>
				<view v-for="(item,index) in navList" :key="index" class='item font-color' @click="swichNav(index)">
					<view class='line bg_color' :class='currentTab==index ? "on":""'></view>
					<view class='iconfont' :class='item.icon+" "+(currentTab==index ? "on":"")'></view>
					<view class="tab_text">{{item.name}}</view>
				</view>
			</view>
			<view class='wrapper'>
				<view :hidden='currentTab != 0' class='list'>
					<form @submit="subCash" report-submit='true'>
						<view class='item acea-row row-between-wrapper'>
							<view class='name'>持卡人</view>
							<view class='input'>
								<input placeholder='请输入持卡人姓名' placeholder-class='placeholder' name="name" maxlength="20"></input>
							</view>
						</view>
						<view class='item acea-row row-between-wrapper'>
							<view class='name'>卡号</view>
							<view class='input'>
								<input type='number' placeholder='请填写卡号' placeholder-class='placeholder' name="cardum"
									maxlength="19"></input>
							</view>
						</view>
						<view class='item acea-row row-between-wrapper'>
							<view class='name'>银行</view>
							<view class='input'>
								<picker @change="bindPickerChange" :value="index" :range="array">
									<text class='Bank'>{{array[index]}}</text>
									<text class='iconfont icon-qiepian38'></text>
								</picker>
							</view>
						</view>
						<view class='item acea-row row-between-wrapper'>
							<view class='name'>提现</view>
							<view class='input'><input :placeholder='cashPlaceholder' placeholder-class='placeholder' name="money"
									type='digit'></input></view>
						</view>
						<view class='tip'>
							当前可提现金额: <text class="price">￥{{commission.commissionCount}},</text>冻结佣金：￥{{commission.brokenCommission}}
						</view>
						<view class='tip'>
							说明: 每笔佣金的冻结期为{{commission.brokenDay}}天，到期后可提现
						</view>
						<button formType="submit" class='bnt bg-color'>提现</button>
					</form>
				</view>
				<view :hidden='currentTab != 1' class='list'>
					<form @submit="subCash" report-submit='true'>
						<view class='item acea-row row-between-wrapper'>
							<view class='name'>账号</view>
							<view class='input'>
								<input placeholder='请填写您的微信账号' placeholder-class='placeholder' name="name" maxlength="20"></input>
							</view>
						</view>
						<view class='item acea-row row-between-wrapper'>
							<view class='name'>提现</view>
							<view class='input'>
								<input :placeholder='cashPlaceholder' placeholder-class='placeholder' name="money" type='digit'
									maxlength="5"></input>
							</view>
						</view>
						<view class='item acea-row row-top row-between'>
							<view class='name'>收款码</view>
							<view class="input acea-row">
								<view class="picEwm" v-if="qrcodeUrlW">
									<image :src="qrcodeUrlW"></image>
									<text class='iconfont icon-guanbi1 font-color' @click='DelPicW'></text>
								</view>
								<view class='pictrue acea-row row-center-wrapper row-column' @click='uploadpic("W")' v-else>
									<text class='iconfont icon-icon25201'></text>
									<view>上传图片</view>
								</view>
							</view>
						</view>
						<view class='tip'>
							当前可提现金额: <text class="price">￥{{commission.commissionCount}},</text>冻结佣金：￥{{commission.brokenCommission}}
						</view>
						<view class='tip'>
							说明: 每笔佣金的冻结期为{{commission.brokenDay}}天，到期后可提现
						</view>
						<button formType="submit" class='bnt bg-color'>提现</button>
					</form>
				</view>
				<view :hidden='currentTab != 2' class='list'>
					<form @submit="subCash" report-submit='true'>
						<view class='item acea-row row-between-wrapper'>
							<view class='name'>账号</view>
							<view class='input'>
								<input placeholder='请填写您的支付宝账号' placeholder-class='placeholder' name="name" maxlength="20"></input>
							</view>
						</view>
						<view class='item acea-row row-between-wrapper'>
							<view class='name'>提现</view>
							<view class='input'>
								<input :placeholder='cashPlaceholder' placeholder-class='placeholder' name="money" type='digit'
									maxlength="5"></input>
							</view>
						</view>
						<view class='item acea-row row-top row-between'>
							<view class='name'>收款码</view>
							<view class="input acea-row">
								<view class="picEwm" v-if="qrcodeUrlZ">
									<image :src="qrcodeUrlZ"></image>
									<text class='iconfont icon-guanbi1 font-color' @click='DelPicZ'></text>
								</view>
								<view class='pictrue acea-row row-center-wrapper row-column' @click='uploadpic("Z")' v-else>
									<text class='iconfont icon-icon25201'></text>
									<view>上传图片</view>
								</view>
							</view>
						</view>
						<view class='tip'>
							当前可提现金额: <text class="price">￥{{commission.commissionCount}},</text>冻结佣金：￥{{commission.brokenCommission}}
						</view>
						<view class='tip'>
							说明: 每笔佣金的冻结期为{{commission.brokenDay}}天，到期后可提现
						</view>
						<button formType="submit" class='bnt'>提现</button>
					</form>
				</view>
				<view :hidden='currentTab != 3' class='list'>
					<form @submit="subCash" report-submit='true'>
						<view class='item acea-row row-between-wrapper'>
							<view class='name'>当前余额</view>
							<view class='input'>
								{{userInfo.nowMoney}}
							</view>
						</view>
						<view class='item acea-row row-between-wrapper'>
							<view class='name'>提现</view>
							<view class='input'>
								<input :placeholder='cashPlaceholder' placeholder-class='placeholder' name="money" type='digit'>
								</input>
							</view>
						</view>
						<view class='tip'>
							当前可提现金额: <text class="price">￥{{commission.commissionCount}},</text>冻结佣金：￥{{commission.brokenCommission}}
						</view>
						<view class='tip'>
							说明: 每笔佣金的冻结期为{{commission.brokenDay}}天，到期后可提现
						</view>
						<button formType="submit" class='bnt bg-color'>提现</button>
					</form>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { ref, computed, nextTick, watch, getCurrentInstance } from 'vue';
	import { onLoad } from '@dcloudio/uni-app';
	import {
		extractCash,
		extractBank,
		extractUser,
		transferIn
	} from '@/api/user.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from 'pinia';
	import {
		Debounce
	} from '@/utils/validate.js'
import { useColor } from '@/composables/useColor.js';
	let app = getApp();

	const { proxy } = getCurrentInstance();
	const appStore = useAppStore();
	const { isLogin, userInfo } = storeToRefs(appStore);

	// data
	const navList = ref([{
			'name': '银行卡',
			'icon': 'icon-yinhangqia'
		},
		{
			'name': '微信',
			'icon': 'icon-weixin2'
		},
		{
			'name': '支付宝',
			'icon': 'icon-icon34'
		},
		{
			'name': '余额',
			'icon': 'icon-yuezhifu'
		}
	]);
	const currentTab = ref(0);
	const index = ref(0);
	const array = ref([]); //提现银行
	const minPrice = ref(0.00); //最低提现金额
	const cashPlaceholder = computed(() => Number(minPrice.value) > 0
		? `最低提现金额${minPrice.value}`
		: '请输入提现金额');
	// const userInfo = ref([]);
	const isClone = ref(false);
	const commission = ref({});
	const qrcodeUrlW = ref("");
	const qrcodeUrlZ = ref("");
	const isCommitted = ref(false); //防止多次提交
	const { colorStyle } = useColor();
	const theme = ref(app.globalData.theme);
	const money = ref('');

	watch(isLogin, (newV, oldV) => {
		if (newV) {
			getUserExtractBank();
			getExtractUser();
		}
	}, { deep: true });

	onLoad(() => {
		if (isLogin.value) {
			getUserExtractBank();
			getExtractUser();
		} else {
			toLogin();
		}
	});

	function uploadpic(type) {
		proxy.$util.uploadImageOne({
			url: 'upload/image',
			name: 'multipart',
			model: "user",
			pid: 1
		}, function(res) {
			if (type === 'W') {
				qrcodeUrlW.value = res.data.url;
			} else {
				qrcodeUrlZ.value = res.data.url;
			}
		});
	}
	/**
	 * 删除图片
	 * 
	 */
	function DelPicW() {
		qrcodeUrlW.value = "";
	}
	function DelPicZ() {
		qrcodeUrlZ.value = "";
	}
	function getExtractUser() {
		extractUser().then(res => {
			commission.value = res.data;
			minPrice.value = res.data.minPrice;
		})
	}
	function getUserExtractBank() {
		extractBank().then(res => {
			let arr = res.data;
			arr.unshift("请选择银行");
			array.value = arr;
		});
	}
	function swichNav(current) {
		currentTab.value = current;
	}
	function bindPickerChange(e) {
		index.value = e.detail.value;
	}
	function moneyInput(e) {
		//正则表达试
		e.target.value = (e.target.value.match(/^\d*(\.?\d{0,2})/g)[0]) || null
		//重新赋值给input
		nextTick(() => {
			money.value = e.target.value
		})

	}
	const subCash = Debounce(function(e) {
		let value = e.detail.value;
		if (currentTab.value == 0) { //银行卡
			if (value.name.length == 0) return proxy.$util.Tips({
				title: '请填写持卡人姓名'
			});
			if (value.cardum.length == 0) return proxy.$util.Tips({
				title: '请填写卡号'
			});
			if (index.value == 0) return proxy.$util.Tips({
				title: "请选择银行"
			});
			value.extractType = 'bank';
			value.bankName = array.value[index.value];
		} else if (currentTab.value == 1) { //微信
			value.extractType = 'weixin';
			if (value.name.length == 0) return proxy.$util.Tips({
				title: '请填写微信号'
			});
			value.wechat = value.name;
			value.qrcodeUrl = qrcodeUrlW.value;
		} else if (currentTab.value == 2) { //支付宝
			value.extractType = 'alipay';
			if (value.name.length == 0) return proxy.$util.Tips({
				title: '请填写账号'
			});
			value.alipayCode = value.name;
			value.qrcodeUrl = qrcodeUrlZ.value;
		}
		if (value.money.length == 0) return proxy.$util.Tips({
			title: '请填写提现金额'
		});
		if (!(/^(\d?)+(\.\d{0,2})?$/.test(value.money))) return proxy.$util.Tips({
			title: '提现金额保留2位小数'
		});
		if (value.money < minPrice.value) return proxy.$util.Tips({
			title: '提现金额不能低于' + minPrice.value
		});
		if (isCommitted.value == false) {
			isCommitted.value = true;
			if (currentTab.value == 3) {
				transferIn({
					price: parseFloat(value.money)
				}).then(res => {
					appStore.changInfo({
						amount1: 'brokeragePrice',
						amount2: proxy.$util.$h.Sub(userInfo.value.brokeragePrice, parseFloat(value.money))
					});
					return proxy.$util.Tips({
						title: '提现成功',
						icon: 'success'
					}, {
						tab: 3,
						url: '/pages/promoter/user_spread_user/index'
					});
				}).catch(err => {
					return proxy.$util.Tips({
						title: err
					});
				})
			} else {
				extractCash(value).then(res => {
					return proxy.$util.Tips({
						title: "提现成功",
						icon: 'success'
					}, {
						tab: 2,
						url: '/pages/promoter/user_spread_user/index'
					});
					isCommitted.value = false;
				}).catch(err => {
					isCommitted.value = false;
					return proxy.$util.Tips({
						title: err
					});
				});
			}
		}
	})
</script>

<style lang="scss">
	page {
		background-color: #fff !important;
	}

	.cash-withdrawal .nav {
		height: 130rpx;
		box-shadow: 0 10rpx 10rpx #f8f8f8;
	}

	.cash-withdrawal .nav .item {
		font-size: 26rpx;
		flex: 1;
		text-align: center;
	}

	.cash-withdrawal .nav .item~.item {
		border-left: 1px solid #f0f0f0;
	}

	.cash-withdrawal .nav .item .iconfont {
		width: 40rpx;
		height: 40rpx;
		border-radius: 50%;
		@include coupons_border_color(theme);
		@include main_color(theme);
		text-align: center;
		line-height: 37rpx;
		margin: 0 auto 6rpx auto;
		font-size: 22rpx;
		box-sizing: border-box;
	}

	.cash-withdrawal .nav .item .iconfont.on {
		@include main_bg_color(theme);
		color: #fff !important;
		@include coupons_border_color(theme);
		// border-color: $theme-color;
	}

	.cash-withdrawal .nav .item .line {
		width: 2rpx;
		height: 20rpx;
		margin: 0 auto;
		transition: height 0.3s;
	}

	.tab_text {
		@include main_color(theme);
	}

	.bg_color {
		@include main_bg_color(theme);
	}

	.cash-withdrawal .nav .item .line.on {
		height: 39rpx;
	}

	.cash-withdrawal .wrapper .list {
		padding: 0 30rpx;
	}

	.cash-withdrawal .wrapper .list .item {
		border-bottom: 1rpx solid #eee;
		min-height: 28rpx;
		font-size: 30rpx;
		color: #333;
		padding: 39rpx 0;
	}

	.cash-withdrawal .wrapper .list .item .name {
		width: 130rpx;
	}

	.cash-withdrawal .wrapper .list .item .input {
		width: 505rpx;
	}

	.cash-withdrawal .wrapper .list .item .input .placeholder {
		color: #bbb;
	}

	.cash-withdrawal .wrapper .list .item .picEwm,
	.cash-withdrawal .wrapper .list .item .pictrue {
		width: 140rpx;
		height: 140rpx;
		border-radius: 3rpx;
		position: relative;
		margin-right: 23rpx;
	}

	.cash-withdrawal .wrapper .list .item .picEwm image {
		width: 100%;
		height: 100%;
		border-radius: 3rpx;
	}

	.cash-withdrawal .wrapper .list .item .picEwm .icon-guanbi1 {
		position: absolute;
		right: -14rpx;
		top: -16rpx;
		font-size: 40rpx;
	}

	.cash-withdrawal .wrapper .list .item .pictrue {
		border: 1px solid rgba(221, 221, 221, 1);
		font-size: 22rpx;
		color: #BBBBBB;
	}

	.cash-withdrawal .wrapper .list .item .pictrue .icon-icon25201 {
		font-size: 47rpx;
		color: #DDDDDD;
		margin-bottom: 3px;
	}

	.cash-withdrawal .wrapper .list .tip {
		font-size: 26rpx;
		color: #999;
		margin-top: 25rpx;
	}

	.cash-withdrawal .wrapper .list .bnt {
		font-size: 32rpx;
		color: #fff;
		width: 690rpx;
		height: 90rpx;
		text-align: center;
		border-radius: 50rpx;
		line-height: 90rpx;
		margin: 64rpx auto;
		@include main_bg_color(theme);
	}

	.cash-withdrawal .wrapper .list .tip2 {
		font-size: 26rpx;
		color: #999;
		text-align: center;
		margin: 44rpx 0 20rpx 0;
	}

	.cash-withdrawal .wrapper .list .value {
		height: 135rpx;
		line-height: 135rpx;
		border-bottom: 1rpx solid #eee;
		width: 690rpx;
		margin: 0 auto;
	}

	.cash-withdrawal .wrapper .list .value input {
		font-size: 80rpx;
		color: #282828;
		height: 135rpx;
		text-align: center;
	}

	.cash-withdrawal .wrapper .list .value .placeholder2 {
		color: #bbb;
	}

	.price {
		@include price_color(theme);
	}
</style>
