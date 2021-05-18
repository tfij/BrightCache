package pl.tfij.brightcache.fixture

import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull

import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@CompileStatic
class SelfThreadExecutorService implements ExecutorService {
    private boolean shutdown = false

    @Override
    void shutdown() {
        shutdown = true
    }

    @Override
    List<Runnable> shutdownNow() {
        shutdown = true
        return []
    }

    @Override
    boolean isShutdown() {
        return shutdown
    }

    @Override
    boolean isTerminated() {
        return shutdown
    }

    @Override
    boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return shutdown
    }

    @Override
    def <T> Future<T> submit(@NotNull Callable<T> task) {
        requireActive()
        return CompletableFuture.completedFuture(task.call())
    }

    @Override
    <T> Future<T> submit(@NotNull Runnable task, T result) {
        requireActive()
        task.run()
        return CompletableFuture.completedFuture(result)
    }

    @Override
    Future<?> submit(@NotNull Runnable task) {
        requireActive()
        task.run()
        return CompletableFuture.completedFuture(Void)
    }

    @Override
    <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
        requireActive()
        return tasks.collect { CompletableFuture.completedFuture(it.call()) as Future<T> }
    }

    @Override
    <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        requireActive()
        return tasks.collect { CompletableFuture.completedFuture(it.call()) as Future<T> }
    }

    @Override
    <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        requireActive()
        return tasks.collect { it.call() }.find()
    }

    @Override
    <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        requireActive()
        return tasks.collect { it.call() }.find()
    }

    @Override
    void execute(@NotNull Runnable command) {
        requireActive()
        command.run()
    }

    private void requireActive() {
        if (shutdown) {
            throw new RejectedExecutionException("SelfThreadExecutorService ")
        }
    }
}
