package io.mem0r1es.activitysubsumer.concurrent;

import io.mem0r1es.activitysubsumer.utils.SubsumerConfig;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Ivan Gavrilović
 */
public class ActivityOpsExecutor {
    private static ActivityOpsExecutor instance = null;
    private ExecutorService service;


    private ActivityOpsExecutor(int numThrads){
        service = Executors.newFixedThreadPool(numThrads);
    }

    public static ActivityOpsExecutor get(){
        if (instance == null) instance = new ActivityOpsExecutor(SubsumerConfig.NUM_THREADS);
        return instance;
    }

    public <T> Future<T> submit(Callable<T> callable){
        return service.submit(callable);
    }
}
