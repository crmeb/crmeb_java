<template>
  <div class="news-box" :style="boxStyle" v-if="configObj">
    <div class="item" :style="textColor">
      <div class="img-box"><img :src="imgUrl" alt="" /></div>
      <div class="right-box" :style="textPosition">{{ list[0].chiild[0].val }}</div>
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
import { mapState } from 'vuex';

export default {
  name: 'home_news_roll',
  cname: '新闻播报',
  configName: 'c_news_roll',
  type: 0, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'news', // 外面匹配名称
  icon: 't-icon-zujian-xinwenbobao',
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
    //外部盒子
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
    //文字位置
    textPosition() {
      return {
        'text-align': this.configObj.textPosition.list[this.configObj.textPosition.tabVal].style,
      };
    },
    //文字颜色
    textColor() {
      return {
        color: this.configObj.textColor.color[0].item,
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
  },
  data() {
    return {
      // 默认初始化数据禁止修改
      defaultConfig: {
        isHide: false,
        name: 'news',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '新闻播报',
        },
        directionConfig: {
          title: '滚动方向',
          tabTitle: '文字设置',
          name: 'directionConfig',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '上下',
              icon: 'icon-shangxia',
              style: 'left',
            },
            {
              val: '左右',
              icon: 'icon-zuoyou',
              style: 'center',
            },
          ],
        },
        textPosition: {
          title: '文本位置',
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
            {
              val: '居右',
              icon: 'icon-juyou',
              style: 'right',
            },
          ],
        },
        textColor: {
          title: '文字颜色',
          color: [
            {
              item: '#333',
            },
          ],
          default: [
            {
              item: '#333',
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
        listConfig: {
          title: '最多可添加10个版块；鼠标拖拽左侧圆点可调整版块顺序',
          tabTitle: '公告设置',
          max: 10,
          list: [
            {
              status: true,
              chiild: [
                {
                  title: '标题',
                  val: '标题',
                  max: 30,
                  pla: '选填，不超过30个字',
                  empty: true,
                },
                {
                  title: '链接',
                  val: '链接',
                  max: 200,
                  pla: '请输入连接',
                  status: true,
                },
              ],
            },
          ],
        },
        // 上间距
        upConfig: {
          tabTitle: '边距设置',
          title: '上边距',
          val: 0,
          min: 0,
          max: 100,
        },
        // 下间距
        downConfig: {
          title: '下边距',
          val: 0,
          min: 0,
          max: 100,
        },
        // 左右间距
        lrConfig: {
          title: '左右边距',
          val: 12,
          min: 0,
          max: 15,
        },
        // 页面间距
        mbConfig: {
          title: '页面间距',
          tabTitle: '边距设置',
          val: 10,
          min: 0,
        },
        logoConfig: {
          tabTitle: '图标设置',
          header: '上传图标',
          title: '上传图标',
          tips: '支持上传1张图片，建议宽度130*36px',
          isShow: 1,
          url: require('@/assets/imgs/new_header.png'),
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
      pageData: {},
      list: [],
      imgUrl: '',
      configObj: null,
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
        this.list = data.listConfig.list;
        this.imgUrl = data.logoConfig.url;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.pageOn {
  border-radius: 6px !important;
}
.news-box {
  .item {
    display: flex;
    align-items: center;
    margin: 0 10px;
    height: 39px;
    .img-box {
      width: 75px;
      height: 18px;
      border-right: 1px solid #ddd;
      padding-right: 10px;
      img {
        width: 100%;
        height: 100%;
      }
    }

    .right-box {
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      margin-left: 8px;
      margin-top: 3px;
      background-size: 20px 20px;
      background-position: right center;
      background-repeat: no-repeat;
    }
  }
}
</style>
