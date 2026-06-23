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
import { mapState, mapMutations, mapActions } from 'vuex';
import { getArticleList, themeArticleCategory } from '@/api/theme';
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
          configNme: 'styleConfig',
        },
        {
          components: toolCom.c_set_up,
          configNme: 'setUp',
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
          configNme: 'nameConfig',
        },
        {
          components: toolCom.c_radio,
          configNme: 'toneConfig',
        },
      ],
      twoStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'likeSuccessColor',
        },
      ],
      threeStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'nameColor',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'timeColor',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'browseColor',
        },
        // {
        //   components: toolCom.c_bg_color,
        //   configNme: 'likeColor',
        // },
        {
          components: toolCom.c_bg_color,
          configNme: 'statisticColor',
        },
        {
          components: toolCom.c_common_style,
          configNme: 'c_common_style',
        },
      ],
      setUp: 0,
      type: 0,
    };
  },
  watch: {
    num(nVal) {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[nVal]));
      this.configObj = value;
      this.categoryList();
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
        // 取前两个
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
        if (nVal == 0) {
          let tempArr = [
            {
              components: toolCom.c_title,
              configNme: 'titleArticle',
            },
            {
              components: toolCom.c_select,
              configNme: 'selectConfig',
            },
            {
              components: toolCom.c_input_number,
              configNme: 'numConfig',
            },
            {
              components: toolCom.c_title,
              configNme: 'titleList',
            },
            {
              components: toolCom.c_checkbox,
              configNme: 'checkboxList',
            },
          ];
          this.rCom = arr.concat(tempArr);
        } else {
          if (this.type) {
            this.rCom = [...arr, ...this.oneStyle, ...this.twoStyle, ...this.threeStyle];
          } else {
            this.rCom = [...arr, ...this.oneStyle, ...this.threeStyle];
          }
        }
      },
      deep: true,
    },
    'configObj.toneConfig.tabVal': {
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
          if (nVal) {
            this.rCom = [...arr, ...this.oneStyle, ...this.twoStyle, ...this.threeStyle];
          } else {
            this.rCom = [...arr, ...this.oneStyle, ...this.threeStyle];
          }
        }
      },
    },
  },
  mounted() {
    this.$nextTick(() => {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[this.num]));
      this.configObj = this.patchConfig(value);
      this.categoryList();
    });
  },
  methods: {
    patchConfig(data) {
      if (!data) return data;
      if (!data.paddingConfig) {
        this.$set(data, 'paddingConfig', {
          isAll: false,
          title: '内边距',
          val: 0,
          min: 0,
          max: 100,
          valList: [
            { val: data.topConfig ? data.topConfig.val : 0 },
            { val: data.prConfig ? data.prConfig.val : 0 },
            { val: data.bottomConfig ? data.bottomConfig.val : 0 },
            { val: data.prConfig ? data.prConfig.val : 0 },
          ],
        });
      }
      if (!data.marginConfig) {
        this.$set(data, 'marginConfig', {
          isAll: false,
          title: '外边距',
          val: 0,
          min: 0,
          max: 100,
          valList: [{ val: data.mbConfig ? data.mbConfig.val : 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        });
      }
      return data;
    },
    categoryList() {
      themeArticleCategory()
        .then((res) => {
          let data = [];
          res.data.map((item) => {
            data.push({ title: item.title, activeValue: item.id.toString() });
          });
          this.configObj.selectConfig.list = data;
          // this.$store.commit('mobildConfig/UPDATEARR', { num: this.num, val: this.pageData })
        })
        .catch((err) => {
          this.$message.error(err.msg);
        });
    },
    // 获取组件参数
    getConfig(data) {
      if (data.name == 'radio') {
        return;
      }
      let val = {
        cid: this.configObj.selectConfig.activeValue,
        page: 1,
        limit: parseInt(this.configObj.numConfig.val),
      };
      getArticleList(val)
        .then((res) => {
          this.configObj.selectList.list = res.list || (res.data && res.data.list) || [];
        })
        .catch((err) => {
          this.$message.error((err && (err.msg || err.message)) || '文章列表获取失败');
        });
    },
    handleSubmit(name) {
      let obj = {};
      obj.activeIndex = this.activeIndex;
      obj.data = this.configObj;
      this.add(obj);
    },
    ...mapMutations({
      add: 'mobildConfig/UPDATEARR',
    }),
  },
};
</script>

<style scoped lang="scss">
.title-tips {
  padding-bottom: 10px;
  font-size: 14px;
  color: #333;
  span {
    margin-right: 14px;
    color: #999;
  }
}
</style>
