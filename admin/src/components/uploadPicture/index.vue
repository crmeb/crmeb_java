                                                                                                                                                                                                                                                               <template>
  <div>
    <el-row :gutter="30">
      <el-col v-bind="grid">
        <div class="Nav">
          <div class="input">
            <el-input
              v-model="filterText"
              placeholder="选择分类"
              prefix-icon="el-icon-search"
              style="width: 100%;"
              clearable
            />
          </div>
          <div class="trees-coadd">
            <div class="scollhide">
              <div class="trees" :style="{maxHeight:(!pictureType?'345px':'700px')}">
                <el-tree
                  ref="tree"
                  :data="treeData2"
                  :filter-node-method="filterNode"
                  :props="defaultProps"
                  highlight-current
                >
                  <div slot-scope="{ node, data}" class="custom-tree-node" @click.stop="handleNodeClick(data)">
                    <div>
                      <span class="custom-tree-node-label" :title="node.label">{{ node.label }}</span>
                      <span
                        v-if="data.space_property_name"
                        style="font-size: 11px;color: #3889b1"
                      >（{{ data.name }}）</span>
                    </div>
                    <span class="el-ic">
                      <el-dropdown>
                        <span class="el-dropdown-link">
                          <i class="el-icon-more"></i>
                        </span>
                        <el-dropdown-menu slot="dropdown">
                          <el-dropdown-item @click.native="onAdd(data.id)">添加分类</el-dropdown-item>
                          <el-dropdown-item v-if="node.label !== '全部图片'" @click.native="onEdit(data.id)">编辑分类</el-dropdown-item>
                          <el-dropdown-item v-if="node.label !== '全部图片'" @click.native="handleDelete(data.id)">删除分类</el-dropdown-item>
                        </el-dropdown-menu>
                      </el-dropdown>
                    </span>
                  </div>
                </el-tree>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col v-bind="grid2" class="colLeft">
        <div class="conter mb15">
          <div class="bnt">
            <el-button v-if="!pictureType" size="small" type="primary" class="mr20 mb20" @click="checkPics">使用选中图片</el-button>
            <div class="mr20 mb20">
              <el-button-group>
                <el-tooltip class="item" effect="dark" content="上传图片" placement="top-start">
                  <el-upload
                    class="upload-demo"
                    action
                    :http-request="handleUploadForm"
                    :on-change="imgSaveToUrl"
                    :headers="myHeaders"
                    :show-file-list="false"
                    multiple
                  >
                    <el-button icon="el-icon-upload2" size="mini" style="font-size: 15px;"></el-button>
                  </el-upload>
                </el-tooltip>
                <el-tooltip class="item" effect="dark" content="添加分类" placement="top-start">
                  <el-button icon="el-icon-circle-plus-outline" style="font-size: 15px;" size="mini" @click="onAdd(10000)"></el-button>
                </el-tooltip>
                <el-tooltip class="item" effect="dark" content="删除图片" placement="top-start">
                  <el-button icon="el-icon-delete" style="font-size: 15px;" size="mini" @click.stop="editPicList('图片')"></el-button>
                </el-tooltip>
              </el-button-group>
            </div>
            <el-select v-model="sleOptions.attachment_category_name" placeholder="图片移动至" class="mb20" size="small">
              <el-option
                class="demo"
                :label="sleOptions.attachment_category_name"
                :value="sleOptions.attachment_category_id"
                style="max-width: 560px;height:200px;overflow: auto;background-color:#fff"
              >
                <el-tree
                  ref="tree2"
                  :data="treeData2"
                  :filter-node-method="filterNode"
                  :props="defaultProps"
                  highlight-current
                  @node-click="handleSelClick"
                />
              </el-option>
            </el-select>
          </div>
          <div class="pictrueList acea-row" v-loading="loadingPic">
            <div v-show="isShowPic" class="imagesNo">
              <i class="el-icon-picture" style="font-size: 60px;color: rgb(219, 219, 219);" />
              <span class="imagesNo_sp">图片库为空</span>
            </div>
            <div class="conters scrollbarAll" :style="{maxHeight:(!pictureType?'296px':'700px')}">
              <div
                v-for="(item, index) in pictrueList.list"
                :key="index"
                class="gridPic"
              >
                <img v-lazy="item.sattDir?item.sattDir:localImg" :class="item.isSelect ? 'on': '' " @click="changImage(item, index, pictrueList.list)" />
                <!--<img v-if="index === 0" v-lazy="item.localImg?item.localImg:item.sattDir" :class="item.isSelect ? 'on': '' " @click="changImage(item, index, pictrueList.list)" />-->
                <!--<img v-else v-lazy="item.sattDir?item.sattDir:localImg" :class="item.isSelect ? 'on': '' " @click="changImage(item, index, pictrueList.list)" />-->
              </div>
            </div>
          </div>
        </div>
        <div class="block">
          <el-pagination
            :page-sizes="!pictureType?[10, 20, 30, 40]:[30,60,90,120]"
            :page-size="tableData.limit"
            :current-page="tableData.page"
            :pager-count="5"
            layout="total, sizes, prev, pager, next, jumper"
            :total="pictrueList.total"
            @size-change="handleSizeChange"
            @current-change="pageChange"
          />
        </div>
      </el-col>
    </el-row>
    <el-dialog
      :title="bizTitle"
      :visible.sync="visible"
      destroy-on-close
      :close-on-click-modal="false"
      :modal="modals"
      @close="closeModel"
    >
      <el-form ref="editPram" :model="editPram" label-width="100px"  v-loading="loading">
        <el-form-item label="上级分类" prop="pid"
                      :rules="[{ type: 'number',required:true,message:'请选择上级分类',trigger:['blur','change'] }]"
        >
          <el-cascader v-model="editPram.pid" :options="treeData2" :props="categoryProps" style="width:100%"/>
        </el-form-item>
        <el-form-item
          label="分类名称"
          prop="name"
          :rules="[{ required:true,message:'请输入分类名称',trigger:['blur','change'] }]"
        >
          <el-input v-model="editPram.name" placeholder="分类名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="editPram.sort"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handlerSubmit('editPram')">确定</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { addCategroy, listCategroy, treeCategroy, infoCategroy, updateCategroy, deleteCategroy } from '@/api/categoryApi'
import { fileImageApi, fileListApi, fileDeleteApi, attachmentMoveApi } from '@/api/systemSetting'
import { getToken } from '@/utils/auth'
export default {
  name: 'Upload',
  props: {
    pictureType: {
      type: String,
      default: ''
    },
    isMore: {
      type: String,
      default: '1'
    },
    modelName: {
      type: String,
      default: ''
    },
    checkedMore: {
      type: Array,
      default: ()=> []
    }
  },
  data() {
    return {
      loadingPic: false,
      loading: false,
      modals: false,
      allTreeList:[],
      categoryProps: {
        value: 'id',
        label: 'name',
        children: 'child',
        expandTrigger: 'hover',
        checkStrictly: true,
        emitPath: false
      },
      editPram:{
        pid: 1000,
        name: '',
        type: 2,
        sort: 1,
        status: 0,
        url:'url',
        id: 0
      },
      visible: false,
      bizTitle: '',
      sleOptions: {
        attrId: '',
        pid: ''
      },
      list: [],
      grid: {
        xl: 7,
        lg: 7,
        md: 7,
        sm: 7,
        xs: 24
      },
      grid2: {
        xl: 17,
        lg: 17,
        md: 17,
        sm: 17,
        xs: 24
      },
      filterText: '',
      treeData: [],
      treeData2: [],
      defaultProps: {
        children: 'child',
        label: 'name'
      },
      tableData: {
        page: 1,
        limit: 10,
        pid: 0
      },
      classifyId: 0,
      myHeaders: { 'X-Token': getToken() },
      treeFrom: {
        status: -1,
        type: 2
      },
      pictrueList: {
        list: [],
        total: 0
      },
      isShowPic: false,
      checkPicList: [],
      ids: [],
      listPram: {
        pid: 0,
        type: 2,
        status: 0,
        name: '',
        page: 1,
        limit: 9999
      },
      localImg: ''
    }
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val)
    }
  },
  mounted() {
    this.pictureType ? this.tableData.limit = 30 : this.tableData.limit = 10
    if (this.$route && this.$route.query.field === 'dialog') import('../../../public/UEditor/dialogs/internal.js')
    this.getList()
    this.getFileList()
    // this.handlerGetList()
  },
  methods: {
    // 选取图片后自动回调，里面可以获取到文件
    imgSaveToUrl(event){  // 也可以用file
      this.localFile=event.raw  // 或者 this.localFile=file.raw

      // 转换操作可以不放到这个函数里面，
      // 因为这个函数会被多次触发，上传时触发，上传成功也触发
      let reader = new FileReader()
      reader.readAsDataURL(this.localFile);// 这里也可以直接写参数event.raw

      // 转换成功后的操作，reader.result即为转换后的DataURL ，
      // 它不需要自己定义，你可以console.integralLog(reader.result)看一下
      reader.onload=()=>{
        // console.integralLog(reader.result)
      }

      /* 另外一种本地预览方法 */
      let URL = window.URL || window.webkitURL;
      this.localImg = URL.createObjectURL(event.raw);
      // 转换后的地址为 blob:http://xxx/7bf54338-74bb-47b9-9a7f-7a7093c716b5
    },
    closeModel(){
      this.$refs['editPram'].resetFields();
    },
    handlerSubmit(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if( this.editPram.pid==10000) this.editPram.pid = 0
          this.bizTitle==='添加分类' ? addCategroy(this.editPram).then(data => {
            this.$message.success('创建成功')
            this.visible = false
            this.getList()
          }) : updateCategroy(this.editPram).then(data => {
            this.$message.success('编辑成功')
            this.visible = false
            this.getList()
          })
        } else {
          return false;
        }
      })
    },
    // 表单分类
    handlerGetList() {
      let datas = {
        name: '全部图片',
        id: ''
      }
      treeCategroy(this.treeFrom).then(data => {
        this.allTreeList = data
        this.allTreeLis.unshift(datas)
      })
    },
    // 搜索分类
    filterNode(value, data) {
      if (!value) return true
      return data.name.indexOf(value) !== -1
    },
    // 所有分类
    getList() {
      const data = {
        name: '全部图片',
        id: 10000
      }
      treeCategroy(this.treeFrom).then(res => {
        this.treeData = res
        this.treeData.unshift(data)
        this.treeData2 = [...this.treeData]
      })
    },
    // 添加分类
    onAdd(id) {
      this.tableData.pid = id
      if ( this.tableData.pid === 10000) this.tableData.pid = 0
      this.bizTitle = '添加分类'
      this.visible = true
      if(id) this.editPram = {
          pid: id,
          name: '',
          type: 2,
          sort: 1,
          status: 0,
          url:'url',
          id: 0
      }
    },
    // 编辑
    onEdit(id) {
      if( id === 10000 ) id = 0
      this.bizTitle = '编辑分类'
      this.loading = true
      infoCategroy({id:id}).then((res) => {
        this.editPram = res
        this.loading = false
      })
      this.visible = true
    },
    // 删除
    handleDelete(id) {
      if(id === 10000) id = 0
      this.$modalSure().then(() => {
        deleteCategroy({id:id}).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    handleNodeClick(data) {
      this.tableData.pid = data.id
      this.getFileList()
    },
    // 上传
    handleUploadForm(param){
      const formData = new FormData()
      const data = {
        model: this.modelName ? this.modelName : this.$route.path.split("/")[1],
        pid: this.tableData.pid
      }
      formData.append('multipart', param.file)
      let loading = this.$loading({
        lock: true,
        text: '上传中，请稍候...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      fileImageApi(formData, data).then(res => {
        loading.close()
        this.$message.success('上传成功')
        this.getFileList()
      }).catch(res => {
        loading.close()
      })
    },
    // 文件列表
    getFileList() {
      if ( this.tableData.pid === 10000) this.tableData.pid = 0
      this.loadingPic = true;
      fileListApi(this.tableData).then(async res => {
        this.pictrueList.list = res.list
        if(this.tableData.page === 1 && this.pictrueList.list.length > 0) this.pictrueList.list[0].localImg = this.localImg
        if (this.pictrueList.list.length) {
          this.isShowPic = false
        } else {
          this.isShowPic = true
        }
        this.pictrueList.total = res.total
        this.loadingPic = false;
      }).catch(()=>{
        this.loadingPic = false;
      })
    },
    pageChange(page) {
      this.tableData.page = page
      this.getFileList()
    },
    handleSizeChange(val) {
      this.tableData.limit = val
      this.getFileList()
    },
    // 选中图片
    changImage(item, index, row) {
      let selectItem = ''
      this.$set(item, 'isSelect', item.isSelect === undefined ? true : !item.isSelect)
      selectItem = this.pictrueList.list.filter((item) => { return item.isSelect === true })
      this.checkPicList = selectItem
      this.ids = []
      this.checkPicList.map((item, index) => {
        this.ids.push(item.attId)
      })
    },
    // 点击使用选中图片
    checkPics() {
        if (!this.checkPicList.length) return this.$message.warning('请先选择图片')
        if (this.$route && this.$route.query.field === 'dialog') {
          let str = '';
          for (let i = 0; i < this.checkPicList.length; i++) {
            str += '<img src="' + this.checkPicList[i].sattDir + '">'
          }
          /* eslint-disable */
          nowEditor.dialog.close(true);
          nowEditor.editor.setContent(str,true)
        }else{
          if(this.isMore==='1' && this.checkPicList.length > 1){ return this.$message.warning('最多只能选一张图片')}
          this.$emit('getImage', [...this.checkedMore,...this.checkPicList ])
        }
    },
    // 删除图片
    editPicList(tit) {
      if (!this.checkPicList.length) return this.$message.warning('请先选择图片')
      this.$modalSure().then(() => {
        fileDeleteApi(this.ids.join(',')).then(() => {
          this.$message.success('刪除成功')
          this.getFileList()
          this.checkPicList = []
        })
      })
    },
    // 移动分类点击
    handleSelClick(node) {
      if (this.ids.length) {
        this.sleOptions = {
          attrId: this.ids.join(','),
          pid: node.id
        }
        this.getMove()
      } else {
        this.$message.warning('请先选择图片')
      }
    },
    getMove() {
      attachmentMoveApi(this.sleOptions).then(async res => {
        this.$message.success('操作成功')
        this.clearBoth()
        this.getFileList()
      }).catch(res => {
        this.clearBoth()
      })
    },
    clearBoth() {
      this.sleOptions = {
        attrId: '',
        pid: ''
      }
      this.checkPicList = []
      this.ids = []
    }

  }
}
</script>

<style scoped lang="scss">
  .demo::-webkit-scrollbar {
    display: none; /* Chrome Safari */
  }

  .demo {
    scrollbar-width: none; /* firefox */
    -ms-overflow-style: none; /* IE 10+ */
    overflow-x: hidden;
    overflow-y: auto;
  }
  .temp {
    height: 0;
    margin-bottom: 0;
    border: none;
    padding: 0;
  }
  .selectTreeClass {
    background: #d5e8fc;
  }
  .treeBox {
    width: 100%;
    height: 100%;
  }
  .upload-demo {
    display: inline-block !important;
    float: left;
  }
  .tree_w {
    padding: 20px 30px
  }
  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 12px;
    padding-right: 8px;
    color: #4386c6;
  }
  .custom-tree-node-label{
    display: block;
    width: 125px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow:ellipsis;
  }
  .el-ic {
    display: none;
    i, span {
      /*padding: 0 14px;*/
      font-size: 18px;
      font-weight: 600;
    }
    .svg-icon {
      color: #4386c6;
    }
  }
  .el-tree-node__content {
    height: 38px;
  }
  .el-tree-node__expand-icon {
    color: #428bca;
    /*padding: 10px 10px 0px 10px !important;*/
  }
  .el-tree-node__content:hover .el-ic {
    color: #428bca !important;
    display: inline-block;
  }
  .el-tree-node__content:hover {
    font-weight: bold;
  }
  .el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content :hover {
    .el-tree-node__expand-icon.is-leaf {
      color: transparent;
      cursor: default;
    }
    /*background-color: #3998d9;*/
    .custom-tree-node {
      font-weight: bold;
    }
    .el-tree-node__expand-icon {
      font-weight: bold;
    }
  }
  .el-dialog__body {
    .upload-container .image-preview .image-preview-wrapper img {
      height: 100px;
    }
    .el-dialog .el-collapse-item__wrap {
      padding-top: 0px;

    }
    .spatial_img {
      .el-collapse-item__wrap {
        margin-bottom: 0;
        padding-top: 0px;
      }
    }
    .upload-container .image-preview .image-preview-wrapper {
      width: 120px;
    }
    .upload-container .image-preview .image-preview-action {
      line-height: 100px;
      height: 100px;
    }
  }
  .trees-coadd {
    width: 100%;
    border-radius: 4px;
    overflow: hidden;
    position: relative;
    .scollhide {
      overflow-x: hidden;
      overflow-y: scroll;
      padding: 10px 0 10px 0;
      box-sizing: border-box;
      .trees {
        width: 100%;
      }
    }
    .scollhide::-webkit-scrollbar {
      display: none;
    }
  }
  .conters{
    display: flex;
    flex-wrap: wrap;
    /*max-height: 296px;*/
    overflow: auto;
  }
  .conters:after {content: ""; width: 410px !important; }
  .gridPic{
    margin-right: 15px;
    margin-bottom: 10px;
    width: 110px;
    height: 110px;
    cursor: pointer;
    img{
      width: 100%;
      height: 100%;
      display: block;
    }
  }
  .conter {
    width: 99%;
    height: 100%;
    .bnt {
      width: 100%;
      padding: 0 13px 10px 7px;
      box-sizing: border-box;
      display: flex;
    }
    .pictrueList {
      /*padding-left: 15px;*/
      width: 100%;
      el-image {
        width: 100%;
        border: 2px solid #fff;
      }
      .on {
        border: 2px solid #5FB878;
      }
    }
    .el-image{
      width: 110px;
      height: 110px;
      cursor: pointer;
    }
    .imagesNo{
      width: 100%;
      display: flex;
      justify-content: center;
      flex-direction: column;
      align-items: center;
      margin: 65px 0;
      .imagesNo_sp{
        font-size: 13px;
        color: #dbdbdb;
        line-height: 3;
      }
    }
  }
</style>
