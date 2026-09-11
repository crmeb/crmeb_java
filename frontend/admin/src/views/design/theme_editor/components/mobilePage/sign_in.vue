<template>
  <common_wrapper :config="configObj">
    <div
      class="signIn"
      :style="{
        background: ``,
        borderRadius: bgRadius,
      }"
    >
      <div class="signInBg acea-row row-middle row-around" v-if="styleConfig == 0">
        <div class="item">
          <img :src="gift4Img" />
          <div>今天</div>
        </div>
        <div class="item">
          <img :src="pointsImg" />
          <div>周二</div>
        </div>
        <div class="item">
          <img :src="pointsImg" />
          <div>周三</div>
        </div>
        <div class="item">
          <img :src="gift3Img" />
          <div>周四</div>
        </div>
        <div class="item">
          <img :src="gift2Img" />
          <div>周五</div>
        </div>
        <div class="item">
          <img :src="pointsImg" />
          <div>周六</div>
        </div>
        <div class="item gift">
          <img :src="giftImg" />
          <div>周日</div>
        </div>
        <div
          class="bnt"
          :style="{
            color: toneConfig ? bntTxtColor : '#fff',
            background: toneConfig ? `linear-gradient(90deg,${bntBgColorRight} 0%,${bntBgColorLeft} 100%)` : themeColor,
          }"
        >
          签到
        </div>
      </div>
      <div class="signInBg on acea-row row-between-wrapper" v-else>
        <div class="acea-row row-middle">
          <div class="pictrue">
            <img :src="signInGiftImg" />
          </div>
          <div>
            <div class="acea-row row-middle">
              <span class="name">签到立即获取</span>
              <div
                class="points acea-row row-center-wrapper"
                :style="{
                  color: toneConfig ? labelTxtColor : colorStyle.theme,
                  background: toneConfig ? labelBgColor : colorStyle.theme,
                }"
              >
                <div
                  class="pointsBg acea-row row-center-wrapper"
                  :style="{
                    background: toneConfig ? '' : 'rgba(255,255,255,0.9)',
                  }"
                >
                  <img :src="pointsImg" />
                  <span>+20</span>
                </div>
              </div>
            </div>
            <div class="tips">连续签到3天，额外活动15积分</div>
          </div>
        </div>
        <div
          class="bnt"
          :style="{
            color: toneConfig ? bntTxtColor : '#fff',
            background: toneConfig ? `linear-gradient(90deg,${bntBgColorRight} 0%,${bntBgColorLeft} 100%)` : themeColor,
          }"
        >
          立即签到
        </div>
      </div>
    </div>
  </common_wrapper>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue';
import { useMobildConfigStore } from '@/store/modules/mobildConfig';
import gift2Img from '@/views/design/theme_editor/assets/images/gift2.png';
import gift3Img from '@/views/design/theme_editor/assets/images/gift3.png';
import gift4Img from '@/views/design/theme_editor/assets/images/gift4.png';
import giftImg from '@/views/design/theme_editor/assets/images/gift.png';
import pointsImg from '@/views/design/theme_editor/assets/images/points.png';
import signInGiftImg from '@/views/design/theme_editor/assets/images/signInGift.png';

defineOptions({
  name: 'sign_in',
  cname: '签到',
  icon: '#iconzujian-qiandao',
  configName: 'c_sign_in',
  type: 1,
  defaultName: 'signIn',
});

const props = defineProps({
  index: {
        type: null,
        default: -1,
      },
      num: {
        type: null,
      },
      colorStyle: {
        type: null,
      },
});


const mobildConfigStore = useMobildConfigStore();

const defaultConfig = {
        cname: '签到',
        name: 'signIn',
        timestamp: props.num,
        isHide: false,
        setUp: {
          tabVal: 0,
        },
        titleLeft: '展示设置',
        titleRight: '签到样式',
        titleCurrency: '通用样式',
        styleConfig: {
          title: '选择风格',
          tabVal: 0,
          type: 'signIn',
        },
        toneConfig: {
          title: '色调',
          tabVal: 0,
          tabList: [
            {
              name: '跟随主题风格',
            },
            {
              name: '自定义',
            },
          ],
        },
        bntBgColor: {
          title: '按钮背景',
          name: 'bntBgColor',
          default: [
            {
              item: '#FF7931',
            },
            {
              item: '#E93323',
            },
          ],
          color: [
            {
              item: '#FF7931',
            },
            {
              item: '#E93323',
            },
          ],
        },
        bntTxtColor: {
          title: '按钮文字',
          name: 'bntTxtColor',
          default: [
            {
              item: '#FFF',
            },
          ],
          color: [
            {
              item: '#FFF',
            },
          ],
        },
        labelBgColor: {
          title: '标签背景',
          name: 'labelBgColor',
          default: [
            {
              item: '#FCEAE9',
            },
          ],
          color: [
            {
              item: '#FCEAE9',
            },
          ],
        },
        labelTxtColor: {
          title: '标签文字',
          name: 'labelBgColor',
          default: [
            {
              item: '#E93323',
            },
          ],
          color: [
            {
              item: '#E93323',
            },
          ],
        },
        bottomBgColor: {
          title: '底部背景',
          name: 'bgColor',
          default: [
            {
              item: '#f5f5f5',
            },
          ],
          color: [
            {
              item: '#f5f5f5',
            },
          ],
        },
        paddingConfig: {
          title: '内边距',
          val: 12,
          min: 0,
          isAll: false,
          max: 100,
          valList: [{ val: 12 }, { val: 12 }, { val: 12 }, { val: 12 }],
        },
        marginConfig: {
          title: '外边距',
          val: 0,
          min: 0,
          isAll: false,
          max: 100,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        },
        fillet: {
          title: '背景圆角',
          type: 0,
          list: [
            {
              val: '全部',
              icon: 'iconcaozuo-zhengti',
            },
            {
              val: '单个',
              icon: 'iconcaozuo-bianjiao',
            },
          ],
          valName: '圆角值',
          val: 0,
          min: 0,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        },
        zIndexConfig: {
          title: '组件上浮',
          val: 0,
          min: 0,
        },
        componentBgConfig: {
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
        },
        borderConfig: {
          title: '边框设置',
          tabVal: 0,
          tabList: [{ name: '隐藏' }, { name: '显示' }],
          val: 0, // 0: Hide, 1: Show
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
        },
        shadowConfig: {
          title: '阴影设置',
          tabVal: 0,
          tabList: [{ name: '隐藏' }, { name: '显示' }],
          val: 0, // 0: Off, 1: On
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
        },
      };

const pageData = ref({});
const configObj = ref(null);
const bottomConfig = ref('');
const styleConfig = ref(0);
const toneConfig = ref(0);
const bntBgColorLeft = ref('');
const bntBgColorRight = ref('');
const bntTxtColor = ref('');
const labelBgColor = ref('');
const labelTxtColor = ref('');
const bgColorLeft = ref('');
const bgColorRight = ref('');
const bgColorLeft2 = ref('');
const bgColorRight2 = ref('');
const mbConfig = ref(0);
const bgRadius = ref(0);
const themeColor = ref('');
const zIndexConfig = ref(null);
const componentBgConfig = ref(null);
const borderConfig = ref(null);
const shadowConfig = ref(null);

function setConfig(data) {
  if (!data) return;
        let dataClone = JSON.parse(JSON.stringify(data));
        for (let key in defaultConfig) {
          if (dataClone[key] == undefined) {
            dataClone[key] = JSON.parse(JSON.stringify(defaultConfig[key]));
          }
        }

        if (!dataClone.paddingConfig) {
          dataClone.paddingConfig = {
            title: '内边距',
            val: 0,
            min: 0,
            max: 100,
            isAll: false,
            valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
          };
          if (dataClone.topConfig) dataClone.paddingConfig.valList[0].val = dataClone.topConfig.val;
          if (dataClone.prConfig) {
            dataClone.paddingConfig.valList[1].val = dataClone.prConfig.val;
            dataClone.paddingConfig.valList[3].val = dataClone.prConfig.val;
          }
          if (dataClone.bottomConfig) dataClone.paddingConfig.valList[2].val = dataClone.bottomConfig.val;
        }

        if (!dataClone.marginConfig) {
          dataClone.marginConfig = {
            title: '外边距',
            val: 0,
            min: 0,
            max: 100,
            isAll: false,
            valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
          };
          if (dataClone.mbConfig) dataClone.marginConfig.valList[0].val = dataClone.mbConfig.val;
        }

        configObj.value = dataClone;
        zIndexConfig.value = dataClone.zIndexConfig.val;
        componentBgConfig.value = dataClone.componentBgConfig;
        borderConfig.value = dataClone.borderConfig;
        shadowConfig.value = dataClone.shadowConfig;

        styleConfig.value = dataClone.styleConfig.tabVal;
        toneConfig.value = dataClone.toneConfig.tabVal;
        bntBgColorLeft.value = dataClone.bntBgColor.color[0].item;
        bntBgColorRight.value = dataClone.bntBgColor.color[1].item;
        bntTxtColor.value = dataClone.bntTxtColor.color[0].item;
        labelBgColor.value = dataClone.labelBgColor.color[0].item;
        labelTxtColor.value = dataClone.labelTxtColor.color[0].item;
        themeColor.value = `linear-gradient(90deg,${props.colorStyle.theme} 0%,${props.colorStyle.gradient} 100%)`;
        // bottomBgColor.value = data.bottomBgColor.color[0].item;
        let fillet = dataClone.fillet.type;
        let filletVal = dataClone.fillet.val;
        let valList = dataClone.fillet.valList;
        bgRadius.value = fillet
          ? valList[0].val + 'px ' + valList[1].val + 'px ' + valList[3].val + 'px ' + valList[2].val + 'px'
          : filletVal + 'px';
}

watch(
  pageData,
  (nVal, oVal) => {
    setConfig(nVal);
  },
  { deep: true },
);
watch(
  () => props.num,
  (nVal, oVal) => {
    let data = mobildConfigStore.defaultArray[nVal];
            setConfig(data);
  },
  { deep: true },
);
watch(
  () => mobildConfigStore.defaultArray,
  (nVal, oVal) => {
    let data = mobildConfigStore.defaultArray[props.num];
            setConfig(data);
  },
  { deep: true },
);

onMounted(() => {
  nextTick(() => {
        pageData.value = mobildConfigStore.defaultArray[props.num];
        setConfig(pageData.value);
      });
});

</script>
<style scoped lang="scss">
.signIn {
  width: 100%;
  // background: linear-gradient(90deg, #e93323 0%, #ff7931 100%);

  .signInBg {
    width: 100%;
    height: 70px;
    border-radius: 8px;
    // background: linear-gradient(90deg, #ffe8f5 0%, #f1fbfd 100%);
    padding: 0 3px;
    .item {
      text-align: center;
      font-size: 11px;
      color: #999999;
      padding-top: 5px;
      &.gift {
        padding-bottom: 3px;
        img {
          width: 32px;
          height: 32px;
          margin-bottom: 2px;
        }
      }
      img {
        width: 24px;
        height: 24px;
        display: block;
        margin-bottom: 6px;
      }
    }
    .bnt {
      width: 44px;
      height: 24px;
      background: linear-gradient(90deg, #e93323 0%, #ff7931 100%);
      border-radius: 12px;
      text-align: center;
      line-height: 24px;
      font-size: 12px;
      color: #fff;
    }
    &.on {
      background: #fff;
      padding: 0 10px;
      .bnt {
        width: 70px;
        height: 26px;
        background: linear-gradient(90deg, #e93323 0%, #ff7931 100%);
        border-radius: 13px;
        font-size: 12px;
        color: #ffffff;
        text-align: center;
        line-height: 26px;
      }
      .pictrue {
        width: 44px;
        height: 44px;
        margin-right: 10px;
        img {
          width: 100%;
          height: 100%;
        }
      }
      .name {
        color: #282828;
        font-size: 15px;
        font-weight: 600;
        line-height: 1;
      }
      .points {
        width: 40px;
        height: 16px;
        background: #fceae9;
        border-radius: 12px;
        font-size: 10px;
        color: #e93323;
        margin-left: 2px;

        .pointsBg {
          width: 100%;
          height: 100%;
        }

        img {
          width: 14px;
          height: 14px;
          display: block;
        }
      }
      .tips {
        color: #999999;
        font-size: 12px;
        margin-top: 6px;
      }
    }
  }
}
</style>
