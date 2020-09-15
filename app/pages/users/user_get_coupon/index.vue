<template>
	<view>
		<view class='coupon-list' v-if="couponsList.length">
			<view class='item acea-row row-center-wrapper' v-for="(item,index) in couponsList" :key="index">
				<view class='money' :class='item.isUse ? "moneyGray" : "" '>
					<view>￥<text class='num'>{{item.money}}</text></view>
					<view class="pic-num">满{{item.minPrice}}元可用</view>
				</view>
				<view class='text'>
					<view class='condition line1'>
					    <span class='line-title' :class='(item.isUse==true || item.isUse==2)?"gray":""' v-if='item.type===0'>通用劵</span>
					    <span class='line-title' :class='(item.isUse==true || item.isUse==2)?"gray":""' v-else-if='item.type===1'>品类券</span>
					    <span class='line-title' :class='(item.isUse==true || item.isUse==2)?"gray":""' v-else>商品券</span>
					    <span>{{item.name}}</span>
					</view>
					<view class='data acea-row row-between-wrapper'>
						<view>{{ item.receiveStartTime ? item.receiveStartTime + "-" : ""}}{{ item.receiveEndTime }}</view>
						<view class='bnt gray' v-if="item.isUse==true">已领取</view>
						<view class='bnt bg-color' v-else @click='getCoupon(item.id,index)'>立即领取</view>
					</view>
				</view>
			</view>
		</view>
		<view class='loadingicon acea-row row-center-wrapper' v-if="couponsList.length">
		     <text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
		  </view>
		<view class='noCommodity' v-else-if="!couponsList.length && loading==true">
			<view class='pictrue'>
				<image src='../../../static/images/noCoupon.png'></image>
			</view>
		</view>
		<!-- #ifdef MP -->
		<authorize @onLoadFun="onLoadFun" :isAuto="isAuto" :isShowAuth="isShowAuth" @authColse="authColse"></authorize>
		<!-- #endif -->
	</view>
</template>

<script>
	import {
		getCoupons,
		setCouponReceive
	} from '@/api/api.js';
	import {
		toLogin
	} from '@/libs/login.js';
	 import {
	 	mapGetters
	 } from "vuex";
	 // #ifdef MP
	 import authorize from '@/components/Authorize';
	 // #endif
		export default {
			components: {
				// #ifdef MP
				authorize
				// #endif
			},
			data() {
				return {
					couponsList: [],
					loading: false,
					loadend: false,
					loadTitle: '加载更多',//提示语
					page: 1,
					limit: 20,
					isAuto: false, //没有授权的不会自动授权
					isShowAuth: false //是否隐藏授权
				};
			},
			computed: mapGetters(['isLogin']),
			onLoad(){
				if(this.isLogin){
					// #ifdef H5
					this.getUseCoupons();
					// #endif
				}else{
					// #ifdef H5 || APP-PLUS
					toLogin();
					// #endif 
					// #ifdef MP
					this.isAuto = true;
					this.$set(this,'isShowAuth',true)
					// #endif
				}
			},
			 /**
			   * 页面上拉触底事件的处理函数
			   */
			  onReachBottom: function () {
			    this.getUseCoupons();
			  },
			methods: {
				onLoadFun(){
					this.getUseCoupons();
				},
				// 授权关闭
				authColse:function(e){
					this.isShowAuth = e
				},
				 getCoupon:function(id,index){
				    let that = this;
				    let list = that.couponsList;
					let ids = [];
					ids.push(id);
				    //领取优惠券
				    setCouponReceive(ids).then(function (res) {
				      list[index].isUse = true;
					  that.$set(that,'couponsList',list);
				      that.$util.Tips({ title: '领取成功' });
				    },function(res){
				      return that.$util.Tips({title:res});
				    })
				  },
				   /**
				     * 获取领取优惠券列表
				    */
				    getUseCoupons:function(){
				      let that=this
				      if(this.loadend) return false;
				      if(this.loading) return false;
				      getCoupons({ page: that.page, limit: that.limit }).then(res=>{
				        let list=res.data,loadend=list.length < that.limit;
				        let couponsList = that.$util.SplitArray(list, that.couponsList);
						that.$set(that,'couponsList',couponsList);
						that.loadend = loadend;
						that.loadTitle = loadend ? '我也是有底线的' : '加载更多';
						that.page = that.page + 1;
						that.loading = false;
				      }).catch(err=>{
						  that.loading = false;
						  that.loadTitle = '加载更多';
				      });
				    }
			}
		}
</script>

<style scoped>
	.condition .line-title{
	  width:90rpx;
	  padding: 0 10rpx;
	  box-sizing: border-box;
	  background:rgba(255,247,247,1);
	  border:1px solid rgba(232,51,35,1);
	  opacity:1;
	  border-radius:20rpx;
	  font-size:20rpx;
	  color: #E83323;
	  margin-right: 12rpx;
	}
	.condition .line-title.gray{
	  border-color:#BBB;
	  color:#bbb;
	  background-color:#F5F5F5;
	}
	.coupon-list .pic-num{
	  color: #FFFFFF;
	  font-size: 24rpx;
	}
</style>
