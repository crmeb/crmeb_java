<template>
  <div class="goodClass">
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
        <div class="pictrue" :style="{ backgroundColor: themeColor }"><img :src="item.image" /></div>
        <div class="name">{{ item.name }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { themeInfo, themeSave } from '@/api/theme';
import setting from '@/utils/settingMer';

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
      themeColor: '',
    };
  },
  created() {
    this.getInfo();
    this.getTheme();
  },
  methods: {
    getTheme() {
      themeInfo(this.$route.query.id, 'theme').then((res) => {
        this.themeColor = res.data ? res.data.theme_color : '#E93323';
      });
    },
    getInfo() {
      themeInfo(this.$route.query.id, 'category').then((res) => {
        this.activeStyle = res.data.status ? res.data.status - 1 : 0;
      });
    },
    selectTap(index) {
      this.activeStyle = index;
    },
    saveOnly(num) {
      this.$emit('parentFun', true);
      this.activeStyle = num == 1 ? 0 : this.activeStyle;
      themeSave(this.$route.query.id, {
        type: 'category',
        value: num == 1 ? 1 : this.activeStyle + 1,
      }).then((res) => {
        if (this.$route.query.id == 0) {
          this.$router.replace({ query: { ...this.$route.query, id: res.data.id } });
        }
        this.$message.success(res.msg);
      });
    },
    saveAndClose() {
      // 先触发父组件事件
      this.$emit('parentFun', true);

      // 保存数据
      themeSave(this.$route.query.id, {
        type: 'category',
        value: this.activeStyle + 1,
      })
        .then((res) => {
          // 如果是新建（id为0），更新路由参数
          if (this.$route.query.id == 0) {
            this.$router.replace({ query: { ...this.$route.query, id: res.data.id } });
          }

          // 显示成功消息
          this.$message.success(res.msg);

          // 保存成功后跳转回主题列表页面
          this.$router.push('/design/my_theme');
        })
        .catch((err) => {
          // 保存失败时的处理
          this.$message.error(err.msg || '保存失败');
        });
    },
  },
};
</script>
<style lang="scss" scoped>
.goodClass {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
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
