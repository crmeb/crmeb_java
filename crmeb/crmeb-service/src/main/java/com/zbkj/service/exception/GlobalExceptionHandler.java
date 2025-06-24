package com.zbkj.service.exception;

import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.exception.ExceptionLog;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ExceptionLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

/**
 * 全局参数、异常拦截
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

//    private static String REQUESTBODY = "requestBodyMessage";

    @Autowired
    private ExceptionLogService exceptionLogService;

    /**
     * 拦截表单参数校验
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({BindException.class})
    public CommonResult bindException(HttpServletRequest request, BindException e) {
        doLog(request, e);
        BindingResult bindingResult = e.getBindingResult();
        return CommonResult.failed(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
    }

    /**
     * 拦截JSON参数校验
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult bindException(HttpServletRequest request, MethodArgumentNotValidException e) {
        doLog(request, e);
        BindingResult bindingResult = e.getBindingResult();
        return CommonResult.failed(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
    }

    /**
     * 拦截参数类型不正确
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public CommonResult bindException(HttpServletRequest request, HttpMediaTypeNotSupportedException e) {
        doLog(request, e);
        return CommonResult.failed().setMessage(Objects.requireNonNull(e.getMessage()));
    }

    /**
     * 错误SQL语句异常
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    public CommonResult handleBadSqlGrammarException(HttpServletRequest request, BadSqlGrammarException e) {
        doLog(request, e);
        return CommonResult.failed().setMessage("服务器数据异常，请联系管理员");
    }

    /**
     * 拦截表示违反数据库的完整性约束导致的异常
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public CommonResult handleDataIntegrityViolationException(HttpServletRequest request, DataIntegrityViolationException e) {
        doLog(request, e);
        return CommonResult.failed().setMessage( "服务器数据异常，请联系管理员");
    }

    /**
     * 拦截违反数据库的非完整性约束导致的异常，可能也会拦截一些也包括 SQL 语句错误、连接问题、权限问题等各种数据库异常
     */
    @ExceptionHandler(UncategorizedSQLException.class)
    public CommonResult handleUncategorizedSqlException(HttpServletRequest request, UncategorizedSQLException e) {
        doLog(request, e);
        return CommonResult.failed().setMessage("服务器数据异常，请联系管理员");
    }

    //声明要捕获的异常
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public <T> CommonResult<?> defaultExceptionHandler(HttpServletRequest request, Exception e) {
        doLog(request, e);
        e.printStackTrace();
        if (e instanceof CrmebException) {
            return CommonResult.failed().setMessage(Objects.requireNonNull(e.getMessage()));
        }
        if (e instanceof MissingServletRequestParameterException) {
            return CommonResult.failed().setMessage(Objects.requireNonNull(e.getMessage()));
        }
        //未知错误
        return CommonResult.failed().setMessage(e.getMessage());
    }

    /**
     * 打印日志
     */
    private void doLog(HttpServletRequest request, Exception e) {
        log.error("捕获到异常：", e);
        // 加入数据库日志记录
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        // 异常的详情
        String expDetail = sw.toString();

        try {
            sw.close();
        } catch (IOException ioException) {
            log.error("异常日志：关闭异常详情Writer异常");
        }

        // 异常的url
        String expUrl = request.getRequestURI();

        // 异常的参数 暂时不记录入参
//        Object body = request.getAttribute(REQUESTBODY);
//        String expParams = ObjectUtil.isNotNull(body) ? body.toString() : "";

        // 异常的类型
        String expType = e.getClass().getName();

        // 异常的类名
        StackTraceElement stackTraceElement = e.getStackTrace()[0];
        String expController = stackTraceElement.getClassName();

        // 异常的方法名
        String expMethod = stackTraceElement.getMethodName();

        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setExpUrl(expUrl);
//        exceptionLog.setExpParams(expParams);
        exceptionLog.setExpParams("");
        exceptionLog.setExpType(expType);
        exceptionLog.setExpController(expController);
        exceptionLog.setExpMethod(expMethod);
        exceptionLog.setExpDetail(expDetail);

        exceptionLogService.save(exceptionLog);
    }
}
