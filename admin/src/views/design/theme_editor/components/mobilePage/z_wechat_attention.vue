<template>
  <common_wrapper :config="wrapperConfig">
    <div class="flex-box" v-if="configObj">
      <div class="left">
        <div class="img-box">
          <div class="empty-box on">
            <img :src="imgUrl" alt="" v-if="imgUrl" />
            <img src="../../assets/images/noPictrue.png" v-else />
          </div>
        </div>
        <div class="name">{{ txt }}</div>
      </div>
      <div class="right">
        <div class="btn" :style="{ borderColor: themeColor, color: themeColor }">关注</div>
        <div class="iconfont iconguanbi5"></div>
      </div>
    </div>
  </common_wrapper>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
export default {
  name: 'z_wechat_attention',
  cname: '关注公众号',
  configName: 'c_wechat_attention',
  icon: '#iconzujian-gongzhonghao',
  type: 2, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'follow', // 外面匹配名称
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
    wrapperConfig() {
      return {
        ...this.configObj,
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
      configObj: null,
      // 默认初始化数据禁止修改
      defaultConfig: {
        cname: '关注公众号',
        name: 'follow',
        timestamp: this.num,
        isHide: false,
        setUp: {
          tabVal: 0,
        },
        titleLeft: '标题设置',
        positionTitle: '位置设置',
        pictrueTitle: '图片设置',
        codeTitle: '关注二维码',
        titleRight: '关注按钮',
        titleCurrency: '通用样式',
        positionConfig: {
          title: '展示位置',
          tabVal: 0,
          tabList: [
            {
              name: '顶部',
            },
            {
              name: '底部',
            },
          ],
        },
        titleConfig: {
          title: '标题名称',
          value: '标题',
          place: '请输入标题',
          max: 10,
        },
        imgConfig: {
          info: '建议：图片尺寸92px * 92px',
          url: '',
          type: 'code',
          name: '上传图片',
        },
        codeConfig: {
          url: '',
          type: 'code',
          name: '上传二维码',
        },
        themeColor: {
          title: '按钮颜色',
          default: [
            {
              item: '#E93323',
            },
          ],
          color: [
            {
              item: '#E93323',
            },
          ],
        },
        bgColor: {
          title: '背景颜色',
          default: [
            {
              item: '#fff',
            },
            {
              item: '#fff',
            },
          ],
          color: [
            {
              item: '#fff',
            },
            {
              item: '#fff',
            },
          ],
        },
        paddingConfig: {
          title: '内边距',
          isAll: false,
          val: 0,
          min: 0,
          max: 100,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        },
        marginConfig: {
          title: '外边距',
          isAll: false,
          val: 0,
          min: 0,
          max: 100,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
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
      cSlider: '',
      bgColor: '',
      confObj: {},
      pageData: {},
      themeColor: '',
      imgUrl: '',
      txt: '',
      fillet: 0,
      filletVal: 0,
      valList: [],
      bgRadius: 0,
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
      this.bgColor = data.bgColor.color;
      this.themeColor = data.themeColor.color[0].item;
      this.imgUrl = data.imgConfig.url;
      this.txt = data.titleConfig.value;
      this.fillet = data.fillet.type;
      this.filletVal = data.fillet.val;
      this.valList = data.fillet.valList;
      this.bgRadius = this.fillet
        ? this.valList[0].val +
          'px ' +
          this.valList[1].val +
          'px ' +
          this.valList[3].val +
          'px ' +
          this.valList[2].val +
          'px'
        : this.filletVal + 'px';

      if (!this.configObj.paddingConfig) {
        this.$set(this.configObj, 'paddingConfig', {
          title: '内边距',
          isAll: false,
          val: 0,
          min: 0,
          max: 100,
          valList: [
            { val: 0 },
            { val: data.prConfig ? data.prConfig.val : 0 },
            { val: 0 },
            { val: data.prConfig ? data.prConfig.val : 0 },
          ],
        });
      }
      if (!this.configObj.marginConfig) {
        this.$set(this.configObj, 'marginConfig', {
          title: '外边距',
          isAll: false,
          val: 0,
          min: 0,
          max: 100,
          valList: [{ val: data.mbConfig ? data.mbConfig.val : 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        });
      }
      for (let key in this.defaultConfig) {
        if (this.configObj[key] === undefined) {
          this.$set(this.configObj, key, this.defaultConfig[key]);
        }
      }
    },
  },
};
</script>

<style scoped lang="scss">
.flex-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 10px;
  height: 60px;

  .iconfont {
    color: #999;
    font-size: 15px;
    margin-left: 10px;
  }

  .right {
    display: flex;
    align-items: center;
    margin-left: 10px;
  }

  .left {
    display: flex;
    align-items: center;

    .img-box,
    .empty-box {
      width: 46px;
      height: 46px;
      border-radius: 50%;

      img {
        width: 100%;
        height: 100%;
        border-radius: 50%;
      }
    }

    .name {
      margin-left: 10px;
      font-size: 15px;
      color: #333;
    }
  }

  .btn {
    width: 56px;
    height: 28px;
    border: 1px solid #e93323;
    opacity: 1;
    border-radius: 25px;
    color: #e93323;
    font-size: 12px;
    text-align: center;
    line-height: 28px;
  }

  .iconfont {
    font-size: 20px;
  }
}
</style>
