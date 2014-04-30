Feature: state space exploration with vanishing states


@tangibleAndVanishing
Scenario Outline: Parsing examples:
  Given I use the Petri net located at <file>
  When I generate the exploration graph
  Then I expect to see <number> state transitions

  Examples:
  | file                              | number |
  | /all_immediate.xml                |   2    |
  | /simple_vanishing.xml             |   7    |
  | /simple_color.xml                 |   2    |
  | /complex_color.xml                |   8    |
  | /complex_color_all_immediate.xml  |   8    |
  | /cyclic_vanishing.xml             |   9    |
  | /timeless_trap.xml                |   7    |
