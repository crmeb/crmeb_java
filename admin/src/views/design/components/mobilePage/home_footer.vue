<template>
  <div v-if="configObj" class="page-fooot">
    <div class="overflowy" :style="boxStyle">
      <div class="page-foot" @click="showFoot">
        <div class="foot-item" v-for="(item, index) in menuList" :key="index">
          <div
            v-if="
              (!item.checked && !item.unchecked) ||
              (item.checked && !item.unchecked) ||
              (!item.checked && item.unchecked)
            "
          >
            <img src="@/assets/imgs/moren.jpg" alt="" />
          </div>
          <div v-else>
            <img :src="item.checked" alt="" v-if="index === curIndex" />
            <img :src="item.unchecked" alt="" v-else />
          </div>
          <p
            :style="{
              color: curIndex === index ? (themeStyle ? configObj.checkColor.color[0].item : mobileTheme) : fontColor,
            }"
          >
            {{ item.name }}
          </p>
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
import { is } from 'core-js/core/object';
import { mapState, mapGetters } from 'vuex';
export default {
  name: 'home_footer',
  cname: '底部导航',
  configName: 'c_home_footer',
  icon: 't-icon-zujian-dibucaidan',
  type: 0, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'footer', // 外面匹配名称
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
        { margin: '0' + ' ' + this.configObj.lrConfig.val + 'px' + ' ' + 0 },
      ];
    },
    //标签文字颜色
    fontColor() {
      return this.configObj.fontColor.color[0].item;
    },
    //选中颜色
    checkColor() {
      return { background: this.configObj.checkColor.color[0].item };
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
        name: 'footer',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '底部导航',
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
          isShow: 0,
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
        lrConfig: {
          title: '左右边距',
          tabTitle: '边距设置',
          val: 0,
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
        menuList: {
          tabTitle: '内容设置',
          tabVal: 0,
          list: [
            {
              checked: require('@/assets/imgs/foot-002.png'),
              unchecked: require('@/assets/imgs/foot-001.png'),
              name: '首页',
              link: '/pages/index/index',
            },
            {
              checked: require('@/assets/imgs/foot-004.png'),
              unchecked: require('@/assets/imgs/foot-003.png'),
              name: '分类',
              link: '/pages/goods_cate/goods_cate',
            },
            {
              checked: require('@/assets/imgs/foot-008.png'),
              unchecked: require('@/assets/imgs/foot-007.png'),
              name: '购物车',
              link: '/pages/order_addcart/order_addcart',
            },
            {
              checked: require('@/assets/imgs/foot-0010.png'),
              unchecked: require('@/assets/imgs/foot-009.png'),
              name: '我的',
              link: '/pages/user/index',
            },
          ],
        },
      },
      pageData: {},
      curIndex: 0,
      configObj: null,
      menuList: [],
      themeStyle: 0,
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.pageData = this.$store.state.mobildConfig.defaultArray[this.num];
      this.setConfig(this.pageData);
    });
  },
  methods: {
    // 页面底部点击
    showFoot() {
      // this.activeIndex = -101;
      // let obj = {};
      // for (var i in mConfig) {
      //   if (i == 'pageFoot') {
      //     obj = mConfig[i];
      //     obj.configName = mConfig[i].name;
      //     obj.cname = '底部菜单';
      //   }
      // }
      // let abc = obj;
      // this.rConfig = [];
      // this.rConfig[0] = JSON.parse(JSON.stringify(obj));
    },
    setConfig(data) {
      if (!data) return;
      if (data) {
        this.configObj = data;
        this.menuList = data.menuList.list;
        this.themeStyle = data.themeStyleConfig.tabVal;
      }
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep.scrollCon {
  position: absolute;
  bottom: 0;
  left: 50%;
  margin-left: -190px;
  overflow: hidden;
}

.page-foot {
  position: relative;
  width: 100%;
  margin: 4px auto 3px auto;
  display: flex;

  .delete-box {
    display: none;
    position: absolute;
    left: -2px;
    top: 0;
    width: 383px;
    height: 100%;
    border: 2px dashed var(--prev-color-primary) !important;
    padding: 10px 0;
  }

  &:hover,
  &.on {
    /*cursor: move;*/

    .delete-box {
      /*display: block;*/
    }
  }

  &.on {
    cursor: move;

    .delete-box {
      display: block;
      border: 2px solid var(--prev-color-primary) !important;
      box-shadow: 0 0 10px 0 rgba(24, 144, 255, 0.3);
    }
  }
}

.overflowy {
  overflow-y: scroll;
  width: 100%;

  .picture {
    width: 379px;
    height: 20px;
    margin: 0 auto;
    background-color: #fff;
  }
}

.page-fooot {
  display: flex;
  background: #fff;
  position: relative;
  width: 100%;

  // .bg{
  //     position: absolute;
  //     top: 0;
  //     left: 0;
  //     width: 379px;
  //     height: 100%;
  // }
  &.filter {
    filter: blur(1px);
    // opacity: .8;
  }

  .foot-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    .empty-box,
    img {
      width: 25px;
      height: 25px;
    }

    p {
      padding: 0;
      margin: 0;
      font-size: 12px;
      color: #282828;

      &.on {
        color: #00a4f8;
      }
    }
  }
}
</style>
