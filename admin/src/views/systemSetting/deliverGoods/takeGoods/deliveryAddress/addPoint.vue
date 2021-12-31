<template>
  <el-dialog v-model="dialogFormVisible" :title="id?'修改提货点':'添加提货点'" :visible.sync="dialogFormVisible" width="750px" @close="cancel">
    <template v-if="dialogFormVisible">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="150px" class="demo-ruleForm" @submit.native.prevent v-loading="loading">
        <el-form-item label="提货点名称：" prop="name">
          <el-input v-model="ruleForm.name" maxlength="40" placeholder="请输入提货点名称" class="dialogWidth"></el-input>
        </el-form-item>
        <el-form-item label="提货点简介：">
          <el-input v-model="ruleForm.introduction" maxlength="100"  placeholder="请输入提货点简介" class="dialogWidth"></el-input>
        </el-form-item>
        <el-form-item label="提货点手机号：" prop="phone">
          <el-input v-model="ruleForm.phone" placeholder="请输入提货点手机号" class="dialogWidth"></el-input>
        </el-form-item>
        <el-form-item label="提货点地址：" prop="address">
          <el-cascader
            class="dialogWidth"
            clearable
            v-model="ruleForm.address"
            :options="addresData"
            :props="{ value: 'name', label: 'name',children:'child',expandTrigger: 'hover'}"
            @change="handleChange"></el-cascader>
        </el-form-item>
        <el-form-item label="详细地址：" prop="detailedAddress">
          <el-input v-model="ruleForm.detailedAddress" placeholder="请输入详细地址" class="dialogWidth"></el-input>
        </el-form-item>
        <el-form-item label="提货点营业：">
          <el-time-picker
            is-range
            v-model="dayTime"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            placeholder="请选择时间营业时间"
            value-format="HH:mm:ss"
            @change="onchangeTime">
          </el-time-picker>
        </el-form-item>
        <!-- prop="image"-->
        <el-form-item label="提货点logo：">
          <div class="upLoadPicBox" @click="modalPicTap('1')">
            <div class="pictrue" v-if="ruleForm.image"><img :src="ruleForm.image"></div>
            <div v-else class="upLoad">
              <i class="el-icon-camera cameraIconfont" />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="经纬度：" prop="latitude">
          <el-tooltip content="请点击查找位置选择位置">
            <el-input v-model="ruleForm.latitude" placeholder="请查找位置" class="dialogWidth" readOnly>
              <el-button slot="append" @click="onSearch">查找位置</el-button>
            </el-input>
          </el-tooltip>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="editForm('ruleForm')" v-if="id"  v-hasPermi="['admin:system:store:update']">修改</el-button>
        <el-button type="primary" @click="submitForm('ruleForm')" v-else v-hasPermi="['admin:system:store:save']">提交</el-button>
      </div>
      <el-dialog v-model="modalMap" title='上传经纬度' :visible.sync="modalMap" append-to-body class="mapBox" width="500px">
        <iframe
          id="mapPage" width="100%" height="100%" frameborder=0
          v-bind:src="keyUrl"
        ></iframe>
      </el-dialog>
    </template>
  </el-dialog>
</template>

<script>
  import { storeSaveApi, storeInfoApi, storeUpdateApi } from '@/api/storePoint';
  import * as logistics from '@/api/logistics'
  import { configInfo } from '@/api/systemConfig';
  import Templates from "../../../../appSetting/wxAccount/wxTemplate/index";
  import {Debounce} from '@/utils/validate'
  export default {
    name: "index",
    components: {Templates},
    // props: {
    //   children: 'child',
    //   label: 'name',
    //   value: 'name'
    // },
    data() {
      const validatePhone = (rule, value, callback) => {
        if (!value) {
          return callback(new Error('请填写手机号'));
        } else if (!/^1[3456789]\d{9}$/.test(value)) {
          callback(new Error('手机号格式不正确!'));
        } else {
          callback();
        }
      };
      const validateUpload = (rule, value, callback) => {
        if (!this.ruleForm.image) {
          callback(new Error('请上传提货点logo'))
        } else {
          callback()
        }
      };
      return {
        loading: false,
        dialogFormVisible: false,
        modalMap: false,
        keyUrl: '',
        addresData:[],
        ruleForm: {
          name: '',
          introduction: '',
          phone: '',
          address: '',
          detailedAddress: '',
          dayTime: '',
          image:'',
          latitude: ''
        },
        id:0,
        dayTime:['',''],
        rules: {
          name: [
            { required: true, message: '请输入提货点名称', trigger: 'blur' }
          ],
          address: [
            { required: true, message: '请选择提货点地址', trigger: 'change' }
          ],
          dayTime: [
            { required: true, type: 'array', message: '请选择提货点营业时间', trigger: 'change' }
          ],
          phone: [
            { required: true, validator: validatePhone, trigger: 'blur' }
          ],
          detailedAddress: [
            { required: true, message: '请输入详细地址', trigger: 'blur' }
          ],
          image: [
            { required: true, validator: validateUpload, trigger: 'change' }
          ],
          latitude: [
            { required: true, message: '请选择经纬度', trigger: 'blur' }
          ]
        }
      }
    },
    created(){
      this.ruleForm.image = '';
      let cityList = JSON.parse(sessionStorage.getItem('cityList'));
      this.addresData = cityList;
      this.getCityList();
      this.getKey();
    },
    mounted(){
      window.addEventListener('message', function (event) {
        // 接收位置信息，用户选择确认位置点后选点组件会触发该事件，回传用户的位置信息
        var loc = event.data;
        if (loc && loc.module === 'locationPicker') { // 防止其他应用也会向该页面post信息，需判断module是否为'locationPicker'
          window.parent.selectAdderss(loc);
        }
      }, false);
      window.selectAdderss = this.selectAdderss;
    },
    methods:{
      //详情
      getInfo (id) {
        let that = this;
        that.id = id;
        this.loading = true;
        storeInfoApi({id:id}).then(res=>{
          that.ruleForm = res;
          that.ruleForm.address = res.address.split(",");
          that.dayTime = res.dayTime.split(",")
          this.loading = false;
        })
      },
      //取消
      cancel (){
        this.dialogFormVisible = false;
        this.clearFrom();
        this.ruleForm.image = '';
        this.resetForm('ruleForm');
        this.id = 0
      },
      //重置
      resetForm (name) {
        this.$refs[name].resetFields();
      },
      // 提交
      submitForm:Debounce(function(name) {
        this.$refs[name].validate((valid) => {
          if (valid) {
            storeSaveApi(this.ruleForm).then(async () => {
              this.$message.success('提交成功');
              this.dialogFormVisible = false;
              this.$parent.tableList();
              this.$parent.storeGetCount();
              this.clearFrom();
              this.resetForm(name);
              this.id = 0;
            })
          } else {
            return false;
          }
        })
      }),
      //编辑
      editForm:Debounce(function(name){
        this.$refs[name].validate((valid) => {
          if (valid) {
            this.handleChange(this.ruleForm.address);
            storeUpdateApi(this.ruleForm,this.id).then(async () => {
              this.$message.success('编辑成功');
              this.dialogFormVisible = false;
              this.$parent.tableList();
              this.clearFrom();
              this.resetForm(name);
              this.id = 0;
            })
          } else {
            return false;
          }
        })
      }),
      //数据归为初始状态
      clearFrom(){
        this.ruleForm.introduction = '';
        this.dayTime = ['',''];
      },
      //确认省市区
      handleChange(e){
        let province = e[0];
        let city = e[1];
        let area = e[2];
        if(e.length===2){
          this.ruleForm.address = province + ',' + city;
        }else if(e.length===3){
          this.ruleForm.address = province + ',' + city + ',' + area;
        }
      },
      //营业时间
      onchangeTime(e){
        this.ruleForm.dayTime = e ? e.join(',') : '';
      },
      //上传图片
      modalPicTap (tit) {
        const _this = this
        this.$modalUpload(function(img) {
          _this.ruleForm.image = img[0].sattDir
        },tit, 'system')
      },
      //查找位置
      onSearch () {
        this.modalMap = true;
      },
      // 选择经纬度
      selectAdderss (data) {
        this.ruleForm.latitude = data.latlng.lat + ',' + data.latlng.lng;
        this.modalMap = false;
      },
      // key值
      getKey () {
        const _pram = { id: 74 };
        configInfo(_pram).then(async res => {
          let keys = res.tengxun_map_key;
          this.keyUrl = `https://apis.map.qq.com/tools/locpicker?type=1&key=${keys}&referer=myapp`;
        })
      },
      getCityList() {
        logistics.cityListTree().then(res => {
          sessionStorage.setItem('cityList',JSON.stringify(res));
          let cityList = JSON.parse(sessionStorage.getItem('cityList'));
          this.addresData = cityList;
        }).catch(res => {
          this.$message.error(res.message)
        })
      },
    }
  }
</script>

<style lang="scss">
  .el-input-group__append, .el-input-group__prepend{
    background-color:#1890ff!important;
    color: #fff!important;
    border-color:#1890ff!important;
    border-radius: 0 4px 4px 0;
  }
  .cascaderW{
    width: 350px;
  }
  .mapBox .el-dialog__body{
    height: 640px !important;
  }
</style>

