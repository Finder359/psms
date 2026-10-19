package org.jeecg.modules.psms.workorder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.psms.workorder.entity.BuWorkOrder;
import org.jeecg.modules.psms.workorder.mapper.BuWorkOrderMapper;
import org.jeecg.modules.psms.workorder.service.IBuWorkOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BuWorkOrderServiceImpl extends ServiceImpl<BuWorkOrderMapper, BuWorkOrder> implements IBuWorkOrderService {

    public static final String STATUS_PENDING_ACCEPT = "0";
    public static final String STATUS_ASSIGNED = "1";
    public static final String STATUS_WAIT_ACCEPTANCE = "3";
    public static final String STATUS_CLOSED = "4";
    public static final String STATUS_RETURNED = "5";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignOrder(String orderId, String assignTo, String currentUsername) {
        if (!StringUtils.hasText(assignTo)) {
            throw new JeecgBootException("维修人员不能为空");
        }
        BuWorkOrder order = getWorkOrderOrThrow(orderId);
        requireStatus(order, STATUS_PENDING_ACCEPT, "当前工单状态不允许派单");
        order.setAssignTo(assignTo);
        order.setOrderStatus(STATUS_ASSIGNED);
        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleOrder(String orderId, String handleResult, String currentUsername) {
        if (!StringUtils.hasText(handleResult)) {
            throw new JeecgBootException("维修处理结果不能为空");
        }
        BuWorkOrder order = getWorkOrderOrThrow(orderId);
        requireStatus(order, STATUS_ASSIGNED, "当前工单状态不允许维修反馈");
        if (!StringUtils.hasText(order.getAssignTo())) {
            throw new JeecgBootException("当前工单未分配维修人员，不能反馈");
        }
        if (!order.getAssignTo().equals(currentUsername)) {
            throw new JeecgBootException("仅被指派的维修人员可反馈处理结果");
        }
        order.setHandleResult(handleResult);
        order.setOrderStatus(STATUS_WAIT_ACCEPTANCE);
        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void evaluateOrder(String orderId, Integer evaluateScore, String evaluateOpinion, String currentUsername) {
        if (evaluateScore == null) {
            throw new JeecgBootException("验收评分不能为空");
        }
        BuWorkOrder order = getWorkOrderOrThrow(orderId);
        requireStatus(order, STATUS_WAIT_ACCEPTANCE, "当前工单状态不允许验收评价");
        order.setEvaluateScore(evaluateScore);
        order.setEvaluateOpinion(evaluateOpinion);
        if (evaluateScore >= 4) {
            order.setOrderStatus(STATUS_CLOSED);
        } else {
            order.setOrderStatus(STATUS_RETURNED);
        }
        updateById(order);
    }

    private BuWorkOrder getWorkOrderOrThrow(String orderId) {
        BuWorkOrder order = getById(orderId);
        if (order == null) {
            throw new JeecgBootException("工单不存在");
        }
        return order;
    }

    private void requireStatus(BuWorkOrder order, String expectedStatus, String message) {
        if (!expectedStatus.equals(order.getOrderStatus())) {
            throw new JeecgBootException(message);
        }
    }
}
