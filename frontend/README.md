# Android client source

The Android client is maintained on this repository's `fe-integration` branch.
The backend branch previously contained a gitlink at
`frontend/UTE-Training-Points-System`, but it had no `.gitmodules` entry and
therefore produced an empty, non-checkoutable directory after cloning.

To inspect the Android client without replacing the backend working tree:

```bash
git fetch origin fe-integration
git worktree add ../UTE-Training-Points-Android origin/fe-integration
```

Backend and Android changes should be reviewed and tested on their respective
branches. The client is not bundled into the backend build.
