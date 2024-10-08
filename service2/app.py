from flask import Flask, jsonify
import os
import subprocess
import socket

app = Flask(__name__)

@app.route('/info', methods=['GET'])
def get_info():
    return jsonify({
        'ip': socket.gethostbyname(socket.gethostname()),
        'processes': subprocess.getoutput("ps -ax"),
        'diskSpace': subprocess.getoutput("df"),
        'uptime': subprocess.getoutput("uptime")
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
