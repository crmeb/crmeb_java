// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { login, logout, getInfo } from '@/api/user';
import { getToken, setToken, removeToken } from '@/utils/auth';
import router, { resetRouter } from '@/router';
import { isLoginApi } from '@/api/sms';
import Cookies from 'js-cookie';
import { Loading } from 'element-ui';
import * as roleApi from '@/api/roleApi.js';
import { formatFlatteningRoutes } from '@/utils/system.js';
import * as Auth from '@/libs/wechat';

const state = {
  token: getToken(),
  name: '',
  avatar: '',
  introduction: '',
  roles: [],
  isLogin: Cookies.get('isLogin'),
  permissions: [],
  captcha: {
    captchaVerification: '',
    secretKey: '',
    token: '',
  }, //滑块验证token
  // 菜单数据
  menuList: JSON.parse(localStorage.getItem('MerPlatAdmin_MenuList')) || [],
  oneLvMenus: [],
  oneLvRoutes: JSON.parse(localStorage.getItem('MerPlatAdmin_oneLvRoutes')) || [],
  childMenuList: [],
};

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token;
  },
  SET_ISLOGIN: (state, isLogin) => {
    state.isLogin = isLogin;
    Cookies.set(isLogin);
  },
  SET_INTRODUCTION: (state, introduction) => {
    state.introduction = introduction;
  },
  SET_NAME: (state, name) => {
    state.name = name;
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar;
  },
  SET_ROLES: (state, roles) => {
    state.roles = roles;
  },
  SET_PERMISSIONS: (state, permissions) => {
    state.permissions = permissions;
  },
  SET_CAPTCHA: (state, captcha) => {
    state.captcha = captcha;
  },
  SET_MENU_LIST: (state, menuList) => {
    state.menuList = menuList;
  },
  setOneLvMenus(state, oneLvMenus) {
    state.oneLvMenus = oneLvMenus;
  },
  setOneLvRoute(state, oneLvRoutes) {
    state.oneLvRoutes = oneLvRoutes;
  },
  childMenuList(state, list) {
    state.childMenuList = list;
  },
};

const actions = {
  // user login
  login({ commit }, userInfo) {
    const { account, pwd, key, code, wxCode } = userInfo;
    Loading.service();
    return new Promise((resolve, reject) => {
      login(userInfo)
        .then((data) => {
          let loadingInstance = Loading.service();
          loadingInstance.close();
          commit('SET_TOKEN', data.token);
          Cookies.set('JavaInfo', JSON.stringify(data));
          setToken(data.token);
          resolve();
        })
        .catch((error) => {
          reject(error);
        });
    });
  },

  // 短信是否登录
  isLogin({ commit }, userInfo) {
    return new Promise((resolve, reject) => {
      isLoginApi()
        .then(async (res) => {
          commit('SET_ISLOGIN', res.isLogin);
          resolve(res);
        })
        .catch((res) => {
          commit('SET_ISLOGIN', false);
          reject(res);
        });
    });
  },

  // get user info
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo(state.token)
        .then((data) => {
          if (!data) {
            reject('Verification failed, please Login again.');
          }
          const { roles, account } = data;
          // roles must be a non-empty array
          if (!roles || roles.length <= 0) {
            reject('getInfo: roles must be a non-null array!');
          }

          commit('SET_ROLES', roles);
          // commit('SET_ROLES', ['admin'])
          commit('SET_NAME', account);
          // commit('SET_AVATAR', avatar)
          commit('SET_AVATAR', 'http://kaifa.crmeb.net/system/images/admin_logo.png');
          commit('SET_INTRODUCTION', 'CRMEB admin');
          commit('SET_PERMISSIONS', data.permissionsList); //权限标识
          resolve(data);
        })
        .catch((error) => {
          reject(error);
        });
    });
  },

  // user logout
  handleLogout({ commit, state, dispatch }) {
    Loading.service();
    return new Promise((resolve, reject) => {
      logout(state.token)
        .then(() => {
          let loadingInstance = Loading.service();
          loadingInstance.close();
          commit('SET_TOKEN', '');
          commit('SET_ROLES', []);
          commit('SET_PERMISSIONS', []);
          removeToken();
          resetRouter();
          // localStorage.clear();
          Cookies.remove('storeStaffList');
          Cookies.remove('JavaInfo');
          sessionStorage.removeItem('token');
          // reset visited views and cached views
          // to fixed https://github.com/PanJiaChen/vue-element-admin/issues/2485
          dispatch('tagsView/delAllViews', null, { root: true });
          resolve();
        })
        .catch((error) => {
          reject(error);
        });
    });
  },

  // remove token
  resetToken({ commit }) {
    return new Promise((resolve) => {
      commit('SET_TOKEN', '');
      commit('SET_ROLES', []);
      removeToken();
      resolve();
    });
  },
  // 设置token
  setToken({ commit }, state) {
    return new Promise((resolve) => {
      commit('SET_TOKEN', state.token);
      Cookies.set('JavaInfo', JSON.stringify(state));
      setToken(data.token);
      resolve();
    });
  },
  getMenus({ commit }) {
    function formatTwoStageRoutes(arr) {
      if (arr.length <= 0) return false;
      const newArr = [];
      const cacheList = [];
      arr.forEach((v) => {
        if (v && v.meta && v.meta.keepAlive) {
          newArr.push({ ...v });
          cacheList.push(v.name);
          this.$store.dispatch('keepAliveNames/setCacheKeepAlive', cacheList);
        }
      });
      return newArr;
    }

    return new Promise(async (resolve, reject) => {
      let accessRoutes = await roleApi.menuListApi();
      accessRoutes = replaceChildListWithChildren(accessRoutes);
      //处理移动端路由
      !Auth.isPhone()
        ? (accessRoutes = accessRoutes.filter((item) => item.path !== '/javaMobile'))
        : (accessRoutes = accessRoutes.filter((item) => item.path === '/javaMobile'));
      // let accessRoutes = formatRoutes(menusAll);
      // const accessRoutes = await dispatch('permission/generateRoutes', roles, { root: true });
      commit('SET_MENU_LIST', accessRoutes);
      localStorage.setItem('MerPlatAdmin_MenuList', JSON.stringify(accessRoutes));
      let arr = formatFlatteningRoutes(router.options.routes);
      formatTwoStageRoutes(arr);
      let routes = formatFlatteningRoutes(accessRoutes);
      localStorage.setItem('MerPlatAdmin_oneLvRoutes', JSON.stringify(routes));
      commit('setOneLvMenus', arr);
      commit('setOneLvRoute', routes);
      resolve(resolve);
    });
  },
};

// 递归函数，用于替换 childList 为 children
function replaceChildListWithChildren(data) {
  return data.map((item) => {
    // 检查是否存在 childList 字段
    if (item.childList) {
      // 递归处理 childList 中的每个子对象
      const children = replaceChildListWithChildren(item.childList);
      // 创建一个新的对象，将 childList 替换为 children
      const title = item.name;
      const path = item.component;
      return {
        ...item,
        children,
        title,
        path,
        // 删除原来的 childList 字段
        childList: undefined,
        name: undefined,
        component: undefined,
      };
    }
    // 如果不存在 childList 字段，直接返回原对象
    return item;
  });
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
};
