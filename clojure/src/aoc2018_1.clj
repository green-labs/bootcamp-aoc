(ns aoc2018-1
  (:require [clojure.string :as string]))

;; 파트 1
;; 주어진 입력의 모든 숫자를 더하시오.
;; 예) +10 -2 -5 +1 이 입력일 경우 4를 출력

#_(def vals (string/split-lines
           (slurp "resources/2018_1_sample.txt")))


#_(apply + (map #(Integer/parseInt %) 
             (string/split-lines 
             (slurp "resources/2018_1_sample.txt"))))


;; 파트 2
;; 주어진 입력의 숫자를 더할 때 마다 나오는 숫자 중, 처음으로 두번 나오는 숫자를 리턴하시오.
;; 예) +3, +3, +4, -2, -4 는 10이 처음으로 두번 나오는 숫자임.
;; 0 -> 3 (+3) -> 6 (+3) -> 10(+4) -> 8(-2) -> 4(-4) -> 7(+3) -> 10(+3) -> ...




(def infile (map #(Integer/parseInt %)
               (string/split-lines
                (slurp "resources/2018_1_sample.txt"))))

;(contains? sum 1)
;(def t 0)
;(let [t (+ (last sum) (first vals))] prn t)
;(sum 1)
;(sum 0)
;(last sum)
;(first vals)
;(+ (last sum) 1)
;(+ nil 1) 
;(let [t 1] [(conj t sum)])

;(conj sum 1)
(defn chksum [vals] 
    (loop [v vals
           tempsum 0
           setofsum #{}]
      (prn v)
      (let [tempsum1 (+ tempsum v)]
        (prn v tempsum setofsum)
        (if (or (empty? v) (contains? setofsum tempsum1))
          tempsum1
          (recur (rest v)
                 tempsum1
                 (conj setofsum tempsum1))))))
      
      #_(defn chksum [setofsum vals]
    (let [t 0] (prn t)
         (let [t (+ t (first vals))] (prn t) 
          (if (or (= (last vals) nil) (contains? setofsum t)) 
            t 
            (comp (rest vals) (conj setofsum t) (prn setofsum))))))

#_(defn chksum [setofsum vals]
 (loop [v vals]
    (if (empty? v)
      v
      (recur (rest v)
             ))))
     ; )

(chksum infile)
;(prn infile sum)
