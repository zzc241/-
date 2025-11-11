package ruiji.ruiji.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import ruiji.ruiji.mapper.EmployeeMapper;
import ruiji.ruiji.pojo.Employee;
import ruiji.ruiji.service.EmployeeService;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    
}
