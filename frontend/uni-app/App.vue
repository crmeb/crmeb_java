<script>
// 普通 script 块仅用于声明 globalData（uni-app 通过 getApp().globalData 访问，
// 必须以组件选项形式暴露，<script setup> 无法直接提供此字段）。
var statusBarHeight = uni.getSystemInfoSync().statusBarHeight; //手机端头部手机时间位置高度
import Cache from '@/utils/cache.js';

export default {
	globalData: {
		statusBarHeight: statusBarHeight, //手机端头部手机时间位置高度
		spread: 0, //推广人id
		code: 0,
		isLogin: false,
		userInfo: {},
		MyMenus: [],
		windowHeight: 0,
		navHeight: 0,
		navH: 0,
		id: 0, //商品id
		isIframe: false,
		theme: 'theme1', //主题色
		// 启动时先等待服务端返回最新登录配置，避免使用过期缓存触发公众号授权。
		publicLoginType: '', //公众号登录方式(单选),1微信授权，2手机号登录
		routinePhoneVerification: '', //小程序手机号校验类型（多选）1微信小程序验证 2短信验证
		companyName: uni.getStorageSync('companyName') ? uni.getStorageSync('companyName') : '欢迎你', //公司名称
		mobileLoginLogo: uni.getStorageSync('mobileLoginLogo') || `${Cache.get("imgHost")}/crmebimage/perset/staticImg/logo2.png` //登录页logo
	}
}
</script>

<script setup>
import { getCurrentInstance, onMounted } from 'vue';
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app';
import { HTTP_REQUEST_URL } from '@/config/app';
import Cache from '@/utils/cache.js';
import { loginConfigApi } from '@/api/api.js';
import { useAppStore } from "@/store/app.js";
import { applyH5ThemeStyle, applyTheme } from '@/utils/theme.js';
// #ifdef H5
import Auth from '@/libs/wechat.js';
// #endif
// #ifdef MP
import Routine from '@/libs/routine.js';
// #endif

const { proxy } = getCurrentInstance();
let app = null;

function resolveApp() {
	const appInstance = getApp({
		allowDefault: true
	});
	app = appInstance || app || {
		globalData: {}
	};
	if (!app.globalData) app.globalData = {};
	return app;
}

function getThemeIdFromLaunch(option = {}) {
	const query = option.query || {};
	let themeId = query.theme_id || '';
	if (!themeId && query.scene) {
		const value = proxy.$util.getUrlParams(decodeURIComponent(query.scene));
		themeId = value.theme_id || '';
	}
	if (themeId) {
		uni.setStorageSync("previewThemeId", themeId);
		return themeId;
	}
	return uni.getStorageSync("previewThemeId") || '';
}

function isThemePreview(option = {}) {
	const query = option.query || {};
	if (query.type === 'iframeVisualizing') return true;
	if (query.theme_id) return true;
	if (uni.getStorageSync("previewThemeId")) return true;
	if (query.scene) {
		const value = proxy.$util.getUrlParams(decodeURIComponent(query.scene));
		return !!value.theme_id;
	}
	return false;
}

function setSiteTitle(title) {
	if (!title) return;
	// #ifdef H5
	document.title = title;
	// #endif
	// #ifndef H5
	if (!getCurrentPages().length) return;
	uni.setNavigationBarTitle({
		title,
		fail() {}
	});
	// #endif
}

//获取登录配置
function getLoginConfig() {
	return loginConfigApi().then(res => {
		let data = res.data;
		//公众号登录方式(单选),1微信授权，2手机号登录
		app.globalData.publicLoginType = data.publicLoginType;
		Cache.set('publicLoginType', data.publicLoginType);
		//小程序手机号校验类型（多选）1微信小程序验证 2短信验证
		app.globalData.routinePhoneVerification = data.routinePhoneVerification;
		//公司名称
		app.globalData.companyName = data.siteName || '欢迎你';
		uni.setStorageSync('companyName', data.siteName);
		//登录页logo
		app.globalData.mobileLoginLogo = data.mobileLoginLogo;
		uni.setStorageSync('mobileLoginLogo', data.mobileLoginLogo);
		//版权title
		setSiteTitle(data.siteName);
	}).catch(() => {
		// 登录配置获取失败时也不要沿用旧的公众号授权配置，默认使用手机号验证码登录。
		app.globalData.publicLoginType = '2';
		Cache.set('publicLoginType', '2');
	})
}

onLaunch((option) => {
	option = option || {};
	resolveApp();
	const appStore = useAppStore();
	//获取登录配置
	const loginConfigPromise = getLoginConfig();
	const isThemePreviewFlag = isThemePreview(option);
	const launchQuery = option.query || {};
	let previewThemeId = getThemeIdFromLaunch(option);
	applyTheme(previewThemeId).catch(() => {});

	// #ifdef APP-PLUS || H5
	uni.getSystemInfo({
		success: function(res) {
			// 首页没有title获取的整个页面的高度，里面的页面有原生标题要减掉就是视口的高度
			// 状态栏是动态的可以拿到 标题栏是固定写死的是44px
			let height = res.windowHeight - res.statusBarHeight - 44
			// #ifdef H5 || APP-PLUS
			app.globalData.windowHeight = res.windowHeight + 'px'
			// #endif
		}
	});
	// #endif
	// #ifdef MP
	const updateManager = uni.getUpdateManager();
	updateManager.onCheckForUpdate(function(res) {
		// 请求完新版本信息的回调
		if (res.hasUpdate) {
			updateManager.onUpdateReady(function(res2) {
				uni.showModal({
					title: '更新提示',
					content: '发现新版本，是否重启应用?',
					cancelColor: '#eeeeee',
					confirmColor: '#FF0000',
					success(res2) {
						if (res2.confirm) {
							// 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
							updateManager.applyUpdate();
						}
					}
				});
			});
		}
	});

	updateManager.onUpdateFailed(function(res) {
		// 新的版本下载失败
		uni.showModal({
			title: '提示',
			content: '检查到有新版本，但下载失败，请检查网络设置',
			success(res) {
				if (res.confirm) {
					// 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
					updateManager.applyUpdate();
				}
			}
		});
	});

	if (HTTP_REQUEST_URL == '') {
		console.error(
			"请配置根目录下的config.js文件中的 'HTTP_REQUEST_URL'\n\n请修改开发者工具中【详情】->【AppID】改为自己的Appid\n\n请前往后台【小程序】->【小程序配置】填写自己的 appId and AppSecret"
		);
		return false;
	}
	//小程序扫码进入场景
	if (launchQuery.hasOwnProperty('scene')) {
		switch (option.scene) {
			case 1047: //扫描小程序码
			case 1048: //长按图片识别小程序码
			case 1049: //手机相册选取小程序码
			case 1001: //直接进入小程序

			let value = proxy.$util.getUrlParams(decodeURIComponent(launchQuery.scene));
			let mapeMpQrCodeValue = proxy.$util.formatMpQrCodeData(value);
			app.globalData = Object.assign(app.globalData, mapeMpQrCodeValue);
			app.globalData.spread = mapeMpQrCodeValue.spread ? mapeMpQrCodeValue.spread : '';
			break;
		}
	}
	if (option.spread) app.globalData.spread = option.spread;
	// #endif
	// 获取导航高度；
	uni.getSystemInfo({
		success: function(res) {
			app.globalData.navHeight = res.statusBarHeight * (750 / res.windowWidth) + 91;
		}
	});
	// #ifdef MP
	let menuButtonInfo = uni.getMenuButtonBoundingClientRect();
	app.globalData.navH = menuButtonInfo.top * 2 + menuButtonInfo.height / 2;
	// #endif

	// #ifdef H5
	if (launchQuery.hasOwnProperty('type') && launchQuery.type == "iframeVisualizing") {
		app.globalData.isIframe = true;
	} else {
		app.globalData.isIframe = false;
	}

		Promise.all([loginConfigPromise]).then(() => {
			// wx公众号授权回调兼容处理
			const decodeAuthBackUrl = (backUrl, fallbackUrl) => {
				if (!backUrl) return fallbackUrl;
				try {
					return decodeURIComponent(decodeURIComponent(backUrl));
				} catch (error) {
					try {
						return decodeURIComponent(backUrl);
					} catch (e) {
						return fallbackUrl;
					}
				}
			};
			const goWechatBindPhone = (authKey, backUrl) => {
				if (!authKey) return;
				Cache.set('snsapiKey', authKey);
				const bindUrl = '/pages/users/wechat_login/index?authKey=' + encodeURIComponent(authKey) +
					(backUrl ? '&back_url=' + encodeURIComponent(backUrl) : '');
				uni.redirectTo({
					url: bindUrl,
					fail() {
						uni.navigateTo({
							url: bindUrl
						});
					}
				});
			};
			const isWechatLoginPage = location.pathname.indexOf('/pages/users/wechat_login/index') !== -1;
			//publicLoginType，公众号登录方式(单选),1微信授权，2手机号登录
			if (!isThemePreviewFlag && !appStore.isLogin && Auth.isWeixin() && app.globalData.publicLoginType == 1) {
				const {
					code
				} = launchQuery;
				if (code && code != uni.getStorageSync('snsapiCode') && !isWechatLoginPage) {
					// 存储静默授权code
					uni.setStorageSync('snsapiCode', code);
					Auth.auth(code, Cache.get('SPREAD'))
						.then(res => {
							const backUrl = launchQuery.back_url || '';
							const snRouter = decodeAuthBackUrl(backUrl, location.pathname + location.search);
							uni.setStorageSync('snRouter', snRouter);
							if (res.type === 'register') {
								goWechatBindPhone(res.key, backUrl);
								return;
							}
							if (res.type === 'login') {
								appStore.LOGIN({
									token: res.token
								});
								appStore.SETUID(res.uid);
								location.replace(snRouter);
							}
						})
						.catch(() => {});
				}
			}
		});
	// #endif

	// #ifdef MP
	// 小程序静默授权
	if (!isThemePreviewFlag && !appStore.isLogin) {
		Routine.getCode().then(code => {
			Routine.authUserInfo(code, { spread_spid: app.globalData.spread || 0 })
		})
		.catch(res => {
			uni.hideLoading();
		});
	}
	// #endif
	// 主题变色由 getThemeInfo("theme", data) 统一返回 theme_color / gradient_color 等配置。
	app.globalData.theme = uni.getStorageSync('theme') || 'theme1';
	Cache.set('theme', app.globalData.theme);
	// #ifdef H5
	window.document.documentElement.setAttribute('data-theme', app.globalData.theme);
	applyH5ThemeStyle(uni.getStorageSync('viewColor'));
	// #endif
});

onMounted(async () => {
	const appStore = useAppStore();
	if (appStore.isLogin && !Cache.get('USER_INFO')) await appStore.USERINFO();
});

onShow(() => {
	// #ifdef H5
	uni.getSystemInfo({
		success(e) {
			/* 窗口宽度大于420px且不在PC页面且不在移动设备时跳转至 PC.html 页面 */
			if (e.windowWidth > 430 && !window.top.isPC && !/iOS|Android/i.test(e.system)) {
				/* 若你的项目未设置根目录（默认为 / 时），则使用下方代码 */
				window.location.pathname = '/static/html/pc.html';
			}
		}
	})
	// #endif
});

onHide(() => {});
</script>
<style lang="scss">
	@import url("@/plugin/animate/animate.min.css");
	@import 'static/css/base.css';
	/* #ifdef MP */
	@import 'static/iconfont/iconfont.mp.css';
	/* #endif */
	/* #ifndef MP */
	@import 'static/iconfont/iconfont.css';
	/* #endif */
	@import 'static/css/guildford.css';
	@import 'static/css/style.scss';
	@import 'static/css/unocss.css';
	@import 'static/fonts/font.css';

	/* 条件编译，仅在H5平台生效 */
	// #ifdef H5
	body::-webkit-scrollbar,
	html::-webkit-scrollbar {
		display: none;
	}

	// #endif
	view {
		box-sizing: border-box;
	}

	.bg-color-red {
		background-color: #E93323;
	}

	.syspadding {
		padding-top: var(--status-bar-height);
	}

	.flex {
		display: flex;
	}
	// 导航高度重置
	.open-location {
		height: 100vh;
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
