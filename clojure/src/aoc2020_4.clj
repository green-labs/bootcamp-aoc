(ns aoc2020_4
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]))


;part 1


(def hcl-regex #"^#[a-f0-9._%+-]{6}$")
(def hgt-regex #"^(\d+)(\S+)")
(def pid-regex #"^[0-9]{9}$")

(defn is-height?
  [input]
  (let [[_ h unit] (re-find hgt-regex input)
        height (Integer/parseInt h)]

    (cond
      (= unit "cm") (<= 150 height 193)
      (= unit "in") (<= 59 height 76)
      :else false)))

(s/def :passport/eyesuit #{:amb :blu :brn :gry :grn :hzl :oth})
(s/def :passport/hair-type (s/and string? #(re-matches hcl-regex %)))
(s/def :passport/height-type (s/and string? is-height?))

;;part 1
(s/def :passport-simple/cid string?)
(s/def :passport-simple/byr string?)
(s/def :passport-simple/iyr string?)
(s/def :passport-simple/eyr string?)
(s/def :passport-simple/hcl string?)
(s/def :passport-simple/hgt string?)

;;part 2
(s/def :passport/pid (s/and string? #(re-matches pid-regex %)))
(s/def :passport/cid int?)
(s/def :passport/byr (s/and int? #(>= % 1920) #(<= % 2002)))
(s/def :passport/iyr (s/and int? #(>= % 2010) #(<= % 2020)))
(s/def :passport/eyr (s/and int? #(>= % 2020) #(<= % 2030)))
(s/def :passport/ecl :passport/eyesuit) ;#{color ddd}
(s/def :passport/hcl :passport/hair-type)
(s/def :passport/hgt :passport/height-type) ;(or regex 175 cm  /57in)

;;spec에 함수가있음
;;(spec/def :passport/ecl #{"amb" "blu" "brn" "gry" "grn" "hzl" "oth"})
;;{:length 175 :unit :cm}
;;(let [[_ v1 v2] input])


(s/valid? :passport/pid "012445679")

;285 in total
(s/def :passport-simple/person
  (s/keys :req-un [:passport-simple/pid :passport-simple/byr :passport-simple/iyr :passport-simple/eyr
                   :passport-simple/ecl :passport-simple/hcl :passport-simple/hgt]
          :opt-un [:passport-simple/cid]))

;;part 2
(s/def :passport/person
  (s/keys :req-un [:passport/pid :passport/byr :passport/iyr :passport/eyr
                   :passport/ecl :passport/hcl :passport/hgt]
          :opt-un [:passport/cid]))

(s/valid? :passport/person
          {:ecl :gry :pid "860033327" :eyr 2020 :hcl "#fffffd" :byr 1937 :iyr 2017 :cid 147 :hgt "183cm"})


(defn parse-file-to-str
  "parse file input to string
   input: string file
   output: string"
  [input]
  (->  input
       io/resource
       slurp
       string/split-lines))


(defn split-field
  "split a vector of strings to each words
   input: '(\"hcl:#ae17e1 iyr:2013\" \"eyr:2024\" \"ecl:brn pid:760753108 byr:1931\" \"hgt:179cm\")
   output: '(\"hcl:#ae17e1\" \"iyr:2013\" \"eyr:2024\" \"ecl:brn\" \"pid:760753108\" \"byr:1931\" \"hgt:179cm\")"
  [input]
  (mapcat #(string/split % #" ") input))


;set? 
;찾아보기 !! case 
;해보기 !!!! 구분 기준을 \n\n

(defn word-to-kv
  "parse one word to key - value map
   input: hcl:#ae17e1
   output: {:hcl #ae17e1}
   "
  [input]
  (let [[_ key val] (re-find #"(\S+):(\S+)" input)]
    (cond
      (= key "cid") {(keyword key) (Integer/parseInt val)}
      (= key "byr") {(keyword key) (Integer/parseInt val)}
      (= key "iyr") {(keyword key) (Integer/parseInt val)}
      (= key "eyr") {(keyword key) (Integer/parseInt val)}
      (= key "ecl") {(keyword key) (keyword val)}
      :else {(keyword key) val})
    ;{(keyword key) val}
    ))

(defn strings-to-passport
  "parse a list of strings which has field information for a person
   input: '(\"hcl:#ae17e1 iyr:2013\" \"eyr:2024\" \"ecl:brn pid:760753108 byr:1931\" \"hgt:179cm\")
   output: {:hcl \"#ae17e1\", :iyr \"2013\", :eyr \"2024\", :ecl \"brn\", :pid \"760753108\", :byr \"1931\", :hgt \"179cm\"}"
  [input]
  (->> input
       split-field
       (map word-to-kv)
       (apply conj)))


(comment
  (->> "2020_4_sample.txt"

       ;parse
       parse-file-to-str
       (partition-by #(= "" %))
       (remove #(= (first %) "")) ;(= "" (first %))

       ;preprocess
       (map strings-to-passport)

       ;aggregate
       ;(map #(s/explain :passport/person %))
       (map #(s/valid? :passport/person %)) 
       (filter #(true? %))

       ;print
       count))