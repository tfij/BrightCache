package pl.tfij.brightcache.fixture

import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull

import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@CompileStatic
class SelfExecutorService implements ExecutorService {
    @Override
    void shutdown() {}

    @Override
    List<Runnable> shutdownNow() {
        return []
    }

    @Override
    boolean isShutdown() {
        return false
    }

    @Override
    boolean isTerminated() {
        return false
    }

    @Override
    boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return false
    }

    @Override
    def <T> Future<T> submit(@NotNull Callable<T> task) {
        return CompletableFuture.completedFuture(task.call())
    }

    @Override
    <T> Future<T> submit(@NotNull Runnable task, T result) {
        task.run()
        return CompletableFuture.completedFuture(result)
    }

    @Override
    Future<?> submit(@NotNull Runnable task) {
        task.run()
        return CompletableFuture.completedFuture(Void)
    }

    @Override
    <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return tasks.collect { CompletableFuture.completedFuture(it.call()) as Future<T> }
    }

    @Override
    <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return tasks.collect { CompletableFuture.completedFuture(it.call()) as Future<T> }
    }

    @Override
    <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return tasks.collect { it.call() }.find()
    }

    @Override
    <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return tasks.collect { it.call() }.find()
    }

    @Override
    void execute(@NotNull Runnable command) {
        command.run()
    }
}
