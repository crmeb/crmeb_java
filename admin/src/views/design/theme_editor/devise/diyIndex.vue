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
                dragClass="dragClass"
                filter=".search , .navbar , .homeComb , .service"
              >
                <div
                  class="list-group-item"
                  :class="{
                    search: element.cname == '搜索框',
                    navbar: element.cname == '选项卡',
                    homeComb: element.cname == '轮播搜索',
                    service: element.cname == '悬浮按钮',
                  }"
                  v-for="element in item.list"
                  :key="element.id"
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
              </draggable>
            </div>
          </div>
        </div>
        <!-- 中间自定义配置移动端页面 -->
        <div class="wrapper-con">
          <div class="content">
            <div class="contxt">
              <div class="overflowy">
                <div class="picture"><img src="@/assets/images/electric.png" /></div>
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
                      group="people"
                      @change="log"
                      filter=".top"
                      :move="onMove"
                      animation="300"
                    >
                      <div
                        class="mConfig-item"
                        :class="{
                          on: activeIndex == key,
                          top: item.name == 'search_box' || item.name == 'nav_bar',
                          hide: defaultArrays[item.num].isHide,
                        }"
                        v-for="(item, key) in mConfig"
                        :key="key"
                        @click.stop="bindconfig(item, key)"
                        :style="
                          colorTxt
                            ? 'background-color:' + colorPickerTxt + ';'
                            : 'background-color: rgba(255,255,255, 0);'
                        "
                      >
                        <component
                          :is="item.name"
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
                              :class="defaultArrays[item.num].isHide ? 'iconyincang' : 'iconxianshi'"
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
              :is="item.configName"
              @config="config"
              :activeIndex="activeIndex"
              :num="item.num"
              :index="key"
            ></component>
          </div>
        </div>
      </div>
    </el-card>
    <el-dialog :visible.sync="modal" width="540px" title="预览">
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
    <el-dialog :visible.sync="nameModal" width="470px" title="设置模版名称" :show-close="true">
      <el-input v-model="saveName" placeholder="请输入模版名称"></el-input>
      <span slot="footer" class="dialog-footer">
        <el-button v-db-click @click="nameModal = false">取 消</el-button>
        <el-button type="primary" v-db-click @click="saveModal">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script crossorigin="anonymous">
import {
  categoryList,
  themeInfo,
  themeSave,
  setDefault,
  recovery,
  diyUpdateName,
  getRoutineCode,
  saveThemeImage,
} from '@/api/theme';
import { fileUpload } from '@/api/theme';
import vuedraggable from 'vuedraggable';
import mPage from '@/views/design/theme_editor/components/mobilePage/index.js';
import mConfig from '@/views/design/theme_editor/components/mobileConfig/index.js';
import home_bottom_menu from '@/views/design/theme_editor/components/mobilePage/home_bottom_menu.vue';

import footPage from '@/views/design/theme_editor/components/pagesFoot';
import { mapState } from 'vuex';
import html2canvas from 'html2canvas';
import theme from '@/views/design/theme_editor/mixins/theme';
import Setting from '@/utils/settingMer';
import QRCode from 'qrcodejs2';

const DISABLED_JAVA_THEME_COMPONENTS = ['points_mall', 'pointsMall', 'sign_in', 'signIn', 'wechat_live', 'liveBroadcast'];

export default {
  inject: ['reload', 'setDirty'],
  name: 'index.vue',
  components: {
    footPage,
    html2canvas,
    draggable: vuedraggable,
    ...mPage,
    ...mConfig,
    home_bottom_menu,
  },
  filters: {
    filterTxt(val) {
      if (val) {
        return (val = val.substr(0, val.length - 1));
      }
    },
  },
  computed: {
    ...mapState({
      titleTxt: (state) => state.mobildConfig.pageTitle || '首页',
      showTxt: (state) => state.mobildConfig.pageShow,
      colorTxt: (state) => state.mobildConfig.pageColor,
      bgPic: (state) => state.mobildConfig.pagePic,
      picTxt: (state) => state.mobildConfig.pagePic,
      colorPickerTxt: (state) => state.mobildConfig.pageColorPicker,
      tabValTxt: (state) => state.mobildConfig.pageTabVal,
      picUrlTxt: (state) => state.mobildConfig.pagePicUrl,
      pageFooterType: (state) =>
        state.mobildConfig.pageFooter && state.mobildConfig.pageFooter.navConfig
          ? state.mobildConfig.pageFooter.navConfig.tabVal
          : 0,
      pageFooterBottom: (state) =>
        state.mobildConfig.pageFooter && state.mobildConfig.pageFooter.mbConfig
          ? state.mobildConfig.pageFooter.mbConfig.val
          : 0,
      defaultArrays: (state) => state.mobildConfig.defaultArray,
    }),
    nameTxt: {
      get() {
        return this.$store.state.mobildConfig.pageName;
      },
      set(value) {
        this.$store.commit('mobildConfig/UPNAME', value);
      },
    },
    isMicroPage() {
      return this.$route.query.page_type == 'micro';
    },
  },
  mixins: [theme],
  data() {
    return {
      BaseURL: Setting.httpUrl + '/',
      qrcodeImg: '',
      modal: false,
      clientHeight: '', //页面动态高度
      rollHeight: '',
      leftMenu: [], // 左侧菜单
      lConfig: [], // 左侧组件
      mConfig: [], // 中间组件渲染
      rConfig: [], // 右侧组件配置
      activeConfigName: '',
      propsObj: {}, // 组件传递的数据,
      activeIndex: -100, // 选中的下标
      number: 0,
      pageId: '',
      pageName: '',
      pageType: '',
      category: [],
      tabList: [
        {
          title: '组件库',
          key: 0,
        },
        {
          title: '页面链接',
          key: 1,
        },
      ],
      footActive: false,
      loading: false,
      relLoading: false,
      isSearch: false,
      isTab: false,
      isFllow: false,
      isComb: false,
      isService: false,
      visible: true,
      diyStatus: 0,
      nameModal: false,
      saveName: '',
    };
  },
  beforeCreate() {
    this.$store.commit('mobildConfig/SETEMPTY');
  },
  created() {
    this.categoryList();
    this.pageId = this.$route.query.id;
    this.pageName = this.$route.query.name;
    this.pageType = this.$route.query.type;
    this.lConfig = this.objToArr(mPage).filter((item) => !this.isDisabledThemeComponent(item));
    let imgList = {
      imgList: [require('@/assets/images/foot-005.png'), require('@/assets/images/foot-006.png')],
      name: '购物车',
      link: '/pages/order_addcart/order_addcart',
    };
    this.$nextTick(() => {
      this.$store.commit('mobildConfig/FOOTER', { title: '是否自定义', name: imgList });
      this.arraySort();
      if (this.pageId != 0 || this.$route.query.tid) {
        this.getDefaultConfig();
      } else if (this.pageType == 'home') {
        this.showTitle();
      } else {
        // 清空 vuex 中的 defaultArray
        this.$store.commit('mobildConfig/DEFAULTARRAY', {});
        this.$store.commit('mobildConfig/RESET_BOTTOM_MENU');
      }
      this.clientHeight = `${document.documentElement.clientHeight}` - 65.81; //获取浏览器可视区域高度
      let H = `${document.documentElement.clientHeight}` - 180;
      this.rollHeight = H > 650 ? 650 : H;
      let that = this;
      window.onresize = function () {
        that.clientHeight = `${document.documentElement.clientHeight}` - 65.81;
        let H = `${document.documentElement.clientHeight}` - 180;
        that.rollHeight = H > 650 ? 650 : H;
      };
    });
  },
  watch: {
    mConfig: {
      handler(nVal, oVal) {
        if (this.setDirty) {
          this.setDirty(true);
        }
      },
      deep: true,
    },
    'mConfig.length': {
      handler(nVal, oVal) {
        if (this.setDirty) {
          this.setDirty(true);
        }
      },
    },
    defaultArrays: {
      handler(nVal, oVal) {
        if (this.setDirty) {
          this.setDirty(true);
        }
      },
      deep: true,
    },
  },
  methods: {
    exportView() {
      let that = this;
      this.loading = true;
      this.$nextTick(() => {
        console.log(this.mConfig);
      });
    },
    importView() {},
    // 将远程图片（OSS等）转为 base64，绕过 html2canvas 跨域限制
    async convertImagesToBase64(container) {
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
    },

    // 保存为封面 - 截取长图并上传
    saveCover() {
      // 保存当前选中的组件索引
      const previousActiveIndex = this.activeIndex;
      this.$message.info('正在生成封面图片，请稍候...');

      setTimeout(async () => {
        try {
          // 取消选中状态
          this.activeIndex = -999;
          this.rConfig = [];

          // 等待 DOM 更新
          await this.$nextTick();

          // 获取 imgContainer 元素
          const container = document.getElementById('imgContainer');
          if (!container) {
            this.$message.error('未找到页面容器元素');
            // 恢复选中状态
            this.activeIndex = previousActiveIndex;
            this.restoreActiveComponent(previousActiveIndex);
            return;
          }

          // 将容器内所有远程图片（含OSS）预先转为 base64，解决 html2canvas 跨域白图问题
          await this.convertImagesToBase64(container);

          // 使用 html2canvas 截取长图
          const canvas = await html2canvas(container, {
            useCORS: true,
            allowTaint: true, // 已手动转为 base64，可放开 allowTaint
            logging: false,
            scale: 2, // 提高清晰度
            backgroundColor: this.colorTxt ? this.colorPickerTxt : '#ffffff',
            scrollY: -window.scrollY,
            scrollX: -window.scrollX,
            windowWidth: container.scrollWidth,
            windowHeight: container.scrollHeight,
          });

          // 将 canvas 转换为 blob
          canvas.toBlob(
            async (blob) => {
              if (!blob) {
                this.$message.error('图片生成失败');
                // 恢复选中状态
                this.activeIndex = previousActiveIndex;
                this.restoreActiveComponent(previousActiveIndex);
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
                  this.$message.success('封面保存成功！');
                  // 这里可以根据需要将图片URL保存到页面配置中
                  this.coverUrl = res.data.src;
                  if (!this.mConfig.length) return this.$message.warning('请先配置DIY数据');
                  if (!this.pageId) return this.$message.warning('请先保存DIY数据');
                  saveThemeImage(this.pageId, { image: this.coverUrl, type: this.pageType });
                } else {
                  this.$message.error(res.msg || '封面保存失败');
                }
              } catch (err) {
                this.$message.error(err.msg || '上传失败，请重试');
              } finally {
                // 恢复选中状态
                this.activeIndex = previousActiveIndex;
                this.restoreActiveComponent(previousActiveIndex);
              }
            },
            'image/png',
            0.95,
          ); // 图片质量 0.95
        } catch (error) {
          console.error('截图失败：', error);
          this.$message.error('生成封面失败，请重试');
          // 恢复选中状态
          this.activeIndex = previousActiveIndex;
          this.restoreActiveComponent(previousActiveIndex);
        }
      }, 300);
    },
    // 恢复选中的组件
    restoreActiveComponent(index) {
      if (index === -100) {
        // 恢复页面设置
        this.showTitle();
      } else if (index === -101) {
        // 恢复底部菜单
        this.showFoot();
      } else if (index === -102) {
        // 恢复底部菜单
        this.showBottomMenu();
      } else if (index >= 0 && index < this.mConfig.length) {
        // 恢复组件选中
        const item = this.mConfig[index];
        this.bindconfig(item, index);
      }
    },
    preview() {
      this.modal = true;
      this.creatQrCode(this.pageId, this.diyStatus);
      this.routineCode(this.pageId);
    },
    //小程序二维码
    routineCode(id) {
      getRoutineCode(id)
        .then((res) => {
          this.qrcodeImg = res.data.image;
        })
        .catch((err) => {
          this.$message.error(err);
        });
    },
    //生成二维码
    creatQrCode(id, status) {
      this.$refs.qrCodeUrl.innerHTML = '';
      let url = `${this.BaseURL}pages/index/index?theme_id=${this.$route.query.id}`;
      // if (status) {
      //   url = `${this.BaseURL}pages/index/index`;
      // } else {
      //   url = `${this.BaseURL}pages/annex/special/index?id=${id}`;
      // }
      var qrcode = new QRCode(this.$refs.qrCodeUrl, {
        text: url, // 需要转换为二维码的内容
        width: 160,
        height: 160,
        colorDark: '#000000',
        colorLight: '#ffffff',
        correctLevel: QRCode.CorrectLevel.H,
      });
    },
    changName(val) {
      this.$store.commit('mobildConfig/UPNAME', val);
    },
    cancel() {
      this.visible = false;
    },
    determine() {
      if (this.nameTxt.trim() == '') {
        return this.$message.error('请输入模板名称');
      }
      if (this.pageId == 0) {
        this.$message.success('修改成功');
        return false;
      }
      diyUpdateName(this.pageId, { name: this.nameTxt })
        .then((res) => {
          this.visible = false;
          this.$message.success(res.msg);
        })
        .catch((err) => {
          this.$message.error(err.msg);
        });
      this.visible = false;
    },
    returnTap() {
      this.$msgbox({
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
          this.$router.push(this.isMicroPage ? '/design/micro_theme' : '/design/my_theme');
        })
        .catch(() => {});
    },
    leftRemove({ to, from, item, clone, oldIndex, newIndex }) {
      if (this.isSearch && newIndex == 0) {
        if (item._underlying_vm_.name == 'z_wechat_attention') {
          this.isFllow = true;
        } else {
          this.$store.commit('mobildConfig/ARRAYREAST', this.mConfig[0].num);
          this.mConfig.splice(0, 1);
        }
      }
      if ((this.isFllow = true && newIndex >= 1)) {
        this.$store.commit('mobildConfig/ARRAYREAST', this.mConfig[0].num);
      }
    },
    onMove(e) {
      if (e.relatedContext.element.name == 'search_box') return false;
      if (e.relatedContext.element.name == 'nav_bar') return false;
      if (e.relatedContext.element.name == 'home_comb') return false;
      return true;
    },
    onCopy() {
      this.$message.success('复制成功');
    },
    onError() {
      this.$message.error('复制失败');
    },
    checkCanAddComponent(item) {
      if (this.isDisabledThemeComponent(item)) {
        this.$message.warning('当前 Java 版本暂不支持该组件');
        return false;
      }
      if (item.name == 'search_box') {
        if (this.isSearch) {
          this.$message.error('该组件只能添加一次');
          return false;
        }
        if (this.isComb) {
          this.$message.error('轮播搜索不能和搜索组件与选项卡组件同时存在');
          return false;
        }
      }
      if (item.name == 'nav_bar') {
        if (this.isTab) {
          this.$message.error('该组件只能添加一次');
          return false;
        }
        if (this.isComb) {
          this.$message.error('轮播搜索不能和搜索组件与选项卡组件同时存在');
          return false;
        }
      }
      if (item.name == 'home_comb') {
        if (this.isComb) {
          this.$message.error('该组件只能添加一次');
          return false;
        }
        if (this.isSearch || this.isTab) {
          this.$message.error('轮播搜索不能和搜索组件与选项卡组件同时存在');
          return false;
        }
      }
      if (item.name == 'home_service' && this.isService) {
        this.$message.error('该组件只能添加一次');
        return false;
      }
      return true;
    },
    isDisabledThemeComponent(item) {
      if (!item) return false;
      return DISABLED_JAVA_THEME_COMPONENTS.includes(item.name) || DISABLED_JAVA_THEME_COMPONENTS.includes(item.defaultName);
    },
    sanitizeThemeValue(value) {
      let result = {};
      Object.keys(value || {}).forEach((key) => {
        if (!this.isDisabledThemeComponent(value[key])) {
          result[key] = value[key];
        }
      });
      return result;
    },
    sanitizeThemeComponent(value) {
      if (!value || this.isDisabledThemeComponent(value)) return null;
      return JSON.parse(JSON.stringify(value));
    },
    removeThemeComponent(value, name) {
      Object.keys(value || {}).forEach((key) => {
        if (value[key] && value[key].name == name) {
          delete value[key];
        }
      });
      return value;
    },
    appendBottomMenu(value) {
      let bottomMenu = this.sanitizeThemeComponent(this.$store.state.mobildConfig.bottomMenu);
      if (!bottomMenu || bottomMenu.name != 'bottomMenu') return value;
      let timestamp = bottomMenu.timestamp || new Date().getTime() * 1000;
      bottomMenu.timestamp = timestamp;
      bottomMenu.id = bottomMenu.id || 'id' + timestamp;
      value[timestamp] = bottomMenu;
      return value;
    },
    updateSpecialComponentStatus(name, status) {
      if (name == 'search_box') {
        this.isSearch = status;
      }
      if (name == 'nav_bar') {
        this.isTab = status;
      }
      if (name == 'home_comb') {
        this.isComb = status;
      }
      if (name == 'home_service') {
        this.isService = status;
      }
    },
    //设置默认数据
    setmoren() {
      setDefault(this.pageId)
        .then((res) => {
          this.$message.success(res.msg);
        })
        .catch((err) => {
          this.$message.error(err.msg);
        });
    },
    //恢复默认
    getmoren() {
      recovery(this.pageId)
        .then((res) => {
          this.$message.success(res.msg);
          this.reload();
        })
        .catch((err) => {
          this.$message.error(err.msg);
        });
    },
    // 页面标题点击
    showTitle() {
      this.activeIndex = -100;
      let obj = {};
      for (var i in mConfig) {
        if (i == 'pageTitle') {
          // this.rConfig = obj
          obj = mConfig[i];
          obj.configName = mConfig[i].name;
          obj.cname = '页面设置';
        }
      }
      let abc = obj;
      this.rConfig = [];
      this.rConfig[0] = JSON.parse(JSON.stringify(obj));
    },
    // 页面底部点击
    showFoot() {
      this.activeIndex = -101;
      let obj = {};
      for (var i in mConfig) {
        if (i == 'pageFoot') {
          // this.rConfig = obj
          obj = mConfig[i];
          obj.configName = mConfig[i].name;
          obj.cname = '底部菜单';
        }
      }
      let abc = obj;
      this.rConfig = [];
      this.rConfig[0] = JSON.parse(JSON.stringify(obj));
    },
    showBottomMenu() {
      this.activeIndex = -102;
      let obj = {};
      for (var i in mConfig) {
        if (i == 'c_bottom_menu') {
          // this.rConfig = obj
          obj = mConfig[i];
          obj.configName = mConfig[i].name;
          obj.cname = '底部菜单';
        }
      }
      this.rConfig = [];
      this.rConfig.push(JSON.parse(JSON.stringify(obj)));
    },
    // 对象转数组
    objToArr(data) {
      let obj = Object.keys(data || {});
      let m = obj.map((key) => data[key]);
      return m;
    },
    log(evt) {
      // 中间拖拽排序
      if (evt.moved) {
        if (evt.moved.element.name == 'search_box') {
          return this.$message.warning('该组件禁止拖拽');
        }
        // if (evt.moved.element.name == "nav_bar") {
        //     return this.$message.warning("该组件禁止拖拽");
        // }
        evt.moved.oldNum = this.mConfig[evt.moved.oldIndex].num;
        evt.moved.newNum = this.mConfig[evt.moved.newIndex].num;
        evt.moved.status = evt.moved.oldIndex > evt.moved.newIndex;
        this.mConfig.forEach((el, index) => {
          el.num = new Date().getTime() * 1000 + index;
        });
        evt.moved.list = this.mConfig;
        this.rConfig = [];
        let item = evt.moved.element;
        let tempItem = JSON.parse(JSON.stringify(item));
        this.rConfig.push(tempItem);
        this.activeIndex = evt.moved.newIndex;
        this.$store.commit('mobildConfig/SETCONFIGNAME', item.name);
        this.$store.commit('mobildConfig/defaultArraySort', evt.moved);
      }
      // 从左向右拖拽排序
      if (evt.added) {
        let data = evt.added.element;
        if (!this.checkCanAddComponent(data)) {
          this.mConfig.splice(evt.added.newIndex, 1);
          return;
        }
        let obj = {};
        let timestamp = new Date().getTime() * 1000;
        data.num = timestamp;
        this.activeConfigName = data.name;
        let tempItem = JSON.parse(JSON.stringify(data));
        tempItem.id = 'id' + tempItem.num;
        this.mConfig[evt.added.newIndex] = tempItem;
        this.rConfig = [];
        this.rConfig.push(tempItem);
        this.updateSpecialComponentStatus(data.name, true);
        this.mConfig.forEach((el, index) => {
          el.num = new Date().getTime() * 1000 + index;
        });
        evt.added.list = this.mConfig;
        this.activeIndex = evt.added.newIndex;
        // 保存组件名称
        this.$store.commit('mobildConfig/SETCONFIGNAME', data.name);
        this.$store.commit('mobildConfig/defaultArraySort', evt.added);
      }
    },
    cloneDog(data) {
      // this.mConfig.push(tempItem)
      return {
        ...data,
      };
    },
    //数组元素互换位置
    swapArray(arr, index1, index2) {
      arr[index1] = arr.splice(index2, 1, arr[index1])[0];
      return arr;
    },
    //点击上下移动；
    movePage(item, index, type) {
      if (type) {
        if (index == 0) {
          return;
        }
      } else {
        if (index == this.mConfig.length - 1) {
          return;
        }
      }
      if (item.name == 'search_box' || item.name == 'nav_bar' || item.name == 'home_comb') {
        return this.$message.warning('该组件禁止移动');
      }
      if (type) {
        if (
          this.mConfig[index - 1].name == 'search_box' ||
          this.mConfig[index - 1].name == 'nav_bar' ||
          this.mConfig[index - 1].name == 'home_comb'
        ) {
          return this.$message.warning('搜索框或选项卡或轮播搜索必须为顶部');
        }
        this.swapArray(this.mConfig, index - 1, index);
      } else {
        this.swapArray(this.mConfig, index, index + 1);
      }
      let obj = {};
      this.rConfig = [];
      obj.oldIndex = index;
      if (type) {
        obj.newIndex = index - 1;
      } else {
        obj.newIndex = index + 1;
      }
      this.mConfig.forEach((el, index) => {
        el.num = new Date().getTime() * 1000 + index;
      });
      let tempItem = JSON.parse(JSON.stringify(item));
      this.rConfig.push(tempItem);
      obj.element = item;
      obj.list = this.mConfig;
      if (type) {
        this.activeIndex = index - 1;
      } else {
        this.activeIndex = index + 1;
      }

      this.$store.commit('mobildConfig/SETCONFIGNAME', item.name);
      this.$store.commit('mobildConfig/defaultArraySort', obj);
    },
    // 组件添加
    addDomCon(item, type, index) {
      if (!this.checkCanAddComponent(item)) {
        return false;
      }
      this.updateSpecialComponentStatus(item.name, true);
      let obj = {};
      let timestamp = new Date().getTime() * 1000;
      item.num = `${timestamp}`;
      item.id = `id${timestamp}`;
      this.activeConfigName = item.name;
      let tempItem = JSON.parse(JSON.stringify(item));
      if (item.name == 'home_comb') {
        this.rConfig = [];
        this.mConfig.unshift(tempItem);
        this.activeIndex = 0;
        this.rConfig.push(tempItem);
      } else if (item.name == 'search_box') {
        this.rConfig = [];
        this.mConfig.unshift(tempItem);
        this.activeIndex = 0;
        this.rConfig.push(tempItem);
      } else if (item.name == 'nav_bar') {
        this.rConfig = [];
        if (this.mConfig[0] && this.mConfig[0].name === 'search_box') {
          this.mConfig.splice(1, 0, tempItem);
          this.activeIndex = 1;
        } else {
          this.mConfig.splice(0, 0, tempItem);
          this.activeIndex = 0;
        }
        this.rConfig.push(tempItem);
      } else {
        if (type) {
          this.rConfig = [];
          if (this.activeIndex == 0 && this.mConfig[1] && this.mConfig[1].name == 'nav_bar') {
            this.activeIndex = 2;
          } else {
            this.activeIndex = this.activeIndex >= 0 ? this.activeIndex + 1 : this.mConfig.length;
          }
          this.mConfig.splice(this.activeIndex, 0, tempItem);
          this.rConfig.push(tempItem);
        } else {
          this.mConfig.splice(index + 1, 0, tempItem);
          this.activeIndex = index;
        }
      }
      this.mConfig.forEach((el, index) => {
        el.num = new Date().getTime() * 1000 + index;
      });
      // 保存组件名称
      obj.element = item;
      obj.list = this.mConfig;
      this.$store.commit('mobildConfig/SETCONFIGNAME', item.name);
      this.$store.commit('mobildConfig/defaultArraySort', obj);
      return true;
    },
    //中间页点击添加模块；
    bindAddDom(item, type, index) {
      // 复制
      if (type == 0) {
        let defaultArray = this.$store.state.mobildConfig.defaultArray;
        let configData = JSON.parse(JSON.stringify(defaultArray[item.num]));

        // 找到原始配置以获取 cname, icon 等基础信息
        let baseItem = item;
        this.lConfig.forEach((j) => {
          if (item.name == j.name) {
            baseItem = j;
          }
        });

        // 先调用 addDomCon 添加一个新组件，然后立即用 configData 覆盖它
        if (!this.addDomCon(baseItem, type, index)) {
          return;
        }

        // 获取刚添加的组件（在 index+1 位置，因为 addDomCon 是 splice(index+1, 0, ...)）
        let newIndex = index + 1;
        let newItem = this.mConfig[newIndex];

        // 重新获取最新的 defaultArray (引用)
        let currentDefaultArray = this.$store.state.mobildConfig.defaultArray;

        // 保留新生成的 num 和 timestamp 相关字段，覆盖其他配置
        let newConfig = {
          ...configData,
          num: newItem.num,
          id: newItem.id, // 确保使用新生成的唯一ID
          timestamp: currentDefaultArray[newItem.num].timestamp,
        };

        // 提交更新
        this.$store.commit('mobildConfig/UPDATEARR', { num: newItem.num, val: newConfig });
      } else {
        let i = item;
        this.lConfig.forEach((j) => {
          if (item.name == j.name) {
            i = j;
          }
        });
        if (!this.addDomCon(i, type, index)) {
          return;
        }
      }
    },
    //左边配置模块点击添加；
    addDom(item, type) {
      this.addDomCon(item, type);
    },
    // 点击显示相应的配置
    bindconfig(item, index) {
      this.rConfig = [];
      let tempItem = JSON.parse(JSON.stringify(item));
      this.rConfig.push(tempItem);
      this.activeIndex = index;
      this.$store.commit('mobildConfig/SETCONFIGNAME', item.name);
    },
    bindHide(item) {
      let obj = this.$store.state.mobildConfig.defaultArray;
      let num = this.rConfig[0].num;
      obj[num].isHide = !obj[num].isHide;
      this.$store.commit('mobildConfig/UPDATEARR', { num: num, val: obj[num] });
    },
    // 组件删除
    bindDelete(item, key) {
      this.$confirm('确定要删除此组件吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          this.updateSpecialComponentStatus(item.name, false);
          this.mConfig.splice(key, 1);
          this.rConfig.splice(0, 1);
          if (this.mConfig.length != key) {
            this.rConfig.push(this.mConfig[key]);
          } else {
            if (this.mConfig.length) {
              this.activeIndex = key - 1;
              this.rConfig.push(this.mConfig[key - 1]);
            } else {
              this.showTitle();
            }
          }
          // 删除第几个配置
          this.$store.commit('mobildConfig/DELETEARRAY', item);
        })
        .catch(() => {});
    },
    // 组件返回
    config(data) {
      let propsObj = this.propsObj;
      propsObj.data = data;
      propsObj.name = this.activeConfigName;
    },
    addSort(arr, index1, index2) {
      arr[index1] = arr.splice(index2, 1, arr[index1])[0];
      return arr;
    },
    // 数组排序
    arraySort() {
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
      this.lConfig.map((el, index) => {
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
          if (this.pageType !== 'home' && this.pageType !== 'user') {
            if (el.name === 'home_product_info' || el.cname === '商品信息') {
              goods.list.unshift(el);
            } else {
              goods.list.push(el);
            }
          }
        }
        if (el.type == 4) {
          if (this.pageType !== 'home' && this.pageType !== 'detail') user.list.push(el);
        }
      });
      tempArr.push(basis, marketing);
      if (this.pageType !== 'home' && this.pageType !== 'user') tempArr.push(goods);
      if (this.pageType !== 'home' && this.pageType !== 'detail') tempArr.push(user);
      tempArr.push(tool);
      this.leftMenu = tempArr;
    },
    // toImage(val){
    //     html2canvas(this.$refs.imgContainer,{
    //         useCORS:true,
    //         logging:true,
    //         taintTest: false,
    //         backgroundColor: null
    //     }).then((canvas) => {
    //         let imgUrl = canvas.toDataURL('image/jpeg');
    //         this.diySaveDate(val,imgUrl)
    //     });
    // },
    diySaveDate(val, num, title, save) {
      let pageData = {};
      if (['home', 'detail', 'user'].includes(this.pageType)) {
        pageData = {
          type: this.pageType,
          value: val,
          title: this.titleTxt,
          name: this.nameTxt || '模板',
          is_show: this.showTxt ? 1 : 0,
          is_bg_color: this.colorTxt ? 1 : 0,
          is_bg_pic: this.bgPic ? 1 : 0,
          color_picker: this.colorPickerTxt,
          bg_pic: this.picUrlTxt,
          cover_pic: this.coverUrl,
        };
      } else if (['category', 'theme'].includes(this.pageType)) {
        pageData = val;
      }
      let requestData = {
        type: this.pageType,
        value: pageData,
      };
      if (this.$route.query.page_type === 'micro') {
        requestData.page_type = 'micro';
      }
      if (title) {
        requestData.title = title;
      }
      if (this.$route.query.tid) {
        requestData.tid = this.$route.query.tid;
      }
      themeSave(title ? 0 : this.pageId, requestData)
        .then((res) => {
          if (this.pageId != res.data.id && !title) {
            let query = { ...this.$route.query, id: res.data.id };
            delete query.tid; // 保存后移除 tid
            this.$router.replace({ query });
            this.pageId = res.data.id;
          }
          this.$message.success(res.msg);
          let that = this;
          this.nameModal = false;
          if (num == 2) {
            this.relLoading = false;
            setTimeout(() => {
              let page = this.isMicroPage
                ? '/design/micro_theme'
                : '/design/my_theme';
              window.location.replace(page);
            }, 2000);
          } else {
            this.loading = false;
          }
          if (this.setDirty) {
            this.setDirty(false);
          }
        })
        .catch((res) => {
          this.relLoading = false;
          this.loading = false;
          this.$message.error(res.msg);
        });
    },
    saveModal() {
      if (!this.saveName) return this.$message.warning('请先输入模板名称');
      this.saveConfig(1, this.saveName);
    },
    closeWindow() {
      this.$msgbox({
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
            // this.saveConfig();
            window.close();
          }, 1000);
        })
        .catch(() => {});
    },
    // 保存配置
    saveConfig(num, type, save) {
      if (this.mConfig.length == 0) {
        return this.$message.error('暂未添加任何组件，保存失败！');
      }
      if (num == 1) {
        this.loading = true;
      } else {
        this.relLoading = true;
      }
      let val = this.sanitizeThemeValue(this.$store.state.mobildConfig.defaultArray);
      if (!this.footActive && this.pageType == 'home') {
        let timestamp = new Date().getTime() * 1000;
        val[timestamp] = this.$store.state.mobildConfig.pageFooter;
        this.footActive = true;
      } else if (this.pageType == 'detail') {
        val = this.appendBottomMenu(this.removeThemeComponent(val, 'bottomMenu'));
      }
      this.$nextTick(() => {
        this.diySaveDate(val, num, type, save);
      });
    },
    // 获取默认配置
    getDefaultConfig() {
      let id = this.pageId;
      if (id == 0 && this.$route.query.tid) {
        id = this.$route.query.tid;
      }
      themeInfo(id, this.pageType).then((res) => {
        let obj = {};
        let tempARR = [];
        let data = res.data;
        this.$store.commit('mobildConfig/titleUpdata', data.title);
        this.$store.commit('mobildConfig/nameUpdata', data.name);
        this.$store.commit('mobildConfig/showUpdata', data.is_show);
        this.$store.commit('mobildConfig/colorUpdata', data.is_bg_color || 0);
        this.$store.commit('mobildConfig/picUpdata', data.is_bg_pic || 0);
        this.$store.commit('mobildConfig/pickerUpdata', data.color_picker || '#f5f5f5');
        this.$store.commit('mobildConfig/radioUpdata', data.bg_tab_val || 0);
        this.$store.commit('mobildConfig/picurlUpdata', data.bg_pic || '');
        this.diyStatus = data.status;
        let newArr = this.objToArr(data.value).filter((item) => !this.isDisabledThemeComponent(item));

        function sortNumber(a, b) {
          return a.timestamp - b.timestamp;
        }
        newArr.sort(sortNumber);
        let hasBottomMenu = false;
        newArr.map((el, index) => {
          if (el.name == 'headerSerch') {
            this.isSearch = true;
          }
          if (el.name == 'tabNav') {
            this.isTab = true;
          }
          if (el.name == 'homeComb') {
            this.isComb = true;
          }
          if (el.name == 'customerService') {
            this.isService = true;
          }
          if (el.name == 'goodList') {
            // let storage = window.localStorage;
            // storage.setItem(el.timestamp, el.selectConfig.activeValue);
          }
          if (el.name == 'bottomMenu') {
            hasBottomMenu = true;
            this.$store.commit('mobildConfig/UPBOTTOMMENU', el);
            return;
          }
          el.id = 'id' + el.timestamp;
          this.lConfig.map((item, j) => {
            if (el.name == item.defaultName) {
              item.num = el.timestamp;
              item.id = 'id' + el.timestamp;
              let tempItem = JSON.parse(JSON.stringify(item));
              tempARR.push(tempItem);
              obj[el.timestamp] = el;
              this.mConfig.push(tempItem);
              // 保存默认组件配置
              this.$store.commit('mobildConfig/ADDARRAY', {
                num: el.timestamp,
                val: el,
              });
            }
          });
        });
        let objs = newArr[newArr.length - 1] || {};

        if (this.pageType == 'home' && objs.name == 'pageFoot') {
          this.$store.commit('mobildConfig/footPageUpdata', objs);
          this.showTitle();
        } else if (this.pageType == 'detail' && !hasBottomMenu) {
          this.$store.commit('mobildConfig/RESET_BOTTOM_MENU');
        }
      });
    },
    categoryList() {
      categoryList((res) => {
        this.category = res.data;
      });
    },
    // 重置
    reast() {
      if (this.pageId == 0) {
        this.$message.error('新增页面，无法重置');
      } else {
        this.$confirm('此操作将清空模板内容, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }).then((res) => {
          this.mConfig = [];
          this.rConfig = [];
          this.activeIndex = -99;
          this.getDefaultConfig();
        });
      }
    },
    getBadgeText(val) {
      const map = {
        user: '用户',
        article: '文章',
        coupon: '优惠券',
        goods: '商品',
      };
      return map[val] || '';
    },
  },
  beforeDestroy() {
    this.$store.commit('mobildConfig/titleUpdata', '');
    this.$store.commit('mobildConfig/nameUpdata', '');
    this.$store.commit('mobildConfig/showUpdata', 1);
    this.$store.commit('mobildConfig/colorUpdata', 0);
    this.$store.commit('mobildConfig/picUpdata', 0);
    this.$store.commit('mobildConfig/pickerUpdata', '#f5f5f5');
    this.$store.commit('mobildConfig/radioUpdata', 0);
    this.$store.commit('mobildConfig/picurlUpdata', '');
    this.$store.commit('mobildConfig/SETEMPTY');
  },
  destroyed() {
    this.$store.commit('mobildConfig/titleUpdata', '');
    this.$store.commit('mobildConfig/nameUpdata', '');
    this.$store.commit('mobildConfig/showUpdata', 1);
    this.$store.commit('mobildConfig/colorUpdata', 0);
    this.$store.commit('mobildConfig/picUpdata', 0);
    this.$store.commit('mobildConfig/pickerUpdata', '#f5f5f5');
    this.$store.commit('mobildConfig/radioUpdata', 0);
    this.$store.commit('mobildConfig/picurlUpdata', '');
    this.$store.commit('mobildConfig/SETEMPTY');
  },
};
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
::v-deep .el-card__body {
  padding: 0;
}
::v-deep {
  .icondel_1,
  .upload-box {
    cursor: pointer;
  }
  .el-checkbox,
  .el-radio {
    margin-bottom: 15px;
    margin-right: 15px;
  }
}
.c_label {
  margin-top: 0;
}
::v-deep .el-button--small {
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
// ::v-deep .c_row-item {
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

    ::v-deep .ivu-tabs-bar {
      margin-bottom: 16px;
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

::v-deep .ivu-scroll-loader {
  display: none;
}

::v-deep .ivu-card-body {
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
::v-deep .el-radio__label {
  font-size: 12px !important;
}
::v-deep .el-input__suffix-inner{
  line-height: 2.5;
}
</style>
