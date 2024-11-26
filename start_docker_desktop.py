import subprocess
import time
import sys
import os

def start_docker_desktop_windows():
    try:
        docker_path = r"C:\Program Files\Docker\Docker\Docker Desktop.exe"
        if not os.path.isfile(docker_path):
            print(f"Docker Desktop executable not found at: {docker_path}")
            return
        subprocess.Popen([docker_path])
        print("Docker Desktop started.")
    except Exception as e:
        print(f"An error occurred: {e}")

def start_docker_desktop_mac():
    try:
        subprocess.Popen(["open", "-a", "Docker"])
        print("Docker Desktop started.")
    except Exception as e:
        print(f"An error occurred: {e}")

def start_docker_desktop_linux():
    try:
        password = 'foden'
        process = subprocess.Popen(['sudo', '-S', 'systemctl', 'start', 'docker'], stdin=subprocess.PIPE, stderr=subprocess.PIPE)
        process.communicate(input=password.encode() + b'\n')
        print("Docker service started on Linux.")
    except Exception as e:
        print(f"An error occurred: {e}")

def is_docker_ready():
    try:
        result = subprocess.run(["docker", "info"], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        if result.returncode == 0:
            return True
        else:
            print("Docker is not ready yet. stderr:", result.stderr)
            return False
    except Exception as e:
        print(f"An error occurred while checking Docker status: {e}")
        return False

def wait_for_docker_ready(timeout=300):
    start_time = time.time()
    while time.time() - start_time < timeout:
        if is_docker_ready():
            print("Docker is ready.")
            return True
        print("Waiting for Docker to be ready...")
        time.sleep(5)  # Wait for 5 seconds before checking again
    print("Docker did not become ready in the given time.")
    return False

if __name__ == "__main__":
    if sys.platform == "win32":
        start_docker_desktop_windows()
        if wait_for_docker_ready():
            # Here you can run your docker-compose commands
            print("Ready to run docker-compose.")
        else:
            print("Docker did not become ready.")
    elif sys.platform == "darwin":
        start_docker_desktop_mac()
        if wait_for_docker_ready():
            # Here you can run your docker-compose commands
            print("Ready to run docker-compose.")
        else:
            print("Docker did not become ready.")
    elif sys.platform == "linux" or sys.platform == "linux2":
            print("Docker Engine is ready.")
    else:
        print("This script supports only Windows, macOS, and Linux.")
