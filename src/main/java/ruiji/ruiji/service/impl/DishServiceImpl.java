package ruiji.ruiji.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import ruiji.ruiji.dto.DishDto;
// import ruiji.ruiji.mapper.CategoryMapper;
import ruiji.ruiji.mapper.DishMapper;
// import ruiji.ruiji.pojo.Category;
import ruiji.ruiji.pojo.Dish;
import ruiji.ruiji.pojo.DishFlavor;
import ruiji.ruiji.service.DishFlavorService;
import ruiji.ruiji.service.DishService;

@Service

public class DishServiceImpl extends ServiceImpl<DishMapper , Dish> implements DishService {
    
    @Autowired
    private DishFlavorService dishFlavorService;

    // @Autowired
    // private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {

        this.save(dishDto);

        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();

        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }

        dishFlavorService.saveBatch(flavors);

    }


    @Override
    public DishDto getByIdWithFlavor(Long id) { 
        DishDto dishDto = new DishDto();
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish, dishDto);

        // // 使用 categoryMapper 获取分类名称
        // Category category = categoryMapper.selectById(dish.getCategoryId());
        // if (category != null) {
        //     dishDto.setCategoryName(category.getName());
        // }

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        
        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) { 

        this.updateById(dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());   // 设置菜品id
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}
