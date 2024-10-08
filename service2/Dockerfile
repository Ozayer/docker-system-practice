# Use an official Python runtime as a parent image
FROM python:3.9-slim

# Set the working directory
WORKDIR /usr/src/app

# Install necessary utilities (ps, uptime)
RUN apt-get update && apt-get install -y procps

# Copy the application code to the container
COPY . .

# Install Flask and any other dependencies
RUN pip install Flask

# Expose port 5000
EXPOSE 5000

# Run the Flask app
ENTRYPOINT ["python", "app.py"]
