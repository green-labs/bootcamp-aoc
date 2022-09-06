(ns aoc2018_6
  (:require [clojure.string :as string]
            [clojure.java.io :as io]))

;; 파트 1
;; 입력 : 좌표의 쌍이 N개 주어짐

;; 1, 1 ; id 1 not A
;; 1, 6 ; id 2 not B
;; 8, 3 ; id 3
;; 3, 4 ; D
;; 5, 5 ; E
;; 8, 9; F

;; 각 점은 1 tick이 지날때 마다 상,하,좌,우로 증식함.
;; (x * 1000) + y 

;;  ..........
;;  .A........
;;  ..........
;;  ........C.
;;  ...D......
;;  .....E....
;;  .B........
;;  ..........
;;  ..........
;;  ........F.


;;  aaaaa.cccc
;;  aAaaa.cccc
;;  aaaddecccc
;;  aadddeccCc
;;  ..dDdeeccc
;;  bb.deEeecc
;;  bBb.eeee..
;;  bbb.eeefff
;;  bbb.eeffff
;;  bbb.ffffFf


;; 여기서 . 으로 표기된 부분은 각 출발 지점으로부터 '같은 거리'에 있는 부분을 뜻함.
;; 맵 크기에는 제한이 없어 무한으로 뻗어나간다고 할 때, 가장 큰 유한한 면적의 크기를 반환 (part-1)

(defn parse-file-to-str
  "parse file input to string
   input: string file
   output: string"
  [input]
  (->  input
       io/resource
       slurp
       string/split-lines))



(defn coordinate-str-to-int
  "parse coordinate string to integer value
   input: 1, 2 string indicating coordinate info 
   output: {:X 1 :Y 2}"
  [input]
  (let [[id
         X,
         Y]
        input]
    {:X (Integer/parseInt X) ;1
     :Y (Integer/parseInt Y)
     :id id})) ;; x, y를 이용해서 naming

(defn parse-coordinates
  [input]
  (->> input
       parse-file-to-str
       (map #(re-find #"(\d+), (\d+)" %)) ;; 1, 1  {"1,2" "1" "2"}
       (map coordinate-str-to-int)))

(defn mht-dist
  [x1 y1 x2 y2]
  (+ (abs (- x1 x2))
     (abs (- y1 y2))))

(defn make-dist-map
  [input]
  (let [min-x (apply min (map :X input))
        min-y (apply min (map :Y input))
        max-x (apply max (map :X input))
        max-y (apply max (map :Y input))]
    (for [curr-x (range min-x (inc max-x))
          curr-y (range min-y (inc max-y))
          :let [INF (if (or (= curr-x min-x) (= curr-x max-x) (= curr-y min-y) (= curr-y max-y))
                      true
                      false)]]

      {:coord (+ (* curr-x 1000) curr-y)
       :points
       (->> (for [in input]
             [(:id in)
              (mht-dist (:X in) (:Y in) curr-x curr-y)])
           (sort-by second)
       )
       :inf INF
       })))

(defn find-inf-points
  [input]
  (->> input
       (filter #(= (:inf %) true))
       (map :min)
       (map first)
       set))

(defn not-contains?
  [input key]
  (not (contains? input key)))
(not-contains? #{"8, 3" "3, 4" "1, 6" "1, 1" "5, 5"} "5, 5")


(defn filter-inf-points
  [input]
  (let [inf-points (find-inf-points input)]
    inf-points
    (filter #(not-contains? inf-points (first (:min %))) input)
    ))


(comment
  (->> "2018_6_sample.txt"
       parse-coordinates
       make-dist-map
       (remove #(= (second (first (:points %))) (second (second (:points %)))))
       (map #(assoc % :min (first (:points %))))
       filter-inf-points
       (map :min)
       (map first)
       frequencies
       (sort-by second >)
       first
       ))

;;{:coord 1001, :dist-to-ids {{:id 0, :dist 17} {:id 1, :dist 15}...}
;; 나자신을 결과로 배열로 만드는 것은 어떨까
;; :x 1 :y 3  (1, 3)  (:1003 )  
;; 1001 -> :A :B :C :D :min / 1002->ABCDE
;; A->1001 1002.... 
;; vector 자체를 키워드로 쓸 수 있음

;; 이해하기 어려운 코드 vs 빠른 코드
;; {:1001 {{:id 0, :dist 17} {:id 1, :dist 15}}}


;; 파트 2
;; 안전(safe) 한 지역은 근원지'들'로부터의 맨하탄거리(Manhattan distance, 격자를 상하좌우로만 움직일때의 최단 거리)의 '합'이 N 미만인 지역임.

;;  ..........
;;  .A........
;;  ..........
;;  ...###..C.
;;  ..#D###...
;;  ..###E#...
;;  .B.###....
;;  ..........
;;  ..........
;;  ........F.

;; Distance to coordinate A: abs(4-1) + abs(3-1) =  5
;; Distance to coordinate B: abs(4-1) + abs(3-6) =  6
;; Distance to coordinate C: abs(4-8) + abs(3-3) =  4
;; Distance to coordinate D: abs(4-3) + abs(3-4) =  2
;; Distance to coordinate E: abs(4-5) + abs(3-5) =  3
;; Distance to coordinate F: abs(4-8) + abs(3-9) = 10
;; Total distance: 5 + 6 + 4 + 2 + 3 + 10 = 30

;; N이 10000 미만인 안전한 지역의 사이즈를 구하시오.
