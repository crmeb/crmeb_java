// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
/**
 * diy自定义组件
 * */
export default {
  namespaced: true,
  state: {
    configName: '',
    pageTitle: '',
    pageName: '' || '模板',
    pageShow: 1,
    pageColor: 1,
    pagePic: 0,
    pageColorPicker: '#f5f5f5',
    pageTabVal: '0',
    pagePicUrl: '',
    returnAddress: '',
    titleColor: '#000000',
    titleBgColor: '#fff',
    // 已知组件列表默认数据 数组
    defaultArray: {},
  },
  mutations: {
    FOOTER(state, data) {
      state.pageFooter.status.title = data.title;
      state.pageFooter.menuList[2] = data.name;
    },
    /**
     * 隐藏组件，更新数据显示值
     * @constructor
     */
    UPDATESHOW(state, data) {
      state.defaultArray[data.num].isHide = !state.defaultArray[data.num].isHide;
    },
    /**
     * @description 默认配置push到数组里面
     * @param {Object} state vuex state
     * @param {Object} data
     * 把默认数据添加到默认数组里面，解耦重复组件公用一条配置的问题
     */
    ADDARRAY(state, data) {
      data.val.id = 'id' + data.val.timestamp;
      state.defaultArray[data.num] = data.val;
    },
    /**
     * @description 删除列表第几个默认数据
     * @param {Object} state vuex state
     * @param {Object} data 数据
     */
    DELETEARRAY(state, data) {
      let tempObj = delete state.defaultArray[data.num];
    },
    /**
     * @description 删除列表第几个默认数据
     * @param {Object} state vuex state
     * @param {Object} data 数据
     */
    ARRAYREAST(state, data) {
      let tempObj = delete state.defaultArray[data];
    },
    /**
     * @description 数组排序
     * @param {Object} state vuex state
     * @param {Object} data 位置index记录
     */
    defaultArraySort(state, data) {
      let newArr = objToArr(state.defaultArray);
      let sortArr = [];
      let newObj = {};
      function objToArr(data) {
        let obj = Object.keys(data);
        let m = obj.map((key) => data[key]);
        return m;
      }
      function swapArray(arr, index1, index2) {
        let oldObj = {};
        let newObj = {};
        let active = 0;
        arr.forEach((el, index) => {
          if (!el.id) {
            el.id = 'id' + el.timestamp;
          }
          data.list.forEach((item, j) => {
            if (el.id == item.id) {
              el.timestamp = item.num;
            }
          });
        });
        return arr;
      }
      if (data.oldIndex != undefined) {
        sortArr = JSON.parse(JSON.stringify(swapArray(newArr, data.newIndex, data.oldIndex)));
      } else {
        newArr.splice(data.newIndex, 0, data.element.data().defaultConfig);
        sortArr = JSON.parse(JSON.stringify(swapArray(newArr, 0, 0)));
      }
      for (let i = 0; i < sortArr.length; i++) {
        newObj[sortArr[i].timestamp] = sortArr[i];
      }
      state.defaultArray = Object.assign({}, newObj);
    },
    /**
     * @description 更新数组某一组数据
     * @param {Object} state vuex state
     * @param {Object} data
     */
    UPDATEARR(state, data) {
      for (var k in state.defaultArray) {
        if (state.defaultArray[k].id == data.val.id) {
          state.defaultArray[k] = data.val;
        }
      }
      let value = Object.assign({}, state.defaultArray);
      state.defaultArray = value;
    },
    /**
     * @description 保存组件名称
     * @param {Object} state vuex state
     * @param {string} data
     */
    SETCONFIGNAME(state, name) {
      state.configName = name;
    },
    /**
     * @description 默认组件清空
     * @param {Object} state vuex state
     * @param {string} data
     */
    SETEMPTY(state, name) {
      state.defaultArray = {};
    },
    UPTITLE(state, val) {
      state.pageTitle = val;
    },
    UPNAME(state, val) {
      state.pageName = val;
    },
    UPSHOW(state, val) {
      state.pageShow = val;
    },
    UPCOLOR(state, val) {
      state.pageColor = val;
    },
    UPPIC(state, val) {
      state.pagePic = val;
    },
    UPPICKER(state, val) {
      state.pageColorPicker = val;
    },
    UPRADIO(state, val) {
      state.pageTabVal = val;
    },
    UPPICURL(state, val) {
      state.pagePicUrl = val;
    },
    UPPReturnAddress(state, val) {
      state.returnAddress = val;
    },
    UPPTitleColor(state, val) {
      state.titleColor = val;
    },
    UPPTitleBgColor(state, val) {
      state.titleBgColor = val;
    },
    /**
     * @description 更新foot菜单配置
     * @param {Object} state vuex state
     * @param {string} data
     */
    footUpdata(state, data) {
      state.pageFooter = [];
      state.pageFooter = data;
    },
    /**
     * @description 更新foot自定义开关
     * @param {Object} state vuex state
     * @param {string} data
     */
    footStatus(state, data) {
      state.pageFooter.status.status = data;
    },
    /**
     * @description 更新foot配置
     * @param {Object} state vuex state
     * @param {string} data
     */
    footPageUpdata(state, data) {
      state.pageFooter = data;
    },
    /**
     * @description 更新title配置
     * @param {Object} state vuex state
     * @param {string} data
     */
    titleUpdata(state, data) {
      state.pageTitle = data;
    },
    /**
     * @description 更新name配置
     * @param {Object} state vuex state
     * @param {string} data
     */
    nameUpdata(state, data) {
      state.pageName = data;
    },
    //
    showUpdata(state, data) {
      state.pageShow = data;
    },
    colorUpdata(state, data) {
      state.pageColor = data;
    },
    picUpdata(state, data) {
      state.pagePic = data;
    },
    /**
     * @description 更新页面背景色
     * @param {Object} state vuex state
     * @param {string} data
     */
    pickerUpdata(state, data) {
      state.pageColorPicker = data;
    },
    radioUpdata(state, data) {
      state.pageTabVal = data;
    },
    picurlUpdata(state, data) {
      state.pagePicUrl = data;
    },
    /**
     * 更新页面设置中返回地址
     * @param state
     * @param data
     */
    returnAddressUpdata(state, data) {
      state.returnAddress = data;
    },
    /**
     * 更新页面设置中顶部颜色
     * @param state
     * @param data
     */
    titleBgColorUpdata(state, data) {
      state.titleBgColor = data;
    },
    /**
     * 更新页面设置中顶部颜色
     * @param state
     * @param data
     */
    titleColorUpdata(state, data) {
      state.titleColor = data;
    },
  },
  actions: {
    getData({ commit }, data) {},
  },
};
