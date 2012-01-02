package ch.choosle.proto;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;

public class MultiMethods {

	private static final int NUM = 4000000;
	private Object p0 = new Object();
	private Integer p1 = 1;
	private String p2 = "1";
	private Object[] ps = new Object[] { p0, p1, p2 };
	private int result;
	private HashMap<Class<?>, Method> map = new HashMap<Class<?>, Method>();
	private HashMap<String, Method> map2 = new HashMap<String, Method>();
	private IdentityHashMap<Class<?>, Method> map3 = new IdentityHashMap<Class<?>, Method>();
	private Method m0;
	private Method m1;
	private Method m2;
	private Class<?>[] types = new Class<?>[] { String.class, Integer.class, Object.class };
	private Caller[] callers = new Caller[] { new Caller() {
		@Override
		public void call(MultiMethods target, Object obj) {
			target.m((String)obj);
		}
	}, new Caller() {
		@Override
		public void call(MultiMethods target, Object obj) {
			target.m((Integer)obj);
		}
	}, new Caller() {
		@Override
		public void call(MultiMethods target, Object obj) {
			target.m(obj);
		}
	} };

	static interface Caller {
		void call(MultiMethods target, Object obj);
	}

	static class IntCaller implements Caller {
		public void call(MultiMethods target, Object obj) {
			target.m((Integer)obj);
		}
	}

	private IdentityHashMap<Class<?>, Caller> map4 = new IdentityHashMap<Class<?>, Caller>();

	public MultiMethods() {
		Method[] ms = MultiMethods.class.getMethods();
		for (int i = 0; i < ms.length; i++) {
			Method m = ms[i];
			if (!"m".equals(m.getName()))
				continue;
			this.map.put(m.getParameterTypes()[0], m);
			this.map2.put(m.getParameterTypes()[0].getName(), m);
			this.map3.put(m.getParameterTypes()[0], m);
		}
		try {
			this.m0 = MultiMethods.class.getMethod("m", Object.class);
			this.m1 = MultiMethods.class.getMethod("m", Integer.class);
			this.m2 = MultiMethods.class.getMethod("m", String.class);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		this.map4.put(Object.class, new Caller() {
			@Override
			public void call(MultiMethods target, Object obj) {
				target.m(obj);
			}
		});
		this.map4.put(Integer.class, new IntCaller());
		this.map4.put(String.class, new Caller() {
			@Override
			public void call(MultiMethods target, Object obj) {
				target.m((String)obj);
			}
		});
	}

	public static void main(String[] args) {
		new MultiMethods().go();
	}

	private void go() {
		try {
			direct();
			none();
			instOf();
			cast();
			cast2();
			cast3();
			cast4();
			cast5();
			invoke();
			map();
			map2();
			map3();
			map4();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cast() {
		m(Object.class.cast(ps[0]));
		m(Integer.class.cast(ps[1]));
		m(String.class.cast(ps[2]));
		this.result = 0;
		long s = System.nanoTime();
		for (int i = 0; i < NUM; i++) {
			m(Object.class.cast(ps[0]));
			m(Integer.class.cast(ps[1]));
			m(String.class.cast(ps[2]));
		}
		msg("cast", System.nanoTime() - s);
	}

	private void cast2() {
		// just to make sure this fails...
		callCast2(ps[0]);
		callCast2(ps[1]);
		callCast2(ps[2]);
		this.result = 0;
		long s = System.nanoTime();
		for (int i = 0; i < NUM; i++) {
			callCast2(ps[0]);
			callCast2(ps[1]);
			callCast2(ps[2]);
		}
		msg("cast2", System.nanoTime() - s);
	}

	private void callCast2(Object obj) {
		for (int i = 0; i < types.length; i++) {
			Class<?> t = types[i];
			if (t.isInstance(obj)) {
				m(t.cast(obj));
				return;
			}
		}
	}

	private void cast3() {
		// just to make sure this fails...
		m(ps[0].getClass().cast(ps[0]));
		m(ps[1].getClass().cast(ps[1]));
		m(ps[2].getClass().cast(ps[2]));
		this.result = 0;
		long s = System.nanoTime();
		for (int i = 0; i < NUM; i++) {
			m(ps[0].getClass().cast(ps[0]));
			m(ps[1].getClass().cast(ps[1]));
			m(ps[2].getClass().cast(ps[2]));
		}
		msg("cast3", System.nanoTime() - s);
	}

	private void cast4() {
		// just to make sure this fails...
		callCast4(ps[0].getClass(), ps[0]);
		callCast4(ps[1].getClass(), ps[1]);
		callCast4(ps[2].getClass(), ps[2]);
		this.result = 0;
		long s = System.nanoTime();
		for (int i = 0; i < NUM; i++) {
			callCast4(ps[0].getClass(), ps[0]);
			callCast4(ps[1].getClass(), ps[1]);
			callCast4(ps[2].getClass(), ps[2]);
		}
		msg("cast4", System.nanoTime() - s);
	}

	private <T> void callCast4(Class<T> c, Object obj) {
		m(c.cast(obj));
	}

	private void cast5() {
		// maybe the hash calculation in map4() takes long...
		callCast5(ps[0]);
		callCast5(ps[1]);
		callCast5(ps[2]);
		this.result = 0;
		long s = System.nanoTime();
		for (int i = 0; i < NUM; i++) {
			callCast5(ps[0]);
			callCast5(ps[1]);
			callCast5(ps[2]);
		}
		msg("cast5", System.nanoTime() - s);
	}

	private void callCast5(Object obj) {
		Class<?> t = obj.getClass();
		for (int i = 0; i < types.length; i++) {
			if (types[i] == t) {
				callers[i].call(this, obj);
				return;
			}
		}
	}

	private void instOf() {
		callInstOf(ps[0]);
		callInstOf(ps[1]);
		callInstOf(ps[2]);
		this.result = 0;
		long s = System.nanoTime();
		for (int i = 0; i < NUM; i++) {
			callInstOf(ps[0]);
			callInstOf(ps[1]);
			callInstOf(ps[2]);
		}
		msg("instOf", System.nanoTime() - s);
	}

	private void callInstOf(Object obj) {
		if (obj instanceof String)
			m((String)obj);

		// add some dummy tests
		else if (obj instanceof Method)
			m((Integer)obj);
		else if (obj instanceof Boolean)
			m((Integer)obj);
		else if (obj instanceof Double)
			m((Integer)obj);
		else if (obj instanceof Float)
			m((Integer)obj);

		else if (obj instanceof Integer)
			m((Integer)obj);
		else
			m(obj);
	}

	private void map() throws Exception {
		map.get(ps[0].getClass()).invoke(this, ps[0]);
		map.get(ps[1].getClass()).invoke(this, ps[1]);
		map.get(ps[2].getClass()).invoke(this, ps[2]);
		this.result = 0;
		long s = System.nanoTime();
		for (int i = 0; i < NUM; i++) {
			map.get(ps[0].getClass()).invoke(this, ps[0]);
			map.get(ps[1].getClass()).invoke(this, ps[1]);
			map.get(ps[2].getClass()).invoke(this, ps[2]);
		}
		msg("map", System.nanoTime() - s);
	}

	private void map2() throws Exception {
		map2.get(ps[0].getClass().getName()).invoke(this, ps[0]);
		map2.get(ps[1].getClass().getName()).invoke(this, ps[1]);
		map2.get(ps[2].getClass().getName()).invoke(this, ps[2]);
		this.result = 0;
		long s = System.nanoTime();
		for (int i = 0; i < NUM; i++) {
			map2.get(ps[0].getClass().getName()).invoke(this, ps[0]);
			map2.get(ps[1].getClass().getName()).invoke(this, ps[1]);
			map2.get(ps[2].getClass().getName()).invoke(this, ps[2]);
		}
		msg("map2", System.nanoTime() - s);
	}

	private void map3() throws Exception {
		map3.get(ps[0].getClass()).invoke(this, ps[0]);
		map3.get(ps[1].getClass()).invoke(this, ps[1]);
		map3.get(ps[2].getClass()).invoke(this, ps[2]);
		this.result = 0;
		long s = System.nanoTime();
		for (int i = 0; i < NUM; i++) {
			map3.get(ps[0].getClass()).invoke(this, ps[0]);
			map3.get(ps[1].getClass()).invoke(this, ps[1]);
			map3.get(ps[2].getClass()).invoke(this, ps[2]);
		}
		msg("map3", System.nanoTime() - s);
	}

	private void map4() {
		map4.get(ps[0].getClass()).call(this, ps[0]);
		map4.get(ps[1].getClass()).call(this, ps[1]);
		map4.get(ps[2].getClass()).call(this, ps[2]);
		this.result = 0;
		long s = System.nanoTime();
		for (int i = 0; i < NUM; i++) {
			map4.get(ps[0].getClass()).call(this, ps[0]);
			map4.get(ps[1].getClass()).call(this, ps[1]);
			map4.get(ps[2].getClass()).call(this, ps[2]);
		}
		msg("map4", System.nanoTime() - s);
	}

	private void none() {
		m(ps[0]);
		m(ps[1]);
		m(ps[2]);
		this.result = 0;
		long s = System.nanoTime();
		for (int i = 0; i < NUM; i++) {
			m(ps[0]);
			m(ps[1]);
			m(ps[2]);
		}
		msg("none", System.nanoTime() - s);
	}

	private void invoke() throws Exception {
		m0.invoke(this, p0);
		m1.invoke(this, p1);
		m2.invoke(this, p2);
		this.result = 0;
		long s = System.nanoTime();
		for (int i = 0; i < NUM; i++) {
			m0.invoke(this, p0);
			m1.invoke(this, p1);
			m2.invoke(this, p2);
		}
		msg("invoke", System.nanoTime() - s);
	}

	private void direct() {
		m(p0);
		m(p1);
		m(p2);
		this.result = 0;
		long s = System.nanoTime();
		for (int i = 0; i < NUM; i++) {
			m(p0);
			m(p1);
			m(p2);
		}
		msg("direct", System.nanoTime() - s);
	}

	public void m(Object object) {
		this.result += 1;
	}

	public void m(Integer integer) {
		this.result += 4;
	}

	public void m(String string) {
		this.result += 16;
	}

	private void msg(String msg, long t) {
		System.out.println(msg + ": " + (this.result / NUM) + " in " + (t / 1000 / 1000) + "ms");
	}

}
