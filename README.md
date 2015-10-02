# clue-card

A Clojure library designed to ... well, that part is up to you.

## Usage

FIXME

## Terminology

| Term               | Definition                                                                                                                                                                                                                                                                                                                                 |
|--------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| perspective player | the player that is using the app, we know everything from his/her prespective                                                                                                                                                                                                                                                               |
| know               | cards that are in the perspective players hand                                                                                                                                                                                                                                                                                              |
| discovered         | cards that have been shown to the perspective player to disprove a guess (or because of a spy glass)                                                                                                                                                                                                                                        |
| deduced            | cards that perspective player can reason exists in another players hand because he/she has evidence of other cards (e.g. for a guess Mustard with the knife in the dinning room if perspective player has both Mustard and knife in his hand he can reason that dinning room is in another players hand if that player disproves the guess) |
| evidence           | cards that the perspective player either knows, has discovered, or has deduced                                                                                                                                                                                                                                                              |
| suspect            | cards that the perspective player cannot reason exist in another players hand during a guess (e.g. for a guess Mustard with the knife in the dinning room if perspective player has none of the cards but the guess was disproved then we must assume that the disproving player has one of these clues)                                    |


## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
