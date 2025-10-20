# ---- FLASK MODEL SERVICE DOCKERFILE ----

# Base image with Python
FROM python:3.12-slim

# Install required system libraries for LightGBM
RUN apt-get update && apt-get install -y libgomp1 && rm -rf /var/lib/apt/lists/*

# Set work directory
WORKDIR /app

# Copy all files (.py, .pkl, requirements.txt)
COPY . .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Expose the port Flask runs on
EXPOSE 5000

# Start the Flask app
CMD ["python", "model-service.py"]
