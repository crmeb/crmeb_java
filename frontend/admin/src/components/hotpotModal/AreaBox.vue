<template>
  <div
    :style="{
      width: areaInit.areaWidth + 'px',
      height: areaInit.areaHeight + 'px',
      left: areaInit.starX + 'px',
      top: areaInit.starY + 'px',
    }"
    class="areaBox"
    @mousedown.left.stop="mouseDownLint($event)"
    @mouseup.left.stop="mouseUp($event)"
    @dblclick="editBoxShow = true"
  >
    <div class="prompt-text">
      <div class="prompt-item num">{{ areaInit.name }}</div>
      <div class="prompt-item" :style="{ color: isSet ? '#2d8cf0' : '#f00' }">
        {{ isSet ? '(已设置)' : '(未设置)' }}
      </div>
    </div>
    <!--删除-->
    <div class="del" @click.stop="del()">
      <i class="el-icon-close"></i>
    </div>
    <!--形变点-->
    <div class="shape" @mousedown.left.stop="shapeDown($event)" @mouseup.left.stop="mouseUp($event)" />
    <!--编辑框-->

    <!-- 修改框 -->
    <el-dialog title="设置热区" v-model="editBoxShow" width="560px" append-to-body custom-class="hotpot-area-dialog">
      <div class="area-set">
        <div class="area-label">热区跳转链接：</div>
        <div class="area-content">
          <el-input v-model="url" readonly placeholder="选择跳转链接">
            <template #suffix>
              <i class="iconfont iconlianjietubiao" @click="getLink"> </i>
            </template>
          </el-input>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editBoxShow = false">取消</el-button>
          <el-button type="primary" @click="addURL">确定</el-button>
        </span>
      </template>
    </el-dialog>
    <linkaddress ref="linkaddresRef" @linkUrl="linkUrl"></linkaddress>
    <img class="right-bottom" :src="rightBottomImg" alt="" />
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, getCurrentInstance } from 'vue';
import { ElMessage, ElMessageBox } from '@/utils/elementPlusFeedback';
import linkaddress from '@/components/linkaddress';
import rightBottomImg from '@/assets/imgs/rightBottom.png';

defineOptions({ name: 'AreaBox' });

const props = defineProps({
  areaInit: {
    type: Object,
    default: () => {},
  },
  areaDataIndex: {
    type: Number,
    default: null,
  },
  link: {
    type: String,
    default: '',
  },
  title: {
    type: String,
    default: '',
  },
  type: {
    type: Number,
    default: -1,
  },
  parentWidth: {
    type: Number,
    default: 0,
  },
  parentHeight: {
    type: Number,
    default: 0,
  },
});

const emit = defineEmits(['delAreaBox', 'addURL']);

const areaTitle = ref('');
const url = ref('');
// box操作初始点
const move = ref({
  // 拖动
  startX: 0,
  starY: 0,
  // 形变
  start1X: 0,
  start1Y: 0,
});
const editBoxShow = ref(false);
const itemIndex = ref('');
const linkaddresRef = ref(null);

// 拖动/形变临时记录点（原 this.starX/starY/star1X/star1Y）
const starX = ref(0);
const starY = ref(0);
const star1X = ref(0);
const star1Y = ref(0);

const isSet = computed(() => {
  return !!props.link;
});

watch(
  () => props.title,
  (val) => {
    areaTitle.value = val;
  },
);

watch(
  () => props.link,
  (val) => {
    url.value = val;
  },
);

onMounted(() => {
  url.value = props.link;
});

function getLink() {
  // 打开添加链接的模态框
  linkaddresRef.value.modals = true;
}

function handleClose(done) {
  ElMessageBox.confirm('确认关闭？', '提示', { type: 'warning' })
    .then((_) => {
      done();
    })
    .catch((_) => {});
}

// 删除
function del() {
  emit('delAreaBox', props.areaDataIndex);
}

// 结束拖动/变形
function mouseUp() {
  document.onmousemove = null;
}

// 形变开始
function shapeDown(e) {
  e.preventDefault();

  star1X.value = e.clientX;
  star1Y.value = e.clientY;
  // 获取左部和底部的偏移量

  if (!document.onmousemove) {
    const initX = props.areaInit.areaWidth;
    const initY = props.areaInit.areaHeight;
    document.onmousemove = (ev) => {
      props.areaInit.areaWidth = initX + ev.clientX - star1X.value;
      if (props.areaInit.areaWidth < 50) {
        props.areaInit.areaWidth = 50;
      }
      props.areaInit.areaHeight = initY + ev.clientY - star1Y.value;
      if (props.areaInit.areaHeight < 50) {
        props.areaInit.areaHeight = 50;
      }
    };
  }
}

function linkUrl(e) {
  url.value = e;
}

function addURL() {
  if (!url.value) return ElMessage.error('请选择跳转链接');
  emit('addURL', props.areaDataIndex, url.value);
  editBoxShow.value = false;
}

// 开始拖动限制范围
function mouseDownLint(e) {
  e.preventDefault();
  starX.value = e.clientX;
  starY.value = e.clientY;
  const childrenDiv = e.target || e;
  //获取子元素的宽高
  let childrenWidth = childrenDiv.getBoundingClientRect().width;
  let childrenHight = childrenDiv.getBoundingClientRect().height;
  if (!document.onmousemove) {
    const initX = props.areaInit.starX;
    const initY = props.areaInit.starY;

    document.onmousemove = (ev) => {
      // 移动位置
      let nLeft = initX + ev.clientX - starX.value;
      let nTop = initY + ev.clientY - starY.value;
      nLeft = nLeft <= 0 ? 0 : nLeft; //判断左边是否越界
      nTop = nTop <= 0 ? 0 : nTop; //判断上边是否越界
      let nRight = nLeft + childrenWidth;
      let nBottom = nTop + childrenHight;
      // 判断右边是否越界
      if (nRight >= props.parentWidth) {
        nLeft = props.parentWidth - childrenWidth;
      }
      // 判断下边是否越界
      if (nBottom >= props.parentHeight) {
        nTop = props.parentHeight - childrenHight;
      }
      props.areaInit.starX = nLeft;
      props.areaInit.starY = nTop;
    };
  }
}

// 开始拖动不限制范围
function mouseDown(e) {
  e.preventDefault();
  starX.value = e.clientX;
  starY.value = e.clientY;
  if (!document.onmousemove) {
    const initX = props.areaInit.starX;
    const initY = props.areaInit.starY;
    document.onmousemove = (ev) => {
      props.areaInit.starX = initX + ev.clientX - starX.value;
      props.areaInit.starY = initY + ev.clientY - starY.value;
    };
  }
}
</script>

<style scoped lang="scss">
.areaBox {
  position: absolute;
  background: rgba(24, 144, 255, 0.5);
  border: 1px dashed #1890ff;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #1989fa;
  font-size: 12px;
  box-sizing: border-box;
  cursor: move;

  .prompt-text {
    overflow: hidden;
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    max-width: 100%;
    max-height: 100%;
    text-align: center;
    align-items: center;
    color: #fff;

    .num {
      font-size: 12px;
    }

    .prompt-item {
      color: #fff;
      margin: 0 2px;
    }
  }

  .del {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 16px;
    height: 16px;
    line-height: 16px;
    font-size: 12px;
    background: #1890ff;
    color: #fff;
    text-align: center;
    border-radius: 0 0 0 3px;
    position: absolute;
    right: 7px;
    top: 7px;
    transform: translate3d(50%, -50%, 0);
    cursor: pointer;
  }

  .del:hover {
    width: 16px;
    height: 16px;
    line-height: 16px;
  }

  .shape {
    position: absolute;
    width: 7px;
    height: 7px;
    background: transparent;
    right: 0;
    bottom: 0;
    transform: translate3d(50%, 50%, 0);
    cursor: nwse-resize;
  }
}

.area-set {
  display: flex;
  align-items: center;
  margin: 16px 0;
}

.area-label {
  width: 100px;
}

.area-content {
  flex: 1;
}
.el-input.el-input--small {
  width: 83%;
}
.el-form-item {
  margin-bottom: 0 !important;
}
:deep(.el-form-item__label) {
  font-weight: 500 !important;
}
.right-bottom {
  position: absolute;
  right: 2px;
  bottom: 2px;
}
:deep(.el-dialog__body) {
  overflow-y: hidden;
  padding: 30px 24px 20px 24px;
}
</style>
