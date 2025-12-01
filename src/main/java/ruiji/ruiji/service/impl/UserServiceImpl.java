package ruiji.ruiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ruiji.ruiji.pojo.User;
import ruiji.ruiji.mapper.UserMapper;
import ruiji.ruiji.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{
}
