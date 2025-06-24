<template>
  <div v-if="configObj">
    <div class="seckill" :style="boxStyle">
      <div class="bg_box" :style="boxBgStyle"></div>
      <div class="title acea-row row-between-wrapper">
        <div class="acea-row row-middle">
          <div class="pictrue skeleton-rect">
            <el-image :src="src" class="image"></el-image>
          </div>
          <div class="lines"></div>
          <div class="point skeleton-rect" :style="titleColor">{{ configObj.titleConfig.val }}</div>
        </div>
        <div class="more acea-row row-center-wrapper skeleton-rect">GO<i class="el-icon-arrow-right"></i></div>
      </div>
      <div class="conter" v-show="listStyle === 0">
        <div style="white-space: nowrap; vertical-align: middle">
          <div class="itemCon" :style="contentConfig" v-for="(item, index) in spikeList" :key="index">
            <div class="item">
              <div class="pictrue skeleton-rect">
                <!--<el-image :src="item.image" :style="contentStyle" mode="aspectFill"></el-image>-->
                <div class="empty-box" :style="contentStyle"><span class="iconfont icontupian"></span></div>
              </div>
              <div v-show="nameShow" class="name line1 skeleton-rect" :style="nameColor">{{ item.name }}</div>
              <div v-show="priceShow" class="x_money line1 skeleton-rect semiBold" :style="priceColor">
                ¥<span class="num">{{ item.price }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="conter_y" :style="gridGap" v-show="listStyle === 1">
        <div class="item" :style="contentConfig" v-for="(item, index) in spikeList" :key="index">
          <div class="pictrue">
            <div class="empty-box" :style="contentStyle"><span class="iconfont icontupian"></span></div>
            <!--<el-image :src="item.image" :style="contentStyle" mode="aspectFill"></el-image>-->
          </div>
          <div class="text-info text-add">
            <div v-show="nameShow">
              <div class="title line2" :style="nameColor">{{ item.name }}</div>
            </div>
            <div v-show="priceShow" class="price semiBold" :style="priceColor">
              <div>
                <span>￥</span>
                {{ item.price }}
              </div>
            </div>
          </div>
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
  name: 'home_seckill',
  cname: '秒杀',
  icon: 't-icon-zujian-miaosha',
  configName: 'c_home_seckill',
  type: 1, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'seckill', // 外面匹配名称
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
        { margin: this.configObj.mbConfig.val + 'px' + ' ' + this.configObj.lrConfig.val + 'px' + ' ' + 0 },
        { padding: this.configObj.upConfig.val + 'px' + ' ' + '10px' + ' ' + this.configObj.downConfig.val + 'px' },
      ];
    },
    //图片背景
    boxBgStyle() {
      return {
        backgroundImage: `url('https://api.admin.merchant.crmeb.xbdzz.cn/crmebimage/public/activitystyle/2023/05/30/5fb66919d78c4c2892fee5900915fba5bepulyen1m.png'),linear-gradient(${this.configObj.bgColor.color[0].item}, ${this.configObj.bgColor.color[1].item})`,
      };
    },
    //图片样式
    contentStyle() {
      return {
        'border-radius': this.configObj.contentStyle.val + 'px',
      };
    },
    //内容边距
    contentConfig() {
      return [{ 'margin-right': this.configObj.contentConfig.val ? this.configObj.contentConfig.val + 'px' : '0' }];
    },
    //标题颜色
    titleColor() {
      return { color: this.configObj.titleColor.color[0].item };
    },
    //名称颜色
    nameColor() {
      return { color: this.configObj.nameColor.color[0].item };
    },
    //价格颜色
    priceColor() {
      return { color: this.themeStyle ? this.configObj.priceColor.color[0].item : this.mobileTheme };
    },
    //内容间距
    gridGap() {
      return { 'grid-gap': this.configObj.contentConfig.val + 'px' };
    },
    //商品名称
    nameShow() {
      if (this.configObj.typeConfig.activeValue.indexOf(0) !== -1) {
        return true;
      } else {
        return false;
      }
    },
    //商品价格
    priceShow() {
      if (this.configObj.typeConfig.activeValue.indexOf(1) !== -1) {
        return true;
      } else {
        return false;
      }
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
        this.configObj.priceColor.isShow = this.configObj.themeStyleConfig.tabVal;
      },
      deep: true,
    },
  },
  data() {
    return {
      defaultConfig: {
        isHide: false,
        name: 'seckill',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '秒杀',
        },
        tabConfig: {
          title: '展示样式',
          tabTitle: '布局设置',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '样式1',
              icon: 'icon-yangshiyi',
            },
            {
              val: '样式2',
              icon: 'icon-yangshier',
            },
          ],
        },
        logoConfig: {
          isShow: 1,
          tabTitle: '图标设置',
          title: '上传图标',
          tips: '建议：124px*32px',
          url: localStorage.getItem('mediaDomain') + '/crmebimage/perset/202412/seckillTitle.png',
        },
        titleConfig: {
          tabTitle: '标题设置',
          title: '标题内容',
          val: '邀请你的好友来参与',
          place: '请输入标题',
          isShow: 1,
          max: 15,
        },
        linkConfig: {
          title: '更多链接',
          val: '/pages/activity/goods_seckill/index',
          place: '请选择链接',
          isShow: 1,
          max: 100,
        },
        //显示内容
        typeConfig: {
          tabTitle: '显示内容',
          name: 'rowsNum',
          title: '展示信息',
          activeValue: [0, 1],
          list: [
            {
              val: '商品名称',
            },
            {
              val: '商品价格',
            },
          ],
        },
        // 背景颜色
        bgColor: {
          tabTitle: '颜色设置',
          title: '背景颜色',
          color: [
            {
              item: '#FF2200',
            },
            {
              item: '#FFFFFF',
            },
          ],
          default: [
            {
              item: '#FF2200',
            },
            {
              item: '#FFFFFF',
            },
          ],
        },
        nameColor: {
          title: '商品名称颜色',
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
        titleColor: {
          title: '标题颜色',
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
        priceColor: {
          isShow: 0,
          title: '价格颜色',
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
        bgStyle: {
          tabTitle: '圆角设置',
          title: '背景圆角',
          name: 'bgStyle',
          val: 7,
          min: 0,
          max: 30,
        },
        contentStyle: {
          title: '图片圆角',
          name: 'contentStyle',
          val: 5,
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
        // 左右间距
        lrConfig: {
          title: '左右边距',
          val: 12,
          min: 0,
          max: 25,
        },
        mbConfig: {
          title: '页面间距',
          val: 10,
          min: 0,
        },
        contentConfig: {
          title: '内容间距',
          val: 10,
          min: 0,
          max: 30,
        },
      },
      configObj: null,
      src: '',
      listStyle: 0,
      spikeList: [
        {
          img: '',
          name: '一家很有品的店',
          price: '234',
        },
        {
          img: '',
          name: '一家很有品的店',
          price: '177',
          ot_price: '300',
        },
        {
          img: '',
          name: '一家很有品的店',
          price: '177',
          ot_price: '300',
        },
        {
          img: '',
          name: '一家很有品的店',
          price: '234',
          ot_price: '300',
        },
      ],
      themeStyle: 0,
    };
  },
  mounted() {
    this.$nextTick(() => {
      if (this.num) {
        let pageData = this.$store.state.mobildConfig.defaultArray[this.num];
        this.setConfig(pageData);
      }
    });
  },
  methods: {
    setConfig(data) {
      if (!data) return;
      if (data) {
        this.configObj = data;
        this.listStyle = this.configObj.tabConfig.tabVal;
        this.src = this.configObj.logoConfig.url
          ? this.configObj.logoConfig.url
          : localStorage.getItem('mediaDomain') + '/crmebimage/perset/202412/seckillTitle.png';
        this.themeStyle = data.themeStyleConfig.tabVal;
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.more {
  position: relative;
}
.seckill {
  width: auto;
  background: #fff;
  border-radius: 7px;
  box-sizing: border-box;
  position: relative;
  z-index: 1;
  overflow: hidden;
  .bg_box {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 128px;
    background-repeat: no-repeat;
    background-size: cover;
  }
  .title {
    .pictrue {
      width: 62px;
      height: 16px;

      el-image,
      .image,
      ::v-deep.el-image {
        width: 100%;
        height: 100%;
      }
    }

    .lines {
      width: 1px;
      height: 12px;
      background-color: #fff;
      opacity: 0.6;
      margin-left: 8px;
    }

    .point {
      font-size: 10px;
      color: #fff;
      margin-left: 10px;
      margin-right: 2px;
      z-index: 9;
    }
    .styleAll {
      width: 17px;
      height: 17px;
      background-color: #2f2f2f;
      border-radius: 3px;
      color: #fff;
      text-align: center;
    }

    .more {
      width: 43px;
      height: 20px;
      background: linear-gradient(142deg, #ffe9ce 0%, #ffd6a7 100%);
      opacity: 1;
      border-radius: 18px;
      font-size: 11px;
      color: #fe960f;
      padding-left: 4px;
      font-weight: 800;
      .iconfont {
        font-size: 10px;
      }
    }
  }

  .conter {
    border-radius: 12px;
    margin-top: 15px;
    position: relative;
    z-index: 9;
    .itemCon {
      display: inline-block;
      width: 93px;

      .item {
        width: 100%;

        .pictrue {
          width: 100%;
          height: 93px;
          overflow: hidden;

          image {
            width: 100%;
            height: 100%;
            border-radius: 3px;
          }
        }

        .name {
          font-size: 13px;
          color: #333;
          margin-top: 4px;
        }
        .y_money {
          font-size: 12px;
          color: #999999;
          text-decoration: line-through;
        }

        .x_money {
          font-size: 14px;
          height: 100%;
          font-weight: bold;
          margin-top: 2px;
          .num {
            font-size: 14px;
          }
        }

        .money {
          margin-top: 7px;
        }
      }
    }
  }
}
.conter_y {
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  grid-template-rows: auto;
  grid-gap: 10px;
  width: 100%;
  margin-top: 15px;
  position: relative;
  .item {
    display: flex;
    width: 100%;

    .pictrue {
      width: 108px;
      height: 108px;

      image {
        width: 100%;
        height: 100%;
      }
    }
    .text-info {
      //z-index: 9;
      margin-left: 10px;
      flex: 1;
      .title {
        width: 100%;
        height: 40px;
        line-height: 20px;
        color: #333;
      }

      .old-price {
        font-weight: normal;
        font-size: 12px;
        color: #999;
      }

      .price {
        font-size: 18px;
        font-weight: 600;

        text {
          padding-bottom: 2px;
          font-size: 13px;
          font-weight: normal;
        }
      }
    }
  }
}
</style>
