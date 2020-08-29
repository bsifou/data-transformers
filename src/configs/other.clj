(ns configs.other)



{:CMN_SCHEMA_ITEM  {:ui-path [:ia :schemas]
                    :doc "Configures the UI for entities"}
 ;; :CMN_ENTITY_CONFIG_ITEM {:ui-path [:ia :schemas]
 ;;                          :doc "Configures the UI for entities"



 :CMN_ENTITY_CONFIG {:ui-path? [:ia :entities]
                     :doc "Configures the UI for entities"}

 :CMN_ENTITY_CONFIG_ITEM {:ui-path? [:ia :entities]
                     :doc "Configures the UI for entities"}}


;; ("ADMIN_"
;;  "ADM_"
;;  "BSS_"
;;  "BUDGExoT_"
;;  "CMN_"
;;  "CMS_"
;;  "CON_"
;;  "CT_"
;;  "CUSTOM_"
;;  "DB_"
;;  "EDIT_"
;;  "EXT_"
;;  "GDM_"
;;  "GOODS_"
;;  "HME_"
;;  "IA_"
;;  nil
;;  "IMPORTED_"
;;  "META_"
;;  "MIG_"
;;  "_"
;;  "OM_"
;;  "OPSPORTAL_"
;;  "ORDER_"
;;  "PE_"
;;  "PLM_"
;;  "PO_"
;;  "PP_"
;;  "PR_"
;;  "PUNCHOUT_"
;;  "PURCHASE_"
;;  "RECEIVING_"
;;  "REQUISITION_"
;;  "SHOP_"
;;  "SUP_"
;;  "TEM_"
;;  "TM_"
;;  "WF_")

(def atom1 (xml/parse-str "<?xml version='1.0' encoding='UTF-8'?>
<feed xmlns='http://www.w3.org/2005/Atom'>
  <id>tag:blogger.com,1999:blog-28403206</id>
  <updated>2008-02-14T08:00:58.567-08:00</updated>
  <title type='text'>n01senet</title>
  <link rel='alternate' type='text/html' href='http://n01senet.blogspot.com/'/>
  <entry>
    <id>1</id>
    <published>2008-02-13</published>
    <title type='text'>clojure is the best lisp yet</title>
    <author><name>Chouser</name></author>
  </entry>
  <entry>
    <id>2</id>
    <published>2008-02-07</published>
    <title type='text'>experimenting with vnc</title>
    <author><name>agriffis</name></author>
  </entry>
</feed>
"))

(defn convert
  ([m]
   (convert {} m))
  ([r m]
   (assoc r (:tag m) (let [children (node-children m)]
                       (if (has-children? m)
                         #_(do (prn (node-value m)) (node-value m))
                         (reduce #(merge %1 (convert %1 %2)) {} children)
                         (node-value m)
                         #_(node-value m)
                         
                         #_(apply merge (map  (partial convert {}) children)))))))

#_(defn convert2
  ([m]
   (convert2 {} m))
  ([r m]
   (assoc r (:tag m) (let [children (node-children m)]
                       (if (has-children? m)
                         #_(do (prn (node-value m)) (node-value m))
                         (reduce #(merge %1 (convert2 %1 %2)) {} children)
                         0
                         #_(node-value m)
                         
                         #_(apply merge (map  (partial convert {}) children)))))))

#_(let [parsed (xml/parse (io/input-stream "foo.xml"))
     ; ks (distinct (map :tag (:content parsed)))
     ; strs (map #(re-find #":[A-Z]*_" (str %))  ks)
      ]
  (pp/pprint parsed))

;; (nil
;;  ":CMN_"
;;  ":OM_"
;;  ":CT_"
;;  ":INVOICE_"
;;  ":PURCHASE_"
;;  ":PP_"
;;  ":LOCALIZATIONRESOURCE_"
;;  ":OVERRIDELOCALIZATIONRESOURCE_"
;;  ":EXT_"
;;  ":IA_"
;;  ":ADM_"
;;  ":WF_"
;;  ":ORGANIZATION_")

