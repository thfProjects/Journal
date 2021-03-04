package com.thf.journal;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRunner {

    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler hander = new Handler(Looper.getMainLooper());

    public interface Callback<R> {
        void onComplete(R result);
    }

    public <R> void executeAsync(Callable<R> callable, Callback<R> callback){
        executor.execute(()->{
            R result = null;
            try {
                result = callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            R finalResult = result;
            hander.post(()->{
                callback.onComplete(finalResult);
            });
        });
    }
}
