<template>
  <div class="diy-page">
    <!-- 旧 Header 已迁移至新编辑模块顶部，移除该块 -->
    <el-card :bordered="false" shadow="never">
      <div class="diy-wrapper" :style="'height:' + clientHeight + 'px;'">
        <!-- 左侧 -->
        <div class="left">
          <div class="wrapper" :style="'height:' + clientHeight + 'px;'">
            <div class="list" v-for="(item, index) in leftMenu" :key="index">
              <div class="tips" @click="item.isOpen = !item.isOpen">
                {{ item.title }}
                <div class="iconfont iconyou" v-if="!item.isOpen"></div>
                <div class="iconfont iconxia" v-else></div>
              </div>
              <!-- 拖拽组件 -->
              <draggable
                class="dragArea list-group"
                :list="item.list"
                :group="{ name: 'people', pull: 'clone', put: false }"
                :clone="cloneDog"
                :item-key="getDraggableItemKey"
                dragClass="dragClass"
                filter=".search , .navbar , .homeComb , .service"
              >
                <template #item="{ element }">
                  <div
                    class="list-group-item"
                    :class="{
                      search: element.cname == '搜索框',
                      navbar: element.cname == '选项卡',
                      homeComb: element.cname == '轮播搜索',
                      service: element.cname == '悬浮按钮',
                    }"
                    @click="addDom(element, 1)"
                    v-show="item.isOpen"
                  >
                    <div>
                      <div class="position" style="display: none">释放鼠标将组建添加到此处</div>
                      <svg class="conter iconfont icon svg-icon" aria-hidden="true">
                        <use :xlink:href="element.icon"></use>
                      </svg>
                      <p class="conter">{{ element.cname }}</p>
                    </div>
                  </div>
                </template>
              </draggable>
            </div>
          </div>
        </div>
        <!-- 中间自定义配置移动端页面 -->
        <div class="wrapper-con">
          <div class="content">
            <div class="contxt">
              <div class="overflowy">
                <div class="picture"><img :src="electricImg" /></div>
                <div
                  v-if="pageType == 'home'"
                  class="page-title"
                  :class="{ on: activeIndex == -100 }"
                  @click="showTitle"
                >
                  {{ titleTxt }}
                  <div class="delete-box"></div>
                  <div class="handle"></div>
                </div>
              </div>
              <div class="scrollCon" :style="'height:' + rollHeight + 'px;'">
                <div style="width: 460px; margin: 0 auto">
                  <div
                    class="scroll-box"
                    :class="
                      picTxt && tabValTxt == 2
                        ? 'fullsize noRepeat'
                        : picTxt && tabValTxt == 1
                        ? 'repeat ysize'
                        : 'noRepeat ysize'
                    "
                    :style="
                      'background-color:' +
                      (colorTxt ? colorPickerTxt : '') +
                      ';background-image: url(' +
                      (picTxt ? picUrlTxt : '') +
                      ');min-height:' +
                      rollHeight +
                      'px;'
                    "
                    id="imgContainer"
                  >
                    <draggable
                      class="dragArea list-group"
                      :list="mConfig"
                      :item-key="getDraggableItemKey"
                      group="people"
                      @change="log"
                      filter=".top"
                      :move="onMove"
                      animation="300"
                    >
                      <template #item="{ element: item, index: key }">
                        <div
                          class="mConfig-item"
                          :class="{
                            on: activeIndex == key,
                            top: item.name == 'search_box' || item.name == 'nav_bar',
                            hide: defaultArrays[item.num] && defaultArrays[item.num].isHide,
                          }"
                          @click.stop="bindconfig(item, key)"
                          :style="
                            colorTxt
                              ? 'background-color:' + colorPickerTxt + ';'
                              : 'background-color: rgba(255,255,255, 0);'
                          "
                        >
                          <component
                            :is="resolveComp(item.name)"
                            ref="getComponentData"
                            :configData="propsObj"
                            :index="key"
                            :num="item.num"
                            :colorStyle="colorStyle"
                          ></component>
                          <div class="delete-box">
                            <div class="handleType">
                              <div
                                class="iconfont"
                                :class="defaultArrays[item.num] && defaultArrays[item.num].isHide ? 'iconyincang' : 'iconxianshi'"
                                @click.stop="bindHide(item)"
                              ></div>
                              <div class="iconfont iconshanchu3" @click.stop="bindDelete(item, key)"></div>
                              <div class="iconfont icona-fuzhi1" @click.stop="bindAddDom(item, 0, key)"></div>
                              <div
                                class="iconfont iconshang"
                                :class="key === 0 ? 'on' : ''"
                                @click.stop="movePage(item, key, 1)"
                              ></div>
                              <div
                                class="iconfont iconxia"
                                :class="key === mConfig.length - 1 ? 'on' : ''"
                                @click.stop="movePage(item, key, 0)"
                              ></div>
                            </div>
                          </div>
                          <div class="handle"></div>
                          <div class="delete-name" :class="{ on: activeIndex == key }">
                            <span
                              v-if="
                                item.name == 'home_custom_component' &&
                                defaultArrays[item.num] &&
                                defaultArrays[item.num].selectType
                              "
                              class="custom-badge"
                              >{{ getBadgeText(defaultArrays[item.num].selectType.activeValue) }}</span
                            >
                            {{ item.cname }}
                          </div>
                        </div>
                      </template>
                    </draggable>
                  </div>
                </div>
              </div>
              <div class="overflowy" v-if="pageType == 'home' && !isMicroPage">
                <div
                  class="page-foot"
                  @click="showFoot"
                  :class="{ on: activeIndex == -101 }"
                  :style="pageFooterType == 1 ? 'bottom:' + (50 + pageFooterBottom) + 'px' : ''"
                >
                  <footPage></footPage>
                  <div class="delete-box"></div>
                  <div class="handle"></div>
                </div>
              </div>
              <div class="overflowy" v-if="pageType == 'detail'">
                <div class="page-foot" @click="showBottomMenu" :class="{ on: activeIndex == -102 }">
                  <home_bottom_menu :colorStyle="colorStyle"></home_bottom_menu>
                  <div class="delete-box"></div>
                  <div class="handle"></div>
                </div>
              </div>
              <div class="defaultData" v-if="pageId !== 0">
                <!-- <div class="data" @click="setmoren">设置默认</div>
                <div class="data" @click="getmoren">恢复默认</div> -->
                <el-button class="data" @click="showTitle">页面设置</el-button>
                <el-button class="data" @click="nameModal = true">另存模版</el-button>
                <el-button class="data" @click="reast">重置</el-button>
                <el-button v-if="!isMicroPage" class="data" @click="saveCover">保存为封面</el-button>
              </div>
            </div>
          </div>
        </div>
        <!-- 右侧页面设置 -->
        <div class="right-box">
          <div class="mConfig-item" style="background-color: #fff" v-for="(item, key) in rConfig" :key="key">
            <!-- <div class="title-bar">{{ item.cname }}</div> -->
            <component
              v-if="canRenderRightConfig(item)"
              :is="resolveComp(item.configName)"
              :key="getRightConfigKey(item)"
              @config="config"
              :activeIndex="activeIndex"
              :num="item.num"
              :index="key"
            ></component>
          </div>
        </div>
      </div>
    </el-card>
    <el-dialog v-model="modal" width="540px" title="预览">
      <div>
        <div v-viewer class="acea-row row-around code">
          <div class="acea-row row-column-around row-between-wrapper">
            <div class="QRpic" ref="qrCodeUrl"></div>
            <span class="mt10">公众号二维码</span>
          </div>
          <div class="acea-row row-column-around row-between-wrapper">
            <div class="QRpic">
              <img v-lazy="qrcodeImg" />
            </div>
            <span class="mt10">小程序二维码</span>
          </div>
        </div>
      </div>
    </el-dialog>
    <el-dialog v-model="nameModal" width="470px" title="设置模版名称" :show-close="true">
      <el-input v-model="saveName" placeholder="请输入模版名称"></el-input>
      <template #footer>
        <span class="dialog-footer">
          <el-button v-db-click @click="nameModal = false">取 消</el-button>
          <el-button type="primary" v-db-click @click="saveModal">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {
  ref,
  reactive,
  computed,
  watch,
  inject,
  nextTick,
  onMounted,
  onBeforeUnmount,
  onUnmounted,
  getCurrentInstance,
} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from '@/utils/elementPlusFeedback';
import {
  categoryList as categoryListApi,
  themeInfo,
  themeSave,
  setDefault,
  recovery,
  diyUpdateName,
  getRoutineCode,
  saveThemeImage,
} from '@/api/theme';
import { fileUpload } from '@/api/theme';
import draggable from 'vuedraggable';
import { getDraggableItemKey } from '@/utils/draggableKey';
import mPage from '@/views/design/theme_editor/components/mobilePage/index.js';
import mobileConfigModule from '@/views/design/theme_editor/components/mobileConfig/index.js';
import home_bottom_menu from '@/views/design/theme_editor/components/mobilePage/home_bottom_menu.vue';

import footPage from '@/views/design/theme_editor/components/pagesFoot';
import { useMobildConfigStore } from '@/store/modules/mobildConfig';
import html2canvas from 'html2canvas';
import { getColorChange } from '@/api/theme';
import Setting from '@/utils/settingMer';
import QRCode from 'qrcodejs2';
import electricImg from '@/assets/images/electric.png';
import foot005Img from '@/assets/images/foot-005.png';
import foot006Img from '@/assets/images/foot-006.png';

defineOptions({ name: 'diyIndex' });

const props = defineProps({
  pageType: {
    type: String,
    default: '',
  },
});

const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();
const mobildConfigStore = useMobildConfigStore();

const reload = inject('reload', null);
const setDirty = inject('setDirty', null);

// 所有动态组件汇总（mobilePage + mobileConfig），供 <component :is> 解析字符串名称
const allComponents = { ...mPage, ...mobileConfigModule };
function resolveComp(name) {
  return allComponents[name] || name;
}

function getRightConfigKey(item) {
  const currentConfig = defaultArrays.value[item.num] || {};
  return `${item.configName}-${item.num}-${Object.keys(currentConfig).length}`;
}

const RIGHT_CONFIG_DIRECT_RENDER_COMPONENTS = ['c_product_info'];

function canRenderRightConfig(item) {
  if (!item || item.num === undefined) return true;
  if (item.configName === 'c_custom_component') return true;
  if (RIGHT_CONFIG_DIRECT_RENDER_COMPONENTS.includes(item.configName)) return true;
  const currentConfig = defaultArrays.value[item.num];
  return !!(currentConfig && currentConfig.setUp);
}

const DISABLED_JAVA_THEME_COMPONENTS = [
  'points_mall',
  'pointsMall',
  'sign_in',
  'signIn',
  'wechat_live',
  'liveBroadcast',
  'home_paid_vip',
  'homePaidVip',
  'c_paid_vip',
];

// --- theme mixin 内联 ---
const colorList = [
  {
    theme: '#42CA4D',
    priceColor: '#FF7600',
    minorColor: 'rgba(108, 198, 94, 0.5)',
    minorColorT: 'rgba(66, 202, 77, 0.1)',
    bntColor: '#FE960F',
    gradient: '#4DEA4D',
  },
  {
    theme: '#e93323',
    priceColor: '#e93323',
    minorColor: 'rgba(233, 51, 35, 0.5)',
    minorColorT: 'rgba(233, 51, 35, 0.1)',
    bntColor: '#FE960F',
    gradient: '#FF7931',
  },
  {
    theme: '#1DB0FC',
    priceColor: '#FD502F',
    minorColor: 'rgba(58, 139, 236, 0.5)',
    minorColorT: 'rgba(9, 139, 243, 0.1)',
    bntColor: '#22CAFD',
    gradient: '#5ACBFF',
  },
  {
    theme: '#FF448F',
    priceColor: '#FF448F',
    minorColor: 'rgba(255, 68, 143, 0.5)',
    minorColorT: 'rgba(255, 68, 143, 0.1)',
    bntColor: '#282828',
    gradient: '#FF67AD',
  },
  {
    theme: '#FE5C2D',
    priceColor: '#FE5C2D',
    minorColor: 'rgba(254, 92, 45, 0.5)',
    minorColorT: 'rgba(254, 92, 45, 0.1)',
    bntColor: '#FDB000',
    gradient: '#FF9451',
  },
  {
    theme: '#E0A558',
    priceColor: '#DA8C18',
    minorColor: 'rgba(224, 165, 88, 0.5)',
    minorColorT: 'rgba(224, 165, 88, 0.1)',
    bntColor: '#1A1A1A',
    gradient: '#FFCD8C',
  },
];
const colorStatusMap = {
  1: 2,
  2: 0,
  3: 1,
  4: 3,
  5: 4,
  6: 5,
};
const current = ref(3);
const colorStyle = ref(colorList[1]);
function getInfo() {
  getColorChange('color_change')
    .then((res = {}) => {
      const status = res.data && res.data.status ? res.data.status : 3;
      current.value = status;
      colorStyle.value = colorList[colorStatusMap[status] ?? 1] || colorList[1];
    })
    .catch(() => {
      current.value = 3;
      colorStyle.value = colorList[1];
    });
}
// --- end theme mixin ---

// beforeCreate: 清空 store
mobildConfigStore.SETEMPTY();
mobildConfigStore.RESET_PAGE_FOOTER();

const BaseURL = Setting.httpUrl + '/';
const qrcodeImg = ref('');
const modal = ref(false);
const clientHeight = ref(''); //页面动态高度
const rollHeight = ref('');
const leftMenu = ref([]); // 左侧菜单
const lConfig = ref([]); // 左侧组件
const mConfig = ref([]); // 中间组件渲染
const rConfig = ref([]); // 右侧组件配置
const activeConfigName = ref('');
const propsObj = ref({}); // 组件传递的数据,
const activeIndex = ref(-100); // 选中的下标
const number = ref(0);
const pageId = ref('');
const pageName = ref('');
const pageType = ref('');
const category = ref([]);
const tabList = ref([
  {
    title: '组件库',
    key: 0,
  },
  {
    title: '页面链接',
    key: 1,
  },
]);
const footActive = ref(false);
const loading = ref(false);
const relLoading = ref(false);
const isSearch = ref(false);
const isTab = ref(false);
const isFllow = ref(false);
const isComb = ref(false);
const isService = ref(false);
const visible = ref(true);
const diyStatus = ref(0);
const nameModal = ref(false);
const saveName = ref('');
const coverUrl = ref('');
const qrCodeUrl = ref(null);
const getComponentData = ref(null);

// mapState -> computed
const titleTxt = computed(() => mobildConfigStore.pageTitle || '首页');
const showTxt = computed(() => mobildConfigStore.pageShow);
const colorTxt = computed(() => mobildConfigStore.pageColor);
const bgPic = computed(() => mobildConfigStore.pagePic);
const picTxt = computed(() => mobildConfigStore.pagePic);
const colorPickerTxt = computed(() => mobildConfigStore.pageColorPicker);
const tabValTxt = computed(() => mobildConfigStore.pageTabVal);
const picUrlTxt = computed(() => mobildConfigStore.pagePicUrl);
const pageFooterType = computed(() =>
  mobildConfigStore.pageFooter && mobildConfigStore.pageFooter.navConfig
    ? mobildConfigStore.pageFooter.navConfig.tabVal
    : 0,
);
const pageFooterBottom = computed(() =>
  mobildConfigStore.pageFooter && mobildConfigStore.pageFooter.mbConfig
    ? mobildConfigStore.pageFooter.mbConfig.val
    : 0,
);
const defaultArrays = computed(() => mobildConfigStore.defaultArray);

const nameTxt = computed({
  get() {
    return mobildConfigStore.pageName;
  },
  set(value) {
    mobildConfigStore.UPNAME(value);
  },
});

const isMicroPage = computed(() => route.query.page_type == 'micro');

// filters
function filterTxt(val) {
  if (val) {
    return (val = val.substr(0, val.length - 1));
  }
}

// watch
watch(
  mConfig,
  (nVal, oVal) => {
    if (setDirty) {
      setDirty(true);
    }
  },
  { deep: true },
);
watch(
  () => mConfig.value.length,
  (nVal, oVal) => {
    if (setDirty) {
      setDirty(true);
    }
  },
);
watch(
  defaultArrays,
  (nVal, oVal) => {
    if (setDirty) {
      setDirty(true);
    }
  },
  { deep: true },
);

function exportView() {
  loading.value = true;
  nextTick(() => {
    console.log(mConfig.value);
  });
}
function importView() {}
// 将远程图片（OSS等）转为 base64，绕过 html2canvas 跨域限制
async function convertImagesToBase64(container) {
  const imgs = container.querySelectorAll('img');
  const tasks = Array.from(imgs).map((img) => {
    return new Promise((resolve) => {
      const src = img.getAttribute('src');
      if (!src || src.startsWith('data:')) {
        resolve();
        return;
      }
      // 给 URL 追加时间戳，避免浏览器缓存导致丢失 CORS 响应头
      const separator = src.includes('?') ? '&' : '?';
      const cacheBustUrl = `${src}${separator}_t=${Date.now()}`;
      fetch(cacheBustUrl, { mode: 'cors', cache: 'no-store' })
        .then((res) => res.blob())
        .then((blob) => {
          const reader = new FileReader();
          reader.onloadend = () => {
            img.setAttribute('src', reader.result);
            resolve();
          };
          reader.onerror = () => resolve(); // 转换失败则跳过，不阻塞
          reader.readAsDataURL(blob);
        })
        .catch(() => resolve()); // 请求失败则跳过
    });
  });
  await Promise.all(tasks);
}

// 保存为封面 - 截取长图并上传
function saveCover() {
  // 保存当前选中的组件索引
  const previousActiveIndex = activeIndex.value;
  ElMessage.info('正在生成封面图片，请稍候...');

  setTimeout(async () => {
    try {
      // 取消选中状态
      activeIndex.value = -999;
      rConfig.value = [];

      // 等待 DOM 更新
      await nextTick();

      // 获取 imgContainer 元素
      const container = document.getElementById('imgContainer');
      if (!container) {
        ElMessage.error('未找到页面容器元素');
        // 恢复选中状态
        activeIndex.value = previousActiveIndex;
        restoreActiveComponent(previousActiveIndex);
        return;
      }

      // 将容器内所有远程图片（含OSS）预先转为 base64，解决 html2canvas 跨域白图问题
      await convertImagesToBase64(container);

      // 使用 html2canvas 截取长图
      const canvas = await html2canvas(container, {
        useCORS: true,
        allowTaint: true, // 已手动转为 base64，可放开 allowTaint
        logging: false,
        scale: 2, // 提高清晰度
        backgroundColor: colorTxt.value ? colorPickerTxt.value : '#ffffff',
        scrollY: -window.scrollY,
        scrollX: -window.scrollX,
        windowWidth: container.scrollWidth,
        windowHeight: container.scrollHeight,
      });

      // 将 canvas 转换为 blob
      canvas.toBlob(
        async (blob) => {
          if (!blob) {
            ElMessage.error('图片生成失败');
            // 恢复选中状态
            activeIndex.value = previousActiveIndex;
            restoreActiveComponent(previousActiveIndex);
            return;
          }

          // 创建 FormData 上传
          const formData = new FormData();
          const fileName = `cover_${Date.now()}.png`;
          const file = new File([blob], fileName, { type: 'image/png' });
          formData.append('multipart', file);

          try {
            // 上传到后端
            const res = await fileUpload(formData);
            if (res.status === 200) {
              ElMessage.success('封面保存成功！');
              // 这里可以根据需要将图片URL保存到页面配置中
              coverUrl.value = res.data.src;
              if (!mConfig.value.length) return ElMessage.warning('请先配置DIY数据');
              if (!pageId.value) return ElMessage.warning('请先保存DIY数据');
              saveThemeImage(pageId.value, { image: coverUrl.value, type: pageType.value });
            } else {
              ElMessage.error(res.msg || '封面保存失败');
            }
          } catch (err) {
            ElMessage.error(err.msg || '上传失败，请重试');
          } finally {
            // 恢复选中状态
            activeIndex.value = previousActiveIndex;
            restoreActiveComponent(previousActiveIndex);
          }
        },
        'image/png',
        0.95,
      ); // 图片质量 0.95
    } catch (error) {
      console.error('截图失败：', error);
      ElMessage.error('生成封面失败，请重试');
      // 恢复选中状态
      activeIndex.value = previousActiveIndex;
      restoreActiveComponent(previousActiveIndex);
    }
  }, 300);
}
// 恢复选中的组件
function restoreActiveComponent(index) {
  if (index === -100) {
    // 恢复页面设置
    showTitle();
  } else if (index === -101) {
    // 恢复底部菜单
    showFoot();
  } else if (index === -102) {
    // 恢复底部菜单
    showBottomMenu();
  } else if (index >= 0 && index < mConfig.value.length) {
    // 恢复组件选中
    const item = mConfig.value[index];
    bindconfig(item, index);
  }
}
function preview() {
  modal.value = true;
  creatQrCode(pageId.value, diyStatus.value);
  routineCode(pageId.value);
}
//小程序二维码
function routineCode(id) {
  getRoutineCode(id)
    .then((res) => {
      qrcodeImg.value = res.data.image;
    })
    .catch((err) => {
      ElMessage.error(err);
    });
}
//生成二维码
function creatQrCode(id, status) {
  qrCodeUrl.value.innerHTML = '';
  let url = `${BaseURL}pages/index/index?theme_id=${route.query.id}`;
  // if (status) {
  //   url = `${BaseURL}pages/index/index`;
  // } else {
  //   url = `${BaseURL}pages/annex/special/index?id=${id}`;
  // }
  var qrcode = new QRCode(qrCodeUrl.value, {
    text: url, // 需要转换为二维码的内容
    width: 160,
    height: 160,
    colorDark: '#000000',
    colorLight: '#ffffff',
    correctLevel: QRCode.CorrectLevel.H,
  });
}
function changName(val) {
  mobildConfigStore.UPNAME(val);
}
function cancel() {
  visible.value = false;
}
function determine() {
  if (nameTxt.value.trim() == '') {
    return ElMessage.error('请输入模板名称');
  }
  if (pageId.value == 0) {
    ElMessage.success('修改成功');
    return false;
  }
  diyUpdateName(pageId.value, { name: nameTxt.value })
    .then((res) => {
      visible.value = false;
      ElMessage.success(res.msg);
    })
    .catch((err) => {
      ElMessage.error(err.msg);
    });
  visible.value = false;
}
function returnTap() {
  ElMessageBox({
    title: '温馨提示',
    message: '确定离开此页面？系统可能不会保存您所做的更改。',
    showCancelButton: true,
    cancelButtonText: '取消',
    confirmButtonText: '确定',
    iconClass: 'el-icon-warning',
    confirmButtonClass: 'btn-custom-cancel',
    showClose: false,
  })
    .then(() => {
      router.push(isMicroPage.value ? '/design/micro_theme' : '/design/my_theme');
    })
    .catch(() => {});
}
function leftRemove({ to, from, item, clone, oldIndex, newIndex }) {
  if (isSearch.value && newIndex == 0) {
    if (item._underlying_vm_.name == 'z_wechat_attention') {
      isFllow.value = true;
    } else {
      mobildConfigStore.ARRAYREAST(mConfig.value[0].num);
      mConfig.value.splice(0, 1);
    }
  }
  if ((isFllow.value = true && newIndex >= 1)) {
    mobildConfigStore.ARRAYREAST(mConfig.value[0].num);
  }
}
function onMove(e) {
  if (e.relatedContext.element.name == 'search_box') return false;
  if (e.relatedContext.element.name == 'nav_bar') return false;
  if (e.relatedContext.element.name == 'home_comb') return false;
  return true;
}
function onCopy() {
  ElMessage.success('复制成功');
}
function onError() {
  ElMessage.error('复制失败');
}
function checkCanAddComponent(item) {
  if (isDisabledThemeComponent(item)) {
    ElMessage.warning('当前 Java 版本暂不支持该组件');
    return false;
  }
  if (item.name == 'search_box') {
    if (isSearch.value) {
      ElMessage.error('该组件只能添加一次');
      return false;
    }
    if (isComb.value) {
      ElMessage.error('轮播搜索不能和搜索组件与选项卡组件同时存在');
      return false;
    }
  }
  if (item.name == 'nav_bar') {
    if (isTab.value) {
      ElMessage.error('该组件只能添加一次');
      return false;
    }
    if (isComb.value) {
      ElMessage.error('轮播搜索不能和搜索组件与选项卡组件同时存在');
      return false;
    }
  }
  if (item.name == 'home_comb') {
    if (isComb.value) {
      ElMessage.error('该组件只能添加一次');
      return false;
    }
    if (isSearch.value || isTab.value) {
      ElMessage.error('轮播搜索不能和搜索组件与选项卡组件同时存在');
      return false;
    }
  }
  if (item.name == 'home_service' && isService.value) {
    ElMessage.error('该组件只能添加一次');
    return false;
  }
  return true;
}
function isDisabledThemeComponent(item) {
  if (!item) return false;
  return DISABLED_JAVA_THEME_COMPONENTS.includes(item.name) || DISABLED_JAVA_THEME_COMPONENTS.includes(item.defaultName);
}
function sanitizeThemeValue(value) {
  let result = {};
  Object.keys(value || {}).forEach((key) => {
    if (!isDisabledThemeComponent(value[key])) {
      result[key] = value[key];
    }
  });
  return result;
}
function sanitizeThemeComponent(value) {
  if (!value || isDisabledThemeComponent(value)) return null;
  return JSON.parse(JSON.stringify(value));
}
function findThemeComponent(value, name) {
  let result = null;
  Object.keys(value || {}).some((key) => {
    if (value[key] && value[key].name == name) {
      result = {
        key,
        value: value[key],
      };
      return true;
    }
    return false;
  });
  return result;
}
function removeThemeComponent(value, name) {
  Object.keys(value || {}).forEach((key) => {
    if (value[key] && value[key].name == name) {
      delete value[key];
    }
  });
  return value;
}
function appendBottomMenu(value) {
  let bottomMenu = sanitizeThemeComponent(mobildConfigStore.bottomMenu);
  if (!bottomMenu || bottomMenu.name != 'bottomMenu') return value;
  let timestamp = bottomMenu.timestamp || new Date().getTime() * 1000;
  bottomMenu.timestamp = timestamp;
  bottomMenu.id = bottomMenu.id || 'id' + timestamp;
  value[timestamp] = bottomMenu;
  return value;
}
function appendPageFooter(value) {
  let oldPageFooter = findThemeComponent(value, 'pageFoot');
  let pageFooter = sanitizeThemeComponent(mobildConfigStore.pageFooter);
  if (!pageFooter || pageFooter.name != 'pageFoot') return value;
  if (oldPageFooter && oldPageFooter.value) {
    pageFooter = Object.assign({}, oldPageFooter.value, pageFooter);
  }
  removeThemeComponent(value, 'pageFoot');
  let timestamp = pageFooter.timestamp || (oldPageFooter && oldPageFooter.value.timestamp) || new Date().getTime() * 1000;
  pageFooter.timestamp = timestamp;
  pageFooter.id = pageFooter.id || 'id' + timestamp;
  value[timestamp] = pageFooter;
  return value;
}
function updateSpecialComponentStatus(name, status) {
  if (name == 'search_box') {
    isSearch.value = status;
  }
  if (name == 'nav_bar') {
    isTab.value = status;
  }
  if (name == 'home_comb') {
    isComb.value = status;
  }
  if (name == 'home_service') {
    isService.value = status;
  }
}
//设置默认数据
function setmoren() {
  setDefault(pageId.value)
    .then((res) => {
      ElMessage.success(res.msg);
    })
    .catch((err) => {
      ElMessage.error(err.msg);
    });
}
//恢复默认
function getmoren() {
  recovery(pageId.value)
    .then((res) => {
      ElMessage.success(res.msg);
      reload && reload();
    })
    .catch((err) => {
      ElMessage.error(err.msg);
    });
}
// 页面标题点击
function showTitle() {
  activeIndex.value = -100;
  let obj = {};
  for (var i in mobileConfigModule) {
    if (i == 'pageTitle') {
      // rConfig.value = obj
      obj = mobileConfigModule[i];
      obj.configName = mobileConfigModule[i].name;
      obj.cname = '页面设置';
    }
  }
  let abc = obj;
  rConfig.value = [];
  rConfig.value[0] = JSON.parse(JSON.stringify(obj));
}
// 页面底部点击
function showFoot() {
  activeIndex.value = -101;
  let obj = {};
  for (var i in mobileConfigModule) {
    if (i == 'pageFoot') {
      // rConfig.value = obj
      obj = mobileConfigModule[i];
      obj.configName = mobileConfigModule[i].name;
      obj.cname = '底部菜单';
    }
  }
  let abc = obj;
  rConfig.value = [];
  rConfig.value[0] = JSON.parse(JSON.stringify(obj));
}
function showBottomMenu() {
  activeIndex.value = -102;
  let obj = {};
  for (var i in mobileConfigModule) {
    if (i == 'c_bottom_menu') {
      // rConfig.value = obj
      obj = mobileConfigModule[i];
      obj.configName = mobileConfigModule[i].name;
      obj.cname = '底部菜单';
    }
  }
  rConfig.value = [];
  rConfig.value.push(JSON.parse(JSON.stringify(obj)));
}
// 对象转数组
function objToArr(data) {
  let obj = Object.keys(data || {});
  let m = obj.map((key) => data[key]);
  return m;
}
function log(evt) {
  // 中间拖拽排序
  if (evt.moved) {
    if (evt.moved.element.name == 'search_box') {
      return ElMessage.warning('该组件禁止拖拽');
    }
    // if (evt.moved.element.name == "nav_bar") {
    //     return ElMessage.warning("该组件禁止拖拽");
    // }
    evt.moved.oldNum = mConfig.value[evt.moved.oldIndex].num;
    evt.moved.newNum = mConfig.value[evt.moved.newIndex].num;
    evt.moved.status = evt.moved.oldIndex > evt.moved.newIndex;
    mConfig.value.forEach((el, index) => {
      el.num = new Date().getTime() * 1000 + index;
    });
    evt.moved.list = mConfig.value;
    rConfig.value = [];
    let item = evt.moved.element;
    let tempItem = JSON.parse(JSON.stringify(item));
    rConfig.value.push(tempItem);
    activeIndex.value = evt.moved.newIndex;
    mobildConfigStore.SETCONFIGNAME(item.name);
    mobildConfigStore.defaultArraySort(evt.moved);
  }
  // 从左向右拖拽排序
  if (evt.added) {
    let data = evt.added.element;
    if (!checkCanAddComponent(data)) {
      mConfig.value.splice(evt.added.newIndex, 1);
      return;
    }
    let obj = {};
    let timestamp = new Date().getTime() * 1000;
    data.num = timestamp;
    activeConfigName.value = data.name;
    let tempItem = JSON.parse(JSON.stringify(data));
    tempItem.id = 'id' + tempItem.num;
    mConfig.value[evt.added.newIndex] = tempItem;
    rConfig.value = [];
    rConfig.value.push(tempItem);
    updateSpecialComponentStatus(data.name, true);
    mConfig.value.forEach((el, index) => {
      el.num = new Date().getTime() * 1000 + index;
    });
    evt.added.list = mConfig.value;
    activeIndex.value = evt.added.newIndex;
    // 保存组件名称
    mobildConfigStore.SETCONFIGNAME(data.name);
    mobildConfigStore.defaultArraySort(evt.added);
  }
}
function cloneDog(data) {
  // mConfig.value.push(tempItem)
  return {
    ...data,
  };
}
//数组元素互换位置
function swapArray(arr, index1, index2) {
  arr[index1] = arr.splice(index2, 1, arr[index1])[0];
  return arr;
}
//点击上下移动；
function movePage(item, index, type) {
  if (type) {
    if (index == 0) {
      return;
    }
  } else {
    if (index == mConfig.value.length - 1) {
      return;
    }
  }
  if (item.name == 'search_box' || item.name == 'nav_bar' || item.name == 'home_comb') {
    return ElMessage.warning('该组件禁止移动');
  }
  if (type) {
    if (
      mConfig.value[index - 1].name == 'search_box' ||
      mConfig.value[index - 1].name == 'nav_bar' ||
      mConfig.value[index - 1].name == 'home_comb'
    ) {
      return ElMessage.warning('搜索框或选项卡或轮播搜索必须为顶部');
    }
    swapArray(mConfig.value, index - 1, index);
  } else {
    swapArray(mConfig.value, index, index + 1);
  }
  let obj = {};
  rConfig.value = [];
  obj.oldIndex = index;
  if (type) {
    obj.newIndex = index - 1;
  } else {
    obj.newIndex = index + 1;
  }
  mConfig.value.forEach((el, index) => {
    el.num = new Date().getTime() * 1000 + index;
  });
  let tempItem = JSON.parse(JSON.stringify(item));
  rConfig.value.push(tempItem);
  obj.element = item;
  obj.list = mConfig.value;
  if (type) {
    activeIndex.value = index - 1;
  } else {
    activeIndex.value = index + 1;
  }

  mobildConfigStore.SETCONFIGNAME(item.name);
  mobildConfigStore.defaultArraySort(obj);
}
// 组件添加
function addDomCon(item, type, index) {
  if (!checkCanAddComponent(item)) {
    return false;
  }
  updateSpecialComponentStatus(item.name, true);
  let obj = {};
  let timestamp = new Date().getTime() * 1000;
  item.num = `${timestamp}`;
  item.id = `id${timestamp}`;
  activeConfigName.value = item.name;
  let tempItem = JSON.parse(JSON.stringify(item));
  if (item.name == 'home_comb') {
    rConfig.value = [];
    mConfig.value.unshift(tempItem);
    activeIndex.value = 0;
    rConfig.value.push(tempItem);
  } else if (item.name == 'search_box') {
    rConfig.value = [];
    mConfig.value.unshift(tempItem);
    activeIndex.value = 0;
    rConfig.value.push(tempItem);
  } else if (item.name == 'nav_bar') {
    rConfig.value = [];
    if (mConfig.value[0] && mConfig.value[0].name === 'search_box') {
      mConfig.value.splice(1, 0, tempItem);
      activeIndex.value = 1;
    } else {
      mConfig.value.splice(0, 0, tempItem);
      activeIndex.value = 0;
    }
    rConfig.value.push(tempItem);
  } else {
    if (type) {
      rConfig.value = [];
      if (activeIndex.value == 0 && mConfig.value[1] && mConfig.value[1].name == 'nav_bar') {
        activeIndex.value = 2;
      } else {
        activeIndex.value = activeIndex.value >= 0 ? activeIndex.value + 1 : mConfig.value.length;
      }
      mConfig.value.splice(activeIndex.value, 0, tempItem);
      rConfig.value.push(tempItem);
    } else {
      mConfig.value.splice(index + 1, 0, tempItem);
      activeIndex.value = index;
    }
  }
  mConfig.value.forEach((el, index) => {
    el.num = new Date().getTime() * 1000 + index;
  });
  // 保存组件名称
  obj.element = item;
  obj.list = mConfig.value;
  mobildConfigStore.SETCONFIGNAME(item.name);
  mobildConfigStore.defaultArraySort(obj);
  return true;
}
//中间页点击添加模块；
function bindAddDom(item, type, index) {
  // 复制
  if (type == 0) {
    let defaultArray = mobildConfigStore.defaultArray;
    let configData = JSON.parse(JSON.stringify(defaultArray[item.num]));

    // 找到原始配置以获取 cname, icon 等基础信息
    let baseItem = item;
    lConfig.value.forEach((j) => {
      if (item.name == j.name) {
        baseItem = j;
      }
    });

    // 先调用 addDomCon 添加一个新组件，然后立即用 configData 覆盖它
    if (!addDomCon(baseItem, type, index)) {
      return;
    }

    // 获取刚添加的组件（在 index+1 位置，因为 addDomCon 是 splice(index+1, 0, ...)）
    let newIndex = index + 1;
    let newItem = mConfig.value[newIndex];

    // 重新获取最新的 defaultArray (引用)
    let currentDefaultArray = mobildConfigStore.defaultArray;

    // 保留新生成的 num 和 timestamp 相关字段，覆盖其他配置
    let newConfig = {
      ...configData,
      num: newItem.num,
      id: newItem.id, // 确保使用新生成的唯一ID
      timestamp: currentDefaultArray[newItem.num].timestamp,
    };

    // 提交更新
    mobildConfigStore.UPDATEARR({ num: newItem.num, val: newConfig });
  } else {
    let i = item;
    lConfig.value.forEach((j) => {
      if (item.name == j.name) {
        i = j;
      }
    });
    if (!addDomCon(i, type, index)) {
      return;
    }
  }
}
//左边配置模块点击添加；
function addDom(item, type) {
  addDomCon(item, type);
}
// 点击显示相应的配置
function bindconfig(item, index) {
  rConfig.value = [];
  let tempItem = JSON.parse(JSON.stringify(item));
  rConfig.value.push(tempItem);
  activeIndex.value = index;
  mobildConfigStore.SETCONFIGNAME(item.name);
}
function bindHide(item) {
  let obj = mobildConfigStore.defaultArray;
  let num = rConfig.value[0].num;
  if (!obj[num]) return;
  obj[num].isHide = !obj[num].isHide;
  mobildConfigStore.UPDATEARR({ num: num, val: obj[num] });
}
// 组件删除
function bindDelete(item, key) {
  ElMessageBox.confirm('确定要删除此组件吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      updateSpecialComponentStatus(item.name, false);
      mConfig.value.splice(key, 1);
      rConfig.value.splice(0, 1);
      if (mConfig.value.length != key) {
        rConfig.value.push(mConfig.value[key]);
      } else {
        if (mConfig.value.length) {
          activeIndex.value = key - 1;
          rConfig.value.push(mConfig.value[key - 1]);
        } else {
          showTitle();
        }
      }
      // 删除第几个配置
      mobildConfigStore.DELETEARRAY(item);
    })
    .catch(() => {});
}
// 组件返回
function config(data) {
  let propsObjVal = propsObj.value;
  propsObjVal.data = data;
  propsObjVal.name = activeConfigName.value;
}
function addSort(arr, index1, index2) {
  arr[index1] = arr.splice(index2, 1, arr[index1])[0];
  return arr;
}
// 数组排序
function arraySort() {
  let tempArr = [];
  let basis = {
    title: '基础组件',
    list: [],
    isOpen: true,
  };
  let marketing = {
    title: '营销组件',
    list: [],
    isOpen: true,
  };
  let goods = {
    title: '商品组件',
    list: [],
    isOpen: true,
  };
  let user = {
    title: '用户组件',
    list: [],
    isOpen: true,
  };
  let tool = {
    title: '工具组件',
    list: [],
    isOpen: true,
  };
  lConfig.value.map((el, index) => {
    if (el.type == 0) {
      basis.list.push(el);
    }
    if (el.type == 1) {
      marketing.list.push(el);
    }
    if (el.type == 2) {
      tool.list.push(el);
    }
    if (el.type == 3) {
      if (pageType.value !== 'home' && pageType.value !== 'user') {
        if (el.name === 'home_product_info' || el.cname === '商品信息') {
          goods.list.unshift(el);
        } else {
          goods.list.push(el);
        }
      }
    }
    if (el.type == 4) {
      if (pageType.value !== 'home' && pageType.value !== 'detail') user.list.push(el);
    }
  });
  tempArr.push(basis, marketing);
  if (pageType.value !== 'home' && pageType.value !== 'user') tempArr.push(goods);
  if (pageType.value !== 'home' && pageType.value !== 'detail') tempArr.push(user);
  tempArr.push(tool);
  leftMenu.value = tempArr;
}
function diySaveDate(val, num, title, save) {
  let pageData = {};
  if (['home', 'detail', 'user'].includes(pageType.value)) {
    pageData = {
      type: pageType.value,
      value: val,
      title: titleTxt.value,
      name: nameTxt.value || '模板',
      is_show: showTxt.value ? 1 : 0,
      is_bg_color: colorTxt.value ? 1 : 0,
      is_bg_pic: bgPic.value ? 1 : 0,
      color_picker: colorPickerTxt.value,
      bg_tab_val: tabValTxt.value,
      bg_pic: picUrlTxt.value,
      cover_pic: coverUrl.value,
    };
  } else if (['category', 'theme'].includes(pageType.value)) {
    pageData = val;
  }
  let requestData = {
    type: pageType.value,
    value: pageData,
  };
  if (route.query.page_type === 'micro') {
    requestData.page_type = 'micro';
  }
  if (title) {
    requestData.title = title;
  }
  if (route.query.tid) {
    requestData.tid = route.query.tid;
  }
  themeSave(title ? 0 : pageId.value, requestData)
    .then((res) => {
      if (pageId.value != res.data.id && !title) {
        let query = { ...route.query, id: res.data.id };
        delete query.tid; // 保存后移除 tid
        router.replace({ query });
        pageId.value = res.data.id;
      }
      ElMessage.success(res.msg);
      nameModal.value = false;
      if (num == 2) {
        relLoading.value = false;
        setTimeout(() => {
          let page = isMicroPage.value ? '/design/micro_theme' : '/design/my_theme';
          window.location.replace(page);
        }, 2000);
      } else {
        loading.value = false;
      }
      if (setDirty) {
        setDirty(false);
      }
    })
    .catch((res) => {
      relLoading.value = false;
      loading.value = false;
      ElMessage.error(res.msg);
    });
}
function saveModal() {
  if (!saveName.value) return ElMessage.warning('请先输入模板名称');
  saveConfig(1, saveName.value);
}
function closeWindow() {
  ElMessageBox({
    title: '提示',
    message: '关闭页面前请先保存数据，未保存的话数据会丢失',
    showCancelButton: true,
    cancelButtonText: '取消',
    confirmButtonText: '确定',
    iconClass: 'el-icon-warning',
    confirmButtonClass: 'btn-custom-cancel',
  })
    .then(() => {
      setTimeout(() => {
        // saveConfig();
        window.close();
      }, 1000);
    })
    .catch(() => {});
}
// 保存配置
function saveConfig(num, type, save) {
  if (mConfig.value.length == 0) {
    return ElMessage.error('暂未添加任何组件，保存失败！');
  }
  if (num == 1) {
    loading.value = true;
  } else {
    relLoading.value = true;
  }
  let val = sanitizeThemeValue(mobildConfigStore.defaultArray);
  if (pageType.value == 'home') {
    val = appendPageFooter(val);
  } else if (pageType.value == 'detail') {
    val = appendBottomMenu(removeThemeComponent(val, 'bottomMenu'));
  }
  nextTick(() => {
    diySaveDate(val, num, type, save);
  });
}
// 获取默认配置
function getDefaultConfig() {
  let id = pageId.value;
  if (id == 0 && route.query.tid) {
    id = route.query.tid;
  }
  themeInfo(id, pageType.value).then((res) => {
    let obj = {};
    let tempARR = [];
    let data = res.data;
    mobildConfigStore.titleUpdata(data.title);
    mobildConfigStore.nameUpdata(data.name);
    mobildConfigStore.showUpdata(data.is_show);
    mobildConfigStore.colorUpdata(data.is_bg_color || 0);
    mobildConfigStore.picUpdata(data.is_bg_pic || 0);
    mobildConfigStore.pickerUpdata(data.color_picker || '#f5f5f5');
    mobildConfigStore.radioUpdata(data.bg_tab_val || 0);
    mobildConfigStore.picurlUpdata(data.bg_pic || '');
    diyStatus.value = data.status;
    let newArr = objToArr(data.value).filter((item) => !isDisabledThemeComponent(item));

    function sortNumber(a, b) {
      return a.timestamp - b.timestamp;
    }
    newArr.sort(sortNumber);
    let hasBottomMenu = false;
    let hasPageFooter = false;
    newArr.map((el, index) => {
      if (el.name == 'headerSerch') {
        isSearch.value = true;
      }
      if (el.name == 'tabNav') {
        isTab.value = true;
      }
      if (el.name == 'homeComb') {
        isComb.value = true;
      }
      if (el.name == 'customerService') {
        isService.value = true;
      }
      if (el.name == 'goodList') {
        // let storage = window.localStorage;
        // storage.setItem(el.timestamp, el.selectConfig.activeValue);
      }
      if (el.name == 'bottomMenu') {
        hasBottomMenu = true;
        mobildConfigStore.UPBOTTOMMENU(el);
        return;
      }
      if (pageType.value == 'home' && el.name == 'pageFoot') {
        hasPageFooter = true;
        footActive.value = true;
        mobildConfigStore.footPageUpdata(el);
        return;
      }
      el.id = 'id' + el.timestamp;
      lConfig.value.map((item, j) => {
        if (el.name == item.defaultName) {
          item.num = el.timestamp;
          item.id = 'id' + el.timestamp;
          let tempItem = JSON.parse(JSON.stringify(item));
          tempARR.push(tempItem);
          obj[el.timestamp] = el;
          mConfig.value.push(tempItem);
          // 保存默认组件配置
          mobildConfigStore.ADDARRAY({
            num: el.timestamp,
            val: el,
          });
        }
      });
    });
    if (pageType.value == 'home') {
      if (!hasPageFooter) {
        mobildConfigStore.RESET_PAGE_FOOTER();
      }
      showTitle();
    } else if (pageType.value == 'detail' && !hasBottomMenu) {
      mobildConfigStore.RESET_BOTTOM_MENU();
    }
  });
}
function categoryList() {
  categoryListApi((res) => {
    category.value = res.data;
  });
}
// 重置
function reast() {
  if (pageId.value == 0) {
    ElMessage.error('新增页面，无法重置');
  } else {
    ElMessageBox.confirm('此操作将清空模板内容, 是否继续?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then((res) => {
      mConfig.value = [];
      rConfig.value = [];
      activeIndex.value = -99;
      getDefaultConfig();
    });
  }
}
function getBadgeText(val) {
  const map = {
    user: '用户',
    article: '文章',
    coupon: '优惠券',
    goods: '商品',
  };
  return map[val] || '';
}

// --- created 逻辑 ---
categoryList();
pageId.value = route.query.id;
pageName.value = route.query.name;
pageType.value = props.pageType || route.query.type;
lConfig.value = objToArr(mPage).filter((item) => !isDisabledThemeComponent(item));
let imgList = {
  imgList: [foot005Img, foot006Img],
  name: '购物车',
  link: '/pages/order_addcart/order_addcart',
};
nextTick(() => {
  mobildConfigStore.FOOTER({ title: '是否自定义', name: imgList });
  arraySort();
  if (pageId.value != 0 || route.query.tid) {
    getDefaultConfig();
  } else if (pageType.value == 'home') {
    showTitle();
  } else {
    // 清空 vuex 中的 defaultArray
    mobildConfigStore.DEFAULTARRAY({});
    mobildConfigStore.RESET_BOTTOM_MENU();
  }
  clientHeight.value = `${document.documentElement.clientHeight}` - 65.81; //获取浏览器可视区域高度
  let H = `${document.documentElement.clientHeight}` - 180;
  rollHeight.value = H > 650 ? 650 : H;
  window.onresize = function () {
    clientHeight.value = `${document.documentElement.clientHeight}` - 65.81;
    let H = `${document.documentElement.clientHeight}` - 180;
    rollHeight.value = H > 650 ? 650 : H;
  };
});

onMounted(() => {
  getInfo();
});

// beforeDestroy + destroyed
function cleanupStore() {
  mobildConfigStore.titleUpdata('');
  mobildConfigStore.nameUpdata('');
  mobildConfigStore.showUpdata(1);
  mobildConfigStore.colorUpdata(0);
  mobildConfigStore.picUpdata(0);
  mobildConfigStore.pickerUpdata('#f5f5f5');
  mobildConfigStore.radioUpdata(0);
  mobildConfigStore.picurlUpdata('');
  mobildConfigStore.SETEMPTY();
  mobildConfigStore.RESET_PAGE_FOOTER();
}
onBeforeUnmount(() => {
  cleanupStore();
});
onUnmounted(() => {
  cleanupStore();
});

defineExpose({
  saveConfig,
  preview,
  pageId,
  lConfig,
  mConfig,
  rConfig,
  activeIndex,
  showTitle,
});
</script>
<style>
.el-main {
  padding: 0px !important;
}
</style>
<style scoped>
.header-title {
  background: var(--prev-color-primary);
  border-radius: 0;
  margin-bottom: 0;
  padding: 16px;
}
.ivu-page-header-title {
  color: #fff;
  font-size: 16px;
}
</style>
<style scoped lang="scss">
:deep(.el-card__body) {
  padding: 0;
}
:deep(.icondel_1),
:deep(.upload-box) {
  cursor: pointer;
}
:deep(.el-checkbox),
:deep(.el-radio) {
  margin-bottom: 15px;
  margin-right: 15px;
}
.c_label {
  margin-top: 0;
}
:deep(.el-button--small) {
  // border-radius: 0;
  border-radius: 4px;
}
.look,
.look:hover,
.look:focus,
.look:active,
.close,
.close:hover,
.close:focus,
.close:active {
  background: var(--prev-color-primary);
  color: #fff;
  border-color: #fff;
}

.save,
.save:hover,
.save:active,
.save:focus {
  background: #fff;
  color: var(--prev-color-primary);
  border-color: var(--prev-color-primary);
}
// :deep(.c_row-item) {
//   margin-bottom: 10px;
// }
.ysize {
  background-size: 100%;
}

.fullsize {
  background-size: 100% 100%;
}

.repeat {
  background-repeat: repeat;
}

.noRepeat {
  background-repeat: no-repeat;
}
.fl_header {
  color: #fff;
  .f-title {
    position: relative;
  }
  .return {
    color: #fff;
    margin-right: 34px;
    margin-left: 5px;
    &::after {
      content: ' ';
      position: absolute;
      width: 1px;
      height: 16px;
      background-color: rgba(238, 238, 238, 0.5);
      left: 65px;
      top: 50%;
      margin-top: -8px;
    }
  }
  .iconfont {
    color: #fff;
  }
  .f_title {
    &:hover {
      .return {
        color: rgba(255, 255, 255, 0.8);
      }
      .iconfanhui {
        color: rgba(255, 255, 255, 0.8);
      }
    }
    .name {
      font-size: 16px;
    }
    .iconfont {
      margin-left: 10px;
      color: #fff;
    }
  }
}
.wrapper-con {
  position: relative;
  flex: 1;
  background: #f0f2f5;
  display: flex;
  justify-content: center;
  padding-top: 40px;
  height: 100%;
  .acticons {
    position: absolute;
    right: 20px;
    top: 20px;
    display: flex;
    flex-direction: column;
    z-index: 1;
    .el-button + .el-button {
      margin-left: 0;
    }
  }
  /* min-width 700px; */
}
.main .content-wrapper {
  padding: 0 !important;
}
.defaultData {
  /* margin-left 20px; */
  cursor: pointer;
  position: absolute;
  left: 50%;
  margin-left: 235px;

  .data {
    display: block;
    margin-top: 20px;
    color: #282828;
    background-color: #fff;
    width: 94px;
    text-align: center;
    height: 32px;
    border-radius: 3px;
    font-size: 12px;
    margin-left: 0 !important;
    border: none;
  }

  .data:hover {
    color: var(--prev-color-primary);
    border: none;
    box-shadow: 0px 1px 6px 0px rgba(0, 0, 0, 0.03);
  }
  .data:focus {
    color: #fff;
    background-color: var(--prev-color-primary);
    border: 1px solid var(--prev-color-primary);
  }
}

.overflowy {
  margin-right: 4px;

  .picture {
    width: 375px;
    height: 20px;
    margin: 0 auto;
    background-color: #fff;
  }
}

.bnt {
  width: 80px !important;
}

/* 定义滑块 内阴影+圆角 */
::-webkit-scrollbar-thumb {
  -webkit-box-shadow: inset 0 0 6px #fff;
  display: none;
}

.left:hover::-webkit-scrollbar-thumb,
.right-box:hover::-webkit-scrollbar-thumb {
  display: block;
}

.contxt:hover ::-webkit-scrollbar-thumb {
  display: block;
}

::-webkit-scrollbar {
  width: 4px !important; /* 对垂直流动条有效 */
}

.scrollCon {
  overflow-y: scroll;
  overflow-x: hidden;
}

.scroll-box .position {
  display: block !important;
  height: 40px;
  text-align: center;
  line-height: 40px;
  border: 1px dashed var(--prev-color-primary);
  color: var(--prev-color-primary);
  background-color: #edf4fb;
}

.scroll-box .conter {
  display: none !important;
}
.conter {
  margin-top: 3px;
}
.dragClass {
  background-color: #fff;
}

.ivu-mt {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.diy-wrapper {
  max-width: 100%;
  min-width: 1100px;
  display: flex;
  justify-content: space-between;
  height: calc(100vh - 62px);
  .left {
    min-width: 300px;
    max-width: 300px;
    /* border 1px solid #DDDDDD */
    border-radius: 4px;
    height: 100%;

    .title-bar {
      display: flex;
      color: #333;
      border-bottom: 1px solid #eee;
      border-radius: 4px;
      cursor: pointer;

      .title-item {
        display: flex;
        align-items: center;
        justify-content: center;
        flex: 1;
        height: 45px;

        &.on {
          color: var(--prev-color-primary);
          font-size: 14px;
          border-bottom: 1px solid var(--prev-color-primary);
        }
      }
    }

    .wrapper {
      padding: 15px;
      overflow-y: scroll;
      -webkit-overflow-scrolling: touch;

      .tips {
        display: flex;
        justify-content: space-between;
        padding-bottom: 15px;
        font-size: 13px;
        color: #000;
        cursor: pointer;

        .ivu-icon {
          color: #000;
        }
      }
    }

    .link-item {
      padding: 10px;
      border-bottom: 1px solid #f5f5f5;
      font-size: 12px;
      color: #323232;

      .name {
        font-size: 14px;
        color: var(--prev-color-primary);
      }
      .copy_btn {
        cursor: pointer;
      }

      .link-txt {
        margin-top: 2px;
        word-break: break-all;
      }

      .params {
        margin-top: 5px;
        color: #1cbe6b;
        word-break: break-all;

        .txt {
          color: #323232;
        }

        span {
          &:last-child i {
            display: none;
            color: red;
          }
        }
      }

      .lable {
        display: flex;
        margin-top: 5px;
        color: #999;

        p {
          flex: 1;
          word-break: break-all;
        }

        button {
          margin-left: 30px;
          width: 38px;
        }
      }
    }

    .dragArea.list-group {
      display: flex;
      flex-wrap: wrap;

      .list-group-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        width: 74px;
        height: 66px;
        margin-right: 17px;
        margin-bottom: 10px;
        font-size: 12px;
        color: #666;
        cursor: pointer;
        border-radius: 5px;
        text-align: center;

        &:hover {
          box-shadow: 0 0 5px 0 rgba(24, 144, 255, 0.3);
          border-radius: 5px;
          transform: scale(1.1);
          transition: all 0.2s;
        }

        &:nth-child(3n) {
          margin-right: 0;
        }
      }
    }
  }

  .content {
    position: relative;
    height: 100%;
    width: 100%;

    .page-foot {
      position: relative;
      width: 375px;
      margin: 0 auto 20px auto;

      .delete-box {
        display: none;
        position: absolute;
        left: -2px;
        top: 0;
        width: 379px;
        height: 100%;
        border: 2px dashed var(--prev-color-primary);
        padding: 10px 0;
      }

      &:hover,
      &.on {
        /* cursor: move; */
        .delete-box {
          /* display: block; */
        }
      }

      &.on {
        cursor: move;

        .delete-box {
          display: block;
          border: 2px solid var(--prev-color-primary);
          box-shadow: 0 0 10px 0 rgba(24, 144, 255, 0.3);
        }
      }
    }

    .page-title {
      position: relative;
      height: 35px;
      line-height: 35px;
      background: #fff;
      font-size: 15px;
      color: #333333;
      text-align: center;
      width: 375px;
      margin: 0 auto;

      .delete-box {
        display: none;
        position: absolute;
        left: -2px;
        top: 0;
        width: 379px;
        height: 100%;
        border: 2px dashed var(--prev-color-primary);
        padding: 10px 0;

        span {
          position: absolute;
          right: 0;
          bottom: 0;
          width: 32px;
          height: 16px;
          line-height: 16px;
          display: inline-block;
          text-align: center;
          font-size: 10px;
          color: #fff;
          background: rgba(0, 0, 0, 0.4);
          margin-left: 2px;
          cursor: pointer;
          z-index: 11;
        }
      }

      &:hover,
      &.on {
        /* cursor: move; */
        .delete-box {
          /* display: block; */
        }
      }

      &.on {
        cursor: move;

        .delete-box {
          display: block;
          border: 2px solid var(--prev-color-primary);
          box-shadow: 0 0 10px 0 rgba(24, 144, 255, 0.3);
        }
      }
    }

    .scroll-box {
      flex: 1;
      background-color: #fff;
      width: 375px;
      margin: 0 auto;
      padding-top: 1px;
    }

    .dragArea.list-group {
      width: 100%;
      height: 100%;

      .mConfig-item {
        position: relative;
        cursor: move;
        &.hide {
          &::before {
            position: absolute;
            content: '已隐藏';
            background: rgba(0, 0, 0, 0.5);
            width: 100%;
            height: 100%;
            z-index: 99;
            color: #fff;
            display: flex;
            align-items: center;
            justify-content: center;
          }
        }
        .delete-name.on {
          background: var(--prev-color-primary-light-3);
          color: #fff;
          &::before {
            background: var(--prev-color-primary-light-3);
          }
        }
        .delete-name {
          position: absolute;
          top: 6px;
          background: #fff;
          left: -100px;
          width: 86px;
          height: 32px;
          text-align: center;
          line-height: 32px;
          font-size: 13px;
          color: #666;
          border-radius: 3px;

          .custom-badge {
            position: absolute;
            top: -6px;
            left: -16px;
            background: #ffaa18;
            color: #fff;
            font-size: 10px;
            padding: 0 5px;
            border-radius: 3px;
            line-height: 14px;
            z-index: 10;
          }

          &::before {
            content: '';
            position: absolute;
            width: 10px;
            height: 10px;
            background: #fff;
            transform: rotate(45deg);
            top: 50%;
            right: -5px;
            margin-top: -5px;
          }
        }
        .delete-box {
          display: none;
          position: absolute;
          left: -2px;
          top: 0;
          width: 379px;
          height: 100%;
          border: 2px dashed var(--prev-color-primary);

          /* padding: 10px 0; */
          .handleType {
            position: absolute;
            right: -43px;
            top: 0;
            width: 36px;
            border-radius: 4px;
            background-color: var(--prev-color-primary);
            cursor: pointer;
            color: #fff;
            font-weight: bold;
            text-align: center;
            padding: 4px 0;
            .el-tooltip {
              background-color: inherit;
              color: inherit;
            }
            .iconfont {
              padding: 5px 0;
              color: #fff;

              &.on {
                opacity: 0.4;
              }
            }
          }
        }

        &.on {
          cursor: move;

          .delete-box {
            display: block;
            border: 2px solid var(--prev-color-primary);
            box-shadow: 0 0 10px 0 rgba(24, 144, 255, 0.3);
          }
        }
      }

      .mConfig-item:hover {
        transform: scale(1.01);
        box-shadow: 0 0 10px 0 rgba(24, 144, 255, 0.3);
        transition: all 0.2s;
      }
    }
  }

  .right-box {
    max-width: 400px;
    min-width: 400px;
    height: 100%;
    border-radius: 4px;
    overflow: scroll;
    overflow-x: hidden;
    -webkit-overflow-scrolling: touch;

    :deep(.ivu-tabs-bar) {
      margin-bottom: 16px;
    }

    :deep(.el-slider__runway.show-input) {
      margin-right: 14px !important;
    }

    :deep(.el-slider__input) {
      width: 72px !important;
    }

    :deep(.el-slider__input .el-input__wrapper),
    :deep(.el-input-number:not(.el-slider__input) .el-input__wrapper) {
      padding-left: 8px;
      padding-right: 8px;
    }

    :deep(.el-slider__input .el-input__inner),
    :deep(.el-input-number:not(.el-slider__input) .el-input__inner) {
      text-align: center;
    }

    :deep(.el-input-number:not(.el-slider__input)) {
      width: 82px !important;
    }

    :deep(.numbox .acea-row.row-middle) {
      justify-content: flex-end;
    }

    :deep(.sub-settings .input-box) {
      width: 92px;
    }

    .title-bar {
      width: 100%;
      height: 45px;
      line-height: 45px;
      padding-left: 24px;
      color: #000;
      border-radius: 4px;
      border-bottom: 1px solid #eee;
      font-size: 14px;
    }
  }

  ::-webkit-scrollbar {
    width: 6px;
    background-color: transparent;
  }

  ::-webkit-scrollbar-track {
    border-radius: 10px;
  }

  ::-webkit-scrollbar-thumb {
    background-color: #bfc1c4;
  }
}

.foot-box {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 80px;
  background: #fff;
  box-shadow: 0px -2px 4px 0px rgba(0, 0, 0, 0.03);

  button {
    width: 100px;
    height: 32px;
    font-size: 13px;

    &:first-child {
      margin-right: 20px;
    }
  }
}

:deep(.ivu-scroll-loader) {
  display: none;
}

:deep(.ivu-card-body) {
  width: 100%;
  padding: 0;
  height: calc(100vh - 73px);
}

.rbtn {
  position: absolute;
  right: 20px;
}
.code {
  position: relative;
}

.QRpic {
  width: 160px;
  height: 160px;

  img {
    width: 100%;
    height: 100%;
  }
}
.contxt {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  height: 100%;
}

.contxt:hover ::-webkit-scrollbar-thumb {
  display: block;
}
.icon {
  width: 28px;
  height: 28px;
  // vertical-align: -0.15em;
  fill: currentColor;
  overflow: hidden;
}
:deep(.el-radio__label) {
  font-size: 12px !important;
}
:deep(.el-input__suffix-inner){
  line-height: 2.5;
}
</style>
