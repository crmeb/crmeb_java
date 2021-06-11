<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-row :gutter="30">
        <el-col v-bind="grid" class="left mb15 ml40">
          <div>
            <img class="top" src="@/assets/imgs/mobilehead.png">
            <img class="bottom" src="@/assets/imgs/mobilefoot.png">
            <div
              style="background: #F4F5F9; min-height: 438px; position: absolute;
         top: 63px; width: 320px; "
            />
            <div class="textbot">
              <div v-for="(item,indx) in list" :key="indx" class="li" :class="{active:item === formValidate}">
                <div>
                  <div class="add" @click="add(item,indx)">
                    <i class="el-icon-plus" />
                    <div class="arrow" />
                  </div>
                  <div class="tianjia">
                    <div
                      v-for="(j,index) in item.sub_button"
                      :key="index"
                      class="addadd menuBox"
                      :class="{active:j === formValidate}"
                      @click="gettem(j,index,indx)"
                    >
                      <el-tooltip class="item" effect="dark" :content="j.name" placement="top-start">
                        <el-button>{{ j.name || '二级菜单' }}</el-button>
                      </el-tooltip>
                    </div>
                  </div>
                </div>
                <div class="text menuBox" @click="gettem(item,indx,null)">
                  <el-tooltip class="item" effect="dark" :content="item.name" placement="top-start">
                    <el-button>{{ item.name || '一级菜单' }}</el-button>
                  </el-tooltip>
                </div>
              </div>
              <div v-show="list.length < 3" class="li">
                <div class="text" @click="addtext"><i class="el-icon-plus" /></div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xl="11" :lg="12" :md="22" :sm="22" :xs="22">
          <div v-if="checkedMenuId !== null">
            <div class="dividerTitle acea-row row-between row-bottom">
              <span class="title">菜单信息</span>
              <el-button slot="extra" size="small" type="danger" @click="deltMenus">删除</el-button>
              <el-divider />
            </div>
            <el-col :span="24" class="userAlert">
              <div class="box-card right">
                <el-alert
                  class="mb15"
                  title="已添加子菜单，仅可设置菜单名称"
                  type="success"
                  show-icon
                />
                <el-form ref="formValidate" :model="formValidate" :rules="ruleValidate" label-width="100px" class="mt20">
                  <el-form-item label="菜单名称" prop="name">
                    <el-input v-model="formValidate.name" placeholder="请填写菜单名称" class="spwidth" />
                  </el-form-item>
                  <el-form-item label="规则状态" prop="type">
                    <el-select v-model="formValidate.type" placeholder="请选择规则状态" class="spwidth">
                      <el-option value="click" label="关键字">关键字</el-option>
                      <el-option value="view" label="跳转网页">跳转网页</el-option>
                      <el-option value="miniprogram" label="小程序">小程序</el-option>
                    </el-select>
                  </el-form-item>
                  <div v-if="formValidate.type === 'click'">
                    <el-form-item label="关键字" prop="key">
                      <el-input v-model="formValidate.key" placeholder="请填写关键字" class="spwidth" />
                    </el-form-item>
                  </div>
                  <div v-if="formValidate.type === 'miniprogram'">
                    <el-form-item label="appid" prop="appid">
                      <el-input v-model="formValidate.appid" placeholder="请填写appid" class="spwidth" />
                    </el-form-item>
                    <el-form-item label="备用网页" prop="url">
                      <el-input v-model="formValidate.url" placeholder="请填写备用网页" class="spwidth" />
                    </el-form-item>
                    <el-form-item label="小程序路径" prop="pagepath">
                      <el-input v-model="formValidate.pagepath" placeholder="请填写小程序路径" class="spwidth" />
                    </el-form-item>
                  </div>
                  <div v-if="formValidate.type === 'view'">
                    <el-form-item label="跳转地址" prop="url">
                      <el-input v-model="formValidate.url" placeholder="请填写跳转地址" class="spwidth" />
                    </el-form-item>
                  </div>
                </el-form>
              </div>
            </el-col>
          </div>
          <el-col v-if="isTrue" :span="24">
            <el-button size="mini" type="primary" style="display: block;margin: 10px auto;" @click="submenus('formValidate')">保存并发布</el-button>
          </el-col>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
  import { wechatMenuApi, wechatMenuAddApi } from '@/api/wxApi'
  export default {
    name: 'WechatMenus',
    data() {
      return {
        grid: {
          xl: 8,
          lg: 8,
          md: 8,
          sm: 8,
          xs: 24
        },
        grid2: {
          xl: 16,
          lg: 16,
          md: 16,
          sm: 16,
          xs: 24
        },
        modal2: false,
        formValidate: {
          name: '',
          type: 'click',
          appid: '',
          url: '',
          key: '',
          pagepath: '',
          id: 0
        },
        ruleValidate: {
          name: [
            { required: true, message: '请填写菜单名称', trigger: 'blur' }
          ],
          key: [
            { required: true, message: '请填写关键字', trigger: 'blur' }
          ],
          appid: [
            { required: true, message: '请填写appid', trigger: 'blur' }
          ],
          pagepath: [
            { required: true, message: '请填写小程序路径', trigger: 'blur' }
          ],
          url: [
            { required: true, message: '请填写跳转地址', trigger: 'blur' }
          ],
          type: [
            { required: true, message: '请选择规则状态', trigger: 'change' }
          ]
        },
        parentMenuId: null,
        list: [],
        checkedMenuId: null,
        isTrue: false
      }
    },
    mounted() {
      this.getMenus()
      if (this.list.length) {
        this.formValidate = this.list[this.activeClass]
      } else {
        return this.formValidate
      }
    },
    methods: {
      // 添加一级字段函数
      defaultMenusData() {
        return {
          type: 'click',
          name: '',
          sub_button: []
        }
      },
      // 添加二级字段函数
      defaultChildData() {
        return {
          type: 'click',
          name: ''
        }
      },
      // 获取 菜单
      getMenus() {
        wechatMenuApi().then(async res => {
          const data = res.menu
          this.list = data.button
        })
      },
      // 点击保存提交
      submenus(name) {
        if (this.isTrue && !this.checkedMenuId && this.checkedMenuId !== 0) {
          this.putData()
        } else {
          this.$refs[name].validate((valid) => {
            if (valid) {
              this.putData()
            } else {
              if (!this.check()) return false
            }
          })
        }
      },
      // 新增data
      putData() {
        const data = {
          button: this.list
        }
        wechatMenuAddApi(data).then(async res => {
          this.$message.success('提交成功')
          this.checkedMenuId = null
          this.formValidate = {}
          this.isTrue = false
        })
      },
      // 点击元素
      gettem(item, index, pid) {
        this.checkedMenuId = index
        this.formValidate = item
        this.parentMenuId = pid
        this.isTrue = true
      },
      // 增加二级
      add(item, index) {
        if (!this.check()) return false
        if (item.sub_button.length < 5) {
          const data = this.defaultChildData()
          const id = item.sub_button.length
          item.sub_button.push(data)
          this.formValidate = data
          this.checkedMenuId = id
          this.parentMenuId = index
          this.isTrue = true
        }
      },
      // 增加一级
      addtext() {
        if (!this.check()) return false
        const data = this.defaultMenusData()
        const id = this.list.length
        this.list.push(data)
        this.formValidate = data
        this.checkedMenuId = id
        this.parentMenuId = null
        this.isTrue = true
      },
      // 判断函数
      check: function() {
        const reg = /[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\.?/
        if (this.checkedMenuId === null) return true
        if (!this.isTrue) return true
        if (!this.formValidate.name) {
          this.$message.warning('请输入按钮名称!')
          return false
        }
        if (this.formValidate.type === 'click' && !this.formValidate.key) {
          this.$message.warning('请输入关键字!')
          return false
        }
        if (this.formValidate.type === 'view' && !(reg.test(this.formValidate.url))) {
          this.$message.warning('请输入正确的跳转地址!')
          return false
        }
        if (this.formValidate.type === 'miniprogram' &&
          (!this.formValidate.appid ||
            !this.formValidate.pagepath ||
            !this.formValidate.url)) {
          this.$message.warning('请填写完整小程序配置!')
          return false
        }
        return true
      },
      // 删除
      deltMenus() {
        if (this.isTrue) {
          this.$modalSure().then(() => {
            this.del()
          })
        } else {
          this.$message.warning('请选择菜单!')
        }
      },
      // 确认删除
      del() {
        this.parentMenuId === null ? this.list.splice(this.checkedMenuId, 1) : this.list[this.parentMenuId].sub_button.splice(this.checkedMenuId, 1)
        this.parentMenuId = null
        this.formValidate = {
          name: '',
          type: 'click',
          appid: '',
          url: '',
          key: '',
          pagepath: '',
          id: 0
        }
        this.isTrue = false
        this.modal2 = false
        this.checkedMenuId = null
        this.$refs['formValidate'].resetFields()
        this.submenus('formValidate')
      }
    }
  }
</script>

<style scoped lang="scss">
  .menuBox{
    /deep/.el-button{
      border: none;
      background: bottom;
      padding: 0 !important;
      overflow:hidden;
      text-overflow:ellipsis;
      white-space:nowrap;
      width: 100%;
    }
  }
  *{
    -moz-user-select: none; /*火狐*/
    -webkit-user-select: none; /*webkit浏览器*/
    -ms-user-select: none; /*IE10*/
    -khtml-user-select: none; /*早期浏览器*/
    user-select: none;
  }
  .title{
    margin-bottom: -19px !important;
    padding-bottom: 17px !important;
  }
  .left {
    min-width: 390px;
    min-height: 550px;
    position: relative;
    padding-left: 40px;
  }

  .top {
    position: absolute;
    top: 0px;
  }

  .bottom {
    position: absolute;
    bottom: 0px;
  }

  .textbot {
    position: absolute;
    bottom: 0px;
    left: 59px;
    width: 100%;
  }
  .active {
    border: 1px solid #44B549 !important;
    color: #44B549 !important;
  }
  .li {
    float: left;
    width: 93px;
    line-height: 48px;
    border: 1px solid #E7E7EB;
    background: #FAFAFA;
    text-align: center;
    cursor: pointer;
    color: #999;
    position: relative;
  }
  .text{
    height: 50px;
    overflow:hidden;
    text-overflow:ellipsis;
    white-space:nowrap;
  }
  .text:hover {
    color: #000;
  }

  .add {
    position: absolute;
    bottom: 65px;
    width: 100%;
    line-height: 48px;
    border: 1px solid #E7E7EB;
    background: #FAFAFA;
  }
  .arrow {
    position: absolute;
    bottom: -16px;
    left: 36px;
    /* 圆角的位置需要细心调试哦 */
    width: 0;
    height: 0;
    font-size: 0;
    border: solid 8px;
    border-color:#fff #F4F5F9 #F4F5F9 #F4F5F9;
  }
  .tianjia {
    position: absolute;
    bottom: 115px;
    width: 100%;
    line-height: 48px;
    background: #FAFAFA;
  }
  .addadd {
    width: 100%;
    line-height: 48px;
    border: 1px solid #E7E7EB;
    background: #FAFAFA;
    height: 48px;
    padding: 0 5px;
    box-sizing: border-box;
  }
  .right {
    background: #fff;
    min-height: 400px;
  }
  .spwidth{
    width: 100%;
  }
</style>
