<template>
  <div class="layout-navbars-breadcrumb-user" :style="{ flex: layoutUserFlexNum }">
    <div class="layout-navbars-breadcrumb-user-icon" @click="refresh">
      <i class="el-icon-refresh-right" title="刷新"></i>
    </div>
    <div class="layout-navbars-breadcrumb-user-icon" @click="onSearchClick">
      <i class="el-icon-search" title="菜单搜索"></i>
    </div>
    <div class="layout-navbars-breadcrumb-user-icon" @click="onScreenfullClick">
      <i
        :title="isScreenfull ? '关全屏' : '开全屏'"
        :class="!isScreenfull ? 'el-icon-full-screen' : 'el-icon-crop'"
      ></i>
    </div>
    <el-dropdown :show-timeout="70" :hide-timeout="50" @command="onDropdownCommand">
      <span class="layout-navbars-breadcrumb-user-link">
        {{ getUserInfos.name }}
        <i class="el-icon-arrow-down el-icon--right"></i>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="password">个人中心</el-dropdown-item>
          <el-dropdown-item command="users">修改密码</el-dropdown-item>
          <el-dropdown-item divided command="logOut">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    <div class="layout-navbars-breadcrumb-user-icon" @click="onLayoutSetingClick">
      <i class="el-icon-setting" title="布局配置"></i>
    </div>
    <Search ref="searchRef" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import screenfull from 'screenfull'
import { Session, Local } from '@/utils/storage.js'
import Search from '@/layout/navBars/breadcrumb/search.vue'
import { ElMessage, ElMessageBox } from '@/utils/elementPlusFeedback'
import { useThemeConfigStore } from '@/store/modules/themeConfig'
import { useUserStore } from '@/store/modules/user'
import bus from '@/utils/bus'

defineOptions({ name: 'layoutBreadcrumbUser' })

const route = useRoute()
const router = useRouter()
const themeConfigStore = useThemeConfigStore()
const userStore = useUserStore()

const isScreenfull = ref(false)
const isShowUserNewsPopover = ref(true)
const disabledI18n = ref('zh-cn')
const disabledSize = ref('')
const isDot = ref(false)
const label = ref({
  mer_name: 'admin'
})
const list = ref([])
const dealtList = ref([])
const searchRef = ref(null)

// 获取用户信息
const getUserInfos = computed(() => {
  return userStore
})

// 设置弹性盒子布局 flex
const layoutUserFlexNum = computed(() => {
  let { layout, isClassicSplitMenu } = themeConfigStore.themeConfig
  let num = ''
  if (layout === 'defaults' || (layout === 'classic' && !isClassicSplitMenu) || layout === 'columns') num = 1
  else num = null
  return num
})

onMounted(() => {
  if (Local.get('JavaPlatThemeConfigPrev')) {
    initComponentSize()
  }
})

function initIsDot(status) {
  isDot.value = status
}

function openNews() {
  // isShowUserNewsPopover.value = !isShowUserNewsPopover.value;
  isDot.value = false
}

// 搜索点击
function onSearchClick() {
  searchRef.value?.openSearch()
}

// 布局配置点击
function onLayoutSetingClick() {
  bus.emit('openSetingsDrawer')
}

function refresh() {
  bus.emit('onTagsViewRefreshRouterView', route.path)
}

// 全屏点击
function onScreenfullClick() {
  console.log(screenfull)

  if (!screenfull.isEnabled) {
    ElMessage.warning('暂不不支持全屏')
    return false
  }
  screenfull.toggle()
  screenfull.on('change', () => {
    if (screenfull.isFullscreen) isScreenfull.value = true
    else isScreenfull.value = false
  })
  // 监听菜单 horizontal.vue 滚动条高度更新
  bus.emit('updateElScrollBar')
}

// 初始化全局组件大小（element-ui medium/mini -> element-plus default/small）
function initComponentSize() {
  switch (Local.get('JavaPlatThemeConfigPrev').globalComponentSize) {
    case '':
      disabledSize.value = ''
      break
    case 'medium':
      disabledSize.value = 'default'
      break
    case 'small':
      disabledSize.value = 'small'
      break
    case 'mini':
      disabledSize.value = 'small'
      break
  }
}

// `dropdown 下拉菜单` 当前项点击
function onDropdownCommand(path) {
  if (path === 'logOut') {
    setTimeout(() => {
      ElMessageBox({
        closeOnClickModal: false,
        closeOnPressEscape: false,
        title: '提示',
        message: '此操作将退出登录, 是否继续?',
        showCancelButton: true,
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'sure-modal',
        beforeClose: async (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            instance.confirmButtonText = '退出中'
            setTimeout(async () => {
              await userStore.handleLogout()
              router.push(`/login?redirect=${route.fullPath}`)
              done()
            }, 150)
          } else {
            done()
          }
        }
      })
        .then(() => {
          // 清除缓存/token等
          Session.clear()
          // 使用 reload 时，不需要调用 resetRoute() 重置路由
          window.location.reload()
        })
        .catch(() => {})
    }, 150)
  } else if (path === 'password') {
    router.push(`/maintain/user`)
  } else if (path === 'users') {
    router.push(`/maintain/update`)
  }
}
</script>

<style scoped lang="scss">
.layout-navbars-breadcrumb-user {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  i {
    height: 50px;
  }
  &-link {
    height: 100%;
    display: flex;
    align-items: center;
    white-space: nowrap;

    &-photo {
      width: 30px;
      height: 30px;
      border-radius: 100%;
    }
  }

  i {
    line-height: 50px;
  }

  &-icon {
    padding: 0 10px;
    cursor: pointer;
    color: var(--prev-bg-topBarColor);
    line-height: 50px;
    display: flex;
    align-items: center;

    &:hover {
      background: var(--prev-color-hover);

      i {
        display: inline-block;
        animation: logoAnimation 0.3s ease-in-out;
      }
    }

    .item {
      position: relative;
    }

    .icon-tip {
      position: absolute;
      background: #f56464;
      width: 6px;
      height: 6px;
      border-radius: 100%;
      top: -1px;
      right: 0px;
    }

    .el-icon-bell {
      font-size: 15px;
      color: var(--prev-bg-topBarColor);
    }
  }

  & :deep(.el-dropdown) {
    color: var(--prev-bg-topBarColor);
    cursor: pointer;
  }

  & :deep(.el-badge) {
    height: 40px;
    line-height: 40px;
    display: flex;
    align-items: center;
  }

  & :deep(.el-badge__content.is-fixed) {
    top: 12px;
  }
}

.noticedrop {
  padding: 0;
}

.noticedrop .el-dropdown-menu {
  padding: 0;
}

.noticedrop .el-dropdown-menu__item {
  background-color: #ffffff;
  padding: 0;
  border-radius: 6px;
}

.item_content {
  display: inline-block;
  white-space: nowrap;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 10px;
  line-height: 20px;
  font-size: 13px;
}

.item_content .title {
  color: #333333;
  font-weight: bold;
}

.item_content .message {
  color: #666666;
}

.moreBtn {
  color: #666666;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  cursor: pointer;
}

:deep(.el-card__body) {
  padding: 0 24px 10px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: '';
}

.clearfix:after {
  clear: both;
}

.box-card {
  width: 240px;
}

:deep(.el-tabs__header) {
  margin: 0;
}

:deep(.el-card__header) {
  padding: 10px 24px 0;
  font-weight: bold;
  border: none;
}

.tab-empty {
  text-align: center;
  margin-top: 15px;
}

.empty-text {
  color: #999999;
  font-size: 12px;
}

.empty-img {
  display: inline-block;
  width: 160px;
  height: 123px;
}
</style>
