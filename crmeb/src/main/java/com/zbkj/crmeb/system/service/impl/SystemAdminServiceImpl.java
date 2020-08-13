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
        lambdaQueryWrapper.setEntity(systemAdmin);
        return dao.selectList(lambdaQueryWrapper);
    }

    @Override
    public SystemAdminResponse login(SystemAdminRequest request) throws Exception {
        LambdaQueryWrapper<SystemAdmin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemAdmin::getAccount, request.getAccount());
        SystemAdmin systemAdmin = dao.selectOne(lambdaQueryWrapper);
        String encryptPassword =
        CrmebUtil.encryptPassword(request.getPwd(),request.getAccount());
        if(null == systemAdmin){
            throw new CrmebException("用户不存在");
        }
        if(!systemAdmin.getPwd().equals(encryptPassword)){
            throw new CrmebException("账号或者密码不正确");
        }
        TokenModel tokenModel = tokenManager.createToken(systemAdmin.getAccount(), systemAdmin.getId().toString(), TokenModel.TOKEN_REDIS);
        SystemAdminResponse systemAdminResponse = new SystemAdminResponse();
        systemAdminResponse.setToken(tokenModel.getToken());
        BeanUtils.copyProperties(systemAdmin, systemAdminResponse);
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
    public SystemAdminResponse saveAdmin(SystemAdminAddRequest systemAdminAddRequest) throws Exception {
        // 管理员名称唯一校验
        SystemAdminRequest systemAdminRequest = new SystemAdminRequest();
        BeanUtils.copyProperties(systemAdminAddRequest, systemAdminRequest);
        SystemAdminResponse systemAdminResponseExsit = getInfo(systemAdminRequest);
        if(null != systemAdminResponseExsit){
            throw new CrmebException("管理员已存在");
        }
        // 执行新增管理员操作
        String pwd = CrmebUtil.encryptPassword(systemAdminAddRequest.getPwd(), systemAdminAddRequest.getAccount());
        systemAdminAddRequest.setPwd(pwd);
        SystemAdmin systemAdmin = new SystemAdmin();
        BeanUtils.copyProperties(systemAdminAddRequest, systemAdmin);
        if(dao.insert(systemAdmin) > 0){
            SystemAdminResponse systemAdminResponse = new SystemAdminResponse();
            BeanUtils.copyProperties(systemAdminAddRequest, systemAdminResponse);
            return systemAdminResponse;
        }

        throw new CrmebException("新增管理员异常");
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

