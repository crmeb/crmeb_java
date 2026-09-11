<template>
  <div class="footer" v-if="footConfig">
    <p class="tips">图片建议宽度81*81px；鼠标拖拽左侧圆点可调整导航顺序</p>
    <draggable
      class="dragArea list-group"
      :list="footConfig"
      :item-key="getDraggableItemKey"
      group="peoples"
      handle=".iconfont"
    >
      <template #item="{ element: item, index }">
        <div class="box-item">
          <div class="left-tool">
            <span class="iconfont iconxingzhuangjiehe"></span>
          </div>
          <div class="right-wrapper">
            <div class="acea-row" v-if="navStyle != 1">
              <div class="title">图标</div>
              <div class="img-wrapper">
                <div class="img-item" v-for="(img, j) in item.imgList" :key="j" @click="modalPicTap(index, j)">
                  <div class="pictrue" v-if="img">
                    <img :src="img" alt="" />
                    <p class="txt">替换</p>
                  </div>
                  <div class="empty-img" v-else>
                    <span class="iconfont iconjiahao"></span>
                  </div>
                  <div class="name">{{ j == 0 ? '选中' : '未选中' }}</div>
                </div>
              </div>
            </div>
            <div class="c_row-item" v-if="navStyle != 2">
              <el-col class="label" :span="4"> 名称 </el-col>
              <el-col class="slider-box" :span="20">
                <el-input v-model="item.name" placeholder="选填不超过10个字" />
              </el-col>
            </div>
            <div class="c_row-item">
              <el-col class="label" :span="4"> 链接 </el-col>
              <el-col class="slider-box" :span="20">
                <div>
                  <el-input v-model="item.link" placeholder="选填不超过10个字">
                    <template #suffix>
                      <i class="el-icon-link" @click="getLink(index)" />
                    </template>
                  </el-input>
                </div>
              </el-col>
            </div>
          </div>
          <div class="del-box" @click="deleteMenu(index)">
            <span class="iconfont iconcha"></span>
          </div>
        </div>
      </template>
    </draggable>
    <el-button class="add-btn" @click="addMenu" v-if="footConfig.length < 5">+ 添加板块</el-button>
    <div>
      <el-dialog v-model="modalPic" width="1024px" title="上传底部菜单" :mask-closable="false">
        <uploadPictures
          :isChoice="isChoice"
          @getPic="getPic"
          :gridBtn="gridBtn"
          :gridPic="gridPic"
          v-if="modalPic"
        ></uploadPictures>
      </el-dialog>
    </div>
    <linkaddress ref="linkaddresRef" @linkUrl="linkUrl"></linkaddress>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue';
import { ElMessageBox } from 'element-plus';
import draggable from 'vuedraggable';
import { useMobildConfigStore } from '@/store/modules/mobildConfig';
import uploadPictures from '@/views/design/theme_editor/components/uploadPictures';
import linkaddress from '@/components/linkaddress';
import noPic from '@/assets/imgs/no.png';
import { getDraggableItemKey } from '@/utils/draggableKey';

defineOptions({ name: 'c_foot' });

const props = defineProps({
  configObj: {
    type: Object,
    default: function () {
      return {};
    },
  },
  configNme: {
    type: String,
    default: '',
  },
});

const mobildConfigStore = useMobildConfigStore();

const val1 = ref('');
const val2 = ref('');
const footConfig = ref([]);
const modalPic = ref(false);
const isChoice = ref('单选');
const itemIndex = ref(0);
const itemChildIndex = ref(0);
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
const navStyle = ref(0);
const linkaddresRef = ref(null);

watch(
  () => props.configObj,
  (nVal, oVal) => {
    footConfig.value = nVal[props.configNme];
    navStyle.value = nVal.navStyleConfig.tabVal;
  },
  { deep: true },
);

footConfig.value = props.configObj[props.configNme];

function linkUrl(e) {
  footConfig.value[itemIndex.value].link = e;
}
function getLink(index) {
  itemIndex.value = index;
  linkaddresRef.value.modals = true;
}
// 点击图文封面
function modalPicTap(parent, child) {
  itemIndex.value = parent;
  itemChildIndex.value = child;
  modalPic.value = true;
}
// 获取图片信息
function getPic(pc) {
  nextTick(() => {
    footConfig.value[itemIndex.value].imgList[itemChildIndex.value] = pc.att_dir;
    modalPic.value = false;
    mobildConfigStore.footUpdata(footConfig.value);
  });
}
// 添加模块
function addMenu() {
  let obj = {
    imgList: [noPic, noPic],
    name: '自定义',
    link: '',
  };
  footConfig.value.push(obj);
}
function deleteMenu(index) {
  ElMessageBox({
    title: '提示',
    message: '是否确定删除该菜单',
    showCancelButton: true,
    cancelButtonText: '取消',
    confirmButtonText: '删除',
    iconClass: 'el-icon-warning',
    confirmButtonClass: 'btn-custom-cancel',
  })
    .then(() => {
      footConfig.value.splice(index, 1);
    })
    .catch(() => {});
}
</script>

<style scoped lang="scss">
:deep(.ivu-input ){
  font-size: 12px !important;
}
.dragArea {
  padding-bottom: 20px;
}
.footer {
  padding: 0 15px;
}
.tips {
  font-size: 12px;
  color: #bbbbbb;
}
.box-item {
  position: relative;
  display: flex;
  margin-top: 15px;
  padding: 20px 20px 20px 0;
  background: #f9f9f9;
  border-radius: 3px;
  .del-box {
    position: absolute;
    right: -13px;
    top: -18px;
    cursor: pointer;
    .iconfont {
      color: #ccc;
      font-size: 24px;
    }
  }
}
.left-tool {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  .iconfont {
    color: #999;
    font-size: 18px;
    cursor: move;
  }
}
.right-wrapper {
  flex: 1;
  .title {
    color: #999999;
    font-size: 12px;
    width: 50px;
    margin-top: 23px;
  }
  .img-wrapper {
    display: flex;
    .img-item {
      width: 64px;
      margin-right: 20px;
      .name {
        color: #bbbbbb;
        font-size: 12px;
        text-align: center;
        margin-top: 7px;
      }
      .pictrue {
        width: 100%;
        height: 64px;
        cursor: pointer;
        border: 1px solid #eeeeee;
        position: relative;
        border-radius: 3px;
      }
      img {
        display: block;
        width: 100%;
        height: 100%;
      }
      .empty-img {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
        width: 100%;
        height: 100%;
        font-size: 12px;
        color: #bfbfbf;
        border: 1px solid #eeeeee;
        border-radius: 3px;
        .iconfont {
          font-size: 24px;
        }
      }
      .txt {
        position: absolute;
        left: 0;
        bottom: 0;
        width: 100%;
        height: 22px;
        line-height: 22px;
        text-align: center;
        background: rgba(0, 0, 0, 0.5);
        color: #fff;
        font-size: 12px;
        border-radius: 0 0 3px 3px;
      }
    }
  }
  .c_row-item {
    margin-top: 10px;
    .label {
      color: #999999;
      font-size: 12px;
    }
  }
}
.add-btn {
  width: 100%;
  height: 40px;
  margin-bottom: 30px;
}
</style>
