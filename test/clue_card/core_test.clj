(ns clue-card.core-test
  (:require [clojure.test :refer :all]
            [clue-card.core :refer :all]))

(def clue-game-test {:suspect  #{"Mustard" "White" "Peach" "Green" "Plumb"}
                     :weapon   #{"knife" "pipe" "poison" "revolver"}
                     :location #{"hall" "dining" "kitchen" "fountain"}})

(def base-game-1 {:players     ["a" "b" "c" "d"]
                  :prespective "c"
                  :have        {"c" {:suspect  #{"Mustard" "Plumb"}
                                     :weapon   #{"knife"}
                                     :location #{"hall" "dining"}}}
                  ;:lack        {"c" {:suspect  #{"White" "Peach" "Green"}
                  ;                   :weapon   #{"pipe" "poison" "revolver"}
                  ;                   :location #{"kitchen" "fountain"}}}
                  :guesses     []
                  :poss-clues  clue-game-test})

(def base-game-2 {:players     ["a" "b" "c" "d" "e"]
                  :prespective "d"
                  :have        {"d" {:suspect  #{"Mustard" "Plumb"}
                                     :weapon   #{"knife"}
                                     :location #{"hall" "dining"}}}
                  ;:lack        {"d" {:suspect  #{"White" "Peach" "Green"}
                  ;                   :weapon   #{"pipe" "poison" "revolver"}
                  ;                   :location #{"kitchen" "fountain"}}}
                  :guesses     []
                  :poss-clues  clue-game-test})

(deftest utility-tests
  (testing "various utilities"
    (are [a b] (= a b)
               (get-middle-players (:players base-game-1) (guess "a" "White" "knife" "dining" "b"))
               []

               (get-middle-players (:players base-game-1) (guess "a" "White" "knife" "dining" "c"))
               ["b"]

               (get-middle-players (:players base-game-1) (guess "c" "White" "knife" "dining" "b"))
               ["d" "a"]

               (get-middle-players (:players base-game-2) (guess "c" "White" "knife" "dining" "b"))
               ["d" "e" "a"]

               (get-middle-players (:players base-game-1) (guess "d" "White" "gun" "hall" "b"))
               ["a"]

               (has-clue? (guess "a" "White" "gun" "kitchen" "b") (into base-game-1 {:have {"b" {:weapon #{"gun"}}}}) :weapon)
               true

               (already-know-clue? (guess "a" "White" "gun" "kitchen" "b") (into base-game-1 {:have {"b" {:weapon #{"gun"}}}}))
               true
               (already-know-clue? (guess "a" "White" "gun" "kitchen" "b") (into base-game-1 {:have {"b" {:weapon #{"knife"}}}}))
               false

               (combine-clue-type :weapon {"a" {:suspect #{"White" "Green"} :weapon #{"gun"}} "b" {:weapon #{"knife"}}})
               #{"gun" "knife"}

               (combine-clue-type :suspect {"a" {:suspect #{"White" "Green"} :weapon #{"gun"}} "b" {:weapon #{"knife"}}})
               #{"White" "Green"}

               (combine-clue-type :location {"a" {:suspect #{"White" "Green"} :weapon #{"gun"}} "b" {:weapon #{"knife"}}})
               #{}

               (combine-knowledge {"c" {:suspect  #{"Mustard" "Plumb"}
                                        :weapon   #{"knife"}
                                        :location #{"hall" "dining"}}
                                   "b" {:weapon #{"gun"}}})
               {:suspect  #{"Mustard" "Plumb"}
                :weapon   #{"gun" "knife"}
                :location #{"hall" "dining"}}

               (clue-known? "White" :suspect base-game-1)
               false
               (clue-known? "Mustard" :suspect base-game-1)
               true
               (clue-known? "gun" :weapon base-game-1)
               false
               (clue-known? "knife" :weapon base-game-1)
               true

               (filter-known-clues-for-guess (guess "a" "White" "gun" "kitchen" "b") base-game-1)
               {:suspect "White" :weapon "gun" :location "kitchen"}

               (filter-known-clues-for-guess (guess "a" "White" "knife" "hall" "b") base-game-1)
               {:suspect "White"}

               (deduce-have-knowledge (merge base-game-1 {:guesses [(guess "d" "White" "gun" "kitchen" "a") (guess "a" "White" "knife" "hall" "b")]}))
               [["b" {:suspect "White"}]]

               (deduce-have-knowledge (merge base-game-1 {:guesses [(guess "d" "White" "gun" "kitchen" "a") (guess "a" "White" "knife" "hall" "b") (guess "d" "Mustard" "pipe" "hall" "a")]}))
               [["b" {:suspect "White"}] ["a" {:weapon "pipe"}]]

               (deduce-have-knowledge (merge base-game-1 {:guesses [(guess "d" "White" "gun" "kitchen" "a")]}))
               []

               (add-deduction (:have base-game-1) ["b" {:weapon "gun"}])
               {"b" {:weapon #{"gun"}}, "c" {:weapon #{"knife"}, :suspect #{"Mustard" "Plumb"}, :location #{"hall" "dining"}}}

               (add-deduction (:have base-game-1) [])
               {"c" {:weapon #{"knife"}, :suspect #{"Mustard" "Plumb"}, :location #{"hall" "dining"}}}

               (:have (add-deductions-to-game (merge base-game-1 {:guesses [(guess "a" "White" "knife" "hall" "b")]})))
               {"b" {:suspect #{"White"}}, "c" {:weapon #{"knife"}, :suspect #{"Mustard" "Plumb"}, :location #{"hall" "dining"}}}

               (:have (add-deductions-to-game (merge base-game-1 {:guesses [(guess "a" "White" "gun" "hall" "b")]})))
               {"c" {:weapon #{"knife"}, :suspect #{"Mustard" "Plumb"}, :location #{"hall" "dining"}}}

               (:have (add-deductions-to-game (merge base-game-1 {:guesses [(guess "a" "White" "gun" "hall" "b")]})))
               {"c" {:weapon #{"knife"}, :suspect #{"Mustard" "Plumb"}, :location #{"hall" "dining"}}}

               (merge-player-lack {:suspect #{"White"} :weapon #{"gun" "knife"} :location #{"dining"}} (guess "a" "Green" "rope" "dining" "c"))
               {:suspect #{"White" "Green"} :weapon #{"gun" "knife" "rope"} :location #{"dining"}}

               (merge-player-lack {:suspect #{"White"} :weapon #{"gun"} :location #{"hall"}} (guess "a" "White" "rope" "dining" "c"))
               {:suspect #{"White"} :weapon #{"gun" "rope"} :location #{"dining" "hall"}}

               (lack-info-from-game base-game-1 (guess "d" "White" "gun" "hall" "b"))
               {"a" {:suspect #{"White"} :weapon #{"gun"} :location #{"hall"}}}

               (lack-info-from-game (merge base-game-1 {:lack {"a" {:suspect #{"Green"} :weapon #{"rope"} :location #{"hall"}}}}) (guess "c" "White" "gun" "hall" "b"))
               {"a" {:suspect #{"White" "Green"} :weapon #{"gun" "rope"} :location #{"hall"}} "d" {:suspect #{"White"} :weapon #{"gun"} :location #{"hall"}}}
               )))

(deftest turn-tests
  (testing "various turns"
    (are [a b] (= a b) nil nil
               (:guesses (add-turn
                           (guess "a" "White" "gun" "kitchen" "b")
                           base-game-1))
               [(guess "a" "White" "gun" "kitchen" "b")]

               ;new turn gets added to :turns in game
               (:guesses (add-turn
                           (guess "a" "White" "gun" "kitchen" "b")
                           base-game-1))
               [(guess "a" "White" "gun" "kitchen" "b")]

               ;prespective player can guess a disproving card
               (:have (add-turn
                        (guess "a" "White" "gun" "kitchen" "b")
                        (into base-game-1 {:have {"b" {:weapon #{"gun"}}}})))
               {"b" {:weapon #{"gun"}}}

               ;prespective player can deduce a card is in disprovers hand because two of three clues in his hand
               (:have (add-turn (guess "a" "Plumb" "gun" "dining" "b")
                                (merge base-game-1 {:id "can-deduce"})))
               {"c" {:weapon #{"knife"}, :suspect #{"Mustard" "Plumb"}, :location #{"hall" "dining"}}
                "b" {:weapon #{"gun"}}}

               ;nothing gets added to :have when I cannot know it
               (:have (add-turn (guess "a" "White" "gun" "kitchen" "b")
                                base-game-1))
               {"c" {:weapon #{"knife"}, :suspect #{"Mustard" "Plumb"}, :location #{"hall" "dining"}}}

               ;same player makes the same guess with the same disprover
               (:have (add-turn (guess "a" "Plumb" "gun" "dining" "b")
                                (merge base-game-1 {:guesses [(guess "a" "Plumb" "gun" "dining" "b")]})))
               {"c" {:weapon #{"knife"}, :suspect #{"Mustard" "Plumb"}, :location #{"hall" "dining"}}
                "b" {:weapon #{"gun"}}}

               ;same guess with the same disprover
               (:have (add-turn (guess "d" "Plumb" "gun" "dining" "b")
                                (merge base-game-1 {:guesses [(guess "a" "Plumb" "gun" "dining" "b")]})))
               {"c" {:weapon #{"knife"}, :suspect #{"Mustard" "Plumb"}, :location #{"hall" "dining"}}
                "b" {:weapon #{"gun"}}}

               (:lack (add-turn (guess "c" "Plumb" "gun" "dining" "b") base-game-1))
               {"a" {:suspect #{"Plumb"} :weapon #{"gun"} :location #{"dining"}} "d" {:suspect #{"Plumb"} :weapon #{"gun"} :location #{"dining"}}}
               )))
