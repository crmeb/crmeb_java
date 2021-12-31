<template>
  <el-dialog
    v-if="dialogVisible"
    title="用户等级"
    :visible.sync="dialogVisible"
    width="500px"
    :before-close="handleClose" >
    <el-form :model="user" :rules="rules" ref="user" label-width="100px" class="demo-ruleForm" v-loading="loading">
      <el-form-item label="等级名称" prop="name">
        <el-input v-model="user.name" placeholder="请输入等级名称"></el-input>
      </el-form-item>
      <el-form-item label="等级" prop="grade">
        <el-input  v-model.number="user.grade" placeholder="请输入等级"></el-input>
      </el-form-item>
      <el-form-item label="享受折扣(%)" prop="discount">
        <el-input-number  :min="0" :max="100" step-strictly  v-model="user.discount" placeholder="请输入享受折扣"></el-input-number>
      </el-form-item>
      <el-form-item label="经验" prop="experience">
        <el-input-number  v-model.number="user.experience" placeholder="请输入经验" :min="0" step-strictly></el-input-number>
      </el-form-item>
      <el-form-item label="图标" prop="icon">
        <div class="upLoadPicBox" @click="modalPicTap('1', 'icon')">
          <div v-if="user.icon" class="pictrue"><img :src="user.icon"></div>
          <div v-else-if="formValidate.icon" class="pictrue"><img :src="formValidate.icon"></div>
          <div v-else class="upLoad">
            <i class="el-icon-camera cameraIconfont" />
          </div>
        </div>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="resetForm('user')">取 消</el-button>
      <el-button type="primary" @click="submitForm('formValidate')" v-hasPermi="['admin:system:user:level:update','admin:system:user:level:save']">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  import { levelSaveApi, levelInfoApi, levelUpdateApi } from '@/api/user'
  import {Debounce} from '@/utils/validate'
  const obj = {
    name:'',
    grade: 1,
    discount: '',
    icon: '',
    image: '',
    id: null
  }
  export default {
    name: "CreatGrade",
    props:{
      'user':Object
    },
    data() {
      return {
        dialogVisible: false,
        formValidate: Object.assign({},obj),
        loading: false,
        rules:{
          name: [
            {  required: true, message: '请输入等级名称', trigger: 'blur' }
          ],
          grade: [
            {  required: true, message: '请输入等级', trigger: 'blur' },
            { type: 'number', message: '等级必须为数字值'}
          ],
          discount: [
            {  required: true, message: '请输入折扣', trigger: 'blur'},
          ],
          experience: [
            {  required: true, message: '请输入经验', trigger: 'blur'},
            { type: 'number', message: '经验必须为数字值'}
          ],
          icon: [
            {  required: true, message: '请上传图标', trigger: 'change' }
          ],
          image: [
            {  required: true, message: '请上传用户背景', trigger: 'change' }
          ],
        }
      }
    },
    methods:{
      // 点击商品图
      modalPicTap (tit, num) {
         const _this = this
        this.$modalUpload(function(img) {
          tit==='1'&& num === 'icon' ? _this.formValidate.icon = img[0].sattDir : _this.formValidate.image = img[0].sattDir
          this.$set(_this.user,'icon', _this.formValidate.icon);
          this.$set(_this.user,'isShow', false);
        },tit , 'user')
      },
      info(id) {
        this.loading = true
        levelInfoApi({id: id}).then(res => {
          this.formValidate = res
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      },
      handleClose() {
         this.$nextTick(() => {
          this.$refs.user.resetFields();
        })
        this.dialogVisible = false;
        // this.user = Object.assign({}, '')
      },
      submitForm:Debounce(function(formName) {
        this.$refs.user.validate((valid) => {
          if (valid) {
            this.loading = true
            let data = {
              discount:this.user.discount,
              experience:this.user.experience,
              grade:this.user.grade,
              icon:this.user.icon,
              id:this.user.id,
              isShow:this.user.isShow,
              name:this.user.name
            };
            this.user.id ? levelUpdateApi(this.user.id, data).then(res => {
              this.$message.success('编辑成功')
              this.loading = false
              this.handleClose()
              this.formValidate = Object.assign({},obj)
              this.$parent.getList()
            }).catch(() => {
              this.loading = false
            }): levelSaveApi(this.user).then(res => {
              this.$message.success('添加成功')
              this.loading = false
              this.handleClose()
              this.formValidate = Object.assign({},obj)
              this.$parent.getList()
            }).catch(() => {
              this.loading = false
              this.formValidate = Object.assign({},obj)
            })
          } else {
            return false;
          }
        });
      }),
      resetForm(formName) {
        
        // this[formName] = {};
         this.$nextTick(() => {
          this.$refs.user.resetFields();
        })
        this.dialogVisible = false;
      }
    }
  }
</script>

<style scoped>

</style>
