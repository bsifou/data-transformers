;   Copyright (c) John Valente. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns ^{:doc "Functions to emit a Clojure map as XML."
      :author "John Valente"}
     configs.utils
  (:refer-clojure :exclude [contains?])
  (:require [clojure.set :as set]))

;; This package is intended to write Clojure data as XML in a way that seems
;; natural and obvious -- at least, to me.
;;
;; If you have a map, {:foo "bar"}, I'd think the obvious way to write that
;; into XML would be: <foo>bar</foo>.
;;
;; Apparently, that is not so obvious.  I originally wrote this quick and dirty
;; code around 2014.  I first searched for an existing package, certain that a
;; good one must exist.  I found stuff to generate XML from Clojure, but they
;; all seemed to want the data to be annotated.  At best, given a nested map
;; like {:foo {:bar "hello"}}, they'd produce <foo bar="hello" />.  That's not
;; what I wanted.
;;
;; Now it's 2018 and the clojure.data.xml package has been created.  But it
;; does the same thing as the other packages did.  It also, by default,
;; produces unformatted, unreadable XML.  It has the ability to do indentation,
;; but warns that it is so slow that it should be considered a debugging
;; feature.
;;
;; I suppose one can conclude that clojure.data.xml is intended for use in
;; creating large amounts of fully-featured XML, using the Clojure language.
;; That's not what I want.  I want to represent Clojure data -- any Clojure
;; data, with an emphasis on what is most common -- as XML.
;;
;; This package has no dependencies, which is nice in some sense, but also
;; means I'm likely re-inventing the wheel a lot.  It might be nice to
;; integrate this with data.xml, so that it would take generic Clojure data,
;; massage it into the format necessary for data.xml to treat maps as tag
;; and content, and then pass it on to data.xml, specifying the "slow"
;; indentation be used.
;;
;; Or I could just make this better.

(def spaces-per-indent 2)

(defn padding [depth]
  (apply str (repeat (* spaces-per-indent depth) \space)))

(declare xml-element)

(def line-break "\n") ;; could use \newline character?
(def sequence-tag "elt")
(def xml-header "<?xml version='1.0' encoding='UTF-8'?>")

(defn xml-tag-name [requested-tag]
  (name requested-tag))

(defn xml-open-tag [tag-name]
  (str "<" (xml-tag-name tag-name) ">"))
   
(defn xml-close-tag [tag-name]
  (str "</" (xml-tag-name tag-name) ">"))

(defn xml-empty-tag [tag-name]
  (str "<" (xml-tag-name tag-name) " />"))
   
(defn xml-comment [depth & msgs]
  (str
   (padding depth)
   "<!-- "
   (apply str msgs)
   " -->"
   line-break))

(defn lisp-false [x]
  (or (not x)
      (and (sequential? x) (empty? x))))

(defn xml-key-value [k v depth]
  (if (lisp-false v)
    (xml-empty-tag k)
    (str
     (xml-open-tag k)
     ;line-break
     (xml-element 0 v)
     ;line-break
     ;(padding depth)
     (xml-close-tag k))))

(defn xml-map-entry [depth [k v]]
  (str
   (padding depth)
   (xml-key-value k v depth)))

(defn xml-seq-element [depth x]
  (str
   (padding depth)
   ;(xml-open-tag sequence-tag)
   ;line-break
   (xml-element (inc depth) x)
   ;line-break
   (padding depth)
   ;(xml-close-tag sequence-tag)
   ;line-break
   ))

(defn xml-element [depth x]
  (cond
    (or (string? x) (char? x) (number? x))
    (str (padding depth) x)

    (instance? String x)
    (str (padding depth) x)

    (instance? clojure.lang.Named x)
    (str (padding depth) (xml-tag-name x))

    (map? x)
    (->> x
         (map #(xml-map-entry depth %))
         (interpose line-break)
         (apply str))
    
    (sequential? x)
    (->> x
         (map #(xml-seq-element depth %))
         (interpose line-break)
         (apply str))

    :default
    (str
     (xml-comment
      depth
      "warning: unrecognized type: "
      (type x))
     (padding depth)
     (str x))))
(defn xml [x]
  (str
   xml-header
   ;line-break
   (xml-element 0 x)))

(defn deep-merge [v & vs]
  (letfn [(rec-merge [v1 v2]
                     (if (and (map? v1) (map? v2))
                       (merge-with deep-merge v1 v2)
                       v2))]
    (when (some identity vs)
      (reduce #(rec-merge %1 %2) v vs))))


;; -------------------------------------------------------------------------



(defn- flatten-keys* [acc ks m]
  (if (and (map? m)
           (not (empty? m)))
    (reduce into
            (map (fn [[k v]]
                   (flatten-keys* acc (conj ks k) v))
                 m))
    (assoc acc ks m)))

(defn flatten-keys
  "Transforms a nested map into a map where keys are paths through
  the original map and values are leafs these paths lead to.

  ```clojure
  (flatten-keys {:a {:b {:c :x
  :d :y}}})
  => {[:a :b :c] :x
  [:a :b :d] :y}
  ```"
  [m]
  (if (empty? m)
    m
    (flatten-keys* {} [] m)))

(defn deflatten-keys
  "Builds a nested map out of a map obtained from [[flatten-keys]].

  ```clojure
  (deflatten-keys {[:a :b :c] :x
  [:a :b :d] :y})
  => {:a {:b {:c :x
  :d :y}}}
  ```"
  [m]
  (reduce (fn [acc [ks v]]
            (update-in acc ks
                       (fn [x]
                         (if x
                           (if (every? map? [x v])
                             (merge v x)
                             x)
                           v))))
          {} m))

(defn map-difference
  "Returns a submap of m excluding any entry whose key appear in any of
  the remaining maps."
  [m & ms]
  (apply dissoc m (keys (apply merge ms))))

(defn map-intersection
  "Returns a submap of m including only entries whose key appear in all of
  the remaining maps."
  [m & ms]
  (select-keys m (apply set/intersection (map (comp set keys) ms))))




(let [h {:a {:aa {:aaa1 1
                  :aaa2 2}}
         :b {:bb 3}}
      paths [[:a :aa :aaa1]
             [:b :bb]]
      flat-h (flatten-keys h)
      paths-flat-h (zipmap paths (repeat nil))
      diff-h (map-difference flat-h paths-flat-h)
      orphane-h-paths (keys diff-h)]
  (println orphane-h-paths)
  ;; [[:a :aa :aaa2]]
  )
