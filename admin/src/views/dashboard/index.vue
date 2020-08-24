<template>
  <div>
    <!--头部-->
    <base-info ref="baseInfo"/>
    <!--小方块-->
    <grid-menu class="mb20"/>
    <!--订单统计-->
    <visit-chart ref="visitChart"/>
    <!--用户-->
    <user-chart ref="userChart" class="mb20"/>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import baseInfo from './components/baseInfo';
import gridMenu from './components/gridMenu';
import visitChart from './components/visitChart';
import userChart from './components/userChart';
export default {
  name: 'Dashboard',
  components: { baseInfo, gridMenu, visitChart, userChart },
  data() {
    return {
      currentRole: 'adminDashboard'
    }
  },
  computed: {
    ...mapGetters([
      'roles'
    ])
  },
  created() {
    if (!this.roles.includes('admin')) {
      this.currentRole = 'editorDashboard'
    }
  }
}
</script>
