package ruiji.ruiji.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import ruiji.ruiji.common.CustomException;
import ruiji.ruiji.dto.SetmealDto;
import ruiji.ruiji.mapper.SetmealMapper;
import ruiji.ruiji.pojo.Setmeal;
import ruiji.ruiji.pojo.SetmealDish;
import ruiji.ruiji.service.SetMealDishService;
import ruiji.ruiji.service.SetmealService;


@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper , Setmeal> implements SetmealService{
    @Autowired
    private SetMealDishService setMealDishService;
    
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);
        //保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setMealDishService.saveBatch(setmealDishes);
    }


    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) { 
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        Long cnt = this.count(queryWrapper);

        if(cnt > 0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();

        queryWrapper1.in(SetmealDish::getSetmealId, ids);

        setMealDishService.remove(queryWrapper1);
    }
}
