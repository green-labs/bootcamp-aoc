(ns aoc2018-1
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

;; 파트 1
;; 주어진 입력의 모든 숫자를 더하시오.
;; 예) +10 -2 -5 +1 이 입력일 경우 4를 출력

;; map이라는 좋은 키워드 얻음
(def day1-1
  (-> (io/resource "day1.sample.txt")
      slurp ;;메모리라..더 좋은게 필요한데..
      (str/split #"\n+")
      (->> (map #(Integer/parseInt %)))))

;; reduce라는 좋은 좋은 키워드를 얻음
(reduce + day1-1)


;;; 짜려고 했던거 실패...
(defn day1-1-old []
  (let [value 0]
    (println "start" value)
    (with-open [readline (io/reader "resources/day1.sample.txt")]
      (doseq [line (line-seq readline)]
        (println "number" (#(Integer/parseInt %) line))
        (+ value  (#(Integer/parseInt %) line)))
      (-> (println "sum" value)))))

(day1-1-old)

;; 파트 2
;; 주어진 입력의 숫자를 더할 때 마다 나오는 숫자 중, 처음으로 두번 나오는 숫자를 리턴하시오.
;; 예) +3, +3, +4, -2, -4 는 10이 처음으로 두번 나오는 숫자임.
;; 0 -> 3 (+3) -> 6 (+3) -> 10(+4) -> 8(-2) -> 4(-4) -> 7(+3) -> 10(+3) -> ...
