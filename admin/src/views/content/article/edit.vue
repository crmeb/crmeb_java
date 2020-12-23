<template>
  <div class="components-container">
    <el-form ref="pram" label-width="150px" :model="pram">
      <el-form-item label="标题" prop="title" :rules="[{required:true, message:'请填写标题', trigger:['blur','change']}]">
        <el-input v-model="pram.title" placeholder="标题" maxlength="100"/>
      </el-form-item>
      <el-form-item label="作者" prop="author" :rules="[{required:true, message:'请填作者', trigger:['blur','change']}]">
        <el-input v-model="pram.author" placeholder="作者" maxlength="20"/>
      </el-form-item>
      <el-form-item label="文章分类">
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
      <!--<el-form-item label="微信公众号封面" prop="imageInput" :rules="[{ required: true, message: '请上传图文封面', trigger: 'change' }]">-->
        <!--<div class="upLoadPicBox">-->
          <!--<div v-if="pram.imageInput" class="pictrue"><img :src="pram.imageInput"></div>-->
          <!--<el-upload-->
            <!--v-else-->
            <!--class="upload-demo mr10 mb15"-->
            <!--action-->
            <!--:http-request="handleUploadForm"-->
            <!--:headers="myHeaders"-->
            <!--:show-file-list="false"-->
            <!--multiple-->
          <!--&gt;-->
            <!--<div class="upLoad">-->
              <!--<i class="el-icon-camera cameraIconfont" />-->
            <!--</div>-->
          <!--</el-upload>-->
        <!--</div>-->
      <!--</el-form-item>-->
      <el-form-item label="文章简介" prop="synopsis" :rules="[{required:true, message:'请填写文章简介', trigger:['blur','change']}]">
        <el-input v-model="pram.synopsis" maxlength="100" type="textarea" :rows="2" resize="none" placeholder="文章简介" />
      </el-form-item>
      <el-form-item label="文章内容" prop="content" :rules="[{required:true, message:'请填写文章内容', trigger:['blur','change']}]">
        <ueditor-from v-model="pram.content" :content="pram.content" />
      </el-form-item>
      <el-form-item label="是否Banner">
        <el-switch v-model="pram.isBanner" />
      </el-form-item>
      <el-form-item label="是否热门">
        <el-switch v-model="pram.isHot" />
      </el-form-item>
      <!--<el-form-item label="原文链接">-->
        <!--<p>原文链接选填，填写之后在图文左下方会出现此链接</p>-->
        <!--<el-input v-model="pram.url" placeholder="原文链接" />-->
      <!--</el-form-item>-->
      <el-form-item>
        <el-button type="primary" @click="handerSubmit('pram')">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import Tinymce from '@/components/Tinymce/index'
import * as categoryApi from '@/api/categoryApi.js'
import * as constants from '@/utils/constants.js'
import * as articleApi from '@/api/article.js'
import * as selfUtil from '@/utils/ZBKJIutil.js'
import { fileImageApi } from '@/api/systemSetting'
import { getToken } from '@/utils/auth'
export default {
  // name: "edit",
  components: { Tinymce },
  props: {
    isEdit: {
      type: Number,
      required: true
    },
    editData: {
      type: Object
    }
  },
  data() {
    return {
      constants,
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
        content: null,
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
      myHeaders: { 'X-Token': getToken() }
    }
  },
  mounted() {
    this.handlerGetCategoryTreeData()
    this.hadlerInitEditData()
  },
  methods: {
    // 上传
    handleUploadForm(param){
      const formData = new FormData()
      formData.append('media', param.file)
      let loading = this.$loading({
        lock: true,
        text: '上传中，请稍候...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      fileImageApi(formData, {type: 'image'}).then(res => {
        loading.close()
        this.pram.mediaId = res.mediaId

        // this.formValidate.contents.mediaId = res.mediaId
        // this.formValidate.contents.srcUrl = res.url
        this.$message.success('上传成功')
      }).catch(() => {
        loading.close()
      })
    },
    modalPicTap(tit) {
      const _this = this
      this.$modalUpload(function(img) {
        _this.pram.imageInput = img[0].sattDir
      }, tit, 'content')
    },
    hadlerInitEditData() {
      if (this.isEdit !== 1) return
      const { author, cid, content, imageInputs,
        isBanner, isHot, shareSynopsis, shareTitle, sort, synopsis, title, url, id } = this.editData
      this.pram.author = author
      this.pram.cid = Number.parseInt(cid)
      this.pram.content = content
      this.pram.imageInput = imageInputs
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
    handerSubmit(form) {
      this.$refs[form].validate(valid => {
        if (!valid) return
        if (this.isEdit === 0) {
          this.handlerSave()
        } else {
          this.handlerUpdate()
        }
      })
    },
    handlerUpdate() {
      this.pram.cid = Array.isArray(this.pram.cid) ? this.pram.cid[0] : this.pram.cid
      this.pram.shareTitle = this.pram.title
      this.pram.shareSynopsis = this.pram.synopsis
      articleApi.UpdateArticle(this.pram).then(data => {
        this.$message.success('编辑文章成功')
        this.$emit('hideDialog')
      })
    },
    handlerSave() {
      this.pram.cid = Array.isArray(this.pram.cid) ? this.pram.cid[0] : this.pram.cid
      this.pram.shareTitle = this.pram.title
      this.pram.shareSynopsis = this.pram.synopsis
      articleApi.AddArticle(this.pram).then(data => {
        this.$message.success('新增文章成功')
        this.$emit('hideDialog')
      })
    }
  }
}
</script>

<style scoped>

</style>
