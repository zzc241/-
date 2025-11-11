package ruiji.ruiji.dto;

import ruiji.ruiji.pojo.Setmeal;
import ruiji.ruiji.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
