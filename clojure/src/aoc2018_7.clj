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
    {:req (first req) ; required step (prerequisite) for this step
     :step (first step) ; target step
     :time (int (first req))
     :sched false
     })) ; only for dummy

(defn make-dummy-table
  "add dummy table for steps which don't have requirement
   input: 
   {:req C, :step A, :time 65 :sched false}
   {:req C, :step A, :time 70 :sched false}..
   output:
   {:req D, :step E :time 97 :sched false}
   {:req F, :step E :time 98 :sched false}
   {:req d, :step A :time 61 :sched false}
   {:req d, :step B :time 62 :sched false}...
   "
  [input]
  (let [standard-time (dec (int \A))]
    (concat input
            (for [ch (range (int \A) (inc (int \Z)))]
              {:req nil ;indicating dummy
               :step (char ch)
               :time (+ (- ch standard-time) 60) ;time consuming for this step
               :sched false})))) ; flag indicating that this step is in progress

(defn find-available-step
  "find available step which 
   1) doesn't have dependency to other step 2) first in alphabetical order
   input: 
   {:req D, :step E :time 97 :sched false}
   {:req F, :step E :time 98 :sched false}
   {:req d, :step A :time 61 :sched false}
   {:req d, :step B :time 62 :sched false}...
   output: C "
  [input]
  (->> input
       (map :step)
       frequencies
       (sort-by (juxt val key)) ;sort by order frequency->alphabet
       first ;minimum frequency
       key ;key
       ))

;(\C 1 \D 3 \E 4) -> {:id \C :val 1} ... (map fn[k v]  {:id \C :val 1} )
;(sort... :id :val)

(comment
  (->> '(\A \B \A \C \B)
       frequencies
       (apply min-key val)
  ))

(defn schedule-work
  "gather available steps in each timing after repeating removing dependency and find available step
   input: 
   {:req D, :step E :time 97 :sched false}
   {:req F, :step E :time 98 :sched false}
   {:req d, :step A :time 61 :sched false}
   {:req d, :step B :time 62 :sched false}...
   output: \"CABDFE\""
  [input]
  (loop [in input
         res ""]
    (if (empty? in)
      res
      (let [available-step (find-available-step in)]
        (recur (remove #(or (= (:step %) available-step)
                            (= (:req %) available-step)) in)
               (str res available-step))))))

; loop-recur => reduce & reduced => iterate, take-while / drop-while

(comment
  (->> "2018_7_sample copy.txt"

       ;parse
       parse-file-to-str
       (map make-step-table)

       ;process
       make-dummy-table

       ;aggregate
       schedule-work
       ))



(defn find-available-steps-to-limit
  "find available steps to limit number which doesn't have dependency to other step
   input: 
   {:req D, :step E :time 97 :sched false}
   {:req F, :step E :time 98 :sched false}
   {:req d, :step A :time 61 :sched false}
   {:req d, :step B :time 62 :sched false}, 2
   output: A, B"
  [input limit]
  (->> input
       (filter #(false? (:sched %))) ;target unscheduled steps
       (map :step)
       frequencies
       (filter #(= (val %) 1)) ; ;appeared only once - no dependency
       (sort-by key) ;sort by alphabet 
       (take limit) ;limit
       (map key)))

(defn count-steps-in-progress
  "count steps which are in progress, indicated by :sched true
   input:
    {:req D, :step E :time 97 :sched false}
   {:req F, :step E :time 98 :sched false}
   {:req d, :step A :time 61 :sched true}
   {:req d, :step B :time 62 :sched true}...
   output: 2"
  [input]
  (->> input
       (filter :sched) ; filter steps in progress
       count))


(defn schedule-available-work
  "check available work and make them in progress by making true flag in :sched
   input:
   {:req d, :step A :time 61 :sched false}
   {:req d, :step B :time 62 :sched true}
   output:
   {:req d, :step A :time 61 :sched true}
   {:req d, :step B :time 62 :sched true}
   "
  [input]
  (let [steps-in-progress (count-steps-in-progress input)
        available-worker (- 5 steps-in-progress)
        available-step (set (if (> available-worker 0)
                              (find-available-steps-to-limit input available-worker)
                              nil))] ;;when 용법 찾아보기
    (if (nil? available-step) ;nil? 로 체크해봐도 됨
      input 
      (map #(if (available-step (:step %)) ;contains 생략 가능
              (update % :sched (constantly true))
              %) input))))


(defn remove-work-ended
  "remove completed steps which have no left time,
  and requirement lines to them because there is no more dependency
   input: 
   {:req A, :step E :time 97 :sched false} ; A is no more required 
   {:req F, :step E :time 98 :sched false}
   {:req d, :step A :time 0 :sched true}; step A is completed
   output: 
   {:req F, :step E :time 98 :sched false}
   "
  [input]
  (let [removable-work (filter #(= (:time %) 0) input)
        removable-work-step (set (map #(:step %) removable-work))]
    (remove #(or (removable-work-step (:step %)) 
                 (removable-work-step (:req %))) input))
)

;A data -> B data (필요한 것만 뽑아서) ->c
; Adata -> A' (update )-> A'' -> A''' finite state machine 2020 day8
; frequency 쓰기전에 dummy 를 만드는 쪽으로 !

(defn organize-work-in-second
  "organize steps in interation of time tick- check steps in every seconds
   input: 
     {:req F, :step E :time 97 :sched false} ;requirement completed
     {:req d, :step F :time 0 :sched true} ; completed -> deleted
     {:req d, :step C :time 15 :sched false} ; started because doesn't have prerequisite
   output: 
     {:req d, :step C :time 14 :sched true}"
  [input] ;[requirement & others] & 찾아보기 
  (->> input 
       schedule-available-work ;make the flag :sched true if the step is available
       (map #(if (:sched %) (update % :time dec) %)) ; decrease time when :sched == true
       remove-work-ended)) ; remove completed steps with the lines which required them


(defn schedule-work-in-seconds
  "accumulate seconds to execute steps in each timing 
   after repeating removing dependency and find available step
   input: 
     {:req D, :step E :time 97 :sched false}
     {:req d, :step D :time 10 :sched false}
     {:req d, :step E :time 15 :sched false}
   output: 25 (steps were excuted by D->E order, and took 25 seconds in total)"
  [input]
  (loop [in input
         seconds 0]
    (if (empty? in)
      seconds
      (recur (organize-work-in-second in)
             (inc seconds)))))

(comment
  (->> "2018_7_sample.txt"

       ;parse
       parse-file-to-str
       (map make-step-table)

       ;process
       make-dummy-table

       ;aggregate
       schedule-work-in-seconds
       ))
