<template>
  <el-dialog v-model="dialogFormVisible" :title="id?'修改核销员':'添加核销员'"  :visible.sync="dialogFormVisible" width="750px" @close="cancel">
    <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="150px" class="demo-ruleForm" @submit.native.prevent v-loading="loading">
      <el-form-item label="管理员：" prop="uid">
        <span v-text="ruleForm.avatar"></span>
        <el-button type="primary" size="small" @click="upImg">选择管理员</el-button>
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
        <el-input v-model="ruleForm.staffName" placeholder="请输入核销员名称" class="dialogWidth"></el-input>
      </el-form-item>
      <el-form-item label="手机号码：">
        <el-input v-model="ruleForm.phone" placeholder="请输入手机号码" class="dialogWidth"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="cancel">取 消</el-button>
      <el-button type="primary" @click="editForm('ruleForm')" v-if="id" v-hasPermi="['admin:system:staff:update']">修改</el-button>
      <el-button type="primary" @click="submitForm('ruleForm')" v-else v-hasPermi="['admin:system:staff:save']">提交</el-button>
    </div>
    <customer-info ref="customer" @upImgUid="upImgUid"></customer-info>
  </el-dialog>
</template>

<script>
  import customerInfo from '@/components/customerInfo';
  import { storeStaffSaveApi, storeStaffUpdateApi, storeStaffInfoApi, storeListApi } from '@/api/storePoint';
  import { getStoreStaff } from '@/libs/public'
  import {Debounce} from '@/utils/validate'
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
          loading: false,
          dialogFormVisible: false,
          id:0,
          ruleForm:{
            phone:'',
            storeId:'',
            uid:'',
            avatar: ''
          },
          name: '',
          rules: {
            uid: [
              { required: true, message: '请选择管理员', trigger: 'change' }
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
        upImgUid(row){
          this.ruleForm.avatar = row.account
          this.ruleForm.uid = row.id;
        },
        upImg(){
          this.$refs.customer.dialogFormVisible = true;
          this.$refs.customer.tableList();
        },
        //详情
        getInfo (id) {
          this.id = id;
          this.loading = true
          storeStaffInfoApi({id:id}).then(res=>{
            this.ruleForm = res;
            this.loading = false
          }).catch(res=>{
            this.loading = false
          })
        },
        //取消
        cancel(){
          this.dialogFormVisible = false;
          this.clearFrom();
          this.resetForm('ruleForm');
          this.ruleForm.avatar = '';
          this.id = 0
        },
        //数据归为初始状态
        clearFrom(){
          this.ruleForm.phone = '';
          this.ruleForm.staffName = '';
        },
        //重置
        resetForm (name) {
          this.$refs[name].resetFields();
        },
        // 提交
        submitForm:Debounce(function(name) {
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
                getStoreStaff()
              })
            } else {
              return false;
            }
          })
        }),
        //  编辑
        editForm:Debounce(function(name){
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
                getStoreStaff()
              })
            } else {
              return false;
            }
          })
        })
      }
    }
</script>

<style scoped>

</style>
