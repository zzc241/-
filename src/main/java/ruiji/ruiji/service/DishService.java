package ruiji.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;

import ruiji.ruiji.dto.DishDto;
import ruiji.ruiji.pojo.Dish;


public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);


    public void updateWithFlavor(DishDto dishDto);
}
