# PKD Data Collector
A simple mod to collect data for PKD helper!

---

## Features

### Room Scanner

> `/scanroom <room_name>`
>
> `/scanroom export`

This command scans the room you are currently in and collects all the blocks in it.
This data is stored in columns ("x,z" -> ("y" -> "block_name)) omitting air for compactness.

The data is stored in a file called `room_data.json` in the .minecraft folder when exported.