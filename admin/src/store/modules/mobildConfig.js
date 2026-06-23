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
const bottomMenu = {
  cname: '底部菜单',
  name: 'bottomMenu',
  isHide: false,
  setUp: { tabVal: 0 },
  entryConfig: {
    title: '入口内容',
    tabVal: 0,
    tabList: [{ name: '默认' }, { name: '自定义' }],
  },
  styleTitle: '样式设置',
  contentConfigTitle: '内容设置',
  showContent: {
    title: '显示内容',
    name: 'showContent',
    type: [3, 1, 2],
    list: [
      { id: 3, name: '首页', icon: 'icon-shouye6' },
      { id: 1, name: '收藏', icon: 'icon-shoucang4' },
      { id: 2, name: '购物车', icon: 'icon-gouwuche' },
      { id: 0, name: '客服', icon: 'icon-kefu' },
      { id: 4, name: '分享', icon: 'icon-fenxiang4' },
    ],
  },
  cartButton: {
    title: '购物车按钮',
    tabVal: 0,
    tabList: [{ name: '显示' }, { name: '隐藏' }],
  },
  menuConfig: {
    title: '最多可添加1张图片，建议宽度90 * 90px',
    bnt: '添加',
    type: 1,
    listStyle: 0,
    maxList: 100,
    list: [
      {
        img: '',
        type: 0,
        show: true,
        icon: '',
        info: [
          { title: '标题', value: '标题', tips: '选填，不超过4个字', max: 4 },
          { title: '链接', value: '', tips: '请输入链接', max: 100 },
        ],
      },
    ],
  },
  buttonStyleTitle: '按钮设置',
  toneConfig: {
    title: '按钮色调',
    tabVal: 0,
    tabList: [{ name: '跟随主题风格' }, { name: '自定义' }],
  },
  cartColor: {
    title: '购物车按钮',
    default: [{ item: '#FAAD14' }, { item: '#FAAD14' }],
    color: [{ item: '#FAAD14' }, { item: '#FAAD14' }],
  },
  buyColor: {
    title: '购买按钮',
    default: [{ item: '#E93323' }, { item: '#E93323' }],
    color: [{ item: '#E93323' }, { item: '#E93323' }],
  },
  generalStyleTitle: '通用样式',
  moduleColor: {
    title: '组件背景',
    default: [{ item: '#fff' }, { item: '#fff' }],
    color: [{ item: '#fff' }, { item: '#fff' }],
  },
  bottomBgColor: {
    title: '底部背景',
    default: [{ item: '#F5F5F5' }],
    color: [{ item: '#F5F5F5' }],
  },
  c_common_style: {
    color: [{ item: '#fff' }, { item: '#fff' }],
    color2: [{ item: '#F5F5F5' }],
    lr: 0,
    type: 0,
  },
  iconColor: {
    title: '图标颜色',
    default: [{ item: '#333' }],
    color: [{ item: '#333' }],
  },
  iconSize: { title: '图标大小', val: 20, min: 10, max: 50 },
  iconRotate: { title: '旋转角度', val: 0, min: 0, max: 360 },
  padding: { title: '内边距', val: 0, min: 0, max: 50 },
  fillet: {
    title: '背景圆角',
    type: 0,
    val: 0,
    min: 0,
    valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
  },
  menuPcFillet: {
    title: '圆角设置',
    type: 0,
    val: 0,
    min: 0,
    valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
  },
};

const pageFooter = {
  cname: '底部导航',
  name: 'pageFoot',
  setUp: { tabVal: 0 },
  titleLeft: '展示设置',
  titleNav: '导航内容',
  titleRight: '颜色设置',
  titleCurrency: '通用样式',
  effectConfig: {
    title: '展示效果',
    tabVal: 1,
    tabList: [{ name: '系统默认' }, { name: '自定义' }],
  },
  navConfig: {
    title: '导航类型',
    tabVal: 0,
    tabList: [{ name: '底部固定' }, { name: '底部悬浮' }],
  },
  navStyleConfig: {
    title: '导航样式',
    tabVal: 0,
    tabList: [{ name: '图片+文字' }, { name: '文字' }, { name: '图片' }],
  },
  toneConfig: {
    title: '色调',
    tabVal: 1,
    tabList: [{ name: '跟随主题风格' }, { name: '自定义' }],
  },
  topConfig: { title: '上边距', val: 0, min: 0 },
  bottomConfig: { title: '下边距', val: 0, min: 0 },
  prConfig: { title: '左右边距', val: 10, min: 0 },
  mbConfig: { title: '页面下间距', val: 25, min: 0 },
  fillet: {
    title: '背景圆角',
    type: 0,
    val: 30,
    min: 0,
    valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
  },
  txtColor: {
    title: '文字颜色',
    name: 'txtColor',
    default: [{ item: '#282828' }],
    color: [{ item: '#282828' }],
  },
  activeTxtColor: {
    title: '选中文字颜色',
    name: 'txtColor',
    default: [{ item: '#F62C2C' }],
    color: [{ item: '#F62C2C' }],
  },
  bgColor: {
    title: '背景颜色',
    name: 'bgColor',
    default: [{ item: '#fff' }],
    color: [{ item: '#fff' }],
  },
  bgColor2: {
    title: '背景颜色',
    name: 'bgColor2',
    default: [{ item: 'rgba(255,255,255,0.8)' }],
    color: [{ item: 'rgba(255,255,255,0.8)' }],
  },
  status: {
    title: '是否自定义',
    name: 'status',
    status: false,
  },
  menuList: [
    {
      imgList: [require('@/assets/images/foot-001.png'), require('@/assets/images/foot-002.png')],
      name: '首页',
      link: '/pages/index/index',
    },
    {
      imgList: [require('@/assets/images/foot-003.png'), require('@/assets/images/foot-004.png')],
      name: '分类',
      link: '/pages/goods_cate/goods_cate',
    },
    {
      imgList: [require('@/assets/images/foot-005.png'), require('@/assets/images/foot-006.png')],
      name: '购物车',
      link: '/pages/order_addcart/order_addcart',
    },
    {
      imgList: [require('@/assets/images/foot-007.png'), require('@/assets/images/foot-008.png')],
      name: '我的',
      link: '/pages/user/index',
    },
  ],
};

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
    bottomMenu: JSON.parse(JSON.stringify(bottomMenu)),
    pageFooter: JSON.parse(JSON.stringify(pageFooter)),
  },
  mutations: {
    FOOTER(state, data) {
      if (state.pageFooter.status) state.pageFooter.status.title = data.title;
      state.pageFooter.menuList[2] = data.name;
    },
    UPBOTTOMMENU(state, data) {
      state.bottomMenu = data;
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
    DEFAULTARRAY(state, data) {
      state.defaultArray = data || {};
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
      state.pageFooter.menuList = [];
      state.pageFooter.menuList = data;
    },
    /**
     * @description 更新foot自定义开关
     * @param {Object} state vuex state
     * @param {string} data
     */
    footStatus(state, data) {
      if (state.pageFooter.status) state.pageFooter.status.status = data;
    },
    footType(state, data) {
      state.pageFooter.navConfig.tabVal = data;
    },
    footBottom(state, data) {
      state.pageFooter.mbConfig.val = data;
    },
    /**
     * @description 更新foot配置
     * @param {Object} state vuex state
     * @param {string} data
     */
    footPageUpdata(state, data) {
      state.pageFooter = data;
    },
    bottomMenuUpdata(state, data) {
      state.bottomMenu = data;
    },
    RESET_BOTTOM_MENU(state) {
      state.bottomMenu = JSON.parse(JSON.stringify(bottomMenu));
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
