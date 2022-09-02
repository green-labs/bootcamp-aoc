(ns aoc2018_4
    (:require [clojure.string :as string]
            [clojure.java.io :as io]))
;; 파트 1
;; 입력:

;; [1518-11-01 00:00] Guard #10 begins shift :id 10
;; 우선은 reduce ~~로 if로
;; input parsing을 두번 : line 별로 -> 그 다음 정리
;;앞의 타임스탬프를 먼저 자르고 뒤에 처리 정렬 (sort로)

;; [1518-11-01 00:05] falls asleep :id 10
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
;; partition-by 
;; {:id 10 :min 1} {:id 10 :min 2}
;; {(guard #10) (falls asleep) (wakes up)}

;; 키워드: 가드(Guard) 번호, 자는 시간(falls asleep), 일어나는 시간(wakes up).
;; 각 가드들은 교대 근무를 시작하고 (begins shift) 졸았다가 일어났다를 반복함.
;; 위의 예시에서 10번 가드는 0시 5분에 잤다가 25분에 일어나고, 또 0시 30분에 잠들었다가 0시 55분에 깨어남.
;; 가드들에 대해서 자고 깨는 시간 정보들이 입력으로 주어짐.

;; 파트 1은 “주어진 입력에 대해서, 가장 오랜시간 잠들어있었던 가드의 ID와, 그 가드가 가장 빈번하게 잠들어 있었던 분(minute)의 곱을 구하라”
;; 만약 20번 가드가 0시 10분~36분, 다음날 0시 5분~11분, 다다음날 0시 11분~13분 이렇게 잠들어 있었다면, “11분“이 가장 빈번하게 잠들어 있던 ‘분’. 그럼 답은 20 * 11 = 220.

(defn make-guard-day
  [input]
  (let [arranged-input (apply concat input)]
    {:id (:id (first arranged-input))
     :minutes (partition 2 (map :minute (rest arranged-input)))}))

(defn parse-file-to-str-line
  "parse file input to string list
   input: string file with line alignment
   output: vetor of strings"
  [input]
  (->  input
       (io/resource)
       (slurp)
       (string/split-lines)))

;; table을 애초에 :id "#10", :minutes (5 25 30 55)}
;; 말고 {:id "#10", :minutes (5 25)} {:id "#10" :minutes (30 55)}
;; 으로 생성 혹은 분리하는 방법은 없었을까 -> 그냥 partitions로 나눔
;; 자꾸 map을 쓰게됨.. reductions로 된 예제가 있다면 보고싶음

(defn make-sleep-table
  [input]
  (let [id (:id input)
        mintable (:minutes input)]
  (for [min-item mintable]
    (for [minute (range (first min-item) (last min-item))]
      {:id id
       :minute minute}))))

(defn fabric-info-str-to-int
  "parse fabric information string to integer value
   input: string vector including fabric info [#1@1,3:4x4 1 1 3 4 4]
   output: (1 3 4 4)"
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
     :id id
     }))

(defn max-sleep-guard
  [input]
  (->> input
       (map :id)
       frequencies ;; {"#10" 50, "#99" 33}
       (sort-by val) ;; (["#99" 33] ["#10" 50])
       last ;;["#10" 50]
       first ;;"#10"
       ))

(defn max-sleep-guard-minute
  [id input]
  (->> input
       ;(prn "id"id)
       (filter #(= (:id %) id))
       frequencies
       (sort-by val)
       last
       first
       :minute)
)
(defn find-sleepyhead
  [input]
  (let [sleepy-id (max-sleep-guard input)]
    (*
       (max-sleep-guard-minute sleepy-id input)
       (Integer/parseInt (re-find #"\d+" sleepy-id))))
)
(comment
  (->> "2018_4_sample.txt"
       parse-file-to-str-line
       sort
       (map #(re-find #"(?:\d+-)(\d+)-(\d+) (\d+):(\d+)] (\S+) (\S+)" %))
       (map fabric-info-str-to-int)
       (partition-by #(= (:guard %) "Guard"))
       (partition 2)
       (map make-guard-day)
       (mapcat make-sleep-table)
       (apply concat)
       find-sleepyhead       
       )
)

(defn max-frequent-sleep-min
  [input]
  (let [id (Integer/parseInt (re-find #"\d+" (:id input)))
        min (:minute input)]
    (* id min)))
;; 파트 2
;; 주어진 분(minute)에 가장 많이 잠들어 있던 가드의 ID과 그 분(minute)을 곱한 값을 구하라.

(comment
  (->> "2018_4_sample.txt"
       parse-file-to-str-line
       sort
       (map #(re-find #"(?:\d+-)(\d+)-(\d+) (\d+):(\d+)] (\S+) (\S+)" %))
       (map fabric-info-str-to-int)
       ;(map :guard)
       (partition-by #(= (:guard %) "Guard"))
       (partition 2)
       (map make-guard-day)
       (mapcat make-sleep-table)
       (apply concat)
       frequencies
       (sort-by val)
       last
       first
       max-frequent-sleep-min
       ))
