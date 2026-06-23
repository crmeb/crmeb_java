<template>
  <div class="mobile-config">
    <div v-for="(item, key) in rCom" :key="key">
      <component
        :is="item.components.name"
        :configObj="configObj"
        ref="childData"
        @getConfig="getConfig"
        :key="key"
        :configNme="item.configNme"
      ></component>
    </div>
    <rightBtn :activeIndex="activeIndex" :configObj="configObj"></rightBtn>
  </div>
</template>

<script>
import toolCom from '@/views/design/theme_editor/components/mobileConfigRight/index.js';
import rightBtn from '@/views/design/theme_editor/components/rightBtn/index.vue';
import { mapState, mapMutations, mapActions } from 'vuex';
export default {
  name: 'c_home_bargain',
  componentsName: 'home_bargain',
  components: {
    ...toolCom,
    rightBtn,
  },
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
  data() {
    return {
      configObj: {},
      rCom: [
        {
          components: toolCom.c_card_select,
          configNme: 'goodStyleConfig',
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
          configNme: 'styleConfig',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'headerBgColor',
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
      twoContentColorImg: [
        {
          components: toolCom.c_upload_img,
          configNme: 'imgColorConfig',
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
          configNme: 'tipTxtConfig',
        },
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
        {
          components: toolCom.c_checkbox,
          configNme: 'checkboxInfo',
        },
      ],
      fourContent: [
        {
          components: toolCom.c_radio,
          configNme: 'bargainConfig',
        },
      ],
      oneStyle: [
        {
          components: toolCom.c_title,
          configNme: 'titleRight',
        },
      ],
      twoStyle: [],
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
      fourBntStyle: [
        {
          components: toolCom.c_slider,
          configNme: 'bntNumber',
        },
      ],
      fourColorStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'tipsColor',
        },
      ],
      fourColorStyle2: [
        {
          components: toolCom.c_bg_color,
          configNme: 'tipsColor2',
        },
      ],
      fourGoodsStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'dividerColor',
        },
        {
          components: toolCom.c_title,
          configNme: 'titleGoodsStyle',
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
          components: toolCom.c_bg_color,
          configNme: 'goodsNameColor',
        },
      ],
      goodsPriceStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'goodsPriceColor',
        },
      ],
      toneStyle: [
        {
          components: toolCom.c_radio,
          configNme: 'toneConfig',
        },
      ],
      joinNumStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'joinNumColor',
        },
      ],
      joinNumStyle2: [
        {
          components: toolCom.c_bg_color,
          configNme: 'joinNumColor2',
        },
      ],
      joinBgStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'joinBgColor',
        },
      ],
      fiveStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'bargainPriceColor',
        },
      ],
      bntStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'goodsBntColor',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'goodsBntTxtColor',
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
    };
  },
  watch: {
    num(nVal) {
      this.configObj = this.$store.state.mobildConfig.defaultArray[nVal];
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
          this.getRComStyle(arr, this.type, this.type2, this.type3, this.type5, this.type4);
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
          this.getRComStyle(arr, nVal, this.type2, this.type3, this.type5, this.type4);
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
          this.getRComStyle(arr, this.type, nVal, this.type3, this.type5, this.type4);
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
          this.getRComStyle(arr, this.type, this.type2, nVal, this.type5, this.type4);
        }
      },
      deep: true,
    },
    'configObj.bargainConfig.tabVal': {
      handler(nVal, oVal) {
        this.type5 = nVal;
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
          this.getRComStyle(arr, this.type, this.type2, this.type3, nVal, this.type4);
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
          this.getRComStyle(arr, this.type, this.type2, this.type3, this.type5, nVal);
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
      if (!data.headerBgColor) {
        this.$set(data, 'headerBgColor', {
          title: '头部背景',
          name: 'headerBgColor',
          default: [
            {
              item: '#F62C2C',
            },
            {
              item: '#F96E29',
            },
          ],
          color: [
            {
              item: '#F62C2C',
            },
            {
              item: '#F96E29',
            },
          ],
        });
      }
      if (!data.paddingConfig) {
        this.$set(data, 'paddingConfig', {
          isAll: false,
          title: '内边距',
          val: 0,
          min: 0,
          max: 100,
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
          isAll: false,
          title: '外边距',
          val: 0,
          min: 0,
          max: 100,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        });
        if (data.mbConfig) data.marginConfig.valList[0].val = data.mbConfig.val;
      }
      if (!data.c_common_style) {
        this.$set(data, 'c_common_style', {
          color: {
            title: '背景颜色',
            val: '',
            name: 'bgColor',
          },
          color2: {
            title: '线条颜色',
            val: '',
            name: 'lineColor',
          },
          lr: {
            title: '左右边距',
            val: 0,
            min: 0,
            max: 100,
          },
          type: 0,
        });
      }
      return data;
    },
    getRComContent(arr, type, type2, type3) {
      if (type == 0) {
        if (type2 == 0) {
          if (type3 == 2 || type3 == 3) {
            this.rCom = [
              ...arr,
              ...this.oneContent,
              ...this.twoContent,
              ...this.twoContentColorImg,
              ...this.threeContent,
            ];
          } else {
            this.rCom = [
              ...arr,
              ...this.oneContent,
              ...this.twoContent,
              ...this.twoContentColorImg,
              ...this.threeContent,
              ...this.fourContent,
            ];
          }
        } else {
          if (type3 == 2 || type3 == 3) {
            this.rCom = [...arr, ...this.oneContent, ...this.twoContent, ...this.twoContentText, ...this.threeContent];
          } else {
            this.rCom = [
              ...arr,
              ...this.oneContent,
              ...this.twoContent,
              ...this.twoContentText,
              ...this.threeContent,
              ...this.fourContent,
            ];
          }
        }
      } else {
        // When type != 0 (Background Image Style), remove headerBgColor from oneContent
        const oneContentFiltered = this.oneContent.filter((item) => item.configNme !== 'headerBgColor');

        if (type2 == 0) {
          if (type3 == 2 || type3 == 3) {
            this.rCom = [
              ...arr,
              ...oneContentFiltered,
              ...this.oneContentImg,
              ...this.twoContent,
              ...this.twoContentImg,
              ...this.threeContent,
            ];
          } else {
            this.rCom = [
              ...arr,
              ...oneContentFiltered,
              ...this.oneContentImg,
              ...this.twoContent,
              ...this.twoContentImg,
              ...this.threeContent,
              ...this.fourContent,
            ];
          }
        } else {
          if (type3 == 2 || type3 == 3) {
            this.rCom = [
              ...arr,
              ...oneContentFiltered,
              ...this.oneContentImg,
              ...this.twoContent,
              ...this.twoContentText,
              ...this.threeContent,
            ];
          } else {
            this.rCom = [
              ...arr,
              ...oneContentFiltered,
              ...this.oneContentImg,
              ...this.twoContent,
              ...this.twoContentText,
              ...this.threeContent,
              ...this.fourContent,
            ];
          }
        }
      }
    },
    getRComStyle(arr, type, type2, type3, type5, type4) {
      let obj = [
        ...arr,
        ...this.oneStyle,
        ...this.twoStyle,
        ...this.fourStyle2,
        ...this.fourBntStyle,
        ...this.fourColorStyle2,
        ...this.fourGoodsStyle,
        ...this.goodsPriceStyle,
        ...this.toneStyle,
        ...this.currencyStyle,
      ];
      let obj2 = [
        ...arr,
        ...this.oneStyle,
        ...this.twoStyle,
        ...this.threeStyle,
        ...this.fourStyle2,
        ...this.fourBntStyle,
        ...this.fourColorStyle2,
        ...this.fourGoodsStyle,
        ...this.goodsPriceStyle,
        ...this.toneStyle,
        ...this.currencyStyle,
      ];
      let obj3 = [
        ...arr,
        ...this.oneStyle,
        ...this.fourStyle,
        ...this.fourBntStyle,
        ...this.fourColorStyle,
        ...this.fourGoodsStyle,
        ...this.goodsPriceStyle,
        ...this.toneStyle,
        ...this.currencyStyle,
      ];
      let obj4 = [
        ...arr,
        ...this.oneStyle,
        ...this.threeStyle,
        ...this.fourStyle,
        ...this.fourBntStyle,
        ...this.fourColorStyle,
        ...this.fourGoodsStyle,
        ...this.goodsPriceStyle,
        ...this.toneStyle,
        ...this.currencyStyle,
      ];
      if (type == 0) {
        if (type2 == 0) {
          if (type3 == 0) {
            if (type5 == 0) {
              if (type4 == 0) {
                this.rCom = obj;
              } else {
                this.rCom = [
                  ...arr,
                  ...this.oneStyle,
                  ...this.twoStyle,
                  ...this.fourStyle2,
                  ...this.fourBntStyle,
                  ...this.fourColorStyle2,
                  ...this.fourGoodsStyle,
                  ...this.goodsPriceStyle,
                  ...this.toneStyle,
                  ...this.joinNumStyle2,
                  ...this.fiveStyle,
                  ...this.bntStyle,
                  ...this.currencyStyle,
                ];
              }
            } else {
              if (type4 == 0) {
                this.rCom = obj;
              } else {
                this.rCom = [
                  ...arr,
                  ...this.oneStyle,
                  ...this.twoStyle,
                  ...this.fourStyle2,
                  ...this.fourBntStyle,
                  ...this.fourColorStyle2,
                  ...this.fourGoodsStyle,
                  ...this.goodsPriceStyle,
                  ...this.toneStyle,
                  ...this.joinNumStyle2,
                  ...this.fiveStyle,
                  ...this.currencyStyle,
                ];
              }
            }
          } else if (type3 == 1) {
            if (type5 == 0) {
              if (type4 == 0) {
                this.rCom = obj;
              } else {
                this.rCom = [
                  ...arr,
                  ...this.oneStyle,
                  ...this.twoStyle,
                  ...this.fourStyle2,
                  ...this.fourBntStyle,
                  ...this.fourColorStyle2,
                  ...this.fourGoodsStyle,
                  ...this.goodsPriceStyle,
                  ...this.toneStyle,
                  ...this.fiveStyle,
                  ...this.bntStyle,
                  ...this.currencyStyle,
                ];
              }
            } else {
              if (type4 == 0) {
                this.rCom = obj;
              } else {
                this.rCom = [
                  ...arr,
                  ...this.oneStyle,
                  ...this.twoStyle,
                  ...this.fourStyle2,
                  ...this.fourBntStyle,
                  ...this.fourColorStyle2,
                  ...this.fourGoodsStyle,
                  ...this.goodsPriceStyle,
                  ...this.toneStyle,
                  ...this.fiveStyle,
                  ...this.currencyStyle,
                ];
              }
            }
          } else if (type3 == 2) {
            if (type4 == 0) {
              this.rCom = obj;
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle,
                ...this.fourStyle2,
                ...this.fourBntStyle,
                ...this.fourColorStyle2,
                ...this.fourGoodsStyle,
                ...this.goodsPriceStyle,
                ...this.toneStyle,
                ...this.fiveStyle,
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
                ...this.fourBntStyle,
                ...this.fourColorStyle2,
                ...this.fourGoodsStyle,
                ...this.toneStyle,
                ...this.currencyStyle,
              ];
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle,
                ...this.fourStyle2,
                ...this.fourBntStyle,
                ...this.fourColorStyle2,
                ...this.fourGoodsStyle,
                ...this.toneStyle,
                ...this.joinNumStyle,
                ...this.joinBgStyle,
                ...this.fiveStyle,
                ...this.currencyStyle,
              ];
            }
          }
        } else {
          if (type3 == 0) {
            if (type5 == 0) {
              if (type4 == 0) {
                this.rCom = obj2;
              } else {
                this.rCom = [
                  ...arr,
                  ...this.oneStyle,
                  ...this.twoStyle,
                  ...this.threeStyle,
                  ...this.fourStyle2,
                  ...this.fourBntStyle,
                  ...this.fourColorStyle2,
                  ...this.fourGoodsStyle,
                  ...this.goodsPriceStyle,
                  ...this.toneStyle,
                  ...this.joinNumStyle2,
                  ...this.fiveStyle,
                  ...this.bntStyle,
                  ...this.currencyStyle,
                ];
              }
            } else {
              if (type4 == 0) {
                this.rCom = obj2;
              } else {
                this.rCom = [
                  ...arr,
                  ...this.oneStyle,
                  ...this.twoStyle,
                  ...this.threeStyle,
                  ...this.fourStyle2,
                  ...this.fourBntStyle,
                  ...this.fourColorStyle2,
                  ...this.fourGoodsStyle,
                  ...this.goodsPriceStyle,
                  ...this.toneStyle,
                  ...this.joinNumStyle2,
                  ...this.fiveStyle,
                  ...this.currencyStyle,
                ];
              }
            }
          } else if (type3 == 1) {
            if (type5 == 0) {
              if (type4 == 0) {
                this.rCom = obj2;
              } else {
                this.rCom = [
                  ...arr,
                  ...this.oneStyle,
                  ...this.twoStyle,
                  ...this.threeStyle,
                  ...this.fourStyle2,
                  ...this.fourBntStyle,
                  ...this.fourColorStyle2,
                  ...this.fourGoodsStyle,
                  ...this.goodsPriceStyle,
                  ...this.toneStyle,
                  ...this.fiveStyle,
                  ...this.bntStyle,
                  ...this.currencyStyle,
                ];
              }
            } else {
              if (type4 == 0) {
                this.rCom = obj2;
              } else {
                this.rCom = [
                  ...arr,
                  ...this.oneStyle,
                  ...this.twoStyle,
                  ...this.threeStyle,
                  ...this.fourStyle2,
                  ...this.fourBntStyle,
                  ...this.fourColorStyle2,
                  ...this.fourGoodsStyle,
                  ...this.goodsPriceStyle,
                  ...this.toneStyle,
                  ...this.fiveStyle,
                  ...this.currencyStyle,
                ];
              }
            }
          } else if (type3 == 2) {
            if (type4 == 0) {
              this.rCom = obj2;
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle,
                ...this.threeStyle,
                ...this.fourStyle2,
                ...this.fourBntStyle,
                ...this.fourColorStyle2,
                ...this.fourGoodsStyle,
                ...this.goodsPriceStyle,
                ...this.toneStyle,
                ...this.fiveStyle,
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
                ...this.fourBntStyle,
                ...this.fourColorStyle2,
                ...this.fourGoodsStyle,
                ...this.toneStyle,
                ...this.currencyStyle,
              ];
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.twoStyle,
                ...this.threeStyle,
                ...this.fourStyle2,
                ...this.fourBntStyle,
                ...this.fourColorStyle2,
                ...this.fourGoodsStyle,
                ...this.toneStyle,
                ...this.joinNumStyle,
                ...this.joinBgStyle,
                ...this.fiveStyle,
                ...this.currencyStyle,
              ];
            }
          }
        }
      } else {
        if (type2 == 0) {
          if (type3 == 0) {
            if (type5 == 0) {
              if (type4 == 0) {
                this.rCom = obj3;
              } else {
                this.rCom = [
                  ...arr,
                  ...this.oneStyle,
                  ...this.fourStyle,
                  ...this.fourBntStyle,
                  ...this.fourColorStyle,
                  ...this.fourGoodsStyle,
                  ...this.goodsPriceStyle,
                  ...this.toneStyle,
                  ...this.joinNumStyle,
                  ...this.fiveStyle,
                  ...this.bntStyle,
                  ...this.currencyStyle,
                ];
              }
            } else {
              if (type4 == 0) {
                this.rCom = obj3;
              } else {
                this.rCom = [
                  ...arr,
                  ...this.oneStyle,
                  ...this.fourStyle,
                  ...this.fourBntStyle,
                  ...this.fourColorStyle,
                  ...this.fourGoodsStyle,
                  ...this.goodsPriceStyle,
                  ...this.toneStyle,
                  ...this.joinNumStyle,
                  ...this.fiveStyle,
                  ...this.currencyStyle,
                ];
              }
            }
          } else if (type3 == 1) {
            if (type5 == 0) {
              if (type4 == 0) {
                this.rCom = obj3;
              } else {
                this.rCom = [
                  ...arr,
                  ...this.oneStyle,
                  ...this.fourStyle,
                  ...this.fourBntStyle,
                  ...this.fourColorStyle,
                  ...this.fourGoodsStyle,
                  ...this.goodsPriceStyle,
                  ...this.toneStyle,
                  ...this.fiveStyle,
                  ...this.bntStyle,
                  ...this.currencyStyle,
                ];
              }
            } else {
              if (type4 == 0) {
                this.rCom = obj3;
              } else {
                this.rCom = [
                  ...arr,
                  ...this.oneStyle,
                  ...this.fourStyle,
                  ...this.fourBntStyle,
                  ...this.fourColorStyle,
                  ...this.fourGoodsStyle,
                  ...this.goodsPriceStyle,
                  ...this.toneStyle,
                  ...this.fiveStyle,
                  ...this.currencyStyle,
                ];
              }
            }
          } else if (type3 == 2) {
            if (type4 == 0) {
              this.rCom = obj3;
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.fourStyle,
                ...this.fourBntStyle,
                ...this.fourColorStyle,
                ...this.fourGoodsStyle,
                ...this.goodsPriceStyle,
                ...this.toneStyle,
                ...this.fiveStyle,
                ...this.currencyStyle,
              ];
            }
          } else {
            if (type4 == 0) {
              this.rCom = obj3;
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.fourStyle,
                ...this.fourBntStyle,
                ...this.fourColorStyle,
                ...this.fourGoodsStyle,
                ...this.toneStyle,
                ...this.joinNumStyle,
                ...this.joinBgStyle,
                ...this.fiveStyle,
                ...this.currencyStyle,
              ];
            }
          }
        } else {
          if (type3 == 0) {
            if (type5 == 0) {
              if (type4 == 0) {
                this.rCom = obj4;
              } else {
                this.rCom = [
                  ...arr,
                  ...this.oneStyle,
                  ...this.threeStyle,
                  ...this.fourStyle,
                  ...this.fourBntStyle,
                  ...this.fourColorStyle,
                  ...this.fourGoodsStyle,
                  ...this.goodsPriceStyle,
                  ...this.toneStyle,
                  ...this.joinNumStyle,
                  ...this.fiveStyle,
                  ...this.bntStyle,
                  ...this.currencyStyle,
                ];
              }
            } else {
              if (type4 == 0) {
                this.rCom = obj4;
              } else {
                this.rCom = [
                  ...arr,
                  ...this.oneStyle,
                  ...this.threeStyle,
                  ...this.fourStyle,
                  ...this.fourBntStyle,
                  ...this.fourColorStyle,
                  ...this.fourGoodsStyle,
                  ...this.goodsPriceStyle,
                  ...this.toneStyle,
                  ...this.joinNumStyle,
                  ...this.fiveStyle,
                  ...this.currencyStyle,
                ];
              }
            }
          } else if (type3 == 1) {
            if (type5 == 0) {
              if (type4 == 0) {
                this.rCom = obj4;
              } else {
                this.rCom = [
                  ...arr,
                  ...this.oneStyle,
                  ...this.threeStyle,
                  ...this.fourStyle,
                  ...this.fourBntStyle,
                  ...this.fourColorStyle,
                  ...this.fourGoodsStyle,
                  ...this.goodsPriceStyle,
                  ...this.toneStyle,
                  ...this.fiveStyle,
                  ...this.bntStyle,
                  ...this.currencyStyle,
                ];
              }
            } else {
              if (type4 == 0) {
                this.rCom = obj4;
              } else {
                this.rCom = [
                  ...arr,
                  ...this.oneStyle,
                  ...this.threeStyle,
                  ...this.fourStyle,
                  ...this.fourBntStyle,
                  ...this.fourColorStyle,
                  ...this.fourGoodsStyle,
                  ...this.goodsPriceStyle,
                  ...this.toneStyle,
                  ...this.fiveStyle,
                  ...this.currencyStyle,
                ];
              }
            }
          } else if (type3 == 2) {
            if (type4 == 0) {
              this.rCom = obj4;
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.threeStyle,
                ...this.fourStyle,
                ...this.fourBntStyle,
                ...this.fourColorStyle,
                ...this.fourGoodsStyle,
                ...this.goodsPriceStyle,
                ...this.toneStyle,
                ...this.fiveStyle,
                ...this.currencyStyle,
              ];
            }
          } else {
            if (type4 == 0) {
              this.rCom = obj4;
            } else {
              this.rCom = [
                ...arr,
                ...this.oneStyle,
                ...this.threeStyle,
                ...this.fourStyle,
                ...this.fourBntStyle,
                ...this.fourColorStyle,
                ...this.fourGoodsStyle,
                ...this.toneStyle,
                ...this.joinNumStyle,
                ...this.joinBgStyle,
                ...this.fiveStyle,
                ...this.currencyStyle,
              ];
            }
          }
        }
      }
    },
    // 获取组件参数
    getConfig(data) {},
  },
};
</script>

<style scoped></style>
