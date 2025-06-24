<template>
  <div class="divBox">
    <pages-header
      ref="pageHeader"
      :title="activityTpye === 1 ? `${isEdit ? '编辑' : '添加'}氛围图` : `${isEdit ? '编辑' : '添加'}活动边框`"
      :backUrl="activityTpye === 1 ? '/marketing/atmosphere/list' : '/marketing/border/list'"
    ></pages-header>
    <el-card class="box-card mb20 mt14" :body-style="{ padding: '0 20px 20px' }" :bordered="false" shadow="never">
      <el-tabs
        v-if="tabList.length > 0"
        class="list-tabs"
        v-model="currentTab"
        @tab-click="onChangeType('formValidate')"
      >
        <el-tab-pane v-for="(item, index) in tabList" :key="index" :name="item.value" :label="item.title" />
      </el-tabs>
      <el-form
        ref="formValidate"
        v-loading="fullscreenLoading"
        class="formValidate mt20"
        :rules="ruleValidate"
        :model="formValidate"
        label-width="120px"
        @submit.native.prevent
      >
        <div v-show="currentTab == 1">
          <el-form-item label="活动名称：" prop="name">
            <el-input v-model="formValidate.name" size="small" class="selWidth" placeholder="请输入活动名称" />
          </el-form-item>
          <el-form-item label="活动时间：" prop="timeVal">
            <el-date-picker
              class="mb10 selWidth"
              v-model="formValidate.timeVal"
              :default-time="['00:00:00', '23:59:59']"
              type="datetimerange"
              range-separator="至"
              value-format="yyyy-MM-dd HH:mm:ss"
              start-placeholder="开始日期"
              :picker-options="pickerOptions"
              end-placeholder="结束日期"
              @change="onchangeTime"
            >
            </el-date-picker>
            <p class="desc">{{ `设置活动${activityTpye === 1 ? '氛围图' : '边框'}在商城展示时间` }}</p>
          </el-form-item>
          <el-form-item :label="activityTpye === 1 ? '活动氛围图：' : '活动边框：'" prop="style">
            <div class="upLoadPicBox mb10" @click="modalPicTap(false)">
              <div v-if="formValidate.style" class="pictrue"><img :src="formValidate.style" /></div>
              <div v-else class="upLoad">
                <i class="el-icon-camera cameraIconfont" />
              </div>
            </div>
            <p class="desc">{{ activityTpye === 1 ? '750*100px' : '750*750px' }}</p>
          </el-form-item>
          <el-form-item label="是否开启：">
            <el-switch
              :width="56"
              v-model="formValidate.status"
              :active-value="true"
              :inactive-value="false"
              active-text="开启"
              inactive-text="关闭"
            />
          </el-form-item>
        </div>
        <div v-show="currentTab == 2">
          <el-form-item label-width="0">
            <el-radio-group v-model="formValidate.method">
              <el-radio :label="0">全部商品参与</el-radio>
              <el-radio :label="1">指定商品参与</el-radio>
              <el-radio :label="3">指定分类参与</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="formValidate.method == 1" label-width="0">
            <el-button size="small" type="primary" @click="addGoods">添加商品</el-button>
            <el-button size="small" @click="batchDel" :disabled="!multipleSelection.length">批量删除</el-button>
          </el-form-item>
          <el-form-item v-if="formValidate.method == 1" label-width="0">
            <el-table
              ref="tableList"
              v-loading="listLoading"
              :data="tableData.data"
              style="width: 100%"
              size="small"
              @selection-change="handleSelectionChange"
              @select-all="selectAll"
              @select="selectOne"
              class="tableSelection"
            >
              <el-table-column type="selection" width="55"> </el-table-column>
              <el-table-column prop="id" label="ID" width="55"> </el-table-column>
              <el-table-column label="商品图" min-width="80">
                <template slot-scope="scope">
                  <div class="demo-image__preview line-heightOne">
                    <el-image :src="scope.row.image" :preview-src-list="[scope.row.image]" />
                  </div>
                </template>
              </el-table-column>
              <el-table-column :show-overflow-tooltip="true" prop="storeName" label="商品名称" min-width="200" />
              <el-table-column prop="price" label="售价" min-width="90" />
              <el-table-column prop="stock" label="库存" min-width="70" />
              <el-table-column label="操作" width="140" fixed="right">
                <template slot-scope="scope">
                  <a @click="handleDelete(scope.$index, scope.row)">删除</a>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-form-item
            v-if="formValidate.method == 3"
            label="选择分类："
            :span="24"
            label-width="100px"
            prop="proCategorylist"
          >
            <el-cascader
              v-model="formValidate.proCategorylist"
              class="selWidth"
              :props="props"
              :options="adminProductClassify"
              filterable
              placeholder=""
              :show-all-levels="false"
              clearable
            />
          </el-form-item>
        </div>
      </el-form>
    </el-card>
    <el-card class="fixed-card" :bordered="false" shadow="never">
      <el-button v-show="currentTab == 1" size="small" type="primary" @click="submitForm('formValidate', 1)"
        >下一步</el-button
      >
      <el-button v-show="currentTab == 2" size="small" @click="currentTab = '1'" class="priamry_border"
        >上一步</el-button
      >
      <el-button v-show="currentTab == 2" size="small" type="primary" @click="submitForm('formValidate', 2)"
        >保存</el-button
      >
    </el-card>
  </div>
</template>

<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import { atuosphereUpdateApi, selectProductList, createAtuosphere } from '@/api/marketing';
import { productListbyidsApi } from '@/api/store';
import pagesHeader from '@/components/pagesHeader/index.vue';
import { mapGetters } from 'vuex';
export default {
  components: {
    pagesHeader,
  },
  name: 'addAtmosphere',
  directives: {
    // 计算是否滚动到最下面
    selectLoadMore: {
      bind(el, binding) {
        // 获取element-ui定义好的scroll盒子
        const SELECTWRAP_DOM = el.querySelector('.el-select-dropdown .el-select-dropdown__wrap');
        SELECTWRAP_DOM.addEventListener(
          'scroll',
          function () {
            if (this.scrollHeight - this.scrollTop < this.clientHeight + 1) {
              binding.value();
            }
          },
          { passive: true },
        );
      },
    },
  },
  data() {
    return {
      multiple: true,
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7 || time.getTime() > Date.now() + 600 * 8.64e7; //限制未来时间
        },
      },
      props: {
        value: 'id',
        label: 'name',
        children: 'child',
        expandTrigger: 'hover',
        checkStrictly: false,
        multiple: true,
        emitPath: false,
      },
      currentTab: '1',
      tabList: [
        { value: '1', title: '基础设置' },
        { value: '2', title: '使用范围' },
      ],
      fullscreenLoading: false,
      formValidate: {
        name: '',
        starttime: '',
        endtime: '',
        style: '',
        status: 0,
        method: 0,
        products: null,
        proCategorylist: [],
        type: null,
        timeVal: [],
        id: null,
      },

      ruleValidate: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        style: [{ required: true, message: '请选择图片', trigger: 'blur' }],
        proCategorylist: [{ type: 'array', required: true, message: '请至少选择一个分类', trigger: 'change' }],
        timeVal: [{ type: 'array', required: true, message: '请选择时间', trigger: 'change' }],
      },
      listLoading: false,
      selectGoods: false,
      multipleSelection: [],
      merSelect: [],
      tableData: {
        data: [],
        total: 0,
      },
      options: [
        {
          value: 'all',
          label: '所有页',
        },
        {
          value: 'one',
          label: '当前页',
        },
      ],
      tableFrom: {
        page: 1,
        limit: 20,
        spu_ids: [],
      },
      ids: [],
      search: {
        limit: 10,
        page: 1,
      },
      brandList: [],
      loading: false,
      tempRoute: {},
    };
  },
  created() {
    this.tempRoute = Object.assign({}, this.$route);
  },
  computed: {
    ...mapGetters(['adminProductClassify']),
    isEdit() {
      return this.$route.params.id ? true : false;
    },
    activityTpye() {
      return this.$route.path.indexOf('border') !== -1 ? 0 : 1;
    },
  },
  mounted() {
    if (!localStorage.getItem('adminProductClassify')) this.$store.dispatch('product/getAdminProductClassify');
    if (this.$route.params.id) {
      this.setTagsViewTitle();
      this.getInfo(this.$route.params.id);
    }
  },
  methods: {
    // 点击选项
    onChangeType(formName) {
      //this.$refs[formName].resetFields();
    },
    // 根据id查询商品列表，编辑时用
    getproductListbyids() {
      productListbyidsApi(this.formValidate.products).then((res) => {
        this.tableData.data = res.list;
      });
    },
    // 活动时间
    onchangeTime(e) {
      this.$set(this.formValidate, 'timeVal', e);
      this.formValidate.starttime = e ? e[0] : '';
      this.formValidate.endtime = e ? e[1] : '';
    },
    //上传图片
    modalPicTap(multiple) {
      const _this = this;
      this.$modalUpload(
        function (img) {
          if (!img) return;
          _this.formValidate.style = img[0].sattDir;
          if (_this.formValidate.style) _this.$refs.formValidate.clearValidate(['style']);
        },
        multiple,
        'activitystyle',
      );
    },
    setTagsViewTitle() {
      const title = this.activityTpye === 1 ? '编辑氛围图' : '编辑活动边框';
      const route = Object.assign({}, this.tempRoute, {
        title: `${title}-${this.$route.params.id}`,
      });
      this.$store.dispatch('tagsView/updateVisitedView', route);
    },
    // 氛围图详情
    getInfo(id) {
      this.fullscreenLoading = true;
      let data = JSON.parse(localStorage.getItem('activitystyle'));
      let info = JSON.parse(JSON.stringify(data));
      this.formValidate.starttime = info.starttime;
      this.formValidate.endtime = info.endtime;
      this.formValidate.style = info.style;
      this.formValidate.status = info.status;
      this.formValidate.method = info.method;
      this.formValidate.name = info.name;
      this.formValidate.products = info.products;
      this.formValidate.type = this.activityTpye;
      this.formValidate.timeVal = [info.starttime, info.endtime];
      if (info.method === 1) this.getproductListbyids();
      switch (info.method) {
        case 0:
          this.formValidate.products = '';
          break;
        case 1:
          this.formValidate.products = '';
          break;
        case 2:
          break;
        case 3:
          this.formValidate.proCategorylist = info.products.split(',').map((item) => item * 1);
          break;
        default:
          break;
      }
      this.fullscreenLoading = false;
      this.fullscreenLoading = false;
    },
    addGoods() {
      const _this = this;
      this.$modalGoodList(
        function (row) {
          _this.listLoading = false;
          _this.tableData.data = row;
        },
        'many',
        _this.tableData.data,
      );
    },
    getList(num) {
      this.tableFrom.page = num || this.tableFrom.page;
      selectProductList({
        spu_ids: this.tableFrom.spu_ids.toString(),
        page: this.tableFrom.page,
        limit: this.tableFrom.limit,
      })
        .then((res) => {
          this.tableData.data = res.data.list;
          this.tableData.total = res.data.count;
          if (this.selectAllPage == 'all') {
            this.multipleSelection.push(...this.tableData.data);
            this.multipleSelection.forEach((row) => {
              this.$refs.tableList.toggleRowSelection(row, true);
            });
          }

          this.listLoading = false;
        })
        .catch((res) => {
          this.$message.error(res.message);
          this.listLoading = false;
        });
    },
    //行删除
    handleDelete(index, row) {
      this.$modalSure('删除该商品吗？').then(() => {
        this.tableData.data.splice(index, 1);
      });
    },
    //批量删除
    batchDel() {
      this.$modalSure('批量删除商品吗？').then(() => {
        let data = [];
        for (let item1 of this.tableData.data) {
          let _index = this.multipleSelection.findIndex((c) => c.id === item1.id);
          if (_index === -1) {
            data.push(item1);
          }
        }
        this.tableData.data = data;
      });
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    selectAll(data) {
      let id = data.map((i, index) => {
        return i.spu_id;
      });
      this.ids = Array.from(new Set([...this.ids, ...id]));
    },
    selectOne(data, row) {
      let id = data.map((i, index) => {
        return i.spu_id;
      });
      let index = this.ids.findIndex((e) => {
        return e == row.spu_id;
      });
      this.ids.splice(index, 1);
      this.ids = Array.from(new Set([...this.ids, ...id]));
    },
    handleCommand(command) {
      this.$message('click on item ' + command);
      this.selectAllPage = command === 'all';
      this.$nextTick(() => {
        this.$refs.tableList.toggleAllSelection();
      });
    },
    submitForm(formName, num) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (num === 1) {
            this.currentTab = '2';
          } else {
            switch (this.formValidate.method) {
              case 0:
                this.formValidate.products = '';
                break;
              case 1:
                if (!this.tableData.data.length) return this.$message.error('请选择至少一个商品');
                let data = [];
                this.tableData.data.map((item) => data.push(item.id));
                this.formValidate.products = data.toString();
                break;
              case 2:
                break;
              case 3:
                if (!this.formValidate.proCategorylist.length) return this.$message.error('请选择至少一个分类');
                this.formValidate.products = this.formValidate.proCategorylist.toString();
                break;
              default:
                break;
            }
            this.formValidate.type = this.activityTpye;
            if (this.formValidate.style == '') return this.$message.error('请上传氛围图');
            if (this.isEdit) this.formValidate.id = this.$route.params.id;
            this.isEdit
              ? atuosphereUpdateApi(this.formValidate).then((res) => {
                  this.$message.success('编辑成功');
                  this.$router.push({
                    path: this.activityTpye === 1 ? '/marketing/atmosphere/list' : '/marketing/border/list',
                  });
                })
              : createAtuosphere(this.formValidate).then((res) => {
                  this.$message.success('新增成功');
                  this.$router.push({
                    path: this.activityTpye === 1 ? '/marketing/atmosphere/list' : '/marketing/border/list',
                  });
                });
          }
        } else {
          return false;
        }
      });
    },
    pageChange(page) {
      this.tableFrom.page = page;
      this.getList('');
    },
    handleSizeChange(val) {
      this.tableFrom.limit = val;
      this.getList('');
    },
  },
};
</script>

<style lang="scss" scoped>
.desc {
  color: #999;
  font-size: 12px;
  line-height: 16px;
  margin: 0;
}

::v-deep.el-input__suffix {
  right: 10px;
  line-height: 32px;
}

.selWidth {
  width: 460px !important;
}
</style>
