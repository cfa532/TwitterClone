(()=>{
    // request, lapi are global variables
    // each comment is a tweet object
    const RETWEET_COUNT = "tweet_retweet_count"
    const RETWEET_LIST = "tweet_retweet_list"

    let origTweetId = request["tweetid"]

    var authSid = lapi.BELoginAsAuthor()
    let mmsid = lapi.MMOpen(authSid, origTweetId, "cur")
    let count = lapi.Get(mmsid, RETWEET_COUNT)
    count++
    console.log("retweet mid=", origTweetId, count)

    lapi.Set(mmsid, RETWEET_COUNT, count)
    lapi.MMBackup(authSid, origTweetId, "", "delref=true")
    // lapi.MiMeiPubish(authSid, "", tweetid)

    return count
})()

class ScorePair {
    constructor(score, member) {
      this.score = score;
      this.member = member;
    }
};