from sklearn.externals import joblib
import numpy as np
from sklearn2pmml import PMMLPipeline, sklearn2pmml


new_clf = joblib.load("train_model.m")
print(new_clf.predict(np.array([[3, 1, 2, 1, 0]])))

print(new_clf.predict(np.array([[3, 1, 2, 1, 0], [6, 0, 1, 6, 2]])))
