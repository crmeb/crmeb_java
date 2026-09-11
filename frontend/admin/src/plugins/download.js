import axios from 'axios';
import { getToken } from '@/utils/auth';
import { ElMessage as Message } from '@/utils/elementPlusFeedback';

const baseURL = import.meta.env.VITE_APP_BASE_API;

export default {
  name(name, isDelete = true) {
    var url = baseURL + '/common/download?fileName=' + encodeURI(name) + '&delete=' + isDelete;
    axios({
      method: 'get',
      url: url,
      responseType: 'blob',
      headers: { Authorization: 'Bearer ' + getToken() },
    }).then(async (res) => {
      const isLogin = await this.blobValidate(res.data);
      if (isLogin) {
        const blob = new Blob([res.data]);
        this.saveAs(blob, decodeURI(res.headers['download-filename']));
      } else {
        Message.error('无效的会话，或者会话已过期，请重新登录。');
      }
    });
  },
  resource(resource) {
    var url = baseURL + '/common/download/resource?resource=' + encodeURI(resource);
    axios({
      method: 'get',
      url: url,
      responseType: 'blob',
      headers: { Authorization: 'Bearer ' + getToken() },
    }).then(async (res) => {
      const isLogin = await this.blobValidate(res.data);
      if (isLogin) {
        const blob = new Blob([res.data]);
        this.saveAs(blob, decodeURI(res.headers['download-filename']));
      } else {
        Message.error('无效的会话，或者会话已过期，请重新登录。');
      }
    });
  },
  zip(url, name) {
    var url = baseURL + url;
    axios({
      method: 'get',
      url: url,
      responseType: 'blob',
      headers: { Authorization: 'Bearer ' + getToken() },
    }).then(async (res) => {
      const isLogin = await this.blobValidate(res.data);
      if (isLogin) {
        const blob = new Blob([res.data], { type: 'application/zip' });
        this.saveAs(blob, name);
      } else {
        Message.error('无效的会话，或者会话已过期，请重新登录。');
      }
    });
  },
  async saveAs(text, name, opts) {
    // file-saver 改为动态加载，避免进入口 chunk（下载为用户触发，非首屏）
    const { saveAs } = await import('file-saver');
    saveAs(text, name, opts);
  },
  async blobValidate(data) {
    try {
      const text = await data.text();
      JSON.parse(text);
      return false;
    } catch (error) {
      return true;
    }
  },
};
