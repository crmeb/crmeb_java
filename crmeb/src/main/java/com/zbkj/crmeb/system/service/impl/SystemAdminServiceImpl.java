package com.zbkj.crmeb.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.authorization.manager.TokenManager;
import com.zbkj.crmeb.authorization.model.TokenModel;
import com.zbkj.crmeb.system.dao.SystemAdminDao;
import com.zbkj.crmeb.system.model.SystemAdmin;
import com.zbkj.crmeb.system.request.SystemAdminAddRequest;
import com.zbkj.crmeb.system.request.SystemAdminRequest;
import com.zbkj.crmeb.system.response.SystemAdminResponse;
import com.zbkj.crmeb.system.service.SystemAdminService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @Description SystemAdminServiceImpl 接口实现
* @since 2020-04-13
*/
@Service
public class SystemAdminServiceImpl extends ServiceImpl<SystemAdminDao, SystemAdmin> implements SystemAdminService {

    @Resource
    private SystemAdminDao dao;

    @Resource
    private TokenManager tokenManager;


    @Override
    public List<SystemAdmin> getList(SystemAdminRequest request, PageParamRequest pageParamRequest){
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带SystemAdminRequest类的多条件查询
        LambdaQueryWrapper<SystemAdmin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        SystemAdmin systemAdmin = new SystemAdmin();
        BeanUtils.copyProperties(request,systemAdmin);
//        lambdaQueryWrapper.setEntity(systemAdmin);
        if(StringUtils.isNotBlank(systemAdmin.getAccount())){
            lambdaQueryWrapper.eq(SystemAdmin::getAccount, systemAdmin.getAccount());
        }

        if(null != systemAdmin.getId()){
            lambdaQueryWrapper.eq(SystemAdmin::getId, systemAdmin.getId());
        }
        if(null != systemAdmin.getIsDel()){
            lambdaQueryWrapper.eq(SystemAdmin::getIsDel, systemAdmin.getIsDel());
        }
        if(StringUtils.isNotBlank(systemAdmin.getLastIp())){
            lambdaQueryWrapper.eq(SystemAdmin::getLastIp, systemAdmin.getLastIp());
        }

        if(null != systemAdmin.getLevel()){
            lambdaQueryWrapper.eq(SystemAdmin::getLevel, systemAdmin.getLevel());
        }
        if(null != systemAdmin.getLoginCount()){
            lambdaQueryWrapper.eq(SystemAdmin::getLoginCount, systemAdmin.getLoginCount());
        }
        if(StringUtils.isNotBlank(systemAdmin.getRealName())){
            lambdaQueryWrapper.like(SystemAdmin::getRealName, systemAdmin.getRealName());
            lambdaQueryWrapper.or().like(SystemAdmin::getAccount, systemAdmin.getRealName());
        }
        if(StringUtils.isNotBlank(systemAdmin.getRoles())){
            lambdaQueryWrapper.eq(SystemAdmin::getRoles, systemAdmin.getRoles());
        }
        if(null != systemAdmin.getStatus()){
            lambdaQueryWrapper.eq(SystemAdmin::getStatus, systemAdmin.getStatus());
        }
        return dao.selectList(lambdaQueryWrapper);
    }

    @Override
    public SystemAdminResponse login(SystemAdminRequest request, String ip) {
        LambdaQueryWrapper<SystemAdmin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemAdmin::getAccount, request.getAccount());
        SystemAdmin systemAdmin = dao.selectOne(lambdaQueryWrapper);
        String encryptPassword =
        CrmebUtil.encryptPassword(request.getPwd(),request.getAccount());
        if(null == systemAdmin){
            throw new CrmebException("用户不存在");
        }

        if(!systemAdmin.getStatus()){
            throw new CrmebException("用户已经被禁用");
        }

        if(systemAdmin.getIsDel()){
            throw new CrmebException("用户已经被删除");
        }

        if(!systemAdmin.getPwd().equals(encryptPassword)){
            throw new CrmebException("账号或者密码不正确");
        }
        TokenModel tokenModel = tokenManager.createToken(systemAdmin.getAccount(), systemAdmin.getId().toString(), TokenModel.TOKEN_REDIS);
        SystemAdminResponse systemAdminResponse = new SystemAdminResponse();
        systemAdminResponse.setToken(tokenModel.getToken());
        BeanUtils.copyProperties(systemAdmin, systemAdminResponse);

        //更新最后登录信息
        systemAdmin.setLoginCount(systemAdmin.getLoginCount() + 1);
        systemAdmin.setLastIp(ip);
        updateById(systemAdmin);

        return systemAdminResponse;
    }

    @Override
    public Boolean logout(String token) throws Exception {
        tokenManager.deleteToken(token, TokenModel.TOKEN_REDIS);
        return true;
    }

    /**
     * 管理员详情
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public SystemAdminResponse getInfo(SystemAdminRequest request) throws Exception {
        QueryWrapper queryWrapper = new QueryWrapper();
//        queryWrapper.eq("account", request.getAccount());
//        queryWrapper.eq("account", request.getAccount());
        queryWrapper.eq("id", request.getId());
        SystemAdmin systemAdmin = dao.selectOne(queryWrapper);

        if(null == systemAdmin || systemAdmin.getId() < 0){
            return null;
        }
        SystemAdminResponse systemAdminResponse = new SystemAdminResponse();
        BeanUtils.copyProperties(systemAdmin, systemAdminResponse);
        return systemAdminResponse;
    }

    /**
     * 新增管理员
     * @param systemAdminAddRequest
     * @return
     */
    @Override
    public SystemAdminResponse saveAdmin(SystemAdminAddRequest systemAdminAddRequest) {
        try {
            // 管理员名称唯一校验
            Integer result = checkAccount(systemAdminAddRequest.getAccount());
            if (result > 0) {
                throw new CrmebException("管理员已存在");
            }

            SystemAdminRequest systemAdminRequest = new SystemAdminRequest();
            BeanUtils.copyProperties(systemAdminAddRequest, systemAdminRequest);

            // 执行新增管理员操作
            String pwd = CrmebUtil.encryptPassword(systemAdminAddRequest.getPwd(), systemAdminAddRequest.getAccount());
            systemAdminAddRequest.setPwd(pwd);
            SystemAdmin systemAdmin = new SystemAdmin();
            BeanUtils.copyProperties(systemAdminAddRequest, systemAdmin);
            SystemAdminResponse systemAdminResponse = new SystemAdminResponse();
            BeanUtils.copyProperties(systemAdminAddRequest, systemAdminResponse);
            return systemAdminResponse;

        }catch (Exception e){
            throw new CrmebException("新增管理员异常 " + e.getMessage());
        }

    }

    private Integer checkAccount(String account) {
        LambdaQueryWrapper<SystemAdmin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemAdmin::getAccount, account);
        return dao.selectCount(lambdaQueryWrapper);

    }

    @Override
    public SystemAdminResponse updateAdmin(SystemAdminRequest systemAdminRequest) throws Exception {
        SystemAdminResponse systemAdminResponseExsit = getInfo(systemAdminRequest);
        if(null == systemAdminResponseExsit){
            throw new CrmebException("管理员不存在");
        }
        SystemAdmin systemAdmin = new SystemAdmin();
        BeanUtils.copyProperties(systemAdminRequest, systemAdmin);
        if(null != systemAdminRequest.getPwd()){
            String pwd = CrmebUtil.encryptPassword(systemAdminRequest.getPwd(), systemAdminRequest.getAccount());
            systemAdmin.setPwd(pwd);
        }
        if(dao.updateById(systemAdmin) > 0){
            SystemAdminResponse systemAdminResponse = new SystemAdminResponse();
            BeanUtils.copyProperties(systemAdmin, systemAdminResponse);
            return systemAdminResponse;
        }
        throw new CrmebException("更新管理员异常");
    }

    @Override
    public SystemAdminResponse getInfoByToken(String token) throws Exception{
        Boolean tokenExsit = tokenManager.checkToken(token, TokenModel.TOKEN_REDIS);
        if(!tokenExsit){
            throw new CrmebException("当前token无效");
        }
        TokenModel tokenModel = tokenManager.getToken(token, TokenModel.TOKEN_REDIS);
        SystemAdminRequest systemAdminRequest = new SystemAdminRequest();
//        systemAdminRequest.setAccount(tokenModel.getUserNo());
        systemAdminRequest.setId(tokenModel.getUserId());
        return getInfo(systemAdminRequest);
    }

    /**
     * 获取当前用户id
     * @author Mr.Zhang
     * @since 2020-04-28
     * @return Integer
     */
    @Override
    public Integer getAdminId() {
        return Integer.parseInt(tokenManager.getLocalInfoException("id"));
    }

    /**
     * 获取当前用户信息
     * @author Mr.Zhang
     * @since 2020-04-28
     * @return SystemAdmin
     */
    @Override
    public SystemAdmin getInfo() {
        return getInfo(getAdminId());
    }

    /**
     * 获取用户信息
     * @author Mr.Zhang
     * @since 2020-04-28
     * @return SystemAdmin
     */
    @Override
    public SystemAdmin getInfo(Integer adminId) {
        return getById(adminId);
    }
}

