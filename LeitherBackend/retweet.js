(()=>{
    // request, lapi are global variables
    // each comment is a tweet object
    const RETWEET_COUNT = "retweet_count_key"
    const RETWEET_LIST = "retweet_user_list_key"

    let tweetId = request["tweetid"]
    console.log("tweet mid=", tweetId)

    var authSid = lapi.BELoginAsAuthor()
    let mmsid = lapi.MMOpen(authSid, tweetId, "cur")
    let count = lapi.Get(mmsid, RETWEET_COUNT)

    let cl = lapi.Get(mmsid, RETWEET_LIST)
    if (request["action"] != "remove") {
        count++
        lapi.Set(mmsid, RETWEET_LIST, cl.push(tweetId))
    } else {
        // remove the tweet ID
        count--
        cl = cl.filter(item => item !== tweetId)
        lapi.Set(mmsid, RETWEET_LIST, cl)
    }
    lapi.Set(mmsid, RETWEET_COUNT, count)
    lapi.MMBackup(sid, tweetId, "", "delref=true")
    lapi.MiMeiPubish(authSid, "", tweetid)

    count
})()