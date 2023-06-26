package com.yangdai.calc;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * 用途：全局异常拦截
 * @author 30415
 */
public class NeverCrash {

    private final static String TAG = NeverCrash.class.getSimpleName();
    private final static NeverCrash INSTANCE = new NeverCrash();

    private boolean debugMode;
    private MainCrashHandler mainCrashHandler;
    private UncaughtCrashHandler uncaughtCrashHandler;

    private NeverCrash() {
    }

    public static NeverCrash getInstance() {
        return INSTANCE;
    }

    private synchronized MainCrashHandler getMainCrashHandler() {
        if (null == mainCrashHandler) {
            mainCrashHandler = (t, e) -> {
            };
        }
        return mainCrashHandler;
    }

    /**
     * 主线程发生异常时的回调，可用于打印日志文件
     * <p>
     * 注意跨线程操作的可能
     */
    public NeverCrash setMainCrashHandler(MainCrashHandler mainCrashHandler) {
        this.mainCrashHandler = mainCrashHandler;
        return this;
    }

    private synchronized UncaughtCrashHandler getUncaughtCrashHandler() {
        if (null == uncaughtCrashHandler) {
            uncaughtCrashHandler = (t, e) -> {
            };
        }
        return uncaughtCrashHandler;
    }

    /**
     * 子线程发生异常时的回调，可用于打印日志文件
     * <p>
     * 注意跨线程操作的可能
     */
    public NeverCrash setUncaughtCrashHandler(UncaughtCrashHandler uncaughtCrashHandler) {
        this.uncaughtCrashHandler = uncaughtCrashHandler;
        return this;
    }

    private boolean isDebugMode() {
        return debugMode;
    }

    /**
     * debug模式，会打印log日志，且toast提醒发生异常，反之则都没有
     */
    public NeverCrash setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }

    /**
     * 完成监听异常的注册
     *
     * @param application application
     */
    public void register(Application application) {
        //主线程异常拦截
        new Handler(Looper.getMainLooper()).post(() -> {
            while (true) {
                try {
                    Looper.loop();
                } catch (Throwable e) {
                    if (isDebugMode()) {
                        Log.e(TAG, "未捕获的主线程异常行为", e);
                        new Handler(Looper.getMainLooper()).post(() ->
                                Toast.makeText(application, "主线程发生异常，请查看控制台日志!\n此提醒和控制台打印仅在debug版本下有效!", Toast.LENGTH_LONG).show());
                    }
                    getMainCrashHandler().mainException(Looper.getMainLooper().getThread(), e);
                }
            }
        });

        //子线程异常拦截
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            if (isDebugMode()) {
                Log.e(TAG, "未捕获的子线程异常行为", e);
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(application, "子线程发生异常，请查看控制台日志!\n此提醒和控制台打印仅在debug版本下有效!", Toast.LENGTH_LONG).show());
            }
            getUncaughtCrashHandler().uncaughtException(t, e);
        });
    }

    public interface MainCrashHandler {
        void mainException(Thread t, Throwable e);
    }

    public interface UncaughtCrashHandler {
        void uncaughtException(Thread t, Throwable e);
    }
}
