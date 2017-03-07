(ns om-tutorial.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(enable-console-print!)

(def app-state (atom {:count 0})) ; page state

;; UI component
(defui Counter
  static om/IQuery ; https://github.com/omcljs/om/wiki/Quick-Start-%28om.next%29#1-implement-omnextiquery
  (query [this]
    [:count])
  Object
  (render [this]
    (let [{:keys [count]} (om/props this)]
      (dom/div nil
        (dom/span nil (str "Count: " count))
        (dom/button
          #js {:onClick
               (fn [e]
                 (om/transact! this '[(increment)]) ; fire off an om route query
                 #_(.log js/console "click event:" e))
               :onMouseOver
               (fn [e]
                 (println "hovered"))}
          "Click me!")))))

;; read function for the route parser
(defn read [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:value :not-found})))

;; mutate function for the route parser
(defn mutate [{:keys [state] :as env} key params]
  (if (= 'increment key) ;only handles the 'increment query
    {:value {:keys [:count]}
     :action #(swap! state update-in [:count] inc)}
    {:value :not-found}))

;; a state reconciler tying the page state to a parser
;; (refer to https://github.com/omcljs/om/wiki/Quick-Start-%28om.next%29#adding-state)
(def reconciler
  (om/reconciler
    {:state app-state
     :parser (om/parser {:read read :mutate mutate})}))

;; fire off the UI component along its state reconciler
(om/add-root! reconciler
  Counter (gdom/getElement "app"))
