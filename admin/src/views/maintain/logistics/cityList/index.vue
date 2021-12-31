<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-table
        v-loading="listLoading"
        :data="tableData"
        size="mini"
        class="table"
        row-key="cityId"
        highlight-current-row
        border
        lazy
        :load="load"
        :header-cell-style=" {fontWeight:'bold'}"
        :tree-props="{children: 'child', hasChildren: 'hasChildren'}"
      >
        <el-table-column
          prop="cityId"
          label="编号"
          min-width="100"
        />
        <el-table-column
        prop="parentName"
          label="上级名称"
          min-width="100"
        >
          <!-- <div>{{ parentName }}</div> -->
        </el-table-column>
        <el-table-column
          prop="name"
          min-width="250"
          label="地区名称"
        >
        </el-table-column>
        <el-table-column
          fixed="right"
          min-width="80"
          label="操作"
        >
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="editCity(scope.row)" v-hasPermi="['admin:system:city:update']">编辑</el-button>
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
import {Debounce} from '@/utils/validate'
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
      listLoading: true,
      dialogVisible: false,
      // 选中id
      editId: 0,
      formShow: false,
      formData: {},
      isCreate: 0,
    }
  },
  created() {
    this.getCityList()
  },
  methods: {
    // 获取城市数据
    getCityList() {
      this.listLoading = true
      logistics.cityList({
        parentId: this.parentId
      }).then(data => {
        this.listLoading = false
        let arr = [];
        data.forEach((item) => {
          let str = {};
          str = item;
          str.hasChildren = true;
          str.parentName = '中国';
          arr.push(str);
        });
        this.tableData = arr;
      })
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
      this.$confirm('请勿频繁修改此配置项', '提示', {
          confirmButtonText: '确定修改',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.editId = item.id
          this.parentId = item.parentId;
          const _pram = { id: this.formId }
          systemFormConfigApi.getFormConfigInfo(_pram).then(data => {
            this.formShow = false
            this.isCreate = 0
            this.getCityInfo()
            this.dialogVisible = true
            this.formConf = JSON.parse(data.content)
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消'
          });
        });
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
    submit:Debounce(function(data) {
      const param = {
        id: this.editId,
        parentId: this.parentId,
        name: data.name
      }
      logistics.cityUpdate(param).then(res => {
        this.$message.success('修改成功')
        this.dialogVisible = false
        // this.getCityList()
      })
    }),
    //  关闭模态框
    handleClose(done) {
      this.formConf.fields = []
      this.dialogVisible = false
    },
     load(tree, treeNode, resolve){
       logistics.cityList({
          parentId: tree.cityId,
        }).then(data => {
          let arr = [];
          data.forEach((item) => {
            let str = {};
            str = item;
            str.hasChildren = true; //给每个省市节点赋一个可展开的值
            str.parentName = tree.name
            this.parentName = tree.name
            if(item.level == 2){
              str.hasChildren = false;//当节点为三级时，不可展开
            }
            arr.push(str);
          });
         resolve(arr)
        })
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
