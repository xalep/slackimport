package com.siruplab.slack.slackimport

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

fun main(args: Array<String>) {
  val importer = Importer(File(args[0]))
  show("most prolific users", importer.mostProlificUsers())
  show("most active channels", importer.mostActiveChannels())
  show("subtypes", importer.subTypes())
}

fun <T> show(description: String, values: List<T>, show: (Any) -> Unit = { System.out.println(it) }) {
  show.invoke("*** $description ***")
  values.forEach { show.invoke(it!!) }
}

class Importer(mainDir: File) {
  val users: Users
  val channels: Channels
  val messages: List<Message>

  init {
    val objectMapper = ObjectMapper()

    users = Users(objectMapper
        .readTree(File(mainDir, "users.json"))
        .map { User(it) })

    channels = Channels(objectMapper
        .readTree(File(mainDir, "channels.json"))
        .map { Channel(it) })

    messages = mainDir.listFiles { file, s -> File(file, s).isDirectory }
        .flatMap { channelDir ->
          val channelId = channels.channel(channelDir.name)
          channelDir.listFiles().asIterable()
              .flatMap { objectMapper.readTree(it) }
              .map { Message(it, channelId) }
        }
  }

  fun mostProlificUsers(): List<Pair<String?, Int>> {
    return messages
        .filter { it.user != null }
        .countBy(5) { users.userName(it.user!!) }
  }

  fun mostActiveChannels(): List<Pair<String, Int>> {
    return messages
        .countBy(5) { it.channel.name }
  }

  fun subTypes(): List<Pair<String, Int>> {
    return messages
        .filter { it.subType != null }
        .countBy { it.subType!! }
  }

  fun <T, K> Iterable<T>.countBy(top: Int = Int.MAX_VALUE, keySelector: (T) -> K): List<Pair<K, Int>> {
    return groupBy { keySelector.invoke(it) }
        .map { Pair(it.key, it.value.size) }
        .sortedByDescending { it.second }
        .take(top)
  }
}


