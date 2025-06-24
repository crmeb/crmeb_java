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
import * as categoryApi from '@/api/categoryApi.js';
import * as articleApi from '@/api/article.js';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'c_home_article',
  componentsName: 'home_article',
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
      categoryId: '',
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
              configNme: 'layoutConfig',
            },
            {
              components: toolCom.c_txt_tab,
              configNme: 'layoutConfig',
            },
            {
              components: toolCom.c_title,
              configNme: 'selectConfig',
            },
            {
              components: toolCom.c_select,
              configNme: 'selectConfig',
            },
            {
              components: toolCom.c_input_number,
              configNme: 'numConfig',
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
              configNme: 'timeColor',
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
              configNme: 'contentConfig',
            },
            {
              components: toolCom.c_slider,
              configNme: 'lrConfig',
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
        //文章分类
        if (localStorage.getItem('adminArticleClassify')) {
          this.configObj.selectConfig.list = JSON.parse(localStorage.getItem('adminArticleClassify'));
        } else {
          if (checkPermi(['admin:article:category:list'])) this.handlerGetTreeList();
        }
      },
      deep: true,
    },
  },
  mounted() {
    this.$nextTick(() => {
      if (this.num) {
        let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[this.num]));
        this.configObj = value;
      }
    });
  },
  created() {},
  methods: {
    checkPermi,
    //文章分类
    handlerGetTreeList() {
      categoryApi.listCategroy({ type: 3, status: '' }).then((data) => {
        let list = data.filter((item) => {
          return item.status;
        });
        localStorage.setItem('adminArticleClassify', JSON.stringify(list));
        this.configObj.selectConfig.list = list;
      });
    },
    getConfig(data) {
      if (data.name === 'select') this.categoryId = data.values;
      if (data.name && (data.name === 'select' || data.name === 'number')) this.getArticleList();
    },
    //文章列表
    getArticleList() {
      articleApi
        .ListArticle({
          author: null,
          cid: this.categoryId,
          page: 1,
          title: '',
          limit: this.configObj.numConfig.val,
        })
        .then((data) => {
          this.configObj.selectConfig.articleList = data.list;
        });
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
