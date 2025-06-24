// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------

import Cookies from 'js-cookie';
// cookie保存的天数
import { forEach, hasOneOf, objEqual } from '@/libs/tools';
import { cloneDeep } from 'lodash';
const title = '';

// 设置setCookies；
// setToken
export const setCookies = (key, val, cookieExpires) => {
  Cookies.set(key, val, { expires: cookieExpires || 1 });
};
// 获取getCookies；
// getToken
export const getCookies = (key) => {
  return Cookies.get(key);
};

/**
 * 从cookie中移除指定的键值对
 * @param {string} key - 要移除的键名
 * @returns {boolean} - 移除成功返回true，否则返回false
 */
export const removeCookies = (key) => {
  return Cookies.remove(key);
};

/**
 * 判断一个菜单项是否有子菜单
 * @param {object} item - 菜单项对象
 * @returns {boolean} - 有子菜单返回true，否则返回false
 */
export const hasChild = (item) => {
  return item.children && item.children.length !== 0;
};

/**
 * 判断当前用户是否有权限访问该菜单项
 * @param {object} item - 菜单项对象
 * @param {array} access - 用户拥有的权限列表
 * @returns {boolean} - 有权限返回true，否则返回false
 */
const showThisMenuEle = (item, access) => {
  // 判断菜单项是否设置了权限
  if (item.meta && item.meta.access && item.meta.access.length) {
    // 判断用户是否拥有该菜单项的权限
    if (hasOneOf(item.meta.access, access)) return true;
    else return false;
  } else return true;
};

/**
 * @param {Array} list 通过路由列表得到菜单列表
 * @returns {Array}
 */
export const getMenuByRouter = (list, access) => {
  let res = [];
  forEach(list, (item) => {
    if (!item.meta || (item.meta && !item.meta.hideInMenu)) {
      let obj = {
        icon: (item.meta && item.meta.icon) || '',
        name: item.name,
        meta: item.meta,
      };
      if ((hasChild(item) || (item.meta && item.meta.showAlways)) && showThisMenuEle(item, access)) {
        obj.children = getMenuByRouter(item.children, access);
      }
      if (item.meta && item.meta.href) obj.href = item.meta.href;
      if (showThisMenuEle(item, access)) res.push(obj);
    }
  });
  return res;
};

/**
 * @param {Array} routeMetched 当前路由metched
 * @returns {Array}
 */
export const getBreadCrumbList = (route, homeRoute) => {
  let homeItem = {
    ...homeRoute,
  };
  let routeMetched = route.matched;
  if (routeMetched.some((item) => item.name === homeRoute.name)) return [homeItem];
  let res = routeMetched
    .filter((item) => {
      return item.meta === undefined || !item.meta.hideInBread;
    })
    .map((item) => {
      let meta = { ...item.meta };
      if (meta.title && typeof meta.title === 'function') {
        meta.__titleIsFunction__ = true;
        meta.title = meta.title(route);
      }
      let obj = {
        icon: (item.meta && item.meta.icon) || '',
        name: item.name,
        meta: meta,
      };
      return obj;
    });
  res = res.filter((item) => {
    return !item.meta.hideInMenu;
  });
  return [{ ...homeItem, to: homeRoute.path }, ...res];
};

/**
 * 获取处理后的路由标题
 * @param {Object} route - 路由对象
 * @returns {Object} - 处理后的路由对象
 */
export const getRouteTitleHandled = (route) => {
  // 克隆路由对象和元数据对象
  let router = { ...route };
  let meta = { ...route.meta };
  let title = '';
  // 判断元数据对象中是否存在标题属性
  if (meta.title) {
    // 如果标题属性是函数类型，则调用该函数并将结果赋值给title
    if (typeof meta.title === 'function') {
      meta.__titleIsFunction__ = true;
      title = meta.title(router);
    } else title = meta.title;
  }
  // 将处理后的标题赋值给元数据对象中的title属性
  meta.title = title;
  // 将处理后的元数据对象赋值给路由对象中的meta属性
  router.meta = meta;
  // 返回处理后的路由对象
  return router;
};

/**
 * 显示路由标题
 * @param {Object} item - 当前路由对象
 * @param {Object} vm - Vue实例对象
 * @returns {string|undefined} - 返回路由标题字符串或undefined
 */
export const showTitle = (item, vm) => {
  // 从路由元数据中获取标题和标题是否为函数的标志
  let { title, __titleIsFunction__ } = item.meta;
  // 如果没有标题则直接返回
  if (!title) return;
  // 如果标题不存在，则从路由对象中获取名称作为标题
  title = (item.meta && item.meta.title) || item.name;
  // 返回标题字符串
  return title;
};

/**
 * @description 本地存储和获取标签导航列表
 */
export const setTagNavListInLocalstorage = (list) => {
  localStorage.setItem('tagNaveListJavaPlat', JSON.stringify(list));
};
/**
 * @returns {Array} 其中的每个元素只包含路由原信息中的name, path, meta三项
 */
export const getTagNavListFromLocalstorage = () => {
  const list = localStorage.getItem('tagNaveListJavaPlat');
  return list ? JSON.parse(list) : [];
};

/**
 * @param {Array} routers 路由列表数组
 * @description 用于找到路由列表中name为home的对象
 */
export const getHomeRoute = (routers, homeName = 'home') => {
  let i = -1;
  let len = routers.length;
  let homeRoute = {};
  while (++i < len) {
    let item = routers[i];
    if (item.children && item.children.length) {
      let res = getHomeRoute(item.children, homeName);
      if (res.name) return res;
    } else {
      if (item.name === homeName) homeRoute = item;
    }
  }
  return homeRoute;
};

/**
 * @param {*} list 现有标签导航列表
 * @param {*} newRoute 新添加的路由原信息对象
 * @description 如果该newRoute已经存在则不再添加
 */
export const getNewTagList = (list, newRoute) => {
  const { name, path, meta } = newRoute;
  let newList = [...list];
  if (newList.findIndex((item) => item.path === path) >= 0) return newList;
  else newList.push({ name, path, meta });
  return newList;
};

/**
 * @param {*} access 用户权限数组，如 ['super_admin', 'admin']
 * @param {*} route 路由列表
 */
const hasAccess = (access, route) => {
  if (route.meta && route.meta.access) return hasOneOf(access, route.meta.access);
  else return true;
};

/**
 * 权鉴
 * @param {*} name 即将跳转的路由name
 * @param {*} access 用户权限数组
 * @param {*} routes 路由列表
 * @description 用户是否可跳转到该页
 */
export const canTurnTo = (name, access, routes) => {
  const routePermissionJudge = (list) => {
    return list.some((item) => {
      if (item.children && item.children.length) {
        return routePermissionJudge(item.children);
      } else if (item.name === name) {
        return hasAccess(access, item);
      }
    });
  };

  return routePermissionJudge(routes);
};

/**
 * @param {String} url
 * @description 从URL中解析参数
 */
export const getParams = (url) => {
  const keyValueArr = url.split('?')[1].split('&');
  let paramObj = {};
  keyValueArr.forEach((item) => {
    const keyValue = item.split('=');
    paramObj[keyValue[0]] = keyValue[1];
  });
  return paramObj;
};

/**
 * @param {Array} list 标签列表
 * @param {String} name 当前关闭的标签的name
 */
export const getNextRoute = (list, route) => {
  let res = {};
  if (list.length === 2) {
    res = getHomeRoute(list);
  } else {
    const index = list.findIndex((item) => routeEqual(item, route));
    if (index === list.length - 1) res = list[list.length - 2];
    else res = list[index + 1];
  }
  return res;
};

/**
 * @param {Number} times 回调函数需要执行的次数
 * @param {Function} callback 回调函数
 */
export const doCustomTimes = (times, callback) => {
  let i = -1;
  while (++i < times) {
    callback(i);
  }
};

/**
 * @param {Object} file 从上传组件得到的文件对象
 * @returns {Promise} resolve参数是解析后的二维数组
 * @description 从Csv文件中解析出表格，解析成二维数组
 */
export const getArrayFromFile = (file) => {
  let nameSplit = file.name.split('.');
  let format = nameSplit[nameSplit.length - 1];
  return new Promise((resolve, reject) => {
    let reader = new FileReader();
    reader.readAsText(file); // 以文本格式读取
    let arr = [];
    reader.onload = function (evt) {
      let data = evt.target.result; // 读到的数据
      let pasteData = data.trim();
      arr = pasteData
        .split(/[\n\u0085\u2028\u2029]|\r\n?/g)
        .map((row) => {
          return row.split('\t');
        })
        .map((item) => {
          return item[0].split(',');
        });
      if (format === 'csv') resolve(arr);
      else reject(new Error('[Format Error]:你上传的不是Csv文件'));
    };
  });
};

/**
 * @param {Array} array 表格数据二维数组
 * @returns {Object} { columns, tableData }
 * @description 从二维数组中获取表头和表格数据，将第一行作为表头，用于在表格中展示数据
 */
export const getTableDataFromArray = (array) => {
  let columns = [];
  let tableData = [];
  if (array.length > 1) {
    let titles = array.shift();
    columns = titles.map((item) => {
      return {
        title: item,
        key: item,
      };
    });
    tableData = array.map((item) => {
      let res = {};
      item.forEach((col, i) => {
        res[titles[i]] = col;
      });
      return res;
    });
  }
  return {
    columns,
    tableData,
  };
};

/**
 * 查找元素的上一个指定标签名的父节点
 * @param {Element} ele - 要查找的元素
 * @param {string} tag - 指定的标签名
 * @returns {Element|undefined} - 返回找到的父节点或 undefined
 */
export const findNodeUpper = (ele, tag) => {
  // 如果元素存在父节点
  if (ele.parentNode) {
    // 如果父节点的标签名与指定标签名相同
    if (ele.parentNode.tagName === tag.toUpperCase()) {
      // 返回父节点
      return ele.parentNode;
    } else {
      // 递归查找父节点的父节点
      return findNodeUpper(ele.parentNode, tag);
    }
  }
};

/**
 * 根据类名查找元素的上级节点
 * @param {HTMLElement} ele - 目标元素
 * @param {string[]} classes - 目标类名数组
 * @returns {HTMLElement|null} - 返回符合条件的上级节点，若不存在则返回 null
 */
export const findNodeUpperByClasses = (ele, classes) => {
  // 获取目标元素的父节点
  let parentNode = ele.parentNode;
  if (parentNode) {
    // 获取父节点的类名列表
    let classList = parentNode.classList;
    if (
      // 判断父节点是否存在目标类名
      classList &&
      classes.every((className) => classList.contains(className))
    ) {
      // 返回符合条件的父节点
      return parentNode;
    } else {
      // 递归查找父节点的父节点
      return findNodeUpperByClasses(parentNode, classes);
    }
  }
};

/**
 * 从当前元素向下查找指定标签名的子元素
 * @param {HTMLElement} ele - 当前元素
 * @param {string} tag - 目标标签名
 * @returns {HTMLElement|undefined} - 返回找到的子元素或 undefined
 */
export const findNodeDownward = (ele, tag) => {
  // 将目标标签名转换为大写字母
  const tagName = tag.toUpperCase();
  // 如果当前元素存在子节点
  if (ele.childNodes.length) {
    let i = -1;
    let len = ele.childNodes.length;
    // 遍历子节点
    while (++i < len) {
      let child = ele.childNodes[i];
      // 如果找到目标标签名的子元素，则返回该子元素
      if (child.tagName === tagName) return child;
      // 否则递归查找子元素的子元素
      else return findNodeDownward(child, tag);
    }
  }
};

/**
 * 根据用户权限判断是否可以查看某个页面或操作
 * @param {Array} access - 当前用户的权限数组
 * @param {Array} canViewAccess - 可以查看的权限数组
 * @returns {Boolean} - 是否可以查看
 */
export const showByAccess = (access, canViewAccess) => {
  // 调用 hasOneOf 函数判断当前用户是否有查看权限
  return hasOneOf(canViewAccess, access);
};

/**
 * @description 根据name/params/query判断两个路由对象是否相等
 * @param {*} route1 路由对象
 * @param {*} route2 路由对象
 */
export const routeEqual = (route1, route2) => {
  const params1 = route1.params || {};
  const params2 = route2.params || {};
  const query1 = route1.query || {};
  const query2 = route2.query || {};
  return route1.name === route2.name && objEqual(params1, params2) && objEqual(query1, query2);
};

/**
 * 判断打开的标签列表里是否已存在这个新添加的路由对象
 */
export const routeHasExist = (tagNavList, routeItem) => {
  let len = tagNavList.length;
  let res = false;
  doCustomTimes(len, (index) => {
    if (routeEqual(tagNavList[index], routeItem)) res = true;
  });
  return res;
};

export const localSave = (key, value) => {
  localStorage.setItem(key, value);
};

export const localRead = (key) => {
  return localStorage.getItem(key) || '';
};

// scrollTop animation
export const scrollTop = (el, from = 0, to, duration = 500, endCallback) => {
  if (!window.requestAnimationFrame) {
    window.requestAnimationFrame =
      window.webkitRequestAnimationFrame ||
      window.mozRequestAnimationFrame ||
      window.msRequestAnimationFrame ||
      function (callback) {
        return window.setTimeout(callback, 1000 / 60);
      };
  }
  const difference = Math.abs(from - to);
  const step = Math.ceil((difference / duration) * 50);

  const scroll = (start, end, step) => {
    if (start === end) {
      endCallback && endCallback();
      return;
    }

    let d = start + step > end ? end : start + step;
    if (start > end) {
      d = start - step < end ? end : start - step;
    }

    if (el === window) {
      window.scrollTo(d, d);
    } else {
      el.scrollTop = d;
    }
    window.requestAnimationFrame(() => scroll(d, end, step));
  };
  scroll(from, to, step);
};

/**
 * @description 根据当前跳转的路由设置显示在浏览器标签的title
 * @param {Object} routeItem 路由对象
 * @param {Object} vm Vue实例
 */
export const setTitle = (routeItem, vm) => {
  let winTitle = localStorage.getItem('ADMIN_TITLE') || title;
  const handledRoute = getRouteTitleHandled(routeItem);
  const pageTitle = showTitle(handledRoute, vm);
  const resTitle = pageTitle ? `${winTitle} - ${pageTitle}` : winTitle;
  window.document.title = resTitle;
};

export const R = (menuList, newOpenMenus) => {
  menuList.forEach((item) => {
    let newMenu = {};
    for (let i in item) {
      if (i !== 'children') newMenu[i] = cloneDeep(item[i]);
    }
    newOpenMenus.push(newMenu);
    item.children && R(item.children, newOpenMenus);
  });
  return newOpenMenus;
};

/**
 * 获取当前菜单打开状态
 * @param {Object} to - 当前路由对象
 * @param {Array} menuList - 菜单列表
 * @returns {Array} - 当前菜单打开状态数组
 */
export function getMenuopen(to, menuList) {
  // 初始化所有菜单数组
  const allMenus = [];
  // 遍历菜单列表
  menuList.forEach((menu) => {
    // 转换菜单结构
    const menus = transMenu(menu, []);
    // 添加当前菜单到所有菜单数组
    allMenus.push({
      path: menu.path,
      openNames: [],
    });
    // 添加子菜单到所有菜单数组
    menus.forEach((item) => allMenus.push(item));
  });
  // 查找当前菜单
  const currentMenu = allMenus.find((item) => item.path === to.path);
  // 返回当前菜单打开状态数组
  return currentMenu ? currentMenu.openNames : [];
}

/**
 * 将菜单转换为可展开的形式
 * @param {Object} menu - 菜单对象
 * @param {Array} openNames - 已打开的菜单路径数组
 * @returns {Array} - 可展开的菜单数组
 */
function transMenu(menu, openNames) {
  // 判断当前菜单是否有子菜单
  if (menu.children && menu.children.length) {
    // 将当前菜单路径添加到已打开的菜单路径数组中
    const itemOpenNames = openNames.concat([menu.path]);
    // 递归处理子菜单
    return menu.children.reduce((all, item) => {
      // 将当前子菜单的路径和已打开的菜单路径数组添加到结果数组中
      all.push({
        path: item.path,
        openNames: itemOpenNames,
      });
      // 递归处理子菜单的子菜单
      const foundChildren = transMenu(item, itemOpenNames);
      // 将子菜单的子菜单添加到结果数组中
      return all.concat(foundChildren);
    }, []);
  } else {
    // 如果当前菜单没有子菜单，则直接将当前菜单添加到结果数组中
    return [menu].map((item) => {
      return {
        path: item.path,
        openNames: openNames,
      };
    });
  }
}

export function wss(wsSocketUrl) {
  let ishttps = document.location.protocol == 'https:';
  if (ishttps) {
    return wsSocketUrl.replace('ws:', 'wss:');
  } else {
    return wsSocketUrl.replace('wss:', 'ws:');
  }
}
