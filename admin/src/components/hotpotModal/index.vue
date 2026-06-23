<template>
  <div>
    <el-dialog
      title="编辑热区"
      :visible.sync="dialogVisible"
      fullscreen
      append-to-body
      custom-class="hotpot-dialog"
      @opened="openModal"
      @close="handleDialogClose"
    >
      <div class="operationFloor">
        <div class="imgBox" @mouseup.left.stop="changeStop()">
          <div ref="container" class="container" id="img-box-container">
            <img
              ref="backgroundImg"
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
              :area-init.sync="item"
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
              <img class="ml5" src="@/assets/imgs/qipaokuang.png" alt="" />
              <div class="ml5 title-text">可框选热区范围，双击设置热区信息</div>
            </div>
          </div>
          <div class="actions">
            <el-button type="primary" size="small" @click="addAreaBox">添加热区</el-button>
            <el-button size="small" @click="resetAreaBox">重置</el-button>
          </div>
          <div v-for="(item, index) in areaData" :key="index" class="form-row">
            <!-- <span class="iconfont iconrequbianji"></span> -->
            <el-input :maxlength="6" class="item-input" v-model="item.name"></el-input>
            <div class="form-item label">
              <div @click="getLink(index)">
                <el-input :value="item.link" class="toLink" :style="linkInputStyle" readonly placeholder="选择跳转链接">
                  <i class="iconfont iconlianjietubiao" slot="suffix"> </i>
                </el-input>
              </div>
            </div>
            <i class="el-icon-delete" @click="delAreaBox(index)" />
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-actions">
        <el-button @click="cancelAreaData">取消</el-button>
        <el-button type="primary" @click="saveAreaData">确定</el-button>
      </div>
    </el-dialog>
    <linkaddress ref="linkaddres" @linkUrl="linkUrl"></linkaddress>
  </div>
</template>

<script>
import AreaBox from './AreaBox';
import linkaddress from '@/components/linkaddress';

export default {
  name: 'OperationFloor',
  components: {
    AreaBox,
    linkaddress,
  },
  props: {
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
  },
  data() {
    return {
      /**
       * @description 对话框是否可见
       * @type {boolean}
       */
      dialogVisible: false,
      /**
       * @description 开始的x坐标
       * @type {number}
       */
      starX: 0,
      /**
       * @description 开始的y坐标
       * @type {number}
       */
      starY: 0,
      /**
       * @description 区域宽度
       * @type {number}
       */
      areaWidth: 0,
      /**
       * @description 区域高度
       * @type {number}
       */
      areaHeight: 0,
      /**
       * @description 当前显示的图片索引
       * @type {boolean}
       */
      caseShow: false,
      /**
       * @description 当前图片的宽度
       * @type {null}
       */
      nowImgWidth: null,
      /**
       * @description 区域数据
       * @type {Array}
       */
      areaData: [],
      /**
       * @description 当前显示的图片编号
       * @type {number}
       */
      imgNum: 1,
      /**
       * @description 父元素宽度
       * @type {number}
       */
      parentWidth: 0,
      /**
       * @description 父元素高度
       * @type {number}
       */
      parentHeight: 0,
      /**
       * @description 默认宽度
       * @type {number}
       */
      defaultWidth: 750,
      /**
       * @description 当前显示的图片索引
       * @type {number}
       */
      itemIndex: 0,
      nowNum: 0,
    };
  },
  computed: {
    imageSrc() {
      if (Array.isArray(this.imgs)) {
        const first = this.imgs[0] || {};
        return first.img || first.url || first.att_dir || first.sattDir || '';
      }
      return this.imgs || '';
    },
  },
  watch: {
    imgAreaData(val) {
      this.areaData = [...(val || [])];
    },
  },
  mounted() {
    this.areaData = [...(this.imgAreaData || [])];
  },
  methods: {
    //添加热区
    addAreaBox() {
      if (this.areaData.length === 50) return;
      this.nowNum++;
      let starX = ((this.nowNum - 1) % 4) * 114;
      let starY = Math.floor((this.nowNum - 1) / 4) * 114;
      if (starY > this.parentHeight - 114) {
        starY = this.parentHeight - 114;
      }
      this.areaData.push({
        starX,
        starY,
        name: `热区${this.nowNum}`,
        areaWidth: 114,
        areaHeight: 114,
        nowImgWidth: this.defaultWidth,
        link: '',
      });
    },
    //重置热区
    resetAreaBox() {
      this.nowNum = 0;
      this.areaData = [];
    },
    cancelAreaData() {
      this.areaData = [...this.imgAreaData];
      this.dialogVisible = false;
    },
    saveAreaData() {
      this.$emit('saveAreaData', this.areaData);
      this.dialogVisible = false;
    },
    handleDialogClose() {
      document.onmousemove = null;
      this.caseShow = false;
    },
    openModal() {
      this.$nextTick(() => {
        const parentDiv = this.$refs.container;
        this.parentWidth = this.defaultWidth;
        this.parentHeight = parentDiv ? parentDiv.clientHeight : 450;
      });
    },
    closeModal() {
      this.$Modal.confirm({
        title: '提示信息',
        content: '<p>未保存内容，是否在离开前放弃保存？</p>',
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          this.$Modal.remove();
          this.dialogVisible = false;
        },
      });
    },
    // 绘画热区开始
    mouseDown(e) {
      e.preventDefault();
      this.caseShow = true;
      // 记录滑动的初始值
      this.starX = e.layerX;
      this.starY = e.layerY;
      // 鼠标滑动的过程
      if (!document.onmousemove) {
        let maxWidth = this.defaultWidth - e.layerX;
        document.onmousemove = (ev) => {
          if (ev.layerX - this.starX < maxWidth) {
            this.areaWidth = ev.layerX - this.starX;
          } else {
            this.areaWidth = maxWidth;
          }
          this.areaHeight = ev.layerY - this.starY;
        };
      }
    },
    // 绘画热区结束
    changeStop() {
      document.onmousemove = null;
      this.imgNum = this.areaData.length + 1;
      if (this.caseShow && this.areaWidth > 10 && this.areaHeight > 10) {
        const data = {
          name: `热区${this.imgNum}`,
          starX: this.starX,
          starY: this.starY,
          areaWidth: this.areaWidth < 50 ? 50 : this.areaWidth,
          areaHeight: this.areaHeight < 50 ? 50 : this.areaHeight,
          nowImgWidth: this.defaultWidth,
          link: '',
        };
        this.areaData.push(data);
      }
      // 初始化绘图
      this.caseShow = false;
      this.starX = 0;
      this.starY = 0;
      this.areaWidth = 0;
      this.areaHeight = 0;
    },
    // 删除指定热区
    delAreaBox(index) {
      /* 删除某个热区 */
      this.areaData.splice(index, 1);
      // this.$emit('delAreaData', this.areaData);
      /* 删除后 每个热区按顺序重新编号 */
      if (this.areaData) {
        const arr = this.areaData.filter((i) => i.number > index);
        if (!arr) return;
        arr.forEach((i) => i.number--);
        if (this.areaData[this.areaData.length - 1]) {
          this.imgNum = this.areaData[this.areaData.length - 1].number + 1;
        } else {
          this.imgNum = 1;
        }
      }
    },
    // 添加网址
    addURL(index, url) {
      let obj = {
        ...this.areaData[index],
        link: url,
      };
      this.$set(this.areaData, index, obj);
    },
    /**
     * 检查列表中每个元素是否都有 link 属性
     * @param {Array} list - 待检查的列表
     * @returns {Boolean} - 是否所有元素都有 link 属性
     */
    checkData(list) {
      let isCheck = true;
      list.some((val) => {
        if (!val.link) {
          isCheck = false;
        }
      });
      return isCheck;
    },
    /**
     * @description 获取链接地址并打开添加链接的模态框
     * @param {number} index - 当前项的索引值
     */
    getLink(index) {
      // 设置当前项的索引值
      this.itemIndex = index;
      // 打开添加链接的模态框
      // this.$refs.linkaddres.currenType = 'link';
      // this.$refs.linkaddres.mockData('link');
      this.$refs.linkaddres.modals = true;
    },
    /**
     * @description 处理链接地址的输入事件
     * @param {string} e - 链接地址
     */
    linkUrl(e) {
      // 将链接地址存储到对应的数据项中
      this.areaData[this.itemIndex].link = e;
    },
  },
};
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
::v-deep .el-input__suffix {
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
::v-deep .el-input__inner {
  padding: 0 8px !important;
}
::v-deep .hotpot-dialog {
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
