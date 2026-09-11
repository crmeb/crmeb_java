<template>
  <div>
    <el-dialog
      title="编辑热区"
      v-model="dialogVisible"
      fullscreen
      append-to-body
      custom-class="hotpot-dialog"
      @opened="openModal"
      @close="handleDialogClose"
    >
      <div class="operationFloor">
        <div class="imgBox" @mouseup.left.stop="changeStop()">
          <div ref="containerRef" class="container" id="img-box-container">
            <img
              ref="backgroundImgRef"
              :src="imageSrc"
              ondragstart="return false;"
              oncontextmenu="return false;"
              onselect="document.selection.empty();"
              alt="img"
              @mousedown.left.stop="mouseDown($event)"
            />
            <!--draw hotpot-->
            <div
              v-show="caseShow"
              :style="{
                width: areaWidth + 'px',
                height: areaHeight + 'px',
                left: starX + 'px',
                top: starY + 'px',
              }"
              class="area"
            />
            <!--be hotpot-->
            <AreaBox
              v-for="(item, index) in areaData"
              :area-data-index="index"
              :key="'area' + index"
              :link="item.link"
              :title="item.title"
              :type="parseInt(item.type)"
              v-model:area-init="areaData[index]"
              :parent-width="parentWidth"
              :parent-height="parentHeight"
              @delAreaBox="delAreaBox"
              @addURL="addURL"
            />
          </div>
        </div>
        <!-- 热区链接配置 -->
        <div class="form">
          <div class="acea-row row-middle">
            <div class="mb12 titleTop acea-row row-middle">
              <span>热区管理</span>
              <span class="ml5 iconfont iconrequwenzitishi"></span>
              <img class="ml5" :src="qipaokuangImg" alt="" />
              <div class="ml5 title-text">可框选热区范围，双击设置热区信息</div>
            </div>
          </div>
          <div class="actions">
            <el-button type="primary" @click="addAreaBox">添加热区</el-button>
            <el-button @click="resetAreaBox">重置</el-button>
          </div>
          <div v-for="(item, index) in areaData" :key="index" class="form-row">
            <!-- <span class="iconfont iconrequbianji"></span> -->
            <el-input :maxlength="6" class="item-input" v-model="areaData[index].name"></el-input>
            <div class="form-item label">
              <div @click="getLink(index)">
                <el-input :value="item.link" class="toLink" :style="linkInputStyle" readonly placeholder="选择跳转链接">
                  <template #suffix>
                    <i class="iconfont iconlianjietubiao"> </i>
                  </template>
                </el-input>
              </div>
            </div>
            <i class="el-icon-delete" @click="delAreaBox(index)" />
          </div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-actions">
          <el-button @click="cancelAreaData">取消</el-button>
          <el-button type="primary" @click="saveAreaData">确定</el-button>
        </div>
      </template>
    </el-dialog>
    <linkaddress ref="linkaddresRef" @linkUrl="linkUrl"></linkaddress>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, nextTick, getCurrentInstance } from 'vue';
import AreaBox from './AreaBox';
import linkaddress from '@/components/linkaddress';
import qipaokuangImg from '@/assets/imgs/qipaokuang.png';

defineOptions({ name: 'OperationFloor' });

const props = defineProps({
  /**
   * @description 图片数据对象
   * @type {ImgData}
   */
  imgs: {
    type: [String, Array], // 图片
    default: '',
  },
  /**
   * @description 是否为热门汤品
   * @type {boolean}
   */
  isHotPot: {
    type: Boolean, // 布尔类型
    default: () => false, // 默认值为false
  },
  /**
   * @description 图片区域数据对象
   * @type {AreaData[]}
   */
  imgAreaData: {
    type: Array, // 数组类型
    default: () => [], // 默认值为空数组
  },
  /**
   * @description 链接输入框样式对象
   * @type {LinkInputStyle}
   */
  linkInputStyle: {
    type: Object, // 对象类型
    default: () => ({
      // 默认值为一个包含width属性的对象
      width: '260px',
      height: '32px',
      borderRadius: '4px',
    }),
  },
});

const emit = defineEmits(['saveAreaData']);

const { proxy } = getCurrentInstance();

const containerRef = ref(null);
const backgroundImgRef = ref(null);
const linkaddresRef = ref(null);

const dialogVisible = ref(false);
const starX = ref(0);
const starY = ref(0);
const areaWidth = ref(0);
const areaHeight = ref(0);
const caseShow = ref(false);
const nowImgWidth = ref(null);
const areaData = ref([]);
const imgNum = ref(1);
const parentWidth = ref(0);
const parentHeight = ref(0);
const defaultWidth = ref(750);
const itemIndex = ref(0);
const nowNum = ref(0);

function open() {
  dialogVisible.value = true;
}

defineExpose({ open });

const imageSrc = computed(() => {
  if (Array.isArray(props.imgs)) {
    const first = props.imgs[0] || {};
    return first.img || first.url || first.att_dir || first.sattDir || '';
  }
  return props.imgs || '';
});

watch(
  () => props.imgAreaData,
  (val) => {
    areaData.value = [...(val || [])];
  },
);

onMounted(() => {
  areaData.value = [...(props.imgAreaData || [])];
});

//添加热区
function addAreaBox() {
  if (areaData.value.length === 50) return;
  nowNum.value++;
  let starXVal = ((nowNum.value - 1) % 4) * 114;
  let starYVal = Math.floor((nowNum.value - 1) / 4) * 114;
  if (starYVal > parentHeight.value - 114) {
    starYVal = parentHeight.value - 114;
  }
  areaData.value.push({
    starX: starXVal,
    starY: starYVal,
    name: `热区${nowNum.value}`,
    areaWidth: 114,
    areaHeight: 114,
    nowImgWidth: defaultWidth.value,
    link: '',
  });
}

//重置热区
function resetAreaBox() {
  nowNum.value = 0;
  areaData.value = [];
}

function cancelAreaData() {
  areaData.value = [...props.imgAreaData];
  dialogVisible.value = false;
}

function saveAreaData() {
  emit('saveAreaData', areaData.value);
  dialogVisible.value = false;
}

function handleDialogClose() {
  document.onmousemove = null;
  caseShow.value = false;
}

function openModal() {
  nextTick(() => {
    const parentDiv = containerRef.value;
    parentWidth.value = defaultWidth.value;
    parentHeight.value = parentDiv ? parentDiv.clientHeight : 450;
  });
}

function closeModal() {
  // 原 this.$Modal.confirm 为 iView/element-ui 旧 API，迁移后保留调用（proxy.$Modal 可能未注册，属预存问题）
  proxy.$Modal.confirm({
    title: '提示信息',
    content: '<p>未保存内容，是否在离开前放弃保存？</p>',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      proxy.$Modal.remove();
      dialogVisible.value = false;
    },
  });
}

// 绘画热区开始
function mouseDown(e) {
  e.preventDefault();
  caseShow.value = true;
  // 记录滑动的初始值
  starX.value = e.layerX;
  starY.value = e.layerY;
  // 鼠标滑动的过程
  if (!document.onmousemove) {
    let maxWidth = defaultWidth.value - e.layerX;
    document.onmousemove = (ev) => {
      if (ev.layerX - starX.value < maxWidth) {
        areaWidth.value = ev.layerX - starX.value;
      } else {
        areaWidth.value = maxWidth;
      }
      areaHeight.value = ev.layerY - starY.value;
    };
  }
}

// 绘画热区结束
function changeStop() {
  document.onmousemove = null;
  imgNum.value = areaData.value.length + 1;
  if (caseShow.value && areaWidth.value > 10 && areaHeight.value > 10) {
    const data = {
      name: `热区${imgNum.value}`,
      starX: starX.value,
      starY: starY.value,
      areaWidth: areaWidth.value < 50 ? 50 : areaWidth.value,
      areaHeight: areaHeight.value < 50 ? 50 : areaHeight.value,
      nowImgWidth: defaultWidth.value,
      link: '',
    };
    areaData.value.push(data);
  }
  // 初始化绘图
  caseShow.value = false;
  starX.value = 0;
  starY.value = 0;
  areaWidth.value = 0;
  areaHeight.value = 0;
}

// 删除指定热区
function delAreaBox(index) {
  /* 删除某个热区 */
  areaData.value.splice(index, 1);
  // this.$emit('delAreaData', this.areaData);
  /* 删除后 每个热区按顺序重新编号 */
  if (areaData.value) {
    const arr = areaData.value.filter((i) => i.number > index);
    if (!arr) return;
    arr.forEach((i) => i.number--);
    if (areaData.value[areaData.value.length - 1]) {
      imgNum.value = areaData.value[areaData.value.length - 1].number + 1;
    } else {
      imgNum.value = 1;
    }
  }
}

// 添加网址
function addURL(index, url) {
  let obj = {
    ...areaData.value[index],
    link: url,
  };
  areaData.value[index] = obj;
}

/**
 * 检查列表中每个元素是否都有 link 属性
 * @param {Array} list - 待检查的列表
 * @returns {Boolean} - 是否所有元素都有 link 属性
 */
function checkData(list) {
  let isCheck = true;
  list.some((val) => {
    if (!val.link) {
      isCheck = false;
    }
  });
  return isCheck;
}

/**
 * @description 获取链接地址并打开添加链接的模态框
 * @param {number} index - 当前项的索引值
 */
function getLink(index) {
  // 设置当前项的索引值
  itemIndex.value = index;
  // 打开添加链接的模态框
  // this.$refs.linkaddres.currenType = 'link';
  // this.$refs.linkaddres.mockData('link');
  linkaddresRef.value.modals = true;
}

/**
 * @description 处理链接地址的输入事件
 * @param {string} e - 链接地址
 */
function linkUrl(e) {
  // 将链接地址存储到对应的数据项中
  areaData.value[itemIndex.value].link = e;
}
</script>

<style scoped lang="scss">
.btn {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 16px 0;
}

.dialog-footer {
  text-align: right;
  margin-top: 20px;
  margin-right: 20px;
}

.operationFloor {
  display: flex;
  position: relative;
  gap: 24px;
  height: calc(100vh - 138px);
  overflow: hidden;
  .header {
    .titleBox {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 100px;

      .name {
        font-size: 13px;
        font-weight: bold;
      }
    }

    .textBox {
      font-size: 12px;
      color: #777;
      margin-bottom: 10px;
    }
  }

  .imgBox::-webkit-scrollbar {
    display: none;
    /* Chrome Safari */
  }

  .imgBox {
    flex: 1;
    display: flex;
    justify-content: center;
    min-width: 0;
    height: 100%;
    overflow: auto;
    background: #f7f8fa;

    .container {
      position: relative;
      align-self: flex-start;
      border: 1px solid #ebeef5;
      background: #fff;
    }

    img {
      cursor: crosshair;
      display: block;
      width: 750px;
      max-width: none;
    }

    .area {
      position: absolute;
      width: 200px;
      height: 200px;
      left: 200px;
      top: 300px;
      background: rgba(#2980b9, 0.3);
      border: 1px dashed #34495e;
    }
  }
}

.form {
  font-size: 12px;
  flex: 0 0 420px;
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;
  padding: 4px 4px 80px 0;
  box-sizing: border-box;
  .actions {
    margin-bottom: 8px;
  }
  .form-row {
    display: flex;
    margin: 12px 0;
    align-items: center;
    padding: 12px;
    background: #f9f9f9;
    border-radius: 4px;
    .form-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      white-space: nowrap;
      margin: 0 10px;
      font-size: 12px;

      .num {
        width: 69px;
        color: #999;
        font-size: 12px;
      }

      .label {
        color: #c7c7c7;
      }
    }
  }

  .el-icon-delete {
    font-size: 16px;
    cursor: pointer;
  }
}

.setHot-text {
  border: 1px solid #abdcff;
  background-color: #f0faff;
  padding: 5px 10px;
}

.toLink {
  cursor: pointer;
}

.item-input {
  width: 94px;
}
.titleTop {
  color: #333;
  font-size: 16px;
  position: relative;
  .title-text {
    position: absolute;
    color: #999;
    font-size: 12px;
    font-weight: 400;
    left: 100px;
  }
}
:deep(.el-input__suffix) {
  line-height: 30px !important;
}
.iconrequwenzitishi {
  color: #2d8cf0;
}
.ml5 {
  margin-left: 5px;
}
.mb12 {
  margin-bottom: 12px;
}
:deep(.el-input__inner) {
  padding: 0 8px !important;
}
:deep(.hotpot-dialog) {
  .el-dialog__body {
    height: calc(100vh - 110px);
    padding: 16px 24px;
    overflow: hidden;
    box-sizing: border-box;
  }
  .el-dialog__footer {
    padding: 10px 24px 16px;
    border-top: 1px solid #ebeef5;
  }
}
.dialog-actions {
  text-align: right;
}
</style>

<style lang="scss">
.hotpot-dialog {
  .el-dialog__body {
    height: calc(100vh - 110px);
    padding: 16px 24px;
    overflow: hidden;
    box-sizing: border-box;
  }
  .el-dialog__footer {
    padding: 10px 24px 16px;
    border-top: 1px solid #ebeef5;
  }
}
</style>
