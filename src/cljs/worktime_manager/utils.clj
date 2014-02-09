(ns worktime-manager.utils)

(defmacro to-week [date-str]
  `(worktime-manager.utils/get-week-number (worktime-manager.utils/str->date ~date-str)))
