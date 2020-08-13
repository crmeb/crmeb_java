
<script>
	import { checkLogin } from "./libs/login";
	import { HTTP_REQUEST_URL } from './config/app';

	export default {
		globalData: {
			spid: 0,
			code:0,
			isLogin:false,
			userInfo:{},
			MyMenus:[]
		 },
		onLaunch: function(option) {
			let that = this;
			// #ifdef MP
			 if (HTTP_REQUEST_URL==''){
			      console.error("请配置根目录下的config.js文件中的 'HTTP_REQUEST_URL'\n\n请修改开发者工具中【详情】->【AppID】改为自己的Appid\n\n请前往后台【小程序】->【小程序配置】填写自己的 appId and AppSecret");
			      return false;
			    }
			 if (option.query.hasOwnProperty('scene')){
			      switch (option.scene) {
			        //扫描小程序码
			        case 1047:
					  let val = that.$util.getUrlParams(decodeURIComponent(option.query.scene));
			          that.globalData.code = val.pid;
			          break;
			        //长按图片识别小程序码
			        case 1048:
			          that.globalData.code = option.query.scene;
			          break;
			        //手机相册选取小程序码
			        case 1049:
			          that.globalData.code = option.query.scene;
			          break;
			        //直接进入小程序
			        case 1001:
			          that.globalData.spid = option.query.scene;
			          break;
			      }
			    }
				// #endif
			// 获取导航高度；
			uni.getSystemInfo({
			    success: function (res) {
					that.globalData.navHeight = res.statusBarHeight * (750 / res.windowWidth) + 91;
			    }
			});
		},
		mounted() {
		},
		onHide: function() {
			//console.log('App Hide')
		}
	}
</script>

<style>
	@import url("@/plugin/animate/animate.min.css");
	@import 'static/css/base.css';
	@import 'static/iconfont/iconfont.css';
	@import 'static/css/guildford.css';
	@import 'static/css/style.scss';
	view{
		box-sizing: border-box;
	}
	.bg-color-red {
	  background-color: #e93323!important;
	}
	.syspadding{
		padding-top: var(--status-bar-height);
	}
	.flex{
		display: flex;
	}
	.uni-scroll-view::-webkit-scrollbar {
		/* 隐藏滚动条，但依旧具备可以滚动的功能 */
		display: none
	}
	::-webkit-scrollbar {
	  width: 0;
	  height: 0;
	  color: transparent;
	}
</style>
