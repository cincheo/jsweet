package def.js;
@jsweet.lang.Interface
public abstract class PromiseLike<T> extends def.js.Object {
    /**
    * Attaches callbacks for the resolution and/or rejection of the Promise.
    * @param onfulfilled The callback to execute when the Promise is resolved.
    * @param onrejected The callback to execute when the Promise is rejected.
    * @returns A Promise for the completion of which ever callback is executed.
    */
    @jsweet.lang.Name("then")
    native public <TResult> PromiseLike<TResult> thenOnfulfilledFunctionOnrejectedFunction(java.util.function.Function<T,TResult> onfulfilled, java.util.function.Function<java.lang.Object,TResult> onrejected);
    @jsweet.lang.Name("then")
    native public <TResult> PromiseLike<TResult> thenOnfulfilledFunction(java.util.function.Function<T,TResult> onfulfilled, java.util.function.Consumer<java.lang.Object> onrejected);
    /**
    * Attaches callbacks for the resolution and/or rejection of the Promise.
    * @param onfulfilled The callback to execute when the Promise is resolved.
    * @param onrejected The callback to execute when the Promise is rejected.
    * @returns A Promise for the completion of which ever callback is executed.
    */
    @jsweet.lang.Name("then")
    native public <TResult> PromiseLike<TResult> thenOnfulfilledFunction(java.util.function.Function<T,TResult> onfulfilled);
    /**
    * Attaches callbacks for the resolution and/or rejection of the Promise.
    * @param onfulfilled The callback to execute when the Promise is resolved.
    * @param onrejected The callback to execute when the Promise is rejected.
    * @returns A Promise for the completion of which ever callback is executed.
    */
    native public <TResult> PromiseLike<TResult> then();
    /**
    * Attaches callbacks for the resolution and/or rejection of the Promise.
    * @param onfulfilled The callback to execute when the Promise is resolved.
    * @param onrejected The callback to execute when the Promise is rejected.
    * @returns A Promise for the completion of which ever callback is executed.
    */
    @jsweet.lang.Name("then")
    native public <TResult> PromiseLike<TResult> thenOnfulfilledPromiseLikeFunctionOnrejectedFunction(java.util.function.Function<T,PromiseLike<TResult>> onfulfilled, java.util.function.Function<java.lang.Object,TResult> onrejected);
    /**
    * Attaches callbacks for the resolution and/or rejection of the Promise.
    * @param onfulfilled The callback to execute when the Promise is resolved.
    * @param onrejected The callback to execute when the Promise is rejected.
    * @returns A Promise for the completion of which ever callback is executed.
    */
    @jsweet.lang.Name("then")
    native public <TResult> PromiseLike<TResult> thenOnfulfilledFunctionOnrejectedPromiseLikeFunction(java.util.function.Function<T,TResult> onfulfilled, java.util.function.Function<java.lang.Object,PromiseLike<TResult>> onrejected);
    /**
    * Attaches callbacks for the resolution and/or rejection of the Promise.
    * @param onfulfilled The callback to execute when the Promise is resolved.
    * @param onrejected The callback to execute when the Promise is rejected.
    * @returns A Promise for the completion of which ever callback is executed.
    */
    @jsweet.lang.Name("then")
    native public <TResult> PromiseLike<TResult> thenOnfulfilledPromiseLikeFunctionOnrejectedPromiseLikeFunction(java.util.function.Function<T,PromiseLike<TResult>> onfulfilled, java.util.function.Function<java.lang.Object,PromiseLike<TResult>> onrejected);
    @jsweet.lang.Name("then")
    native public <TResult> PromiseLike<TResult> thenOnfulfilledPromiseLikeFunction(java.util.function.Function<T,PromiseLike<TResult>> onfulfilled, java.util.function.Consumer<java.lang.Object> onrejected);
    /**
    * Attaches callbacks for the resolution and/or rejection of the Promise.
    * @param onfulfilled The callback to execute when the Promise is resolved.
    * @param onrejected The callback to execute when the Promise is rejected.
    * @returns A Promise for the completion of which ever callback is executed.
    */
    @jsweet.lang.Name("then")
    native public <TResult> PromiseLike<TResult> thenOnfulfilledPromiseLikeFunction(java.util.function.Function<T,PromiseLike<TResult>> onfulfilled);
}

