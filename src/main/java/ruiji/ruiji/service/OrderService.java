package ruiji.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ruiji.ruiji.pojo.Orders;

public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);
}
