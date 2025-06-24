<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-button type="primary" @click="onAdd(null)" v-hasPermi="['admin:user:group:save', 'admin:user:tag:save']">{{
          $route.path.indexOf('group') !== -1 ? '添加用户分组' : '添加用户标签'
        }}</el-button>
      </div>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" size="small">
        <el-table-column label="ID" min-width="80" prop="id" />
        <el-table-column :label="$route.path.indexOf('group') !== -1 ? '分组名称' : '标签名称'" min-width="180">
          <template slot-scope="{ row }">
            <span v-text="$route.path.indexOf('group') !== -1 ? row.groupName : row.name"></span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="scope">
            <a @click="onAdd(scope.row)" v-hasPermi="['admin:user:group:update', 'admin:user:tag:update']">编辑</a>
            <el-divider direction="vertical"></el-divider>
            <a
              @click="handleDelete(scope.row.id, scope.$index)"
              disable
              v-hasPermi="['admin:user:group:delete', 'admin:user:tag:delete']"
              >删除</a
            >
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
    <el-dialog
      :title="
        $route.path.indexOf('group') !== -1
          ? `${labelPram.id ? '编辑' : '添加'}分组名称`
          : `${labelPram.id ? '编辑' : '添加'}标签名称`
      "
      :visible.sync="visible"
      width="540px"
      :close-on-click-modal="false"
    >
      <el-form :rules="rules" :model="labelPram" ref="ruleForm" label-width="75px" class="demo-dynamic">
        <el-form-item :label="$route.path.indexOf('group') !== -1 ? '分组名称：' : '标签名称：'" prop="value">
          <el-input
            v-model="labelPram.value"
            :placeholder="$route.path.indexOf('group') !== -1 ? '请填写分组名称' : '请填写标签名称'"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="confirm()">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  tagListApi,
  tagDeleteApi,
  tagSaveApi,
  tagInfoApi,
  tagUpdateApi,
  groupListApi,
  groupDeleteApi,
  groupSaveApi,
  groupUpdateApi,
} from '@/api/user';
export default {
  name: 'UserGroup',
  data() {
    return {
      visible: false,
      tableFrom: {
        page: 1,
        limit: 20,
      },
      tableData: {
        data: [],
        total: 0,
      },
      listLoading: true,
      labelPram: {
        value: '',
        id: '',
      },
      rules: {
        value: [{ required: true, message: '请输入用户标签', trigger: 'blur' }],
      },
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    info() {},
    onAdd(row) {
      if (row) {
        this.labelPram.id = row.id;
        this.labelPram.value = this.$route.path.indexOf('group') !== -1 ? row.groupName : row.name;
      } else {
        this.labelPram.id = '';
        this.labelPram.value = '';
      }
      this.visible = true;
    },
    confirm() {
      if (this.$route.path.indexOf('group') !== -1) {
        this.labelPram.id
          ? groupUpdateApi({ id: this.labelPram.id }, { groupName: this.labelPram.value }).then(() => {
              this.$message.success('编辑成功');
              this.getList();
              this.visible = false;
            })
          : groupSaveApi({ groupName: this.labelPram.value }).then(() => {
              this.$message.success('新增成功');
              this.getList();
              this.visible = false;
            });
      } else {
        this.labelPram.id
          ? tagUpdateApi({ id: this.labelPram.id }, { name: this.labelPram.value }).then(() => {
              this.$message.success('编辑成功');
              this.getList();
              this.visible = false;
            })
          : tagSaveApi({ name: this.labelPram.value }).then(() => {
              this.$message.success('新增成功');
              this.getList();
              this.visible = false;
            });
      }
    },
    // 列表
    getList() {
      this.listLoading = true;
      this.$route.path.indexOf('group') !== -1
        ? groupListApi(this.tableFrom)
            .then((res) => {
              this.tableData.data = res.list;
              this.tableData.total = res.total;
              this.listLoading = false;
            })
            .catch((res) => {
              this.listLoading = false;
            })
        : tagListApi(this.tableFrom)
            .then((res) => {
              this.tableData.data = res.list;
              this.tableData.total = res.total;
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
    // 删除
    handleDelete(id, idx) {
      this.$modalSure('删除吗？所有用户已经关联的数据都会清除').then(() => {
        this.$route.path.indexOf('group') !== -1
          ? groupDeleteApi({ id: id }).then(() => {
              this.$message.success('删除成功');
              if (this.tableData.data.length === 1 && this.tableFrom.page > 1)
                this.tableFrom.page = this.tableFrom.page - 1;
              this.getList();
            })
          : tagDeleteApi({ id: id }).then(() => {
              this.$message.success('删除成功');
              if (this.tableData.data.length === 1 && this.tableFrom.page > 1)
                this.tableFrom.page = this.tableFrom.page - 1;
              this.getList();
            });
      });
    },
  },
};
</script>

<style scoped lang="scss"></style>
