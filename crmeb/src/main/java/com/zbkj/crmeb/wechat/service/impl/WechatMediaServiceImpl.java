package com.zbkj.crmeb.wechat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.UploadWeChatMediaUtil;
import com.zbkj.crmeb.wechat.service.WeChatService;
import com.zbkj.crmeb.wechat.service.WechatMediaService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mr.Zhang
 * @Description WechatMediaServiceImpl 接口实现
 * @since 2020-04-18
 */
@Data
@Service
public class WechatMediaServiceImpl implements WechatMediaService {

    @Autowired
    private WeChatService weChatService;

    private static final long maxSizeImage = 1024 * 1024 * 10;

    private static final String suffixNameImage = "bmp,png,jpeg,jpg,gif";

    private static final long maxSizeVoice = 1024 * 1024 * 2;

    private static final String suffixNameVoice = "mp3,wma,wav,amr";



    /**
     * 上传素材到微信素材库
     * @param file MultipartFile 上传文件
     * @param type String 类型 媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）
     * @author Mr.Zhang
     * @since 2020-04-18
     * @return String
     */
    public  Map<String, String> upload(MultipartFile file, String type) throws Exception{
        try{
            String[] split = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
            String suffixName = split[split.length-1];
            isValidPic(file.getSize(), suffixName, type);

            String url = weChatService.getUploadMedia() + "&type=" + type;
            JSONObject response = UploadWeChatMediaUtil.uploadFile(url, file.getResource().getInputStream(), file.getOriginalFilename());
            if(null == response || !response.containsKey("media_id")){
                assert response != null;
                throw new CrmebException("素材上传失败" + response.getString("errmsg"));
            }

            Map<String, String> map = new HashMap<>();
            map.put("mediaId", response.getString("media_id"));
            map.put("url", response.getString("url"));
            map.put("name", file.getOriginalFilename().replace(suffixName, ""));

            return map;

//            String mediaId =  response.getString("media_id") ; //媒体文件id
//            int count = weChatService.getMediaCount(type); //总数
//
//            if(count == 0){
//                throw new CrmebException("素材图片上传失败, 没有从微信服务器上找到相关信息");
//            }
//
//            //获取最新的5个素材来对比
//            JSONObject info = weChatService.getMediaInfo(type, count-1, 5); //微信返回数据是倒序排序，所以拿到最新的5个即可，这里考虑到并发上传的问题，如果不存在并发只需要拿最新的一个即可
//            if(null == info){
//                throw new CrmebException("素材图片上传失败, 没有从微信服务器上找到相关信息1");
//            }
//            String mediaUrl = "";
//            List<MediaDetailVo> list = JSONObject.parseArray(info.toJSONString(), MediaDetailVo.class);
//            for (MediaDetailVo mediaDetailVo : list) {
//                if(mediaId.equals(mediaDetailVo.getMediaId())){
//                    mediaUrl = mediaDetailVo.getUrl();
//                    break;
//                }
//            }
//
//            if(StringUtils.isBlank(mediaUrl)){
//                throw new CrmebException("素材图片上传失败, 没有从微信服务器上找到相关信息2");
//            }
//
//            Map<String, String> map = new HashMap<>();
//            map.put("mediaId", mediaId);
//            map.put("mediaUrl", mediaUrl);
//
//            return map;

        }catch (Exception e){
            throw new CrmebException(e.getMessage());
        }


    }


    /**
     * 是否符合微信规范
     * @param size long 文件大小
     * @param suffixName String 后缀名
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    private void isValidPic(long size, String suffixName, String type) {
        JSONObject config = getConfig();
        if(!config.containsKey(type)){
            throw new CrmebException("不支持此类型");
        }

        long supportSize = config.getJSONObject(type).getLong("size");
        if(supportSize < size){
            throw new CrmebException("文件大小不能超过" + supportSize);
        }
        String supportNameSuffix = config.getJSONObject(type).getString("suffix");
        List<String> suffixNameList = CrmebUtil.stringToArrayStr(supportNameSuffix);
        if(!suffixNameList.contains(suffixName)){
            throw new CrmebException("文件格式必须是" + supportSize);
        }
    }

    /**
     * 参数配置
     * @author Mr.Zhang
     * @since 2020-04-18
     * @return JSONObject
     */
    private JSONObject getConfig(){
        String data = "{" +
                "image:{size:\""+maxSizeImage+"\", suffix: \""+suffixNameImage+"\"}, " +
                "voice:{size:\""+maxSizeVoice+"\", suffix: \""+suffixNameVoice+"\"}" +
                "}";
        return JSONObject.parseObject(data);
    }

}
