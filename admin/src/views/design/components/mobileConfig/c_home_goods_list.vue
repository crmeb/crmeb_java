<template>
  <div class="mobile-config pro">
    <div v-for="(item, key) in rCom" :key="key">
      <component
        :is="item.components.name"
        :configObj="configObj"
        ref="childData"
        :configNme="item.configNme"
        :key="key"
        @getConfig="getConfig"
        :index="activeIndex"
        :number="num"
        :num="item.num"
      ></component>
    </div>
    <rightBtn :activeIndex="activeIndex" :configObj="configObj"></rightBtn>
  </div>
</template>
<script>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import { productLstApi } from '@/api/store';
import toolCom from '../mobileConfigRight/index.js';
import { mapGetters } from 'vuex';
import rightBtn from '../rightBtn/index.vue';
export default {
  name: 'c_home_goods_list',
  componentsName: 'home_goods_list',
  cname: '商品列表',
  props: {
    activeIndex: {
      type: null,
    },
    num: {
      type: null,
    },
    index: {
      type: null,
    },
  },
  components: {
    ...toolCom,
    rightBtn,
  },
  computed: {
    ...mapGetters(['adminProductClassify']),
    //商品排序
    goodsSort() {
      return this.configObj.goodsSort.tabVal;
    },
    //选择方式
    tabConfig() {
      return this.configObj.tabConfig.tabVal;
    },
  },
  data() {
    return {
      configObj: {},
      rCom: [
        {
          components: toolCom.c_checked_tab,
          configNme: 'setUp',
        },
      ],
      automatic: [
        {
          components: toolCom.c_title,
          configNme: 'itemStyle',
        },
        {
          components: toolCom.c_txt_tab,
          configNme: 'itemStyle',
        },
        {
          components: toolCom.c_title,
          configNme: 'typeConfig',
        },
        {
          components: toolCom.c_checkbox_group,
          configNme: 'typeConfig',
        },
        {
          components: toolCom.c_title,
          configNme: 'tabConfig',
        },
        {
          components: toolCom.c_tab_radio,
          configNme: 'tabConfig',
        },
        {
          components: toolCom.c_goods,
          configNme: 'goodsList',
        },
        {
          components: toolCom.c_cascader,
          configNme: 'selectConfig',
        },
        {
          components: toolCom.c_input_number,
          configNme: 'numConfig',
        },
        {
          components: toolCom.c_txt_tab,
          configNme: 'goodsSort',
        },
      ],
      setUp: 0,
      type: 0,
      lockStatus: false,
    };
  },
  watch: {
    num(nVal) {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[nVal]));
      this.configObj = value;
    },
    configObj: {
      handler(nVal, oVal) {
        this.$store.commit('mobildConfig/UPDATEARR', { num: this.num, val: nVal });
      },
      deep: true,
    },
    'configObj.setUp.tabVal': {
      handler(nVal, oVal) {
        this.setUp = nVal;
        var arr = [this.rCom[0]];
        if (nVal == 0) {
          this.rCom = arr.concat(this.automatic);
        } else {
          let tempArr = [
            {
              components: toolCom.c_title,
              configNme: 'bgColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'bgColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'titleColor',
            },
            {
              components: toolCom.c_radio,
              configNme: 'themeStyleConfig',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'priceColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'soldColor',
            },
            {
              components: toolCom.c_title,
              configNme: 'upConfig',
            },
            {
              components: toolCom.c_slider,
              configNme: 'upConfig',
            },
            {
              components: toolCom.c_slider,
              configNme: 'downConfig',
            },
            {
              components: toolCom.c_slider,
              configNme: 'lrConfig',
            },
            {
              components: toolCom.c_slider,
              configNme: 'contentConfig',
            },
            {
              components: toolCom.c_slider,
              configNme: 'mbConfig',
            },
            {
              components: toolCom.c_title,
              configNme: 'bgStyle',
            },
            {
              components: toolCom.c_slider,
              configNme: 'bgStyle',
            },
            {
              components: toolCom.c_slider,
              configNme: 'contentStyle',
            },
          ];
          this.rCom = arr.concat(tempArr);
        }
      },
      deep: true,
    },
    //选择方式选择，0指定商品，1指定品牌，2指定分类，3指定商户
    'configObj.tabConfig.tabVal': {
      handler(nVal, oVal) {
        this.type = nVal;
        switch (this.type) {
          case 0:
            this.$set(this.configObj.selectConfig, 'list', this.adminProductClassify);
            this.$set(this.configObj.numConfig, 'isShow', 0); //是否显示inputnumber框
            this.$set(this.configObj.goodsList, 'isShow', 1); //选择商品
            this.$set(this.configObj.selectConfig, 'isShow', 0); //选择分类
            this.$set(this.configObj.goodsSort, 'isShow', 0); //商品排序
            break;
          case 1:
            this.$set(this.configObj.selectConfig, 'list', this.adminProductClassify);
            this.$set(this.configObj.numConfig, 'isShow', 1);
            this.$set(this.configObj.goodsList, 'isShow', 0); //选择商品
            this.$set(this.configObj.selectConfig, 'isShow', 1); //选择分类
            this.$set(this.configObj.goodsSort, 'isShow', 1); //商品排序
            break;
        }
      },
      deep: true,
    },
  },
  mounted() {
    this.$nextTick(() => {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[this.num]));
      this.configObj = value;
      if (!localStorage.getItem('adminProductClassify')) this.$store.dispatch('product/getAdminProductClassify');
      this.$nextTick(() => {
        this.$set(this.configObj.selectConfig, 'list', this.adminProductClassify);
      });
    });
  },
  methods: {
    getConfig(data) {
      //选择tab获取商品列表.0指定商品
      if (data.name && data.name === 'goods') this.configObj.goodsList.list = data.values;
      //选择tab获取商品列表,1指定分类
      this.getProList(data);
    },
    //选择tab获取商品列表
    getProList(data) {
      if (
        data.name &&
        (data.name === 'merchantName' ||
          data.name === 'select' ||
          data.name === 'cascader' ||
          data.name === 'radio' ||
          data.name === 'number')
      ) {
        let activeValue = '';
        let priceOrder = '';
        let salesOrder = '';
        switch (this.tabConfig) {
          case 0:
            break;
          case 1:
            activeValue = this.configObj.selectConfig.activeValue.join(',');
            break;
        }
        if (this.goodsSort === 0) {
          priceOrder = '';
          salesOrder = '';
        } else if (this.goodsSort === 1) {
          priceOrder = '';
          salesOrder = 'desc';
        } else {
          priceOrder = 'desc';
          salesOrder = '';
        }
        productLstApi({
          cateId: this.tabConfig === 1 ? activeValue : null,
          page: 1,
          limit: this.configObj.numConfig.val,
          type: '1',
          priceOrder: priceOrder,
          salesOrder: salesOrder,
        }).then((res) => {
          //选择tab获取商品列表,1指定分类
          switch (this.configObj.tabConfig.tabVal) {
            case 1:
              this.configObj.selectConfig.goodsList = res.list;
              break;
          }
        });
      }
    },
  },
};
</script>

<style scoped lang="scss">
.pro {
  //padding: 15px 15px 0;
  .tips {
    height: 50px;
    line-height: 50px;
    color: #999;
    font-size: 12px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  }

  .btn-box {
    padding-bottom: 20px;
  }
}
</style>
