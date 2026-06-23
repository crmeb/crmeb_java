<template>
	<view :data-theme="theme">
		<view class='personal-data borderPad'>
			<view class='list borRadius14' v-for="(item,index) in tabList" :key="index" @click="goMultiple(item.info)">
				<view class='item acea-row row-between-wrapper'>
					<view>{{item.title}}</view>
					<text class='iconfont icon-xiangyou'></text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	// +----------------------------------------------------------------------
	// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
	// +----------------------------------------------------------------------
	// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
	// +----------------------------------------------------------------------
	// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
	// +----------------------------------------------------------------------
	// | Author: CRMEB Team <admin@crmeb.com>
	// +----------------------------------------------------------------------
	import {
		mapGetters
	} from "vuex";
  import {goToAgreement} from "@/libs/order";
	let app = getApp();
	export default {
		data() {
			return {
				theme: app.globalData.theme,
				tabList: [{
						title: "用户协议",
						info: 'userinfo',
						save: 'usersave'
					},
					{
						title: "隐私政策",
						info: 'userprivacyinfo',
						save: 'userprivacysave'
					},
					{
						title: "关于我们",
						info: 'aboutusinfo',
						save: 'aboutussave'
					},
					{
						title: "资质证照",
						info: 'intelligentinfo',
						save: 'intelligentsave'
					},
					{
						title: "优惠券协议",
						info: 'coupon/agreement/info',
						save: 'platfromrulesave'
					},
				],
			}
		},
		onLoad() {

		},
		methods: {
			goMultiple(e){
        goToAgreement(e)
			}
		}
	}
</script>

<style scoped lang="scss">
	.personal-data{
		.list {
			margin-top: 30rpx;
			background-color: #fff;
			.item {
				border-bottom: 1rpx solid #f2f2f2;
				padding: 24rpx;
				font-size: 32rpx;
				color: #333333;
				.input {
					text-align: right;
					color: #BBBBBB;
					.iconfont {
						font-size: 35rpx;
						margin-left: 20rpx;
					}
				}
				.icon-xiangyou {
					color: #BBBBBB;
				}
			}
		}
	}
</style>
