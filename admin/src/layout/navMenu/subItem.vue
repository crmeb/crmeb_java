<!--
 * @Author: From-wh from-wh@hotmail.com
 * @Date: 2023-03-09 15:45:51
 * @FilePath: /admin/src/layout/navMenu/subItem.vue
 * @Description:
-->
<template>
  <div>
    <template v-for="val in chil" :key="val.path">
      <el-sub-menu :index="val.path" v-if="val.children && val.children.length > 0">
        <template #title>
          <span @click.stop="handleSubMenuTitleClick(val)">{{ val.title }}</span>
        </template>
        <sub-item :chil="val.children" />
      </el-sub-menu>
      <template v-else>
        <el-menu-item :index="val.path">
          <template #title>
            <span>{{ val.title }}</span>
          </template>
        </el-menu-item>
      </template>
    </template>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

defineOptions({ name: 'subItem' })

const router = useRouter()

const props = defineProps({
  chil: {
    type: Array,
    default() {
      return []
    }
  }
})

// 有 children 的子菜单点击标题时导航到第一个叶子节点
function handleSubMenuTitleClick(val) {
  if (val.children && val.children.length > 0) {
    const firstLeaf = findFirstLeaf(val.children)
    if (firstLeaf) {
      router.push(firstLeaf.path)
    }
  }
}

function findFirstLeaf(children) {
  console.log('findFirstLeaf', children)
  if (!children || children.length === 0) return null
  if (children[0].children && children[0].children.length > 0) {
    return findFirstLeaf(children[0].children)
  }
  return children[0]
}
</script>
