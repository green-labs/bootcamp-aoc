(ns aoc2020_8
  (:require [clojure.string :as string]
            [clojure.java.io :as io]))

(def op-regex #"(\S+) (\S+)")


(defn make-bootcode-map
  "make a bootcode map indicating operation and its value
   input: \"nop +0\"
   output: {:op \"nop\", :value 0}
   "
  [input]
  (let [[_ op value] (re-find op-regex input)]
    {:op op
     :value (Integer/parseInt value)}))

(defn parse-file-to-record
  "parse file input to data record
   input: string file
   output: boot codes vector
   ({:op \"nop\", :value 0}
   {:op \"acc\", :value 1}
   {:op \"jmp\", :value 4}..
   "
  [input]
  (->>  input
        io/resource
        slurp
        string/split-lines
        (map make-bootcode-map)))


(defn execute-operation
  [program] ;naming 다시 
  (let [in (:record program)
        line (:line program)
        acc (:acc program)
        code (nth in line)
        op (:op code)
        val (:value code)
        visited (conj (:visited program) line)]
    (case op
      "nop" {:line (inc line) :acc acc :record in :visited visited}
      "acc" {:line (inc line) :acc (+ acc val) :record in :visited visited}
      "jmp" {:line (+ line val) :acc acc :record in :visited visited})))


(def not-contains? (complement contains?))

;;vector ㄹ로 선언하면 안ㅓ힘 contains?는 vector가 다른 목적의 동작임..!!!!

(defn execute-boot-code
  [input]
  (take-while #(not-contains? (:visited %) (:line %))
              (iterate execute-operation
                       {:line 0 :acc 0 :record input :visited #{}})))

;threading macro 3줄로 !!~~
;구조분해를 map으로 할 수 있음
;{:keys [0 0 input #{}]}, as 찾아보기~~!



(comment
  (->> "2020_8_sample.txt"
       execute-boot-code
       last))



(def input
  '({:op "nop", :value 0}
    {:op "acc", :value 1}
    {:op "jmp", :value 4}
    {:op "acc", :value 3}
    {:op "jmp", :value -3}
    {:op "acc", :value -99}
    {:op "acc", :value 1}
    {:op "jmp", :value -4}
    {:op "acc", :value 6}))

(let [{:keys [op val]} {:op "nop", :value 0}]
     op)

;; (take 10 (iterate execute-operation
;;                   {:line 0 :acc 0 :record input :visited []}))
;; (last
;;  (take-while #(not-contains? (:visited %) (:line %))
;;              (iterate execute-operation
;;                       {:line 0 :acc 0 :record input :visited #{}})))
