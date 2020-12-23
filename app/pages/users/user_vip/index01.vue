<template>
	<view>
		<view class='member-center'>
			<view class='header'>
				<swiper :indicator-dots="indicatorDots" :autoplay="autoplay" :circular="circular" :interval="interval" :duration="duration"
				 @change="bindchange" previous-margin="30px" next-margin="30px">
					<block v-for="(item,index) in VipList" :key="index">
						<swiper-item>
							<view class="memberBg" :class="swiperIndex == index ? 'active' : 'quiet'" :style='"background-image:url("+item.image+")"'
							 mode='aspectFill'>
								<view class='name'>{{item.name}}</view>
								<view class='discount'>可享受商品折扣:{{item.discount}}折<text class='iconfont icon-zhekou'></text></view>
								<view class='lock' v-if="item.grade < grade"><text class='iconfont icon-xuanzhong1'></text>已解锁更高等级</view>
								
								<view class='lock' v-if="item.grade > grade"><text class='iconfont icon-quanxianguanlisuozi'></text>该会员等级尚未解锁</view>
								
								<view class='nav acea-row' v-if="grade==item.grade">
									<view class='item' v-if="indexn <= 3" v-for="(itemn,indexn) in item.task_list" :key="indexn">
										<view class='num'>{{itemn.number}}</view>
										<view>{{itemn.real_name}}</view>
									</view>
								</view>
							</view>
						</swiper-item>
					</block>
				</swiper>
			</view>
			<view class='wrapper'>
				<view class='title acea-row row-between-wrapper'>
					<view><text class='iconfont icon-jingyanzhi'></text>会员升级要求</view>
					<view class='num'><text class='current'>{{reach_count || 0}}</text>/{{task.length || 0}}</view>
				</view>
				<view class='list'>
					<view class='item' v-for="(item,index) in task" :key="index">
						<view class='top acea-row row-between-wrapper'>
							<view class='name' @click='opHelp(index)'>{{item.name}}<text v-if="item.illustrate" class='iconfont icon-wenti'></text></view>
							<view v-if="item.finish">已满足条件</view>
							<view v-else>未满足条件</view>
						</view>
						<view class="cu-progress">
							<view class='bg-red' :style="'width:'+item.speed+'%;'"></view>
						</view>
						<view class='experience acea-row row-between-wrapper'>
							<view>{{item.task_type_title}}</view>
							<view><text class='num'>{{item.new_number || 0}}</text>/{{item.number || 0}}</view>
						</view>
					</view>
				</view>
			</view>
			<recommend :hostProduct="hostProduct"></recommend>
			<view class='growthValue' :class='growthValue==false?"on":""'>
				<view class='pictrue'>
					<image src='../../columnGoods/static/value.jpg'></image><text class='iconfont icon-guanbi3' @click='growthValue'></text>
				</view>
				<view class='conter'>{{illustrate}}</view>
			</view>
			<view class='mask' :hidden='growthValue' @click='growthValueClose'></view>
		</view>
		<!-- #ifdef MP -->
		<authorize @onLoadFun="onLoadFun" :isAuto="isAuto" :isShowAuth="isShowAuth" @authColse="authColse"></authorize>
		<!-- #endif -->
	</view>
</template>

<script>
	import {
		userLevelGrade,
		userLevelTask,
		userLevelDetection
	} from '@/api/user.js';
	import {
		getProductHot
	} from '@/api/store.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	// #ifdef MP
	import authorize from '@/components/Authorize';
	// #endif
	import recommend from '@/components/recommend';
	export default {
		components: {
			recommend,
			// #ifdef MP
			authorize
			// #endif
		},
		data() {
			return {
				reach_count:0,
				VipList: [],
				indicatorDots: false,
				circular: true,
				autoplay: false,
				interval: 3000,
				duration: 500,
				swiperIndex: 0,
				growthValue: true,
				task: [], //任务列表
				illustrate: '', //任务说明
				level_id: 0, //任务id,
				hostProduct: [],
				grade: 0,
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false ,//是否隐藏授权
				hotScroll:false,
				hotPage:1,
				hotLimit:10
			};
		},
		computed: mapGetters(['isLogin']),
		watch: {
			VipList: function() {
				console.log('观察')
				let that = this;
				if (that.VipList.length > 0) {
					that.VipList.forEach(function(item, index) {
						if (item.is_clear === false) {
							// that.swiper.slideTo(index);
							that.activeIndex = index;
							that.grade = item.grade;
						}
					});
				}
			}
		},
		onLoad() {
			if (this.isLogin) {
				this.setLeveLComplete();
				this.get_host_product();
			} else {
				// #ifdef H5 || APP-PLUS
				toLogin();
				// #endif 
				// #ifdef MP
				this.isAuto = true;
				this.$set(this, 'isShowAuth', true)
				// #endif
			}
			let that = this;
			setTimeout(function() {
				that.loading = true
			}, 500)
		},
		methods: {
			onLoadFun: function() {
				this.setLeveLComplete();
				this.get_host_product();
			},
			// 授权关闭
			authColse: function(e) {
				this.isShowAuth = e
			},
			/**
			 * 获取我的推荐
			 */
			get_host_product: function() {
				let that = this;
				getProductHot().then(res => {
					let that = this;
					if(that.hotScroll) return
					getProductHot(
						that.hotPage,
						that.hotLimit,
					).then(res => {
						that.hotPage++
						that.hotScroll = res.data.length<that.hotLimit
						that.hostProduct = that.hostProduct.concat(res.data)
					});
				});
			},
			/**
			 * 会员切换
			 * 
			 */
			bindchange(e) {
				let index = e.detail.current;
				this.swiperIndex = index;
				this.level_id = this.VipList[index].id || 0;
				// this.grade = this.VipList[index].grade
				this.getTask();
			},
			/**
			 * 关闭说明
			 */
			growthValueClose: function() {
				this.growthValue = true;
			},
			/**
			 * 打开说明
			 */
			opHelp: function(index) {
				this.growthValue = false;
				this.illustrate = this.task[index].illustrate;
			},
			/**
			 * 设置会员
			 */
			setLeveLComplete: function() {
				let that = this;
				userLevelDetection().then(res => {
					that.getVipList();
				});
			},
			/**
			 * 获取会员等级
			 * 
			 */
			getVipList: function() {
				let that = this;
				userLevelGrade().then(res => {
					that.$set(that, 'VipList', res.data.list);
					that.task = res.data.task.task;
					that.reach_count = res.data.task.reach_count;
					that.level_id = res.data.list[0] ? res.data.list[0].id : 0;
					let arr = [];
					// res.data.list.forEach(function(item, index) {
					// 	if (item.is_clear == false) {
					// 		arr.push(item.grade);
					// 	}
					// })
					// that.grade = arr[0] || 0;
					// that.grade = res.data.list[0].grade
				});
			},
			/**
			 * 获取任务要求
			 */
			getTask: function() {
				let that = this;
				userLevelTask(that.level_id).then(res => {
					that.task = res.data.task;
					that.reach_count = res.data.reach_count;
				});
			}
		},
		onReachBottom() {
			this.get_host_product();
		}
	}
</script>

<style scoped lang="scss">
	.member-center .header {
		background-color: #232323;
		width: 100%;
		padding: 50rpx 0;
	}

	.member-center .header swiper {
		width: 100%;
		height: 328rpx;
		position: relative;
	}

	.member-center .header swiper-item .memberBg.active {
		transform: none;
		transition: all 0.2s ease-in 0s;
	}

	.member-center .header swiper-item .memberBg.quiet {
		transform: scale(0.9);
		transition: all 0.2s ease-in 0s;
	}

	.member-center .header swiper-item .memberBg {
		width: 100%;
		height: 328rpx;
		border-radius: 16rpx;
		color: #fff;
		position: relative;
		background-size: 100% 100%
	}

	.member-center .header swiper-item .memberBg image {
		width: 89rpx;
		height: 108rpx;
		display: block;
		position: absolute;
		right: 60rpx;
	}

	.member-center .header swiper-item .memberBg .name {
		font-size: 46rpx;
		font-weight: bold;
		padding: 40rpx 0 0 35rpx;
	}

	.member-center .header swiper-item .memberBg .discount {
		font-size: 28rpx;
		font-weight: bold;
		margin: 15rpx 0 0 35rpx;
	}

	.member-center .header swiper-item .memberBg .discount .iconfont {
		margin-left: 10rpx;
		font-size: 30rpx;
	}

	.member-center .header swiper-item .memberBg .nav {
		margin-top: 55rpx;
	}

	.member-center .header swiper-item .memberBg .nav .item {
		flex: 1;
		text-align: center;
		color: rgba(255, 255, 255, 0.6);
		position: relative;
	}

	.member-center .header swiper-item .memberBg .nav .item .num {
		font-size: 40rpx;
		color: #fff;
		font-family: 'Guildford Pro';
	}

	.member-center .header swiper-item .memberBg .nav .item~.item::before {
		position: absolute;
		width: 2rpx;
		height: 32rpx;
		background-color: rgba(255, 255, 255, 0.6);
		content: '';
		left: 0;
		top: 50%;
		transform: translateY(-50%);
	}

	.member-center .header swiper-item .memberBg .lock {
		font-size: 26rpx;
		margin: 73rpx 0 0 35rpx;
	}

	.member-center .header swiper-item .memberBg .lock .iconfont {
		font-size: 37rpx;
		margin-right: 15rpx;
		vertical-align: -4rpx;
	}

	.member-center .wrapper {
		background-color: #fff;
		padding-bottom: 16rpx;
		margin-bottom: 20rpx;
	}

	.member-center .wrapper .title {
		height: 98rpx;
		padding: 0 30rpx;
		font-size: 30rpx;
		font-weight: bold;
		color: #282828;
	}

	.member-center .wrapper .title .iconfont {
		color: #ffae06;
		font-weight: normal;
		font-size: 40rpx;
		margin-right: 12rpx;
		vertical-align: -2rpx;
	}

	.member-center .wrapper .title .num {
		font-size: 28rpx;
		color: #999;
	}

	.member-center .wrapper .title .num .current {
		color: #ffae06;
	}

	.member-center .wrapper .list .item {
		width: 690rpx;
		height: 184rpx;
		background-color: #f9f9f9;
		margin: 0 auto 20rpx auto;
		padding: 27rpx 0 22rpx 0;
		border-radius: 12rpx;
		box-sizing: border-box;
	}

	.member-center .wrapper .list .item .top {
		padding-right: 27rpx;
		font-size: 26rpx;
		color: #999;
	}

	.member-center .wrapper .list .item .top .name {
		border-left: 6rpx solid #ffae06;
		padding-left: 20rpx;
		font-size: 28rpx;
		color: #282828;
		font-weight: bold;
	}

	.member-center .wrapper .list .item .top .name .iconfont {
		color: #999;
		font-size: 30rpx;
		vertical-align: -2rpx;
		margin-left: 10rpx;
	}

	.member-center .wrapper .list .item .cu-progress {
		overflow: hidden;
		height: 12rpx;
		background-color: #eee;
		width: 636rpx;
		border-radius: 20rpx;
		margin: 35rpx auto 0 auto;
	}

	.member-center .wrapper .list .item .cu-progress .bg-red {
		width: 0;
		height: 100%;
		transition: width 0.6s ease;
		background-color: #ffaa29;
		border-radius: 20rpx;
	}

	.member-center .wrapper .list .item .experience {
		margin-top: 17rpx;
		padding: 0 27rpx;
		font-size: 24rpx;
		color: #999;
	}

	.member-center .wrapper .list .item .experience .num {
		color: #ffad07;
	}

	.member-center .growthValue {
		background-color: #fff;
		border-radius: 16rpx;
		position: fixed;
		top: 266rpx;
		left: 50%;
		width: 560rpx;
		height: 740rpx;
		margin-left: -280rpx;
		z-index: 99;
		transform: translate3d(0, -200%, 0);
		transition: all .3s cubic-bezier(.25, .5, .5, .9);
	}

	.member-center .growthValue.on {
		transform: translate3d(0, 0, 0);
	}

	.member-center .growthValue .pictrue {
		width: 100%;
		height: 257rpx;
		position: relative;
	}

	.member-center .growthValue .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 16rpx 16rpx 0 0;
	}

	.member-center .growthValue .conter {
		padding: 0 35rpx;
		font-size: 30rpx;
		color: #333;
		margin-top: 58rpx;
		line-height: 1.5;
		height: 350rpx;
		overflow: auto;
	}

	.member-center .growthValue .pictrue .iconfont {
		position: absolute;
		font-size: 65rpx;
		color: #fff;
		top: 775rpx;
		left: 50%;
		transform: translateX(-50%);
	}
</style>