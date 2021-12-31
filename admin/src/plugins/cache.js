const sessionCache = {
  set(key, value) {
    if (!sessionStorage) {
      return;
    }
    if (key != null && value != null) {
      sessionStorage.setItem(key, value);
    }
  },
  get(key) {
    if (!sessionStorage) {
      return null;
    }
    if (key == null) {
      return null;
    }
    return sessionStorage.getItem(key);
  },
  setJSON(key, jsonValue) {
    if (jsonValue != null) {
      this.set(key, JSON.stringify(jsonValue));
    }
  },
  getJSON(key) {
    const value = this.get(key);
    if (value != null) {
      return JSON.parse(value);
    }
  },
  remove(key) {
    sessionStorage.removeItem(key);
  }
};
const localCache = {
  set(key, value) {
    if (!localStorage) {
      return;
    }
    if (key != null && value != null) {
      localStorage.setItem(key, value);
    }
  },
  get(key) {
    if (!localStorage) {
      return null;
    }
    if (key == null) {
      return null;
    }
    return localStorage.getItem(key);
  },
  setJSON(key, jsonValue) {
    if (jsonValue != null) {
      this.set(key, JSON.stringify(jsonValue));
    }
  },
  getJSON(key) {
    const value = this.get(key);
    if (value != null) {
      return JSON.parse(value);
    }
  },
  remove(key) {
    localStorage.removeItem(key);
  },
	// 检测缓存是否存在
	has(key)
	{
		return localStorage.getItem(key) ? true:false;
	},
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
        localStorage.setItem(options.name,JSON.stringify(options));
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
        localStorage.setItem(options.name,options.value);
    }
  }
};

export default {
  /**
   * 会话级缓存
   */
  session: sessionCache,
  /**
   * 本地缓存
   */
  local: localCache
};
