Narrative:
In order to develop an application that requires a stack efficiently
As a development team
I would like to use an interface and implementation in Java directly
 
GivenStories: de/codecentric/jbehave/stack_scenarios_example.story,de/codecentric/jbehave/Precondition.story
Scenario:DEMO-2  Stack search given

Given an empty stack
When the string Java is added
And the string C++ is added
And the string PHP is added
And the element Java is searched for
Then the position returned should be 5