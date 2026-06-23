<template>
  <div class="divBox">
    <el-card class="card-box" shadow="never" :bordered="false">
      <div class="flex">
        <!-- 左侧预览 -->
        <div class="iframe" :bordered="false">
          <div class="nofonts" v-if="!splashFrom.adList.length">暂无照片，请添加~</div>
          <swiper :options="swiperOption" class="swiperimg on">
            <swiper-slide class="swiperimg on" v-for="(item, index) in splashFrom.adList" :key="index + 'a'">
              <img :src="item.imageUrl" mode="aspectFill" />
              {{ item }}
            </swiper-slide>
          </swiper>
        </div>
        <!-- 广告设置 -->
        <div class="content">
          <div class="ml20">
            <div class="right-box">
              <div class="title-bar-line">开屏广告设置</div>
              <div class="from-tips">建议尺寸：750 * 1334px，拖拽图片可调整图片顺序哦，最多添加五张</div>
              <div class="list-box mt20">
                <el-form :model="splashFrom">
                  <el-form-item label="开屏广告:">
                    <el-switch
                      v-model="splashFrom.splashAdSwitch"
                      :active-value="1"
                      :inactive-value="0"
                      :width="55"
                      active-text="开启"
                      inactive-text="关闭"
                    />
                  </el-form-item>
                  <el-form-item label="广告时间:">
                    <el-input-number
                      v-model.number="splashFrom.splashAdShowTime"
                      type="number"
                      size="small"
                      :min="1"
                      placeholder="请输入开屏广告时间"
                      style="width: 150px"
                    ></el-input-number
                    >（单位：秒）
                    <div class="from-tips">广告N秒之后进行自动关闭（按照广告时间进行倒计时展示）。</div>
                  </el-form-item>
                  <el-form-item label="展示间隔:">
                    <el-input-number
                      v-model.number="splashFrom.splashAdShowInterval"
                      type="number"
                      size="small"
                      :min="0"
                      placeholder="请输入广告间隔时间"
                      style="width: 150px"
                    ></el-input-number
                    >（单位：小时）
                    <div class="from-tips">
                      在设置的时间内，重复打开商城，只展示一次开屏广告。设置0代表每次进入商城均会出现开屏广告。
                    </div>
                  </el-form-item>
                </el-form>
                <FromList :configObj="advertisementConfig" @getPicList="getPicList"></FromList>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
    <el-card class="bottom-card">
      <div class="save">
        <el-button
          size="small"
          type="primary"
          v-hasPermi="['admin:page:layout:splash:ad:save']"
          v-debounceClick="handleAdvertisementSave"
          >{{ loadingBtn ? '提交中 ...' : '保存' }}</el-button
        >
      </div>
    </el-card>
  </div>
</template>
<script>
import FromList from '@/components/FromList';
import { advertisementDefault } from '@/views/design/advertisement/default';
import { checkPermi } from '@/utils/permission';
import { splashGetApi, splashSaveApi } from '@/api/devise';
export default {
  name: '',
  components: {
    FromList,
  },
  mixins: [],
  props: {},
  data() {
    return {
      advertisementConfig: Object.assign({}, advertisementDefault()), //选择链接数据
      loadingBtn: false,
      // 广告设置
      splashFrom: {
        adList: [],
        splashAdShowInterval: 0, // 展示间隔
        splashAdShowTime: 0, // 广告时间
        splashAdSwitch: 0, // 广告开关
      },
      // 轮播图配置项
      swiperOption: {
        //显示分页
        pagination: {
          el: '.swiper-pagination',
        },
        //设置点击箭头
        navigation: {
          nextEl: '.swiper-button-next',
          prevEl: '.swiper-button-prev',
        },
        //自动轮播
        autoplay: {
          delay: 2000,
          //当用户滑动图片后继续自动轮播
          disableOnInteraction: false,
        },
        //开启循环模式
        loop: false,
      },
    };
  },
  mounted() {
    if (checkPermi(['admin:page:layout:splash:ad:get'])) this.getAdvertisement();
  },
  methods: {
    // 开屏广告新增
    handleAdvertisementSave() {
      this.advertisementConfig.list.map((item, index) => {
        item.sort = index + 1;
      });
      let data = {
        ...this.splashFrom,
        adList: this.advertisementConfig.list,
      };
      this.loadingBtn = true;
      splashSaveApi(data)
        .then((res) => {
          this.$message.success('保存成功');
          this.loadingBtn = false;
          this.getAdvertisement();
        })
        .catch(() => {
          this.loadingBtn = false;
        });
    },
    // 开屏广告数据
    getAdvertisement() {
      splashGetApi().then((res) => {
        this.splashFrom = res;
        this.advertisementConfig.list = res.adList;
      });
    },
    // 获取广告图片
    getPicList(data) {
      this.splashFrom.adList = data
    }
  },
};
</script>
<style lang="scss" scoped>
.card-box {
  padding: 20px 50px;
  ::v-deep .el-card__body {
    padding: 0;
  }
  .iframe {
    margin-left: 20px;
    position: relative;
    width: 350px;
    height: 75vh;
    max-height: 650px;
    background: #ffffff;
    border: 1px solid #eeeeee;
    opacity: 1;
    border-radius: 10px;
    .nofonts {
      text-align: center;
      line-height: 125px;
    }
    .swiperimg {
      width: 335px;
      height: 145px;
      border-radius: 8px;

      &.on {
        height: 75vh;
        max-height: 650px;
        width: 350px;
        line-height: 145px;
      }
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }
}
.content {
  max-height: calc(100vh - 200px);
  overflow-y: scroll;
  width: 100%;
  .right-box {
    margin-left: 40px;
  }
  &::-webkit-scrollbar {
    width: 0;
    height: 0;
    background-color: transparent;
  }
}
.bottom-card {
  position: absolute;
  bottom: 0;
  right: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50px;
  width: 100%;
  ::v-deep .el-card__body {
    padding: 0;
  }
}
</style>
