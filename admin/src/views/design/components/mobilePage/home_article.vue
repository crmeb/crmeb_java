<template>
  <div v-if="configObj">
    <div class="mobile-page" :style="boxStyle">
      <div class="list">
        <div v-if="articleList.length === 0" class="item acea-row row-between" :style="layoutConfig">
          <div class="pictrues tui-skeleton-rect">
            <div class="img-box">
              <div class="empty-box" :style="{ borderRadius: `${configObj.contentStyle.val}px` }">
                <span class="iconfont icontupian"></span>
              </div>
            </div>
          </div>
          <div class="text text-box">
            <div class="text acea-row row-column-between">
              <div class="name line2 tui-skeleton-rect" :style="titleColor">
                文章标题展示，最多展示2行，仍过长展示不下时展示为…
              </div>
              <div class="tui-skeleton-rect" :style="timeColor">2022-05-05 11:40</div>
            </div>
          </div>
        </div>
        <template v-if="articleList.length > 0">
          <div
            class="item acea-row row-between"
            v-for="(item, index) in articleList"
            :key="index"
            :style="layoutConfig"
          >
            <div class="pictrues tui-skeleton-rect">
              <el-image
                :src="item.imageInput"
                class="pic"
                :style="{ borderRadius: `${configObj.contentStyle.val}px` }"
              ></el-image>
            </div>
            <div class="text text-box">
              <div class="name line2 tui-skeleton-rect" :style="titleColor">{{ item.title }}</div>
              <div class="tui-skeleton-rect" :style="timeColor">{{ item.updateTime }}</div>
            </div>
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
  name: 'home_article', // 组件名称
  cname: '文章列表', // 标题名称
  icon: 't-icon-zujian-wenzhangliebiao',
  defaultName: 'homeArticle', // 外面匹配名称
  configName: 'c_home_article', // 右侧配置名称
  type: 0, // 0 基础组件 1 营销组件 2工具组件
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
    //时间颜色
    timeColor() {
      return [{ color: this.configObj.timeColor.color[0].item }];
    },
    //标题颜色
    titleColor() {
      return [{ color: this.configObj.titleColor.color[0].item }];
    },
    //布局样式
    layoutConfig() {
      return [
        { marginBottom: `${this.configObj.contentConfig.val}px ` },
        { flexDirection: this.configObj.layoutConfig.tabVal === 0 ? '' : 'row-reverse' },
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
        name: 'homeArticle',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '文章列表',
        },
        //数量
        numConfig: {
          val: 6,
          isShow: 1,
          title: '文章数量',
          max: 8,
        },
        //文章分类
        selectConfig: {
          title: '文章分类',
          tabTitle: '文章设置',
          activeValue: [],
          isMultiple: false,
          list: [],
          articleList: [],
          isShow: 1,
        },
        bgStyle: {
          tabTitle: '圆角设置',
          title: '背景圆角',
          name: 'bgStyle',
          val: 0,
          min: 0,
          max: 30,
        },
        mbConfig: {
          title: '页面间距',
          val: 10,
          min: 0,
        },
        contentStyle: {
          title: '内容圆角',
          name: 'contentStyle',
          val: 0,
          min: 0,
          max: 30,
        },
        titleColor: {
          title: '标题颜色',
          name: 'titleColor',
          default: [
            {
              item: '#282828',
            },
          ],
          color: [
            {
              item: '#282828',
            },
          ],
        },
        timeColor: {
          title: '时间颜色',
          name: 'timeColor',
          default: [
            {
              item: '#282828',
            },
          ],
          color: [
            {
              item: '#282828',
            },
          ],
        },
        // 左右间距
        lrConfig: {
          title: '左右边距',
          val: 12,
          min: 0,
          max: 15,
        },
        // 文章间距content
        contentConfig: {
          title: '文章间距',
          val: 10,
          min: 0,
          max: 100,
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
        // 布局样式
        layoutConfig: {
          cname: 'layoutConfig',
          title: '布局样式',
          tabTitle: '布局设置',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '居左',
              icon: 'icon-juzuo',
            },
            {
              val: '居右',
              icon: 'icon-juyou',
            },
          ],
        },
      },
      articleList: [],
      pageData: {},
      mTOP: 0,
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
        this.articleList = data.selectConfig.articleList;
        this.mTOP = data.upConfig.val;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.list {
  padding-left: 10px;
  padding-right: 10px;
  .item &:last-child {
    margin-bottom: 0;
  }
}
.line2 {
  height: 42px;
  line-height: 22px;
  margin-bottom: 18px;
}
.empty-box {
  width: 100%;
  height: 78px;
  background: #f3f9ff;
}
.list .item {
  &:last-child {
    margin-bottom: 0 !important;
  }
  /*padding: 17px 0;*/
  .pictrues {
    width: 125px;
    height: 78px;
    .pic {
      width: 100%;
      height: 100%;
      border-radius: 7px;
    }
  }
  .text {
    height: 78px;
    font-size: 15px;
    color: #333333;
  }
}
.text-box {
  flex: 1;
  margin-left: 10px;
}
</style>
