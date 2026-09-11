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
import { defineStore } from 'pinia';
import { ref } from 'vue';
import foot001 from '@/assets/images/foot-001.png';
import foot002 from '@/assets/images/foot-002.png';
import foot003 from '@/assets/images/foot-003.png';
import foot004 from '@/assets/images/foot-004.png';
import foot005 from '@/assets/images/foot-005.png';
import foot006 from '@/assets/images/foot-006.png';
import foot007 from '@/assets/images/foot-007.png';
import foot008 from '@/assets/images/foot-008.png';

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
      imgList: [foot001, foot002],
      name: '首页',
      link: '/pages/index/index',
    },
    {
      imgList: [foot003, foot004],
      name: '分类',
      link: '/pages/goods_cate/goods_cate',
    },
    {
      imgList: [foot005, foot006],
      name: '购物车',
      link: '/pages/order_addcart/order_addcart',
    },
    {
      imgList: [foot007, foot008],
      name: '我的',
      link: '/pages/user/index',
    },
  ],
};

export const useMobildConfigStore = defineStore('mobildConfig', () => {
  const configName = ref('');
  const pageTitle = ref('');
  const pageName = ref('' || '模板');
  const pageShow = ref(1);
  const pageColor = ref(1);
  const pagePic = ref(0);
  const pageColorPicker = ref('#f5f5f5');
  const pageTabVal = ref('0');
  const pagePicUrl = ref('');
  const returnAddress = ref('');
  const titleColor = ref('#000000');
  const titleBgColor = ref('#fff');
  // 已知组件列表默认数据 数组
  const defaultArray = ref({});
  const bottomMenuRef = ref(JSON.parse(JSON.stringify(bottomMenu)));
  const pageFooterRef = ref(JSON.parse(JSON.stringify(pageFooter)));

  function FOOTER(data) {
    if (pageFooterRef.value.status) pageFooterRef.value.status.title = data.title;
    pageFooterRef.value.menuList[2] = data.name;
  }
  function UPBOTTOMMENU(data) {
    bottomMenuRef.value = data;
  }
  /**
   * 隐藏组件，更新数据显示值
   * @constructor
   */
  function UPDATESHOW(data) {
    defaultArray.value[data.num].isHide = !defaultArray.value[data.num].isHide;
  }
  /**
   * @description 默认配置push到数组里面
   * @param {Object} data
   * 把默认数据添加到默认数组里面，解耦重复组件公用一条配置的问题
   */
  function ADDARRAY(data) {
    data.val.id = 'id' + data.val.timestamp;
    defaultArray.value[data.num] = data.val;
  }
  /**
   * @description 删除列表第几个默认数据
   * @param {Object} data 数据
   */
  function DELETEARRAY(data) {
    let tempObj = delete defaultArray.value[data.num];
  }
  /**
   * @description 删除列表第几个默认数据
   * @param {Object} data 数据
   */
  function ARRAYREAST(data) {
    let tempObj = delete defaultArray.value[data];
  }
  /**
   * @description 数组排序
   * @param {Object} data 位置index记录
   */
  function defaultArraySort(data) {
    let newArr = objToArr(defaultArray.value);
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
      // 新增组件：Vue2 时代通过 data.element.data().defaultConfig 获取组件默认配置，
      // 迁移到 <script setup> 后组件不再暴露 data() 方法。这里以组件元数据构建
      // 最小初始配置插入，组件 onMounted 时 setConfig 会用本地 defaultConfig 补全其余字段。
      const el = data.element || {};
      const initialConfig = {
        name: el.defaultName || el.name,
        cname: el.cname,
        timestamp: el.num,
        id: el.id || 'id' + el.num,
        isHide: false,
      };
      newArr.splice(data.newIndex, 0, initialConfig);
      sortArr = JSON.parse(JSON.stringify(swapArray(newArr, 0, 0)));
    }
    for (let i = 0; i < sortArr.length; i++) {
      newObj[sortArr[i].timestamp] = sortArr[i];
    }
    defaultArray.value = Object.assign({}, newObj);
  }
  /**
   * @description 更新数组某一组数据
   * @param {Object} data
   */
  function UPDATEARR(data) {
    const val = data.val || data.data;
    if (!val) return;

    let targetKey = data.num !== undefined ? data.num : val.timestamp;
    if (targetKey === undefined || !defaultArray.value[targetKey]) {
      for (var k in defaultArray.value) {
        if (defaultArray.value[k].id == val.id) {
          targetKey = k;
          break;
        }
      }
    }
    if (targetKey === undefined) return;

    const oldVal = defaultArray.value[targetKey] || {};
    const nextVal = Object.assign({}, oldVal, val);
    nextVal.timestamp = targetKey;
    if (!nextVal.id) nextVal.id = 'id' + nextVal.timestamp;

    if (JSON.stringify(oldVal) === JSON.stringify(nextVal)) {
      return;
    }
    defaultArray.value = Object.assign({}, defaultArray.value, {
      [targetKey]: nextVal,
    });
  }
  /**
   * @description 保存组件名称
   * @param {string} name
   */
  function SETCONFIGNAME(name) {
    configName.value = name;
  }
  /**
   * @description 默认组件清空
   * @param {string} name
   */
  function SETEMPTY(name) {
    defaultArray.value = {};
  }
  function DEFAULTARRAY(data) {
    defaultArray.value = data || {};
  }
  function UPTITLE(val) {
    pageTitle.value = val;
  }
  function UPNAME(val) {
    pageName.value = val;
  }
  function UPSHOW(val) {
    pageShow.value = val;
  }
  function UPCOLOR(val) {
    pageColor.value = val;
  }
  function UPPIC(val) {
    pagePic.value = val;
  }
  function UPPICKER(val) {
    pageColorPicker.value = val;
  }
  function UPRADIO(val) {
    pageTabVal.value = val;
  }
  function UPPICURL(val) {
    pagePicUrl.value = val;
  }
  function UPPReturnAddress(val) {
    returnAddress.value = val;
  }
  function UPPTitleColor(val) {
    titleColor.value = val;
  }
  function UPPTitleBgColor(val) {
    titleBgColor.value = val;
  }
  /**
   * @description 更新foot菜单配置
   * @param {string} data
   */
  function footUpdata(data) {
    pageFooterRef.value.menuList = [];
    pageFooterRef.value.menuList = data;
  }
  /**
   * @description 更新foot自定义开关
   * @param {string} data
   */
  function footStatus(data) {
    if (pageFooterRef.value.status) pageFooterRef.value.status.status = data;
  }
  function footType(data) {
    pageFooterRef.value.navConfig.tabVal = data;
  }
  function footBottom(data) {
    pageFooterRef.value.mbConfig.val = data;
  }
  /**
   * @description 更新foot配置
   * @param {string} data
   */
  function footPageUpdata(data) {
    pageFooterRef.value = data;
  }
  function bottomMenuUpdata(data) {
    bottomMenuRef.value = data;
  }
  function RESET_BOTTOM_MENU() {
    bottomMenuRef.value = JSON.parse(JSON.stringify(bottomMenu));
  }
  function RESET_PAGE_FOOTER() {
    pageFooterRef.value = JSON.parse(JSON.stringify(pageFooter));
  }
  /**
   * @description 更新title配置
   * @param {string} data
   */
  function titleUpdata(data) {
    pageTitle.value = data;
  }
  /**
   * @description 更新name配置
   * @param {string} data
   */
  function nameUpdata(data) {
    pageName.value = data;
  }
  //
  function showUpdata(data) {
    pageShow.value = data;
  }
  function colorUpdata(data) {
    pageColor.value = data;
  }
  function picUpdata(data) {
    pagePic.value = data;
  }
  /**
   * @description 更新页面背景色
   * @param {string} data
   */
  function pickerUpdata(data) {
    pageColorPicker.value = data;
  }
  function radioUpdata(data) {
    pageTabVal.value = data;
  }
  function picurlUpdata(data) {
    pagePicUrl.value = data;
  }
  /**
   * 更新页面设置中返回地址
   * @param data
   */
  function returnAddressUpdata(data) {
    returnAddress.value = data;
  }
  /**
   * 更新页面设置中顶部颜色
   * @param data
   */
  function titleBgColorUpdata(data) {
    titleBgColor.value = data;
  }
  /**
   * 更新页面设置中顶部颜色
   * @param data
   */
  function titleColorUpdata(data) {
    titleColor.value = data;
  }
  function getData(data) {}

  return {
    configName,
    pageTitle,
    pageName,
    pageShow,
    pageColor,
    pagePic,
    pageColorPicker,
    pageTabVal,
    pagePicUrl,
    returnAddress,
    titleColor,
    titleBgColor,
    defaultArray,
    bottomMenu: bottomMenuRef,
    pageFooter: pageFooterRef,
    FOOTER,
    UPBOTTOMMENU,
    UPDATESHOW,
    ADDARRAY,
    DELETEARRAY,
    ARRAYREAST,
    defaultArraySort,
    UPDATEARR,
    SETCONFIGNAME,
    SETEMPTY,
    DEFAULTARRAY,
    UPTITLE,
    UPNAME,
    UPSHOW,
    UPCOLOR,
    UPPIC,
    UPPICKER,
    UPRADIO,
    UPPICURL,
    UPPReturnAddress,
    UPPTitleColor,
    UPPTitleBgColor,
    footUpdata,
    footStatus,
    footType,
    footBottom,
    footPageUpdata,
    bottomMenuUpdata,
    RESET_BOTTOM_MENU,
    RESET_PAGE_FOOTER,
    titleUpdata,
    nameUpdata,
    showUpdata,
    colorUpdata,
    picUpdata,
    pickerUpdata,
    radioUpdata,
    picurlUpdata,
    returnAddressUpdata,
    titleBgColorUpdata,
    titleColorUpdata,
    getData,
  };
});
