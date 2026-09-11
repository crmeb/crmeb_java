<template>
  <div>
    <div class="i-layout-page-header">
      <router-link :to="{ path: '/design/my_theme' }"
        ><el-button class="mr20">返回</el-button></router-link
      >
      <span class="ivu-page-header-title mr20">页面设计</span>
    </div>
    <el-card :bordered="false" shadow="never" class="ivu-mt">
      <div class="flex-wrapper">
        <!-- :src="iframeUrl" -->
        <iframe class="iframe-box" :src="iframeUrl" frameborder="0" ref="iframe"></iframe>
        <div>
          <div class="content">
            <rightConfig :name="configName" :pageId="pageId"></rightConfig>
          </div>
        </div>
        <links></links>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue';
import { useRoute } from 'vue-router';
import { diyGetInfo, diySave } from '@/api/theme';
import rightConfig from '@/components/rightConfig/index';
import links from './links';
import Setting from '@/utils/settingMer';

defineOptions({ name: 'index' });

const route = useRoute();
const { proxy } = getCurrentInstance();

const configName = ref('');
const iframeUrl = ref('');
const setConfig = ref('');
const updataConfig = ref('');
const pageId = ref(0);
const iframe = ref(null);

let rPageId = route.query.id;
let names = route.query.name;
setConfig.value = '' + '/' + names + '/setConfig';
updataConfig.value = '' + '/' + names + '/updataConfig';
pageId.value = parseInt(rPageId);
iframeUrl.value = `${location.origin}/pages/index/index?mdType=iframeWindow`;
diyGetInfo(parseInt(rPageId)).then((datas) => {
  let data = datas.data.info.value;
  upData(data);
});

//接收iframe值
function handleMessage(event) {
  if (event.data.name) {
    configName.value = event.data.name;
    add(event.data.name);
  }
}
function add(data) {
  proxy.$store.commit(setConfig.value, data);
}
function upData(data) {
  proxy.$store.commit(updataConfig.value, data);
}

onMounted(() => {
  //监听子页面给当前页面传值
  window.addEventListener('message', handleMessage, false);
});
</script>

<style lang="scss" scoped>
.content {
  width: 450px;
}
.flex-wrapper {
  display: flex;
}
.iframe-box {
  width: 375px;
  height: 700px;
  /* border: 1px solid #ddd; */
  border-radius: 4px;
  box-shadow: 0 0 7px #cccccc;
}
.right-box {
  width: 400px;
  margin-left: 50px;
  border: 1px solid #ddd;
  border-radius: 4px;
  .title-bar {
    width: 100%;
    height: 38px;
    line-height: 38px;
    padding-left: 24px;
    color: #333;
    border-radius: 4px;
    border-bottom: 1px solid #eee;
  }
}
</style>
