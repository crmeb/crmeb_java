<template>
  <div v-if="configObj" :style="mbConfig">
    <div class="menus" :style="tabBgColor">
      <div
        class="item shrink"
        v-for="(item, index) in navlist"
        :key="index"
        :class="{ on: curIndex === index }"
        :style="{
          color: curIndex === index ? (checkThemeStyle ? configObj.checkColor.color[0].item : mobileTheme) : fontColor,
        }"
      >
        {{ item.val }} <span :style="checkColor"></span>
      </div>
    </div>

    <div :style="boxStyle" class="mobile-page paddingBox" :class="bgStyle === 0 ? '' : 'pageOn'">
      <div class="home_product">
        <!-- 单列 -->
        <template v-if="itemStyle == 0">
          <div class="list-wrapper itemA" v-if="goodslist && goodslist.length > 0">
            <div
              class="item"
              v-for="(item, index) in goodslist"
              :key="index"
              :style="{ marginBottom: contentConfig + 'px' }"
            >
              <div class="img-box">
                <img v-if="item.image" :style="contentStyle" :src="item.image" alt="" />
                <div v-else class="empty-box" :style="contentStyle">
                  <span class="iconfont icontupian"></span>
                </div>
              </div>
              <div class="info">
                <div class="hd">
                  <div class="text">
                    <div class="title line2" :style="titleColor">
                      <span v-if="titleShow">{{ item.storeName }}</span>
                    </div>
                  </div>
                </div>
                <div class="price" :style="priceColor">
                  <div class="num" v-if="priceShow">
                    ￥<span>{{ item.price }}</span>
                  </div>
                </div>
                <div class="sold" v-if="soldShow" :style="soldColor">
                  已售 {{ Math.floor(item.sales) + Math.floor(item.ficti) || 0 }} {{ item.unitName }}
                </div>
              </div>
            </div>
          </div>
          <div class="list-wrapper itemA" v-else>
            <div class="item" :style="{ marginBottom: contentConfig + 'px' }">
              <div class="img-box">
                <div class="empty-box" :style="contentStyle"><span class="iconfont icontupian"></span></div>
              </div>
              <div class="info">
                <div class="hd">
                  <div class="text">
                    <div class="title line2">
                      <span v-if="titleShow" :style="titleColor">商品名称</span>
                    </div>
                  </div>
                </div>
                <div class="price" :style="priceColor">
                  <div class="num" v-if="priceShow">¥<span>199</span></div>
                </div>
                <div class="sold" v-if="soldShow" :style="soldColor">已售 999 件</div>
              </div>
            </div>
            <div class="item" :style="{ marginBottom: contentConfig + 'px' }">
              <div class="img-box">
                <div class="empty-box" :style="contentStyle"><span class="iconfont icontupian"></span></div>
              </div>
              <div class="info">
                <div class="hd">
                  <div class="text">
                    <div class="title line2">
                      <span v-if="titleShow" :style="titleColor">商品名称</span>
                    </div>
                  </div>
                </div>
                <div class="price" :style="priceColor">
                  <div class="num" v-if="priceShow">¥<span>199</span></div>
                </div>
                <div class="sold" v-if="soldShow" :style="soldColor">已售 999 件</div>
              </div>
            </div>
          </div>
        </template>
        <!-- 二列 -->
        <template v-if="itemStyle == 1">
          <div
            class="list-wrapper itemC"
            v-if="goodslist && goodslist.length > 0"
            :style="{ gridGap: contentConfig + 'px' }"
          >
            <div class="item auto" :style="contentStyle" v-for="(item, index) in goodslist" :key="index">
              <div class="img-box">
                <img v-if="item.image" :src="item.image" alt="" />
                <div v-else class="empty-box"><span class="iconfont icontupian"></span></div>
              </div>
              <div class="info acea-row row-column row-between">
                <div class="hd line2 title" v-if="titleShow" :style="titleColor">
                  <span>{{ item.storeName }}</span>
                </div>
                <div>
                  <div class="price" :style="priceColor">
                    <div class="num" v-if="priceShow">
                      ¥<span>{{ item.price }}</span>
                    </div>
                  </div>
                  <div class="sold" v-if="soldShow" :style="soldColor">
                    已售 {{ Math.floor(item.sales) + Math.floor(item.ficti) || 0 }} {{ item.unitName }}
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="list-wrapper itemC" v-else :style="{ gridGap: contentConfig + 'px' }">
            <div class="item auto">
              <div class="img-box">
                <div class="empty-box" :style="contentStyle"><span class="iconfont icontupian"></span></div>
              </div>
              <div class="info">
                <div class="hd acea-row">
                  <span class="title line2" v-if="titleShow" :style="titleColor">商品名称</span>
                </div>
                <div class="price" :style="priceColor">
                  <div class="num" v-if="priceShow">¥<span>66.66</span></div>
                </div>
                <div class="sold" v-if="soldShow" :style="soldColor">已售 999 件</div>
              </div>
            </div>
            <div class="item auto">
              <div class="img-box">
                <div class="empty-box" :style="contentStyle"><span class="iconfont icontupian"></span></div>
              </div>
              <div class="info">
                <div class="hd acea-row">
                  <span class="title line2" v-if="titleShow" :style="titleColor">商品名称</span>
                </div>
                <div class="price" :style="priceColor">
                  <div class="num" v-if="priceShow">¥<span>66.66</span></div>
                </div>
                <div class="sold" v-if="soldShow" :style="soldColor">已售 999 件</div>
              </div>
            </div>
          </div>
        </template>
        <!-- 三列 -->
        <template v-if="itemStyle == 2">
          <div
            class="list-wrapper itemB listC"
            :style="{ gridGap: contentConfig + 'px' }"
            v-if="goodslist && goodslist.length > 0"
          >
            <div class="item auto" v-for="(item, index) in goodslist" :key="index">
              <div class="img-box">
                <img v-if="item.image" :style="contentStyle" :src="item.image" alt="" />
                <div v-else class="empty-box" :style="contentStyle">
                  <span class="iconfont icontupian"></span>
                </div>
              </div>
              <div class="info acea-row row-column row-between">
                <div v-if="titleShow" class="hd line2 title" :style="titleColor">
                  <span>{{ item.storeName }}</span>
                </div>
                <div>
                  <div class="price" :style="priceColor">
                    <div class="num" v-if="priceShow">
                      ¥<span>{{ item.price }}</span>
                    </div>
                  </div>
                  <div class="sold" v-if="soldShow" :style="soldColor">
                    已售 {{ Math.floor(item.sales) + Math.floor(item.ficti) || 0 }} {{ item.unitName }}
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="list-wrapper itemB listC" :style="{ gridGap: contentConfig + 'px' }" v-else>
            <div class="item auto">
              <div class="img-box">
                <div class="empty-box" :style="contentStyle"><span class="iconfont icontupian"></span></div>
              </div>
              <div class="info">
                <div class="hd acea-row">
                  <span class="title line2" v-if="titleShow" :style="titleColor">商品名称</span>
                </div>
                <div class="price" :style="priceColor">
                  <div class="num" v-if="priceShow">¥<span>66.66</span></div>
                </div>
                <div class="sold" v-if="soldShow" :style="soldColor">已售 999 件</div>
              </div>
            </div>
            <div class="item auto">
              <div class="img-box">
                <div class="empty-box" :style="contentStyle"><span class="iconfont icontupian"></span></div>
              </div>
              <div class="info">
                <div class="hd acea-row">
                  <span class="title line2" v-if="titleShow" :style="titleColor">商品名称</span>
                </div>
                <div class="price" :style="priceColor">
                  <div class="num" v-if="priceShow">¥<span>66.66</span></div>
                </div>
                <div class="sold" v-if="soldShow" :style="soldColor">已售 999 件</div>
              </div>
            </div>
            <div class="item">
              <div class="img-box">
                <div class="empty-box" :style="contentStyle"><span class="iconfont icontupian"></span></div>
              </div>
              <div class="info">
                <div class="hd acea-row">
                  <span class="title line2" v-if="titleShow" :style="titleColor">商品名称</span>
                </div>
                <div class="price" :style="priceColor">
                  <div class="num" v-if="priceShow">¥<span>66.66</span></div>
                </div>
                <div class="sold" v-if="soldShow" :style="soldColor">已售 999 件</div>
              </div>
            </div>
          </div>
        </template>
        <!-- 大图 -->
        <template v-if="itemStyle == 3">
          <div class="listBig" v-if="goodslist && goodslist.length > 0">
            <div
              class="itemBig"
              v-for="(item, index) in goodslist"
              :key="index"
              :style="{ marginBottom: contentConfig + 'px' }"
            >
              <div class="img-box">
                <img v-if="item.image" :style="contentStyle" :src="item.image" alt="" />
                <div v-else class="empty-box" :style="contentStyle">
                  <span class="iconfont icontupian"></span>
                </div>
              </div>
              <div class="info">
                <div class="hd line2 title" :style="titleColor">
                  <span v-if="titleShow">{{ item.storeName }}</span>
                </div>
                <div class="price" :style="priceColor">
                  <div class="num" v-if="priceShow">
                    ¥<span>{{ item.price }}</span>
                  </div>
                </div>
                <div class="sold" v-if="soldShow" :style="soldColor">
                  已售 {{ Math.floor(item.sales) + Math.floor(item.ficti) || 0 }} {{ item.unitName }}
                </div>
              </div>
            </div>
          </div>
          <div class="listBig" v-else>
            <div class="itemBig" :style="{ marginBottom: contentConfig + 'px' }">
              <div class="img-box">
                <div class="empty-box" :style="contentStyle"><span class="iconfont icontupian"></span></div>
              </div>
              <div class="info">
                <div class="hd acea-row">
                  <span class="title line2" v-if="titleShow" :style="titleColor">商品名称</span>
                </div>
                <div class="price" :style="priceColor">
                  <div class="num" v-if="priceShow">¥<span>66.66</span></div>
                </div>
                <div class="sold" v-if="soldShow" :style="soldColor">已售 999 件</div>
              </div>
            </div>
            <div class="itemBig" :style="{ marginBottom: contentConfig + 'px' }">
              <div class="img-box">
                <div class="empty-box" :style="contentStyle"><span class="iconfont icontupian"></span></div>
              </div>
              <div class="info">
                <div class="hd acea-row">
                  <span v-if="titleShow" class="title line2" :style="titleColor">商品名称</span>
                </div>
                <div class="price" :style="priceColor">
                  <div class="num" v-if="priceShow">¥<span>66.66</span></div>
                </div>
                <div class="sold" v-if="soldShow" :style="soldColor">已售 999 件</div>
              </div>
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
import { mapState, mapGetters } from 'vuex';
export default {
  name: 'home_tab',
  cname: '选项卡',
  configName: 'c_home_tab',
  icon: 't-icon-zujian-xuanxiangka',
  type: 0, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'homeTab', // 外面匹配名称
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
        {
          background: `linear-gradient(${this.configObj.bgColor.color[0].item}, ${this.configObj.bgColor.color[1].item})`,
        },
        { margin: this.configObj.topConfig.val + 'px' + ' ' + this.configObj.lrConfig.val + 'px' + ' ' + 0 },
        { padding: this.configObj.upConfig.val + 'px' + ' ' + 0 + ' ' + this.configObj.downConfig.val + 'px' },
      ];
    },
    //页面间距
    mbConfig() {
      return { marginTop: this.configObj.mbConfig.val ? this.configObj.mbConfig.val + 'px' : 0 };
    },
    //选项卡背景颜色
    tabBgColor() {
      return {
        background: `linear-gradient(${this.configObj.tabBgColor.color[0].item}, ${this.configObj.tabBgColor.color[1].item})`,
      };
    },
    //商品标题颜色
    titleColor() {
      return { color: this.configObj.titleColor.color[0].item };
    },
    //标签文字颜色
    fontColor() {
      return this.configObj.fontColor.color[0].item;
    },
    //选中颜色
    checkColor() {
      return { background: this.checkThemeStyle ? this.configObj.checkColor.color[0].item : this.mobileTheme };
    },
    //价格颜色
    priceColor() {
      return { color: this.priceThemeStyle ? this.configObj.priceColor.color[0].item : this.mobileTheme };
    },
    //已售数量
    soldColor() {
      return { color: this.configObj.soldColor.color[0].item };
    },
    //内容圆角
    contentStyle() {
      return { 'border-radius': this.configObj.contentStyle.val ? this.configObj.contentStyle.val + 'px' : '0' };
    },
    //商品名称
    titleShow() {
      if (this.configObj.typeConfig.activeValue.indexOf(0) !== -1) {
        return true;
      } else {
        return false;
      }
    },
    //价格
    priceShow() {
      if (this.configObj.typeConfig.activeValue.indexOf(1) !== -1) {
        return true;
      } else {
        return false;
      }
    },
    //销量
    soldShow() {
      if (this.configObj.typeConfig.activeValue.indexOf(2) !== -1) {
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
        this.$nextTick(() => {
          let data = this.$store.state.mobildConfig.defaultArray[nVal];
          this.setConfig(data);
        });
      },
      deep: true,
    },
    defaultArray: {
      handler(nVal, oVal) {
        this.$nextTick(() => {
          let data = this.$store.state.mobildConfig.defaultArray[this.num];
          this.setConfig(data);
        });
      },
      deep: true,
    },
    configObj: {
      handler(nVal, oVal) {
        this.configObj.selectConfig.tabVal = this.configObj.tabItemConfig.tabVal;
      },
      deep: true,
    },
    checkThemeStyle: {
      handler(nVal, oVal) {
        this.configObj.checkColor.isShow = this.configObj.checkThemeStyleConfig.tabVal;
      },
      deep: true,
    },
    priceThemeStyle: {
      handler(nVal, oVal) {
        this.configObj.priceColor.isShow = this.configObj.priceThemeStyleConfig.tabVal;
      },
      deep: true,
    },
  },
  data() {
    return {
      curIndex: 0, //分类选中
      // 默认初始化数据禁止修改
      defaultConfig: {
        isHide: false,
        name: 'homeTab',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '选项卡',
        },
        tabItemConfig: {
          tabTitle: '选项卡设置',
          title: '选项卡设置',
          name: 'tabItemConfig',
          tabVal: 0,
          selectCate: [],
          list: [
            {
              val: '选项卡',
              activeList: null,
              activeTabIndex: 0,
              brandGoodsList: [],
              selectConfigList: [],
              merList: [],
              selectCate: [],
            },
          ],
        },
        itemStyle: {
          tabTitle: '展现形式',
          title: '展现样式',
          name: 'itemSstyle',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '单列',
              icon: 'icon-shangpin-danlie',
            },
            {
              val: '两列',
              icon: 'icon-shangpin-lianglie',
            },
            {
              val: '三列',
              icon: 'icon-shangpin-sanlie',
            },
            {
              val: '大图',
              icon: 'icon-shangpin-datu',
            },
          ],
        },
        tabConfig: {
          tabTitle: '商品设置',
          title: '商品列表',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              name: '指定商品',
            },
            {
              name: '指定分类',
            },
          ],
        },
        //显示内容
        typeConfig: {
          title: '展示信息',
          tabTitle: '显示内容',
          name: 'rowsNum',
          activeValue: [0, 1, 2],
          list: [
            {
              val: '商品名称',
            },
            {
              val: '商品价格',
            },
            {
              val: '已售数量',
            },
          ],
        },
        //分类
        selectConfig: {
          title: '商品分类',
          activeValue: [],
          list: [],
          isMultiple: true,
          goodsList: [],
          isShow: 0,
          tabVal: 0,
        },
        goodsSort: {
          title: '商品排序',
          name: 'goodsSort',
          tabVal: 0,
          isShow: 0,
          list: [
            {
              val: '综合',
              icon: 'icon-daohang-3hang',
            },
            {
              val: '销量',
              icon: 'icon-xiaoliang',
            },
            {
              val: '价格',
              icon: 'icon-jiage',
            },
          ],
        },
        numConfig: {
          val: 6,
          isShow: 1,
          max: 50,
        },
        //背景设置
        bgColor: {
          tabTitle: '背景设置',
          title: '背景颜色',
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
        tabBgColor: {
          title: '背景颜色',
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
        //标签设置
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
        //选中文字色调
        checkThemeStyleConfig: {
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
        //价格色调
        priceThemeStyleConfig: {
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
              item: '#e93323',
            },
          ],
          default: [
            {
              item: '#e93323',
            },
          ],
        },
        priceColor: {
          isShow: 0,
          title: '价格颜色',
          name: 'priceColor',
          default: [
            {
              item: '#e93323',
            },
          ],
          color: [
            {
              item: '#e93323',
            },
          ],
        },
        soldColor: {
          title: '已售数量颜色',
          name: 'soldColor',
          color: [
            {
              item: '#666',
            },
          ],
          default: [
            {
              item: '#666',
            },
          ],
        },
        titleColor: {
          title: '商品标题颜色',
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
        labelColor: {
          tabTitle: '显示内容',
          title: '标签背景颜色',
          name: 'labelColor',
          color: [
            {
              item: '#e93323',
            },
          ],
          default: [
            {
              item: '#e93323',
            },
          ],
        },
        // 上间距
        upConfig: {
          title: '上边距',
          tabTitle: '边距设置',
          val: 10,
          min: 0,
          max: 100,
        },
        // 下间距
        downConfig: {
          tabTitle: '边距设置',
          title: '下边距',
          val: 10,
          min: 0,
          max: 100,
        },
        lrConfig: {
          title: '左右边距',
          val: 12,
          min: 0,
          max: 15,
        },
        contentConfig: {
          title: '商品间距',
          val: 10,
          min: 0,
          max: 20,
        },
        mbConfig: {
          title: '页面间距',
          val: 0,
          min: 0,
        },
        topConfig: {
          title: '内容间距',
          val: 10,
          min: 0,
          max: 30,
        },
        //商品设置
        productList: {},
        goodsList: {
          max: 20,
          list: [],
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
          title: '内容圆角',
          name: 'contentStyle',
          val: 7,
          min: 0,
          max: 30,
        },
      },
      navlist: [],
      goodslist: [], //商品列表
      pageData: {},
      itemStyle: 0,
      bgStyle: 0,
      configObj: null,
      contentConfig: '', //内容间距
      checkThemeStyle: 0,
      priceThemeStyle: 0,
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
      setTimeout(() => {
        // 让表单重复渲染待编辑数据
        this.getConfig(data);
      }, 500);
    },
    getConfig(data) {
      this.configObj = { ...data };
      this.navlist = data.tabItemConfig.list; // 分类
      this.curIndex = data.tabItemConfig.tabVal; //分类选中的值
      this.checkThemeStyle = data.checkThemeStyleConfig.tabVal;
      this.priceThemeStyle = data.priceThemeStyleConfig.tabVal;
      if (data.tabItemConfig.list.length) {
        this.itemStyle = data.tabItemConfig.list[this.curIndex].activeList
          ? data.tabItemConfig.list[this.curIndex].activeList.styleType
          : 0;
        switch (data.tabConfig.tabVal) {
          case 0:
            this.goodslist = data.tabItemConfig.list[this.curIndex].activeList
              ? data.tabItemConfig.list[this.curIndex].activeList.goods
              : []; //指定商品列表
            break;
          case 1:
            this.goodslist = data.tabItemConfig.list[this.curIndex].activeList
              ? data.tabItemConfig.list[this.curIndex].selectConfigList
              : []; //分类商品列表展示
            break;
        }
        this.contentConfig = data.contentConfig.val || 0;
      }
    },
    // 列表
    arrayChunk(arr) {
      this.leftList = [];
      this.rightList = [];
      for (let i = 0; i < arr.length; i++) {
        if (i % 2 == 0) {
          this.leftList.push(arr[i]);
        } else {
          this.rightList.push(arr[i]);
        }
      }
    },
  },
};
</script>
<style scoped lang="scss">
.auto {
  width: auto !important;
}

.listC {
  display: grid !important;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: auto;
  width: 100%;
}

.menus {
  width: 100%;
  padding: 0 20px 0 15px;
  cursor: pointer;
  overflow: hidden;
  position: relative;
  height: 37px;
  display: flex;
  .category {
    width: 20px;
    color: #fff;
    position: absolute;
    right: 15px;
    top: 12px;

    span {
      font-size: 18px;
    }
  }

  .item {
    position: relative;
    float: left;
    margin-right: 15px;
    text-align: center;
    color: #fff;
    font-size: 14px;
    height: 38px;
    line-height: 36px;

    // width: 60px;
    &.on {
      font-size: 15px;

      span {
        display: block;
        position: absolute;
        left: 50%;
        bottom: 5px;
        width: 16px;
        height: 2px;
        transform: translateX(-50%);
        background: #fff;
      }
    }
  }
}

.sold {
  font-size: 11px;
  color: #666;
  margin-top: 3px;
}

.itemOn {
  border-radius: 0 !important;

  img,
  .empty-box {
    border-radius: 0 !important;
  }

  .img-box {
    .label {
      border-radius: 0 0 8px 0 !important;
    }
  }
}

.pageOn {
  border-radius: 8px !important;
}

.line2 {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.listBig {
  width: 100%;
  padding: 10px 0 10px 0;

  .itemBig {
    width: 100%;
    margin-bottom: 15px;
    border-radius: 10px;

    .img-box {
      width: 100%;
      height: 355px;
      position: relative;
      border-radius: 50px;

      img {
        width: 100%;
        height: 355px;
        border-radius: 10px;
      }

      .empty-box {
        border-radius: 10px;
      }

      .label {
        position: absolute;
        top: 0;
        left: 0;
        width: 59px;
        height: 25px;
        line-height: 25px;
        text-align: center;
        color: #fff;
        font-size: 12px;
        border-radius: 8px 0 8px 0;
      }
    }

    .name {
      font-size: 15px;
      font-weight: bold;
      margin-top: 8px;
      padding: 0 10px;
    }

    .coupon {
      width: 16px;
      height: 18px;
      line-height: 18px;
      text-align: center;
      font-size: 12px;
      margin-right: 5px;
      display: inline-block;
    }

    .price {
      font-weight: bold;
      font-size: 12px;

      .num {
        font-size: 18px;
        margin-right: 5px;
      }

      .old-price {
        color: #aaa !important;
        font-weight: normal;
        text-decoration: line-through;
      }
    }
  }
}

.paddingBox {
  padding-bottom: 0;
}

.home_product {
  padding: 0 10px;

  .hd_nav {
    display: flex;
    height: 65px;
    padding: 0 5px;

    .item {
      display: flex;
      flex-direction: column;
      justify-content: center;

      /*width: 25%;*/
      .title {
        font-size: 16px;
        color: #282828;
      }

      .label {
        width: 62px;
        height: 18px;
        line-height: 18px;
        text-align: center;
        background: transparent;
        border-radius: 8px;
        color: #999999;
        font-size: 12px;

        &.active {
          .title {
            color: #ff4444;
          }

          .label {
            color: #fff;
            background: linear-gradient(270deg, rgba(255, 84, 0, 1) 0%, rgba(255, 0, 0, 1) 100%);
          }
        }
      }
    }
  }

  .info {
    padding: 10px 5px;

    .title {
      font-size: 13px;
      color: #282828;
      line-height: 21px;
    }

    .text {
      display: flex;
      align-items: center;
      margin-top: 10px;
    }

    .label {
      padding: 0 2px;
      border-radius: 2px;
      color: #fff;
      font-size: 9px;
      text-align: center;
      line-height: 16px;
      height: 15px;
      margin-right: 4px;
    }

    .coupon,
    .ship {
      padding: 0 5px;
      line-height: 13px;
      border-radius: 2px;
      font-size: 10px;
      margin-left: 3px;
    }

    .ship {
      color: #ff9000;
      border: 1px solid #ff9000;
    }

    .old-price {
      color: #aaa;
      font-size: 13px;
      text-decoration: line-through;
      margin-left: 3px;
    }

    .price {
      display: flex;
      align-items: center;
      margin-top: 3px;

      .num {
        font-size: 14px;

        span {
          font-size: 14px;
        }
      }

      .label {
        width: 16px;
        height: 18px;
        margin-left: 5px;
        text-align: center;
        line-height: 18px;
        font-size: 11px;

        &.on {
          margin-left: 0;
        }
      }
    }
  }

  .list-wrapper {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;

    .item {
      /*width: 48.5%;*/
      &:last-child {
        margin-bottom: 0 !important;
      }

      border-radius: 10px;

      .img-box {
        position: relative;
        width: 100%;
        height: 173px;

        img,
        .box {
          width: 100%;
          height: 100%;
          border-radius: 10px;
        }

        .empty-box {
          background: #f3f9ff;
          border-radius: 10px;
        }

        .box {
          background: #d8d8d8;
        }
      }
    }

    &.itemA {
      .text {
        margin-top: 0 !important;
      }

      .item {
        display: flex;
        width: 100%;

        .img-box {
          position: relative;
          width: 110px;
          height: 110px;

          img,
          .box,
          .empty-box {
            border-radius: 10px;
          }
        }

        .info {
          display: flex;
          justify-content: space-between;
          flex-direction: column;
          flex: 1;
          margin-left: 5px;
          padding: 5px 10px;

          .num {
            font-weight: bold;
          }
        }
      }
    }

    &.itemB {
      justify-content: inherit;

      .item {
        overflow: hidden;

        &:nth-child(3n) {
          margin-right: 0;
        }

        .img-box {
          position: relative;
          width: 100%;
          height: 110px;

          img,
          .box,
          .empty-box {
            border-radius: 10px;
          }
        }

        .price {
          display: block;
        }
      }
    }

    &.itemC {
      display: grid !important;
      grid-template-columns: repeat(2, 1fr);
      grid-template-rows: auto;
      width: 100%;

      .item {
        background-color: #fff;
        overflow: hidden;

        img,
        .box,
        .empty-box {
          border-radius: 10px;
        }

        .price {
          margin-top: 3px;
        }
      }
    }
  }
}
.shrink {
  flex-shrink: 0;
}
</style>
