<template>
  <div>
    <el-form ref="formDynamic" size="small" :model="formDynamic" v-loading="loading"  :rules="rules" class="attrFrom mb20" label-width="100px" @submit.native.prevent>
      <el-row :gutter="24">
        <el-col :span="8">
          <el-form-item label="规格名称：" prop="ruleName">
            <el-input  maxlength="20" v-model="formDynamic.ruleName" placeholder="请输入标题名称" />
          </el-form-item>
        </el-col>
        <el-col v-for="(item, index) in formDynamic.ruleValue" :key="index" :span="24" class="noForm">
          <el-form-item>
            <div class="acea-row row-middle"><span class="mr5">{{ item.value }}</span><i class="el-icon-circle-close" @click="handleRemove(index)" /></div>
            <div class="rulesBox">
              <el-tag
                v-for="(j, indexn) in item.detail"
                :key="indexn"
                closable
                size="medium"
                :disable-transitions="false"
                class="mb5 mr10"
                @close="handleClose(item.detail,indexn)"
              >
                {{ j }}
              </el-tag>
              <el-input
                v-if="item.inputVisible"
                ref="saveTagInput"
                v-model="item.detail.attrsVal"
                class="input-new-tag"
                size="small"
                @keyup.enter.native="createAttr(item.detail.attrsVal,index)"
                @blur="createAttr(item.detail.attrsVal,index)"
              />
              <el-button v-else class="button-new-tag" size="small" @click="showInput(item)">+ 添加</el-button>
            </div>
          </el-form-item>
        </el-col>
        <el-col v-if="isBtn" :span="24" class="mt10" style="padding-left: 0;padding-right: 0;">
          <el-col :span="8">
            <el-form-item label="规格：">
              <el-input v-model="attrsName" placeholder="请输入规格" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="规格值：">
              <el-input v-model="attrsVal" placeholder="请输入规格值" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-button type="primary" class="mr10" @click="createAttrName">确定</el-button>
            <el-button @click="offAttrName">取消</el-button>
          </el-col>
        </el-col>
        <Spin v-if="spinShow" size="large" fix />
      </el-row>
      <el-button v-if="!isBtn" type="primary" icon="md-add" class="ml40 mt10" @click="addBtn">添加新规格</el-button>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary"  @click="dialogFormVisible = false">确 定</el-button>
      </div>
    </el-form>
    <span class="footer acea-row">
      <el-button @click="resetForm('formDynamic')">取消</el-button>
      <el-button type="primary" :loading="loadingBtn" @click="handleSubmit('formDynamic')">确 定</el-button>
    </span>
  </div>
</template>

<script>
import { attrCreatApi, attrEditApi } from '@/api/store'
export default {
  name: 'CreatAttr',
  props: {
    currentRow: {
      type: Object,
      default: null
    },
    keyNum: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      loadingBtn: false,
      loading: false,
      dialogVisible: false,
      inputVisible: false,
      inputValue: '',
      spinShow: false,
      grid: {
        xl: 3,
        lg: 3,
        md: 12,
        sm: 24,
        xs: 24
      },
      modal: false,
      index: 1,
      rules: {
        ruleName: [
          { required: true, message: '请输入规格名称', trigger: 'blur' }
        ]
      },
      formDynamic: {
        ruleName: '',
        ruleValue: []
      },
      attrsName: '',
      attrsVal: '',
      isBtn: false,
      results: [],
      result: [],
      ids: 0
    }
  },
  watch: {
    currentRow: {
      handler: function(val, oldVal) {
        this.formDynamic = val
      },
      immediate: true
    },
    keyNum: {
      deep: true,
      handler(val) {
        if( val>0 ) this.clear()
      }
    }
  },
  mounted() {
    this.formDynamic.ruleValue.map(item => {
      this.$set(item, 'inputVisible', false)
    })
  },
  methods: {
    resetForm(formName) {
      this.$msgbox.close()
      this.clear()
      this.$refs[formName].resetFields()
    },
    // 添加按钮
    addBtn() {
      this.isBtn = true
    },
    handleClose(item, index) {
      item.splice(index, 1)
    },
    // 取消
    offAttrName() {
      this.isBtn = false
    },
    // 删除
    handleRemove(index) {
      this.formDynamic.ruleValue.splice(index, 1)
    },
    // 添加规则名称
    createAttrName() {
      if (this.attrsName && this.attrsVal) {
        const data = {
          value: this.attrsName,
          detail: [
            this.attrsVal
          ]
        }
        this.formDynamic.ruleValue.push(data)
        var hash = {}
        this.formDynamic.ruleValue = this.formDynamic.ruleValue.reduce(function(item, next) {
          /* eslint-disable */
            hash[next.value] ? '' : hash[next.value] = true && item.push(next);
            return item
          }, [])
          this.attrsName = '';
          this.attrsVal = '';
          this.isBtn = false;
        } else {
          this.$message.warning('请添加规格名称');
        }
      },
      // 添加属性
      createAttr (num, idx) {
        if (num) {
          this.formDynamic.ruleValue[idx].detail.push(num);
          var hash = {};
          this.formDynamic.ruleValue[idx].detail = this.formDynamic.ruleValue[idx].detail.reduce(function (item, next) {
            /* eslint-disable */
            hash[next] ? '' : hash[next] = true && item.push(next);
            return item
          }, [])
          this.formDynamic.ruleValue[idx].inputVisible = false
        } else {
          this.$message.warning('请添加属性');
        }
      },
    showInput(item) {
      this.$set(item, 'inputVisible', true)
    },
      // 提交
      handleSubmit (name) {
        const data={
          "id": this.currentRow.id || 0,
          "ruleName": this.formDynamic.ruleName,
          "ruleValue": JSON.stringify(this.formDynamic.ruleValue)
        }
        this.$refs[name].validate((valid) => {
          if (valid) {
            if (this.formDynamic.ruleValue.length === 0) {
              return this.$message.warning('请至少添加一条属性规格！');
            }
            this.loadingBtn = true
            this.loading = true
            setTimeout(() => {
              this.currentRow.id? attrEditApi(data).then(res => {
                this.$message.success('提交成功');
                this.$msgbox.close()
                this.clear();
                this.$emit('getList');
                this.loading = false
                this.loadingBtn = false
              }).catch(() => {
                this.loading = false
                this.loadingBtn = false
              }):attrCreatApi(data).then(res => {
                this.$message.success('提交成功');
                this.$msgbox.close()
                this.$emit('getList');
                this.clear();
                this.loading = false
                this.loadingBtn = false
              }).catch(() => {
                this.loading = false
                this.loadingBtn = false
              })
            }, 1200);
          } else {
            this.loading = false
            this.loadingBtn = false
            return false
          }
        })
      },
      clear () {
        this.$refs['formDynamic'].resetFields();
        this.formDynamic.ruleValue = [];
        this.formDynamic.ruleName=''
        this.isBtn = false;
        this.attrsName = '';
        this.attrsVal = '';
      },
      handleInputConfirm() {
        const inputValue = this.inputValue
        if (inputValue) {
          this.dynamicTags.push(inputValue)
        }
        this.inputVisible = false
        this.inputValue = ''
      }
    }
  }
</script>

<style scoped lang="scss">
  .button-new-tag {
    height: 28px;
    line-height: 26px;
    padding-top: 0;
    padding-bottom: 0;
  }
  .input-new-tag {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
  }
  .footer{
    justify-content: flex-end;
  }
</style>
