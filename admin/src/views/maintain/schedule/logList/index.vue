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

<script>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import * as schedule from '@/api/schedule.js';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { Debounce } from '@/utils/validate';
export default {
  name: 'CompanyList',
  data() {
    return {
      tableData: {
        data: [],
        total: 0,
      },
      loading: false,
      dialogVisible: false,
      editId: 0,
      tableFrom: {
        page: 1,
        limit: 20,
        total: 0,
      },
    };
  },
  created() {
    if (checkPermi(['admin:schedule:job:log:list'])) this.getjobLogList();
  },
  methods: {
    checkPermi,
    //  获取定时任务日志分页列表
    getjobLogList() {
      this.loading = true;
      schedule
        .jobLogList({
          page: this.tableFrom.page,
          limit: this.tableFrom.limit,
        })
        .then((res) => {
          this.loading = false;
          this.tableData = res;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    // 分页
    pageChange(e) {
      this.tableFrom.page = e;
      this.getjobLogList();
    },
    handleSizeChange(e) {
      this.tableFrom.limit = e;
      this.getjobLogList();
    },
  },
};
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
