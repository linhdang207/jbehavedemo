package de.codecentric.jbehave;

import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.reporters.NullStoryReporter;

import java.util.List;
import java.util.Map;

/**
 * Created by thongmgnguyen on 12/7/2016.
 */
public class qTestCustomReport extends NullStoryReporter {
  //  public void storyNotAllowed(Story story, String filter) {
//    System.out.print("storyNotAllowed");
//  }
//
  private Story story;
  private ExamplesTable table;

  public void beforeStory(Story story, boolean givenStory) {
    System.out.println("beforeStory " + story.getName() + " " + givenStory);
    this.story = story;
  }

  //
  public void afterStory(boolean givenStory) {
    System.out.println("afterStory " + givenStory);
  }

  public void scenarioNotAllowed(Scenario scenario, String filter) {
    System.out.println("storyNotAllowed");
  }

  public void beforeScenario(String scenarioTitle) {
    System.out.println("beforeScenario " + scenarioTitle);
  }

//  public void scenarioMeta(Meta meta) {
//    System.out.println("storyNotAllowed");
//  }

  public void afterScenario() {
    System.out.println("afterScenario");
  }

/*
public void givenStories(GivenStories givenStories) {
System.out.println("storyNotAllowed");
}
public void givenStories(List<String> storyPaths) {
System.out.println("storyNotAllowed");
}
*/

  public void beforeExamples(List<String> steps, ExamplesTable table) {
    System.out.println("beforeExamples");
    this.table = table;
  }

  public void example(Map<String, String> tableRow) {
    System.out.println("example ");
    this.table.getRows().indexOf(tableRow);
  }
//
//  public void afterExamples() {
//
//  }

  public void successful(String step) {
    System.out.println("step: " + step + " successful");
  }

  public void ignorable(String step) {
    System.out.println("step: " + step + " ignorable");
  }

  public void pending(String step) {
    System.out.println("step: " + step + " pending");
  }

//  public void notPerformed(String step) {
//
//  }

  public void failed(String step, Throwable cause) {
    System.out.println("step: " + step + " failed ");
    System.out.println("cause: " + cause);
  }

//  public void failedOutcomes(String step, OutcomesTable table) {
//
//  }

//  public void dryRun() {
//
//  }
}
