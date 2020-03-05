package source.structural.defaultMethodsWithInnerInterfaces;

import static source.structural.defaultMethodsWithInnerInterfaces.Util.checkCriticalNotNull;
import static source.structural.defaultMethodsWithInnerInterfaces.Util.checkNotNull;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

class Util {
	static void checkCriticalNotNull(Object o) {}
	static void checkNotNull(Object o) {}
}

interface MyIterator<T> {
	T next();
	boolean hasNext();
    default void forEachRemaining(Consumer<? super Double> consumer) {
    }
}

public interface MyPrimitiveIterator<T, C> extends MyIterator<T> {

	  void forEachRemaining(C consumer);

	  interface OfDouble extends MyPrimitiveIterator<Double, DoubleConsumer> {
	    double nextDouble();

	    @Override
	    default Double next() {
	      return nextDouble();
	    }

	    @Override
	    default void forEachRemaining(DoubleConsumer consumer) {
	      checkNotNull(consumer);
	      while (hasNext()) {
	        //consumer.accept(nextDouble());
	      }
	    }

	    @Override
	    default void forEachRemaining(Consumer<? super Double> consumer) {
	      if (consumer instanceof DoubleConsumer) {
	        forEachRemaining((DoubleConsumer) consumer);
	      } else {
	        checkCriticalNotNull(consumer);
	        forEachRemaining((DoubleConsumer) null);
	      }
	    }
	  }
}