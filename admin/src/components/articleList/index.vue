<template>
  <div class="divBox">
    <div class="header clearfix">
      <div class="container">
        <el-form inline>
          <el-form-item>
            <el-input v-model="listPram.keywords" placeholder="请输入关键词" clearable />
          </el-form-item>
          <el-form-item>
            <el-cascader v-model="listPram.cid" :options="categoryTreeData" :props="categoryProps" placeholer="选择分类" clearable />
          </el-form-item>
          <el-form-item>
            <el-button size="mini" type="primary" @click="handerSearch">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <el-table :data="listData.list"  style="width: 100%"
              size="mini"
              max-height="400"
              tooltip-effect="dark"
              highlight-current-row>
      <el-table-column
        label=""
        width="55">
        <template slot-scope="{ row, index }">
          <el-radio v-model="templateRadio" :label="row.id" @change.native="getTemplateRow(row)">&nbsp;</el-radio>
        </template>
      </el-table-column>
      <el-table-column label="图片" min-width="80">
        <template slot-scope="scope">
          <div class="demo-image__preview">
            <el-image
              style="width: 36px; height: 36px"
              :src="scope.row.imageInput"
              :preview-src-list="imgList"
            />
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="150" />
      <el-table-column prop="visit" label="浏览量" min-width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.visit | filterEmpty }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" min-width="150"/>
      <!--<el-table-column label="操作" min-width="150">-->
        <!--<template slot-scope="scope">-->
          <!--<el-button type="warning" disabled>关联产品</el-button>-->
        <!--</template>-->
      <!--</el-table-column>-->
    </el-table>
    <div class="block mb20">
      <el-pagination
        :current-page="listPram.page"
        :page-sizes="constants.page.limit"
        :layout="constants.page.layout"
        :total="listData.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
  import * as articleApi from '@/api/article.js'
  import * as categoryApi from '@/api/categoryApi.js'
  import * as selfUtil from '@/utils/ZBKJIutil.js'
  export default {
    // name: "list",
    props: {
      handle: {
        type: String,
        default: ''
      }
    },
    data() {
      return {
        templateRadio:'',
        imgList: [],
        constants: this.$constants,
        listPram: {
          keywords: null,
          cid: null,
          page: 1,
          limit: this.$constants.page.limit[0]
        },
        listData: { list: [], total: 0 },
        editDialogConfig: {
          visible: false,
          data: {},
          isEdit: 0 // 0=add 1=edit
        },
        categoryTreeData: [],
        categoryProps: {
          value: 'id',
          label: 'name',
          children: 'child',
          expandTrigger: 'hover',
          checkStrictly: true,
          emitPath: false
        }
      }
    },
    mounted() {
      this.handlerGetListData(this.listPram)
      this.handlerGetCategoryTreeData()
    },
    methods: {
      getTemplateRow(row){
        this.$emit('getArticle', row)
      },
      handerSearch() {
        this.listPram.page = 1
        this.handlerGetListData(this.listPram)
      },
      handlerGetListData(pram) {
        articleApi.ListArticle(pram).then(data => {
          this.listData = data
          // this.listData.list.map((item) => {
          //   item.imageInput.map(i => {
          //     this.imgList.push(i)
          //   })
          // })
        })
      },
      handlerGetCategoryTreeData() {
        const _pram = { type: this.constants.categoryType[2].value, status: 1 }
        categoryApi.treeCategroy(_pram).then(data => {
          this.categoryTreeData = selfUtil.addTreeListLabelForCasCard(data)
        })
      },
      handlerHideDialog() {
        this.handlerGetListData(this.listPram)
        this.editDialogConfig.visible = false
      },
      handlerDelete(rowData) {
        this.$confirm('确定删除当前数据', '提示').then(result => {
          articleApi.DelArticle(rowData).then(data => {
            this.$message.success('删除数据成功')
            this.handlerGetListData(this.listPram)
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
