package survivalblock.train_across_time.common.util.ptr;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
abstract class ASMPointer<R, T, S> {
    protected BiPredicate<S, R> predicate;
    protected boolean debug = false;

    protected S self() {
        return (S) this;
    }

    public S debug() {
        this.debug = true;
        return self();
    }

    public S and(BiPredicate<S, R> predicate) {
        this.predicate = this.predicate.and(predicate);
        return self();
    }

    public S and(ASMPointer<R, T, S> pointer) {
        return and(pointer.predicate);
    }

    public S or(BiPredicate<S, R> predicate) {
        this.predicate = this.predicate.or(predicate);
        return self();
    }

    public S or(ASMPointer<R, T, S> pointer) {
        return or(pointer.predicate);
    }

    /**
     * The first value that matches this pointer, or empty if none match
     */
    public abstract Optional<R> find(T target);

    public R findOrThrow(T target) {
        return find(target).orElseThrow(() -> new NullPointerException("Unable to find " + this + " in " + target));
    }

    public void findOrThrow(T target, Consumer<R> out) {
        out.accept(findOrThrow(target));
    }

    protected static String toString(String name, @Nullable String ... values) {
        var actualValues = Arrays.stream(values).filter(Objects::nonNull).toList();
        return actualValues.isEmpty() ? name : name + "[" + String.join(", ", actualValues) + "]";
    }
}
