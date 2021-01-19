<template>
  <div class="components-container">
    <el-form inline>
      <el-form-item>
        <el-button type="primary" @click="handlerOpenEditData({},0)">添加数据</el-button>
      </el-form-item>
      <el-form-item>
        <el-select v-model="listPram.status" placeholder="状态" clearable @change="handlerSearch">
          <el-option
            v-for="item in constants.showHiddenStatus"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <!--        <el-form-item label="关键词">-->
      <!--          <el-input v-model="listPram.keywords" placeholder="请输入关键词" clearable></el-input>-->
      <!--        </el-form-item>-->
      <!--        <el-form-item>-->
      <!--          <el-button type="primary" @click="handlerSearch">查询</el-button>-->
      <!--        </el-form-item>-->
    </el-form>
    <el-dialog
      :title="editDataConfig.isCreate === 0?'添加数据':'编辑数据'"
      :visible.sync="editDataConfig.visible"
      append-to-body
      destroy-on-close
    >
      <edit
        v-if="editDataConfig.visible"
        :formid="formId"
        :edit-data="editDataConfig.editData"
        :is-create="editDataConfig.isCreate"
        @hideDialog="handlerHideDia"
      />
    </el-dialog>
    <el-table
      :data="dataList.list"
      style="width: 100%;margin-bottom: 20px;"
    >
      <el-table-column label="编号" prop="id" />
      <el-table-column
        v-for="item,index in formConf.fields"
        :key="index"
        :label="item.__config__.label"
        :prop="item.__vModel__"
      >
        <template slot-scope="scope"><!-- 自定义维护特殊列展示 -->
          <span v-if="item.__config__.label === '状态'">
            {{ scope.row[item.__vModel__] | filterShowOrHideForFormConfig }}</span>
          <span v-else>{{ scope.row[item.__vModel__] }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="primary" @click="handlerOpenEditData(scope.row,1)">编辑</el-button>
          <el-button type="danger" @click="handlerDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page="listPram.page"
      :page-sizes="constants.page.limit"
      :layout="constants.page.layout"
      :total="dataList.total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script>
import edit from './edit'
import * as systemGroupDataApi from '@/api/systemGroupData.js'
import * as systemFormConfigApi from '@/api/systemFormConfig.js'
export default {
  // name: "combineDataList"
  components: { edit },
  data() {
    return {
      constants: this.$constants,
      listPram: {
        gid: null,
        keywords: null,
        status: null, // 1=开启 2=关闭
        page: 1,
        limit: constants.page.limit[0]
      },
      editDataConfig: {
        visible: false,
        isCreate: 0, // 0=create 1=edit
        editData: {}
      },
      formConf: { fields: [] },
      dataList: { list: [], total: 0 },
      formId: constants.formConfigIds[1].id
    }
  },
  mounted() {
    this.handlerGetFormConfig()
    this.listPram.gid = this.formId
    this.handlerGetListData(this.listPram)
  },
  methods: {
    handlerSearch() {
      this.listPram.page = 1
      this.handlerGetListData(this.listPram)
    },
    handlerGetListData(pram) { // 获取列表数据
      systemGroupDataApi.groupDataList(pram).then(data => {
        const _selfList = []
        data.list.forEach(_lItem => {
          _lItem.value = JSON.parse(_lItem.value)
          const _fields = _lItem.value.fields
          const _rowData = {}
          _fields.map((item) => {
            _rowData[item.name] = item.value
          })
          _rowData.id = _lItem.id
          _rowData.sort = _lItem.sort
          _rowData.status = _lItem.status
          _selfList.push(_rowData)
        })
        this.dataList.list = _selfList
        this.dataList.total = data.total
      })
    },
    handlerGetFormConfig() { // 获取表单配置后生成table列
      const _pram = { id: this.formId }
      systemFormConfigApi.getFormConfigInfo(_pram).then(data => {
        this.formConf = JSON.parse(data.content)
      })
    },
    handlerOpenEditData(rowData, isCreate) {
      this.editDataConfig.editData = rowData
      this.editDataConfig.isCreate = isCreate
      this.editDataConfig.visible = true
    },
    handlerHideDia() {
      this.handlerGetListData(this.listPram)
      this.editDataConfig.visible = false
    },
    handlerDelete(rowData) {
      this.$confirm('确实删除当前数据', '提示').then(() => {
        systemGroupDataApi.groupDataDelete(rowData).then(data => {
          this.$message.success('删除数据成功')
          this.handlerHideDia()
        })
      })
    },
    handleSizeChange(val) {
      this.listPram.limit = val
      this.handlerGetListData(this.listPram)
    },
    handleCurrentChange(val) {
      this.listPram.page = val
      this.handlerGetListData(this.listPram)
    }
  }
}
</script>

<style scoped>

</style>
