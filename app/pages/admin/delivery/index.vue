<template>
	<view class="deliver-goods">
		<header>
			<view class="order-num acea-row row-between-wrapper">
				<view class="num line1">订单号：{{ order_id }}</view>
				<view class="name line1">
					<span class="iconfont icon-yonghu2"></span>{{ delivery.nickname }}
				</view>
			</view>
			<view class="address">
				<view class="name">
					{{ delivery.real_name
          }}<span class="phone">{{ delivery.user_phone }}</span>
				</view>
				<view>{{ delivery.user_address }}</view>
			</view>
			<view class="line"><image src="@/static/images/line.jpg" /></view>
		</header>
		<view class="wrapper">
			<view class="item acea-row row-between-wrapper">
				<view>发货方式</view>
				<view class="mode acea-row row-middle row-right">
					<view class="goods" :class="active === index ? 'on' : ''" v-for="(item, index) in types" :key="index" @click="changeType(item, index)">
						{{ item.title }}<span class="iconfont icon-xuanzhong2"></span>
					</view>
				</view>
			</view>
			<block v-if="logistics.length>0">
				<view class="list" v-show="active === 0">
					<view class="item acea-row row-between-wrapper">
						<view>发货方式</view>
						<view class="select-box">
							<picker class="pickerBox" @change="bindPickerChange" :value="seIndex" :range="logistics" range-key="name">
								<!-- <view></view> -->
								<view class="uni-input">{{logistics[seIndex].name}}</view>
							</picker>
						</view>
					</view>
					<view class="item acea-row row-between-wrapper">
						<view>快递单号</view>
						<input type="text" placeholder="填写快递单号" v-model="delivery_id" class="mode" />
					</view>
				</view>
			</block>
			
			<view class="list" v-show="active === 1">
				<view class="item acea-row row-between-wrapper">
					<view>送货人</view>
					<input type="text" placeholder="填写送货人" v-model="delivery_name" class="mode" />
				</view>
				<view class="item acea-row row-between-wrapper">
					<view>送货电话</view>
					<input type="text" placeholder="填写送货电话" v-model="delivery_id" class="mode" />
				</view>
			</view>
		</view>
		<view style="height:1.2rem;"></view>
		<view class="confirm" @click="saveInfo">确认提交</view>
	</view>
</template>
<script>
	import {
		getAdminOrderDelivery,
		setAdminOrderDelivery,
		getLogistics
	} from "@/api/admin";
	import { checkPhone } from '@/utils/validate.js'
	export default {
		name: "GoodsDeliver",
		components: {},
		props: {},
		data: function() {
			return {
				types: [{
						type: "express",
						title: "发货"
					},
					{
						type: "send",
						title: "送货"
					},
					{
						type: "fictitious",
						title: "无需发货"
					}
				],
				active: 0,
				order_id: "",
				delivery: [],
				logistics: [],
				delivery_type: "express",
				delivery_name: "",
				delivery_id: "",
				seIndex:0,
			};
		},
		watch: {
			"$route.params.oid": function(newVal) {
				let that = this;
				if (newVal != undefined) {
					that.order_id = newVal;
					that.getIndex();
				}
			}
		},
		onLoad: function(option) {
			this.order_id = option.id;
			this.getIndex();
			this.getLogistics();
		},
		methods: {
			changeType: function(item, index) {
				this.active = index;
				this.delivery_type = item.type;
				this.delivery_name = "";
				this.delivery_id = "";
			},
			getIndex: function() {
				let that = this;
				getAdminOrderDelivery(that.order_id).then(
					res => {
						that.delivery = res.data;
					},
					error => {
						that.$dialog.error(error.msg);
					}
				);
			},
			getLogistics: function() {
				let that = this;
				getLogistics().then(
					res => {
						that.logistics = res.data;
					},
					error => {
						that.$dialog.error(error.msg);
					}
				);
			},
			async saveInfo() {
				let that = this,
					delivery_type = that.delivery_type,
					delivery_name = that.logistics[that.seIndex].name,
					delivery_id = that.delivery_id,
					userName = that.delivery_name,
					save = {};
				save.order_id = that.order_id;
				save.delivery_type = that.delivery_type;
				switch (delivery_type) {
					case "send":
						if(!userName){
							return that.$util.Tips({
								title:'请填写送货人姓名'
							})
						}
						if(!delivery_id || !checkPhone(delivery_id)){
							return that.$util.Tips({
								title:'请填写正确的手机号码'
							})
						}
						save.delivery_name = userName;
						save.delivery_id = delivery_id;
						that.setInfo(save);
						break;
					case "express":
						if(!delivery_id){
							return this.$util.Tips({
								title:'请填写快递单号'
							})
						}	
						save.delivery_name = delivery_name;
						save.delivery_id = delivery_id;
						that.setInfo(save);
						break;
					case "fictitious":
						that.setInfo(save);
						break;
				}
			},
			setInfo: function(item) {
				let that = this;
				console.log(item);
				setAdminOrderDelivery(item).then(
					res => {
						that.$util.Tips({
							title:res.msg,
							icon:'success',
							mask:true
						})
						setTimeout(res=>{
							uni.navigateBack();
						},2000)
					},
					error => {
						that.$dialog.error(error.msg);
					}
				);
			},
			bindPickerChange(e){
				console.log(e,'tar')
				this.seIndex = e.detail.value
			}
		}
	};
</script>

<style lang="scss">
	/*发货*/
	.deliver-goods header {
		width: 100%;
		background-color: #fff;
		margin-top: 10upx;
	}

	.deliver-goods header .order-num {
		padding: 0 30upx;
		border-bottom: 1px solid #f5f5f5;
		height: 67upx;
	}

	.deliver-goods header .order-num .num {
		width: 430upx;
		font-size: 26upx;
		color: #282828;
		position: relative;
	}

	.deliver-goods header .order-num .num:after {
		position: absolute;
		content: '';
		width: 1px;
		height: 30upx;
		background-color: #ddd;
		top: 50%;
		margin-top: -15upx;
		right: 0;
	}

	.deliver-goods header .order-num .name {
		width: 260upx;
		font-size: 26upx;
		color: #282828;
		text-align: center;
	}

	.deliver-goods header .order-num .name .iconfont {
		font-size: 35upx;
		color: #477ef3;
		vertical-align: middle;
		margin-right: 10upx;
	}

	.deliver-goods header .address {
		font-size: 26upx;
		color: #868686;
		background-color: #fff;
		padding: 30upx;
	}

	.deliver-goods header .address .name {
		font-size: 34upx;
		color: #282828;
		margin-bottom: 10upx;
	}

	.deliver-goods header .address .name .phone {
		margin-left: 40upx;
	}

	.deliver-goods header .line {
		width: 100%;
		height: 3upx;
	}

	.deliver-goods header .line image {
		width: 100%;
		height: 100%;
		display: block;
	}

	.deliver-goods .wrapper {
		width: 100%;
		background-color: #fff;
	}

	.deliver-goods .wrapper .item {
		border-bottom: 1px solid #f0f0f0;
		padding: 0 30upx;
		height: 96upx;
		font-size: 32upx;
		color: #282828;
		position: relative;
	}

	.deliver-goods .wrapper .item .mode {
		width: 460upx;
		height: 100%;
		text-align: right;
	}

	.deliver-goods .wrapper .item .mode .iconfont {
		font-size: 30upx;
		margin-left: 13upx;
	}

	.deliver-goods .wrapper .item .mode .goods~.goods {
		margin-left: 30upx;
	}

	.deliver-goods .wrapper .item .mode .goods {
		color: #bbb;
	}

	.deliver-goods .wrapper .item .mode .goods.on {
		color: #477ef3;
	}

	.deliver-goods .wrapper .item .icon-up {
		position: absolute;
		font-size: 35upx;
		color: #2c2c2c;
		right: 30upx;
	}

	.deliver-goods .wrapper .item select {
		direction: rtl;
		padding-right: 60upx;
		position: relative;
		z-index: 2;
	}

	.deliver-goods .wrapper .item input::placeholder {
		color: #bbb;
	}

	.deliver-goods .confirm {
		font-size: 32upx;
		color: #fff;
		width: 100%;
		height: 100upx;
		background-color: #477ef3;
		text-align: center;
		line-height: 100upx;
		position: fixed;
		bottom: 0;
	}
	.select-box{
		flex: 1;
		height: 100%;
		
		.pickerBox{
			display: flex;
			align-items: center;
			justify-content: flex-end;
			width: 100%;
			height: 100%;
		}
	}
	
</style>
