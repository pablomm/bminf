
es.uam.eps.bmi.recsys.data.FeaturesImpl // Modificado por faltas de comprobacion si elemento es null
es.uam.eps.bmi.recsys.data.RatingsImpl  // Modificado por faltas de comprobacion si elemento es null
es.uam.eps.bmi.recsys.RecommendationImpl
es.uam.eps.bmi.recsys.recommender.AbstractRecommender // Modificado para no recomendar cosas ya puntuadas 
es.uam.eps.bmi.recsys.recommender.AverageRecommender // Modificado para simplificar el codigo como se sugiere en la memoria
es.uam.eps.bmi.recsys.recommender.UserKNNRecommender // Modificado para no normalizar y pequeño fallo que hacia multiplicar por null
es.uam.eps.bmi.recsys.recommender.NormUserKNNRecommender // Modificado para normalizar y filtrar usando minimo de vecinos correctamente
es.uam.eps.bmi.recsys.recommender.similarity.CosineUserSimilarity // Modificado para no iterar sobre todos los elementos de la collecion, solo sobre los puntuados
es.uam.eps.bmi.recsys.recommender.similarity.CosineFeatureSimilarity // Modificada para calcular bien los modulos y no iterar sobre todos los usuarios
es.uam.eps.bmi.recsys.recommender.CentroidRecommender // Rehecho completamente
es.uam.eps.bmi.recsys.recommender.ItemNNRecommender // Nueva
es.uam.eps.bmi.recsys.recommender.similarity.CosineItemSimilarity // Modificada para calcular bien los modulos y no iterar sobre todos los usuarios
es.uam.eps.bmi.recsys.recommender.similarity.JaccardFeatureSimilarity // Nueva
es.uam.eps.bmi.recsys.recommender.similarity.PearsonSimilarity // Nueva
es.uam.eps.bmi.recsys.recommender.UserCenterKNNRecommender // Nueva
es.uam.eps.bmi.recsys.metric.Precision // Nueva
es.uam.eps.bmi.recsys.metric.Recall // Nueva
es.uam.eps.bmi.recsys.metric.Rsme // Nueva
es.uam.eps.bmi.recsys.test.StudenTest // Nueva

