package org.mentalsmash.whadl;

import java.io.File;

import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.slf4j.LoggerFactory;

//import ch.qos.logback.classic.LoggerContext;
//import ch.qos.logback.classic.joran.JoranConfigurator;
//import ch.qos.logback.core.joran.spi.JoranException;
//import ch.qos.logback.core.util.StatusPrinter;

public class WhadlTester {
	public static void main(String[] args) throws Exception {
//		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//
//		try {
//			JoranConfigurator configurator = new JoranConfigurator();
//			configurator.setContext(lc);
//			lc.reset();
//			configurator.doConfigure("data/logback.xml");
//		} catch (JoranException je) {
//			// StatusPrinter will handle this
//		}
//		StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

		int maxTries = Integer.parseInt(args[0]);
		int maxValidatorTries = Integer.parseInt(args[1]);

		String army = args[2];
		String list = args[3];

		int failed = 0;
		long maxRunTime = 0;
		long minRunTime = Long.MAX_VALUE;
		long[] runTimes = new long[maxTries];

		int i = 0;
		while ((i++) < maxTries) {
			long startTime = 0;
			long stopTime = 0;
			try {
				// System.out.println("VALIDATOR MAX TRIES: " +
				// maxValidatorTries);
				Whadl w = new Whadl(maxValidatorTries);
				Army baseArmy = w.parseArmy(new File("data/BaseData.whadl"));
				w.validate(baseArmy, true);
				Army a = w.parseArmy(new File("data/" + army + ".whadl"));
				w.validate(a, true);

				startTime = System.currentTimeMillis();
				ArmyInstance ai = w.parseArmyInstance(new File("builds/" + list
						+ ".whadl"));
				w.validate(ai);
				stopTime = System.currentTimeMillis();
				System.out.println("TRY N." + i + " SUCCESS");
			} catch (Exception e) {
				stopTime = System.currentTimeMillis();
				failed++;
				System.out.println("TRY N." + i + " FAIL (fails=" + failed
						+ ")");
			}
			long runTime = stopTime - startTime;
			if (runTime < minRunTime) {
				minRunTime = runTime;
			}

			if (runTime > maxRunTime) {
				maxRunTime = runTime;
			}

			runTimes[i - 1] = runTime;
		}

		long runTimesSum = 0;
		for (int j = 0; j < runTimes.length; j++) {
			runTimesSum += runTimes[j];
		}

		double avgRunTime = runTimesSum / maxTries;

		System.out.println("FAILS: " + failed + "/" + maxTries + " "
				+ avgRunTime + " " + maxRunTime + " " + minRunTime);
		System.out.println("FAIL-RATIO:"
				+ ((((double) failed) / ((double) maxTries)) * 100));

	}
}
