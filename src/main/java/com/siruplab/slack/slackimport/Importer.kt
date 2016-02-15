package com.siruplab.slack.slackimport

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

fun main(args: Array<String>) {
  val mainDir = File(args[0]);

  val objectMapper = ObjectMapper()

  val users = Users(objectMapper
      .readTree(File(mainDir, "users.json"))
      .map { User(it) })


  val channels = Channels(objectMapper
      .readTree(File(mainDir, "channels.json"))
      .map { Channel(it) })

  val messages = mainDir.listFiles { file, s -> File(file, s).isDirectory }
      .flatMap { channelDir ->
        val channelId = channels.channel(channelDir.name)
        channelDir.listFiles().asIterable()
            .flatMap { objectMapper.readTree(it) }
            .map { Message(it, channelId) }
      }

  System.out.println("*** Most prolific users ***")
  messages
      .filter { it.user != null }
      .countBy(5) { users.userName(it.user!!) }
      .forEach { System.out.println(it) }

  System.out.println("*** Most active channels ***")
  messages
      .countBy(5) { it.channel.name }
      .forEach { System.out.println(it) }


  System.out.println("*** subtypes ***")
  messages
      .filter { it.subType != null }
      .countBy { it.subType }
      .forEach { System.out.println(it) }

}

fun <T, K> Iterable<T>.countBy(top: Int = Int.MAX_VALUE, keySelector: (T) -> K): List<Pair<K, Int>> {
  return groupBy { keySelector.invoke(it) }
      .map { Pair(it.key, it.value.size) }
      .sortedByDescending { it.second }
      .take(top)
}


