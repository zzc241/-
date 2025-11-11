package ruiji.ruiji.common;

public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id)
    {
        // threadLocal.set(id);
        threadLocal.set(1417012167126876162L);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
