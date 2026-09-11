<template>
  <div class="hot_imgs">
    <div class="title" v-if="configData.title">
      {{ configData.title }}
    </div>
    <div
      class="type-switch"
      style="margin-bottom: 15px"
      v-if="
        (defaults.name == 'menus' ||
          defaults.name == 'member' ||
          configNme == 'menuConfig' ||
          configNme == 'shortcutConfig') &&
        configNme != 'assetConfig'
      "
    >
      <div class="type-title">{{ configData.listStyleName || '图文内容' }}</div>
      <el-radio-group v-if="configData.listStyle != -1" v-model="configData.listStyle">
        <el-radio :label="0" :value="0">图片</el-radio>
        <el-radio :label="1" :value="1">图标</el-radio>
      </el-radio-group>
    </div>
    <div class="type-switch" style="margin-bottom: 15px" v-if="configNme == 'assetConfig'">
      <!-- <span class="type-title">展示样式</span> -->
      <el-radio-group v-model="configData.listStyle">
        <el-radio v-if="configData.assetMode == 0" :label="2" :value="2">数字(上)</el-radio>
        <el-radio v-if="configData.assetMode == 0" :label="3" :value="3">数字(左)</el-radio>
        <el-radio :label="0" :value="0">图片</el-radio>
        <el-radio :label="1" :value="1">图标</el-radio>
      </el-radio-group>
    </div>
    <div class="list-box">
      <draggable
        class="dragArea list-group"
        :list="configData.list"
        :item-key="getDraggableItemKey"
        group="peoples"
        handle=".move-icon"
      >
        <template #item="{ element: item, index }">
          <div class="item">
            <div class="delect-btn" @click.stop="bindDelete(item, index)" v-if="!configData.isCube">
              <span class="iconfont icondel_1"></span>
            </div>
            <div class="move-icon">
              <span class="iconfont iconxingzhuangjiehe"></span>
            </div>
            <div
              class="img-box"
              @click="modalPicTap('单选', index)"
              v-if="configData.listStyle == 0 || configData.listStyle == -1"
            >
              <img :src="item.img" alt="" v-if="item.img" />
              <div class="upload-box" v-else>
                <i class="el-icon-plus"></i>
              </div>
            </div>
            <div class="img-box" @click="openIconModal(index)" v-else-if="configData.listStyle == 1">
              <div class="upload-box" v-if="!item.icon">
                <i class="el-icon-plus"></i>
              </div>
              <div
                style="
                  width: 100%;
                  height: 100%;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  background: #fff;
                  border: 1px solid #eee;
                  border-radius: 4px;
                "
                v-else
              >
                <span :class="['mb-iconfont', item.icon]" style="font-size: 30px; color: #666"></span>
              </div>
            </div>
            <div class="info">
              <div class="info-item" v-for="(infos, key) in item.info" :key="key">
                <span class="span">{{ infos.title }}</span>
                <div class="input-box">
                  <el-input v-model="infos.value" :placeholder="infos.tips" :maxlength="infos.max">
                    <template #suffix>
                      <i
                        v-if="infos.title == '链接'"
                        class="el-icon-link"
                        @blur="onBlur"
                        @click="getLink(index, key, item.info)"
                      />
                    </template>
                  </el-input>
                </div>
              </div>
              <div class="info-item" v-if="configData.type">
                <span class="span">状态</span>
                <el-switch v-model="item.show" />
              </div>
            </div>
          </div>
        </template>
      </draggable>
      <div>
        <el-dialog v-model="modalPic" width="1024px" title="上传图片">
          <uploadPictures
            :isChoice="isChoice"
            @getPic="getPic"
            :gridBtn="gridBtn"
            :gridPic="gridPic"
            v-if="modalPic"
          ></uploadPictures>
        </el-dialog>
      </div>
    </div>
    <template v-if="configData.list">
      <div class="add-btn" v-if="configData.list && configData.list.length < configData.maxList">
        <el-button class="btn" type="primary" ghost @click="addBox">
          <span class="iconfont iconjiahao"></span>{{ configData.bnt }}
        </el-button>
      </div>
    </template>
    <linkaddress ref="linkaddresRef" @linkUrl="linkUrl"></linkaddress>
    <c_icon_select ref="iconSelectRef" @select="handleIconSelect"></c_icon_select>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue';
import draggable from 'vuedraggable';
import uploadPictures from '@/views/design/theme_editor/components/uploadPictures';
import linkaddress from '@/components/linkaddress';
import c_icon_select from './c_icon_select.vue';
import { getDraggableItemKey } from '@/utils/draggableKey';

defineOptions({ name: 'c_menu_list' });

const props = defineProps({
  configObj: {
    type: Object,
  },
  configNme: {
    type: String,
  },
  index: {
    type: null,
  },
});

const defaults = ref({});
const configData = ref({});
const menus = ref([]);
const list = ref([
  {
    title: 'aa',
    val: '',
  },
]);
const modalPic = ref(false);
const isChoice = ref('单选');
const gridBtn = ref({
  xl: 4,
  lg: 8,
  md: 8,
  sm: 8,
  xs: 8,
});
const gridPic = ref({
  xl: 6,
  lg: 8,
  md: 12,
  sm: 12,
  xs: 12,
});
const activeIndex = ref(0);
const indexLast = ref(0);
const lastObj = ref({});
const linkaddresRef = ref(null);
const iconSelectRef = ref(null);

onMounted(() => {
  nextTick(() => {
    defaults.value = props.configObj;
    configData.value = props.configObj[props.configNme] || {};
    if (configData.value && configData.value.listStyle === undefined) {
      configData.value.listStyle = 0;
    }
  });
});

watch(
  () => props.configObj,
  (nVal, oVal) => {
    defaults.value = nVal;
    configData.value = nVal[props.configNme] || {};
    if (configData.value && configData.value.listStyle === undefined) {
      configData.value.listStyle = 0;
    }
  },
  { deep: true },
);

function linkUrl(e) {
  configData.value.list[activeIndex.value].info[indexLast.value].value = e;
  if (defaults.value.name == 'pictureCube') {
    defaults.value.picStyle.picList[defaults.value.picStyle.tabVal].link = e;
  }
}
function getLink(index, key, item) {
  indexLast.value = item.length - 1;
  // if (key != item.length - 1) {
  //   return;
  // }
  activeIndex.value = index;
  linkaddresRef.value.modals = true;
}
function addBox() {
  if (!Array.isArray(configData.value.list)) configData.value.list = [];
  const lastItem = configData.value.list[configData.value.list.length - 1] || lastObj.value;
  configData.value.list.push(createEmptyListItem(lastItem));
}

function createEmptyListItem(item) {
  const template = item && Array.isArray(item.info) && item.info.length ? item : getDefaultListItem();
  const nextItem = JSON.parse(JSON.stringify(template));
  nextItem.img = '';
  nextItem.type = 0;
  nextItem.show = true;
  nextItem.icon = '';
  nextItem.info.forEach((info) => {
    info.value = '';
  });
  return nextItem;
}

function getDefaultListItem() {
  const info = [
    {
      title: '标题',
      value: '',
      tips: '选填，不超过4个字',
      max: 4,
    },
  ];

  if (props.configNme === 'memberConfig' || props.configNme === 'rightEntryConfig') {
    info.push({
      title: '描述',
      value: '',
      tips: '选填，不超过6个字',
      max: 6,
    });
  }

  if (props.configNme !== 'ms2RightsList') {
    info.push({
      title: '链接',
      value: '',
      tips: '请输入链接',
      max: 100,
    });
  }

  return {
    img: '',
    type: 0,
    show: true,
    icon: '',
    info,
  };
}
// 点击图文封面
function modalPicTap(title, index) {
  activeIndex.value = index;
  modalPic.value = true;
}
function openIconModal(index) {
  activeIndex.value = index;
  iconSelectRef.value.show();
}
function handleIconSelect(icon) {
  configData.value.list[activeIndex.value].icon = icon;
}
// 添加自定义弹窗
function addCustomDialog(editorId) {
  window.UE.registerUI(
    'test-dialog',
    function (editor, uiName) {
      let dialog = new window.UE.ui.Dialog({
        iframeUrl: '/admin/widget.images/index.html?fodder=dialog',
        editor: editor,
        name: uiName,
        title: '上传图片',
        cssRules: 'width:1200px;height:500px;padding:20px;',
      });
      this.dialog = dialog;
      // 参考上面的自定义按钮
      var btn = new window.UE.ui.Button({
        name: 'dialog-button',
        title: '上传图片',
        cssRules: `background-image: url(../../../assets/images/icons.png);background-position: -726px -77px;`,
        onclick: function () {
          // 渲染dialog
          dialog.render();
          dialog.open();
        },
      });

      return btn;
    },
    37,
  );
}
// 获取图片信息
function getPic(pc) {
  nextTick(() => {
    configData.value.list[activeIndex.value].img = pc.att_dir;
    let data = defaults.value.menuConfig;
    if (data && data.isCube) {
      defaults.value.picStyle.picList.splice(defaults.value.picStyle.tabVal, 1, {
        image: pc.att_dir,
        link: data.list[0].info[0].value,
      });
    }
    modalPic.value = false;
  });
}
function onBlur() {
  let data = defaults.value.menuConfig;
  defaults.value.picStyle.picList[defaults.value.picStyle.tabVal].link = data.list[0].info[0].value;
}
// 删除
function bindDelete(item, index) {
  if (configData.value.list.length == 1) {
    lastObj.value = configData.value.list[0];
  }
  configData.value.list.splice(index, 1);
}
</script>

<style scoped lang="scss">
:deep(.ivu-input-icon ){
  color: #bbbbbb;
}

:deep(.ivu-input-word-count ){
  color: #bbbbbb;
}

.hot_imgs {
  margin: 0 15px 20px 15px;
  .type-title {
    font-size: 12px;
    color: #999;
    margin-right: 30px;
  }

  :deep(.el-radio) {
    margin-bottom: 0px;
  }
  .title {
    padding-bottom: 21px;
    color: #999;
    font-size: 12px;
  }
  .type-switch {
    display: flex;
    align-items: center;
  }
  .list-box {
    .item {
      position: relative;
      display: flex;
      background: #f9f9f9;
      align-items: center;
      padding: 16px 20px 16px 0;
      margin-bottom: 16px;
      border-radius: 3px;

      .delect-btn {
        position: absolute;
        right: -13px;
        top: -16px;

        .iconfont {
          font-size: 25px;
          color: #ccc;
        }
      }

      .move-icon {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 30px;
        cursor: move;
      }

      .img-box {
        position: relative;
        width: 64px;
        height: 64px;

        img {
          width: 100%;
          height: 100%;
          border-radius: 3px;
        }
      }

      .info {
        flex: 1;
        margin-left: 22px;

        .info-item {
          display: flex;
          align-items: center;
          margin-bottom: 10px;

          &:nth-last-child(1) {
            margin-bottom: 0;
          }

          .span {
            width: 40px;
            font-size: 12px;
            color: #999;
          }

          .input-box {
            flex: 1;
          }
        }
      }
    }
  }

  .add-btn {
    margin-top: 10px;

    .btn {
      width: 100%;
      height: 36px;
      color: #fff;

      .iconfont {
        font-size: 11px;
        margin-right: 5px;
      }
    }
  }
}

.upload-box {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: #fff;
  border-radius: 4px;
  border: 1px solid #eeeeee;
  color: #ccc;
}

.iconfont {
  color: #dddddd;
  font-size: 16px;
}
</style>
