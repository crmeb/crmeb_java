<template>
	<view class='sharing-packets' :style="{ top: top + 'px'}"
	:class="sharePacket.touchstart?'hide_left':'' "
	 @touchmove.stop.prevent="setTouchMove" @click="handleleterClick()" v-if="!sharePacket.isState">
	    <view class='sharing-con' :style="{backgroundImage:'url('+imgHost+ '/' + picBg+')'}">
	        <view class='text' >
				<view class="main_color">会员分享返</view>
				<view class='money price'><text class='label'>￥</text>{{sharePacket.priceName}}</view>
				<view class='tip'>下单即返佣金</view>
				<view class='shareBut'>立即分享</view>
	      </view>
	    </view>  
	</view>
</template>
<!--  -->
<script>
	import { getImageDomain } from '@/api/api.js'
	export default {
		props: {
			sharePacket: {
				type: Object,
				default: function(){
					return {isState: true,priceName:'',touchstart:false}
				}
			}
		},
		data() {
			return {
				imgHost:'',
				picBg:'crmebimage/perset/share_tip/share_tip1.png',
				top: "260",
			};
		},
		created(){
			let that = this;
			uni.getStorage({
			    key: 'theme',
			    success: function (res) {
					switch (res.data) {
						case 'theme1':
							that.picBg = 'crmebimage/perset/share_tip/share_tip1.png';
							break;
						case 'theme2':
							that.picBg = 'crmebimage/perset/share_tip/share_tip2.png';
							break;
						case 'theme3':
							that.picBg = 'crmebimage/perset/share_tip/share_tip3.png';
							break;
						case 'theme4':
							that.picBg = 'crmebimage/perset/share_tip/share_tip4.png';
							break;
						case 'theme5':
							that.picBg = 'crmebimage/perset/share_tip/share_tip5.png';
							break;
					}
			    }
			});
			getImageDomain().then(res=>{
				that.$set(that,'imgHost',res.data);
			})
		},
		methods: {
			goShare:function(){
				this.$emit('listenerActionSheet');
			},
			setTouchMove(e) {
				var that = this;
				if (e.touches[0].clientY < 545 && e.touches[0].clientY > 66) {
					that.top = e.touches[0].clientY
				}
			},
			handleleterClick(){
				if(this.sharePacket.touchstart){
					this.$emit('showShare',false)
				}else{
					// this.$emit('showShare',true)
					this.goShare()
				}
			}
		},
	}
</script>

<style scoped lang="scss">
	.sharing-packets{
		position:fixed;left:30rpx;z-index:99;transition: all .2s linear;
		&.hide_left{
			transition: all .2s linear;left: -110rpx;
			transform: scale(.6);
		}
	}
	.sharing-packets .iconfont{width:44rpx;height:44rpx;border-radius:50%;text-align:center;line-height:44rpx;background-color:#999;font-size:20rpx;color:#fff;margin:0 auto;box-sizing:border-box;padding-left:1px;}
	.sharing-packets .line{width:2rpx;height:40rpx;background-color:#999;margin:0 auto;}
	.sharing-packets .sharing-con{width:187rpx;height:210rpx;position:relative;background-size: cover;}
	.sharing-packets .sharing-con .text{position:absolute;top:30rpx;font-size:20rpx;width:100%;text-align:center;}
	.sharing-packets .sharing-con .text .money{font-size:32rpx;font-weight:bold;margin-top:5rpx;}
	.sharing-packets .sharing-con .text .money .label{font-size:20rpx;}
	.sharing-packets .sharing-con .text .tip{font-size:18rpx;color:#999;margin-top:5rpx;}
	.sharing-packets .sharing-con .text .shareBut{font-size:22rpx;color:#fff;margin-top:28rpx;height:50rpx;line-height:50rpx;}
	.main_color{@include main_color(theme);}
	.price{@include price_color(theme);}
</style>
