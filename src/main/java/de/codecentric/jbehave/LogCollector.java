package de.codecentric.jbehave;


import de.codecentric.jbehave.util.Https;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.reporters.NullStoryReporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * LogCollector
 *
 * @author Dzung Nguyen
 * @version $Id LogCollector 2015-03-31 10:25:30z dzungvnguyen $
 * @since 1.0
 */
public class LogCollector extends NullStoryReporter {
  //~ class properties ========================================================
  private TestRun testRun;
  private static String logBasePath;
  private static Long jobInstanceId;

  //~ class members ===========================================================


  private Story story;
  private ExamplesTable table;

  public void beforeStory(Story story, boolean givenStory) {
    this.logBasePath = resolveLogBasePath();
    this.jobInstanceId = resolveJobInstanceId();
    if (!givenStory) {
      this.story = story;
    }
  }

  //
  public void afterStory(boolean givenStory) {

  }

  public void scenarioNotAllowed(Scenario scenario, String filter) {
  }

  public void beforeScenario(String scenarioTitle) {
    this.testRun = new TestRun(this.story, scenarioTitle);
  }

  public void afterScenario() {
    this.testRun.submitTestRunLog();
    this.testRun = null;
  }

  public void beforeExamples(List<String> steps, ExamplesTable table) {
    this.table = table;
    this.testRun.examplesTable = table;
  }

  public void example(Map<String, String> tableRow) {
    this.table.getRows().indexOf(tableRow);
  }

  public void afterExamples() {

  }

  public void successful(String step) {
    if (testRun.status != TestRun.Status.SKIP && testRun.status != TestRun.Status.FAIL) {
      testRun.status = TestRun.Status.SKIP;
    }
  }

  public void ignorable(String step) {
    if (testRun.status == TestRun.Status.PASS) {
      testRun.status = TestRun.Status.SKIP;
    }
  }

  public void pending(String step) {
    if (testRun.status == TestRun.Status.PASS) {
      testRun.status = TestRun.Status.SKIP;
    }
  }

  public void notPerformed(String step) {
    if (testRun.status == TestRun.Status.PASS) {
      testRun.status = TestRun.Status.SKIP;
    }
  }

  public void failed(String step, Throwable cause) {
    testRun.status = TestRun.Status.FAIL;
    testRun.cause = cause;
  }


  /**
   * @return the log base path of log attachment.
   */
  private static String resolveLogBasePath() {
    if (logBasePath == null) {
      logBasePath = System.getenv("LOG_PATH");
      if (Https.isEmpty(logBasePath)) {
        logBasePath = System.getProperty("LOG_PATH");
      }
    }

    return logBasePath;
  }


  /**
   * @return the job instance identifier.
   */
  private static Long resolveJobInstanceId() {
    if (jobInstanceId == null) {
      String jobInstanceIdStr = System.getenv("JOB_INSTANCE_ID");
      if (Https.isEmpty(jobInstanceIdStr)) {
        jobInstanceIdStr = System.getProperty("JOB_INSTANCE_ID", "-1");
      }

      jobInstanceId = Long.parseLong(jobInstanceIdStr);
    }

    return jobInstanceId;
  }
  //~ class helpers ===========================================================

  /**
   * TestRun
   *
   * @author Dzung Nguyen
   * @version $Id TestRun 2015-03-31 13:02:30z dzungvnguyen $
   * @since 1.0
   */
  private static class TestRun {

    public enum Status {
      PASS("PASS"), SKIP("SKIP"), FAIL("FAIL");

      private final String status;

      Status(String status) {
        this.status = status;
      }

      public String getName() {
        return status;
      }
    }

    //~ class properties ======================================================
    long startTime;
    Story story;
    Status status;
    Throwable cause;
    String scenarioTitle;
    ExamplesTable examplesTable;

    //~ class members =========================================================
    /**
     * Creates {@link TestRun test run} from the given scenario instance.
     *
     * @param scenario the givn scenario instance.
     */

    /**
     * Creates {@link TestRun test run} instance.
     */
    public TestRun(Story story, String scenarioTitle) {
      this.startTime = System.currentTimeMillis();
      this.story = story;
      this.scenarioTitle = scenarioTitle;
    }

    /**
     * Submit test run log.
     */
    public void submitTestRunLog() {
      try {
        // do not support empty scenario.
        String submitContent;
        if (status == Status.FAIL) {
          submitContent = toJsonString(getClassName(), getMethodName(), status.getName(), cause);
        } else {
          submitContent = toJsonString(getClassName(), getMethodName(), status.getName());
        }

        System.out.println(submitContent);
//        Https.submitLog(submitContent.getBytes(Charset.forName("UTF-8")));
      } finally {
        startTime = -1;
      }
    }

    /**
     * @return the class name.
     */
    private String getClassName() {
      return this.story.getPath();
    }

    /**
     * @return the method name.
     */
    private String getMethodName() {
//      if ("scenario_outline".equals(scenario.getKeyword())
//        || "Scenario Outline".equals(scenario.getKeyword())) {
//        return scenario.getName() + '(' + scenario.getId() + ')';
//      }
//
      return scenarioTitle;
    }

    /**
     * @return the json object of the given test result.
     */
    private String toJsonString(String className, String methodName, String status) {
      return toJsonString(className, methodName, status, null);
    }

    /**
     * @return the json object of the given test result.
     */
    private String toJsonString(String className, String methodName, String status, Throwable cause) {
      StringBuilder testLogBuilder = new StringBuilder("{");

      testLogBuilder.append(quote("jobInstanceId")).append(":").append(resolveJobInstanceId()).append(",");
      testLogBuilder.append(quote("className")).append(":").append(quote(className)).append(",");
      testLogBuilder.append(quote("methodName")).append(":").append(quote(methodName)).append(",");
      testLogBuilder.append(quote("status")).append(":").append(quote(status)).append(",");

      testLogBuilder.append(quote("startTime")).append(":").append((startTime < 0 ? System.currentTimeMillis() : startTime)).append(",");
      testLogBuilder.append(quote("endTime")).append(":").append(System.currentTimeMillis());

      // build the test log.
      String testLogFile = writeExceptionToLog(cause);
      if (testLogFile != null) {
        testLogBuilder.append(",");
        testLogBuilder.append(quote("logPath")).append(":").append(quote(testLogFile));
      }
      testLogBuilder.append("}");

      return testLogBuilder.toString();
    }

    /**
     * @return the exception log file.
     */
    private String writeExceptionToLog(Throwable cause) {
      if (cause != null) {
        File logFile = new File(resolveLogBasePath(), UUID.randomUUID().toString() + ".txt");
        try {
          PrintWriter writer = new PrintWriter(new FileWriter(logFile));
          cause.printStackTrace(writer);
          closeQuietly(writer);
        } catch (IOException ioe) {
          // never mind, I don't want to handle this exception.
        }

        // return absolute file.
        return logFile.getAbsolutePath();
      }

      return null;
    }

    /**
     * Close the writer.
     *
     * @param writer the given writer to store data.
     */
    private void closeQuietly(PrintWriter writer) {
      try {
        if (writer != null) {
          writer.flush();
          writer.close();
        }
      } catch (Exception ex) {
        // never mind, we don't want to handle this exception.
      }
    }

    /**
     * Quote the string.
     *
     * @param string the given string to qoute.
     * @return the quote value.
     */
    private static String quote(String string) {
      if (string != null && string.length() != 0) {
        char c = 0;
        int len = string.length();
        StringBuffer sb = new StringBuffer(len + 4);
        sb.append('\"');

        for (int i = 0; i < len; ++i) {
          char b = c;
          c = string.charAt(i);
          switch (c) {
            case '\b':
              sb.append("\\b");
              break;
            case '\t':
              sb.append("\\t");
              break;
            case '\n':
              sb.append("\\n");
              break;
            case '\f':
              sb.append("\\f");
              break;
            case '\r':
              sb.append("\\r");
              break;
            case '\"':
            case '\\':
              sb.append('\\');
              sb.append(c);
              break;
            case '/':
              if (b == 60) {
                sb.append('\\');
              }

              sb.append(c);
              break;
            default:
              if (c >= 32 && (c < 128 || c >= 160)) {
                sb.append(c);
              } else {
                String t = "000" + Integer.toHexString(c);
                sb.append("\\u" + t.substring(t.length() - 4));
              }
          }
        }

        sb.append('\"');
        return sb.toString();
      } else {
        return "\"\"";
      }
    }
  }
}
