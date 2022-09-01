(ns aoc2018_3
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.set :as set]))


;; 파트 1
;; 다음과 같은 입력이 주어짐.

;; #1 @ 1,3: 4x4 {:x 1 :y 3 :xlen 4 :ylen 4} => for {1,3 1,4 1,5 1,6 "2,3"} range!! for 도 쓰기 좋음
;; #2 @ 3,1: 4x4 {3,1 }
;; #3 @ 5,5: 2x2

;; 규칙으로 포맷 정의 가능 regular expression 으로 => replace 몇개  

;; # 뒤에 오는 숫자는 ID, @ 뒤에 오는 숫자 쌍 (a, b)는 시작 좌표, : 뒤에 오는 (c x d)는 격자를 나타냄.
;; 입력의 정보대로 격자 공간을 채우면 아래와 같이 됨.

;;      ........
;;      ...2222.
;;      ...2222.
;;      .11XX22.
;;      .11XX22.
;;      .111133.
;;      .111133.
;;      ........

;; 여기서 XX는 ID 1, 2, 3의 영역이 두번 이상 겹치는 지역.
;; 겹치는 지역의 갯수를 출력하시오. (위의 예시에서는 4)

(defn parse-file-to-str-line
  "parse file input to string list
   input: string file with line alignment
   output: vetor of strings"
  [input]
  (->  input
       (io/resource)
       (slurp)
       (string/split-lines)))

(defn fabric-info-str-to-int
  "parse fabric information string to integer value
   input: string vector including fabric info [#1@1,3:4x4 1 1 3 4 4]
   output: (1 3 4 4)"
  [input]
  (let [[_
         id
         starting-x
         starting-y
         width
         height]
        input]
    (map #(Integer/parseInt %) [id starting-x starting-y width height])))

(defn make-set-of-fabric-in-need
  [input]
  "make set of fabric coordinate which we need with given x, y, width, height
   input: (5 5 2 2) => {:starting-x 5 :starting-y 5}//vector index에 의미를 부여하지 않도록 받을때부터 map으로
   output: [5 5] [5 6] [6 5] [6 6]"
  (let [[id
         starting-x
         starting-y
         width
         height]
        input]
    {:id id
     :fab-set
     (set (for [x (range starting-x (+ starting-x width))
                y (range starting-y (+ starting-y height))]
            [x y]))}))

(defn count-overlap-fabric
  [input]
  (count
   (filter (fn [[_ v]] (> v 1))
    input)))

(comment
  (->> "2018_3_sample.txt"
       parse-file-to-str-line
       (map #(re-find #"#(\d+) @ (\d+),(\d+): (\d+)x(\d+)" %))
       (map fabric-info-str-to-int)
       (map make-set-of-fabric-in-need)
       (map :fab-set)
       (apply concat)
       frequencies
       count-overlap-fabric))

;; 파트 2
;; 입력대로 모든 격자를 채우고 나면, 정확히 한 ID에 해당하는 영역이 다른 어떤 영역과도 겹치지 않음
;; 위의 예시에서는 ID 3 이 ID 1, 2와 겹치지 않음. 3을 출력.
;; 겹치지 않는 영역을 가진 ID를 출력하시오. (문제에서 답이 하나만 나옴을 보장함)

(defn get-overlap-fabric
  [input]
  (->> input
       (mapcat :fab-set)
       (frequencies)))


(defn parse-fabric-data
  [input]
  (->> input
       parse-file-to-str-line
       (map #(re-find #"#(\d+) @ (\d+),(\d+): (\d+)x(\d+)" %))
       (map fabric-info-str-to-int)
       (map make-set-of-fabric-in-need)))


(defn make-overlap-map
  [input]
  (->> input
       (mapcat :fab-set)
       frequencies
       (filter (fn [[_ v]] (> v 1)))
       keys)
)
(defn count-inter-with-id
  [overlap-map fabric] 
  (count
   (set/intersection
    (set overlap-map)
    (set (:fab-set fabric)))))

(defn compare-overlap-fabric
  [input]
  (let [overlap-map (make-overlap-map input)
        ]
    (:id (first 
    (filter
     #(zero? ((partial count-inter-with-id overlap-map) %))
     input))
  ))
)  

(comment
  (->> "2018_3_sample.txt"
       parse-fabric-data
       compare-overlap-fabric
       )
)

