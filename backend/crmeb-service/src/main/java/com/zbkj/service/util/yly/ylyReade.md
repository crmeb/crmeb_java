下请求方法在子线程中执行

1. 下载下方的jar包，将jar导入项目中

2. 需要先对RequestMethod类进行init初始化设置，配置相关易联云信息
   RequestMethod.getInstance().init(client_id,client_secret);
   参数：* @param client_id 易联云颁发给开发者的应用ID
   * @param client_secret 易联云颁发给开发者的应用密钥

3. 开放型应用方法实例

   RequestMethod.getInstance().getCodeOpen(String redirect_uri);
   参数：redirect_uri 开发者自身的回调地址 (需要urlencode)

   RequestMethod.getInstance().getOpenAccessToken(String code)；
   参数：* @param code  授权码code

   极速授权 仅支持开放服务
   RequestMethod.getInstance().scanCodeModel(String machine_code,String qr_key);
   参数：* @param machine_code  易联云打印机终端号
   * @param qr_key  特殊密钥(有效期为300秒)

自有型新应用

获取Access Token
RequestMethod.getInstance().getAccessToken();

Refresh token更新Access Token
RequestMethod.getInstance().getRefreshAccessToken(String refresh_token);
参数：* @param refresh_token 更新access_token所需，有效时间35天

添加打印机
RequestMethod.getInstance().addPrinter( machine_code, msign, access_token);
参数：* @param machine_code 易联云打印机终端号
* @param msign 易联云打印机终端密钥
* @param access_token 授权的token 必要参数，有效时间35天

文本打印
RequestMethod.getInstance().printIndex(String access_token,String machine_code,String content,String origin_id)；
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号
* @param content 打印内容(需要urlencode)，排版指令详见打印机指令
* @param origin_id 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母 ，且在同一个client_id下唯一。详见商户订单号

图形打印
RequestMethod.getInstance().picturePrintIndex(String access_token,String machine_code,String picture_url,String origin_id)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号
* @param picture_url 线上图片地址,格式为 jpg，jpeg，png ， K4图片宽度不能超过384像素。理论上图片 （像素宽/8）*像素高 不能超过 100*1024。K5图片宽度不能超过108*8像素。理论上图片 （像素宽/8）*像素高 不能超过 200*1024。
* @param origin_id 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母 ，且在同一个client_id下唯一。详见商户订单号

面单打印
RequestMethod.getInstance().expressPrintIndex(String access_token,String machine_code,String content,String origin_id)；
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号
* @param content Json字符串
* @param origin_id 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母 ，且在同一个client_id下唯一。详见商户订单号

设置内置语音接口  
注意: 仅支持K4-WA、K4-GAD、K4-WGEAD、k6型号（除k6-wh外）
RequestMethod.getInstance().printerSetVoice(String access_token,String machine_code,String content,String is_file,String aid,String origin_id)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号
* @param content 播报内容 , 音量(1~9) , 声音类型(0,1,3,4) 组成json ! 示例 ["测试",9,0] 或者是在线语音链接! 语音内容请小于24kb
* @param is_file true or false , 判断content是否为在线语音链接，格式MP3
* @param aid 0~9 , 定义需设置的语音编号,若不提交,默认升序
* @param origin_id 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母 ，且在同一个client_id下唯一。详见商户订单号

删除内置语音接口  
注意: 仅支持K4-WA、K4-GAD、K4-WGEAD、k6型号（除k6-wh外）
RequestMethod.getInstance().printerDeleteVoice(String access_token,String machine_code,String aid,String origin_id)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号
* @param aid 0~9 , 定义需删除的语音编号
* @param origin_id 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母 ，且在同一个client_id下唯一。详见商户订单号

删除终端授权
RequestMethod.getInstance().printerDeletePrinter(String access_token,String machine_code)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号

添加应用菜单  
注意: 仅支持除k4-WA，k4-WH外的k4或w1机型．唤醒应用菜单请使用<MA>打印指令
RequestMethod.getInstance().printMenuAddPrintMenu(String access_token,String machine_code,String content)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号
* @param content json格式的应用菜单（其中url和菜单名称需要urlencode)

关机重启接口
RequestMethod.getInstance().printShutdownRestart(String access_token,String machine_code,String response_type)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号
* @param response_type 重启:restart,关闭:shutdown

声音调节接口
RequestMethod.getInstance().printSetSound(String access_token,String machine_code,String response_type,String voice)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号
* @param response_type 蜂鸣器:buzzer,喇叭:horn
* @param voice [0,1,2,3] 4种音量设置

获取机型打印宽度接口
RequestMethod.getInstance().printPrintInfo(String access_token,String machine_code)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号

获取机型软硬件版本接口
RequestMethod.getInstance().printGetVersion(String access_token,String machine_code)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号

取消所有未打印订单
RequestMethod.getInstance().printCancelAll(String access_token,String machine_code)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号

取消单条未打印订单
RequestMethod.getInstance().printCancelOne(String access_token,String machine_code,String order_id)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号
* @param order_id 通过打印接口返回的订单号

设置logo接口
RequestMethod.getInstance().printSetIcon(String access_token,String machine_code,String img_url)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号
* @param img_url 图片地址,图片宽度最大为350px,文件大小不能超过40Kb

取消logo接口
RequestMethod.getInstance().printDeleteIcon(String access_token,String machine_code)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号

打印方式接口
RequestMethod.getInstance().printBtnPrint(String access_token,String machine_code,String response_type)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号
* @param response_type 开启:btnopen,关闭:btnclose; 按键打印

接单拒单设置接口
RequestMethod.getInstance().printGetOrder(String access_token,String machine_code,String response_type)
参数；* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号
* @param response_type 开启:open,关闭:close

设置推送url接口
RequestMethod.getInstance().oauthSetPushUrl(String access_token,String machine_code,String cmd,String url,String status)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号
* @param cmd 打印完成标识oauth_finish，接单拒单标识oauth_getOrder，终端状态标识oauth_printStatus， 按键请求标识oauth_request
* @param url 推送地址填写必须以http://或https://开头的地址。推送地址需要支持GET访问，当GET请求访问时，请直接返回{"data":"OK"}，用于推送地址的可用性测试
* @param status 开启open，关闭close

获取订单状态接口
RequestMethod.getInstance().printerGetOrderStatus(String access_token,String machine_code,String order_id)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号
* @param order_id 易联云订单id

获取订单状态接口
RequestMethod.getInstance().printerGetOrderPagingList(String access_token,String machine_code,String page_index,String page_size)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号
* @param page_index 查询条件—当前页码,暂只提供前3页数据
* @param page_size 查询条件—每页显示条数,每页最大条数100

获取终端状态接口
RequestMethod.getInstance().printerGetPrintStatus(String access_token,String machine_code)
参数：* @param access_token 授权的token 必要参数
* @param machine_code 易联云打印机终端号
