(ns coremodule
  (:require [amazonica.aws.s3 :as s3]
            [amazonica.aws.cognitoidp :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as strs])
  (:gen-class))

(defn content-type [upload-name]
  (let [ext (last (strs/split upload-name #"\."))]
    (cond
      (= ext "png") "image/png"
      (or (= ext "jpeg") (= ext "jpg")) "image/jpeg"
      (= ext "html") "text/html"
      :else "text/plain")))

(defn upload [file-path upload-name]
  (let [ctype (content-type file-path)
        bucket-name (System/getenv "BUCKET_NAME")]
    (println (str "Uploading: " upload-name " with type " ctype))
    (s3/put-object :bucket-name bucket-name
                   :key upload-name
                   :file file-path
                   :metadata {:server-side-encryption "AES256"
                              :content-type ctype})
    (println (str
              "fileurl: https://s3."
              (System/getenv "AWS_DEFAULT_REGION")
              ".amazonaws.com/"
              bucket-name
              "/" upload-name))))

(defn -main
  [& args]
  (println (list-user-pools { :max-results 2}))
  (let [file-path (first args)]
    (if (= (count args) 2)
      (upload file-path (ffirst args))
      (upload file-path (last (strs/split file-path #"/"))))))
