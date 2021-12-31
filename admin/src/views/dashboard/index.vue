<template>
  <div>
    <!--头部-->
    <base-info ref="baseInfo" v-if="checkPermi(['admin:statistics:home:index'])" />
    <!--小方块-->
    <grid-menu class="mb20"/>
    <!--订单统计-->
    <visit-chart ref="visitChart"/>
    <!--用户-->
    <user-chart ref="userChart" class="mb20" v-if="checkPermi(['admin:statistics:home:chart:user'])" />
  </div>
</template>

<script>
import baseInfo from './components/baseInfo'; 
import gridMenu from './components/gridMenu';
import visitChart from './components/visitChart';
import userChart from './components/userChart';
import { checkPermi } from "@/utils/permission"; // 权限判断函数
export default {
  name: 'Dashboard',
  components: { baseInfo, gridMenu, visitChart, userChart}, 
  data() {
    return {
      authStatus:null,
      authHost:'',
      authQueryStatus:false
    }
  },
  methods:{
    checkPermi,
  },
}
</script>
