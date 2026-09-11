<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form inline label-width="75px">
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
          <el-form-item label="用户搜索：">
            <UserSearchInput ref="userSearchInput" v-model="tableFrom" />
          </el-form-item>
          <!-- <el-form-item label="微信昵称：">
            <el-input
              v-model="tableFrom.keywords"
              placeholder="请输入用户昵称"
              class="selWidth"

            ></el-input>
          </el-form-item> -->
          <el-form-item>
            <el-button type="primary" @click="getList(1)">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <el-table v-loading="listLoading" :data="tableData.data" class="table" highlight-current-row>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="130" />
        <el-table-column
          sortable
          prop="balance"
          label="积分余量"
          min-width="120"
          :sort-method="
            (a, b) => {
              return a.balance - b.balance;
            }
          "
        />
        <el-table-column
          sortable
          label="明细数字"
          min-width="120"
          prop="integral"
          :sort-method="
            (a, b) => {
              return a.integral - b.integral;
            }
          "
        />
        <el-table-column label="备注" min-width="120" prop="mark" />
        <el-table-column label="用户昵称" min-width="120" prop="nickName" />
        <el-table-column prop="updateTime" label="	添加时间" width="170" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { integralListApi } from '@/api/marketing';
import cardsData from '@/components/cards/index';

const { proxy } = getCurrentInstance();

const loading = ref(false);
const options = ref([]);
const fromList = proxy.$constants.fromList;
const listLoading = ref(false);
const tableData = reactive({
  data: [],
  total: 0,
});
const tableFrom = reactive({
  page: 1,
  limit: 20,
  dateLimit: '',
  content: '',
  searchType: 'all',
});
const userIdList = ref([]);
const userList = ref([]);
const timeVal = ref([]);
const values = ref([]);
const userSearchInput = ref(null);

//重置
const handleReset = () => {
  timeVal.value = [];
  tableFrom.dateLimit = '';
  tableFrom.keywords = '';
  tableFrom.content = '';
  tableFrom.searchType = 'all';
  getList();
};
const seachList = () => {
  tableFrom.page = 1;
  getList();
};
// 选择时间
const selectChange = (tab) => {
  tableFrom.dateLimit = tab;
  tableFrom.page = 1;
  timeVal.value = [];
  getList();
};
// 具体日期
const onchangeTime = (e) => {
  timeVal.value = e;
  tableFrom.dateLimit = e ? timeVal.value.join(',') : '';
  tableFrom.page = 1;
  getList();
};
// 列表
const getList = () => {
  listLoading.value = true;
  integralListApi(tableFrom)
    .then((res) => {
      tableData.data = res.list;
      tableData.total = res.total;
      listLoading.value = false;
    })
    .catch((res) => {
      listLoading.value = false;
    });
};
const pageChange = (page) => {
  tableFrom.page = page;
  getList();
};
const handleSizeChange = (val) => {
  tableFrom.limit = val;
  getList();
};

onMounted(() => {
  getList();
  // this.getUserList()
});
</script>

<style lang="sass" scoped></style>
