Feature: state space exploration with vanishing states


@tangibleAndVanishing
Scenario: Parsing all immediate transitions
  Given I use the Petri net located at /all_immediate.xml
  When I generate the exploration graph
  Then I expect to see 2 state transitions