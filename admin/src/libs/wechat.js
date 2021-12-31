// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2021 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import WechatJSSDK from "wechat-jssdk/dist/client.umd";
import { getWechatConfig, wechatAuth } from "@/api/wxApi";
import { getToken, removeToken, setToken } from '@/utils/auth'
import { parseQuery } from "@/utils";
import Cookies from 'js-cookie'
const STATE_KEY = "wx_authorize_state";
import store from "@/store";
const WX_AUTH = "wx_auth";
const BACK_URL = "login_back_url";
const LOGINTYPE = "loginType";
let instance;
let wechatObj;
const LONGITUDE = "user_longitude";
const LATITUDE = "user_latitude";

const WECHAT_SCRIPT_URL = "//res.wx.qq.com/open/js/jweixin-1.6.0.js";

/**
 * 是否是微信
 */
export function isWeixin() {
  return navigator.userAgent.toLowerCase().indexOf("micromessenger") !== -1;
}

/**
 * 是否是移动端
 */
export function isPhone() {
  return /(iPhone|iPad|iPod|iOS|Android)/i.test(navigator.userAgent);
}

export default function wechat() {
  return new Promise((resolve, reject) => {
    if (instance) return resolve(instance);
    getWechatConfig()
      .then(res => {
        const _wx = WechatJSSDK(res);
        wechatObj = _wx;
        _wx
          .initialize()
          .then(() => {
            instance = _wx.wx;
            instance.initConfig = res;
            resolve(instance);
          })
          .catch(reject);
      })
      .catch(err => {
        reject(err);
      });
  });
}

export function loginByWxCode(code) {
  return new Promise((resolve, reject) => {
    let loginType = getToken();
    wechatAuth(code)
      .then((res) => {
        store.commit('SET_TOKEN', res.token)
        setToken(res.token)
        Cookies.set(WX_AUTH, code);
        resolve(res);
      })
      .catch((err)=>{
        reject(err);
      });
  });
}

export function getWXCodeByUrl(path, step) {
  if( getToken() ) return;
  generatorWxUrl(path, step);
}

export function generatorWxUrl(path, step) {
  wechat().then(wx => {
    window.location.href = getAuthUrl(wx.initConfig, path, step);
  });
}

function getAuthUrl(config, path, step) {
  const finalUrl = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${config.appId}&redirect_uri=${encodeURIComponent(path)}&response_type=code&scope=snsapi_base&state=${step}#wechat_redirect`;
  return finalUrl;
}

function getQueryString(name) {
  var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
  var reg_rewrite = new RegExp("(^|/)" + name + "/([^/]*)(/|$)", "i");
  var r = window.location.search.substr(1).match(reg);
  var q = window.location.pathname.substr(1).match(reg_rewrite);
  if(r != null){
    return unescape(r[2]);
  }else if(q != null){
    return unescape(q[2]);
  }else{
    return null;
  }
}

/**
 * 公众号事件
 * @param name 事件名
 * @param config 配置
 * @returns {Promise<unknown>}
 */
export function wechatEvevt(name, config) {
  return new Promise((resolve, reject) => {
    let wx;
    let configDefault = {
      fail(res) {
        if (wx) return reject({ is_ready: true, wx: wx });
        getWechatConfig().then(res => {
          wechatObj.signSignature({
            nonceStr: res.nonceStr,
            signature: res.signature,
            timestamp: res.timestamp
          });
          wx = wechatObj.getOriginalWx();
          reject({ is_ready: true, wx: wx });
        });
      },
      success(res) {
        resolve(res);
      },
      cancel(err) {
        reject(err);
      },
      complete(err) {
        reject(err);
      }
    };
    Object.assign(configDefault, config);
    getWechatConfig().then(res => {
      const _wx = WechatJSSDK(res);
      _wx.initialize().then(() => {
        instance = _wx.getOriginalWx();
        instance.ready(() => {
          if (typeof name === "object") {
            name.forEach(item => {
              instance[item] && instance[item](configDefault);
            });
          } else instance[name] && instance[name](configDefault);
        });
      });
    });
  });
}

export function ready() {
  return new Promise(resolve => {
    if (typeof instance !== "undefined") {
      instance.ready(() => {
        resolve(instance);
      });
    } else {
      getWechatConfig().then(res => {
        const _wx = WechatJSSDK(res);
        _wx.initialize().then(() => {
          instance = _wx.wx;
          instance.ready(() => {
            resolve(instance);
          });
        });
      });
    }
  });
}
