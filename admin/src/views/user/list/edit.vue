<template>
  <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
      <el-form-item label="用户编号：">
        <el-input v-model="ruleForm.id" disabled class="selWidth"></el-input>
      </el-form-item>
      <el-form-item label="真实姓名：">
        <el-input v-model="ruleForm.realName" class="selWidth"></el-input>
      </el-form-item>
      <el-form-item label="手机号码：" prop="phone">
        <el-input v-model.number="ruleForm.phone" class="selWidth"></el-input>
      </el-form-item>
      <el-form-item label="生日：">
        <el-date-picker
          v-model="ruleForm.birthday"
          type="date"
          class="selWidth"
          placeholder="选择日期"
          format="yyyy 年 MM 月 dd 日"
          value-format="yyyy-MM-dd">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="身份证号：">
        <el-input v-model="ruleForm.cardId" class="selWidth"></el-input>
      </el-form-item>
      <el-form-item label="用户地址：">
        <el-input v-model="ruleForm.addres" class="selWidth"></el-input>
      </el-form-item>
      <el-form-item label="用户备注：">
        <el-input v-model="ruleForm.mark" type="textarea" class="selWidth"></el-input>
      </el-form-item>
      <el-form-item label="用户等级：">
        <el-select v-model="ruleForm.level" placeholder="请选择"  class="selWidth" clearable filterable>
          <el-option :value="item.id" v-for="(item, index) in levelList" :key="index" :label="item.name"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="用户分组：">
        <el-select v-model="ruleForm.groupId" placeholder="请选择"  class="selWidth" clearable filterable>
          <el-option :value="item.id" v-for="(item, index) in groupList" :key="index" :label="item.groupName"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="用户标签：">
        <el-select v-model="labelData" placeholder="请选择"  class="selWidth" clearable filterable multiple >
          <el-option :value="item.id" v-for="(item, index) in labelLists" :key="index" :label="item.name"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="推广员">
        <el-radio-group v-model="ruleForm.isPromoter">
          <el-radio :label="true">开启</el-radio>
          <el-radio :label="false">关闭</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="ruleForm.status">
          <el-radio :label="true">开启</el-radio>
          <el-radio :label="false">关闭</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm('ruleForm')">提交</el-button>
        <el-button @click="resetForm('ruleForm')">取消</el-button>
      </el-form-item>
    </el-form>
</template>

<script>
  import { groupListApi, levelListApi, tagListApi, userInfoApi, userUpdateApi } from '@/api/user'
  const defaultObj = {
    birthday: '',
    cardId: '',
    id : null,
    mark: '',
    phone: '',
    realName: '',
    addres:'',
    groupId: '',
    level: '',
    isPromoter: false,
    status: false,
  }
  export default {
    name: "UserEdit",
    props:{
      uid: {
        type: Number,
        default: null
      }
    },
    data() {
      return {
        ruleForm: Object.assign({}, defaultObj),
        groupData: [],
        labelData: [],
        labelLists: [],
        levelList: [],
        groupList: [],
        rules: {}
      }
    },
    mounted() {
      if(this.uid) this.userInfo()
      this.groupLists()
      this.levelLists()
      this.getTagList()
    },
    methods: {
      // 详情
      userInfo () {
        userInfoApi({ id: this.uid}).then(async res => {
          this.ruleForm = {
            birthday: res.birthday,
            cardId: res.cardId,
            id : res.uid,
            mark: res.mark,
            phone: res.phone,
            realName: res.realName,
            status: res.status,
            addres: res.addres,
            groupId: Number(res.groupId) || '',
            level: res.level || '',
            isPromoter: res.isPromoter,
            tagId: res.tagId || ''
          }
          this.labelData = res.tagId ? res.tagId.split(',').map(Number): []
        })
      },
      // 分组列表
      groupLists () {
        groupListApi({ page: 1, limit: 9999}).then(async res => {
          this.groupList = res.list
        })
      },
      //标签列表
      getTagList () {
        tagListApi({ page: 1, limit: 9999}).then(res => {
          this.labelLists = res.list
        })
      },
      // 等级列表
      levelLists () {
        levelListApi({ page: 1, limit: 9999, isShow: 1, isDel: 0}).then(async res => {
          this.levelList = res.list
        })
      },
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.ruleForm.tagId=this.labelData.join(',')
            userUpdateApi({id: this.ruleForm.id},this.ruleForm).then(async res => {
              this.$message.success('编辑成功')
              this.$parent.$parent.visible = false
              this.$parent.$parent.getList()
            })
          } else {
            return false;
          }
        });
      },
      resetForm(formName) {
        this.$refs[formName].resetFields();
        this.$emit('resetForm');
      }
    }
  }
</script>

<style scoped>
 .selWidth{
   width: 90%;
 }
</style>
