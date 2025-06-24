<template>
  <!--选项卡组件-->
  <div class="hot_imgs borderPadding">
    <div class="list-box">
      <draggable class="dragArea list-group" :list="configData.list" group="peoples" handle=".move-icon">
        <div v-for="(item, index) in configData.list" :key="index" class="acea-row row-middle">
          <div class="move-icon">
            <i class="iconfont icontuozhuaitubiao" style="font-size: 20px; color: #dddddd" />
          </div>
          <div class="item" :class="activeIndex === index ? 'checked' : ''" @click="onChecked(item, index)">
            <div class="info">
              <div class="info-item">
                <span>{{ item.val }} </span>
              </div>
            </div>
          </div>
          <div class="delete" @click.stop="bindDelete(index)">
            <i class="el-icon-error" style="font-size: 20px" />
          </div>
        </div>
      </draggable>
    </div>
    <template v-if="configData.list">
      <div class="add-btn">
        <el-button size="small" icon="el-icon-plus" plain style="width: 100%; height: 35px" @click="addBox"
          >添加选项卡</el-button
        >
      </div>
    </template>
    <div class="title-bar mt10">选项卡内容</div>
    <div class="c_row-item">
      <div class="label">
        <span>选项名称</span>
      </div>
      <div class="slider-box">
        <el-input
          v-model="tabVal"
          placeholder="请输入选项名称（最多10个字）"
          :step="1"
          maxlength="10"
          minlength="1"
          style="text-align: right"
          @input="onInput"
        ></el-input>
      </div>
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
import vuedraggable from 'vuedraggable';
export default {
  name: 'c_tab_input',
  props: {
    configObj: {
      type: Object,
    },
    configNme: {
      type: String,
    },
    index: {
      type: null,
    },
    isRub: {
      type: Boolean,
      default: false,
    },
  },
  components: {
    draggable: vuedraggable,
  },
  data() {
    return {
      tabVal: '',
      defaults: {},
      configData: {},
      menus: [],
      list: [
        {
          title: '',
          val: '',
        },
      ],
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
      indexLast: 0,
      lastObj: {},
      linkList: [],
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.defaults = this.configObj;
      this.configData = this.configObj[this.configNme];
      this.tabVal = this.configData.list[0].val;
      this.activeIndex = this.configData.tabVal;
      // if (this.configData && this.configData.isSmall) this.getPageLinks();
    });
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
  methods: {
    onInput(value) {
      this.configData.list[this.activeIndex].val = value;
    },
    onChecked(item, index) {
      this.activeIndex = index;
      this.tabVal = item.val;
      this.$emit('getConfig', { name: 'tab_input', values: index });
    },
    addBox() {
      this.activeIndex += 1; //默认选中新填的tab
      this.configData.list.push({ val: '选项卡' });
      this.$emit('getConfig', { name: 'add_tab', values: this.configData.list });
    },
    onBlur() {
      let data = this.defaults.menuConfig;
      this.defaults.picStyle.picList[this.defaults.picStyle.tabVal].link = data.list[0].info[0].val;
    },
    // 删除
    bindDelete(index) {
      this.configData.list.splice(index, 1);
      this.$emit('getConfig', { name: 'del_tab', values: this.configData.list, index: index });
    },
  },
};
</script>

<style scoped lang="scss">
.acea-row {
  position: relative;
}
.delete {
  position: absolute;
  right: -7px;
  top: -8px;
  color: #cccccc;
}
.label {
  font-size: 12px;
  color: #999;
}
.slider-box {
  width: 81%;
}
.checked {
  border: 1px solid var(--prev-color-primary) !important;
}
.title-bar {
  height: 38px;
  line-height: 38px;
  color: #333;
  font-size: 14px;
  position: relative;
}
.hot_imgs {
  margin-bottom: 20px;
  .title {
    padding: 13px 0;
    color: #bbbbbb;
    font-size: 12px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  }

  .list-box {
    .item {
      width: 473px;
      position: relative;
      display: flex;
      height: 35px;
      line-height: 35px;
      margin-left: 10px;
      margin-bottom: 10px;
      border: 1px solid #dcdfe6;
      .move-icon {
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
      }

      .img-box {
        position: relative;
        width: 64px;
        height: 64px;
        margin-left: 20px;
        img {
          width: 100%;
          height: 100%;
        }
      }
    }
    .info {
      flex: 1;
      margin-left: 22px;
      .info-item {
        display: flex;
        align-items: center;
        span {
          /*width: 40px;*/
          font-size: 13px;
        }
        .input-box {
          flex: 1;
          ::v-deep.ivu-input {
            font-size: 13px !important;
          }
        }
      }
    }

    .delect-btn {
      position: absolute;
      right: -7px;
      top: -12px;
      .iconfont-diy,
      .iconfont {
        font-size: 25px;
        color: #999;
      }
    }
  }
  .add-btn {
    margin-top: 10px;
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
.iconfont-diy,
.iconfont {
  color: #dddddd;
  font-size: 28px;
}
.el-select {
  display: block;
}
.move-icon {
  margin-bottom: 10px;
}
</style>
