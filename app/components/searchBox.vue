<template>
	<view  class="acea-row search-contain" :style="{'margin-top':searchTop+'px'}">
		<text class='iconfont icon-fanhui2' @click="toBack" v-if="toBackShow"></text>
		<view class='search-box acea-row row-between-wrapper' :style="[searchBoxStyle]">
			<text class='iconfont icon-sousuo2'></text>
			<input  :value="searchVal" @confirm="inputSearch" type='text' confirm-type='search' name="search" placeholder='点击搜索商品' placeholder-class='placeholder' maxlength="20"></input>
		</view>
	</view>
</template>

<script>
	export default {
		props: {
			//是否展示返回按钮
			toBackShow: {
				type: Boolean,
				default: true
			},
			searchValue:{
				type: String,
				default: ''
			}
		},
		data(){
			return{
				searchVal:'',
				searchTop:0,
				searchRight:0,
				searchHeight:0,
				statusWidth:0,
			}
		},
		watch:{
			searchValue(val){
				this.searchVal=val
			}
		},
		computed:{
			searchBoxStyle(){
				return {
					height:this.searchHeight + 'px',
					flex:1,
					marginRight:this.statusWidth + this.searchRight+'px',
				}
			}
		},
		created() {
			const res = uni.getMenuButtonBoundingClientRect()
			this.searchTop=uni.getMenuButtonBoundingClientRect().top
			const statusRight = res.right //胶囊右边界坐标
			const jnHeight = res.height //胶囊高度
			this.statusWidth= res.width
			this.searchHeight=jnHeight
			//搜索框宽度计算
			uni.getSystemInfo({
				success:res=>{
					this.searchRight=res.windowWidth-statusRight
				}
			})
		},
		methods:{
			inputSearch(e){
				this.$emit('searchChange',e)
			},
			toBack(){
				uni.navigateBack()
			}
		}
	}
</script>

<style scoped lang="scss">
	.search-contain{
		padding: 0 20rpx 10rpx 0;
	}
	.search-box {
		margin-left: 16rpx;
		background-color: #f7f7f7;
		border-radius: 33rpx;
		padding: 0 35rpx;
		box-sizing: border-box;
		height: 66rpx;
	}
	.icon-fanhui2{
		line-height: 66rpx;
	}
	.search-box input {
		width: 85%;
		font-size: 26rpx;
	}

	.search-box .placeholder {
		color: #bbb;
	}

	.search-box .iconfont {
		color: #000;
		font-size: 35rpx;
	}
</style>