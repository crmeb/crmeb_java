/**
 * 主题色色值
 */
export function filterTheme(status) {
	const app = typeof getApp === 'function' ? getApp() : null;
	return uni.getStorageSync('themeColor') || (app && app.globalData && app.globalData.themeColor) || '';
}
