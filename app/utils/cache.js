// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2021 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { EXPIRE } from '../config/app';

class Cache {
	
	constructor(handler) {
	    this.cacheSetHandler = uni.setStorageSync;
		this.cacheGetHandler = uni.getStorageSync;
		this.cacheClearHandler = uni.removeStorageSync;
		this.cacheExpire = '_expire_2019_12_17_18_44';
		this.name = 'storage';
	}
	
	/**
	 * 获取当前时间戳
	 */
	time()
	{
		return Math.round(new Date() / 1000);
	}
	
	/**
	 * 日期字符串转时间戳
	 * @param {Object} expiresTime
	 */
	strTotime(expiresTime){
		let expires_time = expiresTime.substring(0, 19);
		expires_time = expires_time.replace(/-/g, '/');
		return Math.round(new Date(expires_time).getTime() / 1000);
	}
	
	setExpireCaheTag(key, expire) {
		expire = expire !== undefined ? expire : EXPIRE;
		if (typeof expire === 'number') {
			let tag = this.cacheGetHandler(this.cacheExpire), newTag = [],newKeys = [];
			if (typeof tag === 'object' && tag.length) {
				newTag = tag.map(item => {
					newKeys.push(item.key);
					if (item.key === key) {
						item.expire = expire === 0 ? 0 : this.time() + expire;
					}
					return item;
				});
			} 
			if (!newKeys.length || newKeys.indexOf(key) === -1) {
				newTag.push({
					key: key,
					expire: expire === 0 ? 0 : this.time() + expire
				});
			}
			this.cacheSetHandler(this.cacheExpire, newTag);
		}
	}
	
	/**
	 * 设置过期时间缓存
	 * @param {Object} name key
	 * @param {Object} value value
	 * @param {Object} expire 过期时间
	 * @param {Object} startTime 记录何时将值存入缓存，毫秒级
	 */
	setItem(params){
	    let obj = {
	        name:'',
	        value:'',
	        expires:"",
	        startTime:new Date().getTime()
	    }
	    let options = {};
	    //将obj和传进来的params合并
	    Object.assign(options,obj,params);
	    if(options.expires){
			//如果options.expires设置了的话
			//以options.name为key，options为值放进去
	        // localStorage.setItem(options.name,JSON.stringify(options));
			uni.setStorageSync(options.name,JSON.stringify(options));
	    }else{
			//如果options.expires没有设置，就判断一下value的类型
	       	let type = Object.prototype.toString.call(options.value);
	       	//如果value是对象或者数组对象的类型，就先用JSON.stringify转一下，再存进去
	        if(Object.prototype.toString.call(options.value) == '[object Object]'){
	            options.value = JSON.stringify(options.value);
	        }
	        if(Object.prototype.toString.call(options.value) == '[object Array]'){
	            options.value = JSON.stringify(options.value);
	        }
	        // localStorage.setItem(options.name,options.value);
			uni.setStorageSync(options.name,options.value);
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
			this.setExpireCaheTag(key,expire);
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
	
	/**
	 * 获取缓存，调用后无需转换数据类型
	 * @param {Object} key
	 */
	getItem(name){
	    // let item = localStorage.getItem(name);
		let item = uni.getStorageSync(name);
	    //先将拿到的试着进行json转为对象的形式
	    try{
	        item = JSON.parse(item);
	    }catch(error){
	    //如果不行就不是json的字符串，就直接返回
	        item = item;
	    }
	    //如果有startTime的值，说明设置了失效时间
	    if(item.startTime){
	        let date = new Date().getTime();
	        //何时将值取出减去刚存入的时间，与item.expires比较，如果大于就是过期了，如果小于或等于就还没过期
	        if(date - item.startTime > item.expires){
	        //缓存过期，清除缓存，返回false
	            // localStorage.removeItem(name);
				uni.removeStorageSync(name);
	            return false;
	        }else{
	        //缓存未过期，返回值
	            return item.value;
	        }
	    }else{
	    //如果没有设置失效时间，直接返回值
	        return item;
	    }
	}
}


export default new Cache;