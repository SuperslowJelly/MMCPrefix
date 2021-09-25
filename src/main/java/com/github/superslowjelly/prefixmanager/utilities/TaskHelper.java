package com.github.superslowjelly.prefixmanager.utilities;

import com.github.superslowjelly.prefixmanager.PrefixManager;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

public class TaskHelper {

    public static void sync(Runnable task) {
        Task.builder()
            .execute(task)
            .submit(PrefixManager.get());
    }

    public static void async(Runnable task) {
        Task.builder()
            .async()
            .execute(task)
            .submit(PrefixManager.get());
    }

    public static void scheduleSync(Runnable task, long delay, TimeUnit timeUnit) {
        Task.builder()
            .execute(task)
            .delay(delay, timeUnit)
            .submit(PrefixManager.get());
    }

    public static void scheduleASync(long delay, TimeUnit timeUnit, Runnable task) {
        Task.builder()
            .async()
            .execute(task)
            .delay(delay, timeUnit)
            .submit(PrefixManager.get());
    }
}
