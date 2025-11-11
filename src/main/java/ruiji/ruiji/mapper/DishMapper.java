package ruiji.ruiji.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import ruiji.ruiji.pojo.Dish;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
    
}
