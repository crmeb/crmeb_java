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
export default {
  name: 'c_member',
  componentsName: 'home_member',
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
      setUp: 0,
    };
  },
  watch: {
    num(nVal) {
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
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.styleConfig.tabVal': {
      handler(nVal, oVal) {
        if (nVal == 3 || nVal == 4) {
          if (this.configObj.nameColor.color[0].item == '#fff') this.configObj.nameColor.color[0].item = '#333';
          if (this.configObj.numColor.color[0].item == '#fff') this.configObj.numColor.color[0].item = '#333';
          // 背景色也修改为 333
          if (this.configObj.compo.img - box - girdnentBgConfig.colorConfig.color[0].item == '#E93323')
            this.configObj.componentBgConfig.colorConfig.color[0].item = '#333';
          if (this.configObj.componentBgConfig.colorConfig.color[1].item == '#E93323')
            this.configObj.componentBgConfig.colorConfig.color[1].item = '#333';
          if (this.configObj.dataNumColor.color[0].item == '#fff') this.configObj.dataNumColor.color[0].item = '#333';
          if (this.configObj.dataTitleColor.color[0].item == '#fff')
            this.configObj.dataTitleColor.color[0].item = '#333';
        } else {
          if (this.configObj.dataNumColor.color[0].item == '#333') this.configObj.dataNumColor.color[0].item = '#fff';
          if (this.configObj.dataTitleColor.color[0].item == '#333')
            this.configObj.dataTitleColor.color[0].item = '#fff';
          if (this.configObj.nameColor.color[0].item == '#333') this.configObj.nameColor.color[0].item = '#fff';
          if (this.configObj.numColor.color[0].item == '#333') this.configObj.numColor.color[0].item = '#fff';
          if (this.configObj.componentBgConfig.colorConfig.color[0].item == '#333')
            this.configObj.componentBgConfig.colorConfig.color[0].item = '#E93323';
          if (this.configObj.componentBgConfig.colorConfig.color[1].item == '#333')
            this.configObj.componentBgConfig.colorConfig.color[1].item = '#E93323';
        }
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.memberStyleConfig.tabVal': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.assetMode.tabVal': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.menuConfig.listStyle': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.shortcutConfig.listStyle': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.assetMode.tabVal': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.ms2TitleType.tabVal': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.ms3BgMode.tabVal': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.ms4BgMode.tabVal': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
  },
  mounted() {
    this.$nextTick(() => {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[this.num]));
      this.configObj = value;
      this.configObj = this.patchConfig(this.configObj);
    });
  },
  methods: {
    updateRCom() {
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
        let tempArr = [
          {
            components: toolCom.c_title,
            configNme: 'titleLeft',
          },
          {
            components: toolCom.c_radio,
            configNme: 'userInfoConfig',
          },
          {
            components: toolCom.c_radio,
            configNme: 'memberStyleConfig',
          },
        ];

        // Menu Config - Available for Style 1, 3, 5 (Index 0, 2, 4)
        if (
          this.configObj.styleConfig.tabVal == 0 ||
          this.configObj.styleConfig.tabVal == 2 ||
          this.configObj.styleConfig.tabVal == 4
        ) {
          tempArr.push({
            components: toolCom.c_menu_list,
            configNme: 'menuConfig',
          });
        }

        // Shortcut Config - Left Top
        // if (this.configObj.styleConfig.tabVal == 0 || this.configObj.styleConfig.tabVal == 2) {
        //   tempArr.push({
        //     components: toolCom.c_menu_list,
        //     configNme: 'shortcutConfig',
        //   });
        // }

        tempArr.push({
          components: toolCom.c_radio,
          configNme: 'assetMode',
        });
        if (this.configObj.assetMode.tabVal == 0) {
          tempArr.push({
            components: toolCom.c_radio,
            configNme: 'dataStyle',
          });
          tempArr.push({
            components: toolCom.c_checkbox,
            configNme: 'checkboxInfo',
          });
        } else {
          tempArr.push({
            components: toolCom.c_menu_list,
            configNme: 'assetConfig',
          });
        }
        // Member Style 1 (Index 0)
        if (this.configObj.memberStyleConfig.tabVal == 0) {
          tempArr.push({
            components: toolCom.c_menu_list,
            configNme: 'memberConfig',
          });
        }
        // Member Style 2 (Index 1)
        if (this.configObj.memberStyleConfig.tabVal == 1) {
          tempArr.push({ components: toolCom.c_title, configNme: 'infoStyleText' });
          tempArr.push({ components: toolCom.c_radio, configNme: 'ms2TitleType' });
          if (this.configObj.ms2TitleType.tabVal == 0) {
            tempArr.push({ components: toolCom.c_input_item, configNme: 'ms2TitleText' });
          } else {
            tempArr.push({ components: toolCom.c_upload_img, configNme: 'ms2TitleImage' });
          }
          tempArr.push({ components: toolCom.c_input_item, configNme: 'ms2IntroText' });
          tempArr.push({ components: toolCom.c_menu_list, configNme: 'ms2RightsList' });
          tempArr.push({ components: toolCom.c_upload_img, configNme: 'ms2ExplainIcons' });
          tempArr.push({ components: toolCom.c_input_item, configNme: 'ms2ExplainText' });
          // tempArr.push({ components: toolCom.c_bg_color, configNme: 'ms2ExplainColor' });
          tempArr.push({ components: toolCom.c_input_item, configNme: 'ms2ButtonText' });
          tempArr.push({ components: toolCom.c_input_item, configNme: 'ms2ButtonLink' });
          // tempArr.push({ components: toolCom.c_bg_color, configNme: 'ms2ButtonColor' });
          // tempArr.push({ components: toolCom.c_bg_color, configNme: 'ms2ButtonBgColor' });
        }
        // Member Style 3 (Index 2)
        if (this.configObj.memberStyleConfig.tabVal == 2) {
          tempArr.push(
            {
              components: toolCom.c_title,
              configNme: 'infoStyleText',
            },
            { components: toolCom.c_input_item, configNme: 'ms3TitleText' },
            { components: toolCom.c_input_item, configNme: 'ms3ButtonText' },
          );
        }
        // Info Style 4 (Index 3)
        if (this.configObj.styleConfig.tabVal == 3) {
          tempArr.push({
            components: toolCom.c_menu_list,
            configNme: 'rightEntryConfig',
          });
        }
        this.rCom = arr.concat(tempArr);
      } else {
        // Style Settings
        let styleArr = [
          {
            components: toolCom.c_title,
            configNme: 'infoStyleText',
          },
          {
            components: toolCom.c_bg_color,
            configNme: 'nameColor',
          },
          {
            components: toolCom.c_slider,
            configNme: 'nameSize',
          },
          {
            components: toolCom.c_bg_color,
            configNme: 'numColor',
          },
          {
            components: toolCom.c_slider,
            configNme: 'numSize',
          },

          {
            components: toolCom.c_title,
            configNme: 'iconStyleText',
          },
          ...(this.configObj.assetMode.tabVal === 0
            ? [
                {
                  components: toolCom.c_bg_color,
                  configNme: 'dataTitleColor',
                },
                {
                  components: toolCom.c_bg_color,
                  configNme: 'dataNumColor',
                },
              ]
            : [
                {
                  components: toolCom.c_bg_color,
                  configNme: 'assetIconColor',
                },
                {
                  components: toolCom.c_slider,
                  configNme: 'assetIconSize',
                },
                {
                  components: toolCom.c_bg_color,
                  configNme: 'assetTextColor',
                },
                {
                  components: toolCom.c_slider,
                  configNme: 'assetTextSize',
                },
              ]),

          {
            components: toolCom.c_title,
            configNme: 'memberStyleText',
          },
          {
            components: toolCom.c_common_style,
            configNme: 'c_common_style',
          },
        ];
        const memberStyleIndex = styleArr.findIndex((item) => item.configNme === 'memberStyleText');
        if (this.configObj.memberStyleConfig.tabVal !== 2 && this.configObj.memberStyleConfig.tabVal !== 3) {
          styleArr.splice(memberStyleIndex + 1, 0, {
            components: toolCom.c_bg_color,
            configNme: 'cardBgColor',
          });
        }
        // Style 4 Module Styles
        if (this.configObj.styleConfig.tabVal == 3) {
          let assetConfigIndex = styleArr.findIndex((item) => item.configNme === 'assetConfigText');
          if (assetConfigIndex !== -1) {
            styleArr.splice(assetConfigIndex, 0, {
              components: toolCom.c_title,
              configNme: 'moduleStyleText',
            });
            styleArr.splice(assetConfigIndex + 1, 0, {
              components: toolCom.c_bg_color,
              configNme: 'moduleBgColor',
            });
            styleArr.splice(assetConfigIndex + 2, 0, {
              components: toolCom.c_bg_color,
              configNme: 'moduleTextColor',
            });
            styleArr.splice(assetConfigIndex + 3, 0, {
              components: toolCom.c_fillet,
              configNme: 'moduleRadius',
            });
          }
          styleArr.push({
            components: toolCom.c_fillet,
            configNme: 'cardBgRadius',
          });
        }
        // Member Style 2 Styles
        if (this.configObj.memberStyleConfig.tabVal == 1) {
          let ms2StyleArr = [];
          if (this.configObj.ms2TitleType.tabVal == 0) {
            ms2StyleArr.push({
              components: toolCom.c_bg_color,
              configNme: 'ms2TitleColor',
            });
          }
          ms2StyleArr.push(
            {
              components: toolCom.c_bg_color,
              configNme: 'ms2IntroColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'ms2RightsColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'ms2ExplainColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'ms2ButtonBgColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'ms2ButtonColor',
            },
            {
              components: toolCom.c_fillet,
              configNme: 'cardBgRadius',
            },
          );
          let memberStyleIndex = styleArr.findIndex((item) => item.configNme === 'memberStyleText');
          if (memberStyleIndex !== -1) {
            styleArr.splice(memberStyleIndex + 1, 0, ...ms2StyleArr);
          }
        }
        // Member Style 3 Styles
        if (this.configObj.memberStyleConfig.tabVal == 2) {
          let memberStyleIndex = styleArr.findIndex((item) => item.configNme === 'memberStyleText');
          if (memberStyleIndex !== -1) {
            styleArr.splice(memberStyleIndex + 1, 0, {
              components: toolCom.c_radio,
              configNme: 'ms3BgMode',
            });
            if (this.configObj.ms3BgMode && this.configObj.ms3BgMode.tabVal === 0) {
              styleArr.splice(memberStyleIndex + 2, 0, {
                components: toolCom.c_bg_color,
                configNme: 'cardBgColor',
              });
            } else {
              styleArr.splice(memberStyleIndex + 2, 0, {
                components: toolCom.c_upload_img,
                configNme: 'ms3BackgroundImage',
              });
            }

            let ms3StyleArr = [
              {
                components: toolCom.c_bg_color,
                configNme: 'ms3TitleColor',
              },
              {
                components: toolCom.c_bg_color,
                configNme: 'ms3ButtonColor',
              },
              {
                components: toolCom.c_margin_style,
                configNme: 'ms3PaddingConfig',
              },
              {
                components: toolCom.c_fillet,
                configNme: 'cardBgRadius',
              },
            ];
            styleArr.splice(memberStyleIndex + 3, 0, ...ms3StyleArr);
          }
        }
        // Member Style 4 Styles
        if (this.configObj.memberStyleConfig.tabVal == 3) {
          let memberStyleIndex = styleArr.findIndex((item) => item.configNme === 'memberStyleText');
          if (memberStyleIndex !== -1) {
            styleArr.splice(memberStyleIndex + 1, 0, {
              components: toolCom.c_radio,
              configNme: 'ms4BgMode',
            });
            if (this.configObj.ms4BgMode && this.configObj.ms4BgMode.tabVal === 0) {
              styleArr.splice(memberStyleIndex + 2, 0, {
                components: toolCom.c_bg_color,
                configNme: 'cardBgColor',
              });
            } else {
              styleArr.splice(memberStyleIndex + 2, 0, {
                components: toolCom.c_upload_img,
                configNme: 'ms4BackgroundImage',
              });
            }
          }
        }
        if (
          this.configObj.styleConfig.tabVal == 0 ||
          this.configObj.styleConfig.tabVal == 2 ||
          this.configObj.styleConfig.tabVal == 4
        ) {
          let isMenuIcon = this.configObj.menuConfig && this.configObj.menuConfig.listStyle == 1;
          let isShortcutIcon = this.configObj.shortcutConfig && this.configObj.shortcutConfig.listStyle == 1;
          if (isMenuIcon || isShortcutIcon) {
            styleArr.splice(6, 0, {
              components: toolCom.c_icon_style,
              configNme: 'iconStyleConfig',
            });
          }
        }
        this.rCom = arr.concat(styleArr);
      }
    },
    patchConfig(data) {
      if (!data) return data;
      if (!data.assetIconColor) {
        this.$set(data, 'assetIconColor', {
          title: '图标颜色',
          default: [{ item: '#fff' }],
          color: [{ item: '#fff' }],
        });
      }
      if (!data.assetIconSize) {
        this.$set(data, 'assetIconSize', {
          title: '图标大小',
          val: 24,
          min: 10,
          max: 32,
        });
      }
      if (!data.assetTextColor) {
        this.$set(data, 'assetTextColor', {
          title: '文字颜色',
          default: [{ item: '#fff' }],
          color: [{ item: '#fff' }],
        });
      }
      if (!data.assetTextSize) {
        this.$set(data, 'assetTextSize', {
          title: '文字大小',
          val: 12,
          min: 10,
          max: 32,
        });
      }
      if (data.styleConfig && data.styleConfig.tabList.length < 5) {
        data.styleConfig.tabList.push({ name: '样式五' });
      }
      if (!data.nameColor) {
        this.$set(data, 'nameColor', {
          title: '昵称颜色',
          default: [{ item: '#fff' }],
          color: [{ item: '#fff' }],
        });
      }
      if (!data.nameSize) {
        this.$set(data, 'nameSize', {
          title: '昵称文字',
          val: 16,
          min: 10,
          max: 30,
        });
      }
      if (!data.numColor) {
        this.$set(data, 'numColor', {
          title: 'ID/手机号',
          default: [{ item: '#fff' }],
          color: [{ item: '#fff' }],
        });
      }
      if (!data.numSize) {
        this.$set(data, 'numSize', {
          title: '文字大小',
          val: 10,
          min: 10,
          max: 30,
        });
      }
      if (!data.userInfoConfig) {
        this.$set(data, 'userInfoConfig', {
          title: '用户信息',
          tabVal: 0,
          tabList: [{ name: '手机号' }, { name: 'ID' }],
        });
      }
      if (!data.memberStyleConfig) {
        this.$set(data, 'memberStyleConfig', {
          title: '会员样式',
          tabVal: 0,
          tabList: [{ name: '样式一' }, { name: '样式二' }, { name: '样式三' }, { name: '样式四' }],
        });
      }
      if (!data.iconStyleConfig) {
        this.$set(data, 'iconStyleConfig', {
          title: '图标样式',
          name: 'iconStyleConfig',
          type: 1,
          color: {
            title: '颜色',
            default: [{ item: '#fff' }],
            color: [{ item: '#fff' }],
          },
          size: {
            title: '大小',
            val: 20,
            min: 12,
            max: 100,
          },
          padding: {
            title: '内边距',
            val: 0,
            min: 0,
            max: 100,
          },
          rotate: {
            title: '旋转',
            val: 0,
            min: 0,
            max: 360,
          },
        });
      }
      if (!data.dataTitleColor) {
        this.$set(data, 'dataTitleColor', {
          title: '标题颜色',
          default: [{ item: '#fff' }],
          color: [{ item: '#fff' }],
        });
      }
      if (!data.dataNumColor) {
        this.$set(data, 'dataNumColor', {
          title: '数字颜色',
          default: [{ item: '#fff' }],
          color: [{ item: '#fff' }],
        });
      }
      if (!data.zIndexConfig) {
        this.$set(data, 'zIndexConfig', {
          title: '组件上浮',
          val: 0,
          min: 0,
        });
      }
      if (!data.componentBgConfig) {
        this.$set(data, 'componentBgConfig', {
          title: '背景设置',
          tabVal: 0,
          tabList: [{ name: '颜色' }, { name: '图片' }],
          colorConfig: {
            title: '背景颜色',
            default: [{ item: '#fff' }, { item: '#fff' }],
            color: [{ item: '#fff' }, { item: '#fff' }],
          },
          colorDirection: {
            title: '渐变方向',
            tabVal: 0,
            tabList: [{ name: '横向' }, { name: '纵向' }, { name: '左斜' }, { name: '右斜' }],
          },
          imageConfig: {
            header: '背景图片',
            title: '',
            name: '上传图片',
            type: 'code',
            url: '',
            info: '建议尺寸：750px * 400px',
          },
        });
      }
      if (!data.borderConfig) {
        this.$set(data, 'borderConfig', {
          title: '边框设置',
          tabVal: 0,
          tabList: [{ name: '隐藏' }, { name: '显示' }],
          val: 0,
          styleConfig: {
            title: '边框样式',
            tabVal: 0,
            tabList: [
              { name: '实线', style: 'solid' },
              { name: '虚线', style: 'dashed' },
              { name: '点状', style: 'dotted' },
            ],
          },
          widthConfig: {
            title: '边框粗细',
            val: 1,
            min: 1,
          },
          colorConfig: {
            title: '边框颜色',
            default: [{ item: '#e5e5e5' }],
            color: [{ item: '#e5e5e5' }],
          },
        });
      }
      if (data.borderConfig && data.borderConfig.styleConfig && !data.borderConfig.styleConfig.tabList) {
        this.$set(data.borderConfig, 'styleConfig', {
          title: '边框样式',
          tabVal: 0,
          tabList: [
            { name: '实线', style: 'solid' },
            { name: '虚线', style: 'dashed' },
            { name: '点状', style: 'dotted' },
          ],
        });
      }
      if (!data.shadowConfig) {
        this.$set(data, 'shadowConfig', {
          title: '阴影设置',
          tabVal: 0,
          tabList: [{ name: '隐藏' }, { name: '显示' }],
          val: 0,
          colorConfig: {
            title: '阴影颜色',
            default: [{ item: 'rgba(0,0,0,0.1)' }],
            color: [{ item: 'rgba(0,0,0,0.1)' }],
          },
          xConfig: {
            title: 'X轴偏移',
            val: 0,
            min: -50,
          },
          yConfig: {
            title: 'Y轴偏移',
            val: 0,
            min: -50,
          },
          blurConfig: {
            title: '模糊半径',
            val: 10,
            min: 0,
          },
          spreadConfig: {
            title: '扩展半径',
            val: 0,
            min: -50,
          },
        });
      }
      if (!data.menuConfig) {
        this.$set(data, 'menuConfig', {
          title: '操作内容设置',
          listStyleName: '展示样式',
          bnt: '添加',
          type: 1,
          listStyle: 0,
          maxList: 2,
          list: [
            {
              img: '',
              type: 0,
              show: true,
              icon: '',
              info: [
                {
                  title: '标题',
                  value: '标题',
                  tips: '选填，不超过4个字',
                  max: 4,
                },
                {
                  title: '链接',
                  value: '',
                  tips: '请输入链接',
                  max: 100,
                },
              ],
            },
            {
              img: '',
              type: 0,
              show: true,
              icon: '',
              info: [
                {
                  title: '标题',
                  value: '标题',
                  tips: '选填，不超过4个字',
                  max: 4,
                },
                {
                  title: '链接',
                  value: '',
                  tips: '请输入链接',
                  max: 100,
                },
              ],
            },
          ],
        });
      }

      if (!data.assetMode) {
        this.$set(data, 'assetMode', {
          title: '展示模式',
          tabVal: 0,
          tabList: [{ name: '数据展示' }, { name: '图文展示' }],
        });
      }
      if (!data.dataStyle) {
        this.$set(data, 'dataStyle', {
          title: '数据布局',
          tabVal: 0,
          tabList: [{ name: '数字-文字(纵)' }, { name: '文字-数字(横)' }, { name: '文字-数字(纵)' }],
        });
      }
      if (!data.checkboxInfo) {
        this.$set(data, 'checkboxInfo', {
          title: '数据选择',
          name: 'checkboxInfo',
          maxList: 5,
          type: [1, 2, 3],
          list: [
            { id: 1, name: '余额' },
            { id: 2, name: '积分' },
            { id: 3, name: '优惠券' },
            { id: 4, name: '收藏' },
            { id: 5, name: '浏览记录' },
          ],
        });
      }
      if (!data.ms3BgMode) {
        this.$set(data, 'ms3BgMode', {
          title: '背景设置',
          tabVal: 0,
          tabList: [
            { name: '背景颜色', val: 0 },
            { name: '背景图片', val: 1 },
          ],
        });
      }
      if (!data.ms3BgColor) {
        this.$set(data, 'ms3BgColor', {
          title: '背景颜色',
          default: [{ item: '#fff' }, { item: '#fff' }],
          color: [{ item: '#fff' }, { item: '#fff' }],
        });
      }
      if (!data.ms3BackgroundImage) {
        this.$set(data, 'ms3BackgroundImage', {
          title: '',
          url: '',
        });
      }
      if (!data.ms4BgMode) {
        this.$set(data, 'ms4BgMode', {
          title: '背景设置',
          tabVal: 0,
          tabList: [
            { name: '背景颜色', val: 0 },
            { name: '背景图片', val: 1 },
          ],
        });
      }
      if (!data.ms4BgColor) {
        this.$set(data, 'ms4BgColor', {
          title: '背景颜色',
          default: [{ item: '#fff' }, { item: '#fff' }],
          color: [{ item: '#fff' }, { item: '#fff' }],
        });
      }
      if (!data.ms4BackgroundImage) {
        this.$set(data, 'ms4BackgroundImage', {
          title: '',
          url: '',
        });
      }

      if (!data.assetConfig) {
        this.$set(data, 'assetConfig', {
          title: '快捷入口',
          listStyle: 0,
          maxList: 5,
          bnt: '添加',
          list: [
            {
              img: '',
              icon: 'icon-yue',
              info: [
                { title: '标题', value: '余额', tips: '选填，不超过4个字', max: 4 },
                { title: '链接', value: '/pages/users/user_money/index', tips: '请输入链接', max: 100 },
              ],
            },
            {
              img: '',
              icon: 'icon-jifen',
              info: [
                { title: '标题', value: '积分', tips: '选填，不超过4个字', max: 4 },
                { title: '链接', value: '/pages/users/user_integral/index', tips: '请输入链接', max: 100 },
              ],
            },
            {
              img: '',
              icon: 'icon-youhuiquan',
              info: [
                { title: '标题', value: '优惠券', tips: '选填，不超过4个字', max: 4 },
                { title: '链接', value: '/pages/users/user_coupon/index', tips: '请输入链接', max: 100 },
              ],
            },
            {
              img: '',
              icon: 'icon-shoucang',
              info: [
                { title: '标题', value: '收藏', tips: '选填，不超过4个字', max: 4 },
                { title: '链接', value: '/pages/users/user_goods_collection/index', tips: '请输入链接', max: 100 },
              ],
            },
            {
              img: '',
              icon: 'icon-zuji',
              info: [
                { title: '标题', value: '浏览记录', tips: '选填，不超过4个字', max: 4 },
                { title: '链接', value: '/pages/users/user_visit/index', tips: '请输入链接', max: 100 },
              ],
            },
          ],
        });
      }

      if (!data.paddingConfig) {
        this.$set(data, 'paddingConfig', {
          isAll: false,
          title: '内边距',
          val: 15,
          min: 0,
          max: 100,
          valList: [{ val: 15 }, { val: 15 }, { val: 15 }, { val: 15 }],
        });
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
      }
      if (!data.rightEntryConfig) {
        this.$set(data, 'rightEntryConfig', {
          title: '右侧入口',
          listStyleName: '展示样式',
          bnt: '添加',
          type: 1,
          listStyle: -1,
          maxList: 1,
          list: [
            {
              img: '',
              type: 0,
              show: true,
              icon: '',
              info: [
                {
                  title: '标题',
                  value: '积分商城',
                  tips: '选填，不超过4个字',
                  max: 4,
                },
                {
                  title: '描述',
                  value: '积分可换好物',
                  tips: '选填，不超过6个字',
                  max: 6,
                },
                {
                  title: '链接',
                  value: '',
                  tips: '请输入链接',
                  max: 100,
                },
              ],
            },
          ],
        });
      }
      if (!data.leftMenuConfig) {
        this.$set(data, 'leftMenuConfig', {
          title: '左侧内容',
          listStyleName: '展示样式',
          bnt: '添加',
          type: 1,
          listStyle: 1,
          maxList: 3,
          list: [
            {
              img: '',
              type: 0,
              show: true,
              icon: 'icon-yue',
              info: [
                {
                  title: '标题',
                  value: '余额',
                  tips: '选填，不超过4个字',
                  max: 4,
                },
                {
                  title: '链接',
                  value: '',
                  tips: '请输入链接',
                  max: 100,
                },
              ],
            },
            {
              img: '',
              type: 0,
              show: true,
              icon: 'icon-jifen',
              info: [
                {
                  title: '标题',
                  value: '积分',
                  tips: '选填，不超过4个字',
                  max: 4,
                },
                {
                  title: '链接',
                  value: '',
                  tips: '请输入链接',
                  max: 100,
                },
              ],
            },
            {
              img: '',
              type: 0,
              show: true,
              icon: 'icon-youhuiquan',
              info: [
                {
                  title: '标题',
                  value: '优惠券',
                  tips: '选填，不超过4个字',
                  max: 4,
                },
                {
                  title: '链接',
                  value: '',
                  tips: '请输入链接',
                  max: 100,
                },
              ],
            },
          ],
        });
      }
      if (!data.memberConfig) {
        this.$set(data, 'memberConfig', {
          listStyleName: '展示样式',
          bnt: '添加',
          type: 1,
          listStyle: -1,
          maxList: 2,
          list: [
            {
              img: '',
              type: 0,
              show: true,
              icon: '',
              info: [
                {
                  title: '标题',
                  value: '会员中心',
                  tips: '选填，不超过4个字',
                  max: 4,
                },
                {
                  title: '描述',
                  value: '查看新权益',
                  tips: '选填，不超过6个字',
                  max: 6,
                },
                {
                  title: '链接',
                  value: '',
                  tips: '请输入链接',
                  max: 100,
                },
              ],
            },
            {
              img: '',
              type: 0,
              show: true,
              icon: '',
              info: [
                {
                  title: '标题',
                  value: '积分商城',
                  tips: '选填，不超过4个字',
                  max: 4,
                },
                {
                  title: '描述',
                  value: '限量兑神券',
                  tips: '选填，不超过6个字',
                  max: 6,
                },
                {
                  title: '链接',
                  value: '',
                  tips: '请输入链接',
                  max: 100,
                },
              ],
            },
          ],
        });
      }

      // Member Style 2 Configs
      if (!data.ms2TitleType) {
        this.$set(data, 'ms2TitleType', {
          title: '标题类型',
          tabVal: 0,
          tabList: [{ name: '文字' }, { name: '图片' }],
        });
      }
      if (!data.ms2TitleText) {
        this.$set(data, 'ms2TitleText', {
          title: '标题文字',
          value: 'SVIP 会员',
          max: 10,
        });
      }
      if (!data.ms2TitleColor) {
        this.$set(data, 'ms2TitleColor', {
          title: '标题颜色',
          default: [{ item: '#8B572A' }],
          color: [{ item: '#8B572A' }],
        });
      }
      if (!data.ms2TitleImage) {
        this.$set(data, 'ms2TitleImage', {
          header: '',
          title: '',
          name: '标题图片',
          type: 'code',
          url: '',
          info: '建议尺寸：162px * 36px',
        });
      }
      if (!data.ms2IntroText) {
        this.$set(data, 'ms2IntroText', {
          title: '简介文字',
          value: '商城购物可享98折',
          max: 20,
        });
      }
      if (!data.ms2IntroColor) {
        this.$set(data, 'ms2IntroColor', {
          title: '简介颜色',
          default: [{ item: '#8B572A' }],
          color: [{ item: '#8B572A' }],
        });
      }
      if (!data.ms2RightsList) {
        this.$set(data, 'ms2RightsList', {
          title: '权益图标',
          listStyleName: '建议：40px*40px；鼠标拖拽版块可调整图片顺序',
          bnt: '添加',
          type: 1,
          listStyle: -1,
          maxList: 2,
          list: [
            {
              img: '',
              icon: 'icon-zk',
              info: [
                { title: '标题', value: '购物折扣', tips: '选填，不超过4个字', max: 6 },
                { title: '链接', value: '', tips: '请输入链接', max: 100 },
              ],
            },
            {
              img: '',
              icon: 'icon-mz',
              info: [
                { title: '标题', value: '专属徽章', tips: '选填，不超过4个字', max: 6 },
                { title: '链接', value: '', tips: '请输入链接', max: 100 },
              ],
            },
          ],
        });
      }
      if (!data.ms2ExplainIcons) {
        this.$set(data, 'ms2ExplainIcons', {
          header: '',
          title: '',
          name: '说明图片',
          type: 'code',
          url: '',
          delType: 1,
          info: '建议：94px * 32px',
        });
      }
      if (!data.ms2ExplainText) {
        this.$set(data, 'ms2ExplainText', {
          title: '说明文字',
          value: '掌握更多快速升级技巧',
          max: 20,
        });
      }
      if (!data.ms2ExplainColor) {
        this.$set(data, 'ms2ExplainColor', {
          title: '说明颜色',
          default: [{ item: '#8B572A' }],
          color: [{ item: '#8B572A' }],
        });
      }
      if (!data.ms2ButtonText) {
        this.$set(data, 'ms2ButtonText', {
          title: '按钮文字',
          value: '去获取',
          max: 6,
        });
      }
      if (!data.ms2ButtonLink) {
        this.$set(data, 'ms2ButtonLink', {
          title: '按钮链接',
          value: '/pages/users/user_vip/index',
          max: 100,
          type: 'link',
        });
      }
      if (!data.ms2ButtonColor) {
        this.$set(data, 'ms2ButtonColor', {
          title: '文字颜色',
          default: [{ item: '#5A350C' }],
          color: [{ item: '#5A350C' }],
        });
      }
      if (!data.ms2ButtonBgColor) {
        this.$set(data, 'ms2ButtonBgColor', {
          title: '背景颜色',
          default: [{ item: '#F6D99D' }],
          color: [{ item: '#F6D99D' }],
        });
      }
      if (!data.ms2RightsColor) {
        this.$set(data, 'ms2RightsColor', {
          title: '权益文字',
          default: [{ item: '#8B572A' }],
          color: [{ item: '#8B572A' }],
        });
      }

      if (!data.moduleStyleText) {
        this.$set(data, 'moduleStyleText', '模块样式');
      }
      if (!data.moduleBgColor) {
        this.$set(data, 'moduleBgColor', {
          title: '模块背景',
          default: [{ item: '#fff' }, { item: '#fff' }],
          color: [{ item: '#fff' }, { item: '#fff' }],
        });
      }
      if (!data.moduleTextColor) {
        this.$set(data, 'moduleTextColor', {
          title: '模块文字',
          default: [{ item: '#333' }],
          color: [{ item: '#333' }],
        });
      }
      if (!data.moduleRadius) {
        this.$set(data, 'moduleRadius', {
          title: '模块圆角',
          type: 0,
          list: [
            { val: '全部', icon: 'iconcaozuo-zhengti' },
            { val: '单个', icon: 'iconcaozuo-bianjiao' },
          ],
          valName: '圆角值',
          val: 8,
          min: 0,
          max: 100,
          valList: [{ val: 8 }, { val: 8 }, { val: 8 }, { val: 8 }],
        });
      }
      if (!data.cardBgColor) {
        this.$set(data, 'cardBgColor', {
          title: '会员背景色',
          default: [{ item: '#fff' }, { item: '#fff' }],
          color: [{ item: '#fff' }, { item: '#fff' }],
        });
      }
      if (!data.cardBgRadius) {
        this.$set(data, 'cardBgRadius', {
          title: '会员背景圆角',
          type: 0,
          list: [
            { val: '全部', icon: 'iconcaozuo-zhengti' },
            { val: '单个', icon: 'iconcaozuo-bianjiao' },
          ],
          valName: '圆角值',
          val: 10,
          min: 0,
          max: 100,
          valList: [{ val: 10 }, { val: 10 }, { val: 10 }, { val: 10 }],
        });
      }
      // Member Style 3 Configs
      if (!data.ms3TitleText) {
        this.$set(data, 'ms3TitleText', {
          title: '说明文字',
          value: '开通会员，尊享更多权益',
          max: 20,
        });
      }
      if (!data.ms3TitleColor) {
        this.$set(data, 'ms3TitleColor', {
          title: '说明颜色',
          default: [{ item: '#333' }],
          color: [{ item: '#333' }],
        });
      }
      if (!data.ms3ButtonText) {
        this.$set(data, 'ms3ButtonText', {
          title: '按钮文字',
          value: '立即开通',
          max: 10,
        });
      }
      if (!data.ms3ButtonColor) {
        this.$set(data, 'ms3ButtonColor', {
          title: '按钮颜色',
          default: [{ item: '#e93323' }],
          color: [{ item: '#e93323' }],
        });
      }
      if (!data.ms3PaddingConfig) {
        this.$set(data, 'ms3PaddingConfig', {
          title: '内边距',
          val: 10,
          min: 0,
          max: 100,
          valList: [{ val: 10 }, { val: 10 }, { val: 10 }, { val: 10 }],
        });
      }
      if (!data.ms3BackgroundImage) {
        this.$set(data, 'ms3BackgroundImage', {
          header: '',
          title: '',
          name: '上传图片',
          type: 'code',
          url: '',
          info: '建议：宽662px*92px',
        });
      }
      if (!data.ms4BackgroundImage) {
        this.$set(data, 'ms4BackgroundImage', {
          header: '',
          title: '',
          name: '上传图片',
          type: 'code',
          url: '',
          info: '建议：750px * 200px',
        });
      }
      return data;
    },
    // 获取组件参数
    getConfig(data) {},
  },
};
</script>
