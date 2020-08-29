(ns configs.server
  (:require [clojure.edn :as edn]
            [qlkit.core :as ql]
            [configs.parsers :as pa]
            [clojure.data.xml :as xml]
            [clojure.xml :as cxml]
            [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.data.zip :asd zip]
            [clojure.zip :as z]
            [clojure.data.zip.xml :as xmlzip]
            [xml-in.core :as xmlin]
            [clojure.string :as s]
            [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.core :as csk]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [camel-snake-kebab.extras :as cske]
            [configs.utils :as utils]
            [clojure.set :as cset]
            [medley.core :as medley]
            [clj-time.format :as f]))


(defn format-date
  [d]
  #_(println d)
  
  (if (empty? d)
    "2018-03-01T00:00:00"
    (let [year (apply str (take 4 d))
         month (apply str (take 2 (drop 4 d)))
         day (apply str (drop 6 d))
         date-str (str year "-" month "-" day )]
     (f/unparse (f/formatter :date-hour-minute-second)(f/parse (f/formatter :year-month-day) date-str)))))

(defn parse-xml [path]
  (xml/parse (io/input-stream path)))

;; (:import [org.apache.commons.compress.compressors.bzip2
;;           BZip2CompressorInputStream])


#_(into {}
        (for [m (xmlzip/xml-> root :head :meta)]
          [(keyword (xmlzip/attr m :type))
           (xmlzip/text m)]))

;; (defn bz2-reader "produce a Reader on a bzipped file"
;;   [filename]
;;   (-> filename
;;       io/file
;;       io/input-stream 
;;       BZip2CompressorInputStream.
;;      io/reader))


#_(let [parsed (xml/parse (io/input-stream "x.xml"))
        ks (distinct (map :tag (:content parsed)))
        strs (map #(re-find #":[A-Z]*_" (str %))  ks)]
    (pp/pprint (distinct strs)))

#_(let [parsed (xml/parse (io/input-stream "x.xml"))
        ks (distinct (map :tag (:content parsed)))
        filtered (filter #(= (:tag %) :PP_CONFIGURATION_TABLE) (:content parsed))
        strs (map #(re-find #":[A-Z]*_" (str %))  ks)]
    (pp/pprint filtered))

#_(let [parsed (xml/parse (io/input-stream "tables.clj"))
                                        ;      ks (distinct (map :tag (:content parsed)))
                                        ;      strs (map #(re-find #":[A-Z]*_" (str %))  ks)
        ]
    (pp/pprint parsed))

#_(let [ss (s/split-lines (slurp "tables.clj"))
        strs (-> (map #(re-find #"[A-Z]*_" (str %))  ss) distinct)]
    (pp/pprint strs))


;; configuration mappings
;; TODO to be generated 

;; files


(def foo (xml/parse (io/input-stream "config-files/foo.xml")))
(def foo2 (xml/parse (io/input-stream "config-files/foo2.xml")))

(def ^{:private true} csv-test
  "Year,Make,Model
1997,Ford,E350
2000,Mercury,Cougar")

(def csv-foo (csv/read-csv (io/reader "config-files/simple.csv")))

(def orgs (xml/parse (io/input-stream "Organization.xml")))
(def x (xml/parse (io/input-stream "x.xml")))
(def x2 (xml/parse (io/input-stream "x2.xml")))


;; CSV


(defn read-column [reader column-index]
  (let [data (csv/read-csv reader)]
    (map #(nth % column-index) data)))

(defn sum-second-column [filename]
  (with-open [reader (io/reader filename)]
    (->> (read-column reader 1)
         (drop 1)
         (map #(Double/parseDouble %))
         (reduce + 0))))



(defn handler [req]
  (when (= (:uri req) "/endpoint")
    (let [query  (edn/read-string (slurp (:body req)))
          result (ql/parse-query query)]
      {:status 200
       :body   (pr-str result)})))

(ql/mount {:parsers {:read   pa/read
                     :mutate pa/mutate}})

(defn dbg [node]
  (if (associative? node)
    (cxml/emit-element (dissoc node :content))
    (cxml/emit-element node))
  node)

(defn as-short-xml [node]
  (clojure.string/trim ; remove trailing \n
   (with-out-str
     (if (associative? node)
       (cxml/emit-element (dissoc node :content))
       (cxml/emit-element node)))))

;; (xml-> foo-zip :top xmlzip/text)


#_(clojure.data.xml/parse  (io/input-stream "foo.xml"))

#_(def foo-zip (-> "foo.xml" io/input-stream xml/parse zip/xml-zip))

                                        ;(xml-> (clojure.zip/xml-zip y) :top text)

(def target-result {:top {:mid {:bot "botvalue"}}})


;; {(:tag foo) (:content foo)} 

;; {:top {:bot botvalue :bot2 botvalue2}}


(defn nested? [col] (not (nil? (:tag col))))

#_(defn parsed->map [{:keys [tag  [content1 & restparsed :as all]]}]
    (if ()
      {tag (recur (parsed->map content1))}))

(defn reducer [r k v]
  (if (= k :tag) (assoc r k)))

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map keyword) ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(comment
  (defn convert [m r]
    (hash-map (:tag m) (let [ns (remove string?  (:content m))]
                         (if

                             (convert))))))

#_(let [r (convert2 {} x2)]

    (clojure.pprint/pprint r (clojure.java.io/writer "x3.edn")))

;; sol 1 xml-seq
;; sol 2 optimize convert 

(defn node-children [n]
  (remove string? (:content n)))

(defn has-children? [n]
  (some map? (:content n))
  #_(not (map? (:content n))))

(defn has-more-children? [n]
  (-> (>= (count (node-children n)) 2)))

(defn has-more-children? [n]
  (-> n
      node-children
      count
      (>= , 2)
      (and , (has-children? n))))

(defn node-value [n]
  ((fnil clojure.string/trim "") (first (:content n))))

;; (defn tax-amount
;;   ([amount]
;;      (tax-amount amount 35))
;;   ([amount rate]
;;      (Math/round (double (* amount (/ rate 100))))))


(defn convert3
  ([m]
     (convert3 {} m))

  ([r m]
     (let [;k (csk/->kebab-case (:tag m))
           k (:tag m)
           children (node-children m)]
       #_(prn "k" k)
       (assoc r
         k
         (cond
           (has-more-children? m)
           (map (partial convert3 r) children)

           (has-children? m)
           #_(reduce #(merge %1 (convert3 %1 %2)) {} children)
           (convert3 r (first children))

           :else
                                        ;99
           (node-value m))))))

#_(defn convert-r [m]
    (let [m m
          r {}
          k (:tag m)]
      (if (has-children? m)

        (loop [r r
               childs (node-children m)]

          (assoc r k (reduce #(merge %1 (convert %1  %2)) {} childs)))
        (assoc r k (node-value m)))))

#_(hash-map (:tag m) (let [children (remove string?  (:content m))]
                       (if

                           (convert))))

#_(hash-map (:tag m) (let [children (remove string?  (:content m))]
                       (if

                           (convert))))

;; transforming suppliers


(defn map-part-model2 [m]
  (reduce-kv (fn [r k v] (assoc
                             r
                           k
                           (apply utils/deep-merge v))) {} m))


;; hacky we iterate #depth of times with

;;(nth (iterate map-model test-model2) 4)


(defn map-model [m]
  (reduce-kv (fn [r k v]
               (cond

                 (map? v)
                 (do
                   #_(print v)
                   (assoc r k (map-model v)))

                 (seq? v)
                 (let [merged (apply utils/deep-merge v)]
                                        ;(def g-merged merged)
                   (assoc r k
                          merged))

                 :else
                 (assoc r k v))) {}  m))

(defn map-model3 [m]
  (let [magic-number 4]
    (nth (iterate map-model m) magic-number)))



(def test-model {:company "",
                 :bank-accounts {:supplier-bank-account '({:bban ""} {:name ""})}})

(def test-model2
  {:supplier
   '({:company ""}
     {:bank-accounts
      {:supplier-bank-account
       '({:bban ""}
         {:name ""})}}
     {:supplier-identifiers
      {:supplier-identifier
       '({:key ""} {:value ""} {:default-party-id ""})}}
     {:additional-datas {:additional-data '({:key ""} {:value ""})}}
     {:email-addresses {:supplier-email {:email-address ""}}})})

(def test-model3
  {:supplier
   [{:company ""}
    {:bank-accounts {:supplier-bank-account [{:bban ""} {:name ""}]}}
    {:supplier-identifiers
     {:supplier-identifier
      [{:key ""} {:value ""} {:default-party-id ""}]}}]})

;; (clojure.set/rename-keys {:a 1 :b 2} {:a :new-a :b :new-b} )
;; (clojure.walk/postwalk-replace {:a :new-a :b :new-b} {:a 1 :b 2}  )
;; (clojure.set/rename-keys {:a 1 :b 2} {:a :new-a :b :new-b} )
;; (cset/difference (set (keys one-supp-map)) (set (keys one-supp-in)))

;; (distinct (flatten (keys (flatten-keys configs.server/accounts-model))))



(def csv-suppliers-in
  (map (partial drop-last 3) ;identity
       (csv/read-csv (io/reader "config-files/suppliers-in.csv") :seperator \;)))

(def supps-model (parse-xml "config-files/suppliers-model.xml"))
(def supps-model-map (cske/transform-keys csk/->kebab-case-keyword (convert3 supps-model)))

(def supps-in-map (cske/transform-keys csk/->kebab-case-string (csv-data->maps csv-suppliers-in)))
(def one-supp-in (first supps-in-map))
(def one-supp-map (->
                   (cske/transform-keys csk/->kebab-case-string (map-model3 supps-model-map))
                   (get-in ["document-element" "supplier"])))


;;; -------------------------------------------------------------


#_(def csv-suppliers-in
    (map (partial drop-last 3) ;identity
         (csv/read-csv (io/reader "config-files/accounts-in") :seperator \;)))

(def supps-model (parse-xml "config-files/suppliers-model.xml"))

(def supps-model-map (cske/transform-keys csk/->kebab-case-keyword (convert3 supps-model)))

(def supps-in-map (cske/transform-keys csk/->kebab-case-string (csv-data->maps csv-suppliers-in)))

(def one-supp-in (first supps-in-map))



;;



(def supps-model (parse-xml "config-files/suppliers-model.xml"))

(def supps-model-map (cske/transform-keys csk/->kebab-case-keyword (convert3 supps-model)))

(def csv-accounts-in
  (csv/read-csv (io/reader "accounts-in.csv") :seperator \;))


(def accounts-in (cske/transform-keys csk/->kebab-case-keyword (csv-data->maps csv-accounts-in)))






(def one-supp-in (first supps-in-map))
;; (def one-supp-map (->
;;                    (cske/transform-keys csk/->kebab-case-string (map-model3 supps-model-map))
;;                    (get-in ["document-element" "supplier"])))

;;; -------------------------------------------------------------







;; (def accounts-in (->> (->  "config-files/accounts-in.xml"
;;                            parse-xml
;;                            convert3
;;                                         ;(get , :DocumentElement))
;;                            (cske/transform-keys csk/->kebab-case-keyword)
;;                                         ;(map map-model3)

;;                                         ;(map-model3 ,)
;;                            ))





(def exchange-rates-in (->  "config-files/Source_ExchangeRate.xml"
                            parse-xml
                            convert3
                            map-model3))


;; ---------




                                        ;(def one-account-in (first accounts-in))

(def accounts-model (-> "config-files/accounts-model.xml"
                        parse-xml
                        convert3
                        map-model3
                        ((partial cske/transform-keys csk/->kebab-case-keyword))))

(def all-keys (distinct (flatten (keys (configs.utils/flatten-keys configs.server/accounts-model)))))


#_(def transformed {:document-element
                    (->> accounts-in
                         (map
                          (fn [x]
                            (-> x
                                (update , :item #(cset/rename-keys %
                                                                   {:object-account-code :text-1
                                                                    :object-account-name-fr :text-2}))
                                ;; only select keys that are in model 

                                (update , :item #(select-keys % all-keys))

                                (update , :item #(assoc % :inherit 1 :company "FluxymFrance")))
                            #_(select-keys , all-keys)                        #_(dissoc))))})



(def exchange-rates-map (map map-model3 (:LstExcRatesResponse  (:soapenvBody (:soapenvEnvelope (convert3 (parse-xml "config-files/Source_ExchangeRate.xml")))))))

(def exchange-rate-transformed
  (->> exchange-rates-map
       (map
        (fn [x]
          (let [rename-map {:ExchangeRate :Num1
                            :LocalCurrency :Text_1
                            :Currency :Text_2
                            :RateDate :Date1
                            :Company :Company}]

            (-> x
                (cset/rename-keys {:LstExcRatesResponseItem :Item})
                (update , :Item #(cset/rename-keys %
                                                   rename-map))
                (update , :Item #(select-keys % (vals rename-map) ))
                (update , :Item #(assoc % :Date1 (format-date (:Date1 %)) ))))))))







;; creating a map

(def result {:DocumentElement (reduce
                               (fn [r m]
                                 (let [m2 (:Item m)
                                       from (:Text_1 m2)
                                       to (:Text_2 m2)
                                       rate (clojure.edn/read-string (:Num1 m2))
                                       new-map (assoc m2  :Text_1 to :Text_2 from :Num1 (format "%.6f" (/ 1 rate)))]
                                   (if (not (= from to))
                                     (conj r m {:Item new-map})
                                     (conj r m ))))
                               [] exchange-rate-transformed)})




#_(get-in one-supp-map)

(spit "accounts.xml" (configs.utils/xml
               (cske/transform-keys  name {:DocumentElement
                                           (map (fn [m]
                                                  {:Item (-> (clojure.set/rename-keys m {:no. :Text_1 :name :Text_2})
                                                             (assoc :Company "BGC" :Inherit 1)
                                                             (update :Text_2 (fn [s] (clojure.string/replace s #"&" "&amp;"))))})

                                                accounts-in)})))






#_(xml/emit-str
 (xml/element
  :documentElement {}
  (map (fn make-node [[f s]]
         (if (map? s)
           (xml/element f {} (map make-node (seq s)))
           (xml/element f {} s)))
       (seq r))))


;; https://zaiste.net/creating_xml_with_clojure/


(xml/emit-str (xml/element :book {} "TITLE"))
