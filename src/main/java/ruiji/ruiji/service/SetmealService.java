package ruiji.ruiji.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import ruiji.ruiji.dto.SetmealDto;
import ruiji.ruiji.pojo.Setmeal;


public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);
}
