Scenario: main scenario
GivenStories: de/codecentric/jbehave/Precondition.story
Given an empty stack
When the string Java is added
And the string C++ is added
And the string PHP is added
And the element Java is searched for
Then the position returned should be <pos>

Examples:
|pos|second|
|1|bbbb|
|2|dddd|
|3|rrrr|