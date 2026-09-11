<template>
	<view class="groupBox" :style="[...boxPadding]" v-if="groupProductList.length">
		<view class="group" :style="[...boxStyle]">
			<view class="group-top acea-row row-middle row-between" :style="[bgImgStyle]">
				<view class="group-top-left acea-row">
					<image v-if="selectStyle == 0" :src="logoUrl" alt="" class="logo">
						<view v-else class="titleFont" :style="[...headerTitleConfig]">{{ titleConfig }}</view>
						<view class="interval" :style="[lineColor]"></view>
						<view class="num" :style="[titleColor]">低至0元免费拿</view>
				</view>
				<view class="group-top-right" :style="[headerBtnColor]" @click="toMore">
					更多
					<text class="iconfont icon-you" :style="[headerBtnColor]"></text>
				</view>
			</view>
			<!-- 样式一 -->
			<view v-if="listStyle == 0" :style="[...boxBgStyle]" class="group-bottom">
				<view v-for="(item, index) in groupProductList" :key="index" :style="[contentConfig]">
					<view class=" acea-row row-between" @click="toGroupDetail(item.id,item.product)">
						<view class="group-bottom-left">
							<view class="img acea-row row-center row-middle">
								<easy-loadimage :image-src="item.image" width="250rpx" height="250rpx"
									:radius="dataConfig.contentStyle.val"></easy-loadimage>
							</view>
						</view>
						<view class="group-bottom-right acea-row row-column row-between">
							<view class="right-top">
								<view class="title line2" v-if="typeShow.includes(0)" :style="[nameColor]">
									{{item.title}}
								</view>
								<view class="pink acea-row" v-if="typeShow.includes(1)">
									<view class="people-box acea-row" :style="[groupTitleColor]">
										<view class="people" :style="[groupTitleColor]">
											<text class="iconfont icon-kanjiahuohua"></text>
											<text>{{item.sales}}人砍价成功</text>
										</view>
									</view>
								</view>
							</view>
							<view class="right-bottom acea-row  row-between">
								<view class="price">
									<view class="pinkNum" v-if="typeShow.includes(2)" :style="[priceColor]"><text
											class="pinkNum-title"></text><text class="pinkNum-icon">￥</text><text
											class="pinkNum-num semiBold">{{item.minPrice}}</text>
									</view>
									<view class="num otNum regular" v-if="typeShow.includes(3)"
										:style="[originalColor]"><text class="num-title"></text><text
											class="icon-num ">￥{{item.price}}</text>
									</view>
								</view>
								<view class="btnBox" v-if="groupBtnShow">
									<view class="btn" :style="[...btnColor]">参与砍价</view>
								</view>
							</view>
						</view>
					</view>
				</view>
			</view>
			<!-- 样式二 -->
			<view class="group-bottom two acea-row row-between grid-list" v-if="listStyle == 1"
				:style="[...boxBgStyle]">
				<view v-for="(item, index) in groupProductList" :key="index" @click="toGroupDetail(item.id)">
					<view class="group-bottom-left">
						<view class="img acea-row row-center row-middle big-img">
							<easy-loadimage :image-src="item.image" width="324rpx" height="324rpx"
								:radius="dataConfig.contentStyle.val"></easy-loadimage>
						</view>
					</view>
					<view class="two-item">
						<view class="title acea-row">
							<text class="line1" v-if="typeShow.includes(0)" :style="[nameColor]">
								{{item.title}}
							</text>
						</view>
						<view class="two-item-bottom acea-row row-between">
							<view class="price">
								<view class="pinkNum" v-if="typeShow.includes(2)" :style="[priceColor]">
									<text class="num-icon">￥</text><text class="num semiBold">{{item.minPrice}}</text>
								</view>
								<view class="otNum regular" v-if="typeShow.includes(3)" :style="[originalColor]">
									￥{{item.price}}
								</view>
							</view>
							<view class="btnBox" v-if="groupBtnShow">
								<view class="btn" :style="[...btnColor]">去砍价</view>
							</view>
						</view>
					</view>
				</view>
			</view>
			<!-- 样式三 -->
			<view class="group-bottom three acea-row grid-three" v-if="listStyle == 2" :style="[...boxBgStyle]">
				<view v-for="(item, index) in groupProductList" :key="index" class="three-box"
					@click="toGroupDetail(item.id)">
					<view class="group-bottom-left">
						<view class="img acea-row row-center row-middle three-img">
							<easy-loadimage class="loadimage" width="100%" height="100%" :image-src="item.image"
								:radius="dataConfig.contentStyle.val"></easy-loadimage>
						</view>
					</view>
					<view class="two-item">
						<view class="title acea-row">
							<text class="line1" v-if="typeShow.includes(0)" :style="[nameColor]">
								{{item.title}}
							</text>
						</view>
						<view class="two-item-bottom">
							<view class="price">
								<view class="pinkNum line1" v-if="typeShow.includes(2)" :style="[priceColor]">
									<text class="num-icon"><text class="min-text">低至</text>￥</text><text
										:style="[{'font-size':item.minPrice.length>6?'30rpx':'36rpx'}]"
										class="num semiBold">{{item.minPrice}}</text>
								</view>
								<view class="otNum regular" v-if="typeShow.includes(3)" :style="[originalColor]">
									￥{{item.price}}
								</view>
							</view>
						</view>
					</view>
				</view>
			</view>
			<!-- 样式四 -->
			<view class="group-bottom four acea-row " v-if="listStyle == 3" :style="[...boxBgStyle]">
				<scroll-view scroll-x="true" class="scroll_view">
					<view v-for="(item, index) in groupProductList" :key="index" class="four-item" :style="[fourStyle]"
						@click="toGroupDetail(item.id)">
						<view class="group-bottom-left">
							<view class="img acea-row row-center row-middle four-img">
								<easy-loadimage :image-src="item.image" width="214rpx" height="214rpx"
									:radius="dataConfig.contentStyle.val"></easy-loadimage>
							</view>
						</view>
						<view class="two-item">
							<view class="title acea-row" v-if="typeShow.includes(0)" :style="[nameColor]">
								<text class="line1">
									{{item.title}}
								</text>
							</view>
							<view class="two-item-bottom">
								<view class="price">
									<view class="people-size-box" v-if="typeShow.includes(1)" :style="[...peopleStyle]">
										<view class="people-size">{{item.sales}}人已砍成功</view>
									</view>
									<view class="pinkNum" v-if="typeShow.includes(2)" :style="[priceColor]">
										<text class="num-icon">￥</text><text
											class="num semiBold">{{item.minPrice}}</text>
									</view>
								</view>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>
			<emptyPage :mTop="'0'" v-if="groupProductList.length==0" title="暂无砍价商品，去看看其他商品吧~～"
				:imgSrc="urlDomain+'/crmebimage/presets/noActivity.png'"></emptyPage>
		</view>
	</view>
</template>

<script setup>
	import { ref, reactive, computed } from 'vue';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		getBargainIndexApi
	} from '@/api/activity.js';
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	import emptyPage from '@/components/emptyPage.vue'
	import util from '@/utils/util.js';
	import Cache from '@/utils/cache.js';
	import { filterTheme } from '@/filters';
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from 'pinia';

	const app = getApp();
	const appStore = useAppStore();
	const { userInfo: userData, uid } = storeToRefs(appStore);

	const props = defineProps({
		dataConfig: {
			type: Object,
			default: () => {}
		},
	});

	const urlDomain = ref(Cache.get("imgHost"));
	const listStyle = ref(0);
	const logoUrl = ref(null);
	const typeShow = ref([0, 1, 2, 3]);
	const groupBtnShow = ref(true);
	const selectStyle = ref('');
	const titleConfig = ref('');
	const selectBgImg = ref('');
	const bgImgUrl = ref('');
	const headerTitleStyle = ref(0);
	const old = reactive({ scrollTop: 0 });
	const groupInfo = ref({});
	const groupProductList = ref([]);
	const themeColor = ref(filterTheme(app.globalData.theme));

	//容器样式
	//最外层盒子的样式
	const boxStyle = computed(() => [{
			'border-radius': props.dataConfig.bgStyle.val ? 2 * props.dataConfig.bgStyle.val + 'rpx' : '0'
		},
		{
			margin: 0 + ' ' + 2 * props.dataConfig.lrConfig.val + 'rpx' + ' ' + 0
		},
		{
			background: `linear-gradient(to right,${props.dataConfig.bgColor.color[0].item}, ${props.dataConfig.bgColor.color[1].item})`,
		},
	]);
	//边距
	const boxPadding = computed(() => [{
			padding: 2 * props.dataConfig.upConfig.val + 'rpx' + ' ' + '0rpx' + ' ' + 2 * props.dataConfig
				.downConfig.val + 'rpx',
		},
		{
			margin: 2 * props.dataConfig.mbConfig.val + 'rpx' + ' ' + 0 + ' ' + 0
		},
	]);
	//背景颜色
	const boxBgStyle = computed(() => [{
			gap: listStyle.value != 3 ? `${2*props.dataConfig.contentConfig.val}rpx` : ''
		},
		{
			background: `linear-gradient(to right,${props.dataConfig.contentBgColor.color[0].item}, ${props.dataConfig.contentBgColor.color[1].item})`,
		},
		{
			'justify-content': 'space-between',
		},
	]);
	const fourStyle = computed(() => ({
		'margin-right': listStyle.value == 3 ? `${2*props.dataConfig.contentConfig.val}rpx` : ''
	}));
	//标题颜色
	const titleColor = computed(() => ({
		color: props.dataConfig.titleColor.color[0].item,
	}));
	//头部按钮颜色
	const headerBtnColor = computed(() => ({
		color: props.dataConfig.headerBtnColor.color[0].item,
	}));
	//商品名称颜色
	const nameColor = computed(() => ({
		color: props.dataConfig.nameColor.color[0].item,
	}));
	//商品原价颜色
	const originalColor = computed(() => ({
		color: props.dataConfig.originalColor.color[0].item,
	}));
	//砍价价格颜色
	const priceColor = computed(() => ({
		color: props.dataConfig.themeStyleConfig.tabVal ? props.dataConfig.priceColor.color[0].item : themeColor.value,
	}));
	//标签颜色
	const groupTitleColor = computed(() => ({
		color: props.dataConfig.themeStyleConfig.tabVal ? props.dataConfig.groupTitleColor.color[0].item : themeColor.value,
	}));
	const peopleStyle = computed(() => [{
			color: '#fff',
		},
		{
			background: `linear-gradient(to right,${
		            props.dataConfig.themeStyleConfig.tabVal ? props.dataConfig.btnColor.color[0].item : '#FF7931'
		          }, ${props.dataConfig.themeStyleConfig.tabVal? props.dataConfig.btnColor.color[1].item : themeColor.value})`,
		},
		{
			'align-self': 'baseline',
		},
		{
			'border-radius': '14rpx',
		},
	]);
	//已拼颜色
	const groupTitleFontColor = computed(() => ({
		color: props.dataConfig.groupTitleColor.color[0].item,
	}));
	//分割线颜色
	const lineColor = computed(() => ({
		border: `1rpx solid ${props.dataConfig.lineColor.color[0].item}`,
	}));
	//按钮颜色
	const btnColor = computed(() => [{
			background: `linear-gradient(to right,${
		            props.dataConfig.themeStyleConfig.tabVal ? props.dataConfig.btnColor.color[0].item : '#FF7931'
		          }, ${props.dataConfig.themeStyleConfig.tabVal ? props.dataConfig.btnColor.color[1].item : themeColor.value})`,
		},
		{
			color: props.dataConfig.btnFontColor.color[0].item,
		}
	]);
	//图片圆角
	const contentStyle = computed(() => ({
		'border-radius': props.dataConfig.contentStyle.val ? 2 * props.dataConfig.contentStyle.val + 'rpx' : '0',
	}));
	//样式一内容边距
	const contentConfig = computed(() => ({
		'paddingBottom': 2 * props.dataConfig.contentConfig.val + 'rpx',
	}));
	//背景图片
	const bgImgStyle = computed(() => ({
		'background': selectBgImg.value == 0 ? `url(${bgImgUrl.value})` :
			`linear-gradient(to right,${props.dataConfig.bgColor.color[0].item}, ${props.dataConfig.bgColor.color[1].item})`,
	}));
	//标题文字格式
	const headerTitleConfig = computed(() => [{
			'font-weight': headerTitleStyle.value == 0 ? 600 : ''
		},
		{
			'font-style': headerTitleStyle.value == 2 ? 'italic' : 'normal'
		},
		{
			color: props.dataConfig.headerTitleColor.color[0].item,
		}
	]);

	// created
	setConfig()
	getInfo()

	//去砍价
	function toGroupDetail(id) {
		if (uid.value) {
			uni.navigateTo({
				url: `/pages/activity/goods_bargain_details/index?id=${id}&startBargainUid=${uid.value}`
			})
		} else {
			toLogin()
		}
	}
	function getInfo() {
		getBargainIndexApi().then(function(res) {
			groupProductList.value = res.data.productList;
			groupInfo.value = {
				totalPeople: res.data.totalPeople,
				avatarList: res.data.avatarList
			}
		}).catch((res) => {
			return util.Tips({
				title: res
			});
		})
	}
	function scroll(e) {
		old.scrollTop = e.detail.scrollTop
	}
	// 更多
	function toMore() {
		uni.navigateTo({
			url: '/pages/activity/goods_bargain/index'
		})
	}
	function setConfig() {
		listStyle.value = props.dataConfig.tabConfig.tabVal;
		logoUrl.value = props.dataConfig.logoConfig.url;
		typeShow.value = props.dataConfig.typeConfig.activeValue;
		groupBtnShow.value = props.dataConfig.groupBtnConfig.tabVal == 0 ? true : false;
		selectStyle.value = props.dataConfig.selectStyle.tabVal;
		titleConfig.value = props.dataConfig.titleConfig.val;
		selectBgImg.value = props.dataConfig.selectBgImg.tabVal;
		bgImgUrl.value = props.dataConfig.bgImg.url;
		headerTitleStyle.value = props.dataConfig.headerTitleStyle.tabVal;
	}
</script>

<style lang="scss" scoped>
	.groupBox {
		overflow: hidden;

		.group {
			overflow: hidden;

			.group-top {
				width: 100%;
				height: 100rpx;
				padding: 0 24rpx;
				background-size: cover !important;

				.group-top-left {
					align-items: center;

					.ml10 {
						margin-top: 4rpx;
					}

					.logo {
						width: 154rpx;
						height: 32rpx;
						object-fit: contain;
					}

					.interval {
						width: 0rpx;
						height: 28rpx;
						margin-left: 20rpx;
						margin-top: 4rpx;
						opacity: 0.6;
					}

					.pinkHead {
						width: 108rpx;
						height: 36rpx;
					}

					.num {
						height: 32rpx;
						line-height: 32rpx;
						font-size: 26rpx;
						margin-left: 16rpx;
						color: #FFFFFF;
						margin-top: 6rpx;
					}
				}

				.group-top-right {
					height: 32rpx;
					line-height: 32rpx;
					font-size: 24rpx;

					.icon-xiangyou {
						font-size: 24rpx;
					}
				}
			}

			.group-bottom {
				width: 100%;
				padding: 20rpx;

				.img {
					width: 240rpx;
					// height: 240rpx;
					// background: #F3F9FF;

				}

				.big-img.img {
					width: 100%;
					height: 324rpx;
				}

				.three-img.img {
					width: 100%;
					height: 210rpx;
				}

				.loadimage {
					width: 100%;
					height: 100%;
				}

				.four-img {
					width: 100%;
					height: 100%;
				}

				.group-bottom-right {
					flex: 1;
					margin-left: 20rpx;

					.right-top {
						.title {
							font-size: 28rpx;
							color: #333333;
						}

						.people-box {
							border-radius: 8rpx;
						}

						.pink {
							margin-top: 16rpx;
							font-size: 22rpx;
							border-radius: 8rpx;

							.people {
								color: #fff;
								padding: 4rpx 12rpx;
								border-radius: 8rpx;
							}

							.groupNum {
								background-color: rgba(255, 255, 255, 0.9);
								padding: 4rpx 12rpx;
								border-radius: 0 6rpx 6rpx 0;
								margin-left: 2rpx;
							}
						}
					}

					.right-bottom {

						.price {

							.pinkNum {

								.pinkNum-num {
									font-size: 36rpx;
								}
							}

							.num {
								color: #999999;
							}
						}

						.btnBox {
							// margin-top: 16rpx;
							font-size: 22rpx;
							display: flex;
							align-items: end;

							.btn {
								padding: 12rpx 20rpx;
								border-radius: 50rpx;
							}
						}
					}
				}
			}

			.group-bottom.two {
				.two-item {
					width: 100%;

					.title {
						margin-top: 20rpx;

						.numPink {
							color: #ffffff;
							padding: 6rpx 12rpx;
							border-radius: 4rpx;
							font-size: 22rpx;
						}

						.line1 {
							width: 300rpx;
						}
					}

					.two-item-bottom {
						margin-top: 10rpx;

						.pinkNum {

							.num {
								font-size: 36rpx;
							}
						}

						.otNum {
							font-size: 26rpx;
							color: #999999;
						}

						.btnBox {
							margin-top: 16rpx;
							font-size: 22rpx;

							.btn {
								padding: 12rpx 24rpx;
								border-radius: 50rpx;
							}
						}
					}
				}
			}

			.group-bottom.three {
				.three-box {
					position: relative;
				}

				.numPink {
					color: #ffffff;
					padding: 6rpx 12rpx;
					border-radius: 32rpx;
					font-size: 22rpx;
					z-index: 9;
				}

				.two-item {
					width: 100%;

					.title {
						margin-top: 20rpx;
						// width: 100%;

						.numPink {
							color: #ffffff;
							padding: 4rpx 12rpx;
							border-radius: 32rpx;
							font-size: 22rpx;
						}

						.line1 {
							width: 180rpx;
						}
					}

					.two-item-bottom {
						margin-top: 10rpx;

						.pinkNum {

							.num {
								width: 200rpx;
								font-size: 36rpx;
							}
						}

						.otNum {
							font-size: 26rpx;
							color: #999999;
						}

						.btnBox {
							margin-top: 16rpx;
							font-size: 22rpx;

							.btn {
								padding: 12rpx 24rpx;
								border-radius: 50rpx;
							}
						}
					}
				}
			}

			.group-bottom.four {
				overflow: hidden;
				flex-wrap: nowrap;
				position: relative;

				.four-item {
					position: relative;
				}

				.numPink {
					color: #ffffff;
					padding: 6rpx 12rpx;
					border-radius: 32rpx;
					font-size: 22rpx;
					z-index: 9;
				}


				.two-item {
					width: 210rpx;

					.title {
						margin-top: 16rpx;

						.numPink {
							color: #ffffff;
							padding: 6rpx 12rpx;
							border-radius: 32rpx;
							font-size: 22rpx;
						}

						.line1 {
							width: 210rpx;
						}
					}

					.two-item-bottom {
						margin-top: 10rpx;

						.pinkNum {
							margin-top: 12rpx;

							.num {
								font-size: 36rpx;
							}
						}

						.otNum {
							font-size: 26rpx;
						}

						.btnBox {
							margin-top: 16rpx;
							font-size: 22rpx;

							.btn {
								padding: 12rpx 24rpx;
								border-radius: 50rpx;
							}
						}
					}
				}
			}
		}

		.price {
			display: flex;
			flex-direction: column;
			justify-content: flex-end;
		}

		.grid-list {
			display: grid !important;
			grid-template-columns: repeat(2, 1fr);
			grid-template-rows: auto;
			width: 100%;
		}

		.grid-three {
			width: 100%;
			display: grid !important;
			grid-template-columns: repeat(3, 1fr);
			grid-template-rows: auto;
		}
	}

	.titleFont {
		font-size: 32rpx;
	}

	.scroll_view {
		white-space: nowrap;

		.four-item {
			display: inline-block;
			color: #999999;
		}
	}

	.num-icon {
		font-weight: 500 !important;
	}

	.numPink {
		background-color: rgba(255, 255, 255, 0.9);
	}

	.numPink-box {
		border-radius: 8rpx;
	}

	.numPink-box-special {
		position: absolute;
		left: 10rpx;
		top: 10rpx;
		border-radius: 32rpx;
	}

	.avater-box {
		position: relative;
		display: flex;
		align-items: end;
		margin-top: 4rpx;

		image {
			width: 36rpx;
			height: 36rpx;
			border-radius: 18rpx;
		}

		.avater1 {
			margin-left: 20rpx;
		}

		.avater2 {
			position: absolute;
			margin-left: 48rpx;
		}

		.avater3 {
			position: absolute;
			left: 78rpx;
		}
	}

	.regular {
		line-height: 34rpx !important;
		font-weight: 400 !important;
	}

	.otNum {
		text-decoration: line-through;
	}

	.people-size {
		font-size: 20rpx;
		padding: 4rpx 10rpx;
	}

	.huo {
		width: 16rpx;
		height: 20rpx;
		vertical-align: baseline;
		margin-right: 4rpx;
	}

	.icon-kanjiahuohua {
		font-size: 24rpx;
	}

	.min-text {
		font-size: 22rpx !important;
		font-weight: 500;
	}

	.icon-you {
		font-size: 24rpx;
	}

	.regular {
		line-height: 17px !important;
	}
</style>