(ns configs.snippets
  (:require [clojure.xml :as xml]
            [clojure.string :as string]))

;;; (require ' ')


;; from https://stackoverflow.com/questions/49703245/how-to-elegantly-parse-xml-in-clojure

;; (def doc
;;   (->
;; "<document>
;;   <sentence id=\"1\">
;;     <word id=\"1.1\">
;;       Foo
;;     </word>
;;     <word id=\"1.2\">
;;       bar
;;     </word>
;;   </sentence>
;; </document>
;; " .getBytes java.io.ByteArrayInputStream. xml/parse))

;; (->> doc
;;   xml-seq
;;   (filter (comp #{:sentence} :tag))
;;   (map :content)
;;   (map #(transduce
;;           (comp
;;             (mapcat :content)
;;             (map string/trim)
;;             (interpose " "))
;;           str %)))
