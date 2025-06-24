<template>
  <div class="mobile-page" v-if="configObj" :style="boxStyle">
    <div class="empty-box video" :style="contantRadius">
      <video :src="videoUrl"></video>
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
  name: 'home_video',
  cname: '视频',
  icon: 't-icon-zujian-shipin',
  configName: 'c_home_video',
  type: 0, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'video', // 外面匹配名称
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
        { margin: this.configObj.mbConfig.val + 'px' + ' ' + this.configObj.lrConfig.val + 'px' + ' ' + 0 },
        { padding: this.configObj.upConfig.val + 'px' + ' ' + 0 + ' ' + this.configObj.downConfig.val + 'px' },
      ];
    },
    contantRadius() {
      return [{ 'border-radius': this.configObj.contantStyle.val ? this.configObj.contantStyle.val + 'px' : '0' }];
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
      defaultConfig: {
        isHide: false,
        name: 'video',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '视频',
        },
        tabConfig: {
          title: '视频类型',
          name: 'tabConfig',
          tabTitle: '视频内容设置',
          list: [
            {
              val: '手动上传',
              icon: 'icon-shoudongshangchuan',
            },
            {
              val: '视频链接',
              icon: 'icon-shipinlianjie',
            },
          ],
          tabVal: 0,
        },
        uploadVideo: {
          title: '上传视频',
          name: 'uploadVideo',
          isShow: 1,
          tips: '建议上传大小：宽152px，高60px',
          header: '设置logo',
          url: '',
        },
        cover: {
          name: 'cover',
          isShow: 1,
          tips: '建议上传大小：宽152px，高60px',
          header: '设置logo',
          url: '',
          title: '视频封面',
          val: '',
        },
        link: {
          title: '视频链接',
          value: '',
          place: '请输入链接地址',
          isShow: 0,
        },
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
        bgStyle: {
          tabTitle: '圆角设置',
          title: '背景圆角',
          name: 'bgStyle',
          val: 0,
          min: 0,
          max: 30,
        },
        contantStyle: {
          tabTitle: '圆角设置',
          title: '内容圆角',
          name: 'contantStyle',
          val: 0,
          min: 0,
          max: 30,
        },
        // 上间距
        upConfig: {
          tabTitle: '边距设置',
          title: '上边距',
          val: 10,
          min: 0,
          max: 100,
        },
        // 下间距
        downConfig: {
          title: '下边距',
          val: 10,
          min: 0,
        },
        mbConfig: {
          title: '页面间距',
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
      },
      configObj: null,
      videoUrl: '',
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
        if (this.configObj.tabConfig.tabVal === 0) {
          this.videoUrl = this.configObj.uploadVideo.url;
        } else {
          this.videoUrl = this.configObj.link.value;
        }
      }
    },
  },
};
</script>

<style scoped lang="scss">
.video {
  width: 100%;
  height: 239px !important;
  video {
    width: 100%;
    height: 100%;
  }
}
</style>
