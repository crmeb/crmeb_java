<template>
  <div class="divBox relative">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form
          inline
          size="small"
          :model="userFrom"
          ref="userFrom"
          :label-position="labelPosition"
          label-width="75px"
        >
          <div class="acea-row search-form row-between">
            <div class="search-form-box">
              <el-form-item label="用户搜索：">
                <el-input v-model="userFrom.keywords" placeholder="请输入姓名或手机号" clearable class="selWidth" />
              </el-form-item>
              <el-form-item label="用户等级：">
                <el-select v-model="levelData" placeholder="请选择" class="selWidth" clearable filterable multiple>
                  <el-option
                    :value="item.id"
                    v-for="(item, index) in levelList"
                    :key="index"
                    :label="item.name"
                  ></el-option>
                </el-select>
              </el-form-item>
            </div>
            <el-form-item class="search-form-sub" v-if="!collapse">
              <el-button type="primary" icon="ios-search" label="default" @click="userSearchs">搜索</el-button>
              <el-button class="ResetSearch mr14" @click="reset('userFrom')" size="small">重置</el-button>
              <a class="ivu-ml-8" @click="collapse = !collapse">
                <template v-if="!collapse"> 展开 <i class="el-icon-arrow-down"></i> </template>
                <template v-else> 收起 <i class="el-icon-arrow-up"></i> </template>
              </a>
            </el-form-item>
          </div>
          <div v-if="collapse" class="acea-row search-form">
            <div class="search-form-box">
              <el-form-item label="用户分组：">
                <el-select v-model="groupData" placeholder="请选择" class="selWidth" clearable filterable multiple>
                  <el-option
                    :value="item.id"
                    v-for="(item, index) in groupList"
                    :key="index"
                    :label="item.groupName"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="用户标签：">
                <el-select v-model="labelData" placeholder="请选择" class="selWidth" clearable filterable multiple>
                  <el-option
                    :value="item.id"
                    v-for="(item, index) in labelLists"
                    :key="index"
                    :label="item.name"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="国家：">
                <el-select
                  v-model="userFrom.country"
                  placeholder="请选择"
                  class="selWidth"
                  clearable
                  @on-change="changeCountry"
                >
                  <el-option value="" label="全部"></el-option>
                  <el-option value="CN" label="中国"></el-option>
                  <el-option value="OTHER" label="国外"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="省份：">
                <el-cascader
                  :options="addresData"
                  :props="propsCity"
                  filterable
                  v-model="address"
                  @change="handleChange"
                  clearable
                  class="selWidth"
                ></el-cascader>
              </el-form-item>
              <el-form-item label="消费情况：">
                <el-select v-model="userFrom.payCount" placeholder="请选择" class="selWidth" clearable>
                  <el-option value="" label="全部"></el-option>
                  <el-option value="0" label="0"></el-option>
                  <el-option value="1" label="1+"></el-option>
                  <el-option value="2" label="2+"></el-option>
                  <el-option value="3" label="3+"></el-option>
                  <el-option value="4" label="4+"></el-option>
                  <el-option value="5" label="5+"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="时间选择：">
                <el-date-picker
                  v-model="timeVal"
                  align="right"
                  unlink-panels
                  value-format="yyyy-MM-dd"
                  format="yyyy-MM-dd"
                  size="small"
                  type="daterange"
                  placement="bottom-end"
                  placeholder="自定义时间"
                  class="selWidth"
                  @change="onchangeTime"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                />
              </el-form-item>
              <el-form-item label="访问情况：">
                <el-select v-model="userFrom.accessType" placeholder="请选择" class="selWidth" clearable>
                  <el-option :value="0" label="全部"></el-option>
                  <el-option :value="1" label="首次访问"></el-option>
                  <el-option :value="2" label="时间段访问过"></el-option>
                  <el-option :value="3" label="时间段未访问"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="性别：">
                <el-select v-model="userFrom.sex" placeholder="请选择" class="selWidth">
                  <el-option :value="''" label="全部"></el-option>
                  <el-option :value="0" label="未知"></el-option>
                  <el-option :value="1" label="男"></el-option>
                  <el-option :value="2" label="女"></el-option>
                  <el-option :value="3" label="保密"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="身份：">
                <el-select v-model="userFrom.isPromoter" placeholder="请选择" class="selWidth">
                  <el-option :value="''" label="全部"></el-option>
                  <el-option :value="1" label="推广员"></el-option>
                  <el-option :value="0" label="普通用户"></el-option>
                </el-select>
              </el-form-item>
            </div>
            <el-form-item class="search-form-sub-bottom">
              <el-button type="primary" icon="ios-search" label="default" @click="userSearchs">搜索</el-button>
              <el-button class="ResetSearch mr14" @click="reset('userFrom')" size="small">重置</el-button>
              <a class="ivu-ml-8" @click="collapse = !collapse">
                <template v-if="!collapse"> 展开 <i class="el-icon-arrow-down"></i> </template>
                <template v-else> 收起 <i class="el-icon-arrow-up"></i> </template>
              </a>
            </el-form-item>
          </div>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <div slot="header" class="clearfix">
        <el-tabs v-model="loginType" @tab-click="getList(1)">
          <el-tab-pane :label="item.name" :name="item.type.toString()" v-for="(item, index) in headeNum" :key="index" />
        </el-tabs>
        <div>
          <el-button @click="onSend" type="primary" v-hasPermi="['admin:coupon:user:receive']">发送优惠券</el-button>
          <el-button :disabled="!selectionList.length" @click="setBatch('group')" v-hasPermi="['admin:user:group']"
            >批量设置分组</el-button
          >
          <el-button :disabled="!selectionList.length" @click="setBatch('label')" v-hasPermi="['admin:user:tag']"
            >批量设置标签</el-button
          >
        </div>
      </div>
      <el-table
        ref="table"
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        @selection-change="onSelectTab"
        highlight-current-row
      >
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" inline class="demo-table-expand">
              <el-form-item label="身份：">
                <span>{{ props.row.isPromoter | filterIsPromoter }}</span>
              </el-form-item>
              <el-form-item label="首次访问：">
                <span>{{ props.row.createTime | filterEmpty }}</span>
              </el-form-item>
              <el-form-item label="近次访问：">
                <span>{{ props.row.lastLoginTime | filterEmpty }}</span>
              </el-form-item>
              <el-form-item label="手机号：">
                <span>{{ props.row.phone | filterEmpty }}</span>
              </el-form-item>
              <el-form-item label="标签：">
                <span>{{ props.row.tagName | filterEmpty }}</span>
              </el-form-item>
              <el-form-item label="地址：">
                <span>{{ props.row.addres | filterEmpty }}</span>
              </el-form-item>
              <el-form-item label="备注：" style="width: 100%; display: flex; margin-right: 10px">
                <span>{{ props.row.mark | filterEmpty }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column type="selection" width="55"> </el-table-column>
        <el-table-column prop="uid" label="ID" min-width="80" v-if="checkedCities.includes('ID')" />
        <el-table-column label="头像" min-width="80" v-if="checkedCities.includes('头像')">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.avatar"
                :preview-src-list="[scope.row.avatar]"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="姓名" min-width="160" v-if="checkedCities.includes('姓名')">
          <template slot-scope="scope">
            <span>{{ scope.row.nickname | filterEmpty }} | {{ scope.row.sex | sexFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column label="用户等级" min-width="100" v-if="checkedCities.includes('用户等级')">
          <template slot-scope="scope">
            <span>{{ scope.row.level | levelFilter | filterEmpty }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="groupName" label="分组" min-width="100" v-if="checkedCities.includes('分组')">
          <template slot-scope="scope">
            <span>{{ scope.row.groupName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="spreadNickname" label="推荐人" min-width="130" v-if="checkedCities.includes('推荐人')" />
        <el-table-column label="手机号" min-width="100" v-if="checkedCities.includes('手机号')">
          <template slot-scope="scope">
            <span>{{ scope.row.phone | filterEmpty }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="nowMoney" label="余额" min-width="100" v-if="checkedCities.includes('余额')" />
        <el-table-column prop="integral" label="积分" min-width="100" v-if="checkedCities.includes('积分')" />
        <el-table-column label="操作" width="160" fixed="right" :render-header="renderHeader">
          <template slot-scope="scope">
            <a @click="onDetails(scope.row.uid)" v-hasPermi="['admin:user:topdetail']">详情</a>
            <el-divider direction="vertical"></el-divider>
            <a @click="editUser(scope.row.uid)" v-hasPermi="['admin:user:infobycondition']">编辑</a>
            <el-divider direction="vertical"></el-divider>
            <el-dropdown trigger="click">
              <span class="el-dropdown-link"> 更多<i class="el-icon-arrow-down el-icon--right" /> </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  @click.native="editPoint(scope.row.uid)"
                  v-if="checkPermi(['admin:user:operate:founds'])"
                  >积分余额</el-dropdown-item
                >
                <el-dropdown-item @click.native="setBatch('group', scope.row)" v-if="checkPermi(['admin:user:group'])"
                  >设置分组</el-dropdown-item
                >
                <el-dropdown-item @click.native="setBatch('label', scope.row)" v-if="checkPermi(['admin:user:tag'])"
                  >设置标签</el-dropdown-item
                >
                <el-dropdown-item @click.native="setPhone(scope.row)" v-if="checkPermi(['admin:user:update:phone'])"
                  >修改手机号</el-dropdown-item
                >
                <el-dropdown-item
                  @click.native="onLevel(scope.row.uid, scope.row.level)"
                  v-if="checkPermi(['admin:user:update:level'])"
                  >修改用户等级</el-dropdown-item
                >
                <el-dropdown-item
                  @click.native="setExtension(scope.row)"
                  v-if="checkPermi(['admin:user:update:spread'])"
                  >修改上级推广人</el-dropdown-item
                >
                <el-dropdown-item
                  @click.native="clearSpread(scope.row)"
                  v-if="scope.row.spreadUid && scope.row.spreadUid > 0 && checkPermi(['admin:retail:spread:clean'])"
                  >清除上级推广人</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[20, 40, 60, 80]"
          :page-size="userFrom.limit"
          :current-page="userFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
          background
        />
      </div>
    </el-card>
    <div class="card_abs" v-show="card_select_show" :style="{ top: collapse ? 570 + 'px' : 270 + 'px' }">
      <template>
        <div class="cell_ht">
          <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange"
            >全选</el-checkbox
          >
          <el-button type="text" @click="checkSave()">保存</el-button>
        </div>
        <el-checkbox-group v-model="checkedCities" @change="handleCheckedCitiesChange">
          <el-checkbox v-for="item in columnData" :label="item" :key="item" class="check_cell">{{ item }}</el-checkbox>
        </el-checkbox-group>
      </template>
    </div>
    <!--修改推广人-->
    <el-dialog title="修改推广人" :visible.sync="extensionVisible" width="540px" :before-close="handleCloseExtension">
      <el-form
        class="formExtension"
        ref="formExtension"
        :model="formExtension"
        :rules="ruleInline"
        label-width="75px"
        @submit.native.prevent
        v-loading="loading"
      >
        <el-form-item label="用户头像：" prop="image">
          <div class="upLoadPicBox" @click="modalPicTap">
            <div v-if="formExtension.image" class="pictrue"><img :src="formExtension.image" /></div>
            <div v-else class="upLoad">
              <i class="el-icon-camera cameraIconfont" />
            </div>
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="extensionVisible = false">取消</el-button>
        <el-button type="primary" @click="onSubExtension('formExtension')">确定</el-button>
      </span>
    </el-dialog>
    <!--用户列表-->
    <el-dialog class="user-dialog" title="用户列表" :visible.sync="userVisible" width="900px">
      <user-list @closeDialog="userVisible = false" v-if="userVisible" @getTemplateRow="getTemplateRow"></user-list>
    </el-dialog>
    <!--批量设置-->
    <el-dialog title="设置" :visible.sync="dialogVisible" width="540px" :before-close="handleClose">
      <el-form
        :model="dynamicValidateForm"
        ref="dynamicValidateForm"
        label-width="75px"
        class="demo-dynamic"
        v-loading="loading"
      >
        <el-form-item
          prop="groupId"
          label="用户分组："
          :rules="[{ required: true, message: '请选择用户分组', trigger: 'change' }]"
          v-if="batchName === 'group'"
          key="1"
        >
          <el-select v-model="dynamicValidateForm.groupId" placeholder="请选择分组" style="width: 100%" filterable>
            <el-option
              :value="item.id"
              v-for="(item, index) in groupList"
              :key="index"
              :label="item.groupName"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          prop="groupId"
          label="用户标签："
          :rules="[{ required: true, message: '请选择用户标签', trigger: 'change' }]"
          v-else
        >
          <el-select
            v-model="dynamicValidateForm.groupId"
            placeholder="请选择标签"
            style="width: 100%"
            multiple
            filterable
          >
            <el-option :value="item.id" v-for="(item, index) in labelLists" :key="index" :label="item.name"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="submitForm('dynamicValidateForm')">确定</el-button>
      </span>
    </el-dialog>
    <!--编辑-->
    <el-dialog title="编辑" :visible.sync="visible" width="900px">
      <edit-from v-if="visible" :uid="uid" @resetForm="resetForm"></edit-from>
    </el-dialog>
    <!--积分余额-->
    <el-dialog
      title="积分余额"
      :visible.sync="VisiblePoint"
      width="540px"
      :close-on-click-modal="false"
      :before-close="handlePointClose"
    >
      <el-form
        :model="PointValidateForm"
        ref="PointValidateForm"
        label-width="80px"
        class="demo-dynamic"
        v-loading="loadingPoint"
      >
        <el-form-item label="修改余额：" required>
          <el-radio-group v-model="PointValidateForm.moneyType">
            <el-radio :label="1">增加</el-radio>
            <el-radio :label="2">减少</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="余额：" required>
          <el-input-number
            controls-position="right"
            type="text"
            v-model="PointValidateForm.moneyValue"
            :precision="2"
            :step="0.1"
            :min="0"
            :max="999999"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="修改积分：" required>
          <el-radio-group v-model="PointValidateForm.integralType">
            <el-radio :label="1">增加</el-radio>
            <el-radio :label="2">减少</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="积分：" required>
          <el-input-number
            controls-position="right"
            type="text"
            step-strictly
            v-model="PointValidateForm.integralValue"
            :min="0"
            :max="999999"
          ></el-input-number>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="handlePointClose">取消</el-button>
        <el-button type="primary" :loading="loadingBtn" @click="submitPointForm('PointValidateForm')">确定</el-button>
      </span>
    </el-dialog>
    <!--账户详情-->
    <user-details ref="userDetailFrom" :userNo="uid"></user-details>
    <!-- 用户等级 -->
    <el-dialog title="设置" :visible.sync="levelVisible" width="540px" :before-close="Close">
      <level-edit :levelInfo="levelInfo" :levelList="levelList"></level-edit>
    </el-dialog>
  </div>
</template>

<script>
import {
  userListApi,
  groupListApi,
  levelListApi,
  tagListApi,
  groupPiApi,
  tagPiApi,
  foundsApi,
  updateSpreadApi,
  updatePhoneApi,
} from '@/api/user';
import { spreadClearApi } from '@/api/distribution';
import editFrom from './edit';
import userDetails from './userDetails';
import levelEdit from './level';
import userList from '@/components/userList';
import * as logistics from '@/api/logistics.js';
import Cookies from 'js-cookie';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { Debounce } from '@/utils/validate';
export default {
  name: 'UserIndex',
  components: { editFrom, userDetails, userList, levelEdit },
  filters: {
    sexFilter(status) {
      const statusMap = {
        0: '未知',
        1: '男',
        2: '女',
        3: '保密',
      };
      return statusMap[status];
    },
  },
  data() {
    return {
      formExtension: {
        image: '',
        spreadUid: '',
        userId: '',
      },
      ruleInline: {},
      extensionVisible: false,
      userVisible: false,
      levelInfo: '',
      pickerOptions: this.$timeOptions,
      loadingBtn: false,
      PointValidateForm: {
        integralType: 2,
        integralValue: 0,
        moneyType: 2,
        moneyValue: 0,
        uid: '',
      },
      loadingPoint: false,
      VisiblePoint: false,
      visible: false,
      userIds: '',
      dialogVisible: false,
      levelVisible: false,
      levelData: [],
      groupData: [],
      labelData: [],
      selData: [],
      labelPosition: 'right',
      collapse: false,
      props: {
        children: 'child',
        label: 'name',
        value: 'name',
        emitPath: false,
      },
      propsCity: {
        children: 'child',
        label: 'name',
        value: 'name',
      },
      headeNum: [
        { type: '', name: '全部用户' },
        { type: 'wechat', name: '微信公众号用户' },
        { type: 'routine', name: '微信小程序用户' },
        { type: 'h5', name: 'H5用户' },
      ],
      listLoading: true,
      tableData: {
        data: [],
        total: 0,
      },
      loginType: '',
      userFrom: {
        labelId: '',
        userType: '',
        sex: '',
        isPromoter: '',
        country: '',
        payCount: '',
        accessType: 0,
        dateLimit: '',
        keywords: '',
        province: '',
        city: '',
        page: 1,
        limit: 20,
        level: '',
        groupId: '',
      },
      grid: {
        xl: 8,
        lg: 12,
        md: 12,
        sm: 24,
        xs: 24,
      },
      levelList: [],
      labelLists: [],
      groupList: [],
      selectedData: [],
      timeVal: [],
      addresData: [],
      dynamicValidateForm: {
        groupId: [],
      },
      loading: false,
      groupIdFrom: [],
      selectionList: [],
      batchName: '',
      uid: 0,
      keyNum: 0,
      address: [],
      multipleSelectionAll: [],
      idKey: 'uid',
      card_select_show: false,
      checkAll: false,
      checkedCities: ['ID', '头像', '姓名', '用户等级', '分组', '推荐人', '手机号', '余额', '积分'],
      columnData: ['ID', '头像', '姓名', '用户等级', '分组', '推荐人', '手机号', '余额', '积分'],
      isIndeterminate: true,
    };
  },
  activated() {
    this.userFrom.keywords = '';
    this.loginType = '0';
    this.getList(1);
  },
  mounted() {
    this.getList();
    this.groupLists();
    this.levelLists();
    this.getTagList();
    if (checkPermi(['admin:system:city:list:tree'])) this.getCityList();
  },
  methods: {
    checkPermi,
    setPhone(row) {
      this.$prompt('', '修改手机号', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputErrorMessage: '请输入修改手机号',
        inputType: 'text',
        inputValue: row.phone,
        inputPlaceholder: '请输入手机号',
        closeOnClickModal: false,
        inputValidator: (value) => {
          if (!value) return '请填写手机号';
        },
      })
        .then(({ value }) => {
          updatePhoneApi({ id: row.uid, phone: value }).then(() => {
            this.$message.success('编辑成功');
            this.getList();
          });
        })
        .catch(() => {
          this.$message.info('取消输入');
        });
    },
    // 清除
    clearSpread(row) {
      this.$modalSure('解除【' + row.nickname + '】的上级推广人吗').then(() => {
        spreadClearApi(row.uid).then((res) => {
          this.$message.success('清除成功');
          this.getList();
        });
      });
    },
    onSubExtension(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          updateSpreadApi(this.formExtension).then((res) => {
            this.$message.success('设置成功');
            this.extensionVisible = false;
            this.getList();
          });
        } else {
          return false;
        }
      });
    },
    getTemplateRow(row) {
      this.formExtension.image = row.avatar;
      this.formExtension.spreadUid = row.uid;
    },
    setExtension(row) {
      this.formExtension = {
        image: '',
        spreadUid: '',
        userId: row.uid,
      };
      this.extensionVisible = true;
    },
    handleCloseExtension() {
      this.extensionVisible = false;
    },
    modalPicTap() {
      this.userVisible = true;
    },
    resetForm() {
      this.visible = false;
    },
    reset(formName) {
      this.userFrom = {
        labelId: '',
        userType: '',
        sex: '',
        isPromoter: '',
        country: '',
        payCount: '',
        accessType: 0,
        dateLimit: '',
        keywords: '',
        province: '',
        city: '',
        page: 1,
        limit: 15,
        level: '',
        groupId: '',
      };
      this.address = [];
      this.levelData = [];
      this.groupData = [];
      this.labelData = [];
      this.timeVal = [];
      this.getList();
    },
    // 列表
    async getCityList() {
      let res = await logistics.cityListTree();
      //res.forEach((el, index) => {
      //     el.child.forEach((cel, j) => {
      //       delete cel.child
      //     })
      //   })
      this.addresData = res;
      // })
    },
    // 发送文章
    sendNews() {
      if (this.selectionList.length === 0) return this.$message.warning('请先选择用户');
      const _this = this;
      this.$modalArticle(function (row) {}, 'send');
    },
    // 发送优惠劵
    onSend() {
      if (this.selectionList.length === 0) return this.$message.warning('请选择要设置的用户');
      const _this = this;
      this.$modalCoupon(
        'send',
        (this.keyNum += 1),
        [],
        function (row) {
          _this.formValidate.give_coupon_ids = [];
          _this.couponData = [];
          row.map((item) => {
            _this.formValidate.give_coupon_ids.push(item.coupon_id);
            _this.couponData.push(item.title);
          });
          _this.selectionList = [];
        },
        this.userIds,
        'user',
      );
    },
    Close() {
      this.levelVisible = false;
    },
    // 账户详情
    onDetails(id) {
      this.uid = id;
      this.$refs.userDetailFrom.getUserDetail(id);
      this.$refs.userDetailFrom.dialogUserDetail = true;
    },
    // 等级
    onLevel(id, level) {
      var userLevel = new Object();
      this.levelList.forEach((item) => {
        if (item.id == level) {
          userLevel.gradeLevel = item.grade;
        }
      });
      userLevel.uid = id;
      userLevel.level = level;
      this.levelInfo = userLevel;
      this.levelVisible = true;
    },
    // 积分余额
    editPoint(id) {
      this.uid = id;
      this.VisiblePoint = true;
    },
    // 积分余额
    submitPointForm: Debounce(function (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.PointValidateForm.uid = this.uid;
          this.loadingBtn = true;
          foundsApi(this.PointValidateForm)
            .then((res) => {
              this.$message.success('设置成功');
              this.loadingBtn = false;
              this.handlePointClose();
              this.getList();
            })
            .catch(() => {
              this.loadingBtn = false;
            });
        } else {
          return false;
        }
      });
    }),
    // 积分余额
    handlePointClose() {
      this.VisiblePoint = false;
      this.PointValidateForm = {
        integralType: 2,
        integralValue: 0,
        moneyType: 2,
        moneyValue: 0,
        uid: '',
      };
    },
    editUser(id) {
      this.uid = id;
      this.visible = true;
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.loading = true;
          this.batchName === 'group'
            ? groupPiApi({ groupId: this.dynamicValidateForm.groupId, id: this.userIds })
                .then((res) => {
                  this.$message.success('设置成功');
                  this.loading = false;
                  this.handleClose();
                  this.getList();
                })
                .catch(() => {
                  this.loading = false;
                })
            : tagPiApi({ tagId: this.dynamicValidateForm.groupId.join(','), id: this.userIds })
                .then((res) => {
                  this.$message.success('设置成功');
                  this.loading = false;
                  this.handleClose();
                  this.getList();
                })
                .catch(() => {
                  this.loading = false;
                });
        } else {
          return false;
        }
      });
    },
    setBatch(name, row) {
      this.batchName = name;
      if (row) {
        this.userIds = row.uid;
        if (this.batchName === 'group') {
          this.dynamicValidateForm.groupId = row.groupId ? Number(row.groupId) : '';
        } else {
          this.dynamicValidateForm.groupId = row.tagId ? row.tagId.split(',').map(Number) : [];
        }
      } else {
        this.dynamicValidateForm.groupId = '';
      }
      if (this.multipleSelectionAll.length === 0 && !row) return this.$message.warning('请选择要设置的用户');
      this.dialogVisible = true;
    },
    handleClose() {
      this.dialogVisible = false;
      this.$refs['dynamicValidateForm'].resetFields();
    },
    // 全选
    onSelectTab(selection) {
      this.selectionList = selection;
      setTimeout(() => {
        this.changePageCoreRecordData();
        let data = [];
        if (this.multipleSelectionAll.length) {
          this.multipleSelectionAll.map((item) => {
            data.push(item.uid);
          });
          this.userIds = data.join(',');
        }
      }, 50);
    },
    // 搜索
    userSearchs() {
      this.userFrom.page = 1;
      this.getList();
    },
    // 选择国家
    changeCountry() {
      if (this.userFrom.country === 'OTHER' || !this.userFrom.country) {
        this.selectedData = [];
        this.userFrom.province = '';
        this.userFrom.city = '';
        this.address = [];
      }
    },
    // 选择地址
    handleChange(value) {
      this.userFrom.province = value[0];
      this.userFrom.city = value[1];
    },
    // 具体日期
    onchangeTime(e) {
      this.timeVal = e;
      this.userFrom.dateLimit = e ? this.timeVal.join(',') : '';
    },
    // 分组列表
    groupLists() {
      groupListApi({ page: 1, limit: 9999 }).then(async (res) => {
        this.groupList = res.list;
      });
    },
    //标签列表
    getTagList() {
      tagListApi({ page: 1, limit: 9999 }).then((res) => {
        this.labelLists = res.list;
      });
    },
    // 等级列表
    levelLists() {
      levelListApi().then(async (res) => {
        this.levelList = res;
        localStorage.setItem('single-admin-levelKey', JSON.stringify(res));
      });
    },
    // 列表
    getList(num) {
      this.listLoading = true;
      this.userFrom.page = num ? num : this.userFrom.page;
      this.userFrom.userType = this.loginType;
      if (this.loginType == 0) this.userFrom.userType = '';
      this.userFrom.level = this.levelData.join(',');
      this.userFrom.groupId = this.groupData.join(',');
      this.userFrom.labelId = this.labelData.join(',');
      userListApi(this.userFrom)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          this.$nextTick(function () {
            this.setSelectRow(); // 调用跨页选中方法
          });
          this.listLoading = false;
        })
        .catch(() => {
          this.listLoading = false;
        });
      this.checkedCities = this.$cache.local.has('user_stroge')
        ? this.$cache.local.getJSON('user_stroge')
        : this.checkedCities;
      this.$set(this, 'card_select_show', false);
    },
    // 设置选中的方法
    setSelectRow() {
      if (!this.multipleSelectionAll || this.multipleSelectionAll.length <= 0) {
        return;
      }
      // 标识当前行的唯一键的名称
      const idKey = this.idKey;
      const selectAllIds = [];
      this.multipleSelectionAll.forEach((row) => {
        selectAllIds.push(row[idKey]);
      });
      this.$refs.table.clearSelection();
      for (var i = 0; i < this.tableData.data.length; i++) {
        if (selectAllIds.indexOf(this.tableData.data[i][idKey]) >= 0) {
          // 设置选中，记住table组件需要使用ref="table"
          this.$refs.table.toggleRowSelection(this.tableData.data[i], true);
        }
      }
    },
    // 记忆选择核心方法
    changePageCoreRecordData() {
      // 标识当前行的唯一键的名称
      const idKey = this.idKey;
      const that = this;
      // 如果总记忆中还没有选择的数据，那么就直接取当前页选中的数据，不需要后面一系列计算
      if (this.multipleSelectionAll.length <= 0) {
        this.multipleSelectionAll = this.selectionList;
        return;
      }
      // 总选择里面的key集合
      const selectAllIds = [];
      this.multipleSelectionAll.forEach((row) => {
        selectAllIds.push(row[idKey]);
      });
      const selectIds = [];
      // 获取当前页选中的id
      this.selectionList.forEach((row) => {
        selectIds.push(row[idKey]);
        // 如果总选择里面不包含当前页选中的数据，那么就加入到总选择集合里
        if (selectAllIds.indexOf(row[idKey]) < 0) {
          that.multipleSelectionAll.push(row);
        }
      });
      const noSelectIds = [];
      // 得到当前页没有选中的id
      this.tableData.data.forEach((row) => {
        if (selectIds.indexOf(row[idKey]) < 0) {
          noSelectIds.push(row[idKey]);
        }
      });
      noSelectIds.forEach((uid) => {
        if (selectAllIds.indexOf(uid) >= 0) {
          for (let i = 0; i < that.multipleSelectionAll.length; i++) {
            if (that.multipleSelectionAll[i][idKey] == uid) {
              // 如果总选择中有未被选中的，那么就删除这条
              that.multipleSelectionAll.splice(i, 1);
              break;
            }
          }
        }
      });
    },
    pageChange(page) {
      this.changePageCoreRecordData();
      this.userFrom.page = page;
      this.getList();
    },
    handleSizeChange(val) {
      this.changePageCoreRecordData();
      this.userFrom.limit = val;
      this.getList();
    },
    // 删除
    handleDelete(id, idx) {
      this.$modalSure().then(() => {
        productDeleteApi(id).then(() => {
          this.$message.success('删除成功');
          this.getList();
        });
      });
    },
    onchangeIsShow(row) {
      row.isShow
        ? putOnShellApi(row.id)
            .then(() => {
              this.$message.success('上架成功');
              this.getList();
            })
            .catch(() => {
              row.isShow = !row.isShow;
            })
        : offShellApi(row.id)
            .then(() => {
              this.$message.success('下架成功');
              this.getList();
            })
            .catch(() => {
              row.isShow = !row.isShow;
            });
    },
    renderHeader(h) {
      return (
        <p>
          <span style="padding-right:5px;">操作</span>
          <i class="el-icon-setting" onClick={() => this.handleAddItem()}></i>
        </p>
      );
    },
    handleAddItem() {
      if (this.card_select_show) {
        this.$set(this, 'card_select_show', false);
      } else if (!this.card_select_show) {
        this.$set(this, 'card_select_show', true);
      }
    },
    handleCheckAllChange(val) {
      this.checkedCities = val ? this.columnData : [];
      this.isIndeterminate = false;
    },
    handleCheckedCitiesChange(value) {
      let checkedCount = value.length;
      this.checkAll = checkedCount === this.columnData.length;
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.columnData.length;
    },
    checkSave() {
      this.card_select_show = false;
      this.$modal.loading('正在保存到本地，请稍候...');
      this.$cache.local.setJSON('user_stroge', this.checkedCities);
      setTimeout(this.$modal.closeLoading(), 1000);
    },
  },
};
</script>

<style scoped lang="scss">
/*.timeBox{*/
/*width: 100%;*/
/*::v-deep.el-form-item__content{*/
/*width: 87% !important;*/
/*}*/
/*}*/
.el-dropdown-link {
  cursor: pointer;
  color: #409eff;
  font-size: 12px;
}

.el-icon-arrow-down {
  font-size: 12px;
}

.text-right {
  text-align: right;
}

.demo-table-expand {
  font-size: 0;
}

.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}

.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 33.33%;
}

.seachTiele {
  line-height: 30px;
}

.container {
  min-width: 821px;

  ::v-deepel-form-item {
    width: 100%;
  }

  ::v-deepel-form-item__content {
    width: 72%;
  }
}

.ivu-ml-8 {
  font-size: 12px;
  color: var(--prev-color-primary) !important;
}

.relative {
  position: relative;
}

.card_abs {
  position: absolute;
  padding-bottom: 15px;
  right: 40px;
  width: 200px;
  background: #fff;
  z-index: 99999;
  box-shadow: 0px 0px 14px 0px rgba(0, 0, 0, 0.1);
}

.cell_ht {
  height: 50px;
  padding: 15px 20px;
  box-sizing: border-box;
  border-bottom: 1px solid #eeeeee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.check_cell {
  width: 100%;
  padding: 15px 20px 0;
}

::v-deep .el-checkbox__input.is-checked + .el-checkbox__label {
  color: #606266;
}
::v-deep .user-dialog .el-dialog__body {
  padding: 0;
  height: 600px;
}
.search-box {
  position: absolute;
  top: 20px;
  right: 0;
}
::v-deep .search-form {
  position: relative;
}
.search-form-sub-bottom {
  position: absolute;
  right: 0;
  bottom: 0;
}
</style>
