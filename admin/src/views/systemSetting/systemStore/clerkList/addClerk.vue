<template>
  <el-dialog v-model="dialogFormVisible" :title="id?'修改核销员':'添加核销员'"  :visible.sync="dialogFormVisible" width="750px" @close="cancel">
    <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="150px" class="demo-ruleForm" @submit.native.prevent>
      <el-form-item label="商城用户：" prop="avatar">
        <div class="publicPicBox" @click="upImg">
          {{ruleForm.avatar}}
          <div class="pictrue" v-if="ruleForm.avatar">
            <el-image
              :src="ruleForm.avatar"
              fit="cover"></el-image>
          </div>
          <div class="upLoad acea-row row-center-wrapper" v-else>
            <i class="el-icon-camera iconfont"></i>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="所属提货点：" prop="storeId">
        <el-select v-model="ruleForm.storeId" placeholder="请选择" style="width:50%" clearable>
          <el-option
            v-for="item in storeSelectList"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="核销员名称：">
        <el-input v-model="ruleForm.staffName" placeholder="请输入提货点简介" class="dialogWidth"></el-input>
      </el-form-item>
      <el-form-item label="手机号码：">
        <el-input v-model="ruleForm.phone" placeholder="请输入提货点简介" class="dialogWidth"></el-input>
      </el-form-item>
      <el-form-item label="核销开关：">
        <el-radio-group v-model="ruleForm.verifyStatus">
          <el-radio :label="1">开启</el-radio>
          <el-radio :label="0">关闭</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="状态：">
        <el-radio-group v-model="ruleForm.status">
          <el-radio :label="1">开启</el-radio>
          <el-radio :label="0">关闭</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="cancel">取 消</el-button>
      <el-button type="primary" @click="editForm('ruleForm')" v-if="id">修改</el-button>
      <el-button type="primary" @click="submitForm('ruleForm')" v-else>提交</el-button>
    </div>
    <customer-info ref="customer" @upImgUid="upImgUid"></customer-info>
  </el-dialog>
</template>

<script>
  import customerInfo from '@/components/customerInfo';
  import { storeStaffSaveApi, storeStaffUpdateApi, storeStaffInfoApi, storeListApi } from '@/api/storePoint';
    export default {
      name: "addClerk",
      components: { customerInfo },
      props: {
        storeSelectList: Array
      },
      data(){
        const validateUpload = (rule, value, callback) => {
          if (!this.ruleForm.avatar) {
            callback(new Error('请上传商城用户图片'))
          } else {
            callback()
          }
        };
        return{
          dialogFormVisible: false,
          id:0,
          ruleForm:{
            avatar:'',
            phone:'',
            staffName:'',
            status:1,
            storeId:'',
            verifyStatus:1,
            uid:''
          },
          rules: {
            avatar: [
              { required: true, validator: validateUpload, trigger: 'change' }
            ],
            storeId: [
              { required: true, message: '请选择提货点地址', trigger: 'change' }
            ]
          }
        }
      },
      created(){

      },
      mounted(){

      },
      methods:{
        //接收来自子集的值；
        upImgUid(id,img){
          this.ruleForm.avatar = img;
          this.ruleForm.uid = id;
        },
        upImg(){
          this.$refs.customer.dialogFormVisible = true;
          this.$refs.customer.tableList();
        },
        //详情
        getInfo (id) {
          let that = this;
          that.id = id;
          storeStaffInfoApi({id:id}).then(res=>{
            that.ruleForm = res;
          }).catch(res=>{
            this.$message.error(res.message);
          })
        },
        //取消
        cancel(){
          this.dialogFormVisible = false;
          this.clearFrom();
          this.resetForm('ruleForm');
          this.id = 0
        },
        //数据归为初始状态
        clearFrom(){
          this.ruleForm.phone = '';
          this.ruleForm.staffName = '';
          this.ruleForm.status = 1;
          this.ruleForm.verifyStatus = 1;
        },
        //重置
        resetForm (name) {
          this.$refs[name].resetFields();
        },
        // 提交
        submitForm (name) {
          this.$refs[name].validate((valid) => {
            if (valid) {
              let phone = this.ruleForm.phone;
              if(phone){
                 if(!/^1[3456789]\d{9}$/.test(phone)){
                   return this.$message.error('手机号格式不正确');
                 }
              }
              storeStaffSaveApi(this.ruleForm).then(async () => {
                this.$message.success('提交成功');
                this.dialogFormVisible = false;
                this.$emit("tableList");
                this.clearFrom();
                this.resetForm(name);
                this.id = 0;
              }).catch(res => {
                this.$message.error(res.message);
              })
            } else {
              return false;
            }
          })
        },
        //  编辑
        editForm(name){
          this.$refs[name].validate((valid) => {
            if (valid) {
              let phone = this.ruleForm.phone;
              if(phone){
                if(!/^1[3456789]\d{9}$/.test(phone)){
                  return this.$message.error('手机号格式不正确');
                }
              }
              storeStaffUpdateApi(this.ruleForm).then(async () => {
                this.$message.success('编辑成功');
                this.dialogFormVisible = false;
                this.$emit("tableList");
                this.clearFrom();
                this.resetForm(name);
                this.id = 0;
              }).catch(res => {
                this.$message.error(res.message);
              })
            } else {
              return false;
            }
          })
        }
      }
    }
</script>

<style scoped>

</style>
