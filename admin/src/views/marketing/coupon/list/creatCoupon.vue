<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="150px" class="demo-ruleForm">
        <el-form-item label="优惠劵名称" prop="name">
          <el-input v-model="ruleForm.name" style="width: 350px" placeholder="请输入优惠券名称"></el-input>
        </el-form-item>
        <el-form-item label="优惠劵类型">
          <el-radio-group v-model="ruleForm.useType">
            <el-radio :label="1">通用券</el-radio>
            <el-radio :label="2">商品券</el-radio>
            <el-radio :label="3">品类券</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="选择品类：" prop="primaryKey" v-if="ruleForm.useType === 3">
          <el-cascader v-model="ruleForm.primaryKey" :options="merCateList" :props="props2" clearable class="selWidth" :show-all-levels="false" />
        </el-form-item>
        <el-form-item label="商品：" v-if="ruleForm.useType === 2" prop="checked">
          <div class="acea-row">
            <template v-if="ruleForm.checked.length">
              <div class="pictrue" v-for="(item, index) in ruleForm.checked" :key="index">
                <img :src="item.image">
                <i class="el-icon-error btndel" @click="handleRemove(index)" />
              </div>
            </template>
            <div class="upLoadPicBox" @click="changeGood">
              <div class="upLoad">
                <i class="el-icon-camera cameraIconfont" />
              </div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="优惠券面值" prop="money">
          <el-input-number v-model="ruleForm.money" :min="1" label="描述文字"></el-input-number>
        </el-form-item>
        <el-form-item label="使用门槛">
          <el-radio-group v-model="threshold">
            <el-radio :label="false">无门槛</el-radio>
            <el-radio :label="true">有门槛</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="优惠券最低消费" prop="minPrice" v-if="threshold">
          <el-input-number v-model="ruleForm.minPrice" :min="1" label="描述文字"></el-input-number>
        </el-form-item>
        <el-form-item label="使用有效期">
          <el-radio-group v-model="ruleForm.isFixedTime">
            <el-radio :label="false">天数</el-radio>
            <el-radio :label="true">时间段</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="使用有效期限（天）" prop="day" v-if="!ruleForm.isFixedTime">
          <el-input-number v-model="ruleForm.day" :min="0" :max="999" label="描述文字"></el-input-number>
        </el-form-item>
        <el-form-item label="使用有效期限" prop="resource" v-if="ruleForm.isFixedTime">
          <el-date-picker
            style="width: 550px"
            v-model="termTime"
            type="datetimerange"
            range-separator="至"
            value-format="yyyy-MM-dd HH:mm:ss"
            start-placeholder="开始日期"
            :picker-options="pickerOptions"
            end-placeholder="结束日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="领取是否限时" prop="isForever">
          <el-radio-group v-model="ruleForm.isForever">
            <el-radio :label="true">限时</el-radio>
            <el-radio :label="false">不限时</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="领取时间" v-if="ruleForm.isForever">
          <el-date-picker
            style="width: 550px"
            v-model="isForeverTime"
            type="datetimerange"
            range-separator="至"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="pickerOptions"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @blur="handleTimestamp">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="领取方式" prop="resource">
          <el-radio-group v-model="ruleForm.type">
            <el-radio :label="1">手动领取</el-radio>
            <el-radio :label="2">新人券</el-radio>
            <el-radio :label="3">赠送券</el-radio>
            <!--<el-radio :label="4">付费会员卷</el-radio>-->
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否限量" prop="isLimited">
          <el-radio-group v-model="ruleForm.isLimited">
            <el-radio :label="true">限量</el-radio>
            <el-radio :label="false">不限量</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="发布数量" prop="total" v-if="ruleForm.isLimited">
          <el-input-number v-model="ruleForm.total" :min="1" label="排序"></el-input-number>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="ruleForm.sort" :min="0" label="排序"></el-input-number>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="ruleForm.status">
            <el-radio :label="true">开启</el-radio>
            <el-radio :label="false">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button size="mini" type="primary" @click="submitForm('ruleForm')" :loading="loading" v-hasPermi="['admin:coupon:save']">立即创建</el-button>
          <!--<el-button @click="resetForm('ruleForm')">重置</el-button>-->
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
  import { couponSaveApi, couponInfoApi } from '@/api/marketing'
  import { categoryApi } from '@/api/store'
  import {Debounce} from '@/utils/validate'
  export default {
    name: "creatCoupon",
    data() {
      return {
        pickerOptions: {
          disabledDate(time) {
            // return time.getTime() < new Date().setTime(new Date().getTime() - 3600 * 1000 * 24); //不限制未来时间
            return time.getTime() < Date.now() - 8.64e7 || time.getTime() > Date.now() + 600 * 8.64e7; //限制未来时间
          }
        },
        loading: false,
        threshold: false,
        termTime: [],
        props2: {
          children: 'child',
          label: 'name',
          value: 'id',
          checkStrictly: true,
          emitPath: false
        },
        couponType: 0,
        term: 'termday',
        merCateList: [], // 商户分类筛选
        ruleForm: {
          useType: 1,
          isFixedTime: false,
          name: '',
          money: 1,
          minPrice: 1,
          day: null,
          isForever: false,
          primaryKey: '',
          type: 2,
          isLimited: false,
          useStartTime: '', // 使用
          useEndTime: '', // 结束
          receiveStartTime: '', //领取
          receiveEndTime: '',
          sort: 0,
          total: 1,
          status: false,
          checked: []
        },
        isForeverTime: [],
        rules: {
          name: [
            { required: true, message: '请输入优惠券名称', trigger: 'blur' }
          ],
          day: [
            { required: true, message: '请输入使用有效期限（天）', trigger: 'blur' }
          ],
          money: [
            { required: true, message: '请输入优惠券面值', trigger: 'blur' }
          ],
          primaryKey: [
            { required: true, message: '请选择品类', trigger: 'change' }
          ],
          checked:  [
            { required: true, message: '请至少选择一个商品', trigger: 'change', type: 'array' }
          ],
          isForeverTime: [
            { required: true, message: '请选择领取时间', trigger: 'change', type: 'array' }
          ],
          total: [
            { required: true, message: '请输入发布数量', trigger: 'blur' }
          ],
          minPrice: [
            { required: true, message: '请输入最低消费', trigger: 'blur' }
          ]
        }
      }
    },
    mounted() {
      this.getCategorySelect()
      if( this.$route.params.id ) this.getInfo()
    },
    methods: {
      handleTimestamp(){

      },
      // 商品分类；
      getCategorySelect() {
        categoryApi({ status: -1, type: 1 }).then(res => {
          this.merCateList = res
          this.merCateList.map(item => {
            this.$set(item , 'disabled', true)
          })
        })
      },
      getInfo(){
        this.loading = true
        couponInfoApi({id: this.$route.params.id}).then(res => {
          const info = res.coupon
          this.ruleForm = {
            useType: info.useType,
            isFixedTime: info.isFixedTime,
            isForever: info.isForever,
            name: info.name,
            money: info.money,
            minPrice: info.minPrice,
            day: info.day,
            type: info.type,
            isLimited: info.isLimited,
            sort: info.sort,
            total: info.total,
            status: info.status,
            primaryKey: Number(info.primaryKey),
            checked: res.product || []
          }
          info.minPrice == 0 ? this.threshold = false : this.threshold = true
          info.isForever ? this.isForeverTime = [info.receiveStartTime, info.receiveEndTime] : this.isForeverTime = []
          info.isFixedTime && info.useStartTime && info.useEndTime ? this.termTime = [info.useStartTime, info.useEndTime] : this.termTime = []
          this.loading = false
        }).catch(res => {
          this.loading = false
          this.$message.error(res.message)
        })
      },
      handleRemove (i) {
        this.ruleForm.checked.splice(i, 1)
      },
      changeGood(){
        const _this = this
        this.$modalGoodList(function(row) {
          _this.ruleForm.checked = row
        },'many',_this.ruleForm.checked)
      },
      submitForm:Debounce(function(formName) {
       if( (this.ruleForm.isFixedTime && !this.termTime) || this.ruleForm.isFixedTime && !this.termTime.length) return this.$message.warning("请选择使用有效期限")
       if( (this.ruleForm.isForever && !this.isForeverTime) || (this.ruleForm.isForever && !this.isForeverTime.length)) return this.$message.warning("请选择请选择领取时间")
        if( this.ruleForm.useType === 2 ) this.ruleForm.primaryKey = this.ruleForm.checked.map(item => {return item.id}).join(',')
        if( this.ruleForm.useType === 1 ) this.ruleForm.primaryKey = ''
        if( !this.threshold ) this.ruleForm.minPrice = 0
        if( !this.ruleForm.isLimited ) this.ruleForm.total = 0
        this.ruleForm.isFixedTime && this.termTime.length ? (this.ruleForm.useStartTime = this.termTime[0], this.ruleForm.day = null) : this.ruleForm.useStartTime = ''
        this.ruleForm.isFixedTime && this.termTime.length ? (this.ruleForm.useEndTime = this.termTime[1], this.ruleForm.day = null) : this.ruleForm.useEndTime = ''
        this.ruleForm.isForever && this.isForeverTime.length ? this.ruleForm.receiveStartTime = this.isForeverTime[0] : this.ruleForm.receiveStartTime = ''
        this.ruleForm.isForever && this.isForeverTime.length ? this.ruleForm.receiveEndTime = this.isForeverTime[1] : this.ruleForm.receiveEndTime = ''
        this.$refs[formName].validate((valid) => {
          if (valid) {
           this.loading = true
            couponSaveApi(this.ruleForm).then(() => {
              this.$message.success("新增成功")
              this.loading = false
              setTimeout(() => {
                this.$router.push({ path: `/marketing/coupon/list` })
              }, 200);
            }).catch(() => {
              this.loading = false
            })
          } else {
            this.loading = false
            return false;
          }
        });
      }),
      
    }
  }
</script>

<style scoped lang="scss">
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
