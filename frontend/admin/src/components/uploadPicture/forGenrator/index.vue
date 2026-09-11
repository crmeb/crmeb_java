<template>
  <div class="self-upload">
    <div class="acea-row" v-if="multiple">
      <div
        v-for="(item, index) in imageList"
        :key="index"
        class="pictrue"
        draggable="false"
        @dragstart="handleDragStart($event, item)"
        @dragover.prevent="handleDragOver($event, item)"
        @dragenter="handleDragEnter($event, item)"
        @dragend="handleDragEnd($event, item)"
      >
        <img :src="item.sattDir" />
        <i class="el-icon-error btndel" @click="handleRemove(index)" />
      </div>
      <div class="upLoadPicBox" @click="modalPicTap('2')" v-show="imageList.length < 20">
        <div class="upLoad">
          <i class="el-icon-camera cameraIconfont" />
        </div>
      </div>
    </div>
    <div class="upLoadPicBox" @click="modalPicTap('1')" v-else>
      <div v-if="image" class="pictrue"><img :src="image" /></div>
      <div v-else class="upLoad">
        <i class="el-icon-camera cameraIconfont" />
      </div>
    </div>
    <el-dialog append-to-body v-model="visible" width="1024px" :before-close="handleClose" :modal="true">
      <upload-index v-if="visible" :checkedMore="imageList" :isMore="isMore" @getImage="getImage" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onBeforeMount } from 'vue';
import UploadIndex from '@/components/uploadPicture/index.vue';

defineOptions({ name: 'UploadFroms' });

const props = defineProps({
  modelValue: {},
  multiple: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['update:modelValue']);

const image = ref('');
const visible = ref(false);
const callback = ref(function () {});
const isMore = ref('');
const imageList = ref([]);
const dragging = ref(null);

onBeforeMount(() => {
  if (props.multiple) {
    // 接收 v-model 数据
    if (props.modelValue) {
      imageList.value = JSON.parse(props.modelValue);
    }
  } else {
    // 接收 v-model 数据
    if (props.modelValue) {
      image.value = props.modelValue;
    }
  }
  // 处理多选
  isMore.value = props.multiple ? '2' : '1';
});

function handleClose() {
  visible.value = false;
}

function getImage(img) {
  if (props.multiple) {
    let obj = {};
    imageList.value = img.reduce((cur, next) => {
      obj[next.attId] ? '' : (obj[next.attId] = true && cur.push(next));
      return cur;
    }, []);
    emit('update:modelValue', JSON.stringify(imageList.value));
  } else {
    image.value = img[0].sattDir;
    emit('update:modelValue', image.value);
  }
  visible.value = false;
}

// 点击商品图
function modalPicTap(tit, num, i) {
  visible.value = true;
}

function handleRemove(i) {
  imageList.value.splice(i, 1);
  emit('update:modelValue', JSON.stringify(imageList.value));
}

// 移动
function handleDragStart(e, item) {
  dragging.value = item;
}

function handleDragEnd(e, item) {
  dragging.value = null;
}

function handleDragOver(e) {
  e.dataTransfer.dropEffect = 'move';
}

function handleDragEnter(e, item) {
  e.dataTransfer.effectAllowed = 'move';
  if (item === dragging.value) {
    return;
  }
  const newItems = [...imageList.value];
  const src = newItems.indexOf(dragging.value);
  const dst = newItems.indexOf(item);
  newItems.splice(dst, 0, ...newItems.splice(src, 1));
  imageList.value = newItems;
}
</script>

<style scoped lang="scss">
.self-upload {
  width: 100%;
}
.btndel {
  position: absolute;
  z-index: 1;
  width: 20px !important;
  height: 20px !important;
  left: 43px;
  top: 1px;
}
.pictrue {
  width: 60px;
  height: 60px;
  border: 1px dotted rgba(0, 0, 0, 0.1);
  margin-right: 10px;
  position: relative;
  cursor: pointer;
  img {
    width: 100%;
    height: 100%;
  }
}
:deep(.el-dialog__close) {
  position: absolute;
  top: 15px;
  right: 15px;
}
:deep(.el-dialog__header) {
  border-bottom: none;
  padding: 0 !important;
}
</style>
