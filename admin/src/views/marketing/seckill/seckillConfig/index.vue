<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form inline>
          <el-form-item label="秒杀状态：">
            <el-select
              v-model="tableFrom.status"
              placeholder="请选择"
              class="filter-item selWidth"
              @change="getList(1)"
              clearable
            >
              <el-option label="关闭" value="'0'" />
              <el-option label="开启" value="'1'" />
            </el-select>
          </el-form-item>
          <el-form-item label="秒杀名称：">
            <el-input v-model="tableFrom.name" placeholder="请输入秒杀名称" class="selWidth" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getList(1)">搜索</el-button>
            <el-button size="small" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <div slot="header" class="clearfix">
        <el-button type="primary" @click="add" v-hasPermi="['admin:seckill:manger:save']">添加秒杀配置</el-button>
      </div>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" size="mini" ref="multipleTable">
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column label="秒杀名称" min-width="100">
          <template slot-scope="scope">
            <router-link :to="{ path: '/marketing/seckill/list/' + scope.row.id }">
              <el-button type="text" size="small">{{ scope.row.name }}</el-button>
            </router-link>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="秒杀时段" min-width="100">
          <template slot-scope="scope">
            {{ scope.row.time.split(',').join(' - ') }}
          </template>
        </el-table-column>
        <el-table-column label="轮播图" min-width="200">
          <template slot-scope="scope">
            <div class="acea-row" v-if="scope.row.silderImgs">
              <div class="demo-image__preview mr5" v-for="item in JSON.parse(scope.row.silderImgs)" :key="item.attId">
                <el-image style="width: 36px; height: 36px" :src="item.sattDir" :preview-src-list="[item.sattDir]" />
              </div>
            </div>
            <span v-else>无</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" min-width="150">
          <template slot-scope="scope">
            <el-switch
              v-if="checkPermi(['admin:seckill:manger:update:status'])"
              v-model="scope.row.status"
              active-value="'1'"
              inactive-value="'0'"
              active-text="开启"
              inactive-text="关闭"
              @change="onchangeIsShow(scope.row)"
            />
            <span v-else>{{ scope.row.status ? '开启' : '关闭' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="130" />
        <el-table-column label="操作" width="170" fixed="right">
          <template slot-scope="scope">
            <a
              @click="handleEdit(scope.row.id)"
              v-hasPermi="['admin:seckill:manger:info', 'admin:seckill:manger:update']"
              >编辑</a
            >
            <el-divider direction="vertical"></el-divider>
            <a @click="handleDelete(scope.row.id, scope.$index)" v-hasPermi="['admin:seckill:manger:delete']">删除</a>
            <el-divider direction="vertical"></el-divider>
            <router-link :to="{ path: '/marketing/seckill/creatSeckill/creat/' + scope.row.id }">
              <a v-hasPermi="['admin:seckill:save']">添加商品</a>
            </router-link>
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

    <el-dialog
      :title="isCreate === 0 ? '添加数据' : '编辑数据'"
      :visible.sync="dialogVisible"
      width="700px"
      :before-close="handleClose"
    >
      <div v-loading="loading">
        <zb-parser
          :form-id="formId"
          :is-create="isCreate"
          :edit-data="editData"
          @submit="handlerSubmit"
          @resetForm="resetForm"
          @closeDialog="dialogVisible = false"
          v-if="dialogVisible"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script>
import zbParser from '@/components/FormGenerator/components/parser/ZBParser';
import { configSaveForm, configInfo } from '@/api/systemConfig.js';
import {
  seckillListApi,
  seckillUpdateApi,
  seckillInfoApi,
  seckillSaveApi,
  seckillDeleteApi,
  seckillConfigStatusApi,
} from '@/api/marketing';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { Debounce } from '@/utils/validate';
export default {
  name: 'SeckillConfig',
  components: { zbParser },
  data() {
    return {
      dialogVisible: false,
      isShow: true,
      isCreate: 0,
      editData: {},
      formId: 123,
      listLoading: true,
      tableData: {
        data: [],
        total: 0,
      },
      tableFrom: {
        page: 1,
        limit: 20,
        name: '',
        isDel: false,
        status: '',
      },
      seckillId: '',
      loading: false,
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    checkPermi,
    //重置
    handleReset() {
      this.tableFrom.status = '';
      this.tableFrom.name = '';
      this.getList();
    },
    resetForm(formValue) {
      this.dialogVisible = false;
    },
    // 删除
    handleDelete(id, idx) {
      this.$modalSure('永久删除该配置').then(() => {
        seckillDeleteApi({ id: id }).then(() => {
          this.$message.success('删除成功');
          if (this.tableData.data.length === 1 && this.tableFrom.page > 1)
            this.tableFrom.page = this.tableFrom.page - 1;
          this.getList();
        });
      });
    },
    onchangeIsShow(row) {
      seckillConfigStatusApi(row.id, { status: row.status })
        .then(async () => {
          this.$message.success('修改成功');
          this.getList();
        })
        .catch(() => {
          row.status = !row.status;
        });
    },
    onEditSort(row) {
      this.$set(row, 'isEdit', true);
    },
    onBlur(row) {
      this.$set(row, 'isEdit', false);
      this.onEdit(row.id, row);
    },
    // 获取表单详情
    getFormInfo(id) {
      this.loading = true;
      seckillInfoApi({ id: id })
        .then((res) => {
          this.editData = res;
          this.dialogVisible = true;
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    // 编辑
    handleEdit(id) {
      this.seckillId = id;
      this.getFormInfo(id);
      this.isCreate = 1;
    },
    // 编辑
    onEdit(id, obj) {
      const data = obj ? obj : this.editData;
      seckillUpdateApi({ id }, data)
        .then((res) => {
          this.isSuccess();
        })
        .catch((res) => {
          this.listLoading = false;
        });
    },
    // 提交
    handlerSubmit: Debounce(function (formValue) {
      if (formValue.time.split(',')[0].split(':')[0] > formValue.time.split(',')[1].split(':')[0])
        return this.$message.error('请填写正确的时间范围');
      this.isCreate === 0
        ? seckillSaveApi(formValue).then((res) => {
            this.isSuccess();
          })
        : seckillUpdateApi({ id: this.seckillId }, formValue).then((res) => {
            this.isSuccess();
          });
    }),
    isSuccess() {
      this.$message.success('操作成功');
      this.dialogVisible = false;
      this.getList();
    },
    // 列表
    getList(num) {
      this.listLoading = true;
      this.tableFrom.page = num ? num : this.tableFrom.page;
      seckillListApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          this.tableData.data.map((item) => this.$set(item, 'isEdit', false));
          this.listLoading = false;
        })
        .catch((res) => {
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
    add() {
      this.isCreate = 0;
      this.dialogVisible = true;
    },
    handleClose() {
      this.dialogVisible = false;
      this.editData = {};
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep .el-form-item__content .el-date-editor:nth-of-type(2) {
  margin-left: 8px;
}
</style>
