<template>
	<view :data-theme="theme" :style="colorStyle">
		<block v-if="bargain.length>0">
			<div class="bargain-record" ref="container">
				<div class="item borRadius14" v-for="(item, index) in bargain" :key="index">
					<div class="picTxt acea-row row-between-wrapper">
						<div class="pictrue">
							<image :src="item.image" />
						</div>
						<div class="text acea-row row-column-around">
							<div class="line1" style="width: 100%;">{{ item.title }}</div>
							<count-down :justify-left="'justify-content:left'" :bgColor="bgColor" :is-day="true" :tip-text="'倒计时 '" :day-text="'天'"
							 :hour-text="' 时 '" :minute-text="' 分 '"
							 :second-text="' 秒 '" :datatime="item.stopTime/1000" v-if="item.status === 1"></count-down>
							<div class="successTxt font_color" v-else-if="item.status === 3 && item.isDel === false">砍价成功</div>
							<div class="successTxt " v-else-if="item.status === 3 && item.isDel === true && item.isPay === false">砍价失败</div>
							<div class="endTxt" v-else>活动已结束</div>
							<div class="money">
								已砍至<span class="symbol font_color">￥</span><span class="num font_color">{{ item.surplusPrice }}</span>
							</div>
						</div>
					</div>
					<div class="bottom acea-row row-between-wrapper">
						<div class="purple" v-if="item.status === 1">活动进行中</div>
						<div class="success" v-else-if="item.status === 3 && item.isDel === false">砍价成功</div>
						<div class="end" v-else-if="item.status === 3 && item.isDel === true && item.isPay === false">砍价失败</div>
						<div class="end" v-else>活动已结束</div>
						<div class="acea-row row-middle row-right">
							<div class="bnt bg_color" v-if="item.status === 3 && !item.isOrder" @click="goConfirm(item)">
								去付款
							</div>
							<div class="bnt bg_color" v-if="item.status === 3 && !item.isDel && item.isOrder && !item.isPay" @click="goPay(item.surplusPrice,item.orderNo)">
								立即付款
							</div>
							<div class="bnt bg_color" v-if="item.status === 1" @click="goDetail(item.id)">
								继续砍价
							</div>
							<div class="bnt bg_color" v-if="item.status === 2" @click="goList">重开一个</div>
						</div>
					</div>
				</div>
				<Loading :loaded="status" :loading="loadingList"></Loading>
			</div>
		</block>
		<block v-if="bargain.length == 0">
			<emptyPage title="暂无砍价记录～"></emptyPage>
		</block>
		<payment :pay_close="pay_close" @onChangeFun='onChangeFun' :order_id="pay_order_id" :totalPrice='totalPrice'></payment>
	</view>
</template>
<script setup>
import { ref } from "vue";
import { onShow, onReachBottom } from "@dcloudio/uni-app";
import CountDown from "@/pages/activity/components/countDown/index.vue";
import emptyPage from "@/components/emptyPage.vue";
import { getBargainUserList, getBargainUserCancel } from "@/api/activity.js";
import Loading from "@/components/Loading/index.vue";
import payment from "@/components/payment/index.vue";
import { toLogin } from "@/libs/login.js";
import { setThemeColor } from "@/utils/setTheme.js";
import animationType from "@/utils/animationType.js";
import util from "@/utils/util.js";
import { useAppStore } from "@/store/app.js";
import { storeToRefs } from "pinia";
import { useColor } from '@/composables/useColor.js';

const app = getApp();
const appStore = useAppStore();
const { isLogin, userInfo, uid } = storeToRefs(appStore);

const bgColor = ref({ bgColor: "", Color: "#E93323", width: "40rpx", timeTxtwidth: "28rpx", isDay: false });
const bargain = ref([]);
const status = ref(false);
const loadingList = ref(false);
const page = ref(1);
const limit = ref(20);
const payMode = ref([
  { name: "微信支付", icon: "icon-weixinzhifu", value: "weixin", title: "微信快捷支付" },
  { name: "余额支付", icon: "icon-yuezhifu", value: "yue", title: "可用余额:", number: 0 },
]);
const pay_close = ref(false);
const pay_order_id = ref("");
const totalPrice = ref("0");
const theme = ref(app.globalData.theme);
const { colorStyle } = useColor();

onShow(() => {
  if (isLogin.value) {
    payMode.value[1].number = userInfo.value && userInfo.value.nowMoney || 0;
    getBargainUserListFn();
    bgColor.value.Color = setThemeColor();
  } else {
    toLogin();
  }
});

onReachBottom(() => { getBargainUserListFn(); });

function goPay(pay_price, order_id) {
  pay_close.value = true;
  pay_order_id.value = order_id;
  totalPrice.value = pay_price;
}

const paymentActionMap = {
  payClose,
  pay_complete,
  pay_fail,
};

function onChangeFun(e) {
  let opt = e;
  let action = opt.action || null;
  let value = opt.value != undefined ? opt.value : null;
  const handler = paymentActionMap[action];
  if (typeof handler === "function") handler(value);
}

function payClose() { pay_close.value = false; }

function pay_complete() {
  status.value = false;
  page.value = 1;
  bargain.value = [];
  pay_close.value = false;
  getBargainUserListFn();
}

function pay_fail() { pay_close.value = false; }

function goConfirm(item) {
  if (!isLogin.value) { toLogin(); return; }
  uni.navigateTo({
    animationType: animationType.type, animationDuration: animationType.duration,
    url: `/pages/activity/goods_bargain_details/index?id=${item.id}&startBargainUid=${uid.value}&storeBargainId=${item.bargainUserId}`,
  });
}

function goDetail(id) {
  uni.navigateTo({
    animationType: animationType.type, animationDuration: animationType.duration,
    url: `/pages/activity/goods_bargain_details/index?id=${id}&startBargainUid=${uid.value}`,
  });
}

function goList() {
  uni.navigateTo({
    animationType: animationType.type, animationDuration: animationType.duration,
    url: "/pages/activity/goods_bargain/index",
  });
}

function getBargainUserListFn() {
  if (loadingList.value) return;
  if (status.value) return;
  getBargainUserList({ page: page.value, limit: limit.value })
    .then((res) => {
      status.value = res.data.list.length < limit.value;
      bargain.value.push.apply(bargain.value, res.data.list);
      page.value++;
      loadingList.value = false;
    })
    .catch((res) => {
      util.Tips({ title: res });
    });
}

function getBargainUserCancelFn(bargainId) {
  getBargainUserCancel({ bargainId })
    .then((res) => {
      status.value = false;
      loadingList.value = false;
      page.value = 1;
      bargain.value = [];
      getBargainUserListFn();
      util.Tips({ title: res });
    })
    .catch((res) => {
      util.Tips({ title: res });
    });
}
</script>
<style lang="scss">
	page {
		// @include main_bg_color(theme);
		height: 100vh;
		overflow: auto;
	}
</style>
<style lang="scss" scoped>
	/*砍价记录*/
	.bargain-record{
		padding: 0 30rpx 15rpx;
	}
	.bargain-record .item .picTxt .text .time {
		height: 36rpx;
		line-height: 36rpx;
		.styleAll {
			color: #fc4141;
			font-size:24rpx;
		}
	}
	.bargain-record .item .picTxt .text .time .red {
		color: #999;
		font-size:24rpx;
	}
	.bargain-record .item {
		background-color: #fff;
		margin-top: 15rpx;
		padding: 30rpx 24rpx 0 24rpx;
	}

	.bargain-record .item .picTxt {
		border-bottom: 1px solid #f0f0f0;
		padding-bottom: 30rpx;
	}

	.bargain-record .item .picTxt .pictrue {
		width: 150upx;
		height: 150upx;
	}

	.bargain-record .item .picTxt .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 6upx;
	}

	.bargain-record .item .picTxt .text {
		width: 470rpx;
		font-size: 30upx;
		color: #333333;
		height: 160rpx;
	}

	.bargain-record .item .picTxt .text .time {
		font-size: 24upx;
		color: #868686;
		justify-content: left !important;
	}
	
	.bargain-record .item .picTxt .text .successTxt{
		font-size:24rpx;
	}
	
	.bargain-record .item .picTxt .text .endTxt{
		font-size:24rpx;
		color: #999;
	}
	.bargain-record .item .picTxt .text .money {
		font-size: 24upx;
		color: #999999;
	}

	.bargain-record .item .picTxt .text .money .num {
		font-size: 32upx;
		font-weight: bold;
	}

	.bargain-record .item .picTxt .text .money .symbol {
		font-weight: bold;
	}

	.bargain-record .item .bottom {
		height: 100upx;
		font-size: 27upx;
	}

	.bargain-record .item .bottom .purple {
		color: #f78513;
	}

	.bargain-record .item .bottom .end {
		color: #999;
	}

	.bargain-record .item .bottom .success {
		color: $theme-color;
	}

	.bargain-record .item .bottom .bnt {
		font-size: 27upx;
		color: #fff;
		width: 176upx;
		height: 60upx;
		border-radius: 32upx;
		text-align: center;
		line-height: 60upx;
	}
	.bg_color{
		@include main_bg_color(theme);
	}
	.font_color{
		@include price_color(theme);
	}
	.bargain-record .item .bottom .bnt.cancel {
		color: #aaa;
		border: 1px solid #ddd;
	}

	.bargain-record .item .bottom .bnt~.bnt {
		margin-left: 18upx;
	}
</style>
