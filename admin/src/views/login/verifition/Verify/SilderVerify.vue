<template>
  <div class="silder-range" :class="rangeStatus ? 'success' : ''">
    <i @mousedown="rangeMove" :class="rangeStatus ? successIcon : startIcon"></i>
    {{ rangeStatus ? successText : startText }}
  </div>
</template>
<script>
export default {
  props: {
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
  },
  name: 'SilderVerify',
  data() {
    return {
      rangeStatus: '',
      startMoveTime: '', //移动开始的时间
      endMovetime: '', //移动结束的时间
      successText: '',
    };
  },
  methods: {
    rangeMove(e) {
      let ele = e.target;
      let startX = e.clientX;
      let eleWidth = ele.offsetWidth;
      let parentWidth = ele.parentElement.offsetWidth;
      let MaxX = parentWidth - eleWidth;
      if (this.rangeStatus) {
        //不运行
        return false;
      }
      document.onmousemove = (e) => {
        let endX = e.clientX;
        this.disX = endX - startX;
        if (this.disX <= 0) {
          this.disX = 0;
        }
        if (this.disX >= MaxX - eleWidth) {
          //减去滑块的宽度,体验效果更好
          this.disX = MaxX;
        }
        ele.style.transition = '.1s all';
        ele.style.transform = 'translateX(' + this.disX + 'px)';
        e.preventDefault();
        this.startMoveTime = new Date().getTime();
      };
      document.onmouseup = () => {
        if (this.disX !== MaxX) {
          ele.style.transition = '.5s all';
          ele.style.transform = 'translateX(0)';

          this.$emit('failed', this.rangeStatus);
        } else {
          this.endMovetime = new Date().getTime();
          this.rangeStatus = true;
          this.successText = `${((this.endMovetime - this.startMoveTime) / 1000).toFixed(2)}s验证成功`;
          this.$emit('success', this.rangeStatus);
        }
        document.onmousemove = null;
        document.onmouseup = null;
      };
    },
  },
};
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
