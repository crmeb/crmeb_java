<template>
  <div>
    <template v-if="selectModel">
      <el-tree
        ref="tree"
        :data="treeList"
        show-checkbox
        check-strictly
        node-key="id"
        @check="getCurrentNode"
        :default-checked-keys="selectModelKeysNew"
        :props="treeProps">
      </el-tree>
    </template>
    <template v-else>
      <div class="divBox">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <div class="container">
              <el-form inline size="small">
                <el-form-item>
                  <el-select v-model="listPram.status" placeholder="状态" class="selWidth">
                    <el-option label="全部" :value="-1"></el-option>
                    <el-option label="显示" :value="1"></el-option>
                    <el-option label="不显示" :value="0"></el-option>
                    <!--<el-option-->
                      <!--v-for="item in constants.roleListStatus"-->
                      <!--:key="item.value"-->
                      <!--:label="item.label"-->
                      <!--:value="item.value"-->
                    <!--/>-->
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-input v-model="listPram.name" placeholder="名称" clearable class="selWidth"/>
                </el-form-item>
                <el-form-item>
                  <el-button size="mini" @click="handlerGetList">查询</el-button>
                </el-form-item>
              </el-form>
            </div>
            <el-button size="mini" type="primary" @click="handleAddMenu({id:0,name:'顶层目录'})">新增{{ biztype.name }}</el-button>
          </div>
          <el-table
            ref="treeList"
            :data="treeList"
            size="mini"
            class="table"
            highlight-current-row
            row-key="id"
            :tree-props="{children: 'child', hasChildren: 'hasChildren'}"
          >
            <el-table-column prop="name" label="名称" min-width="200">
              <template slot-scope="scope">
                {{ scope.row.name }} | {{ scope.row.id }}
              </template>
            </el-table-column>
            <template v-if="!selectModel">
              <el-table-column label="类型" min-width="150">
                <template slot-scope="scope">
                  <span>{{ scope.row.type | filterCategroyType | filterEmpty }}</span>
                </template>
              </el-table-column>
              <el-table-column label="分类图标" min-width="80">
                <template slot-scope="scope">
                  <div class="listPic" v-if=" biztype.value === 5 ">
                    <i :class="'el-icon-' + scope.row.extra" style="font-size: 20px;" />
                  </div>
                  <div class="demo-image__preview" v-else>
                    <el-image
                      style="width: 36px; height: 36px"
                      :src="scope.row.extra"
                      :preview-src-list="[scope.row.extra]"
                    />
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="Url" min-width="250">
                <template slot-scope="scope">
                  <span>{{ scope.row.url }}</span>
                </template>
              </el-table-column>
              <el-table-column label="排序" prop="sort" min-width="150" />
              <el-table-column
                label="状态"
                min-width="150"
              >
                <template slot-scope="scope">
                  <el-switch
                    v-model="scope.row.status"
                    :active-value="true"
                    :inactive-value="false"
                    active-text="显示"
                    inactive-text="隐藏"
                    @change="onchangeIsShow(scope.row)"
                  />
                </template>
              </el-table-column>
              <!--<el-table-column label="启用状态" width="150">-->
                <!--<template slot-scope="scope">-->
                  <!--<span>{{ scope.row.status | filterYesOrNo }}</span>-->
                <!--</template>-->
              <!--</el-table-column>-->
              <el-table-column label="操作" min-width="200" fixed="right">
                <template slot-scope="scope">
                  <el-button
                    v-if="(biztype.value === 1 && scope.row.pid === 0) || biztype.value === 5"
                    type="text"
                    size="small"
                    @click="handleAddMenu(scope.row)"
                  >添加子目录</el-button>
                  <el-button type="text"  size="small" @click="handleEditMenu(scope.row)">编辑</el-button>
                  <el-button type="text"  size="small" @click="handleDelMenu(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </template>
          </el-table>
        </el-card>
      </div>
    </template>
    <el-dialog
      :title="(editDialogConfig.isCreate===0?`创建${biztype.name}`:`编辑${biztype.name}`)"
      :visible.sync="editDialogConfig.visible"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <edit
        v-if="editDialogConfig.visible"
        :prent="editDialogConfig.prent"
        :is-create="editDialogConfig.isCreate"
        :edit-data="editDialogConfig.data"
        :biztype="editDialogConfig.biztype"
        :all-tree-list="treeList"
        @hideEditDialog="hideEditDialog"
      />
    </el-dialog>
  </div>
</template>

<script>
import * as categoryApi from '@/api/categoryApi.js'
import info from './info'
import edit from './edit'
import * as constants from '@/utils/constants.js'
import * as selfUtil from '@/utils/ZBKJIutil.js'
export default {
  // name: "list"
  components: { info, edit },
  props: {
    biztype: { // 类型，1 产品分类，2 附件分类，3 文章分类， 4 设置分类， 5 菜单分类
      type: Object,
      default: { value: -1 },
      validator: (obj) => {
        return obj.value > 0
      }
    },
    pid: {
      type: Number,
      default: 0,
      validator: (value) => {
        return value >= 0
      }
    },
    selectModel: { // 是否选择模式
      type: Boolean,
      default: false
    },
    // selectModelKeys: {
    //   type: String
    // },
    selectModelKeys: {
      type: Array
    },
    rowSelect: {}
  },
  data() {
    return {
      selectModelKeysNew: this.selectModelKeys,
      loading: false,
      constants,
      treeProps: {
        label: 'name',
        children: 'child',
        expandTrigger: 'hover',
        checkStrictly: true,
        emitPath: false
      },
      // treeCheckedKeys:[],// 选择模式下的属性结构默认选中
      multipleSelection: [],
      editDialogConfig: {
        visible: false,
        isCreate: 0, // 0=创建，1=编辑
        prent: {}, // 父级对象
        data: {},
        biztype: this.biztype // 统一主业务中的目录类型
      },
      dataList: [],
      treeList: [],
      listPram: {
       pid: this.pid,
        type: this.biztype.value,
        status: -1,
        name: '',
        page: constants.page.page,
        limit: constants.page.limit[0]
      },
      viewInfoConfig: {
        data: null,
        visible: false
      }
    }
  },
  mounted() {
   /* if(this.biztype.value === 3){
      this.listPram.pageSize = constants.page.pageSize[4]
      this.handlerGetList()
    }else{*/
    this.handlerGetTreeList()
    // }
  },
  methods: {
    onchangeIsShow(row){
      categoryApi.categroyUpdateStatus( row.id ).then(() => {
        this.$message.success('修改成功')
        this.handlerGetTreeList()
      })
    },
    handleEditMenu(rowData) {
      this.editDialogConfig.isCreate = 1
      this.editDialogConfig.data = rowData
      this.editDialogConfig.prent = rowData
      this.editDialogConfig.visible = true
    },
    handleAddMenu(rowData) {
      this.editDialogConfig.isCreate = 0
      this.editDialogConfig.prent = rowData
      this.editDialogConfig.data = {}
      this.editDialogConfig.biztype = this.biztype
      this.editDialogConfig.visible = true
    },
    getCurrentNode(data) {
      let node = this.$refs.tree.getNode(data);
      this.childNodes(node);
      this.parentNodes(node);
      //是否编辑的表示
      // this.ruleForm.isEditorFlag = true;
      //编辑时候使用
      this.$emit('rulesSelect', this.$refs.tree.getCheckedKeys());
      // this.selectModelKeys = this.$refs.tree.getCheckedKeys();
      //无论编辑和新增点击了就传到后台这个值
      // this.$emit('rulesSelect', this.$refs.tree.getCheckedKeys().concat(this.$refs.tree.getHalfCheckedKeys()));
      // this.ruleForm.menuIdsisEditor = this.$refs.tree.getCheckedKeys().concat(this.$refs.tree.getHalfCheckedKeys());
    },
    //具体方法可以看element官网api
    childNodes(node){
      let len = node.childNodes.length;
      for(let i = 0; i < len; i++){
        node.childNodes[i].checked = node.checked;
        this.childNodes(node.childNodes[i]);
      }
    },
    parentNodes(node){
      if(node.parent){
        for(let key in node){
          if(key == "parent"){
            node[key].checked = true;
            this.parentNodes(node[key]);
          }
        }
      }
    },
    handleDelMenu(rowData) {
      this.$confirm('确定删除当前数据?').then(() => {
        categoryApi.deleteCategroy(rowData).then(data => {
          this.handlerGetTreeList()
          this.$message.success('删除成功')
        })
      })
    },
    handlerGetList() {
      this.handlerGetTreeList();
      // categoryApi.listCategroy({status:this.listPram.status, type: 1 }).then(data => {
      //   this.treeList = data.list
      // })
    },
    handlerGetTreeList() {
      // this.biztype.value === 5 && !this.selectModel) ?  -1 : 1
      // const _pram = { type: this.biztype.value, status: !this.selectModel ? -1 : (this.biztype.value === 5 ? -1 : 1) }
      const _pram = { type: this.biztype.value, status: this.listPram.status, name: this.listPram.name  }
      this.loading = true
      this.biztype.value!==3 ? categoryApi.treeCategroy(_pram).then(data => {
        this.treeList = this.handleAddArrt(data)
        this.loading = false
      }).catch(()=>{
        this.loading = false
      }) : categoryApi.listCategroy({ type: 3, status: '', pid: this.listPram.pid}).then(data => {
        this.treeList = data.list
      })
    },
    handlerGetInfo(id) {
      this.viewInfoConfig.data = id
      this.viewInfoConfig.visible = true
    },
    handleNodeClick(data) {
      console.log('data:', data)
    },
    handleAddArrt(treeData) {
      const _result = selfUtil.addTreeListLabel(treeData)
      return _result
    },
    hideEditDialog() {
      setTimeout(() => {
        this.editDialogConfig.prent = {}
        this.editDialogConfig.type = 0
        this.editDialogConfig.visible = false
        this.handlerGetTreeList()
      }, 200)
    },
    handleSelectionChange(d1, { checkedNodes, checkedKeys, halfCheckedNodes, halfCheckedKeys }) {
      // this.multipleSelection =  checkedKeys.concat(halfCheckedKeys)
      this.multipleSelection = checkedKeys
      this.$emit('rulesSelect', this.multipleSelection)
    }
  }
}
</script>

<style scoped>
  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;
  }
</style>
