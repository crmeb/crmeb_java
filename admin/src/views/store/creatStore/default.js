export const defaultObj = {
  image: '',
  sliderImages: [],
  sliderImage: '',
  name: '',
  intro: '',
  keyword: '',
  cateIds: [], // 商品分类id
  cateId: null, // 商品分类id传值
  unitName: '',
  sort: 0,
  isShow: false,
  tempId: '',
  header: [],
  attrValue: [
    {
      image: '',
      price: 0,
      cost: 0,
      otPrice: 0,
      stock: 0,
      barCode: '',
      weight: 0,
      volume: 0,
    },
  ],
  attrValueList: [
    {
      image: '',
      brokerage: 0,
      brokerageTwo: 0,
      price: 0.01,
      vipPrice: 0.01,
      cost: 0.01,
      otPrice: 0.01,
      stock: 0,
      barCode: '',
      itemNumber: '',
      weight: 0,
      volume: 0,
      cdkeyId: null,
      cdkeyLibraryName: '',
      expand: '',
      isShow: true,
      attrValueReservationList: [] //预约商品规格值补充信息
    },
  ], // 数据提交用
  attrList: [], // 数据提交用
  attrs: [], ////数据渲染用
  selectRule: '',
  isSub: false,
  isPaidMember: false,
  content: '',
  specType: false,
  id: undefined,
  couponIds: [],
  coupons: [],
  categoryId: 0,
  guaranteeIds: '',
  guaranteeIdsList: null,
  brandId: '',
  type: 0, // 商品类型
  isAutoSubmitAudit: false, //是否自动提审
  isAutoUp: false, //是否自动上架
  deliveryMethodList: ['1'],
  deliveryMethod: '',
  systemFormId: null,
  refundSwitch: true,
  timeSlotList: [],  //预约时间段
  supplementReservation: {
    advanceReservationHour: 1,
    advanceReservationSwitch: true,
    cancelOrderHour: 0,
    cancelOrderSwitch: true,
    formDataSubmitType: 1,
    remainingNumShouSwitch: true,
    reservationDateDayRange: 0,
    reservationDateEndDate: '',
    reservationDateStartDate: '',
    reservationDateType: 1, // 可约日期类型：1-每天，2-自定义
    reservationDateWeekRange: '',
    reservationDateWeekRangeList: [1,2,3,4,5,6,7],
    timeSlotClassifyType: 1, //时段划分类型：1-自动，2-自定义
    timeSlotSpan: 60, //时段时间跨度,分钟,时段自动划分有值
    timeSlotStartEndTime: '', //时段起止时间,时段自动划分有值
  },

  // 临时数据，接口不需要的字段
  temporaryData: {
    customizeTimeClone: [['', '']], // 自定义划分库存
    reservationTime: ['', ''], // 自动划分起止时间
  },
  //这俩不是后端要的数据
  timeSlotAutomatic: [] , //自动时间段，
  timeSlotCustomize: [] , // 自定义时间段，
  ///////////
};
export const objTitle = {
  price: {
    title: '售价（元）',
  },
  vipPrice: {
    title: '会员价（元）',
  },
  cost: {
    title: '成本价（元）',
  },
  otPrice: {
    title: '划线价（元）',
  },
  stock: {
    title: '库存',
  },
  barCode: {
    title: '商品编码',
  },
  itemNumber: {
    title: '商品条码',
  },
  weight: {
    title: '重量（KG）',
  },
  volume: {
    title: '体积(m³)',
  },
  brokerage: {
    title: '一级返佣(%)',
  },
  brokerageTwo: {
    title: '二级返佣(%)',
  },
};
