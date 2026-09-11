<template>
  <div class="silder-range" :class="rangeStatus ? 'success' : ''">
    <i @mousedown="rangeMove" :class="rangeStatus ? successIcon : startIcon"></i>
    {{ rangeStatus ? successText : startText }}
  </div>
</template>
<script setup>
import { ref } from 'vue';

defineOptions({ name: 'SilderVerify' });

defineProps({
  //成功图标
  successIcon: {
    type: String,
    default: 'el-icon-success',
  },
  //成功文字
  // successText: {
  //   type: String,
  //   default: "验证成功"
  // },
  //开始的图标
  startIcon: {
    type: String,
    default: 'el-icon-d-arrow-right',
  },
  //开始的文字
  startText: {
    type: String,
    default: '拖动滑块到最右侧',
  },
});

const emit = defineEmits(['failed', 'success']);

const rangeStatus = ref('');
const startMoveTime = ref(''); //移动开始的时间
const endMovetime = ref(''); //移动结束的时间
const successText = ref('');
let disX = 0;

function rangeMove(e) {
  let ele = e.target;
  let startX = e.clientX;
  let eleWidth = ele.offsetWidth;
  let parentWidth = ele.parentElement.offsetWidth;
  let MaxX = parentWidth - eleWidth;
  if (rangeStatus.value) {
    //不运行
    return false;
  }
  document.onmousemove = (e) => {
    let endX = e.clientX;
    disX = endX - startX;
    if (disX <= 0) {
      disX = 0;
    }
    if (disX >= MaxX - eleWidth) {
      //减去滑块的宽度,体验效果更好
      disX = MaxX;
    }
    ele.style.transition = '.1s all';
    ele.style.transform = 'translateX(' + disX + 'px)';
    e.preventDefault();
    startMoveTime.value = new Date().getTime();
  };
  document.onmouseup = () => {
    if (disX !== MaxX) {
      ele.style.transition = '.5s all';
      ele.style.transform = 'translateX(0)';

      emit('failed', rangeStatus.value);
    } else {
      endMovetime.value = new Date().getTime();
      rangeStatus.value = true;
      successText.value = `${((endMovetime.value - startMoveTime.value) / 1000).toFixed(2)}s验证成功`;
      emit('success', rangeStatus.value);
    }
    document.onmousemove = null;
    document.onmouseup = null;
  };
}
</script>
<style scoped>
.silder-range {
  background-color: #e3e4e6;
  position: relative;
  transition: 1s all;
  user-select: none;
  color: #333;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 45px; /*no*/
  border-radius: 28px;
}
.silder-range i {
  position: absolute;
  left: 0;
  width: 60px; /*no*/
  height: 100%;
  color: #919191;
  background-color: #fff;
  border: 1px solid #bbb;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 28px;
}
.silder-range.success {
  background-color: #7ac23c;
  color: #fff;
  border-radius: 28px;
}
.silder-range.success i {
  color: #7ac23c;
  border-radius: 28px;
}
</style>
