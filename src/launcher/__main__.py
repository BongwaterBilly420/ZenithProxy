import sys
import time

import github_api
import launch_platform
from launch_config import LaunchConfig, read_launch_config_file
from launcher import launcher_exec
from setup import setup_execute, rescue_invalid_system
from update_launcher import update_launcher_exec
from update_zenith import update_zenith_exec

config = LaunchConfig()
api = github_api.GitHubAPI(config)

print("ZenithProxy Launcher Initializing...")

# for use with relaunches just so we don't get stuck in an infinite update loop if something goes wrong
no_launcher_update = False
# to go straight to setup and exit
setup_only = False
for arg in sys.argv:
    if arg == "--no-launcher-update":
        no_launcher_update = True
    if arg == "--setup":
        setup_only = True

if setup_only:
    setup_execute(config)
    sys.exit(0)

try:
    while True:
        json_data = read_launch_config_file()
        if json_data is None:
            print("launch_config.json not found, running setup.")
            setup_execute(config)
            continue
        config.load_launch_config_data(json_data)
        if not config.validate_launch_config():
            print("launch_config.json has invalid values, running setup.")
            setup_execute(config)
            continue
        print("Loaded launch_config.json successfully")
        if not launch_platform.validate_system_with_config(config):
            rescue_invalid_system(config)
            continue
        if no_launcher_update:
            no_launcher_update = False
        else:
            update_launcher_exec(config, api)
        update_zenith_exec(config, api)
        launcher_exec(config)
        print("Restarting in 3 seconds...")
        time.sleep(3)
except KeyboardInterrupt:
    sys.exit(0)
