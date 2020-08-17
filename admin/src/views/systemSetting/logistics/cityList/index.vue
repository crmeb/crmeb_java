<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix" v-if="parentId>0">
        <el-form inline size="small">
          <el-form-item>
            <el-button type="primary" @click="back()">返回</el-button>
            <!--          <el-button type="primary" @click="addcity()"><i class="el-icon-plus" />添加城市</el-button>-->
          </el-form-item>
        </el-form>
      </div>
      <el-table
        v-loading="loading"
        :data="tableData"
        size="mini"
        class="table"
        highlight-current-row
      >
        <el-table-column
          prop="cityId"
          label="编号"
          min-width="100"
        />
        <el-table-column
          label="上级名称"
          min-width="100"
        >
          <div>{{ parentName }}</div>
        </el-table-column>
        <el-table-column
          min-width="250"
          label="地区名称"
        >
          <template slot-scope="scope">
            <el-link :underline="false" @click="cityDetail(scope.row)">{{ scope.row.name }}</el-link>
          </template>
        </el-table-column>
        <!--<el-table-column-->
          <!--label="状态"-->
          <!--min-width="100"-->
        <!--&gt;-->
          <!--<template slot-scope="scope">-->
            <!--<el-switch-->
              <!--v-model="scope.row.isShow"-->
              <!--class="demo"-->
              <!--active-text="开启"-->
              <!--inactive-text="关闭"-->
              <!--@change="cityStatus(scope.row)"-->
            <!--/>-->
          <!--</template>-->
        <!--</el-table-column>-->
        <el-table-column
          fixed="right"
          min-width="80"
          label="操作"
        >
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="editCity(scope.row)">编辑</el-button>
            <!--            <el-button type="text" size="small">删除</el-button>-->
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog
      title="提示"
      :visible.sync="dialogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <parser v-if="formShow" :form-conf="formConf" :form-edit-data="formData" :is-edit="isCreate === 1" @submit="submit" />
    </el-dialog>
  </div>
</template>

<script>
import parser from '@/components/FormGenerator/components/parser/Parser'
import * as systemFormConfigApi from '@/api/systemFormConfig.js'
import * as logistics from '@/api/logistics.js'
export default {
  name: 'CityList',
  components: { parser },
  data() {
    return {
      // 表单
      formConf: { fields: [] },
      // 生成表单id
      formId: 70,
      tableData: [],
      // 上级名称
      parentName: '中国',
      // 父级城市id
      parentId: 0,
      loading: false,
      dialogVisible: false,
      // 选中id
      editId: 0,
      formShow: false,
      formData: {},
      isCreate: 0
    }
  },
  created() {
    this.getCityList()
  },
  methods: {
    // 获取城市数据
    getCityList() {
      this.loading = true
      logistics.cityList({
        parentId: this.parentId
      }).then(data => {
        this.loading = false
        this.tableData = data
      })
    },
    // 城市详情
    cityDetail(item) {
      this.parentId = item.cityId
      this.parentName = item.name
      this.getCityList()
    },
    // 返回
    back() {
      this.parentName = '中国'
      this.parentId = 0
      this.getCityList()
    },
    // 状态
    cityStatus(e) {
      logistics.updateStatus({
        id: e.id,
        cityId: e.cityId,
        status: e.isShow
      }).then(res => {
        this.$message.success('操作成功')
      }).catch(() => {
        e.isShow = !e.isShow
      })
    },
    // 编辑
    editCity(item) {
      this.editId = item.id
      const _pram = { id: this.formId }
      systemFormConfigApi.getFormConfigInfo(_pram).then(data => {
        this.formShow = false
        this.isCreate = 0
        this.getCityInfo()
        this.dialogVisible = true
        this.formConf = JSON.parse(data.content)
      })
    },
    // 详情
    getCityInfo() {
      logistics.cityInfo({
        id: this.editId
      }).then(res => {
        this.isCreate = 1
        this.formData = res
        this.formData.parentName = this.parentName
        setTimeout(() => { // 让表单重复渲染待编辑数据
          this.formShow = true
        }, 80)
      })
    },
    submit(data) {
      const param = {
        id: this.editId,
        parentId: this.parentId,
        name: data.name
      }
      logistics.cityUpdate(param).then(res => {
        this.dialogVisible = false
        this.getCityList()
      })
    },
    //  关闭模态框
    handleClose(done) {
      this.formConf.fields = []
      this.dialogVisible = false
    }
  }
}
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
</style>
