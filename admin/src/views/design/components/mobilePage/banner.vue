<template>
  <div class="mobile-page" :style="boxStyle" v-if="configObj">
    <div class="banner-two">
      <div class="img-box">
        <div class="img-item itemLeft" v-if="swiperType == 0">
          <img
            class="img-left"
            :style="contentStyleLeft"
            :src="imgSrcList.length > 1 && imgSrcList[1].img"
            alt=""
            v-if="imgSrcList.length > 1 && imgSrcList[1].img"
          />
          <div :style="contentStyleLeft" class="empty-box empty-left" v-else></div>
        </div>
        <div class="img-item item-middle">
          <img
            class="img-middle"
            :style="contentStyleMiddle"
            :src="imgSrcList.length && imgSrcList[0].img"
            alt=""
            v-if="imgSrcList.length && imgSrcList[0].img"
          />
          <div :style="contentStyleMiddle" class="empty-box empty-middle" v-else>
            <span class="iconfont iconfont icontupian"></span>
          </div>
          <div>
            <div class="dot more-dot" :style="dotStyle" v-if="docType === 0">
              <div
                class="dot-item"
                :style="{ background: themeStyle ? configObj.docColor.color[0].item : mobileTheme }"
              ></div>
              <div class="dot-item"></div>
              <div class="dot-item"></div>
            </div>
            <div class="dot more-dot line-dot" :style="dotStyle" v-if="docType === 1">
              <div
                class="line_dot-item"
                :style="{ background: themeStyle ? configObj.docColor.color[0].item : mobileTheme }"
              ></div>
              <div class="line_dot-item"></div>
              <div class="line_dot-item"></div>
            </div>
          </div>
        </div>
        <div class="img-item item-right" v-if="swiperType == 0">
          <img
            class="img-right"
            :style="contentStyleRight"
            :src="imgSrcList.length > 2 && imgSrcList[2].img"
            alt=""
            v-if="imgSrcList.length > 2 && imgSrcList[2].img"
          />
          <div :style="contentStyleRight" class="empty-box empty-right" v-else></div>
        </div>
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
  name: 'banner', // 组件名称
  cname: '轮播图', // 标题名称
  icon: 't-icon-zujian-lunbotu',
  defaultName: 'swiperBg', // 外面匹配名称
  configName: 'c_banner', // 右侧配置名称
  type: 0, // 0 基础组件 1 营销组件 2工具组件
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
    //最外层盒子的样式
    boxStyle() {
      return [
        { 'border-radius': this.configObj.bgStyle.val ? this.configObj.bgStyle.val + 'px' : '0' },
        {
          background: `linear-gradient(${this.configObj.bgColor.color[0].item}, ${this.configObj.bgColor.color[1].item})`,
        },
        { margin: this.configObj.mbConfig.val + 'px' + ' ' + this.configObj.lrConfig.val + 'px' + ' ' + 0 },
        { padding: this.configObj.upConfig.val + 'px' + ' ' + 0 + ' ' + this.configObj.downConfig.val + 'px' },
      ];
    },
    //内容圆角
    contentStyleLeft() {
      return [
        {
          'border-radius':
            0 + 'px ' + this.configObj.contentStyle.val + 'px ' + this.configObj.contentStyle.val + 'px ' + 0 + 'px',
        },
      ];
    },
    contentStyleMiddle() {
      return [
        {
          'border-radius': this.configObj.contentStyle.val + 'px ',
        },
      ];
    },
    contentStyleRight() {
      return [
        {
          'border-radius':
            this.configObj.contentStyle.val + 'px ' + 0 + 'px ' + 0 + 'px ' + this.configObj.contentStyle.val + 'px',
        },
      ];
    },
    //指示器样式
    dotStyle() {
      return [
        { 'padding-left': this.configObj.lrConfig.val ? this.configObj.lrConfig.val + 40 + 'px' : '40' + 'px' },
        { 'padding-right': this.configObj.lrConfig.val ? this.configObj.lrConfig.val + 40 + 'px' : '40' + 'px' },
        {
          'justify-content':
            this.configObj.txtStyle.tabVal === 1
              ? 'center'
              : this.configObj.txtStyle.tabVal === 2
              ? 'flex-end'
              : 'flex-start',
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
    themeStyle: {
      handler(nVal, oVal) {
        this.configObj.docColor.isShow = this.configObj.themeStyleConfig.tabVal;
      },
      deep: true,
    },
  },
  data() {
    return {
      // 默认初始化数据禁止修改
      defaultConfig: {
        isHide: false,
        name: 'swiperBg',
        isShow: true,
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '轮播图',
        },
        // 图片列表
        swiperConfig: {
          tabTitle: '版块设置',
          tips: '建议：图片尺寸750*332px；鼠标拖拽版块可调整图片顺序',
          title: '建议：图片尺寸750*332px；鼠标拖拽版块可调整图片顺序',
          maxList: 1000,
          list: [
            {
              img: '',
              info: [
                {
                  title: '标题',
                  value: '今日推荐',
                  tips: '选填，不超过8个字',
                  max: 8,
                },
                {
                  title: '链接',
                  value: '',
                  tips: '请选择链接',
                  max: 100,
                },
              ],
            },
          ],
        },
        swiperStyleConfig: {
          tabTitle: '展示设置',
          title: '选择风格',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '样式1',
            },
            {
              val: '样式2',
            },
          ],
        },
        // 上间距
        upConfig: {
          title: '上边距',
          val: 10,
          min: 0,
          max: 100,
        },
        // 下间距
        downConfig: {
          tabTitle: '边距设置',
          title: '下边距',
          val: 10,
          min: 0,
        },
        bgStyle: {
          tabTitle: '圆角设置',
          title: '背景圆角',
          name: 'bgStyle',
          val: 0,
          min: 0,
          max: 30,
        },
        // 左右间距
        lrConfig: {
          title: '左右边距',
          val: 12,
          min: 6,
          max: 15,
        },
        // 页面间距
        mbConfig: {
          title: '页面间距',
          val: 0,
          min: 0,
        },
        // 背景颜色
        bgColor: {
          tabTitle: '颜色设置',
          title: '背景颜色',
          color: [
            {
              item: '#FFFFFF',
            },
            {
              item: '#FFFFFF',
            },
          ],
          default: [
            {
              item: '#FFFFFF',
            },
            {
              item: '#FFFFFF',
            },
          ],
        },
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
        // 指示器颜色
        docColor: {
          isShow: 0,
          title: '指示器颜色',
          name: 'docColor',
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
        // 轮播图点样式
        docConfig: {
          cname: 'swiper',
          title: '指示器样式',
          tabTitle: '指示器设置',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '圆形',
              icon: 'icon-yuandian',
            },
            {
              val: '直线',
              icon: 'icon-xiantiao',
            },
            {
              val: '无指示器',
              icon: 'icon-buxianshi',
            },
          ],
        },
        contentStyle: {
          title: '内容圆角',
          name: 'contentStyle',
          val: 7,
          min: 0,
          max: 30,
        },
        txtStyle: {
          title: '指示器位置',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '居左',
              icon: 'icon-juzuo',
            },
            {
              val: '居中',
              icon: 'icon-juzhong',
            },
            {
              val: '居右',
              icon: 'icon-juyou',
            },
          ],
        },
      },
      configObj: null,
      pageData: {},
      imgSrcList: [],
      docType: 0,
      swiperType: 0,
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
    onChange() {},
    setConfig(data) {
      if (!data) return;
      if (data) {
        this.configObj = data;
        this.imgSrcList = data.swiperConfig.list;
        this.docType = data.docConfig.tabVal;
        this.swiperType = data.swiperStyleConfig.tabVal;
        this.themeStyle = data.themeStyleConfig.tabVal;
      }
    },
  },
};
</script>
<style scoped lang="scss">
.empty-box {
  height: 150px;
  flex: 1;
}

.mobile-page {
  position: relative;
  width: auto;

  .banner {
    width: 100%;
    height: 150px;
    margin-top: 0;
    position: relative;

    &.on {
      margin-top: -160px;
    }

    img {
      width: 100%;
      height: 100%;
      border-radius: 10px;
      object-fit: cover;
      &.doc {
        border-radius: 0 !important;
      }
    }
  }

  .bg {
    width: 100%;
    height: 160px;
  }
}

.dot {
  position: absolute;
  left: 0;
  bottom: 12px;
  width: 100%;
  display: flex;
  align-items: center;

  &.number {
    bottom: 4px;
  }

  .num {
    width: 25px;
    height: 18px;
    line-height: 18px;
    background-color: #000;
    color: #fff;
    opacity: 0.3;
    border-radius: 8px;
    font-size: 12px;
    text-align: center;
  }

  .dot-item {
    width: 5px;
    height: 5px;
    background: #aaaaaa;
    border-radius: 50%;
    margin: 0 3px;
  }

  &.line-dot {
    bottom: 12px;

    .line_dot-item {
      width: 8px;
      height: 2px;
      background: #aaaaaa;
      margin: 0 3px;
    }
  }
}
.banner-two {
  position: relative;
  z-index: 1;
  overflow: hidden;
  width: 100%;
  height: 150px;

  &.on {
    margin-top: -160px;
  }

  img {
    width: 100%;
    border-radius: 10px;
    // object-fit: contain;

    &.doc {
      border-radius: 0;
    }
  }

  .img-middle {
    height: 150px;
    background-position: center;
    object-fit: cover;
  }

  .img-left {
    height: 140px;
  }

  .img-right {
    height: 140px;
  }

  .empty-box {
    height: 150px;
    background: #f3f9ff;
  }

  .empty-left {
    height: 140px;
  }

  .empty-middle {
    height: 150px;
  }

  .empty-right {
    height: 140px;
  }

  .item-middle {
    flex: 1;
    margin: 0 10px;
  }

  .img-box {
    width: 100%;
    height: 100%;
    display: flex;
  }

  .itemLeft {
    width: 20px;
    height: 134px;
    margin-top: 5px;
  }

  .item-right {
    width: 20px;
    height: 134px;
    margin-top: 5px;
  }
}
.icontupian {
  font-size: 30px !important;
  color: #bbbfc8;
}
</style>
