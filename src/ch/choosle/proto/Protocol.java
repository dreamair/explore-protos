package ch.choosle.proto;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

public class Protocol {

	public interface ResultProvider {
		Object getResult();
	}

	public void start() {
		Properties prs = System.getProperties();
		System.out.println(prs.get("java.vendor") + " " + prs.get("java.version") + " (" + prs.get("java.vm.name") + " "
				+ prs.get("java.vm.version") + ")");
		RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = RuntimemxBean.getInputArguments();
		System.out.println("Args: " + arguments);
		System.out.println(prs.get("os.name") + " (" + prs.get("os.version") + " " + prs.get("os.arch") + ")");
		System.out.println(this.getClass().getName() + " (" + (new Date()) + ")");
	}

	public void msg(String msg, int numRuns, long timeNanos, long memory, long memoryTotal, Object result) {
		System.out.println(msg + ": " + result + ", " + (numRuns / 1000000) + "M in " + (timeNanos / 1000 / 1000) + "ms ("
				+ (memory / 1024 / 1024) + "MB) " + (memoryTotal / 1024 / 1024));
	}

	public void msg(String msg, Throwable ex) {
		System.err.println(msg);
		ex.printStackTrace();
	}

	public void end() {
	}

	public void measure(String title, int numRuns, Callable<Void> callable, ResultProvider result) {
		try {
			System.gc();
			// -XX:CompileThreshold=10000
			// http://www.oracle.com/technetwork/java/javase/tech/vmoptions-jsp-140102.html
			for (int i = 0; i < 20000; i++)
				callable.call();
			System.gc();
			Runtime r = Runtime.getRuntime();
			long freeMemory = r.freeMemory();
			long totalMemory = r.totalMemory();
			long s = System.nanoTime();
			for (int i = 0; i < numRuns; i++)
				callable.call();
			this.msg(title, numRuns, System.nanoTime() - s, (r.totalMemory() - r.freeMemory()) - (totalMemory - freeMemory),
					r.totalMemory() - totalMemory, result.getResult());
		}
		catch (Exception ex) {
			this.msg(title, ex);
		}
	}

}
