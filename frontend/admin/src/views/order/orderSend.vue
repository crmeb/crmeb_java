<template>
  <el-dialog
    :close-on-click-modal="false"
    v-model="modals"
    title="发送货"
    class="order_box"
    :before-close="handleClose"
    width="900px"
  >
    <el-form
      ref="formItemRef"
      v-loading="loading"
      :model="formItem"
      label-width="130px"
      @submit.prevent
      :rules="rules"
    >
      <el-form-item label="选择类型：">
        <el-radio-group
          v-model="formItem.deliveryType"
          :disabled="isEdit"
          @change="changeRadioType(formItem.deliveryType)"
          required
        >
          <el-radio label="express" value="express">发货</el-radio>
          <el-radio label="send" value="send">送货</el-radio>
          <el-radio label="fictitious" value="fictitious">虚拟</el-radio>
        </el-radio-group>
      </el-form-item>
      <!--发货-->
      <div v-if="formItem.deliveryType === 'express'">
        <el-form-item label="发货类型：">
          <el-radio-group
            :disabled="isEdit"
            v-model="formItem.expressRecordType"
            @change="changeRadio(formItem.expressRecordType)"
          >
            <el-radio label="1" value="1">手动填写</el-radio>
            <el-radio label="2" value="2" v-if="checkPermi(['admin:order:sheet:info'])">电子面单打印</el-radio>
            <el-radio label="3" value="3">商家寄件</el-radio>
          </el-radio-group>
        </el-form-item>
        <!--商家寄件-->
        <template v-if="formItem.expressRecordType === '3'">
          <el-form-item label="寄件人姓名：" prop="shipment.sendRealName">
            <el-input v-model="formItem.shipment.sendRealName" placeholder="请输入寄件人姓名"></el-input>
          </el-form-item>
          <el-form-item label="寄件人电话：" prop="shipment.sendPhone">
            <el-input v-model="formItem.shipment.sendPhone" placeholder="请输入寄件人电话"></el-input>
          </el-form-item>
          <el-form-item label="寄件人地址：" prop="shipment.sendAddress">
            <el-input v-model="formItem.shipment.sendAddress" placeholder="请输入寄件人地址"></el-input>
          </el-form-item>
          <el-form-item label="快递公司：" prop="shipment.kuaidicom">
            <el-select
              value-key="id"
              v-model="formItem.shipment.kuaidicom"
              filterable
              @change="onChangeShipmentExpress"
              style="width: 100%"
            >
              <el-option
                v-for="(item, i) in shipmentExpress"
                :value="item.value"
                :key="item.id"
                :label="item.label"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="快递业务类型：" prop="shipment.serviceType">
            <el-select
              v-model="formItem.shipment.serviceType"
              filterable
              placeholder="请选择业务类型"
              style="width: 100%"
            >
              <el-option v-for="item in serviceTypeList" :value="item" :key="item">{{ item }}</el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="电子面单：" class="express_temp_id" prop="shipment.tempid">
            <div class="acea-row">
              <el-select
                v-model="formItem.shipment.tempid"
                placeholder="请选择电子面单"
                :class="[formItem.shipment.tempid ? 'width9' : 'width8']"
                @change="onChangeExpressTempId"
              >
                <el-option
                  v-for="(item, i) in expressTempIdList"
                  :value="item.temp_id"
                  :key="i"
                  :label="item.temp_id"
                ></el-option>
              </el-select>
              <div v-if="formItem.shipment.tempid" style="position: relative">
                <div class="tempImgList ml10">
                  <div class="demo-image__preview">
                    <el-image
                      style="width: 36px; height: 36px"
                      :src="expressTempIdImg"
                      :preview-src-list="[expressTempIdImg]" preview-teleported
                    />
                  </div>
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="取件日期：">
            <el-radio-group v-model="formItem.shipment.dayType" type="button">
              <el-radio :label="0" :value="0">今天</el-radio>
              <el-radio :label="1" :value="1">明天</el-radio>
              <el-radio :label="2" :value="2">后天</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="取件时间：">
            <el-time-picker
              is-range
              format="HH:mm"
              v-model="pickupTime"
              value-format="HH:mm"
              range-separator="-"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              placeholder="选择时间范围"
              @change="onchangeTime"
            />
          </el-form-item>
        </template>
        <!--手动填写、电子面单打印-->
        <template v-else>
          <el-form-item v-model="nowCompany" label="快递公司：" prop="company">
            <el-select
              style="width: 100%"
              value-key="code"
              v-model="formItem.company"
              filterable
              @change="onChangeExport"
            >
              <el-option v-for="(item, i) in express" :value="item" :key="item.code" :label="item.name"></el-option>
            </el-select>
          </el-form-item>
        </template>
        <!--手动填写-->
        <el-form-item v-if="formItem.expressRecordType === '1'" label="快递单号：" prop="expressNumber">
          <el-input v-model="formItem.expressNumber" placeholder="请输入快递单号"></el-input>
        </el-form-item>
        <!--电子面单打印-->
        <template v-if="formItem.expressRecordType === '2'">
          <el-form-item label="电子面单：" class="express_temp_id" prop="expressTempId">
            <div class="acea-row">
              <el-select
                v-model="formItem.expressTempId"
                placeholder="请选择电子面单"
                :class="[formItem.expressTempId ? 'width9' : 'width8']"
                @change="onChangeImg"
              >
                <el-option
                  v-for="(item, i) in exportTempList"
                  :value="item.temp_id"
                  :key="i"
                  :label="item.title"
                ></el-option>
              </el-select>
              <div v-if="formItem.expressTempId" style="position: relative">
                <div class="tempImgList ml10">
                  <div class="demo-image__preview">
                    <el-image style="width: 36px; height: 36px" :src="tempImg" :preview-src-list="[tempImg]" preview-teleported />
                  </div>
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="寄件人姓名：" prop="toName">
            <el-input v-model="formItem.toName" placeholder="请输入寄件人姓名"></el-input>
          </el-form-item>
          <el-form-item label="寄件人电话：" prop="toTel">
            <el-input v-model="formItem.toTel" placeholder="请输入寄件人电话"></el-input>
          </el-form-item>
          <el-form-item label="寄件人地址：" prop="toAddr">
            <el-input v-model="formItem.toAddr" placeholder="请输入寄件人地址"></el-input>
          </el-form-item>
        </template>
      </div>
      <!--送货-->
      <div v-if="formItem.deliveryType === 'send'">
        <el-form-item label="送货人姓名：" prop="deliveryName">
          <el-input v-model="formItem.deliveryName" placeholder="请输入送货人姓名"></el-input>
        </el-form-item>
        <el-form-item label="送货人电话：" prop="deliveryTel">
          <el-input v-model="formItem.deliveryTel" placeholder="请输入送货人电话"></el-input>
        </el-form-item>
      </div>
      <!-- <div>
        <el-form-item label="">
          <div style="color: #cecece">顺丰请输入单号：收件人或寄件人手机号后四位</div>
          <div style="color: #cecece">例如：SF000000000000:3941</div>
        </el-form-item>
      </div> -->
    </el-form>
    <template #footer>
      <div>
        <el-button @click="cancel('formItem')">取消</el-button>
        <el-button type="primary" @click="putSend('formItem')">提交</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="jsx">
import { ref, reactive, watch, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { orderSendApi, sheetInfoApi, updateTrackingNumberApi } from '@/api/order';
import { expressAllApi, exportTempApi, shipmentExpressApi } from '@/api/sms';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { Debounce } from '@/utils/validate';
//修改引入打印扩展
import printJS from 'print-js';

const validatePhone = (rule, value, callback) => {
  if (!value) {
    return callback(new Error('请填写手机号'));
  } else if (!/^1[3456789]\d{9}$/.test(value)) {
    callback(new Error('手机号格式不正确!'));
  } else {
    callback();
  }
};

defineOptions({ name: 'orderSend' });

const props = defineProps({
  orderId: {
    type: String,
    default: '',
  },
  expressListNormal: {
    type: Array,
    default: () => [],
  },
  expressListElec: {
    type: Array,
    default: () => [],
  },
  orderDetail: {
    type: Object,
    default: null,
  },
});

const emit = defineEmits(['submitFail']);

const formItemRef = ref(null);

const formItem = reactive({
  deliveryType: 'express',
  expressRecordType: '1',
  expressCode: '',
  company: '', //快递公司
  deliveryName: '',
  deliveryTel: '',
  expressName: '',
  expressNumber: '',
  expressTempId: '',
  toAddr: '',
  toName: '',
  toTel: '',
  orderNo: '',
  shipment: {
    sendRealName: '',
    sendPhone: '',
    sendAddress: '',
    kuaidicom: '', //快递公司编码
    serviceType: '', //快递业务类型
    pickupStartTime: '', // 取件开始时间
    pickupEndTime: '', // 取件结束时间
    tempid: '', //电子面单模板id
    dayType: 1, //时间
  },
});
const modals = ref(false);
const exportTempList = ref([]);
const tempImg = ref('');
const rules = {
  'shipment.sendRealName': [{ required: true, message: '请输入寄件人姓名', trigger: 'blur' }],
  'shipment.sendPhone': [{ required: true, validator: validatePhone, trigger: 'blur' }],
  'shipment.sendAddress': [{ required: true, message: '请输入寄件人地址', trigger: 'blur' }],
  'shipment.kuaidicom': [{ required: true, message: '请选择快递公司', trigger: 'change' }],
  'shipment.serviceType': [{ required: true, message: '请选择业务类型', trigger: 'change' }],
  'shipment.tempid': [{ required: true, message: '请选择电子面单模板', trigger: 'change' }],
  toName: [{ required: true, message: '请输寄件人姓名', trigger: 'blur' }],
  toTel: [{ required: true, validator: validatePhone, trigger: 'blur' }],
  toAddr: [{ required: true, message: '请输入寄件人地址', trigger: 'blur' }],
  company: [{ required: true, message: '请选择快递公司', trigger: 'change' }],
  expressNumber: [{ required: true, message: '请输入快递单号', trigger: 'blur' }],
  expressTempId: [{ required: true, message: '请选择电子面单', trigger: 'change' }],
  deliveryName: [{ required: true, message: '请输入送货人姓名', trigger: 'blur' }],
  deliveryTel: [{ required: true, validator: validatePhone, trigger: 'blur' }],
};
const expressType = ref('normal');
const loading = ref(false);
const express = ref([]); //物流公司
const isEdit = ref(false); //是否是编辑
const shipmentExpress = ref([]); //一号通物流公司
const serviceTypeList = ref([]); //业务类型
const expressTempIdList = ref([]); //商家发货电子面单
const expressTempIdImg = ref(''); // 商家发货电子面单图片
const pickupTime = ref(['', '']); // 取件时间
const nowCompany = ref('');

watch(modals, (val) => {
  if (val && formItem.expressRecordType === '3') {
    if (checkPermi(['admin:pass:shipment:express']) && !shipmentExpress.value.length) {
      getShipmentExpress();
    }
  }
});

watch(
  () => props.orderDetail,
  (val) => {
    if (val) {
      loading.value = true;
      isEdit.value = true;
      getExpressDetail(val);
      if (val.expressRecordType === '3' && checkPermi(['admin:pass:shipment:express']) && !shipmentExpress.value.length) {
        getShipmentExpress();
      }
    } else {
      isEdit.value = false;
      loading.value = false;
    }
  },
  { immediate: false, deep: true }
);

//一号通 商家寄件 快递列表
function getShipmentExpress() {
  shipmentExpressApi().then((res) => {
    shipmentExpress.value = res.data;
  });
}
// 取件时间选择
function onchangeTime(e) {
  formItem.shipment.pickupStartTime = e[0];
  formItem.shipment.pickupEndTime = e[1];
}
//商家寄件选择快递公司获取电子面单列表
function onChangeShipmentExpress(value) {
  formItem.shipment.serviceType = '';
  let expressItem = shipmentExpress.value.find((item) => {
    return item.value === value;
  });
  if (expressItem === undefined) {
    return;
  }
  serviceTypeList.value = expressItem.types; //业务类型
  expressTempIdList.value = expressItem.list; //商家发货电子面单
}
//选择电子面单
function onChangeExpressTempId(item) {
  expressTempIdList.value.map((i) => {
    if (i.temp_id === item) expressTempIdImg.value = i.pic;
  });
}
//物流信息详情, 快递单号，快递公司，快递公司code
function getExpressDetail(val) {
  if (val.deliveryType === 'send') {
    formItem.deliveryTel = val.deliveryId;
    formItem.deliveryName = val.deliveryName;
  } else {
    formItem.expressName = val.deliveryName;
    formItem.expressNumber = val.deliveryId;
  }
  formItem.deliveryType = val.deliveryType;
  formItem.expressCode = val.deliveryCode;
  formItem.expressRecordType = val.expressRecordType;
  formItem.company = { code: val.deliveryCode, name: val.deliveryName };
  loading.value = false;
}
// 默认信息
function sheetInfo() {
  sheetInfoApi().then(async (res) => {
    formItem.toAddr = res.exportToAddress || '';
    formItem.toName = res.exportToName || '';
    formItem.toTel = res.exportToTel || '';
  });
}
// 快递公司选择
function onChangeExport(val) {
  formItem.expressCode = val.code;
  formItem.expressName = val.name;
  formItem.expressTempId = '';
  if (formItem.expressRecordType === '2') exportTemp(val.code);
}
// 电子面单模板
function exportTemp(code) {
  exportTempApi({ com: code }).then(async (res) => {
    exportTempList.value = res.data.data || [];
  });
}
function onChangeImg(item) {
  exportTempList.value.map((i) => {
    if (i.temp_id === item) tempImg.value = i.pic;
  });
}
//选择类型
function changeRadioType() {
  if (formItem.deliveryType === 'fictitious') {
    formItem.expressId = '';
    formItem.expressCode = '';
  }
}
//选择发货类型
function changeRadio(o) {
  if (o === '3') {
    if (checkPermi(['admin:pass:shipment:express']) && !shipmentExpress.value.length) {
      getShipmentExpress();
    }
    return;
  }
  if (o === '2') {
    express.value = props.expressListElec;
  } else {
    express.value = props.expressListNormal;
  }
  //其他数据置空
  formItem.shipment = {
    sendRealName: '',
    sendPhone: '',
    sendAddress: '',
    kuaidicom: '',
    serviceType: '',
    pickupStartTime: '',
    pickupEndTime: '',
    tempid: '',
    dayType: 1,
  };
  formItem.deliveryName = '';
  formItem.expressCode = '';
  formItem.expressName = '';
  formItem.expressTempId = '';
  formItem.expressNumber = '';
}
// 提交
const putSend = Debounce(function (name) {
  // 打印测试
  //this.printImg("http://api.kuaidi100.com/label/getImage/20230505/FBA3DFCE5C684CB9A13DADA8EE8357FB");
  // 正常业务中使用;
  formItem.orderNo = props.orderId;
  formItemRef.value.validate((valid) => {
    if (valid) {
      !isEdit.value
        ? orderSendApi(formItem).then((data) => {
            // data -》 label是一个网络图片地址，直接打印即可
            if (formItem.expressRecordType === '2') printImg(data.label);
            ElMessage.success('发送货成功');
            modals.value = false;
            formItemRef.value.resetFields();
            emit('submitFail');
          })
        : updateTrackingNumberApi(formItem).then((data) => {
            ElMessage.success('修改快递单号成功');
            modals.value = false;
            formItemRef.value.resetFields();
            emit('submitFail');
          });
    } else {
      ElMessage.error('请填写信息');
    }
  });
});
function handleClose() {
  cancel('formItem');
}
function cancel(name) {
  modals.value = false;
  formItemRef.value.resetFields();
  formItem.deliveryType = 'express';
  formItem.expressRecordType = '1';
}
//修改增加打印方法
function printImg(url) {
  printJS({
    printable: url,
    type: 'image',
    documentTitle: '快递信息',
    style: `img{
      width: 100%;
      height: 476px;
    }`,
  });
}

onMounted(() => {
  express.value = props.expressListNormal;
});

defineExpose({
  modals,
  loading,
  express,
  sheetInfo,
});
</script>

<style scoped lang="scss">
.width8 {
  width: 100%;
}

.width9 {
  width: 95%;
}

.tempImgList {
  // opacity: 1;
  width: 38px !important;
  height: 30px !important;
  // margin-top: -30px;
  cursor: pointer;
  position: absolute;
  z-index: 11;

  img {
    width: 38px !important;
    height: 30px !important;
  }
}
</style>
