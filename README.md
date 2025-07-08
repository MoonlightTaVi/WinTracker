# WinTracker
Track time you spend in apps (on windows).

A simple GUI-driven Java application for Windows 7+ (UNIX systems should have this functionality built-in).

**JDK 17 is required.**

![Screenshot 2025-07-08 140219](https://github.com/user-attachments/assets/e21fece9-21b0-41d1-90bb-c1334ab6bc31)

_You can apply a filter to the titles to see something specific._


![Screenshot 2025-07-08 140310](https://github.com/user-attachments/assets/6faa3a9f-99fe-4186-ab1f-d3e9069dd4bc)

_And use regular expressions (or plain text) to filter some titles from being tracked._

### How it works

The JNA library is used to scan currently open windows and retrieve their titles.

The daemon runs in background, lazily updating SQLite database (with title names and time spent) approximately every ~30 seconds, based on JNA fetching results.

### How to use

Create an `.env` file inside the downloaded application build directory and fill it as follows:
```
USERNAME=username
PASSWORD=password
DB_NAME=window-tracker
SHELL=false
```
The values don't really matter (except `SHELL=false` that blocks starting the CLI, which you don't need), but if you update the app to the latest version, you still CAN use the same SQL file
(that contains info on time tracked), but the `.env` contents must stay the same.

As you start the App, you can fold it to tray (by simply closing). Then right-click the tray icon to either close completely or open the window again.

> Displayed information is fetched by the GUI window only when you first open it from tray;
> You must explicitly fold the app (by clicking `X`) and open it from tray again to see the updated information).

> The app make take up to 10 seconds to close (to prevent data corruption). Don't panic!

You can specify titles to ignore by tracker by clicking the "Ignore list" button.

> Close the popped-up window with ignore list, and it will be saved
> (you won't know if it's saved until you open it again, but trust me, it's saved).

> There is NO way to make _"ignore this, but not this"_ expressions **YET**.

You can completely delete some already existent entries by right-clicking corresponding title and choosing such an option (the titles will be removed on the next daemon update; you may undo deletion if you ~~pass the skill check~~ will be quick and lucky enough).

> NOTE
> If you close the window alfter choosing to delete something and then open it again,
> the titles queued for deletion will be displayed as normal (but they will still be deleted).

You can hover over some GUI elements and see a tooltip (but basically, the information contains: a window title, time the window has been open during the current session,
overall time spent with window open, first time opened date and last time closed date).

You can copy a window title by right-clicking it and choosing such an option (useful for ignore-list interactions).

There are NO any kind of sorting.

You can completely delete the SQL file from the `/data` directory to clear all the information (it will be created _tabula rasa_ on the next start up).

### I don't know how it will work with thousands of entries yet.
You may test it yourself, but I recommend deleting useless information.

The worst that may happen is a long initialization process and high RAM consumption (due to caching). The latter must be fixed later, with reverse compatibility of the DB kept.
