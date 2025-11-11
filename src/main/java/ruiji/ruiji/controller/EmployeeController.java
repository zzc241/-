package ruiji.ruiji.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import ruiji.ruiji.common.R;
import ruiji.ruiji.pojo.Employee;
import ruiji.ruiji.service.EmployeeService;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Object> login(HttpServletRequest request , @RequestBody Employee employee) {
        //1.md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.查数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();//查询条件
        queryWrapper.eq(Employee::getUsername, employee.getUsername());//等值查询
        Employee emp = employeeService.getOne(queryWrapper);//名字已经唯一约束，用getOne
        //3.没查到返回
        if (emp == null){
            return R.error("用户不存在");
        }
        //4.密码比对
        if (!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }
        //5.查看是否禁用
        if (emp.getStatus() == 0){//0禁用，1可用
            return R.error("账号已禁用");
        }
        //6.登陆成功，id存入session
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    //员工退出
    public R<String> logout(HttpServletRequest request){
        //清理session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request , @RequestBody Employee employee){
        log.info("新增员工，员工信息：{}", employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // employee.setCreateTime(LocalDateTime.now());
        // employee.setUpdateTime(LocalDateTime.now());
        // Long empId = (Long) request.getSession().getAttribute("employee");
        // employee.setCreateUser(empId);
        // employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success(null);
    }


    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, String name){ 
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

        //构造分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();

        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }


    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }


    @GetMapping("/{id}")
    public R <Employee> getById(@PathVariable Long id) {
        Employee emp = employeeService.getById(id);
        if(emp == null){
            return R.error("没有查询到员工信息");
        }
        return R.success(emp);
    }


}
