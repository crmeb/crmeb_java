import { EXPIRE } from '../config/app';

class Cache {
	
	constructor(handler) {
	    this.cacheSetHandler = uni.setStorageSync;
		this.cacheGetHandler = uni.getStorageSync;
		this.cacheClearHandler = uni.removeStorageSync;
		this.cacheExpire = '_expire_2019_12_17_18_44';
	}
	
	/**
	 * 获取当前时间戳
	 */
	time()
	{
		return Math.round(new Date() / 1000);
	}
	
	/**
	 * 字符串转时间戳
	 * @param {Object} expiresTime
	 */
	strTotime(expiresTime){
		let expires_time = expiresTime.substring(0, 19);
		expires_time = expires_time.replace(/-/g, '/');
		return Math.round(new Date(expires_time).getTime() / 1000);
	}
	
	/**
	 * 设置过期时间缓存
	 * @param {Object} key
	 * @param {Object} expire
	 */
	setExpireCahe(key,expire)
	{
		expire = expire !== undefined ? expire : EXPIRE;
		if (expire) {
			this.cacheSetHandler(key + this.cacheExpire,this.time() + expire)
		}
	}
	
	/**
	 * 缓存是否过期,过期自动删除
	 * @param {Object} key
	 * @param {Object} $bool true = 删除,false = 不删除
	 */
	getExpireCahe(key,$bool)
	{
		try{
			let time = this.cacheGetHandler(key + this.cacheExpire);
			if (time) {
				let newTime = parseInt(time);
				if (time && time < this.time() && !Number.isNaN(newTime)) {
					if ($bool === undefined || $bool === true) {
						this.cacheClearHandler(key);
						this.cacheClearHandler(key + this.cacheExpire);
					}
					return false;
				} else
					return true;
			} else {
				return !!this.cacheGetHandler(key);
			}
		}catch(e){
			return false;
		}
	}
	
	/**
	 * 设置缓存
	 * @param {Object} key
	 * @param {Object} data
	 */
	set(key,data,expire){
		if(typeof data === 'object')
			data = JSON.stringify(data);
		try{
			this.setExpireCahe(key,expire);
			return this.cacheSetHandler(key,data);
		}catch(e){
			return false;
		}
	}
	
	/**
	 * 检测缓存是否存在
	 * @param {Object} key
	 */
	has(key)
	{
		return this.getExpireCahe(key);
	}
	
	/**
	 * 获取缓存
	 * @param {Object} key
	 * @param {Object} $default
	 * @param {Object} expire
	 */
	get(key,$default,expire){
		try{
			let isBe = this.getExpireCahe(key);
			let data = this.cacheGetHandler(key);
			if (data && isBe) {
				if (typeof $default === 'boolean')
					return JSON.parse(data);
				else
					return data;
			} else {
				if (typeof $default === 'function') {
					let value = $default();
					this.set(key,value,expire);
					return value;
				} else {
					this.set(key,$default,expire);
					return $default;
				}
			}
		}catch(e){
			return null;
		}
	}
	
	/**
	 * 删除缓存
	 * @param {Object} key
	 */
	clear(key)
	{
		try{
			let cahceValue = this.cacheGetHandler(key + this.cacheExpire);
			if(cahceValue)
				this.cacheClearHandler(key + this.cacheExpire);
			return this.cacheClearHandler(key);
		}catch(e){
			return false;
		}
	}
	
	/**
	 * 清除过期缓存
	 */
	clearOverdue()
	{
		// let cacheList = uni.getStorageInfoSync(),that = this;
		// if (typeof cacheList.keys === 'object'){
		// 	cacheList.keys.forEach(item=>{
		// 		that.getExpireCahe(item);
		// 	})
		// }
	}
}


export default new Cache;