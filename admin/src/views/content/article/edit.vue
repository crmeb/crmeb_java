<template>
  <div class="divBox">
    <el-card class="box-card">
      <div class="components-container">
        <el-form ref="pram" label-width="150px" :model="pram">
          <el-form-item label="标题" prop="title" :rules="[{required:true, message:'请填写标题', trigger:['blur','change']}]">
            <el-input v-model="pram.title" placeholder="标题" maxlength="100"/>
          </el-form-item>
          <el-form-item label="作者" prop="author" :rules="[{required:true, message:'请填作者', trigger:['blur','change']}]">
            <el-input v-model="pram.author" placeholder="作者" maxlength="20"/>
          </el-form-item>
          <el-form-item label="文章分类" :rules="[{required:true, message:'请选择分类', trigger:['blur','change']}]">
            <el-select v-model="pram.cid" placeholder="请选择" style="width:100%;">
              <el-option
                v-for="item in categoryTreeData"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="图文封面" prop="imageInput" :rules="[{ required: true, message: '请上传图文封面', trigger: 'change' }]">
            <div class="upLoadPicBox" @click="modalPicTap('1')">
              <div v-if="pram.imageInput" class="pictrue"><img :src="pram.imageInput"></div>
              <div v-else class="upLoad">
                <i class="el-icon-camera cameraIconfont" />
              </div>
            </div>
          </el-form-item>
          <el-form-item label="文章简介" prop="synopsis" :rules="[{required:true, message:'请填写文章简介', trigger:['blur','change']}]">
            <el-input v-model="pram.synopsis" maxlength="100" type="textarea" :rows="2" resize="none" placeholder="文章简介" />
          </el-form-item>
          <el-form-item label="文章内容" prop="content" :rules="[{required:true, message:'请填写文章内容', trigger:['blur','change']}]">
            <Tinymce v-model="pram.content"></Tinymce>
          </el-form-item>
          <el-form-item label="是否Banner">
            <el-switch v-model="pram.isBanner" />
          </el-form-item>
          <el-form-item label="是否热门">
            <el-switch v-model="pram.isHot" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handerSubmit('pram')" v-hasPermi="['admin:article:update']">保存</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script>
import Tinymce from '@/components/Tinymce/index'
import * as categoryApi from '@/api/categoryApi.js'
import * as articleApi from '@/api/article.js'
import * as selfUtil from '@/utils/ZBKJIutil.js'
import { fileImageApi } from '@/api/systemSetting'
import { getToken } from '@/utils/auth'
import {Debounce} from '@/utils/validate'
export default {
  // name: "edit",
  components: { Tinymce },
  data() {
    return {
      loading:false,
      constants: this.$constants,
      categoryTreeData: [],
      categoryProps: {
        value: 'id',
        label: 'name',
        children: 'child',
        expandTrigger: 'hover',
        checkStrictly: true,
        emitPath: false
      },
      pram: {
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
        id: null
        // mediaId: null
      },
      editData: {},
      myHeaders: { 'X-Token': getToken() },
      editorContentLaebl:"",
      // basicForm:{editorContent:""}
    }
  },
  created() {
    this.tempRoute = Object.assign({}, this.$route)
  },
  mounted() {
    if(this.$route.params.id){
      this.getInfo()
      this.setTagsViewTitle()
    }
    this.handlerGetCategoryTreeData()
  },
  methods: {
    getInfo (){
      categoryApi.articleInfoApi({ id: this.$route.params.id }).then(data => {
          this.editData = data
          this.hadlerInitEditData()
      })
    },
    modalPicTap(tit) {
      const _this = this
      this.$modalUpload(function(img) {
        _this.pram.imageInput = img[0].sattDir
      }, tit, 'content')
    },
    hadlerInitEditData() {
      if (!this.$route.params.id) return
      const { author, cid, content, imageInput,
        isBanner, isHot, shareSynopsis, shareTitle, sort, synopsis, title, url, id } = this.editData
      this.pram.author = author
      this.pram.cid = Number.parseInt(cid)
      this.pram.content = content
      this.pram.imageInput = imageInput
      this.pram.isBanner = isBanner
      this.pram.isHot = isHot
      this.pram.shareSynopsis = shareSynopsis
      this.pram.shareTitle = shareTitle
      this.pram.sort = sort
      this.pram.synopsis = synopsis
      this.pram.title = title
      this.pram.url = url
      this.pram.id = id
      // this.pram.mediaId = mediaId
    },
    handlerGetCategoryTreeData() {
      categoryApi.listCategroy({ type: 3, status: '' }).then(data => {
        this.categoryTreeData = data.list
        localStorage.setItem('articleClass', JSON.stringify(data.list))
      })
    },
    handerSubmit:Debounce(function(form) {
      this.$refs[form].validate(valid => {
        if (!valid) return
        if (!this.$route.params.id) {
          this.handlerSave()
        } else {
          this.handlerUpdate()
        }
      })
    }),
    handlerUpdate() {
      this.loading = true
      this.pram.cid = Array.isArray(this.pram.cid) ? this.pram.cid[0] : this.pram.cid
      this.pram.shareTitle = this.pram.title
      this.pram.shareSynopsis = this.pram.synopsis
      articleApi.UpdateArticle(this.pram).then(data => {
        this.$message.success('编辑文章成功')
        this.loading = false
        this.$router.push({ path: '/content/articleManager' });
      }).catch(()=>{
        this.loading = false
      })
    },
    handlerSave() {
      this.loading = true
      this.pram.cid = Array.isArray(this.pram.cid) ? this.pram.cid[0] : this.pram.cid
      this.pram.shareTitle = this.pram.title
      this.pram.shareSynopsis = this.pram.synopsis
      articleApi.AddArticle(this.pram).then(data => {
        this.$message.success('新增文章成功')
        this.loading = false
        this.$router.push({ path: '/content/articleManager' });
      }).catch(()=>{
        this.loading = false
      })
    },
    setTagsViewTitle() {
      const title = '编辑文章'
      const route = Object.assign({}, this.tempRoute, { title: `${title}-${this.$route.params.id}` })
      this.$store.dispatch('tagsView/updateVisitedView', route)
    },
  }
}
</script>

<style scoped>

</style>
