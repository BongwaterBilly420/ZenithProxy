package com.zenith.event;

import com.zenith.util.Pair;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static com.zenith.Shared.DEFAULT_LOG;

/**
 * A simple event bus without reflection.
 *
 * We need to avoid reflection where possible due to GraalVM native compilation.
 * Otherwise, anytime we add event handlers we would need to update the compilation configs and whatnot.
 * which would just create a bunch of footguns for developers.
 *
 * The main drawback to this approach is we need to manage the lifecycle of the subscriptions ourselves.
 * Any object that has methods subscribed MUST unsubscribe itself before the object is garbage collected.
 * In other words, all objects with subscriptions must have references retained somewhere or be unsubscribed before removing references.
 */
public class SimpleEventBus {

    private final ExecutorService executorService;
    private final Reference2ObjectMap<Class<?>, List<Consumer<?>>> handlers = new Reference2ObjectOpenHashMap<>();

    public SimpleEventBus(final ExecutorService executorService) {
        this.executorService = executorService;
    }

    public <T> Subscription subscribe(Class<T> eventType, Consumer<T> handler) {
        handlers.computeIfAbsent(eventType, key -> new CopyOnWriteArrayList<>()).add(handler);
        return new Subscription(() -> unsubscribe(eventType, handler));
    }

    @SafeVarargs
    public final Subscription subscribe(Pair<Class<?>, Consumer<?>>... pairs) {
        for (var pair : pairs)
            handlers.computeIfAbsent(pair.left(), key -> new CopyOnWriteArrayList<>()).add(pair.right());
        return new Subscription(() -> {
            for (var pair : pairs) unsubscribe(pair.left(), pair.right());
        });
    }

    public static <T> Pair<Class<?>, Consumer<?>> pair(Class<T> clazz, Consumer<T> handler) {
        return Pair.of(clazz, handler);
    }

    public void unsubscribe(Class<?> eventType, Consumer<?> handler) {
        var consumers = handlers.get(eventType);
        if (consumers != handlers.defaultReturnValue()) {
            consumers.remove(handler);
            if (consumers.isEmpty()) handlers.remove(eventType);
        }
    }

    // handlers can throw and return exceptions - cancelling subsequent event executions
    public <T> void post(T event) {
        var consumers = handlers.get(event.getClass());
        if (consumers != handlers.defaultReturnValue())
            for (var consumer : consumers) ((Consumer<T>) consumer).accept(event);
    }

    public <T> void postAsync(T event) {
        var consumers = handlers.get(event.getClass());
        if (consumers != handlers.defaultReturnValue())
            executorService.execute(() -> this.postAsyncInternal(event, consumers));
    }


    private <T> void postAsyncInternal(T event, List<Consumer<?>> consumers) {
        try {
            for (var consumer : consumers) ((Consumer<T>) consumer).accept(event);
        } catch (final Throwable e) { // swallow exception so we don't kill the executor
            DEFAULT_LOG.debug("Error handling async event", e);
        }
    }
}
