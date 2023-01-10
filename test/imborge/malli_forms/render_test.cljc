(ns imborge.malli-forms.render-test
  (:require
   #?(:clj [clojure.test :refer :all]
      :cljs [cljs.test :as t :include-macros true :refer :all])
   [imborge.malli-forms.render :refer [render-form render-input]]))

(def SimpleFormSchema
  [:map
   [:name {:ui/placeholder "Enter your name here"} :string]])

(deftest render-input-test
  (testing "string? schema to input[type=text]"
    (is (= [:input {:type "text"
                    :name "field"}]
           (render-input [:field nil string?]))))
  (testing ":string schema to input[type=text]"
    (is (= [:input {:type "text"
                    :name "field"}]
           (render-input [:field nil string?]))))
  (testing "int? schema to input[type=number]"
    (is (= [:input {:type "number"
                    :name "field"}]
           (render-input [:field nil int?]))))
  (testing "can render to textarea"
    (is (= [:textarea {:name "field"}]
           (render-input [:field {:ui/type :textarea} string?])))))

(deftest render-form-test
  (testing "Creates form"
    (is (= [:form {}] (render-form [:map]))))
  (testing "Creates more complicated form"
    (is (=
         [:form {}
          [:input {:type "text"
                   :name "name"}]
          [:input {:type "text"
                   :name "phone"}]
          [:input {:type "text"
                   :name "email"}]
          [:input {:type "number"
                   :name "age"
                   :min  1}]]
         (render-form
          [:map
           [:name  :string]
           [:phone :string]
           [:email :string]
           [:age   pos-int?]])))))
