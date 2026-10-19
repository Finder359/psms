package org.jeecg.modules.psms.workorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.psms.workorder.entity.BuWorkOrder;

public interface IBuWorkOrderService extends IService<BuWorkOrder> {
    void assignOrder(String orderId, String assignTo, String currentUsername);

    void handleOrder(String orderId, String handleResult, String currentUsername);

    void evaluateOrder(String orderId, Integer evaluateScore, String evaluateOpinion, String currentUsername);
}
