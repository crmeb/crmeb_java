<!--
 * @Author: From-wh from-wh@hotmail.com
 * @Date: 2023-03-09 15:45:51
 * @FilePath: /admin/src/layout/navMenu/subItem.vue
 * @Description:
-->
<template>
  <div>
    <template v-for="val in chil">
      <el-submenu :index="val.path" :key="val.path" v-if="val.children && val.children.length > 0">
        <template slot="title">
          <i class="ivu-icon" :class="'el-icon-' + val.icon"></i>
          <span>{{ val.title }}</span>
        </template>
        <sub-item :chil="val.children" />
      </el-submenu>
      <template v-else>
        <el-menu-item :index="val.path" :key="val.path">
          <template>
            <i class="ivu-icon" :class="val.icon ? 'el-icon-' + val.icon : ''"></i>
            <span>{{ val.title }}</span>
          </template>
        </el-menu-item>
      </template>
    </template>
  </div>
</template>

<script>
export default {
  name: 'subItem',
  props: {
    chil: {
      type: Array,
      default() {
        return [];
      },
    },
  },
};
</script>
