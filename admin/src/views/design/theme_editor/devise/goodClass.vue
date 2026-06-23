<template>
  <div class="goodClass">
    <el-card class="h100" :bordered="false" shadow="never">
      <!-- <div class="title">页面设置</div> -->
      <div class="list acea-row row-top">
        <div
          class="item"
          :class="activeStyle == index ? 'on' : ''"
          v-for="(item, index) in classList"
          :key="index"
          v-db-click
          @click="selectTap(index)"
        >
          <div class="pictrue"><img :src="item.image" /></div>
          <div class="name">{{ item.name }}</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { colorChange, getColorChange } from '@/api/theme';
export default {
  name: 'goodClass',
  props: {},
  data() {
    return {
      classList: [
        { image: require('@/assets/imgs/moren.png'), name: '默认模板' },
        { image: require('@/assets/imgs/youxuan.png'), name: '模板1' },
        { image: require('@/assets/imgs/haowu.png'), name: '模板2' },
        { image: require('@/assets/imgs/shengxian.png'), name: '模板3' },
      ],
      activeStyle: '-1',
    };
  },
  created() {
    this.getInfo();
  },
  methods: {
    getInfo() {
      getColorChange('category').then((res) => {
        this.activeStyle = res.data.status ? res.data.status - 1 : 0;
      });
    },
    selectTap(index) {
      this.activeStyle = index;
    },
    onSubmit(num) {
      this.$emit('parentFun', true);
      this.activeStyle = num == 1 ? 0 : this.activeStyle;
      colorChange(num == 1 ? 1 : this.activeStyle + 1, 'category')
        .then((res) => {
          this.$emit('parentFun', false);
          this.$message.success(res.msg);
        })
        .catch((err) => {
          this.$message.error(err.msg);
          this.$emit('parentFun', false);
        });
    },
  },
};
</script>
<style lang="scss" scoped>
.goodClass {
  .title {
    font-size: 14px;
    color: rgba(0, 0, 0, 0.85);
    position: relative;
    padding-left: 11px;
    font-weight: bold;
    &:after {
      position: absolute;
      content: ' ';
      width: 2px;
      height: 14px;
      background-color: var(--prev-color-primary);
      left: 0;
      top: 3px;
    }
  }
  .list {
    .item {
      width: 264px;
      margin: 0px 30px 0 0;
      cursor: pointer;
      .pictrue {
        width: 100%;
        height: 496px;
        border: 1px solid #eeeeee;
        border-radius: 10px;
        img {
          width: 100%;
          height: 100%;
          border-radius: 10px;
        }
      }
      .name {
        font-size: 13px;
        color: rgba(0, 0, 0, 0.85);
        margin-top: 16px;
        text-align: center;
      }
      &.on {
        .pictrue {
          border: 2px solid var(--prev-color-primary);
        }
        .name {
          color: var(--prev-color-primary);
        }
      }
    }
  }
}
</style>
