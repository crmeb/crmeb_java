<template>
    <el-dialog :visible.sync="modals" title="订单记录" class="order_box">
        <el-form ref="formItem" :model="formItem" label-width="110px" @submit.native.prevent>
            <el-form-item label="选择类型：">
                <el-radio-group v-model="formItem.type" @change="changeRadio(formItem.type)">
                    <el-radio label="1">发货</el-radio>
                    <el-radio label="2">送货</el-radio>
                    <el-radio label="3">虚拟</el-radio>
                </el-radio-group>
            </el-form-item>
            <div v-if="formItem.type==='1'">
                <el-form-item label="快递公司：" prop="expressId"
                              :rules="[{ required: true, message: '请选择快递公司', trigger: 'change' }]">
                    <el-select v-model="formItem.expressId" style="width:80%;">
                        <el-option v-for="(item,i) in express" :value="item.id" :key="i" :label="item.name"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="快递单号：" prop="expressCode"
                              :rules="[{ required: true, message: '请输入快递单号', trigger: 'blur' }]">
                    <el-input v-model="formItem.expressCode" placeholder="请输入快递单号" style="width:80%;"></el-input>
                </el-form-item>
            </div>
            <div v-if="formItem.type==='2'">
                <el-form-item label="送货人姓名：" prop="expressId"
                              :rules="[{ required: true, message: '请输入送货人姓名', trigger: 'blur' }]">
                    <el-input v-model="formItem.expressId" placeholder="请输入送货人姓名" style="width:80%;"></el-input>
                </el-form-item>
                <el-form-item label="送货人电话：" prop="expressCode"
                              :rules="[{ required: true, message: '请输入送货人电话', trigger: 'blur' }]">
                    <el-input v-model="formItem.expressCode" placeholder="请输入送货人电话" style="width:80%;"></el-input>
                </el-form-item>
            </div>
        </el-form>
        <div slot="footer">
            <el-button size="mini" type="primary" @click="putSend('formItem')">提交</el-button>
            <el-button size="mini" @click="cancel('formItem')">取消</el-button>
        </div>
    </el-dialog>
</template>

<script>
    import { orderSendApi } from '@/api/order'
    import { expressList } from '@/api/logistics';
    export default {
        name: 'orderSend',
        props: {
            orderId: Number
        },
        data () {
            return {
                formItem: {
                    type: '1',
                    expressId: '',
                    expressCode: '',
                    id: ''
                },
                modals: false,
                express: []
            }
        },
        mounted () {
        },
        methods: {
            changeRadio (o) {
              if( o === '3'){
                this.formItem.expressId = ''
                this.formItem.expressCode = ''
              }
            },
            // 物流公司列表
            getList () {
              expressList({ page: 1, limit: 999, isShow:1 }).then(async res => {
                    this.express = res.list
                })
            },
            // 提交
            putSend(name) {
              this.formItem.id = this.orderId
              this.$refs[name].validate((valid) => {
                if (valid) {
                  orderSendApi(this.formItem).then(async  => {
                    this.$message.success('发送货成功');
                    this.modals = false;
                    this.$refs[name].resetFields();
                    this.$emit('submitFail')
                  })
                } else {
                  this.$message.error('请填写信息');
                }
              })
            },
            cancel (name) {
                this.modals = false;
                this.$refs[name].resetFields();
                this.formItem.type = '1';
            }
        }
    }
</script>

<style scoped>

</style>
