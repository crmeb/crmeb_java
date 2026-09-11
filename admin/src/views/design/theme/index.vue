<template>
  <div class="divBox">
    <el-card class="box-card">
      <div class="theme_box">
        <div class="theme_head">
          <div
            class="tab_color"
            v-for="(item, index) in tabList"
            :key="index"
            :class="active === index ? 'active' : ''"
            @click="selected(index)"
          >
            <div class="color_cont flex align-center">
              <div class="main_c mr-2" :class="item.class">
                <span class="iconfont iconios-checkmark-circle white" v-show="active == index"></span>
              </div>
              <div>{{ item.tit }}</div>
            </div>
          </div>
        </div>
        <div class="theme_content">
          <img :src="item" alt="" v-for="(item, index) in imgList" :key="index" />
        </div>
        <div class="save">
          <el-button type="primary" @click="saveTheme()" v-hasPermi="['admin:system:config:saveuniq']">保存</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { themeSave } from '@/api/systemGroup';
import { changeColorApi, saveColorApi } from '@/api/systemConfig';
import { Debounce } from '@/utils/validate';
import { useSettingsStore } from '@/store/modules/settings';
import theme1Img from '@/assets/theme/theme1.jpg';
import theme2Img from '@/assets/theme/theme2.jpg';
import theme3Img from '@/assets/theme/theme3.jpg';
import theme4Img from '@/assets/theme/theme4.png';
import theme5Img from '@/assets/theme/theme5.jpg';
import goodsList1Img from '@/assets/theme/goodsList1.png';
import goodsList2Img from '@/assets/theme/goodsList2.png';
import goodsList3Img from '@/assets/theme/goodsList3.png';

const settingsStore = useSettingsStore();

const active = ref(0);
const tabList = [
  { tit: '热情红', class: 'bg1' },
  { tit: '家居橙', class: 'bg2' },
  { tit: '生鲜绿', class: 'bg3' },
  { tit: '海鲜蓝', class: 'bg4' },
  { tit: '女神粉', class: 'bg5' },
];
const themeData = ref([]);
const imgList = ref([theme1Img]);
const theme1 = [theme1Img];
const theme2 = [theme2Img];
const theme3 = [theme3Img];
const theme4 = [theme4Img];
const theme5 = [theme5Img];
//主题色
const themeColor = ['#e93323', '#FE5C2D', '#42CA4D', '#1db0fc', '#ff448f'];
const mockGoodsImg = ref(goodsList1Img);
const showTabNav = ref(true);

function selected(index) {
  active.value = index;
  if (index == 0) {
    imgList.value = theme1;
  } else if (index == 1) {
    imgList.value = theme2;
  } else if (index == 2) {
    imgList.value = theme3;
  } else if (index == 3) {
    imgList.value = theme4;
  } else if (index == 4) {
    imgList.value = theme5;
  }
}
//获取默认数据
function getSet() {
  changeColorApi().then((res) => {
    active.value = res.value - 1;
    imgList.value = getTheme(res.value);
  });
}
// 默认数据种给主题色图片赋值
function getTheme(status) {
  const statusMap = {
    1: theme1,
    2: theme2,
    3: theme3,
    4: theme4,
    5: theme5,
  };
  return statusMap[status];
}
// 保存
const saveTheme = Debounce(function () {
  let data = {
    value: active.value + 1,
  };
  saveColorApi(data).then((res) => {
    ElMessage.success('编辑成功');
    settingsStore.SET_mobileThemeColor(themeColor[active.value]);
  });
});

onMounted(() => {
  getSet();
});
</script>

<style lscoped lang="scss">
.flex {
  display: flex;
}

.align-center {
  align-items: center;
}

.theme_head {
  width: 100%;
  display: flex;
  margin: 20px auto;
}

.tab_color {
  width: 114px;
  height: 45px;
  border: 1px solid #e5e5e5;
  margin-right: 20px;
  border-radius: 5px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.color_cont {
  font-size: 14px;
}

.mr-2 {
  margin-right: 10px;
}

.color_bdg {
  display: block;
  width: 18px;
  height: 18px;
  border-top: 1px solid #fff;
  border-bottom: 1px solid #fff;
}

.theme_content {
  width: 100%;
  display: flex;
  margin-bottom: 40px;

  img {
    display: block;
    margin-right: 20px;
    width: 800px;
  }
}

.sm_bg {
  width: 60px;
}

.main_c {
  width: 25px;
  height: 25px;
  border-radius: 5px;
  text-align: center;
  line-height: 25px;
  font-size: 14px;
}

.active {
  border: 1px solid var(--prev-color-primary);
}

.bg1 {
  background: #e93323;
}

.bg2 {
  background: #fe5c2d;
}

.bg3 {
  background: #42ca4d;
}

.bg4 {
  background: #1ca5e9;
}

.bg5 {
  background: #ff448f;
}

.white {
  color: #fff;
}
</style>
