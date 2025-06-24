<template>
  <div v-if="configObj" class="title-box acea-row row-between row-middle" :style="boxStyle">
    <div class="acea-row row-middle">
      <div :style="titleStyle">{{ titleTxt }}</div>
      <div class="ml6" :style="titleFuStyle">{{ titleFuTxt }}</div>
    </div>
    <div v-if="!selectShow" :style="titleRightStyle">{{ titleRightTxt }}<i class="el-icon-arrow-right"></i></div>
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
  name: 'home_title',
  cname: '标题',
  icon: 't-icon-zujian-biaoti',
  configName: 'c_home_title',
  type: 2, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'titles', // 外面匹配名称
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
        {
          'border-radius':
            this.configObj.bgTopStyle.val +
            'px' +
            ' ' +
            this.configObj.bgTopStyle.val +
            'px' +
            ' ' +
            this.configObj.bgDownStyle.val +
            'px' +
            ' ' +
            this.configObj.bgDownStyle.val +
            'px',
        },
        {
          'background-image': this.selectStyle
            ? `linear-gradient(to right,${this.configObj.bgColor.color[0].item}, ${this.configObj.bgColor.color[1].item})`
            : `url(${this.bgImgUrl})`,
        },
        { margin: this.configObj.mbConfig.val + 'px' + ' ' + this.configObj.lrConfig.val + 'px' + ' ' + 0 },
        { padding: this.configObj.upConfig.val + 'px' + ' ' + '10px' + ' ' + this.configObj.downConfig.val + 'px' },
      ];
    },
    titleStyle() {
      return [
        {
          'font-weight':
            this.configObj.textStyle.tabVal == 2
              ? this.configObj.textStyle.list[this.configObj.textStyle.tabVal].style
              : '',
        },
        {
          'font-style':
            this.configObj.textStyle.tabVal != 2
              ? this.configObj.textStyle.list[this.configObj.textStyle.tabVal].style
              : '',
        },
        {
          fontSize: this.configObj.fontSize.val + 'px',
        },
        { color: this.configObj.fontColor.color[0].item },
      ];
    },
    titleFuStyle() {
      return [
        {
          fontSize: this.configObj.fontFuSize.val + 'px',
        },
        { color: this.configObj.fontFuColor.color[0].item },
      ];
    },
    titleRightStyle() {
      return [
        {
          fontSize: this.configObj.fontRightSize.val + 'px',
        },
        { color: this.configObj.fontRightColor.color[0].item },
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
    selectShow: {
      handler(nVal, oVal) {
        if (nVal == 0) {
          this.configObj.titleRightConfig.isShow = 1;
        } else {
          this.configObj.titleRightConfig.isShow = 0;
        }
      },
      deep: true,
    },
    selectStyle: {
      handler(nVal, oVal) {
        if (nVal == 0) {
          this.configObj.bgImg.isShow = 1;
          this.configObj.bgColor.isShow = 0;
        } else {
          this.configObj.bgImg.isShow = 0;
          this.configObj.bgColor.isShow = 1;
        }
      },
      deep: true,
    },
  },
  data() {
    return {
      defaultConfig: {
        isHide: false,
        name: 'titles',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '标题',
        },
        titleConfig: {
          tabTitle: '文本标题',
          title: '标题名称',
          val: '标题',
          place: '请输入标题，限制8个字以内',
          max: 8,
          isShow: 1,
        },
        titleFuConfig: {
          tabTitle: '文本标题',
          title: '副标题',
          val: '副标题',
          place: '请输入副标题，限制12个字以内',
          max: 12,
          isShow: 1,
        },
        titleRightConfig: {
          tabTitle: '右侧文字',
          title: '右侧文字',
          val: '更多',
          place: '请输入右侧文字，限制4个字以内',
          max: 4,
          isShow: 1,
        },
        bgImg: {
          isShow: 1,
          title: '上传背景图',
          tips: '建议：910px*86px',
          url: '',
        },
        selectShow: {
          cname: 'selectShow',
          title: '右侧按钮',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '显示',
            },
            {
              val: '隐藏',
            },
          ],
        },
        selectStyle: {
          cname: 'selectStyle',
          title: '选择风格',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '背景图片',
            },
            {
              val: '背景色',
            },
          ],
        },
        // 背景颜色
        bgColor: {
          tabTitle: '颜色设置',
          title: '背景颜色',
          isShow: 1,
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
        linkConfig: {
          title: '链接',
          val: '',
          place: '请输入链接地址',
          max: 100,
          isShow: 1,
        },
        fontColor: {
          title: '标题颜色',
          tabTitle: '文字设置',
          name: 'fontColor',
          default: [
            {
              item: '#282828',
            },
          ],
          color: [
            {
              item: '#282828',
            },
          ],
        },
        bgTopStyle: {
          tabTitle: '圆角设置',
          title: '上圆角',
          name: 'bgStyle',
          val: 0,
          min: 0,
          max: 30,
        },
        bgDownStyle: {
          title: '下圆角',
          name: 'bgStyle',
          val: 0,
          min: 0,
          max: 30,
        },
        textTitleStyle: {
          title: '文本位置',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '正常',
              icon: 'icon-juzuo',
            },
            {
              val: '倾斜',
              icon: 'icon-juzhong',
            },
            {
              val: '加粗',
              icon: 'icon-juyou',
            },
          ],
        },
        textStyle: {
          title: '文本样式',
          isShow: 1,
          tabVal: 0,
          list: [
            {
              val: '正常',
              style: 'normal',
              icon: 'iconbiaotiwenziyangshi',
            },
            {
              val: '斜体',
              style: 'italic',
              icon: 'iconbiaotiwenzixieti',
            },
            {
              val: '加粗',
              style: 'bold',
              icon: 'iconbiaotiwenzijiacu',
            },
          ],
        },
        fontSize: {
          title: '标题文字',
          val: 20,
          min: 12,
          max: 30,
        },
        fontFuColor: {
          title: '副标题颜色',
          tabTitle: '文字设置',
          name: 'fontFuColor',
          default: [
            {
              item: '#282828',
            },
          ],
          color: [
            {
              item: '#282828',
            },
          ],
        },
        fontFuSize: {
          title: '副标题文字',
          val: 14,
          min: 12,
          max: 20,
        },
        fontRightColor: {
          title: '按钮颜色',
          tabTitle: '文字设置',
          name: 'fontFuColor',
          default: [
            {
              item: '#282828',
            },
          ],
          color: [
            {
              item: '#282828',
            },
          ],
        },
        fontRightSize: {
          title: '按钮文字',
          val: 12,
          min: 12,
          max: 20,
        },
        // 上间距
        upConfig: {
          title: '上边距',
          tabTitle: '边距设置',
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
        // 左右间距
        lrConfig: {
          title: '左右边距',
          val: 12,
          min: 0,
          max: 40,
        },
        mbConfig: {
          title: '页面间距',
          val: 10,
          min: 0,
        },
      },
      configObj: null,
      titleTxt: '',
      titleFuTxt: '',
      titleRightTxt: '',
      link: '',
      txtPosition: '',
      txtStyle: '',
      fontSize: 0,
      mTOP: 0,
      titleColor: '',
      themeColor: '',
      prConfig: 0,
      bgStyle: 0,
      pageData: {},
      selectShow: '',
      selectStyle: '',
      bgImgUrl: '',
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
        this.titleTxt = data.titleConfig.val;
        this.titleFuTxt = data.titleFuConfig.val;
        this.titleRightTxt = data.titleRightConfig.val;
        this.link = data.linkConfig.val;
        this.selectShow = this.configObj.selectShow.tabVal;
        this.selectStyle = this.configObj.selectStyle.tabVal;
        this.bgImgUrl = this.configObj.bgImg.url;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.titleOn {
  border-radius: 10px !important;
}
.title-box {
  color: #282828;
  padding: 5px 10px;
  background-repeat: no-repeat;
}
.ml6 {
  margin-left: 6px;
}
</style>
