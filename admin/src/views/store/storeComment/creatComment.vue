<template>
  <el-form :model="formValidate" :rules="rules" ref="formValidate" label-width="100px" class="demo-formValidate"  v-loading="loading">
    <el-form-item label="商品：" prop="productId">
      <div class="upLoadPicBox" @click="changeGood">
        <div v-if="formValidate.productId" class="pictrue"><img :src="image"></div>
        <div v-else class="upLoad">
          <i class="el-icon-camera cameraIconfont"/>
        </div>
      </div>
    </el-form-item>
    <el-form-item label="用户名称：" prop="nickname">
      <el-input type="text" v-model="formValidate.nickname "></el-input>
    </el-form-item>
    <el-form-item label="评价文字：" prop="comment">
      <el-input type="textarea" v-model="formValidate.comment "></el-input>
    </el-form-item>
    <el-form-item label="商品分数：" prop="productScore" class="productScore">
      <el-rate v-model="formValidate.productScore"></el-rate>
      <!--<el-input-number type="textarea" v-model="formValidate.productScore "></el-input-number>-->
    </el-form-item>
    <el-form-item label="服务分数：" prop="serviceScore" class="productScore">
      <el-rate v-model="formValidate.serviceScore"></el-rate>
      <!--<el-input-number type="textarea" v-model="formValidate.serviceScore "></el-input-number>-->
    </el-form-item>
    <el-form-item label="用户头像：" prop="avatar">
      <div class="upLoadPicBox" @click="modalPicTap('1')">
        <div v-if="formValidate.avatar" class="pictrue"><img :src="formValidate.avatar"></div>
        <div v-else class="upLoad">
          <i class="el-icon-camera cameraIconfont"/>
        </div>
      </div>
    </el-form-item>
    <el-form-item label="评价图片：">
      <div class="acea-row">
        <div
          v-for="(item,index) in pics"
          :key="index"
          class="pictrue"
          draggable="false"
          @dragstart="handleDragStart($event, item)"
          @dragover.prevent="handleDragOver($event, item)"
          @dragenter="handleDragEnter($event, item)"
          @dragend="handleDragEnd($event, item)"
        >
          <img :src="item">
          <i class="el-icon-error btndel" @click="handleRemove(index)" />
          <!--<Button shape="circle" icon="md-close" class="btndel" @click.native="handleRemove(index)" />-->
        </div>
        <div v-if="pics<10" class="upLoadPicBox" @click="modalPicTap('2')">
          <div class="upLoad">
            <i class="el-icon-camera cameraIconfont" />
          </div>
        </div>
      </div>
    </el-form-item>
    <el-form-item>
      <el-button size="mini" type="primary" @click="submitForm('formValidate')">立即创建</el-button>
      <el-button size="mini" @click="resetForm('formValidate')">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
  import { replyCreatApi, replyEditApi, replyInfoApi } from '@/api/store'
  const defaultObj= {
    avatar: '',
    comment: '',
    nickname: '',
    pics: '',
    productId: '',
    productScore: null,
    serviceScore: null
  }
  export default {
    name: "creatComment",
    props:{
      num: {
        type: Number,
        required: 0
      },
    },
    data() {
      var checkProductScore = (rule, value, callback) => {
        if (!value) {
          return callback(new Error('商品分数不能为空'));
        }else{
          callback();
        }
      };
      var checkServiceScore = (rule, value, callback) => {
        if (!value) {
          return callback(new Error('服务分数不能为空'));
        }else{
          callback();
        }
      };
      return {
        loading: false,
        pics: [],
        image: '',
        formValidate: Object.assign({}, defaultObj),
        rules: {
          avatar: [
            {required: true, message: '请选择用户头像', trigger: 'change'},
          ],
          productId: [
            {required: true, message: '请选择商品', trigger: 'change'},
          ],
          comment: [
            {required: true, message: '请填写评价内容', trigger: 'blur'},
          ],
          nickname: [
            {required: true, message: '请填写用户名称', trigger: 'blur'},
          ],
          pics: [
            {required: true, message: '请选择评价图片', trigger: 'change'},
          ],
          productScore: [
            {required: true, validator: checkProductScore, trigger: 'blur'},
          ],
          serviceScore: [
            {required: true, validator: checkServiceScore,  trigger: 'change'},
          ],
        }
      }
    },
    watch: {
      num: {
        handler: function(val) {
          this.resetForm('formValidate')
        },
        deep: true
      }
    },
    methods: {
      changeGood(){
        const _this = this
        this.$modalGoodList(function(row) {
          _this.image = row.image
          _this.formValidate.productId = row.id
        })
      },
      // 点击商品图
      modalPicTap (tit) {
        const _this = this
        _this.$modalUpload(function(img) {
          tit==='1' ? _this.formValidate.avatar = img[0].sattDir : img.map((item) => {
            _this.pics.push( item.sattDir)
          })
        },tit, 'store')
      },
      handleRemove (i) {
        this.pics.splice(i, 1)
      },
      submitForm(formName) {
        this.formValidate.pics = JSON.stringify(this.pics)
        this.$refs[formName].validate((valid) => {
          if (valid) {
            replyCreatApi(this.formValidate).then(() => {
              this.$message.success("新增成功")
              setTimeout(() => {
                // this.clear();
                this.$emit('getList');
              }, 600);
            })
          } else {
            return false;
          }
        });
      },
      resetForm(formName) {
        this.$refs[formName].resetFields()
        this.$msgbox.close()
        this.pics=[]
        this.formValidate.pics=''
      },
      info(){
        this.loading = true
        replyInfoApi(this.formValidate).then(() => {
          this.formValidate = res
          this.loading = false
        }).catch(() => {
          this.loading = false
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
        const newItems = [...this.pics]
        const src = newItems.indexOf(this.dragging)
        const dst = newItems.indexOf(item)
        newItems.splice(dst, 0, ...newItems.splice(src, 1))
        this.pics = newItems;
      }
    }
  }
</script>

<style scoped lang="scss">
  .productScore{
    /deep/.el-rate{
      line-height: 2.4;
    }
  }
  .pictrue{
    width: 60px;
    height: 60px;
    border: 1px dotted rgba(0,0,0,0.1);
    margin-right: 10px;
    position: relative;
    cursor: pointer;
    img{
      width: 100%;
      height: 100%;
    }
  }
  .btndel{
    position: absolute;
    z-index: 1;
    width :20px !important;
    height: 20px !important;
    left: 46px;
    top: -4px;
  }
</style>
