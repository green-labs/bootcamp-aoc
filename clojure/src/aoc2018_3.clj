(ns aoc2018_3
  (:require (clojure [string :as s] [set :as st])))

(def sample "resources/day3.sample.txt")

(defn split-line-input [input]
  (s/split-lines input))

(defn get-increment-position-list
  "전달받은 x y 만큼 2차원 배열 리스트를 반환한다.\n
   input: [x 2 y 2]
   output: ([0 0] [0 1] [1 0] [1 1])
   "
  [[x y]]
  (for [x (range x)
        y (range y)]
    [x y]))

(defn sum-position-list
  "position 목록에 전달받은 현재위치를 더해서 return\n
   input 
    - list : ([0 0] [0 1] [0 2] [1 0] [1 1] [1 2])
    - position: [2 1]]
   output: ([2 1] [2 2] [2 3] [3 1] [3 2] [3 3])
   "
  [{:keys [position-list current-position]}]
  (map (fn [pos] [(+ (first pos) (first current-position)) (+ (last pos) (last current-position))]) position-list))


(defn split-input-by-regex
  "문자열 목록들을 정규식으로 split
   input [`#1 @ 108,350: 22x29` `#2 @ 370,638: 13x12`]
   output ([`#1` `108,350` `22x29`] [`#2` `370,638` `13x12`])
   "
  [list]
  (map (fn [v] (s/split v #" @ |: ")) list))

(defn mapping-data-by-list
  "vector sequence를 hash-map 으로 재가공
   input ([`#1` `@` `108,350:` `22x29`] [`#2` `@` `370,638:` `13x12`])
   output ({:start-pos (108 350), :id `#1`, :grid (22 29)} {:start-pos (370 638), :id `#2`, :grid (13 12)}) 
   "
  [list]
  (map (fn [v] {:start-pos (vec (map read-string (s/split (v 1) #",")))
                :id        (v 0)
                :grid      (vec (map read-string (s/split (v 2) #"x")))}) list))

(defn mapping-position-id [mapping-data]
  (let [[grid id]         ((juxt :grid :id) mapping-data)
        inc-pos-list      (get-increment-position-list grid)
        incresed-pos-list (sum-position-list {:position-list    inc-pos-list
                                              :current-position (:start-pos mapping-data)})]
    (reduce (fn [acc v] (assoc acc (keyword (s/join "," v)) [id])) {} incresed-pos-list)))


(defn group-by-id-list-by-position
  "mapping 된 데이터를 position 기준으로 id를 group 처리
   input '({:start-pos (1 3) :id `#1` :grid (4 4)} {:start-pos (3 1) :id `#2` :grid (4 4)})
   output {:1,5 [`#1`], :5,6 [`#3`], :5,5 [`#3`], :4,6 [`#1`], :1,6 [`#1`], :4,5 [`#1`], :4,4 [`#1` `#2`]}
   "
  [list]
  (reduce (fn [acc mapping-data]
            (merge-with into acc (mapping-position-id mapping-data))) {} list))

(defn make-rect-position
  "시작점에서부터 grid 값만큼 사각형 꼭지점좌표 return"
  [mapping-data]
  (let [[start-pos grid] ((juxt :start-pos :grid) mapping-data)]
    {:x-start (first start-pos)
     :x-end   (+ (first start-pos) (first grid))
     :y-start (last start-pos)
     :y-end   (+ (last start-pos) (last grid))}))

(defn overlap?
  "첫번째 인자의 사각형과 두번째 인자의 사각형 좌표 겹침 여부\n
   :id가 같은 data인 경우 겹침검사 pass\n
   input: [{:id #3, :rect {:x-start 5, :x-end 7, :y-start 5, :y-end 7}} {:id #1, :rect {:x-start 1, :x-end 5, :y-start 3, :y-end 7}}]
   output: boolean
   "
  [target1 target2]
  (if (= (:id target1) (:id target2))
    false
    (let [rect1                                 (:rect target1)
          rect2                                 (:rect target2)
          [x-start-1 x-end-1 y-start-1 y-end-1] ((juxt :x-start :x-end :y-start :y-end) rect1)
          [x-start-2 x-end-2 y-start-2 y-end-2] ((juxt :x-start :x-end :y-start :y-end) rect2)]
      (not (or (or (and (>= x-start-1 x-start-2) (>= x-start-1 x-end-2)) (and (<= x-end-1 x-start-2) (<= x-end-1 x-end-2)))
               (or (and (>= y-start-1 y-start-2) (>= y-start-1 y-end-2)) (and (<= y-end-1 y-start-2) (<= y-end-1 y-end-2))))))))
(defn make-postions-and-id [list]
  (map (fn [v] {:id            (:id v)
                :position-list (sum-position-list {:position-list    (get-increment-position-list (:grid v))
                                                   :current-position (:start-pos v)})}) list))


(defn overlap-by-intersection?
  [[mapping-data1 mapping-data2]]
  (let [positions1 (:position-list mapping-data1)
        positions2 (:position-list mapping-data2)]
    (->> (st/intersection (set positions1)
                          (set positions2))
         count
         (#(< 0 %)))))

(defn check-all-not-overlap
  "target의 rect좌표가 목록의 rect좌표들에 겹치는게 하나도 없는지 여부 확인
   input target => {:id #1, :rect {:x-start 1, :x-end 5, :y-start 3, :y-end 7}}
         list => [{:id #1, :rect {:x-start 1, :x-end 5, :y-start 3, :y-end 7}}
            {:id #2, :rect {:x-start 3, :x-end 7, :y-start 1, :y-end 5}}
            {:id #3, :rect {:x-start 5, :x-end 7, :y-start 5, :y-end 7}}
            {:id #4, :rect {:x-start 10, :x-end 12, :y-start 10, :y-end 12}}]
   "
  [target list]
  (every? #(not (overlap? target %)) list))

(defn find-one-not-overlap-data
  "data 목록중에 겹침이 없는 data 하나가 나올경우 즉시 해당 data return"
  [list]
  (reduce (fn [list target]
            (if (check-all-not-overlap target list)
              (reduced target)
              list)) list list))


(defn parse-id [mapping-data]
  (s/replace (:id mapping-data) "#" ""))

(defn make-id-and-rect
  "id 와 rect 로 이루어진 hash-map return
   input: ({:start-pos [108 350], :id `#1`, :grid [22 29])})
   output: ({:id `#1` :rect {:x-start 1 :x-end 5 :y-start 3 :y-end 7})
   "
  [list]
  (map (fn [v] {:id   (:id v)
                :rect (make-rect-position v)}) list))

(defn is-not-overlab-position-from-position-list
  "position 들을 비교하여 중복이 자기자신만 있는지 체크"
  [position-list position]
  (->> position-list
       (map #(overlap-by-intersection? [position %]))
       (filter #(identity %))
       count
       (#(when (= 1 %) position))))

(defn find-not-overlap [list]
  (keep #(is-not-overlab-position-from-position-list list %) list))

(defn part1 []
  (->> (slurp sample)
       (split-line-input)
       (split-input-by-regex)
       (mapping-data-by-list)
       (group-by-id-list-by-position)
       vals
       (filter #(> (count %) 1))
       count))

(defn parse-input []
  (->> (slurp sample)
       (split-line-input)
       (split-input-by-regex)
       (mapping-data-by-list)))

(defn part1-refact []
  (->> (parse-input)
       (group-by-id-list-by-position)
       vals
       (filter #(> (count %) 1))
       count))

(defn part2 []
  (->> (slurp sample)
       (split-line-input)
       (split-input-by-regex)
       (mapping-data-by-list)
       (make-id-and-rect)
       (find-one-not-overlap-data)
       (parse-id)))

(defn part2-refact []
  (->> (parse-input)
       (make-postions-and-id)
       (find-not-overlap)
       first
       (parse-id)))


(comment
  (part1)
  (part1-refact)
  (part2)
  (part2-refact))

;; 1 3   5  7
;; 3 1   7  5
;; 5 5   7  7


;; 파트 1
;; 다음과 같은 입력이 주어짐.

;; #1 @ 1,3: 4x4
;; #2 @ 3,1: 4x4
;; #3 @ 5,5: 2x2

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




;; 파트 2
;; 입력대로 모든 격자를 채우고 나면, 정확히 한 ID에 해당하는 영역이 다른 어떤 영역과도 겹치지 않음
;; 위의 예시에서는 ID 3 이 ID 1, 2와 겹치지 않음. 3을 출력.
;; 겹치지 않는 영역을 가진 ID를 출력하시오. (문제에서 답이 하나만 나옴을 보장함)
