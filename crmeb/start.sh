#!/bin/bash

#开始时间 时间戳
startTime=`date +'%Y-%m-%d %H:%M:%S'`

#接口项目站点路径（目录按照各自配置）
APP_PATH=/www/wwwroot/api.java.crmeb.net
#APP_PATH=/Library/WebServer/Documents/zhongbang/java/crmeb/target

#jar包文件名称
APP_NAME=$APP_PATH/Crmeb.jar

#日志文件名称
LOG_FILE=$APP_PATH/crmeb_out.log

#启动环境   # 如果需要配置数据和redis，请在 application-prod.yml中修改, 用jar命令修改即可
APP_YML=--spring.profiles.active=prod


#数据库配置

rm -rf $LOG_FILE

echo "开始停止 Crmeb 项目进程"
#查询进程，并杀掉当前jar/java程序

pid=`ps -ef|grep $APP_NAME | grep -v grep | awk '{print $2}'`
if [ $pid ];then
  echo "pid: $pid"
  kill -9 $pid
  echo "Crmeb 项目进程进程终止成功"
fi

sleep 2

#判断jar包文件是否存在，如果存在启动jar包，并时时查看启动日志

if test -e $APP_NAME;then
  echo '文件存在,开始启动此程序...'

# 启动jar包，指向日志文件，2>&1 & 表示打开或指向同一个日志文件  --spring.profiles.active=prod 启动 prod环境


  nohup java -jar $APP_NAME $APP_YML  > crmeb_out.log 2>&1 &
  echo "正在发布中，请稍后......"
  sleep 10s

  #通过检测日志来判断
  while [ -f $LOG_FILE ]
  do
      success=`grep "Started CrmebApplication in " $LOG_FILE`
      if [[ "$success" != "" ]]
      then
#          echo "Crmeb start ........."
          break
      else
#          echo "Crmeb Running ......."
          sleep 1s
      fi

#      echo "开始检测启动失败标记"
      fail=`grep "Fail" $LOG_FILE`
      if [[ "$fail" != "" ]]
      then
          echo "项目启动失败"
          tail -f $LOG_FILE
          break
      else
#          echo "Crmeb Running ......."
          sleep 1s
      fi

  done
  echo "Crmeb Started Success"

endTime=`date +'%Y-%m-%d %H:%M:%S'`
startSecond=$(date --date="$startTime" +%s);
endSecond=$(date --date="$endTime" +%s);

total=$((endSecond-startSecond))
# shellcheck disable=SC2027
echo "本次运行时间： "$total"s"
# shellcheck disable=SC2086
echo "当前时间："$endTime


#  #消息推送到企业微信群
#  curl 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=4f7df494-cece-49c2-8e04-60610c937b4a' \
#   -H 'Content-Type: application/json' \
#   -d '
#   {
#    "msgtype": "text",
#    "text": {
#        "content": "Api项目部署成功！\n 耗时：'$total's \n 当前时间：'"$endTime"'",
#        "mentioned_list":["@all"]
#    }
#  }'

##实时查看启动日志
#  #tail -f $LOG_FILE
#  pid1=;
#  while [[ $pid1 = "" ]]
#  do
#    sleep 5
#    pid1=`ps -ef|grep $APP_NAME | grep -v grep | awk '{print $2}'`
#  done
#
#  sleep 5
#  echo "发布成功！"
#
#  #消息推送到企业微信群
#  curl 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=4f7df494-cece-49c2-8e04-60610c937b4a' \
#   -H 'Content-Type: application/json' \
#   -d '
#   {
#    "msgtype": "text",
#    "text": {
#        "content": "Api项目部署成功！",
#        "mentioned_list":["@all"]
#    }
#  }'
else
  echo '$APP_NAME 文件不存在,请检查。'
fi

#在 $APP_NAME 同级目录下运行  ./start.sh 命令即可启动项目。
