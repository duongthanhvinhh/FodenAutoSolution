import subprocess
import time
import sys
import os

def stop_docker_desktop_windows():
    try:
        docker_process_name = "Docker Desktop.exe"
        result = subprocess.run(["tasklist"], stdout=subprocess.PIPE, text=True)
        if docker_process_name in result.stdout:
            subprocess.run(["taskkill", "/IM", docker_process_name, "/F"], check=True)
            print("Docker Desktop has been stopped.")
        else:
            print("Docker Desktop was not running.")
    except Exception as e:
        print(f"An error occurred while stopping Docker Desktop: {e}")

def stop_docker_desktop_mac():
    try:
        subprocess.run(["osascript", "-e", 'quit app "Docker"'], check=True)
        print("Docker Desktop has been stopped.")
    except Exception as e:
        print(f"An error occurred while stopping Docker Desktop: {e}")

def is_docker_stopped():
    try:
        result = subprocess.run(["docker", "info"], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        if result.returncode != 0:
            return True
        else:
            print("Docker is still running. stdout:", result.stdout)
            return False
    except Exception as e:
        print(f"An error occurred while checking Docker status: {e}")
        return False

if __name__ == "__main__":
    if sys.platform == "win32":
        stop_docker_desktop_windows()
        print("Docker Desktop has been successfully stopped.")
    elif sys.platform == "darwin":
        stop_docker_desktop_mac()
        print("Docker Desktop has been successfully stopped.")
    else:
        print("This script supports only Windows and macOS.")
