(()=>{
    // request, lapi are global variables
    // each comment is a tweet object
    const RETWEET_COUNT = "retweet_count_key"
    const RETWEET_LIST = "retweet_user_list_key"

    let retweetId = request["retweetid"]
    let origTweetId = request["tweetid"]
    console.log("tweet mid=", origTweetId)

    var authSid = lapi.BELoginAsAuthor()
    let mmsid = lapi.MMOpen(authSid, origTweetId, "cur")
    let count = lapi.Get(mmsid, RETWEET_COUNT)

    if (request["action"] != "remove") {
        count++
        lapi.Zadd(mmsid, RETWEET_LIST, new ScorePair(Date.now(), retweetId))
    } else {
        // remove the tweet ID
    }
    lapi.Set(mmsid, RETWEET_COUNT, count)
    lapi.MMBackup(sid, origTweetId, "", "delref=true")
    lapi.MiMeiPubish(authSid, "", tweetid)

    return count
})()

class ScorePair {
    constructor(score, member) {
      this.score = score;
      this.member = member;
    }
};