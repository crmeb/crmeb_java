<template>
  <div class="mobile-page" v-if="configObj" :style="boxStyle">
    <div class="menus">
      <div
        class="item line1"
        v-for="(item, index) in list"
        :class="{ on: curIndex === index }"
        :style="{
          color: curIndex === index ? (themeStyle ? configObj.checkColor.color[0].item : mobileTheme) : fontColor,
        }"
      >
        {{ item.title }} <span :style="checkColor"></span>
      </div>
    </div>
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
import { mapState, mapGetters } from 'vuex';
export default {
  name: 'nav_bar',
  configName: 'c_nav_bar',
  cname: '商品分类',
  icon: 't-icon-zujian-shangpinfenlei',
  type: 0, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'tabNav', // 外面匹配名称
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
    ...mapGetters(['mobileTheme']),
    //外部盒子
    boxStyle() {
      return [
        { 'border-radius': this.configObj.bgStyle.val ? this.configObj.bgStyle.val + 'px' : '0' },
        {
          background: `linear-gradient(${this.configObj.bgColor.color[0].item}, ${this.configObj.bgColor.color[1].item})`,
        },
        { margin: 0 + ' ' + this.configObj.lrConfig.val + 'px' + ' ' + 0 },
        // { padding: this.configObj.upConfig.val + 'px' + ' ' + '10px' + ' ' + this.configObj.downConfig.val + 'px' },
      ];
    },
    //标签文字颜色
    fontColor() {
      return this.configObj.fontColor.color[0].item;
    },
    //选中颜色
    checkColor() {
      return {
        background: this.themeStyle ? this.configObj.checkColor.color[0].item : this.mobileTheme,
      };
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
    themeStyle: {
      handler(nVal, oVal) {
        this.configObj.checkColor.isShow = this.configObj.themeStyleConfig.tabVal;
      },
      deep: true,
    },
  },
  data() {
    return {
      // 默认初始化数据禁止修改
      defaultConfig: {
        isHide: false,
        name: 'tabNav',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '商品分类',
        },
        listConfig: {
          title: '鼠标拖拽左侧圆点可调整选项卡顺序',
          tabTitle: '选项卡设置',
          max: 10,
          list: [
            {
              title: '精选',
              val: '',
              type: 0,
            },
            {
              title: '靓丽美妆',
              val: '',
              type: 0,
            },
          ],
        },
        status: {
          title: '开关',
          default: {
            status: false,
          },
        },
        fontColor: {
          tabTitle: '标签设置',
          title: '文字颜色',
          name: 'fontColor',
          color: [
            {
              item: '#282828',
            },
          ],
          default: [
            {
              item: '#282828',
            },
          ],
        },
        //色调
        themeStyleConfig: {
          title: '色调',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '跟随主题风格',
            },
            {
              val: '自定义',
            },
          ],
        },
        checkColor: {
          title: '选中颜色',
          name: 'checkColor',
          color: [
            {
              item: '#E93323',
            },
          ],
          default: [
            {
              item: '#E93323',
            },
          ],
        },
        // 背景颜色
        bgColor: {
          title: '背景颜色',
          tabTitle: '颜色设置',
          default: [
            {
              item: '#FFFFFF',
            },
            {
              item: '#FFFFFF',
            },
          ],
          color: [
            {
              item: '#FFFFFF',
            },
            {
              item: '#FFFFFF',
            },
          ],
        },
        // 上间距
        // upConfig: {
        //   tabTitle: '边距设置',
        //   title: '上边距',
        //   val: 0,
        //   min: 0,
        //   max: 100,
        // },
        // 下间距
        // downConfig: {
        //   title: '下边距',
        //   val: 0,
        //   min: 0,
        //   max: 100,
        // },
        // 左右间距
        lrConfig: {
          title: '左右边距',
          val: 0,
          min: 0,
          max: 30,
        },
        bgStyle: {
          tabTitle: '圆角设置',
          title: '背景圆角',
          name: 'bgStyle',
          val: 0,
          min: 0,
          max: 30,
        },
      },
      curIndex: 0,
      pageData: {},
      configObj: null,
      list: [],
      themeStyle: 0,
    };
  },
  mounted() {
    this.$nextTick(() => {
      if (this.num) {
        this.pageData = this.$store.state.mobildConfig.defaultArray[this.num];
        this.setConfig(this.pageData);
      }
    });
  },
  methods: {
    setConfig(data) {
      if (!data) return;
      if (data) {
        this.configObj = data;
        this.list = this.configObj.listConfig.list;
        this.themeStyle = data.themeStyleConfig.tabVal;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.menus {
  width: 100%;
  padding: 0 20px 0 15px;
  cursor: pointer;
  overflow: hidden;
  position: relative;
  height: 37px;
  .category {
    width: 20px;
    color: #fff;
    position: absolute;
    right: 15px;
    top: 12px;
    span {
      font-size: 18px;
    }
  }
  .item {
    position: relative;
    float: left;
    margin-right: 15px;
    text-align: center;
    color: #fff;
    font-size: 14px;
    height: 38px;
    line-height: 36px;
    // width: 60px;
    &.on {
      font-size: 15px;
      font-weight: 600 !important;
      span {
        display: block;
        position: absolute;
        left: 50%;
        bottom: 5px;
        width: 16px;
        height: 2px;
        transform: translateX(-50%);
        background: #fff;
      }
    }
  }
}
</style>
