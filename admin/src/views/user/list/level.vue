<template>
  <el-form :model="ruleForm" ref="ruleForm" label-width="100px" class="demo-ruleForm" >
    <el-form-item>
      <el-alert title="请勿频繁更改，以免计算产生混乱！" type="warning"></el-alert>
    </el-form-item>
    <el-form-item label="用户等级" label-width="100px">
      <el-select v-model="ruleForm.levelId" clearable placeholder="请选择" @change="currentSel">
        <el-option
          v-for="item in levelList"
          :key="item.grade"
          :label="item.name"
          :value="item.id"
        >
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="扣除经验" label-width="100px" v-if="grade =='' ? false : grade < levelInfo.gradeLevel">
      <el-switch v-model="ruleForm.isSub"></el-switch>
    </el-form-item>
    <el-form-item>
      <el-button @click="resetForm('ruleForm')">取消</el-button>
      <el-button type="primary" @click="submitForm('ruleForm')">确定</el-button>
    </el-form-item>
  </el-form>
</template>
<script>
  import { userLevelUpdateApi} from '@/api/user'
  import {Debounce} from '@/utils/validate'
  export default {
    props:{
      levelInfo:{
        type:Object,
        default:{},
      },
      levelList:{
        type:Array,
        default:[]
      }
    },
    data() {
      return {
        grade:'',
        levelStatus:false,
        ruleForm: {
          isSub: false,
          levelId:"",
          uid:this.levelInfo.uid
        },
      };
    },
    created(){
      this.ruleForm.levelId = this.levelInfo.level?Number(this.levelInfo.level):''
    },
    watch: {
      levelInfo(val){
        this.ruleForm.uid = val.uid || 0;
        this.ruleForm.levelId = this.levelInfo.level?Number(this.levelInfo.level):val.levelId;
      },
    },
    methods: {
      submitForm:Debounce(function(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            userLevelUpdateApi(this.ruleForm).then(res=>{
              this.$message.success('编辑成功');
              this.$parent.$parent.getList();
              this.$parent.$parent.levelVisible = false;
              this.$refs[formName].resetFields()
              this.grade = '';
            })
          } else {
            return false;
          }
        });
      }),
      currentSel(){
        this.levelList.forEach(item=>{
          if(item.id == this.ruleForm.levelId){
            this.grade = item.grade;
          }
        })
      },
      resetForm(formName) {
        this.$nextTick(() => {
          this.$refs[formName].resetFields();
          this.grade = '';
        })
        this.$parent.$parent.levelVisible = false
      },
    },
  };
</script>
