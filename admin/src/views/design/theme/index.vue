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

<script>
import { themeSave } from '@/api/systemGroup';
import { changeColorApi, saveColorApi } from '@/api/systemConfig';
import { Debounce } from '@/utils/validate';
export default {
  data() {
    return {
      active: 0,
      tabList: [
        { tit: '热情红', class: 'bg1' },
        { tit: '家居橙', class: 'bg2' },
        { tit: '生鲜绿', class: 'bg3' },
        { tit: '海鲜蓝', class: 'bg4' },
        { tit: '女神粉', class: 'bg5' },
      ],
      themeData: [],
      imgList: [require('@/assets/theme/theme1.jpg')],
      theme1: [require('@/assets/theme/theme1.jpg')],
      theme2: [require('@/assets/theme/theme2.jpg')],
      theme3: [require('@/assets/theme/theme3.jpg')],
      theme4: [require('@/assets/theme/theme4.png')],
      theme5: [require('@/assets/theme/theme5.jpg')],
      //主题色
      themeColor: ['#e93323', '#FE5C2D', '#42CA4D', '#1db0fc', '#ff448f'],
    };
  },
  mounted() {
    this.getSet();
  },
  methods: {
    selected(index) {
      this.active = index;
      if (index == 0) {
        this.$set(this, 'imgList', this.theme1);
      } else if (index == 1) {
        this.$set(this, 'imgList', this.theme2);
      } else if (index == 2) {
        this.$set(this, 'imgList', this.theme3);
      } else if (index == 3) {
        this.$set(this, 'imgList', this.theme4);
      } else if (index == 4) {
        this.$set(this, 'imgList', this.theme5);
      }
    },
    //获取默认数据
    getSet() {
      changeColorApi().then((res) => {
        this.active = res.value - 1;
        this.imgList = this.getTheme(res.value);
      });
    },
    // 默认数据种给主题色图片赋值
    getTheme(status) {
      const statusMap = {
        1: this.theme1,
        2: this.theme2,
        3: this.theme3,
        4: this.theme4,
        5: this.theme5,
      };
      return statusMap[status];
    },
    // 保存
    saveTheme: Debounce(function () {
      let data = {
        value: this.active + 1,
      };
      saveColorApi(data).then((res) => {
        this.$message.success('编辑成功');
        this.$store.commit('settings/SET_mobileThemeColor', this.themeColor[this.active]);
      });
    }),
  },
};
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
