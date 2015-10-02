(ns clue-card.core
  ; (:require [clojure.core])
  )

(defn guess
  "Create a map representing a guess."
  ([player suspect weapon location disprover]
   {:player player :suspect suspect :weapon weapon :location location :disproved-by disprover})
  ([player suspect weapon location disprover disproving-clue]
   {:player player :suspect suspect :weapon weapon :location location :disproved-by disprover :disproving-clue disproving-clue}))

(defn add-guess
  "Add the guess to the game."
  [guess game]
  (merge game {:guesses (conj (:guesses game) guess)}))

(defn get-middle-players
  "Get players between guesser and disprover, e.g. if a guesses, and d disproves with players [a b c d] vector [b c] should be returned."
  [arr guess]
  (let [a (subvec (into arr arr) (inc (.indexOf arr (:player guess))))]
    (subvec a 0 (.indexOf a (:disproved-by guess)))))

(defn has-clue?
  "Does the disprover from the guess already know a clue of clue-type in the game."
  [guess game clue-type]
  (contains? (or (clue-type ((:have game) (:disproved-by guess))) #{}) (clue-type guess)))

(defn already-know-clue?
  "Determines if the perspective user already knows a clue from the disprover that can disprove the guess.
  If this is the case no new knowledge can be captured."
  [guess game]
  (or (has-clue? guess game :suspect)
      (has-clue? guess game :weapon)
      (has-clue? guess game :location)))

(defn combine-clue-type
  "Get all clues of a type from :have knowledge."
  [clue-type knowledge]
  (reduce (fn [prev [_ clues]] (into prev (or (clue-type clues) #{}))) #{} knowledge))

(defn clue-known?
  "Is the clue known?"
  [clue clue-type game]
  (contains? (combine-clue-type clue-type (:have game)) clue))

(defn combine-knowledge
  "Combine clues of all types."
  [knowledge]
  {:suspect  (combine-clue-type :suspect knowledge)
   :weapon   (combine-clue-type :weapon knowledge)
   :location (combine-clue-type :location knowledge)})

(defn filter-known-clues-for-guess
  "Returns clues from the guess that are not already known."
  [guess game]
  (if (already-know-clue? guess game)
    []
    (->> {:suspect  (or (clue-known? (:suspect guess) :suspect game) (:suspect guess))
          :weapon   (or (clue-known? (:weapon guess) :weapon game) (:weapon guess))
          :location (or (clue-known? (:location guess) :location game) (:location guess))}
         (filter (fn [[_ v]] (not= v true)))
         (into {}))))

(defn merge-player-lack [lack guess]
  {:suspect (conj (or (:suspect lack) #{}) (:suspect guess))
   :weapon (conj (or (:weapon lack) #{}) (:weapon guess))
   :location (conj (or (:location lack) #{}) (:location guess))})

(defn deduce-have-knowledge
  "Takes a game and returns a list of new deductions"
  [game]
  (->> (:guesses game)
       (map (fn [g] [(:disproved-by g) (filter-known-clues-for-guess g game)]))
       (filter #(= (count (last %)) 1))))

(defn lack-info-from-game
  "Adds cards known to be lacking to repective players knowledge."
  [game guess]
  (reduce (fn [lack player]
            (merge lack {player (merge-player-lack (lack player) guess)}))
          (or (:lack game) {})
          (get-middle-players (:players game) guess)))

(defn add-deduction
  "Add a single deduction to knowledge."
  [knowledge [player clue]]
  (let [players-deductions (or (knowledge player) {})
        clue-type (ffirst clue)
        clue-card (last (first clue))
        players-deductions-of-type (and clue-type (or (clue-type players-deductions) #{}))]
    (if (nil? clue-type)
      knowledge
      (->> {clue-type (conj players-deductions-of-type clue-card)}
           (merge players-deductions)
           ((fn [x] (merge knowledge {player x})))))))


(defn add-deductions-to-game
  "Learns what can be deduced and adds that to the games knowledge."
  [game]
  (let [deductions (deduce-have-knowledge game)]
    (if (empty? deductions)
      game
      (let [h (reduce add-deduction (:have game) deductions)]
        (merge game {:have h})))))

(defn deduce-knowledge
  "Ensures that all knowledge that can be learned is learned by recursing add-deductions-to-game until there are no more changes."
  ([game] (deduce-knowledge game nil))
  ([game old-game]
   (if (= game old-game)
     game
     (recur
       (add-deductions-to-game game)
       game))))

;TODO Add deductions based on lacking knowledge
(defn add-turn
  "Uses guess information to deterine if any new information can be deduced based on the game as of yet with the added guess.
  This will recursively call itself until no more additions can be made."
  [guess game]
  (let [g (merge (add-guess guess game) {:lack (lack-info-from-game game guess)})]
    (if (already-know-clue? guess game)
      g
      (deduce-knowledge g))))
