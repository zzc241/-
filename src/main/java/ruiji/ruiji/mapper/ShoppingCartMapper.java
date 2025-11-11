package ruiji.ruiji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ruiji.ruiji.pojo.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

}
