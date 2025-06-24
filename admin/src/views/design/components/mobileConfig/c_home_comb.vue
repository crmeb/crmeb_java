<template>
  <div class="mobile-config">
    <el-form ref="formInline">
      <div v-for="(item, key) in rCom" :key="key">
        <component
          :is="item.components.name"
          :configObj="configObj"
          ref="childData"
          :configNme="item.configNme"
          :key="key"
          :index="activeIndex"
          :num="item.num"
          @getConfig="getConfig"
        ></component>
      </div>
      <rightBtn :activeIndex="activeIndex" :configObj="configObj"></rightBtn>
    </el-form>
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
export default {
  name: 'c_home_comb',
  componentsName: 'home_comb',
  cname: '头部组件',
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
      hotIndex: 1,
      configObj: {}, // 配置对象
      rCom: [
        {
          components: toolCom.c_checked_tab,
          configNme: 'setUp',
        },
      ],
      automatic: [
        {
          components: toolCom.c_tab,
          configNme: 'tabConfig',
        },
        {
          components: toolCom.c_upload_img,
          configNme: 'logoConfig',
        },
        {
          components: toolCom.c_input_item,
          configNme: 'hotWords',
        },
      ], // 当前页面组件
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
    'configObj.tabConfig.tabVal': {
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
              configNme: 'searConfig',
            },
            {
              components: toolCom.c_txt_tab,
              configNme: 'searConfig',
            },
            {
              components: toolCom.c_upload_img,
              configNme: 'logoConfig',
            },
            {
              components: toolCom.c_upload_img,
              configNme: 'logoFixConfig',
            },
            {
              components: toolCom.c_title,
              configNme: 'placeWords',
            },
            {
              components: toolCom.c_input_item,
              configNme: 'placeWords',
            },
            {
              components: toolCom.c_title,
              configNme: 'hotWords',
            },
            {
              components: toolCom.c_hot_word,
              configNme: 'hotWords',
            },
            {
              components: toolCom.c_input_item,
              configNme: 'titleConfig',
            },
            {
              components: toolCom.c_title,
              configNme: 'textColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'textColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'borderColor',
            },
            {
              components: toolCom.c_txt_tab,
              configNme: 'textPosition',
            },
            {
              components: toolCom.c_slider,
              configNme: 'contentStyle',
            },
          ];
          this.rCom = arr.concat(tempArr);
        } else if (nVal == 1) {
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
              configNme: 'listConfig',
            },
            {
              components: toolCom.c_radio,
              configNme: 'tabShowConfig',
            },
            {
              components: toolCom.c_classify,
              configNme: 'listConfig',
            },
            // {
            //   components: toolCom.c_title,
            //   configNme: 'fontColor',
            // },
            // {
            //   components: toolCom.c_bg_color,
            //   configNme: 'fontColor',
            // },
            // {
            //   components: toolCom.c_bg_color,
            //   configNme: 'checkColor',
            // },
          ];
          this.rCom = arr.concat(tempArr);
        } else if (nVal == 2) {
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
              configNme: 'swiperConfig',
            },
            {
              components: toolCom.c_radio,
              configNme: 'swiperStyleConfig',
            },
            {
              components: toolCom.c_menu_list,
              configNme: 'swiperConfig',
            },
            {
              components: toolCom.c_title,
              configNme: 'docConfig',
            },
            {
              components: toolCom.c_txt_tab,
              configNme: 'txtStyle',
            },
            {
              components: toolCom.c_radio,
              configNme: 'themeStyleConfig',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'docColor',
            },
            {
              components: toolCom.c_txt_tab,
              configNme: 'docConfig',
            },
            {
              components: toolCom.c_slider,
              configNme: 'contentStyleBanner',
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
    });
  },
  methods: {
    getConfig() {
      if (this.configObj.searConfig.tabVal === 0) {
        this.configObj.logoConfig.isShow = 0;
        this.configObj.logoFixConfig.isShow = 0;
      } else {
        this.configObj.logoConfig.isShow = 1;
        this.configObj.logoFixConfig.isShow = 1;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.title-tips {
  padding-bottom: 10px;
  color: #333;
  span {
    margin-right: 14px;
    color: #999;
  }
}
</style>
