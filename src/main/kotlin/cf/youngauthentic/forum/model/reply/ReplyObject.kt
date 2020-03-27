package cf.youngauthentic.forum.model.reply

class ReplyObject(replyProjection: ReplyProjection) {
    val replyId = replyProjection.rid
    val replyContent = replyProjection.replyContent
    val replyTime = replyProjection.replyTime
    val lastEditTime = replyProjection.lastEditTime
    val priority = replyProjection.priority
    val isBestAnswer = replyProjection.bestAnswer
    val user = replyProjection.userByUid
    val vote = 0
}