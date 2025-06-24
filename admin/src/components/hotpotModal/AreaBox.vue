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
    <el-dialog title="设置热区" :visible.sync="editBoxShow" width="540px" :append-to-body="true">
      <el-form>
        <el-form-item label="热区跳转链接:">
          <div @click="getLink">
            <el-input v-model="link" readonly><i class="iconfont iconlianjietubiao" slot="suffix"> </i></el-input>
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editBoxShow = false">取消</el-button>
        <el-button type="primary" @click="editBoxShow = false">确定</el-button>
      </span>
    </el-dialog>
    <linkaddress ref="linkaddres" @linkUrl="linkUrl"></linkaddress>
    <img class="right-bottom" src="@/assets/imgs/rightBottom.png" alt="" />
  </div>
</template>

<script>
import linkaddress from '@/components/linkaddress';
export default {
  name: 'AreaBox',
  components: { linkaddress },
  props: {
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
  },
  data() {
    return {
      areaTitle: '',
      url: '',
      // box操作初始点
      move: {
        // 拖动
        startX: 0,
        starY: 0,
        // 形变
        start1X: 0,
        start1Y: 0,
      },
      editBoxShow: false,
      itemIndex: '',
    };
  },
  computed: {
    isSet() {
      return !!this.link;
    },
  },
  watch: {
    title(val) {
      this.areaTitle = val;
    },
    link(val) {
      this.url = val;
    },
  },
  mounted() {
    this.url = this.link;
  },
  methods: {
    getLink() {
      // 打开添加链接的模态框
      this.$refs.linkaddres.dialogVisible = true;
    },
    handleClose(done) {
      this.$confirm('确认关闭？', '提示', { type: 'warning' })
        .then((_) => {
          done();
        })
        .catch((_) => {});
    },
    // 删除
    del() {
      this.$emit('delAreaBox', this.areaDataIndex);
    },

    // 结束拖动/变形
    mouseUp() {
      document.onmousemove = null;
    },
    // 形变开始
    shapeDown(e) {
      e.preventDefault();

      this.star1X = e.clientX;
      this.star1Y = e.clientY;
      // 获取左部和底部的偏移量

      if (!document.onmousemove) {
        const initX = this.areaInit.areaWidth;
        const initY = this.areaInit.areaHeight;
        document.onmousemove = (ev) => {
          this.areaInit.areaWidth = initX + ev.clientX - this.star1X;
          if (this.areaInit.areaWidth < 50) {
            this.areaInit.areaWidth = 50;
          }
          this.areaInit.areaHeight = initY + ev.clientY - this.star1Y;
          if (this.areaInit.areaHeight < 50) {
            this.areaInit.areaHeight = 50;
          }
        };
      }
    },
    linkUrl(e) {
      // 将链接地址存储到对应的数据项中
      this.$emit('addURL', this.areaDataIndex, e);
    },
    // 开始拖动限制范围
    mouseDownLint(e) {
      e.preventDefault();
      this.starX = e.clientX;
      this.starY = e.clientY;
      const childrenDiv = e.target || e;
      //获取子元素的宽高
      let childrenWidth = childrenDiv.getBoundingClientRect().width;
      let childrenHight = childrenDiv.getBoundingClientRect().height;
      if (!document.onmousemove) {
        const initX = this.areaInit.starX;
        const initY = this.areaInit.starY;

        document.onmousemove = (ev) => {
          // 移动位置
          let nLeft = initX + ev.clientX - this.starX;
          let nTop = initY + ev.clientY - this.starY;
          nLeft = nLeft <= 0 ? 0 : nLeft; //判断左边是否越界
          nTop = nTop <= 0 ? 0 : nTop; //判断上边是否越界
          let nRight = nLeft + childrenWidth;
          let nBottom = nTop + childrenHight;
          // 判断右边是否越界
          if (nRight >= this.parentWidth) {
            nLeft = this.parentWidth - childrenWidth;
          }
          // 判断下边是否越界
          if (nBottom >= this.parentHeight) {
            nTop = this.parentHeight - childrenHight;
          }
          this.areaInit.starX = nLeft;
          this.areaInit.starY = nTop;
        };
      }
    },
    // 开始拖动不限制范围
    mouseDown(e) {
      e.preventDefault();
      this.starX = e.clientX;
      this.starY = e.clientY;
      if (!document.onmousemove) {
        const initX = this.areaInit.starX;
        const initY = this.areaInit.starY;
        document.onmousemove = (ev) => {
          this.areaInit.starX = initX + ev.clientX - this.starX;
          this.areaInit.starY = initY + ev.clientY - this.starY;
        };
      }
    },
  },
};
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
::v-deep .el-form-item__label {
  font-weight: 500 !important;
}
.right-bottom {
  position: absolute;
  right: 2px;
  bottom: 2px;
}
::v-deep .el-dialog__body {
  overflow-y: hidden;
  padding: 30px 24px 20px 24px;
}
</style>
