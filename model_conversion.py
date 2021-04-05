import sys
import io
import os
import tensorflow as tf
from tensorflow.keras.models import load_model
import tensorflowjs as tfjs
import h5py
tf.compat.v1.disable_eager_execution()

model = tf.keras.models.load_model('/opt/files/topology/' + sys.argv[1] + ".h5")
model.summary()
tfjs.converters.save_keras_model(model, "/opt/files/topology/" + sys.argv[1])
