package cf.youngauthentic.forum.model.reply

class ReplyObject(replyProjection: ReplyProjection) {
    var rid = replyProjection.rid
    var replyContent = replyProjection.replyContent
    var replyTime = replyProjection.replyTime
    var lastEditTime = replyProjection.lastEditTime
    var priority = replyProjection.priority
    var isBestAnswer = replyProjection.isBestAnswer
    var userByUserId = replyProjection.userByUserId
    var vote = 0
}