<template>
  <div class="divBox">
    <el-card class="box-card"  v-loading="loadingAll">
      <el-alert
        :closable="false"
        title="你可用该标题的模板搭配不同的关键词使用，配置提交后关键词种类和顺序将不能修改"
        type="warning">
      </el-alert>
      <el-divider></el-divider>
      <el-row>
        <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
          <div class="tmplmsg-box">
            <div class="tmplmsg-preview">
              <div class="tmplmsg-preview-title mb35" v-text="form.title"></div>
              <div class="acea-row row-middle tmplmsg-preview-cont mb10" v-for="(item, index) in KeywordCheck" :key="item.kid">
                <label v-text="item.name"></label>
                <span v-text="item.example"></span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="24" :md="16" :lg="16" :xl="16">
          <div class="tmplmsg-form">
            <el-form ref="form" :model="form" :rules="rules" label-width="100px" size="mini">
              <el-form-item label="配置关键词" prop="checkList">
                <div class="tmplmsg-form-cont">
                  <el-checkbox-group v-model="form.checkList" :max="5" @change="handleChecked">
                    <el-checkbox :label="item.kid" v-for="item in KeywordList" :key="item.kid">{{item.name}}</el-checkbox>
                  </el-checkbox-group>
                </div>
              </el-form-item>
              <el-form-item :label=" '已选择(' + KeywordCheck.length + '/5)'">
                <span v-if="KeywordCheck.length ===0 ">请先从上方选择关键词</span>
                <div v-else class="tmplmsg-form-check">
                  <div class="tmplmsg-form-check-list mb10 acea-row row-between" v-for="(item, index) in KeywordCheck" :key="item.kid"
                       draggable="true"
                       @dragstart="handleDragStart($event, item)"
                       @dragover.prevent="handleDragOver($event, item)"
                       @dragover="handleDragEnter($event, item)"
                       @dragend="handleDragEnd($event, item)">
                    <span v-text="item.name"></span>
                    <i class="el-icon-close" @click="closeCheck(index)"></i>
                  </div>
                </div>
              </el-form-item>
              <el-form-item label="场景说明" prop="sceneDesc">
                <el-input
                  type="textarea"
                  :rows="2"
                  placeholder="请输入场景说明"
                  v-model="form.sceneDesc">
                </el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="loading" @click="onSubmit('form')">提交</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
  import { getWeChatKeywordsByTidApi, myTempSaveApi, publicTempInfoApi, myTempInfoApi, myTempUpdateApi } from '@/api/wxApi'
  export default {
    name: "creatPublicTemplate",
    data() {
      return {
        KeywordList: [],
        form: {
          checkList: [],
          kid: '',
          sceneDesc: '',
          tid: '',
          title: '',
          extra: ''
        },
        KeywordCheck: [],
        loading: false,
        loadingAll: false,
        rules: {
          sceneDesc: [
            { required: true, message: '请填写场景说明', trigger: 'blur' }
          ],
          checkList: [
            { type: 'array', required: true, message: '请至少选择一个关键词', trigger: 'change' }
          ]
        },
        tempRoute: {}
      }
    },
    created() {
      this.tempRoute = Object.assign({}, this.$route)
    },
    mounted() {
      this.getKeywordList()
      if( this.$route.params.id !== '0' )this.getTitle()
      if( this.$route.params.myId!== '0'){
        this.setTagsViewTitle()
        this.wxInfo()
      }
    },
    methods: {
      // 设置tab标题
      setTagsViewTitle() {
        const title = '编辑模板'
        const route = Object.assign({}, this.tempRoute, { title: `${title}-${this.$route.params.myId}` })
        this.$store.dispatch('tagsView/updateVisitedView', route)
      },
      handleChecked(val) {
        this.KeywordCheck = this.KeywordList.filter(item=> val.some(ele=>ele == item.kid))
      },
      onSubmit(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            let data = {
                kid: this.form.checkList.join(','),
                sceneDesc: this.form.sceneDesc,
                tid: this.$route.params.tid,
                title: this.form.title,
                extra: JSON.stringify(this.KeywordCheck)
            }
            this.loading = true
            this.$route.params.myId === '0' ? myTempSaveApi(data).then(res => {
              this.$message.success('提交成功')
              this.$router.push('/appSetting/publicRoutine/publicRoutineTemplate')
              this.loading = false
            }).catch(()=>{
              this.loading = false
            }) : myTempUpdateApi({id: this.$route.params.myId}, data).then(res => {
              this.$message.success('提交成功')
              this.$router.push('/appSetting/publicRoutine/routineTemplate')
              this.loading = false
            }).catch(()=>{
              this.loading = false
            })
          } else {
            return false;
          }
        });
      },
      closeCheck(i) {
        this.form.checkList.splice(i, 1)
      },
      // 详情
      wxInfo() {
        myTempInfoApi({ id: this.$route.params.myId}).then(res => {
          this.form = {
            checkList: res.kid.split(',').map(Number),
            sceneDesc: res.sceneDesc,
            tid: res.tid,
            title: res.title,
            extra: res.extra
          }
          this.KeywordCheck = JSON.parse(res.extra)
        })
      },
      // 标题
      getTitle() {
        publicTempInfoApi({ id: this.$route.params.id}).then(res => {
          this.form.title = res.title
        })
      },
      // 关键字列表
      getKeywordList() {
        this.loadingAll = true
        getWeChatKeywordsByTidApi({ tid: this.$route.params.tid}).then(res => {
          this.KeywordList = res
          for (let i=0;i<res; i++) {
            this.$set(this.form.checkList, i,res[i])
          }
          this.loadingAll = false
        }).catch(() => {
          this.loadingAll = false
        })
      },
      // 移动
      handleDragStart (e, item) {
        this.dragging = item;
      },
      handleDragEnd (e, item) {
        this.dragging = null
      },
      handleDragOver (e) {
        e.dataTransfer.dropEffect = 'move'
      },
      handleDragEnter (e, item) {
        e.dataTransfer.effectAllowed = 'move'
        if (item === this.dragging) {
          return
        }
        const newItems = [...this.KeywordCheck]
        const src = newItems.indexOf(this.dragging)
        const dst = newItems.indexOf(item)
        newItems.splice(dst, 0, ...newItems.splice(src, 1))
        this.KeywordCheck = newItems;
      }
    }
  }
</script>

<style scoped lang="scss">
.tmplmsg{
  &-box{
    border: 1px solid #E7E7EB;
    border-radius: 5px;
    width: 90%;
    min-width: 325px;
    margin-right: 30px;
  }
  &-preview{
    min-height: 230px;
    padding: 15px;
    &-title{
      font-size: 14px;
    }
    &-cont{
      font-size: 13px;
      label{
        width: 100px;
      }
    }
  }
  &-form{
    position: relative;
    width: 60%;
    height: auto;
    background: #f6f8f9;
    background-clip: padding-box;
    padding: 20px 20px;
    &-cont{
      width: 100%;
      height: auto;
      background: #fff;
      padding: 15px;
      max-height: 250px;
      overflow-y: auto;
      /deep/.el-checkbox{
        display: block !important;
      }
    }
    &-check{
      &-list{
        width: 100%;
        background: #fff;
        line-height: 37px;
        height: 37px;
        align-items: center;
        padding: 0 15px;
        box-sizing: border-box;
        cursor: pointer;
      }
      /deep/.el-alert--success{
        line-height: normal !important;
      }
    }
  }
  &-form::after {
    content:"";
    position: absolute;
    right: 100%;
    top: 26px;
    width: 0;
    height: 0;
    border-top: 13px solid transparent;
    border-right: 26px solid #f6f8f9;
    border-bottom: 13px solid transparent;
  }
}
</style>
