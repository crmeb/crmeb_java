<template>
  <div class="divBox">
    <el-card class="box-card">
      <div class="clearfix">
        <div class="container">
          <el-form inline label-width="70px">
            <el-form-item label="时间选择：">
              <optionDatePicker v-model="timeVal" @changeOptTime="onchangeTime"></optionDatePicker>
              <!-- <el-date-picker
                v-model="timeVal"
                value-format="YYYY-MM-DD"
                format="YYYY-MM-DD"

                type="daterange"
                placement="bottom-end"
                placeholder="自定义时间"
                style="width: 260px"
                @change="onchangeTime"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
              /> -->
            </el-form-item>
            <el-form-item label="提现状态：">
              <el-select class="selWidth" @change="getList(1)" v-model="tableFrom.status" placeholder="请选择">
                <el-option label="全部" value=""></el-option>
                <el-option label="审核中" value="0"></el-option>
                <el-option label="已提现" value="1"></el-option>
                <el-option label="已拒绝" value="-1"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="提现方式：">
              <el-select class="selWidth" @change="getList(1)" v-model="tableFrom.extractType" placeholder="请选择">
                <el-option label="全部" value=""></el-option>
                <el-option label="银行卡" value="bank"></el-option>
                <el-option label="支付宝" value="alipay"></el-option>
                <el-option label="微信" value="weixin"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="关键字：">
              <el-input
                v-model="tableFrom.keywords"
                placeholder="微信号/姓名/支付宝账号/银行卡号/失败原因"
                class="selWidth"

                clearable
              >
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="getList(1)">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>
    <div class="mt14">
      <cards-data :cardLists="cardLists" v-if="checkPermi(['admin:finance:apply:balance'])"></cards-data>
    </div>
    <el-card class="box-card">
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"

        class="table"
        highlight-current-row
      >
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="用户信息" min-width="180">
          <template #default="scope">
            <p>用户昵称：{{ scope.row.nickName }}</p>
            <p>用户id：{{ scope.row.uid }}</p>
          </template>
        </el-table-column>
        <el-table-column prop="extractPrice" label="提现金额" min-width="120" />
        <el-table-column label="提现方式" min-width="100">
          <template #default="scope">
            <span>{{ $filters.extractTypeFilter(scope.row.extractType) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="账号" min-width="200">
          <template #default="scope">
            <div v-if="scope.row.extractType === 'bank'">
              <p>姓名：{{ scope.row.realName }}</p>
              <p>卡号：{{ scope.row.bankCode }}</p>
              <p>开户行：{{ scope.row.bankName }}</p>
            </div>
            <span v-else-if="scope.row.extractType === 'alipay'">
              <p>姓名：{{ scope.row.realName }}</p>
              <p>支付宝号：{{ scope.row.alipayCode }}</p>
              <div class="acea-row">
                收款码：
                <div class="demo-image__preview" v-if="scope.row.qrcodeUrl">
                  <el-image :src="scope.row.qrcodeUrl" :preview-src-list="[scope.row.qrcodeUrl]" preview-teleported />
                </div>
                <div v-else>无</div>
              </div>
            </span>
            <span v-else-if="scope.row.extractType === 'weixin'">
              <p>姓名：{{ scope.row.realName }}</p>
              <p>微信号：{{ scope.row.wechat }}</p>
              <div class="acea-row">
                收款码：
                <div class="demo-image__preview" v-if="scope.row.qrcodeUrl">
                  <el-image :src="scope.row.qrcodeUrl" :preview-src-list="[scope.row.qrcodeUrl]" preview-teleported />
                </div>
                <div v-else>无</div>
              </div>
            </span>
            <span v-else>已退款</span>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" min-width="200">
          <template #default="scope">
            <div>
              <span class="spBlock">{{ $filters.extractStatusFilter(scope.row.status) }}</span>
              <span v-if="scope.row.status === -1">拒绝原因：{{ scope.row.failMsg }}</span>
            </div>
            <template v-if="scope.row.status === 0 && checkPermi(['admin:finance:apply:apply'])">
              <el-button
                type="danger"
                :icon="Close"
                v-debounceClick="
                  () => {
                    handleFail(scope.row.id);
                  }
                "
                >未通过</el-button
              >
              <el-button
                type="primary"
                :icon="Check"
                v-debounceClick="
                  () => {
                    handlePass(scope.row.id);
                  }
                "
                >通过</el-button
              >
            </template>
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="200">
          <template #default="scope">
            <span class="spBlock">{{ $filters.filterEmpty(scope.row.mark) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="150" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="scope">
            <a v-if="scope.row.status !== 1" @click="handleEdit(scope.row)" v-hasPermi="['admin:finance:apply:update']"
              >编辑</a
            >
            <span v-else>无</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[20, 40, 60, 80]"
          :page-size="tableFrom.limit"
          :current-page="tableFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
          background
        />
      </div>
    </el-card>

    <!--编辑-->
    <el-dialog title="编辑" v-model="dialogVisible" width="540px" :before-close="handleClose">
      <!--微信-->
      <zb-parser
        v-if="dialogVisible && (tableFrom.extractType === 'weixin' || extractType === 'weixin')"
        :form-id="124"
        :is-create="isCreate"
        :edit-data="editData"
        @submit="handlerSubmit"
        @resetForm="resetForm"
        @closeDialog="dialogVisible = false"
      />
      <!--支付宝-->
      <zb-parser
        v-if="dialogVisible && (tableFrom.extractType === 'alipay' || extractType === 'alipay')"
        :form-id="126"
        :is-create="isCreate"
        :edit-data="editData"
        @submit="handlerSubmit"
        @resetForm="resetForm"
        @closeDialog="dialogVisible = false"
      />
      <!--银行卡-->
      <zb-parser
        v-if="dialogVisible && (tableFrom.extractType === 'bank' || extractType === 'bank')"
        :form-id="125"
        :is-create="isCreate"
        :edit-data="editData"
        @submit="handlerSubmit"
        @resetForm="resetForm"
        @closeDialog="dialogVisible = false"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage, ElMessageBox } from '@/utils/elementPlusFeedback';
import { Close, Check } from '@element-plus/icons-vue';
import { applyListApi, applyBalanceApi, applyUpdateApi, applyStatusApi } from '@/api/financial';
import cardsData from '@/components/cards/index';
import zbParser from '@/components/FormGenerator/components/parser/ZBParser';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'AccountsExtract' });

const { proxy } = getCurrentInstance();
const constants = proxy.$constants;

const editData = ref({});
const isCreate = ref(1);
const dialogVisible = ref(false);
const timeVal = ref([]);
const tableData = reactive({
  data: [],
  total: 0,
});
const listLoading = ref(true);
const tableFrom = reactive({
  extractType: '',
  status: '',
  dateLimit: '',
  keywords: '',
  page: 1,
  limit: 20,
});
const fromList = constants.fromList;
const cardLists = ref([]);
const applyId = ref(null);
const extractType = ref('');

//重置
function handleReset() {
  tableFrom.extractType = '';
  tableFrom.status = '';
  tableFrom.dateLimit = '';
  tableFrom.keywords = '';
  timeVal.value = [];
  getList();
  getBalance();
}
function resetForm() {
  dialogVisible.value = false;
}
function handleEdit(row) {
  extractType.value = row.extractType;
  applyId.value = row.id;
  dialogVisible.value = true;
  isCreate.value = 1;
  editData.value = JSON.parse(JSON.stringify(row));
}
const handlerSubmit = Debounce(function (formValue) {
  formValue.id = applyId.value;
  formValue.extractType = extractType.value;
  applyUpdateApi(formValue).then((data) => {
    ElMessage.success('编辑成功');
    dialogVisible.value = false;
    getList();
  });
});
function handleClose() {
  dialogVisible.value = false;
  editData.value = {};
}
// 审核未通过
function handleFail(id) {
  ElMessageBox.prompt('未通过', '拒绝原因', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputErrorMessage: '请输入原因',
    inputType: 'textarea',
    inputValue: '输入信息不完整或有误!',
    inputPlaceholder: '请输入原因',
    inputValidator: (value) => {
      if (!value) {
        return '请输入原因';
      }
    },
  })
    .then(({ value }) => {
      applyStatusApi({ id: id, status: -1, backMessage: value }).then((res) => {
        ElMessage({
          type: 'success',
          message: '提交成功',
        });
        getList();
      });
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '取消输入',
      });
    });
}
// 审核通过
function handlePass(id) {
  proxy.$modalSure('审核通过吗').then(() => {
    applyStatusApi({ id: id, status: 1 }).then(() => {
      ElMessage.success('操作成功');
      getList();
    });
  });
}
// 金额
function getBalance() {
  applyBalanceApi({ dateLimit: tableFrom.dateLimit }).then((res) => {
    cardLists.value = [
      { name: '待提现金额', count: res.toBeWithdrawn, color: '#1890FF', class: 'one', icon: 'iconzhichujine1' },
      {
        name: '佣金总金额',
        count: res.commissionTotal,
        color: '#A277FF',
        class: 'two',
        icon: 'iconzhifuyongjinjine1',
      },
      { name: '已提现金额', count: res.withdrawn, color: '#EF9C20', class: 'three', icon: 'iconyingyee1' },
      { name: '未提现金额', count: res.unDrawn, color: '#1BBE6B', class: 'four', icon: 'iconyuezhifujine2' },
    ];
  });
}
// 选择时间
function selectChange(tab) {
  timeVal.value = [];
  tableFrom.dateLimit = tab;
  tableFrom.page = 1;
  getList();
  getBalance();
}
// 具体日期
function onchangeTime(e) {
  timeVal.value = e;
  tableFrom.dateLimit = e ? timeVal.value.join(',') : '';
  tableFrom.page = 1;
  getList();
  getBalance();
}
// 列表
function getList(num) {
  listLoading.value = true;
  tableFrom.page = num ? num : tableFrom.page;
  applyListApi(tableFrom)
    .then((res) => {
      tableData.data = res.list;
      tableData.total = res.total;
      listLoading.value = false;
    })
    .catch(() => {
      listLoading.value = false;
    });
}
function pageChange(page) {
  tableFrom.page = page;
  getList();
}
function handleSizeChange(val) {
  tableFrom.limit = val;
  getList();
}

onMounted(() => {
  getList();
  getBalance();
});
</script>

<style scoped>
.selWidth {
  width: 350px;
}
:deep(.dialog-footer-inner) {
  padding-top: 0 !important;
}
:deep([role='dialog'] .el-message-box__content) {
  padding: 30px 24px 20px !important;
}
</style>
