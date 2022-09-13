(ns aoc2018_7
  (:require [clojure.string :as string]
            [clojure.java.io :as io]))

(def step-regex #"Step (\S) must be finished before step (\S) can begin.")

(defn parse-file-to-str
  "parse file input to string
   input: string file
   output: string"
  [input]
  (->  input
       io/resource
       slurp
       string/split-lines))

(defn make-step-table
  "make a table map indicating step requrirement
   input: \"Step C must be finished before step A can begin.\"
   output: {:req C, :step A}
   "
  [input]
  (let [[_ req step] (re-find step-regex input)]
    {:req (first req)
     :step (first step)
     :time (int (first step))
     :sched 0}))

(defn make-dummy-table
  "add dummy table for steps which don't have requirement
   input: 
   {:req C, :step A}
   {:req C, :step F}..
   output:
   {:req D, :step E}
   {:req F, :step E}
   {:req d, :step A}
   {:req d, :step B}
   "
  [input]
  (let [standard-time (dec (int \A))]
    (concat input
            (for [ch (range (int \A) (inc (int \F)))]
              {:req \d :step (char ch) :time (- ch standard-time) :sched 0}))))

(defn find-available-step
  "find available step which 1) doesn't have dependency to other step 2) first in alphabetical order
   input: 
   {:req D, :step E}
   {:req F, :step E}
   {:req d, :step A}
   {:req d, :step B}..
   output: C "
  [input]
  (->> input
       (map :step)
       frequencies
       (sort-by first) ;sort by alphabet
       (sort-by second) ;sort by frequency
       first ;minimum frequency
       first ;key
       ))

(defn schedule-work
  "gather available steps in each timing after repeating removing dependency and find available step
   input: 
   {:req D, :step E}
   {:req F, :step E}
   {:req d, :step A}
   {:req d, :step B}...
   output: \"CABDFE\""
  [input]
  (loop [in input
         res ""]
    (if (empty? in)
      res
      (let [available-step (find-available-step in)]
        (recur (remove #(or (= (:step %) available-step) (= (:req %) available-step)) in)
               (str res available-step))))))

(comment
  (->> "2018_7_sample copy.txt"

       ;parse
       parse-file-to-str
       (map make-step-table)

       ;process
       make-dummy-table

       ;aggregate
      ; schedule-work)
       ))


(dec (int \b))


(def ds '({:req \C, :step \A}
          {:req \C, :step \F}
          {:req \A, :step \B}
          {:req \A, :step \D}
          {:req \B, :step \E}
          {:req \D, :step \E}
          {:req \F, :step \E}
          {:req \d, :step \A, :time 1}
          {:req \d, :step \B, :time 2}
          {:req \d, :step \C, :time 3}
          {:req \d, :step \D, :time 4}
          {:req \d, :step \E, :time 5}
          {:req \d, :step \F, :time 6}))

(map #(if (= (:req %) \d) (update % :time dec) %) ds)


(defn find-available-steps-in-5
  "find available five steps which 1) doesn't have dependency to other step 2) first in alphabetical order
   input: 
   {:req D, :step E}
   {:req F, :step E}
   {:req d, :step A}
   {:req d, :step B}..
   output: C, D.. "
  [input limit]
  (->> input
       (filter #(< (:sched %) 1))
       (map :step)
       frequencies
       (sort-by first) ;sort by alphabet
       ;(sort-by second) ;sort by frequency
       (filter #(= (second %) 1)) ;appeared only once
       (take limit) ;limit
       (map first)))

(defn count-steps-in-progress
  [input]
  (->> input
       (filter #(>= (:sched %) 1))
       count))

(set '())

(defn schedule-available-work
  [input]
  (let [steps-in-progress (count-steps-in-progress input)
        available-worker (- 5 steps-in-progress)
        add-available-step (set (if (> available-worker 0)
                                  (find-available-steps-in-5 input available-worker)
                                  '()))]
    (prn steps-in-progress)
    ;available-worker
    ;(prn input)
    (if (empty? add-available-step)
      input
      (map #(if (contains? add-available-step (:step %)) (update % :sched inc) %) input))))


(defn remove-work-ended
  [input]
  (let [removable-work (filter #(= (:time %) 0) input)
        removable-work-step (set (map #(:step %) removable-work))]
    (prn removable-work-step)
    (remove #(or (contains? removable-work-step (:step %)) (contains? removable-work-step (:req %))) input))
)


(defn organize-work-in-second
  [input]
  (->> input 
       schedule-available-work
       (map #(if (>= (:sched %) 1) (update % :time dec) %))
       remove-work-ended)) 

(defn schedule-work-in-seconds
  "calculate seconds to execute steps in each timing after repeating removing dependency and find available step
   input: 
   {:req D, :step E}
   {:req F, :step E}
   {:req d, :step A}
   {:req d, :step B}...
   output: 15 "
  [input]
  (loop [in input
         seconds 0]
    (if (empty? in)
      seconds
      (recur (organize-work-in-second in)
             (inc seconds)))))

;(take 5 '(1 2 3))

(comment
  (->> "2018_7_sample.txt"

       ;parse
       parse-file-to-str
       (map make-step-table)

       ;process
       make-dummy-table

       ;aggregate
       ;schedule-available-work
       ;organize-work-in-second

       schedule-work-in-seconds
       ))
