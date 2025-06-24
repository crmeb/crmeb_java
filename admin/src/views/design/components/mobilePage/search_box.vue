<template>
  <div class="search-box" :style="boxStyle" v-if="configObj">
    <img :src="logoUrl" alt="" v-if="configObj.logoConfig.isShow === 1 && logoUrl" />
    <div :style="contentStyle" class="box">
      <span class="ml20 line1"
        ><i class="el-icon-search" /><span>{{ hotWord }}</span></span
      >
    </div>
    <div class="capsule"><img src="@/assets/imgs/capsule.png" alt="" /></div>
  </div>
</template>

<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import { mapState } from 'vuex';
export default {
  name: 'search_box',
  cname: '搜索框',
  icon: 't-icon-zujian-sousuokuang',
  configName: 'c_search_box',
  type: 0, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'headerSerch', // 外面匹配名称
  props: {
    index: {
      type: null,
    },
    num: {
      type: null,
    },
  },
  computed: {
    ...mapState('mobildConfig', ['defaultArray']),
    //最外层盒子的样式
    boxStyle() {
      return [
        { 'border-radius': this.configObj.bgStyle.val ? this.configObj.bgStyle.val + 'px' : '0' },
        {
          background: `linear-gradient(${this.configObj.bgColor.color[0].item}, ${this.configObj.bgColor.color[1].item})`,
        },
        { margin: 0 + ' ' + this.configObj.lrConfig.val + 'px' + ' ' + 0 },
        { padding: '12px' + ' ' + '12px' + ' ' + '12px' },
      ];
    },
    //内容圆角
    contentStyle() {
      return [
        { 'border-radius': this.configObj.contentStyle.val ? this.configObj.contentStyle.val + 'px' : '0' },
        {
          background: this.configObj.borderColor.color[0].item,
        },
        {
          color: this.configObj.textColor.color[0].item,
        },
        {
          'text-align': this.configObj.textPosition.list[this.configObj.textPosition.tabVal].style,
        },
      ];
    },
  },
  watch: {
    pageData: {
      handler(nVal, oVal) {
        this.setConfig(nVal);
      },
      deep: true,
    },
    num: {
      handler(nVal, oVal) {
        let data = this.$store.state.mobildConfig.defaultArray[nVal];
        this.setConfig(data);
      },
      deep: true,
    },
    defaultArray: {
      handler(nVal, oVal) {
        let data = this.$store.state.mobildConfig.defaultArray[this.num];
        this.setConfig(data);
      },
      deep: true,
    },
  },
  data() {
    return {
      // 默认初始化数据禁止修改
      defaultConfig: {
        isHide: false,
        name: 'headerSerch',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '搜索框',
        },
        textPosition: {
          title: '文本位置',
          tabTitle: '文本位置',
          name: 'textPosition',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '居左',
              icon: 'icon-juzuo',
              style: 'left',
            },
            {
              val: '居中',
              icon: 'icon-juzhong',
              style: 'center',
            },
          ],
        },
        bgStyle: {
          title: '背景圆角',
          tabTitle: '圆角设置',
          name: 'bgStyle',
          val: 0,
          min: 0,
          max: 30,
        },
        contentStyle: {
          title: '内容圆角',
          name: 'contentStyle',
          val: 30,
          min: 0,
          max: 30,
        },
        // 背景颜色
        bgColor: {
          title: '背景颜色',
          tabTitle: '颜色设置',
          color: [
            {
              item: '#E93323',
            },
            {
              item: '#E93323',
            },
          ],
          default: [
            {
              item: '#E93323',
            },
            {
              item: '#E93323',
            },
          ],
        },
        // 框体颜色
        borderColor: {
          title: '框体颜色',
          color: [
            {
              item: '#fff',
            },
          ],
          default: [
            {
              item: '#fff',
            },
          ],
        },
        textColor: {
          title: '输入文字颜色',
          default: [
            {
              item: '#303133',
            },
          ],
          color: [
            {
              item: '#303133',
            },
          ],
        },
        hotWords: {
          title: '搜索热词',
          tabTitle: '搜索热词',
          tips: '最多可设置20个热词，鼠标拖拽左侧圆点可调整热词顺序',
          list: [
            {
              val: '兰蔻小黑瓶',
            },
          ],
        },
        placeWords: {
          title: '提示文字',
          tabTitle: '提示文字设置',
          val: '搜索商品名称',
          isShow: 1,
        },
        // prConfig: {
        //   title: '背景边距',
        //   val: 15,
        //   min: 0,
        // },
        titleConfig: {
          title: '切换时间',
          val: 3,
          place: '设置搜索热词显示时间',
          max: 100,
          unit: '秒',
          isShow: 1,
        },
        // 左右间距
        lrConfig: {
          tabTitle: '边距设置',
          title: '左右边距',
          val: 12,
          min: 0,
          max: 20,
        },
        searConfig: {
          title: '选择模板',
          tabTitle: '布局设置',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '通栏',
              icon: 'icon-tonglan',
              count: 1,
            },
            {
              val: 'logo',
              icon: 'icon-logo',
              count: 2,
            },
          ],
        },
        logoConfig: {
          isShow: 0,
          title: '默认logo',
          tips: '建议上传大小：宽138px，高48px',
          header: '设置logo',
          url: '',
        },
        logoFixConfig: {
          isShow: 0,
          title: '吸顶logo',
          tips: '建议上传大小：宽138px，高48px',
          header: '设置logo',
          url: '',
        },
      },
      // tabVal: '',
      bgColor: [],
      pageData: {},
      logoUrl: '',
      hotWord: '',
      slider: 0,
      prConfig: 0,
      configObj: null,
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.pageData = this.$store.state.mobildConfig.defaultArray[this.num];
      this.setConfig(this.pageData);
    });
  },
  methods: {
    setConfig(data) {
      if (!data) return;
      if (data) {
        this.configObj = data;
        this.bgColor = data.bgColor.color;
        this.logoUrl = data.logoConfig.url;
        this.prConfig = data.lrConfig.val;
        this.hotWord = data.hotWords.list.length > 0 ? data.hotWords.list[0].val : data.placeWords.val;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.search-box {
  display: flex;
  align-items: center;
  cursor: pointer;
  backdrop-filter: blur(15px);
  img {
    width: 66px;
    height: 33px;
    margin-right: 10px;
  }
  .box {
    flex: 1;
    height: 30px;
    line-height: 30px;
    font-size: 12px;
    padding-left: 10px;
    background: rgba(228, 228, 228, 0.9);
    position: relative;
    i {
      font-size: 13px;
      // position: absolute;
      top: 8px;
      margin-top: 2px;
    }
    .line1 {
      display: inline-block;
      width: 150px;
    }
  }
}
.capsule {
  width: 80px;
  height: 30px;
  margin-left: 7px;
  opacity: 0.8;
  img {
    width: 80px;
    height: 30px;
  }
}
</style>
