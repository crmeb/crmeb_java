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
import rightBtn from '@/views/design/theme_editor/components/rightBtn/index.vue';
import { mapMutations } from 'vuex';
export default {
  name: 'c_points_mall',
  componentsName: 'points_mall',
  cname: '积分商城',
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
      // 组件参数配置
      option: {
        submitBtn: false,
      },
      configObj: {}, // 配置对象
      rCom: [
        {
          components: toolCom.c_card_select,
          configNme: 'goodStyleConfig',
        },
        {
          components: toolCom.c_set_up,
          configNme: 'setUp',
        },
      ], // 当前页面组件
      oneContent: [
        {
          components: toolCom.c_title,
          configNme: 'titleLeft',
        },
        {
          components: toolCom.c_radio,
          configNme: 'styleConfig',
        },
      ],
      oneContentImg: [
        {
          components: toolCom.c_upload_img,
          configNme: 'imgBgConfig',
        },
      ],
      twoContent: [
        {
          components: toolCom.c_radio,
          configNme: 'titleConfig',
        },
      ],
      twoContentImg: [
        {
          components: toolCom.c_upload_img,
          configNme: 'imgConfig',
        },
      ],
      twoContentImg2: [
        {
          components: toolCom.c_upload_img,
          configNme: 'imgConfig2',
        },
      ],
      twoContentText: [
        {
          components: toolCom.c_input_item,
          configNme: 'titleTxtConfig',
        },
      ],
      threeContent: [
        {
          components: toolCom.c_input_item,
          configNme: 'rightBntConfig',
        },
        {
          components: toolCom.c_title,
          configNme: 'titleGoods',
        },
        {
          components: toolCom.c_slider,
          configNme: 'numberConfig',
        },
      ],
      oneStyle: [
        {
          components: toolCom.c_title,
          configNme: 'titleRight',
        },
      ],
      twoStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'headerBgColor',
        },
      ],
      threeStyle: [
        {
          components: toolCom.c_radio,
          configNme: 'titleText',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'titleColor',
        },
        {
          components: toolCom.c_slider,
          configNme: 'titleNumber',
        },
      ],
      fourStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'headerBntColor',
        },
      ],
      fourStyle2: [
        {
          components: toolCom.c_bg_color,
          configNme: 'headerBntColor2',
        },
      ],
      fiveStyle: [
        {
          components: toolCom.c_slider,
          configNme: 'bntNumber',
        },
        {
          components: toolCom.c_title,
          configNme: 'titleGoodsStyle',
        },
        {
          components: toolCom.c_fillet,
          configNme: 'filletImg',
        },
      ],
      goodsNameStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'goodsNameColor',
        },
      ],
      goodsNameStyle2: [
        {
          components: toolCom.c_bg_color,
          configNme: 'goodsNameColor2',
        },
      ],
      toneStyle: [
        {
          components: toolCom.c_radio,
          configNme: 'toneConfig',
        },
      ],
      goodsPriceStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'goodsPriceColor',
        },
      ],
      goodsPriceStyle2: [
        {
          components: toolCom.c_bg_color,
          configNme: 'goodsPriceColor2',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'goodsUnitPriceColor2',
        },
      ],
      goodsUnitPriceStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'goodsUnitPriceColor',
        },
      ],
      priceBgStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'priceBgColor',
        },
      ],
      titleStyle: [
        {
          components: toolCom.c_title,
          configNme: 'titleCurrency',
        },
      ],
      moduleStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'moduleColor',
        },
      ],
      moduleStyle2: [
        {
          components: toolCom.c_bg_color,
          configNme: 'moduleColor2',
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
        var arr = [
          {
            components: toolCom.c_card_select,
            configNme: 'goodStyleConfig',
          },
          {
            components: toolCom.c_set_up,
            configNme: 'setUp',
          },
        ];
        if (nVal == 0) {
          this.getRComContent(arr, this.type, this.type2, this.type3);
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
            configNme: 'goodStyleConfig',
          },
          {
            components: toolCom.c_set_up,
            configNme: 'setUp',
          },
        ];
        if (this.setUp == 0) {
          this.getRComContent(arr, nVal, this.type2, this.type3);
        } else {
          this.getRComStyle(arr, nVal, this.type2, this.type3, this.type4);
        }
      },
      deep: true,
    },
    'configObj.titleConfig.tabVal': {
      handler(nVal, oVal) {
        this.type2 = nVal;
        var arr = [
          {
            components: toolCom.c_card_select,
            configNme: 'goodStyleConfig',
          },
          {
            components: toolCom.c_set_up,
            configNme: 'setUp',
          },
        ];
        if (this.setUp == 0) {
          this.getRComContent(arr, this.type, nVal, this.type3);
        } else {
          this.getRComStyle(arr, this.type, nVal, this.type3, this.type4);
        }
      },
      deep: true,
    },
    'configObj.goodStyleConfig.tabVal': {
      handler(nVal, oVal) {
        this.type3 = nVal;
        var arr = [
          {
            components: toolCom.c_card_select,
            configNme: 'goodStyleConfig',
          },
          {
            components: toolCom.c_set_up,
            configNme: 'setUp',
          },
        ];
        if (this.setUp == 0) {
          this.getRComContent(arr, this.type, this.type2, nVal);
        } else {
          this.getRComStyle(arr, this.type, this.type2, nVal, this.type4);
        }
      },
      deep: true,
    },
    'configObj.toneConfig.tabVal': {
      handler(nVal, oVal) {
        this.type4 = nVal;
        var arr = [
          {
            components: toolCom.c_card_select,
            configNme: 'goodStyleConfig',
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
        this.$set(data, 'paddingConfig', {
          title: '内边距',
          val: 0,
          min: 0,
          max: 100,
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        });
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
          isAll: false,
          val: 0,
          min: 0,
          max: 100,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        });
        if (data.mbConfig) data.marginConfig.valList[0].val = data.mbConfig.val;
      }
      if (!data.componentBgConfig) {
        this.$set(data, 'componentBgConfig', {
          title: '背景设置',
          tabVal: 0,
          tabList: [{ name: '颜色' }, { name: '图片' }],
          colorConfig: {
            title: '背景颜色',
            default: [
              {
                item: '#F5F5F5',
              },
              {
                item: '#F5F5F5',
              },
            ],
            color: [
              {
                item: '#F5F5F5',
              },
              {
                item: '#F5F5F5',
              },
            ],
          },
          imgBgConfig: {
            info: '建议：750px * 100 ~ 200px',
            url: '',
            type: 'code',
            delType: 0,
            name: '背景图片',
          },
        });
      }
      if (!data.zIndexConfig) {
        this.$set(data, 'zIndexConfig', {
          title: '组件上浮',
          val: 0,
          min: 0,
        });
      }
      if (!data.borderConfig) {
        this.$set(data, 'borderConfig', {
          title: '组件边框',
          show: false,
          color: {
            item: '#E5E5E5',
          },
          scale: {
            val: 1,
            min: 1,
          },
        });
      }
      if (!data.shadowConfig) {
        this.$set(data, 'shadowConfig', {
          title: '组件阴影',
          show: false,
          color: {
            item: '#E5E5E5',
          },
          scale: {
            val: 1,
            min: 1,
          },
        });
      }
      if (!data.c_common_style) {
        data.c_common_style = {
          color: 'rgba(255,255,255,1)',
          color2: 'rgba(255,255,255,1)',
          lr: 0,
          type: 0,
        };
      }
      return data;
    },
    getRComContent(arr, type, type2, type3) {
      if (type == 0) {
        if (type2 == 0) {
          if (type3 == 0) {
            this.rCom = [...arr, ...this.oneContent, ...this.twoContent, ...this.twoContentImg, ...this.threeContent];
          } else {
            this.rCom = [...arr, ...this.oneContent, ...this.twoContent, ...this.twoContentImg2, ...this.threeContent];
          }
        } else {
          this.rCom = [...arr, ...this.oneContent, ...this.twoContent, ...this.twoContentText, ...this.threeContent];
        }
      } else {
        if (type2 == 0) {
          this.rCom = [
            ...arr,
            ...this.oneContent,
            ...this.oneContentImg,
            ...this.twoContent,
            ...this.twoContentImg,
            ...this.threeContent,
          ];
        } else {
          this.rCom = [
            ...arr,
            ...this.oneContent,
            ...this.oneContentImg,
            ...this.twoContent,
            ...this.twoContentText,
            ...this.threeContent,
          ];
        }
      }
    },
    getRComStyle(arr, type, type2, type3, type4) {
      let obj = [
        ...arr,
        ...this.oneStyle,
        ...this.twoStyle,
        ...this.fourStyle2,
        ...this.fiveStyle,
        ...this.toneStyle,
        ...this.moduleStyle2,
        ...this.currencyStyle,
      ];
      let obj2 = [
        ...arr,
        ...this.oneStyle,
        ...this.twoStyle,
        ...this.threeStyle,
        ...this.fourStyle2,
        ...this.fiveStyle,
        ...this.toneStyle,
        ...this.moduleStyle2,
        ...this.currencyStyle,
      ];
      let obj3 = [
        ...arr,
        ...this.oneStyle,
        ...this.fourStyle,
        ...this.fiveStyle,
        ...this.toneStyle,
        ...this.currencyStyle,
      ];
      let obj4 = [
        ...arr,
        ...this.oneStyle,
        ...this.threeStyle,
        ...this.fourStyle,
        ...this.fiveStyle,
        ...this.toneStyle,
        ...this.currencyStyle,
      ];
      if (type == 0) {
        if (type2 == 0) {
          if (type3 == 0) {
            if (type4 == 0) {
              this.rCom = obj;
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle,
                ...this.fourStyle2,
                ...this.fiveStyle,
                ...this.toneStyle,
                ...this.goodsPriceStyle,
                ...this.priceBgStyle,
                ...this.moduleStyle2,
                ...this.currencyStyle,
              ];
            }
          } else {
            if (type4 == 0) {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle,
                ...this.fourStyle2,
                ...this.fiveStyle,
                ...this.goodsNameStyle,
                ...this.toneStyle,
                ...this.moduleStyle2,
                ...this.currencyStyle,
              ];
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle,
                ...this.fourStyle2,
                ...this.fiveStyle,
                ...this.goodsNameStyle,
                ...this.toneStyle,
                ...this.goodsPriceStyle2,
                ...this.moduleStyle2,
                ...this.currencyStyle,
              ];
            }
          }
        } else {
          if (type3 == 0) {
            if (type4 == 0) {
              this.rCom = obj2;
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle,
                ...this.threeStyle,
                ...this.fourStyle2,
                ...this.fiveStyle,
                ...this.toneStyle,
                ...this.goodsPriceStyle,
                ...this.priceBgStyle,
                ...this.moduleStyle2,
                ...this.currencyStyle,
              ];
            }
          } else {
            if (type4 == 0) {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle,
                ...this.threeStyle,
                ...this.fourStyle2,
                ...this.fiveStyle,
                ...this.goodsNameStyle,
                ...this.toneStyle,
                ...this.moduleStyle2,
                ...this.currencyStyle,
              ];
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle,
                ...this.threeStyle,
                ...this.fourStyle2,
                ...this.fiveStyle,
                ...this.goodsNameStyle,
                ...this.toneStyle,
                ...this.goodsPriceStyle2,
                ...this.moduleStyle2,
                ...this.currencyStyle,
              ];
            }
          }
        }
      } else {
        if (type2 == 0) {
          if (type3 == 0) {
            if (type4 == 0) {
              this.rCom = obj3;
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.fourStyle,
                ...this.fiveStyle,
                ...this.toneStyle,
                ...this.goodsPriceStyle,
                ...this.priceBgStyle,
                ...this.currencyStyle,
              ];
            }
          } else {
            if (type4 == 0) {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.fourStyle,
                ...this.fiveStyle,
                ...this.goodsNameStyle2,
                ...this.toneStyle,
                ...this.currencyStyle,
              ];
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.fourStyle,
                ...this.fiveStyle,
                ...this.goodsNameStyle2,
                ...this.toneStyle,
                ...this.goodsPriceStyle,
                ...this.goodsUnitPriceStyle,
                ...this.currencyStyle,
              ];
            }
          }
        } else {
          if (type3 == 0) {
            if (type4 == 0) {
              this.rCom = obj4;
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.threeStyle,
                ...this.fourStyle,
                ...this.fiveStyle,
                ...this.toneStyle,
                ...this.goodsPriceStyle,
                ...this.priceBgStyle,
                ...this.currencyStyle,
              ];
            }
          } else {
            if (type4 == 0) {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.threeStyle,
                ...this.fourStyle,
                ...this.fiveStyle,
                ...this.goodsNameStyle2,
                ...this.toneStyle,
                ...this.currencyStyle,
              ];
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.threeStyle,
                ...this.fourStyle,
                ...this.fiveStyle,
                ...this.goodsNameStyle2,
                ...this.toneStyle,
                ...this.goodsPriceStyle,
                ...this.goodsUnitPriceStyle,
                ...this.currencyStyle,
              ];
            }
          }
        }
      }
    },
    getConfig(data) {},
  },
};
</script>

<style scoped></style>
