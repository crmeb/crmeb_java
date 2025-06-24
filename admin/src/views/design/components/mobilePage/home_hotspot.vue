<template>
  <div class="mobile-page" v-if="configObj" :style="boxStyle">
    <div class="advert">
      <template v-if="style === 0">
        <div class="advertItem01 acea-row" v-for="(item, index) in picList" :key="index">
          <img :src="item.image" v-if="item.image" :style="contentStyle" />
          <div class="empty-box" v-else>
            <span class="iconfont icontupian"></span>
          </div>
        </div>
      </template>
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
  name: 'home_hotspot',
  cname: '热区',
  configName: 'c_hotspot',
  icon: 't-icon-zujian-requ',
  type: 0, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'homeHotspot', // 外面匹配名称
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
    contentStyle() {
      return [{ 'border-radius': this.configObj.contentStyle.val ? this.configObj.contentStyle.val + 'px' : '0' }];
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
        name: 'homeHotspot',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '热区',
        },
        tabConfig: {
          title: '选择样式',
          tabTitle: '内容设置',
          isShow: 0,
          tabVal: 0,
          list: [
            {
              val: '样式一',
              icon: 'icon-tupian-yangshiyi',
              count: 1,
            },
          ],
        },
        checkoutConfig: {
          title: '图片设置',
          tabTitle: '内容设置',
          checkoutVal: '2',
          isShow: 0,
          list: [],
          hotspot: [],
        },
        picStyle: {
          tabTitle: '内容设置',
          tabVal: 0,
          isHotspot: 1,
          picList: [
            {
              image: '',
              link: '',
            },
          ],
        },
        menuConfig: {
          title: '',
          maxList: 1,
          isCube: 1,
          isSmall: false,
          list: [
            {
              img: '',
              info: [
                {
                  title: '链接',
                  tips: '请输入链接',
                  value: '',
                  max: 100,
                },
              ],
            },
          ],
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
        contentStyle: {
          tabTitle: '圆角设置',
          title: '内容圆角',
          name: 'contentStyle',
          val: 0,
          min: 0,
          max: 30,
        },
        // 左右间距
        lrConfig: {
          title: '左右边距',
          val: 12,
          min: 0,
          max: 40,
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
        // 页面间距
        igConfig: {
          title: '图片间距',
          val: 0,
          min: 0,
          max: 30,
        },
      },
      pageData: {},
      style: 0,
      picList: [],
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
        this.style = data.tabConfig.tabVal;
        if (!data.picStyle.picList.length) {
          this.picList = [
            {
              image: '',
              link: '',
            },
          ];
        } else {
          this.picList = data.picStyle.picList;
        }
      }
    },
  },
};
</script>
<style scoped lang="scss">
.pageOn {
  border-radius: 12px !important;

  .advertItem01 {
    img {
      border-radius: 10px;
    }
  }

  .advertItem02 {
    .item {
      &:nth-child(1) {
        img {
          border-radius: 10px 0 0 10px;
        }
      }

      &:nth-child(2) {
        img {
          border-radius: 0 10px 10px 0;
        }
      }
    }
  }

  .advertItem03 {
    .item {
      &:nth-child(1) {
        img {
          border-radius: 10px 0 0 10px;
        }
      }

      &:nth-child(2) {
        img {
          border-radius: 0;
        }
      }

      &:nth-child(3) {
        img {
          border-radius: 0 10px 10px 0;
        }
      }
    }
  }

  .advertItem04 {
    .item {
      &:nth-child(1) {
        img {
          border-radius: 10px 0 0 10px;
        }
      }

      &:nth-child(2) {
        .pic {
          &:nth-child(1) {
            img {
              border-radius: 0 10px 0 0;
            }
          }

          &:nth-child(2) {
            img {
              border-radius: 0 0 10px 0;
            }
          }
        }
      }
    }
  }

  .advertItem05 {
    .item {
      &:nth-child(1) {
        img {
          border-radius: 10px 0 0 10px;
        }
      }

      &:nth-child(2) {
        img {
          border-radius: 0;
        }
      }

      &:nth-child(4) {
        img {
          border-radius: 0 10px 10px 0;
        }
      }
    }
  }

  .advertItem06 {
    .item {
      &:nth-child(1) {
        img {
          border-radius: 10px 0 0 0;
        }
      }

      &:nth-child(2) {
        img {
          border-radius: 0 10px 0 0;
        }
      }

      &:nth-child(3) {
        img {
          border-radius: 0 0 0 10px;
        }
      }

      &:nth-child(4) {
        img {
          border-radius: 0 0 10px 0;
        }
      }
    }
  }
}

.mobile-page {
  .advert {
    .advertItem01 {
      width: 100%;
      height: 100%;

      .empty-box {
        width: 100%;
        height: 379px;
        border-radius: 0;

        .icontupian {
          font-size: 50px;
          color: #999;
        }
      }

      img {
        width: 100%;
        height: 100%;
      }
    }

    .advertItem02 {
      width: 100%;

      .item {
        width: 50%;
        height: auto;

        img {
          width: 100%;
          height: 100%;
        }

        .empty-box {
          width: 100%;
          height: 189.5px;
          border-radius: 0;
        }
      }
    }

    .advertItem03 {
      .item {
        width: 33.3333%;

        .empty-box {
          width: 100%;
          height: 126.4px;
          border-radius: 0;
        }
      }
    }

    .advertItem04 {
      .item {
        width: 50%;
        height: 189.5px;

        .empty-box {
          width: 100%;
          height: 100%;
          border-radius: 0;
        }

        img {
          width: 100%;
          height: 100%;
        }

        .pic {
          width: 100%;
          height: 94.75px;
        }
      }
    }

    .advertItem05 {
      .item {
        width: 25%;

        .empty-box {
          width: 100%;
          height: 94.75px;
          border-radius: 0;
        }
      }
    }

    .advertItem06 {
      .item {
        width: 50%;
        height: 95px;

        img {
          width: 100%;
          height: 100%;
        }

        .empty-box {
          width: 100%;
          height: 100%;
          border-radius: 0;
        }
      }
    }
  }
}
</style>
