<template>
  <!--上传图片-->
  <div class="upload_img acea-row row-between borderPadding" v-if="configData && configData.isShow === 1">
    <div class="acea-row row-column row-middle align-left">
      <div class="header">{{ configData.title }}</div>
      <div class="title">{{ configData.tips }}</div>
    </div>
    <div class="box" @click="modalPicTap">
      <img :src="configData.url" alt="" v-if="configData.url" />
      <div class="upload-box" v-else><span class="iconfont icon-tianjia1" /></div>
      <span
        class="iconfont-diy iconfont icon-tianjia1"
        @click.stop="bindDelete"
        v-if="configData.url && configData.type"
      ></span>
    </div>
  </div>
</template>

<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import { mapState } from 'vuex';
export default {
  name: 'c_upload_img',
  computed: {
    ...mapState({
      tabVal: (state) => state.admin.mobildConfig.searchConfig.data.tabVal,
    }),
  },
  props: {
    configObj: {
      type: Object,
    },
    configNme: {
      type: String,
    },
  },
  data() {
    return {
      defaultList: [
        {
          name: 'a42bdcc1178e62b4694c830f028db5c0',
          url: 'https://o5wwk8baw.qnssl.com/a42bdcc1178e62b4694c830f028db5c0/avatar',
        },
        {
          name: 'bc7521e033abdd1e92222d733590f104',
          url: 'https://o5wwk8baw.qnssl.com/bc7521e033abdd1e92222d733590f104/avatar',
        },
      ],
      defaults: {},
      configData: {},
      modalPic: false,
      isChoice: '单选',
      gridBtn: {
        xl: 4,
        lg: 8,
        md: 8,
        sm: 8,
        xs: 8,
      },
      gridPic: {
        xl: 6,
        lg: 8,
        md: 12,
        sm: 12,
        xs: 12,
      },
      activeIndex: 0,
    };
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.defaults = nVal;
        this.configData = nVal[this.configNme];
      },
      immediate: true,
      deep: true,
    },
  },
  created() {
    this.defaults = this.configObj;
    this.configData = this.configObj[this.configNme];
  },
  methods: {
    bindDelete() {
      this.configData.url = '';
    },
    // 点击图文封面
    modalPicTap() {
      const _this = this;
      this.$modalUpload(function (img) {
        if (!img) return;
        _this.configData.url = img[0].sattDir;
      });
    },
  },
};
</script>

<style scoped lang="scss">
.header {
  font-size: 12px;
  color: #999;
  // margin-bottom: 9px;
}
.upload_img {
  align-items: center;
}
.title {
  margin: 10px 0 5px;
  padding-bottom: 3px;
  /*border-bottom:1px solid rgba(0,0,0,0.05);*/
  font-size: 12px;
  color: #bbbbbb;
}
.box {
  width: 64px;
  height: 64px;
  border-radius: 4px 4px 4px 4px;
  margin-bottom: 20px;
  position: relative;
  cursor: pointer;
  .iconfont-diy,
  .iconfont {
    position: absolute;
    color: #999;
  }

  img {
    width: 100%;
    height: 100%;
  }
}

.upload-box {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  width: 64px;
  height: 64px;
  border-radius: 4px 4px 4px 4px;
  opacity: 1;
  border: 1px solid #eeeeee;
  font-size: 12px;
  color: #cccccc;
  .iconfont {
    font-size: 16px;
  }
}
.align-left {
  align-items: flex-start;
}
</style>
