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
   input: \"1, 2\", \"1\", \"2\"  
   output: {:X 1 :Y 2 :id \"1, 2\"}"
  [input]
  (let [[id X Y] input]
    {:X (Integer/parseInt X) ;1
     :Y (Integer/parseInt Y) ;2
     :id id})) ;; x, y를 이용해서 naming


(defn parse-coordinates
  "parse coordinate file to integer map
   input: 1, 2 \n 2, 4
   output: {:X 1 :Y 2 :id \"1, 2\"} {:X 2 :Y 4 :id \"2, 4\"}"  
  [input]
  (->> input
       parse-file-to-str
       (map #(re-find #"(\d+), (\d+)" %)) ;; 1, 1  {"1,2" "1" "2"}
       (map coordinate-str-to-int)))


(defn mht-dist
  "calculate manhattan distance between to coordinates
   input: 1 2, 2 4
   output: 3"
  [x1 y1 x2 y2]
  (+ (abs (- x1 x2))
     (abs (- y1 y2))))

(defn make-dist-map
  "make distance map from each coordinates of field
   with distances to all given points
   input: given points like {:X 1 :Y 2 :id \"1, 2\"} {:X 2 :Y 4 :id \"2, 4\"}
   output: map using one dimensional point
   ({:coord 1001,
  :points ([\"1, 1\" 0] [\"1, 6\" 5] ...),
  :inf true}
  {:coord 1002,
  :points ([\"1, 1\" 1] [\"1, 6\" 4]... ),
  :inf true}...
   "
  [input]
  (let [min-x (apply min (map :X input)) ;;define field start and end
        min-y (apply min (map :Y input))
        max-x (apply max (map :X input))
        max-y (apply max (map :Y input))]
    (for [curr-x (range min-x (inc max-x))
          curr-y (range min-y (inc max-y))
          :let [inf? (or (= curr-x min-x) (= curr-x max-x) (= curr-y min-y) (= curr-y max-y))]]

      {
       :coord [curr-x curr-y]
       :points
       (->> (for [in input]
              [(:id in)
               (mht-dist (:X in) (:Y in) curr-x curr-y)])
            (sort-by second))
       :inf inf?})))

;;boolean에는 ? 쓰면 가독성이 높아짐 boundary로 작명했어도 될듯 !!

;
(defn find-inf-points
  "find points which will extend infinitely checking boundaries
   using :inf keyword
   input: 
   {:coord 7001,
   :points ([\"8, 3\" 3] [\"1, 1\" 6] [\"5, 5\" 6] [\"3, 4\" 7] [\"8, 9\" 9] [\"1, 6\" 11]),
   :inf true,
   :min [\"8, 3\" 3]}...
   output: #{\"8, 3\" \"1, 6\" \"1, 1\" \"8, 9\"}
   "
  [input]
  (->> input
       (filter :inf) ;;idiomatic 주의!!
       (map :min)
       (map first)
       set))

;찾아보기 !!!! compliment  

(defn filter-inf-points
  "remove the points which will extend infinitely
   input:
   {:coord 7001,
    :points ([\"8, 3\" 3] [\"1, 1\" 6] [\"5, 5\" 6] [\"3, 4\" 7] [\"8, 9\" 9] [\"1, 6\" 11]),
    :inf true,
    :min [\"8, 3\" 3]}...
   
   output: 
   {:coord 2003,
    :points ([\"3, 4\" 2] [\"1, 1\" 3] [\"1, 6\" 4] [\"5, 5\" 5] [\"8, 3\" 6] [\"8, 9\" 12]),
    :inf false,
    :min [\"3, 4\" 2]}..."
  [input]
  (let [inf-points (find-inf-points input)] 
    (remove #(contains? inf-points (first (:min %))) input)
    ))

(defn has-same-dist-to-multipoints?
  "return boolean whether input coordinate has multiple points 
   with same distance
   input: 
   {:coord 7001,
    :points ([\"8, 3\" 3] [\"1, 1\" 3] [\"5, 5\" 6] [\"3, 4\" 7] [\"8, 9\" 9] [\"1, 6\" 11]),
    :inf true,
    :min [\"8, 3\" 3]}
   output: true"
  [input]
  (let [points (:points input)]
    (= (second (first points)) (second (second points)))))

(comment
  (->> "2018_6_sample.txt"
       ;parsing
       parse-coordinates 
       make-dist-map 

       ;preprocess 
       (remove has-same-dist-to-multipoints?)
       (map #(assoc % :min (first (:points %)))) ;associate min point
       filter-inf-points
       
       ;aggregation
       (map :min) ;;the closest point
       (map first) ;;id
       frequencies
       
       ;print
       (sort-by second >) ;;val
       first)) ;;max

;; [[id distance]] min :dist

;;{:coord 1001, :dist-to-ids {{:id 0, :dist 17} {:id 1, :dist 15}...}
;; 나자신을 결과로 배열로 만드는 것은 어떨까
;; :x 1 :y 3  (1, 3)  (:1003 )  
;; 1001 -> :A :B :C :D :min / 1002->ABCDE
;; A->1001 1002.... 
;; vector 자체를 키워드로 쓸 수 있음

;; 이해하기 어려운 코드 vs 빠른 코드
;; {:1001 {{:id 0, :dist 17} {:id 1, :dist 15}}}
;;내일 ~~ 2020 1,4

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

(defn sum-dist-to-points
  "calculate sum of distances to all points
   input: 
    {:coord 7001,
    :points ([\"8, 3\" 3] [\"1, 1\" 3] [\"5, 5\" 6] [\"3, 4\" 7] [\"8, 9\" 9] [\"1, 6\" 11]),
    :inf false,
    :min [\"8, 3\" 3]}
   output: 39"
  [input]
  (->> input
       :points
       (map second)
       (apply +)))

(comment
  (->> "2018_6_sample.txt"
       ;parsing
       parse-coordinates
       make-dist-map

       ;preprocess
       (map sum-dist-to-points)

       ;aggregation
       (filter #(> (int 10000) %))
       
       ;print
       count
       ) 
)