import store from '../store';
import { checkLogin } from './login';
import { login } from '../api/public';
import Cache from '../utils/cache';
import { STATE_R_KEY, USER_INFO, EXPIRES_TIME, LOGIN_STATUS} from './../config/cache';
class Routine 
{
	
	constructor() 
	{
	    this.scopeUserInfo = 'scope.userInfo';
	}
	
	async getUserCode(){
		let isAuth = await this.isAuth(), code = '' ;
		if(isAuth)
			code = await this.getCode();
		return code;
	}
	
	/**
	 * 获取用户信息
	 */
	getUserProfile(){
		let  that = this , code = this.getUserCode();
		return new Promise( (resolve,reject) => {
			uni.getUserProfile({
				lang: 'zh_CN',
				desc: '用于完善会员资料', // 声明获取用户个人信息后的用途，后续会展示在弹窗中，请谨慎填写
				success(user) {
					if(code) user.code = code;
					resolve({userInfo:user,islogin:false});
				},
				fail(res){
					reject(res);
				}
			})
		})
	}
	
	/**
	 * 获取用户信息
	 */
	authorize()
	{
		let that = this;
		return new Promise((resolve,reject)=>{
			if(checkLogin())
				return resolve({
					userInfo:Cache.get(USER_INFO,true),
					islogin:true,
				});
			uni.authorize({
			    scope: that.scopeUserInfo,
			    success() {
					resolve({islogin:false});
			    },
				fail(res){
					reject(res);
				}
			})
		})
	}
	
	async getCode(){
		let provider = await this.getProvider();
		return new Promise((resolve,reject)=>{
			uni.login({
				provider:provider,
				success(res) {
					if (res.code) Cache.set(STATE_R_KEY, res.code ,10800);
					return resolve(res.code);
				},
				fail(){
					return reject(null);
				}
			})
		})
	}
	
	/**
	 * 获取服务供应商
	 */
	getProvider()
	{
		return new Promise((resolve,reject)=>{
			uni.getProvider({
				service:'oauth',
				success(res) {
					resolve(res.provider);
				},
				fail() {
					resolve(false);
				}
			});
		});
	}
	
	/**
	 * 是否授权
	 */
	isAuth(){
		let that = this;
		return new Promise((resolve,reject)=>{
			uni.getSetting({
				success(res) {
					if (!res.authSetting[that.scopeUserInfo]) {
						resolve(true)
					} else {
						resolve(true);
					}
				},
				fail(){
					 resolve(false);
				}
			});
		});
	}
	/**
	 * 小程序登录
	 */
	authUserInfo(code,data)
	{
		return new Promise((resolve, reject)=>{
			login(code,data).then(res=>{
				if(res.data.type==='login'){
					store.commit('LOGIN', {
						token: res.data.token
					});
					store.commit("SETUID", res.data.uid);
				}
				return resolve(res);
			}).catch(res=>{
				return reject(res);
			})
		})
	}
}

export default new Routine();