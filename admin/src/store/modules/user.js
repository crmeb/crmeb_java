// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { defineStore } from 'pinia';
import { ref, reactive } from 'vue';
import { login as loginApi, logout, getInfo as getInfoApi } from '@/api/user';
import { getToken, setToken, removeToken } from '@/utils/auth';
import router, { resetRouter } from '@/router';
import { isLoginApi } from '@/api/sms';
import Cookies from 'js-cookie';
import { ElLoading } from '@/utils/elementPlusFeedback';
import * as roleApi from '@/api/roleApi.js';
import { formatFlatteningRoutes } from '@/utils/system.js';
import { useTagsViewStore } from './tagsView';

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken());
  const name = ref('');
  const avatar = ref('');
  const introduction = ref('');
  const roles = ref([]);
  const isLogin = ref(Cookies.get('isLogin'));
  const permissions = ref([]);
  const captcha = reactive({
    captchaVerification: '',
    secretKey: '',
    token: '',
  });
  // 菜单数据
  const menuList = ref(JSON.parse(localStorage.getItem('MerPlatAdmin_MenuList')) || []);
  const oneLvMenus = ref([]);
  const oneLvRoutes = ref(JSON.parse(localStorage.getItem('MerPlatAdmin_oneLvRoutes')) || []);
  const childMenuList = ref([]);

  // mutations
  function SET_TOKEN(val) {
    token.value = val;
  }
  function SET_ISLOGIN(val) {
    isLogin.value = val;
    Cookies.set(val);
  }
  function SET_INTRODUCTION(val) {
    introduction.value = val;
  }
  function SET_NAME(val) {
    name.value = val;
  }
  function SET_AVATAR(val) {
    avatar.value = val;
  }
  function SET_ROLES(val) {
    roles.value = val;
  }
  function SET_PERMISSIONS(val) {
    permissions.value = val;
  }
  function SET_CAPTCHA(val) {
    Object.assign(captcha, val);
  }
  function SET_MENU_LIST(list) {
    menuList.value = list;
  }
  function setOneLvMenus(list) {
    oneLvMenus.value = list;
  }
  function setOneLvRoute(list) {
    oneLvRoutes.value = list;
  }
  function setChildMenuList(list) {
    childMenuList.value = list;
  }

  // user login
  function login(userInfo) {
    const { account, pwd, key, code, wxCode } = userInfo;
    ElLoading.service();
    return new Promise((resolve, reject) => {
      loginApi(userInfo)
        .then((data) => {
          let loadingInstance = ElLoading.service();
          loadingInstance.close();
          SET_TOKEN(data.token);
          Cookies.set('JavaInfo', JSON.stringify(data));
          setToken(data.token);
          resolve();
        })
        .catch((error) => {
          reject(error);
        });
    });
  }

  // 短信是否登录（原 Vuex action 名 isLogin 与 state.isLogin 同名，Pinia 中改名）
  function checkIsLogin() {
    return new Promise((resolve, reject) => {
      isLoginApi()
        .then(async (res) => {
          SET_ISLOGIN(res.isLogin);
          resolve(res);
        })
        .catch((res) => {
          SET_ISLOGIN(false);
          reject(res);
        });
    });
  }

  // get user info
  function getInfo() {
    return new Promise((resolve, reject) => {
      getInfoApi(token.value)
        .then((data) => {
          if (!data) {
            reject('Verification failed, please Login again.');
          }
          const { roles: _roles, account } = data;
          // roles must be a non-empty array
          if (!_roles || _roles.length <= 0) {
            reject('getInfo: roles must be a non-null array!');
          }

          SET_ROLES(_roles);
          SET_NAME(account);
          SET_AVATAR('http://kaifa.crmeb.net/system/images/admin_logo.png');
          SET_INTRODUCTION('CRMEB admin');
          SET_PERMISSIONS(data.permissionsList); //权限标识
          resolve(data);
        })
        .catch((error) => {
          reject(error);
        });
    });
  }

  // user logout
  function handleLogout() {
    ElLoading.service();
    return new Promise((resolve, reject) => {
      logout(token.value)
        .then(() => {
          let loadingInstance = ElLoading.service();
          loadingInstance.close();
          SET_TOKEN('');
          SET_ROLES([]);
          SET_PERMISSIONS([]);
          removeToken();
          resetRouter();
          Cookies.remove('JavaInfo');
          sessionStorage.removeItem('token');
          // reset visited views and cached views
          useTagsViewStore().delAllViews(null);
          resolve();
        })
        .catch((error) => {
          reject(error);
        });
    });
  }

  // remove token
  function resetToken() {
    return new Promise((resolve) => {
      SET_TOKEN('');
      SET_ROLES([]);
      removeToken();
      resolve();
    });
  }

  // 设置token（注意：原代码存在引用未定义 data 的 bug，此处保持原行为用 state 入参）
  function setTokenState(state) {
    return new Promise((resolve) => {
      SET_TOKEN(state.token);
      Cookies.set('JavaInfo', JSON.stringify(state));
      setToken(state.token);
      resolve();
    });
  }

  function getMenus() {
    function formatTwoStageRoutes(arr) {
      if (arr.length <= 0) return false;
      const newArr = [];
      const cacheList = [];
      arr.forEach((v) => {
        if (v && v.meta && v.meta.keepAlive) {
          newArr.push({ ...v });
          cacheList.push(v.name);
        }
      });
      return newArr;
    }

    return new Promise(async (resolve, reject) => {
      let accessRoutes = await roleApi.menuListApi();
      accessRoutes = replaceChildListWithChildren(accessRoutes);
      SET_MENU_LIST(accessRoutes);
      localStorage.setItem('MerPlatAdmin_MenuList', JSON.stringify(accessRoutes));
      let arr = formatFlatteningRoutes(router.options.routes);
      formatTwoStageRoutes(arr);
      let routes = formatFlatteningRoutes(accessRoutes);
      localStorage.setItem('MerPlatAdmin_oneLvRoutes', JSON.stringify(routes));
      setOneLvMenus(arr);
      setOneLvRoute(routes);
      resolve(resolve);
    });
  }

  return {
    token,
    name,
    avatar,
    introduction,
    roles,
    isLogin,
    permissions,
    captcha,
    menuList,
    oneLvMenus,
    oneLvRoutes,
    childMenuList,
    SET_TOKEN,
    SET_ISLOGIN,
    SET_INTRODUCTION,
    SET_NAME,
    SET_AVATAR,
    SET_ROLES,
    SET_PERMISSIONS,
    SET_CAPTCHA,
    SET_MENU_LIST,
    setOneLvMenus,
    setOneLvRoute,
    setChildMenuList,
    login,
    checkIsLogin,
    getInfo,
    handleLogout,
    resetToken,
    setToken: setTokenState,
    getMenus,
  };
});

// 递归函数，用于替换 childList 为 children
function replaceChildListWithChildren(data) {
  return data.map((item) => {
    // 检查是否存在 childList 字段
    if (item.childList) {
      const children = replaceChildListWithChildren(item.childList);
      const title = item.name;
      const path = item.component;
      return {
        ...item,
        children,
        title,
        path,
        childList: undefined,
        name: undefined,
        component: undefined,
      };
    }
    return item;
  });
}
