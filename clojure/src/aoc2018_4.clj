(ns aoc2018_4
  (:require [clojure.string :as string]
            [clojure.java.io :as io]))
;; 파트 1
;; 입력:

;; [1518-11-01 00:00] Guard #10 begins shift 
;; [1518-11-01 00:05] falls asleep 
;; [1518-11-01 00:25] wakes up
;; [1518-11-01 00:30] falls asleep
;; [1518-11-01 00:55] wakes up
;; [1518-11-01 23:58] Guard #99 begins shift
;; [1518-11-02 00:40] falls asleep
;; [1518-11-02 00:50] wakes up
;; [1518-11-03 00:05] Guard #10 begins shift
;; [1518-11-03 00:24] falls asleep
;; [1518-11-03 00:29] wakes up
;; [1518-11-04 00:02] Guard #99 begins shift
;; [1518-11-04 00:36] falls asleep
;; [1518-11-04 00:46] wakes up
;; [1518-11-05 00:03] Guard #99 begins shift
;; [1518-11-05 00:45] falls asleep
;; [1518-11-05 00:55] wakes up

;; 키워드: 가드(Guard) 번호, 자는 시간(falls asleep), 일어나는 시간(wakes up).
;; 각 가드들은 교대 근무를 시작하고 (begins shift) 졸았다가 일어났다를 반복함.
;; 위의 예시에서 10번 가드는 0시 5분에 잤다가 25분에 일어나고, 또 0시 30분에 잠들었다가 0시 55분에 깨어남.
;; 가드들에 대해서 자고 깨는 시간 정보들이 입력으로 주어짐.

;; 파트 1은 “주어진 입력에 대해서, 가장 오랜시간 잠들어있었던 가드의 ID와, 그 가드가 가장 빈번하게 잠들어 있었던 분(minute)의 곱을 구하라”
;; 만약 20번 가드가 0시 10분~36분, 다음날 0시 5분~11분, 다다음날 0시 11분~13분 이렇게 잠들어 있었다면, “11분“이 가장 빈번하게 잠들어 있던 ‘분’. 그럼 답은 20 * 11 = 220.

;; ({:id "#10", :minutes ((5 25) (30 55))}
;;  {:id "#99", :minutes (40 50)}
;;  {:id "#10", :minutes (24 29)}
;;  {:id "#99", :minutes (36 46)}
;;  {:id "#99", :minutes (45 55 56 59)})

(defn find-the-most-frequent-value
  "return the most frequent value from given collection
   input: [1 2 1 3 1]
   output: 1"
  [input]
  (->> input
       frequencies 
       (sort-by val >)
       first 
       first
       ))

(defn parse-file-to-str-line
  "parse file input to string list
   input: string file with line alignment
   output: vetor of strings"
  [input]
  (->  input
       (io/resource)
       (slurp)
       (string/split-lines)))

(defn guard-chart-str-to-int
  "parse guard work record string to integer value
   input: string vector including work info
   output: {:month 2, :day 3, :hour 0, :minute 3, :guard \"Guard,\" :id \"#691\"}"
  [input]
  (let [[_
         month
         day
         hour
         minute
         guard
         id]
        input]
    {:month (Integer/parseInt month)
     :day (Integer/parseInt day)
     :hour (Integer/parseInt hour)
     :minute (Integer/parseInt minute)
     :guard guard
     :id id}))

(defn parse-guard-records
  "parse input string file to guard working record for a day 
   to extract minutes in sleep
   input: 
    [1518-11-01 00:00] Guard #10 begins shift 
    [1518-11-01 00:05] falls asleep 
   output:
    (({:month 11, :day 1, :hour 0, :minute 0, :guard \"Guard\", :id \"#10\"})
    ({:month 11, :day 1, :hour 0, :minute 5, :guard \"falls\", :id \"asleep\"}
   "
  [input]
  (->> input
       parse-file-to-str-line
       sort
       (map #(re-find #"(?:\d+-)(\d+)-(\d+) (\d+):(\d+)] (\S+) (\S+)" %))
       (map guard-chart-str-to-int)
       (partition-by #(= (:guard %) "Guard"))
       (partition 2)))

(defn make-guard-day
  "divide guard records to guard id and sleeping timestamp
   input:
    (({:month 11, :day 1, :hour 0, :minute 0, :guard \"Guard\", :id \"#10\"})
    ({:month 11, :day 1, :hour 0, :minute 5, :guard \"falls\", :id \"asleep\"}
     {:month 11, :day 1, :hour 0, :minute 25, :guard \"wakes\", :id \"up\"}...))
   output: 
    {:id \"#10\", :minutes ((5 25) (30 55))}
   "
  [input]
  (let [arranged-input (apply concat input)]
    {:id
     (:id (first arranged-input))
     :minutes
     (partition 2 (map :minute (rest arranged-input)))}))

(defn make-sleep-table
  "make a table with a guard id and minutes when the guard slept
   input:
    {:id \"#10\", :minutes ((5 25) (30 55))}
   output:
    ({:id \"#10\", :minute 5} {:id \"#10\", :minute 6}
     {:id \"#10\", :minute 7}...)
   "
  [input]
  (let [id (:id input)
        mintable (:minutes input)]
    (for [min-item mintable]
      (for [minute (range (first min-item) (last min-item))]
        {:id id
         :minute minute}))))

(defn make-sleep-table-for-guards
  "make a table for all guards records to gather slept minutes
   input: 
   (({:month 11, :day 1, :hour 0, :minute 0, :guard \"Guard\", :id \"#10\"})
    ({:month 11, :day 1, :hour 0, :minute 5, :guard \"falls\", :id \"asleep\"}
     {:month 11, :day 1, :hour 0, :minute 25, :guard \"wakes\", :id \"up\"}...))
   output:
    ({:id \"#10\", :minute 5} {:id \"#10\", :minute 6}...
     {:id \"#99\", :minute 40} ...)
   "
  [input]
  (->> input
       (map make-guard-day)
       (mapcat make-sleep-table)
       (apply concat)))

(defn max-sleep-guard
  "find a guard who slepth most frequently and return its id
   input:
    ({:id \"#10\", :minute 5} {:id \"#10\", :minute 6}...
     {:id \"#99\", :minute 40} ...)
   output: \"#10\"
   "
  [input]
  (->> input
       (map :id) ;; {"#10" "#10" "#10" "#99" "#99" ...}
       find-the-most-frequent-value))

;; 질문: 이건 왜 빨간줄뜰까? (map (fn [k v] {(keyword k) v}))

(defn max-sleep-guard-minute
  "find the minute when the guard who has given id slept most frequently
   input: 
     \"#10\"
     ({:id \"#10\", :minute 5} {:id \"#10\", :minute 6}...
     {:id \"#99\", :minute 40} ...)
   output: 5
   "
  [id input]
  (->> input
       (filter #(= (:id %) id)) ;; {{:id "#10", :minute 42} {:id "#10", :minute 22}...
       find-the-most-frequent-value
       :minute ;; 24
       ))

(defn find-sleepyhead
  "find sleepyhead guard who slept most frequently
   and the minute when the guard slept most
   input: 
     ({:id \"#10\", :minute 5} {:id \"#10\", :minute 6}...
     {:id \"#99\", :minute 40} ...)
   output:
     50 ; 10 (id of guard) * 5 (the minutes the guard love to sleep)
   "
  [input]
  (let [sleepy-id (max-sleep-guard input)]
    (*
     (max-sleep-guard-minute sleepy-id input)
     (Integer/parseInt (re-find #"\d+" sleepy-id)))))

(comment
  (->> "2018_4_sample.txt"
       ;;parsing
       parse-guard-records

       ;;process
       make-sleep-table-for-guards

       ;aggregation & print
       find-sleepyhead))


;; 파트 2
;; 주어진 분(minute)에 가장 많이 잠들어 있던 가드의 ID과 그 분(minute)을 곱한 값을 구하라.
;; 가장 많이 집중해서 잔 guard와 그 분(minutes)의 곱

(defn max-frequent-sleep-min
  "print the multiply of id of a guard who slept most at his favorite minute
   and the minute
   input: {:id \"#99\", :minute 45}
   output: 4455
   "
  [input]
  (let [id (Integer/parseInt (re-find #"\d+" (:id input)))
        min (:minute input)]
    (* id min)))

(comment
  (->> "2018_4_sample.txt"
       ;;parsing
       parse-guard-records

       ;;process
       make-sleep-table-for-guards

       ;;aggregation
       find-the-most-frequent-value

       ;;print
       max-frequent-sleep-min))
