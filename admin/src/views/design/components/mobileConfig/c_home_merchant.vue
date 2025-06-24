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
import toolCom from '../mobileConfigRight/index.js';
import rightBtn from '../rightBtn/index.vue';
import { mapGetters } from 'vuex';
export default {
  name: 'c_home_merchant',
  componentsName: 'home_merchant',
  computed: {
    ...mapGetters(['mediaDomain']),
  },
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
          components: toolCom.c_checked_tab,
          configNme: 'setUp',
        },
      ],
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
        var arr = [this.rCom[0]];
        if (nVal == 0) {
          let tempArr = [
            {
              components: toolCom.c_title,
              configNme: 'tabConfig',
            },
            {
              components: toolCom.c_txt_tab,
              configNme: 'tabConfig',
            },
            {
              components: toolCom.c_title,
              configNme: 'logoConfig',
            },
            {
              components: toolCom.c_upload_img,
              configNme: 'logoConfig',
            },
            {
              components: toolCom.c_title,
              configNme: 'titleConfig',
            },
            {
              components: toolCom.c_input_item,
              configNme: 'titleConfig',
            },
            {
              components: toolCom.c_input_item,
              configNme: 'linkConfig',
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
              configNme: 'listConfig',
            },
            {
              components: toolCom.c_tab_radio,
              configNme: 'listConfig',
            },
            {
              components: toolCom.c_input_number,
              configNme: 'numConfig',
            },
            {
              components: toolCom.c_merchantName,
              configNme: 'activeValueMer',
            },
          ];
          this.rCom = arr.concat(tempArr);
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
              components: toolCom.c_bg_color,
              configNme: 'nameColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'labelColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'labelFontColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'moreColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'merColor',
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
  },
  mounted() {
    this.$nextTick(() => {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[this.num]));
      this.configObj = value;
      this.configObj.logoConfig.url = this.mediaDomain + '/crmebimage/perset/haodian.png';
    });
  },
  created() {},
  methods: {
    getConfig(data) {
      if (data.name === 'tab_radio' && data.values === 0) {
        this.configObj.numConfig.isShow = 1;
        this.configObj.activeValueMer.isShow = 0;
      } else if (data.name === 'tab_radio' && data.values === 1) {
        this.configObj.numConfig.isShow = 0;
        this.configObj.activeValueMer.isShow = 1;
      }
    },
  },
};
</script>

<style scoped></style>
