<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-button type="primary" size="small" @click="handleAddJob" v-hasPermi="['admin:schedule:job:add']"
          >添加定时任务</el-button
        >
      </div>
      <el-table v-loading="listLoading" :data="tableData" size="mini" class="table">
        <el-table-column prop="jobId" label="任务id" min-width="60" />
        <el-table-column prop="beanName" label="定时任务类名" min-width="150" />
        <el-table-column prop="methodName" label="方法名" min-width="150" />
        <el-table-column prop="cronExpression" min-width="120" label="cron表达式" />
        <el-table-column prop="params" label="参数" min-width="100" />
        <el-table-column label="状态" min-width="80">
          <template slot-scope="scope">
            <el-switch
              v-if="checkPermi(['admin:schedule:job:start', 'admin:schedule:job:suspend'])"
              v-model="scope.row.status"
              :active-value="0"
              :inactive-value="1"
              active-text="正常"
              inactive-text="暂停"
              @change="onchangeIsShow(scope.row)"
            />
            <span v-else>{{ scope.row.status == 0 ? '正常' : '暂停' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column prop="createTime" label="创建时间" min-width="120" />
        <el-table-column fixed="right" width="150" label="操作">
          <template slot-scope="scope">
            <el-button
              class="list-btn"
              :disabled="scope.row.status == 0"
              type="text"
              size="small"
              @click="onEdit(scope.row)"
              v-hasPermi="['admin:schedule:job:update']"
              >编辑</el-button
            >
            <el-divider direction="vertical"></el-divider>
            <el-button
              class="list-btn"
              type="text"
              size="small"
              @click="onTrig(scope.row)"
              v-hasPermi="['admin:schedule:job:trig']"
              >触发</el-button
            >
            <el-divider direction="vertical"></el-divider>
            <el-button
              class="list-btn"
              :disabled="scope.row.status == 0"
              type="text"
              size="small"
              @click="handleDelete(scope.row.jobId, scope.$index)"
              v-hasPermi="['admin:schedule:job:delete']"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <creat-Job
      ref="creatJobs"
      :editData="editData"
      :dialogVisible="dialogVisible"
      @getList="getjobList"
      @closeModel="closeModel"
    />
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
import creatJob from './creatJob';
import { Debounce } from '@/utils/validate';
import { checkPermi } from '@/utils/permission';
import { param } from '@/utils';
export default {
  name: 'list',
  components: { creatJob },
  data() {
    return {
      tableData: [],
      listLoading: false,
      dialogVisible: false,
      status: 0,
      jobId: 0,
      editData: {
        jobId: 0,
        beanName: '',
        cronExpression: '',
        methodName: '',
        remark: '',
        param: '',
      },
    };
  },
  created() {
    if (checkPermi(['admin:schedule:job:list'])) this.getjobList();
  },
  methods: {
    checkPermi, //权限控制
    // 获取定时任务列表
    getjobList() {
      this.listLoading = true;
      schedule
        .jobList()
        .then((data) => {
          this.listLoading = false;
          this.tableData = data;
        })
        .catch(() => {
          this.listLoading = false;
        });
    },
    submit: Debounce(function (data) {}),
    //  关闭模态框
    handleClose(done) {
      this.formConf.fields = [];
      this.dialogVisible = false;
    },
    onchangeIsShow(row) {
      if (row.status == 1) {
        schedule
          .scheduleJobSuspend(row.jobId)
          .then(() => {
            this.$message.success('修改成功');
            this.getjobList();
          })
          .catch(() => {
            row.status = !row.status;
          });
      } else {
        schedule
          .scheduleJobStart(row.jobId)
          .then(() => {
            this.$message.success('修改成功');
            this.getjobList();
          })
          .catch(() => {
            row.status = !row.status;
          });
      }
    },
    // 添加定时任务
    handleAddJob() {
      this.editData = {};
      this.dialogVisible = true;
    },
    // 修改定时任务
    onEdit(row) {
      this.editData = row;
      this.dialogVisible = true;
    },
    onTrig(row) {
      schedule.scheduleJobTrig(row.jobId).then((res) => {
        this.$message.success('触发成功');
        this.getjobList();
      });
    },
    closeModel() {
      this.dialogVisible = false;
    },
    // 删除定时任务
    handleDelete(id, idx) {
      this.$modalSure().then(() => {
        schedule.scheduleJobDelete(id).then((res) => {
          this.$message.success('删除成功');
          this.getjobList();
        });
      });
    },
  },
};
</script>

<style lang="scss" scoped>
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
.list-btn {
  padding: 0 !important;
}
</style>
