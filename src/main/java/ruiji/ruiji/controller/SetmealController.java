package ruiji.ruiji.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;
import ruiji.ruiji.common.R;
import ruiji.ruiji.dto.SetmealDto;
import ruiji.ruiji.pojo.Category;
import ruiji.ruiji.pojo.Setmeal;
import ruiji.ruiji.service.CategoryService;
import ruiji.ruiji.service.SetmealService;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;


    @RequestMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name){
        Page<Setmeal> pageinfo = new Page<>(page , pageSize);
        Page<SetmealDto> pageinfoDto = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(name != null , Setmeal::getName , name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageinfo , queryWrapper);

        BeanUtils.copyProperties(pageinfo , pageinfoDto , "records");

        List<Setmeal> records = pageinfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            if(categoryId != null){
                Category category = categoryService.getById(categoryId);
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        pageinfoDto.setRecords(list);
        

        return R.success(pageinfoDto);
    }

    @PostMapping("/status/{status}")
    public R<String> updateByStatus(@PathVariable int status , @RequestParam("ids") List<Long> ids){
        log.info("status:{}", status);
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();

        updateWrapper.in(Setmeal::getId, ids);

        updateWrapper.set(Setmeal::getStatus, status);

        setmealService.update(updateWrapper);

        return R.success("success");
    }

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("setmealDto:{}", setmealDto);

        setmealService.saveWithDish(setmealDto);
        // LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        // queryWrapper.eq(Setmeal::getName, setmealDto.getName());
        return R.success("success");
    }


    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}", ids);

        setmealService.removeWithDish(ids);
        return R.success("success");
    }

    @GetMapping("/list")
    @Cacheable(value = "setmeal" , key = "#setmeal.categoryId" , unless = "#result == null")
    public R<List<Setmeal>> list(Setmeal setmeal){ 

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }
}
