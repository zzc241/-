package ruiji.ruiji.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import ruiji.ruiji.common.CustomException;
import ruiji.ruiji.mapper.CategoryMapper;
import ruiji.ruiji.pojo.Category;
import ruiji.ruiji.pojo.Dish;
import ruiji.ruiji.pojo.Setmeal;
import ruiji.ruiji.service.CategoryService;
import ruiji.ruiji.service.DishService;
import ruiji.ruiji.service.SetmealService;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper , Category> implements CategoryService {
    

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Override
    public void remove(Long id) {
        /**如果有关联菜品或者套餐，则抛出一个业务异常 */

        LambdaQueryWrapper <Dish> dishquerywrapper = new LambdaQueryWrapper<>();

        dishquerywrapper.eq(Dish::getCategoryId, id);
        Long cnt1 = dishService.count(dishquerywrapper);

        if(cnt1 != 0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        
        LambdaQueryWrapper <Setmeal> setmealquerywrapper = new LambdaQueryWrapper<>();

        setmealquerywrapper.eq(Setmeal::getCategoryId, id);

        Long cnt2 = setmealService.count(setmealquerywrapper);

        if(cnt2 != 0){
            throw new CustomException("当前分类下关联了套餐，不能删除");

        }

        super.removeById(id);

    }
}
