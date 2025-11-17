package ruiji.ruiji.controller;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DurationFormat.Unit;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;
import ruiji.ruiji.common.R;
import ruiji.ruiji.dto.DishDto;
import ruiji.ruiji.pojo.Category;
import ruiji.ruiji.pojo.Dish;
import ruiji.ruiji.pojo.DishFlavor;
import ruiji.ruiji.service.CategoryService;
import ruiji.ruiji.service.DishFlavorService;
import ruiji.ruiji.service.DishService;



@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @GetMapping("/page")
    public R<Page<DishDto>> page(int page , int pageSize  , String name){
        log.info("page = {}, pageSize = {}", page, pageSize);
        Page<DishDto> Dtopageinfo = new Page<>();
        Page<Dish> pageinfo = new Page<>(page , pageSize);

        LambdaQueryWrapper<Dish> querywrapper = new LambdaQueryWrapper<>();

        querywrapper.like(name != null , Dish::getName, name);

        querywrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageinfo, querywrapper);

        BeanUtils.copyProperties(pageinfo, Dtopageinfo , "records");

        List<Dish> records = pageinfo.getRecords();
        
        List<DishDto> list = records.stream().map((item) -> { 
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();

            if(categoryId != null){
                Category category = categoryService.getById(categoryId);
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;

        }).collect(Collectors.toList());

        Dtopageinfo.setRecords(list);
        // return R.success(pageinfo);
        return R.success(Dtopageinfo);
    }

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("dishDto:{}", dishDto);

        dishService.saveWithFlavor(dishDto);
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return R.success("新增菜品成功");
    }


    @GetMapping("/{id}")
    public R<DishDto> updateById(@PathVariable Long id){ 

        log.info("查询的id为:{}", id);
        DishDto dishDto = dishService.getByIdWithFlavor(id);


        return R.success(dishDto);
    }


    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("dishDto:{}", dishDto);

        dishService.updateWithFlavor(dishDto);
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return R.success("修改菜品成功");

    }


    @PostMapping("status/{status}")
    public R<String> updateStatus(@PathVariable int status, @RequestParam("ids") List<Long> ids){
        log.info("status:{}", status);

        String key = "dish_" + ids;
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        
        updateWrapper.in(Dish::getId , ids);
        updateWrapper.set(Dish::getStatus, status);
        // updateWrapper.eq(Dish::getId, ids);
        // updateWrapper.set(Dish::getStatus, status);
        dishService.update(updateWrapper);
        redisTemplate.delete(key);
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return R.success("修改成功");
    }
    

    @DeleteMapping()
    public R<String> delete(@RequestParam("ids") List<Long> ids){
        log.info("删除分类，id为：{}", ids);

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(Dish::getId, ids);
        dishService.remove(queryWrapper);

        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return R.success("删除成功");
    }

    /**
     * 根据条件查询数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList = null;

        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if(dishDtoList != null){
            return R.success(dishDtoList);
        }
        else{
            log.info("查询菜品:{}", dish);
            LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

            queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
            queryWrapper.eq(Dish::getStatus , 1);

            queryWrapper.orderByAsc(Dish::getSort, Dish::getUpdateTime);

            List<Dish> list = dishService.list(queryWrapper);

            log.info("查询结果:{}", list);
            List<DishDto> listDto = list.stream().map((item) -> {
                DishDto dishDto = new DishDto();
                BeanUtils.copyProperties(item, dishDto);
                Long categoryId = item.getCategoryId();

                Category category = categoryService.getById(categoryId);
                if(category != null){
                    dishDto.setCategoryName(category.getName());
                }
                Long dishId = item.getId();
                LambdaQueryWrapper<DishFlavor> queryWrapperFlavor = new LambdaQueryWrapper<>();
                queryWrapperFlavor.eq(DishFlavor::getDishId, dishId);
                List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapperFlavor);
                dishDto.setFlavors(dishFlavors);

                return dishDto;
            }).collect(Collectors.toList());

            redisTemplate.opsForValue().set(key, listDto , 60 , TimeUnit.MINUTES);

            log.info("查询结果:{}", list);
            return R.success(listDto);
        }
    }
}
