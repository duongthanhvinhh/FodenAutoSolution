import docker
import time

client = docker.from_env()

def check_container_status(container_names, retries=5, interval=5):
    for _ in range(retries):
        for container_name in container_names:
            try:
                container = client.containers.get(container_name)
                if container.status != 'running':
                    break
            except docker.errors.NotFound:
                # Container not exist
                break
        else:
            return True  # All containers are running
        time.sleep(interval)
    return False  # All retries failed

# Containers
container_names = ['seleniumHub', 'seleniumgrid1-FirefoxService-1', 'seleniumgrid1-EdgeService-1', 'seleniumgrid1-ChromeService-1']

# Check status
if check_container_status(container_names, retries=5, interval=5):
    print("All containers are running")
else:
    print("Some containers are not running")