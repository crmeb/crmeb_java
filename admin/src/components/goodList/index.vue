<template>
  <div class="divBox">
    <div class="header clearfix">
      <div class="container">
        <el-form inline>
          <el-form-item label="商品分类：">
            <el-cascader v-model="tableFrom.cateId" :options="merCateList" :props="props" clearable class="selWidth mr20" @change="getList(1)"/>
          </el-form-item>
          <el-form-item label="商品搜索：">
            <el-input v-model="tableFrom.keywords" @input="onInput($event)" placeholder="请输入商品名称，关键字，产品编号" class="selWidth">
              <el-button slot="append" icon="el-icon-search" @click="getList(1)"/>
            </el-input>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <el-table
      v-loading="listLoading"
      :data="tableData.data"
      style="width: 100%"
      size="mini"
      ref="multipleTable"
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        key="2"
        v-if="handleNum === 'many'"
        width="55">
        <template slot="header" slot-scope="scope">
          <el-checkbox slot="reference" :value="isChecked && checkedPage.indexOf(tableFrom.page) > -1" @change="changeType" />
        </template>
        <template slot-scope="scope">
          <el-checkbox
            :value="checkedIds.indexOf(scope.row.id) > -1"
            @change="(v)=>changeOne(v,scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
        width="55"
        key="1"
        v-if="handleNum !== 'many'"
      >
        <template slot-scope="scope">
          <el-radio :label="scope.row.id" v-model="templateRadio" @change.native="getTemplateRow(scope.row)">&nbsp</el-radio>
        </template>
      </el-table-column>
      <el-table-column
        prop="id"
        label="ID"
        min-width="50"
      />
      <el-table-column label="商品图" width="80">
        <template slot-scope="scope">
          <div class="demo-image__preview">
            <el-image
              style="width: 36px; height: 36px"
              :src="scope.row.image"
              :preview-src-list="imgList"
            />
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="storeName"
        label="商品名称"
        min-width="180"
      />
      <el-table-column
      label="商品分类"
      min-width="150"
      >
        <template slot-scope="scope">
          <span v-for="(item, index) in scope.row.cateValues.split(',')" :key="index" class="mr10">{{ item }}</span>
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
      />
    </div>
    <div v-if="handleNum === 'many'" class="right-align">
      <el-button size="small" type="primary" @click="ok">确定</el-button>
    </div>
  </div>
</template>

<script>
import { productLstApi, productDeleteApi, categoryApi, putOnShellApi, offShellApi, productHeadersApi } from '@/api/store'
export default {
  name: 'GoodList',
  props: {
    handleNum: {
      type: String,
      default: ''
    },
    checked: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      templateRadio:0,
      merCateList: [],
      props: {
        children: 'child',
        label: 'name',
        value: 'id',
        emitPath: false
      },
      listLoading: true,
      tableData: {
        data: [],
        total: 0
      },
      tableFrom: {
        page: 1,
        limit: 10,
        cateId: '',
        keywords: '',
        type: '1'
      },
      imgList: [],
      multipleSelection: [],
      checkedPage: [],
      isChecked: false,
      isIndex: 0,
      checkBox: [],
      checkedIds: [] // 订单当前页选中的数据
    }
  },
  mounted() {
    this.getList()
    if(this.checked.length){
      let [ ...arr2 ] = this.checked
      this.checkBox = arr2
      this.checkedIds = arr2.map(item => {
        return item.id
      })
    }
    this.getCategorySelect()
  },
  methods: {
    onInput(e){
      this.$forceUpdate();
    },
    changeType(v) {
      this.isChecked = v
        const index = this.checkedPage.indexOf(this.tableFrom.page)
        this.isIndex = index
        this.checkedPage.push(this.tableFrom.page)
        if (index > -1) {
          this.checkedPage.splice(index, 1)
        }
        this.syncCheckedId(v)
    },
    changeOne(v, order) {
      if (v) {
        const index = this.checkedIds.indexOf(order.id)
        if (index === -1){
          this.checkedIds.push(order.id)
          this.checkBox.push(order)
        }
      } else {
        const index = this.checkedIds.indexOf(order.id)
        if (index > -1){
          this.checkedIds.splice(index, 1)
          this.checkBox.splice(index, 1)
        }
      }
    },
    syncCheckedId(o) {
      const ids = this.tableData.data.map(v => v.id)
      if(o){
        this.tableData.data.forEach(item => {
          const index = this.checkedIds.indexOf(item.id)
          if (index === -1) {
            this.checkedIds.push(item.id)
            this.checkBox.push(item)
          }
        })
      }else{
        this.tableData.data.forEach(item => {
          const index = this.checkedIds.indexOf(item.id)
          if (index > -1) {
            this.checkedIds.splice(index, 1)
            this.checkBox.splice(index, 1)
          }
        })
      }
    },
    handleSelectionChange(val) {
      const tables = []
      val.map((item) => {
        tables.push({ src: item.image,
          id: item.id })
      })
      this.multipleSelection = tables
    },
    ok() {
      this.$emit('getStoreItem', this.checkBox)
    },
    getTemplateRow(row){
      this.$emit('getStoreItem', row)
    },
    // 商户分类；
    getCategorySelect() {
      categoryApi({ status: -1, type: 1 }).then(res => {
        this.merCateList = res
      }).catch(res => {
        this.$message.error(res.message)
      })
    },
    getList(num) {
      this.listLoading = true
      this.tableFrom.page = num ? num : this.tableFrom.page;
      productLstApi(this.tableFrom).then(res => {
        this.tableData.data = res.list
        this.tableData.total = res.total
        this.tableData.data.map((item) => {
          this.imgList.push(item.image)
        })
        this.tableData.data.forEach(item => {
          this.checked.forEach(element => {
            if (Number(item.id) === Number(element.id)) {
              this.$nextTick(() => {
                this.$refs.multipleTable.toggleRowSelection(item, true)
              })
            }
          })
        })
        this.listLoading = false
      }).catch(res => {
        this.listLoading = false
        this.$message.error(res.message)
      })
    },
    pageChange(page) {
      this.tableFrom.page = page
      this.getList()
    },
    handleSizeChange(val) {
      this.tableFrom.limit = val
      this.getList()
    }
  }
}
</script>

<style scoped lang="scss">
  .right-align{
    float: right;
  }
  .selWidth{
    width: 250px !important;
  }
  .seachTiele{
    line-height: 35px;
  }
  .fr{
    float: right;
  }
</style>
