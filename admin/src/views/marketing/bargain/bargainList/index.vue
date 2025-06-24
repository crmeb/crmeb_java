<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form inline size="small" label-width="75px">
          <el-form-item label="时间选择：">
            <el-date-picker
              v-model="timeVal"
              value-format="yyyy-MM-dd"
              format="yyyy-MM-dd"
              size="small"
              type="daterange"
              placement="bottom-end"
              placeholder="自定义时间"
              style="width: 260px"
              @change="onchangeTime"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
            />
          </el-form-item>
          <el-form-item label="砍价状态：">
            <el-select
              v-model="tableFrom.status"
              placeholder="请选择"
              class="filter-item selWidth mr20"
              @change="getList(1)"
              clearable
            >
              <el-option label="进行中" :value="1" />
              <el-option label="未完成" :value="2" />
              <el-option label="已成功" :value="3" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" size="mini" ref="multipleTable">
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column label="头像" min-width="80">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.avatar"
                :preview-src-list="[scope.row.avatar]"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="发起用户" prop="nickname" min-width="100" />
        <el-table-column label="开启时间" prop="addTime" min-width="150" />
        <el-table-column label="砍价商品" prop="title" min-width="300">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="right" :open-delay="800">
              <div class="text_overflow" slot="reference">{{ scope.row.title }}</div>
              <div class="pup_card">{{ scope.row.title }}</div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="最低价" prop="bargainPriceMin" min-width="100" />
        <el-table-column label="当前价" prop="nowPrice" min-width="100" />
        <el-table-column label="总砍价次数" prop="peopleNum" min-width="100" />
        <el-table-column label="剩余砍价次数" min-width="100" prop="num" />
        <el-table-column prop="dataTime" label="结束时间" min-width="150" />
        <el-table-column label="砍价状态" min-width="150">
          <template slot-scope="scope">
            <span
              class="tag-background tag-padding"
              :class="scope.row.status == 1 ? 'doingTag' : scope.row.status == 2 ? 'notStartTag' : 'endTag'"
              >{{ scope.row.status | bargainStatusFilter }}</span
            >
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <a @click="handleLook(scope.row.id)" class="mr10">查看详情</a>
          </template>
        </el-table-column>
      </el-table>
      <div class="block mb20">
        <el-pagination
          :page-sizes="[10, 20, 30, 40]"
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
    <el-dialog title="查看详情" :visible.sync="dialogVisible" width="650px" :before-close="handleClose">
      <el-table
        v-loading="listLoadingPink"
        :data="tableDataPink.data"
        style="width: 100%"
        size="mini"
        ref="multipleTable"
      >
        <el-table-column prop="uid" label="用户id" min-width="50" />
        <el-table-column label="用户头像" min-width="80">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.avatar"
                :preview-src-list="[scope.row.avatar]"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="用户名称" prop="nickname" min-width="100" />
        <el-table-column label="砍价金额" prop="price" min-width="100" />
        <el-table-column label="砍价时间" prop="addTime" min-width="180" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { bargainListListApi, bargainOrderPinkApi } from '@/api/marketing';
export default {
  name: 'index',
  data() {
    return {
      listLoadingPink: false,
      dialogVisible: false,
      tableDataPink: {
        data: [],
      },
      tableData: {
        data: [],
        total: 0,
      },
      listLoading: false,
      tableFrom: {
        dateLimit: '',
        status: '',
        page: 1,
        limit: 20,
      },
      fromList: this.$constants.fromList,
      timeVal: [],
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    handleClose() {
      this.dialogVisible = false;
    },
    handleLook(id) {
      this.dialogVisible = true;
      this.getPink(id);
    },
    getPink(id) {
      this.listLoadingPink = true;
      bargainOrderPinkApi(id)
        .then((res) => {
          this.tableDataPink.data = res;
          this.listLoadingPink = false;
        })
        .catch(() => {
          this.listLoadingPink = false;
        });
    },
    selectChange(tab) {
      this.tableFrom.dateLimit = tab;
      this.tableFrom.page = 1;
      this.timeVal = [];
      this.getList();
    },
    // 具体日期
    onchangeTime(e) {
      this.timeVal = e;
      this.tableFrom.dateLimit = e ? this.timeVal.join(',') : '';
      this.tableFrom.page = 1;
      this.getList();
    },
    // 列表
    getList(num) {
      this.listLoading = true;
      this.tableFrom.page = num ? num : this.tableFrom.page;
      bargainListListApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          this.listLoading = false;
        })
        .catch(() => {
          this.listLoading = false;
        });
    },
    pageChange(page) {
      this.tableFrom.page = page;
      this.getList();
    },
    handleSizeChange(val) {
      this.tableFrom.limit = val;
      this.getList();
    },
  },
};
</script>

<style scoped lang="scss">
.el-table__body {
  width: 100%;
  table-layout: fixed !important;
}
.text_overflow {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 400px;
}
.pup_card {
  width: 200px;
  border-radius: 5px;
  padding: 5px;
  box-sizing: border-box;
  font-size: 12px;
  line-height: 16px;
}
::v-deep .el-dialog__body {
  padding-bottom: 20px;
}
</style>
