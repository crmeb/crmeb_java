<template>
  <div>
    <el-form ref="editPram" :model="editPram" label-width="130px">
      <el-form-item
        label="分类名称"
        prop="name"
        :rules="[{ required:true,message:'请输入分类名称',trigger:['blur','change'] }]"
      >
        <el-input v-model="editPram.name"  :maxlength="biztype.value === 1 ? 8 : 20" placeholder="分类名称" />
      </el-form-item>
      <el-form-item label="URL" v-if="biztype.value!==1 && biztype.value!==3">
        <el-input v-model="editPram.url" placeholder="URL" />
      </el-form-item>
      <el-form-item label="父级" v-if="biztype.value!==3">
        <el-cascader v-model="editPram.pid" :disabled="isCreate ===1 && editPram.pid ===0" :options="biztype.value === 5 ? allTreeList : parentOptions" :props="categoryProps" style="width:100%" />
      </el-form-item>
      <el-form-item label="菜单图标" v-if="biztype.value===5">
        <el-input placeholder="请选择菜单图标" v-model="editPram.extra">
          <el-button slot="append" icon="el-icon-circle-plus-outline" @click="addIcon"></el-button>
        </el-input>
      </el-form-item>
      <el-form-item label="分类图标(180*180)" v-if="biztype.value === 1 || biztype.value === 3">
        <div class="upLoadPicBox" @click="modalPicTap('1')">
          <div v-if="editPram.extra" class="pictrue"><img :src="editPram.extra"></div>
          <div v-else class="upLoad">
            <i class="el-icon-camera cameraIconfont" />
          </div>
        </div>
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="editPram.sort"/>
      </el-form-item>
      <el-form-item label="状态">
        <el-switch v-model="editPram.status"  active-text="显示"
                   inactive-text="隐藏" :active-value="true" :inactive-value="false" />
      </el-form-item>
      <el-form-item label="扩展字段" v-if="biztype.value !== 1 && biztype.value !== 3 && biztype.value !== 5">
        <el-input v-model="editPram.extra" type="textarea" placeholder="扩展字段" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loadingBtn" @click="handlerSubmit('editPram')">确定</el-button>
        <el-button @click="close">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<!--创建和编辑公用一个组件-->
<script>
import * as categoryApi from '@/api/categoryApi.js'
import * as selfUtil from '@/utils/ZBKJIutil.js'
export default {
  // name: "edit"
  props: {
    prent: {
      type: Object,
      required: true
    },
    isCreate: {
      type: Number,
      default: 0
    },
    editData: {
      type: Object
    },
    biztype: {
      type: Object,
      required: true
    },
    allTreeList: {
      type: Array
    }
  },
  data() {
    return {
      loadingBtn: false,
      constants: this.$constants,
      editPram: {
        extra: null,
        name: null,
        pid: null,
        sort: 0,
        status: true,
        type: this.biztype.value,
        url: null,
        id: 0
      },
      categoryProps: {
        value: 'id',
        label: 'name',
        children: 'child',
        expandTrigger: 'hover',
        checkStrictly: true,
        emitPath: false
      },
      parentOptions: []
    }
  },
  mounted() {
    this.initEditData()
  },
  methods: {
    // 点击图标
    addIcon() {
      const _this = this
      _this.$modalIcon(function(icon) {
        _this.editPram.extra = icon
      })
    },
    // 点击商品图
    modalPicTap (tit, num, i) {
      const _this = this
      const attr = []
      this.$modalUpload(function(img) {
        if(tit==='1'&& !num){
          _this.editPram.extra = img[0].sattDir
        }
        if(tit==='2'&& !num){
          img.map((item) => {
            attr.push(item.attachment_src)
            _this.formValidate.slider_image.push(item)
          });
        }
      },tit, 'store')
    },
    close() {
      this.$emit('hideEditDialog')
    },
    initEditData() {
      this.parentOptions = [...this.allTreeList]
      this.addTreeListLabelForCasCard(this.parentOptions, 'child')
      const { extra, name, pid, sort, status, type, id, url } = this.editData
      if(this.isCreate === 1){
        this.editPram.extra = extra
        this.editPram.name = name
        this.editPram.pid = pid
        this.editPram.sort = sort
        this.editPram.status = status
        this.editPram.type = type
        this.editPram.url = url
        this.editPram.id = id
      }else{
        this.editPram.pid = this.prent.id
        this.editPram.type = this.biztype.value
      }
    },
    addTreeListLabelForCasCard(arr, child) {
      arr.forEach((o,i) => {
        if (o.child && o.child.length) {
          // o.disabled = true
          o.child.forEach((j) => {
            j.disabled = true
          })
        }
      })
    },
    handlerSubmit(formName) {
      this.$refs[formName].validate((valid) => {
        if (!valid) return
        this.handlerSaveOrUpdate(this.isCreate === 0)
      })
    },
    handlerSaveOrUpdate(isSave) {
      if (isSave) {
       // this.editPram.pid = this.prent.id
        this.loadingBtn = true
        categoryApi.addCategroy(this.editPram).then(data => {
          this.$emit('hideEditDialog')
          this.$message.success('创建目录成功')
          this.loadingBtn = false
        }).catch(() => {
          this.loadingBtn = false
        })
      } else {
        this.editPram.pid = Array.isArray(this.editPram.pid) ? this.editPram.pid[0] : this.editPram.pid
        this.loadingBtn = true
        categoryApi.updateCategroy(this.editPram).then(data => {
          this.$emit('hideEditDialog')
          this.$message.success('更新目录成功')
          this.loadingBtn = false
        }).catch(() => {
          this.loadingBtn = false
        })
      }
    }
  }
}
</script>

<style scoped>

</style>
