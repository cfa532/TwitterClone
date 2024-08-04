(()=>{
    // request, lapi are global variables
    // each comment is a tweet object
    const COMMENT_COUNT = "tweet_comment_count"
    const COMMENT_LIST = "comment_list_key"

    let commentId = request["commentid"]
    let tweetId = request["tweetid"]
    let authSid = lapi.BELoginAsAuthor()
    let mmsid = lapi.MMOpen(authSid, tweetId, "cur")
    let count = lapi.Get(mmsid, COMMENT_COUNT)

    if (request["action"] != "remove") {
        count++
        lapi.Zadd(mmsid, COMMENT_LIST, new ScorePair(Date.now(), commentId))
    } else {
        // remove the tweet ID
    }
    lapi.Set(mmsid, COMMENT_COUNT, count)
    lapi.MMBackup(sid, tweetId, "", "delref=true")
    lapi.MiMeiPubish(authSid, "", tweetid)

    return count
})()

class ScorePair {
    constructor(score, member) {
      this.score = score;
      this.member = member;
    }
};