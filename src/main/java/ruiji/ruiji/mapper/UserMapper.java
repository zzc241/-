package ruiji.ruiji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ruiji.ruiji.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User>{
}
