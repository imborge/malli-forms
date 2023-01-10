(ns imborge.malli-forms.render
  (:require [malli.core :as m]))

(def type->input-type
  {:string   "text"
   'string?  "text"
   'int?     "number"
   'pos-int? "number"})

(defmulti render-type-attr
  (fn [[field-name ?props ?schema :as entry]]
    (or  (m/type ?schema))))

(defmethod render-type-attr :default [type]
  (type->input-type type))

(defn props->attrs [?props]
  (->> ?props
       (filter (fn [[k _v]] (= "ui" (namespace k))))
       (map (fn [[k v]] [(keyword (name k)) v]))
       (remove (fn [[k v]] (and (= :type k)
                                (= :textarea (keyword v)))))
       (into {})))

(defmulti render-tag-name
  (fn [[field-name ?props ?schema :as entry]]
    (some-> (:ui/type ?props)
            keyword)))

(defmethod render-tag-name :textarea
  [[field-name ?props ?schema :as entry]]
  :textarea)

(defmethod render-tag-name :default
  [_]
  :input)

(defmulti render-attrs
  (fn [[_ _ ?schema]]
    (m/type ?schema)))

(defmethod render-attrs :string
  [[field-name ?props ?schema :as entry]]
  (let [is-textarea? (= :textarea (keyword (:ui/type ?props)))]
    (-> (merge
         {:name (name field-name)}
         {:type "text"}
         (props->attrs ?props))
        (dissoc (when is-textarea? :type)))))

(defmethod render-attrs 'string?
  [[field-name ?props]]
  (render-attrs [field-name ?props :string]))

(defmethod render-attrs 'pos-int?
  [[field-name ?props]]
  (merge
   {:min 1
    :type "number"
    :name (name field-name)}
   (props->attrs ?props)))

(defmethod render-attrs 'nat-int?
  [[field-name ?props]]
  (merge
   {:min 0
    :type "number"
    :name (name field-name)}
   (props->attrs ?props)))

(defmethod render-attrs :default
  [[field-name ?props ?schema :as entry]]
  (merge
   {:name (name field-name)
    :type (type->input-type (m/type ?schema))}
   (-> (props->attrs ?props))))

(defmulti render-input
  (fn [[_ _ ?schema]]
    (m/type ?schema)))

(defmethod render-input :default
  [[field-name ?props ?schema :as entry]]
  [(render-tag-name entry)
   (render-attrs entry)])

(defn render-form
  "Turns a ?schema to form-hiccup, ?schema must be a schema for a map"
  ([?schema]
   (render-form {} ?schema))
  ([params ?schema]
   {:pre [(= :map (m/type ?schema))]}
   (into [:form params]
         (map render-input (m/children ?schema)))))
