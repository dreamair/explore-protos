package ch.choosle.proto;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.concurrent.Callable;

import ch.choosle.proto.Protocol.ResultProvider;

public class MultiMethodsVisitor implements ResultProvider {

	private static final int NUM_RUNS = 20000000;

	public interface Type {
		int getType();

		void accept(MultiMethodsVisitor visitor);
	}

	public class Type0 implements Type {
		@Override
		public void accept(MultiMethodsVisitor visitor) {
			visitor.m(this);
		}

		@Override
		public int getType() {
			return 0;
		}
	}

	public class Type1 implements Type {
		@Override
		public void accept(MultiMethodsVisitor visitor) {
			visitor.m(this);
		}

		@Override
		public int getType() {
			return 1;
		}
	}

	public class Type2 implements Type {
		@Override
		public void accept(MultiMethodsVisitor visitor) {
			visitor.m(this);
		}

		@Override
		public int getType() {
			return 2;
		}
	}

	private Type0 p0 = new Type0();
	private Type1 p1 = new Type1();
	private Type2 p2 = new Type2();
	private Type[] ps = new Type[] { p0, p1, p2 };
	private int result;
	private HashMap<Class<?>, Method> map = new HashMap<Class<?>, Method>();
	private HashMap<String, Method> map2 = new HashMap<String, Method>();
	private IdentityHashMap<Class<?>, Method> map3 = new IdentityHashMap<Class<?>, Method>();
	private Method m0;
	private Method m1;
	private Method m2;
	private Class<?>[] types = new Class<?>[] { Type0.class, Type1.class, Type2.class };
	private Caller[] callers = new Caller[] { new Caller() {
		@Override
		public void call(MultiMethodsVisitor target, Object obj) {
			target.m((Type0) obj);
		}
	}, new Caller() {
		@Override
		public void call(MultiMethodsVisitor target, Object obj) {
			target.m((Type1) obj);
		}
	}, new Caller() {
		@Override
		public void call(MultiMethodsVisitor target, Object obj) {
			target.m((Type2) obj);
		}
	} };

	static interface Caller {
		void call(MultiMethodsVisitor target, Object obj);
	}

	private IdentityHashMap<Class<?>, Caller> map4 = new IdentityHashMap<Class<?>, Caller>();
	private Protocol protocol;

	public MultiMethodsVisitor() {
		Method[] ms = MultiMethodsVisitor.class.getMethods();
		for (int i = 0; i < ms.length; i++) {
			Method m = ms[i];
			if (!"m".equals(m.getName()))
				continue;
			this.map.put(m.getParameterTypes()[0], m);
			this.map2.put(m.getParameterTypes()[0].getName(), m);
			this.map3.put(m.getParameterTypes()[0], m);
		}
		try {
			this.m0 = MultiMethodsVisitor.class.getMethod("m", Type0.class);
			this.m1 = MultiMethodsVisitor.class.getMethod("m", Type1.class);
			this.m2 = MultiMethodsVisitor.class.getMethod("m", Type2.class);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		this.map4.put(Type0.class, new Caller() {
			@Override
			public void call(MultiMethodsVisitor target, Object obj) {
				target.m((Type0) obj);
			}
		});
		this.map4.put(Type1.class, new Caller() {
			@Override
			public void call(MultiMethodsVisitor target, Object obj) {
				target.m((Type1) obj);
			}
		});
		this.map4.put(Type2.class, new Caller() {
			@Override
			public void call(MultiMethodsVisitor target, Object obj) {
				target.m((Type2) obj);
			}
		});
	}

	public static void main(String[] args) {
		new MultiMethodsVisitor().go();
	}

	private void go() {
		this.protocol = new Protocol();
		this.protocol.start();
		this.result = 0;
		this.protocol.measure("_direct", NUM_RUNS, new Callable<Void>() {
			@Override
			public final Void call() throws Exception {
				direct();
				return null;
			}
		}, this);
		this.result = 0;
		this.protocol.measure("_cast", NUM_RUNS, new Callable<Void>() {
			@Override
			public final Void call() throws Exception {
				cast();
				return null;
			}
		}, this);
		this.result = 0;
		this.protocol.measure("_invoke", NUM_RUNS, new Callable<Void>() {
			@Override
			public final Void call() throws Exception {
				invoke();
				return null;
			}
		}, this);
		this.result = 0;
		this.protocol.measure("instOf", NUM_RUNS, new Callable<Void>() {
			@Override
			public final Void call() throws Exception {
				instOf();
				return null;
			}
		}, this);
		this.result = 0;
		this.protocol.measure("cast5", NUM_RUNS, new Callable<Void>() {
			@Override
			public final Void call() throws Exception {
				cast5();
				return null;
			}
		}, this);
		this.result = 0;
		this.protocol.measure("cast6", NUM_RUNS, new Callable<Void>() {
			@Override
			public final Void call() throws Exception {
				cast6();
				return null;
			}
		}, this);
		this.result = 0;
		this.protocol.measure("reflect", NUM_RUNS, new Callable<Void>() {
			@Override
			public final Void call() throws Exception {
				reflect();
				return null;
			}
		}, this);
		this.result = 0;
		this.protocol.measure("map", NUM_RUNS, new Callable<Void>() {
			@Override
			public final Void call() throws Exception {
				map();
				return null;
			}
		}, this);
		this.result = 0;
		this.protocol.measure("map2", NUM_RUNS, new Callable<Void>() {
			@Override
			public final Void call() throws Exception {
				map2();
				return null;
			}
		}, this);
		this.result = 0;
		this.protocol.measure("map3", NUM_RUNS, new Callable<Void>() {
			@Override
			public final Void call() throws Exception {
				map3();
				return null;
			}
		}, this);
		this.result = 0;
		this.protocol.measure("map4", NUM_RUNS, new Callable<Void>() {
			@Override
			public final Void call() throws Exception {
				map4();
				return null;
			}
		}, this);
		this.result = 0;
		this.protocol.measure("visit", NUM_RUNS, new Callable<Void>() {
			@Override
			public final Void call() throws Exception {
				visit();
				return null;
			}
		}, this);
		this.protocol.end();
	}

	@Override
	public Object getResult() {
		return this.result / NUM_RUNS;
	}

	private void reflect() throws Exception {
		MultiMethodsVisitor.class.getMethod("m", ps[0].getClass()).invoke(this, ps[0]);
		MultiMethodsVisitor.class.getMethod("m", ps[1].getClass()).invoke(this, ps[1]);
		MultiMethodsVisitor.class.getMethod("m", ps[2].getClass()).invoke(this, ps[2]);
	}

	private void visit() {
		ps[0].accept(this);
		ps[1].accept(this);
		ps[2].accept(this);
	}

	private void cast() {
		m(Type0.class.cast(ps[0]));
		m(Type1.class.cast(ps[1]));
		m(Type2.class.cast(ps[2]));
	}

	private void cast5() {
		// maybe the hash calculation in map4() takes long...
		callCast5(ps[0]);
		callCast5(ps[1]);
		callCast5(ps[2]);
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

	private void cast6() {
		// type code as index
		this.callers[ps[0].getType()].call(this, ps[0]);
		this.callers[ps[1].getType()].call(this, ps[1]);
		this.callers[ps[2].getType()].call(this, ps[2]);
	}

	private void instOf() {
		callInstOf(ps[0]);
		callInstOf(ps[1]);
		callInstOf(ps[2]);
	}

	private void callInstOf(Object obj) {
		if (obj instanceof String)
			m((Type0) obj);

		// add some dummy tests
		else if (obj instanceof Method)
			m((Type0) obj);
		else if (obj instanceof Boolean)
			m((Type0) obj);
		else if (obj instanceof Double)
			m((Type0) obj);
		else if (obj instanceof Float)
			m((Type0) obj);
		else if (obj instanceof Method)
			m((Type0) obj);
		else if (obj instanceof Boolean)
			m((Type0) obj);
		else if (obj instanceof Double)
			m((Type0) obj);
		else if (obj instanceof Float)
			m((Type0) obj);
		else if (obj instanceof Method)
			m((Type0) obj);
		else if (obj instanceof Boolean)
			m((Type0) obj);

		else if (obj instanceof Type0)
			m((Type0) obj);
		else if (obj instanceof Type1)
			m((Type1) obj);
		else if (obj instanceof Type2)
			m((Type2) obj);
	}

	private void map() throws Exception {
		map.get(ps[0].getClass()).invoke(this, ps[0]);
		map.get(ps[1].getClass()).invoke(this, ps[1]);
		map.get(ps[2].getClass()).invoke(this, ps[2]);
	}

	private void map2() throws Exception {
		map2.get(ps[0].getClass().getName()).invoke(this, ps[0]);
		map2.get(ps[1].getClass().getName()).invoke(this, ps[1]);
		map2.get(ps[2].getClass().getName()).invoke(this, ps[2]);
	}

	private void map3() throws Exception {
		map3.get(ps[0].getClass()).invoke(this, ps[0]);
		map3.get(ps[1].getClass()).invoke(this, ps[1]);
		map3.get(ps[2].getClass()).invoke(this, ps[2]);
	}

	private void map4() {
		map4.get(ps[0].getClass()).call(this, ps[0]);
		map4.get(ps[1].getClass()).call(this, ps[1]);
		map4.get(ps[2].getClass()).call(this, ps[2]);
	}

	private void invoke() throws Exception {
		m0.invoke(this, p0);
		m1.invoke(this, p1);
		m2.invoke(this, p2);
	}

	private void direct() {
		m(p0);
		m(p1);
		m(p2);
	}

	public void m(Type0 object) {
		this.result += 1;
	}

	public void m(Type1 integer) {
		this.result += 4;
	}

	public void m(Type2 string) {
		this.result += 16;
	}

}

/*-


 * _direct: 21 in 25ms instOf: 21 in 189ms _cast: 21 in 37ms cast5: 21 in 972ms
 * cast6: 21 in 105ms _invoke: 21 in 1073ms reflect: 21 in 17026ms map: 21 in
 * 1829ms map2: 21 in 1805ms map3: 21 in 1561ms map4: 21 in 662ms visit: 21 in
 * 16ms

 10M
 Sun Microsystems Inc. 1.6.0_25 (Java HotSpot(TM) 64-Bit Server VM 20.0-b11)
 Windows 7 (6.1 amd64)
 ch.choosle.proto.MultiMethodsVisitor (Mon Jan 02 09:44:37 CET 2012)
 _direct: 21 in 5ms
 _cast: 21 in 14ms
 _invoke: 21 in 478ms
 instOf: 21 in 60ms
 cast5: 21 in 330ms
 cast6: 21 in 13ms
 reflect: 21 in 7854ms
 map: 21 in 894ms
 map2: 21 in 930ms
 map3: 21 in 809ms
 map4: 21 in 254ms
 visit: 21 in 7ms

 10M
 Sun Microsystems Inc. 1.6.0_25 (Java HotSpot(TM) 64-Bit Server VM 20.0-b11)
 Windows 7 (6.1 amd64)
 ch.choosle.proto.Protocol (Mon Jan 02 10:40:23 CET 2012)
 _direct: 21 in 9ms
 _cast: 21 in 19ms
 _invoke: 21 in 554ms
 instOf: 21 in 149ms
 cast5: 21 in 369ms
 cast6: 21 in 119ms
 reflect: 21 in 8798ms
 map: 21 in 903ms
 map2: 21 in 1149ms
 map3: 21 in 804ms
 map4: 21 in 317ms
 visit: 21 in 110ms

 Sun Microsystems Inc. 1.6.0_25 (Java HotSpot(TM) 64-Bit Server VM 20.0-b11)
 Windows 7 (6.1 amd64)
 ch.choosle.proto.Protocol (Mon Jan 02 10:53:18 CET 2012)
 _direct: 21, 20M in 11ms
 _cast: 21, 20M in 37ms
 _invoke: 21, 20M in 1061ms
 instOf: 21, 20M in 283ms
 cast5: 21, 20M in 705ms
 cast6: 21, 20M in 219ms
 reflect: 21, 20M in 14386ms
 map: 21, 20M in 1773ms
 map2: 21, 20M in 1859ms
 map3: 21, 20M in 1563ms
 map4: 21, 20M in 626ms
 visit: 21, 20M in 204ms

 Sun Microsystems Inc. 1.6.0_25 (Java HotSpot(TM) 64-Bit Server VM 20.0-b11)
Args: [-Xms512m, -Xmx512m, -Xbatch, -Xnoclassgc, -Xincgc, -Dfile.encoding=UTF-8]
Windows 7 (6.1 amd64)
ch.choosle.proto.Protocol (Mon Jan 02 14:01:52 CET 2012)
_direct: 21, 20M in 7ms (0MB) 0
_cast: 21, 20M in 17ms (0MB) 0
_invoke: 21, 20M in 965ms (41MB) 0
instOf: 21, 20M in 284ms (0MB) 0
cast5: 21, 20M in 643ms (0MB) 0
cast6: 21, 20M in 263ms (0MB) 0
reflect: 21, 20M in 15114ms (12MB) 0
map: 21, 20M in 1773ms (41MB) 0
map2: 21, 20M in 1779ms (41MB) 0
map3: 21, 20M in 1557ms (41MB) 0
map4: 21, 20M in 583ms (0MB) 0
visit: 21, 20M in 205ms (0MB) 0

// ~40 dummy instanceof checks
Sun Microsystems Inc. 1.6.0_25 (Java HotSpot(TM) 64-Bit Server VM 20.0-b11)
Args: [-Xms512m, -Xmx512m, -Xbatch, -Xnoclassgc, -Xincgc, -Dfile.encoding=UTF-8]
Windows 7 (6.1 amd64)
ch.choosle.proto.Protocol (Thu Jan 05 10:34:25 CET 2012)
_direct: 21, 20M in 7ms (0MB) 0
_cast: 21, 20M in 16ms (0MB) 0
_invoke: 21, 20M in 954ms (41MB) 0
instOf: 21, 20M in 592ms (0MB) 0
cast5: 21, 20M in 642ms (0MB) 0
cast6: 21, 20M in 261ms (0MB) 0
map4: 21, 20M in 582ms (0MB) 0
visit: 21, 20M in 204ms (0MB) 0

// ~20 dummy instanceof checks
Sun Microsystems Inc. 1.6.0_25 (Java HotSpot(TM) 64-Bit Server VM 20.0-b11)
Args: [-Xms512m, -Xmx512m, -Xbatch, -Xnoclassgc, -Xincgc, -Dfile.encoding=UTF-8]
Windows 7 (6.1 amd64)
ch.choosle.proto.Protocol (Thu Jan 05 10:37:37 CET 2012)
_direct: 21, 20M in 7ms (0MB) 0
_cast: 21, 20M in 16ms (0MB) 0
_invoke: 21, 20M in 953ms (41MB) 0
instOf: 21, 20M in 469ms (0MB) 0
cast5: 21, 20M in 642ms (0MB) 0
cast6: 21, 20M in 261ms (0MB) 0
map4: 21, 20M in 583ms (0MB) 0
visit: 21, 20M in 205ms (0MB) 0

// ~10 dummy instanceof checks
Sun Microsystems Inc. 1.6.0_25 (Java HotSpot(TM) 64-Bit Server VM 20.0-b11)
Args: [-Xms512m, -Xmx512m, -Xbatch, -Xnoclassgc, -Xincgc, -Dfile.encoding=UTF-8]
Windows 7 (6.1 amd64)
ch.choosle.proto.Protocol (Thu Jan 05 11:16:07 CET 2012)
_direct: 21, 20M in 7ms (0MB) 0
_cast: 21, 20M in 17ms (0MB) 0
_invoke: 21, 20M in 964ms (41MB) 0
instOf: 21, 20M in 285ms (0MB) 0
cast5: 21, 20M in 643ms (0MB) 0
cast6: 21, 20M in 261ms (0MB) 0
map4: 21, 20M in 584ms (0MB) 0
visit: 21, 20M in 205ms (0MB) 0

*/