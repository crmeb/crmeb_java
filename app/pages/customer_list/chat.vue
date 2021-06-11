<template>
	<div class="broadcast-details" :style="'height:'+windowH+'px'">
		<view class="hd-wrapper" :class="active === true ? 'on' : ''">
			<scroll-view scroll-y="true" style="height: 100%; overflow: hidden;" :scroll-top="scrollTop" scroll-with-animation="true">
				<div class="broadcast-details_order">
					<div class="broadcast-details_box" v-if="productId && productInfo.id">
						<div class="broadcast_details_img">
							<img :src="productInfo.image" />
						</div>
						<div class="broadcast_details_picBox">
							<div class="broadcast_details_tit" v-text="productInfo.storeName"></div>
							<div class="acea-row row-between">
								<div class="broadcast_details_pic">
									￥{{ productInfo.price
									}}<span class="broadcast_details_pic_num">￥{{ productInfo.otPrice }}</span>
								</div>
								<div class="broadcast_details_btn" @click="sendProduct">
									发送客服
								</div>
							</div>
						</div>
					</div>
					<div class="broadcast_box" v-if="orderId && orderInfo.id">
						<div class="broadcast-details_num broadcast_num">
							<span>订单号：{{ orderInfo.order_id }}</span>
							<span>{{ orderInfo.add_time_y }} {{ orderInfo.add_time_h }}</span>
						</div>
						<div class="broadcast-details_box">
							<div class="broadcast_details_img">
								<img :src="cartInfo.productInfo.image" />
								<div class="broadcast_details_model">
									{{ orderInfo.cartInfo ? orderInfo.cartInfo.length : 0 }}件商品
								</div>
							</div>
							<div class="broadcast_details_picBox">
								<div class="broadcast_details_tit">
									{{ cartInfo.productInfo.store_name }}
								</div>
								<div class="acea-row row-between">
									<div class="broadcast_details_pic">
										￥{{ cartInfo.productInfo.price
										}}<span class="broadcast_details_pic_num">￥{{ cartInfo.productInfo.ot_price }}</span>
									</div>
									<div class="broadcast_details_btn" @click="sendOrder">
										发送客服
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="chat" ref="chat" >
					<template v-for="item in history">
						<div class="item acea-row row-top" v-if="item.uid === toUid" :key="item.id">
							<div class="pictrue"><img :src="item.avatar" /></div>
							<div class="text">
								<div class="name">{{ item.nickname }}</div>
								<div class="acea-row">
									<!--商品链接-->
									<div v-if="item.msn_type === 6 && item.orderInfo.id">
										<router-link class="broadcast-details_num" :to="{
			                path: '/pages/admin/orderDetail/index?id=' + item.orderInfo.order_id
			              }">
											<span>订单号：{{ item.orderInfo.order_id }}</span>
										</router-link>
										<div class="conter acea-row row-middle">
											<div class="broadcast-details_order noPad" v-for="(val, inx) in item.orderInfo.cartInfo" :key="val.id">
												<router-link class="broadcast-details_box noPad" :to="{ path: '/pages/goods_details/index?id=' + val.product_id }" v-if="inx == 0">
													<div class="broadcast_details_img">
														<img :src="val.productInfo.image" />
														<div class="broadcast_details_model">
															{{
			                        item.orderInfo.cartInfo
			                          ? item.orderInfo.cartInfo.length
			                          : 0
			                      }}件商品
														</div>
													</div>
													<div class="broadcast_details_picBox noPad">
														<div class="broadcast_details_tit" v-text="val.productInfo.store_name"></div>
														<div class="broadcast_details_pic">
															￥{{ val.productInfo.price }}
														</div>
													</div>
												</router-link>
											</div>
										</div>
									</div>

									<!--商品链接-->
									<div class="conter acea-row row-middle" v-if="item.msn_type === 5">
										<div class=" noPad">
											<router-link class="acea-row row-column-around noPad" v-if="item.productInfo.id" :to="{ path: '/pages/goods_details/index?id=' + item.productInfo.id }">
												<div class="broadcast_details_img_no">
													<img :src="item.productInfo.image" />
												</div>
												<div class="broadcast_details_picBox_no noPad">
													<div class="broadcast_details_pic">
														￥{{ item.productInfo.price }}
													</div>
													<div class="broadcast_details_tit_no" v-text="item.productInfo.store_name"></div>
												</div>
											</router-link>
										</div>
									</div>

									<div class="conter acea-row row-middle" v-if="item.msn_type === 4">
										<img src="/static/images/signal2.gif" class="signal" style="margin-right: 0.27rem;" />12’’
									</div>

									<div class="conter acea-row row-middle" v-if="item.msn_type === 3">
										<img :src="item.msn" />
									</div>

									<div class="conter acea-row row-middle" v-if="item.msn_type === 2">
										<i class="em" :class="item.msn"></i>
									</div>

									<div class="conter acea-row row-middle" v-if="item.msn_type === 1">
										{{ item.msn }}
									</div>
								</div>
							</div>
						</div>

						<div class="item acea-row row-top row-right" v-else :key="item.id">
							<div class="text textR">
								<div class="name">{{ item.nickname }}</div>
								<div class="acea-row ">
									<!--商品链接-->
									<router-link v-if="item.msn_type === 6 && item.orderInfo.id" :to="{
			              path: '/pages/admin/orderDetail/index?id=' + item.orderInfo.order_id
			            }">
										<div class="broadcast-details_num">
											<span>订单号：{{ item.orderInfo.order_id }}</span>
										</div>
										<div class="conter acea-row row-middle">
											<div class="broadcast-details_order noPad" v-for="(val, inx) in item.orderInfo.cartInfo" :key="val.id">
												<div class="broadcast-details_box noPad" v-if="inx == 0">
													<div class="broadcast_details_img">
														<img :src="val.productInfo.image" />
														<div class="broadcast_details_model">
															{{
			                        item.orderInfo.cartInfo
			                          ? item.orderInfo.cartInfo.length
			                          : 0
			                      }}件商品
														</div>
													</div>
													<div class="broadcast_details_picBox noPad">
														<div class="broadcast_details_tit" v-text="val.productInfo.store_name"></div>
														<div class="broadcast_details_pic">
															￥{{ val.productInfo.price }}
														</div>
													</div>
												</div>
											</div>
										</div>
									</router-link>

									<!--商品链接-->
									<div class="conter acea-row row-middle" v-if="item.msn_type === 5">
										<div class=" noPad">
											<router-link class="acea-row row-column-around noPad" v-if="item.productInfo.id" :to="{ path: '/pages/goods_details/index?id=' + item.productInfo.id }">
												<div class="broadcast_details_img_no">
													<img :src="item.productInfo.image" />
												</div>
												<div class="broadcast_details_picBox_no noPad">
													<div class="broadcast_details_pic">
														￥{{ item.productInfo.price }}
													</div>
													<div class="broadcast_details_tit_no" v-text="item.productInfo.store_name"></div>
												</div>
											</router-link>
										</div>
									</div>

									<div class="conter acea-row row-middle" v-if="item.msn_type === 3">
										<img :src="item.msn" />
									</div>

									<div class="conter acea-row row-middle" v-if="item.msn_type === 2">
										<i class="em" :class="item.msn"></i>
									</div>

									<div class="conter acea-row row-middle" v-if="item.msn_type === 1">
										{{ item.msn }}
									</div>
								</div>
							</div>
							<div class="pictrue"><img :src="item.avatar" /></div>
						</div>
					</template>
				</div>
				<div :style=" active === true
			      ? 'height:' + footerConH + 'rem;'
			      : 'height:' + footerH + 'rem;'
			  "></div>
			</scroll-view>	
		</view>
		<div class="footerCon" :class="active === true ? 'on' : ''" :style="'transform: translate3d(0,' + percent + '%,0);'" ref="footerCon">
			<form>
				<div class="footer acea-row row-between row-bottom" ref="footer">
					<!--<img-->
					<!--:src="-->
					<!--voice === true-->
					<!--? require('@assets/images/keyboard.png')-->
					<!--: require('@assets/images/voice.png')-->
					<!--"-->
					<!--@click="voiceBnt"-->
					<!--/>-->

					<img @click="uploadImg" src="/static/images/plus.png" />
					<img :src="
              active === true
                ? '/static/images/keyboard.png'
                : '/static/images/face.png'
            "
					 @click="emoticon" />
					<div class="voice acea-row row-center-wrapper" v-if="voice" @touchstart.prevent="start" @touchmove.prevent="move"
					 @touchend.prevent="end">
						{{ speak }}
					</div>
					<!-- <p contenteditable="true" class="input" ref="input" v-show="!voice" @keydown="keydown($event)" @keyup="keyup" @focus="focus"></p> -->
					<input type="text" placeholder="请输入内容" class="input" ref="input" v-show="!voice" @input="bindInput" @keyup="keyup"
					 @focus="focus" cursor-spacing="20" v-model="textCon">
					<div class="send" :class="sendColor === true ? 'font-color-red' : ''" @click="sendTest">
						发送
					</div>
				</div>
			</form>
			<div class="banner slider-banner">
				<swiper class="swiper-wrapper" :autoplay="autoplay" :circular="circular" :interval="interval" :duration="duration"
				 v-if="emojiGroup.length > 0">
					<block v-for="(emojiList, index) in emojiGroup" :key="index">
						<swiper-item>
							<i class="em" :class="emoji" v-for="emoji in emojiList" :key="emoji" @click="addEmoji(emoji)"></i>
							<img src="/static/images/del.png" class="emoji-outer" />
						</swiper-item>
					</block>


					<!-- <swiper-slide class="swiper-slide acea-row" v-for="(emojiList, index) in emojiGroup" :key="index">
						<i class="em" :class="emoji" v-for="emoji in emojiList" :key="emoji" @click="addEmoji(emoji)"></i>
						<img src="/static/images/del.png" class="emoji-outer" />
					</swiper-slide>
					<div class="swiper-pagination" slot="pagination"></div> -->
				</swiper>
			</div>
		</div>
		<div class="recording" v-if="recording">
			<img src="/static/images/recording.png" />
		</div>
		<home></home>
	</div>
</template>
<script>
	import emojiList from "@/utils/emoji";
	import Socket from "@/libs/chat";
	import {
		getChatRecord
	} from "@/api/user";
	import {
		getProductDetail
	} from "@/api/store";
	import {
		getOrderDetail
	} from "@/api/order";
	import easyUpload from '@/components/easy-upload/easy-upload.vue'
	import {
		TOKENNAME,
		HTTP_REQUEST_URL
	} from '@/config/app.js';
	import home from '@/components/home';

	const chunk = function(arr, num) {
		num = num * 1 || 1;
		var ret = [];
		arr.forEach(function(item, i) {
			if (i % num === 0) {
				ret.push([]);
			}
			ret[ret.length - 1].push(item);
		});
		return ret;
	};

	const NAME = "CustomerService";

	export default {
		name: NAME,
		components: {
			easyUpload,
			home
		},
		props: {
			couponList: {
				type: Array,
				default: () => []
			}
		},
		data: function() {
			return {
				url: `${HTTP_REQUEST_URL}/api/upload/image`,
				headers: {
					"Authori-zation": "Bearer " + this.$store.state.app.token
				},
				emojiGroup: chunk(emojiList, 20),
				active: false,
				voice: false,
				speak: "按住 说话",
				recording: false,
				swiperOption: {
					pagination: {
						el: ".swiper-pagination",
						clickable: true
					},
					speed: 1000,
					observer: true,
					observeParents: true
				},
				percent: 0,
				footerConH: 0,
				footerH: 1.08,
				socket: null,
				toUid: 0,
				page: 1,
				limit: 30,
				loading: false,
				loaded: false,
				history: [],
				sendColor: false,
				sendtxt: "",
				productId: 0,
				productInfo: {},
				orderId: "",
				orderInfo: {},
				cartInfo: {},
				autoplay: false,
				circular: true,
				interval: 3000,
				duration: 500,
				upload_max: 2, //图片大小
				//上传的图片地址
				uploadImages: [],
				//展示的图片地址
				uploads: [],
				// 超出限制数组
				exceeded_list: [],
				windowH: 0,
				isBQ: false,
				scrollTop:0 ,//滚动距离
				textCon:'' //文字
			};
		},
		watch: {
			$route(n) {
				if (n.name === NAME) {
					if (n.params.productId) this.productId = n.params.productId;
					else this.productId = 0;

					if (n.query.orderId) this.orderId = n.query.orderId;
					else this.orderId = "";
				}
			}
		},
		beforeDestroy() {
			this.socket && this.socket.close();
		},
		onLoad(option) {
			let self = this
			this.toUid = option.uid || 0;
			this.productId = parseInt(option.productId) || 0;
			this.orderId = option.orderId || ""
			uni.getSystemInfo({
				success: function(res) {
					self.windowH = res.windowHeight
				}
			})
		},
		onReady: function() {
			this.height();
			this.getHistory();
			this.getproductInfo();
			this.getOrderInfo();
			this.socket = new Socket();
			this.socket.vm(this);
			this.$on(["reply", "chat"], data => {
				this.history.push(data);
				this.$nextTick(function() {
					window.scrollTo(0, document.documentElement.scrollHeight + 999);
				});
			});
			this.$on("socket_error", () => {
				this.$dialog.error("连接失败");
			});
			this.$on("err_tip", data => {
				this.$dialog.error(data);
			});
			this.$on("socket_open", () => {
				this.socket.send({
					data: {
						id: this.toUid
					},
					type: "to_chat"
				});
			});
			document.addEventListener("scroll", this.scroll, false);
			
		},
		destroyed() {
			document.removeEventListener("scroll", this.scroll);
		},
		methods: {
			uploadImg() {
				let self = this
				uni.chooseImage({
					count: 1, //默认1
					sizeType: ['original', 'compressed'], //可以指定是原图还是压缩图，默认二者都有
					sourceType: ['album', 'camera'], //从相册选择
					success: (res) => {
						for (let i = 0; i < res.tempFiles.length; i++) {
							if (Math.ceil(res.tempFiles[i].size / 1024) < this.upload_max * 1024) {
								this.uploads.push(res.tempFiles[i].path)
								this.uploadImages.push(res.tempFiles[i].path)
							} else {
								this.exceeded_list.push(i === 0 ? 1 : i + 1);
								uni.showModal({
									title: '提示',
									content: `第${[...new Set(this.exceeded_list)].join(',')}张图片超出限制${this.upload_max}MB,已过滤`
								});
							}
						}
						uni.uploadFile({
							url: self.url, //仅为示例，非真实的接口地址
							filePath: self.uploadImages[0],
							name: 'file',
							header: {
								"Authori-zation": "Bearer " + self.$store.state.app.token
							},
							//请求参数
							success: (uploadFileRes) => {
								let data = JSON.parse(uploadFileRes.data)
								self.sendMsg(data.data.url, 3)
							}
						});
					},
					fail: (err) => {
						uni.showModal({
							content: JSON.stringify(err)
						});
					}
				});
			},
			getOrderInfo() {
				if (!this.orderId) return;
				getOrderDetail(this.orderId).then(res => {
					this.orderInfo = res.data;
					if (this.orderInfo.add_time_h) {
						this.orderInfo.add_time_h = this.orderInfo.add_time_h.substring(
							0,
							this.orderInfo.add_time_h.lastIndexOf(":")
						);
					}
					if (this.orderInfo.cartInfo.length) {
						this.cartInfo = this.orderInfo.cartInfo[0];
					}
				});
			},
			getproductInfo() {
				let that = this;
				if (!this.productId) return;
				getProductDetail(this.productId).then(res => {
					that.productInfo = res.data.storeInfo;
				});
			},
			scroll() {
				if (window.scrollY < 300 && !this.loaded && !this.loading)
					this.getHistory();
			},
			imageuploaded(res) {
				if (res.status !== 200) return this.$dialog.error(res || "上传图片失败");
				this.sendMsg(res.data.url, 3);
			},
			getHistory() {
				if (this.loading || this.loaded) return;
				this.loading = true;
				getChatRecord(this.toUid, {
						page: this.page,
						limit: this.limit
					})
					.then(({
						data
					}) => {
						this.history = data.list.concat(this.history);
						if (this.page === 1) {
							this.$nextTick(function() {
								window.scrollTo(0, document.documentElement.scrollHeight + 999);
								this.height();
							});
						}
						this.page++;
						this.loading = false;
						this.loaded = data.length < this.limit;
					})
					.catch(err => {
						this.$dialog.error(err || "加载失败");
					});
			},
			focus: function() {
				this.active = false;
			},
			keyup: function() {
				if (this.$refs.input.innerHTML.length > 0) {
					this.sendColor = true;
				} else {
					this.sendColor = false;
				}
			},
			addEmoji(name) {
				this.sendMsg(name, 2);
			},
			sendMsg(msn, type) {
				this.height();
				this.socket.send({
					data: {
						msn,
						type,
						to_uid: this.toUid
					},
					type: "chat"
				});
			},
			sendTest() {
				this.sendMsg(this.textCon, 1);
				this.textCon = ''
				this.height();
			},
			sendProduct() {
				this.sendMsg(this.productId, 5);
				this.productId = 0;
				this.productInfo = {};
			},
			sendOrder() {
				this.sendMsg(this.orderId, 6);
				this.orderId = 0;
				this.orderInfo = {};
			},
			bindInput: function(e) {
				if(e.detail.value){
					this.sendColor = true
				}else{
					this.sendColor = false
				}
				this.height();
			},
			start() {
				var that = this;
				this.longClick = 0;
				this.timeOutEvent = setTimeout(function() {
					that.longClick = 1;
				}, 500);
				that.speak = "松开 结束";
				that.recording = true;
			},
			move() {
				clearTimeout(this.timeOutEvent);
				this.timeOutEvent = 0;
			},
			end() {
				clearTimeout(this.timeOutEvent);
				if (this.timeOutEvent !== 0 && this.longClick === 0) {
					//点击
					//此处为点击事件----在此处添加跳转详情页
				}
				this.speak = "按住 说话";
				this.recording = false;
				return false;
			},
			voiceBnt: function() {
				this.active = false;
				if (this.voice === true) {
					this.voice = false;
					this.$nextTick(function() {
						this.$refs.input.focus();
					});
				} else {
					this.voice = true;
				}
				window.scrollTo(0, document.documentElement.scrollHeight);
				this.percent = 0;
				this.footerConH = 0;
				this.footerH = 0;
				this.$nextTick(function() {
					this.height();
				});
			},
			emoticon: function() {
				this.voice = false;
				if (this.active === true) {
					this.active = false;
					this.isBQ = false
					// this.$nextTick(function() {
					// 	this.$refs.input.focus();
					// });
				} else {
					this.active = true;
					this.isBQ = true
					// this.$nextTick(function() {
					// 	this.$refs.input.blur();
					// });
				}
				this.height();
			},
			height() {
				let scrollTop = 0
				let footerConH = this.$refs.footerCon.offsetHeight;
				let footerH = this.$refs.footer.offsetHeight;
				let scale = 750 / window.screen.availWidth;
				this.footerConH = (footerConH * scale) / 100;
				this.footerH = (footerH * scale) / 100;
				this.percent = ((this.footerConH - this.footerH) / this.footerConH) * 100;
				setTimeout(res=>{
					let info = uni.createSelectorQuery().select(".chat");
					info.boundingClientRect(function(data) { //data - 各种参数
						 // 获取元素宽度
						scrollTop = data.height
					}).exec()
					if(this.active){
						this.scrollTop = scrollTop+500
					}else{
						this.scrollTop = scrollTop+100
					}
				},600)
				
				
			},
		}
	};
</script>

<style lang="scss">
	page {
		width: 100%;
		height: 100%;
	}

	/* #ifdef H5 */
	@import url("@/plugin/emoji-awesome/css/google.min.css");

	/* #endif */
	.broadcast_num {
		padding: 0 10rpx !important;
	}

	.noPad {
		padding: 0 !important;
		margin-bottom: 0 !important;
		height: auto !important;
	}

	.broadcast-details_num {
		width: 100%;
		height: 80rpx;
		line-height: 80rpx;
		color: #000000;
		font-size: 26rpx;
		display: flex;
		justify-content: space-between;
		background: #fff;
		border-bottom: 1px dashed rgba(0, 0, 0, 0.2);
		padding: 0 24rpx;
	}

	.broadcast-details_order {
		padding: 15rpx;
	}

	.broadcast-details_box {
		padding: 24rpx;
		display: flex;
		background: #fff;
		border-radius: 6px;
		margin-bottom: 24rpx;
	}

	.broadcast_details_model {
		width: 100%;
		height: 43rpx;
		background: rgba(0, 0, 0, 0.5);
		border-radius: 0px 0px 8px 8px;
		position: absolute;
		z-index: 2;
		bottom: 0;
		font-size: 22rpx;
		color: #fff;
		text-align: center;
		line-height: 43rpx;
	}

	.broadcast_details_img {
		width: 140rpx;
		height: 140rpx;
		border-radius: 8px;
		overflow: hidden;
		position: relative;
	}

	.broadcast_details_img img {
		width: 100%;
		height: 100%;
	}

	.broadcast_details_img_no {
		width: 100%;
		height: auto;
		border-radius: 8px 8px 0px 0px;
		overflow: hidden;
		margin-bottom: 10rpx;
	}

	.broadcast_details_picBox_no {
		width: 100%;
	}

	.broadcast_details_img_no img {
		width: 100%;
		height: 100%;
	}

	.broadcast_details_tit {
		font-size: 28rpx;
		color: #333333;
		height: 85rpx;
		font-weight: 800;
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		text-align: left !important;
	}

	.broadcast_details_tit_no {
		font-size: 28rpx;
		color: #333333;
		font-weight: 800;
		text-align: left;
		margin-top: 5rpx;
	}

	.broadcast_details_picBox {
		width: 75%;
		margin-left: 24rpx;
	}

	.broadcast_details_pic {
		font-size: 36rpx;
		color: $theme-color;
		text-align: left;
	}

	.broadcast_details_pic_num {
		text-decoration: line-through;
		font-size: 28rpx;
		color: rgba(0, 0, 0, 0.5);
		margin-left: 0.1rem;
	}

	.broadcast_details_btn {
		width: 160rpx;
		height: 50rpx;
		background: #e83323;
		opacity: 1;
		border-radius: 125rpx;
		color: #fff;
		font-size: 24rpx;
		text-align: center;
		line-height: 50rpx;
	}

	.broadcast-details .chat {
		padding: 1rpx 23rpx 0 3rpx;
		margin-bottom: 3rpx;
	}

	.broadcast-details .chat .item {
		margin-top: 37rpx;
	}

	.broadcast-details .chat .item .pictrue {
		width: 80rpx;
		height: 80rpx;
		margin-top: 10rpx;
	}

	.broadcast-details .chat .item .pictrue img {
		width: 100%;
		height: 100%;
		border-radius: 50%;
	}

	.broadcast-details .chat .item .text {
		margin-left: 20rpx;
	}

	.broadcast-details .chat .item .text.textR {
		text-align: right;
		margin: 0 20rpx 0 0;
	}

	.broadcast-details .chat .item .text .name {
		font-size: 24rpx;
		color: #999;
	}

	.broadcast-details .chat .item .text .name .return {
		color: #509efb;
		margin-left: 17rpx;
	}

	.broadcast-details .chat .item .text.textR .name .return {
		margin: 0 0.17rem 0 0;
	}

	.broadcast-details .chat .item .text .conter {
		background-color: #fff;
		border-radius: 8rpx;
		padding: 16rpx 20rpx;
		font-size: 30rpx;
		color: #333;
		position: relative;
		max-width: 496rpx;
		margin-top: 2rpx;
	}

	.broadcast-details .chat .item .text .spot {
		width: 15rpx;
		height: 15rpx;
		background-color: #c00000;
		border-radius: 50%;
		margin-left: 20rpx;
	}

	.broadcast-details .chat .item .text .conter:before {
		position: absolute;
		content: "";
		width: 0;
		height: 0;
		border-bottom: 9rpx solid transparent;
		border-right: 14rpx solid #fff;
		border-top: 9rpx solid transparent;
		left: -14rpx;
		top: 25rpx;
	}

	.broadcast-details .chat .item .text.textR .conter:before {
		left: unset;
		right: -14rpx;
		transform: rotateY(180deg);
	}

	.broadcast-details .chat .item .text .conter img {
		width: 100%;
		display: block;
	}

	.broadcast-details .chat .item .text .conter .signal {
		width: 48rpx;
		height: 48rpx;
	}

	.broadcast-details .chat .item .text .conter .signal.signalR {
		transform: rotate(180deg);
		-ms-transform: rotate(180deg);
		-webkit-transform: rotate(180deg);
	}

	.broadcast-details .footerCon {
		
		height: 100rpx;
		width: 100%;
		transition: all 0.005s cubic-bezier(0.25, 0.5, 0.5, 0.9);
		background-color: #fff;
	}

	.broadcast-details .footerCon.on {
		position: relative;
		top: -300rpx;
		transform: translate3d(0, 0, 0) !important;
	}

	.broadcast-details .footerCon .banner .swiper-slide {
		flex-wrap: wrap;
		-webkit-flex-wrap: wrap;
		background-color: #fff;
		padding-bottom: 50rpx;
		border-top: 1px solid #f5f5f5;
	}

	.broadcast-details .footerCon .banner .swiper-slide .emoji-outer,
	.swiper-slide .em {
		display: block;
		width: 50rpx;
		height: 50rpx;
		margin: 40rpx 0 0 50rpx;
	}

	.broadcast-details .footerCon .banner .swiper-container-horizontal>.swiper-pagination-bullets {
		bottom: 10rpx;
	}

	.broadcast-details .footerCon .slider-banner .swiper-pagination-bullet-active {
		background-color: #999;
	}

	.broadcast-details .recording {
		width: 300rpx;
		height: 300rpx;
		position: fixed;
		top: 40%;
		left: 50%;
		margin-left: -150rpx;
	}

	.broadcast-details .recording img {
		width: 100%;
		height: 100%;
	}

	.broadcast-details .footer {
		width: 100%;
		background-color: #fff;
		padding: 17rpx 26rpx;
	}

	.broadcast-details .footer img {
		width: 61rpx;
		height: 60rpx;
		display: block;
	}

	.broadcast-details .footer .input,
	.broadcast-details .footer .voice {
		width: 440rpx;
		border-radius: 10rpx;
		background-color: #e5e5e5;
		/* padding: 17rpx 30rpx; */
		height: 60rpx;
		padding-left: 20rpx;
	}

	.input {}

	.broadcast-details .footer .input {
		max-height: 150rpx;
		overflow-y: auto;
		overflow-x: hidden;
	}

	.broadcast-details .footer .send {
		width: 70rpx;
		text-align: center;
		height: 60rpx;
		line-height: 60rpx;
		font-weight: bold;
	}

	.em {
		display: inline-block;
		width: 50rpx;
		height: 50rpx;
		margin: 40rpx 0 0 50rpx;
	}

	.emoji-outer {
		position: absolute;
		right: 50rpx;
		bottom: 30rpx;
		width: 50rpx;
		height: 50rpx;
	}

	.broadcast-details {
		display: flex;
		flex-direction: column;
		width: 100%;
		overflow: hidden;
		.hd-wrapper {
			flex: 1;
			display: flex;
			flex-direction: column;
			overflow: hidden;
			&.on{
				padding-bottom: 300rpx;
			}
		}
	}
</style>
