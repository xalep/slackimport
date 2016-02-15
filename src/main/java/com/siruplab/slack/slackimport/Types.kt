package com.siruplab.slack.slackimport

import com.fasterxml.jackson.databind.JsonNode

data class ChannelId(val id: String)
data class UserId(val id: String)


open class DataNode(val raw: JsonNode) {
  fun string(path: String): String? {
    return raw.get(path)?.asText()
  }
}

class Channel(raw: JsonNode) : DataNode(raw) {
  val id = ChannelId(string("id")!!)
  val name = string("name")
  val creator = UserId(string("creator")!!)
  override fun toString(): String {
    return "Channel(id=$id, name=$name, creator=$creator)"
  }
}

class Channels(val channels: List<Channel>) {

  fun channel(name: String): Channel {
    return channels.find { it.name == name }!!;
  }

}

class User(raw: JsonNode) : DataNode(raw) {
  val id = UserId(string("id")!!)
  val name = string("name")!!
  val realName = string("real_name")

  /** returns the best available name for the user */
  val bestName: String
    get() {
      return if (realName.isNullOrEmpty()) name else realName!!
    }

  override fun toString(): String {
    return "User(id=$id, name=$name, realName=$realName)"
  }

}

class Users(val users: List<User>) {

  fun userName(userId: UserId): String? {
    return users.find { it.id == userId }?.bestName
  }

}


class Message(raw: JsonNode, val channel: Channel) : DataNode(raw) {
  val text = string("text")
  val user: UserId? = if (string("user") == null) null else UserId(string("user")!!)
  val subType = string("subtype")
  override fun toString(): String {
    return "Message(channel=${channel.name}, text=$text, user=$user, subType=$subType)"
  }
}

