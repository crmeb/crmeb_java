<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-table v-loading="loading" :data="tableData.list">
        <el-table-column prop="jobId" label="任务id" min-width="60" />
        <el-table-column prop="logId" label="任务日志id" min-width="80" />
        <el-table-column label="定时任务类名" min-width="150" prop="beanName" />
        <el-table-column min-width="120" label="方法名" prop="methodName" />
        <el-table-column prop="params" label="参数" min-width="100" />
        <el-table-column prop="times" label="耗时(单位：毫秒)" min-width="100"> </el-table-column>
        <el-table-column prop="error" label="失败信息" min-width="200"> </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="120"> </el-table-column>
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
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import { ref, reactive } from 'vue';
import * as schedule from '@/api/schedule.js';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'CompanyList' });

const tableData = reactive({
  data: [],
  total: 0,
});
const loading = ref(false);
const dialogVisible = ref(false);
const editId = ref(0);
const tableFrom = reactive({
  page: 1,
  limit: 20,
  total: 0,
});

//  获取定时任务日志分页列表
function getjobLogList() {
  loading.value = true;
  schedule
    .jobLogList({
      page: tableFrom.page,
      limit: tableFrom.limit,
    })
    .then((res) => {
      loading.value = false;
      Object.assign(tableData, res);
    })
    .catch(() => {
      loading.value = false;
    });
}
// 分页
function pageChange(e) {
  tableFrom.page = e;
  getjobLogList();
}
function handleSizeChange(e) {
  tableFrom.limit = e;
  getjobLogList();
}

// created
if (checkPermi(['admin:schedule:job:log:list'])) getjobLogList();
</script>

<style lang="scss" scoped>
.selWidth {
  width: 350px;
}
.el-icon-plus {
  margin-right: 5px;
}

.demo .el-switch__label {
  position: absolute;
  display: none;
  color: #fff;
}

/*打开时文字位置设置*/
.demo .el-switch__label--right {
  z-index: 1;
}

/*关闭时文字位置设置*/
.demo .el-switch__label--left {
  z-index: 1;
  left: 19px;
}

/*显示文字*/
.demo .el-switch__label.is-active {
  display: block;
}

.demo.el-switch .el-switch__core,
.el-switch .el-switch__label {
  width: 60px !important;
}
.formBox {
  .el-input-number--medium {
    width: 100px;
  }
}
</style>
