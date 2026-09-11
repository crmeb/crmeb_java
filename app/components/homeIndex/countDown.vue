<template>
	<!-- 时间倒计时 -->
	<view class="time" :style="justifyLeft">
		<text class="" v-if="tipText">{{ tipText }}</text>
		<text class="styleAll p6" v-if="isDay === true" :style="{background:bgColor.bgColor,color:bgColor.Color}">{{ day }}{{bgColor.isDay?'天':''}}</text>
		<text class="timeTxt" v-if="dayText" :style="{width:bgColor.timeTxtwidth,color:bgColor.bgColor}">{{ dayText }}</text>
		<text class="styleAll" :class='isCol?"timeCol":""' :style="{background:bgColor.bgColor,color:bgColor.Color,width:bgColor.width}">{{ hour }}</text>
		<text class="timeTxt" v-if="hourText" :class='isCol?"whit":""' :style="{width:bgColor.timeTxtwidth,color:bgColor.bgColor}">{{ hourText }}</text>
		<text class="styleAll" :class='isCol?"timeCol":""' :style="{background:bgColor.bgColor,color:bgColor.Color,width:bgColor.width}">{{ minute }}</text>
		<text class="timeTxt" v-if="minuteText" :class='isCol?"whit":""' :style="{width:bgColor.timeTxtwidth,color:bgColor.bgColor}">{{ minuteText }}</text>
		<text class="styleAll" :class='isCol?"timeCol":""' :style="{background:bgColor.bgColor,color:bgColor.Color,width:bgColor.width}">{{ second }}</text>
		<text class="timeTxt" v-if="secondText">{{ secondText }}</text>
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
		import { ref, onMounted } from 'vue';

		const props = defineProps({
			justifyLeft: {
				type: String,
				default: ""
			},
			//距离开始提示文字
			tipText: {
				type: String,
				default: "倒计时"
			},
			dayText: {
				type: String,
				default: "天"
			},
			hourText: {
				type: String,
				default: "时"
			},
			minuteText: {
				type: String,
				default: "分"
			},
			secondText: {
				type: String,
				default: "秒"
			},
			datatime: {
				type: Number,
				default: 0
			},
			isDay: {
				type: Boolean,
				default: true
			},
			isCol: {
				type: Boolean,
				default: false
			},
			bgColor: {
				type: Object,
				default: null
			}
		});

		const day = ref("00");
		const hour = ref("00");
		const minute = ref("00");
		const second = ref("00");

		// created
		show_time();

		onMounted(() => {});

		function show_time() {
			function runTime() {
				//时间函数
				let intDiff = props.datatime - Date.parse(new Date()) / 1000; //获取数据中的时间戳的时间差；
				let dayNum = 0,
					hourNum = 0,
					minuteNum = 0,
					secondNum = 0;
				if (intDiff > 0) {
					//转换时间
					if (props.isDay === true) {
						dayNum = Math.floor(intDiff / (60 * 60 * 24));
					} else {
						dayNum = 0;
					}
					hourNum = Math.floor(intDiff / (60 * 60)) - dayNum * 24;
					minuteNum = Math.floor(intDiff / 60) - dayNum * 24 * 60 - hourNum * 60;
					secondNum =
						Math.floor(intDiff) -
						dayNum * 24 * 60 * 60 -
						hourNum * 60 * 60 -
						minuteNum * 60;
					if (hourNum <= 9) hourNum = "0" + hourNum;
					if (minuteNum <= 9) minuteNum = "0" + minuteNum;
					if (secondNum <= 9) secondNum = "0" + secondNum;
					day.value = dayNum;
					hour.value = hourNum;
					minute.value = minuteNum;
					second.value = secondNum;
				} else {
					day.value = "00";
					hour.value = "00";
					minute.value = "00";
					second.value = "00";
				}
			}
			runTime();
			setInterval(runTime, 1000);
		}
	</script>

<style scoped>
	.p6{
		padding: 0 8rpx;
	}
	.styleAll{
		/* color: #fff; */
		font-size: 24rpx;
		height: 36rpx;
		line-height: 36rpx;
		border-radius: 6rpx;
		text-align: center;
		/* padding: 0 6rpx; */
	}
	.timeTxt{
		    text-align: center;
		    /* width: 16rpx; */
		    height: 36rpx;
		    line-height: 36rpx;
		    display: inline-block;
	}
	.whit{
		color: #fff !important;
	}
	.time {
		display: flex;
		justify-content: center;
	}

	.red {
		color: #fc4141;
		margin: 0 4rpx;
	}

	.timeCol {
		/* width: 40rpx;
		height: 40rpx;
		line-height: 40rpx;
		text-align:center;
		border-radius: 6px;
		background: #fff;
		font-size: 24rpx; */
		color: #E93323;
	}
</style>