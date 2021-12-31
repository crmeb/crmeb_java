<template>
  <div class="divBox">
    <div>
      <el-card :bordered="false" class="box-card">
        <div>
          <el-tabs v-model="currentTab" @tab-click="changeTab">
            <el-tab-pane
              :label="item.label"
              :name="item.value.toString()"
              v-for="(item, index) in headerList"
              :key="index + '-only'"
            />
          </el-tabs>
        </div>
        <el-row type="flex" class="mb20 mt-1">
          <el-col>
            <el-button
              type="primary"
              icon="el-icon-document"
              @click="syncRoutine()"
              v-hasPermi="['admin:wechat:routine:sync']"
              >同步小程序订阅消息</el-button
            >
            <el-button
              type="primary"
              icon="el-icon-document"
              @click="syncWechat()"
              v-hasPermi="['admin:wechat:whcbqhn:sync']"
              >同步微信模版消息</el-button
            >
          </el-col>
        </el-row>
        <div class="description">
          <p><span class="iconfont iconxiaochengxu"></span> 小程序经营类目：生活服务 > 百货/超市/便利店</p>
          <p><span class="iconfont icongongzhonghao"></span> 公众号经营类目：IT科技/互联网|电子商务，IT科技/IT软件与服务</p>
        </div>
        <el-table 
        :data="levelLists" 
        ref="table" 
        class="mt25" 
        size="small" 
        v-loading="loadingList"
        :header-cell-style=" {fontWeight:'bold'}">
          <el-table-column label="ID" prop="id" width="80"></el-table-column>
          <el-table-column label="通知类型" prop="type"></el-table-column>
          <el-table-column label="通知场景说明" prop="description"></el-table-column>
          <el-table-column label="标识" prop="mark"></el-table-column>
          <el-table-column label="公众号模板" prop="isWechat" v-if="currentTab == '1'">
            <template slot-scope="scope" v-if="scope.row.isWechat !== 0">
              <el-switch
                v-model="scope.row.isWechat"
                :active-value="1"
                :inactive-value="2"
                active-text="启用"
                inactive-text="禁用"
                @change="changeWechat(scope.row)"
              >
              </el-switch>
            </template>
          </el-table-column>
          <el-table-column label="小程序订阅" prop="isRoutine" v-if="currentTab == '1'">
            <template slot-scope="scope" v-if="scope.row.isRoutine !== 0">
              <el-switch
                v-model="scope.row.isRoutine"
                :active-value="1"
                :inactive-value="2"
                active-text="启用"
                inactive-text="禁用"
                @change="changeRoutine(scope.row)"
              >
              </el-switch>
            </template>
          </el-table-column>
          <el-table-column label="发送短信" prop="isSms">
            <template slot-scope="scope" v-if="scope.row.isSms !== 0">
              <el-switch
                v-model="scope.row.isSms"
                :active-value="1"
                :inactive-value="2"
                active-text="启用"
                inactive-text="禁用"
                @change="changeSms(scope.row)"
              >
              </el-switch>
            </template>
          </el-table-column>
          <el-table-column label="设置" prop="id">
            <template slot-scope="scope">
              <el-button type="text" @click="setting(scope.row)" v-hasPermi="['admin:system:notification:detail']">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
    <el-dialog
      title="通知详情"
      :visible.sync="centerDialogVisible"
      width="50%"
      >
      <el-tabs :value="infoTab" @tab-click="changeInfo">
        <el-tab-pane
          :label="item.label"
          :name="item.value.toString()"
          v-for="(item, index) in currentTab == '1' ? infoList : infoList1"
          :key="index"
        />
        <el-form ref="form" :model="form" label-width="80px">
          <el-form-item label="ID">
            <el-input v-model="form.id" disabled></el-input>
          </el-form-item>
          <el-form-item label="模板名" v-if="form.name">
            <el-input v-model="form.name" disabled></el-input>
          </el-form-item>
          <el-form-item label="模板ID" v-if="form.tempId">
            <el-input v-model="form.tempId"></el-input>
          </el-form-item>
          <el-form-item label="模板编号" v-if="form.tempKey"> 
            <el-input v-model="form.tempKey" disabled></el-input>
          </el-form-item>
          <el-form-item label="模板说明" v-if="form.title"> 
            <el-input v-model="form.title" disabled></el-input>
          </el-form-item>
          <el-form-item label="模板内容" v-if="form.content"> 
            <el-input v-model="form.content" disabled></el-input>
          </el-form-item>
          <el-form-item label="状态">
            <el-radio-group v-model="form.status">
              <el-radio label="1">开启</el-radio>
              <el-radio label="2">关闭</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </el-tabs>
      <span slot="footer" class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submit()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {notificationListApi,notificationRoutine,notificationWechat,notificationSms,notificationDetail,notificationUpdate} from '@/api/systemFormConfig'
import {wechatAsyncApi,routineAsyncApi} from '@/api/wxApi'
import {Debounce} from '@/utils/validate'
export default {
  data() {
    return {
      modalTitle: "",
      notificationModal: false,
      headerList: [
        { label: "通知会员", value: "1" },
        { label: "通知平台", value: "2" },
      ],
      id:0,
      levelLists: [],
      currentTab: "1",
      loading: false,
      formData: {},
      industry: null,
      loadingList:false,
      centerDialogVisible:false,
      infoList:[],
      infoList1:[
        { label: "短信", value: "sms" },
      ],
      form:{
        content:'',
        name:'',
        id:'',
        status:null,
        tempId:'',
        tempKey:'',
        title:'',
      },
      detailType:'',
      infoTab:''
    };
  },
  created() {
    this.getNotificationList(Number(this.currentTab));
  },
  methods: {
    changeTab(data) {
      this.getNotificationList(data.name);
    },
    //获取消息列表
    getNotificationList(id){
      this.loadingList = true;
      notificationListApi({sendType:id}).then(res=>{
        this.loadingList = false;
        this.levelLists = res;
      }).catch(res=>{
        this.loadingList = false;
      })
    },
    //公众号消息开关
    changeWechat(row){
      notificationWechat(row.id).then(res=>{
        this.$modal.msgSuccess("修改成功");
      })
    },
    //小程序消息开关
    changeRoutine(row){
      notificationRoutine(row.id).then(res=>{
        this.$modal.msgSuccess("修改成功");
      })
    },
    //短信消息开关
    changeSms(row){
      notificationSms(row.id).then(res=>{
        this.$modal.msgSuccess("修改成功");
      })
    },
   //详情tab切换
    changeInfo(data){
      this.getNotificationDetail(data);
    },
    //详情数据
    getNotificationDetail(param){
      let data = {
        id:this.id,
        type:param.name
      };
      this.$set(this,'detailType',data.type);
      notificationDetail(data).then(res=>{
        this.form = res;
        this.$set(this.form,'status',res.status.toString());
      })
    },
    // 设置
    setting(row) {
      this.infoList = [];
      this.id = row.id;
      this.centerDialogVisible = true;
      if(row.isWechat !== 0){
        this.infoList.push({ label: "公众号模板消息", value: "wechat" });
      }
       if(row.isRoutine !== 0){
        this.infoList.push({ label: "小程序订阅消息", value: "routine"});
      }
      if(row.isSms !== 0){
        this.infoList.push({ label: "短信", value: "sms" });
      }
      this.infoTab = this.infoList[0].value;
      this.getNotificationDetail({name:this.infoTab});
    },
    //修改通知
    submit:Debounce(function(){
      let data = {
        id:this.id,
        status:Number(this.form.status),
        tempId:this.form.tempId,
        type:this.detailType
      };
      notificationUpdate(data).then(res=>{
        this.$modal.msgSuccess("修改成功");
        this.centerDialogVisible = false;
        this.getNotificationList();
      })
    }),
    syncWechat(){
      wechatAsyncApi().then(res=>{
        this.$message.success('同步成功');
      })
    },
    syncRoutine(){
      routineAsyncApi().then(res=>{
        this.$message.success('同步成功');
      })
    }
  },
};
</script>

<style scoped>
  .mt-1{
    margin-top:6px;
  }
  .description{
    padding: 16px;
    position: relative;
    border-radius: 4px;
    margin-bottom: 20px;
    color: #515a6e;
    line-height: 1.5;
    font-size: 14px;
    border: 1px solid #abdcff;
    background-color: #f0faff;
  }
  .iconfont{
    color:#06C05F;
  }
</style>