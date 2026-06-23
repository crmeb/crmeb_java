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
        :num="item.num"
      ></component>
    </div>
    <rightBtn :activeIndex="activeIndex" :configObj="configObj"></rightBtn>
  </div>
</template>

<script>
import toolCom from '@/views/design/theme_editor/components/mobileConfigRight/index.js';
import { mapState, mapMutations, mapActions } from 'vuex';
import rightBtn from '@/views/design/theme_editor/components/rightBtn/index.vue';
import { getProProduct } from '@/api/theme';
export default {
  name: 'c_home_product',
  componentsName: 'home_product',
  cname: '商品选项卡',
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
          configNme: 'titleLeft',
        },
        {
          components: toolCom.c_radio,
          configNme: 'slideConfig',
        },
        {
          components: toolCom.c_title,
          configNme: 'titleTab',
        },
        {
          components: toolCom.c_promotion,
          configNme: 'tabConfig',
        },
        {
          components: toolCom.c_title,
          configNme: 'titleCart',
        },
        {
          components: toolCom.c_radio,
          configNme: 'cartConfig',
        },
      ],
      twoContent: [
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
          components: toolCom.c_radio,
          configNme: 'toneConfig',
        },
      ],
      twoStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'decorateColor',
        },
      ],
      twoStyle2: [
        {
          components: toolCom.c_bg_color,
          configNme: 'decorateColor2',
        },
      ],
      threeStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'textColor2',
        },
      ],
      threeStyle2: [
        {
          components: toolCom.c_bg_color,
          configNme: 'textColor',
        },
      ],
      threeStyle3: [
        {
          components: toolCom.c_bg_color,
          configNme: 'textColor3',
        },
      ],
      fourStyle: [
        {
          components: toolCom.c_title,
          configNme: 'titleCart',
        },
        {
          components: toolCom.c_radio,
          configNme: 'toneCartConfig',
        },
      ],
      fourStyle2: [
        {
          components: toolCom.c_bg_color,
          configNme: 'goodsPriceColor',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'bntBgColor',
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
    };
  },
  watch: {
    num(nVal) {
      // debugger;
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[nVal]));
      this.configObj = this.patchConfig(value);
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
          this.getRComContent(arr, this.type3);
        } else {
          this.getRComStyle(arr, this.type, this.type2, this.type3, this.type4);
        }
      },
      deep: true,
    },
    'configObj.styleConfig.tabVal': {
      handler(nVal, oVal) {
        this.type = nVal;
        var arr = [
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
          this.getRComStyle(arr, nVal, this.type2, this.type3, this.type4);
        }
      },
    },
    'configObj.cartConfig.tabVal': {
      handler(nVal, oVal) {
        this.type3 = nVal;
        var arr = [
          {
            components: toolCom.c_card_select,
            configNme: 'styleConfig',
          },
          {
            components: toolCom.c_set_up,
            configNme: 'setUp',
          },
        ];
        if (this.setUp == 0) {
          this.getRComContent(arr, nVal);
        } else {
          this.getRComStyle(arr, this.type, this.type2, nVal, this.type4);
        }
      },
    },
    'configObj.toneConfig.tabVal': {
      handler(nVal, oVal) {
        this.type2 = nVal;
        var arr = [
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
          this.getRComStyle(arr, this.type, nVal, this.type3, this.type4);
        }
      },
    },
    'configObj.toneCartConfig.tabVal': {
      handler(nVal, oVal) {
        this.type4 = nVal;
        var arr = [
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
          this.getRComStyle(arr, this.type, this.type2, this.type3, nVal);
        }
      },
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
        data.marginConfig = {
          title: '外边距',
          val: 0,
          min: 0,
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        };
        if (data.mbConfig) data.marginConfig.valList[0].val = data.mbConfig.val;
      }
      return data;
    },
    getRComContent(arr, type3) {
      if (type3 == 0) {
        this.rCom = [...arr, ...this.oneContent, ...this.twoContent];
      } else {
        this.rCom = [...arr, ...this.oneContent];
      }
    },
    getRComStyle(arr, type, type2, type3, type4) {
      let obj = [...arr, ...this.oneStyle, ...this.currencyStyle];
      let obj2 = [...arr, ...this.oneStyle, ...this.fourStyle, ...this.currencyStyle];
      if (type == 0) {
        if (type2 == 0) {
          if (type3 == 0) {
            if (type4 == 0) {
              this.rCom = obj2;
            } else {
              this.rCom = [...arr, ...this.oneStyle, ...this.fourStyle, ...this.fourStyle2, ...this.currencyStyle];
            }
          } else {
            this.rCom = obj;
          }
        } else {
          if (type3 == 0) {
            if (type4 == 0) {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle,
                ...this.threeStyle,
                ...this.fourStyle,
                ...this.currencyStyle,
              ];
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle,
                ...this.threeStyle,
                ...this.fourStyle,
                ...this.fourStyle2,
                ...this.currencyStyle,
              ];
            }
          } else {
            this.rCom = [
              ...arr,
              ...this.oneStyle,
              ...this.twoStyle,
              ...this.threeStyle,
              ...this.fourStyle,
              ...this.currencyStyle,
            ];
          }
        }
      } else if (type == 1) {
        if (type2 == 0) {
          if (type3 == 0) {
            if (type4 == 0) {
              this.rCom = obj2;
            } else {
              this.rCom = [...arr, ...this.oneStyle, ...this.fourStyle, ...this.fourStyle2, ...this.currencyStyle];
            }
          } else {
            this.rCom = obj;
          }
        } else {
          if (type3 == 0) {
            if (type4 == 0) {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle,
                ...this.threeStyle2,
                ...this.fourStyle,
                ...this.currencyStyle,
              ];
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle,
                ...this.threeStyle2,
                ...this.fourStyle,
                ...this.fourStyle2,
                ...this.currencyStyle,
              ];
            }
          } else {
            this.rCom = [
              ...arr,
              ...this.oneStyle,
              ...this.twoStyle,
              ...this.threeStyle2,
              ...this.fourStyle,
              ...this.currencyStyle,
            ];
          }
        }
      } else if (type == 2) {
        if (type2 == 0) {
          if (type3 == 0) {
            if (type4 == 0) {
              this.rCom = obj2;
            } else {
              this.rCom = [...arr, ...this.oneStyle, ...this.fourStyle, ...this.fourStyle2, ...this.currencyStyle];
            }
          } else {
            this.rCom = obj;
          }
        } else {
          if (type3 == 0) {
            if (type4 == 0) {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle2,
                ...this.threeStyle,
                ...this.fourStyle,
                ...this.currencyStyle,
              ];
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle2,
                ...this.threeStyle,
                ...this.fourStyle,
                ...this.fourStyle2,
                ...this.currencyStyle,
              ];
            }
          } else {
            this.rCom = [
              ...arr,
              ...this.oneStyle,
              ...this.twoStyle2,
              ...this.threeStyle,
              ...this.fourStyle,
              ...this.currencyStyle,
            ];
          }
        }
      } else {
        if (type2 == 0) {
          if (type3 == 0) {
            if (type4 == 0) {
              this.rCom = obj2;
            } else {
              this.rCom = [...arr, ...this.oneStyle, ...this.fourStyle, ...this.fourStyle2, ...this.currencyStyle];
            }
          } else {
            this.rCom = obj;
          }
        } else {
          if (type3 == 0) {
            if (type4 == 0) {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle,
                ...this.threeStyle3,
                ...this.fourStyle,
                ...this.currencyStyle,
              ];
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle,
                ...this.threeStyle3,
                ...this.fourStyle,
                ...this.fourStyle2,
                ...this.currencyStyle,
              ];
            }
          } else {
            this.rCom = [
              ...arr,
              ...this.oneStyle,
              ...this.twoStyle,
              ...this.threeStyle3,
              ...this.fourStyle,
              ...this.currencyStyle,
            ];
          }
        }
      }
    },
    getConfig(data) {
      let configObj = this.configObj.tabConfig.list[this.configObj.tabConfig.tabCur];
      let activeValue = configObj.selectConfig.activeValue;
      if (!data.name) {
        return;
      }
      if (data.name == 'selectType' && data.values == 1) {
        return;
      }
      let type = configObj.tabVal;
      let dataObj = {
        page: 1,
        limit: configObj.numConfig.val,
        priceOrder: configObj.goodsSort == 2 ? 'desc' : '',
        salesOrder: configObj.goodsSort == 1 ? 'desc' : '',
      };
      if (type == 1) {
        this.configObj.productList.list = [];
        return;
      } else if (type == 3) {
        // this.configObj.productList.list = [];
        dataObj.cate_id = activeValue;
      } else {
        dataObj.store_label_id = configObj.goodsLabel.activeValue;
      }
      getProProduct(dataObj).then((res) => {
        configObj.productList.list = res.data;
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
