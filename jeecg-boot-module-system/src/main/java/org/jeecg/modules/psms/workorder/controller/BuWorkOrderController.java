package org.jeecg.modules.psms.workorder.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.psms.workorder.entity.BuWorkOrder;
import org.jeecg.modules.psms.workorder.service.IBuWorkOrderService;
import org.jeecg.modules.psms.workorder.service.impl.BuWorkOrderServiceImpl;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.system.mapper.SysUserRoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "运维工单管理")
@RestController
@RequestMapping("/psms/workOrder")
@Slf4j
public class BuWorkOrderController extends JeecgController<BuWorkOrder, IBuWorkOrderService> {

    private static final String ROLE_MAINTENANCE = "4";
    private static final String ROLE_DISPATCHER = "5";
    private static final String ROLE_COMPANY = "6";

    @Autowired
    private IBuWorkOrderService buWorkOrderService;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @AutoLog(value = "运维工单-企业发起报修")
    @ApiOperation(value = "企业发起报修", notes = "仅角色6可发起，状态初始化为0(待受理)")
    @PostMapping(value = "/create")
    public Result<?> create(@RequestBody BuWorkOrder req) {
        LoginUser loginUser = getLoginUser();
        requireRole(loginUser.getUsername(), ROLE_COMPANY, "仅餐饮企业用户可发起报修");
        if (!StringUtils.hasText(req.getFaultDesc())) {
            throw new JeecgBootException("故障描述不能为空");
        }
        if (!StringUtils.hasText(req.getRepairMan())) {
            throw new JeecgBootException("报修联系人不能为空");
        }
        if (!StringUtils.hasText(req.getRepairPhone())) {
            throw new JeecgBootException("报修电话不能为空");
        }
        req.setOrderStatus(BuWorkOrderServiceImpl.STATUS_PENDING_ACCEPT);
        if (!StringUtils.hasText(req.getOrderNo())) {
            req.setOrderNo("WO" + System.currentTimeMillis());
        }
        buWorkOrderService.save(req);
        return Result.OK("报修工单创建成功");
    }

    @AutoLog(value = "运维工单-分页查询")
    @ApiOperation(value = "工单查询", notes = "分页查询工单（兼容/list）")
    @GetMapping(value = {"/queryPageList", "/list"})
    public Result<?> queryPageList(BuWorkOrder buWorkOrder,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<BuWorkOrder> queryWrapper = QueryGenerator.initQueryWrapper(buWorkOrder, req.getParameterMap());
        Page<BuWorkOrder> page = new Page<>(pageNo, pageSize);
        IPage<BuWorkOrder> pageList = buWorkOrderService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    @AutoLog(value = "运维工单-监管派单")
    @ApiOperation(value = "监管派单", notes = "仅角色5可操作，状态0->1")
    @PostMapping(value = "/assign")
    public Result<?> assign(@RequestBody @Valid AssignReq req) {
        LoginUser loginUser = getLoginUser();
        requireRole(loginUser.getUsername(), ROLE_DISPATCHER, "仅监管派单员可派单");
        buWorkOrderService.assignOrder(req.getOrderId(), req.getAssignTo(), loginUser.getUsername());
        return Result.OK("派单成功");
    }

    @AutoLog(value = "运维工单-维修反馈")
    @ApiOperation(value = "维修反馈", notes = "仅角色4且需是被指派维修人，状态1->3")
    @PostMapping(value = "/handle")
    public Result<?> handle(@RequestBody @Valid HandleReq req) {
        LoginUser loginUser = getLoginUser();
        requireRole(loginUser.getUsername(), ROLE_MAINTENANCE, "仅一线维修人员可反馈");
        buWorkOrderService.handleOrder(req.getOrderId(), req.getHandleResult(), loginUser.getUsername());
        return Result.OK("维修反馈成功");
    }

    @AutoLog(value = "运维工单-企业验收评价")
    @ApiOperation(value = "企业验收评价", notes = "仅角色6可操作，状态3->4(满意)或3->5(不满意)")
    @PostMapping(value = "/evaluate")
    public Result<?> evaluate(@RequestBody @Valid EvaluateReq req) {
        LoginUser loginUser = getLoginUser();
        requireRole(loginUser.getUsername(), ROLE_COMPANY, "仅餐饮企业用户可验收评价");
        buWorkOrderService.evaluateOrder(req.getOrderId(), req.getEvaluateScore(), req.getEvaluateOpinion(), loginUser.getUsername());
        return Result.OK("验收评价提交成功");
    }

    private LoginUser getLoginUser() {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (loginUser == null) {
            throw new JeecgBootException("用户未登录");
        }
        return loginUser;
    }

    private void requireRole(String username, String roleCode, String errorMessage) {
        List<String> roleCodes = sysUserRoleMapper.getRoleByUserName(username);
        if (roleCodes == null || !roleCodes.contains(roleCode)) {
            throw new JeecgBootException(errorMessage);
        }
    }

    @Data
    public static class AssignReq {
        private String orderId;
        private String assignTo;
    }

    @Data
    public static class HandleReq {
        private String orderId;
        private String handleResult;
    }

    @Data
    public static class EvaluateReq {
        private String orderId;
        private Integer evaluateScore;
        private String evaluateOpinion;
    }
}
