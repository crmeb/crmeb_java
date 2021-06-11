<template>
  <el-dialog
    title="用户等级"
    :visible.sync="dialogVisible"
    width="500px"
    :before-close="handleClose">
    <el-form :model="formValidate" :rules="rules" ref="formValidate" label-width="100px" class="demo-ruleForm" v-loading="loading">
      <el-form-item label="等级名称" prop="name">
        <el-input v-model="formValidate.name" placeholder="请输入等级名称"></el-input>
      </el-form-item>
      <el-form-item label="等级" prop="grade">
        <el-input  v-model.number="formValidate.grade" placeholder="请输入等级"></el-input>
      </el-form-item>
      <el-form-item label="享受折扣(%)" prop="discount">
        <el-input-number  :min="0" :max="100" step-strictly  v-model="formValidate.discount" placeholder="请输入享受折扣"></el-input-number>
      </el-form-item>
      <el-form-item label="经验" prop="experience">
        <el-input-number  v-model.number="formValidate.experience" placeholder="请输入经验" :max="999999" step-strictly></el-input-number>
      </el-form-item>
      <el-form-item label="图标" prop="icon">
        <div class="upLoadPicBox" @click="modalPicTap('1', 'icon')">
          <div v-if="formValidate.icon" class="pictrue"><img :src="formValidate.icon"></div>
          <div v-else class="upLoad">
            <i class="el-icon-camera cameraIconfont" />
          </div>
        </div>
      </el-form-item>
      <!--<el-form-item label="用户背景" required prop="image">-->
        <!--<div class="upLoadPicBox" @click="modalPicTap('1', 'image')">-->
          <!--<div v-if="formValidate.image" class="pictrue"><img :src="formValidate.image"></div>-->
          <!--<div v-else class="upLoad">-->
            <!--<i class="el-icon-camera cameraIconfont" />-->
          <!--</div>-->
        <!--</div>-->
      <!--</el-form-item>-->
      <el-form-item label="是否显示" required>
        <el-radio-group v-model="formValidate.isShow">
          <el-radio :label="true" class="radio">显示</el-radio>
          <el-radio :label="false">隐藏</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="resetForm('formValidate')">取 消</el-button>
      <el-button type="primary" @click="submitForm('formValidate')">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  import { levelSaveApi, levelInfoApi, levelUpdateApi } from '@/api/user'
  const obj = {
    name:'',
    grade: 1,
    discount: '',
    icon: '',
    image: '',
    isShow: true,
    id: null
  }
  export default {
    name: "CreatGrade",
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
          ]
        }
      }
    },
    methods:{
      // 点击商品图
      modalPicTap (tit, num) {
        const _this = this
        this.$modalUpload(function(img) {
          tit==='1'&& num === 'icon' ? _this.formValidate.icon = img[0].sattDir : _this.formValidate.image = img[0].sattDir
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
        this.dialogVisible = false
        this.$refs['formValidate'].resetFields();
      },
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.loading = true
            this.formValidate.id ? levelUpdateApi({id:this.formValidate.id}, this.formValidate).then(res => {
              this.$message.success('编辑成功')
              this.loading = false
              this.handleClose()
              this.formValidate = Object.assign({},obj)
              this.$parent.getList()
            }).catch(() => {
              this.loading = false
            }): levelSaveApi(this.formValidate).then(res => {
              this.$message.success('添加成功')
              this.loading = false
              this.handleClose()
              this.$parent.getList()
            }).catch(() => {
              this.loading = false
            })
          } else {
            return false;
          }
        });
      },
      resetForm(formName) {
        this.dialogVisible = false
        this.$refs[formName].resetFields();
      }
    }
  }
</script>

<style scoped>

</style>
