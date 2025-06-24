<template>
	<view :data-theme="theme">
		<view class="CommissionRank">
			<view class="header">
				<view class="rank" v-if="position">您目前的排名<text class="num">{{position}}</text>名</view>
				<view class="rank" v-else>您目前暂无排名</view>
			</view>
			<view class="wrapper">
				<view class="nav acea-row row-around">
					<view class="item" :class="active == index ? 'font_color' : ''" v-for="(item,index) in navList" :key="index"
					 @click="switchTap(index)">
						{{ item }}
					</view>
				</view>
				<view class="list">
					<view class="item acea-row row-between-wrapper" v-for="(item,index) in rankList" :key="index">
						<view class="num" v-if="index <= 2">
							<image :src="urlDomain+'crmebimage/perset/staticImg/medal0'+(index+1)+'.png'"></image>
						</view>
						<view class="num" v-else>
							{{index+1}}
						</view>
						<view class="picTxt acea-row row-between-wrapper">
							<view class="pictrue">
								<image :src="item.avatar"></image>
							</view>
							<view class="text line1">{{item.nickname}}</view>
						</view>
						<view class="people font_color">￥{{item.brokeragePrice}}</view>
					</view>
				</view>
				<view class='noCommodity' v-if="rankList.length == 0 && isShow">
					<emptyPage title="暂无排行～"></emptyPage>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getBrokerageRank,
		brokerageRankNumber
	} from '@/api/user.js';
	import {toLogin} from '@/libs/login.js';
	import emptyPage from '@/components/emptyPage.vue'
	import {mapGetters} from "vuex";
	import {setThemeColor} from '@/utils/setTheme.js'
	const app = getApp();
	export default {
		components: {
			emptyPage
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				navList: ["周排行", "月排行"],
				active: 0,
				rankList: [],
				page: 1,
				limit: 20,
				loadend: false,
				loading: false,
				loadTitle: '加载更多',
				type: 'week',
				position: 0,
				isShow: false,
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false, //是否隐藏授权
				theme:app.globalData.theme,
				bgColor:'#e93323'
			};
		},
		computed: mapGetters(['isLogin']),
		watch:{
			isLogin:{
				handler:function(newV,oldV){
					if(newV){
						this.getBrokerageRankList();
						this.getBrokerageRankNumber(this.type);
					}
				},
				deep:true
			}
		},
		onLoad() {
			if (this.isLogin) {
				this.getBrokerageRankList();
				this.getBrokerageRankNumber(this.type);
			} else {
				toLogin();
			}
			let that = this;
			that.bgColor = setThemeColor();
			uni.setNavigationBarColor({
				frontColor: '#ffffff',
				backgroundColor:that.bgColor,
			});
		},
		methods: {
			onLoadFun: function() {
				this.getBrokerageRankList();
				this.getBrokerageRankNumber(this.type);
			},
			// 授权关闭
			authColse: function(e) {
				this.isShowAuth = e
			},
			switchTap: function(index) {
				this.active = index;
				this.type = index ? 'month' : 'week';
				this.page = 1;
				this.loadend = false;
				this.$set(this, 'rankList', []);
				this.getBrokerageRankList();
				this.getBrokerageRankNumber(this.type);
			},
			getBrokerageRankNumber(type) {
				brokerageRankNumber({
					type: type
				}).then(res => {
					this.position = res.data;
				})
			},
			getBrokerageRankList: function() {
				if (this.loadend) return;
				if (this.loading) return;
				this.loading = true;
				this.loadTitle = '';
				getBrokerageRank({
					page: this.page,
					limit: this.limit,
					type: this.type
				}).then(res => {
					let list = res.data || [];
					let loadend = list.length <= this.limit;
					this.rankList.push.apply(this.rankList, list);
					this.loading = false;
					this.loadend = loadend;
					this.loadTitle = loadend ? '我也是有底线的~' : '加载更多';
					this.$set(this, 'rankList', this.rankList);
					this.isShow = true;
					//this.position = res.data.position;
				}).catch(err => {
					this.loading = false;
					this.loadTitle = '加载更多'; 
				})
			}
		},
		onReachBottom: function() {
			this.getBrokerageRankList();
		}
	}
</script>

<style scoped lang="scss">
	.CommissionRank .header {
		background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAu4AAAFZCAMAAADw5FqoAAAC/VBMVEUAAAD////////////////////////////////+1mz7wgD+ygD////8wgD8wwD7wAD+yAD+yQD8xQD9xwD8xAD+ygD9xQD8wgD+yQD7vgD////4tQD////////5uAD6ugD7vgD////////+yAD////////////////////////////4swD5tgD////5uAD////2rwD7vQD////////9xgD////9yAD8xAD+yQD1qwD7wQD+ywD1rQD5uQD6vQD4tgD8wwD+0D/3sgD2rwD7vwD2sAD3swD3tAD2rgD2sQD/yij5uwD6uwD5vwD4tAD0qQD/nQD/yyv4uAD1sgD/uAb/+OH/zTX0sAD/0UT5vQD/zzv4uwD1tQD/zC/roADzpwD/zjj3uQDyrgDzrADzqgD/0EH/zz7qngDtowD/zDL0rgDxqgD3twD/0kjpmgD/yy3wpgDwpwDyqQD/yUr/00vvpADmlQD1twD/uQn1tADnmQDxqQDtnwDrogD/007upwDxrADvogD/wy3/ykz/xTXypQDtpQD/xTHxpAD/y07vqAD/vhrtogDpnAD/wir/wSbomADnlgD/vBXrnwDonQH/1FD/xkD/yEbkkgD/ugzvoQD/uxD/xzn4ugD/1VP6wiLrnAD/uxv/1lj/yD3/wB/snQD9zzrlkwD/yUL/y1H/xTv/x0P/wjXjkAD/wCPtpgD9zTf5vzH/uhf/vCD/wTD9xDv4uCT/+eX/113/tAD0syT/ySP9yjLyrgz+zTH/twL4uBL/++r7xSb5wjfqmQD3tR/soRHtphTwrhvwqhb/vAD/y0f1tyr5uyj9xwr/vyb0sRj/02f/1VX8xh75vRT/ogD+xhT/uhD0sQzwqQnroAn3tAr9yx37wzT7xi35vQv/zVT8ySr7whnkkQD3uy/8vyz7wgnnmgf+zkX+zSX5vBn/rgDuown+yxX//PD/yB3+yw3/xB37yT//9db7whH/8sj/7bn/2mX/xCX/4Yr/pwL/5pz/3Hj/6qv/vhCX1E/RAAAANHRSTlMAIJ9Av3/fYD8GE9HuI5OA7vdW3nKsRDfBTqvUFQ67YC65jWbYbMo4TFgw3qiY9R3t6Hhzmm/h5QAAQsBJREFUeNrs2kuKAyEUQFGn8aHlv8ys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");
		@include main_bg_color(theme);
		width: 100%;
		height: 344rpx;
		background-size: 100% 100%;
	}

	.CommissionRank .header .rank {
		font-size: 33rpx;
		color: #fff;
		position: absolute;
		top: 160rpx;
		left: 48rpx;
	}

	.CommissionRank .header .rank .num {
		font-size: 51rpx;
		font-weight: bold;
		margin: 0 10rpx;
	}

	.CommissionRank .wrapper {
		width: 710rpx;
		background-color: #fff;
		border-radius: 14rpx;
		margin: -76rpx auto 0 auto;
	}

	.CommissionRank .wrapper .nav {
		height: 99rpx;
		border-bottom: 2.5rpx solid #f3f3f3;
		font-size: 30rpx;
		font-weight: bold;
		color: #999;
		line-height: 99rpx;
	}
	.CommissionRank .wrapper .nav .item.font_color {
		@include main_color(theme);
		@include  tab_border_bottom(theme);
		margin-top: -2rpx;
	}

	.CommissionRank .wrapper .list {
		padding: 0 30rpx;
	}

	.CommissionRank .wrapper .list .item {
		border-bottom: 1px solid #f3f3f3;
		height: 101rpx;
		font-size: 28rpx;
	}

	.CommissionRank .wrapper .list .item .num {
		color: #666;
		width: 70rpx;
	}

	.CommissionRank .wrapper .list .item .num image {
		width: 34rpx;
		height: 40rpx;
		display: block;
	}

	.CommissionRank .wrapper .list .item .picTxt {
		width: 350rpx;
	}

	.CommissionRank .wrapper .list .item .picTxt .pictrue {
		width: 68rpx;
		height: 68rpx;
	}

	.CommissionRank .wrapper .list .item .picTxt .pictrue image {
		width: 100%;
		height: 100%;
		display: block;
		border-radius: 50%;
	}

	.CommissionRank .wrapper .list .item .picTxt .text {
		width: 262rpx;
		color: #333;
	}

	.CommissionRank .wrapper .list .item .people {
		width: 175rpx;
		text-align: right;
	}
	.font_color{
		@include price_color(theme);
	}
</style>
