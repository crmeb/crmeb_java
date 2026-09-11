<template>
  <div class="divBox">
    <pages-header
      ref="pageHeaderRef"
      :title="route.params.id ? '编辑文章' : '添加文章'"
      backUrl="/content/articleManager"
    ></pages-header>
    <el-card class="box-card mt14">
      <div class="components-container">
        <el-form ref="pramRef" label-width="90px" :model="pram">
          <el-form-item
            label="标题："
            prop="title"
            :rules="[{ required: true, message: '请填写标题', trigger: ['blur', 'change'] }]"
          >
            <el-input v-model="pram.title" class="selWidth" placeholder="标题" maxlength="100" />
          </el-form-item>
          <el-form-item
            label="作者："
            prop="author"
            :rules="[{ required: true, message: '请填作者', trigger: ['blur', 'change'] }]"
          >
            <el-input class="selWidth" v-model="pram.author" placeholder="作者" maxlength="20" />
          </el-form-item>
          <el-form-item
            label="文章分类："
            :rules="[{ required: true, message: '请选择分类', trigger: ['blur', 'change'] }]"
          >
            <el-select class="selWidth" v-model="pram.cid" placeholder="请选择">
              <el-option v-for="item in categoryTreeData" :key="item.id" :label="item.name" :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item
            label="图文封面："
            prop="imageInput"
            :rules="[{ required: true, message: '请上传图文封面', trigger: 'change' }]"
          >
            <div class="upLoadPicBox" @click="modalPicTap('1')">
              <div v-if="pram.imageInput" class="pictrue"><img :src="pram.imageInput" /></div>
              <div v-else class="upLoad">
                <i class="el-icon-camera cameraIconfont" />
              </div>
            </div>
          </el-form-item>
          <el-form-item
            label="文章简介："
            prop="synopsis"
            :rules="[{ required: true, message: '请填写文章简介', trigger: ['blur', 'change'] }]"
          >
            <el-input
              v-model="pram.synopsis"
              maxlength="100"
              type="textarea"
              :rows="2"
              resize="none"
              placeholder="文章简介"
              class="selWidth"
            />
          </el-form-item>
          <el-form-item
            label="文章内容："
            prop="content"
            :rules="[{ required: true, message: '请填写文章内容', trigger: ['blur', 'change'] }]"
          >
            <WangEditor v-model="pram.content"></WangEditor>
          </el-form-item>
          <el-form-item label="是否Banner：">
            <el-switch v-model="pram.isBanner" />
          </el-form-item>
          <el-form-item label="是否热门：">
            <el-switch v-model="pram.isHot" />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              @click="handerSubmit('pram')"
              v-hasPermi="['admin:article:update']"
              >保存</el-button
            >
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { useRoute, useRouter } from 'vue-router';
import { useTagsViewStore } from '@/store/modules/tagsView';
import WangEditor from '@/components/wangEditor/index.vue';
import * as categoryApi from '@/api/categoryApi.js';
import * as articleApi from '@/api/article.js';
import * as selfUtil from '@/utils/ZBKJIutil.js';
import { fileImageApi } from '@/api/systemSetting';
import { getToken } from '@/utils/auth';
import { Debounce } from '@/utils/validate';

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();
const tagsViewStore = useTagsViewStore();
const constants = proxy.$constants;

const pageHeaderRef = ref(null);
const pramRef = ref(null);
const loading = ref(false);
const categoryTreeData = ref([]);
const categoryProps = {
  value: 'id',
  label: 'name',
  children: 'child',
  expandTrigger: 'hover',
  checkStrictly: true,
  emitPath: false,
};
const pram = reactive({
  author: null,
  cid: null,
  content: '', //<span>My Document\'s Title</span>
  imageInput: '',
  isBanner: false,
  isHot: null,
  shareSynopsis: null,
  shareTitle: null,
  sort: 0,
  synopsis: null,
  title: null,
  url: null,
  id: null,
  // mediaId: null
});
const editData = ref({});
const myHeaders = { 'X-Token': getToken() };
const editorContentLaebl = ref('');
// basicForm:{editorContent:""}

const tempRoute = ref({});
tempRoute.value = Object.assign({}, route);

function getInfo() {
  categoryApi.articleInfoApi({ id: route.params.id }).then((data) => {
    editData.value = data;
    hadlerInitEditData();
  });
}
function modalPicTap(tit) {
  proxy.$modalUpload(
    function (img) {
      pram.imageInput = img[0].sattDir;
    },
    tit,
    'content',
  );
}
function hadlerInitEditData() {
  if (!route.params.id) return;
  const {
    author,
    cid,
    content,
    imageInput,
    isBanner,
    isHot,
    shareSynopsis,
    shareTitle,
    sort,
    synopsis,
    title,
    url,
    id,
  } = editData.value;
  pram.author = author;
  pram.cid = Number.parseInt(cid);
  pram.content = content;
  pram.imageInput = imageInput;
  pram.isBanner = isBanner;
  pram.isHot = isHot;
  pram.shareSynopsis = shareSynopsis;
  pram.shareTitle = shareTitle;
  pram.sort = sort;
  pram.synopsis = synopsis;
  pram.title = title;
  pram.url = url;
  pram.id = id;
  // pram.mediaId = mediaId
}
function handlerGetCategoryTreeData() {
  categoryApi.listCategroy({ type: 3, status: '' }).then((data) => {
    categoryTreeData.value = data;
    localStorage.setItem('adminArticleClassify', JSON.stringify(data));
  });
}
const handerSubmit = Debounce(function (form) {
  pramRef.value.validate((valid) => {
    if (!valid) return;
    if (!route.params.id) {
      handlerSave();
    } else {
      handlerUpdate();
    }
  });
});
function handlerUpdate() {
  loading.value = true;
  pram.cid = Array.isArray(pram.cid) ? pram.cid[0] : pram.cid;
  pram.shareTitle = pram.title;
  pram.shareSynopsis = pram.synopsis;
  articleApi
    .UpdateArticle(pram)
    .then((data) => {
      ElMessage.success('编辑文章成功');
      loading.value = false;
      router.push({ path: '/content/articleManager' });
    })
    .catch(() => {
      loading.value = false;
    });
}
function handlerSave() {
  loading.value = true;
  pram.cid = Array.isArray(pram.cid) ? pram.cid[0] : pram.cid;
  pram.shareTitle = pram.title;
  pram.shareSynopsis = pram.synopsis;
  articleApi
    .AddArticle(pram)
    .then((data) => {
      ElMessage.success('新增文章成功');
      loading.value = false;
      router.push({ path: '/content/articleManager' });
    })
    .catch(() => {
      loading.value = false;
    });
}
function setTagsViewTitle() {
  const title = '编辑文章';
  const routeObj = Object.assign({}, tempRoute.value, { title: `${title}-${route.params.id}` });
  tagsViewStore.updateVisitedView(routeObj);
}

onMounted(() => {
  if (route.params.id) {
    getInfo();
    setTagsViewTitle();
  }
  handlerGetCategoryTreeData();
});
</script>

<style scoped>
.selWidth {
  width: 460px !important;
}
</style>
