<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-tabs v-model="loginType" @tab-click="getList(1)">
          <el-tab-pane :label="item.name" :name="item.type.toString()" v-for="(item,index) in headeNum" :key="index"/>
        </el-tabs>
        <div class="container">
          <el-form inline size="small" :model="userFrom" ref="userFrom" :label-position="labelPosition"  label-width="100px">
            <el-row>
              <el-col :xs="24" :sm="24" :md="24" :lg="18" :xl="18">
                <el-col v-bind="grid">
                  <el-form-item label="用户搜索：">
                    <el-input v-model="userFrom.keywords" placeholder="请输入姓名" clearable  class="selWidth"/>
                  </el-form-item>
                </el-col>
                <!--<el-col :span="24">-->
                  <!--<el-col v-bind="grid">-->
                    <!--<el-form-item label="会员搜索：">-->
                      <!--<el-input v-model="userFrom.keywords" placeholder="请输入" clearable  class="selWidth"/>-->
                    <!--</el-form-item>-->
                  <!--</el-col>-->
                <!--</el-col>-->
              </el-col>
              <template v-if="collapse">
                <el-col  :xs="24" :sm="24" :md="24" :lg="18" :xl="18">
                  <el-col v-bind="grid">
                    <el-form-item label="用户等级：">
                      <el-select v-model="levelData" placeholder="请选择"  class="selWidth" clearable filterable multiple>
                        <el-option :value="item.id" v-for="(item, index) in levelList" :key="index" :label="item.name"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col v-bind="grid">
                    <el-form-item label="用户分组：">
                      <el-select v-model="groupData" placeholder="请选择"  class="selWidth" clearable filterable multiple>
                        <el-option :value="item.id" v-for="(item, index) in groupList" :key="index" :label="item.groupName"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col v-bind="grid">
                    <el-form-item label="用户标签：">
                      <el-select v-model="labelData" placeholder="请选择"  class="selWidth" clearable filterable multiple>
                        <el-option :value="item.id" v-for="(item, index) in labelLists" :key="index" :label="item.name"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-col>
                <el-col :xs="24" :sm="24" :md="24" :lg="18" :xl="18">
                  <el-col v-bind="grid">
                    <el-form-item label="国家：">
                      <el-select v-model="userFrom.country" placeholder="请选择"  class="selWidth" clearable @on-change="changeCountry">
                        <el-option value="" label="全部"></el-option>
                        <el-option value="CN" label="中国"></el-option>
                        <el-option value="OTHER" label="国外"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col v-bind="grid" v-if="userFrom.country ==='CN'">
                    <el-form-item label="省份：">
                      <el-cascader :options="addresData" :props="propsCity" filterable v-model="address" @change="handleChange" clearable  class="selWidth"></el-cascader>
                    </el-form-item>
                  </el-col>
                </el-col>
                <el-col :xs="24" :sm="24" :md="24" :lg="18" :xl="18">
                  <el-col v-bind="grid">
                    <el-form-item label="性别：">
                      <el-radio-group v-model="userFrom.sex" type="button"  class="selWidth">
                        <el-radio-button label="">
                          <span>全部</span>
                        </el-radio-button>
                        <el-radio-button label="0">
                          <span>未知</span>
                        </el-radio-button>
                        <el-radio-button label="1">
                          <span>男</span>
                        </el-radio-button>
                        <el-radio-button label="2">
                          <span>女</span>
                        </el-radio-button>
                        <el-radio-button label="3">
                          <span>保密</span>
                        </el-radio-button>
                      </el-radio-group>
                    </el-form-item>
                  </el-col>
                  <el-col v-bind="grid">
                    <el-form-item label="身份：">
                      <el-radio-group v-model="userFrom.isPromoter" type="button"  class="selWidth">
                        <el-radio-button label="">
                          <span>全部</span>
                        </el-radio-button>
                        <el-radio-button label="1">
                          <span>推广员</span>
                        </el-radio-button>
                        <el-radio-button label="0">
                          <span>普通用户</span>
                        </el-radio-button>
                      </el-radio-group>
                    </el-form-item>
                  </el-col>
                </el-col>
                <el-col :xs="24" :sm="24" :md="24" :lg="18" :xl="18">
                  <el-col v-bind="grid">
                    <el-form-item label="访问情况：">
                      <el-select v-model="userFrom.accessType" placeholder="请选择"  class="selWidth" clearable>
                        <el-option :value="0" label="全部"></el-option>
                        <el-option :value="1" label="首次访问"></el-option>
                        <el-option :value="2" label="时间段访问过"></el-option>
                        <el-option :value="3" label="时间段未访问"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col v-bind="grid">
                    <el-form-item label="消费情况：">
                      <el-select v-model="userFrom.payCount" placeholder="请选择"  class="selWidth" clearable>
                        <el-option value="" label="全部"></el-option>
                        <el-option value="0" label="0"></el-option>
                        <el-option value="1" label="1+"></el-option>
                        <el-option value="2" label="2+"></el-option>
                        <el-option value="3" label="3+"></el-option>
                        <el-option value="4" label="4+"></el-option>
                        <el-option value="5" label="5+"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-col>
                <el-col :xs="24" :sm="24" :md="24" :lg="18" :xl="18">
                  <el-col v-bind="grid">
                    <el-form-item label="时间选择：" class="timeBox">
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
                        :picker-options="pickerOptions"
                        @change="onchangeTime"
                      />
                    </el-form-item>
                  </el-col>
                </el-col>
              </template>
              <el-col  :xs="24" :sm="24" :md="24" :lg="6" :xl="6" class="text-right userFrom">
                <el-form-item>
                  <el-button type="primary" icon="ios-search" label="default" class="mr15" size="small"   @click="userSearchs">搜索</el-button>
                  <el-button class="ResetSearch mr10" @click="reset('userFrom')"  size="small">重置</el-button>
                  <a class="ivu-ml-8" @click="collapse = !collapse">
                    <template v-if="!collapse">
                      展开 <i class="el-icon-arrow-down"></i>
                    </template>
                    <template v-else>
                      收起 <i class="el-icon-arrow-up"></i>
                    </template>
                  </a>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
        <el-button class="mr10" size="small" @click="onSend">发送优惠券</el-button>
        <!--<el-button v-show="loginType === 'wechat'" size="mini" class="mr10" @click="sendNews">发送文章</el-button>-->
        <el-button class="mr10" size="small" @click="setBatch('group')">批量设置分组</el-button>
        <el-button class="mr10" size="small" @click="setBatch('label')">批量设置标签</el-button>
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
                <span>{{ props.row.updateTime | filterEmpty }}</span>
              </el-form-item>
              <!--<el-form-item label="身份证号：">-->
                <!--<span>{{ props.row.cardId | filterEmpty }}</span>-->
              <!--</el-form-item>-->
              <el-form-item label="手机号：">
                <span>{{ props.row.phone | filterEmpty }}</span>
              </el-form-item>
              <!--<el-form-item label="真实姓名：">-->
                <!--<span>{{ props.row.realName | filterEmpty }}</span>-->
              <!--</el-form-item>-->
              <el-form-item label="标签：">
                <span>{{ props.row.tagName | filterEmpty }}</span>
              </el-form-item>
              <!--<el-form-item label="生日：">-->
                <!--<span>{{ props.row.birthday | filterEmpty }}</span>-->
              <!--</el-form-item>-->
              <el-form-item label="地址：">
                <span>{{ props.row.addres | filterEmpty }}</span>
              </el-form-item>
              <el-form-item label="备注：" style="width: 100%;display: flex;margin-right: 10px;">
                <span>{{ props.row.mark  | filterEmpty}}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column
          type="selection"
          width="55">
        </el-table-column>
        <el-table-column
          prop="uid"
          label="ID"
          min-width="80"
        />
        <el-table-column label="头像" min-width="80">
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
        <el-table-column
          label="姓名"
          min-width="130"
        >
          <template slot-scope="scope">
            <span>{{scope.row.nickname | filterEmpty  }} | {{scope.row.sex | sexFilter}}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="用户等级"
          min-width="100"
        >
          <template slot-scope="scope">
            <span>{{scope.row.level | levelFilter | filterEmpty}}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="groupName"
          label="分组"
          min-width="100"
        />
        <el-table-column
          prop="spreadNickname"
          label="推荐人"
          min-width="130"
        />
        <el-table-column
          label="手机号"
          min-width="100"
        >
          <template slot-scope="scope">
            <span>{{scope.row.phone | filterEmpty}}</span>
          </template>
        </el-table-column>
        <!--<el-table-column-->
          <!--label="用户类型"-->
          <!--min-width="100"-->
        <!--&gt;-->
          <!--<template slot-scope="scope">-->
            <!--<span>{{scope.row.userType | typeFilter}}</span>-->
          <!--</template>-->
        <!--</el-table-column>-->
        <el-table-column
          prop="nowMoney"
          label="余额"
          min-width="100"
        />
        <el-table-column
          prop="integral"
          label="积分"
          min-width="100"
        />
        <el-table-column label="操作" min-width="130" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button type="text" @click="editUser(scope.row.uid)" size="small">编辑</el-button>
            <el-dropdown trigger="click">
              <span class="el-dropdown-link">
                更多<i class="el-icon-arrow-down el-icon--right" />
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="onDetails(scope.row.uid)">账户详情</el-dropdown-item>
                <el-dropdown-item @click.native="editPoint(scope.row.uid)">积分余额</el-dropdown-item>
                <el-dropdown-item @click.native="setBatch('group',scope.row)">设置分组</el-dropdown-item>
                <el-dropdown-item @click.native="setBatch('label',scope.row)">设置标签</el-dropdown-item>
                <el-dropdown-item @click.native="setPhone(scope.row)">修改手机号</el-dropdown-item>
                <el-dropdown-item @click.native="setExtension(scope.row)">修改上级推广人</el-dropdown-item>
                <el-dropdown-item @click.native="clearSpread(scope.row)" v-if="scope.row.spreadUid && scope.row.spreadUid>0">清除上级推广人</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
        </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[15, 30, 45, 60]"
          :page-size="userFrom.limit"
          :current-page="userFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
        />
      </div>
    </el-card>
    <!--修改推广人-->
    <el-dialog
      title="修改推广人"
      :visible.sync="extensionVisible"
      width="500px"
      :before-close="handleCloseExtension">
      <el-form class="formExtension mt20" ref="formExtension" :model="formExtension" :rules="ruleInline" label-width="120px"
               @submit.native.prevent v-loading="loading">
        <el-form-item label="用户头像：" prop="image">
          <div class="upLoadPicBox" @click="modalPicTap">
            <div v-if="formExtension.image" class="pictrue"><img :src="formExtension.image"></div>
            <div v-else class="upLoad">
              <i class="el-icon-camera cameraIconfont"/>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="onSubExtension('formExtension')">确 定</el-button>
      </span>
    </el-dialog>
    <!--用户列表-->
    <el-dialog
      title="用户列表"
      :visible.sync="userVisible"
      width="900px">
      <user-list v-if="userVisible" @getTemplateRow="getTemplateRow"></user-list>
      <span slot="footer" class="dialog-footer">
        <el-button @click="userVisible = false">取 消</el-button>
        <el-button type="primary" @click="userVisible = false">确 定</el-button>
      </span>
    </el-dialog>
    <!--批量设置-->
    <el-dialog
      title="设置"
      :visible.sync="dialogVisible"
      width="500px"
      :before-close="handleClose">
      <el-form :model="dynamicValidateForm" ref="dynamicValidateForm" label-width="100px" class="demo-dynamic" v-loading="loading">
        <el-form-item
          prop="groupId"
          label="用户分组"
          :rules="[{ required: true, message: '请选择用户分组', trigger: 'change' }]"
          v-if="batchName ==='group'"
          key="1"
        >
          <el-select v-model="dynamicValidateForm.groupId" placeholder="请选择分组" style="width: 80%" filterable>
            <el-option :value="item.id" v-for="(item, index) in groupList" :key="index" :label="item.groupName"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          prop="groupId"
          label="用户标签"
          :rules="[{ required: true, message: '请选择用户标签', trigger: 'change' }]"
          v-else
        >
          <el-select v-model="dynamicValidateForm.groupId" placeholder="请选择标签" style="width: 80%" filterable multiple>
            <el-option :value="item.id" v-for="(item, index) in labelLists" :key="index" :label="item.name"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="handleClose">取 消</el-button>
        <el-button type="primary" @click="submitForm('dynamicValidateForm')">确 定</el-button>
      </span>
    </el-dialog>
    <!--编辑-->
    <el-dialog
      title="编辑"
      :visible.sync="visible"
      width="600px"
    >
      <edit-from v-if="visible" :uid="uid" @resetForm="resetForm"></edit-from>
    </el-dialog>
    <!--积分余额-->
    <el-dialog
      title="积分余额"
      :visible.sync="VisiblePoint"
      width="500px"
      :close-on-click-modal="false"
      :before-close="handlePointClose">
      <el-form :model="PointValidateForm" ref="PointValidateForm" label-width="100px" class="demo-dynamic" v-loading="loadingPoint">
        <el-form-item
          label="修改余额"
          required
        >
            <el-radio-group v-model="PointValidateForm.moneyType">
              <el-radio :label="1">增加</el-radio>
              <el-radio :label="2">减少</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item
          label="余额"
          required
        >
          <el-input-number type="text" v-model="PointValidateForm.moneyValue" :precision="2" :step="0.1" :min="0" :max="999999"></el-input-number>
        </el-form-item>
        <el-form-item
          label="修改积分"
          required
        >
          <el-radio-group v-model="PointValidateForm.integralType">
            <el-radio :label="1">增加</el-radio>
            <el-radio :label="2">减少</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          label="积分"
          required
        >
          <el-input-number type="text" step-strictly v-model="PointValidateForm.integralValue" :min="0" :max="999999"></el-input-number>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="handlePointClose">取 消</el-button>
        <el-button type="primary" :loading="loadingBtn" @click="submitPointForm('PointValidateForm')">确 定</el-button>
      </span>
    </el-dialog>
    <!--账户详情-->
    <el-dialog
      title="用户详情"
      :visible.sync="Visible"
      width="1100px"
      v-if="uid"
      :before-close="Close">
      <user-details ref="userDetails" :uid="uid" v-if="Visible"></user-details>
    </el-dialog>
  </div>
</template>

<script>
  import { userListApi, groupListApi, levelListApi, tagListApi, groupPiApi, tagPiApi, foundsApi, updateSpreadApi, updatePhoneApi } from '@/api/user'
  import { spreadClearApi } from '@/api/distribution'
  import editFrom from './edit'
  import userDetails from './userDetails'
  import userList from '@/components/userList'
  import * as logistics from '@/api/logistics.js'
  import Cookies from 'js-cookie'
  export default {
    name: 'UserIndex',
    components:{ editFrom, userDetails,userList },
    filters: {
      sexFilter(status) {
        const statusMap = {
          0: '未知',
          1: '男',
          2: '女',
          3: '保密'
        }
        return statusMap[status]
      }
    },
    data() {
      return {
        formExtension: {
          image: '',
          spreadUid: '',
          userId: ''
        },
        ruleInline: {},
        extensionVisible: false,
        userVisible: false,
        pickerOptions: {
          shortcuts: [
            {
              text: '今天',
              onClick(picker) {
                const end = new Date()
                const start = new Date()
                start.setTime(new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate()))
                picker.$emit('pick', [start, end])
              }
            },
            {
              text: '昨天',
              onClick(picker) {
                const end = new Date()
                const start = new Date()
                start.setTime(start.setTime(new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate() - 1)))
                end.setTime(end.setTime(new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate())))
                picker.$emit('pick', [start, end])
              }
            },
            {
              text: '最近7天',
              onClick(picker) {
                const end = new Date()
                const start = new Date()
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
                picker.$emit('pick', [start, end])
              }
            },
            {
              text: '最近30天',
              onClick(picker) {
                const end = new Date()
                const start = new Date()
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
                picker.$emit('pick', [start, end])
              }
            },
            {
              text: '本月',
              onClick(picker) {
                const end = new Date()
                const start = new Date()
                start.setTime(start.setTime(new Date(new Date().getFullYear(), new Date().getMonth(), 1)))
                picker.$emit('pick', [start, end])
              }
            },
            {
              text: '本年',
              onClick(picker) {
                const end = new Date()
                const start = new Date()
                start.setTime(start.setTime(new Date(new Date().getFullYear(), 0, 1)))
                picker.$emit('pick', [start, end])
              }
            }
          ]
        },
        loadingBtn: false,
        PointValidateForm: {
          integralType: 2,
          integralValue: 0,
          moneyType: 2,
          moneyValue: 0,
          uid: ''
        },
        loadingPoint: false,
        VisiblePoint: false,
        visible: false,
        userIds: '',
        dialogVisible: false,
        levelData: [],
        groupData: [],
        labelData: [],
        selData:[],
        labelPosition:'right',
        collapse: false,
        props: {
          children: 'child',
          label: 'name',
          value: 'name',
          emitPath: false
        },
        propsCity: {
          children: 'child',
          label: 'name',
          value: 'name'
        },
        headeNum: [
          { 'type': '', 'name': '全部用户' },
          { 'type': 'wechat', 'name': '微信公众号用户' },
          { 'type': 'routine', 'name': '微信小程序用户' },
          { 'type': 'h5', 'name': 'H5用户' }
        ],
        listLoading: true,
        tableData: {
          data: [],
          total: 0
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
          limit: 15,
          level: '',
          groupId: ''
        },
        grid: {
          xl: 8,
          lg: 12,
          md: 12,
          sm: 24,
          xs: 24
        },
        grid2: {
          xl: 18,
          lg: 16,
          md: 12,
          sm: 24,
          xs: 24
        },
        grid3: {
          xl: 8,
          lg: 12,
          md: 12,
          sm: 24,
          xs: 24
        },
        levelList: [],
        labelLists: [],
        groupList: [],
        selectedData: [],
        timeVal: [],
        addresData: [],
        dynamicValidateForm:{
          groupId: []
        },
        loading: false,
        groupIdFrom: [],
        selectionList: [],
        batchName: '',
        uid: 0,
        Visible: false,
        keyNum: 0,
        address: [],
        multipleSelectionAll: [],
        idKey:'uid'
      }
    },
    mounted() {
      this.getList()
      this.groupLists()
      this.levelLists()
      this.getTagList()
      this.getCityList()
    },
    methods: {
      setPhone(row) {
        this.$prompt('修改手机号', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputErrorMessage: '请输入修改手机号',
          inputType: 'text',
          inputValue: row.phone,
          inputPlaceholder: '请输入手机号',
          closeOnClickModal: false,
          inputValidator: (value) => {
            if (!value) return '请填写手机号'
            if (!/^1[3456789]\d{9}$/.test(value)) return '手机号格式不正确!'
            // if(!value) return '输入不能为空'
          }
        }).then(({value}) => {
          updatePhoneApi({id: row.uid,phone: value}).then(() => {
            this.$message.success('编辑成功')
            this.getList();
          })
        }).catch(() => {
          this.$message.info('取消输入')
        })
      },
      // 清除
      clearSpread(row) {
        this.$modalSure('解除【' + row.nickname + '】的上级推广人吗').then(() => {
          spreadClearApi(row.uid).then((res) => {
            this.$message.success('清除成功')
            this.getList()
          })
        })
      },
      onSubExtension(formName){
        this.$refs[formName].validate((valid) => {
          if (valid) {
            updateSpreadApi(this.formExtension).then(res => {
              this.$message.success('设置成功')
              this.extensionVisible = false
              this.getList()
            })
          } else {
            return false;
          }
        });
      },
      getTemplateRow(row){
         this.formExtension.image = row.avatar
         this.formExtension.spreadUid = row.uid
      },
      setExtension(row){
        this.formExtension = {
            image: '',
            spreadUid: '',
            userId: row.uid
        };
        this.extensionVisible = true
      },
      handleCloseExtension(){
        this.extensionVisible = false
      },
      modalPicTap(){
        this.userVisible = true
      },
      resetForm(){
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
            groupId: ''
        }
        this.levelData = []
        this.groupData = []
        this.labelData = []
        this.timeVal = []
        this.getList()
      },
      // 列表
      getCityList() {
        logistics.cityListTree().then(res => {
          res.forEach((el, index) => {
            el.child.forEach((cel, j) => {
              delete cel.child
            })
          })
          this.addresData = res
        })
      },
      // 发送文章
      sendNews() {
        if (this.selectionList.length === 0) return this.$message.warning('请先选择用户')
        const _this = this
        this.$modalArticle(function(row) {
        },'send')
      },
      // 发送优惠劵
      onSend(){
        if (this.selectionList.length === 0) return this.$message.warning('请选择要设置的用户');
        const _this = this
        this.$modalCoupon('send', this.keyNum += 1, [],function(row) {
          _this.formValidate.give_coupon_ids = []
          _this.couponData = []
          row.map((item) => {
            _this.formValidate.give_coupon_ids.push(item.coupon_id)
            _this.couponData.push(item.title)
          })
          _this.selectionList = []
        },this.userIds,'user')
      },
      Close() {
        this.Visible = false
      },
      // 账户详情
      onDetails(id){
        this.uid = id
        this.Visible = true
      },
      // 积分余额
      editPoint(id) {
        this.uid = id
        this.VisiblePoint = true
      },
      // 积分余额
      submitPointForm(formName){
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.PointValidateForm.uid = this.uid
            this.loadingBtn = true
            foundsApi(this.PointValidateForm).then(res => {
              this.$message.success('设置成功')
              this.loadingBtn = false
              this.handlePointClose()
              this.getList()
            }).catch(() => {
              this.loadingBtn = false
            })
          } else {
            return false
          }
        })
      },
      // 积分余额
      handlePointClose() {
        this.VisiblePoint = false
        this.PointValidateForm = {
          integralType: 2,
          integralValue: 0,
          moneyType: 2,
          moneyValue: 0,
          uid: ''
        }
      },
      editUser(id) {
        this.uid = id
        this.visible = true
      },
      submitForm(formName) {
        // let data = [];
        // if(!this.userIds){
        //   if(this.multipleSelectionAll.length){
        //     this.multipleSelectionAll.map((item) => {
        //       data.push(item.uid)
        //     });
        //     this.userIds = data.join(',');
        //   }
        // }
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.loading = true
            this.batchName ==='group' ? groupPiApi({groupId: this.dynamicValidateForm.groupId, id: this.userIds}).then(res => {
              this.$message.success('设置成功')
              this.loading = false
              this.handleClose()
              this.getList()
            }).catch(() => {
              this.loading = false
            }) : tagPiApi({tagId: this.dynamicValidateForm.groupId.join(','), id: this.userIds}).then(res => {
              this.$message.success('设置成功')
              this.loading = false
              this.handleClose()
              this.getList()
            }).catch(() => {
              this.loading = false
            })
          } else {
            return false;
          }
        });
      },
      setBatch(name, row){
        this.batchName = name
        if(row){
          this.userIds = row.uid
          if(this.batchName ==='group'){
            this.dynamicValidateForm.groupId = row.groupId?Number(row.groupId):''
          }else{
            this.dynamicValidateForm.groupId = row.tagId?row.tagId.split(',').map(Number):[]
          }
        }else{
          this.dynamicValidateForm.groupId = ''
        }
        if (this.multipleSelectionAll.length === 0 && !row) return this.$message.warning('请选择要设置的用户')
        this.dialogVisible = true
      },
      handleClose(){
        this.dialogVisible = false
        this.$refs['dynamicValidateForm'].resetFields();
      },
      // 全选
      onSelectTab (selection) {
        this.selectionList = selection;
        setTimeout(() => {
          this.changePageCoreRecordData()
          let data = [];
          if(this.multipleSelectionAll.length){
            this.multipleSelectionAll.map((item) => {
              data.push(item.uid)
            });
            this.userIds = data.join(',');
          }
        }, 50)
      },
      // 搜索
      userSearchs () {
        this.userFrom.page = 1;
        this.getList();
      },
      // 选择国家
      changeCountry () {
        if (this.userFrom.country === 'OTHER' || !this.userFrom.country) {
          this.selectedData = [];
          this.userFrom.province = '';
          this.userFrom.city = '';
          this.address = [];
        }
      },
      // 选择地址
      handleChange (value) {
        this.userFrom.province = value[0];
        this.userFrom.city = value[1];
      },
      // 具体日期
      onchangeTime (e) {
        this.timeVal = e;
        this.userFrom.dateLimit = e ? this.timeVal.join(',') : '';
      },
      // 分组列表
      groupLists () {
        groupListApi({ page: 1, limit: 9999}).then(async res => {
          this.groupList = res.list
        })
      },
      //标签列表
      getTagList () {
        tagListApi({ page: 1, limit: 9999}).then(res => {
          this.labelLists = res.list
        })
      },
      // 等级列表
      levelLists () {
        levelListApi({ page: 1, limit: 9999}).then(async res => {
          this.levelList = res.list
          localStorage.setItem('levelKey', JSON.stringify(res.list))
        })
      },
      // 列表
      getList(num) {
        this.listLoading = true
        this.userFrom.page = num ? num : this.userFrom.page;
        this.userFrom.userType = this.loginType
        if(this.loginType == 0) this.userFrom.userType =''
        this.userFrom.level = this.levelData.join(',')
        this.userFrom.groupId = this.groupData.join(',')
        this.userFrom.labelId = this.labelData.join(',')
        userListApi(this.userFrom).then(res => {
          this.tableData.data = res.list
          this.tableData.total = res.total
          this.$nextTick(function() {
            this.setSelectRow()// 调用跨页选中方法
          })
          this.listLoading = false
        }).catch(() => {
          this.listLoading = false
        })
      },
      // 设置选中的方法
      setSelectRow() {
        if (!this.multipleSelectionAll || this.multipleSelectionAll.length <= 0) {
          return
        }
        // 标识当前行的唯一键的名称
        const idKey = this.idKey
        const selectAllIds = []
        this.multipleSelectionAll.forEach(row => {
          selectAllIds.push(row[idKey])
        })
        this.$refs.table.clearSelection()
        for (var i = 0; i < this.tableData.data.length; i++) {
          if (selectAllIds.indexOf(this.tableData.data[i][idKey]) >= 0) {
            // 设置选中，记住table组件需要使用ref="table"
            this.$refs.table.toggleRowSelection(this.tableData.data[i], true)
          }
        }
      },
      // 记忆选择核心方法
      changePageCoreRecordData() {
        // 标识当前行的唯一键的名称
        const idKey = this.idKey
        const that = this
        // 如果总记忆中还没有选择的数据，那么就直接取当前页选中的数据，不需要后面一系列计算
        if (this.multipleSelectionAll.length <= 0) {
          this.multipleSelectionAll = this.selectionList
          return
        }
        // 总选择里面的key集合
        const selectAllIds = []
        this.multipleSelectionAll.forEach(row => {
          selectAllIds.push(row[idKey])
        })
        const selectIds = []
        // 获取当前页选中的id
        this.selectionList.forEach(row => {
          selectIds.push(row[idKey])
          // 如果总选择里面不包含当前页选中的数据，那么就加入到总选择集合里
          if (selectAllIds.indexOf(row[idKey]) < 0) {
            that.multipleSelectionAll.push(row)
          }
        })
        const noSelectIds = []
        // 得到当前页没有选中的id
        this.tableData.data.forEach(row => {
          if (selectIds.indexOf(row[idKey]) < 0) {
            noSelectIds.push(row[idKey])
          }
        })
        noSelectIds.forEach(uid => {
          if (selectAllIds.indexOf(uid) >= 0) {
            for (let i = 0; i < that.multipleSelectionAll.length; i++) {
              if (that.multipleSelectionAll[i][idKey] == uid) {
                // 如果总选择中有未被选中的，那么就删除这条
                that.multipleSelectionAll.splice(i, 1)
                break
              }
            }
          }
        })
      },
      pageChange(page) {
        this.changePageCoreRecordData()
        this.userFrom.page = page
        this.getList()
      },
      handleSizeChange(val) {
        this.changePageCoreRecordData()
        this.userFrom.limit = val
        this.getList()
      },
      // 删除
      handleDelete(id, idx) {
        this.$modalSure().then(() => {
          productDeleteApi(id).then(() => {
            this.$message.success('删除成功')
            this.getList()
          })
        })
      },
      onchangeIsShow(row) {
        row.isShow
          ? putOnShellApi( row.id ).then(() => {
            this.$message.success('上架成功')
            this.getList()
          }).catch(()=>{
            row.isShow = !row.isShow
          }) : offShellApi(row.id).then(() => {
            this.$message.success('下架成功')
            this.getList()
          }).catch(()=>{
            row.isShow = !row.isShow
          })
      }
    }
  }
</script>

<style scoped lang="scss">
  /*.timeBox{*/
    /*width: 100%;*/
    /*/deep/.el-form-item__content{*/
      /*width: 87% !important;*/
    /*}*/
  /*}*/
  .el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
    font-size: 12px;
  }
  .el-icon-arrow-down {
    font-size: 12px;
  }
  .text-right{
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
  .selWidth{
    width: 100% !important;
  }
  .seachTiele{
    line-height: 30px;
  }
  .container{
    min-width: 821px;
    /deep/.el-form-item{
      width: 100%;
    }
    /deep/.el-form-item__content{
      width: 72%;
    }
  }
  .ivu-ml-8{
    font-size: 12px;
    color: #1682e6;
  }
</style>
