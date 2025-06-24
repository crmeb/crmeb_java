<template>
  <!--底部导航，此组件只在微页面中使用，并且生效。首页自定义导航在页面设计中配置-->
  <div v-if="configData" class="borderPadding">
    <div class="acea-row">
      <h4 class="mb10">在此处设置底部导航，只在微页面中展示，首页设置自定义导航需要在页面设计中设置。</h4>
    </div>

    <p class="tips">图片建议宽度81*81px；鼠标拖拽左侧圆点可调整导航顺序</p>
    <draggable class="dragArea list-group" :list="configData.list" group="peoples" handle=".iconfont">
      <div class="box-item" v-for="(item, index) in configData.list" :key="index">
        <div class="left-tool">
          <span class="iconfont icontuozhuaitubiao"></span>
        </div>
        <div class="right-wrapper">
          <div class="img-wrapper mb10">
            <div class="img-item" @click="modalPicTap(index, 'checked')">
              <img :src="item.checked" alt="" v-if="item.checked" />
              <p class="txt" v-if="item.checked">选中</p>
              <div class="img-box" v-else>
                <div class="upload-box"><i class="el-icon-camera-solid" style="font-size: 30px" /></div>
                <p class="txt">选中</p>
              </div>
            </div>
            <div class="img-item" @click="modalPicTap(index, 'unchecked')">
              <img :src="item.unchecked" alt="" v-if="item.unchecked" />
              <p class="txt" v-if="item.unchecked">未选中</p>
              <div class="img-box" v-else>
                <div class="upload-box"><i class="el-icon-camera-solid" style="font-size: 30px" /></div>
                <p class="txt">未选中</p>
              </div>
            </div>
          </div>
          <div class="c_row-item mb10">
            <el-col class="label" :span="4"> 名称 </el-col>
            <el-col :span="19" class="slider-box">
              <el-input size="small" v-model="item.name" maxlength="4" placeholder="不超过4个字" />
            </el-col>
          </div>
          <div class="c_row-item">
            <el-col class="label" :span="4"> 链接 </el-col>
            <el-col :span="19" class="slider-box">
              <div @click="getLink(index)">
                <el-input size="small" icon="ios-arrow-forward" v-model="item.link" readonly placeholder="请选择链接">
                  <el-button slot="append" icon="el-icon-arrow-right"></el-button>
                </el-input>
              </div>
            </el-col>
          </div>
        </div>
        <div class="del-box" @click="deleteMenu(index)">
          <i class="el-icon-error" style="font-size: 20px"></i>
        </div>
      </div>
    </draggable>
    <el-button
      class="add-btn"
      icon="el-icon-plus"
      plain
      @click="addMenu"
      v-if="configData.list && configData.list.length < 5"
      >添加图文导航</el-button
    >
    <linkaddress ref="linkaddres" @linkUrl="linkUrl"></linkaddress>
  </div>
</template>

<script>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import vuedraggable from 'vuedraggable';
import linkaddress from '@/components/linkaddress';
export default {
  name: 'c_foot',
  props: {
    configObj: {
      type: Object,
      default: function () {
        return {};
      },
    },
    configNme: {
      type: String,
      default: '',
    },
    index: {
      type: null,
    },
  },
  components: {
    linkaddress,
    draggable: vuedraggable,
  },
  data() {
    return {
      configData: {},
      modalPic: false,
      itemIndex: 0,
      itemChildIndex: 0,
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
      defaults: {},
    };
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.defaults = nVal;
        this.configData = nVal[this.configNme];
      },
      deep: true,
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.defaults = this.configObj;
      this.configData = this.configObj[this.configNme];
    });
  },
  methods: {
    linkUrl(e) {
      this.configData.list[this.itemIndex].link = e;
    },
    getLink(index) {
      this.itemIndex = index;
      this.$refs.linkaddres.dialogVisible = true;
    },
    // 点击图文封面
    modalPicTap(index, name) {
      let _this = this;
      _this.$modalUpload(function (img) {
        if (!img) return;
        if (name === 'checked') {
          _this.configData.list[index].checked = img[0].sattDir;
        } else {
          _this.configData.list[index].unchecked = img[0].sattDir;
        }
      });
    },
    // 添加模块
    addMenu() {
      let obj = {
        checked: '',
        unchecked: '',
        name: '自定义',
        link: '',
      };
      this.configData.list.push(obj);
    },
    deleteMenu(index) {
      this.$confirm('是否确定删除该菜单?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          this.configData.list.splice(index, 1);
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消',
          });
        });
    },
  },
};
</script>

<style scoped lang="scss">
.img-box {
  position: relative;
  width: 64px;
  height: 64px;
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
  width: 100%;
  height: 100%;
  background: #fff;
  font-size: 12px;
  color: #cccccc;
  .iconfont {
    font-size: 16px;
  }
}
::v-deep.ivu-input {
  font-size: 13px !important;
}
.tips {
  font-size: 12px;
  color: #bbbbbb;
}

.box-item {
  position: relative;
  display: flex;
  margin-top: 20px;
  padding: 16px 20px;
  width: 100%;
  background: #f9f9f9;
  border-radius: 3px 3px 3px 3px;
  .del-box {
    position: absolute;
    right: -9px;
    top: -12px;
    cursor: pointer;
    color: #999;
    .iconfont {
      color: #999;
      font-size: 30px;
    }
  }
  .left-tool {
    display: flex;
    align-items: center;
    justify-content: center;
    .iconfont {
      color: #ddd;
      font-size: 24px;
      cursor: move;
    }
  }

  .right-wrapper {
    margin-left: 20px;
    flex: 1;
    .img-wrapper {
      display: flex;
      .img-item {
        position: relative;
        width: 65px;
        height: 64px;
        margin-right: 20px;
        cursor: pointer;
      }
      img {
        display: block;
        width: 100%;
        height: 100%;
      }
      .txt {
        position: absolute;
        left: 0;
        bottom: 0;
        width: 100%;
        height: 22px;
        line-height: 22px;
        text-align: center;
        background: rgba(0, 0, 0, 0.4);
        color: #fff;
        font-size: 12px;
      }
    }
  }
}

.add-btn {
  margin-top: 20px;
  width: 100%;
  height: 40px;
}
</style>
