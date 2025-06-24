<template>
  <div v-if="configObj" :style="boxStyle">
    <!--多行展示-->
    <div class="mobile-page" v-if="isOne">
      <div class="list_menu" :style="gridColumns">
        <template v-for="(item, index) in vuexMenu">
          <div v-show="item.status" class="item">
            <div class="img-box">
              <img :src="item.img" alt="" v-if="item.img" :style="contentStyle" />
              <div class="empty-box on" v-else :style="contentStyle">
                <span class="iconfont-diy iconfont icontupian"></span>
              </div>
            </div>
            <p :style="titleColor">{{ item.info[0].value }}</p>
          </div>
        </template>
      </div>
    </div>
    <!--单行展示-->
    <div class="mobile-page" v-else>
      <div class="home_menu" :style="gridColumns">
        <template v-for="(item, index) in vuexMenu">
          <div v-show="item.status" class="menu-item">
            <div class="img-box">
              <img :src="item.img" alt="" v-if="item.img" :style="contentStyle" />
              <div class="empty-box on" v-else :style="contentStyle">
                <span class="iconfont-diy iconfont icontupian"></span>
              </div>
            </div>
            <p :style="titleColor">{{ item.info[0].value }}</p>
          </div>
        </template>
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
import { mapState } from 'vuex';
export default {
  name: 'home_menu',
  cname: '导航组',
  icon: 't-icon-zujian-daohangzu',
  configName: 'c_home_menu',
  type: 0, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'menus', // 外面匹配名称
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
    //行数
    gridColumns() {
      if (this.configObj.number.tabVal == 0) {
        return [
          { 'grid-row-gap': this.configObj.contentConfig.val + 'px' },
          { 'grid-template-columns': 'repeat(3, 1fr)' },
        ];
      } else if (this.configObj.number.tabVal == 1) {
        return [
          { 'grid-row-gap': this.configObj.contentConfig.val + 'px' },
          { 'grid-template-columns': 'repeat(4, 1fr)' },
        ];
      } else {
        return [
          { 'grid-row-gap': this.configObj.contentConfig.val + 'px' },
          { 'grid-template-columns': 'repeat(5, 1fr)' },
        ];
      }
    },
    titleColor() {
      return {
        color: this.configObj.titleColor.color[0].item,
      };
    },
    contentStyle() {
      return {
        'border-radius': this.configObj.contentStyle.val ? this.configObj.contentStyle.val + 'px' : '0',
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
        name: 'menus',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '导航组',
        },
        tabConfig: {
          title: '展示样式',
          tabTitle: '展示设置',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '单行展示',
              icon: 'icon-yangshiyi',
            },
            {
              val: '多行展示',
              icon: 'icon-yangshier',
            },
          ],
        },
        rowsNum: {
          title: '显示行数',
          name: 'rowsNum',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '2行',
              icon: 'icon-daohang-2hang',
            },
            {
              val: '3行',
              icon: 'icon-daohang-3hang',
            },
            {
              val: '4行',
              icon: 'icon-daohang-4hang',
            },
          ],
        },
        number: {
          title: '显示个数',
          name: 'number',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '3个',
              icon: 'icon-daohang-3ge',
            },
            {
              val: '4个',
              icon: 'icon-daohang-4ge',
            },
            {
              val: '5个',
              icon: 'icon-daohang-5ge',
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
        contentStyle: {
          title: '内容圆角',
          name: 'contentStyle',
          val: 30,
          min: 0,
          max: 30,
        },
        menuConfig: {
          tabTitle: '内容设置',
          title: '最多可添加1张图片，建议宽度90 * 90px',
          maxList: 100,
          list: [
            {
              img: '',
              info: [
                {
                  title: '标题',
                  value: '今日推荐',
                  tips: '选填，不超过4个字',
                  max: 4,
                  status: true,
                },
                {
                  title: '链接',
                  value: '',
                  tips: '请输入链接',
                  max: 100,
                  status: true,
                },
              ],
              status: true,
            },
            {
              img: '',
              info: [
                {
                  title: '标题',
                  value: '热门榜单',
                  tips: '选填，不超过4个字',
                  max: 4,
                  status: true,
                },
                {
                  title: '链接',
                  value: '',
                  tips: '请输入链接',
                  max: 100,
                  status: true,
                },
              ],
              status: true,
            },
            {
              img: '',
              info: [
                {
                  title: '标题',
                  value: '首发新品',
                  tips: '选填，不超过4个字',
                  max: 4,
                  status: true,
                },
                {
                  title: '链接',
                  value: '',
                  tips: '请输入链接',
                  max: 100,
                  status: true,
                },
              ],
              status: true,
            },
            {
              img: '',
              info: [
                {
                  title: '标题',
                  value: '促销单品',
                  tips: '选填，不超过4个字',
                  max: 4,
                  status: true,
                },
                {
                  title: '链接',
                  value: '',
                  tips: '请输入链接',
                  max: 100,
                  status: true,
                },
              ],
              status: true,
            },
          ],
        },
        bgColor: {
          tabTitle: '颜色设置',
          title: '背景颜色',
          name: 'bgColor',
          color: [
            {
              item: '#fff',
            },
            {
              item: '#fff',
            },
          ],
          default: [
            {
              item: '#fff',
            },
            {
              item: '#fff',
            },
          ],
        },
        titleColor: {
          title: '文字颜色',
          name: 'titleColor',
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
        contentConfig: {
          title: '内容间距',
          val: 10,
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
        // 页面间距
        mbConfig: {
          title: '页面间距',
          val: 10,
          min: 0,
        },
      },
      configObj: null,
      vuexMenu: '',
      isOne: 0,
      number: 0,
      rowsNum: 0,
      pageData: {},
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
    // 对象转数组
    objToArr(data) {
      let obj = Object.keys(data);
      let m = obj.map((key) => data[key]);
      return m;
    },
    setConfig(data) {
      if (!data) return;
      if (data) {
        this.configObj = data;
        this.isOne = data.tabConfig.tabVal;
        let rowsNum = data.rowsNum.tabVal;
        let number = data.number.tabVal;
        let list = this.objToArr(data.menuConfig.list);
        this.number = number;
        this.rowsNum = rowsNum;
        let vuexMenu = [];
        if (rowsNum === 0) {
          if (number === 0) {
            vuexMenu = list.splice(0, 6);
          } else if (number === 1) {
            vuexMenu = list.splice(0, 8);
          } else {
            vuexMenu = list.splice(0, 10);
          }
        } else if (rowsNum === 1) {
          if (number === 0) {
            vuexMenu = list.splice(0, 9);
          } else if (number === 1) {
            vuexMenu = list.splice(0, 12);
          } else {
            vuexMenu = list.splice(0, 15);
          }
        } else {
          if (number === 0) {
            vuexMenu = list.splice(0, 12);
          } else if (number === 1) {
            vuexMenu = list.splice(0, 16);
          } else {
            vuexMenu = list.splice(0, 20);
          }
        }
        this.vuexMenu = vuexMenu;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.pageOn {
  border-radius: 10px !important;
}
.list_menu {
  padding: 0 12px;
  display: grid;
  grid-template-rows: auto;
  .item {
    font-size: 11px;
    color: #282828;
    text-align: center;
    &.four {
      width: 25%;
    }

    &.five {
      width: 20%;
    }

    .img-box {
      width: 50px;
      height: 50px;
      margin: 0 auto 7px auto;
      &.on {
        border-radius: 50%;
        img,
        .empty-box {
          border-radius: 50%;
        }
      }
      img {
        width: 100%;
        height: 100%;
      }
    }
  }
  .icontupian {
    font-size: 16px;
  }
}

.home_menu {
  display: flex;
  overflow: hidden;
  .menu-item {
    font-size: 11px;
    color: #282828;
    text-align: center;
    flex: 0 0 20%;
    .img-box {
      width: 50px;
      height: 50px;
      margin: 0 auto;
      &.on {
        border-radius: 50%;
        img,
        .empty-box {
          border-radius: 50%;
        }
      }
    }
    .box,
    img {
      width: 100%;
      height: 100%;
    }
    .box {
      background: #d8d8d8;
    }
    p {
      margin-top: 5px;
    }
    &:nth-child(5n) {
      margin-right: 0;
    }
  }
  &.on {
    .menu-item {
      margin-right: 51px;
      &:nth-child(5n) {
        margin-right: 51px;
      }

      &:nth-child(4n) {
        margin-right: 0;
      }
    }
  }
  .icontupian {
    font-size: 16px;
  }
}

.dot {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-bottom: 10px;

  &.number {
    bottom: 15px;
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
    .dot-item {
      width: 8px;
      height: 2px;
      background: #aaaaaa;
      margin: 0 3px;
    }
  }
}
</style>
