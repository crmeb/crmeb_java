<template>
  <div class="article-box" v-if="defaults[configNme]">
    <div class="title-bar">文章列表</div>
    <div class="list-box">
      <draggable class="dragArea list-group" :list="defaults[configNme].list" group="peoples" handle=".move-icon">
        <div class="item" v-for="(item, index) in defaults[configNme].list" :key="index">
          <div class="move-icon">
            <span class="iconfont iconxingzhuangjiehe"></span>
          </div>
          <div class="img-box">
            <img :src="item.image_input[0]" alt="" v-if="item.image_input && item.image_input.length" />
            <div class="empty-img" v-else>
              <i class="el-icon-picture"></i>
            </div>
          </div>
          <div class="info">
            <div class="name line1">{{ item.title }}</div>
          </div>
          <span class="iconfont iconshanchu3" @click.stop="bindDelete(index)"></span>
        </div>
      </draggable>
      <div class="add-btn" @click="modals = true">
        <el-button class="btn"><span class="iconfont iconjiahao1"></span>添加</el-button>
      </div>
    </div>

    <el-dialog
      :visible.sync="modals"
      title="文章列表"
      class="paymentFooter"
      width="900px"
      :destroy-on-close="true"
      :close-on-click-modal="false"
    >
      <div class="article-manager">
        <div class="padding-add">
          <el-form
            ref="artFrom"
            :model="artFrom"
            label-width="80px"
            label-position="right"
            @submit.native.prevent
            inline
          >
            <el-form-item label="文章分类：" label-for="cid">
              <el-cascader
                v-model="artFrom.cid"
                placeholder="请选择"
                class="treeSel"
                @change="handleCheckChange"
                :options="treeData"
                :props="props"
                style="width: 250px"
                clearable
              >
              </el-cascader>
            </el-form-item>
            <el-form-item label="文章搜索：" label-for="title">
              <el-input
                clearable
                placeholder="接口暂不支持搜索"
                v-model="artFrom.title"
                class="form_content_width"
                disabled
              />
            </el-form-item>
          </el-form>
        </div>
        <el-table
          :data="cmsList"
          ref="table"
          class="mt14"
          v-loading="loading"
          highlight-current-row
          no-userFrom-text="暂无数据"
          no-filtered-userFrom-text="暂无筛选结果"
          height="400"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55"> </el-table-column>
          <el-table-column label="ID" width="80" prop="id"> </el-table-column>
          <el-table-column label="文章图片" min-width="90">
            <template slot-scope="scope">
              <div class="tabBox_img" v-if="scope.row.image_input && scope.row.image_input.length">
                <img :src="scope.row.image_input[0]" />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="文章名称" min-width="130" prop="title"> </el-table-column>
          <el-table-column label="分类" min-width="130" prop="catename"> </el-table-column>
          <el-table-column label="时间" min-width="130">
            <template slot-scope="scope">
              <span>{{ scope.row.add_time | formatDate }}</span>
            </template>
          </el-table-column>
        </el-table>
        <div class="acea-row row-right page" style="margin-top: 20px">
          <el-pagination
            v-if="total"
            :total="total"
            :current-page.sync="artFrom.page"
            :page-size.sync="artFrom.limit"
            layout="prev, pager, next"
            @current-change="pageChange"
          />
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="modals = false">取 消</el-button>
        <el-button type="primary" @click="addSelectedArticles">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import vuedraggable from 'vuedraggable';
import { getArticleList, themeArticleCategory } from '@/api/theme';
import { formatDate } from '@/utils/validate';

export default {
  name: 'c_article',
  props: {
    configObj: {
      type: Object,
    },
    configNme: {
      type: String,
    },
  },
  components: {
    draggable: vuedraggable,
  },
  filters: {
    formatDate(time) {
      if (!time) return '';
      if (typeof time === 'string') return time;
      if (time !== 0) {
        let date = new Date(time * 1000);
        return formatDate(date, 'yyyy-MM-dd hh:mm');
      }
    },
  },
  data() {
    return {
      modals: false,
      defaults: {},
      loading: false,
      artFrom: {
        cid: '',
        title: '',
        page: 1,
        limit: 10,
      },
      total: 0,
      cmsList: [],
      treeData: [],
      props: {
        value: 'id',
        label: 'title',
        emitPath: false,
        checkStrictly: true,
      },
      multipleSelection: [],
    };
  },
  created() {
    this.defaults = this.configObj;
    this.getClass();
  },
  methods: {
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    addSelectedArticles() {
      if (this.multipleSelection.length === 0) {
        return this.$message.warning('请至少选择一篇文章');
      }
      let list = this.defaults[this.configNme].list;
      let newItems = [];
      this.multipleSelection.forEach((item) => {
        let exists = list.some((i) => i.id === item.id);
        if (!exists) {
          newItems.push(item);
        }
      });
      if (newItems.length === 0) {
        return this.$message.warning('您选择的文章已存在');
      }
      this.defaults[this.configNme].list = list.concat(newItems);
      this.$message.success('添加成功');
      this.modals = false;
    },
    // 获取文章列表
    getList() {
      this.loading = true;
      getArticleList(this.artFrom)
        .then((res) => {
          this.cmsList = res.list || (res.data && res.data.list) || [];
          this.total = res.count || (res.data && res.data.count) || 0;
          this.loading = false;
        })
        .catch((res) => {
          this.loading = false;
          this.$message.error((res && (res.msg || res.message)) || '文章列表获取失败');
        });
    },
    // 获取分类
    getClass() {
      themeArticleCategory()
        .then((res) => {
          this.treeData = this.formatCategory(res.data);
          this.treeData.unshift({ id: '', title: '全部' });
        })
        .catch((res) => {
          this.$message.error((res && (res.msg || res.message)) || '文章分类获取失败');
        });
    },
    formatCategory(list) {
      return (Array.isArray(list) ? list : []).map((item) => {
        return {
          id: item.id,
          title: item.title,
          children: item.children ? this.formatCategory(item.children) : null,
        };
      });
    },
    // 选择分类
    handleCheckChange(data) {
      this.artFrom.cid = data || '';
      this.artFrom.page = 1;
      this.getList();
    },
    // 搜索
    userSearchs() {
      this.artFrom.page = 1;
      this.getList();
    },
    // 分页
    pageChange(e) {
      this.artFrom.page = e;
      this.getList();
    },
    // 选择文章
    selectArticle(row) {
      // 检查是否已存在
      let list = this.defaults[this.configNme].list;
      let exists = list.some((item) => item.id === row.id);
      if (exists) {
        this.$message.warning('该文章已添加');
        return;
      }
      this.defaults[this.configNme].list.push(row);
      this.$message.success('添加成功');
    },
    // 删除文章
    bindDelete(index) {
      this.defaults[this.configNme].list.splice(index, 1);
    },
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.defaults = nVal;
      },
      immediate: true,
      deep: true,
    },
    modals(val) {
      if (val) {
        this.getList();
      }
    },
  },
};
</script>

<style scoped lang="scss">
::v-deep .el-checkbox {
  margin-bottom: 0 !important;
}
.article-box {
  display: flex;
  padding: 0 15px 20px 15px;
  .title-bar {
    color: #fff;
    font-size: 12px;
    margin-right: 30px;
  }
  .list-box {
    flex: 1;
    .item {
      position: relative;
      display: flex;
      align-items: center;
      width: 100%;
      height: 40px;
      margin-bottom: 4px;
      background: #fff;
      border-radius: 4px;
      padding: 0 10px;
      box-sizing: border-box;
      &.sortable-chosen,
      &.sortable-drag,
      &.sortable-ghost {
        background: #f7f7f7 !important;
      }
      .move-icon {
        cursor: move;
        margin-right: 10px;
        .iconfont {
          color: #ddd;
          font-size: 16px;
        }
      }
      .img-box {
        width: 30px;
        height: 30px;
        margin-right: 10px;
        position: relative;
        img {
          width: 100%;
          height: 100%;
          border-radius: 4px;
        }
        .empty-img {
          width: 100%;
          height: 100%;
          background: #f5f5f5;
          border-radius: 4px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #ccc;
          font-size: 14px;
        }
      }
      .info {
        flex: 1;
        overflow: hidden;
        max-width: 170px;
        .name {
          font-size: 12px;
          color: #333;
        }
      }
      .iconshanchu3 {
        display: none;
        color: #999999;
        font-size: 16px;
        cursor: pointer;
        margin-left: 10px;
      }
      &:hover {
        .iconshanchu3 {
          display: block;
        }
      }
    }
  }
  .add-btn {
    margin-left: 15px;
    .iconjiahao1 {
      font-size: 12px;
      color: #999;
      margin-right: 5px;
    }
    .btn {
      width: 100%;
    }
  }
}

.tabBox_img {
  width: 36px;
  height: 36px;
  border-radius: 4px;
  cursor: pointer;
  img {
    width: 100%;
    height: 100%;
  }
}
</style>
