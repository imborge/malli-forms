# malli-forms

A form rendering/validation library for Clojure(Script)!

## Usage

### Clojure (server-side)
```clojure
(def GuestbookForm
  [:map {:form/method :post}
    [:name {:ui/placeholder "Your name here."} :string]
    [:email {:ui/placeholder "your@email.com"} :string]
    [:message {:ui/type :textarea
               :ui/placeholder "Enter message."}]])

;; ring handler
(defn handler [request]
  (let [{:keys [posted? valid? request]} (request->form GuestBookForm request)]
    (if (and posted? valid?)
      (do (db/save! data)
          (redirect "/"))
          
      (render my-form request))))
```

### ClojureScript w/ reagent
```clojurescript
(def doc-atom (r/atom nil))

(def GuestbookForm
  [:map
    {:doc doc-atom
     :on-field-change (fn [field new-field-value] (swap! doc-atom update field new-field-value))
     :on-submit-and-valid (fn [validated-doc] (send-to-server! validated-doc))
	 :on-submit-and-invalid (fn [validation-errors] (js/console.log validation-errors))}
    [:name {:ui/placeholder "Your name here."} :string]
    [:email {:ui/placeholder "your@email.com"} :string]
    [:message {:ui/type :textarea
               :ui/placeholder "Enter message."}]])

(defn form-view []
  [form GuestbookForm]
```