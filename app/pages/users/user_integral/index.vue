<template>
	<view :data-theme="theme">
		<view class='integral-details'>
			<view class='header'>
				<view class='currentScore'>当前积分</view>
				<view class="scoreNum">{{integral.integral||0}}</view>
				<view class='line'></view>
				<view class='nav acea-row'>
					<view class='item'>
						<view class='num'>{{integral.sumIntegral||0}}</view>
						<view>累计积分</view>
					</view>
					<view class='item'>
						<view class='num'>{{integral.deductionIntegral||0}}</view>
						<view>累计消费</view>
					</view>
					<view class='item'>
						<view class='num'>{{integral.frozenIntegral||0}}</view>
						<view>冻结积分</view>
					</view>
				</view>
			</view>
			<view class='wrapper'>
				<view class='nav acea-row'>
					<view class='item acea-row row-center-wrapper' :class='current==index?"on":""' v-for="(item,index) in navList" :key='index'
					 @click='nav(index)'><text class='iconfont' :class="item.icon"></text>{{item.name}}</view>
				</view>
				<view class='list' :hidden='current!=0'>
					<view class='tip acea-row row-middle'><text class='iconfont icon-shuoming'></text>提示：积分数值的高低会直接影响您的会员等级</view>
					<view class='item acea-row row-between-wrapper' v-for="(item,index) in integralList" :key="index">
						<view>
							<view class='state'>{{item.title}}</view>
							<view>{{item.updateTime}}</view>
						</view>
						<view class='num font_color' v-if="item.type===1">+{{item.integral}}</view>
						<view class='num' v-else>-{{item.integral}}</view>
					</view>
					<view class='loadingicon acea-row row-center-wrapper' v-if="integralList.length>0">
						<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
					</view>
					<view v-if="integralList.length == 0">
						<emptyPage title="暂无积分记录哦～"></emptyPage>
					</view>
				</view>
				<view class='list2' :hidden='current!=1'>
					<navigator class='item acea-row row-between-wrapper' open-type='switchTab' hover-class='none' url='/pages/index/index'>
						<view class='pictrue'>
							<image :src="urlDomain+'crmebimage/perset/staticImg/score.png'"></image>
						</view>
						<view class='name'>购买商品可获得积分奖励</view>
						<view class='earn'>赚积分</view>
					</navigator>
					<navigator class='item acea-row row-between-wrapper' hover-class='none' url='/pages/users/user_sgin/index'>
						<view class='pictrue'>
							<image :src="urlDomain+'crmebimage/perset/staticImg/score.png'"></image>
						</view>
						<view class='name'>每日签到可获得积分奖励</view>
						<view class='earn'>赚积分</view>
					</navigator>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import { postIntegralUser, getIntegralList } from '@/api/user.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import emptyPage from '@/components/emptyPage.vue'
	let app = getApp();
	export default {
		components: {
			emptyPage
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				navList: [{
						'name': '分值明细',
						'icon': 'icon-mingxi'
					},
					{
						'name': '分值提升',
						'icon': 'icon-tishengfenzhi'
					}
				],
				current: 0,
				page: 1,
				limit: 10,
				integralList: [],
				integral:{},
				loadend: false,
				loading: false,
				loadTitle: '加载更多',
				theme:app.globalData.theme,
			};
		},
		computed: mapGetters(['isLogin']),
		watch:{
			isLogin:{
				handler:function(newV,oldV){
					if(newV){
						this.getUserInfo();
						this.getIntegralList();
					}
				},
				deep:true
			}
		},
		onLoad() {
			if (this.isLogin) {
				this.getUserInfo();
				this.getIntegralList();
			} else {
				toLogin();
			}
		},
		/**
		   * 页面上拉触底事件的处理函数
		   */
		  onReachBottom: function () {
		    this.getIntegralList();
		  },
		methods: {
			getUserInfo: function() {
				let that = this;
				postIntegralUser().then(function(res) {
					that.$set(that,'integral',res.data);
				});
			},

			/**
			 * 获取积分明细
			 */
			getIntegralList: function() {
				let that = this;
				if (that.loading) return;
				if (that.loadend) return;
				that.loading = true;
				that.loadTitle = '';
				getIntegralList({
					page: that.page,
					limit: that.limit
				}).then(function(res) {
					let list = res.data.list,
						loadend = list.length < that.limit;
					that.integralList = that.$util.SplitArray(list, that.integralList);
					that.$set(that,'integralList',that.integralList);
					that.page = that.page + 1;
					that.loading = false;
					that.loadend = loadend;
					that.loadTitle = loadend ? '我也是有底线的~' : "加载更多";
				}, function(res) {
					this.loading = false;
					that.loadTitle = '加载更多';
				});
			},
			nav: function(current) {
				this.current = current;
			}
		}
	}
</script>

<style scoped lang="scss">
	.integral-details .header {
		background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAu4AAADdCAYAAADkbPfgAAAgAElEQVR4Xu2deZPbOJJHib6POfea//b7f7CN2JiZnat72m677cbGqyGqaVlVRUokkQAeIhiSyySOlxD5UyqRSJNFAhKQgAQk8AyBnHOapuny4Ar+tnwtteT5Da+X73NKqfxN7hKQgAQksIFAueluuMRTJSABCUigJwI550+mabp28Izg73uXIuh/noU9r++nafo5pcSrRQISkIAErhBQuDstJCABCQxEIOf86TRNHAjy8j7as+BRyM+C/n1Kib9ZJCABCQxNINrNemhjOHgJSEACexKYQ1w+mwU6r4j1Vu/7eOnfzQdCXs/8npPFuiQggSYItHoDbwKunZSABCRwNoGcMwK9HHjUey1FyP+EmDduvlczOy4JSGBJQOHufJCABCTQMIGFVx2x/nnDHvV7rVC88T8ZVnMvSq+XgASiElC4R7WM/ZKABCTwDIGcMyKdA8HuvfxDVoTR4IlXxPspkoAEuiLgzb4rczoYCUigZwLzwtIvBvesbzUxIv5tSunt1gs9XwISkEA0Agr3aBaxPxKQgAQWBOZQGMQ6xxGpGUfhTUw8XnhEvAtbR7G645RAZwQU7p0Z1OFIQAJ9EJhzqxfB7r16X7Mi3N+klBDyFglIQALNEPBh0Iyp7KgEJDACgTkc5ss5HGaEIdccI7nhEfCG0dS0gm1LQAKrCSjcV6PyRAlIQALHEZjTOCLYWWxqOZcAYTQI+DfnNmtrEpCABLYRULhv4+XZEpCABHYlMAv2r+ZNknat28o2E1DAb0bmBRKQwJkEFO5n0rYtCUhAAjOBOSQGwa6HPd6sQMD/aAhNPMPYIwmMTkDhPvoMcPwSkMCpBOYsMV8bw34q9lsbIwYeAe8i1lsJep0EJLArAYX7rjitTAISkMDTBHLOeNjJFOO9t62Jwq6sr92RtS2j2VsJ9EjAh0ePVnVMEpBAKAI5Z8Q6ot17bijLbO4Mi1dZxEoojUUCEpDA6QR8iJyO3AYlIIFRCMy52AmLMY69H6MbPtOPLR2JBJojoHBvzmR2WAISaIFAzpnUjhzeZ1sw2PY+EvdO+Ize9+3svEICEriRgA+UG8F5mQQkIIFrBObFp9/oZR9ifiDaEe8uXh3C3A5SAvUJKNzr28AeSEACnRCYc7ITGvNJJ0NyGOsIvE0pvV53qmdJQAISuJ2Awv12dl4pAQlI4JHAnDGG0BjLmATeT9P0yswzYxrfUUvgLAIK97NI244EJNAlAUNjujTrrYMydOZWcl4nAQmsIqBwX4XJkyQgAQl8TGAOjSGe3XupE2RJwNAZ54MEJHAIAR82h2C1UglIoHcCc2524tktErhGgE2bCJ0x64zzQwIS2I2Awn03lFYkAQmMQiDnjGBnUyWLBJ4jQM53xDvx7xYJSEACdxNQuN+N0AokIIFRCAwcz86zgoNsORzl/aXpi3eZ13IgXnnP64iFsSPe8cBbJCABCdxFQOF+Fz4vloAERiEw74L6beepHj+dpunasdezAvFeDrzQvOcVUdt7SAn53t+O8nlxnBKQwDEE9roZH9M7a5WABCQQgECni1C5/38+H58F2DAKEc9GRoh4XnsML3mTUvoxwJS2CxKQQKMEFO6NGs5uS0AC5xDIOSNuiWnv4X5JXH4R63jWIxc88Ah4DjzVvYTa/JRSehUZvH2TgATiEujhQRSXrj2TgASaJjCLdtI9tlwQ62wMhWBv+Z6PBx4B/6YDb/y7lNIPLU8q+y4BCdQh0PJNvA4xW5WABIYg0Hi6R0Q6Yh3R3uN9HhGPgOdo1RPPGH4wXeQQtxMHKYHdCPR4Q98NjhVJQAJjEmhUtJPtBbH+VecLaC8nJaE0xI23uPBT8T7mLcZRS+BmAgr3m9F5oQQk0COBBkU7C0sR671619dOMzzvCHiOljLUvE8p/XPtID1PAhIYm4DCfWz7O3oJSGBBoDHRjlBHsBMWY/mFAKId8f66IQGveHcGS0ACqwgo3Fdh8iQJSKB3Ag0tREWws2A2elaY2lMGAU8MPAK+hTh4s83UnjG2L4EGCCjcGzCSXZSABI4l0EiedkJi2ACKV8t6Ai154BXv6+3qmRIYkoDCfUizO2gJSKAQmHdE/VXg7Ct41vGw42m33E4Arzv50/HCRy5u0hTZOvZNApUJKNwrG8DmJSCBegRyztwDEe1kZIlYEOzEsXuv3s867MxKDnVeo5bXKaUWs+RE5Wm/JNANAR8G3ZjSgUhAAlsJ5JwR7RFjxfGuExYT9QvFVtQRz8fzjoCPmoGGHO+Rv1xEtKl9kkD3BBTu3ZvYAUpAAtcI5JzxZkfLyMI9GcFOPnbL8QQInyEVI7ngoxW+UPwzpdTCwtpo7OyPBLoloHDv1rQOTAISeIpAzpnwk2jimEWnv9bLXmXekj4S73u08nNK6ftonbI/EpBAPQIK93rsbVkCEqhAYM4gg1c7Uvl6XoAaqU+j9YWwFERyNA+3mWZGm4mOVwLPEFC4Oz0kIIFhCATMIFMWx5oxJsYsfAhPmaYp2sJQF6vGmB/2QgLVCSjcq5vADkhAAmcRCLYYldCYqItjzzJJ1Haihc7whYLFqu+jArNfEpDAOQQU7udwthUJSKAygWBx7cTXE67jPbjyvHim+WihM8a7x50r9kwCpxHwoXEaahuSgARqEQgW1042G2LaLfEJEO9O3HuUtIxvU0qv42OzhxKQwFEEFO5HkbVeCUggBIF5kyWytUS43xEaEy2bTQg7Be5EtLj3VymliOkrA5vQrkmgHwIRHmT90HQkEpBAOAJB8rVzr+XLQ7S88eHsFbhDLFpl06bahS8S36eUom4cVZuP7UugawIK967N6+AkMDaBnDNCmdCUmoX77G+D7tBak0uLbZPrnYWrtcu7lFLEvPO1udi+BLonoHDv3sQOUAJjEggSIsM99jfTNJFBxtIHgSji3RSRfcwnRyGBTQQU7ptwebIEJNAKgQAhMor2VibL9n5GCJt5iL1PKUXbMGo7Ta+QgARWE1C4r0blibMHkzmzPABT/l0glXm11/wqsZzXXvnb8piM/XSuBsgio2jvfxpGEO+GzPQ/zxyhBD4gsJewEmswAvMOkdj3k8WxFNj8fSm6y//x98v35dxgo3y2O3ihiqC/9p6/lYPzHv+tB6slM1/va86ZhaC15q2ivf0ptHYE303TVDvDi1lm1lrL8yTQAQGFe3AjzgJ8Kb6LsH7pb8FHFr57RewvBT7v2bmQ4/G9Hv5Ytgyw0RJfGr6IRcXeHESAL/2I95p53t2Y6SDjWq0EIhJQuJ9slQsh/umFRxwxvvyb9jnZPjc2V8T9R6K+CH3F/Y1kN142f77IlV7rs8NuqF9t7Lant02Azz/inc9/rfImpRQh202t8duuBIYhUOvh1hXgOfZ7Kbifel+85V2N38GsIsDDHa8cD/fy+vDe0JxV/FadVHlBKoId4W4ZjwCf5X/M4Xk1Rm9u9xrUbVMCFQgo3F+AfiHKEeSkdSuecf7NIccKk7ejJnnoIuY/OFJKNX9+bw5vzpnPIt72GoXQGEJkLOMSINYdz3ut8jal9LpW47YrAQmcQ2BowXlFlF8K8uI5P8catiKBDwksBT2iACH/kx7669Mk54xo5zN7dqFNNlga+n56NvSg7RGuUnNjJHZUNT1k0MlhtySwB4GuHzRzvGvxipdXPOZ6yveYPdZRiwA/yyPky0G4Tc342locHtutnP7xd5W+MFTnbgeuEqiZaYYv9q+0iwQk0C+B5oX7/MC+FOclpKX58fU79RzZzgRKDH0Jt+EBXjtN3c5DfLq6it52vPxfnjbQmA0t08yWFKwxe3pOr/gsEu9ey/PNpkxDf5E/x8y2IoE6BMIL20U4S/GULz3mNX4Wr2MpW5XAdgKIqKVnHjHf3QM95/z5NE3fbMdz9xWjxLVzn4Uxx+X996lnSEmnWhZjlwXZZT7eDT94BTXj3d2UKfjksHsSuIdAGOG+8JwvBXpZCHrPGL1WAhL4hQCCainmWdBWdqRtklMlbzvrYQiRCXMP3dF43Hf5FaEce29kxXx7uzjeVMzGsiO2j6oi1r1Wika97kda1rolUJHAqQ+d2XvOQ+FSnJuZpeIksOnhCTwsei2LX+cFsE2I+Yqx7b+ZPdC9TB7uyV/PB571MwtfJhG4xGYj4nspfIYImanxK5ex7r3MIschgQsChwj3xaLQS4HOvy0SkEB8AsUrj2c0bIhNzpm86WffV3rJ1879H7EOwyg7vSLi8VT/s2KM+J6fzpohM2aY2dOS1iWBIATuEu6zB30Z91i86Xv/tBoEl92QwLAEPshkE2Hha6W87T2EyDAGcs4j2O96Bhz4acBbTU7y7+dfgg5s6vCqa4XMmNf9cNPagATOJ7Dqpv1CiMv5vbZFCUigNoGSYx6PYvHKn5pFI+eMt/hsT3HLWWQQ7PSfY9W9v/Ykm9snjIaQk1Y3JOOz8vcKvyC4m2qQCWw3JLAngY9u3nPMaPGcL+PR92zXuiQggf4IlFj5h4WHR24EMzsT8BqfKUD5dZHY9hYL3nX63uqvoYhQYuDxwNeIGb/X5sTuE/5zdvkxpdTTuoGz+dmeBMIRSDln0qiVvOc8mM58EIYDYockIIHdCJTwmuKR381jmnMm4wmx5meWFjda4hcJ+n32gtOj7PLgRZ6Po9o4qt4avxr8nFKCl0UCEuiEAML9D52MxWFIQAKxCZRUlHgAWfB6s5DPOeNtP9N73OKCVDzsrYXFrJ3BfBn8a2Ped+Y74v3s8irCmpSzB217EuiVAML9v3odnOOSgARCE0DIl3zeqzPXVEoB+fuTvyjcYzh+Qf23CvH/9/T5lmtL7DghNK0UvN/M+TOLqSHPpG1bEjiYgML9YMBWLwEJrCbwweZQT3kJ5/C+M0M/WARbY2fW1eAWJxJChGg/89eIW/q55zVkbWHxZwuF8LGz++oi1RZmhn2UwEoCCveVoDxNAhI4nQCCo2StYbHru3lR6pkLRFnzQ4x4C0KYsBjYjLhOifCrvzSyAyuLVM9eMPo6pXS2p//0G4YNSmAEAgr3EazsGCXQB4HikecVUY9APVpQt+Jt58sFmWNGLsSQI95vXjtxEjzm799Oaqs08z6lVCOrzcnDtDkJ9E8A4f6f/Q/TEUpAAh0SQAAh0hDxvD9CyLcQ2/7vFTLsRJ1OhKL8XwPivUasuzupRp219ksCGwgo3DfA8lQJSCA0AUQbAr4ceOPvCRtpIZOMov3jKdmCeK+RYcac7qFvX3ZOAusIKNzXcfIsCUigPQKIoyLi8cpvFfLR87azCJVQHkub4v27eQ3HWfYzXOYs0rYjgQMJKNwPhGvVEpBAGAII92VYDf8mbeJTJfouqca0vzy1sPef5y9vL599/hk1dlM1XOZ8O9uiBHYloHDfFaeVSUACjRAoQr545C/j48nQQmrFiIVFqAh3y8sEEMfEvEcszEEWqfJ6Vhk2u0zOufziVj7rvC4PbHB5HyihdpevT9lracvn3vN/5f/L+2v/fliIn1I6c46cNRdt50YCCPf/uPFaL5OABCTQC4GlR573hKE855GvNW6+TBDXfk/sfq2+12o3cp53No96fSKYdykleDRX5lSwRXzzujyK4L72tyLOmxvzRYeXAr8syL98LRm3rr6mlPi7pXECCvfGDWj3JSCBQwiwwBERgHj/bPa+1xbL9IWdro9OgXkI0MqVsulRRMF69oZMeG+JrQ9TZkHO3C6iu7y/fHXe32+15eL98p45SFgZr6yDUNzfz/nQGhTuh+K1cglIoAMCeLV4qCHgERPEv39RYVz8Oho1fKcCjk1NYsM/BU0TyZcK5tdZ5Qc2MzursYUw57NzefCZUpCfZYx17RQv/oOQvzgQ9Yr7dRwPO0vhfhhaK5aABDolUDxViI7ijT86rIaY+992yvOsYbFzKItVoxVCZQiZOau8SSn9uGdjC3HO54EvtuVLbvGa79mcddUnUEILP/DW88U4pXTml9D6JCr0QOFeAbpNSkACXRHAe0kYTRHy5H/fM6yGegmR2bPOrgywYTD/mKYp2g6iZ++kenNayJxz8ZiXuV5et6Za3WAyT22MQPmFcinqH0JxzvylpzFmm7qLcGehk0UCEpCABO4nUB5aJTaekJp7w2oMkbnfLqUG7PPHk0NT1vSeLxSnha9M0/TdS5lKZpG+9KDjSVegr7Gm5zxFoNwfmeslrv7h/UvzUaS/EFC4OxskIAEJHEeghNUgehDzbJi0JaaX88lwY9mPAKEpf92vul1qOjtc5lVK6afHbzP/8qQzR8th7PkuZrWSlQRKXD0innn5cLhQ9jo9hfvKWeVpEpCABO4kULxNCPcikPDGPxcC84c5BOfOpr38ggALVR+FawA6Z2aXYb7RHvOxzMMtXyYD4LILgxAo4TaK+YXBFe6DzH6HKQEJhCOwzFZTFrnyWso30zT9Plyv++hQxI2Z2IzpiFR8y1SLveQ072MWOopbCPAZeRTyI3rmFe63TBuvkYAEJLA/AbxLZZErntD/nj2i+7dkjRBgR1UEfJTCotk9+nO5OVGU8dkPCRxBoDhAyBpVQmy6zmyDcDd+8oipZJ0SkIAE7iPAA+nb+fj1HB9/X41evSRASsS/BEKCaL8l482lRz3QkOyKBKoQQLgj4h/EfG/ZbBTuVeaUjUpAAhLYRAARj0BDwJPTndejc8dv6mCjJ5Nh5sxsLs9hWpsWUo96o5PNblcjwGereOTfti7kFe7V5pENS0ACEriZAA8iYuCLiOe9ZTsBPNykYoxSrsW5Ez619KpH6av9kECrBHCEPHrkZ688f2uiKNybMJOdlIAEJPAkAR44eGF/ozd+8yyB3f/OGVY2X3zABd/PnkHEOjZFsLvx1gGgrVICCwLcB/DINxFao3B37kpAAhLoiwDeeGLjizeeXPCWpwmQ05086rULdiPOvezEW7s/ti+BUQkQI1+EPKE1obzxCHfTjY06NR23BCQwAgFj45+3cs0NmRDrZRfJI1JBjjC/HaMEjiRQwmr4Uv0mwqZQCvcjzW3dEpCABGIR4CGEBx5vPAee+dFDMc4Olyl5qBHsivVYnw97I4GXCPC5RcRXW+SqcH/JRP6/BCQggT4JIFgR7UXA/3aaJnZyHbGQFpL0kEeV4rVjQZxi/SjK1iuBcwkQUlM88adlp1K4n2tkW5OABCQQlQAPIYQ7Qp6FrhyjeOOPyi5TMlco1qPOevslgX0IFBH/Y0rp0A2gFO77GMxaJCABCfREAA8xRwmpwRv/ZU8DvBgLAvtPO4yPLzrLzV92qNIqJCCBxgjgfecXvENi4hHuv2sMiN2VgAQkIIFzCSDiP5+98GUTKNIV9lLujXNHsCP++dn8UG9bL8AdhwQGIVDuC4j4XbLTKNwHmTkOUwISkMBOBJax8Yh4vPGI+tbL/83Ce+04EOt41sqOjGuv8zwJSGA8AsvsNHelmFS4jzd5HLEEJCCBPQnwQPpqmiZEPEermWrYQZVY95fK8gG8iwftpQb9fwlIoCsC3DcIpbkpHl7h3tVccDASkIAEqhIou7gWEc9rK974H6Zp+vsT9PSuV51WNi6BbgkQSoOIX+2FV7h3OxccmAQkIIHqBJZ548lS803gTDU8PEkLeVnKg9XMMNWnkx2QQLcEuFcSdvf6paw0CHfiEy0SkIAEJCCBownguS6LWxHykfLGE6/+x/mLRfkpm8WmFglIQAJnEnhwFqSUEPIfFYX7maawLQlIQAISKAQQx6SYRMAXMV8zbzz9+Z+yK6JmkoAEJFCZwDI3/ON6GoV7ZavYvAQkIAEJPBIomz8h5M/KG08IDLHthMng6bJIQAISiESAe1RZzJoV7pFMY18kIAEJSAACxRtfhDyve+eNx5tFCsi/TtNk/LrzTgISiE7gIYRP4R7dTPZPAhKQgAQgQJrJkq3m6zuQ4FX/8zRNf7ujDi+VgAQkUIUAwp34QosEJCABCUigBQJ4nT5bxMYj5vn3SwXB/qdnUj6+dL3/LwEJSKA6AYV7dRPYAQlIQAISuIMAQp40k09tAKVgvwOul0pAArEIKNxj2cPeSEACEpDA7QQQ8Z/OIv730zR9N00TO6JaJCABCXRBQOHehRkdhAQkIAEJzATYqfWrWcAj5MnPzkJUXh9TqklLAhKQQIsEFO4tWs0+S0ACEpDAJYGlYH+KDtljEPBFzEtRAhKQQFMEEO7EBVokIAEJSEACLRIgNIYsM2sWqC7Hh/cdT7ze+Batbp8lMCgBhfughnfYEpCABBonwC6rhMTgad9jx1W88UXE82qRgAQkEI6Awj2cSeyQBCQgAQk8QwCRzq6qX+wk2K81hTe+hNUg4t2gySkpAQmEIKBwD2EGOyEBCUhAAisI4F0nLGYPD/uK5h5PKd744pHfcq3nSkACEtiNgMJ9N5RWJAEJSEACBxEgfp2wmE8Oqn9rtUtvvGE1W+l5vgQkcDMBhPuvbr7aCyUgAQlIQALHEVjGsR/Xyn01l7CastDVsJr7eHq1BCTwDAGFu9NDAhKQgAQiEjg6jv2oMS+z1SDmzR1/FGnrlcCABBTuAxrdIUtAAhIITKCkd4wSFnMvqmV8vGE199L0egkMTkDhPvgEcPgSkIAEghAo2WJYgNpzKVlqSmhNz2N1bBKQwM4EFO47A7U6CUhAAhLYTKAsPj07W8zmju58gfHxOwO1Ogn0TgDh/m3vg3R8EpCABCQQkgBCnfSOhMdY/hUPX0JreHWhq7NCAhL4gIDC3QkhAQlIQAI1CLCB0pGbKNUY095tloWuRcy70HVvwtYngcYIKNwbM5jdlYAEJNA4ARadRsrJ3hLOy9AahXxL1rOvEtiBgMJ9B4hWIQEJSEACqwiw8FQv+ypUq04q4TQudF2Fy5Mk0D4BhXv7NnQEEpCABKITKBljWIRqOY7AMj7e1JPHcbZmCVQjgHD/plrrNiwBCUhAAr0TYOEpoTGjZYyJYNfikS+CPkKf7IMEJHAHAYX7HfC8VAISkIAEniVAWEzvedlbmgIK+ZasZV8lcIWAwt1pIQEJSEACexNgAeqX0zT1svvp3nyi1LcU8rx3sWsUy9gPCTxBQOHu1JCABCQggT0JEMfuAtQ9iZ5XV8laY/rJ85jbkgQ2EVC4b8LlyRKQgAQk8AwBvOwuQO1niiyFvBtC9WNXR9IwAYQ7u9ZZJCABCUhAArcSKFljDI25lWA71xUBX0S94TXt2M6edkBA4d6BER2CBCQggYoESjy7WWMqGqFi00sBr1e+oiFsegwCCvcx7OwoJSABCRxBgIwxhMYo2o+g226deuXbtZ09D05A4R7cQHZPAhKQQFACLEA1nj2ocYJ1S698MIPYnXYJINzZGMMiAQlIQAISWEMA7zqi3Xj2NbQ85ykCiPmloC//lpgEJPAMAYW700MCEpCABNYSKItQDY1ZS8zzthBQzG+h5blDElC4D2l2By0BCUhgMwFF+2ZkXrATgWueebPZ7ATXatoioHBvy172VgISkEANAoTFEB5jkUAkAsVDf+01Uj/tiwR2I6Bw3w2lFUlAAhLokkCJaTc8pkvzdjsoRX23ph17YAh3drqzSEACEpCABK4RIHOM2WOcGz0SWC6ILaE3T732OH7HFItAcY5cvtJL/vaQZlXhHsto9kYCEpBANAIK92gWsT+1CFyKevqxjLV/6f9r9dt29ydw+Qvk8t/X3j/1/1t69k7hvgWX50pAAhIYk8CnetzHNLyjPpTA5eLal/59+SXh2r+XHe5p8e6aML1r5zx13Zpz17R56AS5Uvn7aZre63E/G7vtSUACEmiLgMK9LXvZWwlsIVBb4EcUyFv4nXmuHvczaduWBCQggUYJKNwbNZzdloAEuiLwKNxN8dWVXR2MBCQggV0JEOOOeLdIQAISkEA9AoTKPCxOVbjXM4ItS0ACEohOANGucI9uJfsnAQn0TkDh3ruFHZ8EJCCBHQgo3HeAaBUSkIAE7iSgcL8ToJdLQAISGIGAwn0EKztGCUggOgGFe3QL2T8JSEACAQgo3AMYwS5IQALDE3gU7p8Pj0IAEpCABCTwFAGE+yfikYAEJCCBqgQQ7pnFqQr3qnawcQlIQAKhCSDaXZwa2kR2TgISGICAwn0AIztECUhAAvcSULjfS9DrJSABCdxPQOF+P0NrkIAEJNA9AYV79yZ2gBKQQAMEFO4NGMkuSkACEqhNAOFujHttK9i+BCQwOoGfywZM7IpnkYAEJCABCVwjoHB3XkhAAhKoTwDh/rA4VeFe3xj2QAISkEBUAgr3qJaxXxKQwEgEFO4jWduxSkACEriRgML9RnBeJgEJSGBHAgr3HWFalQQkIIFeCSRj3Hs1reOSgAQaIqBwb8hYdlUCEpBALQIK91rkbVcCEpDALwQehbsbazgtJCABCUjgKQKEyiDeLRKQgAQkUI9ALotTFe71jGDLEpCABKITQLQr3KNbyf5JQAK9E1C4925hxycBCUhgBwIK9x0gWoUEJCCBOwko3O8E6OUSkIAERiCgcB/Byo5RAhKITkDhHt1C9k8CEpBAAAIK9wBGsAsSkMDwBB6Fu1tZDz8XBCABCUjgWQLGuDtBJCABCdQn8LBzqsK9viHsgQQkIIHIBBTuka1j3yQggREI4HGfFO4jmNoxSkACEriPgML9Pn5eLQEJSOBeAgr3ewl6vQQkIIFBCCjcBzG0w5SABMISULiHNY0dk4AEJCABCUhAAhKQwIcEHmLc9aQ4LSQgAQlIQAISkIAEJBCcgMI9uIHsngQkIAEJSEACEpCABCCgcHceSEACEpCABCQgAQlIoAECCvcGjGQXJSABCUhAAhKQgATGJZBSelycaoz7uPPAkUtAAhKQgAQkIAEJBCfwKNyD99PuSUACEpCABCQgAQlIQALEuEtBAhKQgAQkIAEJSEACEohPQOEe30b2UAISOIDAnAr30wOq3qtK7s/eo/ei+XE9Px9X9S4158nZNMsAAAaFSURBVJTS+11qshIJSKAbAj4UujGlA5GABLYSyDn/apqmyOL9m2mavlLAb7Xss+f/NE3TD9M0RRfFP6SU3u06ciuTgASaJ6Bwb96EDkACEriVwOx1R7x/cmsdJ1xH376dpumLE9rquQk87Aj2tw0M8nVKqYV+NoDSLkqgLwIK977s6WgkIIGNBHLOCGPEe/T74eezgI/8C8FG+qecTgq1H6dpenVKa/c38ialRH8tEpCABD4iEP1BpckkIAEJHE4g5/zZLIoPb2uHBvC8fz1NE322PE2gCPbX0zQ95D9uoPyUUmrlC0YDOO2iBPojoHDvz6aOSAISuIFAzhmPNjHlrRQF/HVLtSjYGcm7lBKhPBYJSEACTxJQuDs5JCABCcwEGhTv9JwvHCxg5XXkezqLTQkxedOQh7189hTt3oUkIIFVBEa+ya8C5EkSkMBYBBoV7xiJWP0vZxEfebHtnhMK7zqLOBHrZItpsSjaW7SafZZAJQIK90rgbVYCEohLoGHxXqASRlOOHu/zpElEsONhbyV+/dqEV7THvQ3YMwmEJNDjDT0kaDslAQm0RaAD8Q5w7vFFwLceSkMoDJ51BHv0HOxrJruifQ0lz5GABD4goHB3QkhAAhJ4gsCcbYYFqz3cK4uIR8BzRA+nwZOOZ50QGI6eNiMye4x3HQlI4CYCPTyMbhq4F0lAAhJYQyDnTN50NkDq7X7JuIqIJ7VkbSGPUMeTXoR6qzHrL02rtyklUlRaJCABCWwm0NuDaDMAL5CABCTwEoGOxfty6DwPEPMcCHleEfMcez4r2MGUA5HOgSedV/7We3Fzpd4t7PgkcDCBPW/GB3fV6iUgAQnUIzDvsIrnvbZnugYEnhVFxJfxl+fH5XOkLBZdvhaxPoI4f8o+r1NKxOdbJCABCdxMQOF+MzovlIAERiOQc+aeScy7u5aOZvzbx8sXmFcppZ5i9G+n4ZUSkMBdBBTud+HzYglIYEQCOWfEO/HhFgk8R4BfGH5IKY38S4MzRAIS2JGAwn1HmFYlAQmMQyDnzG6lbHhkkcA1AsTtI9pbzjOvZSUggWAEFO7BDGJ3JCCBdgjMud6/3nnxZjsA7OlTBEz36NyQgAQOIaBwPwSrlUpAAqMQmBetEjpDFhbL2ATwrrMItddUlmNb19FLIAABhXsAI9gFCUigfQKGzrRvwztHQGgMi1CNZ78TpJdLQAJPE1C4OzskIAEJ7ERg3mmV0JkRU0buRLHJatxUqUmz2WkJtEdA4d6ezeyxBCQQmMCcMhLxbtaZwHbaqWt41wmNMdXjTkCtRgISeJ6Awt0ZIgEJSOAAAnrfD4Aaq0q97LHsYW8kMAQBhfsQZnaQEpBADQKz9520kV/UaN82DyGAl51YdmLaLRKQgAROJaBwPxW3jUlAAiMSmL3vCHgzz7Q7AcgY8yal9KbdIdhzCUigdQIK99YtaP8lIIFmCOSc8bwj4L33NmO1h46S3pFYdjdTastu9lYC3RHw4dGdSR2QBCQQmYDhM5Gt81HfCIf50cWnTdnMzkqgawIK967N6+AkIIGoBOaNm/C+m30mnpGIYycs5m28rtkjCUhgZAIK95Gt79glIIHqBHLOxL0j4D+r3hk7oGB3DkhAAqEJKNxDm8fOSUACoxCYF7B+qYCvYnEXnlbBbqMSkMBWAgr3rcQ8XwISkMCBBGYPPALeEJoDOc9V62E/nrEtSEACOxJQuO8I06okIAEJ7EVgjoEnCw2H9+q9wP6rHnY6ZQMlssVYJCABCTRDwIdBM6ayoxKQwIgE5iw0RcB/MiKDncZMOAxCHcHu5kk7QbUaCUjgXAIK93N525oEJCCBmwnMYTSIeMJovH+vI6l3fR0nz5KABBog4I2/ASPZRQlIQAKXBHLOiHcOstF4L/8QEB51vOs/pZSIY7dIQAIS6IKAN/suzOggJCCBkQko4h+sr1gf+UPg2CUwCAGF+yCGdpgSkMAYBOa0knjhOcgR32shZp0wGA486/zbIgEJSKBrAgr3rs3r4CQggZEJzAtblyK+ZSG/FOrvDIEZeWY7dgmMS0DhPq7tHbkEJDAggXmBKwKeA1EfMVMNIp3Ql3Ig1PWoDzhfHbIEJPAhAYW7M0ICEpDA4ATmnPEIeUT88uAZcdRzAiHOwtHlgVD/WZE++IR0+BKQwJMEjrohi1wCEpCABDohMAv7IuIvxfxTz5Glh7yIdF6zwryTieEwJCCB0wn8P9Y8XQzbEeEmAAAAAElFTkSuQmCC');
		background-repeat: no-repeat;
		background-size: 100% 100%;
		width: 100%;
		height: 460rpx;
		font-size: 72rpx;
		color: #fff;
		padding: 31rpx 0 45rpx 0;
		box-sizing: border-box;
		text-align: center;
		font-family: 'Guildford Pro';
		@include main_bg_color(theme);
	}

	.integral-details .header .currentScore {
		font-size: 26rpx;
		color: rgba(255, 255, 255, 0.8);
		text-align: center;
		margin-bottom: 11rpx;
	}
	
	.integral-details .header .scoreNum{
		font-family: "Guildford Pro";
	}
	
	.integral-details .header .line {
		width: 60rpx;
		height: 3rpx;
		background-color: #fff;
		margin: 20rpx auto 0 auto;
	}

	.integral-details .header .nav {
		font-size: 22rpx;
		color: rgba(255, 255, 255, 0.8);
		flex: 1;
		margin-top: 35rpx;
	}

	.integral-details .header .nav .item {
		width: 33.33%;
		text-align: center;
	}

	.integral-details .header .nav .item .num {
		color: #fff;
		font-size: 40rpx;
		margin-bottom: 5rpx;
		font-family: 'Guildford Pro';
	}

	.integral-details .wrapper .nav {
		flex: 1;
		width: 690rpx;
		border-radius: 20rpx 20rpx 0 0;
		margin: -96rpx auto 0 auto;
		background-color: #f7f7f7;
		height: 96rpx;
		font-size: 30rpx;
		color: #bbb;
	}

	.integral-details .wrapper .nav .item {
		text-align: center;
		width: 50%;
	}

	.integral-details .wrapper .nav .item.on {
		background-color: #fff;
		@include main-color(theme);
		font-weight: bold;
		border-radius: 20rpx 0 0 0;
	}

	.integral-details .wrapper .nav .item:nth-of-type(2).on {
		border-radius: 0 20rpx 0 0;
	}

	.integral-details .wrapper .nav .item .iconfont {
		font-size: 38rpx;
		margin-right: 10rpx;
	}

	.integral-details .wrapper .list {
		background-color: #fff;
		padding: 24rpx 30rpx;
	}

	.integral-details .wrapper .list .tip {
		font-size: 25rpx;
		width: 690rpx;
		height: 60rpx;
		border-radius: 50rpx;
		background-color: #fff5e2;
		border: 1rpx solid #ffeac1;
		color: #c8a86b;
		padding: 0 20rpx;
		box-sizing: border-box;
		margin-bottom: 24rpx;
	}

	.integral-details .wrapper .list .tip .iconfont {
		font-size: 35rpx;
		margin-right: 15rpx;
	}

	.integral-details .wrapper .list .item {
		height: 124rpx;
		border-bottom: 1rpx solid #eee;
		font-size: 24rpx;
		color: #999;
	}

	.integral-details .wrapper .list .item .state {
		font-size: 28rpx;
		color: #282828;
		margin-bottom: 8rpx;
	}

	.integral-details .wrapper .list .item .num {
		font-size: 36rpx;
		font-family: 'Guildford Pro';
	}

	.integral-details .wrapper .list2 {
		background-color: #fff;
		padding: 24rpx 0;
	}

	.integral-details .wrapper .list2 .item {
		background-image: linear-gradient(to right, #fff7e7 0%, #fffdf9 100%);
		width: 690rpx;
		height: 180rpx;
		position: relative;
		border-radius: 10rpx;
		margin: 0 auto 20rpx auto;
		padding: 0 25rpx 0 180rpx;
		box-sizing: border-box;
	}

	.integral-details .wrapper .list2 .item .pictrue {
		width: 90rpx;
		height: 150rpx;
		position: absolute;
		bottom: 0;
		left: 45rpx;
	}

	.integral-details .wrapper .list2 .item .pictrue image {
		width: 100%;
		height: 100%;
	}

	.integral-details .wrapper .list2 .item .name {
		width: 285rpx;
		font-size: 30rpx;
		font-weight: bold;
		color: #c8a86b;
	}

	.integral-details .wrapper .list2 .item .earn {
		font-size: 26rpx;
		color: #c8a86b;
		border: 2rpx solid #c8a86b;
		text-align: center;
		line-height: 52rpx;
		height: 52rpx;
		width: 160rpx;
		border-radius: 50rpx;
	}
	
	.font_color{
		@include price_color(theme);
	}
</style>
