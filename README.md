Little tool to load an export of Slack data

the main in `Importer.kt` takes the path to a folder containing [an export of
Slack data](https://get.slack.help/hc/en-us/articles/201658943-Exporting-your-team-s-Slack-history).

It doesn't do much by itself (users, channels and messages are imported into
data structure), but you can then manipulate the data.
