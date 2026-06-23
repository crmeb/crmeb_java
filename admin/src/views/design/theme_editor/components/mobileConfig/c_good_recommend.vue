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
  name: 'c_good_recommend',
  componentsName: 'home_good_recommend',
  cname: '优品推荐',
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
        {
          components: toolCom.c_card_select,
          configNme: 'styleConfig',
        },
        {
          components: toolCom.c_set_up,
          configNme: 'setUp',
        },
      ],
      setUp: 0,
    };
  },
  watch: {
    num(nVal) {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[nVal]));
      this.configObj = this.patchConfig(value);
      this.setUp = value.setUp.tabVal;
      this.updateRCom();
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
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.headerType.tabVal': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.typeConfig.activeValue': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.cartConfig.tabVal': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.toneConfig.tabVal': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.toneCartConfig.tabVal': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
  },
  mounted() {
    this.$nextTick(() => {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[this.num]));
      this.configObj = this.patchConfig(value);
      this.setUp = value.setUp.tabVal;
      this.updateRCom();
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
    updateRCom() {
      if (!this.configObj.setUp) return;

      let arr = [
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
        // Content Config
        let contentArr = [
          {
            components: toolCom.c_title,
            configNme: 'headerTitle',
          },
          {
            components: toolCom.c_radio,
            configNme: 'headerType',
          },
        ];

        if (this.configObj.headerType && this.configObj.headerType.tabVal == 0) {
          // Text
          contentArr.push({
            components: toolCom.c_input_item,
            configNme: 'headerText',
          });
        } else {
          // Image
          contentArr.push({
            components: toolCom.c_upload_img,
            configNme: 'headerImg',
          });
        }

        contentArr = contentArr.concat([
          {
            components: toolCom.c_title,
            configNme: 'titleGoods',
          },
          {
            components: toolCom.c_select,
            configNme: 'typeConfig',
          },
        ]);

        let type = this.configObj.typeConfig.activeValue;
        if (type == 1) {
          contentArr.push({
            components: toolCom.c_goods,
            configNme: 'goodsList',
          });
        } else if (type == 3) {
          contentArr.push({
            components: toolCom.c_classify,
            configNme: 'classList',
          });
          contentArr.push({
            components: toolCom.c_slider,
            configNme: 'numberConfig',
          });
          contentArr.push({
            components: toolCom.c_radio,
            configNme: 'goodsSort',
          });
        } else if (type == 4) {
          contentArr.push({
            components: toolCom.c_goods_label,
            configNme: 'goodsLabel',
          });
          contentArr.push({
            components: toolCom.c_slider,
            configNme: 'numberConfig',
          });
          contentArr.push({
            components: toolCom.c_radio,
            configNme: 'goodsSort',
          });
        }

        contentArr = contentArr.concat([
          {
            components: toolCom.c_checkbox,
            configNme: 'checkboxInfo',
          },
          {
            components: toolCom.c_radio,
            configNme: 'cartConfig',
          },
        ]);

        if (this.configObj.cartConfig.tabVal == 0) {
          contentArr.push({
            components: toolCom.c_button_img,
            configNme: 'bntStyleConfig',
          });
          contentArr.push({
            components: toolCom.c_radio,
            configNme: 'bntConfig',
          });
        }

        this.rCom = arr.concat(contentArr);
      } else {
        // Style Config
        let styleArr = [
          {
            components: toolCom.c_title,
            configNme: 'headerStyleTitle',
          },
          {
            components: toolCom.c_radio,
            configNme: 'headerAlign',
          },
        ];

        if (this.configObj.headerType.tabVal == 0) {
          styleArr.push({
            components: toolCom.c_radio,
            configNme: 'headerTextConfig', // Font Style (Bold/Normal/Italic)
          });
          styleArr.push({
            components: toolCom.c_bg_color,
            configNme: 'headerColor',
          });
          styleArr.push({
            components: toolCom.c_slider,
            configNme: 'headerFontSize',
          });
        }

        styleArr = styleArr.concat([
          {
            components: toolCom.c_radio,
            configNme: 'goodsName',
          },
          {
            components: toolCom.c_radio,
            configNme: 'toneConfig',
          },
        ]);

        if (this.configObj.toneConfig.tabVal == 1) {
          styleArr.push({
            components: toolCom.c_bg_color,
            configNme: 'goodsNameColor',
          });
          styleArr.push({
            components: toolCom.c_bg_color,
            configNme: 'goodsPriceColor',
          });
          styleArr.push({
            components: toolCom.c_bg_color,
            configNme: 'soldNumColor',
          });
          styleArr.push({
            components: toolCom.c_bg_color,
            configNme: 'scoreColor',
          });
        }

        if (this.configObj.cartConfig.tabVal == 0) {
          styleArr.push({
            components: toolCom.c_title,
            configNme: 'cartStyleTitle',
          });
          styleArr.push({
            components: toolCom.c_radio,
            configNme: 'toneCartConfig',
          });
          if (this.configObj.toneCartConfig.tabVal == 1) {
            styleArr.push({
              components: toolCom.c_bg_color,
              configNme: 'bntBgColor',
            });
          }
        }

        styleArr = styleArr.concat([
          {
            components: toolCom.c_title,
            configNme: 'goodsStyleTitle',
          },
          {
            components: toolCom.c_fillet,
            configNme: 'filletImg',
          },
          {
            components: toolCom.c_title,
            configNme: 'generalStyleTitle',
          },
          {
            components: toolCom.c_common_style,
            configNme: 'c_common_style',
          },
        ]);

        this.rCom = arr.concat(styleArr);
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
  },
};
</script>
