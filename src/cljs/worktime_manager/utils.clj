(ns worktime-manager.utils)

(defmacro to-week [date-str]
  `(worktime-manager.utils/get-week-number (worktime-manager.utils/str->date ~date-str)))

(defmacro format-date [date-str]
  `(worktime-manager.utils/display-date (worktime-manager.utils/str->date ~date-str)))

(defmacro format-short-date [date-str]
  `(worktime-manager.utils/display-short-date (worktime-manager.utils/str->date ~date-str)))
