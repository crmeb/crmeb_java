<template>
  <div class="divBox relative">
    <el-card class="box-card" shadow="never" :bordered="false">
      <div class="clearfix">
        <el-button type="primary" size="small" v-hasPermi="['admin:product:guarantee:add']" @click="handlerOpenEdit(0)"
          >添加保障服务</el-button
        >
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        class="mt20"
        size="small"
        :highlight-current-row="true"
      >
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column prop="name" label="服务条款" min-width="150" />
        <el-table-column label="服务条款图标" min-width="120">
          <template slot-scope="scope">
            <div class="demo-image__preview line-heightOne">
              <el-image :src="scope.row.icon" :preview-src-list="[scope.row.icon]" />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="服务内容描述" min-width="250" :show-overflow-tooltip="true" />
        <el-table-column prop="sort" label="排序" min-width="80" />
        <el-table-column label="创建时间" min-width="150">
          <template slot-scope="scope">
            <span>{{ scope.row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="是否显示" min-width="90" fixed="right">
          <template slot-scope="scope">
            <el-switch
              v-if="checkPermi(['admin:product:guarantee:show:status'])"
              v-model="scope.row.isShow"
              :active-value="true"
              :inactive-value="false"
              active-text="显示"
              inactive-text="隐藏"
              @click.native="onchangeIsShow(scope.row)"
            />
            <div v-else>{{ scope.row.isShow ? '显示' : '隐藏' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template slot-scope="scope">
            <a @click="handlerOpenEdit(1, scope.row)" v-hasPermi="['admin:product:guarantee:update']">编辑</a>
            <el-divider direction="vertical"></el-divider>
            <a @click="handlerOpenDel(scope.row)" v-hasPermi="['admin:product:guarantee:delete']">删除</a>
          </template>
        </el-table-column>
      </el-table>
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
import {
  guaranteeListApi,
  guaranteeAddApi,
  guaranteeDelApi,
  guaranteeUpdateApi,
  guaranteeUpdateShowApi,
} from '@/api/store';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  data() {
    return {
      tableData: {
        data: [],
        total: 0,
      },
      listLoading: false,
      keyNum: 0,
      id: 0,
    };
  },
  mounted() {
    if (checkPermi(['admin:product:guarantee:list'])) this.getList();
  },
  methods: {
    checkPermi,
    // 列表
    getList() {
      this.listLoading = true;
      guaranteeListApi()
        .then((res) => {
          this.tableData.data = res;
          this.listLoading = false;
        })
        .catch((err) => {
          this.listLoading = false;
          this.$message.error(err.message);
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
    handlerOpenEdit(isCreate, editDate) {
      const _this = this;
      this.id = editDate ? editDate.id : 0;
      this.$modalParserFrom(
        isCreate === 0 ? '添加服务条款' : '编辑服务条款',
        '保障服务',
        isCreate,
        isCreate === 0 ? {} : Object.assign({}, editDate),
        function (formValue) {
          _this.submit(formValue);
        },
        (this.keyNum += 4),
      );
    },
    submit(formValue) {
      const data = {
        id: this.id,
        name: formValue.name,
        content: formValue.content,
        icon: formValue.icon,
        sort: formValue.sort,
        isShow: formValue.isShow,
      };
      !this.id
        ? guaranteeAddApi(data)
            .then((res) => {
              this.$message.success('操作成功');
              this.$msgbox.close();
              this.getList();
            })
            .catch(() => {
              this.loading = false;
            })
        : guaranteeUpdateApi(data)
            .then((res) => {
              this.$message.success('操作成功');
              this.$msgbox.close();
              this.getList();
            })
            .catch(() => {
              this.loading = false;
            });
    },
    handlerOpenDel(rowData) {
      this.$modalSure('删除当前保障服务吗').then(() => {
        guaranteeDelApi(rowData.id).then((data) => {
          this.$message.success('删除成功');
          this.getList();
        });
      });
    },
    onchangeIsShow(row) {
      guaranteeUpdateShowApi(row.id).then(() => {
        this.$message.success('操作成功');
        this.getList();
      });
    },
  },
};
</script>
<style>
.el-message-box__headerbtn {
  right: 24px;
}
.upload-form {
  padding-bottom: 0 !important;
}
</style>
