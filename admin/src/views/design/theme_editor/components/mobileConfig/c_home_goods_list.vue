<template>
  <div class="mobile-config">
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
import { getCategory, getProProduct } from '@/api/theme';
import toolCom from '@/views/design/theme_editor/components/mobileConfigRight/index.js';
import { mapState, mapMutations, mapActions } from 'vuex';
import rightBtn from '@/views/design/theme_editor/components/rightBtn/index.vue';
export default {
  name: 'c_home_goods_list',
  componentsName: 'home_goods_list',
  cname: '产品列表',
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
  data() {
    return {
      configObj: {},
      rCom: [
        // {
        //   components: toolCom.c_title,
        //   configNme: 'titleLeft',
        // },
        {
          components: toolCom.c_card_select,
          configNme: 'styleConfig',
        },
        {
          components: toolCom.c_set_up,
          configNme: 'setUp',
        },
      ],
      oneContent: [
        {
          components: toolCom.c_title,
          configNme: 'titleGoods',
        },
        {
          components: toolCom.c_select,
          configNme: 'typeConfig',
        },
      ],
      oneContent1: [
        {
          components: toolCom.c_goods,
          configNme: 'goodsList',
        },
      ],
      oneContent2: [
        {
          components: toolCom.c_brand,
          configNme: 'brandList',
        },
        {
          components: toolCom.c_slider,
          configNme: 'numberConfig',
        },
        {
          components: toolCom.c_radio,
          configNme: 'goodsSort',
        },
      ],
      oneContent3: [
        {
          components: toolCom.c_classify,
          configNme: 'classList',
        },
        {
          components: toolCom.c_slider,
          configNme: 'numberConfig',
        },
        {
          components: toolCom.c_radio,
          configNme: 'goodsSort',
        },
      ],
      oneContent4: [
        {
          components: toolCom.c_goods_label,
          configNme: 'goodsLabel',
        },
        {
          components: toolCom.c_slider,
          configNme: 'numberConfig',
        },
        {
          components: toolCom.c_radio,
          configNme: 'goodsSort',
        },
      ],
      twoContent: [
        {
          components: toolCom.c_title,
          configNme: 'titleContents',
        },
        {
          components: toolCom.c_checkbox,
          configNme: 'checkboxInfo',
        },
      ],
      threeContent: [
        {
          components: toolCom.c_title,
          configNme: 'titleCart',
        },
        {
          components: toolCom.c_radio,
          configNme: 'cartConfig',
        },
      ],
      threeContent1: [
        {
          components: toolCom.c_button_img,
          configNme: 'bntStyleConfig',
        },
        {
          components: toolCom.c_radio,
          configNme: 'bntConfig',
        },
      ],
      oneStyle: [
        {
          components: toolCom.c_title,
          configNme: 'titleRight',
        },
        {
          components: toolCom.c_fillet,
          configNme: 'filletImg',
        },
        {
          components: toolCom.c_radio,
          configNme: 'goodsName',
        },
        {
          components: toolCom.c_radio,
          configNme: 'toneConfig',
        },
      ],
      oneStyle1: [
        {
          components: toolCom.c_bg_color,
          configNme: 'goodsNameColor',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'goodsPriceColor',
        },
      ],
      oneStyle2: [
        {
          components: toolCom.c_bg_color,
          configNme: 'soldNumColor',
        },
      ],
      oneStyle3: [
        {
          components: toolCom.c_bg_color,
          configNme: 'scoreColor',
        },
      ],
      twoStyle: [
        {
          components: toolCom.c_title,
          configNme: 'titleCart',
        },
        {
          components: toolCom.c_radio,
          configNme: 'toneCartConfig',
        },
      ],
      twoStyle1: [
        {
          components: toolCom.c_bg_color,
          configNme: 'bntBgColor',
        },
      ],
      currencyTitleStyle: [
        {
          components: toolCom.c_title,
          configNme: 'titleCurrency',
        },
      ],
      currencyStyle: [
        {
          components: toolCom.c_common_style,
          configNme: 'commonStyle',
        },
      ],
      setUp: 0,
      type: 0,
      type2: 0,
      type3: 0,
      type4: 0,
      type5: 0,
      lockStatus: false,
    };
  },
  watch: {
    num(nVal) {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[nVal]));
      this.configObj = this.patchConfig(value);
      if (!value.selectConfig.list || !value.selectConfig.list[0].value) {
        this.getCategory();
      }
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
        var arr = [
          // {
          //   components: toolCom.c_title,
          //   configNme: 'titleLeft',
          // },
          {
            components: toolCom.c_card_select,
            configNme: 'styleConfig',
          },
          {
            components: toolCom.c_set_up,
            configNme: 'setUp',
          },
        ];
        if (nVal == 0) {
          this.getRComContent(arr, this.type, this.type2, this.type3);
        } else {
          this.getRComStyle(arr, this.type, this.type3, this.type4, this.type5);
        }
      },
      deep: true,
    },
    'configObj.styleConfig.tabVal': {
      handler(nVal, oVal) {
        this.type = nVal;
        var arr = [
          // {
          //   components: toolCom.c_title,
          //   configNme: 'titleLeft',
          // },
          {
            components: toolCom.c_card_select,
            configNme: 'styleConfig',
          },
          {
            components: toolCom.c_set_up,
            configNme: 'setUp',
          },
        ];
        if (this.setUp === 0) {
          this.getRComContent(arr, nVal, this.type2, this.type3);
        } else {
          this.getRComStyle(arr, nVal, this.type3, this.type4, this.type5);
        }
      },
      deep: true,
    },
    'configObj.typeConfig.activeValue': {
      handler(nVal, oVal) {
        this.type2 = nVal;
        var arr = [
          // {
          //   components: toolCom.c_title,
          //   configNme: 'titleLeft',
          // },
          {
            components: toolCom.c_card_select,
            configNme: 'styleConfig',
          },
          {
            components: toolCom.c_set_up,
            configNme: 'setUp',
          },
        ];
        if (this.setUp === 0) {
          this.getRComContent(arr, this.type, nVal, this.type3);
        }
      },
      deep: true,
    },
    'configObj.cartConfig.tabVal': {
      handler(nVal, oVal) {
        this.type3 = nVal;
        var arr = [
          // {
          //   components: toolCom.c_title,
          //   configNme: 'titleLeft',
          // },
          {
            components: toolCom.c_card_select,
            configNme: 'styleConfig',
          },
          {
            components: toolCom.c_set_up,
            configNme: 'setUp',
          },
        ];
        if (this.setUp === 0) {
          this.getRComContent(arr, this.type, this.type2, nVal);
        } else {
          this.getRComStyle(arr, this.type, nVal, this.type4, this.type5);
        }
      },
      deep: true,
    },
    'configObj.toneConfig.tabVal': {
      handler(nVal, oVal) {
        this.type4 = nVal;
        var arr = [
          // {
          //   components: toolCom.c_title,
          //   configNme: 'titleLeft',
          // },
          {
            components: toolCom.c_card_select,
            configNme: 'styleConfig',
          },
          {
            components: toolCom.c_set_up,
            configNme: 'setUp',
          },
        ];
        if (this.setUp) {
          this.getRComStyle(arr, this.type, this.type3, nVal, this.type5);
        }
      },
      deep: true,
    },
    'configObj.toneCartConfig.tabVal': {
      handler(nVal, oVal) {
        this.type5 = nVal;
        var arr = [
          {
            components: toolCom.c_title,
            configNme: 'titleLeft',
          },
          {
            components: toolCom.c_card_select,
            configNme: 'styleConfig',
          },
          {
            components: toolCom.c_set_up,
            configNme: 'setUp',
          },
        ];
        if (this.setUp) {
          this.getRComStyle(arr, this.type, this.type3, this.type4, nVal);
        }
      },
      deep: true,
    },
  },
  mounted() {
    this.$nextTick(() => {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[this.num]));
      this.configObj = this.patchConfig(value);
    });
  },
  methods: {
    patchConfig(data) {
      if (!data) return data;
      if (!data.paddingConfig) {
        data.paddingConfig = {
          title: '内边距',
          val: 0,
          min: 0,
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        };
        if (data.topConfig) data.paddingConfig.valList[0].val = data.topConfig.val;
        if (data.prConfig) {
          data.paddingConfig.valList[1].val = data.prConfig.val;
          data.paddingConfig.valList[3].val = data.prConfig.val;
        }
        if (data.bottomConfig) data.paddingConfig.valList[2].val = data.bottomConfig.val;
      }
      if (!data.marginConfig) {
        this.$set(data, 'marginConfig', {
          title: '外边距',
          val: 0,
          min: 0,
          max: 100,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        });
        if (data.mbConfig) data.marginConfig.valList[0].val = data.mbConfig.val;
      }
      return data;
    },
    getCategory() {
      getCategory({ status: -1, type: 1 }).then((res) => {
        this.configObj.selectConfig.list = res.data;
      });
    },
    getRComContent(arr, type, type2, type3) {
      if (type == 3) {
        if (type2 == 1) {
          this.rCom = [...arr, ...this.oneContent, ...this.oneContent1, ...this.twoContent];
        } else if (type2 == 2) {
          this.rCom = [...arr, ...this.oneContent, ...this.oneContent2, ...this.twoContent];
        } else if (type2 == 3) {
          this.rCom = [...arr, ...this.oneContent, ...this.oneContent3, ...this.twoContent];
        } else {
          this.rCom = [...arr, ...this.oneContent, ...this.oneContent4, ...this.twoContent];
        }
      } else {
        if (type2 == 1) {
          if (type3 == 0) {
            this.rCom = [
              ...arr,
              ...this.oneContent,
              ...this.oneContent1,
              ...this.twoContent,
              ...this.threeContent,
              ...this.threeContent1,
            ];
          } else {
            this.rCom = [...arr, ...this.oneContent, ...this.oneContent1, ...this.twoContent, ...this.threeContent];
          }
        } else if (type2 == 2) {
          if (type3 == 0) {
            this.rCom = [
              ...arr,
              ...this.oneContent,
              ...this.oneContent2,
              ...this.twoContent,
              ...this.threeContent,
              ...this.threeContent1,
            ];
          } else {
            this.rCom = [...arr, ...this.oneContent, ...this.oneContent2, ...this.twoContent, ...this.threeContent];
          }
        } else if (type2 == 3) {
          if (type3 == 0) {
            this.rCom = [
              ...arr,
              ...this.oneContent,
              ...this.oneContent3,
              ...this.twoContent,
              ...this.threeContent,
              ...this.threeContent1,
            ];
          } else {
            this.rCom = [...arr, ...this.oneContent, ...this.oneContent3, ...this.twoContent, ...this.threeContent];
          }
        } else {
          if (type3 == 0) {
            this.rCom = [
              ...arr,
              ...this.oneContent,
              ...this.oneContent4,
              ...this.twoContent,
              ...this.threeContent,
              ...this.threeContent1,
            ];
          } else {
            this.rCom = [...arr, ...this.oneContent, ...this.oneContent4, ...this.twoContent, ...this.threeContent];
          }
        }
      }
    },
    getRComStyle(arr, type, type3, type4, type5) {
      let obj4 = [],
        currencyStyle = [];
      if (type4) {
        if (type == 1 || type == 4) {
          obj4 = [...this.oneStyle1, ...this.oneStyle2];
          currencyStyle = [...this.currencyStyle];
        } else if (type == 0) {
          obj4 = [...this.oneStyle1, ...this.oneStyle2, ...this.oneStyle3];
          currencyStyle = [...this.currencyStyle];
        } else if (type == 2 || type == 3) {
          obj4 = [...this.oneStyle1];
          currencyStyle = [...this.currencyStyle];
        } else {
          obj4 = [...this.oneStyle1, ...this.oneStyle2];
          currencyStyle = [...this.currencyStyle];
        }
      } else {
        if (type == 0 || type == 1 || type == 4) {
          currencyStyle = [...this.currencyStyle];
        } else {
          currencyStyle = [...this.currencyStyle];
        }
      }
      let obj5 = [];
      if (type != 3) {
        if (type5) {
          obj5 = [...this.twoStyle, ...this.twoStyle1];
        } else {
          obj5 = [...this.twoStyle];
        }
      }
      if (type3 == 0) {
        this.rCom = [...arr, ...this.oneStyle, ...obj4, ...obj5, ...currencyStyle];
      } else {
        this.rCom = [...arr, ...this.oneStyle, ...obj4, ...currencyStyle];
      }
    },
    getConfig(data, name) {
      if (name != 'radio' && !data.name && data == 1) {
        this.configObj.goodsList.list = [];
        return;
      }
      if (name != 'radio' && !data.name && data == 0 && !this.configObj.classList.classVal.length) {
        this.configObj.goodsList.list = [];
        return;
      }
      // if( data.name=='radio'){
      //     return;
      // }
      let type = this.configObj.typeConfig.activeValue;
      let dataObj = {
        page: 1,
        limit: this.configObj.numberConfig.val,
        priceOrder: this.configObj.goodsSort.tabVal == 2 ? 'desc' : '',
        salesOrder: this.configObj.goodsSort.tabVal == 1 ? 'desc' : '',
      };
      if (type == 1) {
        this.configObj.productList.list = [];
        return;
      } else if (type == 3) {
        dataObj.cate_id = this.configObj.classList.classVal;
      } else {
        dataObj.store_label_id = this.configObj.goodsLabel.activeValue;
      }
      getProProduct(dataObj).then((res) => {
        this.configObj.productList.list = res.data;
      });
    },
    getCategory() {
      getCategory().then((res) => {
        this.$set(this.configObj.selectConfig, 'list', res.data);
      });
    },
  },
};
</script>

<style scoped lang="scss">
.pro {
  padding: 15px 15px 0;
  .tips {
    height: 50px;
    line-height: 50px;
    color: #999;
    font-size: 12px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  }
}
.btn-box {
  padding-bottom: 20px;
}
</style>
