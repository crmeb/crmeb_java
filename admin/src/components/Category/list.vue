<template>
  <div>
    <template v-if="selectModel">
      <el-tree
        ref="tree"
        :data="treeList"
        show-checkbox
        node-key="id"
        @check="getCurrentNode"
        :default-checked-keys="selectModelKeysNew"
        :props="treeProps"
      >
      </el-tree>
    </template>
    <template v-else>
      <div class="divBox">
        <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
          <div class="padding-add">
            <el-form inline size="small">
              <el-form-item label="分类状态：">
                <el-select v-model="listPram.status" placeholder="状态" class="selWidth" @change="handlerGetList">
                  <el-option label="全部" :value="-1"></el-option>
                  <el-option label="显示" :value="1"></el-option>
                  <el-option label="不显示" :value="0"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="分类名称：">
                <el-input v-model="listPram.name" placeholder="请输入名称" class="selWidth" size="small" clearable>
                </el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handlerGetList">搜索</el-button>
                <el-button size="small" @click="handleReset">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
        <el-card class="box-card mt14">
          <div slot="header" class="clearfix">
            <el-button
              type="primary"
              @click="handleAddMenu({ id: 0, name: '顶层目录' })"
              v-hasPermi="['admin:category:save']"
              >添加{{ biztype.name }}</el-button
            >
          </div>
          <el-table
            ref="treeList"
            :data="treeList"
            class="table"
            highlight-current-row
            row-key="id"
            :tree-props="{ children: 'child', hasChildren: 'hasChildren' }"
          >
            <el-table-column prop="name" label="名称" min-width="240">
              <template slot-scope="scope"> {{ scope.row.name }} | {{ scope.row.id }} </template>
            </el-table-column>
            <template v-if="!selectModel">
              <el-table-column label="类型" min-width="150">
                <template slot-scope="scope">
                  <span>{{ scope.row.type | filterCategroyType | filterEmpty }}</span>
                </template>
              </el-table-column>
              <el-table-column label="分类图标" min-width="80">
                <template slot-scope="scope">
                  <div class="listPic" v-if="biztype.value === 5">
                    <i :class="'el-icon-' + scope.row.extra" style="font-size: 20px" />
                  </div>
                  <div class="demo-image__preview" v-else>
                    <el-image
                      style="width: 36px; height: 36px"
                      :src="scope.row.extra"
                      :preview-src-list="[scope.row.extra]"
                      v-if="scope.row.extra"
                    />
                    <img style="width: 36px; height: 36px" v-else :src="defaultImg" alt="" />
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="Url" min-width="250" v-if="biztype.value === 5" key="2">
                <template slot-scope="scope">
                  <span>{{ scope.row.url }}</span>
                </template>
              </el-table-column>
              <el-table-column label="排序" prop="sort" min-width="150" />
              <el-table-column label="状态" min-width="150">
                <!--  -->
                <template slot-scope="scope" v-if="checkPermi(['admin:category:update:status'])">
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

              <el-table-column label="操作" width="190" fixed="right">
                <template slot-scope="scope">
                  <template v-if="(biztype.value === 1 && scope.row.pid === 0) || biztype.value === 5">
                    <a @click="handleAddMenu(scope.row)">添加子目录</a>
                    <el-divider direction="vertical"></el-divider>
                  </template>
                  <a @click="handleEditMenu(scope.row)" v-hasPermi="['admin:category:info']">编辑</a>
                  <el-divider direction="vertical"></el-divider>
                  <a
                    v-debounceClick="
                      () => {
                        handleDelMenu(scope.row);
                      }
                    "
                    v-hasPermi="['admin:category:delete']"
                    >删除</a
                  >
                </template>
              </el-table-column>
            </template>
          </el-table>
        </el-card>
      </div>
    </template>
    <el-dialog
      :title="editDialogConfig.isCreate === 0 ? `添加${biztype.name}` : `编辑${biztype.name}`"
      :visible.sync="editDialogConfig.visible"
      destroy-on-close
      :close-on-click-modal="false"
      width="540px"
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
import * as categoryApi from '@/api/categoryApi.js';
import info from './info';
import edit from './edit';
import * as selfUtil from '@/utils/ZBKJIutil.js';
import { checkPermi, checkRole } from '@/utils/permission';
export default {
  // name: "list"
  components: { info, edit },
  props: {
    biztype: {
      // 类型，1 产品分类，2 附件分类，3 文章分类， 4 设置分类， 5 菜单分类
      type: Object,
      default: { value: -1 },
      validator: (obj) => {
        return obj.value > 0;
      },
    },
    pid: {
      type: Number,
      default: 0,
      validator: (value) => {
        return value >= 0;
      },
    },
    selectModel: {
      // 是否选择模式
      type: Boolean,
      default: false,
    },
    selectModelKeys: {
      type: Array,
    },
    rowSelect: {},
  },
  data() {
    return {
      selectModelKeysNew: this.selectModelKeys,
      loading: false,
      constants: this.$constants,
      treeProps: {
        label: 'name',
        children: 'child',
        // expandTrigger: 'hover',
        // checkStrictly: false,
        // emitPath: false
      },
      // treeCheckedKeys:[],// 选择模式下的属性结构默认选中
      multipleSelection: [],
      editDialogConfig: {
        visible: false,
        isCreate: 0, // 0=创建，1=编辑
        prent: {}, // 父级对象
        data: {},
        biztype: this.biztype, // 统一主业务中的目录类型
      },
      dataList: [],
      treeList: [],
      listPram: {
        pid: this.pid,
        type: this.biztype.value,
        status: -1,
        name: '',
        page: this.$constants.page.page,
        limit: this.$constants.page.limit[0],
      },
      viewInfoConfig: {
        data: null,
        visible: false,
      },
      defaultImg: require('@/assets/imgs/moren.jpg'),
    };
  },
  mounted() {
    /* if(this.biztype.value === 3){
      this.listPram.pageSize = constants.page.pageSize[4]
      this.handlerGetList()
    }else{*/
    this.handlerGetTreeList();
    // }
  },
  methods: {
    checkPermi, //权限控制
    //重置
    handleReset() {
      this.listPram.status = -1;
      this.listPram.name = '';
      this.handlerGetTreeList();
    },
    onchangeIsShow(row) {
      categoryApi
        .categroyUpdateStatus(row.id)
        .then((res) => {
          this.$message.success('修改成功');
          this.handlerGetTreeList();
        })
        .catch(() => {
          row.status = !row.status;
        });
    },
    handleEditMenu(rowData) {
      this.editDialogConfig.isCreate = 1;
      this.editDialogConfig.data = rowData;
      this.editDialogConfig.prent = rowData;
      this.editDialogConfig.visible = true;
    },
    handleAddMenu(rowData) {
      this.editDialogConfig.isCreate = 0;
      this.editDialogConfig.prent = rowData;
      this.editDialogConfig.data = {};
      this.editDialogConfig.biztype = this.biztype;
      this.editDialogConfig.visible = true;
    },
    getCurrentNode(data) {
      let node = this.$refs.tree.getNode(data);
      this.childNodes(node);
      // this.parentNodes(node);
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
    childNodes(node) {
      let len = node.childNodes.length;
      for (let i = 0; i < len; i++) {
        node.childNodes[i].checked = node.checked;
        this.childNodes(node.childNodes[i]);
      }
    },
    parentNodes(node) {
      if (node.parent) {
        for (let key in node) {
          if (key == 'parent') {
            node[key].checked = true;
            this.parentNodes(node[key]);
          }
        }
      }
    },
    handleDelMenu(rowData) {
      this.$modalSure('删除当前数据?').then(() => {
        categoryApi.deleteCategroy(rowData).then((res) => {
          this.handlerGetTreeList();
          this.$message.success('删除成功');
        });
      });
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
      const _pram = { type: this.biztype.value, status: this.listPram.status, name: this.listPram.name };
      this.loading = true;
      this.biztype.value !== 3
        ? categoryApi
            .treeCategroy(_pram)
            .then((data) => {
              this.treeList = this.handleAddArrt(data);
              localStorage.setItem('adminProductClassify', JSON.stringify(this.treeList));
              this.loading = false;
            })
            .catch(() => {
              this.loading = false;
            })
        : categoryApi
            .listCategroy({ type: 3, status: this.listPram.status, pid: this.listPram.pid, name: this.listPram.name })
            .then((data) => {
              this.treeList = data;
              localStorage.setItem('adminProductClassify', JSON.stringify(this.treeList));
            });
    },
    handlerGetInfo(id) {
      this.viewInfoConfig.data = id;
      this.viewInfoConfig.visible = true;
    },
    handleNodeClick(data) {
      console.log('data:', data);
    },
    handleAddArrt(treeData) {
      const _result = selfUtil.addTreeListLabel(treeData);
      return _result;
    },
    hideEditDialog() {
      setTimeout(() => {
        this.editDialogConfig.prent = {};
        this.editDialogConfig.type = 0;
        this.editDialogConfig.visible = false;
        this.handlerGetTreeList();
      }, 200);
    },
    handleSelectionChange(d1, { checkedNodes, checkedKeys, halfCheckedNodes, halfCheckedKeys }) {
      // this.multipleSelection =  checkedKeys.concat(halfCheckedKeys)
      this.multipleSelection = checkedKeys;
      this.$emit('rulesSelect', this.multipleSelection);
    },
  },
};
</script>

<style lang="scss" scoped>
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
</style>
