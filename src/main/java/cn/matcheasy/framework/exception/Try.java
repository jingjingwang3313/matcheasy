package cn.matcheasy.framework.exception;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @class: Try
 * @author: wangjing
 * @date: 2022/2/8/0008
 * @desc: 函数式处理 try catch
 */
public interface Try<T> extends Serializable
{
    long serialVersionUID = 1L;

    /**
     * 无返回值
     *
     * @param runnable runnable
     * @return Void
     */
    static Try<Void> run(CheckedRunnable runnable)
    {
        Objects.requireNonNull(runnable, "runnable is null");
        try
        {
            runnable.run();
            return new Success<>(null);
        }
        catch (Throwable t)
        {
            return new Failure<>(t);
        }
    }

    /**
     * 有返回值
     *
     * @param supplier supplier
     * @param <T>      T
     * @return T
     */
    static <T> Try<T> of(CheckedFunction<? extends T> supplier)
    {
        Objects.requireNonNull(supplier, "supplier is null");
        try
        {
            return new Success<>(supplier.apply());
        }
        catch (Throwable t)
        {
            return new Failure<>(t);
        }
    }

    /**
     * then
     *
     * @param runnable runnable
     * @return Try
     */
    default Try<T> andThen(Runnable runnable)
    {
        Objects.requireNonNull(runnable, "runnable is null");
        return andThenTry(runnable::run);
    }

    /**
     * then try
     *
     * @param runnable runnable
     * @return Try
     */
    default Try<T> andThenTry(CheckedRunnable runnable)
    {
        Objects.requireNonNull(runnable, "runnable is null");
        if (isFailure())
        {
            return this;
        }
        try
        {
            runnable.run();
            return this;
        }
        catch (Throwable t)
        {
            return new Failure<>(t);
        }
    }

    /**
     * 失败后
     *
     * @param action action
     * @return Try
     */
    default Try<T> onFailure(Consumer<? super Throwable> action)
    {
        Objects.requireNonNull(action, "action is null");
        if (isFailure())
        {
            action.accept(getCause());
        }
        return this;
    }

    /**
     * 成功后
     *
     * @param action action
     * @return Try
     */
    default Try<T> onSuccess(Consumer<? super T> action)
    {
        Objects.requireNonNull(action, "action is null");
        if (isSuccess())
        {
            action.accept(get());
        }
        return this;
    }

    /**
     * finally
     *
     * @param runnable runnable
     * @return Try
     */
    default Try<T> andFinally(Runnable runnable)
    {
        Objects.requireNonNull(runnable, "runnable is null");
        return andFinallyTry(runnable::run);
    }

    /**
     * finally try
     *
     * @param runnable runnable
     * @return Try
     */
    default Try<T> andFinallyTry(CheckedRunnable runnable)
    {
        Objects.requireNonNull(runnable, "runnable is null");
        try
        {
            runnable.run();
            return this;
        }
        catch (Throwable t)
        {
            return new Failure<>(t);
        }
    }

    /**
     * 成功返回T，失败返回other
     *
     * @param other other
     * @return T
     */
    default T getOrElse(T other)
    {
        return this.isEmpty() ? other : this.get();
    }

    /**
     * 成功返回T，失败返回null
     *
     * @return T
     */
    default T getOrNull()
    {
        return this.isEmpty() ? null : this.get();
    }

    T get();

    Throwable getCause();

    boolean isEmpty();

    boolean isFailure();

    boolean isSuccess();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();

    final class Success<T> implements Try<T>, Serializable
    {

        private static final long serialVersionUID = 1L;

        private final T value;

        private Success(T value)
        {
            this.value = value;
        }

        @Override
        public T get()
        {
            return value;
        }

        @Override
        public Throwable getCause()
        {
            throw new UnsupportedOperationException("getCause on Success");
        }

        @Override
        public boolean isEmpty()
        {
            return false;
        }

        @Override
        public boolean isFailure()
        {
            return false;
        }

        @Override
        public boolean isSuccess()
        {
            return true;
        }

        @Override
        public boolean equals(Object obj)
        {
            return (obj == this) || (obj instanceof Success && Objects.equals(value, ((Success<?>) obj).value));
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(value);
        }

        @Override
        public String toString()
        {
            return "Success" + "(" + value + ")";
        }
    }

    final class Failure<T> implements Try<T>, Serializable
    {

        private static final long serialVersionUID = 1L;

        private final Throwable cause;

        private Failure(Throwable cause)
        {
            Objects.requireNonNull(cause, "cause is null");
            if (isFatal(cause))
            {
                sneakyThrow(cause);
            }
            this.cause = cause;
        }

        @Override
        public T get()
        {
            return sneakyThrow(cause);
        }

        @Override
        public Throwable getCause()
        {
            return cause;
        }

        @Override
        public boolean isEmpty()
        {
            return true;
        }

        @Override
        public boolean isFailure()
        {
            return true;
        }

        @Override
        public boolean isSuccess()
        {
            return false;
        }

        @Override
        public boolean equals(Object obj)
        {
            return (obj == this) || (obj instanceof Failure && Arrays.deepEquals(cause.getStackTrace(), ((Failure<?>) obj).cause.getStackTrace()));
        }

        @Override
        public int hashCode()
        {
            return Arrays.hashCode(cause.getStackTrace());
        }

        @Override
        public String toString()
        {
            return "Failure" + "(" + cause + ")";
        }
    }

    static boolean isFatal(Throwable throwable)
    {
        return throwable instanceof InterruptedException
                || throwable instanceof LinkageError
                || throwable instanceof ThreadDeath
                || throwable instanceof VirtualMachineError;
    }

    @SuppressWarnings("unchecked")
    static <T extends Throwable, R> R sneakyThrow(Throwable t) throws T
    {
        throw (T) t;
    }

    @FunctionalInterface
    interface CheckedConsumer<T>
    {

        void accept(T t) throws Throwable;
    }

    @FunctionalInterface
    interface CheckedFunction<R>
    {

        R apply() throws Throwable;
    }

    @FunctionalInterface
    interface CheckedRunnable
    {

        void run() throws Throwable;
    }
}
