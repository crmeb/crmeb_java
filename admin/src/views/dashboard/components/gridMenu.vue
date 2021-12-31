<template>
  <div class="divBox">
    <el-row :gutter="24" class="dashboard-console-grid">
        <el-col v-bind="grid" class="ivu-mb" v-if="checkPermi(['admin:user:list'])">
            <el-card :bordered="false">
                <router-link :to="{path:'/user/index'}">
                    <i class="el-icon-user" style="color:#69c0ff" />
                    <p>会员管理</p>
                </router-link>
            </el-card>
        </el-col>
        <el-col v-bind="grid" class="ivu-mb" v-if="checkPermi(['admin:system:config:info'])">
            <el-card :bordered="false">
                <router-link :to="{path:'/operation/setting'}">
                    <i class="el-icon-setting" style="color:#95de64" />
                    <p>系统设置</p>
                </router-link>
            </el-card>
        </el-col>
        <el-col v-bind="grid" class="ivu-mb" v-if="checkPermi(['admin:product:list'])">
            <el-card :bordered="false">
                <router-link :to="{path:'/store/index'}">
                    <i class="el-icon-goods" style="color:#ff9c6e" />
                    <p>商品</p>
                </router-link>
            </el-card>
        </el-col>
        <el-col v-bind="grid" class="ivu-mb" v-if="checkPermi(['admin:order:list'])">
            <el-card :bordered="false">
                <router-link :to="{path:'/order/index'}">
                    <i class="el-icon-s-order" style="color:#b37feb" />
                    <p>订单管理</p>
                </router-link>
            </el-card>
        </el-col>
        <el-col v-bind="grid" class="ivu-mb" v-if="checkPermi(['admin:pass:login'])">
            <el-card :bordered="false">
                <router-link :to="{path:'/operation/systemSms/config'}">
                    <i class="el-icon-message" style="color:#ffd666" />
                    <p>短信配置</p>
                </router-link>
            </el-card>
        </el-col>
        <el-col v-bind="grid" class="ivu-mb" v-if="checkPermi(['admin:article:list'])">
            <el-card :bordered="false">
                <router-link :to="{path:'/content/articleManager'}">
                    <i class="el-icon-notebook-1" style="color:#5cdbd3" />
                    <p>文章管理</p>
                </router-link>
            </el-card>
        </el-col>
        <el-col v-bind="grid" class="ivu-mb" v-if="checkPermi(['admin:retail:list'])">
            <el-card :bordered="false">
                <router-link :to="{path:'/distribution/index'}">
                    <i class="el-icon-s-finance" style="color:#ff85c0" />
                    <p>分销管理</p>
                </router-link>
            </el-card>
        </el-col>
        <el-col v-bind="grid" class="ivu-mb" v-if="checkPermi(['admin:coupon:list'])">
            <el-card :bordered="false">
                <router-link :to="{path:'/marketing/coupon/list'}">
                    <i class="el-icon-s-ticket" style="color:#ffc069" />
                    <p>优惠券</p>
                </router-link>
            </el-card>
        </el-col>
    </el-row>
  </div>
</template>
<script>
import { checkPermi } from "@/utils/permission"; 
    export default {
        data () {
            return {
                grid: {
                    xl: 3,
                    lg: 3,
                    md: 6,
                    sm: 8,
                    xs: 8
                }
            }
        },
        methods:{
          checkPermi
        }
    }
</script>
<style lang="scss" scoped>
  .ivu-mb{
    // margin-bottom: 10px;
  }
  .divBox {
    padding: 0 20px !important;
  }

  .dashboard-console-grid {
    text-align: center;
    .ivu-card-body {
      padding: 0;
    }
    i {
      font-size: 32px;
    }
    a {
      display: block;
      color: inherit;
    }
    p {
      margin-top: 8px;
    }
  }
</style>
