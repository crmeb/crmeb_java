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
              :icon="Document"
              @click="syncRoutine()"
              v-hasPermi="['admin:wechat:routine:sync']"
              >同步小程序订阅消息</el-button
            >
            <el-button
              type="primary"
              :icon="Document"
              @click="syncWechat()"
              v-hasPermi="['admin:wechat:whcbqhn:sync']"
              >同步微信模版消息</el-button
            >
          </el-col>
        </el-row>
        <div class="description">
          <p><span class="iconfont iconxiaochengxu"></span> 小程序经营类目：生活服务 > 百货/超市/便利店</p>
          <p>
            <span class="iconfont icongongzhonghao"></span> 公众号经营类目：IT科技/互联网|电子商务，IT科技/IT软件与服务
          </p>
        </div>
        <el-table :data="levelLists" ref="tableRef" class="mt25" v-loading="loadingList">
          <el-table-column label="ID" prop="id" width="80"></el-table-column>
          <el-table-column label="通知类型" prop="type"></el-table-column>
          <el-table-column label="通知场景说明" prop="description"></el-table-column>
          <el-table-column label="标识" prop="mark"></el-table-column>
          <el-table-column label="公众号模板" prop="isWechat" v-if="currentTab == '1'">
            <template #default="scope">
              <el-switch
                v-if="scope.row.isWechat !== 0"
                v-model="scope.row.isWechat"
                :active-value="1"
                :inactive-value="2"
                active-text="启用"
                inactive-text="禁用"
                @change="changeWechat(scope.row)"
              >
              </el-switch>
              <span v-else>{{ scope.row.isWechat ? '启用' : '禁用' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="小程序订阅" prop="isRoutine" v-if="currentTab == '1'">
            <template #default="scope">
              <el-switch
                v-if="scope.row.isRoutine !== 0"
                v-model="scope.row.isRoutine"
                :active-value="1"
                :inactive-value="2"
                active-text="启用"
                inactive-text="禁用"
                @change="changeRoutine(scope.row)"
              >
              </el-switch>
              <span v-else>{{ scope.row.isRoutine ? '启用' : '禁用' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="发送短信" prop="isSms">
            <template #default="scope">
              <el-switch
                v-if="scope.row.isSms !== 0"
                v-model="scope.row.isSms"
                :active-value="1"
                :inactive-value="2"
                active-text="启用"
                inactive-text="禁用"
                @change="changeSms(scope.row)"
              >
              </el-switch>
              <span v-else>{{ scope.row.isSms ? '启用' : '禁用' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="设置" prop="id" width="80">
            <template #default="scope">
              <a @click="setting(scope.row)" v-hasPermi="['admin:system:notification:detail']">详情</a>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
    <el-dialog title="通知详情" v-model="centerDialogVisible" width="540px">
      <el-tabs v-model="infoTab" @tab-click="changeInfo">
        <el-tab-pane
          :label="item.label"
          :name="item.value.toString()"
          v-for="(item, index) in currentTab == '1' ? infoList : infoList1"
          :key="index"
        />
        <el-form ref="formRef" :model="form" label-width="80px" v-loading="loading">
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
              <el-radio label="1" value="1">开启</el-radio>
              <el-radio label="2" value="2">关闭</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </el-tabs>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="centerDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submit()">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { Document } from '@element-plus/icons-vue';
import {
  notificationListApi,
  notificationRoutine,
  notificationWechat,
  notificationSms,
  notificationDetail,
  notificationUpdate,
} from '@/api/systemFormConfig';
import { wechatAsyncApi, routineAsyncApi } from '@/api/wxApi';
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'notification' });

const { proxy } = getCurrentInstance();

const modalTitle = ref('');
const notificationModal = ref(false);
const headerList = [
  { label: '通知会员', value: '1' },
  { label: '通知平台', value: '2' },
];
const id = ref(0);
const levelLists = ref([]);
const currentTab = ref('1');
const loading = ref(false);
const formData = ref({});
const industry = ref(null);
const loadingList = ref(false);
const centerDialogVisible = ref(false);
const infoList = ref([]);
const infoList1 = [{ label: '短信', value: 'sms' }];
const form = reactive({
  content: '',
  name: '',
  id: '',
  status: null,
  tempId: '',
  tempKey: '',
  title: '',
});
const detailType = ref('');
const infoTab = ref('');

const tableRef = ref(null);
const formRef = ref(null);

function changeTab(data) {
  currentTab.value = data.paneName;
  getNotificationList(data.paneName);
}
//获取消息列表
function getNotificationList(id) {
  loadingList.value = true;
  notificationListApi({ sendType: id })
    .then((res) => {
      loadingList.value = false;
      levelLists.value = res;
    })
    .catch((res) => {
      loadingList.value = false;
    });
}
//公众号消息开关
function changeWechat(row) {
  notificationWechat(row.id).then((res) => {
    proxy.$modal.msgSuccess('修改成功');
  });
}
//小程序消息开关
function changeRoutine(row) {
  notificationRoutine(row.id).then((res) => {
    proxy.$modal.msgSuccess('修改成功');
  });
}
//短信消息开关
function changeSms(row) {
  notificationSms(row.id).then((res) => {
    proxy.$modal.msgSuccess('修改成功');
  });
}
//详情tab切换
function changeInfo(data) {
  getNotificationDetail({ name: data.paneName });
}
//详情数据
function getNotificationDetail(param) {
  loading.value = true;
  let data = {
    id: id.value,
    type: param.name,
  };
  detailType.value = data.type;
  notificationDetail(data)
    .then((res) => {
      Object.assign(form, res);
      form.status = res.status.toString();
      loading.value = false;
    })
    .catch((res) => {
      loading.value = false;
    });
}
// 设置
function setting(row) {
  infoList.value = [];
  id.value = row.id;
  centerDialogVisible.value = true;
  if (row.isWechat !== 0) {
    infoList.value.push({ label: '公众号模板消息', value: 'wechat' });
  }
  if (row.isRoutine !== 0) {
    infoList.value.push({ label: '小程序订阅消息', value: 'routine' });
  }
  if (row.isSms !== 0) {
    infoList.value.push({ label: '短信', value: 'sms' });
  }
  infoTab.value = infoList.value[0].value;
  getNotificationDetail({ name: infoTab.value });
}
//修改通知
const submit = Debounce(function () {
  let data = {
    id: id.value,
    status: Number(form.status),
    tempId: form.tempId,
    type: detailType.value,
  };
  notificationUpdate(data).then((res) => {
    proxy.$modal.msgSuccess('修改成功');
    centerDialogVisible.value = false;
    getNotificationList(currentTab.value);
  });
});
function syncWechat() {
  wechatAsyncApi().then((res) => {
    ElMessage.success('同步成功');
  });
}
function syncRoutine() {
  routineAsyncApi().then((res) => {
    ElMessage.success('同步成功');
  });
}

// created
getNotificationList(Number(currentTab.value));
</script>

<style scoped>
.mt-1 {
  margin-top: 6px;
}
.description {
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
.iconfont {
  color: #06c05f;
}
</style>
