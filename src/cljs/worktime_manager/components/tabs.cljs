(ns worktime-manager.components.tabs
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :refer [put!]]
            [worktime-manager.components.selectable :refer [dropdown]]))

(defn active? [is-active]
  (if is-active
    {:class "active"}
    {}))

(defn change-year [app owner new-val old-val]
  (when (not= new-val old-val)
    (let [ch (om/get-shared owner :nav-chan)
          week (get-in @app [:current-date :week])]
      (om/update! app [:current-date :year] new-val)
      (put! ch [new-val week]))))

(defn tabs [app owner]
  (reify
    om/IRender
    (render [_]
      (html [:ul.nav.nav-tabs
             [:li (active? (= (:route app) :home))
              [:a {:href "#/"} "Home"]]
             [:li (active? (= (:route app) :stats))
              [:a {:href "#/statistics"} "Statistics"]]
             [:li.dropdown
              [:a.dropdown-toggle {:data-toggle "dropdown"} "Year " [:span.caret]]
              (om/build dropdown app {:opts {:value-key :valid-years
                                             :on-change-fn (partial change-year app owner)}})]]))))
