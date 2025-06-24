<template>
  <div class="mobile-page" v-if="configObj">
    <div class="box" :style="boxStyle" v-html="richText"></div>
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
import { mapState, mapMutations } from 'vuex';
export default {
  name: 'z_ueditor',
  cname: '富文本',
  configName: 'c_ueditor_box',
  icon: 't-icon-zujian-fuwenben',
  type: 2, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'richTextEditor', // 外面匹配名称
  props: {
    index: {
      type: null,
      default: -1,
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
        name: 'richTextEditor',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '富文本',
        },
        // 背景颜色
        bgColor: {
          title: '背景颜色',
          tabTitle: '颜色设置',
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
        lrConfig: {
          title: '左右边距',
          tabTitle: '边距设置',
          val: 12,
          min: 0,
        },
        mbConfig: {
          title: '页面间距',
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
        richText: {
          tabTitle: '富文本内容',
          val: '',
        },
      },
      pageData: {},
      richText: '',
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
        this.richText = data.richText.val;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.mobile-page ::v-deep video {
  width: 100% !important;
}
.box {
  min-height: 100px;
  padding: 10px;
  background: #f5f5f5;
  ::v-deep img {
    max-width: 100%;
    height: auto;
  }
}
</style>
