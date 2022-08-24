package com.zbkj.front.pub;

import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.utils.RestTemplateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * 后台管理员表 前端控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/public/wechat")
@Api(tags = "企业微信消息推送")
public class WeChatPushController {

    private static String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=";

    @Resource
    private RestTemplateUtil restTemplateUtil;


    /**
     * 新增后台管理员表
     * @param message string message
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "gitlab钩子")
    @RequestMapping(value = "/gitlab", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name="message", value="推送消息内容"),
            @ApiImplicitParam(name="token", value="企业微信群token"),
    })
    public CommonResult<Object> gitlab(@RequestBody String message,
                                       @RequestParam(name = "token", required = true) String token){

        Map<String, Object> map = new HashMap<>();
        map.put("msgtype", "text");

        Map<String, Object> text = new HashMap<>();

        //需要@的人
        ArrayList<Object> people = new ArrayList<>();
        people.add("@all");
        text.put("mentioned_list", people);

        //gitlab 动作标签
        JSONObject jsonObject = JSONObject.parseObject(message);
        String action = jsonObject.getString("object_kind");
        String content;
        switch(action){
            case "push":
                content = jsonObject.getJSONArray("commits").getJSONObject(0).getJSONObject("author").getString("name") + " " +
                        action + " " +
                        jsonObject.getString("ref").replace("refs/heads/", "") +
                        "\n 备注：\n" +
                        jsonObject.getJSONArray("commits").getJSONObject(0).getString("message");
                break;
            case "tag_push":
                content = jsonObject.getString("user_name") + " " +
                        action + " " +
                        jsonObject.getString("ref").replace("refs/heads/", "") +
                        "\n 备注：\n" +
                        jsonObject.getJSONArray("commits").getJSONObject(0).getString("message");
                break;
            case "note":
                String author = "未知用户";
                if(jsonObject.containsKey("commit")){
                    author = jsonObject.getJSONObject("commit").getJSONObject("author").getString("name");
                }

                if(jsonObject.containsKey("last_commit")){
                    author = jsonObject.getJSONObject("last_commit").getJSONObject("author").getString("name");
                }

                content = author +
                        " 提交代码到 " +
                        jsonObject.getJSONObject("project").getString("default_branch") +
                        "\n 备注：\n" +
                        jsonObject.getJSONObject("object_attributes").getString("note");
                break;
            case "merge_request":
                content = jsonObject.getJSONObject("object_attributes").getJSONObject("assignee").getString("name") + " " +
                        "合并代码， 从 " +
                        jsonObject.getJSONObject("object_attributes").getString("source_branch") + " ---> " +
                        jsonObject.getJSONObject("object_attributes").getString("target_branch") +
                        "\n 备注：\n" +
                        jsonObject.getJSONObject("object_attributes").getJSONObject("last_commit").getString("message");
                break;
            default:
                content = "gitlab 项目有更新";
        }

        text.put("content", content);
        map.put("text", text);
        String result = restTemplateUtil.postMapData(url + token, map);
        return CommonResult.success(JSONObject.parseObject(result));
    }

    /**
     * 新增后台管理员表
     * @param message string message
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "消息推送")
    @RequestMapping(value = "/push", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="message", value="推送消息内容"),
            @ApiImplicitParam(name="token", value="企业微信群token"),
    })
    public CommonResult<Object> push(@RequestParam(name = "message") String message,
                                    @RequestParam(name = "token") String token){

        Map<String, Object> map = new HashMap<>();
        map.put("msgtype", "text");

        Map<String, Object> text = new HashMap<>();

        //需要@的人
        ArrayList<Object> people = new ArrayList<>();
        people.add("@all");
        text.put("mentioned_list", people);
        text.put("content", message);
        map.put("text", text);
        String result = restTemplateUtil.postMapData(url + token, map);
        return CommonResult.success(JSONObject.parseObject(result));
    }
}



