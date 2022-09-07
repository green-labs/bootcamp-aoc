(ns aoc2020_4
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]))


;part 1

(def hcl-regex #"^#[a-zA-Z0-9._%+-]{6}$")
(def hgt-regex #"^[0-9]+[a-zA-Z]+$")

(s/def :passport/hair-type (s/and string? #(re-matches hcl-regex %)))
(s/def :passport/height-type (s/and string? #(re-matches hgt-regex %)))


;(s/valid? :passport/hair-type "scfa07d")
;(s/explain :passport/height-type "169cm")

(s/def :passport/pid string?)
;(s/def :passport/cid int?)
;(s/def :passport/byr int?)
;(s/def :passport/iyr int?)

;(s/def :passport/eyr int?)

(s/def :passport/cid string?)
(s/def :passport/byr string?)
(s/def :passport/iyr string?)

(s/def :passport/eyr string?)
(s/def :passport/ecl string?)
;(s/def :passport/hcl :passport/hair-type)
;(s/def :passport/hgt :passport/height-type)
(s/def :passport/hcl string?)
(s/def :passport/hgt string?)


;285 in total
(s/def :passport/person
      #_(s/keys) 
       (s/keys :req-un [:passport/pid :passport/byr :passport/iyr :passport/eyr 
                                      :passport/ecl :passport/hcl :passport/hgt]
                            :opt-un [:passport/cid])
       )

(s/valid? :passport/person
          {:ecl "gry" :pid "860033327" :eyr 2020 :hcl "#fffffd" :byr 1937 :iyr 2017 :cid 147 :hgt "183cm"})

(defn parse-file-to-str
  "parse file input to string
   input: string file
   output: string"
  [input]
  (->  input
       io/resource
       slurp
       string/split-lines))

;(split-field '("aee bdd c" "d c"))

(defn split-field
  [input]
  (mapcat #(string/split % #" ") input))

;(map #(re-find #"(\S+):(\S+)" %) '("ecl:gry" "pid:860033327" "eyr:2020" "hcl:#fffffd" "byr:1937" "iyr:2017" "cid:147" "hgt:183cm"))

(defn string-to-kv
  [input]
  (let [[_ key val] (re-find #"(\S+):(\S+)" input)]
    #_(cond 
      (= key "cid") {(keyword key) (Integer/parseInt val)}
      (= key "byr") {(keyword key) (Integer/parseInt val)}
      (= key "iyr") {(keyword key) (Integer/parseInt val)}
      (= key "eyr") {(keyword key) (Integer/parseInt val)}
     :else {(keyword key) val})
    {(keyword key) val}
    )
)
;(string-to-kv "ecl:gry")

(defn strings-to-passport
  [input]
  (->> input
       (map string-to-kv)
       (apply conj))
  )
;(strings-to-passport '("ecl:gry" "pid:860033327" "eyr:2020" "hcl:#fffffd" "byr:1937" "iyr:2017" "cid:147" "hgt:183cm"))

(comment
  (->> "2020_4_sample.txt"
       ;parse
       parse-file-to-str
       (partition-by #(= "" %))
       (remove #(= (first %) ""))
       
       ;preprocess
       (map split-field)
       (map strings-to-passport)

       ;aggregate
       (map #(s/explain :passport/person %))
       (filter #(true? %))
       
       ;print
       count 
  )
)