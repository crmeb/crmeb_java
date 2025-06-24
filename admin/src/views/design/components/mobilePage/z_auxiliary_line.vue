<template>
  <div class="mobile-page" :style="boxStyle" v-if="configObj">
    <div class="box" :style="textStyle"></div>
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
  name: 'z_auxiliary_line',
  cname: '辅助线',
  configName: 'c_auxiliary_line',
  icon: 't-icon-zujian-fuzhuxian',
  type: 2, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'guide', // 外面匹配名称
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
        {
          background: `linear-gradient(${this.configObj.bgColor.color[0].item}, ${this.configObj.bgColor.color[1].item})`,
        },
        { margin: this.configObj.mbConfig.val + 'px' + ' ' + this.configObj.lrConfig.val + 'px' + ' ' + 0 },
      ];
    },
    //线条样式
    textStyle() {
      return [
        {
          borderBottomWidth: this.configObj.heightConfig.val + 'px',
        },
        {
          borderBottomColor: this.configObj.lineColor.color[0].item,
        },
        {
          borderBottomStyle: this.configObj.lineStyle.list[this.configObj.lineStyle.tabVal].style,
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
        name: 'guide',
        setUp: {
          cname: '辅助线',
        },
        timestamp: this.num,
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
        lineColor: {
          title: '线条颜色',
          color: [
            {
              item: '#979797',
            },
          ],
          default: [
            {
              item: '#979797',
            },
          ],
        },
        lineStyle: {
          tabTitle: '样式设置',
          title: '线条样式',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '虚线',
              style: 'dashed',
              icon: 'icon-xuxian',
            },
            {
              val: '实线',
              style: 'solid',
              icon: 'icon-shixian',
            },
            {
              val: '点状线',
              style: 'dotted',
              icon: 'icon-dianzhuangxian',
            },
          ],
        },
        heightConfig: {
          title: '线条高度',
          val: 1,
          min: 1,
        },
        lrConfig: {
          title: '左右边距',
          val: 0,
          min: 0,
        },
        mbConfig: {
          title: '页面间距',
          val: 10,
          min: 0,
        },
      },
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
      }
    },
  },
};
</script>

<style scoped lang="scss">
.mobile-page {
  padding: 10px;
}
</style>
