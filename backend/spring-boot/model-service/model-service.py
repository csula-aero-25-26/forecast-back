# Flask file

from flask import Flask, request, jsonify
import joblib
import numpy as np

app = Flask(__name__)

# Load trained model
MODEL_PATH = "model.pkl"
model = joblib.load(MODEL_PATH)

# Define expected feature order
lag_features = [f"lag{i}" for i in range(1, 28)]
ap_features = [
    "ap_mean", "ap_max",
    "ap_mean_lag1", "ap_mean_lag2", "ap_mean_lag3",
    "ap_max_lag1", "ap_max_lag2", "ap_max_lag3"
]
FEATURES = lag_features + ap_features

# Listens for /predict endpoint
@app.post("/predict")
def predict():
    data = request.get_json()

    # Ensure all required features exist
    if "features" not in data:
        return jsonify({"error": "Missing 'features' in request"}), 400

    # Build ordered numeric feature list
    feats = []
    for f in FEATURES:
        val = data["features"].get(f)
        if val is None:
            return jsonify({"error": f"Missing feature '{f}'"}), 400
        feats.append(float(val))

    X = np.array(feats).reshape(1, -1)

    # Predict next-day flux
    y_pred = model.predict(X)[0]
    return jsonify({"prediction": float(y_pred)})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
