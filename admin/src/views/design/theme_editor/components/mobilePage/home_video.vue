<template>
  <common_wrapper :config="configObj">
    <div
      class="pictrue acea-row row-center-wrapper"
      :class="scaleConfig == 1 ? 'on' : scaleConfig == 2 ? 'on2' : ''"
      :style="'background-image:url(' + (imgUrl ? imgUrl : imgBgUrl) + ');border-radius:' + bgRadius"
    >
      <div class="image acea-row row-center-wrapper">
        <img src="../../assets/images/ic_right2.png" />
      </div>
    </div>
  </common_wrapper>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
export default {
  name: 'home_video',
  cname: '视频',
  configName: 'c_video',
  icon: '#iconzujian-shipin',
  type: 0, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'videos', // 外面匹配名称
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
        cname: '视频',
        name: 'videos',
        timestamp: this.num,
        isHide: false,
        setUp: {
          tabVal: 0,
        },
        zIndexConfig: {
          title: '组件上浮',
          val: 0,
          min: 0,
        },
        borderConfig: {
          title: '边框设置',
          tabVal: 0,
          tabList: [{ name: '隐藏' }, { name: '显示' }],
          val: 0,
          styleConfig: {
            title: '边框样式',
            tabVal: 0,
            tabList: [
              { name: '实线', style: 'solid' },
              { name: '虚线', style: 'dashed' },
              { name: '点状', style: 'dotted' },
            ],
          },
          widthConfig: {
            title: '边框粗细',
            val: 1,
            min: 1,
          },
          colorConfig: {
            title: '边框颜色',
            default: [{ item: '#e5e5e5' }],
            color: [{ item: '#e5e5e5' }],
          },
        },
        shadowConfig: {
          title: '阴影设置',
          tabVal: 0,
          tabList: [{ name: '隐藏' }, { name: '显示' }],
          val: 0,
          colorConfig: {
            title: '阴影颜色',
            default: [{ item: 'rgba(0,0,0,0.1)' }],
            color: [{ item: 'rgba(0,0,0,0.1)' }],
          },
          xConfig: {
            title: 'X轴偏移',
            val: 0,
            min: -50,
          },
          yConfig: {
            title: 'Y轴偏移',
            val: 0,
            min: -50,
          },
          blurConfig: {
            title: '模糊半径',
            val: 10,
            min: 0,
          },
          spreadConfig: {
            title: '扩展半径',
            val: 0,
            min: -50,
          },
        },
        titleLeft: '内容设置',
        titleRight: '通用样式',
        imgConfig: {
          url: '',
          type: 'code',
          delType: 1,
          name: '视频封面',
        },
        videoConfig: {
          url: '',
          type: 'code',
          video: 1,
          delType: 0,
          name: '上传视频',
        },
        scaleConfig: {
          title: '视频比例',
          tabVal: 0,
          tabList: [
            {
              name: '16:9',
            },
            {
              name: '4:3',
            },
            {
              name: '1:1',
            },
          ],
        },
        componentBgConfig: {
          title: '背景设置',
          tabVal: 0,
          tabList: [{ name: '颜色' }, { name: '图片' }],
          colorConfig: {
            title: '背景颜色',
            default: [{ item: '#F5F5F5' }, { item: '#F5F5F5' }],
            color: [{ item: '#F5F5F5' }, { item: '#F5F5F5' }],
          },
          colorDirection: {
            title: '渐变方向',
            tabVal: 0,
            tabList: [{ name: '横向' }, { name: '纵向' }, { name: '左斜' }, { name: '右斜' }],
          },
          imageConfig: {
            header: '背景图片',
            title: '',
            name: '上传图片',
            type: 'code',
            url: '',
            info: '建议尺寸：750px * 400px',
          },
        },
        bottomBgColor: {
          title: '底部背景',
          name: 'bgColor',
          default: [
            {
              item: '#F5F5F5',
            },
          ],
          color: [
            {
              item: '#F5F5F5',
            },
          ],
        },
        topConfig: {
          title: '上边距',
          val: 0,
          min: 0,
        },
        bottomConfig: {
          title: '下边距',
          val: 0,
          min: 0,
        },
        prConfig: {
          title: '左右边距',
          val: 0,
          min: 0,
        },
        mbConfig: {
          title: '页面上间距',
          val: 0,
          min: 0,
        },
        fillet: {
          title: '背景圆角',
          type: 0,
          list: [
            {
              val: '全部',
              icon: 'iconcaozuo-zhengti',
            },
            {
              val: '单个',
              icon: 'iconcaozuo-bianjiao',
            },
          ],
          valName: '圆角值',
          val: 0,
          min: 0,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        },
      },
      imgBgUrl: require('@/assets/images/videoBg.png'),
      confObj: {},
      pageData: {},
      topConfig: '',
      bottomConfig: '',
      prConfig: 0,
      bgRadius: 0,
      imgUrl: '',
      scaleConfig: 0,
      mTop: 0,
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
      this.configObj = data;
      for (let key in this.defaultConfig) {
        if (data[key] == undefined) {
          this.$set(data, key, this.defaultConfig[key]);
        }
      }
      this.scaleConfig = data.scaleConfig.tabVal;
      this.imgUrl = data.imgConfig.url;
      let fillet = data.fillet.type;
      let filletVal = data.fillet.val;
      let valList = data.fillet.valList;
      this.bgRadius = fillet
        ? valList[0].val + 'px ' + valList[1].val + 'px ' + valList[3].val + 'px ' + valList[2].val + 'px'
        : filletVal + 'px';
    },
  },
};
</script>

<style scoped lang="scss">
.pictrue {
  width: 100%;
  height: 213px;
  background-repeat: no-repeat;
  background-size: cover;
  background-position: 50%;
  &.on {
    height: 284px;
  }
  &.on2 {
    height: 375px;
  }
  .image {
    width: 44px;
    height: 44px;
    background: rgba(0, 0, 0, 0.2);
    border-radius: 50%;
    img {
      width: 24px;
      height: 24px;
      display: block;
    }
  }
  .empty-box {
    width: 100%;
    height: 375px;
    border-radius: 0;
    background: #f3f9ff;

    img {
      width: 65px;
      height: 50px;
    }
  }
  img {
    width: 100%;
    height: 100%;
  }
}
</style>
