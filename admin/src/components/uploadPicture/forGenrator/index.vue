<template>
  <div>
    <div class="acea-row" v-if="multiple">
      <div
        v-for="(item,index) in imageList"
        :key="index"
        class="pictrue"
        draggable="false"
        @dragstart="handleDragStart($event, item)"
        @dragover.prevent="handleDragOver($event, item)"
        @dragenter="handleDragEnter($event, item)"
        @dragend="handleDragEnd($event, item)"
      >
        <img :src="item.sattDir">
        <i class="el-icon-error btndel" @click="handleRemove(index)" />
      </div>
      <div class="upLoadPicBox" @click="modalPicTap('2')" v-show="imageList.length<20">
        <div class="upLoad">
          <i class="el-icon-camera cameraIconfont" />
        </div>
      </div>
    </div>
    <div class="upLoadPicBox" @click="modalPicTap('1')" v-else>
      <div v-if="image" class="pictrue"><img :src="image"></div>
      <div v-else class="upLoad">
        <i class="el-icon-camera cameraIconfont" />
      </div>
    </div>
    <el-dialog
      title="上传图片"
      :visible.sync="visible"
      width="896px"
      :before-close="handleClose"
       :modal="false"
    >
      <upload-index v-if="visible" :checkedMore="imageList" :isMore="isMore" @getImage="getImage" />
    </el-dialog>
  </div>
</template>

<script>
import UploadIndex from '@/components/uploadPicture/index.vue'
export default {
  name: 'UploadFroms',
  components: { UploadIndex },
  props:{
    value:{},
    multiple:{
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      image:"",
      visible: false,
      callback: function() {},
      isMore: '',
      imageList: []
    }
  },
  beforeMount(){
    if( this.multiple ){
      // 接收 v-model 数据
      if(this.value){
        this.imageList = JSON.parse(this.value)
      }
    }else{
      // 接收 v-model 数据
      if(this.value){
        this.image = this.value
      }
    }
    // 处理多选
    this.isMore = this.multiple ? '2':'1'
  },
  methods: {
    handleClose() {
      this.visible = false
    },
    getImage(img) {
      if (this.multiple) {
        let obj = {};
        this.imageList = img.reduce((cur,next) => {
          obj[next.attId] ? "" : obj[next.attId] = true && cur.push(next);
          return cur;
        },[])
        this.$emit('input', JSON.stringify(this.imageList))
      } else {
        this.image = img[0].sattDir
        this.$emit('input', this.image)
      }
      this.visible = false
    },
    // 点击商品图
    modalPicTap (tit, num, i) {
      this.visible = true
    },
    handleRemove (i) {
      this.imageList.splice(i, 1)
      this.$emit('input', JSON.stringify(this.imageList))
    },
    // 移动
    handleDragStart (e, item) {
      this.dragging = item;
    },
    handleDragEnd (e, item) {
      this.dragging = null
    },
    handleDragOver (e) {
      e.dataTransfer.dropEffect = 'move'
    },
    handleDragEnter (e, item) {
      e.dataTransfer.effectAllowed = 'move'
      if (item === this.dragging) {
        return
      }
      const newItems = [...this.imageList]
      const src = newItems.indexOf(this.dragging)
      const dst = newItems.indexOf(item)
      newItems.splice(dst, 0, ...newItems.splice(src, 1))
      this.imageList = newItems;
    }
  }
}
</script>

<style scoped lang="scss">
  .btndel{
    position: absolute;
    z-index: 1;
    width :20px !important;
    height: 20px !important;
    left: 46px;
    top: -4px;
  }
  .pictrue{
    width: 60px;
    height: 60px;
    border: 1px dotted rgba(0,0,0,0.1);
    margin-right: 10px;
    position: relative;
    cursor: pointer;
    img{
      width: 100%;
      height: 100%;
    }
  }
</style>
