package uk.ac.warwick.dcs.sherlock.engine.executor.common;

import java.util.concurrent.atomic.*;

import static java.lang.Float.floatToIntBits;
import static java.lang.Float.intBitsToFloat;

public class AtomicFloat extends Number {

	private AtomicInteger bits;

	public AtomicFloat() {
		this(0f);
	}

	public AtomicFloat(float initialValue) {
		bits = new AtomicInteger(floatToIntBits(initialValue));
	}

	public final boolean compareAndSet(float expect, float update) {
		return bits.compareAndSet(floatToIntBits(expect), floatToIntBits(update));
	}

	public final void addTo(float value) {
		bits.accumulateAndGet(floatToIntBits(value), (a, b) -> floatToIntBits(intBitsToFloat(a) + intBitsToFloat(b)));
	}

	@Override
	public double doubleValue() {
		return (double) floatValue();
	}

	@Override
	public float floatValue() {
		return get();
	}

	public final float get() {
		return intBitsToFloat(bits.get());
	}

	public final float getAndSet(float newValue) {
		return intBitsToFloat(bits.getAndSet(floatToIntBits(newValue)));
	}

	@Override
	public int intValue() {
		return (int) get();
	}

	@Override
	public long longValue() {
		return (long) get();
	}

	public final void set(float newValue) {
		bits.set(floatToIntBits(newValue));
	}

}
