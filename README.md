# clu-card

A library to deduce as much knowledge of a Cluedo\* or Clue\* game as possible using only the knowledge that a player can 
gain by observing the actions of each turn during game play.

## Usage
    
The main function in this library is the `add-turn` function which takes two maps, one representing a guess the other
representing a game, and returns a new game with any new deducible information in `:have` or `:lack` which are each a
map with players' names as keys and a map of what knowledge we know about their hand. `:have` contains knowledge about 
what cards and their types each player has in their hand, `:lack` about the cards known not to be in their hand.

    (add-turn (guess "player A" "Mustard" "rope" "dining room" "player C") game-map)
    
`(guess player suspect weapon location disproved-by)` produces a map representing a guess
    
    (guess "player A" "Mustard" "rope" "dining room" "player C") =>
      {:player       "player A"
       :suspect      "Mustard"
       :weapon       "rope"
       :location     "dining room"
       :disproved-by "player C"}
       
For more information about usage I encourage you to look at `core_test.clj` or run the tests using

    $lein test

## Terminology

| Term               | Definition                                                                                        |
|--------------------|---------------------------------------------------------------------------------------------------|
| perspective player | the player that is using the app, we know everything from his/her perspective                     |
| know/knowledge     | knowledge the the prospective player can deduct that players either :have or :lack                |

## License

Copyright © 2015 Mahon Baldwin

Distributed under the Eclipse Public License version 1.0.

\* Clue and Cluedo are registered trademarks of Waddingtons, Parker Brothers, Hasbro, and/or Winning Moves. 
Use of this term is done under fair use as this library is for educational purposes only. The library was named
clu-card to avoid trademark issues and possible litigation. Names of functions and bindings in this library were not named 
in such a way since they would not typically viewed by the general public if this library were to be published in an application. 
Please contact the repository owner with requests in this regard.
