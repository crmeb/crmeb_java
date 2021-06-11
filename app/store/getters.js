export default {
	token: state => state.app.token,
	isLogin: state => !!state.app.token,
	backgroundColor: state => state.app.backgroundColor,
	userInfo: state => state.app.userInfo || {},
	uid: state => state.app.uid,
	homeActive: state => state.app.homeActive,
	home: state => state.app.home,
	chatUrl: state => state.app.chatUrl,
	systemPlatform: state => state.app.systemPlatform,
	productType: state => state.app.productType
};
