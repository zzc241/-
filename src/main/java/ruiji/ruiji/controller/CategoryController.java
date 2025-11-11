package ruiji.ruiji.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;
import ruiji.ruiji.common.R;
import ruiji.ruiji.pojo.Category;
import ruiji.ruiji.service.CategoryService;
/**
 * 
 * 分类管理
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}", category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<Page<Category>> page(int page , int pageSize){
        log.info("page = {}, pageSize = {}", page, pageSize);

        Page<Category> pageinfo = new Page<>(page , pageSize);

        LambdaQueryWrapper<Category> querywrapper = new LambdaQueryWrapper<>();

        querywrapper.orderByDesc(Category::getUpdateTime);

        categoryService.page(pageinfo, querywrapper);
        return R.success(pageinfo);
    }


    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除分类，id为：{}", ids);
        // categoryService.removeById(id);
        categoryService.remove(ids);
        return R.success("分类信息删除成功");
    }


    @PutMapping
    public R<Category> get(@RequestBody Category category){
        log.info("查询分类:{}", category);
        if(category != null){
            categoryService.updateById(category);
            return R.success(category);
        }
        return R.error("没有查询到对应分类");
    }


    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        log.info("查询分类:{}", category);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(category.getType() != null, Category::getType , category.getType());

        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }
}
