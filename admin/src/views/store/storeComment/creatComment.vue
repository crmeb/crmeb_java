<template>
  <el-form
    :model="formValidate"
    :rules="rules"
    ref="formValidateRef"
    label-width="80px"
    class="demo-formValidate"
    v-loading="loading"
  >
    <el-form-item label="商品：" prop="productId">
      <div class="upLoadPicBox" @click="changeGood">
        <div v-if="formValidate.productId" class="pictrue"><img :src="image" /></div>
        <div v-else class="upLoad">
          <i class="el-icon-camera cameraIconfont" />
        </div>
      </div>
    </el-form-item>
    <el-form-item label="用户名称：" prop="nickname">
      <el-input type="text" v-model="formValidate.nickname"></el-input>
    </el-form-item>
    <el-form-item label="评价文字：" prop="comment">
      <el-input type="textarea" v-model="formValidate.comment"></el-input>
    </el-form-item>
    <el-form-item label="商品分数：" prop="productScore">
      <el-rate v-model="formValidate.productScore"></el-rate>
    </el-form-item>
    <el-form-item label="服务分数：" prop="serviceScore">
      <el-rate v-model="formValidate.serviceScore"></el-rate>
    </el-form-item>
    <el-form-item label="用户头像：" prop="avatar">
      <div class="upLoadPicBox" @click="modalPicTap('1')">
        <div v-if="formValidate.avatar" class="pictrue"><img :src="formValidate.avatar" /></div>
        <div v-else class="upLoad">
          <i class="el-icon-camera cameraIconfont" />
        </div>
      </div>
    </el-form-item>
    <el-form-item label="评价图片：">
      <div class="acea-row">
        <div
          v-for="(item, index) in pics"
          :key="index"
          class="pictrue"
          draggable="false"
          @dragstart="handleDragStart($event, item)"
          @dragover.prevent="handleDragOver($event, item)"
          @dragenter="handleDragEnter($event, item)"
          @dragend="handleDragEnd($event, item)"
        >
          <img :src="item" />
          <i class="el-icon-error btndel" @click="handleRemove(index)" />
          <!--<Button shape="circle" icon="md-close" class="btndel" @click.native="handleRemove(index)" />-->
        </div>
        <div v-if="pics < 10" class="upLoadPicBox" @click="modalPicTap('2')">
          <div class="upLoad">
            <i class="el-icon-camera cameraIconfont" />
          </div>
        </div>
      </div>
    </el-form-item>
	    <el-form-item label="评论时间：" prop="createTime">
	      <el-date-picker
	        v-model="formValidate.createTime"
	        type="datetime"
	        placeholder="选择评论时间"
	        value-format="YYYY-MM-DD HH:mm:ss"
	        style="width: 100%"
	      />
	    </el-form-item>
    <el-form-item class="dialog-footer-inner">
      <el-button @click="resetForm('formValidate')">重置</el-button>
      <el-button type="primary" @click="submitForm('formValidate')" :loading="loadingbtn">提交</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="jsx">
import { reactive, ref, watch, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { replyCreatApi, replyEditApi, replyInfoApi } from '@/api/store';
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'creatComment' });

const props = defineProps({
  num: {
    type: Number,
    required: 0,
  },
});
const emit = defineEmits(['getList']);

const { proxy } = getCurrentInstance();

const defaultObj = {
  avatar: '',
  comment: '',
  createTime: '',
  nickname: '',
  pics: '',
  productId: '',
  productScore: null,
  serviceScore: null,
  sku: '',
};

var checkProductScore = (rule, value, callback) => {
  if (!value) {
    return callback(new Error('商品分数不能为空'));
  } else {
    callback();
  }
};
var checkServiceScore = (rule, value, callback) => {
  if (!value) {
    return callback(new Error('服务分数不能为空'));
  } else {
    callback();
  }
};

const loadingbtn = ref(false);
const loading = ref(false);
const pics = ref([]);
const image = ref('');
const formValidateRef = ref(null);
const formValidate = reactive(
  Object.assign({}, defaultObj, {
    createTime: new Date(),
  }),
);
const rules = {
  avatar: [{ required: true, message: '请选择用户头像', trigger: 'change' }],
  productId: [{ required: true, message: '请选择商品', trigger: 'change' }],
  comment: [{ required: true, message: '请填写评价内容', trigger: 'blur' }],
  nickname: [{ required: true, message: '请填写用户名称', trigger: 'blur' }],
  pics: [{ required: true, message: '请选择评价图片', trigger: 'change' }],
  productScore: [{ required: true, validator: checkProductScore, trigger: 'blur' }],
  serviceScore: [{ required: true, validator: checkServiceScore, trigger: 'change' }],
};

function changeGood() {
  proxy.$modalGoodList(function (row) {
    image.value = row.image;
    formValidate.productId = row.id;
    formValidate.sku = row.attrValue[0].suk;
  });
}
// 点击商品图
function modalPicTap(tit) {
  proxy.$modalUpload(
    function (img) {
      tit === '1'
        ? (formValidate.avatar = img[0].sattDir)
        : img.map((item) => {
            pics.value.push(item.sattDir);
          });
    },
    tit,
    'store',
  );
}
function handleRemove(i) {
  pics.value.splice(i, 1);
}
const submitForm = Debounce(function (formName) {
  formValidate.pics = pics.value.length > 0 ? JSON.stringify(pics.value) : '';
  formValidateRef.value.validate((valid) => {
    if (valid) {
      loadingbtn.value = true;
      replyCreatApi(formValidate)
        .then(() => {
          ElMessage.success('新增成功');
          setTimeout(() => {
            // this.clear();
            emit('getList');
          }, 600);
          loadingbtn.value = false;
        })
        .catch(() => {
          loadingbtn.value = false;
        });
    } else {
      return false;
    }
  });
});
function resetForm(formName) {
  formValidateRef.value.resetFields();
  pics.value = [];
  formValidate.pics = '';
}
function info() {
  loading.value = true;
  replyInfoApi(formValidate)
    .then((res) => {
      Object.assign(formValidate, res);
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
    });
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
  const newItems = [...pics.value];
  const src = newItems.indexOf(dragging.value);
  const dst = newItems.indexOf(item);
  newItems.splice(dst, 0, ...newItems.splice(src, 1));
  pics.value = newItems;
}
const dragging = ref(null);

watch(
  () => props.num,
  (val) => {
    resetForm('formValidate');
  },
  { deep: true },
);
</script>

<style scoped lang="scss">
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

.btndel {
  position: absolute;
  z-index: 1;
  width: 20px !important;
  height: 20px !important;
  left: 43px;
  top: 1px;
}
</style>
